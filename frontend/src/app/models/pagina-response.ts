export interface PaginaResponse<T> {
  contenido: T[];
  totalElementos: number;
  totalPaginas: number;
  pagina: number;
  tamano: number;
}
