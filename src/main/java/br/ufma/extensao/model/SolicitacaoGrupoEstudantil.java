package br.ufma.extensao.model;

import br.ufma.extensao.model.enums.StatusSolicitacao;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "solicitacao_grupo_estudantil")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class SolicitacaoGrupoEstudantil {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_solicitacao_grupo")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "solicitante_id")
    private Discente solicitante;

    @Column(name = "nome_grupo")
    private String nomeGrupo;

    @Column(name = "descricao")
    private String descricao;

    @ManyToOne
    @JoinColumn(name = "docente_responsavel_id")
    private Usuario docenteResponsavel;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StatusSolicitacao status;
}
