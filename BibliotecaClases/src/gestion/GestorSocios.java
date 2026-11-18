package gestion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import modelo.Socio;

/**
 * Administra el registro, la modificación, la baja lógica y las consultas de socios.
 */
public class GestorSocios implements Serializable {

    private static final long serialVersionUID = 1L;

    private ArrayList<Socio> socios;

    /**
     * Inicializa un registro de socios vacío.
     */
    public GestorSocios() {
        socios = new ArrayList<>();
    }

    /**
     * Obtiene una copia de la lista de socios registrados.
     *
     * @return una lista independiente con las mismas referencias a los objetos registrados.
     */
    public List<Socio> getSocios() {
        return new ArrayList<>(socios);
    }

    /**
     * Registra un socio activo sin repetir su DNI ni su número de socio.
     *
     * @param socio socio utilizado en la operación.
     * @return {@code true} si se registra; {@code false} si es nulo, está inactivo o alguno de sus identificadores ya existe.
     */
    public boolean altaSocio(Socio socio) {
        if (socio == null || !socio.isActivo() || existeDni(socio.getDni())) {
            return false;
        }

        // También reserva los números de socios inactivos para no reutilizar su identidad.
        for (Socio registrado : socios) {
            if (registrado.getNroSocio() == socio.getNroSocio()) {
                return false;
            }
        }

        socios.add(socio);
        return true;
    }

    /**
     * Desactiva al socio activo con el DNI recibido.
     *
     * @param dni DNI que identifica a la persona.
     * @return {@code true} si se desactiva; {@code false} si no existe un socio activo con ese DNI.
     */
    boolean bajaSocio(String dni) {
        Socio socio = buscarPorDni(dni);

        if (socio == null) {
            return false;
        }

        // El sistema coordinador comprueba los préstamos antes de solicitar esta baja lógica.
        socio.desactivar();
        return true;
    }

    /**
     * Actualiza los datos personales de un socio activo conservando su DNI y su número.
     *
     * @param socio socio utilizado en la operación.
     * @return {@code true} si se actualiza; {@code false} si se recibe un socio nulo o no hay un socio activo con ese DNI.
     */
    public boolean modificarSocio(Socio socio) {
        if (socio == null) {
            return false;
        }

        Socio existente = buscarPorDni(socio.getDni());

        if (existente == null) {
            return false;
        }

        // Conserva el DNI, el número y la instancia que pueden referenciar los préstamos.
        existente.setNombre(socio.getNombre());
        existente.setApellido(socio.getApellido());
        existente.setEmail(socio.getEmail());
        existente.setTelefono(socio.getTelefono());
        return true;
    }

    /**
     * Busca al socio activo con el DNI recibido.
     *
     * @param dni DNI que identifica a la persona.
     * @return el socio encontrado o {@code null} si el DNI es nulo o no corresponde a un socio activo.
     */
    public Socio buscarPorDni(String dni) {
        if (dni == null) {
            return null;
        }

        for (Socio socio : socios) {
            if (socio.isActivo() && socio.getDni().equals(dni.strip())) {
                return socio;
            }
        }

        return null;
    }

    /**
     * Busca socios activos cuyo nombre o apellido contiene el texto recibido, sin distinguir
     * mayúsculas.
     *
     * @param nombre texto que se busca en el nombre o apellido del socio.
     * @return una lista de coincidencias, vacía si la búsqueda es nula, está en blanco o no tiene resultados.
     */
    public List<Socio> buscarPorNombre(String nombre) {
        List<Socio> encontrados = new ArrayList<>();

        if (nombre == null || nombre.isBlank()) {
            return encontrados;
        }

        // Normaliza el texto para admitir coincidencias parciales sin distinguir mayúsculas.
        String buscado = nombre.strip().toLowerCase();

        for (Socio socio : socios) {
            if (socio.isActivo()
                    && (socio.getNombre().toLowerCase().contains(buscado)
                    || socio.getApellido().toLowerCase().contains(buscado))) {
                encontrados.add(socio);
            }
        }

        return encontrados;
    }

    /**
     * Comprueba si un DNI está registrado, incluidos los socios inactivos.
     *
     * @param dni DNI que identifica a la persona.
     * @return {@code true} si el DNI está registrado; {@code false} si es nulo, está en blanco o no existe.
     */
    public boolean existeDni(String dni) {
        if (dni == null || dni.isBlank()) {
            return false;
        }

        // Incluye los socios inactivos para evitar reutilizar un DNI ya registrado.
        for (Socio socio : socios) {
            if (socio.getDni().equals(dni.strip())) {
                return true;
            }
        }

        return false;
    }

}
