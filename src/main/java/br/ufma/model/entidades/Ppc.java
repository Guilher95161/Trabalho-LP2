package br.ufma.model.entidades;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;

// cada versao do PPC tem sua propria UCE com a carga horaria exigida
@Entity
@Table(name = "ppc")
public class Ppc {
    private static int contador = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ppc")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "curso_id")
    private Curso curso;

    private String anoVigencia;                  // ex: "2020", "2025.1"

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "uce_id")
    private UnidadeCurricular uce;

    @Column(name = "data_cadastro")
    private LocalDate dataCadastro;

    @ManyToOne
    @JoinColumn(name = "autor_id")
    private Usuario autorAlteracao;

    private boolean ativa;

    // construtor vazio exigido pelo JPA
    protected Ppc() {
    }

    public Ppc(Curso curso, String anoVigencia, int horasUce,
               Usuario autorAlteracao) {
        this.id = contador++;
        this.curso = curso;
        this.anoVigencia = anoVigencia;
        this.uce = new UnidadeCurricular(horasUce);
        this.dataCadastro = LocalDate.now();
        this.autorAlteracao = autorAlteracao;
        this.ativa = true;
    }

    public Integer getId()                   { return id; }
    public Curso getCurso()                  { return curso; }
    public String getAnoVigencia()           { return anoVigencia; }
    public UnidadeCurricular getUce()        { return uce; }
    public LocalDate getDataCadastro()       { return dataCadastro; }
    public Usuario getAutorAlteracao()       { return autorAlteracao; }
    public boolean isAtiva()                 { return ativa; }

    // conveniencia - evita chamador precisar fazer getUce().getCargaHoraria()
    public int getHorasExtensaoNecessarias() {
        return uce != null ? uce.getCargaHoraria() : 0;
    }

    public void desativar() {
        this.ativa = false;
    }

    @Override
    public String toString() {
        String autor = (autorAlteracao != null) ? autorAlteracao.getNome() : "(desconhecido)";
        String estado = ativa ? "VIGENTE" : "HISTORICA";
        return "[" + id + "] PPC " + curso.getCodigo() + "/" + anoVigencia +
               " | UCE: " + (uce != null ? uce.getCargaHoraria() : 0) + "h" +
               " | Cadastrado em " + dataCadastro +
               " por " + autor +
               " | " + estado;
    }
}
