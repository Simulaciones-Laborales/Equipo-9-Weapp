import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { CreditApplicationResponse } from '@core/models/credit-application-model';
import { KYCVerificationResponse, KYCVerificationStatus } from '@core/models/kyc-model';
import { Status } from '@core/types';
import { patchState, signalStore, withMethods, withState } from '@ngrx/signals';
import { CompanyApi } from '../../services/company-api';

type State = {
  companyId: string;
  kyc: KYCVerificationResponse | null;
  credits: CreditApplicationResponse[];
  fetchStatus: Status;
  redirect: boolean;
};

const initialState: State = {
  companyId: '',
  kyc: null,
  credits: [],
  fetchStatus: 'pending',
  redirect: false,
};

export const Store = signalStore(
  withState(initialState),
  withMethods((store, companyApi = inject(CompanyApi)) => ({
    setCompanyId: (companyId: string) => {
      patchState(store, { companyId });
    },
    fetch: async () => {
      patchState(store, { fetchStatus: 'loading' });

      try {
        const kyc = await companyApi.getKyc(store.companyId());
        const credits = await companyApi.getAllCreditApplications(store.companyId());

        const redirect = kyc && kyc.status !== KYCVerificationStatus.VERIFIED;

        patchState(store, { fetchStatus: 'success', credits, kyc, redirect });
      } catch (e) {
        let statusCode: number = 0;

        if (e instanceof HttpErrorResponse) {
          statusCode = e.status;
        }

        patchState(store, { fetchStatus: 'failure', redirect: statusCode === 404 });
      }
    },
  }))
);
