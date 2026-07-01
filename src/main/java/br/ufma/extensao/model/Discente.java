package br.ufma.extensao.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "discente")
@PrimaryKeyJoinColumn(name = "id_usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true)
public class Discente extends Usuario {
    @Column(name = "horas_cumpridas")
    private int horasCumpridas;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "discente_id")
    @ToString.Exclude
    private List<Certificado> certificados;

    @ManyToOne
    @JoinColumn(name = "curso_id")
    private Curso curso;
}
