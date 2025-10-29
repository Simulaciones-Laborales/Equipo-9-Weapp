export enum KYCVerificationStatus {
  PENDING = 'PENDING',
  VERIFIED = 'VERIFIED',
  REJECTED = 'REJECTED',
  REVIEW_REQUIRED = 'REVIEW_REQUIRED',
}

export type DisplayKycStatus =
  | 'Sin Verificar'
  | 'Verificación Pendiente'
  | 'Verificado'
  | 'Rechazado'
  | 'Por Revisar';

export interface UserKycStatus {
  name: DisplayKycStatus;
  message: string | null;
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
  document1Url: string;
  document2Url: string;
  document3Url: string;
}

export interface UpdateKycStatusDto {
  status: KYCVerificationStatus;
  notes: string;
}
