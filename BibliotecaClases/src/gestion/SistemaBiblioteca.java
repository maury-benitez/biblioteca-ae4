package gestion;

import interfaces.Persistible;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UncheckedIOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import modelo.Categoria;
import modelo.Libro;
import modelo.Prestamo;
import modelo.Socio;
import modelo.Usuario;

/**
 * Coordina los gestores, la sesión de usuario y la persistencia del conjunto de datos de la
 * biblioteca.
 */
public class SistemaBiblioteca implements Persistible, Serializable {

    private static final long serialVersionUID = 1L;
    private static final String RUTA_DATOS = "datos/biblioteca.dat";

    private GestorUsuarios gestorUsuarios;
    private GestorSocios gestorSocios;
    private GestorPrestamos gestorPrestamos;
    private GestorLibros gestorLibros;
    private GestorCategorias gestorCategorias;
    private transient Usuario usuarioLogueado;
    private transient boolean cargaFallida;

    /**
     * Inicializa los gestores y comparte el catálogo con el gestor de categorías.
     */
    public SistemaBiblioteca() {
        gestorUsuarios = new GestorUsuarios();
        gestorSocios = new GestorSocios();
        gestorPrestamos = new GestorPrestamos();
        gestorLibros = new GestorLibros();
        // Comparte el mismo catálogo para que las categorías consulten los libros realmente registrados.
        gestorCategorias = new GestorCategorias(gestorLibros);
    }

    /**
     * Carga los datos persistidos para iniciar el trabajo con la biblioteca.
     */
    public void iniciar() {
        cargarDatos();
    }

    /**
     * Guarda los datos y, si el guardado finaliza correctamente, cierra la sesión.
     */
    public void finalizar() {
        guardarDatos();
        cerrarSesion();
    }

    /** Devuelve el gestor de usuarios compartido por el sistema. */
    public GestorUsuarios getGestorUsuarios() {
        return gestorUsuarios;
    }

    /** Devuelve el gestor de socios compartido por el sistema. */
    public GestorSocios getGestorSocios() {
        return gestorSocios;
    }

    /** Devuelve el gestor de préstamos compartido por el sistema. */
    public GestorPrestamos getGestorPrestamos() {
        return gestorPrestamos;
    }

    /** Devuelve el gestor de libros compartido por el sistema. */
    public GestorLibros getGestorLibros() {
        return gestorLibros;
    }

    /** Devuelve el gestor de categorías compartido por el sistema. */
    public GestorCategorias getGestorCategorias() {
        return gestorCategorias;
    }

    /** Devuelve el usuario de la sesión o {@code null} si no hay sesión activa. */
    public Usuario getUsuarioLogueado() {
        return usuarioLogueado;
    }

    /**
     * Actualiza la sesión con el usuario correspondiente a las credenciales recibidas.
     *
     * @param nombreUsuario nombre de usuario para el acceso al sistema.
     * @param contrasena contraseña utilizada en la operación.
     * @return {@code true} si las credenciales identifican a un usuario; {@code false} si la sesión queda sin usuario.
     */
    public boolean iniciarSesion(String nombreUsuario, String contrasena) {
        // Un intento fallido también elimina la referencia de cualquier sesión anterior.
        usuarioLogueado = gestorUsuarios.iniciarSesion(nombreUsuario, contrasena);
        return haySesionActiva();
    }

    /**
     * Elimina la referencia al usuario de la sesión actual.
     */
    public void cerrarSesion() {
        usuarioLogueado = null;
    }

    /**
     * Comprueba si existe un usuario asociado a la sesión.
     *
     * @return {@code true} si hay un usuario autenticado; {@code false} en otro caso.
     */
    public boolean haySesionActiva() {
        return usuarioLogueado != null;
    }

    /**
     * Da de baja a un socio sin préstamos pendientes, con una sesión iniciada.
     *
     * @param dni DNI que identifica a la persona.
     * @return {@code true} si se desactiva; {@code false} si no hay un socio activo con ese DNI o tiene préstamos pendientes.
     */
    public boolean bajaSocio(String dni) {
        // Centraliza el requisito de sesión antes de permitir cambios en préstamos o bajas.
        if (!haySesionActiva()) {
            throw new IllegalStateException("Debe iniciar sesión para dar de baja un socio.");
        }

        Socio socio = gestorSocios.buscarPorDni(dni);

        // Impide desactivar a un socio mientras deba devolver algún libro.
        if (socio == null || gestorPrestamos.tienePrestamosPendientes(socio)) {
            return false;
        }

        return gestorSocios.bajaSocio(dni);
    }

    /**
     * Da de baja a un libro sin préstamos activos, con una sesión iniciada.
     *
     * @param isbn ISBN que identifica al libro.
     * @return {@code true} si se desactiva; {@code false} si el libro no puede darse de baja.
     */
    public boolean bajaLibro(String isbn) {
        // Centraliza el requisito de sesión antes de permitir cambios en préstamos o bajas.
        if (!haySesionActiva()) {
            throw new IllegalStateException("Debe iniciar sesión para dar de baja un libro.");
        }

        Libro libro = gestorLibros.buscarPorIsbn(isbn);

        // Impide dar de baja un libro mientras tenga préstamos activos.
        if (libro == null || gestorPrestamos.tienePrestamosActivos(libro)) {
            return false;
        }

        return gestorLibros.bajaLibro(isbn);
    }

    /**
     * Busca al socio y al libro registrados y solicita el préstamo con una sesión iniciada.
     *
     * @param dni DNI que identifica a la persona.
     * @param isbn ISBN que identifica al libro.
     * @param fecha fecha en la que se inicia el préstamo.
     * @return {@code true} si se registra; {@code false} si las entidades no existen, están inactivas o no hay disponibilidad.
     */
    public boolean registrarPrestamo(String dni, String isbn, LocalDate fecha) {
        // Centraliza el requisito de sesión antes de permitir cambios en préstamos o bajas.
        if (!haySesionActiva()) {
            throw new IllegalStateException("Debe iniciar sesión para registrar un préstamo.");
        }

        // Utiliza las instancias del registro para mantener coherentes las referencias del préstamo.
        Socio socio = gestorSocios.buscarPorDni(dni);
        Libro libro = gestorLibros.buscarPorIsbn(isbn);

        if (socio == null || libro == null || !socio.isActivo() || !libro.isActivo()) {
            return false;
        }

        return gestorPrestamos.registrarPrestamo(socio, libro, fecha);
    }

    /**
     * Solicita la devolución de un préstamo con una sesión iniciada.
     *
     * @param idPrestamo identificador del préstamo.
     * @param fecha fecha en la que se devuelve el libro.
     * @return {@code true} si se registra y se restituye el ejemplar; {@code false} si el préstamo, la fecha o el stock no lo permiten.
     */
    public boolean registrarDevolucion(int idPrestamo, LocalDate fecha) {
        // Centraliza el requisito de sesión antes de permitir cambios en préstamos o bajas.
        if (!haySesionActiva()) {
            throw new IllegalStateException("Debe iniciar sesión para registrar una devolución.");
        }

        return gestorPrestamos.registrarDevolucion(idPrestamo, fecha);
    }

    /**
     * Valida y serializa la biblioteca en un archivo temporal antes de reemplazar el archivo de
     * datos.
     */
    @Override
    public void guardarDatos() {
        // Protege el archivo existente frente a un guardado posterior a una carga fallida.
        if (cargaFallida) {
            throw new IllegalStateException("No se pueden sobrescribir los datos porque la carga anterior falló.");
        }

        // Rechaza estados inconsistentes antes de crear o reemplazar archivos de datos.
        validarIntegridad();
        Path destino = Path.of(RUTA_DATOS).toAbsolutePath();
        Path temporal = null;

        try {
            Files.createDirectories(destino.getParent());
            // Escribe primero en el mismo directorio para conservar el archivo anterior hasta terminar la serialización.
            temporal = Files.createTempFile(destino.getParent(), "biblioteca-", ".tmp");

            try (ObjectOutputStream salida = new ObjectOutputStream(
                    new BufferedOutputStream(Files.newOutputStream(temporal)))) {
                salida.writeObject(this);
            }

            try {
                // Intenta publicar el archivo completo en una sola operación para reducir escrituras parciales.
                Files.move(temporal, destino, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException ex) {
                // Usa el reemplazo normal cuando el sistema de archivos no admite el movimiento atómico.
                Files.move(temporal, destino, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException ex) {
            throw new UncheckedIOException("No se pudieron guardar los datos de la biblioteca.", ex);
        } finally {
            if (temporal != null) {
                try {
                    Files.deleteIfExists(temporal);
                } catch (IOException ex) {
                    // Pospone la limpieza del temporal si no se puede eliminar inmediatamente.
                    temporal.toFile().deleteOnExit();
                }
            }
        }
    }

    /**
     * Carga y valida la biblioteca persistida, restaura sus gestores y deja la sesión cerrada.
     */
    @Override
    public void cargarDatos() {
        // Bloquea futuros guardados hasta completar una carga válida o comprobar que no existe un archivo previo.
        cargaFallida = true;
        Path archivo = Path.of(RUTA_DATOS);

        // Permite comenzar sin datos persistidos y exige un nuevo inicio de sesión.
        if (Files.notExists(archivo)) {
            cerrarSesion();
            cargaFallida = false;
            return;
        }

        SistemaBiblioteca cargado;

        try (ObjectInputStream entrada = new ObjectInputStream(
                new BufferedInputStream(Files.newInputStream(archivo)))) {
            Object datos = entrada.readObject();

            if (!(datos instanceof SistemaBiblioteca)) {
                throw new IllegalStateException("El archivo no contiene los datos de una biblioteca.");
            }

            cargado = (SistemaBiblioteca) datos;
            // Valida el objeto leído antes de reemplazar los gestores que están en uso.
            cargado.validarIntegridad();
        } catch (IOException ex) {
            throw new UncheckedIOException("No se pudieron cargar los datos de la biblioteca.", ex);
        } catch (ClassNotFoundException ex) {
            throw new IllegalStateException("El archivo contiene clases que no son compatibles con la biblioteca.", ex);
        }

        // Recupera el estado compartido y la secuencia de préstamos sin restaurar una sesión autenticada.
        cargado.restaurarContadorPrestamos();
        gestorUsuarios = cargado.gestorUsuarios;
        gestorSocios = cargado.gestorSocios;
        gestorPrestamos = cargado.gestorPrestamos;
        gestorLibros = cargado.gestorLibros;
        gestorCategorias = cargado.gestorCategorias;
        cerrarSesion();
        cargaFallida = false;
    }

    /**
     * Comprueba la coherencia de gestores, identificadores, referencias y stock; rechaza estados
     * inconsistentes.
     */
    private void validarIntegridad() {
        if (gestorUsuarios == null || gestorSocios == null || gestorPrestamos == null
                || gestorLibros == null || gestorCategorias == null) {
            throw new IllegalStateException("Faltan gestores en los datos de la biblioteca.");
        }
        // Exige la misma instancia de catálogo para evitar que las categorías consulten datos ajenos.
        if (gestorCategorias.getGestorLibros() != gestorLibros) {
            throw new IllegalStateException("El gestor de categorías debe compartir el mismo gestor de libros.");
        }

        // Los conjuntos detectan identificadores repetidos mientras se comprueban los registros.
        Set<String> dniUsuarios = new HashSet<>();
        Set<String> nombresUsuarios = new HashSet<>();

        for (Usuario usuario : gestorUsuarios.getUsuarios()) {
            if (usuario == null || usuario.getDni() == null || usuario.getDni().isBlank()
                    || usuario.getNombreUsuario() == null || usuario.getNombreUsuario().isBlank()
                    || usuario.getContrasena() == null || usuario.getContrasena().isBlank()
                    || !dniUsuarios.add(usuario.getDni()) || !nombresUsuarios.add(usuario.getNombreUsuario())) {
                throw new IllegalStateException("Hay usuarios inválidos o duplicados.");
            }
        }

        Map<String, Socio> sociosPorDni = new HashMap<>();
        Set<Integer> numerosSocios = new HashSet<>();

        for (Socio socio : gestorSocios.getSocios()) {
            if (socio == null || socio.getDni() == null || socio.getDni().isBlank() || socio.getNroSocio() <= 0
                    || sociosPorDni.putIfAbsent(socio.getDni(), socio) != null
                    || !numerosSocios.add(socio.getNroSocio())) {
                throw new IllegalStateException("Hay socios con datos inválidos, DNI o números duplicados.");
            }
        }

        Map<Integer, Categoria> categoriasPorId = new HashMap<>();
        // Aplica la misma comparación sin mayúsculas que utiliza el alta de categorías.
        Set<String> nombresCategorias = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

        for (Categoria categoria : gestorCategorias.getCategorias()) {
            if (categoria == null || categoria.getIdCategoria() <= 0
                    || categoria.getNombre() == null || categoria.getNombre().isBlank()
                    || categoriasPorId.putIfAbsent(categoria.getIdCategoria(), categoria) != null
                    || !nombresCategorias.add(categoria.getNombre())) {
                throw new IllegalStateException("Hay categorías inválidas o duplicadas.");
            }
        }

        Map<String, Libro> librosPorIsbn = new HashMap<>();

        // Comprueba identidad de referencias, no solo igualdad de identificadores, para preservar el grafo serializado.
        for (Libro libro : gestorLibros.getLibros()) {
            if (libro == null || libro.getIsbn() == null || libro.getIsbn().isBlank()
                    || libro.getCategoria() == null
                    || categoriasPorId.get(libro.getCategoria().getIdCategoria()) != libro.getCategoria()
                    || libro.getCantidadEjemplares() < 0 || libro.getEjemplaresDisponibles() < 0
                    || libro.getEjemplaresDisponibles() > libro.getCantidadEjemplares()
                    || librosPorIsbn.putIfAbsent(libro.getIsbn(), libro) != null) {
                throw new IllegalStateException("Hay libros inválidos o con categorías inexistentes.");
            }
        }

        Set<Integer> identificadores = new HashSet<>();
        Map<String, Integer> activosPorLibro = new HashMap<>();

        // Rechaza referencias externas y cuenta los préstamos activos para contrastarlos con el inventario.
        for (Prestamo prestamo : gestorPrestamos.getPrestamos()) {
            if (prestamo == null || prestamo.getIdPrestamo() <= 0
                    || !identificadores.add(prestamo.getIdPrestamo()) || prestamo.getSocio() == null
                    || prestamo.getLibro() == null || prestamo.getFechaPrestamo() == null
                    || sociosPorDni.get(prestamo.getSocio().getDni()) != prestamo.getSocio()
                    || librosPorIsbn.get(prestamo.getLibro().getIsbn()) != prestamo.getLibro()
                    || (prestamo.isDevuelto() && prestamo.getFechaDevolucion().isBefore(prestamo.getFechaPrestamo()))) {
                throw new IllegalStateException("Hay préstamos inválidos o referencias que no pertenecen a la biblioteca.");
            }

            if (prestamo.estaActivo()) {
                if (!prestamo.getSocio().isActivo() || !prestamo.getLibro().isActivo()) {
                    throw new IllegalStateException("Un préstamo activo no puede pertenecer a un socio o libro inactivo.");
                }

                activosPorLibro.merge(prestamo.getLibro().getIsbn(), 1, Integer::sum);
            }
        }

        // Cada ejemplar no disponible debe corresponder exactamente a un préstamo activo.
        for (Libro libro : librosPorIsbn.values()) {
            int prestados = libro.getCantidadEjemplares() - libro.getEjemplaresDisponibles();

            if (prestados != activosPorLibro.getOrDefault(libro.getIsbn(), 0)) {
                throw new IllegalStateException("El stock no coincide con los préstamos activos del libro " + libro.getIsbn() + ".");
            }
        }
    }

    /**
     * Actualiza el contador de préstamos a partir del mayor identificador guardado.
     */
    private void restaurarContadorPrestamos() {
        int maximo = 0;

        // Busca el mayor identificador para que los nuevos préstamos no reutilicen los ya guardados.
        for (Prestamo prestamo : gestorPrestamos.getPrestamos()) {
            maximo = Math.max(maximo, prestamo.getIdPrestamo());
        }

        Prestamo.actualizarContadorPrestamos(maximo);
    }
}
