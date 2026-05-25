package entidades;

import entidades.enums.ModalidadeOportunidade;
import entidades.enums.StatusOportunidade;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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

    // marcacao de UCE: vinculada a uma UCE concreta ou so como texto livre
    private boolean uce;
    private UnidadeCurricular uceVinculada;
    private String componenteCurricular;

    private Set<Discente> filaEspera;
    private Set<Discente> inscritosAprovados;

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
        this.filaEspera = new LinkedHashSet<>();
        this.inscritosAprovados = new LinkedHashSet<>();
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

    public void marcarComoUce(String componenteCurricular) {
        this.uce = true;
        this.componenteCurricular = componenteCurricular;
        this.uceVinculada = null;
    }

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

    // retorna imutavel pra ninguem alterar a fila por fora
    public List<Discente> getFilaEspera() {
        return Collections.unmodifiableList(new ArrayList<>(filaEspera));
    }
    public List<Discente> getInscritosAprovados() {
        return Collections.unmodifiableList(new ArrayList<>(inscritosAprovados));
    }

    public void aprovarProposta() {
        if (this.status == StatusOportunidade.AGUARDANDO_APROVACAO) {
            this.status = StatusOportunidade.ABERTA;
        }
    }

    // discente vai pra aguardando aprovacao; outros perfis vao direto pra ABERTA
    public boolean submeterRascunho() {
        if (this.status != StatusOportunidade.RASCUNHO) return false;
        if (this.responsavel instanceof Discente) {
            this.status = StatusOportunidade.AGUARDANDO_APROVACAO;
        } else {
            this.status = StatusOportunidade.ABERTA;
        }
        return true;
    }

    public boolean editarSeRascunho(String titulo, String descricao,
                                    ModalidadeOportunidade modalidade,
                                    String periodoRealizacao,
                                    int cargaHoraria, int vagas) {
        if (this.status != StatusOportunidade.RASCUNHO) return false;
        this.titulo = titulo;
        this.descricao = descricao;
        this.modalidade = modalidade;
        this.periodoRealizacao = periodoRealizacao;
        this.cargaHoraria = cargaHoraria;
        this.vagas = vagas;
        return true;
    }

    public void solicitarInscricao(Discente discente) {
        // ja aprovado nao entra na fila de espera de novo
        if (!inscritosAprovados.contains(discente)) {
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

    // remove um aprovado e promove quem ta esperando na fila
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
        if (this.status == StatusOportunidade.ABERTA || this.status == StatusOportunidade.EM_EXECUCAO) {
            this.status = StatusOportunidade.ENCERRADA;
        }
    }

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
