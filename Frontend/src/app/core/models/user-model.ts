/**
 * Roles de usuario.
 */
export enum UserRole {
  ADMIN = 'ADMIN',
  OPERADOR = 'OPERADOR',
  PYME = 'PYME',
}

/**
 * Modelo de usuario.
 */
export interface User {
  id: string;
  username: string;
  email: string;
  contact: string;
  role: UserRole;
  isActive: boolean;
  createdAt: Date;
  updatedAt: Date;
}
