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

    /**
     * Abre uma nova solicitacao de aproveitamento (nasce PENDENTE e marca o certificado como solicitado).
     * @param solicitacao solicitacao com solicitante e certificado
     * @return a solicitacao salva (com id gerado)
     * @throws RegraNegocioRunTime se o solicitante ou o certificado nao forem informados
     * @throws EntidadeNaoEncontradaException se o certificado informado nao existir
     */
    @Transactional
    public SolicitacaoAproveitamento salvar(SolicitacaoAproveitamento solicitacao) {
        verificarSolicitacao(solicitacao);
        // uma nova solicitacao sempre nasce PENDENTE e marca o certificado
        solicitacao.setStatus(StatusSolicitacao.PENDENTE);
        solicitacao.setParecer("");
        solicitacao.setDelegadaParaComissao(false);
        solicitacao.setDataCriacao(LocalDate.now());
        solicitacao.setDataAvaliacao(null);
        marcarCertificado(solicitacao.getCertificado().getId(), true);
        return repository.save(solicitacao);
    }

    /**
     * Atualiza o parecer de uma solicitacao existente.
     * @param patch solicitacao com o id e o parecer a atualizar
     * @return a solicitacao atualizada
     * @throws RegraNegocioRunTime se o id nao for informado
     * @throws EntidadeNaoEncontradaException se nao existir solicitacao com esse id
     */
    @Transactional
    public SolicitacaoAproveitamento atualizar(SolicitacaoAproveitamento patch) {
        verificarId(patch);
        SolicitacaoAproveitamento existente = buscarSolicitacao(patch.getId());
        if (patch.getParecer() != null)
            existente.setParecer(patch.getParecer());
        return repository.save(existente);
    }

    /**
     * Remove a solicitacao informada.
     * @param solicitacao solicitacao a remover (precisa ter id)
     * @throws RegraNegocioRunTime se o id nao for informado
     */
    public void remover(SolicitacaoAproveitamento solicitacao) {
        verificarId(solicitacao);
        repository.delete(solicitacao);
    }

    /**
     * Remove a solicitacao pelo id.
     * @param id id da solicitacao a remover
     * @throws EntidadeNaoEncontradaException se nao existir solicitacao com esse id
     */
    public void remover(Integer id) {
        remover(buscarSolicitacao(id));
    }

    // ===== Maquina de estados da solicitacao (StatusSolicitacao) =====

    /**
     * Avalia a solicitacao. Ao deferir, soma as horas do certificado ao total do discente.
     * @param id id da solicitacao (deve estar PENDENTE)
     * @param aprovado true para deferir, false para indeferir
     * @param parecer justificativa da decisao
     * @return a solicitacao no novo status (DEFERIDA ou INDEFERIDA)
     * @throws EntidadeNaoEncontradaException se nao existir solicitacao com esse id
     * @throws OperacaoInvalidaException se a solicitacao nao estiver PENDENTE
     */
    @Transactional
    public SolicitacaoAproveitamento avaliar(Integer id, boolean aprovado, String parecer) {
        SolicitacaoAproveitamento solicitacao = buscarSolicitacao(id);
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

    /**
     * Delega a avaliacao da solicitacao (do coordenador) para a comissao.
     * @param id id da solicitacao (deve estar PENDENTE e ainda nao delegada)
     * @return a solicitacao marcada como delegada
     * @throws EntidadeNaoEncontradaException se nao existir solicitacao com esse id
     * @throws OperacaoInvalidaException se a solicitacao nao estiver PENDENTE ou ja tiver sido delegada
     */
    @Transactional
    public SolicitacaoAproveitamento delegar(Integer id) {
        SolicitacaoAproveitamento solicitacao = buscarSolicitacao(id);
        if (solicitacao.getStatus() != StatusSolicitacao.PENDENTE)
            throw new OperacaoInvalidaException("Solicitacao nao esta PENDENTE (status atual: " + solicitacao.getStatus() + ").");
        if (solicitacao.isDelegadaParaComissao())
            throw new OperacaoInvalidaException("Solicitacao ja foi delegada para a Comissao.");
        solicitacao.setDelegadaParaComissao(true);
        return repository.save(solicitacao);
    }

    /**
     * Cancela a solicitacao e libera o certificado para uma nova solicitacao.
     * @param id id da solicitacao (deve estar PENDENTE)
     * @return a solicitacao no status CANCELADA
     * @throws EntidadeNaoEncontradaException se nao existir solicitacao com esse id
     * @throws OperacaoInvalidaException se a solicitacao nao estiver PENDENTE
     */
    @Transactional
    public SolicitacaoAproveitamento cancelar(Integer id) {
        SolicitacaoAproveitamento solicitacao = buscarSolicitacao(id);
        if (solicitacao.getStatus() != StatusSolicitacao.PENDENTE)
            throw new OperacaoInvalidaException("So e possivel cancelar uma solicitacao PENDENTE.");
        solicitacao.setStatus(StatusSolicitacao.CANCELADA);
        Certificado certificado = solicitacao.getCertificado();
        certificado.setAproveitamentoSolicitado(false);
        certificadoRepository.save(certificado);
        return repository.save(solicitacao);
    }

    /**
     * Reenvia uma solicitacao indeferida para nova avaliacao (so nos 5 dias apos o indeferimento).
     * @param id id da solicitacao (deve estar INDEFERIDA)
     * @return a solicitacao de volta ao status PENDENTE
     * @throws EntidadeNaoEncontradaException se nao existir solicitacao com esse id
     * @throws OperacaoInvalidaException se a solicitacao nao estiver INDEFERIDA ou o prazo de reenvio tiver expirado
     */
    @Transactional
    public SolicitacaoAproveitamento reenviar(Integer id) {
        SolicitacaoAproveitamento solicitacao = buscarSolicitacao(id);
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

    /**
     * Indica se a avaliacao esta atrasada (pendente ha mais de 10 dias).
     * @param solicitacao solicitacao a verificar
     * @return true se estiver PENDENTE ha mais de PRAZO_AVALIACAO_DIAS dias
     */
    public boolean isAvaliacaoAtrasada(SolicitacaoAproveitamento solicitacao) {
        return solicitacao.getStatus() == StatusSolicitacao.PENDENTE
                && diasDesde(solicitacao.getDataCriacao()) > PRAZO_AVALIACAO_DIAS;
    }

    /**
     * Indica se a solicitacao ainda pode ser reenviada (indeferida ha ate 5 dias).
     * @param solicitacao solicitacao a verificar
     * @return true se estiver INDEFERIDA dentro do prazo de reenvio
     */
    public boolean podeReenviar(SolicitacaoAproveitamento solicitacao) {
        return solicitacao.getStatus() == StatusSolicitacao.INDEFERIDA
                && diasDesde(solicitacao.getDataAvaliacao()) <= PRAZO_REENVIO_DIAS;
    }

    /**
     * Calcula quantos dias ainda restam para reenviar uma solicitacao indeferida.
     * @param solicitacao solicitacao a verificar
     * @return o numero de dias restantes (0 se nao estiver INDEFERIDA ou o prazo ja tiver passado)
     */
    public long diasRestantesReenvio(SolicitacaoAproveitamento solicitacao) {
        if (solicitacao.getStatus() != StatusSolicitacao.INDEFERIDA) return 0;
        return Math.max(0, PRAZO_REENVIO_DIAS - diasDesde(solicitacao.getDataAvaliacao()));
    }

    private long diasDesde(LocalDate data) {
        if (data == null) return 0;
        return ChronoUnit.DAYS.between(data, LocalDate.now());
    }

    // ===== Consultas =====

    /**
     * Lista todas as solicitacoes pendentes.
     * @return as solicitacoes com status PENDENTE
     */
    public List<SolicitacaoAproveitamento> listarPendentes() {
        return repository.findByStatus(StatusSolicitacao.PENDENTE);
    }

    /**
     * Lista as solicitacoes pendentes que ainda nao foram delegadas (fila do coordenador).
     * @return as solicitacoes PENDENTE sem delegacao
     */
    public List<SolicitacaoAproveitamento> listarPendentesSemDelegacao() {
        return repository.findByStatus(StatusSolicitacao.PENDENTE).stream()
                .filter(s -> !s.isDelegadaParaComissao())
                .toList();
    }

    /**
     * Lista as solicitacoes pendentes que foram delegadas para a comissao (fila da comissao).
     * @return as solicitacoes PENDENTE com delegacao
     */
    public List<SolicitacaoAproveitamento> listarDelegadas() {
        return repository.findByStatus(StatusSolicitacao.PENDENTE).stream()
                .filter(SolicitacaoAproveitamento::isDelegadaParaComissao)
                .toList();
    }

    /**
     * Busca solicitacoes usando o filtro como exemplo (contains e ignorando maiusculas/minusculas).
     * @param filtro solicitacao com os campos que servem de criterio de busca
     * @return a lista de solicitacoes que casam com o filtro
     */
    public List<SolicitacaoAproveitamento> buscar(SolicitacaoAproveitamento filtro) {
        Example<SolicitacaoAproveitamento> example = Example.of(filtro,
                ExampleMatcher.matching()
                        .withIgnoreCase()
                        .withIgnorePaths("delegadaParaComissao")
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));
        return repository.findAll(example);
    }

    /**
     * Busca uma solicitacao pelo id.
     * @param id id da solicitacao
     * @return a solicitacao encontrada
     * @throws EntidadeNaoEncontradaException se nao existir solicitacao com esse id
     */
    public SolicitacaoAproveitamento buscarPorId(Integer id) {
        return buscarSolicitacao(id);
    }

    private SolicitacaoAproveitamento buscarSolicitacao(Integer id) {
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

    private void verificarSolicitacao(SolicitacaoAproveitamento solicitacao) {
        if (solicitacao == null)
            throw new RegraNegocioRunTime("Uma solicitacao valida deve ser informada.");
        if (solicitacao.getSolicitante() == null)
            throw new RegraNegocioRunTime("Solicitante deve ser informado.");
        if ((solicitacao.getCertificado() == null) || (solicitacao.getCertificado().getId() == null))
            throw new RegraNegocioRunTime("Certificado deve ser informado.");
    }
}
