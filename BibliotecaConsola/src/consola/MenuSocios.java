package consola;

import gestion.GestorSocios;
import gestion.SistemaBiblioteca;
import java.util.List;
import modelo.Socio;

/**
 * Menú de consola para administrar los socios: alta, baja,
 * modificación y búsqueda (RF3).
 */
public class MenuSocios {

    private final SistemaBiblioteca sistema;
    private final EntradaConsola entrada;

    public MenuSocios(SistemaBiblioteca sistema, EntradaConsola entrada) {
        this.sistema = sistema;
        this.entrada = entrada;
    }

    // Muestra el menú y repite hasta que el usuario elige volver
    public void mostrar() {
        int opcion;

        do {
            System.out.println("\n--- SOCIOS ---");
            System.out.println("1. Alta de socio");
            System.out.println("2. Baja de socio");
            System.out.println("3. Modificar socio");
            System.out.println("4. Buscar socios");
            System.out.println("0. Volver");
            opcion = entrada.leerEnteroEnRango("Opción: ", 0, 4);

            switch (opcion) {
                case 1:
                    altaSocio();
                    break;
                case 2:
                    bajaSocio();
                    break;
                case 3:
                    modificarSocio();
                    break;
                case 4:
                    buscarSocios();
                    break;
            }
        } while (opcion != 0);
    }

    // Pide los datos y da de alta un socio con el siguiente número libre
    private void altaSocio() {
        GestorSocios gestorSocios = sistema.getGestorSocios();

        System.out.println("\nAlta de socio");
        String nombre = entrada.leerCadena("Nombre: ");
        String apellido = entrada.leerCadena("Apellido: ");
        String dni = entrada.leerCadena("DNI: ");
        String email = entrada.leerCadena("Email: ");
        String telefono = entrada.leerCadena("Teléfono: ");
        int nroSocio = siguienteNroSocio();

        try {
            Socio socio = new Socio(nombre, apellido, dni, email, telefono, nroSocio);

            if (gestorSocios.altaSocio(socio)) {
                System.out.println("Socio registrado con el número " + nroSocio + ".");
            } else {
                System.out.println("Ya existe un socio con ese DNI.");
            }
        } catch (IllegalArgumentException ex) {
            // el constructor rechazó algún dato, muestro el motivo
            System.out.println(ex.getMessage());
        }
    }

    // Pide el DNI y da de baja al socio si no tiene préstamos pendientes
    private void bajaSocio() {
        System.out.println("\nBaja de socio");
        String dni = entrada.leerCadena("DNI del socio: ");
        Socio socio = sistema.getGestorSocios().buscarPorDni(dni);

        if (socio == null) {
            System.out.println("No existe un socio activo con ese DNI.");
            return;
        }

        System.out.println(socio.mostrarDatos());

        if (sistema.getGestorPrestamos().tienePrestamosPendientes(socio)) {
            System.out.println("No se puede dar de baja: el socio tiene préstamos pendientes.");
            return;
        }

        if (!entrada.confirmar("¿Confirma la baja?")) {
            System.out.println("Operación cancelada.");
            return;
        }

        try {
            if (sistema.bajaSocio(dni)) {
                System.out.println("Socio dado de baja.");
            } else {
                System.out.println("No se pudo dar de baja al socio.");
            }
        } catch (IllegalStateException ex) {
            // el sistema avisa si no hay una sesión iniciada
            System.out.println(ex.getMessage());
        }
    }

    // Pide el DNI y permite cambiar los datos del socio, menos el DNI y el número
    private void modificarSocio() {
        System.out.println("\nModificar socio");
        String dni = entrada.leerCadena("DNI del socio: ");
        Socio socio = sistema.getGestorSocios().buscarPorDni(dni);

        if (socio == null) {
            System.out.println("No existe un socio activo con ese DNI.");
            return;
        }

        System.out.println(socio.mostrarDatos());

        // arranco con los datos actuales y cambio solo los que el usuario elija
        String nombre = socio.getNombre();
        String apellido = socio.getApellido();
        String email = socio.getEmail();
        String telefono = socio.getTelefono();

        if (entrada.confirmar("¿Modificar el nombre?")) {
            nombre = entrada.leerCadena("Nuevo nombre: ");
        }
        if (entrada.confirmar("¿Modificar el apellido?")) {
            apellido = entrada.leerCadena("Nuevo apellido: ");
        }
        if (entrada.confirmar("¿Modificar el email?")) {
            email = entrada.leerCadena("Nuevo email: ");
        }
        if (entrada.confirmar("¿Modificar el teléfono?")) {
            telefono = entrada.leerCadena("Nuevo teléfono: ");
        }

        try {
            Socio modificado = new Socio(nombre, apellido, socio.getDni(), email, telefono,
                    socio.getNroSocio());

            if (sistema.getGestorSocios().modificarSocio(modificado)) {
                System.out.println("Socio modificado.");
            } else {
                System.out.println("No se pudo modificar el socio.");
            }
        } catch (IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
        }
    }

    // Permite buscar un socio por DNI o varios por nombre o apellido
    private void buscarSocios() {
        System.out.println("\nBuscar socios");
        System.out.println("1. Por DNI");
        System.out.println("2. Por nombre o apellido");
        int opcion = entrada.leerEnteroEnRango("Opción: ", 1, 2);

        if (opcion == 1) {
            String dni = entrada.leerCadena("DNI: ");
            Socio socio = sistema.getGestorSocios().buscarPorDni(dni);

            if (socio == null) {
                System.out.println("No existe un socio activo con ese DNI.");
            } else {
                System.out.println(socio.mostrarDatos());
            }
        } else {
            String nombre = entrada.leerCadena("Nombre o apellido: ");
            List<Socio> encontrados = sistema.getGestorSocios().buscarPorNombre(nombre);

            if (encontrados.isEmpty()) {
                System.out.println("No se encontraron socios.");
            }
            for (Socio socio : encontrados) {
                System.out.println(socio.mostrarDatos());
                System.out.println();
            }
        }
    }

    // Calcula el próximo número de socio: el mayor que haya más uno
    private int siguienteNroSocio() {
        int mayor = 0;

        for (Socio socio : sistema.getGestorSocios().getSocios()) {
            if (socio.getNroSocio() > mayor) {
                mayor = socio.getNroSocio();
            }
        }

        return mayor + 1;
    }

}