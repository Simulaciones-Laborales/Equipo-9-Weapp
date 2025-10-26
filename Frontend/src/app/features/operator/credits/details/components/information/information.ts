import { Component, input } from '@angular/core';
import { Subtitle } from '@components/subtitle/subtitle';
import { InformationItem } from '../information-item/information-item';
import { CreditApplicationResponse } from '@core/models/credit-application-model';

@Component({
  selector: 'app-information',
  imports: [Subtitle, InformationItem],
  templateUrl: './information.html',
  styleUrl: './information.css',
})
export class Information {
  readonly detail = input.required<CreditApplicationResponse | null>();
}
