package edu.nazarenko.chesser.service.game;

import edu.nazarenko.chesser.controller.dto.FenDto;
import edu.nazarenko.chesser.exceptions.ChesserException;
import edu.nazarenko.chesser.service.game.pieces.King;
import edu.nazarenko.chesser.service.game.pieces.Piece;

import java.util.ArrayList;
import java.util.List;

public class Board {

    private final FenDto fenDto;
    private final Square[][] board;
    private String pgn;
    private Boolean noLegalMoves = null;

    public Board(Board boardToCopy) {
        this.fenDto = new FenDto(boardToCopy.getFenDto());
        this.board = new Square[8][8];

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                this.board[i][j] = new Square(this, boardToCopy.getSquare(i, j));
            }
        }

        this.pgn = boardToCopy.getPgn();
        this.noLegalMoves = boardToCopy.noLegalMoves;
    }

    public Board(FenDto fenDto, String pgn) {
        this.fenDto = fenDto;
        this.pgn = pgn;
        String fenBoard = fenDto.getFenBoard();

        board = new Square[8][8];
        int row = 0;
        int col = 0;

        for (int i = 0; i < fenBoard.length(); i++) {
            char c = fenBoard.charAt(i);

            if (c == '/') {
                row++;
                col = 0;
            } else if (c >= '1' && c <= '8') {
                for (int j = 0; j < Character.getNumericValue(c); j++) {
                    board[row][col] = new Square(this, row, col);
                    col++;
                }
            } else {
                board[row][col] = Square.builder()
                    .board(this)
                    .rank(row)
                    .file(col)
                    .isEmpty(false)
                    .piece(Piece.charToPiece(c))
                    .build();
                board[row][col].getPiece().setSquare(board[row][col]);

                if (board[row][col].getPiece().isKing()) {
                    King king = (King) board[row][col].getPiece();
                    king.setCanCastleKingSide(fenDto.canCastleKingSide(king.getColor()));
                    king.setCanCastleQueenSide(fenDto.canCastleQueenSide(king.getColor()));
                }

                col++;
            }
        }
    }

    public FenDto getFenDto() {
        return fenDto;
    }

    public String getPgn() {
        return pgn;
    }

    public void setPgn(String pgn) {
        this.pgn = pgn;
    }

    public boolean hasSquare(int rank, int file) {
        return rank >= 0 && rank <= 7 && file >= 0 && file <= 7;
    }

    public Square getSquare(int rank, int file) {
        if (!hasSquare(rank, file)) {
            throw new ChesserException("Square (" + rank + ", " + file + ") does not exist");
        }

        return board[rank][file];
    }

    public Square getSquare(String notation) {
        if (
            notation.length() != 2 ||
                notation.charAt(0) < 'a' ||
                notation.charAt(0) > 'h' ||
                notation.charAt(1) < '1' ||
                notation.charAt(1) > '8'
        ) {
            throw new ChesserException(notation + " is not a correct notation for a square");
        }

        int rank = 8 - notation.charAt(1) + '0';
        int file = notation.charAt(0) - 'a';

        return getSquare(rank, file);
    }

    public Move getMove(String notation, Color color) {

        if (notation.charAt(notation.length() - 1) == '+') {
            notation = notation.substring(0, notation.length() - 1);
        }

        if (notation.equals("O-O")) {
            return new Move(
                this,
                getSquare(color.equals(Color.WHITE) ? 7 : 0, 4),
                getSquare(color.equals(Color.WHITE) ? 7 : 0, 6)
            );
        } else if (notation.equals("O-O-O")) {
            return new Move(
                this,
                getSquare(color.equals(Color.WHITE) ? 7 : 0, 4),
                getSquare(color.equals(Color.WHITE) ? 7 : 0, 2)
            );
        }

        Piece piece;
        String promotion = null;
        Integer rankFrom = null;
        Integer fileFrom = null;
        Integer rankTo = null;
        Integer fileTo = null;

        if (notation.charAt(0) >= 'A' && notation.charAt(0) <= 'Z') {
            piece = Piece.charToPiece(notation.charAt(0));
            piece.setColor(color);
            notation = notation.substring(1);
        } else {
            piece = Piece.newPiece(Piece.PieceType.PAWN, color);
            fileFrom = ((int) notation.charAt(0)) - ((int) 'a');
        }

        if (notation.charAt(notation.length() - 2) == '=') {
            promotion = "" + notation.charAt(notation.length() - 1);
            notation = notation.substring(0, notation.length() - 2);
        }

        if (notation.contains("x")) {
            String[] split = notation.split("x");

            for (int i = 0; i < split[0].length(); i++) {
                char c = split[0].charAt(i);

                if (c >= '0' && c <= '9') {
                    rankFrom = 7 - ((int) c) + ((int) '0');
                } else if (c >= 'a' && c <= 'z') {
                    fileFrom = ((int) c) - ((int) 'a');
                }
            }

            piece = findPiece(piece.getPieceType(), piece.getColor(), rankFrom, fileFrom);

            if (piece == null) {
                throw new ChesserException("No piece found for a move");
            }

            for (int i = 0; i < split[1].length() && split[1].charAt(i) != '='; i++) {
                char c = split[1].charAt(i);

                if (c >= '0' && c <= '9') {
                    rankTo = 7 - ((int) c) + ((int) '0');
                } else if (c >= 'a' && c <= 'z') {
                    fileTo = ((int) c) - ((int) 'a');
                }
            }

            return promotion == null ?
                new Move(this, piece.getSquare(), getSquare(rankTo, fileTo)) :
                new Move(this, piece.getSquare(), getSquare(rankTo, fileTo), promotion);
        } else {
            String toNotation = notation.substring(notation.length() - 2, notation.length());
            Square to = getSquare(toNotation);

            int fromNotationLength = notation.length() - 2;

            for (int i = 0; i < fromNotationLength; i++) {
                char c = notation.charAt(i);

                if (c >= '0' && c <= '9') {
                    rankFrom = 7 - ((int) c) + ((int) '0');
                } else if (c >= 'a' && c <= 'z') {
                    fileFrom = ((int) c) - ((int) 'a');
                }
            }

            piece = findPiece(piece.getPieceType(), piece.getColor(), rankFrom, fileFrom);

            if (piece == null) {
                throw new ChesserException("No piece found for a move");
            }

            return promotion == null ?
                new Move(this, piece.getSquare(), to) :
                new Move(this, piece.getSquare(), to, promotion);
        }
    }

    public boolean isCheck(Color color) {
        King king = King.findKing(this, color);

        for (Square[] rank: board) {
            for (Square square: rank) {
                if (
                    !square.isEmpty() &&
                    !square.getPiece().is(color) &&
                    square.getPiece().isPossibleMove(king.getSquare())
                ) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isMate(Color color) {
        return isCheck(color) && noLegalMoves(color);
    }

    public boolean isStalemate(Color color) {
        return !isCheck(color) && noLegalMoves(color);
    }

    public boolean insufficientMaterial() {
        int whiteBishopsOrKnights = 0;
        int blackBishopsOrKnights = 0;

        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                Square square = getSquare(rank, file);
                Piece piece = square.getPiece();

                if (square.isEmpty()) {
                    return false;
                } else if (piece.isPawn() || piece.isRook() || piece.isQueen()) {
                    return false;
                } else if (piece.isKnight() || piece.isBishop()) {

                    if (piece.isWhite()) {
                        whiteBishopsOrKnights++;
                    } else {
                        blackBishopsOrKnights++;
                    }

                    if (whiteBishopsOrKnights > 1 || blackBishopsOrKnights > 1) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public void resetLegalMoves() {
        this.noLegalMoves = null;
    }

    private boolean noLegalMoves(Color color) {

        if (noLegalMoves != null) {
            return noLegalMoves;
        }

        for (Square[] rank: board) {
            for (Square square: rank) {
                if (!square.isEmpty() && square.getPiece().getColor().equals(color)) {
                    List<Move> moves = square.getPiece().getMoves();

                    for (Move move: moves) {
                        if (move.isLegal()) {
                            return noLegalMoves = false;
                        }
                    }
                }
            }
        }

        return noLegalMoves = true;
    }

    public List<Move> getMoves() {
        List<Move> moves = new ArrayList<>();
        Color color = fenDto.getActiveColor();

        for (Square[] rank: board) {
            for (Square square: rank) {
                if (!square.isEmpty() && square.getPiece().getColor().equals(color)) {
                    List<Move> pieceMoves = square.getPiece().getMoves();

                    for (Move move: pieceMoves) {
                        if (move.isLegal()) {
                            moves.add(move);
                        }
                    }
                }
            }
        }

        return moves;
    }

    public Square getRookStartingSquareQueenSide(Color color) {
        return color.equals(Color.WHITE) ?
            getSquare(7, 0) :
            getSquare(0, 0);
    }

    public Square getRookStartingSquareKingSide(Color color) {
        return color.equals(Color.WHITE) ?
            getSquare(7, 7) :
            getSquare(0, 7);
    }

    public Square getEnPassantTarget() {
        String notation = fenDto.getFenEnPassantTarget();

        if (notation.equals("-")) {
            return null;
        }

        return getSquare(notation);
    }

    public Square getEnPassantTarget(Square square) {
        if (square.getRank() != 3 && square.getRank() != 4) {
            return null;
        } else
            return square.getRank() == 3 ?
                getSquare(2, square.getFile()) :
                getSquare(5, square.getFile());
    }

    public void updateFenBoard() {
        StringBuilder fenBoard = new StringBuilder();

        for (int rank = 0; rank < 8; rank++) {
            int emptySquares = 0;

            for (int file = 0; file < 8; file++) {

                if (getSquare(rank, file).isEmpty()) {
                    emptySquares++;
                } else {

                    if (emptySquares != 0) {
                        fenBoard.append(emptySquares);
                        emptySquares = 0;
                    }

                    fenBoard.append(getSquare(rank, file).getPiece().toChar());
                }
            }

            if (emptySquares != 0) {
                fenBoard.append(emptySquares);
            }

            if (rank != 7) {
                fenBoard.append("/");
            }
        }

        fenDto.setFenBoard(fenBoard.toString());
    }

    public Piece findPiece(Piece.PieceType type, Color color, Integer rank, Integer file) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (
                    !board[i][j].isEmpty() &&
                        board[i][j].getPiece().is(type) &&
                        board[i][j].getPiece().is(color) &&
                        (rank == null || rank == i) &&
                        (file == null || file == j)
                ) {
                    return board[i][j].getPiece();
                }
            }
        }

        return null;
    }

    public List<Piece> whoCanMoveTo(Square to, Color color, Piece.PieceType type) {
        List<Piece> result = new ArrayList<>();

        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                Square from = getSquare(rank, file);

                if (!from.isEmpty()) {
                    Piece piece = from.getPiece();

                    if (piece.is(color) && piece.is(type) && piece.isPossibleMove(to)) {
                        result.add(piece);
                    }
                }
            }
        }

        return result;
    }

    public void print() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                System.out.print(board[i][j].toChar());
            }
            System.out.println();
        }
        System.out.println();
    }
}
