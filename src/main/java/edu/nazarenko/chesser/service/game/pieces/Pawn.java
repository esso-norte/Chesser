package edu.nazarenko.chesser.service.game.pieces;

import edu.nazarenko.chesser.service.game.Board;
import edu.nazarenko.chesser.service.game.Color;
import edu.nazarenko.chesser.service.game.Move;
import edu.nazarenko.chesser.service.game.Square;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {

    public Pawn(Color color) {
        this.color = color;
        this.pieceType = PieceType.PAWN;
    }

    @Override
    public List<Move> getMoves() {
        List<Move> legalMoves = new ArrayList<>();
        Board board = square.getBoard();
        Integer rank = square.getRank();
        Integer file = square.getFile();

        // First double move
        if (color.equals(Color.WHITE)) {
            if (
                rank == 6 &&
                    board.getSquare(4, file).isEmpty() &&
                    board.getSquare(5, file).isEmpty()
            ) {
                legalMoves.add(new Move(board, square, board.getSquare(4, file)));
            }
        } else {
            if (
                rank == 1 &&
                    board.getSquare(2, file).isEmpty() &&
                    board.getSquare(3, file).isEmpty()
            ) {
                legalMoves.add(new Move(board, square, board.getSquare(3, file)));
            }
        }

        int rankDiff = color.equals(Color.WHITE) ? -1 : 1;

        // Ordinary advance
        if (board.getSquare(rank + rankDiff, file).isEmpty()) {
            if (rank + rankDiff == 0 || rank+rankDiff == 7) {
                legalMoves.add(new Move(board, square, board.getSquare(rank + rankDiff, file), "Q"));
                legalMoves.add(new Move(board, square, board.getSquare(rank + rankDiff, file), "R"));
                legalMoves.add(new Move(board, square, board.getSquare(rank + rankDiff, file), "N"));
                legalMoves.add(new Move(board, square, board.getSquare(rank + rankDiff, file), "B"));
            } else {
                legalMoves.add(new Move(board, square, board.getSquare(rank + rankDiff, file)));
            }
        }

        // Captures
        if (
            board.hasSquare(rank + rankDiff, file - 1) &&
                !board.getSquare(rank + rankDiff, file - 1).isEmpty() &&
                !board.getSquare(rank + rankDiff, file - 1).getPiece().getColor().equals(color)
        ) {
            if (rank + rankDiff == 0 || rank+rankDiff == 7) {
                legalMoves.add(new Move(board, square, board.getSquare(rank + rankDiff, file - 1), "Q"));
                legalMoves.add(new Move(board, square, board.getSquare(rank + rankDiff, file - 1), "R"));
                legalMoves.add(new Move(board, square, board.getSquare(rank + rankDiff, file - 1), "N"));
                legalMoves.add(new Move(board, square, board.getSquare(rank + rankDiff, file - 1), "B"));
            } else {

                legalMoves.add(new Move(board, square, board.getSquare(rank + rankDiff, file - 1)));
            }
        }

        if (
            board.hasSquare(rank + rankDiff, file + 1) &&
                !board.getSquare(rank + rankDiff, file + 1).isEmpty() &&
                !board.getSquare(rank + rankDiff, file + 1).getPiece().getColor().equals(color)
        ) {
            if (rank + rankDiff == 0 || rank+rankDiff == 7) {
                legalMoves.add(new Move(board, square, board.getSquare(rank + rankDiff, file + 1), "Q"));
                legalMoves.add(new Move(board, square, board.getSquare(rank + rankDiff, file + 1), "R"));
                legalMoves.add(new Move(board, square, board.getSquare(rank + rankDiff, file + 1), "N"));
                legalMoves.add(new Move(board, square, board.getSquare(rank + rankDiff, file + 1), "B"));
            } else {
                legalMoves.add(new Move(board, square, board.getSquare(rank + rankDiff, file + 1)));
            }
        }

        // En passant capture
        Square target = square.getBoard().getEnPassantTarget();

        if (
            target != null &&
                ((color.equals(Color.WHITE) && target.getRank() == 2) ||
                (color.equals(Color.BLACK) && target.getRank() == 5))
        ) {
            if (
                (target.getRank() == rank + rankDiff && target.getFile() == file + 1) ||
                    (target.getRank() == rank + rankDiff && target.getFile() == file - 1)
            ) {
                legalMoves.add(new Move(board, square, target));
            }
        }

        return legalMoves;
    }
}
