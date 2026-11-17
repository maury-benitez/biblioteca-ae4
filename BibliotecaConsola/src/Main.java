import consola.EntradaConsola;
import consola.MenuPrincipal;
import gestion.SistemaBiblioteca;

public class Main {
    public static void main(String[] args) {
        SistemaBiblioteca sistema = new SistemaBiblioteca();
        EntradaConsola entrada = new EntradaConsola();
        boolean iniciado = false;
        try {
            sistema.iniciar();
            iniciado = true;
            new MenuPrincipal(sistema, entrada).mostrar();
        } catch (RuntimeException ex) {
            System.out.println("No se pudo continuar: " + ex.getMessage());
        } finally {
            if (iniciado) {
                try {
                    sistema.finalizar();
                } catch (RuntimeException ex) {
                    System.out.println("No se pudieron guardar los datos: " + ex.getMessage());
                }
            }
            entrada.cerrar();
        }
    }
}
