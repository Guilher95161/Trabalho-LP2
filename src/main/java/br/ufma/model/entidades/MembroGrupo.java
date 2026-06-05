package br.ufma.model.entidades;

import br.ufma.model.entidades.enums.CargoGrupo;
import java.time.LocalDate;

// Discente + seu cargo atual dentro de um grupo.
// Substitui o antigo Map<Discente, CargoGrupo>, que nao mapeia em ORM:
// aqui vira uma entidade intermediaria pronta pra @OneToMany no GrupoEstudantil.
public class MembroGrupo {
    private Discente discente;
    private CargoGrupo cargo;
    private LocalDate dataEntrada;

    public MembroGrupo(Discente discente, CargoGrupo cargo, LocalDate dataEntrada) {
        this.discente = discente;
        this.cargo = cargo;
        this.dataEntrada = dataEntrada;
    }

    public Discente getDiscente()     { return discente; }
    public CargoGrupo getCargo()      { return cargo; }
    public LocalDate getDataEntrada() { return dataEntrada; }

    public void setCargo(CargoGrupo cargo) { this.cargo = cargo; }
}
