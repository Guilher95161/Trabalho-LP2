package br.ufma.extensao.service;

import br.ufma.extensao.model.Papel;
import br.ufma.extensao.repo.PapelRepository;
import br.ufma.extensao.service.exceptions.EntidadeNaoEncontradaException;
import br.ufma.extensao.service.exceptions.RegraNegocioRunTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PapelService {

    @Autowired
    PapelRepository repository;

    @Transactional
    public Papel salvar(Papel papel) {
        verificaPapel(papel);
        return repository.save(papel);
    }

    @Transactional
    public Papel atualizar(Papel patch) {
        verificarId(patch);
        Papel existente = repository.findById(patch.getId())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Papel nao encontrado."));
        if (patch.getNome() != null && !patch.getNome().isBlank())
            existente.setNome(patch.getNome());
        return repository.save(existente);
    }

    public void remover(Papel papel) {
        verificarId(papel);
        repository.delete(papel);
    }

    public void remover(Integer id) {
        Papel papel = repository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Papel nao encontrado."));
        remover(papel);
    }

    public Papel buscarPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Papel nao encontrado."));
    }

    public List<Papel> buscar(Papel filtro) {
        Example<Papel> example = Example.of(filtro,
                ExampleMatcher.matching()
                        .withIgnoreCase()
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));
        return repository.findAll(example);
    }

    private void verificarId(Papel papel) {
        if ((papel == null) || (papel.getId() == null))
            throw new RegraNegocioRunTime("Papel invalido (sem id).");
    }

    private void verificaPapel(Papel papel) {
        if (papel == null)
            throw new RegraNegocioRunTime("Um papel valido deve ser informado.");
        if ((papel.getNome() == null) || (papel.getNome().trim().equals("")))
            throw new RegraNegocioRunTime("Nome do papel deve ser informado.");
    }
}
