import { computed, inject } from '@angular/core';
import { signalStore, withComputed } from '@ngrx/signals';
import { LayoutStore } from '../layout/layout-store';

export const CompanyStore = signalStore(
  withComputed((_store, layoutStore = inject(LayoutStore)) => ({
    showKycMessage: computed(() => layoutStore.userKycStatus().name !== 'Verificado'),
  }))
);
