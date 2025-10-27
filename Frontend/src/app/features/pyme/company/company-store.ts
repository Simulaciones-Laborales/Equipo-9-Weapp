import { computed, inject } from '@angular/core';
import { Status } from '@core/types';
import { patchState, signalStore, withComputed, withMethods, withState } from '@ngrx/signals';
import { CompanyRequest, CompanyResponse } from './models/company-model';
import { CompanyApi } from './services/company-api';

type State = {
  showKycMessage: boolean;
  companies: CompanyResponse[];
  getCompaniesStatus: Status;
  showNewCompanyForm: boolean;
  createCompanyStatus: Status;
  showCompanies: boolean;
  selectedCompany: CompanyResponse | null;
};

const initialState: State = {
  showKycMessage: false,
  companies: [],
  getCompaniesStatus: 'pending',
  showNewCompanyForm: false,
  createCompanyStatus: 'pending',
  showCompanies: false,
  selectedCompany: null,
};

export const CompanyStore = signalStore(
  { providedIn: 'root' },
  withState(initialState),
  withComputed((store) => ({
    loadingMessage: computed(() => {
      if (store.getCompaniesStatus() === 'loading') {
        return 'Cargando empresas...';
      }

      return null;
    }),
  })),
  withMethods((store, companyApi = inject(CompanyApi)) => ({
    setShowKycMessage: (showKycMessage: boolean) => {
      patchState(store, { showKycMessage });
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
    setSelectedCompany: (company: CompanyResponse) => {
      patchState(store, { selectedCompany: company });
    },
  }))
);
