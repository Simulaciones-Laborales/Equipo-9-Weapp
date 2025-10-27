import { Component, effect, inject, LOCALE_ID } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import {
  emailRegex,
  isInvalid,
  getError,
  passwordRegex,
  contactRegex,
} from '@core/utils/form-utils';
import { FloatLabel } from 'primeng/floatlabel';
import { InputText } from 'primeng/inputtext';
import { Linker } from '../../components/linker/linker';
import { FormStore } from './form-store';
import { Button } from 'primeng/button';
import { RegisterModel } from '../../models/auth-model';
import { Message } from 'primeng/message';
import { TokenStorage } from '@core/services/token-storage';
import { ActivatedRoute, Router } from '@angular/router';
import { Password } from 'primeng/password';
import { DatePicker } from 'primeng/datepicker';
import { Select } from 'primeng/select';
import { CountryUtils } from '@core/services/country-utils';
import { Fieldset } from '../../components/fieldset/fieldset';
import { formatDate } from '@angular/common';
import { RouteByRoleService } from '@core/services/route-by-role-service';

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
  providers: [FormStore],
})
export class Form {
  private readonly _fb = inject(FormBuilder);
  private readonly _tokenStorage = inject(TokenStorage);
  private readonly _router = inject(Router);
  private readonly _route = inject(ActivatedRoute);
  private readonly _countryUtils = inject(CountryUtils);
  private readonly _locale = inject(LOCALE_ID);
  private readonly _routeByRoleService = inject(RouteByRoleService);

  readonly today = new Date();
  readonly store = inject(FormStore);
  readonly countries = this._countryUtils.countries();

  readonly form = this._fb.group({
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],
    email: ['', [Validators.required, Validators.pattern(emailRegex)]],
    contact: ['', [Validators.required, Validators.pattern(contactRegex)]],
    birthDate: ['', Validators.required],
    dni: ['', Validators.required],
    country: ['', Validators.required],
    password: ['', [Validators.required, Validators.pattern(passwordRegex)]],
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

    const selectedDate = this.form.get('birthDate')?.value!;

    const data: RegisterModel = {
      ...(this.form.value as RegisterModel),
      birthDate: formatDate(selectedDate, 'dd/MM/yyyy', this._locale),
    };

    await this.store.register(data);
  }

  private _successful() {
    this.form.reset();
    this._tokenStorage.save(this.store.response()!.data);

    const navigateTo = this._routeByRoleService.getRoute();
    this._router.navigate([navigateTo], { relativeTo: this._route });
  }
}
