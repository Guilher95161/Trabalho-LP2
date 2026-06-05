package br.ufma.servico;

import br.ufma.model.entidades.Certificado;
import br.ufma.model.entidades.Discente;
import br.ufma.model.entidades.SolicitacaoAproveitamento;
import br.ufma.model.entidades.enums.StatusSolicitacao;
import br.ufma.excecao.EntidadeNaoEncontradaException;
import br.ufma.excecao.OperacaoInvalidaException;
import br.ufma.model.repositorio.RepositorioCentral;

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

    public List<Certificado> listarCertificadosAproveitados(Discente d) {
        List<Certificado> aproveitados = new ArrayList<>();
        for (SolicitacaoAproveitamento s : repositorio.findAllSolicitacoes()) {
            if (s.getSolicitante().equals(d) && s.getStatus() == StatusSolicitacao.DEFERIDA) {
                aproveitados.add(s.getCertificado());
            }
        }
        return aproveitados;
    }

    public void avaliarSolicitacao(SolicitacaoAproveitamento s, boolean aprovado, String parecer) {
        if (s.getStatus() != StatusSolicitacao.PENDENTE) {
            throw new OperacaoInvalidaException("Solicitacao nao pode ser avaliada: status atual e " + s.getStatus() + ".");
        }
        s.avaliarSolicitacao(aprovado, parecer);
        if (aprovado) {
            s.getSolicitante().adicionarHoras(s.getCertificado().getCargaHoraria());
        }
    }

    public void delegarParaComissao(int id) {
        SolicitacaoAproveitamento s = repositorio.findSolicitacaoById(id);
        if (s == null) throw new EntidadeNaoEncontradaException("Solicitacao #" + id + " nao encontrada.");
        if (s.getStatus() != StatusSolicitacao.PENDENTE) {
            throw new OperacaoInvalidaException("Solicitacao nao esta PENDENTE (status atual: " + s.getStatus() + ").");
        }
        if (s.isDelegadaParaComissao()) {
            throw new OperacaoInvalidaException("Solicitacao ja foi delegada para a Comissao.");
        }
        s.delegarParaComissao();
    }

    public boolean cancelarSolicitacao(int id, Discente solicitante) {
        SolicitacaoAproveitamento s = repositorio.findSolicitacaoById(id);
        if (s == null || !s.getSolicitante().equals(solicitante)) return false;
        if (s.getStatus() != StatusSolicitacao.PENDENTE) return false;
        s.cancelar();
        // libera o cert pra poder abrir nova solicitacao depois
        s.getCertificado().liberarAproveitamento();
        return true;
    }
}
