import { inject } from '@angular/core';
import { KYCVerificationResponse } from '@core/models/kyc-model';
import { Response } from '@core/models/response-model';
import { UserApi } from '@core/services/user-api';
import { Status } from '@core/types';
import { patchState, signalStore, withMethods, withState } from '@ngrx/signals';

type State = {
  kyc: Response<KYCVerificationResponse[]> | null;
  kycStatus: Status;
  showNewKycForm: boolean;
};

const initialState: State = {
  kyc: null,
  kycStatus: 'pending',
  showNewKycForm: false,
};

export const CompanyStore = signalStore(
  withState(initialState),
  withMethods((store, userApi = inject(UserApi)) => ({
    getKycByUserId: async (userId: string) => {
      patchState(store, { kycStatus: 'loading' });

      try {
        const kyc = await userApi.getAllKYC(userId);
        patchState(store, { kycStatus: 'success', kyc });
      } catch (e) {
        patchState(store, { kycStatus: 'failure', kyc: null });
      }
    },
    setShowNewKycForm: (show: boolean) => {
      patchState(store, { showNewKycForm: show });
    },
  }))
);
