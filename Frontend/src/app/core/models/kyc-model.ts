export enum KYCVerificationStatus {
  PENDING = 'PENDING',
  VERIFIED = 'VERIFIED',
  REJECTED = 'REJECTED',
  REVIEW_REQUIRED = 'REVIEW_REQUIRED',
}

export enum KYCEntityType {
  USER = 'USER',
  COMPANY = 'COMPANY',
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
  selfieUrl: string;
  dniFrontUrl: string;
  dniBackUrl: string;
}
