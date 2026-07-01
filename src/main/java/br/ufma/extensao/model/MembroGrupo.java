package br.ufma.extensao.model;

import br.ufma.extensao.model.enums.CargoGrupo;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "membro_grupo")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class MembroGrupo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_membro_grupo")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "discente_id")
    private Discente discente;

    @Enumerated(EnumType.STRING)
    @Column(name = "cargo")
    private CargoGrupo cargo;

    @Column(name = "data_entrada")
    private LocalDate dataEntrada;
}
