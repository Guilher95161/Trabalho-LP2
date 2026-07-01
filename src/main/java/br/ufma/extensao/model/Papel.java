package br.ufma.extensao.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "papel")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Papel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_papel")
    private Integer id;

    @Column(name = "nome")
    private String nome;
}
