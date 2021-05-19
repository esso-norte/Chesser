package edu.nazarenko.chesser.repository;

import edu.nazarenko.chesser.model.analysis.AnalysisGame;
import edu.nazarenko.chesser.model.game.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface AnalysisGameRepository extends JpaRepository<AnalysisGame, Long> {
//  Optional<User> findByUsername(String username);
    AnalysisGame findByGame(Game game);
}
