package br.ufma.extensao.service;

import br.ufma.extensao.model.Certificado;
import br.ufma.extensao.model.Discente;
import br.ufma.extensao.model.SolicitacaoAproveitamento;
import br.ufma.extensao.model.enums.StatusSolicitacao;
import br.ufma.extensao.repo.CertificadoRepository;
import br.ufma.extensao.repo.DiscenteRepository;
import br.ufma.extensao.repo.SolicitacaoAproveitamentoRepository;
import br.ufma.extensao.service.exceptions.EntidadeNaoEncontradaException;
import br.ufma.extensao.service.exceptions.OperacaoInvalidaException;
import br.ufma.extensao.service.exceptions.RegraNegocioRunTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class SolicitacaoAproveitamentoService {

    // Prazos definidos no regimento da extensao.
    public static final int PRAZO_AVALIACAO_DIAS = 10;
    public static final int PRAZO_REENVIO_DIAS = 5;

    @Autowired
    SolicitacaoAproveitamentoRepository repository;

    @Autowired
    DiscenteRepository discenteRepository;

    @Autowired
    CertificadoRepository certificadoRepository;

    @Transactional
    public SolicitacaoAproveitamento salvar(SolicitacaoAproveitamento solicitacao) {
        verificaSolicitacao(solicitacao);
        // uma nova solicitacao sempre nasce PENDENTE e marca o certificado
        solicitacao.setStatus(StatusSolicitacao.PENDENTE);
        solicitacao.setParecer("");
        solicitacao.setDelegadaParaComissao(false);
        solicitacao.setDataCriacao(LocalDate.now());
        solicitacao.setDataAvaliacao(null);
        marcarCertificado(solicitacao.getCertificado().getId(), true);
        return repository.save(solicitacao);
    }

    @Transactional
    public SolicitacaoAproveitamento atualizar(SolicitacaoAproveitamento solicitacao) {
        verificarId(solicitacao);
        return repository.save(solicitacao);
    }

    public void remover(SolicitacaoAproveitamento solicitacao) {
        verificarId(solicitacao);
        repository.delete(solicitacao);
    }

    public void remover(Integer id) {
        remover(buscarObrigatoria(id));
    }

    // ===== Maquina de estados da solicitacao (StatusSolicitacao) =====

    // ao deferir, as horas do certificado entram no total do discente
    @Transactional
    public SolicitacaoAproveitamento avaliar(Integer id, boolean aprovado, String parecer) {
        SolicitacaoAproveitamento solicitacao = buscarObrigatoria(id);
        if (solicitacao.getStatus() != StatusSolicitacao.PENDENTE)
            throw new OperacaoInvalidaException("So e possivel avaliar uma solicitacao PENDENTE.");
        solicitacao.setParecer(parecer);
        solicitacao.setStatus(aprovado ? StatusSolicitacao.DEFERIDA : StatusSolicitacao.INDEFERIDA);
        solicitacao.setDataAvaliacao(LocalDate.now());
        if (aprovado) {
            Discente discente = solicitacao.getSolicitante();
            discente.setHorasCumpridas(discente.getHorasCumpridas() + solicitacao.getCertificado().getCargaHoraria());
            discenteRepository.save(discente);
        }
        return repository.save(solicitacao);
    }

    @Transactional
    public SolicitacaoAproveitamento delegar(Integer id) {
        SolicitacaoAproveitamento solicitacao = buscarObrigatoria(id);
        if (solicitacao.getStatus() != StatusSolicitacao.PENDENTE)
            throw new OperacaoInvalidaException("Solicitacao nao esta PENDENTE (status atual: " + solicitacao.getStatus() + ").");
        if (solicitacao.isDelegadaParaComissao())
            throw new OperacaoInvalidaException("Solicitacao ja foi delegada para a Comissao.");
        solicitacao.setDelegadaParaComissao(true);
        return repository.save(solicitacao);
    }

    // cancelar libera o certificado para uma nova solicitacao
    @Transactional
    public SolicitacaoAproveitamento cancelar(Integer id) {
        SolicitacaoAproveitamento solicitacao = buscarObrigatoria(id);
        if (solicitacao.getStatus() != StatusSolicitacao.PENDENTE)
            throw new OperacaoInvalidaException("So e possivel cancelar uma solicitacao PENDENTE.");
        solicitacao.setStatus(StatusSolicitacao.CANCELADA);
        Certificado certificado = solicitacao.getCertificado();
        certificado.setAproveitamentoSolicitado(false);
        certificadoRepository.save(certificado);
        return repository.save(solicitacao);
    }

    // so pode reenviar nos 5 primeiros dias apos o indeferimento
    @Transactional
    public SolicitacaoAproveitamento reenviar(Integer id) {
        SolicitacaoAproveitamento solicitacao = buscarObrigatoria(id);
        if (solicitacao.getStatus() != StatusSolicitacao.INDEFERIDA)
            throw new OperacaoInvalidaException("So e possivel reenviar uma solicitacao INDEFERIDA.");
        if (!podeReenviar(solicitacao))
            throw new OperacaoInvalidaException("Prazo de reenvio (" + PRAZO_REENVIO_DIAS + " dias) expirado.");
        solicitacao.setStatus(StatusSolicitacao.PENDENTE);
        solicitacao.setParecer("");
        solicitacao.setDataCriacao(LocalDate.now());
        solicitacao.setDataAvaliacao(null);
        return repository.save(solicitacao);
    }

    // ===== Prazos (regimento) =====

    // pendente ha mais de 10 dias = coordenacao nao avaliou no prazo
    public boolean isAvaliacaoAtrasada(SolicitacaoAproveitamento solicitacao) {
        return solicitacao.getStatus() == StatusSolicitacao.PENDENTE
                && diasDesde(solicitacao.getDataCriacao()) > PRAZO_AVALIACAO_DIAS;
    }

    public boolean podeReenviar(SolicitacaoAproveitamento solicitacao) {
        return solicitacao.getStatus() == StatusSolicitacao.INDEFERIDA
                && diasDesde(solicitacao.getDataAvaliacao()) <= PRAZO_REENVIO_DIAS;
    }

    public long diasRestantesReenvio(SolicitacaoAproveitamento solicitacao) {
        if (solicitacao.getStatus() != StatusSolicitacao.INDEFERIDA) return 0;
        return Math.max(0, PRAZO_REENVIO_DIAS - diasDesde(solicitacao.getDataAvaliacao()));
    }

    private long diasDesde(LocalDate data) {
        if (data == null) return 0;
        return ChronoUnit.DAYS.between(data, LocalDate.now());
    }

    // ===== Consultas =====

    public List<SolicitacaoAproveitamento> listarPendentes() {
        return repository.findByStatus(StatusSolicitacao.PENDENTE);
    }

    public List<SolicitacaoAproveitamento> listarPendentesSemDelegacao() {
        return repository.findByStatus(StatusSolicitacao.PENDENTE).stream()
                .filter(s -> !s.isDelegadaParaComissao())
                .toList();
    }

    public List<SolicitacaoAproveitamento> listarDelegadas() {
        return repository.findByStatus(StatusSolicitacao.PENDENTE).stream()
                .filter(SolicitacaoAproveitamento::isDelegadaParaComissao)
                .toList();
    }

    public List<SolicitacaoAproveitamento> buscar(SolicitacaoAproveitamento filtro) {
        Example<SolicitacaoAproveitamento> example = Example.of(filtro,
                ExampleMatcher.matching()
                        .withIgnoreCase()
                        .withIgnorePaths("delegadaParaComissao")
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));
        return repository.findAll(example);
    }

    private SolicitacaoAproveitamento buscarObrigatoria(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Solicitacao nao encontrada."));
    }

    private void marcarCertificado(Integer certificadoId, boolean solicitado) {
        Certificado certificado = certificadoRepository.findById(certificadoId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Certificado nao encontrado."));
        certificado.setAproveitamentoSolicitado(solicitado);
        certificadoRepository.save(certificado);
    }

    private void verificarId(SolicitacaoAproveitamento solicitacao) {
        if ((solicitacao == null) || (solicitacao.getId() == null))
            throw new RegraNegocioRunTime("Solicitacao invalida (sem id).");
    }

    private void verificaSolicitacao(SolicitacaoAproveitamento solicitacao) {
        if (solicitacao == null)
            throw new RegraNegocioRunTime("Uma solicitacao valida deve ser informada.");
        if (solicitacao.getSolicitante() == null)
            throw new RegraNegocioRunTime("Solicitante deve ser informado.");
        if ((solicitacao.getCertificado() == null) || (solicitacao.getCertificado().getId() == null))
            throw new RegraNegocioRunTime("Certificado deve ser informado.");
    }
}
