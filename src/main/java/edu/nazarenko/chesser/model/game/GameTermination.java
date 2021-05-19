package edu.nazarenko.chesser.model.game;

public enum GameTermination {
    MATE, STALEMATE, INSUFFICIENT_MATERIAL, FIFTY_MOVES, DRAW_AGREEMENT, RESIGNATION;

    public String toString() {
        switch (this) {
            case MATE:
                return "Mate";
            case STALEMATE:
                return "Stalemate";
            case INSUFFICIENT_MATERIAL:
                return "Insufficient Material";
            case FIFTY_MOVES:
                return "Fifty-moves Rule";
            case DRAW_AGREEMENT:
                return "Agreement to Draw";
            case RESIGNATION:
                return "Resignation";
            default:
                return "*";
        }
    }
}
