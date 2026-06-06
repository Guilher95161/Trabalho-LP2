package br.ufma.model.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "usuario")
public abstract class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer id;

    private String nome;
    private String matricula;

    @Column(name = "email", unique = true)
    private String email;

    private String senha;
    private String tipo;
    private boolean ativo;

    // construtor vazio exigido pelo JPA
    protected Usuario() {
    }

    public Usuario(String nome, String matricula, String email, String senha, String tipo) {
        this.nome = nome;
        this.matricula = matricula;
        this.email = email;
        this.senha = senha;
        this.tipo = tipo;
        this.ativo = true;
    }

    public Integer getId(){
        return id;
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

    public boolean isAtivo(){
        return ativo;
    }

    public void desativarConta(){
        this.ativo = false;
    }

    @Override
    public String toString() {
        String status = ativo ? "ATIVO" : "INATIVO";
        return "[" + tipo + " - " + status + "] " + nome + " (" + email + ")";
    }

    // email e o identificador unico do usuario
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usuario)) return false;
        Usuario outro = (Usuario) o;
        return email != null && email.equals(outro.email);
    }

    @Override
    public int hashCode() {
        return (email == null) ? 0 : email.hashCode();
    }
}