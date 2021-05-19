package edu.nazarenko.chesser.service.game;

import edu.nazarenko.chesser.controller.dto.GameDto;
import edu.nazarenko.chesser.model.game.Game;
import edu.nazarenko.chesser.model.game.GameResult;
import edu.nazarenko.chesser.model.game.GameTermination;
import edu.nazarenko.chesser.service.game.Color;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class PGNService {

    public String generatePgnFromGame(Game game) {
        return "[Site \"chesser\"]\n" +
            "[Date \"" + game.getCreated().toString() + "\"]\n" +
            "[White \"" + game.getWhitePlayer().getUsername() + "\"]\n" +
            "[Black \"" + game.getBlackPlayer().getUsername() + "\"]\n" +
            "[Result \"*\"]\n" +
            "[Termination \"%\"]\n\n";
    }

    public String generatePgnFromGameDto(GameDto gameDto) {
        return "[Site \"chesser\"]\n" +
            "[Date \"" + gameDto.getCreated().toString() + "\"]\n" +
            "[White \"" + gameDto.getWhitePlayer().getUsername() + "\"]\n" +
            "[Black \"" + gameDto.getBlackPlayer().getUsername() + "\"]\n" +
            "[Result \"*\"]\n" +
            "[Termination \"%\"]\n\n";
    }

    public String generatePgn(Instant created, String whiteUsername, String blackUsername) {
        return "[Site \"chesser\"]\n" +
            "[Date \"" + created.toString() + "\"]\n" +
            "[White \"" + whiteUsername + "\"]\n" +
            "[Black \"" + blackUsername + "\"]\n" +
            "[Result \"*\"]\n" +
            "[Termination \"%\"]\n\n";
    }

    public String addMove(String pgn, Color color, int moveNo, String move) {
        if (color.equals(Color.WHITE)) {
            return pgn + moveNo + ". " + move + " ";
        } else {
            return pgn + move + " ";
        }
    }

    public String addResult(String pgn, GameResult result, GameTermination termination) {
        pgn = pgn.replace("*", result.toString());
        pgn = pgn.replace("%", termination.toString());
        pgn += result.toString();

        return pgn;
    }

    public String getMoveText(String pgn) {
        int start = pgn.lastIndexOf("]") + 3;
        int end;

        if (pgn.lastIndexOf("1-0") != -1) {
            end = pgn.lastIndexOf("1-0");
        } else if (pgn.lastIndexOf("0-1") != -1) {
            end = pgn.lastIndexOf("0-1");
        } else {
            end = pgn.lastIndexOf("1/2-1/2");
        }

        return pgn.substring(start, end);
    }
}
