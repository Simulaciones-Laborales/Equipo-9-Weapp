import { Component, input } from '@angular/core';
import { Title } from '@components/title/title';
import { Button } from 'primeng/button';
import { CreditsTable } from '../credits-table/credits-table';
import { CreditApplicationResponse } from '@core/models/credit-application-model';

@Component({
  selector: 'app-credits-section',
  imports: [Title, Button, CreditsTable],
  templateUrl: './credits-section.html',
  styleUrl: './credits-section.css',
})
export class CreditsSection {
  readonly credits = input.required<CreditApplicationResponse[]>();
}
