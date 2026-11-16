package modelo;

import java.io.Serializable;

/*
 * Clase abstracta Persona: tiene los datos comunes de un Usuario y un Socio.
 * Es abstracta porque no se crean personas solas.
 */
public abstract class Persona implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String dni;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;

    // Constructor: recibe los cinco datos de la persona
    public Persona(String nombre, String apellido, String dni, String email, String telefono) {
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
        this.dni = dni.strip(); // es final, se asigna una sola vez
        this.email = email.strip();
        this.telefono = telefono.strip();
    }

    // Devuelve el nombre
    public String getNombre() {
        return nombre;
    }

    // Cambia el nombre
    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar en blanco.");
        }

        this.nombre = nombre.strip();
    }

    // Devuelve el apellido
    public String getApellido() {
        return apellido;
    }

    // Cambia el apellido
    public void setApellido(String apellido) {
        if (apellido == null || apellido.isBlank()) {
            throw new IllegalArgumentException("El apellido no puede estar en blanco.");
        }

        this.apellido = apellido.strip();
    }

    // Devuelve el DNI (no tiene setter porque no debe cambiar)
    public String getDni() {
        return dni;
    }

    // Devuelve el email
    public String getEmail() {
        return email;
    }

    // Cambia el email
    public void setEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("El email no puede estar en blanco.");
        }

        this.email = email.strip();
    }

    // Devuelve el teléfono
    public String getTelefono() {
        return telefono;
    }

    // Cambia el teléfono
    public void setTelefono(String telefono) {
        if (telefono == null || telefono.isBlank()) {
            throw new IllegalArgumentException("El teléfono no puede estar en blanco.");
        }

        this.telefono = telefono.strip();
    }

    // Dos personas son iguales si tienen el mismo DNI
    @Override
    public boolean equals(Object obj) {
        boolean iguales = false;

        // verifico que el otro objeto sea una persona
        if (obj instanceof Persona) {
            Persona otra = (Persona) obj;
            iguales = this.dni.equals(otra.dni);
        }

        return iguales;
    }

    // Devuelve el hash calculado con el DNI
    @Override
    public int hashCode() {
        return dni.hashCode();
    }

    // Método abstracto: cada subclase muestra sus datos a su manera
    public abstract String mostrarDatos();

}
