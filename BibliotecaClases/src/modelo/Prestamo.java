package modelo;

import java.io.Serializable;
import java.time.LocalDate;

public class Prestamo implements Serializable {

    private static final long serialVersionUID = 1L;

    private static int contadorPrestamos;

    private final int idPrestamo;
    private final Socio socio;
    private final Libro libro;
    private final LocalDate fechaPrestamo;
    private LocalDate fechaDevolucion;

    public Prestamo(Socio socio, Libro libro, LocalDate fechaPrestamo) {
        if (socio == null) {
            throw new IllegalArgumentException("Debe indicar un socio.");
        }
        if (libro == null) {
            throw new IllegalArgumentException("Debe indicar un libro.");
        }
        if (fechaPrestamo == null) {
            throw new IllegalArgumentException("Debe indicar la fecha del préstamo.");
        }
        if (contadorPrestamos == Integer.MAX_VALUE) {
            throw new IllegalStateException("No se pueden generar más identificadores de préstamos.");
        }

        this.idPrestamo = ++contadorPrestamos;
        this.socio = socio;
        this.libro = libro;
        this.fechaPrestamo = fechaPrestamo;
    }

    public static int getContadorPrestamos() {
        return contadorPrestamos;
    }

    public static void actualizarContadorPrestamos(int ultimoId) {
        if (ultimoId < 0) {
            throw new IllegalArgumentException("El identificador no puede ser negativo.");
        }

        contadorPrestamos = Math.max(contadorPrestamos, ultimoId);
    }

    public int getIdPrestamo() {
        return idPrestamo;
    }

    public Socio getSocio() {
        return socio;
    }

    public Libro getLibro() {
        return libro;
    }

    public LocalDate getFechaPrestamo() {
        return fechaPrestamo;
    }

    public LocalDate getFechaDevolucion() {
        return fechaDevolucion;
    }

    public boolean isDevuelto() {
        return fechaDevolucion != null;
    }

    public boolean registrarDevolucion(LocalDate fechaDevolucion) {
        if (fechaDevolucion == null) {
            throw new IllegalArgumentException("Debe indicar la fecha de devolución.");
        }
        if (isDevuelto() || fechaDevolucion.isBefore(fechaPrestamo)) {
            return false;
        }

        this.fechaDevolucion = fechaDevolucion;
        return true;
    }

    public boolean estaActivo() {
        return !isDevuelto();
    }

    public String mostrarDatos() {
        return "Préstamo: " + idPrestamo
                + "\nSocio: " + socio.getNroSocio() + " - " + socio.getNombre() + " " + socio.getApellido()
                + "\nDNI: " + socio.getDni()
                + "\nLibro: " + libro.getTitulo()
                + "\nISBN: " + libro.getIsbn()
                + "\nFecha de préstamo: " + fechaPrestamo
                + "\nFecha de devolución: " + (isDevuelto() ? fechaDevolucion : "Pendiente")
                + "\nEstado: " + (estaActivo() ? "Activo" : "Devuelto");
    }
}
