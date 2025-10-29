import { inject } from '@angular/core';
import { KYCEntityType, KYCVerificationResponse } from '@core/models/kyc-model';
import { KycVerificationFiles, Status } from '@core/types';
import { patchState, signalStore, withMethods, withState } from '@ngrx/signals';
import { KycApi } from '../../services/kyc-api';

type State = {
  companyId: string;
  kyc: KYCVerificationResponse | null;
  fetchStatus: Status;
  newKycStatus: Status;
  newKyc: KYCVerificationResponse | null;
};

const initialState: State = {
  companyId: '',
  kyc: null,
  fetchStatus: 'pending',
  newKycStatus: 'pending',
  newKyc: null,
};

export const Store = signalStore(
  withState(initialState),
  withMethods((store, kycApi = inject(KycApi)) => ({
    setCompanyId: (companyId: string) => {
      patchState(store, { companyId });
    },
    fetch: async () => {
      patchState(store, { fetchStatus: 'loading' });

      try {
        const kyc = await kycApi.getByCompanyId(store.companyId());
        patchState(store, { fetchStatus: 'success', kyc: kyc.data });
      } catch (e) {
        patchState(store, { fetchStatus: 'failure' });
      }
    },
    startVerification: async (files: KycVerificationFiles) => {
      patchState(store, { newKycStatus: 'loading' });

      try {
        const newKyc = await kycApi.startVerification(
          store.companyId(),
          KYCEntityType.COMPANY,
          files.document1!,
          files.document2!,
          files.document3!
        );

        patchState(store, { newKycStatus: 'success', newKyc: newKyc.data });
      } catch (e) {
        patchState(store, { newKycStatus: 'failure' });
      }
    },
  }))
);
