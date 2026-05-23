import repositorio.RepositorioCentral;
import servicos.AproveitamentoService;
import servicos.CursoService;
import servicos.GrupoService;
import servicos.OportunidadeService;
import servicos.UsuarioService;
import interfaceterminal.MenuTerminal;

public class Main {
    public static void main(String[] args) {
        RepositorioCentral repositorio = new RepositorioCentral();

        // CursoService antes do UsuarioService porque os discentes de teste
        // ja sao criados vinculados ao PPC padrao.
        CursoService cursoService = new CursoService(repositorio);
        UsuarioService usuarioService = new UsuarioService(repositorio, cursoService);
        OportunidadeService oportunidadeService = new OportunidadeService(repositorio);
        AproveitamentoService aproveitamentoService = new AproveitamentoService(repositorio);
        GrupoService grupoService = new GrupoService(repositorio);

        MenuTerminal menu = new MenuTerminal(usuarioService, oportunidadeService, aproveitamentoService, grupoService, cursoService);
        menu.iniciar();
    }
}