import {UserModel} from "./user-model";

export class GameModel {
    id: number;

    whitePlayer: UserModel;
    blackPlayer: UserModel;

    pgn: string;
    fen: string;
    fenFullJson: string;
    finished: boolean;
    result: string;
    analyzed: boolean;
}
