package edu.nazarenko.chesser.service;

import edu.nazarenko.chesser.controller.dto.AuthenticationResponse;
import edu.nazarenko.chesser.controller.dto.LoginRequest;
import edu.nazarenko.chesser.controller.dto.RegisterRequest;
import edu.nazarenko.chesser.exceptions.ChesserException;
import edu.nazarenko.chesser.model.NotificationEmail;
import edu.nazarenko.chesser.model.Stats;
import edu.nazarenko.chesser.model.User;
import edu.nazarenko.chesser.model.verification.VerificationToken;
import edu.nazarenko.chesser.repository.StatsRepository;
import edu.nazarenko.chesser.repository.UserRepository;
import edu.nazarenko.chesser.repository.VerificationTokenRepository;
import edu.nazarenko.chesser.security.JwtProvider;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MailService mailService;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    private final UserRepository userRepository;
    private final StatsRepository statsRepository;

    @Transactional
    public void signup(RegisterRequest registerRequest) {
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setCreated(Instant.now());
        user.setEnabled(false);

        userRepository.save(user);

        Stats stats = Stats.builder()
            .user(user)
            .total(0)
            .wins(0)
            .draws(0)
            .loses(0)
            .ongoing(0)
            .elo(1500.0)
            .eloHistory("[1500.0]")
            .build();
        statsRepository.save(stats);

        String token = generateVerificationToken(user);
        mailService.sendMail(new NotificationEmail(
            "Please Activate Your Account",
            user.getEmail(),
            "Thank you for signing up to Chesser," +
                "please click on the url below to activate your account:," +
                "http://localhost:8080/api/auth/accountVerification/" + token
        ));
    }

    private String generateVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);

        verificationTokenRepository.save(verificationToken);

        return token;
    }

    public void verifyAccount(String token) {
        Optional<VerificationToken> verificationToken = verificationTokenRepository.findByToken(token);
        verificationToken.orElseThrow(() -> new ChesserException("invalid Token"));
        fetchUserAndEnable(verificationToken.get());
    }

    @Transactional
    protected void fetchUserAndEnable(VerificationToken verificationToken) {
        String username = verificationToken.getUser().getUsername();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ChesserException("User not found with name: " + username));
        user.setEnabled(true);
        userRepository.save(user);
    }

    public AuthenticationResponse login(LoginRequest loginRequest) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
            loginRequest.getUsername(),
            loginRequest.getPassword()
        ));
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        String token = jwtProvider.generateToken(authenticate);
        return new AuthenticationResponse(token, loginRequest.getUsername());
    }

    public User getCurrentUser() {
        String username = ((UserDetails) SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getPrincipal()
        ).getUsername();

        return userRepository
            .findByUsername(username)
            .orElseThrow(() -> new ChesserException("Auth error"));
    }
}
