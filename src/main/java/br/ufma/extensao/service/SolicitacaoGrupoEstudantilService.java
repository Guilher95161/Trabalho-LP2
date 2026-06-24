package br.ufma.extensao.service;

import br.ufma.extensao.model.Discente;
import br.ufma.extensao.model.GrupoEstudantil;
import br.ufma.extensao.model.HistoricoCargo;
import br.ufma.extensao.model.MembroGrupo;
import br.ufma.extensao.model.SolicitacaoGrupoEstudantil;
import br.ufma.extensao.model.enums.CargoGrupo;
import br.ufma.extensao.model.enums.StatusSolicitacao;
import br.ufma.extensao.repo.GrupoEstudantilRepository;
import br.ufma.extensao.repo.SolicitacaoGrupoEstudantilRepository;
import br.ufma.extensao.service.exceptions.EntidadeNaoEncontradaException;
import br.ufma.extensao.service.exceptions.OperacaoInvalidaException;
import br.ufma.extensao.service.exceptions.RegraNegocioRunTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class SolicitacaoGrupoEstudantilService {

    @Autowired
    SolicitacaoGrupoEstudantilRepository repository;

    @Autowired
    GrupoEstudantilRepository grupoRepository;

    @Transactional
    public SolicitacaoGrupoEstudantil salvar(SolicitacaoGrupoEstudantil solicitacao) {
        verificaSolicitacao(solicitacao);
        solicitacao.setStatus(StatusSolicitacao.PENDENTE);
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
        remover(buscarObrigatoria(id));
    }

    // aprovar cria o grupo a partir da solicitacao; o solicitante vira PRESIDENTE
    @Transactional
    public SolicitacaoGrupoEstudantil avaliar(Integer id, boolean aprovado) {
        SolicitacaoGrupoEstudantil solicitacao = buscarObrigatoria(id);
        if (solicitacao.getStatus() != StatusSolicitacao.PENDENTE)
            throw new OperacaoInvalidaException("So e possivel avaliar uma solicitacao PENDENTE.");
        if (aprovado) {
            solicitacao.setStatus(StatusSolicitacao.DEFERIDA);
            criarGrupoDaSolicitacao(solicitacao);
        } else {
            solicitacao.setStatus(StatusSolicitacao.INDEFERIDA);
        }
        return repository.save(solicitacao);
    }

    public List<SolicitacaoGrupoEstudantil> listarPendentes() {
        return repository.findByStatus(StatusSolicitacao.PENDENTE);
    }

    public List<SolicitacaoGrupoEstudantil> buscar(SolicitacaoGrupoEstudantil filtro) {
        Example<SolicitacaoGrupoEstudantil> example = Example.of(filtro,
                ExampleMatcher.matching()
                        .withIgnoreCase()
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));
        return repository.findAll(example);
    }

    private void criarGrupoDaSolicitacao(SolicitacaoGrupoEstudantil solicitacao) {
        LocalDate hoje = LocalDate.now();
        Discente solicitante = solicitacao.getSolicitante();
        MembroGrupo presidente = MembroGrupo.builder()
                .discente(solicitante)
                .cargo(CargoGrupo.PRESIDENTE)
                .dataEntrada(hoje)
                .build();
        HistoricoCargo historico = HistoricoCargo.builder()
                .discente(solicitante)
                .cargo(CargoGrupo.PRESIDENTE)
                .dataInicio(hoje)
                .build();
        GrupoEstudantil grupo = GrupoEstudantil.builder()
                .nome(solicitacao.getNomeGrupo())
                .descricao(solicitacao.getDescricao())
                .responsavel(solicitacao.getDocenteResponsavel())
                .membros(new ArrayList<>(List.of(presidente)))
                .historicoCargos(new ArrayList<>(List.of(historico)))
                .build();
        grupoRepository.save(grupo);
    }

    private SolicitacaoGrupoEstudantil buscarObrigatoria(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Solicitacao de grupo nao encontrada."));
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
