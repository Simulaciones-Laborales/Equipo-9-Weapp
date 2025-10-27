import { Component, input } from '@angular/core';

@Component({
  selector: 'app-risk-score-card',
  imports: [],
  templateUrl: './risk-score-card.html',
  styleUrl: './risk-score-card.css',
})
export class RiskScoreCard {
  readonly riskScore = input.required<number | undefined>();
}
