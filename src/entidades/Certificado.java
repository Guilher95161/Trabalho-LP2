package entidades;

public class Certificado {
    private String tituloAtividade;
    private int cargaHoraria;
    private String data;
    private boolean aproveitamentoSolicitado;

    public Certificado(String tituloAtividade, int cargaHoraria, String data) {
        this.tituloAtividade = tituloAtividade;
        this.cargaHoraria = cargaHoraria;
        this.data = data;
        this.aproveitamentoSolicitado = false;
    }

    public String getTituloAtividade()       { return tituloAtividade; }
    public int getCargaHoraria()             { return cargaHoraria; }
    public String getData()                  { return data; }
    public boolean isAproveitamentoSolicitado() { return aproveitamentoSolicitado; }

    public void marcarAproveitamentoSolicitado()  { this.aproveitamentoSolicitado = true; }
    public void liberarAproveitamento()           { this.aproveitamentoSolicitado = false; }

    @Override
    public String toString() {
        String status = aproveitamentoSolicitado ? " [aproveitamento enviado]" : "";
        return tituloAtividade + " | " + cargaHoraria + "h | " + data + status;
    }
}
