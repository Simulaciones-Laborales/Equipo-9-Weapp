import {
  HttpErrorResponse,
  HttpEvent,
  HttpInterceptorFn,
  HttpResponse,
} from '@angular/common/http';
import { inject } from '@angular/core';
import { key } from '@core/utils/http-error-utils';
import { MessageService } from 'primeng/api';
import { catchError, tap, throwError } from 'rxjs';

export const httpInterceptor: HttpInterceptorFn = (req, next) => {
  const messageService = inject(MessageService);

  return next(req).pipe(
    tap((event: HttpEvent<any>) => {
      if (event instanceof HttpResponse) {
        const message = event.body?.message;

        if (message) {
          messageService.add({
            key,
            severity: 'success',
            summary: 'Operación exitosa',
            detail: event.body.message,
          });
        }
      }
    }),
    catchError((error: HttpErrorResponse) => {
      console.error('BORRAR EN PRODUCCIÓN', error.error);
      const { message } = error.error;

      messageService.add({
        key,
        severity: 'error',
        summary: 'Algo salió mal...',
        detail: message,
      });

      return throwError(() => error);
    })
  );
};
