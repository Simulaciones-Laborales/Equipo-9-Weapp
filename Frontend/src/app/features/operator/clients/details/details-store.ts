import { inject } from '@angular/core';
import { KYCVerificationResponse, UpdateKycStatusDto } from '@core/models/kyc-model';
import { User } from '@core/models/user-model';
import { UserApi } from '@core/services/user-api';
import { Status } from '@core/types';
import { CompanyResponse } from '@features/pyme/company/models/company-model';
import { KycApi } from '@features/pyme/company/services/kyc-api';
import { patchState, signalStore, withMethods, withState } from '@ngrx/signals';

type State = {
  userId: string;
  user: User | null;
  fetchStatus: Status;
  companies: CompanyResponse[];
  showDialog: boolean;
  kyc: KYCVerificationResponse | null;
  fetchKycStatus: Status;
  updateKycStatus: Status;
};

const initialState: State = {
  userId: '',
  user: null,
  fetchStatus: 'pending',
  companies: [],
  kyc: null,
  fetchKycStatus: 'pending',
  updateKycStatus: 'pending',
  showDialog: false,
};

export const DetailsStore = signalStore(
  withState(initialState),
  withMethods((store, userApi = inject(UserApi), kycApi = inject(KycApi)) => ({
    setUserId: (userId: string) => {
      patchState(store, { userId });
    },
    fetch: async () => {
      patchState(store, { fetchStatus: 'loading' });

      try {
        const userId = store.userId();
        const user = await userApi.getById(userId);
        const companies = await userApi.getCompanies(userId);

        patchState(store, { fetchStatus: 'success', user, companies: companies.data });
      } catch (e) {
        patchState(store, { fetchStatus: 'failure' });
      }
    },
    fetchKyc: async (userId: string) => {
      patchState(store, { showDialog: true, fetchKycStatus: 'loading' });

      try {
        const kyc = (await userApi.getAllKycByStatus(userId, null)).data[0];
        patchState(store, { fetchKycStatus: 'success', kyc });
      } catch (e) {
        patchState(store, { fetchKycStatus: 'failure' });
      }
    },
    closeDialog: () => {
      patchState(store, { showDialog: false, kyc: null, updateKycStatus: 'pending' });
    },
    updateKyc: async (dto: UpdateKycStatusDto) => {
      patchState(store, { updateKycStatus: 'loading' });

      try {
        await kycApi.updateStatus(store.kyc()!.idKyc, dto);
        patchState(store, { updateKycStatus: 'success' });
      } catch (e) {
        patchState(store, { updateKycStatus: 'failure' });
      }
    },
  }))
);
