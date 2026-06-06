package br.ufma.model.entidades;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "comissao")
@PrimaryKeyJoinColumn(name = "id_usuario")
public class Comissao extends Usuario {

    // construtor vazio exigido pelo JPA
    protected Comissao() {
    }

    public Comissao(String nome, String matricula, String email, String senha) {
        super(nome, matricula, email, senha, "COMISSAO");
    }
}
