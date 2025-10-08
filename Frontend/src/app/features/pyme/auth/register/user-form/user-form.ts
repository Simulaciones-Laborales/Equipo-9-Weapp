import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { emailRegex } from '@core/utils';
import { FloatLabel } from 'primeng/floatlabel';
import { InputText } from 'primeng/inputtext';
import { Password } from 'primeng/password';
import { Checkbox } from 'primeng/checkbox';
import { Linker } from '../../components/linker/linker';

@Component({
  selector: 'app-user-form',
  imports: [ReactiveFormsModule, FloatLabel, InputText, Password, Checkbox, Linker],
  templateUrl: './user-form.html',
  styleUrl: './user-form.css',
})
export class UserForm {
  private readonly _fb = inject(FormBuilder);

  readonly form = this._fb.group({
    name: ['', Validators.required],
    email: ['', [Validators.required, Validators.pattern(emailRegex)]],
    password: ['', Validators.required],
    repeatPassword: ['', Validators.required],
  });
}
