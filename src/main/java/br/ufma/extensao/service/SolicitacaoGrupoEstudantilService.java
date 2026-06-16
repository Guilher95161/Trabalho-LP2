package br.ufma.extensao.service;

import br.ufma.extensao.model.SolicitacaoGrupoEstudantil;
import br.ufma.extensao.repo.SolicitacaoGrupoEstudantilRepository;
import br.ufma.extensao.service.exceptions.EntidadeNaoEncontradaException;
import br.ufma.extensao.service.exceptions.RegraNegocioRunTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SolicitacaoGrupoEstudantilService {

    @Autowired
    SolicitacaoGrupoEstudantilRepository repository;

    @Transactional
    public SolicitacaoGrupoEstudantil salvar(SolicitacaoGrupoEstudantil solicitacao) {
        verificaSolicitacao(solicitacao);
        return repository.save(solicitacao);
    }

    @Transactional
    public SolicitacaoGrupoEstudantil atualizar(SolicitacaoGrupoEstudantil solicitacao) {
        verificarId(solicitacao);
        return repository.save(solicitacao);
    }

    public void remover(SolicitacaoGrupoEstudantil solicitacao) {
        verificarId(solicitacao);
        repository.delete(solicitacao);
    }

    public void remover(Integer id) {
        SolicitacaoGrupoEstudantil solicitacao = repository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Solicitacao de grupo nao encontrada."));
        remover(solicitacao);
    }

    public List<SolicitacaoGrupoEstudantil> buscar(SolicitacaoGrupoEstudantil filtro) {
        Example<SolicitacaoGrupoEstudantil> example = Example.of(filtro,
                ExampleMatcher.matching()
                        .withIgnoreCase()
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));
        return repository.findAll(example);
    }

    private void verificarId(SolicitacaoGrupoEstudantil solicitacao) {
        if ((solicitacao == null) || (solicitacao.getId() == null))
            throw new RegraNegocioRunTime("Solicitacao de grupo invalida (sem id).");
    }

    private void verificaSolicitacao(SolicitacaoGrupoEstudantil solicitacao) {
        if (solicitacao == null)
            throw new RegraNegocioRunTime("Uma solicitacao valida deve ser informada.");
        if (solicitacao.getSolicitante() == null)
            throw new RegraNegocioRunTime("Solicitante deve ser informado.");
        if ((solicitacao.getNomeGrupo() == null) || (solicitacao.getNomeGrupo().trim().equals("")))
            throw new RegraNegocioRunTime("Nome do grupo deve ser informado.");
    }
}
