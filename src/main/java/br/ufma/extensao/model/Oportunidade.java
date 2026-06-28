package br.ufma.extensao.model;

import br.ufma.extensao.model.enums.ModalidadeOportunidade;
import br.ufma.extensao.model.enums.StatusOportunidade;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "oportunidade")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString
public class Oportunidade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_oportunidade")
    private Integer id;

    @Column(name = "titulo")
    private String titulo;

    @Column(name = "descricao")
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(name = "modalidade")
    private ModalidadeOportunidade modalidade;

    @Column(name = "periodo_realizacao")
    private String periodoRealizacao;

    @Column(name = "carga_horaria")
    private int cargaHoraria;

    @Column(name = "vagas")
    private int vagas;

    @ManyToOne
    @JoinColumn(name = "responsavel_id")
    private Usuario responsavel;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StatusOportunidade status;

    @Column(name = "motivo_cancelamento")
    private String motivoCancelamento;

    @ManyToMany
    @JoinTable(
            name = "oportunidade_fila_espera",
            joinColumns = @JoinColumn(name = "id_oportunidade"),
            inverseJoinColumns = @JoinColumn(name = "id_usuario"))
    @OrderColumn(name = "posicao")
    @ToString.Exclude
    private List<Discente> filaEspera = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "oportunidade_inscritos",
            joinColumns = @JoinColumn(name = "id_oportunidade"),
            inverseJoinColumns = @JoinColumn(name = "id_usuario"))
    @OrderColumn(name = "posicao")
    @ToString.Exclude
    private List<Discente> inscritosAprovados = new ArrayList<>();
}
