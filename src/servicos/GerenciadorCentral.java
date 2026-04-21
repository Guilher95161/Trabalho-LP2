package servicos;

import entidades.*;
import repositorio.RepositorioCentral;

import java.util.List;

//MenuTerminal  →  GerenciadorCentral  →  XxxService  →  RepositorioCentral

public class GerenciadorCentral {

    private final UsuarioService usuarioService;
    private final OportunidadeService oportunidadeService;
    private final AproveitamentoService aproveitamentoService;
    private final GrupoService grupoService;

    public GerenciadorCentral() {
        RepositorioCentral repositorio = new RepositorioCentral();

        this.usuarioService       = new UsuarioService(repositorio);
        this.oportunidadeService  = new OportunidadeService(repositorio);
        this.aproveitamentoService = new AproveitamentoService(repositorio);
        this.grupoService         = new GrupoService(repositorio);
    }

    // Delegações → UsuarioService

    public boolean cadastrarUsuario(Usuario u) {
        return usuarioService.cadastrarUsuario(u);
    }

    public Usuario buscarPorEmailSenha(String email, String senha) {
        return usuarioService.autenticar(email, senha);
    }

    public List<Usuario> listarUsuarios() {
        return usuarioService.listarTodos();
    }

    public List<Discente> listarDiscentes() {
        return usuarioService.listarDiscentes();
    }

    public List<Docente> listarDocentes() {
        return usuarioService.listarDocentes();
    }

    public Docente buscarDocentePorId(int idx) {
        return usuarioService.buscarDocentePorIndice(idx);
    }

    public Discente buscarDiscentePorId(int idx) {
        return usuarioService.buscarDiscentePorIndice(idx);
    }

    // Delegações → OportunidadeService

    public void criarOportunidade(Oportunidade o) {
        oportunidadeService.criarOportunidade(o);
    }

    public List<Oportunidade> listarOportunidades() {
        return oportunidadeService.listarTodas();
    }

    public Oportunidade buscarOportunidadePorId(int id) {
        return oportunidadeService.buscarPorId(id);
    }

    public void inscreverDiscente(int oportunidadeId, Discente d) {
        oportunidadeService.inscreverDiscente(oportunidadeId, d);
    }

    public void cancelarInscricao(int oportunidadeId, Discente d) {
        oportunidadeService.cancelarInscricao(oportunidadeId, d);
    }

    public void encerrarOportunidade(int oportunidadeId) {
        oportunidadeService.encerrarOportunidade(oportunidadeId);
    }

    // Delegações → AproveitamentoService

    public void criarSolicitacao(SolicitacaoAproveitamento s) {
        aproveitamentoService.criarSolicitacao(s);
    }

    public List<SolicitacaoAproveitamento> listarSolicitacoes() {
        return aproveitamentoService.listarTodas();
    }

    public List<SolicitacaoAproveitamento> listarSolicitacoesPendentes() {
        return aproveitamentoService.listarPendentes();
    }

    public SolicitacaoAproveitamento buscarSolicitacaoPorId(int id) {
        return aproveitamentoService.buscarPorId(id);
    }

    public void avaliarSolicitacao(SolicitacaoAproveitamento s, boolean aprovado, String parecer) {
        aproveitamentoService.avaliarSolicitacao(s, aprovado, parecer);
    }

    // Delegações → GrupoService

    public void criarGrupo(GrupoEstudantil g) {
        grupoService.criarGrupo(g);
    }

    public List<GrupoEstudantil> listarGrupos() {
        return grupoService.listarTodos();
    }

    public GrupoEstudantil buscarGrupoPorId(int id) {
        return grupoService.buscarPorId(id);
    }
}