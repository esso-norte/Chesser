import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { GameModel } from './models/game-model';
import { Observable } from 'rxjs';
import { MakeMoveResponse } from "./make-move-response.payload";
import {UserModel} from "./models/user-model";
import {ChallengeModel} from "./models/challenge-model";
import {StatsModel} from "./models/stats-model";

@Injectable({
    providedIn: 'root'
})
export class UserService {

    constructor(private http: HttpClient) { }

    getAllUsers(): Observable<Array<UserModel>> {
        return this.http.get<Array<UserModel>>('http://localhost:8080/api/users/');
    }

    challengeUser(id: number): Observable<any> {
        return this.http.post('http://localhost:8080/api/challenge/invite', {
            recipientId: id,
            challengeColor: 0
        });
    }

    getChallenges(): Observable<ChallengeModel[]> {
        return this.http.get<ChallengeModel[]>('http://localhost:8080/api/challenge');
    }

    getGames(): Observable<GameModel[]> {
        return this.http.get<GameModel[]>('http://localhost:8080/api/game');
    }

    getStats(): Observable<StatsModel> {
        return this.http.get<StatsModel>('http://localhost:8080/api/stats');
    }

    acceptChallenge(challenge: ChallengeModel): Observable<GameModel> {
        return this.http.post<GameModel>('http://localhost:8080/api/challenge/accept', {
            challengeId: challenge.id
        })
    }

    // createPost(postPayload: CreatePostPayload): Observable<any> {
    //     return this.http.post('http://localhost:8080/api/posts/', postPayload);
    // }

    // getGame(id: number): Observable<GameModel> {
    //     return this.http.get<GameModel>('http://localhost:8080/api/game/' + id);
    // }

    // getAllPostsByUser(name: string): Observable<GameModel[]> {
    //     return this.http.get<GameModel[]>('http://localhost:8080/api/posts/by-user/' + name);
    // }
}
