package br.ufma.extensao.model;

import br.ufma.extensao.model.enums.StatusSolicitacao;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "solicitacao_aproveitamento")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString
public class SolicitacaoAproveitamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_solicitacao")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "solicitante_id")
    private Discente solicitante;

    @ManyToOne
    @JoinColumn(name = "certificado_id")
    private Certificado certificado;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StatusSolicitacao status;

    @Column(name = "parecer")
    private String parecer;

    @Column(name = "delegada_para_comissao")
    private boolean delegadaParaComissao;

    @Column(name = "data_criacao")
    private LocalDate dataCriacao;

    @Column(name = "data_avaliacao")
    private LocalDate dataAvaliacao;
}
