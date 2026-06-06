package br.ufma.model.entidades;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "discente")
@PrimaryKeyJoinColumn(name = "id_usuario")
public class Discente extends Usuario {
    private int horasCumpridas;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "discente_id")
    private List<Certificado> certificados = new ArrayList<>();

    // alunos do mesmo curso podem ter PPCs diferentes (turmas distintas)
    @ManyToOne
    @JoinColumn(name = "ppc_id")
    private Ppc ppc;

    // construtor vazio exigido pelo JPA
    protected Discente() {
    }

    public Discente(String nome, String matricula, String email, String senha) {
        super(nome, matricula, email, senha, "DISCENTE");
        this.horasCumpridas = 0;
        this.ppc = null;
    }

    public Discente(String nome, String matricula, String email, String senha, Ppc ppc) {
        this(nome, matricula, email, senha);
        this.ppc = ppc;
    }

    public int getHorasCumpridas(){
        return horasCumpridas;
    }
    public List<Certificado> getCertificados(){
        return certificados;
    }
    public Ppc getPpc() {
        return ppc;
    }
    public void setPpc(Ppc ppc) {
        this.ppc = ppc;
    }

    public Curso getCurso() {
        return (ppc != null) ? ppc.getCurso() : null;
    }

    public void adicionarHoras(int horas) {
        this.horasCumpridas += horas;
    }

    public void adicionarCertificado(Certificado c) {
        certificados.add(c);
    }
}