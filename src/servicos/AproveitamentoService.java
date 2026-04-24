package servicos;

import entidades.SolicitacaoAproveitamento;
import repositorio.RepositorioCentral;

import java.util.List;

public class AproveitamentoService {

    private final RepositorioCentral repositorio;

    public AproveitamentoService(RepositorioCentral repositorio) {
        this.repositorio = repositorio;
    }

    public void criarSolicitacao(SolicitacaoAproveitamento s) {
        repositorio.salvarSolicitacao(s);
    }

    public List<SolicitacaoAproveitamento> listarTodas() {
        return repositorio.findAllSolicitacoes();
    }

    public List<SolicitacaoAproveitamento> listarPendentes() {
        return repositorio.findSolicitacoesPendentes();
    }

    public SolicitacaoAproveitamento buscarPorId(int id) {
        return repositorio.findSolicitacaoById(id);
    }

    public void avaliarSolicitacao(SolicitacaoAproveitamento s, boolean aprovado, String parecer) {
        if (!s.getStatus().equals("PENDENTE"))
            return;
        s.avaliarSolicitacao(aprovado, parecer);
        if (aprovado) {
            s.getSolicitante().adicionarHoras(s.getCargaHorariaPleitada());
        }
    }
}