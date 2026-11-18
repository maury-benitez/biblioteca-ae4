package interfaces;

/**
 * Define las operaciones de guardado y carga de datos de un objeto.
 */
public interface Persistible {

    /**
     * Define la operación que guarda los datos del objeto en su medio de persistencia.
     */
    void guardarDatos();

    /**
     * Define la operación que recupera los datos del objeto desde su medio de persistencia.
     */
    void cargarDatos();
}
