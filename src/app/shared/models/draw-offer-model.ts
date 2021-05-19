import {UserModel} from "./user-model";
import {GameModel} from "./game-model";

export class DrawOfferModel {
    empty: boolean;
    id: number;
    game: GameModel;
    player: UserModel;
}
