export enum KYCVerificationStatus {
  PENDING = 'PENDING',
  VERIFIED = 'VERIFIED',
  REJECTED = 'REJECTED',
  REVIEW_REQUIRED = 'REVIEW_REQUIRED',
}

export interface KYCVerificationResponse {
  idKyc: string;
  status: KYCVerificationStatus;
  verificationNotes: string;
  externalReferenceId: string;
  submissionDate: Date;
  verificationDate: Date;
  userId: string;
  userFullName: string;
  userEmail: string;
}
