import { Component, inject } from '@angular/core';
import { LayoutStore } from '@features/pyme/layout/layout-store';

@Component({
  selector: 'app-kyc-warning-message',
  imports: [],
  templateUrl: './kyc-warning-message.html',
  styleUrl: './kyc-warning-message.css',
})
export class KycWarningMessage {
  readonly layoutStore = inject(LayoutStore);
}
