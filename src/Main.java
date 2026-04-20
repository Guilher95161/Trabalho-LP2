import servicos.GerenciadorCentral;
import interfaceterminal.MenuTerminal;

public class Main {
    public static void main(String[] args) {
        GerenciadorCentral gerenciador = new GerenciadorCentral();
        MenuTerminal menu = new MenuTerminal(gerenciador);
        menu.iniciar();
    }
}