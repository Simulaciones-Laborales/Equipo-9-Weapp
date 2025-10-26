import { Pipe, PipeTransform } from '@angular/core';
import { CreditApplicationStatus } from '@core/models/credit-application-model';

@Pipe({
  name: 'creditApplicationStatus',
})
export class CreditApplicationStatusPipe implements PipeTransform {
  transform(value: CreditApplicationStatus | undefined): string {
    if (value === CreditApplicationStatus.APPROVED) {
      return 'Aprobado';
    }

    if (value === CreditApplicationStatus.CANCELLED) {
      return 'Cancelado';
    }

    if (value === CreditApplicationStatus.PENDING) {
      return 'Pendiente';
    }

    if (value === CreditApplicationStatus.REJECTED) {
      return 'Rechazado';
    }

    if (value === CreditApplicationStatus.UNDER_REVIEW) {
      return 'En revisión';
    }

    return '--';
  }
}
