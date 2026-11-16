package modelo;

/**
 * Representa a un usuario de la biblioteca como especialización de Persona.
 */
public class Usuario extends Persona implements Autenticable {

    private String nombreUsuario;
    private String contrasena;

    // Constructor: recibe los datos de la persona y los del usuario
    public Usuario(String nombre, String apellido, String dni, String email, String telefono,
            String nombreUsuario, String contrasena) {
        // llamo al constructor de Persona con los datos comunes
        super(nombre, apellido, dni, email, telefono);
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
    }

    // Devuelve el nombre de usuario
    public String getNombreUsuario() {
        return nombreUsuario;
    }

    // Cambia el nombre de usuario
    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    // Devuelve la contraseña
    public String getContrasena() {
        return contrasena;
    }

    // Cambia la contraseña
    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    // Verifica si el usuario y la contraseña recibidos coinciden con los míos
    @Override
    public boolean validarCredenciales(String nombreUsuario, String contrasena) {
        return this.nombreUsuario.equals(nombreUsuario) && this.contrasena.equals(contrasena);
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