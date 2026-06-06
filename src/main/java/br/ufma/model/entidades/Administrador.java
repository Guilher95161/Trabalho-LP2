package br.ufma.model.entidades;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "administrador")
@PrimaryKeyJoinColumn(name = "id_usuario")
public class Administrador extends Usuario {

    // construtor vazio exigido pelo JPA
    protected Administrador() {
    }

    public Administrador(String nome, String matricula, String email, String senha) {
        super(nome, matricula, email, senha, "ADMINISTRADOR");
    }
}