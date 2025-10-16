import { computed, inject } from '@angular/core';
import { KYCVerificationResponse } from '@core/models/kyc-model';
import { Response } from '@core/models/response-model';
import { UserApi } from '@core/services/user-api';
import { Status } from '@core/types';
import { patchState, signalStore, withComputed, withMethods, withState } from '@ngrx/signals';
import { CompanyRequest, CompanyResponse } from './models/company-model';
import { CompanyApi } from './services/company-api';
import { KycApi } from './services/kyc-api';

type State = {
  kyc: Response<KYCVerificationResponse[]> | null;
  kycStatus: Status;
  showNewKycForm: boolean;
  kycVerificationStatus: Status;
  companies: CompanyResponse[];
  getCompaniesStatus: Status;
  showNewCompanyForm: boolean;
  createCompanyStatus: Status;
};

const initialState: State = {
  kyc: null,
  kycStatus: 'pending',
  showNewKycForm: false,
  kycVerificationStatus: 'pending',
  companies: [],
  getCompaniesStatus: 'pending',
  showNewCompanyForm: false,
  createCompanyStatus: 'pending',
};

export const CompanyStore = signalStore(
  withState(initialState),
  withComputed((store) => ({
    loadingMessage: computed(() => {
      if (store.kycStatus() === 'loading') {
        return 'Cargando documentación...';
      }

      if (store.kycVerificationStatus() === 'loading') {
        return 'Verificando documentación, puede tardar un poco...';
      }

      if (store.getCompaniesStatus() === 'loading') {
        return 'Cargando empresas...';
      }

      return null;
    }),
  })),
  withMethods(
    (
      store,
      userApi = inject(UserApi),
      companyApi = inject(CompanyApi),
      kycApi = inject(KycApi)
    ) => ({
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
      startKycVerification: async (selfie: File, dniFront: File, dniBack: File) => {
        patchState(store, { kycVerificationStatus: 'loading' });

        try {
          const newKyc = await kycApi.startVerification(selfie, dniFront, dniBack);

          store.kyc()?.data.push(newKyc!);

          patchState(store, { kycVerificationStatus: 'success', kyc: store.kyc() });
        } catch (e) {
          patchState(store, { kycVerificationStatus: 'failure' });
        }
      },
      getCompanies: async () => {
        patchState(store, { getCompaniesStatus: 'loading' });

        try {
          const companies = await companyApi.getAllByAuthenticatedUser();
          patchState(store, { getCompaniesStatus: 'success', companies });
        } catch (e) {
          patchState(store, { getCompaniesStatus: 'failure' });
        }
      },
      setShowNewCompanyForm: (show: boolean) => {
        patchState(store, { showNewCompanyForm: show });
      },
      createCompany: async (req: CompanyRequest) => {
        patchState(store, { createCompanyStatus: 'loading' });

        try {
          const company = await companyApi.create(req);
          patchState(store, {
            createCompanyStatus: 'success',
            companies: [...store.companies(), company],
          });
        } catch (e) {
          patchState(store, { createCompanyStatus: 'failure' });
        }
      },
    })
  )
);
