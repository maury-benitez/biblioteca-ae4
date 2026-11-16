package modelo;

/*
 * Clase abstracta Persona: tiene los datos comunes de un Usuario y un Socio.
 * Es abstracta porque no se crean personas solas.
 */
public abstract class Persona {

    private final String dni;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;

    // Constructor: recibe los cinco datos de la persona
    public Persona(String nombre, String apellido, String dni, String email, String telefono) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni; // es final, se asigna una sola vez
        this.email = email;
        this.telefono = telefono;
    }

    // Devuelve el nombre
    public String getNombre() {
        return nombre;
    }

    // Cambia el nombre
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // Devuelve el apellido
    public String getApellido() {
        return apellido;
    }

    // Cambia el apellido
    public void setApellido(String apellido) {
        this.apellido = apellido;
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
        this.email = email;
    }

    // Devuelve el teléfono
    public String getTelefono() {
        return telefono;
    }

    // Cambia el teléfono
    public void setTelefono(String telefono) {
        this.telefono = telefono;
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