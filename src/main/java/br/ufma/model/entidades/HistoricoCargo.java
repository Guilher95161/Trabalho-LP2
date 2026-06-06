package br.ufma.model.entidades;

import br.ufma.model.entidades.enums.CargoGrupo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "historico_cargo")
public class HistoricoCargo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historico")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "discente_id")
    private Discente discente;

    @Enumerated(EnumType.STRING)
    private CargoGrupo cargo;

    private LocalDate dataInicio;
    private LocalDate dataFim; // null enquanto for o cargo atual

    // construtor vazio exigido pelo JPA
    protected HistoricoCargo() {
    }

    public HistoricoCargo(Discente discente, CargoGrupo cargo, LocalDate dataInicio) {
        this.discente = discente;
        this.cargo = cargo;
        this.dataInicio = dataInicio;
        this.dataFim = null;
    }

    public Integer getId()        { return id; }
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
