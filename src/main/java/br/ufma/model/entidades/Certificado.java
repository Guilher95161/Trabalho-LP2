package br.ufma.model.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "certificado")
public class Certificado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_certificado")
    private Integer id;

    private String tituloAtividade;
    private int cargaHoraria;

    @Column(name = "data_emissao")
    private LocalDate data;

    // evita que o mesmo cert seja enviado duas vezes
    private boolean aproveitamentoSolicitado;

    // construtor vazio exigido pelo JPA
    protected Certificado() {
    }

    public Certificado(String tituloAtividade, int cargaHoraria, LocalDate data) {
        this.tituloAtividade = tituloAtividade;
        this.cargaHoraria = cargaHoraria;
        this.data = data;
        this.aproveitamentoSolicitado = false;
    }

    public Integer getId()                   { return id; }
    public String getTituloAtividade()       { return tituloAtividade; }
    public int getCargaHoraria()             { return cargaHoraria; }
    public LocalDate getData()               { return data; }
    public boolean isAproveitamentoSolicitado() { return aproveitamentoSolicitado; }

    public void marcarAproveitamentoSolicitado()  { this.aproveitamentoSolicitado = true; }
    public void liberarAproveitamento()           { this.aproveitamentoSolicitado = false; }

    @Override
    public String toString() {
        String status = aproveitamentoSolicitado ? " [aproveitamento enviado]" : "";
        return tituloAtividade + " | " + cargaHoraria + "h | " + data + status;
    }
}
