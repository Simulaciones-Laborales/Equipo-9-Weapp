import { Pipe, PipeTransform } from '@angular/core';
import { UserRole } from '@core/models/user-model';

@Pipe({
  name: 'userRole',
})
export class UserRolePipe implements PipeTransform {
  transform(value: UserRole): string {
    if (value === UserRole.ADMIN) {
      return 'ADMINISTRADOR';
    }

    if (value === UserRole.OPERADOR) {
      return 'OPERADOR';
    }

    return 'PYME';
  }
}
