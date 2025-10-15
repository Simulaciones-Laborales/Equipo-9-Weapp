import { Component, signal, WritableSignal } from '@angular/core';
import { FileSelectEvent, FileUpload } from 'primeng/fileupload';
import { Button } from 'primeng/button';
import { Message } from 'primeng/message';
import { Divider } from 'primeng/divider';
import { Fieldset } from 'primeng/fieldset';

@Component({
  selector: 'app-new-kyc-form',
  imports: [FileUpload, Button, Message, Divider, Fieldset],
  templateUrl: './new-kyc-form.html',
  styleUrl: './new-kyc-form.css',
})
export class NewKycForm {
  readonly selfie = signal<File | null>(null);
  readonly dniFront = signal<File | null>(null);
  readonly dniBack = signal<File | null>(null);

  linkImage(event: FileSelectEvent, to: WritableSignal<File | null>) {
    to.set(event.files[0]);
  }

  linkImageText(to: WritableSignal<File | null>) {
    return to() === null ? 'Adjuntar Imagen' : 'Imagen cargada';
  }
}
