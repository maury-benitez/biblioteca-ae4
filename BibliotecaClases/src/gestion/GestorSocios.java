package gestion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import modelo.Socio;

/**
 * Administra los socios de la biblioteca: alta, baja, modificación
 * y consulta (RF3). La baja es lógica: el socio queda inactivo.
 */
public class GestorSocios implements Serializable {

    private static final long serialVersionUID = 1L;

    private ArrayList<Socio> socios;

    public GestorSocios() {
        socios = new ArrayList<>();
    }

    // Devuelve una copia de la lista con todos los socios, activos e inactivos
    public List<Socio> getSocios() {
        return new ArrayList<>(socios);
    }

    // Da de alta un socio si no hay otro socio activo con el mismo DNI
    public boolean altaSocio(Socio socio) {
        if (socio == null || existeDni(socio.getDni())) {
            return false;
        }

        socios.add(socio);
        return true;
    }

    // Da de baja al socio dejándolo inactivo. Los préstamos pendientes
    // se controlan antes, en SistemaBiblioteca
    boolean bajaSocio(String dni) {
        Socio socio = buscarPorDni(dni);

        if (socio == null) {
            return false;
        }

        socio.desactivar();
        return true;
    }

    // Actualiza los datos del socio que tiene el mismo DNI que el recibido
    public boolean modificarSocio(Socio socio) {
        if (socio == null) {
            return false;
        }

        Socio existente = buscarPorDni(socio.getDni());

        if (existente == null) {
            return false;
        }

        // el DNI y el número de socio no cambian, copio el resto de los datos
        existente.setNombre(socio.getNombre());
        existente.setApellido(socio.getApellido());
        existente.setEmail(socio.getEmail());
        existente.setTelefono(socio.getTelefono());
        return true;
    }

    // Devuelve el socio activo con ese DNI, o null si no existe
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

    // Devuelve los socios activos cuyo nombre o apellido contiene el texto buscado
    public List<Socio> buscarPorNombre(String nombre) {
        List<Socio> encontrados = new ArrayList<>();

        if (nombre == null || nombre.isBlank()) {
            return encontrados;
        }

        // paso todo a minúsculas para que no importe cómo lo escriban
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

    // Indica si hay un socio activo con ese DNI
    public boolean existeDni(String dni) {
        return buscarPorDni(dni) != null;
    }

}