export interface JwtPayload {
  sub: string;
  issuedAt: Date;
  exp: Date;
}
