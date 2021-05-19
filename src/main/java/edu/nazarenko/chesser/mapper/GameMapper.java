package edu.nazarenko.chesser.mapper;

import edu.nazarenko.chesser.controller.dto.GameDto;
import edu.nazarenko.chesser.model.game.Game;
import org.springframework.stereotype.Service;

@Service
public class GameMapper {

    public GameDto mapGameToDto(Game game) {
        return GameDto.builder()
            .id(game.getId())
            .whitePlayer(game.getWhitePlayer())
            .blackPlayer(game.getBlackPlayer())
            .pgn(game.getPgn())
            .fen(game.getFen())
            .created(game.getCreated())
            .isFinished(game.isFinished())
            .result(game.getResult() != null ? game.getResult().toString() : "")
            .isAnalyzed(game.isAnalyzed())
            .build();
    }

    public Game mapDtoToGame(GameDto gameDto) {
        return Game.builder()
            .whitePlayer(gameDto.getWhitePlayer())
            .blackPlayer(gameDto.getBlackPlayer())
            .pgn(gameDto.getPgn())
            .fen(gameDto.getFen())
            .created(gameDto.getCreated())
            .analyzed(gameDto.isAnalyzed())
            .build();
    }
}
