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

// Discente + seu cargo atual dentro de um grupo.
// Substitui o antigo Map<Discente, CargoGrupo>, que nao mapeia em ORM:
// aqui vira uma entidade intermediaria pronta pra @OneToMany no GrupoEstudantil.
@Entity
@Table(name = "membro_grupo")
public class MembroGrupo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_membro")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "discente_id")
    private Discente discente;

    @Enumerated(EnumType.STRING)
    private CargoGrupo cargo;

    private LocalDate dataEntrada;

    // construtor vazio exigido pelo JPA
    protected MembroGrupo() {
    }

    public MembroGrupo(Discente discente, CargoGrupo cargo, LocalDate dataEntrada) {
        this.discente = discente;
        this.cargo = cargo;
        this.dataEntrada = dataEntrada;
    }

    public Integer getId()            { return id; }
    public Discente getDiscente()     { return discente; }
    public CargoGrupo getCargo()      { return cargo; }
    public LocalDate getDataEntrada() { return dataEntrada; }

    public void setCargo(CargoGrupo cargo) { this.cargo = cargo; }
}
