package edu.nazarenko.chesser.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.util.List;

import static javax.persistence.GenerationType.IDENTITY;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "stats")
public class Stats {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.EAGER)
    private User user;
    private int total;
    private int wins;
    private int draws;
    private int loses;
    private int ongoing;
    private Double elo;
    private String eloHistory;
}
