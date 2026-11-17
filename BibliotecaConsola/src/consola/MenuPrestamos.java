package consola;

import gestion.SistemaBiblioteca;
import java.io.UncheckedIOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import modelo.Prestamo;
import modelo.Socio;

public class MenuPrestamos {
    private final SistemaBiblioteca sistema;
    private final EntradaConsola entrada;

    public MenuPrestamos(SistemaBiblioteca sistema, EntradaConsola entrada) {
        if (sistema == null || entrada == null) {
            throw new IllegalArgumentException("El sistema y la entrada son obligatorios.");
        }
        this.sistema = sistema;
        this.entrada = entrada;
    }

    public void mostrar() {
        while (sistema.haySesionActiva()) {
            System.out.println("\nPRÉSTAMOS Y DEVOLUCIONES");
            System.out.println("1. Registrar préstamo");
            System.out.println("2. Registrar devolución");
            System.out.println("3. Listar préstamos activos");
            System.out.println("4. Historial de un socio");
            System.out.println("5. Informe de préstamos");
            System.out.println("0. Volver");
            try {
                int opcion = entrada.leerEnteroEnRango("Seleccione una opción: ", 0, 5);
                switch (opcion) {
                    case 1: registrarPrestamo(); break;
                    case 2: registrarDevolucion(); break;
                    case 3: listarActivos(); break;
                    case 4: mostrarHistorialSocio(); break;
                    case 5: mostrarInforme(); break;
                    case 0: return;
                    default: break;
                }
            } catch (DateTimeParseException ex) {
                System.out.println("La fecha debe ser válida y tener el formato AAAA-MM-DD.");
            } catch (IllegalArgumentException | UncheckedIOException ex) {
                System.out.println("Error: " + ex.getMessage());
            } catch (IllegalStateException ex) {
                System.out.println("No se pudo continuar: " + ex.getMessage());
                return;
            }
        }
    }

    private void registrarPrestamo() {
        String dni = entrada.leerCadena("DNI del socio: ");
        String isbn = entrada.leerCadena("ISBN del libro: ");
        LocalDate fecha = LocalDate.parse(entrada.leerCadena("Fecha de préstamo (AAAA-MM-DD): "));
        if (sistema.registrarPrestamo(dni, isbn, fecha)) {
            sistema.guardarDatos();
            System.out.println("Préstamo registrado.");
        } else {
            System.out.println("Revise que el socio y el libro estén activos y que haya ejemplares disponibles.");
        }
    }

    private void registrarDevolucion() {
        int id = entrada.leerEnteroEnRango("Número de préstamo: ", 1, Integer.MAX_VALUE);
        LocalDate fecha = LocalDate.parse(entrada.leerCadena("Fecha de devolución (AAAA-MM-DD): "));
        if (!entrada.confirmar("¿Registrar la devolución?")) {
            System.out.println("Devolución cancelada.");
            return;
        }
        if (sistema.registrarDevolucion(id, fecha)) {
            sistema.guardarDatos();
            System.out.println("Devolución registrada.");
        } else {
            System.out.println("Revise el número, el estado del préstamo y la fecha de devolución.");
        }
    }

    private void listarActivos() {
        List<Prestamo> prestamos = sistema.getGestorPrestamos().listarActivos();
        System.out.println("Préstamos activos: " + prestamos.size());
        mostrarPrestamos(prestamos);
    }

    private void mostrarHistorialSocio() {
        String dni = entrada.leerCadena("DNI del socio: ");
        Socio encontrado = null;
        for (Socio socio : sistema.getGestorSocios().getSocios()) {
            if (socio.getDni().equals(dni)) {
                encontrado = socio;
                break;
            }
        }
        if (encontrado == null) {
            System.out.println("No existe un socio con ese DNI.");
            return;
        }
        mostrarPrestamos(sistema.getGestorPrestamos().historialSocio(encontrado));
    }

    private void mostrarInforme() {
        System.out.println(sistema.getGestorPrestamos().generarInforme());
    }

    private void mostrarPrestamos(List<Prestamo> prestamos) {
        if (prestamos.isEmpty()) {
            System.out.println("No hay préstamos para mostrar.");
        }
        for (Prestamo prestamo : prestamos) {
            System.out.println(prestamo.mostrarDatos());
            System.out.println();
        }
    }
}
