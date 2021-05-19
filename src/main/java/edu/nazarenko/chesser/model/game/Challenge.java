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
@Table(name = "challenges")
public class Challenge {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    private User from;
    @ManyToOne(fetch = FetchType.EAGER)
    private User to;
    @Enumerated(EnumType.STRING)
    private ChallengeColor color;
    private Instant created;
}
