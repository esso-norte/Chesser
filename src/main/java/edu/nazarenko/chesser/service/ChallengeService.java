package edu.nazarenko.chesser.service;

import edu.nazarenko.chesser.controller.dto.*;
import edu.nazarenko.chesser.exceptions.ChesserException;
import edu.nazarenko.chesser.mapper.GameMapper;
import edu.nazarenko.chesser.model.Stats;
import edu.nazarenko.chesser.model.User;
import edu.nazarenko.chesser.model.game.Challenge;
import edu.nazarenko.chesser.model.game.Game;
import edu.nazarenko.chesser.repository.ChallengeRepository;
import edu.nazarenko.chesser.repository.GameRepository;
import edu.nazarenko.chesser.repository.StatsRepository;
import edu.nazarenko.chesser.repository.UserRepository;
import edu.nazarenko.chesser.service.game.FENService;
import edu.nazarenko.chesser.service.game.PGNService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ChallengeService {

    private final UserRepository userRepository;
    private final ChallengeRepository challengeRepository;
    private final GameRepository gameRepository;
    private final StatsRepository statsRepository;
    private final GameMapper gameMapper;

    private final AuthService authService;
    private final PGNService pgnService;
    private final FENService fenService;

    public List<ChallengeDto> getAllChallenges() {
        User user = authService.getCurrentUser();

        return challengeRepository.findByTo(user)
            .stream()
            .map(challenge -> ChallengeDto.builder().id(challenge.getId()).from(
                UserDto.builder()
                    .id(challenge.getFrom().getUserId())
                    .username(challenge.getFrom().getUsername())
                    .email(challenge.getFrom().getEmail())
                    .build()
            ).build()).collect(Collectors.toList());
    }

    @Transactional
    public void invite(ChallengeRequest challengeRequest) {
        User challenger = authService.getCurrentUser();

        User recipient = userRepository
            .findById(challengeRequest.getRecipientId())
            .orElseThrow(() -> new ChesserException("Cannot find player with id: " + challengeRequest.getRecipientId()));

        Challenge challenge = Challenge.builder()
            .from(challenger)
            .to(recipient)
            .color(challengeRequest.getChallengeColor())
            .created(Instant.now())
            .build();

        challengeRepository.save(challenge);
    }

    @Transactional
    public GameDto accept(ChallengeAcceptRequest challengeAcceptRequest) {
        User recipient = authService.getCurrentUser();

        Challenge challenge = challengeRepository
            .findById(challengeAcceptRequest.getChallengeId())
            .orElseThrow(() -> new ChesserException("Cannot find challenge with id: " + challengeAcceptRequest.getChallengeId()));

        User challenger = challenge.getFrom();

        Instant created = Instant.now();
        User whitePlayer;
        User blackPlayer;

        switch (challenge.getColor()) {
            case WHITE:
                whitePlayer = challenger;
                blackPlayer = recipient;
                break;
            case BLACK:
                whitePlayer = recipient;
                blackPlayer = challenger;
                break;
            case RANDOM:
                if (Math.random() < 0.5) {
                    whitePlayer = challenger;
                    blackPlayer = recipient;
                } else {
                    whitePlayer = recipient;
                    blackPlayer = challenger;
                }
                break;
            default:
                throw new ChesserException("Unknown challenge color type");
        }

        Game game = Game.builder()
            .whitePlayer(whitePlayer)
            .blackPlayer(blackPlayer)
            .pgn(pgnService.generatePgn(created, whitePlayer.getUsername(), blackPlayer.getUsername()))
            .fen(fenService.startingPosition())
            .created(created)
            .finished(false)
            .build();

        gameRepository.save(game);
        challengeRepository.delete(challenge);

        Stats whiteStats = statsRepository.findByUser(whitePlayer);
        Stats blackStats = statsRepository.findByUser(blackPlayer);
        whiteStats.setOngoing(whiteStats.getOngoing() + 1);
        blackStats.setOngoing(blackStats.getOngoing() + 1);
        statsRepository.save(whiteStats);
        statsRepository.save(blackStats);

        return gameMapper.mapGameToDto(game);
    }
}
