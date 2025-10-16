export interface CompanyRequest {
  name: string;
  taxId: string;
  annualIncome: number;
}

export interface CompanyResponse {
  idCompany: string;
  name: string;
  taxId: string;
  annualIncome: number;
  userId: string;
  userName: string;
  createdAt: Date;
}
