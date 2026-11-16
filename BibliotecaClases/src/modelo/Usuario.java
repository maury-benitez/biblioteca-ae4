package modelo;

/**
 * Representa a un usuario de la biblioteca como especialización de Persona.
 */
public class Usuario extends Persona implements Autenticable {

    private static final long serialVersionUID = 1L;

    private String nombreUsuario;
    private String contrasena;

    // Constructor: recibe los datos de la persona y los del usuario
    public Usuario(String nombre, String apellido, String dni, String email, String telefono,
            String nombreUsuario, String contrasena) {
        // llamo al constructor de Persona con los datos comunes
        super(nombre, apellido, dni, email, telefono);
        if (nombreUsuario == null || nombreUsuario.isBlank()) {
            throw new IllegalArgumentException("El nombre de usuario no puede estar en blanco.");
        }
        if (contrasena == null || contrasena.isBlank()) {
            throw new IllegalArgumentException("La contraseña no puede estar en blanco.");
        }

        this.nombreUsuario = nombreUsuario.strip();
        this.contrasena = contrasena;
    }

    // Devuelve el nombre de usuario
    public String getNombreUsuario() {
        return nombreUsuario;
    }

    // Cambia el nombre de usuario
    public void setNombreUsuario(String nombreUsuario) {
        if (nombreUsuario == null || nombreUsuario.isBlank()) {
            throw new IllegalArgumentException("El nombre de usuario no puede estar en blanco.");
        }

        this.nombreUsuario = nombreUsuario.strip();
    }

    // Devuelve la contraseña
    public String getContrasena() {
        return contrasena;
    }

    // Cambia la contraseña
    public void setContrasena(String contrasena) {
        if (contrasena == null || contrasena.isBlank()) {
            throw new IllegalArgumentException("La contraseña no puede estar en blanco.");
        }

        this.contrasena = contrasena;
    }

    // Verifica si el usuario y la contraseña recibidos coinciden con los míos
    @Override
    public boolean validarCredenciales(String nombreUsuario, String contrasena) {
        return nombreUsuario != null && contrasena != null
                && this.nombreUsuario.equals(nombreUsuario.strip())
                && this.contrasena.equals(contrasena);
    }

    // Muestra los datos del usuario (sin la contraseña)
    @Override
    public String mostrarDatos() {
        return "Usuario: " + nombreUsuario
                + "\nNombre: " + getNombre()
                + "\nApellido: " + getApellido()
                + "\nDNI: " + getDni()
                + "\nEmail: " + getEmail()
                + "\nTeléfono: " + getTelefono();
    }

}
