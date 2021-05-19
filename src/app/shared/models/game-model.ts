import {UserModel} from "./user-model";

export class GameModel {
    id: number;

    whitePlayer: UserModel;
    blackPlayer: UserModel;

    pgn: string;
    fen: string;
    finished: boolean;
    result: string;
}
