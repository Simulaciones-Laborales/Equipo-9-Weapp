import { FormGroup } from '@angular/forms';

export const emailRegex: RegExp = /^[A-Za-z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

export const isInvalid = (form: FormGroup, controlName: string) => {
  const control = form.get(controlName);
  return control?.touched && control?.invalid;
};

export const getError = (form: FormGroup, controlName: string, error: string) => {
  const control = form.get(controlName);
  return control?.touched && control.errors?.[error];
};
