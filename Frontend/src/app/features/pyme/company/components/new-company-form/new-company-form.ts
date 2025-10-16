import { Component, effect, inject, input, output } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Fieldset } from 'primeng/fieldset';
import { FloatLabel } from 'primeng/floatlabel';
import { InputText } from 'primeng/inputtext';
import { Button } from 'primeng/button';
import { CompanyRequest } from '../../models/company-model';
import { getError, isInvalid } from '@core/utils/form-utils';

@Component({
  selector: 'app-new-company-form',
  imports: [ReactiveFormsModule, Fieldset, FloatLabel, InputText, Button],
  templateUrl: './new-company-form.html',
  styleUrl: './new-company-form.css',
})
export class NewCompanyForm {
  readonly loading = input.required<boolean>();
  readonly onSubmit = output<CompanyRequest>();

  private readonly _fb = inject(FormBuilder);

  readonly form = this._fb.group({
    name: ['', Validators.required],
    taxId: ['', Validators.required],
    annualIncome: [0, [Validators.required, Validators.min(0)]],
  });

  constructor() {
    effect(() => {
      if (this.loading()) {
        this.form.disable();
      } else {
        this.form.enable();
      }
    });
  }

  submit() {
    if (this.form.invalid) {
      return this.form.markAllAsTouched();
    }

    this.onSubmit.emit(this.form.value as CompanyRequest);
  }

  isInvalid(controlName: string) {
    return isInvalid(this.form, controlName);
  }

  getError(controlName: string, error: string) {
    return getError(this.form, controlName, error);
  }
}
