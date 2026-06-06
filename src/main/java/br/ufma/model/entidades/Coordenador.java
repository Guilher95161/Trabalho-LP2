package br.ufma.model.entidades;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "coordenador")
@PrimaryKeyJoinColumn(name = "id_usuario")
public class Coordenador extends Usuario {

    // construtor vazio exigido pelo JPA
    protected Coordenador() {
    }

    public Coordenador(String nome, String matricula, String email, String senha) {
        super(nome, matricula, email, senha, "COORDENADOR");
    }
}
