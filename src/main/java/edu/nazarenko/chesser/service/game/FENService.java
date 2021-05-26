package edu.nazarenko.chesser.service.game;

import edu.nazarenko.chesser.service.game.Square;
import org.json.JSONArray;
import org.springframework.stereotype.Service;

@Service
public class FENService {

    public String startingPosition() {
        return "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    }

    public String generateFenBoard(Square[][] board) {
        String fenBoard = "";
        int empty = 0;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j].isEmpty()) {
                    empty++;
                } else {
                    if (empty > 0) {
                        fenBoard += String.valueOf(empty);
                    }
                    empty = 0;
                    fenBoard += board[i][j].toChar();
                }
            }

            if (empty > 0) {
                fenBoard += String.valueOf(empty);
            }
            empty = 0;

            if (i < 7) {
                fenBoard += "/";
            }
        }

        return fenBoard;
    }

    public String createFenJson() {
        return "[\"" + startingPosition() + "\"]";
    }

    public String addFenToJson(String fenJson, String fen) {
        JSONArray fenArray = new JSONArray(fenJson);
        fenArray.put(fen);
        return fenArray.toString();
    }
}
