package entidades;

import java.time.LocalDate;

// cada versao do PPC tem sua propria UCE com a carga horaria exigida
public class Ppc {
    private static int contador = 1;

    private int id;
    private Curso curso;
    private String anoVigencia;                  // ex: "2020", "2025.1"
    private UnidadeCurricular uce;
    private LocalDate dataCadastro;
    private Usuario autorAlteracao;
    private boolean ativa;

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

    public int getId()                       { return id; }
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
