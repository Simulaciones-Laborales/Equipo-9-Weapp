import { computed, inject } from '@angular/core';
import { Status } from '@core/types';
import { patchState, signalStore, withComputed, withMethods, withState } from '@ngrx/signals';
import { CompanyRequest, CompanyResponse } from './models/company-model';
import { CompanyApi } from './services/company-api';
import { LayoutStore } from '../layout/layout-store';

export const CompanyStore = signalStore(
  withComputed((_store, layoutStore = inject(LayoutStore)) => ({
    showKycMessage: computed(() => layoutStore.userKycStatus().name !== 'KYC Verificado'),
  }))
);
