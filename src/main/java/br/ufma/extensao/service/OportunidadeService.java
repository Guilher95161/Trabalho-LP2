package br.ufma.extensao.service;

import br.ufma.extensao.model.Certificado;
import br.ufma.extensao.model.Discente;
import br.ufma.extensao.model.Oportunidade;
import br.ufma.extensao.model.Usuario;
import br.ufma.extensao.model.enums.StatusOportunidade;
import br.ufma.extensao.repo.CertificadoRepository;
import br.ufma.extensao.repo.DiscenteRepository;
import br.ufma.extensao.repo.OportunidadeRepository;
import br.ufma.extensao.service.exceptions.EntidadeNaoEncontradaException;
import br.ufma.extensao.service.exceptions.OperacaoInvalidaException;
import br.ufma.extensao.service.exceptions.RegraNegocioRunTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class OportunidadeService {

    @Autowired
    OportunidadeRepository repository;

    @Autowired
    DiscenteRepository discenteRepository;

    @Autowired
    CertificadoRepository certificadoRepository;

    /**
     * Salva uma nova oportunidade, validando os campos obrigatorios. Quando o status nao vem
     * definido, aplica o padrao: se o responsavel for discente a oportunidade nasce
     * AGUARDANDO_APROVACAO; os demais perfis ja abrem em ABERTA.
     * @param oportunidade oportunidade a ser cadastrada
     * @return a oportunidade salva (com id gerado)
     * @throws RegraNegocioRunTime se titulo, modalidade ou carga horaria forem invalidos
     */
    @Transactional
    public Oportunidade salvar(Oportunidade oportunidade) {
        verificarOportunidade(oportunidade);
        if (oportunidade.getStatus() == null)
            oportunidade.setStatus(criadaPorDiscente(oportunidade)
                    ? StatusOportunidade.AGUARDANDO_APROVACAO
                    : StatusOportunidade.ABERTA);
        return repository.save(oportunidade);
    }

    // responsavel que e discente indica oportunidade criada por aluno: precisa de aprovacao
    private boolean criadaPorDiscente(Oportunidade oportunidade) {
        Usuario responsavel = oportunidade.getResponsavel();
        return responsavel != null
                && responsavel.getId() != null
                && discenteRepository.existsById(responsavel.getId());
    }

    /**
     * Atualiza os campos informados de uma oportunidade existente (so altera o que vier preenchido).
     * @param patch oportunidade com o id e os campos a atualizar
     * @return a oportunidade atualizada
     * @throws RegraNegocioRunTime se o id nao for informado
     * @throws EntidadeNaoEncontradaException se nao existir oportunidade com esse id
     */
    @Transactional
    public Oportunidade atualizar(Oportunidade patch) {
        verificarId(patch);
        Oportunidade existente = buscarOportunidade(patch.getId());
        if (patch.getTitulo() != null && !patch.getTitulo().isBlank())
            existente.setTitulo(patch.getTitulo());
        if (patch.getDescricao() != null)
            existente.setDescricao(patch.getDescricao());
        if (patch.getModalidade() != null)
            existente.setModalidade(patch.getModalidade());
        if (patch.getPeriodoRealizacao() != null)
            existente.setPeriodoRealizacao(patch.getPeriodoRealizacao());
        if (patch.getCargaHoraria() > 0)
            existente.setCargaHoraria(patch.getCargaHoraria());
        if (patch.getVagas() > 0)
            existente.setVagas(patch.getVagas());
        if (patch.getResponsavel() != null)
            existente.setResponsavel(patch.getResponsavel());
        return repository.save(existente);
    }

    /**
     * Remove a oportunidade informada.
     * @param oportunidade oportunidade a remover (precisa ter id)
     * @throws RegraNegocioRunTime se o id nao for informado
     */
    public void remover(Oportunidade oportunidade) {
        verificarId(oportunidade);
        repository.delete(oportunidade);
    }

    /**
     * Remove a oportunidade pelo id.
     * @param id id da oportunidade a remover
     * @throws EntidadeNaoEncontradaException se nao existir oportunidade com esse id
     */
    public void remover(Integer id) {
        remover(buscarOportunidade(id));
    }

    // ===== Maquina de estados (RF012) =====

    /**
     * Submete a oportunidade para aprovacao (RASCUNHO -> AGUARDANDO_APROVACAO).
     * @param id id da oportunidade
     * @return a oportunidade no novo status
     * @throws EntidadeNaoEncontradaException se nao existir oportunidade com esse id
     * @throws OperacaoInvalidaException se a oportunidade nao estiver em RASCUNHO
     */
    @Transactional
    public Oportunidade submeter(Integer id) {
        Oportunidade oportunidade = buscarOportunidade(id);
        if (oportunidade.getStatus() != StatusOportunidade.RASCUNHO)
            throw new OperacaoInvalidaException("So e possivel submeter uma oportunidade em RASCUNHO.");
        oportunidade.setStatus(StatusOportunidade.AGUARDANDO_APROVACAO);
        return repository.save(oportunidade);
    }

    /**
     * Aprova a oportunidade, abrindo-a para inscricoes (AGUARDANDO_APROVACAO -> ABERTA).
     * @param id id da oportunidade
     * @return a oportunidade no novo status
     * @throws EntidadeNaoEncontradaException se nao existir oportunidade com esse id
     * @throws OperacaoInvalidaException se a oportunidade nao estiver AGUARDANDO_APROVACAO
     */
    @Transactional
    public Oportunidade aprovar(Integer id) {
        Oportunidade oportunidade = buscarOportunidade(id);
        if (oportunidade.getStatus() != StatusOportunidade.AGUARDANDO_APROVACAO)
            throw new OperacaoInvalidaException("So e possivel aprovar uma oportunidade AGUARDANDO_APROVACAO.");
        oportunidade.setStatus(StatusOportunidade.ABERTA);
        return repository.save(oportunidade);
    }

    /**
     * Inicia a execucao da oportunidade (ABERTA -> EM_EXECUCAO).
     * @param id id da oportunidade
     * @return a oportunidade no novo status
     * @throws EntidadeNaoEncontradaException se nao existir oportunidade com esse id
     * @throws OperacaoInvalidaException se a oportunidade nao estiver ABERTA
     */
    @Transactional
    public Oportunidade iniciar(Integer id) {
        Oportunidade oportunidade = buscarOportunidade(id);
        if (oportunidade.getStatus() != StatusOportunidade.ABERTA)
            throw new OperacaoInvalidaException("So e possivel iniciar a execucao de uma oportunidade ABERTA.");
        oportunidade.setStatus(StatusOportunidade.EM_EXECUCAO);
        return repository.save(oportunidade);
    }

    /**
     * Encerra a oportunidade, liberando a certificacao (ABERTA ou EM_EXECUCAO -> ENCERRADA).
     * @param id id da oportunidade
     * @return a oportunidade no novo status
     * @throws EntidadeNaoEncontradaException se nao existir oportunidade com esse id
     * @throws OperacaoInvalidaException se a oportunidade nao estiver ABERTA ou EM_EXECUCAO
     */
    @Transactional
    public Oportunidade encerrar(Integer id) {
        Oportunidade oportunidade = buscarOportunidade(id);
        StatusOportunidade status = oportunidade.getStatus();
        if (status != StatusOportunidade.ABERTA && status != StatusOportunidade.EM_EXECUCAO)
            throw new OperacaoInvalidaException("So e possivel encerrar uma oportunidade ABERTA ou EM_EXECUCAO.");
        oportunidade.setStatus(StatusOportunidade.ENCERRADA);
        return repository.save(oportunidade);
    }

    /**
     * Cancela a oportunidade e grava o motivo (permitido em qualquer status menos ENCERRADA/CANCELADA).
     * @param id id da oportunidade
     * @param motivo justificativa do cancelamento
     * @return a oportunidade no status CANCELADA
     * @throws EntidadeNaoEncontradaException se nao existir oportunidade com esse id
     * @throws OperacaoInvalidaException se a oportunidade ja estiver ENCERRADA ou CANCELADA
     */
    @Transactional
    public Oportunidade cancelar(Integer id, String motivo) {
        Oportunidade oportunidade = buscarOportunidade(id);
        StatusOportunidade status = oportunidade.getStatus();
        if (status == StatusOportunidade.ENCERRADA || status == StatusOportunidade.CANCELADA)
            throw new OperacaoInvalidaException("Nao e possivel cancelar uma oportunidade ja ENCERRADA ou CANCELADA.");
        oportunidade.setStatus(StatusOportunidade.CANCELADA);
        oportunidade.setMotivoCancelamento(motivo);
        return repository.save(oportunidade);
    }

    // ===== Inscricoes (fila de espera / aprovados) =====

    /**
     * Inscreve o discente na fila de espera da oportunidade.
     * @param oportunidadeId id da oportunidade
     * @param discenteId id do discente
     * @return a oportunidade com o discente na fila de espera
     * @throws EntidadeNaoEncontradaException se a oportunidade ou o discente nao existirem
     * @throws OperacaoInvalidaException se a oportunidade nao estiver ABERTA ou o discente estiver inativo
     */
    @Transactional
    public Oportunidade inscrever(Integer oportunidadeId, Integer discenteId) {
        Oportunidade oportunidade = buscarOportunidade(oportunidadeId);
        if (oportunidade.getStatus() != StatusOportunidade.ABERTA)
            throw new OperacaoInvalidaException("Inscricao nao permitida: oportunidade esta " + oportunidade.getStatus() + ".");
        Discente discente = buscarDiscente(discenteId);
        if (!discente.isAtivo())
            throw new OperacaoInvalidaException("Discente inativo nao pode se inscrever.");
        // ja aprovado nao volta para a fila de espera
        if (oportunidade.getInscritosAprovados().contains(discente))
            return oportunidade;
        oportunidade.getFilaEspera().add(discente);
        return repository.save(oportunidade);
    }

    /**
     * Avalia uma inscricao da fila de espera, aprovando (ocupa vaga) ou recusando.
     * @param oportunidadeId id da oportunidade
     * @param discenteId id do discente na fila de espera
     * @param aprovar true para aprovar (mover para inscritos), false para so remover da fila
     * @return a oportunidade com a fila atualizada
     * @throws EntidadeNaoEncontradaException se a oportunidade ou o discente nao existirem
     * @throws OperacaoInvalidaException se a oportunidade nao estiver ABERTA, o discente nao estiver na fila ou nao houver vagas
     */
    @Transactional
    public Oportunidade avaliarInscricao(Integer oportunidadeId, Integer discenteId, boolean aprovar) {
        Oportunidade oportunidade = buscarOportunidade(oportunidadeId);
        if (oportunidade.getStatus() != StatusOportunidade.ABERTA)
            throw new OperacaoInvalidaException("So e possivel avaliar inscricoes de uma oportunidade ABERTA.");
        Discente discente = buscarDiscente(discenteId);
        List<Discente> fila = oportunidade.getFilaEspera();
        if (!fila.contains(discente))
            throw new OperacaoInvalidaException("Discente nao esta na fila de espera.");
        if (aprovar) {
            if (oportunidade.getInscritosAprovados().size() >= oportunidade.getVagas())
                throw new OperacaoInvalidaException("Nao ha vagas disponiveis.");
            oportunidade.getInscritosAprovados().add(discente);
        }
        fila.remove(discente);
        return repository.save(oportunidade);
    }

    /**
     * Cancela a inscricao do discente, tirando-o da fila de espera e dos inscritos aprovados.
     * @param oportunidadeId id da oportunidade
     * @param discenteId id do discente
     * @return a oportunidade sem o discente
     * @throws EntidadeNaoEncontradaException se a oportunidade ou o discente nao existirem
     */
    @Transactional
    public Oportunidade cancelarInscricao(Integer oportunidadeId, Integer discenteId) {
        Oportunidade oportunidade = buscarOportunidade(oportunidadeId);
        Discente discente = buscarDiscente(discenteId);
        oportunidade.getFilaEspera().remove(discente);
        oportunidade.getInscritosAprovados().remove(discente);
        return repository.save(oportunidade);
    }

    /**
     * Substitui um participante aprovado por um discente que esta na fila de espera.
     * @param oportunidadeId id da oportunidade
     * @param aRemoverId id do participante aprovado a remover
     * @param substitutoId id do discente da fila que assume a vaga
     * @return a oportunidade com os inscritos atualizados
     * @throws EntidadeNaoEncontradaException se a oportunidade ou algum discente nao existirem
     * @throws OperacaoInvalidaException se a oportunidade nao estiver ABERTA/EM_EXECUCAO ou os discentes nao estiverem nas listas esperadas
     */
    @Transactional
    public Oportunidade substituirParticipante(Integer oportunidadeId, Integer aRemoverId, Integer substitutoId) {
        Oportunidade oportunidade = buscarOportunidade(oportunidadeId);
        StatusOportunidade status = oportunidade.getStatus();
        if (status != StatusOportunidade.ABERTA && status != StatusOportunidade.EM_EXECUCAO)
            throw new OperacaoInvalidaException("Substituicao so e possivel em oportunidade ABERTA ou EM_EXECUCAO.");
        Discente aRemover = buscarDiscente(aRemoverId);
        Discente substituto = buscarDiscente(substitutoId);
        List<Discente> aprovados = oportunidade.getInscritosAprovados();
        List<Discente> fila = oportunidade.getFilaEspera();
        if (!aprovados.contains(aRemover))
            throw new OperacaoInvalidaException("Participante a remover nao esta entre os aprovados.");
        if (!fila.contains(substituto))
            throw new OperacaoInvalidaException("Substituto nao esta na fila de espera.");
        aprovados.remove(aRemover);
        fila.remove(substituto);
        aprovados.add(substituto);
        return repository.save(oportunidade);
    }

    /**
     * Emite certificados para os discentes informados. As horas so entram apos o aproveitamento deferido.
     * @param oportunidadeId id da oportunidade (deve estar ENCERRADA)
     * @param discenteIds ids dos discentes que receberao certificado
     * @return a oportunidade avaliada
     * @throws EntidadeNaoEncontradaException se a oportunidade ou algum discente nao existirem
     * @throws OperacaoInvalidaException se a oportunidade nao estiver ENCERRADA
     */
    @Transactional
    public Oportunidade certificar(Integer oportunidadeId, List<Integer> discenteIds) {
        Oportunidade oportunidade = buscarOportunidade(oportunidadeId);
        if (oportunidade.getStatus() != StatusOportunidade.ENCERRADA)
            throw new OperacaoInvalidaException("So e possivel certificar discentes de uma oportunidade ENCERRADA.");
        LocalDate hoje = LocalDate.now();
        for (Integer discenteId : discenteIds) {
            Discente discente = buscarDiscente(discenteId);
            Certificado certificado = Certificado.builder()
                    .tituloAtividade(oportunidade.getTitulo())
                    .cargaHoraria(oportunidade.getCargaHoraria())
                    .data(hoje)
                    .aproveitamentoSolicitado(false)
                    .build();
            if (discente.getCertificados() == null)
                discente.setCertificados(new ArrayList<>());
            discente.getCertificados().add(certificado);
            discenteRepository.save(discente);
        }
        return oportunidade;
    }

    /**
     * Busca uma oportunidade pelo id.
     * @param id id da oportunidade
     * @return a oportunidade encontrada
     * @throws EntidadeNaoEncontradaException se nao existir oportunidade com esse id
     */
    public Oportunidade buscarPorId(Integer id) {
        return buscarOportunidade(id);
    }

    private Oportunidade buscarOportunidade(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Oportunidade nao encontrada."));
    }

    private Discente buscarDiscente(Integer id) {
        return discenteRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Discente nao encontrado."));
    }

    /**
     * Busca oportunidades usando o filtro como exemplo (contains e ignorando maiusculas/minusculas).
     * @param filtro oportunidade com os campos que servem de criterio de busca
     * @return a lista de oportunidades que casam com o filtro
     */
    public List<Oportunidade> buscar(Oportunidade filtro) {
        Example<Oportunidade> example = Example.of(filtro,
                ExampleMatcher.matching()
                        .withIgnoreCase()
                        .withIgnorePaths("cargaHoraria", "vagas")
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));
        return repository.findAll(example);
    }

    private void verificarId(Oportunidade oportunidade) {
        if ((oportunidade == null) || (oportunidade.getId() == null))
            throw new RegraNegocioRunTime("Oportunidade invalida (sem id).");
    }

    private void verificarOportunidade(Oportunidade oportunidade) {
        if (oportunidade == null)
            throw new RegraNegocioRunTime("Uma oportunidade valida deve ser informada.");
        if ((oportunidade.getTitulo() == null) || (oportunidade.getTitulo().isBlank()))
            throw new RegraNegocioRunTime("Titulo da oportunidade deve ser informado.");
        if (oportunidade.getModalidade() == null)
            throw new RegraNegocioRunTime("Modalidade da oportunidade deve ser informada.");
        if (oportunidade.getCargaHoraria() <= 0)
            throw new RegraNegocioRunTime("Carga horaria deve ser maior que zero.");
    }
}
