package gestion;

import interfaces.Informable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import modelo.Categoria;

/**
 * Administra las categorías y consulta el catálogo para controlar sus libros asociados.
 */
public class GestorCategorias implements Informable, Serializable {

    private static final long serialVersionUID = 1L;

    private ArrayList<Categoria> categorias;
    private GestorLibros gestorLibros;

    /**
     * Inicializa el registro de categorías y lo vincula con el gestor de libros recibido.
     *
     * @param gestorLibros gestor del catálogo utilizado para consultar los libros de las categorías.
     */
    public GestorCategorias(GestorLibros gestorLibros) {
        if (gestorLibros == null) {
            throw new IllegalArgumentException("El gestor de libros no puede ser nulo.");
        }

        this.categorias = new ArrayList<>();
        this.gestorLibros = gestorLibros;
    }

    /**
     * Obtiene una copia de la lista de categorías registradas.
     *
     * @return una lista independiente con las mismas referencias a los objetos registrados.
     */
    public List<Categoria> getCategorias() {
        return new ArrayList<>(categorias);
    }

    /** Devuelve el gestor de libros compartido por el sistema. */
    public GestorLibros getGestorLibros() {
        return gestorLibros;
    }

    /**
     * Registra una categoría si su identificador y su nombre no están repetidos.
     *
     * @param categoria categoría utilizada en la operación.
     * @return {@code true} si se registra; {@code false} si es nula o sus datos de identificación están repetidos.
     */
    public boolean altaCategoria(Categoria categoria) {
        if (categoria == null) {
            return false;
        }
        // Mantiene únicos tanto el identificador como el nombre de cada categoría.
        if (buscarPorId(categoria.getIdCategoria()) != null
                || existeNombre(categoria.getNombre(), categoria.getIdCategoria())) {
            return false;
        }

        categorias.add(categoria);
        return true;
    }

    /**
     * Elimina una categoría que no tiene libros asociados.
     *
     * @param idCategoria identificador de la categoría.
     * @return {@code true} si se elimina; {@code false} si no existe o tiene libros asociados.
     */
    public boolean bajaCategoria(int idCategoria) {
        Categoria categoria = buscarPorId(idCategoria);

        // Conserva las categorías referenciadas por libros, aunque esos libros estén inactivos.
        if (categoria == null || gestorLibros.tieneLibrosAsociados(categoria)) {
            return false;
        }

        categorias.remove(categoria);
        return true;
    }

    /**
     * Actualiza el nombre y la descripción de una categoría existente sin duplicar nombres.
     *
     * @param categoria categoría utilizada en la operación.
     * @return {@code true} si se actualiza; {@code false} si la categoría es nula, no existe o el nombre pertenece a otra.
     */
    public boolean modificarCategoria(Categoria categoria) {
        if (categoria == null) {
            return false;
        }

        Categoria existente = buscarPorId(categoria.getIdCategoria());

        // Evita asignar a esta categoría el nombre de otra categoría registrada.
        if (existente == null
                || existeNombre(categoria.getNombre(), categoria.getIdCategoria())) {
            return false;
        }

        existente.setNombre(categoria.getNombre());
        existente.setDescripcion(categoria.getDescripcion());
        return true;
    }

    /**
     * Busca una categoría por su identificador.
     *
     * @param idCategoria identificador de la categoría.
     * @return la categoría encontrada o {@code null} si no existe.
     */
    public Categoria buscarPorId(int idCategoria) {
        for (Categoria categoria : categorias) {
            if (categoria.getIdCategoria() == idCategoria) {
                return categoria;
            }
        }

        return null;
    }

    /**
     * Genera el listado de categorías con la cantidad de títulos activos de cada una.
     *
     * @return el listado de categorías o un aviso si no hay categorías registradas.
     */
    @Override
    public String generarInforme() {
        if (categorias.isEmpty()) {
            return "No hay categorías registradas.";
        }

        String informe = "LISTADO DE CATEGORÍAS";

        // Cuenta títulos activos por categoría, no la cantidad de ejemplares de esos títulos.
        for (Categoria categoria : categorias) {
            informe = informe + "\n" + categoria.getIdCategoria()
                    + " - " + categoria.getNombre()
                    + " (" + categoria.getDescripcion() + ")"
                    + " - Libros: " + gestorLibros.contarPorCategoria(categoria);
        }

        return informe;
    }

    /**
     * Comprueba si otra categoría utiliza el nombre recibido, sin distinguir mayúsculas.
     *
     * @param nombre nombre que se busca entre las demás categorías.
     * @param idPropio identificador de la categoría que se excluye de la comparación.
     * @return {@code true} si otra categoría tiene ese nombre; {@code false} en otro caso.
     */
    private boolean existeNombre(String nombre, int idPropio) {
        // Excluye el propio identificador para permitir conservar el nombre durante una modificación.
        for (Categoria categoria : categorias) {
            if (categoria.getIdCategoria() != idPropio
                    && categoria.getNombre().equalsIgnoreCase(nombre.strip())) {
                return true;
            }
        }

        return false;
    }

}
