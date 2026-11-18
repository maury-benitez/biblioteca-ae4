package consola;

import java.io.Console;
import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;

/**
 * Centraliza la lectura y validación de entradas de consola, incluida la lectura oculta de
 * contraseñas.
 */
public class EntradaConsola {

    private final Scanner scanner;

    /**
     * Prepara el lector de la entrada estándar de la aplicación.
     */
    public EntradaConsola() {
        scanner = new Scanner(System.in);
    }

    /**
     * Solicita una línea de texto y repite la lectura mientras esté en blanco.
     *
     * @param mensaje texto que se muestra antes de solicitar la entrada.
     * @return el texto no vacío, sin espacios en los extremos.
     */
    public String leerCadena(String mensaje) {
        while (true) {
            System.out.print(mensaje);

            // Distingue el fin de la entrada de un dato vacío para no repetir la lectura indefinidamente.
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

    /**
     * Solicita un número entero y repite la lectura si el texto no puede convertirse.
     *
     * @param mensaje texto que se muestra antes de solicitar la entrada.
     * @return el número entero ingresado.
     */
    public int leerEntero(String mensaje) {
        while (true) {
            String texto = leerCadena(mensaje);

            // Permite corregir entradas no numéricas sin finalizar el menú que las solicitó.
            try {
                return Integer.parseInt(texto);
            } catch (NumberFormatException ex) {
                System.out.println("Debe ingresar un número entero válido.");
            }
        }
    }

    /**
     * Solicita un entero dentro de los límites inclusivos indicados.
     *
     * @param mensaje texto que se muestra antes de solicitar la entrada.
     * @param minimo menor valor permitido, incluido en el intervalo.
     * @param maximo mayor valor permitido, incluido en el intervalo.
     * @return el entero ingresado dentro del intervalo.
     */
    public int leerEnteroEnRango(String mensaje, int minimo, int maximo) {
        // Rechaza intervalos imposibles antes de comenzar a solicitar datos.
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

    /**
     * Lee una contraseña no vacía mediante una consola que oculta los caracteres ingresados.
     *
     * @param mensaje texto que se muestra antes de solicitar la entrada.
     * @return la contraseña ingresada, conservando sus espacios.
     */
    public String leerContrasena(String mensaje) {
        Console consola = System.console();

        // Exige una consola real para evitar leer una contraseña con eco visible.
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
            // Limpia el arreglo temporal de caracteres después de copiar la contraseña a la cadena utilizada por el sistema.
            Arrays.fill(caracteres, '\0');

            if (!contrasena.isBlank()) {
                return contrasena;
            }

            System.out.println("La contraseña no puede estar en blanco.");
        }
    }

    /**
     * Solicita una respuesta afirmativa o negativa y repite la lectura hasta reconocerla.
     *
     * @param mensaje texto que se muestra antes de solicitar la entrada.
     * @return {@code true} para «s», «si» o «sí»; {@code false} para «n» o «no», sin distinguir mayúsculas.
     */
    public boolean confirmar(String mensaje) {
        while (true) {
            // Normaliza la respuesta para aceptar variantes afirmativas y negativas sin distinguir mayúsculas.
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

    /**
     * Cierra el lector y la entrada estándar que utiliza.
     */
    public void cerrar() {
        scanner.close();
    }
}
