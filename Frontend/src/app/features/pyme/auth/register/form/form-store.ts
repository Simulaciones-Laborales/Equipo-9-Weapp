import { inject } from '@angular/core';
import { Status } from '@core/types';
import { patchState, signalStore, withMethods, withState } from '@ngrx/signals';
import { AuthApi } from '../../services/auth-api';
import { LoginRes, RegisterModel } from '../../models/auth-model';
import { Response } from '@core/models/response-model';

type State = {
  response: Response<LoginRes> | null;
  status: Status;
  error: string | null;
};

const initialState: State = {
  response: null,
  status: 'pending',
  error: null,
};

export const FormStore = signalStore(
  withState(initialState),
  withMethods((store, service = inject(AuthApi)) => ({
    register: async (data: RegisterModel) => {
      patchState(store, { status: 'loading' });

      try {
        await service.register(data);
        patchState(store, { status: 'success', error: null });
      } catch (e) {
        const message = e instanceof Error ? e.message : '';
        patchState(store, { status: 'failure', error: message });
      }
    },
  }))
);
