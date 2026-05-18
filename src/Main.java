import repositorio.RepositorioCentral;
import servicos.AproveitamentoService;
import servicos.GrupoService;
import servicos.OportunidadeService;
import servicos.UsuarioService;
import interfaceterminal.MenuTerminal;

public class Main {
    public static void main(String[] args) {
        // Instancia o repositório central
        RepositorioCentral repositorio = new RepositorioCentral();

        // Instancia os serviços passando o repositório
        UsuarioService usuarioService = new UsuarioService(repositorio);
        OportunidadeService oportunidadeService = new OportunidadeService(repositorio);
        AproveitamentoService aproveitamentoService = new AproveitamentoService(repositorio);
        GrupoService grupoService = new GrupoService(repositorio);

        // Injeta os serviços no menu
        MenuTerminal menu = new MenuTerminal(usuarioService, oportunidadeService, aproveitamentoService, grupoService);
        menu.iniciar();
    }
}