import { formatCurrency, formatDate } from '@angular/common';
import { Component, inject, input, LOCALE_ID } from '@angular/core';
import {
  CreditApplicationPurpose,
  CreditApplicationStatus,
} from '@core/models/credit-application-model';
import { CreditApplicationStatusPipe } from '@pipes/credit-application-status-pipe';
import { CreditApplicationPurposePipe } from '@pipes/credit-application-purpose-pipe';

@Component({
  selector: 'app-information-item',
  imports: [],
  templateUrl: './information-item.html',
  styleUrl: './information-item.css',
})
export class InformationItem {
  private readonly _locale = inject(LOCALE_ID);

  readonly name = input.required<string>();
  readonly value = input.required<
    string | number | Date | CreditApplicationPurpose | CreditApplicationStatus | undefined
  >();
  readonly pipe = input<'datetime' | 'currency' | 'purpose' | 'status' | null>(null);

  get formattedValue() {
    if (!this.value()) {
      return '--';
    }

    if (this.pipe() === 'currency') {
      return formatCurrency(this.value() as number, this._locale, '$');
    }

    if (this.pipe() === 'datetime') {
      return formatDate(this.value()!, 'dd/MM/yyyy hh:mm aa', this._locale);
    }

    if (this.pipe() === 'purpose') {
      return new CreditApplicationPurposePipe().transform(this.value() as CreditApplicationPurpose);
    }

    if (this.pipe() === 'status') {
      return new CreditApplicationStatusPipe().transform(this.value() as CreditApplicationStatus);
    }

    return this.value();
  }
}
