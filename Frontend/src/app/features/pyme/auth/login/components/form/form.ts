import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { emailRegex } from '@core/utils';
import { InputTextModule } from 'primeng/inputtext';
import { FloatLabelModule } from 'primeng/floatlabel';
import { PasswordModule } from 'primeng/password';
import { ButtonModule } from 'primeng/button';

@Component({
  selector: 'app-form',
  imports: [ReactiveFormsModule, InputTextModule, FloatLabelModule, PasswordModule, ButtonModule],
  templateUrl: './form.html',
  styleUrl: './form.css',
})
export class Form {
  private readonly _fb = inject(FormBuilder);

  readonly form = this._fb.group({
    email: ['', [Validators.required, Validators.pattern(emailRegex)]],
    password: ['', Validators.required],
  });

  onSubmit() {
    if (this.form.invalid) {
      return this.form.markAllAsTouched();
    }

    console.table(this.form.value);
  }
}
