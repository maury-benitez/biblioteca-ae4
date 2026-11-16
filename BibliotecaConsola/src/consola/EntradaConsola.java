package consola;

import java.io.Console;
import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;

public class EntradaConsola {

    private final Scanner scanner;

    public EntradaConsola() {
        scanner = new Scanner(System.in);
    }

    public String leerCadena(String mensaje) {
        while (true) {
            System.out.print(mensaje);

            if (!scanner.hasNextLine()) {
                throw new IllegalStateException("Se terminó la entrada de consola.");
            }

            String cadena = scanner.nextLine().strip();

            if (!cadena.isEmpty()) {
                return cadena;
            }

            System.out.println("El texto no puede estar en blanco.");
        }
    }

    public int leerEntero(String mensaje) {
        while (true) {
            String texto = leerCadena(mensaje);

            try {
                return Integer.parseInt(texto);
            } catch (NumberFormatException ex) {
                System.out.println("Debe ingresar un número entero válido.");
            }
        }
    }

    public int leerEnteroEnRango(String mensaje, int minimo, int maximo) {
        if (minimo > maximo) {
            throw new IllegalArgumentException("El mínimo no puede ser mayor que el máximo.");
        }

        while (true) {
            int numero = leerEntero(mensaje);

            if (numero >= minimo && numero <= maximo) {
                return numero;
            }

            System.out.println("Ingrese un número entre " + minimo + " y " + maximo + ".");
        }
    }

    public String leerContrasena(String mensaje) {
        Console consola = System.console();

        if (consola == null) {
            throw new IllegalStateException(
                    "No hay una consola disponible para ocultar la contraseña. Ejecute la aplicación desde una terminal.");
        }

        while (true) {
            char[] caracteres = consola.readPassword("%s", mensaje);

            if (caracteres == null) {
                throw new IllegalStateException("Se terminó la entrada de consola.");
            }

            String contrasena = new String(caracteres);
            Arrays.fill(caracteres, '\0');

            if (!contrasena.isBlank()) {
                return contrasena;
            }

            System.out.println("La contraseña no puede estar en blanco.");
        }
    }

    public boolean confirmar(String mensaje) {
        while (true) {
            String respuesta = leerCadena(mensaje + " (s/n): ").toLowerCase(Locale.ROOT);

            if (respuesta.equals("s") || respuesta.equals("si") || respuesta.equals("sí")) {
                return true;
            }
            if (respuesta.equals("n") || respuesta.equals("no")) {
                return false;
            }

            System.out.println("Responda s para confirmar o n para cancelar.");
        }
    }

    public void cerrar() {
        scanner.close();
    }
}
