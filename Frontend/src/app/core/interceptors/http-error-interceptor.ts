import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { key } from '@core/utils/http-error-utils';
import { MessageService } from 'primeng/api';
import { catchError, throwError } from 'rxjs';

export const httpErrorInterceptor: HttpInterceptorFn = (req, next) => {
  const messageService = inject(MessageService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
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
