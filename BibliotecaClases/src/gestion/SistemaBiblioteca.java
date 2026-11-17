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

    public SistemaBiblioteca() {
        gestorUsuarios = new GestorUsuarios();
        gestorSocios = new GestorSocios();
        gestorPrestamos = new GestorPrestamos();
        gestorLibros = new GestorLibros();
        gestorCategorias = new GestorCategorias(gestorLibros);
    }

    public void iniciar() {
        cargarDatos();
    }

    public void finalizar() {
        cerrarSesion();
    }

    public GestorUsuarios getGestorUsuarios() {
        return gestorUsuarios;
    }

    public GestorSocios getGestorSocios() {
        return gestorSocios;
    }

    public GestorPrestamos getGestorPrestamos() {
        return gestorPrestamos;
    }

    public GestorLibros getGestorLibros() {
        return gestorLibros;
    }

    public GestorCategorias getGestorCategorias() {
        return gestorCategorias;
    }

    public Usuario getUsuarioLogueado() {
        return usuarioLogueado;
    }

    public boolean iniciarSesion(String nombreUsuario, String contrasena) {
        usuarioLogueado = gestorUsuarios.iniciarSesion(nombreUsuario, contrasena);
        return haySesionActiva();
    }

    public void cerrarSesion() {
        usuarioLogueado = null;
    }

    public boolean haySesionActiva() {
        return usuarioLogueado != null;
    }

    public boolean bajaSocio(String dni) {
        if (!haySesionActiva()) {
            throw new IllegalStateException("Debe iniciar sesión para dar de baja un socio.");
        }

        Socio socio = gestorSocios.buscarPorDni(dni);

        if (socio == null) {
            return false;
        }

        return gestorSocios.bajaSocio(dni);
    }

    public boolean bajaLibro(String isbn) {
        if (!haySesionActiva()) {
            throw new IllegalStateException("Debe iniciar sesión para dar de baja un libro.");
        }

        Libro libro = gestorLibros.buscarPorIsbn(isbn);

        if (libro == null || gestorPrestamos.tienePrestamosActivos(libro)) {
            return false;
        }

        return gestorLibros.bajaLibro(isbn);
    }

    public boolean registrarPrestamo(String dni, String isbn, LocalDate fecha) {
        Socio socio = gestorSocios.buscarPorDni(dni);
        Libro libro = gestorLibros.buscarPorIsbn(isbn);

        if (socio == null || libro == null || !socio.isActivo() || !libro.isActivo()) {
            return false;
        }

        return gestorPrestamos.registrarPrestamo(socio, libro, fecha);
    }

    public boolean registrarDevolucion(int idPrestamo, LocalDate fecha) {
        if (!haySesionActiva()) {
            throw new IllegalStateException("Debe iniciar sesión para registrar una devolución.");
        }

        return gestorPrestamos.registrarDevolucion(idPrestamo, fecha);
    }

    @Override
    public void guardarDatos() {
        if (cargaFallida) {
            throw new IllegalStateException("No se pueden sobrescribir los datos porque la carga anterior falló.");
        }

        validarIntegridad();
        Path destino = Path.of(RUTA_DATOS).toAbsolutePath();
        Path temporal = null;

        try {
            Files.createDirectories(destino.getParent());
            temporal = Files.createTempFile(destino.getParent(), "biblioteca-", ".tmp");

            try (ObjectOutputStream salida = new ObjectOutputStream(
                    new BufferedOutputStream(Files.newOutputStream(temporal)))) {
                salida.writeObject(this);
            }

            try {
                Files.move(temporal, destino, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException ex) {
                Files.move(temporal, destino, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException ex) {
            throw new UncheckedIOException("No se pudieron guardar los datos de la biblioteca.", ex);
        } finally {
            if (temporal != null) {
                try {
                    Files.deleteIfExists(temporal);
                } catch (IOException ex) {
                    temporal.toFile().deleteOnExit();
                }
            }
        }
    }

    @Override
    public void cargarDatos() {
        cargaFallida = true;
        Path archivo = Path.of(RUTA_DATOS);

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
            cargado.validarIntegridad();
        } catch (IOException ex) {
            throw new UncheckedIOException("No se pudieron cargar los datos de la biblioteca.", ex);
        } catch (ClassNotFoundException ex) {
            throw new IllegalStateException("El archivo contiene clases que no son compatibles con la biblioteca.", ex);
        }

        gestorUsuarios = cargado.gestorUsuarios;
        gestorSocios = new GestorSocios();

        for (Socio socio : cargado.gestorSocios.getSocios()) {
            Socio copia = new Socio(socio.getNombre(), socio.getApellido(), socio.getDni(),
                    socio.getEmail(), socio.getTelefono(), socio.getNroSocio());

            if (!socio.isActivo()) {
                copia.desactivar();
            }

            gestorSocios.altaSocio(copia);
        }

        gestorPrestamos = cargado.gestorPrestamos;
        gestorLibros = cargado.gestorLibros;
        gestorCategorias = cargado.gestorCategorias;
        cerrarSesion();
        cargaFallida = false;
    }

    private void validarIntegridad() {
        if (gestorUsuarios == null || gestorSocios == null || gestorPrestamos == null
                || gestorLibros == null || gestorCategorias == null) {
            throw new IllegalStateException("Faltan gestores en los datos de la biblioteca.");
        }
        if (gestorCategorias.getGestorLibros() != gestorLibros) {
            throw new IllegalStateException("El gestor de categorías debe compartir el mismo gestor de libros.");
        }

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

        for (Libro libro : librosPorIsbn.values()) {
            int prestados = libro.getCantidadEjemplares() - libro.getEjemplaresDisponibles();

            if (prestados != activosPorLibro.getOrDefault(libro.getIsbn(), 0)) {
                throw new IllegalStateException("El stock no coincide con los préstamos activos del libro " + libro.getIsbn() + ".");
            }
        }
    }

    private void restaurarContadorPrestamos() {
        int maximo = 0;

        for (Prestamo prestamo : gestorPrestamos.getPrestamos()) {
            maximo = Math.max(maximo, prestamo.getIdPrestamo());
        }

        Prestamo.actualizarContadorPrestamos(maximo);
    }
}
