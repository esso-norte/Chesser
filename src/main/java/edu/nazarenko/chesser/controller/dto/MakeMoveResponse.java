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
public class MakeMoveResponse {

    private boolean isValid;
}
