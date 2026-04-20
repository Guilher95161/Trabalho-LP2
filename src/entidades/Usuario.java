package entidades;

public class Usuario {
    private String nome;
    private String matricula;
    private String email;
    private String senha;
    private String tipo; // "DISCENTE", "DOCENTE", "GESTOR", "ADMINISTRADOR"

    public Usuario(String nome, String matricula, String email, String senha, String tipo) {
        this.nome = nome;
        this.matricula = matricula;
        this.email = email;
        this.senha = senha;
        this.tipo = tipo;
    }

    public String getNome(){
        return nome;
    }
    public String getMatricula(){
        return matricula;
    }
    public String getEmail(){
        return email;
    }
    public String getSenha(){
        return senha;
    }
    public String getTipo(){
        return tipo;
    }

    public void setNome(String nome){
        this.nome = nome;
    }
    public void setMatricula(String matricula){
        this.matricula = matricula;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public void setSenha(String senha){
        this.senha = senha;
    }
    public void setTipo(String tipo){
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return "[" + tipo + "] " + nome + " (" + email + ")";
    }
}