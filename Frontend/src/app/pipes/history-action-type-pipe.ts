import { Pipe, PipeTransform } from '@angular/core';
import { HistoryActionType } from '@core/models/credit-application-model';

@Pipe({
  name: 'historyActionType',
})
export class HistoryActionTypePipe implements PipeTransform {
  transform(value: HistoryActionType): string {
    if (value === HistoryActionType.APPROVAL) {
      return 'Aprobado';
    }

    if (value === HistoryActionType.AUTOMATION) {
      return 'Automatizado';
    }

    if (value === HistoryActionType.CANCELLATION) {
      return 'Cancelado';
    }

    if (value === HistoryActionType.COMMENT) {
      return 'Comentado';
    }

    if (value === HistoryActionType.CREATION) {
      return 'Creado';
    }

    if (value === HistoryActionType.DELETION) {
      return 'Eliminado';
    }

    if (value === HistoryActionType.OPERATOR_ACTION) {
      return 'Ejecutado por el Operador';
    }

    if (value === HistoryActionType.REJECTION) {
      return 'Rechazado';
    }

    if (value === HistoryActionType.STATUS_CHANGE) {
      return 'Cambio de estado';
    }

    if (value === HistoryActionType.UPDATE) {
      return 'Actualizado';
    }

    return 'Sin acción';
  }
}
