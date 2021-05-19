package edu.nazarenko.chesser.controller.dto;

import edu.nazarenko.chesser.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatsDto {

    private User user;
    private int total;
    private int wins;
    private int draws;
    private int loses;
    private int ongoing;
    private double elo;
    private String eloHistory;
}
