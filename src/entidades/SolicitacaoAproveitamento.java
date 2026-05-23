package entidades;

import entidades.enums.StatusSolicitacao;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class SolicitacaoAproveitamento {
    private static int contador = 1;

    // RF022: prazos
    public static final int PRAZO_AVALIACAO_DIAS = 10; // Coordenador/Comissao deve avaliar em ate 10 dias
    public static final int PRAZO_REENVIO_DIAS = 5;    // Discente tem 5 dias para reenviar uma INDEFERIDA

    private int id;
    private Discente solicitante;
    private Certificado certificado;
    private StatusSolicitacao status;
    private String parecer;
    private boolean delegadaParaComissao;

    private LocalDate dataCriacao;       // quando entrou como PENDENTE
    private LocalDate dataAvaliacao;     // quando foi DEFERIDA ou INDEFERIDA

    public SolicitacaoAproveitamento(Discente solicitante, Certificado certificado) {
        this.id = contador++;
        this.solicitante = solicitante;
        this.certificado = certificado;
        this.status = StatusSolicitacao.PENDENTE;
        this.parecer = "";
        this.delegadaParaComissao = false;
        this.dataCriacao = LocalDate.now();
        this.dataAvaliacao = null;
    }

    public int getId()                         { return id; }
    public Discente getSolicitante()           { return solicitante; }
    public Certificado getCertificado()        { return certificado; }
    public StatusSolicitacao getStatus()       { return status; }
    public String getParecer()                 { return parecer; }
    public boolean isDelegadaParaComissao()    { return delegadaParaComissao; }
    public LocalDate getDataCriacao()          { return dataCriacao; }
    public LocalDate getDataAvaliacao()        { return dataAvaliacao; }

    public void avaliarSolicitacao(boolean aprovado, String parecer) {
        this.parecer = parecer;
        this.status = aprovado ? StatusSolicitacao.DEFERIDA : StatusSolicitacao.INDEFERIDA;
        this.dataAvaliacao = LocalDate.now();
    }

    public void reenviar() {
        this.status = StatusSolicitacao.PENDENTE;
        this.parecer = "";
        this.dataCriacao = LocalDate.now();
        this.dataAvaliacao = null;
    }

    public void cancelar() {
        this.status = StatusSolicitacao.CANCELADA;
    }

    public void delegarParaComissao() {
        this.delegadaParaComissao = true;
    }

    // RF022 - prazos
    public long diasDesdeCriacao() {
        return ChronoUnit.DAYS.between(dataCriacao, LocalDate.now());
    }

    public long diasDesdeAvaliacao() {
        if (dataAvaliacao == null) return 0;
        return ChronoUnit.DAYS.between(dataAvaliacao, LocalDate.now());
    }

    // True se o coordenador/comissao passou do prazo de 10 dias
    public boolean isAvaliacaoAtrasada() {
        return status == StatusSolicitacao.PENDENTE
            && diasDesdeCriacao() > PRAZO_AVALIACAO_DIAS;
    }

    // True se o discente ainda esta dentro dos 5 dias para reenviar
    public boolean podeReenviar() {
        return status == StatusSolicitacao.INDEFERIDA
            && diasDesdeAvaliacao() <= PRAZO_REENVIO_DIAS;
    }

    public long diasRestantesReenvio() {
        if (status != StatusSolicitacao.INDEFERIDA) return 0;
        return Math.max(0, PRAZO_REENVIO_DIAS - diasDesdeAvaliacao());
    }

    @Override
    public String toString() {
        String delegacao = delegadaParaComissao ? " | [DELEGADA A COMISSAO]" : "";
        String alertaPrazo = isAvaliacaoAtrasada() ? " | [PRAZO DE AVALIACAO ESTOURADO]" : "";
        return "[" + id + "] Solicitante: " + solicitante.getNome() +
               " | Certificado: " + certificado.getTituloAtividade() +
               " | " + certificado.getCargaHoraria() + "h" +
               " | Status: " + status + delegacao + alertaPrazo;
    }
}
