package edu.nazarenko.chesser.controller.dto;

import edu.nazarenko.chesser.model.User;
import edu.nazarenko.chesser.model.game.Game;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GameDto {

    private Long id;
    private User whitePlayer;
    private User blackPlayer;
    private String pgn;
    private String fen;
    private String fenFullJson;
    private boolean isFinished;
    private String result;
    Instant created;
    private boolean isAnalyzed;

    public GameDto(Game game) {
        this.id = game.getId();
        this.whitePlayer = game.getWhitePlayer();
        this.blackPlayer = game.getBlackPlayer();
        this.pgn = game.getPgn();
        this.fen = game.getFen();
        this.fenFullJson = game.getFenFullJson();
        this.isFinished = game.isFinished();
        this.result = game.getResult() == null ? "" : game.getResult().toString();
        this.created = game.getCreated();
        this.isAnalyzed = game.isAnalyzed();
    }
}
