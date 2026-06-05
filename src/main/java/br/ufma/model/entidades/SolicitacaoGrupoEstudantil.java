package br.ufma.model.entidades;

import br.ufma.model.entidades.enums.StatusSolicitacao;

public class SolicitacaoGrupoEstudantil {
    private static int contador = 1;

    private int id;
    private Discente solicitante;
    private String nomeGrupo;
    private String descricao;
    private Docente docenteResponsavel;
    private StatusSolicitacao status;

    public SolicitacaoGrupoEstudantil(Discente solicitante, String nomeGrupo, String descricao, Docente docenteResponsavel) {
        this.id = contador++;
        this.solicitante = solicitante;
        this.nomeGrupo = nomeGrupo;
        this.descricao = descricao;
        this.docenteResponsavel = docenteResponsavel;
        this.status = StatusSolicitacao.PENDENTE;
    }

    public int getId() {
        return id;
    }

    public Discente getSolicitante() {
        return solicitante;
    }

    public String getNomeGrupo() {
        return nomeGrupo;
    }

    public String getDescricao() {
        return descricao;
    }

    public Docente getDocenteResponsavel() {
        return docenteResponsavel;
    }

    public StatusSolicitacao getStatus() {
        return status;
    }

    public void aprovar(){
        this.status = StatusSolicitacao.DEFERIDA;
    }

    public void reprovar(){
        this.status = StatusSolicitacao.INDEFERIDA;
    }

    @Override
    public String toString() {
        return "[" + id + "]" + "Grupo: " + nomeGrupo +
                " | Solicitante: " + solicitante.getNome() +
                " | Docente Responsavel: " + docenteResponsavel.getNome() +
                " | Status: " + status;
    }

}
