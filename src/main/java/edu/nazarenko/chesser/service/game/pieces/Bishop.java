package edu.nazarenko.chesser.service.game.pieces;

import edu.nazarenko.chesser.service.game.Board;
import edu.nazarenko.chesser.service.game.Color;
import edu.nazarenko.chesser.service.game.Move;
import edu.nazarenko.chesser.service.game.Square;

import java.util.ArrayList;
import java.util.List;

public class Bishop extends Piece {

    public Bishop(Color color) {
        this.color = color;
        this.pieceType = PieceType.BISHOP;
    }

    @Override
    public List<Move> getMoves() {
        List<Move> legalMoves = new ArrayList<>();
        Board board = square.getBoard();

        int[][] diffs = {
            {1, 1},
            {1, -1},
            {-1, 1},
            {-1, -1}
        };

        for (int[] diff : diffs) {
            int diffRank = diff[0];
            int diffFile = diff[1];

            int rank = square.getRank() + diffRank;
            int file = square.getFile() + diffFile;

            while (
                board.hasSquare(rank, file) &&
                    board.getSquare(rank, file).isEmpty()
            ) {
                legalMoves.add(new Move(board, square, board.getSquare(rank, file)));
                rank += diffRank;
                file += diffFile;
            }

            if (
                board.hasSquare(rank, file) &&
                    !board.getSquare(rank, file).getPiece().getColor().equals(color)
            ) {
                legalMoves.add(new Move(board, square, board.getSquare(rank, file)));
            }
        }

        return legalMoves;
    }
}
