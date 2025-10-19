/**
 * Modelo de respuesta para la aplicación de un crédito.
 */
export interface CreditApplicationResponse {
  idCreditApplication: string;
  companyId: string;
  companyName: string;
  amount: number;
  status: string;
  operatorComments: string;
  createdAt: Date;
  updatedAt: Date;
}
