import consola.EntradaConsola;
import consola.MenuPrincipal;
import gestion.SistemaBiblioteca;

/**
 * Inicia la aplicación de consola y coordina la carga, el guardado y el cierre de sus recursos.
 */
public class Main {
    /**
     * Carga la biblioteca, ejecuta el menú principal y realiza el guardado y cierre de recursos al
     * terminar.
     *
     * @param args argumentos de la línea de comandos; esta aplicación no los utiliza.
     */
    public static void main(String[] args) {
        SistemaBiblioteca sistema = new SistemaBiblioteca();
        EntradaConsola entrada = new EntradaConsola();
        boolean iniciado = false;
        try {
            // Solo habilita el guardado final si la carga inicial terminó sin errores.
            sistema.iniciar();
            iniciado = true;
            new MenuPrincipal(sistema, entrada).mostrar();
        } catch (RuntimeException ex) {
            System.out.println("No se pudo continuar: " + ex.getMessage());
        } finally {
            // Evita sobrescribir los datos existentes si la inicialización de la biblioteca falló.
            if (iniciado) {
                try {
                    sistema.finalizar();
                } catch (RuntimeException ex) {
                    System.out.println("No se pudieron guardar los datos: " + ex.getMessage());
                }
            }
            // Libera el lector incluso si la ejecución del menú o el guardado no pudieron completarse.
            entrada.cerrar();
        }
    }
}
