package br.ufma.extensao.model;

import br.ufma.extensao.model.enums.CargoGrupo;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "historico_cargo")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoricoCargo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historico_cargo")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "discente_id")
    private Discente discente;

    @Enumerated(EnumType.STRING)
    @Column(name = "cargo")
    private CargoGrupo cargo;

    @Column(name = "data_inicio")
    private LocalDate dataInicio;

    @Column(name = "data_fim")
    private LocalDate dataFim;
}
