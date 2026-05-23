package entidades;

import entidades.enums.StatusSolicitacao;

public class SolicitacaoAproveitamento {
    private static int contador = 1;

    private int id;
    private Discente solicitante;
    private Certificado certificado;
    private StatusSolicitacao status;
    private String parecer;
    private boolean delegadaParaComissao;

    public SolicitacaoAproveitamento(Discente solicitante, Certificado certificado) {
        this.id = contador++;
        this.solicitante = solicitante;
        this.certificado = certificado;
        this.status = StatusSolicitacao.PENDENTE;
        this.parecer = "";
        this.delegadaParaComissao = false;
    }

    public int getId()                         { return id; }
    public Discente getSolicitante()           { return solicitante; }
    public Certificado getCertificado()        { return certificado; }
    public StatusSolicitacao getStatus()       { return status; }
    public String getParecer()                 { return parecer; }
    public boolean isDelegadaParaComissao()    { return delegadaParaComissao; }

    public void avaliarSolicitacao(boolean aprovado, String parecer) {
        this.parecer = parecer;
        this.status = aprovado ? StatusSolicitacao.DEFERIDA : StatusSolicitacao.INDEFERIDA;
    }

    public void reenviar() {
        this.status = StatusSolicitacao.PENDENTE;
        this.parecer = "";
    }

    public void cancelar() {
        this.status = StatusSolicitacao.CANCELADA;
    }

    public void delegarParaComissao() {
        this.delegadaParaComissao = true;
    }

    @Override
    public String toString() {
        String delegacao = delegadaParaComissao ? " | [DELEGADA A COMISSAO]" : "";
        return "[" + id + "] Solicitante: " + solicitante.getNome() +
               " | Certificado: " + certificado.getTituloAtividade() +
               " | " + certificado.getCargaHoraria() + "h" +
               " | Status: " + status + delegacao;
    }
}
