import { computed, inject } from '@angular/core';
import {
  KYCEntityType,
  KYCVerificationResponse,
  KYCVerificationStatus,
} from '@core/models/kyc-model';
import { UserApi } from '@core/services/user-api';
import { Status } from '@core/types';
import { patchState, signalStore, withComputed, withMethods, withState } from '@ngrx/signals';
import { CompanyRequest, CompanyResponse } from './models/company-model';
import { CompanyApi } from './services/company-api';
import { KycApi } from './services/kyc-api';

type State = {
  kyc: KYCVerificationResponse[];
  kycStatus: Status;
  showNewKycForm: boolean;
  kycUserVerificationStatus: Status;
  companies: CompanyResponse[];
  getCompaniesStatus: Status;
  showNewCompanyForm: boolean;
  createCompanyStatus: Status;
  showCompanies: boolean;
};

const initialState: State = {
  kyc: [],
  kycStatus: 'pending',
  showNewKycForm: false,
  kycUserVerificationStatus: 'pending',
  companies: [],
  getCompaniesStatus: 'pending',
  showNewCompanyForm: false,
  createCompanyStatus: 'pending',
  showCompanies: false,
};

export const CompanyStore = signalStore(
  withState(initialState),
  withComputed((store) => ({
    loadingMessage: computed(() => {
      if (store.kycStatus() === 'loading') {
        return 'Cargando documentación...';
      }

      if (store.kycUserVerificationStatus() === 'loading') {
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
          const kyc = await userApi.getAllKycByStatus(userId, KYCVerificationStatus.VERIFIED);
          patchState(store, { kycStatus: 'success', kyc: kyc.data });
        } catch (e) {
          patchState(store, { kycStatus: 'failure', kyc: [] });
        }
      },
      setShowNewKycForm: (show: boolean) => {
        patchState(store, { showNewKycForm: show });
      },
      startUserKycVerification: async (
        entityId: string,
        entityType: KYCEntityType,
        selfie: File,
        dniFront: File,
        dniBack: File
      ) => {
        patchState(store, { kycUserVerificationStatus: 'loading' });

        try {
          const kyc = await kycApi.startVerification(
            entityId,
            entityType,
            selfie,
            dniFront,
            dniBack
          );

          patchState(store, { kycUserVerificationStatus: 'success', kyc: [...store.kyc(), kyc!] });
        } catch (e) {
          patchState(store, { kycUserVerificationStatus: 'failure' });
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
            showNewCompanyForm: false,
            showCompanies: true,
          });
        } catch (e) {
          patchState(store, { createCompanyStatus: 'failure' });
        }
      },
      setShowCompanies: (show: boolean) => {
        patchState(store, { showCompanies: show });
      },
    })
  )
);
