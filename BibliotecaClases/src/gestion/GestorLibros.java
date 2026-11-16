package gestion;

import interfaces.Informable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import modelo.Categoria;
import modelo.Libro;

public class GestorLibros implements Informable, Serializable {

    private static final long serialVersionUID = 1L;

    private final ArrayList<Libro> libros;

    public GestorLibros() {
        libros = new ArrayList<>();
    }

    public List<Libro> getLibros() {
        return List.copyOf(libros);
    }

    public boolean altaLibro(Libro libro) {
        if (libro == null) {
            throw new IllegalArgumentException("Debe indicar un libro.");
        }
        if (!libro.isActivo() || buscarPorIsbn(libro.getIsbn()) != null) {
            return false;
        }

        libros.add(libro);
        return true;
    }

    boolean bajaLibro(String isbn) {
        Libro libro = buscarPorIsbn(isbn);

        if (libro == null || !libro.isActivo()
                || libro.getEjemplaresDisponibles() < libro.getCantidadEjemplares()) {
            return false;
        }

        libro.desactivar();
        return true;
    }

    public boolean modificarLibro(Libro libro) {
        if (libro == null) {
            throw new IllegalArgumentException("Debe indicar un libro.");
        }

        Libro registrado = buscarPorIsbn(libro.getIsbn());

        if (registrado == null || !registrado.isActivo() || !libro.isActivo()) {
            return false;
        }
        if (!registrado.actualizarCantidadEjemplares(libro.getCantidadEjemplares())) {
            return false;
        }

        registrado.setTitulo(libro.getTitulo());
        registrado.setAutor(libro.getAutor());
        registrado.setCategoria(libro.getCategoria());
        return true;
    }

    public Libro buscarPorIsbn(String isbn) {
        if (isbn == null || isbn.isBlank()) {
            throw new IllegalArgumentException("El ISBN no puede estar en blanco.");
        }

        String isbnBuscado = isbn.strip();

        for (Libro libro : libros) {
            if (libro.getIsbn().equals(isbnBuscado)) {
                return libro;
            }
        }

        return null;
    }

    public List<Libro> buscarPorTitulo(String titulo) {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("El título de búsqueda no puede estar en blanco.");
        }

        String tituloBuscado = titulo.strip().toLowerCase(Locale.ROOT);
        ArrayList<Libro> resultados = new ArrayList<>();

        for (Libro libro : libros) {
            if (libro.isActivo() && libro.getTitulo().toLowerCase(Locale.ROOT).contains(tituloBuscado)) {
                resultados.add(libro);
            }
        }

        return List.copyOf(resultados);
    }

    public List<Libro> buscarPorAutor(String autor) {
        if (autor == null || autor.isBlank()) {
            throw new IllegalArgumentException("El autor de búsqueda no puede estar en blanco.");
        }

        String autorBuscado = autor.strip().toLowerCase(Locale.ROOT);
        ArrayList<Libro> resultados = new ArrayList<>();

        for (Libro libro : libros) {
            if (libro.isActivo() && libro.getAutor().toLowerCase(Locale.ROOT).contains(autorBuscado)) {
                resultados.add(libro);
            }
        }

        return List.copyOf(resultados);
    }

    public List<Libro> buscarPorCategoria(Categoria categoria) {
        if (categoria == null) {
            throw new IllegalArgumentException("Debe indicar una categoría.");
        }

        ArrayList<Libro> resultados = new ArrayList<>();

        for (Libro libro : libros) {
            if (libro.isActivo() && libro.getCategoria().getIdCategoria() == categoria.getIdCategoria()) {
                resultados.add(libro);
            }
        }

        return List.copyOf(resultados);
    }

    public List<Libro> listarDisponibles() {
        ArrayList<Libro> disponibles = new ArrayList<>();

        for (Libro libro : libros) {
            if (libro.hayDisponibles()) {
                disponibles.add(libro);
            }
        }

        return List.copyOf(disponibles);
    }

    public List<Libro> listarPrestados() {
        ArrayList<Libro> prestados = new ArrayList<>();

        for (Libro libro : libros) {
            if (libro.isActivo() && libro.getEjemplaresDisponibles() < libro.getCantidadEjemplares()) {
                prestados.add(libro);
            }
        }

        return List.copyOf(prestados);
    }

    public int contarPorCategoria(Categoria categoria) {
        return buscarPorCategoria(categoria).size();
    }

    public boolean tieneLibrosAsociados(Categoria categoria) {
        if (categoria == null) {
            throw new IllegalArgumentException("Debe indicar una categoría.");
        }

        for (Libro libro : libros) {
            if (libro.getCategoria().getIdCategoria() == categoria.getIdCategoria()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String generarInforme() {
        StringBuilder informe = new StringBuilder("INFORME DE LIBROS\n");
        informe.append("\nListado general:\n");
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
