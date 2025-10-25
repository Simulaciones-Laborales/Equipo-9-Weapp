import {
  HttpErrorResponse,
  HttpEvent,
  HttpInterceptorFn,
  HttpResponse,
} from '@angular/common/http';
import { inject } from '@angular/core';
import { key } from '@core/utils/http-utils';
import { environment } from 'environments/environment.development';
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
    catchError((e: HttpErrorResponse) => {
      const { production } = environment;

      if (!production) {
        console.error('INTERCEPTED HTTP ERROR', e.error);
      }

      const { status, error } = e;
      const { message } = error;

      if (status !== 403) {
        messageService.add({
          key,
          severity: 'error',
          summary: 'Algo salió mal...',
          detail: message,
        });
      }

      return throwError(() => e);
    })
  );
};
