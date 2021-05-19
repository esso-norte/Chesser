package edu.nazarenko.chesser.controller.dto;

import edu.nazarenko.chesser.model.game.ChallengeColor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChallengeRequest {

    private Long recipientId;
    private ChallengeColor challengeColor;
}
