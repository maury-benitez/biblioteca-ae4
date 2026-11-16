package modelo;

import java.io.Serializable;

public class Libro implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String isbn;
    private String titulo;
    private String autor;
    private Categoria categoria;
    private int cantidadEjemplares;
    private int ejemplaresDisponibles;
    private boolean activo;

    public Libro(String isbn, String titulo, String autor, Categoria categoria, int cantidadEjemplares) {
        if (isbn == null || isbn.isBlank()) {
            throw new IllegalArgumentException("El ISBN no puede estar en blanco.");
        }
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("El título no puede estar en blanco.");
        }
        if (autor == null || autor.isBlank()) {
            throw new IllegalArgumentException("El autor no puede estar en blanco.");
        }
        if (categoria == null) {
            throw new IllegalArgumentException("El libro debe tener una categoría.");
        }
        if (cantidadEjemplares < 0) {
            throw new IllegalArgumentException("La cantidad de ejemplares no puede ser negativa.");
        }

        this.isbn = isbn.strip();
        this.titulo = titulo.strip();
        this.autor = autor.strip();
        this.categoria = categoria;
        this.cantidadEjemplares = cantidadEjemplares;
        this.ejemplaresDisponibles = cantidadEjemplares;
        this.activo = true;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("El título no puede estar en blanco.");
        }

        this.titulo = titulo.strip();
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        if (autor == null || autor.isBlank()) {
            throw new IllegalArgumentException("El autor no puede estar en blanco.");
        }

        this.autor = autor.strip();
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        if (categoria == null) {
            throw new IllegalArgumentException("El libro debe tener una categoría.");
        }

        this.categoria = categoria;
    }

    public int getCantidadEjemplares() {
        return cantidadEjemplares;
    }

    public boolean actualizarCantidadEjemplares(int cantidad) {
        int ejemplaresPrestados = cantidadEjemplares - ejemplaresDisponibles;

        if (cantidad < 0 || cantidad < ejemplaresPrestados) {
            return false;
        }

        cantidadEjemplares = cantidad;
        ejemplaresDisponibles = cantidad - ejemplaresPrestados;
        return true;
    }

    public int getEjemplaresDisponibles() {
        return ejemplaresDisponibles;
    }

    public boolean hayDisponibles() {
        return activo && ejemplaresDisponibles > 0;
    }

    public boolean descontarEjemplar() {
        if (!hayDisponibles()) {
            return false;
        }

        ejemplaresDisponibles--;
        return true;
    }

    public boolean sumarEjemplar() {
        if (ejemplaresDisponibles >= cantidadEjemplares) {
            return false;
        }

        ejemplaresDisponibles++;
        return true;
    }

    public boolean isActivo() {
        return activo;
    }

    public void desactivar() {
        if (ejemplaresDisponibles < cantidadEjemplares) {
            throw new IllegalStateException("No se puede dar de baja un libro con ejemplares prestados.");
        }

        activo = false;
    }

    public String mostrarDatos() {
        return "ISBN: " + isbn
                + "\nTítulo: " + titulo
                + "\nAutor: " + autor
                + "\nCategoría: " + categoria.getNombre()
                + "\nCantidad de ejemplares: " + cantidadEjemplares
                + "\nEjemplares disponibles: " + ejemplaresDisponibles
                + "\nEstado: " + (activo ? "Activo" : "Inactivo");
    }
}
