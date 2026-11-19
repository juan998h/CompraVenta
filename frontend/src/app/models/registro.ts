export interface RegistroRequest {
  correo: string;
  contrasena: string;
  nombre: string;
  rol: 'COMPRADOR' | 'VENDEDOR';
  especialidad: string | null;
}

export interface UsuarioResponse {
  usuarioId: string;
  correo: string;
  nombre: string;
  rol: string;
}