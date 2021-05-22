import { Component, OnInit } from '@angular/core';
import {AuthService} from "../auth/shared/auth.service";
import {ActivatedRoute} from "@angular/router";
import {UserService} from "../shared/user.service";
import {ChallengeModel} from "../shared/models/challenge-model";
import {GameModel} from "../shared/models/game-model";
import {StatsModel} from "../shared/models/stats-model";
import {GameService} from "../shared/game.service";

@Component({
  selector: 'app-profile-page',
  templateUrl: './profile-page.component.html',
  styleUrls: ['./profile-page.component.css']
})
export class ProfilePageComponent implements OnInit {

    username: string;
    stats: StatsModel;
    interval: number;

    eloHistory: any;

    constructor(
        private authService: AuthService,
        private activatedRoute: ActivatedRoute,
        private userService: UserService
    ) { }

    ngOnInit(): void {
        this.username = this.authService.getUserName();

        this.loadData(true);

        this.interval = setInterval(() => {
            this.loadData(false);
        }, 3000);
    }

    loadData(makeChart: boolean): void {
        this.userService.getStats().subscribe(data => {
            this.stats = data;

            if (makeChart) {
                this.eloHistory = JSON.parse(this.stats.eloHistory);
            }
        });
    }

}
