package modelo;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Representa el préstamo de un libro a un socio y registra su eventual devolución.
 */
public class Prestamo implements Serializable {

    private static final long serialVersionUID = 1L;

    private static int contadorPrestamos;

    private final int idPrestamo;
    private final Socio socio;
    private final Libro libro;
    private final LocalDate fechaPrestamo;
    private LocalDate fechaDevolucion;

    /**
     * Crea un préstamo pendiente de devolución y le asigna el siguiente identificador.
     *
     * @param socio socio utilizado en la operación.
     * @param libro libro utilizado en la operación.
     * @param fechaPrestamo fecha en la que se inicia el préstamo.
     */
    public Prestamo(Socio socio, Libro libro, LocalDate fechaPrestamo) {
        // Exige las referencias y la fecha necesarias para que el préstamo sea válido.
        if (socio == null) {
            throw new IllegalArgumentException("Debe indicar un socio.");
        }
        if (libro == null) {
            throw new IllegalArgumentException("Debe indicar un libro.");
        }
        if (fechaPrestamo == null) {
            throw new IllegalArgumentException("Debe indicar la fecha del préstamo.");
        }
        // Impide que el desbordamiento del contador genere identificadores negativos.
        if (contadorPrestamos == Integer.MAX_VALUE) {
            throw new IllegalStateException("No se pueden generar más identificadores de préstamos.");
        }

        this.idPrestamo = ++contadorPrestamos;
        this.socio = socio;
        this.libro = libro;
        this.fechaPrestamo = fechaPrestamo;
    }

    /** Devuelve el último identificador alcanzado por el contador de préstamos. */
    public static int getContadorPrestamos() {
        return contadorPrestamos;
    }

    /**
     * Ajusta el contador de préstamos sin reducir el último identificador alcanzado.
     *
     * @param ultimoId mayor identificador de préstamo que se desea conservar.
     */
    public static void actualizarContadorPrestamos(int ultimoId) {
        if (ultimoId < 0) {
            throw new IllegalArgumentException("El identificador no puede ser negativo.");
        }

        // Mantiene la secuencia de identificadores al recuperar préstamos guardados.
        contadorPrestamos = Math.max(contadorPrestamos, ultimoId);
    }

    /** Devuelve el identificador del préstamo. */
    public int getIdPrestamo() {
        return idPrestamo;
    }

    /** Devuelve el socio del préstamo. */
    public Socio getSocio() {
        return socio;
    }

    /** Devuelve el libro del préstamo. */
    public Libro getLibro() {
        return libro;
    }

    /** Devuelve la fecha del préstamo. */
    public LocalDate getFechaPrestamo() {
        return fechaPrestamo;
    }

    /** Devuelve la fecha de devolución o {@code null} si sigue pendiente. */
    public LocalDate getFechaDevolucion() {
        return fechaDevolucion;
    }

    /** Indica si el préstamo tiene una fecha de devolución registrada. */
    public boolean isDevuelto() {
        return fechaDevolucion != null;
    }

    /**
     * Registra la fecha de devolución si el préstamo sigue pendiente y la fecha es admisible.
     *
     * @param fechaDevolucion fecha en la que se devuelve el libro.
     * @return {@code true} si se registra la devolución; {@code false} si ya se devolvió o la fecha es anterior al préstamo.
     */
    public boolean registrarDevolucion(LocalDate fechaDevolucion) {
        if (fechaDevolucion == null) {
            throw new IllegalArgumentException("Debe indicar la fecha de devolución.");
        }
        // Evita devolver dos veces el mismo préstamo o cerrar el préstamo antes de su inicio.
        if (isDevuelto() || fechaDevolucion.isBefore(fechaPrestamo)) {
            return false;
        }

        this.fechaDevolucion = fechaDevolucion;
        return true;
    }

    /**
     * Comprueba si el préstamo sigue pendiente de devolución.
     *
     * @return {@code true} si no tiene fecha de devolución; {@code false} si ya fue devuelto.
     */
    public boolean estaActivo() {
        return !isDevuelto();
    }

    /**
     * Construye la representación textual de los datos del préstamo.
     *
     * @return el texto con los datos del objeto.
     */
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
