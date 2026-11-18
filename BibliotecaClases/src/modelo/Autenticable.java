package modelo;

/**
 * Define la comprobación de credenciales de los objetos que permiten autenticar usuarios.
 */
public interface Autenticable {

    /**
     * Comprueba si el nombre de usuario y la contraseña corresponden al usuario.
     *
     * @param nombreUsuario nombre de usuario para el acceso al sistema.
     * @param contrasena contraseña utilizada en la operación.
     * @return {@code true} si ambas credenciales coinciden; {@code false} en otro caso.
     */
    boolean validarCredenciales(String nombreUsuario, String contrasena);

}
