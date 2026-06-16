package br.ufma.extensao.service;

import br.ufma.extensao.model.SolicitacaoAproveitamento;
import br.ufma.extensao.repo.SolicitacaoAproveitamentoRepository;
import br.ufma.extensao.service.exceptions.EntidadeNaoEncontradaException;
import br.ufma.extensao.service.exceptions.RegraNegocioRunTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SolicitacaoAproveitamentoService {

    @Autowired
    SolicitacaoAproveitamentoRepository repository;

    @Transactional
    public SolicitacaoAproveitamento salvar(SolicitacaoAproveitamento solicitacao) {
        verificaSolicitacao(solicitacao);
        return repository.save(solicitacao);
    }

    @Transactional
    public SolicitacaoAproveitamento atualizar(SolicitacaoAproveitamento solicitacao) {
        verificarId(solicitacao);
        return repository.save(solicitacao);
    }

    public void remover(SolicitacaoAproveitamento solicitacao) {
        verificarId(solicitacao);
        repository.delete(solicitacao);
    }

    public void remover(Integer id) {
        SolicitacaoAproveitamento solicitacao = repository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Solicitacao nao encontrada."));
        remover(solicitacao);
    }

    public List<SolicitacaoAproveitamento> buscar(SolicitacaoAproveitamento filtro) {
        Example<SolicitacaoAproveitamento> example = Example.of(filtro,
                ExampleMatcher.matching()
                        .withIgnoreCase()
                        .withIgnorePaths("delegadaParaComissao")
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));
        return repository.findAll(example);
    }

    private void verificarId(SolicitacaoAproveitamento solicitacao) {
        if ((solicitacao == null) || (solicitacao.getId() == null))
            throw new RegraNegocioRunTime("Solicitacao invalida (sem id).");
    }

    private void verificaSolicitacao(SolicitacaoAproveitamento solicitacao) {
        if (solicitacao == null)
            throw new RegraNegocioRunTime("Uma solicitacao valida deve ser informada.");
        if (solicitacao.getSolicitante() == null)
            throw new RegraNegocioRunTime("Solicitante deve ser informado.");
        if (solicitacao.getCertificado() == null)
            throw new RegraNegocioRunTime("Certificado deve ser informado.");
    }
}
