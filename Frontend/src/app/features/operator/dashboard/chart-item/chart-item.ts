import { Component, computed, input } from '@angular/core';
import { ChartModule } from 'primeng/chart';
import { Card } from 'primeng/card';

@Component({
  selector: 'app-chart-item',
  imports: [ChartModule, Card],
  templateUrl: './chart-item.html',
  styleUrl: './chart-item.css',
})
export class ChartItem {
  readonly headline = input.required<string>();
  readonly total = input.required<number>();
  readonly data = input.required<any>();
  readonly backgroundColors = input.required<any>();
  readonly hoverBackgroundColors = input.required<any>();

  readonly chartData = computed(() => {
    const data = this.data();

    const keys = Object.keys(data);
    const values = Object.values(data);

    return {
      labels: keys,
      datasets: [
        {
          data: values,
          backgroundColor: this.backgroundColors()(keys),
          hoverBackgroundColor: this.hoverBackgroundColors()(keys),
        },
      ],
    };
  });
}
