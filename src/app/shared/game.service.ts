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

    getGame(id: number): Observable<GameModel> {
        return this.http.get<GameModel>('http://localhost:8080/api/game/' + id);
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

    offerDraw(game: GameModel): Observable<DrawOfferModel> {
        return this.http.post<DrawOfferModel>('http://localhost:8080/api/game/offer-draw', {
            id: game.id
        });
    }

    declineDraw(game: GameModel): Observable<DrawOfferModel> {
        return this.http.post<DrawOfferModel>('http://localhost:8080/api/game/decline-draw', {
            id: game.id
        });
    }

    getDrawOffer(game: GameModel): Observable<DrawOfferModel> {
        return this.http.get<DrawOfferModel>('http://localhost:8080/api/game/' + game.id + '/draw-offer');
    }

    resign(game: GameModel): Observable<any> {
        return this.http.post('http://localhost:8080/api/game/resign', {
            id: game.id
        });
    }

    analyze(game: GameModel): Observable<any> {
        return this.http.post('http://localhost:8080/api/game/analyze', {
            id: game.id
        })
    }

    // getAllPostsByUser(name: string): Observable<GameModel[]> {
    //     return this.http.get<GameModel[]>('http://localhost:8080/api/posts/by-user/' + name);
    // }
}
