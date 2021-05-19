package edu.nazarenko.chesser.repository;

import edu.nazarenko.chesser.model.verification.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
  Optional<RefreshToken> findByToken(String token);

  void deleteByToken(String token);
}
