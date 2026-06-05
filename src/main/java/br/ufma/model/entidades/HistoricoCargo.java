package br.ufma.model.entidades;

import br.ufma.model.entidades.enums.CargoGrupo;
import java.time.LocalDate;

public class HistoricoCargo {

    private Discente discente;
    private CargoGrupo cargo;
    private LocalDate dataInicio;
    private LocalDate dataFim; // null enquanto for o cargo atual

    public HistoricoCargo(Discente discente, CargoGrupo cargo, LocalDate dataInicio) {
        this.discente = discente;
        this.cargo = cargo;
        this.dataInicio = dataInicio;
        this.dataFim = null;
    }

    public Discente getDiscente() { return discente; }
    public CargoGrupo getCargo()  { return cargo; }
    public LocalDate getDataInicio() { return dataInicio; }
    public LocalDate getDataFim()    { return dataFim; }
    public boolean isAtual()         { return dataFim == null; }

    public void encerrar(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    @Override
    public String toString() {
        String fim = (dataFim != null) ? dataFim.toString() : "atual";
        return discente.getNome() + " | " + cargo + " | " + dataInicio + " -> " + fim;
    }
}
