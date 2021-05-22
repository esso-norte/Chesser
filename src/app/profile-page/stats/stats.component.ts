import {Component, Input, OnInit} from '@angular/core';
import {StatsModel} from "../../shared/models/stats-model";

@Component({
    selector: 'stats',
    templateUrl: './stats.component.html',
    styleUrls: ['./stats.component.css']
})
export class StatsComponent implements OnInit {

    @Input() stats: StatsModel;

    constructor() { }

    ngOnInit(): void {
    }

}
