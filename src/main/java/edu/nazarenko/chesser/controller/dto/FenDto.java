package edu.nazarenko.chesser.controller.dto;

import edu.nazarenko.chesser.service.game.Color;
import edu.nazarenko.chesser.service.game.Square;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FenDto {
    private String fenBoard;
    private String fenActiveColor;
    private String fenCastlingAvailability;
    private String fenEnPassantTarget;
    private String fenHalfMoveClock;
    private String fenMoveClock;

    public FenDto(FenDto fenDtoToCopy) {
        this.fenBoard = fenDtoToCopy.getFenBoard();
        this.fenActiveColor = fenDtoToCopy.getFenActiveColor();
        this.fenCastlingAvailability = fenDtoToCopy.getFenCastlingAvailability();
        this.fenEnPassantTarget = fenDtoToCopy.getFenEnPassantTarget();
        this.fenHalfMoveClock = fenDtoToCopy.getFenHalfMoveClock();
        this.fenMoveClock = fenDtoToCopy.getFenMoveClock();
    }

    public FenDto(String fen) {
        String[] fenElements = fen.split(" ");

        fenBoard = fenElements[0];
        fenActiveColor = fenElements[1];
        fenCastlingAvailability = fenElements[2];
        fenEnPassantTarget = fenElements[3];
        fenHalfMoveClock = fenElements[4];
        fenMoveClock = fenElements[5];
    }

    public Color getActiveColor() {
        return fenActiveColor.equals("w") ? Color.WHITE : Color.BLACK;
    }

    public void setActiveColor(Color color) {
        fenActiveColor = color.equals(Color.WHITE) ? "w" : "b";
    }

    public boolean canCastleKingSide(Color color) {
        return color.equals(Color.WHITE) ? fenCastlingAvailability.contains("K") : fenCastlingAvailability.contains("k");
    }

    public boolean canCastleQueenSide(Color color) {
        return color.equals(Color.WHITE) ? fenCastlingAvailability.contains("Q") : fenCastlingAvailability.contains("q");
    }

    public void disableCastling(Color color) {
        boolean canKingWhite = fenCastlingAvailability.contains("K");
        boolean canQueenWhite = fenCastlingAvailability.contains("Q");
        boolean canKingBlack = fenCastlingAvailability.contains("k");
        boolean canQueenBlack = fenCastlingAvailability.contains("q");

        if (color.equals(Color.WHITE)) {
            canKingWhite = false;
            canQueenWhite = false;
        } else {
            canKingBlack = false;
            canQueenBlack = false;
        }

        setDisableCastling(canKingWhite, canQueenWhite, canKingBlack, canQueenBlack);
    }

    public void disableCastlingKingSide(Color color) {
        boolean canKingWhite = fenCastlingAvailability.contains("K");
        boolean canQueenWhite = fenCastlingAvailability.contains("Q");
        boolean canKingBlack = fenCastlingAvailability.contains("k");
        boolean canQueenBlack = fenCastlingAvailability.contains("q");

        if (color.equals(Color.WHITE)) {
            canKingWhite = false;
        } else {
            canKingBlack = false;
        }

        setDisableCastling(canKingWhite, canQueenWhite, canKingBlack, canQueenBlack);
    }

    public void disableCastlingQueenSide(Color color) {
        boolean canKingWhite = fenCastlingAvailability.contains("K");
        boolean canQueenWhite = fenCastlingAvailability.contains("Q");
        boolean canKingBlack = fenCastlingAvailability.contains("k");
        boolean canQueenBlack = fenCastlingAvailability.contains("q");

        if (color.equals(Color.WHITE)) {
            canQueenWhite = false;
        } else {
            canQueenBlack = false;
        }

        setDisableCastling(canKingWhite, canQueenWhite, canKingBlack, canQueenBlack);
    }

    private void setDisableCastling(boolean canKingWhite, boolean canQueenWhite, boolean canKingBlack, boolean canQueenBlack) {
        String result = "";
        result += canKingWhite ? 'K' : "";
        result += canQueenWhite ? 'Q' : "";
        result += canKingBlack ? 'k' : "";
        result += canQueenBlack ? 'q' : "";

        fenCastlingAvailability = result.isEmpty() ? "-" : result;
    }

    public int getHalfMoveClock() {
        return Integer.parseInt(fenHalfMoveClock);
    }

    public void incHalfMoveClock() {
        fenHalfMoveClock = "" + (getHalfMoveClock() + 1);
    }

    public void resetHalfMoveClock() {
        fenHalfMoveClock = "0";
    }

    public int getMoveClock() {
        return Integer.parseInt(fenMoveClock);
    }

    public void incMoveClock() {
        fenMoveClock = "" + (getMoveClock() + 1);
    }

    public String toString() {
        return fenBoard + " " +
            fenActiveColor + " " +
            fenCastlingAvailability + " " +
            fenEnPassantTarget + " " +
            fenHalfMoveClock + " " +
            fenMoveClock;
    }
}
