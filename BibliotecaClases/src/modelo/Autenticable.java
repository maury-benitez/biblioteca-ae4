package modelo;

/*
 * Interfaz Autenticable: la implementan las clases que pueden
 * iniciar sesión en el sistema.
 */
public interface Autenticable {

    // Devuelve true si el usuario y la contraseña recibidos son correctos
    boolean validarCredenciales(String nombreUsuario, String contrasena);

}