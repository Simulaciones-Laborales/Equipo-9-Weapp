import { computed, inject } from '@angular/core';
import { KYCEntityType, KYCVerificationResponse, UserKycStatus } from '@core/models/kyc-model';
import { TokenStorage } from '@core/services/token-storage';
import { UserApi } from '@core/services/user-api';
import { Status } from '@core/types';
import { patchState, signalStore, withComputed, withMethods, withState } from '@ngrx/signals';

type State = {
  kyc: KYCVerificationResponse[];
  status: Status;
};

const initialState: State = {
  kyc: [],
  status: 'pending',
};

export const LayoutStore = signalStore(
  { providedIn: 'root' },
  withState(initialState),
  withComputed((store) => ({
    userKycStatus: computed((): UserKycStatus => {
      if (store.kyc().length === 0) {
        return {
          name: 'Sin Verificar',
          message:
            'Bienvenido, por favor ve al Inicio para iniciar con la verificación de tu KYC personal.',
        };
      }

      const userKyc = store.kyc().filter((k) => k.kycEntityType === KYCEntityType.USER);

      const n = userKyc.length;

      if (n === 0) {
        return {
          name: 'Sin Verificar',
          message:
            'Bienvenido, por favor ve al Inicio para iniciar con la verificación de tu KYC personal.',
        };
      }

      const lastKyc = userKyc.sort((a, b) => {
        if (a.submissionDate <= b.submissionDate) {
          return -1;
        }

        return 1;
      })[n - 1];

      switch (lastKyc.status) {
        case 'PENDING':
          return {
            name: 'Verificación Pendiente',
            message:
              'Tienes un proceso de verificación pendiente, por favor sé paciente hasta que culmine el proceso.',
          };
        case 'VERIFIED':
          return {
            name: 'Verificado',
            message: null,
          };
        case 'REJECTED':
          return {
            name: 'Rechazado',
            message:
              'Tu documentación ha sido rechazada, te invitamos a que inicies nuevamente el proceso de verificación.',
          };
        case 'REVIEW_REQUIRED':
          return {
            name: 'Por Revisar',
            message:
              'Estamos verificando tu documentación con nuestros operadores, por favor sé paciente hasta que culmine el proceso.',
          };
        default:
          return {
            name: 'Sin Verificar',
            message: '',
          };
      }
    }),
  })),
  withMethods((store, userApi = inject(UserApi), tokenStorage = inject(TokenStorage)) => ({
    fetchInitialData: async () => {
      patchState(store, { status: 'loading' });

      try {
        const user = tokenStorage.user();
        const kyc = await userApi.getAllKycByStatus(user!.id, null);

        patchState(store, { kyc: kyc.data, status: 'success' });
      } catch (e) {
        patchState(store, { status: 'failure' });
      }
    },
  }))
);
