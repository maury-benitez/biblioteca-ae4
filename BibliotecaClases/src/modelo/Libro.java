package modelo;

import java.io.Serializable;

/**
 * Representa un título del catálogo y controla sus ejemplares disponibles y su estado de alta.
 */
public class Libro implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String isbn;
    private String titulo;
    private String autor;
    private Categoria categoria;
    private int cantidadEjemplares;
    private int ejemplaresDisponibles;
    private boolean activo;

    /**
     * Crea un libro activo con sus datos y todos sus ejemplares inicialmente disponibles.
     *
     * @param isbn ISBN que identifica al libro.
     * @param titulo título del libro.
     * @param autor autor del libro.
     * @param categoria categoría utilizada en la operación.
     * @param cantidadEjemplares cantidad total de ejemplares del libro.
     */
    public Libro(String isbn, String titulo, String autor, Categoria categoria, int cantidadEjemplares) {
        // Evita crear un objeto que no cumpla sus datos de identificación obligatorios.
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
        // El alta comienza sin préstamos, por lo que todos los ejemplares están disponibles.
        this.ejemplaresDisponibles = cantidadEjemplares;
        this.activo = true;
    }

    /** Devuelve el ISBN del libro. */
    public String getIsbn() {
        return isbn;
    }

    /** Devuelve el título del libro. */
    public String getTitulo() {
        return titulo;
    }

    /**
     * Actualiza el título después de validar el valor recibido.
     *
     * @param titulo título del libro.
     */
    public void setTitulo(String titulo) {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("El título no puede estar en blanco.");
        }

        this.titulo = titulo.strip();
    }

    /** Devuelve el autor del libro. */
    public String getAutor() {
        return autor;
    }

    /**
     * Actualiza el autor después de validar el valor recibido.
     *
     * @param autor autor del libro.
     */
    public void setAutor(String autor) {
        if (autor == null || autor.isBlank()) {
            throw new IllegalArgumentException("El autor no puede estar en blanco.");
        }

        this.autor = autor.strip();
    }

    /** Devuelve la categoría del libro. */
    public Categoria getCategoria() {
        return categoria;
    }

    /**
     * Actualiza la categoría después de validar el valor recibido.
     *
     * @param categoria categoría utilizada en la operación.
     */
    public void setCategoria(Categoria categoria) {
        if (categoria == null) {
            throw new IllegalArgumentException("El libro debe tener una categoría.");
        }

        this.categoria = categoria;
    }

    /** Devuelve la cantidad total de ejemplares del libro. */
    public int getCantidadEjemplares() {
        return cantidadEjemplares;
    }

    /**
     * Actualiza el total de ejemplares conservando la cantidad que está prestada.
     *
     * @param cantidad nueva cantidad total de ejemplares.
     * @return {@code true} si el total es válido y se actualiza; {@code false} si es negativo o menor que los ejemplares prestados.
     */
    public boolean actualizarCantidadEjemplares(int cantidad) {
        int ejemplaresPrestados = cantidadEjemplares - ejemplaresDisponibles;

        // Impide reducir el inventario por debajo de los ejemplares que todavía están prestados.
        if (cantidad < 0 || cantidad < ejemplaresPrestados) {
            return false;
        }

        cantidadEjemplares = cantidad;
        // Recalcula la disponibilidad sin alterar la cantidad de préstamos existentes.
        ejemplaresDisponibles = cantidad - ejemplaresPrestados;
        return true;
    }

    /** Devuelve la cantidad de ejemplares disponibles del libro. */
    public int getEjemplaresDisponibles() {
        return ejemplaresDisponibles;
    }

    /**
     * Comprueba si el libro está activo y tiene algún ejemplar disponible.
     *
     * @return {@code true} si se puede prestar un ejemplar; {@code false} en otro caso.
     */
    public boolean hayDisponibles() {
        return activo && ejemplaresDisponibles > 0;
    }

    /**
     * Reduce la disponibilidad en un ejemplar si el libro puede prestarse.
     *
     * @return {@code true} si se descuenta el ejemplar; {@code false} si no hay disponibilidad o el libro está inactivo.
     */
    public boolean descontarEjemplar() {
        // Evita prestar un libro inactivo o producir una disponibilidad negativa.
        if (!hayDisponibles()) {
            return false;
        }

        ejemplaresDisponibles--;
        return true;
    }

    /**
     * Aumenta la disponibilidad en un ejemplar sin superar el total del libro.
     *
     * @return {@code true} si se suma el ejemplar; {@code false} si todos los ejemplares ya están disponibles.
     */
    public boolean sumarEjemplar() {
        // Evita que una devolución incremente la disponibilidad por encima del inventario.
        if (ejemplaresDisponibles >= cantidadEjemplares) {
            return false;
        }

        ejemplaresDisponibles++;
        return true;
    }

    /** Indica si el libro está activo. */
    public boolean isActivo() {
        return activo;
    }

    /**
     * Da de baja lógicamente el libro si no tiene ejemplares prestados.
     */
    public void desactivar() {
        // La baja debe esperar a que se devuelvan todos los ejemplares prestados.
        if (ejemplaresDisponibles < cantidadEjemplares) {
            throw new IllegalStateException("No se puede dar de baja un libro con ejemplares prestados.");
        }

        activo = false;
    }

    /**
     * Construye la representación textual de los datos del libro.
     *
     * @return el texto con los datos del objeto.
     */
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
