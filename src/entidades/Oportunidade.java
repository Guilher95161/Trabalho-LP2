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

    // Marcacao opcional como UCE. uceVinculada referencia a UCE concreta de um PPC;
    // quando nao temos a UCE no sistema, cai no texto livre componenteCurricular.
    private boolean uce;
    private UnidadeCurricular uceVinculada;
    private String componenteCurricular;

    // Set garante unicidade no proprio tipo e da contains O(1).
    // LinkedHashSet mantem a ordem de chegada (FIFO da fila de espera).
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

    // Versao texto livre - util quando o PPC ainda nao tem essa UCE cadastrada.
    public void marcarComoUce(String componenteCurricular) {
        this.uce = true;
        this.componenteCurricular = componenteCurricular;
        this.uceVinculada = null;
    }

    // Versao vinculada a uma UCE concreta de algum PPC.
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

    // Devolvem List (e nao Set) porque varios pontos do menu precisam de .get(idx).
    // Imutavel para evitar que alguem altere a fila por fora.
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

    // Tira do rascunho. Discente precisa passar pela aprovacao docente;
    // os demais perfis vao direto para ABERTA.
    public boolean submeterRascunho() {
        if (this.status != StatusOportunidade.RASCUNHO) return false;
        if (this.responsavel instanceof Discente) {
            this.status = StatusOportunidade.AGUARDANDO_APROVACAO;
        } else {
            this.status = StatusOportunidade.ABERTA;
        }
        return true;
    }

    // Edicao so e permitida enquanto o rascunho nao foi submetido.
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
        // Se o discente ja esta aprovado nao faz sentido voltar para a fila.
        // Estar duas vezes na fila o Set ja resolve sozinho.
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

    // Tira um aprovado da lista e promove alguem que estava esperando.
    // A justificativa fica a cargo de quem chama (gravada no menu).
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
        // Aceita tanto ABERTA quanto EM_EXECUCAO - as duas representam atividade ainda viva.
        if (this.status == StatusOportunidade.ABERTA || this.status == StatusOportunidade.EM_EXECUCAO) {
            this.status = StatusOportunidade.ENCERRADA;
        }
    }

    // Cancelar nao faz sentido se ja terminou ou ja foi cancelada antes.
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
