import { Component, input, output } from '@angular/core';
import { Title } from '@components/title/title';
import { KycVerificationFiles } from '@core/types';
import { NewKycForm } from '@features/components/new-kyc-form/new-kyc-form';

@Component({
  selector: 'app-kyc-section',
  imports: [Title, NewKycForm],
  templateUrl: './kyc-section.html',
  styleUrl: './kyc-section.css',
})
export class KycSection {
  readonly onSubmit = output<KycVerificationFiles>();
  readonly loading = input.required<boolean>();
}
