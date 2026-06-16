package br.ufma.extensao.service;

import br.ufma.extensao.model.GrupoEstudantil;
import br.ufma.extensao.repo.GrupoEstudantilRepository;
import br.ufma.extensao.service.exceptions.EntidadeNaoEncontradaException;
import br.ufma.extensao.service.exceptions.RegraNegocioRunTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GrupoEstudantilService {

    @Autowired
    GrupoEstudantilRepository repository;

    @Transactional
    public GrupoEstudantil salvar(GrupoEstudantil grupo) {
        verificaGrupo(grupo);
        return repository.save(grupo);
    }

    @Transactional
    public GrupoEstudantil atualizar(GrupoEstudantil grupo) {
        verificarId(grupo);
        return repository.save(grupo);
    }

    public void remover(GrupoEstudantil grupo) {
        verificarId(grupo);
        repository.delete(grupo);
    }

    public void remover(Integer id) {
        GrupoEstudantil grupo = repository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Grupo estudantil nao encontrado."));
        remover(grupo);
    }

    public List<GrupoEstudantil> buscar(GrupoEstudantil filtro) {
        Example<GrupoEstudantil> example = Example.of(filtro,
                ExampleMatcher.matching()
                        .withIgnoreCase()
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));
        return repository.findAll(example);
    }

    private void verificarId(GrupoEstudantil grupo) {
        if ((grupo == null) || (grupo.getId() == null))
            throw new RegraNegocioRunTime("Grupo invalido (sem id).");
    }

    private void verificaGrupo(GrupoEstudantil grupo) {
        if (grupo == null)
            throw new RegraNegocioRunTime("Um grupo valido deve ser informado.");
        if ((grupo.getNome() == null) || (grupo.getNome().trim().equals("")))
            throw new RegraNegocioRunTime("Nome do grupo deve ser informado.");
        if (grupo.getResponsavel() == null)
            throw new RegraNegocioRunTime("Responsavel do grupo deve ser informado.");
    }
}
