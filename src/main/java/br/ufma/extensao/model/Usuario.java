package br.ufma.extensao.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "usuario")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@EqualsAndHashCode(of = "email")
@ToString
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer id;

    @Column(name = "nome")
    private String nome;

    @Column(name = "matricula")
    private String matricula;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "senha")
    @ToString.Exclude
    private String senha;

    @Column(name = "ativo")
    private boolean ativo;

    @ManyToMany
    @JoinTable(
            name = "usuario_papel",
            joinColumns = @JoinColumn(name = "id_usuario"),
            inverseJoinColumns = @JoinColumn(name = "id_papel"))
    @ToString.Exclude
    private List<Papel> papeis;
}
