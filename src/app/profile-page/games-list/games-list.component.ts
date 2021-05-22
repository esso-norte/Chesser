import {Component, Input, OnInit} from '@angular/core';
import {GameModel} from "../../shared/models/game-model";
import {faCircle} from "@fortawesome/free-solid-svg-icons";
import {UserService} from "../../shared/user.service";

@Component({
    selector: 'games-list',
    templateUrl: './games-list.component.html',
    styleUrls: ['./games-list.component.css']
})
export class GamesListComponent implements OnInit {

    faCircle = faCircle;

    @Input() username: string;
    games: GameModel[];

    interval: number;

    constructor(private userService: UserService) { }

    ngOnInit(): void {
        this.loadGames();

        this.interval = setInterval(() => {
            this.loadGames();
        }, 3000);
    }

    loadGames(): void {
        this.userService.getGames().subscribe(data => {
            this.games = data;
        });
    }
}
