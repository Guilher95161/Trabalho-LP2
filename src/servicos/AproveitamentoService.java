package servicos;

import entidades.Discente;
import entidades.SolicitacaoAproveitamento;
import entidades.enums.StatusSolicitacao;
import repositorio.RepositorioCentral;

import java.util.ArrayList;
import java.util.List;

public class AproveitamentoService {

    private final RepositorioCentral repositorio;

    public AproveitamentoService(RepositorioCentral repositorio) {
        this.repositorio = repositorio;
    }

    public void criarSolicitacao(SolicitacaoAproveitamento s) {
        s.getCertificado().marcarAproveitamentoSolicitado();
        repositorio.salvarSolicitacao(s);
    }

    public List<SolicitacaoAproveitamento> listarTodas() {
        return repositorio.findAllSolicitacoes();
    }

    public List<SolicitacaoAproveitamento> listarPendentes() {
        return repositorio.findSolicitacoesPendentes();
    }

    public List<SolicitacaoAproveitamento> listarPendentesSemDelegacao() {
        List<SolicitacaoAproveitamento> resultado = new ArrayList<>();
        for (SolicitacaoAproveitamento s : repositorio.findSolicitacoesPendentes()) {
            if (!s.isDelegadaParaComissao()) resultado.add(s);
        }
        return resultado;
    }

    public List<SolicitacaoAproveitamento> listarDelegadas() {
        List<SolicitacaoAproveitamento> resultado = new ArrayList<>();
        for (SolicitacaoAproveitamento s : repositorio.findSolicitacoesPendentes()) {
            if (s.isDelegadaParaComissao()) resultado.add(s);
        }
        return resultado;
    }

    public SolicitacaoAproveitamento buscarPorId(int id) {
        return repositorio.findSolicitacaoById(id);
    }

    public void avaliarSolicitacao(SolicitacaoAproveitamento s, boolean aprovado, String parecer) {
        if (s.getStatus() != StatusSolicitacao.PENDENTE) return;
        s.avaliarSolicitacao(aprovado, parecer);
        if (aprovado) {
            s.getSolicitante().adicionarHoras(s.getCertificado().getCargaHoraria());
        }
    }

    public boolean delegarParaComissao(int id) {
        SolicitacaoAproveitamento s = repositorio.findSolicitacaoById(id);
        if (s == null || s.getStatus() != StatusSolicitacao.PENDENTE || s.isDelegadaParaComissao()) return false;
        s.delegarParaComissao();
        return true;
    }

    public boolean cancelarSolicitacao(int id, Discente solicitante) {
        SolicitacaoAproveitamento s = repositorio.findSolicitacaoById(id);
        if (s == null || !s.getSolicitante().equals(solicitante)) return false;
        if (s.getStatus() != StatusSolicitacao.PENDENTE) return false;
        s.cancelar();
        s.getCertificado().liberarAproveitamento(); // certificado pode ser reenviado
        return true;
    }
}
