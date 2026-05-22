package entidades;

import entidades.enums.StatusOportunidade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Oportunidade {
    private static int contador = 1;

    private int id;
    private String titulo;
    private int cargaHoraria;
    private int vagas;
    private Usuario responsavel;
    private StatusOportunidade status;// "ABERTA", "ENCERRADA", "CANCELADA"

    private List<Discente> filaEspera;
    private List<Discente> inscritosAprovados;

    public Oportunidade(String titulo, int cargaHoraria, int vagas, Usuario responsavel, StatusOportunidade status) {
        this.id = contador++;
        this.titulo = titulo;
        this.cargaHoraria = cargaHoraria;
        this.vagas = vagas;
        this.responsavel = responsavel;
        this.status = status;
        this.filaEspera = new ArrayList<>();
        this.inscritosAprovados = new ArrayList<>();
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
    public Usuario getResponsavel(){
        return responsavel;
    }
    public StatusOportunidade getStatus(){
        return status;
    }
    public List<Discente> getFilaEspera(){
        return Collections.unmodifiableList(filaEspera);
    }
    public List<Discente> getInscritosAprovados(){
        return Collections.unmodifiableList(inscritosAprovados);
    }

    public void aprovarProposta(){
        if(this.status == StatusOportunidade.AGUARDANDO_APROVACAO){
            this.status = StatusOportunidade.ABERTA;
        }
    }

    public void solicitarInscricao(Discente discente){
        if (!filaEspera.contains(discente) && !inscritosAprovados.contains(discente)){
            filaEspera.add(discente);
        }
    }

    public void avaliarInscricao(Discente discente, boolean aprovar){
        if(filaEspera.contains(discente)){
            filaEspera.remove(discente);
            if (aprovar && inscritosAprovados.size() < vagas){
                inscritosAprovados.add(discente);
            }
        }
    }

    public void cancelarInscricao(Discente discente){
        filaEspera.remove(discente);
        inscritosAprovados.remove(discente);
    }

    public void encerrar() {
        this.status = StatusOportunidade.ENCERRADA;
    }
    public void cancelar() {
        this.status = StatusOportunidade.CANCELADA;
    }

    @Override
    public String toString() {
        return "[" + id + "] " + titulo + " | " + cargaHoraria + "h | Vagas: " + inscritosAprovados.size() + "/" + vagas + " | Status: " + status + " | Resp: " + responsavel.getNome();
    }
}