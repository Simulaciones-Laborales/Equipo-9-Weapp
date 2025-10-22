import { computed, inject } from '@angular/core';
import {
  KYCEntityType,
  DisplayKycStatus,
  KYCVerificationResponse,
  KYCVerificationStatus,
} from '@core/models/kyc-model';
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
    userKycStatus: computed((): DisplayKycStatus => {
      if (store.kyc().length === 0) {
        return 'Sin KYC';
      }

      const userKyc = store.kyc().filter((k) => k.kycEntityType === KYCEntityType.USER);

      const n = userKyc.length;

      if (n === 0) {
        return 'Sin KYC';
      }

      const lastKyc = userKyc.sort((a, b) => {
        if (a.submissionDate <= b.submissionDate) {
          return -1;
        }

        return 1;
      })[n - 1];

      switch (lastKyc.status) {
        case 'PENDING':
          return 'KYC Pendiente';
        case 'VERIFIED':
          return 'KYC Verificado';
        case 'REJECTED':
          return 'KYC Rechazado';
        case 'REVIEW_REQUIRED':
          return 'KYC Por Revisar';
        default:
          return 'Sin KYC';
      }
    }),
  })),
  withMethods((store, userApi = inject(UserApi), tokenStorage = inject(TokenStorage)) => ({
    fetchInitialData: async () => {
      patchState(store, { status: 'loading' });

      try {
        const user = tokenStorage.user();
        const kyc = await userApi.getAllKYC(user!.id);

        patchState(store, { kyc: kyc.data, status: 'success' });
      } catch (e) {
        patchState(store, { status: 'failure' });
      }
    },
  }))
);
