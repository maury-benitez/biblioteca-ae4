package consola;

import gestion.GestorCategorias;
import modelo.Categoria;

/**
 * Presenta las opciones de consola para administrar y listar las categorías de libros.
 */
public class MenuCategorias {

    private final GestorCategorias gestorCategorias;
    private final EntradaConsola entrada;

    /**
     * Prepara el menú con el gestor de categorías y el lector de consola recibidos.
     *
     * @param gestorCategorias gestor que administra las categorías del menú.
     * @param entrada lector utilizado para solicitar y validar las respuestas de consola.
     */
    public MenuCategorias(GestorCategorias gestorCategorias, EntradaConsola entrada) {
        this.gestorCategorias = gestorCategorias;
        this.entrada = entrada;
    }

    /**
     * Presenta las opciones de categorías y atiende la selección hasta que se elige volver.
     */
    public void mostrar() {
        int opcion;

        // Mantiene el menú disponible después de cada operación hasta que se elige volver.
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

    /**
     * Solicita los datos para registrar una categoría y comunica el resultado.
     */
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

            System.out.println(ex.getMessage());
        }
    }

    /**
     * Busca una categoría, comprueba las condiciones de baja y solicita confirmación antes de
     * realizarla.
     */
    private void bajaCategoria() {
        System.out.println("\nBaja de categoría");
        int id = entrada.leerEntero("ID de la categoría: ");
        Categoria categoria = gestorCategorias.buscarPorId(id);

        if (categoria == null) {
            System.out.println("No existe una categoría con ese ID.");
            return;
        }

        System.out.println(categoria.mostrarDatos());

        // Solicita una confirmación explícita antes de aplicar la baja.
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

    /**
     * Solicita los cambios elegidos para una categoría y comunica el resultado de la modificación.
     */
    private void modificarCategoria() {
        System.out.println("\nModificar categoría");
        int id = entrada.leerEntero("ID de la categoría: ");
        Categoria categoria = gestorCategorias.buscarPorId(id);

        if (categoria == null) {
            System.out.println("No existe una categoría con ese ID.");
            return;
        }

        System.out.println(categoria.mostrarDatos());

        // Conserva los valores actuales de los campos que el usuario no elija modificar.
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

    /**
     * Imprime el informe de categorías con la cantidad de títulos activos asociados.
     */
    private void listarCategorias() {
        System.out.println();
        System.out.println(gestorCategorias.generarInforme());
    }

    /**
     * Calcula el siguiente identificador a partir del mayor identificador de categoría registrado.
     *
     * @return el mayor identificador registrado más uno, o uno si no hay categorías.
     */
    private int siguienteId() {
        int mayor = 0;

        // Incluye todos los identificadores registrados para proponer el siguiente número disponible.
        for (Categoria categoria : gestorCategorias.getCategorias()) {
            if (categoria.getIdCategoria() > mayor) {
                mayor = categoria.getIdCategoria();
            }
        }

        return mayor + 1;
    }

}
