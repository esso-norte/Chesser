package edu.nazarenko.chesser.model.game;

import edu.nazarenko.chesser.model.User;
import edu.nazarenko.chesser.service.game.Color;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.GenerationType.IDENTITY;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "games")
public class Game {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    private User whitePlayer;
    @ManyToOne(fetch = FetchType.EAGER)
    private User blackPlayer;
    @Column(columnDefinition="TEXT")
    private String pgn;
    private String fen;
    @Column(columnDefinition="TEXT")
    private String fenFullJson;
    private Instant created;
    private boolean finished;
    @Enumerated(EnumType.STRING)
    private GameResult result;
    @Enumerated(EnumType.STRING)
    private GameTermination termination;
    private boolean analyzed = false;
}
