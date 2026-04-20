package entidades;

public class Gestor extends Usuario {

    public Gestor(String nome, String matricula, String email, String senha) {
        super(nome, matricula, email, senha, "GESTOR");
    }
}