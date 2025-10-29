import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'userActive',
})
export class UserActivePipe implements PipeTransform {
  transform(value: boolean | undefined): string {
    if (value == undefined) {
      return '--';
    }

    if (value === true) {
      return 'Activo';
    }

    return 'Inactivo';
  }
}
