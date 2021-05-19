package edu.nazarenko.chesser.repository;

import edu.nazarenko.chesser.model.User;
import edu.nazarenko.chesser.model.game.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
//  Optional<User> findByUsername(String username);
    List<Game> findByWhitePlayerOrBlackPlayer(User white, User black);
    Game findFirstByAnalyzed(boolean analyzed);
}
