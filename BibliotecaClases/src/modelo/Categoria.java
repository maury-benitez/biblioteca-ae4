package modelo;

import java.io.Serializable;

/**
 * Representa una categoría del catálogo de libros de la biblioteca.
 */
public class Categoria implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int idCategoria;
    private String nombre;
    private String descripcion;

    public Categoria(int idCategoria, String nombre, String descripcion) {
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

    public int getIdCategoria() {
        return idCategoria;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la categoría no puede estar en blanco.");
        }

        this.nombre = nombre.strip();
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        if (descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException("La descripción de la categoría no puede estar en blanco.");
        }

        this.descripcion = descripcion.strip();
    }

    public String mostrarDatos() {
        return "ID de categoría: " + idCategoria
                + "\nNombre: " + nombre
                + "\nDescripción: " + descripcion;
    }

}
