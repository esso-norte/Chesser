package edu.nazarenko.chesser.service.game;

import edu.nazarenko.chesser.exceptions.ChesserException;
import edu.nazarenko.chesser.service.game.pieces.Piece;

import java.util.List;

public class Move {
    private Board board;
    private final Piece piece;

    private Piece promotionPiece = null;

    private Square from;
    private Square to;

    private Boolean isCapture;

    private Boolean isCastles = null;
    private Boolean isShortCastles = null;
    private Boolean isLongCastles = null;

    private Boolean isDoublePawnAdvance = null;
    private Boolean isEnPassant = null;

    public Move(Move moveToCopy) {
        board = new Board(moveToCopy.getBoard());
        from = board.getSquare(moveToCopy.getFrom().getRank(), moveToCopy.getFrom().getFile());
        to = board.getSquare(moveToCopy.getTo().getRank(), moveToCopy.getTo().getFile());
        piece = from.getPiece();
        promotionPiece = moveToCopy.getPromotionPiece();
    }

    public Move(Board board, Square from, Square to) {
        this.board = board;
        this.from = from;
        this.to = to;

        if (from.isEmpty()) {
            throw new ChesserException("Starting square of a move should contain a piece");
        }

        this.piece = from.getPiece();
    }

    public Move(Board board, Square from, Square to, String promotionNotation) {
        this(board, from, to);

        if (promotionNotation != null) {
            promotionPiece = Piece.charToPiece(promotionNotation.charAt(0));
            promotionPiece.setColor(piece.getColor());
        }
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
        this.from = board.getSquare(this.from.getRank(), this.from.getFile());
        this.to = board.getSquare(this.to.getRank(), this.to.getFile());
        this.piece.setSquare(from);
    }

    public Piece getPiece() {
        return piece;
    }

    public Piece getPromotionPiece() {
        return promotionPiece;
    }

    public Square getFrom() {
        return from;
    }

    public Square getTo() {
        return to;
    }

    public boolean isCapture() {

        if (isCapture != null) {
            return isCapture;
        }

        if (!to.isEmpty()) {
            if (to.getPiece().is(piece.getColor())) {
                throw new ChesserException("Pieces can capture only pieces of opposite color");
            } else {
                return isCapture = true;
            }
        } else {
            return isCapture = false;
        }
    }

    public boolean isCastles() {

        if (isCastles != null) {
            return isCastles;
        }

        return isCastles = (isShortCastles() || isLongCastles());
    }

    public boolean isShortCastles() {

        if (isShortCastles != null) {
            return isShortCastles;
        }

        if (!piece.isKing()) {
            return isShortCastles = false;
        }

        return isShortCastles = (from.getFile() - to.getFile() == -2);
    }

    public boolean isLongCastles() {

        if (isLongCastles != null) {
            return isLongCastles;
        }

        if (!piece.isKing()) {
            return isLongCastles = false;
        }

        return isLongCastles = (from.getFile() - to.getFile() == 2);
    }

    public boolean isDoublePawnAdvance() {

        if (isDoublePawnAdvance != null) {
            return isDoublePawnAdvance;
        }

        if (!piece.isPawn()) {
            return isDoublePawnAdvance = false;
        }

        if (from.getRank() == 1 && to.getRank() == 3) {
            return isDoublePawnAdvance = true;
        }

        return isDoublePawnAdvance = (from.getRank() == 6 && to.getRank() == 4);
    }

    public boolean isEnPassant() {

        if (isEnPassant != null) {
            return isEnPassant;
        }

        if (!piece.isPawn()) {
            return isEnPassant = false;
        }

        String notation = board.getFenDto().getFenEnPassantTarget();

        if (notation.equals("-")) {
            return isEnPassant = false;
        }

        Square target = board.getSquare(notation);

        return isEnPassant = (to.getRank().equals(target.getRank()) && to.getFile().equals(target.getFile()));
    }

    public boolean isLegal() {
        if (piece.isPossibleMove(to)) {
            Move moveCopy = new Move(this);
            moveCopy.makeMove();
            return !moveCopy.getBoard().isCheck(piece.getColor());
        } else {
            return false;
        }
    }

    public String makeMove() {
        Color color = piece.getColor();
        String moveNotation = notation();

        if (isShortCastles()) { // Short Castling
            board.getFenDto().disableCastling(color);

            // Update board
            to.setPiece(from.getPiece());
            from.empty();

            Square rookSquare = board.getRookStartingSquareKingSide(color);
            board.getSquare(to.getRank(), to.getFile() - 1).setPiece(rookSquare.getPiece());
            rookSquare.empty();
        } else if (isLongCastles()) { // Long Castling
            board.getFenDto().disableCastling(color);

            // Update board
            to.setPiece(from.getPiece());
            from.empty();

            Square rookSquare = board.getRookStartingSquareQueenSide(color);
            board.getSquare(to.getRank(), to.getFile() + 1).setPiece(rookSquare.getPiece());
            rookSquare.empty();
        } else if (isEnPassant()) { // en-passant capture
            Square capturedPawnSquare = color.equals(Color.WHITE) ?
                board.getSquare(to.getRank() + 1, to.getFile()) :
                board.getSquare(to.getRank() - 1, to.getFile());

            // Update board
            capturedPawnSquare.empty();
            to.setPiece(piece);
            from.empty();

            board.getFenDto().resetHalfMoveClock();
        } else {
            // Update castling
            if (piece.isKing()) {
                board.getFenDto().disableCastling(color);
            } else if (piece.isRook() && from.isRookStartingSquare(color)) {

                if (from.isRookStartingSquareQueenSide(color)) {
                    board.getFenDto().disableCastlingQueenSide(color);
                } else {
                    board.getFenDto().disableCastlingKingSide(color);
                }
            }

            // Update en-passant target
            board.getFenDto().setFenEnPassantTarget(
                isDoublePawnAdvance() ?
                    board.getEnPassantTarget(to).notation() :
                    "-"
            );

            // Update half-move clock
            if (piece.isPawn() || isCapture()) {
                board.getFenDto().resetHalfMoveClock();
            } else {
                board.getFenDto().incHalfMoveClock();
            }

            // Update board
            if (piece.isPawn() && (to.rankIs(0) || to.rankIs(7))) {
                to.setPiece(promotionPiece);
            } else {
                to.setPiece(piece);
            }

            from.empty();
        }

        if (board.isCheck(color.opposite())) {
            moveNotation += "+";
        }

        // Update active color
        board.updateFenBoard();
        board.getFenDto().setActiveColor(color.opposite());

        // Update move clock
        if (color.equals(Color.BLACK)) {
            board.getFenDto().incMoveClock();
        }

        board.resetLegalMoves();

        return moveNotation;
    }

    public String notation() {

        if (isShortCastles()) {
            return "O-O";
        }

        if (isLongCastles()) {
            return "O-O-O";
        }

        if (isEnPassant()) {
            return from.notation().charAt(0) + "x" + to.notation();
        }

        String fromNotation = piece.notation();
        String toNotation = to.notation();

        if (piece.isPawn()) {

            if (isCapture()) {
                fromNotation += from.notation().charAt(0);
            }
        } else {
            List<Piece> whichCanMoveTo = board.whoCanMoveTo(to, piece.getColor(), piece.getPieceType());

            if (whichCanMoveTo.size() > 1) {
                boolean noSameRank = true;
                boolean noSameFile = true;

                for (Piece piece : whichCanMoveTo) {
                    if (!piece.getRank().equals(from.getRank()) || !piece.getFile().equals(from.getFile())) {
                        if (piece.getRank().equals(from.getPiece().getRank())) {
                            noSameRank = false;
                        } else if (piece.getFile().equals(from.getPiece().getFile())) {
                            noSameFile = false;
                        }
                    }
                }

                if ((!noSameRank) && (!noSameFile)) {
                    fromNotation += from.notation();
                } else if (noSameFile) {
                    fromNotation += from.notation().charAt(0);
                } else {
                    fromNotation += from.notation().charAt(1);
                }
            }
        }

        String notation = isCapture() ? fromNotation + "x" + toNotation : fromNotation + toNotation;

        if (promotionPiece != null) {
            notation += "=" + promotionPiece.notation();
        }

        return notation;
    }
}
