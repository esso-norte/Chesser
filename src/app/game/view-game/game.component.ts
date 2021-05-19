import { Component, OnInit } from '@angular/core';
import { faHandshake, faFlag, faCheck, faTimes, faCircle, faChessPawn, faChessRook, faChessKnight, faChessBishop, faChessQueen, faChessKing, IconDefinition } from '@fortawesome/free-solid-svg-icons';
import { AuthService } from '../../auth/shared/auth.service';
import { ActivatedRoute } from '@angular/router';
import { throwError } from "rxjs";
import { GameService } from "../../shared/game.service";
import { GameModel } from "../../shared/models/game-model";
import { DrawOfferModel } from "../../shared/models/draw-offer-model";
import { UserModel } from "../../shared/models/user-model";
import * as mdb from 'mdb-ui-kit';
import {max} from "rxjs/operators";


@Component({
    selector: 'app-game',
    templateUrl: './game.component.html',
    styleUrls: ['./game.component.css']
})

export class GameComponent implements OnInit {
    faCheck = faCheck;
    faTimes = faTimes;
    faCircle = faCircle;
    faHandshake = faHandshake;
    faFlag = faFlag;
    modalPromotion: mdb.Modal;

    username: string;

    drawOfferByCurrentPlayer = false;
    drawOffer: DrawOfferModel;

    gameId: number;
    game: GameModel;
    color: 'white' | 'black' | 'viewer';
    board: string[][] = null;
    moves: string[][] = [];
    interval: number;

    fromRank: number = null;
    fromFile: number = null;
    toRank: number = null;
    toFile: number = null;

    constructor(private authService: AuthService, private activatedRoute: ActivatedRoute, private gameService: GameService) {
        this.gameId = +this.activatedRoute.snapshot.params.gameId;
    }

    ngOnInit(): void {
        this.username = this.authService.getUserName();

        this.loadData();

        this.interval = setInterval(() => {
            this.loadData();
        }, 1000);

        this.modalPromotion = new mdb.Modal(document.getElementById('modalPromotion'), {
            backdrop: true,
            keyboard: true,
            focus: true
        })
    }

    ngOnDestroy() {
        if (this.interval) {
            clearInterval(this.interval);
        }
    }

    loadData(): void {
        this.gameService.getGame(this.gameId).subscribe(data => {
            this.game = data;

            if (this.username === this.game.whitePlayer.username) {
                this.color = 'white';
            } else if (this.username === this.game.blackPlayer.username) {
                this.color = 'black';
            } else {
                this.color = 'viewer';
            }

            if (this.game.finished) {
                this.fromRank = null;
                this.fromFile = null;
            }

            this.loadBoard();

            if (this.game.finished) {
                clearInterval(this.interval);
            }

            let pgn = this.game.pgn.substr(this.game.pgn.lastIndexOf("]") + 3);
            console.log(pgn);
            let resultIndex = Math.max(pgn.lastIndexOf("1-0"), pgn.lastIndexOf("0-1"), pgn.lastIndexOf("1/2-1/2"));
            console.log(resultIndex);
            pgn = pgn.substring(0, resultIndex === -1 ? pgn.length : resultIndex - 1);
            let movesNotation = pgn.split(" ");

            for (let i = 0; i < movesNotation.length; i++)  {
                let moveNo = Math.floor(i / 3);

                if (i % 3 !== 0) {
                    let halfMoveNo = i % 3 - 1;
                    if (movesNotation[i].length > 0) {
                        this.moves[moveNo][halfMoveNo] = movesNotation[i];
                    }
                } else {
                    this.moves[moveNo] = [];
                }
            }

            console.log(this.moves);

            this.gameService.getDrawOffer(this.game).subscribe((data) => {
                this.drawOffer = data;
                this.drawOfferByCurrentPlayer = !this.drawOffer.empty && this.drawOffer.player.username == this.username;
            });
        }, error => {
            throwError(error);
        });
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

    getOpponent(): UserModel {
        console.log(this.color);

        if (this.color === 'white') {
            return this.game.blackPlayer;
        } else if (this.color === 'black') {
            return this.game.whitePlayer;
        } else {
            return null;
        }
    }

    isPlayer(): boolean {
        return this.color !== 'viewer';
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
                this.gameService.makeMove(this.gameId, fromNotation, toNotation)
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

        this.gameService.makeMove(this.gameId, fromNotation, toNotation, piece)
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

    offerDraw(): void {
        this.gameService.offerDraw(this.game).subscribe(() => {
            this.loadData();
        });
    }

    declineDraw(): void {
        this.gameService.declineDraw(this.game).subscribe(() => {
            this.loadData();
        });
    }

    resign(): void {
        this.gameService.resign(this.game).subscribe(() => {
            this.loadData();
        });
    }

    closest(el, selector) {
        var matchesFn;

        // find vendor prefix
        ['matches','webkitMatchesSelector','mozMatchesSelector','msMatchesSelector','oMatchesSelector'].some(function(fn) {
            if (typeof document.body[fn] == 'function') {
                matchesFn = fn;
                return true;
            }
            return false;
        })

        var parent;

        // traverse parents
        while (el) {
            parent = el.parentElement;
            if (parent && parent[matchesFn](selector)) {
                return parent;
            }
            el = parent;
        }

        return null;
    }
}
