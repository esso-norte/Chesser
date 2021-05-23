import {Component, Input, OnChanges, OnInit} from '@angular/core';
import {GameModel} from "../../shared/models/game-model";
import * as mdb from 'mdb-ui-kit';
import {
    faChessBishop, faChessKing,
    faChessKnight,
    faChessPawn, faChessQueen,
    faChessRook,
    IconDefinition
} from "@fortawesome/free-solid-svg-icons";
import {GameService} from "../../shared/game.service";

@Component({
    selector: 'board',
    templateUrl: './board.component.html',
    styleUrls: ['./board.component.css']
})
export class BoardComponent implements OnInit, OnChanges {

    @Input() game: GameModel;
    @Input() color: 'white' | 'black' | 'viewer';
    board: string[][] = null;

    fromRank: number = null;
    fromFile: number = null;
    toRank: number = null;
    toFile: number = null;

    modalPromotion: mdb.Modal;

    constructor(private gameService: GameService) {}

    ngOnInit(): void {
        this.loadData();

        this.modalPromotion = new mdb.Modal(document.getElementById('modalPromotion'), {
            backdrop: true,
            keyboard: true,
            focus: true
        });
    }

    ngOnChanges() {
        this.loadData();
    }

    loadData(): void {

        if (this.game.finished) {
            this.fromRank = null;
            this.fromFile = null;
        }

        this.loadBoard();
    }

    loadBoard(): void {
        let rank = 0;
        let file = 0;
        this.board = [];
        this.board[0] = [];
        const fenBoard = this.game.fen.split(" ")[0];

        for (let i = 0; i < fenBoard.length; i++) {
            const character = fenBoard.charAt(i);

            if (character === '/') {
                rank++;
                file = 0;
                this.board[rank] = [];
            } else if (character >= '1' && character <= '8') {
                for (let j = 0; j < +character - +'0'; j++) {
                    this.board[rank][file] = ' ';
                    file++;
                }
            } else {
                this.board[rank][file] = character;
                file++;
            }
        }
    }

    isWhite(rank: number, file: number): boolean {
        return this.board[rank][file] >= 'A' && this.board[rank][file] <= 'Z';
    }

    isBlack(rank: number, file: number): boolean {
        return this.board[rank][file] >= 'a' && this.board[rank][file] <= 'z';
    }

    isPlayerPiece(rank: number, file: number): boolean {
        if (this.color === 'viewer') {
            return false;
        }

        if (this.color === 'white' && this.isWhite(rank, file)) {
            return true;
        }

        if (this.color === 'black' && this.isBlack(rank, file)) {
            return true;
        }
    }

    charToIcon(character: string): IconDefinition {
        character = character.charAt(0);

        if (character >= 'A' && character <= 'Z') {
            character = String.fromCharCode(character.charCodeAt(0) - 'A'.charCodeAt(0) + 'a'.charCodeAt(0));
        }

        switch (character) {
            case 'p':
                return faChessPawn;
            case 'r':
                return faChessRook;
            case 'n':
                return faChessKnight;
            case 'b':
                return faChessBishop;
            case 'q':
                return faChessQueen;
            case 'k':
                return faChessKing;
        }

        return null;
    }

    click(event: Event, rank: number, file: number): void {
        if (this.color === 'viewer' || this.game.finished) {
            return;
        }

        if (this.isPlayerPiece(rank, file)) {
            if (this.fromRank === rank && this.fromFile === file) {
                this.fromRank = null;
                this.fromFile = null;
            } else {
                this.fromRank = rank;
                this.fromFile = file;
            }
        } else if (this.fromRank !== null && this.fromFile !== null) {
            const fromNotation = this.getNotation(this.fromRank, this.fromFile);
            const toNotation = this.getNotation(rank, file);

            if ((this.board[this.fromRank][this.fromFile] === 'p' && rank === 7) ||
                (this.board[this.fromRank][this.fromFile] === 'P' && rank === 0)) {
                this.toRank = rank;
                this.toFile = file;
                this.modalPromotion.show();
            } else {
                this.gameService.makeMove(this.game.id, fromNotation, toNotation)
                    .subscribe(
                        () => {
                            this.loadData();
                        },
                        () => {
                            this.fromRank = null;
                            this.fromFile = null;
                        });
            }
        }
    }

    promoteTo(piece: string): void {
        this.modalPromotion.hide();
        const fromNotation = this.getNotation(this.fromRank, this.fromFile);
        const toNotation = this.getNotation(this.toRank, this.toFile);

        this.gameService.makeMove(this.game.id, fromNotation, toNotation, piece)
            .subscribe(
                () => {
                    this.loadData();
                },
                () => {
                    this.fromRank = null;
                    this.fromFile = null;
                });
    }

    getNotation(rank: number, file:number): string {
        return String.fromCharCode('a'.charCodeAt(0) + file) + String.fromCharCode('0'.charCodeAt(0) + 8 - rank);
    }
}
