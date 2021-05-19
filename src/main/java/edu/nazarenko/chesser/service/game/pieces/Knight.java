package edu.nazarenko.chesser.service.game.pieces;

import edu.nazarenko.chesser.service.game.Board;
import edu.nazarenko.chesser.service.game.Color;
import edu.nazarenko.chesser.service.game.Move;
import edu.nazarenko.chesser.service.game.Square;

import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece {

    public Knight(Color color) {
        this.color = color;
        this.pieceType = PieceType.KNIGHT;
    }

    @Override
    public List<Move> getMoves() {
        List<Move> legalMoves = new ArrayList<>();
        Board board = square.getBoard();

        int[][] diffs = {
            {2, 1},
            {2, -1},
            {-2, -1},
            {-2, 1},
            {1, 2},
            {1, -2},
            {-1, -2},
            {-1, 2},
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

        return legalMoves;
    }
}
