import { Component, effect, inject, signal } from '@angular/core';
import { Store } from './store';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Fieldset } from 'primeng/fieldset';
import { Button } from 'primeng/button';
import { FloatLabel } from 'primeng/floatlabel';
import { InputNumber } from 'primeng/inputnumber';
import { Select } from 'primeng/select';
import { ActivatedRoute, Router } from '@angular/router';
import { getError, isInvalid } from '@core/utils/form-utils';
import { Message } from 'primeng/message';
import {
  CreateCreditApplicationDto,
  CreditApplicationPurpose,
} from '@core/models/credit-application-model';
import { CreditApplicationPurposePipe } from '@pipes/credit-application-purpose-pipe';
import { FileRemoveEvent, FileSelectEvent, FileUpload } from 'primeng/fileupload';

@Component({
  selector: 'app-form',
  imports: [
    ReactiveFormsModule,
    Fieldset,
    Button,
    FloatLabel,
    InputNumber,
    Select,
    Message,
    FileUpload,
  ],
  templateUrl: './form.html',
  styleUrl: './form.css',
  providers: [Store],
})
export class Form {
  private readonly _fb = inject(FormBuilder);
  private readonly _router = inject(Router);
  private readonly _route = inject(ActivatedRoute);
  private readonly _pipe = new CreditApplicationPurposePipe();
  private readonly _files = signal<File[]>([]);

  readonly form = this._fb.group({
    amount: [0, [Validators.required, Validators.min(0)]],
    creditPurpose: ['', Validators.required],
    termMonths: [0, [Validators.required, Validators.min(0)]],
  });

  readonly store = inject(Store);

  readonly options = [
    this._purpose(CreditApplicationPurpose.CONSUMPTION),
    this._purpose(CreditApplicationPurpose.EMERGENCY),
    this._purpose(CreditApplicationPurpose.INFRASTRUCTURE_IMPROVEMENT),
    this._purpose(CreditApplicationPurpose.INVENTORY_PURCHASE),
    this._purpose(CreditApplicationPurpose.INVESTMENT),
    this._purpose(CreditApplicationPurpose.MARKETING),
    this._purpose(CreditApplicationPurpose.REFINANCING),
    this._purpose(CreditApplicationPurpose.TECHNOLOGY),
    this._purpose(CreditApplicationPurpose.WORK_CAPITAL),
  ];

  constructor() {
    effect(() => {
      if (this.store.status() === 'loading') {
        this.form.disable();
      } else {
        if (this.store.status() === 'success') {
          this._router.navigate(['..'], { relativeTo: this._route }).then(() => {
            this.form.reset();
            this.onClear();
          });
        }

        this.form.enable();
      }
    });
  }

  onSelect(e: FileSelectEvent) {
    this._files.update((files) => [...files, ...e.files]);
  }

  onRemove(e: FileRemoveEvent) {
    this._files.update((files) => files.filter((file) => file !== e.file));
  }

  onClear() {
    this._files.set([]);
  }

  private _purpose(purpose: CreditApplicationPurpose) {
    return {
      value: purpose,
      name: this._pipe.transform(purpose),
    };
  }

  async onSubmit() {
    if (this.form.invalid) {
      return this.form.markAllAsTouched();
    }

    const id = this._route.snapshot.paramMap.get('id')!;

    const dto: CreateCreditApplicationDto = {
      companyId: id,
      amount: this.form.get('amount')?.value!,
      creditPurpose: (this.form.get('creditPurpose')?.value! as any).value,
      termMonths: this.form.get('termMonths')?.value!,
    };

    await this.store.create(dto, this._files());
  }

  isInvalid(name: string) {
    return isInvalid(this.form, name);
  }

  getError(name: string, error: string) {
    return getError(this.form, name, error);
  }
}
