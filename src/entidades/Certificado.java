package entidades;

public class Certificado {
    private String tituloAtividade;
    private int cargaHoraria;
    private String data;

    // evita que o mesmo cert seja enviado duas vezes
    private boolean aproveitamentoSolicitado;

    // snapshot do componente curricular no momento da emissao
    private String componenteCurricular;
    private UnidadeCurricular uceVinculada;

    public Certificado(String tituloAtividade, int cargaHoraria, String data,
                       String componenteCurricular, UnidadeCurricular uceVinculada) {
        this.tituloAtividade = tituloAtividade;
        this.cargaHoraria = cargaHoraria;
        this.data = data;
        this.aproveitamentoSolicitado = false;
        this.componenteCurricular = componenteCurricular;
        this.uceVinculada = uceVinculada;
    }

    public String getTituloAtividade()       { return tituloAtividade; }
    public int getCargaHoraria()             { return cargaHoraria; }
    public String getData()                  { return data; }
    public boolean isAproveitamentoSolicitado() { return aproveitamentoSolicitado; }
    // devolve o snapshot - se a UCE for renomeada depois, o cert preserva o nome original
    public String getComponenteCurricular()  { return componenteCurricular; }
    public UnidadeCurricular getUceVinculada() { return uceVinculada; }

    public void marcarAproveitamentoSolicitado()  { this.aproveitamentoSolicitado = true; }
    public void liberarAproveitamento()           { this.aproveitamentoSolicitado = false; }

    @Override
    public String toString() {
        String status = aproveitamentoSolicitado ? " [aproveitamento enviado]" : "";
        String uceInfo = " [UCE: " + (componenteCurricular != null ? componenteCurricular : "?") + "]";
        return tituloAtividade + " | " + cargaHoraria + "h | " + data + uceInfo + status;
    }
}
