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
  kycEntityType: KYCEntityType;
  entityName: string;
  document1: string;
  document2: string;
  document3: string;
}
