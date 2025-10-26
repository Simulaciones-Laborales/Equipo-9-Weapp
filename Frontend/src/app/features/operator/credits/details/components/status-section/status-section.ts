import { Component, inject, input, output } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import {
  CreditApplicationStatus,
  UpdateCreditApplicationStatusDto,
} from '@core/models/credit-application-model';
import { CreditApplicationStatusPipe } from '@pipes/credit-application-status-pipe';
import { Fieldset } from 'primeng/fieldset';
import { Textarea } from 'primeng/textarea';
import { FloatLabel } from 'primeng/floatlabel';
import { Button } from 'primeng/button';
import { Divider } from 'primeng/divider';

@Component({
  selector: 'app-status-section',
  imports: [
    CreditApplicationStatusPipe,
    Fieldset,
    ReactiveFormsModule,
    Textarea,
    FloatLabel,
    Button,
    Divider,
  ],
  templateUrl: './status-section.html',
  styleUrl: './status-section.css',
})
export class StatusSection {
  private readonly _fb = inject(FormBuilder);
  private readonly _max = 400;

  readonly onUpdateStatus = output<UpdateCreditApplicationStatusDto>();
  readonly creditId = input.required<string | null>();
  readonly creditStatus = input.required<CreditApplicationStatus | undefined>();
  readonly loading = input.required<boolean>();

  readonly form = this._fb.group({
    comments: ['', Validators.max(this._max)],
  });

  get remainingCharacters() {
    return this._max - (this.form.get('comments')?.value?.length ?? 0);
  }
}
