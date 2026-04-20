package servicos;

import entidades.SolicitacaoAproveitamento;
import repositorio.RepositorioCentral;

import java.util.List;

/**
 * Contém todas as regras de negócio relacionadas ao aproveitamento de horas complementares.
 */
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

    /**
     * Busca uma solicitação pelo seu ID.
     *
     * @return a {@link SolicitacaoAproveitamento} encontrada, ou {@code null} se não existir.
     */
    public SolicitacaoAproveitamento buscarPorId(int id) {
        return repositorio.findSolicitacaoById(id).orElse(null);
    }

    /**
     * Avalia uma solicitação de aproveitamento.
     * Regra de negócio: se aprovada, as horas pleiteadas são adicionadas ao histórico do solicitante.
     *
     * @param s        a solicitação a ser avaliada.
     * @param aprovado {@code true} para deferir, {@code false} para indeferir.
     * @param parecer  justificativa do avaliador.
     */
    public void avaliarSolicitacao(SolicitacaoAproveitamento s, boolean aprovado, String parecer) {
        s.avaliarSolicitacao(aprovado, parecer);
        if (aprovado) {
            s.getSolicitante().adicionarHoras(s.getCargaHorariaPleitada());
        }
    }
}