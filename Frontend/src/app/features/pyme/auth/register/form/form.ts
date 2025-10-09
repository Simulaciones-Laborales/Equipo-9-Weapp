import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { emailRegex } from '@core/utils';
import { FloatLabel } from 'primeng/floatlabel';
import { InputText } from 'primeng/inputtext';
import { Checkbox } from 'primeng/checkbox';
import { Linker } from '../../components/linker/linker';

@Component({
  selector: 'app-user-form',
  imports: [ReactiveFormsModule, FloatLabel, InputText, Checkbox, Linker],
  templateUrl: './form.html',
  styleUrl: './form.css',
})
export class Form {
  private readonly _fb = inject(FormBuilder);

  readonly form = this._fb.group({
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],
    email: ['', [Validators.required, Validators.pattern(emailRegex)]],
    contact: ['', Validators.required],
  });
}
