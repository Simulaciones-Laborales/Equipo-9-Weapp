import { Component, input, output } from '@angular/core';
import { LoadingSpinner } from '@components/loading-spinner/loading-spinner';
import { Button } from 'primeng/button';
import { Dialog } from 'primeng/dialog';

@Component({
  selector: 'app-kyc-manager-dialog',
  imports: [Dialog, LoadingSpinner, Button],
  templateUrl: './kyc-manager-dialog.html',
  styleUrl: './kyc-manager-dialog.css',
})
export class KycManagerDialog {
  readonly onClose = output<void>();
  readonly visible = input.required<boolean>();
  readonly loading = input.required<boolean>();
}
