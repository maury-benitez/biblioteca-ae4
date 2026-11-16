package gestion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import modelo.Usuario;

/**
 * Administra los usuarios del sistema: registración, inicio de sesión
 * y recuperación de contraseña y de nombre de usuario (RF1).
 */
public class GestorUsuarios implements Serializable {

    private static final long serialVersionUID = 1L;

    private ArrayList<Usuario> usuarios;

    public GestorUsuarios() {
        usuarios = new ArrayList<>();
    }

    // Devuelve una copia de la lista para que no la modifiquen desde afuera
    public List<Usuario> getUsuarios() {
        return new ArrayList<>(usuarios);
    }

    // Registra un usuario si no existe otro con el mismo nombre de usuario o DNI
    public boolean registrarUsuario(Usuario usuario) {
        if (usuario == null) {
            return false;
        }
        if (existeUsuario(usuario.getNombreUsuario()) || existeDni(usuario.getDni())) {
            return false;
        }

        usuarios.add(usuario);
        return true;
    }

    // Devuelve el usuario si las credenciales son correctas, o null si no coinciden
    public Usuario iniciarSesion(String nombreUsuario, String contrasena) {
        for (Usuario usuario : usuarios) {
            if (usuario.validarCredenciales(nombreUsuario, contrasena)) {
                return usuario;
            }
        }

        return null;
    }

    // Cambia la contraseña si el nombre de usuario y el DNI corresponden al mismo usuario
    public boolean recuperarContrasena(String nombreUsuario, String dni, String nuevaContrasena) {
        if (nombreUsuario == null || dni == null
                || nuevaContrasena == null || nuevaContrasena.isBlank()) {
            return false;
        }

        for (Usuario usuario : usuarios) {
            if (usuario.getNombreUsuario().equals(nombreUsuario.strip())
                    && usuario.getDni().equals(dni.strip())) {
                // no muestro la contraseña anterior, la reemplazo por una nueva (RNF2)
                usuario.setContrasena(nuevaContrasena);
                return true;
            }
        }

        return false;
    }

    // Devuelve el nombre de usuario que corresponde al DNI y email, o null si no existe
    public String recuperarUsuario(String dni, String email) {
        if (dni == null || email == null) {
            return null;
        }

        for (Usuario usuario : usuarios) {
            if (usuario.getDni().equals(dni.strip())
                    && usuario.getEmail().equalsIgnoreCase(email.strip())) {
                return usuario.getNombreUsuario();
            }
        }

        return null;
    }

    // Indica si ya hay un usuario con ese nombre de usuario
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

    // Indica si ya hay un usuario con ese DNI
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