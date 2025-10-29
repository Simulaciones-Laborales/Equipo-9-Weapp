import { Status } from '@core/types';
import { signalStore, withMethods, withState } from '@ngrx/signals';

type State = {
  status: Status;
};

const initialState: State = {
  status: 'pending',
};

export const Store = signalStore(
  withState(initialState),
  withMethods((store) => ({}))
);
