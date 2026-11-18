package modelo;

/**
 * Representa a una persona con credenciales para acceder al sistema de biblioteca.
 */
public class Usuario extends Persona implements Autenticable {

    private static final long serialVersionUID = 1L;

    private String nombreUsuario;
    private String contrasena;

    /**
     * Crea un usuario con sus datos personales y sus credenciales de acceso.
     *
     * @param nombre nombre que se asigna al objeto.
     * @param apellido apellido de la persona.
     * @param dni DNI que identifica a la persona.
     * @param email correo electrónico de la persona.
     * @param telefono número de teléfono de la persona.
     * @param nombreUsuario nombre de usuario para el acceso al sistema.
     * @param contrasena contraseña utilizada en la operación.
     */
    public Usuario(String nombre, String apellido, String dni, String email, String telefono,
            String nombreUsuario, String contrasena) {

        super(nombre, apellido, dni, email, telefono);
        // Evita crear un objeto que no cumpla sus datos de identificación obligatorios.
        if (nombreUsuario == null || nombreUsuario.isBlank()) {
            throw new IllegalArgumentException("El nombre de usuario no puede estar en blanco.");
        }
        if (contrasena == null || contrasena.isBlank()) {
            throw new IllegalArgumentException("La contraseña no puede estar en blanco.");
        }

        this.nombreUsuario = nombreUsuario.strip();
        // Conserva la contraseña tal como se recibió para que los espacios formen parte de la credencial.
        this.contrasena = contrasena;
    }

    /** Devuelve el nombre de usuario. */
    public String getNombreUsuario() {
        return nombreUsuario;
    }

    /**
     * Actualiza el nombre de usuario después de validar el valor recibido.
     *
     * @param nombreUsuario nombre de usuario para el acceso al sistema.
     */
    public void setNombreUsuario(String nombreUsuario) {
        if (nombreUsuario == null || nombreUsuario.isBlank()) {
            throw new IllegalArgumentException("El nombre de usuario no puede estar en blanco.");
        }

        this.nombreUsuario = nombreUsuario.strip();
    }

    /** Devuelve la contraseña del usuario. */
    public String getContrasena() {
        return contrasena;
    }

    /**
     * Actualiza la contraseña después de validar el valor recibido.
     *
     * @param contrasena contraseña utilizada en la operación.
     */
    public void setContrasena(String contrasena) {
        if (contrasena == null || contrasena.isBlank()) {
            throw new IllegalArgumentException("La contraseña no puede estar en blanco.");
        }

        this.contrasena = contrasena;
    }

    /**
     * Comprueba si el nombre de usuario y la contraseña corresponden al usuario.
     *
     * @param nombreUsuario nombre de usuario para el acceso al sistema.
     * @param contrasena contraseña utilizada en la operación.
     * @return {@code true} si ambas credenciales coinciden; {@code false} en otro caso.
     */
    @Override
    public boolean validarCredenciales(String nombreUsuario, String contrasena) {
        return nombreUsuario != null && contrasena != null
                && this.nombreUsuario.equals(nombreUsuario.strip())
                && this.contrasena.equals(contrasena);
    }

    /**
     * Construye la representación textual de los datos del usuario, sin incluir la contraseña.
     *
     * @return el texto con los datos del objeto.
     */
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
