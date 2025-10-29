import { Component, effect, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { FloatLabel } from 'primeng/floatlabel';
import { InputText } from 'primeng/inputtext';
import { Button } from 'primeng/button';
import { CompanyRequest } from '../../../models/company-model';
import { getError, isInvalid } from '@core/utils/form-utils';
import { Message } from 'primeng/message';
import { InputNumber } from 'primeng/inputnumber';
import { Store } from './store';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-new-company-form',
  imports: [ReactiveFormsModule, FloatLabel, InputText, Button, Message, InputNumber],
  templateUrl: './new-company-form.html',
  styleUrl: './new-company-form.css',
  providers: [Store],
})
export class NewCompanyForm {
  private readonly _fb = inject(FormBuilder);
  private readonly _router = inject(Router);
  private readonly _route = inject(ActivatedRoute);

  readonly store = inject(Store);

  readonly form = this._fb.group({
    name: ['', Validators.required],
    taxId: ['', [Validators.required, Validators.maxLength(11), Validators.minLength(11)]],
    annualIncome: [0, [Validators.required, Validators.min(0)]],
  });

  constructor() {
    effect(() => {
      const status = this.store.status();

      if (status === 'loading') {
        this.form.disable();
      } else {
        if (status === 'success') {
          this._router.navigate(['..'], { relativeTo: this._route });
        }

        this.form.enable();
      }
    });
  }

  submit() {
    if (this.form.invalid) {
      return this.form.markAllAsTouched();
    }

    this.store.createCompany(this.form.value as CompanyRequest);
  }

  isInvalid(controlName: string) {
    return isInvalid(this.form, controlName);
  }

  getError(controlName: string, error: string) {
    return getError(this.form, controlName, error);
  }
}
