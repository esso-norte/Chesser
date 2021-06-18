import {Component, Input, OnInit} from '@angular/core';
import { faHandshake, faFlag, faCheck, faTimes, faCircle } from '@fortawesome/free-solid-svg-icons';
import {GameModel} from "../../shared/models/game-model";
import {GameService} from "../../shared/game.service";
import {DrawOfferModel} from "../../shared/models/draw-offer-model";
import {UserModel} from "../../shared/models/user-model";
import {GamePageComponent} from "../game-page.component";

@Component({
  selector: 'control-panel',
  templateUrl: './control-panel.component.html',
  styleUrls: ['./control-panel.component.css']
})
export class ControlPanelComponent implements OnInit {

    faCircle = faCircle;
    faCheck = faCheck;
    faTimes = faTimes;
    faHandshake = faHandshake;
    faFlag = faFlag;

    @Input() username: string;
    @Input() game: GameModel;
    @Input() color: 'white' | 'black' | 'viewer';
    moves: string[][] = [];
    positionsFen: string[][] = [];
    drawOfferByCurrentPlayer = false;
    drawOffer: DrawOfferModel;
    interval: number;

    constructor(private gamePage: GamePageComponent, private gameService: GameService) { }

    ngOnInit(): void {
        this.loadData();

        this.interval = setInterval(() => {
            this.loadData();
        }, 1000);
    }

    loadData(): void {
        this.loadMoves();
        this.loadPositions();

        this.gameService.getDrawOffer(this.game.id).subscribe((data) => {
            this.drawOffer = data;
            this.drawOfferByCurrentPlayer = !this.drawOffer.empty && this.drawOffer.player.username == this.username;
        });
    }

    loadMoves(): void {
        let pgn = this.game.pgn.substr(this.game.pgn.lastIndexOf("]") + 3);
        let resultIndex = Math.max(pgn.lastIndexOf("1-0"), pgn.lastIndexOf("0-1"), pgn.lastIndexOf("1/2-1/2"));
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
    }

    loadPositions(): void {
        let fenArray = JSON.parse(this.game.fenFullJson);
        for (let i = 1; i < fenArray.length; i++) {
            if (i % 2 == 1) {
                this.positionsFen[Math.floor((i - 1) / 2)] = [];
                this.positionsFen[Math.floor((i - 1) / 2)][0] = fenArray[i];
            } else {
                this.positionsFen[Math.floor((i - 1) / 2)][1] = fenArray[i];
            }
        }
    }

    showMove(moveNo: number, halfMoveNo: number): void {
        this.gamePage.positionFen = this.positionsFen[moveNo][halfMoveNo];
        this.gamePage.showLatestPosition = moveNo === this.positionsFen.length - 1 &&
            halfMoveNo === this.positionsFen[moveNo].length - 1;
    }

    isPlayer(): boolean {
        return this.color !== 'viewer';
    }

    getOpponent(): UserModel {

        if (this.color === 'white') {
            return this.game.blackPlayer;
        } else if (this.color === 'black') {
            return this.game.whitePlayer;
        } else {
            return null;
        }
    }

    offerDraw(): void {
        this.gameService.offerDraw(this.game.id).subscribe(() => {
            this.loadData();
        });
    }

    declineDraw(): void {
        this.gameService.declineDraw(this.game.id).subscribe(() => {
            this.loadData();
        });
    }

    resign(): void {
        this.gameService.resign(this.game.id).subscribe(() => {
            this.loadData();
        });
    }

    analyse(): void {
        this.gameService.analyze(this.game.id).subscribe((data) => {
            console.log(data);
        });
    }

    isPlayerMove(): boolean {
        return this.isPlayer() && (this.game.fen.split(" ")[1] === this.color[0]);
    }

    getHeaderClass(): string {
        return this.game.finished ?
            (this.game.result === '1/2-1/2' ? 'grey darken-2 text-white' : this.game.result === '1-0' ? 'bg-white text-dark' : 'bg-dark text-white') :
            (this.game.fen.split(" ")[1] === 'w' ? 'bg-white text-dark' : 'bg-dark text-white');
    }

    getHeaderLabel(): string {
        return this.game.finished ?
            (this.game.result === '1/2-1/2' ? 'WHITE WON' : this.game.result === '1-0' ? 'WHITE WON' : 'BLACK WON') :
            (this.game.fen.split(" ")[1] === 'w' ? 'WHITE TO MOVE' : 'BLACK TO MOVE');
    }
}
