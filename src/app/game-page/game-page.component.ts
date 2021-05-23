import {Component, OnInit} from '@angular/core';
import { AuthService } from '../auth/shared/auth.service';
import { ActivatedRoute } from '@angular/router';
import { throwError } from "rxjs";
import { GameService } from "../shared/game.service";
import { GameModel } from "../shared/models/game-model";


@Component({
    selector: 'app-game',
    templateUrl: './game-page.component.html',
    styleUrls: ['./game-page.component.css']
})

export class GamePageComponent implements OnInit {

    username: string;
    gameId: number;
    game: GameModel;
    color: 'white' | 'black' | 'viewer';
    interval: number;

    constructor(private authService: AuthService, private activatedRoute: ActivatedRoute, private gameService: GameService) {
        this.gameId = +this.activatedRoute.snapshot.params.gameId;
    }

    ngOnInit(): void {
        this.username = this.authService.getUserName();

        this.loadData();

        this.interval = setInterval(() => {
            this.loadData();
        }, 1000);
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
                clearInterval(this.interval);
            }
        }, error => {
            throwError(error);
        });
    }

    isPlayer(): boolean {
        return this.color !== 'viewer';
    }

    isBlack(): boolean {
        return this.color === 'black';
    }

    isWhite(): boolean {
        return this.color === 'white';
    }
}
