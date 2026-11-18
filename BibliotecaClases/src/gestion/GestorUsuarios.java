package gestion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import modelo.Usuario;

/**
 * Administra los usuarios, la comprobación de credenciales y la recuperación de datos de acceso.
 */
public class GestorUsuarios implements Serializable {

    private static final long serialVersionUID = 1L;

    private ArrayList<Usuario> usuarios;

    /**
     * Inicializa un registro de usuarios vacío.
     */
    public GestorUsuarios() {
        usuarios = new ArrayList<>();
    }

    /**
     * Obtiene una copia de la lista de usuarios registrados.
     *
     * @return una lista independiente con las mismas referencias a los objetos registrados.
     */
    public List<Usuario> getUsuarios() {
        return new ArrayList<>(usuarios);
    }

    /**
     * Registra un usuario si no se repiten su nombre de usuario ni su DNI.
     *
     * @param usuario usuario que se desea registrar.
     * @return {@code true} si se registra; {@code false} si es nulo o alguno de esos datos ya existe.
     */
    public boolean registrarUsuario(Usuario usuario) {
        if (usuario == null) {
            return false;
        }
        // Mantiene únicos el nombre de acceso y el DNI dentro del registro de usuarios.
        if (existeUsuario(usuario.getNombreUsuario()) || existeDni(usuario.getDni())) {
            return false;
        }

        usuarios.add(usuario);
        return true;
    }

    /**
     * Busca al usuario cuyas credenciales coinciden con las recibidas.
     *
     * @param nombreUsuario nombre de usuario para el acceso al sistema.
     * @param contrasena contraseña utilizada en la operación.
     * @return el usuario autenticado o {@code null} si las credenciales no coinciden.
     */
    public Usuario iniciarSesion(String nombreUsuario, String contrasena) {
        for (Usuario usuario : usuarios) {
            if (usuario.validarCredenciales(nombreUsuario, contrasena)) {
                return usuario;
            }
        }

        return null;
    }

    /**
     * Sustituye la contraseña del usuario cuyo nombre de usuario y DNI coinciden.
     *
     * @param nombreUsuario nombre de usuario para el acceso al sistema.
     * @param dni DNI que identifica a la persona.
     * @param nuevaContrasena nueva contraseña que se asignará al usuario.
     * @return {@code true} si se actualiza; {@code false} si los datos son nulos, la nueva contraseña está en blanco o no hay coincidencia.
     */
    public boolean recuperarContrasena(String nombreUsuario, String dni, String nuevaContrasena) {
        if (nombreUsuario == null || dni == null
                || nuevaContrasena == null || nuevaContrasena.isBlank()) {
            return false;
        }

        for (Usuario usuario : usuarios) {
            if (usuario.getNombreUsuario().equals(nombreUsuario.strip())
                    && usuario.getDni().equals(dni.strip())) {

                // Sustituye la credencial después de comprobar la identidad, sin mostrar la contraseña anterior.
                usuario.setContrasena(nuevaContrasena);
                return true;
            }
        }

        return false;
    }

    /**
     * Busca el nombre de usuario mediante el DNI y el correo electrónico.
     *
     * @param dni DNI que identifica a la persona.
     * @param email correo electrónico de la persona.
     * @return el nombre de usuario encontrado o {@code null} si los datos son nulos o no coinciden.
     */
    public String recuperarUsuario(String dni, String email) {
        if (dni == null || email == null) {
            return null;
        }

        // Exige la coincidencia del DNI y del correo, sin distinguir mayúsculas en este último.
        for (Usuario usuario : usuarios) {
            if (usuario.getDni().equals(dni.strip())
                    && usuario.getEmail().equalsIgnoreCase(email.strip())) {
                return usuario.getNombreUsuario();
            }
        }

        return null;
    }

    /**
     * Comprueba si el nombre de usuario recibido está registrado.
     *
     * @param nombreUsuario nombre de usuario para el acceso al sistema.
     * @return {@code true} si existe ese nombre de usuario; {@code false} en otro caso.
     */
    public boolean existeUsuario(String nombreUsuario) {
        if (nombreUsuario == null) {
            return false;
        }

        for (Usuario usuario : usuarios) {
            if (usuario.getNombreUsuario().equals(nombreUsuario.strip())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Comprueba si el DNI recibido pertenece a un usuario registrado.
     *
     * @param dni DNI que identifica a la persona.
     * @return {@code true} si existe ese DNI; {@code false} en otro caso.
     */
    public boolean existeDni(String dni) {
        if (dni == null) {
            return false;
        }

        for (Usuario usuario : usuarios) {
            if (usuario.getDni().equals(dni.strip())) {
                return true;
            }
        }

        return false;
    }

}
