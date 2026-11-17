package consola;

import gestion.GestorCategorias;
import modelo.Categoria;

/**
 * Menú de consola para administrar las categorías: alta, baja,
 * modificación y listado con la cantidad de libros (RF5).
 */
public class MenuCategorias {

    private final GestorCategorias gestorCategorias;
    private final EntradaConsola entrada;

    public MenuCategorias(GestorCategorias gestorCategorias, EntradaConsola entrada) {
        this.gestorCategorias = gestorCategorias;
        this.entrada = entrada;
    }

    // Muestra el menú y repite hasta que el usuario elige volver
    public void mostrar() {
        int opcion;

        do {
            System.out.println("\n--- CATEGORÍAS ---");
            System.out.println("1. Alta de categoría");
            System.out.println("2. Baja de categoría");
            System.out.println("3. Modificar categoría");
            System.out.println("4. Listar categorías");
            System.out.println("0. Volver");
            opcion = entrada.leerEnteroEnRango("Opción: ", 0, 4);

            switch (opcion) {
                case 1:
                    altaCategoria();
                    break;
                case 2:
                    bajaCategoria();
                    break;
                case 3:
                    modificarCategoria();
                    break;
                case 4:
                    listarCategorias();
                    break;
            }
        } while (opcion != 0);
    }

    // Pide los datos y da de alta una categoría con el siguiente ID libre
    private void altaCategoria() {
        System.out.println("\nAlta de categoría");
        String nombre = entrada.leerCadena("Nombre: ");
        String descripcion = entrada.leerCadena("Descripción: ");
        int id = siguienteId();

        try {
            Categoria categoria = new Categoria(id, nombre, descripcion);

            if (gestorCategorias.altaCategoria(categoria)) {
                System.out.println("Categoría registrada con el ID " + id + ".");
            } else {
                System.out.println("Ya existe una categoría con ese nombre.");
            }
        } catch (IllegalArgumentException ex) {
            // el constructor rechazó algún dato, muestro el motivo
            System.out.println(ex.getMessage());
        }
    }

    // Pide el ID y elimina la categoría si no tiene libros asociados
    private void bajaCategoria() {
        System.out.println("\nBaja de categoría");
        int id = entrada.leerEntero("ID de la categoría: ");
        Categoria categoria = gestorCategorias.buscarPorId(id);

        if (categoria == null) {
            System.out.println("No existe una categoría con ese ID.");
            return;
        }

        System.out.println(categoria.mostrarDatos());

        if (!entrada.confirmar("¿Confirma la baja?")) {
            System.out.println("Operación cancelada.");
            return;
        }

        if (gestorCategorias.bajaCategoria(id)) {
            System.out.println("Categoría eliminada.");
        } else {
            System.out.println("No se puede eliminar: la categoría tiene libros asociados.");
        }
    }

    // Pide el ID y permite cambiar el nombre y la descripción
    private void modificarCategoria() {
        System.out.println("\nModificar categoría");
        int id = entrada.leerEntero("ID de la categoría: ");
        Categoria categoria = gestorCategorias.buscarPorId(id);

        if (categoria == null) {
            System.out.println("No existe una categoría con ese ID.");
            return;
        }

        System.out.println(categoria.mostrarDatos());

        // arranco con los datos actuales y cambio solo los que el usuario elija
        String nombre = categoria.getNombre();
        String descripcion = categoria.getDescripcion();

        if (entrada.confirmar("¿Modificar el nombre?")) {
            nombre = entrada.leerCadena("Nuevo nombre: ");
        }
        if (entrada.confirmar("¿Modificar la descripción?")) {
            descripcion = entrada.leerCadena("Nueva descripción: ");
        }

        try {
            Categoria modificada = new Categoria(id, nombre, descripcion);

            if (gestorCategorias.modificarCategoria(modificada)) {
                System.out.println("Categoría modificada.");
            } else {
                System.out.println("No se pudo modificar: ya existe otra categoría con ese nombre.");
            }
        } catch (IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
        }
    }

    // Muestra todas las categorías con la cantidad de libros de cada una
    private void listarCategorias() {
        System.out.println();
        System.out.println(gestorCategorias.generarInforme());
    }

    // Calcula el próximo ID: el mayor que haya más uno
    private int siguienteId() {
        int mayor = 0;

        for (Categoria categoria : gestorCategorias.getCategorias()) {
            if (categoria.getIdCategoria() > mayor) {
                mayor = categoria.getIdCategoria();
            }
        }

        return mayor + 1;
    }

}