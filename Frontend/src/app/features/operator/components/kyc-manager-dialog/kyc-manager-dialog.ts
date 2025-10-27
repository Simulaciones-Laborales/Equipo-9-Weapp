import { Component, effect, inject, input, output } from '@angular/core';
import { LoadingSpinner } from '@components/loading-spinner/loading-spinner';
import {
  KYCVerificationResponse,
  KYCVerificationStatus,
  UpdateKycStatusDto,
} from '@core/models/kyc-model';
import { Button } from 'primeng/button';
import { Dialog } from 'primeng/dialog';
import { FileDownloaderService } from '@core/services/file-downloader-service';
import { DialogSubtitle } from '@components/dialog-subtitle/dialog-subtitle';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { FloatLabel } from 'primeng/floatlabel';
import { Select } from 'primeng/select';
import { Textarea } from 'primeng/textarea';
import { Message } from 'primeng/message';
import { isInvalid } from '@core/utils/form-utils';

@Component({
  selector: 'app-kyc-manager-dialog',
  imports: [
    Dialog,
    LoadingSpinner,
    Button,
    DialogSubtitle,
    ReactiveFormsModule,
    FloatLabel,
    Select,
    Textarea,
    Message,
  ],
  templateUrl: './kyc-manager-dialog.html',
  styleUrl: './kyc-manager-dialog.css',
})
export class KycManagerDialog {
  private readonly _fileDownloaderService = inject(FileDownloaderService);
  private readonly _fb = inject(FormBuilder);

  readonly statuses = [
    { name: 'Pendiente', value: KYCVerificationStatus.PENDING },
    { name: 'Rechazado', value: KYCVerificationStatus.REJECTED },
    { name: 'Requiere Revisión', value: KYCVerificationStatus.REVIEW_REQUIRED },
    { name: 'Verificado', value: KYCVerificationStatus.VERIFIED },
  ];

  readonly max = 400;

  readonly form = this._fb.group({
    status: [{ name: '', value: KYCVerificationStatus.PENDING }, Validators.required],
    notes: ['', [Validators.required, Validators.max(this.max)]],
  });

  readonly onSubmit = output<UpdateKycStatusDto>();
  readonly onClose = output<void>();
  readonly visible = input.required<boolean>();
  readonly fetchLoading = input.required<boolean>();
  readonly updateLoading = input.required<boolean>();
  readonly updateSuccess = input.required<boolean>();
  readonly kyc = input.required<KYCVerificationResponse | null>();

  constructor() {
    effect(() => {
      if (this.kyc()) {
        this.form.patchValue({
          status: this.statuses.find((s) => s.value === this.kyc()!.status),
          notes: this.kyc()!.verificationNotes,
        });
      } else {
        this.form.reset();
      }
    });

    effect(() => {
      if (this.updateLoading()) {
        this.form.disable();
      } else {
        this.form.enable();
      }
    });

    effect(() => {
      if (this.updateSuccess()) {
        this.form.reset();
        this.onClose.emit();
      }
    });
  }

  submit() {
    if (this.form.invalid) {
      return this.form.markAllAsTouched();
    }

    const dto: UpdateKycStatusDto = {
      status: this.form.get('status')!.value!.value,
      notes: this.form.get('notes')!.value!,
    };

    this.onSubmit.emit(dto);
  }

  download(url: string | undefined) {
    this._fileDownloaderService.download(url ?? '').subscribe((blob) => {
      const a = document.createElement('a');
      const objectUrl = URL.createObjectURL(blob);
      a.href = objectUrl;
      a.download = 'kycverification';
      a.click();
      URL.revokeObjectURL(objectUrl);
    });
  }

  get remainingCharacters() {
    return this.max - (this.form.get('notes')?.value?.length ?? 0);
  }

  isInvalid(name: string) {
    return isInvalid(this.form, name);
  }
}
