package br.ufma.extensao.service;

import br.ufma.extensao.model.Oportunidade;
import br.ufma.extensao.model.enums.StatusOportunidade;
import br.ufma.extensao.repo.OportunidadeRepository;
import br.ufma.extensao.service.exceptions.EntidadeNaoEncontradaException;
import br.ufma.extensao.service.exceptions.OperacaoInvalidaException;
import br.ufma.extensao.service.exceptions.RegraNegocioRunTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OportunidadeService {

    @Autowired
    OportunidadeRepository repository;

    @Transactional
    public Oportunidade salvar(Oportunidade oportunidade) {
        verificaOportunidade(oportunidade);
        return repository.save(oportunidade);
    }

    @Transactional
    public Oportunidade atualizar(Oportunidade oportunidade) {
        verificarId(oportunidade);
        return repository.save(oportunidade);
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
    public Oportunidade cancelar(Integer id) {
        Oportunidade oportunidade = buscarObrigatoria(id);
        StatusOportunidade status = oportunidade.getStatus();
        if (status == StatusOportunidade.ENCERRADA || status == StatusOportunidade.CANCELADA)
            throw new OperacaoInvalidaException("Nao e possivel cancelar uma oportunidade ja ENCERRADA ou CANCELADA.");
        oportunidade.setStatus(StatusOportunidade.CANCELADA);
        return repository.save(oportunidade);
    }

    private Oportunidade buscarObrigatoria(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Oportunidade nao encontrada."));
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
