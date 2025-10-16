import { inject } from '@angular/core';
import { KYCVerificationResponse } from '@core/models/kyc-model';
import { Response } from '@core/models/response-model';
import { UserApi } from '@core/services/user-api';
import { Status } from '@core/types';
import { patchState, signalStore, withMethods, withState } from '@ngrx/signals';

type State = {
  kyc: Response<KYCVerificationResponse[]> | null;
  status: Status;
};

const initialState: State = {
  kyc: null,
  status: 'pending',
};

export const KycStore = signalStore(
  withState(initialState),
  withMethods((store, userApi = inject(UserApi)) => ({
    fetchAllByUserId: async (userId: string) => {
      patchState(store, { status: 'loading' });

      try {
        const kyc = await userApi.getAllKYC(userId);
        patchState(store, { status: 'success', kyc });
      } catch (e) {
        patchState(store, { status: 'failure', kyc: null });
      }
    },
  }))
);
