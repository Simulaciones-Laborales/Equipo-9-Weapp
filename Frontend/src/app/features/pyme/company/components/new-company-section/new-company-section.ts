import { Component, inject } from '@angular/core';
import { CompanyStore } from '../../company-store';
import { Button } from 'primeng/button';
import { Subtitle } from '@components/subtitle/subtitle';
import { NewCompanyForm } from '../new-company-form/new-company-form';

@Component({
  selector: 'app-new-company-section',
  imports: [Button, Subtitle, NewCompanyForm],
  templateUrl: './new-company-section.html',
  styleUrl: './new-company-section.css',
})
export class NewCompanySection {
  readonly store = inject(CompanyStore);
}
