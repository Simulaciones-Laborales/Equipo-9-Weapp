import { UserRole } from '@core/models/user-model';

/**
 * Modelo de registro de usuarios.
 */
export interface RegisterModel {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  contact: string;
  birthDate: string;
  dni: string;
  country: string;
}

/**
 * Solicitud de autenticación del usuario.
 */
export interface LoginReq {
  email: string;
  password: string;
}

/**
 * Respuesta del servidor a la solicitud de inicio de sesión del usuario.
 */
export interface LoginRes {
  id: string;
  username: string;
  firstName: string;
  lastName: string;
  token: string;
  role: UserRole;
}
