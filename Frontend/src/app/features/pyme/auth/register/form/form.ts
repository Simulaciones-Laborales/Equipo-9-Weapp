import { Component, effect, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { emailRegex, isInvalid, getError } from '@core/utils/form-utils';
import { FloatLabel } from 'primeng/floatlabel';
import { InputText } from 'primeng/inputtext';
import { Linker } from '../../components/linker/linker';
import { FormStore } from './form-store';
import { Button } from 'primeng/button';
import { RegisterModel } from '../../models/auth-model';
import { MessageService } from 'primeng/api';
import { Message } from 'primeng/message';
import { TokenStorage } from '@core/services/token-storage';
import { ActivatedRoute, Router } from '@angular/router';
import { Password } from 'primeng/password';
import { DatePicker } from 'primeng/datepicker';
import { Select } from 'primeng/select';
import { CountryUtils } from '@core/services/country-utils';
import { Fieldset } from '../../components/fieldset/fieldset';

@Component({
  selector: 'app-user-form',
  imports: [
    ReactiveFormsModule,
    FloatLabel,
    InputText,
    Linker,
    Button,
    Message,
    Password,
    DatePicker,
    Select,
    Fieldset,
  ],
  templateUrl: './form.html',
  styleUrl: './form.css',
  providers: [MessageService, FormStore],
})
export class Form {
  private readonly _fb = inject(FormBuilder);
  private readonly _messageService = inject(MessageService);
  private readonly _tokenStorage = inject(TokenStorage);
  private readonly _router = inject(Router);
  private readonly _route = inject(ActivatedRoute);
  private readonly _countryUtils = inject(CountryUtils);

  readonly today = new Date();
  readonly store = inject(FormStore);
  readonly countries = this._countryUtils.countries();

  readonly form = this._fb.group({
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],
    email: ['', [Validators.required, Validators.pattern(emailRegex)]],
    contact: ['', Validators.required],
    birthday: ['', Validators.required],
    dni: ['', Validators.required],
    country: ['', Validators.required],
    password: ['', Validators.required],
  });

  constructor() {
    effect(() => {
      switch (this.store.status()) {
        case 'loading':
          this.form.disable();
          break;
        case 'success':
          this._successful();
          break;
        case 'failure':
          this.form.enable();
          this._showError();
          break;
      }
    });
  }

  isInvalid(controlName: string) {
    return isInvalid(this.form, controlName);
  }

  getError(controlName: string, error: string) {
    return getError(this.form, controlName, error);
  }

  async onRegister() {
    if (this.form.invalid) {
      return this.form.markAllAsTouched();
    }

    await this.store.register(this.form.value as RegisterModel);
  }

  private _successful() {
    this.form.reset();
    this._tokenStorage.save(this.store.response()!.data);
    this._router.navigate(['..', 'dashboard'], { relativeTo: this._route });
  }

  private _showError() {
    this._messageService.add({
      severity: 'error',
      summary: 'Algo salió mal...',
      detail: this.store.error() ?? '',
    });
  }
}
