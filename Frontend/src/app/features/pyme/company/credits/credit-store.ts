import { KycVerificationFiles, Status } from '@core/types';
import { CreditApplicationResponse } from '@core/models/credit-application-model';
import { patchState, signalStore, withComputed, withMethods, withState } from '@ngrx/signals';
import { computed, inject } from '@angular/core';
import { CreditApplicationApi } from '@core/services/credit-application-api';
import { HttpErrorResponse } from '@angular/common/http';
import { KycApi } from '../services/kyc-api';
import { KYCEntityType } from '@core/models/kyc-model';

type Show = 'kyc' | 'list';

type State = {
  companyId: string;
  credits: CreditApplicationResponse[];
  fetchCreditsStatus: Status;
  errorStatusCode: number | null;
  show: Show;
  kycVerificationStatus: Status;
};

const initialState: State = {
  companyId: '',
  credits: [],
  fetchCreditsStatus: 'pending',
  errorStatusCode: null,
  show: 'list',
  kycVerificationStatus: 'pending',
};

export const CreditStore = signalStore(
  withState(initialState),
  withComputed((store) => ({
    loadingMessage: computed(() => {
      if (store.fetchCreditsStatus() === 'loading') {
        return 'Cargando mis aplicaciones de crédito...';
      }

      return null;
    }),
  })),
  withMethods(
    (store, creditApplicationApi = inject(CreditApplicationApi), kycApi = inject(KycApi)) => ({
      fetchAll: async (companyId: string) => {
        patchState(store, { fetchCreditsStatus: 'loading' });

        try {
          const credits = await creditApplicationApi.getAllByCompanyId(companyId);

          patchState(store, {
            companyId,
            credits,
            fetchCreditsStatus: 'success',
            errorStatusCode: null,
          });
        } catch (e) {
          const errorStatusCode = e instanceof HttpErrorResponse ? e.status : null;
          patchState(store, { fetchCreditsStatus: 'failure', errorStatusCode });
        }
      },
      setShow: (show: Show) => {
        patchState(store, { show });
      },
      startKycVerification: async (files: KycVerificationFiles) => {
        patchState(store, { kycVerificationStatus: 'loading' });

        try {
          await kycApi.startVerification(
            store.companyId(),
            KYCEntityType.COMPANY,
            files.document1!,
            files.document2!,
            files.document3!
          );

          patchState(store, { kycVerificationStatus: 'success', show: 'list' });
        } catch (e) {
          patchState(store, { kycVerificationStatus: 'failure' });
        }
      },
    })
  )
);
