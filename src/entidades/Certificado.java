package entidades;

public class Certificado {
    private String tituloAtividade;
    private int cargaHoraria;
    private String data;
    private boolean aproveitamentoSolicitado;
    private boolean uce;                     // RF009 - se a oportunidade original era UCE
    private String componenteCurricular;     // codigo + nome (snapshot, mesmo se UCE for editada depois)
    private UnidadeCurricular uceVinculada;  // RF0009 - referencia opcional a UCE concreta de um PPC

    public Certificado(String tituloAtividade, int cargaHoraria, String data) {
        this.tituloAtividade = tituloAtividade;
        this.cargaHoraria = cargaHoraria;
        this.data = data;
        this.aproveitamentoSolicitado = false;
        this.uce = false;
        this.componenteCurricular = null;
        this.uceVinculada = null;
    }

    public Certificado(String tituloAtividade, int cargaHoraria, String data,
                       boolean uce, String componenteCurricular) {
        this(tituloAtividade, cargaHoraria, data);
        this.uce = uce;
        this.componenteCurricular = componenteCurricular;
    }

    public Certificado(String tituloAtividade, int cargaHoraria, String data,
                       boolean uce, String componenteCurricular, UnidadeCurricular uceVinculada) {
        this(tituloAtividade, cargaHoraria, data, uce, componenteCurricular);
        this.uceVinculada = uceVinculada;
    }

    public String getTituloAtividade()       { return tituloAtividade; }
    public int getCargaHoraria()             { return cargaHoraria; }
    public String getData()                  { return data; }
    public boolean isAproveitamentoSolicitado() { return aproveitamentoSolicitado; }
    public boolean isUce()                   { return uce; }
    public String getComponenteCurricular()  { return componenteCurricular; }
    public UnidadeCurricular getUceVinculada() { return uceVinculada; }

    public void marcarAproveitamentoSolicitado()  { this.aproveitamentoSolicitado = true; }
    public void liberarAproveitamento()           { this.aproveitamentoSolicitado = false; }

    @Override
    public String toString() {
        String status = aproveitamentoSolicitado ? " [aproveitamento enviado]" : "";
        String uceInfo = uce ? " [UCE: " + componenteCurricular + "]" : "";
        return tituloAtividade + " | " + cargaHoraria + "h | " + data + uceInfo + status;
    }
}
