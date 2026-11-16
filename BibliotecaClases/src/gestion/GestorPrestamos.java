package gestion;

import interfaces.Informable;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import modelo.Libro;
import modelo.Prestamo;
import modelo.Socio;

public class GestorPrestamos implements Informable, Serializable {

    private static final long serialVersionUID = 1L;

    private final ArrayList<Prestamo> prestamos;

    public GestorPrestamos() {
        prestamos = new ArrayList<>();
    }

    public List<Prestamo> getPrestamos() {
        return List.copyOf(prestamos);
    }

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
        if (!libro.hayDisponibles()) {
            return false;
        }

        Prestamo prestamo = new Prestamo(socio, libro, fecha);

        if (!libro.descontarEjemplar()) {
            return false;
        }

        prestamos.add(prestamo);
        return true;
    }

    boolean registrarDevolucion(int idPrestamo, LocalDate fecha) {
        if (fecha == null) {
            throw new IllegalArgumentException("Debe indicar la fecha de devolución.");
        }

        Prestamo prestamo = buscarPorId(idPrestamo);

        if (prestamo == null || !prestamo.estaActivo()) {
            return false;
        }

        Libro libro = prestamo.getLibro();

        if (libro.getEjemplaresDisponibles() >= libro.getCantidadEjemplares()) {
            return false;
        }
        if (!prestamo.registrarDevolucion(fecha)) {
            return false;
        }

        return libro.sumarEjemplar();
    }

    public Prestamo buscarPorId(int idPrestamo) {
        for (Prestamo prestamo : prestamos) {
            if (prestamo.getIdPrestamo() == idPrestamo) {
                return prestamo;
            }
        }

        return null;
    }

    public List<Prestamo> listarActivos() {
        ArrayList<Prestamo> activos = new ArrayList<>();

        for (Prestamo prestamo : prestamos) {
            if (prestamo.estaActivo()) {
                activos.add(prestamo);
            }
        }

        return List.copyOf(activos);
    }

    public List<Prestamo> historialSocio(Socio socio) {
        if (socio == null) {
            throw new IllegalArgumentException("Debe indicar un socio.");
        }

        ArrayList<Prestamo> historial = new ArrayList<>();

        for (Prestamo prestamo : prestamos) {
            if (prestamo.getSocio().getDni().equals(socio.getDni())) {
                historial.add(prestamo);
            }
        }

        return List.copyOf(historial);
    }

    public boolean tienePrestamosPendientes(Socio socio) {
        if (socio == null) {
            throw new IllegalArgumentException("Debe indicar un socio.");
        }

        for (Prestamo prestamo : prestamos) {
            if (prestamo.getSocio().getDni().equals(socio.getDni())) {
                return true;
            }
        }

        return false;
    }

    public boolean tienePrestamosActivos(Libro libro) {
        if (libro == null) {
            throw new IllegalArgumentException("Debe indicar un libro.");
        }

        for (Prestamo prestamo : prestamos) {
            if (prestamo.estaActivo() && prestamo.getLibro().getIsbn().equals(libro.getIsbn())) {
                return true;
            }
        }

        return false;
    }

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
