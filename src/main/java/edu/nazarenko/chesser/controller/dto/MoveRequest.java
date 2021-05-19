package edu.nazarenko.chesser.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MoveRequest {

    private Long gameId;
    private String from;
    private String to;
    private String promotion;
}
