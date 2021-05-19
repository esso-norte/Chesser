package edu.nazarenko.chesser.repository;

import edu.nazarenko.chesser.model.Stats;
import edu.nazarenko.chesser.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface StatsRepository extends JpaRepository<Stats, Long> {
//  Optional<User> findByUsername(String username);
    Stats findByUser(User user);
}
