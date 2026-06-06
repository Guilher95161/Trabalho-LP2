package br.ufma.model.entidades;

import br.ufma.model.entidades.enums.StatusSolicitacao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "solicitacao_grupo")
public class SolicitacaoGrupoEstudantil {
    private static int contador = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_solicitacao_grupo")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "solicitante_id")
    private Discente solicitante;

    private String nomeGrupo;
    private String descricao;

    @ManyToOne
    @JoinColumn(name = "docente_id")
    private Docente docenteResponsavel;

    @Enumerated(EnumType.STRING)
    private StatusSolicitacao status;

    // construtor vazio exigido pelo JPA
    protected SolicitacaoGrupoEstudantil() {
    }

    public SolicitacaoGrupoEstudantil(Discente solicitante, String nomeGrupo, String descricao, Docente docenteResponsavel) {
        this.id = contador++;
        this.solicitante = solicitante;
        this.nomeGrupo = nomeGrupo;
        this.descricao = descricao;
        this.docenteResponsavel = docenteResponsavel;
        this.status = StatusSolicitacao.PENDENTE;
    }

    public Integer getId() {
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
