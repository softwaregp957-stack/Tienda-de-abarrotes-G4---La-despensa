package Modelos;
 
/**
 * Roles posibles de un usuario en el sistema.
 * TODO: ajusta estos valores a los roles reales de tu aplicación
 * (deben coincidir, en texto, con lo que guardes en la columna "rol" de la BD).
 */
public enum UsuarioRol {
    ADMIN,
    EMPLEADO;
 
    /**
     * Convierte el texto guardado en la BD a un valor del enum, sin lanzar
     * excepción si viene con mayúsculas/minúsculas distintas o espacios.
     * Si el valor no coincide con ninguno, regresa null (la Vista deberá manejarlo).
     * @param texto
     * @return 
     */
    public static UsuarioRol DesdeTexto(String texto) {
        if (texto == null) {
            return null;
        }
        try {
            return UsuarioRol.valueOf(texto.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}