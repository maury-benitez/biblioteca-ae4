package gestion;

import interfaces.Informable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import modelo.Categoria;

/**
 * Administra las categorías de libros: alta, baja, modificación y
 * listado con la cantidad de libros de cada una (RF5).
 */
public class GestorCategorias implements Informable, Serializable {

    private static final long serialVersionUID = 1L;

    private ArrayList<Categoria> categorias;
    private GestorLibros gestorLibros;

    public GestorCategorias(GestorLibros gestorLibros) {
        if (gestorLibros == null) {
            throw new IllegalArgumentException("El gestor de libros no puede ser nulo.");
        }

        this.categorias = new ArrayList<>();
        this.gestorLibros = gestorLibros;
    }

    // Devuelve una copia de la lista para que no la modifiquen desde afuera
    public List<Categoria> getCategorias() {
        return new ArrayList<>(categorias);
    }

    public GestorLibros getGestorLibros() {
        return gestorLibros;
    }

    // Da de alta una categoría si no se repite el ID ni el nombre
    public boolean altaCategoria(Categoria categoria) {
        if (categoria == null) {
            return false;
        }
        if (buscarPorId(categoria.getIdCategoria()) != null
                || existeNombre(categoria.getNombre(), categoria.getIdCategoria())) {
            return false;
        }

        categorias.add(categoria);
        return true;
    }

    // Elimina la categoría solo si no tiene libros asociados
    public boolean bajaCategoria(int idCategoria) {
        Categoria categoria = buscarPorId(idCategoria);

        if (categoria == null || gestorLibros.tieneLibrosAsociados(categoria)) {
            return false;
        }

        categorias.remove(categoria);
        return true;
    }

    // Actualiza el nombre y la descripción de la categoría que tiene el mismo ID
    public boolean modificarCategoria(Categoria categoria) {
        if (categoria == null) {
            return false;
        }

        Categoria existente = buscarPorId(categoria.getIdCategoria());

        // no permito que quede con el nombre de otra categoría
        if (existente == null
                || existeNombre(categoria.getNombre(), categoria.getIdCategoria())) {
            return false;
        }

        existente.setNombre(categoria.getNombre());
        existente.setDescripcion(categoria.getDescripcion());
        return true;
    }

    // Devuelve la categoría con ese ID, o null si no existe
    public Categoria buscarPorId(int idCategoria) {
        for (Categoria categoria : categorias) {
            if (categoria.getIdCategoria() == idCategoria) {
                return categoria;
            }
        }

        return null;
    }

    // Arma el listado de categorías con la cantidad de libros de cada una
    @Override
    public String generarInforme() {
        if (categorias.isEmpty()) {
            return "No hay categorías registradas.";
        }

        String informe = "LISTADO DE CATEGORÍAS";

        for (Categoria categoria : categorias) {
            informe = informe + "\n" + categoria.getIdCategoria()
                    + " - " + categoria.getNombre()
                    + " (" + categoria.getDescripcion() + ")"
                    + " - Libros: " + gestorLibros.contarPorCategoria(categoria);
        }

        return informe;
    }

    // Indica si otra categoría (con distinto ID) ya usa ese nombre
    private boolean existeNombre(String nombre, int idPropio) {
        for (Categoria categoria : categorias) {
            if (categoria.getIdCategoria() != idPropio
                    && categoria.getNombre().equalsIgnoreCase(nombre.strip())) {
                return true;
            }
        }

        return false;
    }

}