package br.ufma.extensao.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "grupo_estudantil")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GrupoEstudantil {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_grupo")
    private Integer id;

    @Column(name = "nome")
    private String nome;

    @Column(name = "descricao")
    private String descricao;

    @ManyToOne
    @JoinColumn(name = "responsavel_id")
    private Usuario responsavel;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "grupo_id")
    @ToString.Exclude
    private List<MembroGrupo> membros;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "grupo_id")
    @ToString.Exclude
    private List<HistoricoCargo> historicoCargos;
}
