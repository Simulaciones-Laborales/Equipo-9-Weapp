import { inject } from '@angular/core';
import { Response } from '@core/models/response-model';
import { Status } from '@core/types';
import { LoginReq, LoginRes } from '@features/pyme/auth/models/auth-model';
import { AuthApi } from '@features/pyme/auth/services/auth-api';
import { patchState, signalStore, withMethods, withState } from '@ngrx/signals';

type State = {
  userLogged: Response<LoginRes> | null;
  status: Status;
  error: string | null;
};

const initialState: State = {
  userLogged: null,
  status: 'pending',
  error: null,
};

export const LoginStore = signalStore(
  withState(initialState),
  withMethods((store, service = inject(AuthApi)) => ({
    login: async (data: LoginReq) => {
      patchState(store, { status: 'loading', error: null });

      try {
        const userLogged = await service.login(data);
        patchState(store, { status: 'success', error: null, userLogged });
      } catch (e) {
        patchState(store, { status: 'failure', userLogged: null });
      }
    },
  }))
);
