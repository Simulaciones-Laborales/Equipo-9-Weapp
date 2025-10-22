export type Status = 'pending' | 'loading' | 'success' | 'failure';

export type KycVerificationFiles = {
  document1: File | null;
  document2: File | null;
  document3: File | null;
};
