import { Pipe, PipeTransform } from '@angular/core';
import { CreditApplicationPurpose } from '@core/models/credit-application-model';

@Pipe({
  name: 'creditApplicationPurpose',
})
export class CreditApplicationPurposePipe implements PipeTransform {
  transform(value: CreditApplicationPurpose): string {
    return value.trim().replace('_', ' ');
  }
}
