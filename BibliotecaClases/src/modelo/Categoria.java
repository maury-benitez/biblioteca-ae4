package modelo;

import java.io.Serializable;

/**
 * Representa una categoría con la que se clasifican los libros del catálogo.
 */
public class Categoria implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int idCategoria;
    private String nombre;
    private String descripcion;

    /**
     * Crea una categoría con un identificador positivo y datos descriptivos obligatorios.
     *
     * @param idCategoria identificador de la categoría.
     * @param nombre nombre que se asigna al objeto.
     * @param descripcion descripción de la categoría.
     */
    public Categoria(int idCategoria, String nombre, String descripcion) {
        // Evita crear un objeto que no cumpla sus datos de identificación obligatorios.
        if (idCategoria <= 0) {
            throw new IllegalArgumentException("El ID de la categoría debe ser positivo.");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la categoría no puede estar en blanco.");
        }
        if (descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException("La descripción de la categoría no puede estar en blanco.");
        }

        this.idCategoria = idCategoria;
        this.nombre = nombre.strip();
        this.descripcion = descripcion.strip();
    }

    /** Devuelve el identificador de la categoría. */
    public int getIdCategoria() {
        return idCategoria;
    }

    /** Devuelve el nombre de la categoría. */
    public String getNombre() {
        return nombre;
    }

    /**
     * Actualiza el nombre después de validar el valor recibido.
     *
     * @param nombre nombre que se asigna al objeto.
     */
    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la categoría no puede estar en blanco.");
        }

        this.nombre = nombre.strip();
    }

    /** Devuelve la descripción de la categoría. */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Actualiza la descripción después de validar el valor recibido.
     *
     * @param descripcion descripción de la categoría.
     */
    public void setDescripcion(String descripcion) {
        if (descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException("La descripción de la categoría no puede estar en blanco.");
        }

        this.descripcion = descripcion.strip();
    }

    /**
     * Construye la representación textual de los datos de la categoría.
     *
     * @return el texto con los datos del objeto.
     */
    public String mostrarDatos() {
        return "ID de categoría: " + idCategoria
                + "\nNombre: " + nombre
                + "\nDescripción: " + descripcion;
    }

}
