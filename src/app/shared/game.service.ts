import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { GameModel } from './models/game-model';
import { Observable } from 'rxjs';
import { MakeMoveResponse } from "./make-move-response.payload";
import {DrawOfferModel} from "./models/draw-offer-model";

@Injectable({
    providedIn: 'root'
})
export class GameService {

    constructor(private http: HttpClient) { }

    getAllGames(): Observable<Array<GameModel>> {
        return this.http.get<Array<GameModel>>('http://localhost:8080/api/game/');
    }

    // createPost(postPayload: CreatePostPayload): Observable<any> {
    //     return this.http.post('http://localhost:8080/api/posts/', postPayload);
    // }

    getGame(gameId: number): Observable<GameModel> {
        return this.http.get<GameModel>('http://localhost:8080/api/game/' + gameId);
    }

    makeMove(gameId: number, fromNotation: string, toNotation: string, promotion: string = null): Observable<MakeMoveResponse> {

        const body = promotion === null ? {
            gameId: gameId,
            from: fromNotation,
            to: toNotation
        } : {
            gameId: gameId,
            from: fromNotation,
            to: toNotation,
            promotion: promotion
        };

        return this.http.post<MakeMoveResponse>('http://localhost:8080/api/game/move', body);
    }

    offerDraw(gameId: number): Observable<DrawOfferModel> {
        return this.http.post<DrawOfferModel>('http://localhost:8080/api/game/offer-draw', {
            id: gameId
        });
    }

    declineDraw(gameId: number): Observable<DrawOfferModel> {
        return this.http.post<DrawOfferModel>('http://localhost:8080/api/game/decline-draw', {
            id: gameId
        });
    }

    getDrawOffer(gameId: number): Observable<DrawOfferModel> {
        return this.http.get<DrawOfferModel>('http://localhost:8080/api/game/' + gameId + '/draw-offer');
    }

    resign(gameId: number): Observable<any> {
        return this.http.post('http://localhost:8080/api/game/resign', {
            id: gameId
        });
    }

    analyze(gameId: number): Observable<any> {
        return this.http.post('http://localhost:8080/api/game/analyze', {
            id: gameId
        })
    }

    // getAllPostsByUser(name: string): Observable<GameModel[]> {
    //     return this.http.get<GameModel[]>('http://localhost:8080/api/posts/by-user/' + name);
    // }
}
