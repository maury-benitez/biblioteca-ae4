package modelo;

import java.io.Serializable;

/**
 * Reúne los datos personales y la identidad por DNI que comparten usuarios y socios.
 */
public abstract class Persona implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String dni;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;

    /**
     * Inicializa los datos personales obligatorios y el DNI que identifica a la persona.
     *
     * @param nombre nombre que se asigna al objeto.
     * @param apellido apellido de la persona.
     * @param dni DNI que identifica a la persona.
     * @param email correo electrónico de la persona.
     * @param telefono número de teléfono de la persona.
     */
    public Persona(String nombre, String apellido, String dni, String email, String telefono) {
        // Evita crear un objeto que no cumpla sus datos de identificación obligatorios.
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar en blanco.");
        }
        if (apellido == null || apellido.isBlank()) {
            throw new IllegalArgumentException("El apellido no puede estar en blanco.");
        }
        if (dni == null || dni.isBlank()) {
            throw new IllegalArgumentException("El DNI no puede estar en blanco.");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("El email no puede estar en blanco.");
        }
        if (telefono == null || telefono.isBlank()) {
            throw new IllegalArgumentException("El teléfono no puede estar en blanco.");
        }

        this.nombre = nombre.strip();
        this.apellido = apellido.strip();
        // La identidad queda fijada al construir la persona y no cambia con sus datos de contacto.
        this.dni = dni.strip();
        this.email = email.strip();
        this.telefono = telefono.strip();
    }

    /** Devuelve el nombre de la persona. */
    public String getNombre() {
        return nombre;
    }

    /**
     * Actualiza el nombre después de validar el valor recibido.
     *
     * @param nombre nombre que se asigna al objeto.
     */
    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar en blanco.");
        }

        this.nombre = nombre.strip();
    }

    /** Devuelve el apellido de la persona. */
    public String getApellido() {
        return apellido;
    }

    /**
     * Actualiza el apellido después de validar el valor recibido.
     *
     * @param apellido apellido de la persona.
     */
    public void setApellido(String apellido) {
        if (apellido == null || apellido.isBlank()) {
            throw new IllegalArgumentException("El apellido no puede estar en blanco.");
        }

        this.apellido = apellido.strip();
    }

    /** Devuelve el DNI de la persona. */
    public String getDni() {
        return dni;
    }

    /** Devuelve el correo electrónico de la persona. */
    public String getEmail() {
        return email;
    }

    /**
     * Actualiza el correo electrónico después de validar el valor recibido.
     *
     * @param email correo electrónico de la persona.
     */
    public void setEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("El email no puede estar en blanco.");
        }

        this.email = email.strip();
    }

    /** Devuelve el teléfono de la persona. */
    public String getTelefono() {
        return telefono;
    }

    /**
     * Actualiza el teléfono después de validar el valor recibido.
     *
     * @param telefono número de teléfono de la persona.
     */
    public void setTelefono(String telefono) {
        if (telefono == null || telefono.isBlank()) {
            throw new IllegalArgumentException("El teléfono no puede estar en blanco.");
        }

        this.telefono = telefono.strip();
    }

    /**
     * Compara la identidad de dos personas mediante su DNI.
     *
     * @param obj objeto cuya identidad se compara con la de esta persona.
     * @return {@code true} si el objeto recibido es una persona con el mismo DNI; {@code false} en otro caso.
     */
    @Override
    public boolean equals(Object obj) {
        boolean iguales = false;

        // Solo compara DNI entre personas para mantener un criterio de identidad común a las subclases.
        if (obj instanceof Persona) {
            Persona otra = (Persona) obj;
            iguales = this.dni.equals(otra.dni);
        }

        return iguales;
    }

    /**
     * Calcula el código de dispersión a partir del DNI de la persona.
     *
     * @return el código de dispersión del DNI.
     */
    @Override
    public int hashCode() {
        return dni.hashCode();
    }

    /**
     * Define la representación textual de los datos que debe aportar cada tipo de persona.
     *
     * @return el texto con los datos del objeto.
     */
    public abstract String mostrarDatos();

}
