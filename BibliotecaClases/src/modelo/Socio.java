package modelo;

public class Socio extends Persona {

    private static final long serialVersionUID = 1L;

    private final int nroSocio;
    private boolean activo;

    public Socio(String nombre, String apellido, String dni, String email, String telefono, int nroSocio) {
        super(nombre, apellido, dni, email, telefono);

        if (nroSocio <= 0) {
            throw new IllegalArgumentException("El número de socio debe ser positivo.");
        }

        this.nroSocio = nroSocio;
        this.activo = true;
    }

    public int getNroSocio() {
        return nroSocio;
    }

    public boolean isActivo() {
        return activo;
    }

    public void desactivar() {
        activo = false;
    }

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
