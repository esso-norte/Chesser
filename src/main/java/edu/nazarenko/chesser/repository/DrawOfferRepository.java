package edu.nazarenko.chesser.repository;

import edu.nazarenko.chesser.model.User;
import edu.nazarenko.chesser.model.game.Challenge;
import edu.nazarenko.chesser.model.game.DrawOffer;
import edu.nazarenko.chesser.model.game.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DrawOfferRepository extends JpaRepository<DrawOffer, Long> {
//  Optional<User> findByUsername(String username);
    Optional<DrawOffer> findByGame(Game game);
}
