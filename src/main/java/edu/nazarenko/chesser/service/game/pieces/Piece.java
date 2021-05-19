package edu.nazarenko.chesser.service.game.pieces;

import edu.nazarenko.chesser.exceptions.ChesserException;
import edu.nazarenko.chesser.service.game.Color;
import edu.nazarenko.chesser.service.game.Move;
import edu.nazarenko.chesser.service.game.Square;
import lombok.Data;

import java.util.List;

@Data
public abstract class Piece {

    protected Square square;
    protected Color color;
    protected PieceType pieceType;

    public static Piece newPiece(PieceType type, Color color) {
        switch (type) {
            case PAWN:
                return new Pawn(color);
            case ROOK:
                return new Rook(color);
            case KNIGHT:
                return new Knight(color);
            case BISHOP:
                return new Bishop(color);
            case QUEEN:
                return new Queen(color);
            case KING:
                return new King(color);
        }

        throw new ChesserException("Can't create piece for some reason.");
    }

    public static Piece charToPiece(char c) {

        if (c == 'p' || c == 'P') {
            return new Pawn(Color.charToColor(c));
        }

        if (c == 'n' || c == 'N') {
            return new Knight(Color.charToColor(c));
        }

        if (c == 'b' || c == 'B') {
            return new Bishop(Color.charToColor(c));
        }

        if (c == 'r' || c == 'R') {
            return new Rook(Color.charToColor(c));
        }

        if (c == 'q' || c == 'Q') {
            return new Queen(Color.charToColor(c));
        }

        if (c == 'k' || c == 'K') {
            return new King(Color.charToColor(c));
        }

        throw new ChesserException("Can't create piece from char: " + c);
    }

    public Square getSquare() {
        return square;
    }

    public Integer getRank() {
        return square.getRank();
    }

    public Integer getFile() {
        return square.getFile();
    }

    public void setSquare(Square square) {
        this.square = square;
    }

    public boolean is(PieceType type) {
        return pieceType.equals(type);
    }

    public boolean is(Color color) {
        return this.color.equals(color);
    }

    public boolean isPawn() {
        return pieceType.equals(Piece.PieceType.PAWN);
    }

    public boolean isKnight() {
        return pieceType.equals(Piece.PieceType.KNIGHT);
    }

    public boolean isBishop() {
        return pieceType.equals(Piece.PieceType.BISHOP);
    }

    public boolean isRook() {
        return pieceType.equals(Piece.PieceType.ROOK);
    }

    public boolean isQueen() {
        return pieceType.equals(Piece.PieceType.QUEEN);
    }

    public boolean isKing() {
        return pieceType.equals(Piece.PieceType.KING);
    }

    public boolean isWhite() {
        return color.equals(Color.WHITE);
    }

    public boolean isBlack() {
        return color.equals(Color.BLACK);
    }

    public abstract List<Move> getMoves();

    public boolean isPossibleMove(Square to) {
        List<Move> legalMoves = getMoves();

        for (Move move: legalMoves) {
            if (move.getTo().equals(to)) {
                return true;
            }
        }

        return false;
    }

    public String toString() {
        return color + " " + pieceType;
    }

    public String notation() {
        if (isPawn()) {
            return "";
        } else {
            return ("" + toChar()).toUpperCase();
        }
    }

    public char toChar() {
        int diff = color.equals(Color.WHITE) ? 'A' - 'a' : 0;

        switch (pieceType) {
            case PAWN:
                return (char) ('p' + diff);
            case KNIGHT:
                return (char) ('n' + diff);
            case BISHOP:
                return (char) ('b' + diff);
            case ROOK:
                return (char) ('r' + diff);
            case QUEEN:
                return (char) ('q' + diff);
            case KING:
                return (char) ('k' + diff);
        }

        throw new ChesserException("Square to char conversion error");
    }

    public enum PieceType {
        PAWN, KNIGHT, BISHOP, ROOK, QUEEN, KING
    }
}
