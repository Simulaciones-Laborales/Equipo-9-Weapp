import {
  ApplicationConfig,
  provideBrowserGlobalErrorListeners,
  provideZoneChangeDetection,
} from '@angular/core';
import { provideRouter, withPreloading } from '@angular/router';

import { routes } from './app.routes';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { providePrimeNG } from 'primeng/config';
import Theme from '@primeuix/themes/aura';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { tokenInterceptor } from '@core/interceptors/token-interceptor';
import { httpInterceptor } from '@core/interceptors/http-interceptor';
import { MessageService } from 'primeng/api';
import { RolePreloadingStrategy } from '@core/strategies/role-preloading-strategy';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes, withPreloading(RolePreloadingStrategy)),
    provideHttpClient(withInterceptors([tokenInterceptor, httpInterceptor])),
    provideAnimationsAsync(),
    providePrimeNG({
      theme: {
        preset: Theme,
        options: {
          cssLayer: {
            name: 'primeng',
            order: 'theme, base, primeng',
          },
          darkModeSelector: '.my-app-dark',
        },
      },
    }),
    MessageService,
  ],
};
