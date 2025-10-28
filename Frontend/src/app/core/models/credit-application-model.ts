import { RiskDocument } from './risk-document-model';

/**
 * Estado de una solicitud de crédito.
 */
export enum CreditApplicationStatus {
  PENDING = 'PENDING',
  UNDER_REVIEW = 'UNDER_REVIEW',
  APPROVED = 'APPROVED',
  REJECTED = 'REJECTED',
  CANCELLED = 'CANCELLED',
}

/**
 * Propósito o razón de la solicitud de crédito.
 */
export enum CreditApplicationPurpose {
  WORK_CAPITAL = 'CAPITAL_TRABAJO',
  INVESTMENT = 'INVERSION',
  CONSUMPTION = 'CONSUMO',
  REFINANCING = 'REFINANCIAMIENTO',
  INVENTORY_PURCHASE = 'COMPRA_INVENTARIO',
  INFRASTRUCTURE_IMPROVEMENT = 'MEJORA_INFRAESTRUCTURA',
  TECHNOLOGY = 'TECNOLOGIA',
  MARKETING = 'MARKETING',
  EMERGENCY = 'EMERGENCIA',
}

/**
 * Modelo de respuesta para la aplicación de un crédito.
 */
export interface CreditApplicationResponse {
  idCreditApplication: string;
  companyId: string;
  companyName: string;
  amount: number;
  status: CreditApplicationStatus;
  termMonths: number;
  creditPurpose: CreditApplicationPurpose;
  riskScore: number;
  createdAt: Date;
  updatedAt: Date;
  documents: RiskDocument[];
}

/**
 * Modelo para actualizar el estado de una solicitud de crédito.
 */
export interface UpdateCreditApplicationStatusDto {
  newStatus: CreditApplicationStatus;
  comments: string;
}

export enum HistoryActionType {
  CREATION = 'CREATION',
  UPDATE = 'UPDATE',
  STATUS_CHANGE = 'STATUS_CHANGE',
  COMMENT = 'COMMENT',
  OPERATOR_ACTION = 'OPERATOR_ACTION',
  AUTOMATION = 'AUTOMATION',
  APPROVAL = 'APPROVAL',
  REJECTION = 'REJECTION',
  CANCELLATION = 'CANCELLATION',
  DELETION = 'DELETION',
}

export interface CreditApplicationHistory {
  id: string;
  creditApplicationId: string;
  actionType: HistoryActionType;
  action: string;
  comments: string;
  operatorId: string;
  operatorName: string;
  createdAt: Date;
}
