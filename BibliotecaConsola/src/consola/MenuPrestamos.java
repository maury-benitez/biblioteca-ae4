package consola;

import gestion.SistemaBiblioteca;
import java.io.UncheckedIOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import modelo.Prestamo;
import modelo.Socio;

/**
 * Presenta las opciones de consola para registrar préstamos, devoluciones y consultar su
 * historial.
 */
public class MenuPrestamos {
    private final SistemaBiblioteca sistema;
    private final EntradaConsola entrada;

    /**
     * Prepara el menú con el sistema y el lector de consola recibidos.
     *
     * @param sistema sistema de biblioteca compartido por los menús.
     * @param entrada lector utilizado para solicitar y validar las respuestas de consola.
     */
    public MenuPrestamos(SistemaBiblioteca sistema, EntradaConsola entrada) {
        if (sistema == null || entrada == null) {
            throw new IllegalArgumentException("El sistema y la entrada son obligatorios.");
        }
        this.sistema = sistema;
        this.entrada = entrada;
    }

    /**
     * Atiende las opciones de préstamos y devoluciones mientras hay sesión, hasta volver o finalizar
     * la entrada.
     */
    public void mostrar() {
        // No permite continuar operando préstamos después del cierre de la sesión.
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

    /**
     * Solicita el socio, el libro y la fecha; registra el préstamo y guarda los datos si tiene éxito.
     */
    private void registrarPrestamo() {
        String dni = entrada.leerCadena("DNI del socio: ");
        String isbn = entrada.leerCadena("ISBN del libro: ");
        LocalDate fecha = LocalDate.parse(entrada.leerCadena("Fecha de préstamo (AAAA-MM-DD): "));
        if (sistema.registrarPrestamo(dni, isbn, fecha)) {
            // Persiste la operación aceptada junto con el nuevo estado de disponibilidad.
            sistema.guardarDatos();
            System.out.println("Préstamo registrado.");
        } else {
            System.out.println("Revise que el socio y el libro estén activos y que haya ejemplares disponibles.");
        }
    }

    /**
     * Solicita el préstamo, la fecha y la confirmación; registra la devolución y guarda los datos si
     * tiene éxito.
     */
    private void registrarDevolucion() {
        int id = entrada.leerEnteroEnRango("Número de préstamo: ", 1, Integer.MAX_VALUE);
        LocalDate fecha = LocalDate.parse(entrada.leerCadena("Fecha de devolución (AAAA-MM-DD): "));
        // Permite cancelar antes de cambiar el estado del préstamo y del ejemplar.
        if (!entrada.confirmar("¿Registrar la devolución?")) {
            System.out.println("Devolución cancelada.");
            return;
        }
        if (sistema.registrarDevolucion(id, fecha)) {
            // Persiste la devolución y la disponibilidad restituida como parte del mismo conjunto de datos.
            sistema.guardarDatos();
            System.out.println("Devolución registrada.");
        } else {
            System.out.println("Revise el número, el estado del préstamo y la fecha de devolución.");
        }
    }

    /**
     * Imprime la cantidad y los datos de los préstamos pendientes de devolución.
     */
    private void listarActivos() {
        List<Prestamo> prestamos = sistema.getGestorPrestamos().listarActivos();
        System.out.println("Préstamos activos: " + prestamos.size());
        mostrarPrestamos(prestamos);
    }

    /**
     * Busca un socio por DNI, incluido si está inactivo, e imprime su historial de préstamos.
     */
    private void mostrarHistorialSocio() {
        String dni = entrada.leerCadena("DNI del socio: ");
        Socio encontrado = null;
        // Incluye socios inactivos para que la baja lógica no oculte su historial.
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

    /**
     * Imprime el informe de préstamos generado por el gestor.
     */
    private void mostrarInforme() {
        System.out.println(sistema.getGestorPrestamos().generarInforme());
    }

    /**
     * Imprime los datos de los préstamos recibidos o un aviso si la lista está vacía.
     *
     * @param prestamos lista de préstamos cuyos datos se mostrarán.
     */
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
