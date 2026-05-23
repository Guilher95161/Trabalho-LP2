package entidades;

import java.util.ArrayList;
import java.util.List;

public class Discente extends Usuario {
    private int horasCumpridas;
    private List<Certificado> certificados;
    private Ppc ppc; // RF0009 - vinculo com versao especifica do PPC

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

    // Helper - retorna o Curso atraves do PPC (compatibilidade com codigo antigo)
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