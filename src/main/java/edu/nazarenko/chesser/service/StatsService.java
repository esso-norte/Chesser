package edu.nazarenko.chesser.service;

import edu.nazarenko.chesser.controller.dto.*;
import edu.nazarenko.chesser.model.Stats;
import edu.nazarenko.chesser.model.User;
import edu.nazarenko.chesser.repository.StatsRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Slf4j
public class StatsService {

    private final StatsRepository statsRepository;

    private final AuthService authService;

    @Transactional(readOnly = true)
    public StatsDto getStats() {
        User user = authService.getCurrentUser();
        Stats stats = statsRepository.findByUser(user);

        return StatsDto.builder()
            .user(stats.getUser())
            .total(stats.getTotal())
            .wins(stats.getWins())
            .draws(stats.getDraws())
            .loses(stats.getLoses())
            .ongoing(stats.getOngoing())
            .elo(stats.getElo())
            .eloHistory(stats.getEloHistory())
            .build();
    }

    @Transactional
    public void addWin(User winner, User looser) {
        Stats winnerStats = statsRepository.findByUser(winner);
        Stats looserStats = statsRepository.findByUser(looser);

        winnerStats.setOngoing(winnerStats.getOngoing() - 1);
        looserStats.setOngoing(looserStats.getOngoing() - 1);
        winnerStats.setTotal(winnerStats.getTotal() + 1);
        looserStats.setTotal(looserStats.getTotal() + 1);
        winnerStats.setWins(winnerStats.getWins() + 1);
        looserStats.setLoses(looserStats.getLoses() + 1);

        double winnerExpected = 1.0 / (1 + Math.pow(10.0, (looserStats.getElo() - winnerStats.getElo()) / 400));
        double looserExpected = 1.0 / (1 + Math.pow(10.0, (winnerStats.getElo() - looserStats.getElo()) / 400));
        double winnerElo = winnerStats.getElo() + 32 * (1 - winnerExpected);
        double looserElo = looserStats.getElo() + 32 * (0 - looserExpected);
        winnerStats.setElo(winnerElo);
        looserStats.setElo(looserElo);

        JSONArray winnerEloHistory = new JSONArray(winnerStats.getEloHistory());
        JSONArray looserEloHistory = new JSONArray(looserStats.getEloHistory());
        winnerEloHistory.put(Math.floor(winnerElo));
        looserEloHistory.put(Math.floor(looserElo));
        winnerStats.setEloHistory(winnerEloHistory.toString());
        looserStats.setEloHistory(looserEloHistory.toString());

        statsRepository.save(winnerStats);
        statsRepository.save(looserStats);
    }

    public void addDraw(User player1, User player2) {
        Stats whiteStats = statsRepository.findByUser(player1);
        Stats blackStats = statsRepository.findByUser(player2);
        whiteStats.setOngoing(whiteStats.getOngoing() - 1);
        blackStats.setOngoing(blackStats.getOngoing() - 1);
        whiteStats.setTotal(whiteStats.getTotal() + 1);
        blackStats.setTotal(blackStats.getTotal() + 1);
        whiteStats.setDraws(whiteStats.getDraws() + 1);
        blackStats.setDraws(blackStats.getDraws() + 1);

        double whiteExpected = 1.0 / (1 + Math.pow(10.0, (blackStats.getElo() - whiteStats.getElo()) / 400));
        double blackExpected = 1.0 / (1 + Math.pow(10.0, (whiteStats.getElo() - blackStats.getElo()) / 400));
        double whiteElo = whiteStats.getElo() + 32 * (0.5 - whiteExpected);
        double blackElo = blackStats.getElo() + 32 * (0.5 - blackExpected);
        whiteStats.setElo(whiteElo);
        blackStats.setElo(blackElo);

        JSONArray whiteEloHistory = new JSONArray(whiteStats.getEloHistory());
        JSONArray blackEloHistory = new JSONArray(blackStats.getEloHistory());
        whiteEloHistory.put(Math.floor(whiteElo));
        blackEloHistory.put(Math.floor(blackElo));
        whiteStats.setEloHistory(whiteEloHistory.toString());
        blackStats.setEloHistory(blackEloHistory.toString());

        statsRepository.save(whiteStats);
        statsRepository.save(blackStats);
    }
}
