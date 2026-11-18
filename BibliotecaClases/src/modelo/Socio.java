package modelo;

/**
 * Representa a un socio de la biblioteca, con su número de identificación y estado de alta.
 */
public class Socio extends Persona {

    private static final long serialVersionUID = 1L;

    private final int nroSocio;
    private boolean activo;

    /**
     * Crea un socio activo con sus datos personales y un número de socio positivo.
     *
     * @param nombre nombre que se asigna al objeto.
     * @param apellido apellido de la persona.
     * @param dni DNI que identifica a la persona.
     * @param email correo electrónico de la persona.
     * @param telefono número de teléfono de la persona.
     * @param nroSocio número que identifica al socio.
     */
    public Socio(String nombre, String apellido, String dni, String email, String telefono, int nroSocio) {
        super(nombre, apellido, dni, email, telefono);

        // Evita crear un objeto que no cumpla sus datos de identificación obligatorios.
        if (nroSocio <= 0) {
            throw new IllegalArgumentException("El número de socio debe ser positivo.");
        }

        this.nroSocio = nroSocio;
        this.activo = true;
    }

    /** Devuelve el número del socio. */
    public int getNroSocio() {
        return nroSocio;
    }

    /** Indica si el socio está activo. */
    public boolean isActivo() {
        return activo;
    }

    /**
     * Da de baja lógicamente al socio conservando sus datos en el registro.
     */
    public void desactivar() {
        activo = false;
    }

    /**
     * Construye la representación textual de los datos del socio.
     *
     * @return el texto con los datos del objeto.
     */
    @Override
    public String mostrarDatos() {
        return "Socio: " + nroSocio
                + "\nNombre: " + getNombre()
                + "\nApellido: " + getApellido()
                + "\nDNI: " + getDni()
                + "\nEmail: " + getEmail()
                + "\nTeléfono: " + getTelefono()
                + "\nEstado: " + (activo ? "Activo" : "Inactivo");
    }
}
