import { Component, effect, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { emailRegex, getError, isInvalid } from '@core/utils/form-utils';
import { InputTextModule } from 'primeng/inputtext';
import { FloatLabelModule } from 'primeng/floatlabel';
import { PasswordModule } from 'primeng/password';
import { ButtonModule } from 'primeng/button';
import { Linker } from '@features/pyme/auth/components/linker/linker';
import { LoginStore } from './form-store';
import { LoginReq } from '@features/pyme/auth/models/auth-model';
import { Message } from 'primeng/message';
import { TokenStorage } from '@core/services/token-storage';
import { ActivatedRoute, Router } from '@angular/router';
import { Fieldset } from '@features/pyme/auth/components/fieldset/fieldset';

@Component({
  selector: 'app-form',
  imports: [
    ReactiveFormsModule,
    InputTextModule,
    FloatLabelModule,
    PasswordModule,
    ButtonModule,
    Linker,
    Message,
    Fieldset,
  ],
  templateUrl: './form.html',
  styleUrl: './form.css',
  providers: [LoginStore],
})
export class Form {
  private readonly _fb = inject(FormBuilder);
  private readonly _tokenStorage = inject(TokenStorage);
  private readonly _router = inject(Router);
  private readonly _route = inject(ActivatedRoute);

  readonly store = inject(LoginStore);

  readonly form = this._fb.group({
    email: ['', [Validators.required, Validators.pattern(emailRegex)]],
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

  async onSubmit() {
    if (this.form.invalid) {
      return this.form.markAllAsTouched();
    }

    await this.store.login(this.form.value as LoginReq);
  }

  private _successful() {
    this.form.reset();
    this._tokenStorage.save(this.store.userLogged()!.data);
    this._router.navigate(['../..', 'dashboard'], { relativeTo: this._route });
  }
}
