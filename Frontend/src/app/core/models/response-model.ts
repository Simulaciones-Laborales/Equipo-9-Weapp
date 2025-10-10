export interface Response<T> {
  isError: boolean;
  message: string;
  status: string;
  code: number;
  data: T;
}
