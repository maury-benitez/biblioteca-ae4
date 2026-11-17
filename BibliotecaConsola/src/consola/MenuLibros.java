package consola;

import gestion.GestorCategorias;
import gestion.GestorLibros;
import gestion.SistemaBiblioteca;
import java.util.List;
import modelo.Categoria;
import modelo.Libro;

/**
 * Menú de consola para administrar los libros: alta, baja,
 * modificación, búsquedas e informes (RF2).
 */
public class MenuLibros {

    private final SistemaBiblioteca sistema;
    private final EntradaConsola entrada;

    public MenuLibros(SistemaBiblioteca sistema, EntradaConsola entrada) {
        this.sistema = sistema;
        this.entrada = entrada;
    }

    // Muestra el menú y repite hasta que el usuario elige volver
    public void mostrar() {
        int opcion;

        do {
            System.out.println("\n--- LIBROS ---");
            System.out.println("1. Alta de libro");
            System.out.println("2. Baja de libro");
            System.out.println("3. Modificar libro");
            System.out.println("4. Buscar libros");
            System.out.println("5. Informes");
            System.out.println("0. Volver");
            opcion = entrada.leerEnteroEnRango("Opción: ", 0, 5);

            switch (opcion) {
                case 1:
                    altaLibro();
                    break;
                case 2:
                    bajaLibro();
                    break;
                case 3:
                    modificarLibro();
                    break;
                case 4:
                    buscarLibros();
                    break;
                case 5:
                    mostrarInformes();
                    break;
            }
        } while (opcion != 0);
    }

    // Pide los datos y da de alta un libro en una categoría existente
    private void altaLibro() {
        System.out.println("\nAlta de libro");

        if (sistema.getGestorCategorias().getCategorias().isEmpty()) {
            System.out.println("Primero debe cargar al menos una categoría.");
            return;
        }

        String isbn = entrada.leerCadena("ISBN: ");
        String titulo = entrada.leerCadena("Título: ");
        String autor = entrada.leerCadena("Autor: ");
        Categoria categoria = elegirCategoria();

        if (categoria == null) {
            return;
        }

        int cantidad = entrada.leerEntero("Cantidad de ejemplares: ");

        try {
            Libro libro = new Libro(isbn, titulo, autor, categoria, cantidad);

            if (sistema.getGestorLibros().altaLibro(libro)) {
                System.out.println("Libro registrado.");
            } else {
                System.out.println("Ya existe un libro con ese ISBN.");
            }
        } catch (IllegalArgumentException ex) {
            // el constructor rechazó algún dato, muestro el motivo
            System.out.println(ex.getMessage());
        }
    }

    // Pide el ISBN y da de baja el libro si no tiene ejemplares prestados
    private void bajaLibro() {
        System.out.println("\nBaja de libro");
        String isbn = entrada.leerCadena("ISBN del libro: ");
        Libro libro = buscarLibroActivo(isbn);

        if (libro == null) {
            return;
        }

        System.out.println(libro.mostrarDatos());

        if (!entrada.confirmar("¿Confirma la baja?")) {
            System.out.println("Operación cancelada.");
            return;
        }

        try {
            if (sistema.bajaLibro(isbn)) {
                System.out.println("Libro dado de baja.");
            } else {
                System.out.println("No se puede dar de baja: el libro tiene ejemplares prestados.");
            }
        } catch (IllegalStateException ex) {
            // el sistema avisa si no hay una sesión iniciada
            System.out.println(ex.getMessage());
        }
    }

    // Pide el ISBN y permite cambiar los datos del libro, menos el ISBN
    private void modificarLibro() {
        System.out.println("\nModificar libro");
        String isbn = entrada.leerCadena("ISBN del libro: ");
        Libro libro = buscarLibroActivo(isbn);

        if (libro == null) {
            return;
        }

        System.out.println(libro.mostrarDatos());

        // arranco con los datos actuales y cambio solo los que el usuario elija
        String titulo = libro.getTitulo();
        String autor = libro.getAutor();
        Categoria categoria = libro.getCategoria();
        int cantidad = libro.getCantidadEjemplares();

        if (entrada.confirmar("¿Modificar el título?")) {
            titulo = entrada.leerCadena("Nuevo título: ");
        }
        if (entrada.confirmar("¿Modificar el autor?")) {
            autor = entrada.leerCadena("Nuevo autor: ");
        }
        if (entrada.confirmar("¿Modificar la categoría?")) {
            Categoria elegida = elegirCategoria();

            if (elegida == null) {
                return;
            }

            categoria = elegida;
        }
        if (entrada.confirmar("¿Modificar la cantidad de ejemplares?")) {
            cantidad = entrada.leerEntero("Nueva cantidad de ejemplares: ");
        }

        try {
            Libro modificado = new Libro(libro.getIsbn(), titulo, autor, categoria, cantidad);

            if (sistema.getGestorLibros().modificarLibro(modificado)) {
                System.out.println("Libro modificado.");
            } else {
                System.out.println("No se pudo modificar: la cantidad no puede ser menor "
                        + "que los ejemplares prestados.");
            }
        } catch (IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
        }
    }

    // Permite buscar por ISBN, título, autor o categoría
    private void buscarLibros() {
        GestorLibros gestorLibros = sistema.getGestorLibros();

        System.out.println("\nBuscar libros");
        System.out.println("1. Por ISBN");
        System.out.println("2. Por título");
        System.out.println("3. Por autor");
        System.out.println("4. Por categoría");
        int opcion = entrada.leerEnteroEnRango("Opción: ", 1, 4);

        switch (opcion) {
            case 1:
                Libro libro = buscarLibroActivo(entrada.leerCadena("ISBN: "));

                if (libro != null) {
                    System.out.println(libro.mostrarDatos());
                }
                break;
            case 2:
                mostrarLista(gestorLibros.buscarPorTitulo(entrada.leerCadena("Título: ")));
                break;
            case 3:
                mostrarLista(gestorLibros.buscarPorAutor(entrada.leerCadena("Autor: ")));
                break;
            case 4:
                Categoria categoria = elegirCategoria();

                if (categoria != null) {
                    mostrarLista(gestorLibros.buscarPorCategoria(categoria));
                }
                break;
        }
    }

    // Muestra el informe general o los listados de disponibles y prestados
    private void mostrarInformes() {
        GestorLibros gestorLibros = sistema.getGestorLibros();

        System.out.println("\nInformes de libros");
        System.out.println("1. Informe general");
        System.out.println("2. Libros disponibles");
        System.out.println("3. Libros prestados");
        int opcion = entrada.leerEnteroEnRango("Opción: ", 1, 3);

        switch (opcion) {
            case 1:
                System.out.println(gestorLibros.generarInforme());
                break;
            case 2:
                mostrarLista(gestorLibros.listarDisponibles());
                break;
            case 3:
                mostrarLista(gestorLibros.listarPrestados());
                break;
        }
    }

    // Busca un libro por ISBN y avisa si no existe o está dado de baja
    private Libro buscarLibroActivo(String isbn) {
        Libro libro = sistema.getGestorLibros().buscarPorIsbn(isbn);

        if (libro == null || !libro.isActivo()) {
            System.out.println("No existe un libro activo con ese ISBN.");
            return null;
        }

        return libro;
    }

    // Muestra las categorías y devuelve la que elige el usuario, o null si no existe
    private Categoria elegirCategoria() {
        GestorCategorias gestorCategorias = sistema.getGestorCategorias();

        System.out.println(gestorCategorias.generarInforme());
        int id = entrada.leerEntero("ID de la categoría: ");
        Categoria categoria = gestorCategorias.buscarPorId(id);

        if (categoria == null) {
            System.out.println("No existe una categoría con ese ID.");
        }

        return categoria;
    }

    // Muestra los datos de cada libro de la lista, o un aviso si está vacía
    private void mostrarLista(List<Libro> libros) {
        if (libros.isEmpty()) {
            System.out.println("No se encontraron libros.");
        }
        for (Libro libro : libros) {
            System.out.println(libro.mostrarDatos());
            System.out.println();
        }
    }

}