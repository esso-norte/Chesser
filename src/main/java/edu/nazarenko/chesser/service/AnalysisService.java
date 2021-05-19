package edu.nazarenko.chesser.service;

import edu.nazarenko.chesser.controller.dto.*;
import edu.nazarenko.chesser.model.analysis.AnalysisGame;
import edu.nazarenko.chesser.model.analysis.AnalysisPosition;
import edu.nazarenko.chesser.model.game.Game;
import edu.nazarenko.chesser.repository.*;
import edu.nazarenko.chesser.service.game.*;
import edu.nazarenko.chesser.service.game.pieces.Piece;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class AnalysisService {

    private final int MAX_DEPTH = 5;

    private final GameRepository gameRepository;
    private final AnalysisGameRepository analysisGameRepository;
    private final AnalysisPositionRepository analysisPositionRepository;

    private final PGNService pgnService;
    private final FENService fenService;

    public void analyseGame(Game game) {
//        AnalysisGame analysisGame = analysisGameRepository.findByGame(game);
//
//        if (analysisGame != null) {
//            return;
//        }
//
//        analysisGame = AnalysisGame.builder()
//            .game(game)
//            .finished(false)
//            .build();
//        analysisGameRepository.save(analysisGame);
//
//        String pgn = game.getPgn();
//        String moveText = pgnService.getMoveText(pgn);
//        Board board = new Board(new FenDto(fenService.startingPosition()), null);
//        analysePosition(board, MAX_DEPTH);
//
//        List<Move> moves = new ArrayList<>();
//        int indexStart = 0;
//
//        while (indexStart < moveText.length()) {
//            System.out.println(moves.size());
//            int indexEnd = moveText.indexOf(".", moveText.indexOf(".", indexStart) + 1);
//            String moveFull = indexEnd != -1 ? moveText.substring(indexStart, indexEnd) : moveText.substring(indexStart);
//            moveFull = moveFull.substring(0, moveFull.lastIndexOf(" "));
//
//            String[] split = moveFull.split(" ");
//            Move move = board.getMove(split[1], Color.WHITE);
//            moves.add(move);
//            move.makeMove();
//            System.out.println(board.getFenDto().toString());
//            analysePosition(board, MAX_DEPTH);
//
//            if (split.length > 2) {
//                move = board.getMove(split[2], Color.BLACK);
//                moves.add(move);
//                move.makeMove();
//                System.out.println(board.getFenDto().toString());
//                analysePosition(board, MAX_DEPTH);
//            }
//
//            indexStart += moveFull.length() + 1;
//        }
//
//        analysisGame.setFinished(true);
//        analysisGameRepository.save(analysisGame);
    }

    public AnalysisPosition analysePosition(Board board, int depth) {
        AnalysisPosition analysisPosition = analysisPositionRepository.findByFen(board.getFenDto().toString());

        if (analysisPosition != null && analysisPosition.getDepth() >= depth) {
            return analysisPosition;
        }

        if (analysisPosition == null) {
            analysisPosition = new AnalysisPosition();
        }

        if (depth == 0) {

            analysisPosition.setFen(board.getFenDto().toString());
            analysisPosition.setDepth(0);
            analysisPosition.setEvaluation(evaluate(board));

            analysisPositionRepository.save(analysisPosition);

            return analysisPosition;
        }

        List<Move> moves = board.getMoves();
        boolean isWhite = board.getFenDto().getActiveColor().equals(Color.WHITE);
        float evaluation = isWhite ? -99.0F : 99.0F;

        for (Move move: moves) {
            Board newBoard = new Board(board);
            move.setBoard(newBoard);
            move.makeMove();
            evaluation = isWhite ?
                Math.max(evaluation, analysePosition(newBoard, depth - 1).getEvaluation()) :
                Math.min(evaluation, analysePosition(newBoard, depth - 1).getEvaluation());
        }

        analysisPosition.setFen(board.getFenDto().toString());
        analysisPosition.setDepth(depth);
        analysisPosition.setEvaluation(evaluation);

        analysisPositionRepository.save(analysisPosition);

        return analysisPosition;
    }

    private float evaluate(Board board) {
        float result = 0.0F;

        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                Square square = board.getSquare(rank, file);

                if (!square.isEmpty()) {
                    Piece piece = square.getPiece();
                    float pieceValue = 0.0F;

                    switch (piece.getPieceType()) {
                        case PAWN:
                            pieceValue = 1.0F;
                            break;
                        case KNIGHT:
                        case BISHOP:
                            pieceValue = 3.0F;
                            break;
                        case ROOK:
                            pieceValue = 5.0F;
                            break;
                        case QUEEN:
                            pieceValue = 9.0F;
                            break;
                    }

                    result += piece.is(Color.WHITE) ? pieceValue : -pieceValue;
                }
            }
        }

        return result;
    }
}
