package edu.nazarenko.chesser.service.game.pieces;

import edu.nazarenko.chesser.exceptions.ChesserException;
import edu.nazarenko.chesser.service.game.Board;
import edu.nazarenko.chesser.service.game.Color;
import edu.nazarenko.chesser.service.game.Move;
import edu.nazarenko.chesser.service.game.Square;

import java.util.ArrayList;
import java.util.List;

public class King extends Piece {

    private boolean canCastleKingSide;
    private boolean canCastleQueenSide;

    public King(Color color) {
        this.color = color;
        this.pieceType = PieceType.KING;
    }

    public boolean canCastleKingSide() {
        return canCastleKingSide;
    }

    public boolean canCastleQueenSide() {
        return canCastleQueenSide;
    }

    public void setCanCastleKingSide(boolean canCastleKingSide) {
        this.canCastleKingSide = canCastleKingSide;
    }

    public void setCanCastleQueenSide(boolean canCastleQueenSide) {
        this.canCastleQueenSide = canCastleQueenSide;
    }

    @Override
    public List<Move> getMoves() {
        List<Move> legalMoves = new ArrayList<>();
        Board board = square.getBoard();

        int[][] diffs = {
            {1, 1},
            {1, -1},
            {-1, -1},
            {-1, 1},
            {-1, 0},
            {1, 0},
            {0, 1},
            {0, -1},
        };

        for (int[] diff : diffs) {
            int rank = square.getRank() + diff[0];
            int file = square.getFile() + diff[1];

            if (
                board.hasSquare(rank, file) &&
                    (
                        board.getSquare(rank, file).isEmpty() ||
                            !board.getSquare(rank, file).getPiece().getColor().equals(color)
                    )
            ) {
                legalMoves.add(new Move(board, square, board.getSquare(rank, file)));
            }
        }

        if (
            canCastleKingSide &&
                board.getSquare(color.equals(Color.WHITE) ? 7 : 0, 6).isEmpty() &&
                board.getSquare(color.equals(Color.WHITE) ? 7 : 0, 5).isEmpty()
        ) {
            Square to = board.getSquare(color.equals(Color.WHITE) ? 7 : 0, 6);
            legalMoves.add(new Move(board, square, to));
        }

        if (
            canCastleQueenSide &&
                board.getSquare(color.equals(Color.WHITE) ? 7 : 0, 1).isEmpty() &&
                board.getSquare(color.equals(Color.WHITE) ? 7 : 0, 2).isEmpty() &&
                board.getSquare(color.equals(Color.WHITE) ? 7 : 0, 3).isEmpty()
        ) {
            Square to = board.getSquare(color.equals(Color.WHITE) ? 7 : 0, 2);
            legalMoves.add(new Move(board, square, to));
        }

        return legalMoves;
    }

    public static King findKing(Board board, Color color) {
        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                Square square = board.getSquare(rank, file);

                if (
                    !square.isEmpty() &&
                    square.getPiece().isKing() &&
                    square.getPiece().is(color)
                ) {
                    return (King) square.getPiece();
                }
            }
        }

        throw new ChesserException("No King found!");
    }
}
