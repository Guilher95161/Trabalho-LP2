package entidades;

public class Administrador extends Usuario {

    public Administrador(String nome, String matricula, String email, String senha) {
        super(nome, matricula, email, senha, "ADMINISTRADOR");
    }
}