package interfaces;

/**
 * Define la generación de un informe textual a partir de la información de un objeto.
 */
public interface Informable {

    /**
     * Define la generación de un informe textual con los datos del objeto.
     *
     * @return el informe generado por la implementación.
     */
    String generarInforme();
}
