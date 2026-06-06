package br.ufma.model.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;

// requisito de horas de extensao do PPC - art. 1 das normas de extensao
@Entity
@Table(name = "unidade_curricular")
public class UnidadeCurricular {
    private static int contador = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_uce")
    private Integer id;

    @Column(name = "carga_horaria")
    private int cargaHoraria;

    // construtor vazio exigido pelo JPA
    protected UnidadeCurricular() {
    }

    public UnidadeCurricular(int cargaHoraria) {
        this.id = contador++;
        this.cargaHoraria = cargaHoraria;
    }

    public Integer getId()       { return id; }
    public int getCargaHoraria() { return cargaHoraria; }

    public void setCargaHoraria(int ch) { this.cargaHoraria = ch; }

    @Override
    public String toString() {
        return "[" + id + "] UCE (" + cargaHoraria + "h)";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UnidadeCurricular)) return false;
        UnidadeCurricular outra = (UnidadeCurricular) o;
        return Objects.equals(this.id, outra.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
