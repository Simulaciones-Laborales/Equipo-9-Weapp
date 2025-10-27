export type Status = 'pending' | 'loading' | 'success' | 'failure';

export type KycVerificationFiles = {
  document1: File | null;
  document2: File | null;
  document3: File | null;
};

export type Pageable = {
  page: number;
  size: number;
  sort: string[];
};

export type PageableResponse<T> = {
  totalPages: number;
  totalElements: number;
  size: number;
  pageable: { pageNumber: number };
  content: T[];
};
