package entidades;

import java.util.ArrayList;
import java.util.List;

public class Discente extends Usuario {
    private int horasCumpridas;
    private List<Certificado> certificados;
    // alunos do mesmo curso podem ter PPCs diferentes (turmas distintas)
    private Ppc ppc;

    public Discente(String nome, String matricula, String email, String senha) {
        super(nome, matricula, email, senha, "DISCENTE");
        this.horasCumpridas = 0;
        this.certificados = new ArrayList<>();
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