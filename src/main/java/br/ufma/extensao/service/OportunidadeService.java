package br.ufma.extensao.service;

import br.ufma.extensao.model.Oportunidade;
import br.ufma.extensao.repo.OportunidadeRepository;
import br.ufma.extensao.service.exceptions.EntidadeNaoEncontradaException;
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
        Oportunidade oportunidade = repository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Oportunidade nao encontrada."));
        remover(oportunidade);
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
