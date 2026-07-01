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

    /**
     * Salva um novo curso, validando os campos obrigatorios.
     * @param curso curso a ser cadastrado
     * @return o curso salvo (com id gerado)
     * @throws RegraNegocioRunTime se o nome nao for informado
     */
    @Transactional
    public Curso salvar(Curso curso) {
        verificarCurso(curso);
        return repository.save(curso);
    }

    /**
     * Atualiza os campos informados de um curso existente (so altera o que vier preenchido).
     * @param patch curso com o id e os campos a atualizar
     * @return o curso atualizado
     * @throws RegraNegocioRunTime se o id nao for informado
     * @throws EntidadeNaoEncontradaException se nao existir curso com esse id
     */
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

    /**
     * Remove o curso informado.
     * @param curso curso a remover (precisa ter id)
     * @throws RegraNegocioRunTime se o id nao for informado
     */
    public void remover(Curso curso) {
        verificarId(curso);
        repository.delete(curso);
    }

    /**
     * Remove o curso pelo id.
     * @param id id do curso a remover
     * @throws EntidadeNaoEncontradaException se nao existir curso com esse id
     */
    public void remover(Integer id) {
        Curso curso = repository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Curso nao encontrado."));
        remover(curso);
    }

    /**
     * Busca um curso pelo id.
     * @param id id do curso
     * @return o curso encontrado
     * @throws EntidadeNaoEncontradaException se nao existir curso com esse id
     */
    public Curso buscarPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Curso nao encontrado."));
    }

    /**
     * Busca cursos usando o filtro como exemplo (contains e ignorando maiusculas/minusculas).
     * @param filtro curso com os campos que servem de criterio de busca
     * @return a lista de cursos que casam com o filtro
     */
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

    private void verificarCurso(Curso curso) {
        if (curso == null)
            throw new RegraNegocioRunTime("Um curso valido deve ser informado.");
        if ((curso.getNome() == null) || (curso.getNome().isBlank()))
            throw new RegraNegocioRunTime("Nome do curso deve ser informado.");
    }
}
