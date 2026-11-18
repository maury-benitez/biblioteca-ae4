package gestion;

import interfaces.Informable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import modelo.Categoria;
import modelo.Libro;

/**
 * Administra el catálogo de libros, sus búsquedas y los informes de disponibilidad y préstamos.
 */
public class GestorLibros implements Informable, Serializable {

    private static final long serialVersionUID = 1L;

    private final ArrayList<Libro> libros;

    /**
     * Inicializa un catálogo de libros vacío.
     */
    public GestorLibros() {
        libros = new ArrayList<>();
    }

    /**
     * Obtiene una copia no modificable de la lista de libros.
     *
     * @return una lista no modificable con las mismas referencias a los objetos registrados.
     */
    public List<Libro> getLibros() {
        return List.copyOf(libros);
    }

    /**
     * Registra un libro activo cuyo ISBN no está registrado.
     *
     * @param libro libro utilizado en la operación.
     * @return {@code true} si se registra; {@code false} si está inactivo o su ISBN ya existe.
     */
    public boolean altaLibro(Libro libro) {
        if (libro == null) {
            throw new IllegalArgumentException("Debe indicar un libro.");
        }
        // Reserva cada ISBN incluso cuando ya pertenece a un libro dado de baja.
        if (!libro.isActivo() || buscarPorIsbn(libro.getIsbn()) != null) {
            return false;
        }

        libros.add(libro);
        return true;
    }

    /**
     * Da de baja lógicamente un libro activo sin ejemplares prestados.
     *
     * @param isbn ISBN que identifica al libro.
     * @return {@code true} si se desactiva; {@code false} si no existe, está inactivo o tiene ejemplares prestados.
     */
    boolean bajaLibro(String isbn) {
        Libro libro = buscarPorIsbn(isbn);

        // La baja lógica conserva el historial y exige que no haya ejemplares prestados.
        if (libro == null || !libro.isActivo()
                || libro.getEjemplaresDisponibles() < libro.getCantidadEjemplares()) {
            return false;
        }

        libro.desactivar();
        return true;
    }

    /**
     * Actualiza los datos de un libro activo conservando su ISBN y los ejemplares prestados.
     *
     * @param libro libro utilizado en la operación.
     * @return {@code true} si se actualiza; {@code false} si no se cumplen las condiciones de estado o cantidad.
     */
    public boolean modificarLibro(Libro libro) {
        if (libro == null) {
            throw new IllegalArgumentException("Debe indicar un libro.");
        }

        Libro registrado = buscarPorIsbn(libro.getIsbn());

        if (registrado == null || !registrado.isActivo() || !libro.isActivo()) {
            return false;
        }
        // Valida primero el inventario para no aplicar otros cambios si el nuevo total es insuficiente.
        if (!registrado.actualizarCantidadEjemplares(libro.getCantidadEjemplares())) {
            return false;
        }

        // Modifica la instancia registrada para conservar las referencias que guardan los préstamos.
        registrado.setTitulo(libro.getTitulo());
        registrado.setAutor(libro.getAutor());
        registrado.setCategoria(libro.getCategoria());
        return true;
    }

    /**
     * Busca un libro por su ISBN, incluidos los libros inactivos.
     *
     * @param isbn ISBN que identifica al libro.
     * @return el libro encontrado o {@code null} si no existe.
     */
    public Libro buscarPorIsbn(String isbn) {
        if (isbn == null || isbn.isBlank()) {
            throw new IllegalArgumentException("El ISBN no puede estar en blanco.");
        }

        String isbnBuscado = isbn.strip();

        // Incluye los libros inactivos para reconocer ISBN ya utilizados y conservar sus referencias.
        for (Libro libro : libros) {
            if (libro.getIsbn().equals(isbnBuscado)) {
                return libro;
            }
        }

        return null;
    }

    /**
     * Busca libros activos cuyo título contiene el texto recibido, sin distinguir mayúsculas.
     *
     * @param titulo texto que debe estar contenido en el título.
     * @return una lista no modificable de coincidencias, vacía si no hay resultados.
     */
    public List<Libro> buscarPorTitulo(String titulo) {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("El título de búsqueda no puede estar en blanco.");
        }

        // Normaliza el texto con una configuración estable para buscar sin distinguir mayúsculas.
        String tituloBuscado = titulo.strip().toLowerCase(Locale.ROOT);
        ArrayList<Libro> resultados = new ArrayList<>();

        for (Libro libro : libros) {
            if (libro.isActivo() && libro.getTitulo().toLowerCase(Locale.ROOT).contains(tituloBuscado)) {
                resultados.add(libro);
            }
        }

        return List.copyOf(resultados);
    }

    /**
     * Busca libros activos cuyo autor contiene el texto recibido, sin distinguir mayúsculas.
     *
     * @param autor texto que debe estar contenido en el nombre del autor.
     * @return una lista no modificable de coincidencias, vacía si no hay resultados.
     */
    public List<Libro> buscarPorAutor(String autor) {
        if (autor == null || autor.isBlank()) {
            throw new IllegalArgumentException("El autor de búsqueda no puede estar en blanco.");
        }

        // Normaliza el texto con una configuración estable para buscar sin distinguir mayúsculas.
        String autorBuscado = autor.strip().toLowerCase(Locale.ROOT);
        ArrayList<Libro> resultados = new ArrayList<>();

        for (Libro libro : libros) {
            if (libro.isActivo() && libro.getAutor().toLowerCase(Locale.ROOT).contains(autorBuscado)) {
                resultados.add(libro);
            }
        }

        return List.copyOf(resultados);
    }

    /**
     * Busca los libros activos que pertenecen al identificador de la categoría recibida.
     *
     * @param categoria categoría utilizada en la operación.
     * @return una lista no modificable de libros coincidentes, vacía si no hay resultados.
     */
    public List<Libro> buscarPorCategoria(Categoria categoria) {
        if (categoria == null) {
            throw new IllegalArgumentException("Debe indicar una categoría.");
        }

        ArrayList<Libro> resultados = new ArrayList<>();

        // Compara identificadores para reconocer la categoría aunque se reciba otra instancia.
        for (Libro libro : libros) {
            if (libro.isActivo() && libro.getCategoria().getIdCategoria() == categoria.getIdCategoria()) {
                resultados.add(libro);
            }
        }

        return List.copyOf(resultados);
    }

    /**
     * Reúne los libros activos con algún ejemplar disponible.
     *
     * @return una lista no modificable de libros disponibles, vacía si no hay ninguno.
     */
    public List<Libro> listarDisponibles() {
        ArrayList<Libro> disponibles = new ArrayList<>();

        for (Libro libro : libros) {
            if (libro.hayDisponibles()) {
                disponibles.add(libro);
            }
        }

        return List.copyOf(disponibles);
    }

    /**
     * Reúne los libros activos con al menos un ejemplar prestado.
     *
     * @return una lista no modificable de libros con préstamos, vacía si no hay ninguno.
     */
    public List<Libro> listarPrestados() {
        ArrayList<Libro> prestados = new ArrayList<>();

        // Un título tiene préstamos cuando su disponibilidad es menor que su cantidad total.
        for (Libro libro : libros) {
            if (libro.isActivo() && libro.getEjemplaresDisponibles() < libro.getCantidadEjemplares()) {
                prestados.add(libro);
            }
        }

        return List.copyOf(prestados);
    }

    /**
     * Cuenta los títulos activos de la categoría recibida.
     *
     * @param categoria categoría utilizada en la operación.
     * @return la cantidad de títulos activos de esa categoría, sin sumar sus ejemplares.
     */
    public int contarPorCategoria(Categoria categoria) {
        return buscarPorCategoria(categoria).size();
    }

    /**
     * Comprueba si una categoría tiene libros asociados, incluidos los inactivos.
     *
     * @param categoria categoría utilizada en la operación.
     * @return {@code true} si algún libro pertenece a la categoría; {@code false} en otro caso.
     */
    public boolean tieneLibrosAsociados(Categoria categoria) {
        if (categoria == null) {
            throw new IllegalArgumentException("Debe indicar una categoría.");
        }

        // Incluye los libros inactivos porque también conservan una referencia a su categoría.
        for (Libro libro : libros) {
            if (libro.getCategoria().getIdCategoria() == categoria.getIdCategoria()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Genera un informe del catálogo activo y de los libros disponibles y prestados.
     *
     * @return el informe textual con los listados y el total de títulos activos.
     */
    @Override
    public String generarInforme() {
        StringBuilder informe = new StringBuilder("INFORME DE LIBROS\n");
        informe.append("\nListado general:\n");
        // El total del informe cuenta títulos activos y no suma sus ejemplares.
        int cantidadActivos = 0;

        for (Libro libro : libros) {
            if (libro.isActivo()) {
                informe.append(libro.mostrarDatos()).append("\n\n");
                cantidadActivos++;
            }
        }

        if (cantidadActivos == 0) {
            informe.append("No hay libros activos registrados.\n");
        }

        informe.append("Total de títulos activos: ").append(cantidadActivos).append("\n");
        informe.append("\nLibros disponibles:\n");
        List<Libro> disponibles = listarDisponibles();

        if (disponibles.isEmpty()) {
            informe.append("No hay libros disponibles.\n");
        }
        for (Libro libro : disponibles) {
            informe.append(libro.mostrarDatos()).append("\n\n");
        }

        informe.append("\nLibros prestados:\n");
        // Un mismo título puede figurar disponible y prestado si tiene varios ejemplares.
        List<Libro> prestados = listarPrestados();

        if (prestados.isEmpty()) {
            informe.append("No hay libros prestados.\n");
        }
        for (Libro libro : prestados) {
            informe.append(libro.mostrarDatos()).append("\n\n");
        }

        return informe.toString();
    }
}
