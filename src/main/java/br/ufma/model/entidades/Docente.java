package br.ufma.model.entidades;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "docente")
@PrimaryKeyJoinColumn(name = "id_usuario")
public class Docente extends Usuario {

    // construtor vazio exigido pelo JPA
    protected Docente() {
    }

    public Docente(String nome, String matricula, String email, String senha) {
        super(nome, matricula, email, senha, "DOCENTE");
    }
}