import { inject } from '@angular/core';
import { patchState, signalStore, withMethods, withState } from '@ngrx/signals';
import { KycApi } from '../company/services/kyc-api';
import { KycVerificationFiles, Status } from '@core/types';
import { TokenStorage } from '@core/services/token-storage';
import { KYCEntityType } from '@core/models/kyc-model';
import { LayoutStore } from '../layout/layout-store';

type State = {
  showKycButton: boolean;
  showKycForm: boolean;
  startVerificationStatus: Status;
};

const initialState: State = {
  showKycButton: false,
  showKycForm: false,
  startVerificationStatus: 'pending',
};

export const DashboardStore = signalStore(
  withState(initialState),
  withMethods(
    (
      store,
      kycApi = inject(KycApi),
      tokenStorage = inject(TokenStorage),
      layoutStore = inject(LayoutStore)
    ) => ({
      setShowKycButton: (showKycButton: boolean) => {
        patchState(store, { showKycButton });
      },
      setShowUserKycForm: (showKycForm: boolean) => {
        patchState(store, { showKycForm });
      },
      startUserKycVerification: async (files: KycVerificationFiles) => {
        patchState(store, { startVerificationStatus: 'loading' });

        try {
          const { id } = tokenStorage.user()!;
          const { document1, document2, document3 } = files;

          await kycApi.startVerification(
            id,
            KYCEntityType.USER,
            document1!,
            document2!,
            document3!
          );

          await layoutStore.fetchInitialData();

          patchState(store, { startVerificationStatus: 'success' });
        } catch (e) {
          patchState(store, { startVerificationStatus: 'failure' });
        }
      },
    })
  )
);
