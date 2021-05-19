package edu.nazarenko.chesser.model.analysis;

import edu.nazarenko.chesser.model.game.Game;
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
@Table(name = "analysis_game")
public class AnalysisGame {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.EAGER)
    private Game game;
    private boolean finished;
}
