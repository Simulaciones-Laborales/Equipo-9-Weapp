/**
 * Modelo de respuesta para un documento de una solicitud de crédito.
 */
export interface RiskDocument {
  id: string;
  name: string;
  documentUrl: string;
  scoreImpact: number;
}
