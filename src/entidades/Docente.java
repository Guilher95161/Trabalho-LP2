package entidades;

public class Docente extends Usuario {

    public Docente(String nome, String matricula, String email, String senha) {
        super(nome, matricula, email, senha, "DOCENTE");
    }
}