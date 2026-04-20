package entidades;

import java.util.ArrayList;
import java.util.List;

public class Discente extends Usuario {
    private int horasCumpridas;
    private List<Certificado> certificados;

    public Discente(String nome, String matricula, String email, String senha) {
        super(nome, matricula, email, senha, "DISCENTE");
        this.horasCumpridas = 0;
        this.certificados = new ArrayList<>();
    }

    public int getHorasCumpridas(){
        return horasCumpridas;
    }
    public List<Certificado> getCertificados(){
        return certificados;
    }

    public void adicionarHoras(int horas) {
        this.horasCumpridas += horas;
    }

    public void adicionarCertificado(Certificado c) {
        certificados.add(c);
    }
}