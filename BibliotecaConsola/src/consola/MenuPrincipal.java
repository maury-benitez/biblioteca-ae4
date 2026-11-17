package consola;

import gestion.SistemaBiblioteca;
import java.io.UncheckedIOException;

public class MenuPrincipal {
    private final SistemaBiblioteca sistema;
    private final EntradaConsola entrada;
    private final MenuUsuarios menuUsuarios;
    private final MenuSocios menuSocios;
    private final MenuLibros menuLibros;
    private final MenuCategorias menuCategorias;
    private final MenuPrestamos menuPrestamos;

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

    public void mostrar() {
        while (true) {
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
                        return;
                    case 0:
                        break;
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
