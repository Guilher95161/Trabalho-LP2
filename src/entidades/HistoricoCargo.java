package entidades;

import entidades.enums.CargoGrupo;

public class HistoricoCargo {

    private Discente discente;
    private CargoGrupo cargo;
    private String dataInicio;
    private String dataFim; // null enquanto for o cargo atual

    public HistoricoCargo(Discente discente, CargoGrupo cargo, String dataInicio) {
        this.discente = discente;
        this.cargo = cargo;
        this.dataInicio = dataInicio;
        this.dataFim = null;
    }

    public Discente getDiscente() { return discente; }
    public CargoGrupo getCargo()  { return cargo; }
    public String getDataInicio() { return dataInicio; }
    public String getDataFim()    { return dataFim; }
    public boolean isAtual()      { return dataFim == null; }

    public void encerrar(String dataFim) {
        this.dataFim = dataFim;
    }

    @Override
    public String toString() {
        String fim = (dataFim != null) ? dataFim : "atual";
        return discente.getNome() + " | " + cargo + " | " + dataInicio + " -> " + fim;
    }
}
