package edu.nazarenko.chesser.model.game;

import edu.nazarenko.chesser.service.game.Color;

public enum GameResult {
    DRAW, WHITE_WIN, BLACK_WIN;

    public static GameResult fromWinner(Color color) {
        return color.equals(Color.WHITE) ? WHITE_WIN : BLACK_WIN;
    }

    public String toString() {
        switch (this) {
            case DRAW:
                return "1/2-1/2";
            case WHITE_WIN:
                return "1-0";
            case BLACK_WIN:
                return "0-1";
            default:
                return "*";
        }
    }
}
