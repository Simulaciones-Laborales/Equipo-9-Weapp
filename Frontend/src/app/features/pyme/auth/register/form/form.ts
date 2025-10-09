import { Component, effect, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { emailRegex, isInvalid, getError } from '@core/form-utils';
import { FloatLabel } from 'primeng/floatlabel';
import { InputText } from 'primeng/inputtext';
import { Checkbox } from 'primeng/checkbox';
import { Linker } from '../../components/linker/linker';
import { FormStore } from './form-store';
import { Button } from 'primeng/button';
import { Toast } from 'primeng/toast';
import { RegisterModel } from '../../models/auth-model';
import { MessageService } from 'primeng/api';
import { Message } from 'primeng/message';

@Component({
  selector: 'app-user-form',
  imports: [ReactiveFormsModule, FloatLabel, InputText, Checkbox, Linker, Button, Toast, Message],
  templateUrl: './form.html',
  styleUrl: './form.css',
  providers: [MessageService, FormStore],
})
export class Form {
  private readonly _fb = inject(FormBuilder);
  private readonly _messageService = inject(MessageService);
  readonly store = inject(FormStore);

  readonly form = this._fb.group({
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],
    email: ['', [Validators.required, Validators.pattern(emailRegex)]],
    contact: ['', Validators.required],
  });

  constructor() {
    effect(() => {
      switch (this.store.status()) {
        case 'loading':
          this.form.disable();
          break;
        case 'success':
          this.form.reset();
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

  private _showError() {
    console.log(this.store.error());
    this._messageService.add({
      severity: 'error',
      summary: 'Algo salió mal...',
      detail: this.store.error() ?? '',
    });
  }
}
