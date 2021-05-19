package edu.nazarenko.chesser.model.game;

import edu.nazarenko.chesser.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

import static javax.persistence.GenerationType.IDENTITY;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "draw_offers")
public class DrawOffer {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    @OneToOne
    private Game game;
    @ManyToOne(fetch = FetchType.EAGER)
    private User player;
}
