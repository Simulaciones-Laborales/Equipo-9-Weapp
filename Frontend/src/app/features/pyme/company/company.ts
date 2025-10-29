import { Component, inject } from '@angular/core';
import { CompanyStore } from './company-store';
import { KycWarningMessage } from './components/kyc-warning-message/kyc-warning-message';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-company',
  imports: [KycWarningMessage, RouterOutlet],
  templateUrl: './company.html',
  styleUrl: './company.css',
  providers: [CompanyStore],
})
export default class Company {
  readonly store = inject(CompanyStore);
}
