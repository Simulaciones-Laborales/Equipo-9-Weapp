import { Component, inject, input, output, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import {
  CreditApplicationStatus,
  UpdateCreditApplicationStatusDto,
} from '@core/models/credit-application-model';
import { Fieldset } from 'primeng/fieldset';
import { Textarea } from 'primeng/textarea';
import { FloatLabel } from 'primeng/floatlabel';
import { Button } from 'primeng/button';
import { Divider } from 'primeng/divider';
import { ConfirmDialog } from 'primeng/confirmdialog';
import { ConfirmationService } from 'primeng/api';
import { Subtitle } from '@components/subtitle/subtitle';

@Component({
  selector: 'app-status-section',
  imports: [
    Fieldset,
    ReactiveFormsModule,
    Textarea,
    FloatLabel,
    Button,
    Divider,
    ConfirmDialog,
    Subtitle,
  ],
  templateUrl: './status-section.html',
  styleUrl: './status-section.css',
  providers: [ConfirmationService],
})
export class StatusSection {
  private readonly _fb = inject(FormBuilder);
  private readonly _newStatus = signal<CreditApplicationStatus | null>(null);
  private readonly _confirmationService = inject(ConfirmationService);

  readonly max = 400;
  readonly onUpdateStatus = output<UpdateCreditApplicationStatusDto>();
  readonly creditId = input.required<string | null>();
  readonly creditStatus = input.required<CreditApplicationStatus | undefined>();
  readonly loading = input.required<boolean>();

  readonly form = this._fb.group({
    comments: ['', Validators.max(this.max)],
  });

  setNewStatus(status: string) {
    this._newStatus.set(status as CreditApplicationStatus);
  }

  confirm() {
    if (this.newStatus() === null) {
      return;
    }

    this._confirmationService.confirm({
      message: this._confirmationMessage,
      header: 'Confirmación',
      icon: this._confirmationIcon,
      acceptLabel: this._confirmationLabel,
      acceptButtonProps: {
        size: 'small',
        severity: this.severity,
      },
      rejectLabel: 'Cancelar',
      rejectButtonProps: {
        size: 'small',
        severity: 'secondary',
        outlined: true,
      },
    });
  }

  private get _confirmationLabel() {
    switch (this.newStatus()) {
      case 'APPROVED':
        return 'Sí, aprobar';
      case 'UNDER_REVIEW':
        return 'Dejar en Revisión';
      case 'REJECTED':
        return 'Seguro, rechazar';
      default:
        return '';
    }
  }

  private get _confirmationIcon() {
    switch (this.newStatus()) {
      case 'APPROVED':
        return 'pi pi-exclamation-circle';
      case 'UNDER_REVIEW':
        return 'pi pi-exclamation-circle';
      case 'REJECTED':
        return 'pi pi-exclamation-triangle';
      default:
        return '';
    }
  }

  private get _confirmationMessage() {
    switch (this.newStatus()) {
      case 'APPROVED':
        return '¿Deseas aprobar esta solicitud de crédito?';
      case 'UNDER_REVIEW':
        return '¿Piensas dejar esta solicitud bajo revisión?';
      case 'REJECTED':
        return '¿Estás seguro de rechazar esta solicitud de crédito?';
      default:
        return '';
    }
  }

  get remainingCharacters() {
    return this.max - (this.form.get('comments')?.value?.length ?? 0);
  }

  get newStatus() {
    return this._newStatus.asReadonly();
  }

  get severity() {
    switch (this.newStatus()) {
      case 'APPROVED':
        return 'success';
      case 'UNDER_REVIEW':
        return 'warn';
      case 'REJECTED':
        return 'danger';
      default:
        return 'primary';
    }
  }

  get submitText() {
    switch (this.newStatus()) {
      case 'APPROVED':
        return 'Aprobar solicitud de crédito';
      case 'UNDER_REVIEW':
        return 'Dejar en revisión';
      case 'REJECTED':
        return 'Rechazar solicitud de crédito';
      default:
        return 'Selecciona una acción';
    }
  }
}
