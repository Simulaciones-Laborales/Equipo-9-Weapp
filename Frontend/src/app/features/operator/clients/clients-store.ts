import { inject } from '@angular/core';
import { KYCVerificationResponse, UpdateKycStatusDto } from '@core/models/kyc-model';
import { User } from '@core/models/user-model';
import { UserApi } from '@core/services/user-api';
import { Status } from '@core/types';
import { KycApi } from '@features/pyme/company/services/kyc-api';
import { patchState, signalStore, withMethods, withState } from '@ngrx/signals';

type State = {
  clients: User[];
  fetchStatus: Status;
  showDialog: boolean;
  kyc: KYCVerificationResponse | null;
  fetchKycStatus: Status;
  updateKycStatus: Status;
};

const initialState: State = {
  clients: [],
  fetchStatus: 'pending',
  showDialog: false,
  kyc: null,
  fetchKycStatus: 'pending',
  updateKycStatus: 'pending',
};

export const ClientsStore = signalStore(
  withState(initialState),
  withMethods((store, userApi = inject(UserApi), kycApi = inject(KycApi)) => ({
    fetchAll: async () => {
      patchState(store, { fetchStatus: 'loading' });

      try {
        const clients = (await userApi.getAll()).data;
        patchState(store, { fetchStatus: 'success', clients });
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
