import {AfterViewChecked, Component, OnInit, ViewChild} from '@angular/core';
import {AuthService} from "../auth/shared/auth.service";
import {ActivatedRoute} from "@angular/router";
import {UserService} from "../shared/user.service";
import {ChallengeModel} from "../shared/models/challenge-model";
import {GameModel} from "../shared/models/game-model";
import {StatsModel} from "../shared/models/stats-model";
import {GameService} from "../shared/game.service";
import { ChartComponent } from 'angular2-chartjs';

@Component({
  selector: 'app-profile-page',
  templateUrl: './profile-page.component.html',
  styleUrls: ['./profile-page.component.css']
})
export class ProfilePageComponent implements OnInit, AfterViewChecked {

    @ViewChild(ChartComponent) chart: ChartComponent;

    username: string;
    challenges: ChallengeModel[];
    games: GameModel[];
    stats: StatsModel;
    interval: number;

    type = 'line';
    data = {
        labels: ["", "", ""],
        datasets: [
            {
                label: "Rating history",
                data: [],
                backgroundColor: 'rgb(24,56,175, 0.5)',
                borderColor: 'rgb(24,56,175)',
            }
        ]
    };
    options = {
        responsive: true,
        maintainAspectRatio: false,
        scales: {
            yAxes: [{
                display: true,
                ticks: {
                    suggestedMin: 1450
                }
            }],
            xAxes: [{
                display: false
            }]
        },
        legend: {
            display: false
        },
        tooltips: {
            callbacks: {
                label: function(tooltipItem) {
                    return tooltipItem.yLabel;
                }
            }
        }
    };

    constructor(
        private authService: AuthService,
        private activatedRoute: ActivatedRoute,
        private userService: UserService,
        private gameService: GameService
    ) { }

    ngOnInit(): void {
        this.username = this.authService.getUserName();

        this.loadData(true);

        this.interval = setInterval(() => {
            this.loadData(false);
        }, 3000);
    }

    ngAfterViewChecked() {
        this.chart.chart.update();
    }

    loadData(makeChart: boolean): void {
        this.userService.getStats().subscribe(data => {
            this.stats = data;

            if (makeChart) {
                this.data.datasets[0].data = JSON.parse(this.stats.eloHistory);
                this.data.labels = JSON.parse(this.stats.eloHistory);
            }
        });

        this.userService.getChallenges().subscribe(data => {
            this.challenges = data;
        });

        this.userService.getGames().subscribe(data => {
            this.games = data;
        });
    }

    acceptChallenge(challenge: ChallengeModel) {
        this.userService.acceptChallenge(challenge).subscribe(data => {
            window.location.href = 'http://localhost:4200/game/' + data.id;
        });
    }

    declineChallenge(challenge: ChallengeModel) {

    }

    analyze(game: GameModel) {
        this.gameService.analyze(game);
    }

}
