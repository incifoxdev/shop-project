import {
  ApplicationConfig,
  inject,
  LOCALE_ID,
  provideAppInitializer,
  provideBrowserGlobalErrorListeners
} from '@angular/core';

import { registerLocaleData } from '@angular/common';

import { provideRouter } from '@angular/router';

import {
  provideHttpClient,
  withInterceptors
} from '@angular/common/http';

import { firstValueFrom } from 'rxjs';

import localeEsCL from '@angular/common/locales/es-CL';

import { routes } from './app.routes';
import { jwtInterceptor } from './core/interceptors/jwt-interceptor';
import { Auth } from './core/services/auth';

registerLocaleData(localeEsCL, 'es-CL');

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),

    provideRouter(routes),

    provideHttpClient(
      withInterceptors([
        jwtInterceptor
      ])
    ),

    provideAppInitializer(() => {
      const auth = inject(Auth);

      return firstValueFrom(
        auth.restoreSession()
      );
    }),

    {
      provide: LOCALE_ID,
      useValue: 'es-CL'
    }
  ]
};