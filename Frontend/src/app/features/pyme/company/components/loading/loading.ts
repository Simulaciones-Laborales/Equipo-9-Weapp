import { Component, inject } from '@angular/core';
import { CompanyStore } from '../../company-store';
import { ProgressSpinner } from 'primeng/progressspinner';

@Component({
  selector: 'app-loading',
  imports: [ProgressSpinner],
  templateUrl: './loading.html',
  styleUrl: './loading.css',
})
export class Loading {
  readonly store = inject(CompanyStore);
}
