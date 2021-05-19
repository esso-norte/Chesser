package edu.nazarenko.chesser.repository;

import edu.nazarenko.chesser.model.analysis.AnalysisPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AnalysisPositionRepository extends JpaRepository<AnalysisPosition, Long> {
    AnalysisPosition findByFen(String fen);
}
