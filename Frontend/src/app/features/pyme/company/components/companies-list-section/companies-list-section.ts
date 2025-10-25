import { Component, inject } from '@angular/core';
import { Subtitle } from '@components/subtitle/subtitle';
import { Button } from 'primeng/button';
import { CompanyStore } from '../../company-store';
import { CompaniesTable } from '../companies-table/companies-table';

@Component({
  selector: 'app-companies-list-section',
  imports: [Subtitle, Button, CompaniesTable],
  templateUrl: './companies-list-section.html',
  styleUrl: './companies-list-section.css',
})
export class CompaniesListSection {
  readonly store = inject(CompanyStore);
}
