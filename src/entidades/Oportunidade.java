package entidades;

import java.util.ArrayList;
import java.util.List;

public class Oportunidade {
    private static int contador = 1;

    private int id;
    private String titulo;
    private int cargaHoraria;
    private int vagas;
    private Docente responsavel;
    private String status; // "ABERTA", "ENCERRADA", "CANCELADA"
    private List<Discente> inscritos;

    public Oportunidade(String titulo, int cargaHoraria, int vagas, Docente responsavel) {
        this.id = contador++;
        this.titulo = titulo;
        this.cargaHoraria = cargaHoraria;
        this.vagas = vagas;
        this.responsavel = responsavel;
        this.status = "ABERTA";
        this.inscritos = new ArrayList<>();
    }

    public int getId(){
        return id;
    }
    public String getTitulo(){
        return titulo;
    }
    public int getCargaHoraria(){
        return cargaHoraria;
    }
    public int getVagas(){
        return vagas;
    }
    public Docente getResponsavel(){
        return responsavel;
    }
    public String getStatus(){
        return status;
    }
    public List<Discente> getInscritos(){
        return inscritos;
    }

    public void inscreverDiscente(Discente d) {
        if (!inscritos.contains(d))
            inscritos.add(d);
    }

    public void cancelarInscricao(Discente d) {
        inscritos.remove(d);
    }

    public void encerrar() {
        this.status = "ENCERRADA";
    }

    public void cancelar() {
        this.status = "CANCELADA";
    }

    @Override
    public String toString() {
        return "[" + id + "] " + titulo + " | " + cargaHoraria + "h | Vagas: " + vagas + " | Status: " + status + " | Resp: " + responsavel.getNome();
    }
}