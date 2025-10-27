import { computed, inject } from '@angular/core';
import { Status } from '@core/types';
import { patchState, signalStore, withComputed, withMethods, withState } from '@ngrx/signals';
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
  withComputed((store) => ({
    buttonText: computed(() => {
      if (store.status() === 'loading') {
        return 'Creando tu cuenta...';
      }

      return 'Crear cuenta';
    }),
  })),
  withMethods((store, service = inject(AuthApi)) => ({
    register: async (data: RegisterModel) => {
      patchState(store, { status: 'loading' });

      try {
        const response = await service.register(data);
        patchState(store, { status: 'success', error: null, response });
      } catch (e) {
        patchState(store, { status: 'failure', response: null });
      }
    },
  }))
);
