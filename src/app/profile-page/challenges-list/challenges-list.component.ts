import {Component, Input, OnInit} from '@angular/core';
import {ChallengeModel} from "../../shared/models/challenge-model";
import {UserService} from "../../shared/user.service";
import { faCheck, faTimes } from "@fortawesome/free-solid-svg-icons";

@Component({
    selector: 'challenges-list',
    templateUrl: './challenges-list.component.html',
    styleUrls: ['./challenges-list.component.css']
})
export class ChallengesListComponent implements OnInit {

    faCheck = faCheck;
    faTimes = faTimes;

    challenges: ChallengeModel[];

    interval: number;

    constructor(private userService: UserService) { }

    ngOnInit(): void {
        this.loadChallenges();

        this.interval = setInterval(() => {
            this.loadChallenges();
        }, 3000);
    }

    loadChallenges(): void {
        this.userService.getChallenges().subscribe(data => {
            this.challenges = data;
        });
    }

    acceptChallenge(challenge: ChallengeModel) {
        this.userService.acceptChallenge(challenge).subscribe(data => {
            window.location.href = 'http://localhost:4200/game/' + data.id;
        });
    }

    declineChallenge(challenge: ChallengeModel) {

    }

}
