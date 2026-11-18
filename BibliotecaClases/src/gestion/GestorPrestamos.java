package gestion;

import interfaces.Informable;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import modelo.Libro;
import modelo.Prestamo;
import modelo.Socio;

/**
 * Administra los préstamos y devoluciones, coordinando sus estados con la disponibilidad de los
 * libros.
 */
public class GestorPrestamos implements Informable, Serializable {

    private static final long serialVersionUID = 1L;

    private final ArrayList<Prestamo> prestamos;

    /**
     * Inicializa un registro de préstamos vacío.
     */
    public GestorPrestamos() {
        prestamos = new ArrayList<>();
    }

    /**
     * Obtiene una copia no modificable de la lista de préstamos.
     *
     * @return una lista no modificable con las mismas referencias a los objetos registrados.
     */
    public List<Prestamo> getPrestamos() {
        return List.copyOf(prestamos);
    }

    /**
     * Registra un préstamo y descuenta un ejemplar disponible del libro.
     *
     * @param socio socio utilizado en la operación.
     * @param libro libro utilizado en la operación.
     * @param fecha fecha en la que se inicia el préstamo.
     * @return {@code true} si se registra; {@code false} si el socio está inactivo o no se puede descontar un ejemplar.
     */
    boolean registrarPrestamo(Socio socio, Libro libro, LocalDate fecha) {
        if (socio == null) {
            throw new IllegalArgumentException("Debe indicar un socio.");
        }
        if (libro == null) {
            throw new IllegalArgumentException("Debe indicar un libro.");
        }
        if (fecha == null) {
            throw new IllegalArgumentException("Debe indicar la fecha del préstamo.");
        }
        // Solo permite el préstamo de socios activos y libros con disponibilidad.
        if (!socio.isActivo() || !libro.hayDisponibles()) {
            return false;
        }

        Prestamo prestamo = new Prestamo(socio, libro, fecha);

        // Añade el préstamo al registro únicamente si se pudo reservar un ejemplar.
        if (!libro.descontarEjemplar()) {
            return false;
        }

        prestamos.add(prestamo);
        return true;
    }

    /**
     * Registra la devolución de un préstamo activo y restituye un ejemplar disponible.
     *
     * @param idPrestamo identificador del préstamo.
     * @param fecha fecha en la que se devuelve el libro.
     * @return {@code true} si se registra y se restituye el ejemplar; {@code false} si el préstamo, la fecha o el stock no lo permiten.
     */
    boolean registrarDevolucion(int idPrestamo, LocalDate fecha) {
        if (fecha == null) {
            throw new IllegalArgumentException("Debe indicar la fecha de devolución.");
        }

        Prestamo prestamo = buscarPorId(idPrestamo);

        // Evita registrar devoluciones de préstamos inexistentes o ya cerrados.
        if (prestamo == null || !prestamo.estaActivo()) {
            return false;
        }

        Libro libro = prestamo.getLibro();

        // Comprueba el inventario antes de cerrar el préstamo para no restituir un ejemplar de más.
        if (libro.getEjemplaresDisponibles() >= libro.getCantidadEjemplares()) {
            return false;
        }
        if (!prestamo.registrarDevolucion(fecha)) {
            return false;
        }

        // Restituye la disponibilidad únicamente después de aceptar la fecha de devolución.
        return libro.sumarEjemplar();
    }

    /**
     * Busca un préstamo por su identificador.
     *
     * @param idPrestamo identificador del préstamo.
     * @return el préstamo encontrado o {@code null} si no existe.
     */
    public Prestamo buscarPorId(int idPrestamo) {
        for (Prestamo prestamo : prestamos) {
            if (prestamo.getIdPrestamo() == idPrestamo) {
                return prestamo;
            }
        }

        return null;
    }

    /**
     * Reúne los préstamos que siguen pendientes de devolución.
     *
     * @return una lista no modificable de préstamos activos, vacía si no hay ninguno.
     */
    public List<Prestamo> listarActivos() {
        ArrayList<Prestamo> activos = new ArrayList<>();

        for (Prestamo prestamo : prestamos) {
            if (prestamo.estaActivo()) {
                activos.add(prestamo);
            }
        }

        return List.copyOf(activos);
    }

    /**
     * Reúne todos los préstamos asociados al DNI del socio recibido.
     *
     * @param socio socio utilizado en la operación.
     * @return una lista no modificable con los préstamos del socio, incluidos los devueltos.
     */
    public List<Prestamo> historialSocio(Socio socio) {
        if (socio == null) {
            throw new IllegalArgumentException("Debe indicar un socio.");
        }

        ArrayList<Prestamo> historial = new ArrayList<>();

        // Vincula el historial por DNI e incluye tanto los préstamos activos como los devueltos.
        for (Prestamo prestamo : prestamos) {
            if (prestamo.getSocio().getDni().equals(socio.getDni())) {
                historial.add(prestamo);
            }
        }

        return List.copyOf(historial);
    }

    /**
     * Comprueba si el DNI del socio tiene algún préstamo pendiente.
     *
     * @param socio socio utilizado en la operación.
     * @return {@code true} si tiene algún préstamo activo; {@code false} en otro caso.
     */
    public boolean tienePrestamosPendientes(Socio socio) {
        if (socio == null) {
            throw new IllegalArgumentException("Debe indicar un socio.");
        }

        // La coincidencia por DNI impide que otra instancia del socio eluda el control de pendientes.
        for (Prestamo prestamo : prestamos) {
            if (prestamo.estaActivo() && prestamo.getSocio().getDni().equals(socio.getDni())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Comprueba si el ISBN del libro tiene algún préstamo pendiente.
     *
     * @param libro libro utilizado en la operación.
     * @return {@code true} si tiene algún préstamo activo; {@code false} en otro caso.
     */
    public boolean tienePrestamosActivos(Libro libro) {
        if (libro == null) {
            throw new IllegalArgumentException("Debe indicar un libro.");
        }

        // El ISBN permite reconocer el libro aunque se consulte mediante otra instancia.
        for (Prestamo prestamo : prestamos) {
            if (prestamo.estaActivo() && prestamo.getLibro().getIsbn().equals(libro.getIsbn())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Genera el resumen y el detalle de todos los préstamos registrados.
     *
     * @return el informe con los totales de préstamos, activos y devueltos, y sus datos.
     */
    @Override
    public String generarInforme() {
        int activos = listarActivos().size();
        StringBuilder informe = new StringBuilder("INFORME DE PRÉSTAMOS\n");
        informe.append("Total de préstamos: ").append(prestamos.size()).append("\n");
        informe.append("Activos: ").append(activos).append("\n");
        informe.append("Devueltos: ").append(prestamos.size() - activos).append("\n");

        if (prestamos.isEmpty()) {
            informe.append("\nNo hay préstamos registrados.\n");
        }
        for (Prestamo prestamo : prestamos) {
            informe.append("\n").append(prestamo.mostrarDatos()).append("\n");
        }

        return informe.toString();
    }
}
