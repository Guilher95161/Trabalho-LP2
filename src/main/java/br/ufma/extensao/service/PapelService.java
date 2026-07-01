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

    /**
     * Salva um novo papel, validando os campos obrigatorios.
     * @param papel papel a ser cadastrado
     * @return o papel salvo (com id gerado)
     * @throws RegraNegocioRunTime se o nome nao for informado
     */
    @Transactional
    public Papel salvar(Papel papel) {
        verificarPapel(papel);
        return repository.save(papel);
    }

    /**
     * Atualiza o nome de um papel existente.
     * @param patch papel com o id e o nome a atualizar
     * @return o papel atualizado
     * @throws RegraNegocioRunTime se o id nao for informado
     * @throws EntidadeNaoEncontradaException se nao existir papel com esse id
     */
    @Transactional
    public Papel atualizar(Papel patch) {
        verificarId(patch);
        Papel existente = repository.findById(patch.getId())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Papel nao encontrado."));
        if (patch.getNome() != null && !patch.getNome().isBlank())
            existente.setNome(patch.getNome());
        return repository.save(existente);
    }

    /**
     * Remove o papel informado.
     * @param papel papel a remover (precisa ter id)
     * @throws RegraNegocioRunTime se o id nao for informado
     */
    public void remover(Papel papel) {
        verificarId(papel);
        repository.delete(papel);
    }

    /**
     * Remove o papel pelo id.
     * @param id id do papel a remover
     * @throws EntidadeNaoEncontradaException se nao existir papel com esse id
     */
    public void remover(Integer id) {
        Papel papel = repository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Papel nao encontrado."));
        remover(papel);
    }

    /**
     * Busca um papel pelo id.
     * @param id id do papel
     * @return o papel encontrado
     * @throws EntidadeNaoEncontradaException se nao existir papel com esse id
     */
    public Papel buscarPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Papel nao encontrado."));
    }

    /**
     * Busca papeis usando o filtro como exemplo (contains e ignorando maiusculas/minusculas).
     * @param filtro papel com os campos que servem de criterio de busca
     * @return a lista de papeis que casam com o filtro
     */
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

    private void verificarPapel(Papel papel) {
        if (papel == null)
            throw new RegraNegocioRunTime("Um papel valido deve ser informado.");
        if ((papel.getNome() == null) || (papel.getNome().isBlank()))
            throw new RegraNegocioRunTime("Nome do papel deve ser informado.");
    }
}
