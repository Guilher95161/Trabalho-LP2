package br.ufma.model.entidades;

public class Coordenador extends Usuario {

    public Coordenador(String nome, String matricula, String email, String senha) {
        super(nome, matricula, email, senha, "COORDENADOR");
    }
}
