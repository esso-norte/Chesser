package edu.nazarenko.chesser.service;

import edu.nazarenko.chesser.controller.dto.UserDto;
import edu.nazarenko.chesser.model.User;
import edu.nazarenko.chesser.repository.ChallengeRepository;
import edu.nazarenko.chesser.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ChallengeRepository challengeRepository;

    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers(User currentUser) {
        return userRepository.findAll()
            .stream()
            .map(user -> UserDto.builder()
                .id(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build()).collect(Collectors.toList());
    }

}
