package consola;

import gestion.SistemaBiblioteca;
import java.io.UncheckedIOException;
import modelo.Usuario;

/**
 * Presenta el acceso, el registro de usuarios y la recuperación de sus datos de acceso.
 */
public class MenuUsuarios {

    private final SistemaBiblioteca sistema;
    private final EntradaConsola entrada;

    /**
     * Prepara el menú con el sistema y el lector de consola recibidos.
     *
     * @param sistema sistema de biblioteca compartido por los menús.
     * @param entrada lector utilizado para solicitar y validar las respuestas de consola.
     */
    public MenuUsuarios(SistemaBiblioteca sistema, EntradaConsola entrada) {
        if (sistema == null || entrada == null) {
            throw new IllegalArgumentException("El sistema y la entrada de consola son obligatorios.");
        }

        this.sistema = sistema;
        this.entrada = entrada;
    }

    /**
     * Atiende las opciones de acceso hasta iniciar una sesión, elegir salir o finalizar la entrada.
     *
     * @return {@code true} si queda una sesión activa; {@code false} si se sale o no se puede continuar la lectura.
     */
    public boolean mostrarLogueo() {
        // Mantiene las opciones de acceso disponibles hasta autenticar al usuario o elegir salir.
        while (!sistema.haySesionActiva()) {
            System.out.println("\nACCESO A LA BIBLIOTECA");
            System.out.println("1. Iniciar sesión");
            System.out.println("2. Registrar usuario");
            System.out.println("3. Recuperar contraseña");
            System.out.println("4. Recuperar nombre de usuario");
            System.out.println("0. Salir");

            try {
                int opcion = entrada.leerEnteroEnRango("Seleccione una opción: ", 0, 4);

                switch (opcion) {
                    case 1:
                        iniciarSesion();
                        break;
                    case 2:
                        registrarUsuario();
                        break;
                    case 3:
                        recuperarContrasena();
                        break;
                    case 4:
                        recuperarUsuario();
                        break;
                    case 0:
                        return false;
                    default:
                        break;
                }
            } catch (IllegalArgumentException ex) {
                System.out.println("Error: " + ex.getMessage());
            } catch (UncheckedIOException ex) {
                // Aclara que un fallo de persistencia no revierte los cambios que ya se hicieron en memoria.
                System.out.println("No se pudieron guardar los cambios. Los cambios siguen en memoria.");
                System.out.println("Error: " + ex.getMessage());
            } catch (IllegalStateException ex) {
                System.out.println("No se pudo continuar: " + ex.getMessage());
                return false;
            }
        }

        return true;
    }

    /**
     * Solicita las credenciales y comunica el resultado del inicio de sesión.
     */
    private void iniciarSesion() {
        String nombreUsuario = entrada.leerCadena("Nombre de usuario: ");
        String contrasena = entrada.leerContrasena("Contraseña: ");

        if (sistema.iniciarSesion(nombreUsuario, contrasena)) {
            System.out.println("Sesión iniciada correctamente.");
        } else {
            System.out.println("El nombre de usuario o la contraseña son incorrectos.");
        }
    }

    /**
     * Solicita los datos y la confirmación de contraseña, registra el usuario y guarda los datos si
     * tiene éxito.
     */
    private void registrarUsuario() {
        String nombre = entrada.leerCadena("Nombre: ");
        String apellido = entrada.leerCadena("Apellido: ");
        String dni = entrada.leerCadena("DNI: ");
        String email = entrada.leerCadena("Email: ");
        String telefono = entrada.leerCadena("Teléfono: ");
        String nombreUsuario = entrada.leerCadena("Nombre de usuario: ");
        String contrasena = entrada.leerContrasena("Contraseña: ");
        String confirmacion = entrada.leerContrasena("Repita la contraseña: ");

        // Evita guardar una contraseña diferente de la confirmada por el usuario.
        if (!contrasena.equals(confirmacion)) {
            System.out.println("Las contraseñas no coinciden.");
            return;
        }

        Usuario usuario = new Usuario(nombre, apellido, dni, email, telefono, nombreUsuario, contrasena);

        if (sistema.getGestorUsuarios().registrarUsuario(usuario)) {
            // Guarda el cambio de credenciales antes de comunicar que la operación finalizó correctamente.
            sistema.guardarDatos();
            System.out.println("Usuario registrado. Ya puede iniciar sesión.");
        } else {
            System.out.println("Ya existe un usuario con ese nombre de usuario o DNI.");
        }
    }

    /**
     * Solicita los datos de identificación y una nueva contraseña confirmada; guarda el cambio si
     * tiene éxito.
     */
    private void recuperarContrasena() {
        String nombreUsuario = entrada.leerCadena("Nombre de usuario: ");
        String dni = entrada.leerCadena("DNI: ");
        String contrasena = entrada.leerContrasena("Nueva contraseña: ");
        String confirmacion = entrada.leerContrasena("Repita la nueva contraseña: ");

        // Evita guardar una contraseña diferente de la confirmada por el usuario.
        if (!contrasena.equals(confirmacion)) {
            System.out.println("Las contraseñas no coinciden.");
            return;
        }

        if (sistema.getGestorUsuarios().recuperarContrasena(nombreUsuario, dni, contrasena)) {
            // Guarda el cambio de credenciales antes de comunicar que la operación finalizó correctamente.
            sistema.guardarDatos();
            System.out.println("La contraseña fue actualizada.");
        } else {
            System.out.println("El nombre de usuario y el DNI no coinciden con un usuario registrado.");
        }
    }

    /**
     * Solicita el DNI y el correo electrónico e informa el nombre de usuario si coinciden.
     */
    private void recuperarUsuario() {
        String dni = entrada.leerCadena("DNI: ");
        String email = entrada.leerCadena("Email: ");
        String nombreUsuario = sistema.getGestorUsuarios().recuperarUsuario(dni, email);

        if (nombreUsuario == null) {
            System.out.println("El DNI y el email no coinciden con un usuario registrado.");
        } else {
            System.out.println("Su nombre de usuario es: " + nombreUsuario);
        }
    }
}
