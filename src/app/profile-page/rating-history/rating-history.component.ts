import {AfterViewChecked, Component, Input, OnInit, ViewChild} from '@angular/core';
import {ChartComponent} from "angular2-chartjs";

@Component({
    selector: 'rating-history',
    templateUrl: './rating-history.component.html',
    styleUrls: ['./rating-history.component.css']
})
export class RatingHistoryComponent implements OnInit, AfterViewChecked {

    @ViewChild(ChartComponent) chart: ChartComponent;
    @Input() data: any;

    chartMade: boolean = false;

    chartData = {
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

    chartOptions = {
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

    constructor() { }

    ngOnInit(): void {
    }

    ngAfterViewChecked() {
        if (!this.chartMade) {
            this.chartData.datasets[0].data = this.data;
            this.chartData.labels = this.data;
            this.chart.chart.update();
            this.chartMade = true;
        }
    }

}
