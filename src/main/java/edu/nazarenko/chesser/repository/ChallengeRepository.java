package edu.nazarenko.chesser.repository;

import edu.nazarenko.chesser.model.User;
import edu.nazarenko.chesser.model.game.Challenge;
import edu.nazarenko.chesser.model.game.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
//  Optional<User> findByUsername(String username);
    List<Challenge> findByTo(User user);
}
