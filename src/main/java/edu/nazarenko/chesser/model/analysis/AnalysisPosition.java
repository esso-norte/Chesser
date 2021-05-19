package edu.nazarenko.chesser.model.analysis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "analysis_position")
public class AnalysisPosition {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private String fen;
    private Integer depth;
    private Float evaluation;
}
