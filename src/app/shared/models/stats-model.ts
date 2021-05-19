import {UserModel} from "./user-model";

export class StatsModel {
    used: UserModel;
    total: number;
    wins: number;
    draws: number;
    loses: number;
    ongoing: number;
    eloHistory: string;
}
