package br.ufma.extensao.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "curso")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Curso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_curso")
    private Integer id;

    @Column(name = "nome")
    private String nome;

    @Column(name = "curriculo")
    private String curriculo;

    @Column(name = "carga_horaria")
    private Integer cargaHoraria;
}
