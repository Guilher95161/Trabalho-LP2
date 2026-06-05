package br.ufma.model.entidades;

public class Comissao extends Usuario {

    public Comissao(String nome, String matricula, String email, String senha) {
        super(nome, matricula, email, senha, "COMISSAO");
    }
}
