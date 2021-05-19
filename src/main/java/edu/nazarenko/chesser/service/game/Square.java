package edu.nazarenko.chesser.service.game;

import edu.nazarenko.chesser.service.game.pieces.Piece;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Square {

    private Board board;
    private Integer rank;
    private Integer file;
    private boolean isEmpty;
    private Piece piece;

    public Square(Board board, Square squareToCopy) {
        this.board = board;
        this.rank = squareToCopy.getRank();
        this.file = squareToCopy.getFile();
        this.isEmpty = squareToCopy.isEmpty();

        if (!this.isEmpty) {
            this.piece = Piece.newPiece(squareToCopy.getPiece().getPieceType(), squareToCopy.getPiece().getColor());
            this.piece.setSquare(this);
        }
    }

    public Square(Board board, Integer rank, Integer file) {
        this.board = board;
        this.rank = rank;
        this.file = file;
        isEmpty = true;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
        this.isEmpty = false;
        this.piece.setSquare(this);
    }

    public void empty() {
        piece = null;
        isEmpty = true;
    }

    public boolean is(int rank, int file) {
        return this.rank == rank && this.file == file;
    }

    public boolean rankIs(int rank) {
        return this.rank == rank;
    }

    public boolean fileIs(int file) {
        return this.file == file;
    }

    public boolean isRookStartingSquare(Color color) {
        return isRookStartingSquareKingSide(color) || isRookStartingSquareQueenSide(color);
    }

    public boolean isRookStartingSquareKingSide(Color color) {
        return color.equals(Color.WHITE) ?
            rank == 7 && file == 7 :
            rank == 0 && file == 7;
    }

    public boolean isRookStartingSquareQueenSide(Color color) {
        return color.equals(Color.WHITE) ?
            rank == 7 && file == 0 :
            rank == 0 && file == 0;
    }

    public boolean isKingStartingSquare(Color color) {
        return color.equals(Color.WHITE) ?
            rank == 7 && file == 4 :
            rank == 0 && file == 4;
    }

    public boolean isKingSideCastlingTargetSquare(Color color) {
        return color.equals(Color.WHITE) ?
            rank == 7 && file == 6 :
            rank == 0 && file == 6;
    }

    public boolean isQueenSideCastlingTargetSquare(Color color) {
        return color.equals(Color.WHITE) ?
            rank == 7 && file == 2 :
            rank == 0 && file == 2;
    }

    public String notation() {
        return String.valueOf((char) ('a' + file)) + (char) ('0' + 8 - rank);
    }

    public char toChar() {
        return isEmpty ? ' ' : piece.toChar();
    }

    public boolean equals(Square square) {
        return rank.equals(square.getRank()) && file.equals(square.getFile());
    }
}
