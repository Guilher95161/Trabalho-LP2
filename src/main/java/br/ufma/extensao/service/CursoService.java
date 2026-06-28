package br.ufma.extensao.service;

import br.ufma.extensao.model.Curso;
import br.ufma.extensao.repo.CursoRepository;
import br.ufma.extensao.service.exceptions.EntidadeNaoEncontradaException;
import br.ufma.extensao.service.exceptions.RegraNegocioRunTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CursoService {

    @Autowired
    CursoRepository repository;

    @Transactional
    public Curso salvar(Curso curso) {
        verificaCurso(curso);
        return repository.save(curso);
    }

    @Transactional
    public Curso atualizar(Curso patch) {
        verificarId(patch);
        Curso existente = repository.findById(patch.getId())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Curso nao encontrado."));
        if (patch.getNome() != null && !patch.getNome().isBlank())
            existente.setNome(patch.getNome());
        if (patch.getCurriculo() != null)
            existente.setCurriculo(patch.getCurriculo());
        if (patch.getCargaHoraria() != null)
            existente.setCargaHoraria(patch.getCargaHoraria());
        return repository.save(existente);
    }

    public void remover(Curso curso) {
        verificarId(curso);
        repository.delete(curso);
    }

    public void remover(Integer id) {
        Curso curso = repository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Curso nao encontrado."));
        remover(curso);
    }

    public Curso buscarPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Curso nao encontrado."));
    }

    public List<Curso> buscar(Curso filtro) {
        Example<Curso> example = Example.of(filtro,
                ExampleMatcher.matching()
                        .withIgnoreCase()
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));
        return repository.findAll(example);
    }

    private void verificarId(Curso curso) {
        if ((curso == null) || (curso.getId() == null))
            throw new RegraNegocioRunTime("Curso invalido (sem id).");
    }

    private void verificaCurso(Curso curso) {
        if (curso == null)
            throw new RegraNegocioRunTime("Um curso valido deve ser informado.");
        if ((curso.getNome() == null) || (curso.getNome().trim().equals("")))
            throw new RegraNegocioRunTime("Nome do curso deve ser informado.");
    }
}
