package consola;

import gestion.SistemaBiblioteca;
import java.io.UncheckedIOException;

/**
 * Coordina el acceso autenticado a los menús de gestión de la biblioteca.
 */
public class MenuPrincipal {
    private final SistemaBiblioteca sistema;
    private final EntradaConsola entrada;
    private final MenuUsuarios menuUsuarios;
    private final MenuSocios menuSocios;
    private final MenuLibros menuLibros;
    private final MenuCategorias menuCategorias;
    private final MenuPrestamos menuPrestamos;

    /**
     * Prepara el menú principal y sus submenús con el mismo sistema y lector de consola.
     *
     * @param sistema sistema de biblioteca compartido por los menús.
     * @param entrada lector utilizado para solicitar y validar las respuestas de consola.
     */
    public MenuPrincipal(SistemaBiblioteca sistema, EntradaConsola entrada) {
        if (sistema == null || entrada == null) {
            throw new IllegalArgumentException("El sistema y la entrada son obligatorios.");
        }
        this.sistema = sistema;
        this.entrada = entrada;
        menuUsuarios = new MenuUsuarios(sistema, entrada);
        menuSocios = new MenuSocios(sistema, entrada);
        menuLibros = new MenuLibros(sistema, entrada);
        menuCategorias = new MenuCategorias(sistema.getGestorCategorias(), entrada);
        menuPrestamos = new MenuPrestamos(sistema, entrada);
    }

    /**
     * Solicita el acceso cuando corresponde y coordina la navegación hasta salir o finalizar la
     * entrada.
     */
    public void mostrar() {
        while (true) {
            // Requiere autenticación al entrar y vuelve a solicitarla después de cerrar sesión.
            if (!sistema.haySesionActiva() && !menuUsuarios.mostrarLogueo()) {
                return;
            }
            mostrarOpciones();
            try {
                int opcion = entrada.leerEnteroEnRango("Seleccione una opción: ", 0, 5);
                switch (opcion) {
                    case 1:
                        menuLibros.mostrar();
                        break;
                    case 2:
                        menuSocios.mostrar();
                        break;
                    case 3:
                        menuCategorias.mostrar();
                        break;
                    case 4:
                        menuPrestamos.mostrar();
                        break;
                    case 5:
                        sistema.cerrarSesion();
                        System.out.println("Sesión cerrada.");
                        break;
                    case 0:
                        return;
                    default:
                        break;
                }
            } catch (IllegalArgumentException | UncheckedIOException ex) {
                System.out.println("Error: " + ex.getMessage());
            } catch (IllegalStateException ex) {
                System.out.println("No se pudo continuar: " + ex.getMessage());
                return;
            }
        }
    }

    /**
     * Imprime las opciones disponibles en el menú principal.
     */
    private void mostrarOpciones() {
        System.out.println("\nSISTEMA DE BIBLIOTECA");
        System.out.println("1. Libros");
        System.out.println("2. Socios");
        System.out.println("3. Categorías");
        System.out.println("4. Préstamos y devoluciones");
        System.out.println("5. Cerrar sesión");
        System.out.println("0. Salir");
    }
}
