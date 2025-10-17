export type Status = 'pending' | 'loading' | 'success' | 'failure';

export type KycVerificationFiles = {
  selfie: File | null;
  dniFront: File | null;
  dniBack: File | null;
};
