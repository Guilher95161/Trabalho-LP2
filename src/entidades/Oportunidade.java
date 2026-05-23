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

    // RF009/RF0009 - Unidade Curricular de Extensao
    private boolean uce;
    private UnidadeCurricular uceVinculada;  // referencia uma UCE de algum PPC; null se nao for UCE
    private String componenteCurricular;     // texto livre - usado quando nao ha UCE concreta no PPC

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
        this.uce = false;
        this.uceVinculada = null;
        this.componenteCurricular = null;
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
    public boolean isUce()                  { return uce; }
    public UnidadeCurricular getUceVinculada() { return uceVinculada; }
    public String getComponenteCurricular() {
        if (uceVinculada != null) return uceVinculada.getCodigo() + " - " + uceVinculada.getNome();
        return componenteCurricular;
    }

    // RF009 - marca como UCE com texto livre (sem vinculo a um PPC especifico)
    public void marcarComoUce(String componenteCurricular) {
        this.uce = true;
        this.componenteCurricular = componenteCurricular;
        this.uceVinculada = null;
    }

    // RF0009 - marca como UCE vinculada a uma UCE concreta de um PPC
    public void marcarComoUce(UnidadeCurricular u) {
        this.uce = true;
        this.uceVinculada = u;
        this.componenteCurricular = null;
    }

    public void desmarcarUce() {
        this.uce = false;
        this.uceVinculada = null;
        this.componenteCurricular = null;
    }

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

    public void iniciarExecucao() {
        if (this.status == StatusOportunidade.ABERTA) {
            this.status = StatusOportunidade.EM_EXECUCAO;
        }
    }

    public void encerrar() {
        // Pode ser encerrada tanto de ABERTA quanto de EM_EXECUCAO
        if (this.status == StatusOportunidade.ABERTA || this.status == StatusOportunidade.EM_EXECUCAO) {
            this.status = StatusOportunidade.ENCERRADA;
        }
    }

    /**
     * RF012 - cancela a oportunidade. So permite enquanto nao foi ENCERRADA.
     */
    public boolean cancelar() {
        if (this.status == StatusOportunidade.ENCERRADA
                || this.status == StatusOportunidade.CANCELADA) {
            return false;
        }
        this.status = StatusOportunidade.CANCELADA;
        return true;
    }

    @Override
    public String toString() {
        String uceInfo = "";
        if (uce) {
            String label = getComponenteCurricular();
            uceInfo = " | [UCE: " + (label != null ? label : "?") + "]";
        }
        return "[" + id + "] " + titulo +
               " | " + modalidade +
               " | " + periodoRealizacao +
               " | " + cargaHoraria + "h" +
               " | Vagas: " + inscritosAprovados.size() + "/" + vagas +
               " | Status: " + status +
               " | Resp: " + responsavel.getNome() +
               uceInfo;
    }
}
