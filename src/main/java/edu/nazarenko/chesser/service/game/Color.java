package edu.nazarenko.chesser.service.game;

public enum Color {
    WHITE, BLACK;

    public static Color charToColor(char c) {

        if (c >= 'a' && c <= 'z') {
            return BLACK;
        }

        return WHITE;
    }

    public Color opposite() {
        return this == WHITE ? BLACK : WHITE;
    }
}
