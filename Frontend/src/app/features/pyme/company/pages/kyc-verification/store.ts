import { computed, inject } from '@angular/core';
import {
  KYCEntityType,
  KYCVerificationResponse,
  KYCVerificationStatus,
} from '@core/models/kyc-model';
import { KycVerificationFiles, Status } from '@core/types';
import { patchState, signalStore, withComputed, withMethods, withState } from '@ngrx/signals';
import { KycApi } from '../../services/kyc-api';
import { CompanyApi } from '../../services/company-api';

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
  withComputed((store) => ({
    action: computed(() => {
      const kyc = store.kyc();

      if (!kyc) {
        return {
          showForm: true,
          message: '',
          redirect: false,
        };
      }

      if (kyc.status === KYCVerificationStatus.PENDING) {
        return {
          showForm: false,
          message: 'Tu verificación se encuentra pendiente de revisión.',
          redirect: false,
        };
      }

      if (kyc.status === KYCVerificationStatus.REJECTED) {
        return {
          showForm: false,
          message:
            'Tu verificación ha sido rechazada, por favor contacta a uno de nuestros operadores.',
          redirect: false,
        };
      }

      if (kyc.status === KYCVerificationStatus.REVIEW_REQUIRED) {
        return {
          showForm: false,
          message: 'Tu verificación requiere de una nueva revisión.',
          redirect: false,
        };
      }

      return {
        showForm: false,
        message: '',
        redirect: true,
      };
    }),
  })),
  withMethods((store, companyApi = inject(CompanyApi), kycApi = inject(KycApi)) => ({
    setCompanyId: (companyId: string) => {
      patchState(store, { companyId });
    },
    fetch: async () => {
      patchState(store, { fetchStatus: 'loading' });

      try {
        const kyc = await companyApi.getKyc(store.companyId());
        patchState(store, { fetchStatus: 'success', kyc: kyc });
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
