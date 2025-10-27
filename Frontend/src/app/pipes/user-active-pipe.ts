import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'userActive',
})
export class UserActivePipe implements PipeTransform {
  transform(value: boolean): string {
    if (value) {
      return 'Activo';
    }

    return 'Inactivo';
  }
}
