package br.ufma.extensao.service;

import br.ufma.extensao.model.Certificado;
import br.ufma.extensao.model.Discente;
import br.ufma.extensao.model.Oportunidade;
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

    @Transactional
    public Oportunidade salvar(Oportunidade oportunidade) {
        verificaOportunidade(oportunidade);
        return repository.save(oportunidade);
    }

    @Transactional
    public Oportunidade atualizar(Oportunidade patch) {
        verificarId(patch);
        Oportunidade existente = buscarObrigatoria(patch.getId());
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

    public void remover(Oportunidade oportunidade) {
        verificarId(oportunidade);
        repository.delete(oportunidade);
    }

    public void remover(Integer id) {
        remover(buscarObrigatoria(id));
    }

    // ===== Maquina de estados (RF012) =====

    @Transactional
    public Oportunidade submeter(Integer id) {
        Oportunidade oportunidade = buscarObrigatoria(id);
        if (oportunidade.getStatus() != StatusOportunidade.RASCUNHO)
            throw new OperacaoInvalidaException("So e possivel submeter uma oportunidade em RASCUNHO.");
        oportunidade.setStatus(StatusOportunidade.AGUARDANDO_APROVACAO);
        return repository.save(oportunidade);
    }

    @Transactional
    public Oportunidade aprovar(Integer id) {
        Oportunidade oportunidade = buscarObrigatoria(id);
        if (oportunidade.getStatus() != StatusOportunidade.AGUARDANDO_APROVACAO)
            throw new OperacaoInvalidaException("So e possivel aprovar uma oportunidade AGUARDANDO_APROVACAO.");
        oportunidade.setStatus(StatusOportunidade.ABERTA);
        return repository.save(oportunidade);
    }

    @Transactional
    public Oportunidade iniciar(Integer id) {
        Oportunidade oportunidade = buscarObrigatoria(id);
        if (oportunidade.getStatus() != StatusOportunidade.ABERTA)
            throw new OperacaoInvalidaException("So e possivel iniciar a execucao de uma oportunidade ABERTA.");
        oportunidade.setStatus(StatusOportunidade.EM_EXECUCAO);
        return repository.save(oportunidade);
    }

    @Transactional
    public Oportunidade encerrar(Integer id) {
        Oportunidade oportunidade = buscarObrigatoria(id);
        StatusOportunidade status = oportunidade.getStatus();
        if (status != StatusOportunidade.ABERTA && status != StatusOportunidade.EM_EXECUCAO)
            throw new OperacaoInvalidaException("So e possivel encerrar uma oportunidade ABERTA ou EM_EXECUCAO.");
        oportunidade.setStatus(StatusOportunidade.ENCERRADA);
        return repository.save(oportunidade);
    }

    @Transactional
    public Oportunidade cancelar(Integer id, String motivo) {
        Oportunidade oportunidade = buscarObrigatoria(id);
        StatusOportunidade status = oportunidade.getStatus();
        if (status == StatusOportunidade.ENCERRADA || status == StatusOportunidade.CANCELADA)
            throw new OperacaoInvalidaException("Nao e possivel cancelar uma oportunidade ja ENCERRADA ou CANCELADA.");
        oportunidade.setStatus(StatusOportunidade.CANCELADA);
        oportunidade.setMotivoCancelamento(motivo);
        return repository.save(oportunidade);
    }

    // ===== Inscricoes (fila de espera / aprovados) =====

    @Transactional
    public Oportunidade inscrever(Integer oportunidadeId, Integer discenteId) {
        Oportunidade oportunidade = buscarObrigatoria(oportunidadeId);
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

    @Transactional
    public Oportunidade avaliarInscricao(Integer oportunidadeId, Integer discenteId, boolean aprovar) {
        Oportunidade oportunidade = buscarObrigatoria(oportunidadeId);
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

    @Transactional
    public Oportunidade cancelarInscricao(Integer oportunidadeId, Integer discenteId) {
        Oportunidade oportunidade = buscarObrigatoria(oportunidadeId);
        Discente discente = buscarDiscente(discenteId);
        oportunidade.getFilaEspera().remove(discente);
        oportunidade.getInscritosAprovados().remove(discente);
        return repository.save(oportunidade);
    }

    // remove um aprovado e promove um discente que esta na fila de espera
    @Transactional
    public Oportunidade substituirParticipante(Integer oportunidadeId, Integer aRemoverId, Integer substitutoId) {
        Oportunidade oportunidade = buscarObrigatoria(oportunidadeId);
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

    // emite certificados; as horas so entram depois do aproveitamento ser deferido
    @Transactional
    public Oportunidade certificar(Integer oportunidadeId, List<Integer> discenteIds) {
        Oportunidade oportunidade = buscarObrigatoria(oportunidadeId);
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

    public Oportunidade buscarPorId(Integer id) {
        return buscarObrigatoria(id);
    }

    private Oportunidade buscarObrigatoria(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Oportunidade nao encontrada."));
    }

    private Discente buscarDiscente(Integer id) {
        return discenteRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Discente nao encontrado."));
    }

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

    private void verificaOportunidade(Oportunidade oportunidade) {
        if (oportunidade == null)
            throw new RegraNegocioRunTime("Uma oportunidade valida deve ser informada.");
        if ((oportunidade.getTitulo() == null) || (oportunidade.getTitulo().trim().equals("")))
            throw new RegraNegocioRunTime("Titulo da oportunidade deve ser informado.");
        if (oportunidade.getModalidade() == null)
            throw new RegraNegocioRunTime("Modalidade da oportunidade deve ser informada.");
        if (oportunidade.getCargaHoraria() <= 0)
            throw new RegraNegocioRunTime("Carga horaria deve ser maior que zero.");
    }
}
