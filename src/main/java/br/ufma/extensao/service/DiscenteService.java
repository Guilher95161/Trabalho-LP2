package br.ufma.extensao.service;

import br.ufma.extensao.model.Discente;
import br.ufma.extensao.repo.DiscenteRepository;
import br.ufma.extensao.service.exceptions.EntidadeNaoEncontradaException;
import br.ufma.extensao.service.exceptions.RegraNegocioRunTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DiscenteService {

    @Autowired
    DiscenteRepository repository;

    @Transactional
    public Discente salvar(Discente discente) {
        verificaDiscente(discente);
        return repository.save(discente);
    }

    @Transactional
    public Discente atualizar(Discente discente) {
        verificarId(discente);
        return repository.save(discente);
    }

    public void remover(Discente discente) {
        verificarId(discente);
        repository.delete(discente);
    }

    public void remover(Integer id) {
        Discente discente = repository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Discente nao encontrado."));
        remover(discente);
    }

    public List<Discente> buscar(Discente filtro) {
        Example<Discente> example = Example.of(filtro,
                ExampleMatcher.matching()
                        .withIgnoreCase()
                        .withIgnorePaths("ativo", "horasCumpridas")
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));
        return repository.findAll(example);
    }

    private void verificarId(Discente discente) {
        if ((discente == null) || (discente.getId() == null))
            throw new RegraNegocioRunTime("Discente invalido (sem id).");
    }

    private void verificaDiscente(Discente discente) {
        if (discente == null)
            throw new RegraNegocioRunTime("Um discente valido deve ser informado.");
        if ((discente.getNome() == null) || (discente.getNome().trim().equals("")))
            throw new RegraNegocioRunTime("Nome do discente deve ser informado.");
        if ((discente.getEmail() == null) || (discente.getEmail().trim().equals("")))
            throw new RegraNegocioRunTime("Email do discente deve ser informado.");
    }
}
