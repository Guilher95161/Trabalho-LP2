package entidades;

import entidades.enums.ModalidadeOportunidade;
import entidades.enums.StatusOportunidade;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Oportunidade {
    private static int contador = 1;

    private int id;
    private String titulo;
    private String descricao;
    private ModalidadeOportunidade modalidade;
    private String periodoRealizacao;
    private int cargaHoraria;
    private int vagas;
    private Usuario responsavel;
    private StatusOportunidade status;

    private List<Discente> filaEspera;
    private List<Discente> inscritosAprovados;

    public Oportunidade(String titulo, String descricao, ModalidadeOportunidade modalidade,
                        String periodoRealizacao, int cargaHoraria, int vagas,
                        Usuario responsavel, StatusOportunidade status) {
        this.id = contador++;
        this.titulo = titulo;
        this.descricao = descricao;
        this.modalidade = modalidade;
        this.periodoRealizacao = periodoRealizacao;
        this.cargaHoraria = cargaHoraria;
        this.vagas = vagas;
        this.responsavel = responsavel;
        this.status = status;
        this.filaEspera = new ArrayList<>();
        this.inscritosAprovados = new ArrayList<>();
    }

    public int getId()                      { return id; }
    public String getTitulo()               { return titulo; }
    public String getDescricao()            { return descricao; }
    public ModalidadeOportunidade getModalidade() { return modalidade; }
    public String getPeriodoRealizacao()    { return periodoRealizacao; }
    public int getCargaHoraria()            { return cargaHoraria; }
    public int getVagas()                   { return vagas; }
    public Usuario getResponsavel()         { return responsavel; }
    public StatusOportunidade getStatus()   { return status; }

    public List<Discente> getFilaEspera() {
        return Collections.unmodifiableList(filaEspera);
    }
    public List<Discente> getInscritosAprovados() {
        return Collections.unmodifiableList(inscritosAprovados);
    }

    public void aprovarProposta() {
        if (this.status == StatusOportunidade.AGUARDANDO_APROVACAO) {
            this.status = StatusOportunidade.ABERTA;
        }
    }

    public void solicitarInscricao(Discente discente) {
        if (!filaEspera.contains(discente) && !inscritosAprovados.contains(discente)) {
            filaEspera.add(discente);
        }
    }

    public void avaliarInscricao(Discente discente, boolean aprovar) {
        if (filaEspera.contains(discente)) {
            filaEspera.remove(discente);
            if (aprovar && inscritosAprovados.size() < vagas) {
                inscritosAprovados.add(discente);
            }
        }
    }

    public void cancelarInscricao(Discente discente) {
        filaEspera.remove(discente);
        inscritosAprovados.remove(discente);
    }

    // Remove aprovado com justificativa e aprova substituto da fila de espera
    public boolean substituirParticipante(Discente aRemover, Discente substituto) {
        if (!inscritosAprovados.contains(aRemover)) return false;
        if (!filaEspera.contains(substituto)) return false;
        inscritosAprovados.remove(aRemover);
        filaEspera.remove(substituto);
        inscritosAprovados.add(substituto);
        return true;
    }

    public void encerrar() { this.status = StatusOportunidade.ENCERRADA; }
    public void cancelar() { this.status = StatusOportunidade.CANCELADA; }

    @Override
    public String toString() {
        return "[" + id + "] " + titulo +
               " | " + modalidade +
               " | " + periodoRealizacao +
               " | " + cargaHoraria + "h" +
               " | Vagas: " + inscritosAprovados.size() + "/" + vagas +
               " | Status: " + status +
               " | Resp: " + responsavel.getNome();
    }
}
