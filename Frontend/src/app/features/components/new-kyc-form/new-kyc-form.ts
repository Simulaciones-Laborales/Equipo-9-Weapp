import { Component, input, output, signal, WritableSignal } from '@angular/core';
import { FileSelectEvent, FileUpload } from 'primeng/fileupload';
import { Button } from 'primeng/button';
import { Message } from 'primeng/message';
import { Divider } from 'primeng/divider';
import { Fieldset } from 'primeng/fieldset';
import { KycVerificationFiles } from '@core/types';

@Component({
  selector: 'app-new-kyc-form',
  imports: [FileUpload, Button, Message, Divider, Fieldset],
  templateUrl: './new-kyc-form.html',
  styleUrl: './new-kyc-form.css',
})
export class NewKycForm {
  readonly loading = input.required<boolean>();
  readonly title1 = input.required<string>();
  readonly title2 = input.required<string>();
  readonly title3 = input.required<string>();
  readonly onSubmit = output<KycVerificationFiles>();
  readonly document1 = signal<File | null>(null);
  readonly document2 = signal<File | null>(null);
  readonly document3 = signal<File | null>(null);

  linkImage(event: FileSelectEvent, to: WritableSignal<File | null>) {
    to.set(event.files[0]);
  }

  linkImageText(to: WritableSignal<File | null>) {
    return to() === null ? 'Adjuntar Imagen' : 'Imagen cargada';
  }

  isInvalid() {
    return this.document1() === null || this.document2() === null || this.document3() === null;
  }

  submit() {
    if (this.isInvalid()) {
      return;
    }

    const files: KycVerificationFiles = {
      document1: this.document1(),
      document3: this.document3(),
      document2: this.document2(),
    };

    this.onSubmit.emit(files);
  }
}
