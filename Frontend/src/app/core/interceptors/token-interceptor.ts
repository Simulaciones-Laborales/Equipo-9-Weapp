import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { TokenStorage } from '@core/services/token-storage';
import { SKIP_INTERCEPTOR_HEADER } from '@core/utils/http-utils';

export const tokenInterceptor: HttpInterceptorFn = (req, next) => {
  if (req.headers.has(SKIP_INTERCEPTOR_HEADER)) {
    const newHeaders = req.headers.delete(SKIP_INTERCEPTOR_HEADER);
    const newReq = req.clone({ headers: newHeaders });

    return next(newReq);
  }

  const tokenStorage = inject(TokenStorage);
  const token = tokenStorage.token();

  if (!token) {
    return next(req);
  }

  const cloned = req.clone({
    headers: req.headers.set('Authorization', `Bearer ${token}`),
  });

  return next(cloned);
};
