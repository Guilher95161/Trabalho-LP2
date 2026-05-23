package entidades;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Uma versao do PPC. Cada curso pode ter varias (turma 2020, turma 2025...)
// e cada versao traz sua propria carga horaria e suas proprias UCEs.
public class Ppc {
    private static int contador = 1;

    private int id;
    private Curso curso;
    private String anoVigencia;                  // ex: "2020", "2025.1"
    private int horasExtensaoNecessarias;
    private LocalDate dataCadastro;
    private Usuario autorAlteracao;
    private List<UnidadeCurricular> uces;
    private boolean ativa;                       // false quando ja foi substituida por outra versao

    public Ppc(Curso curso, String anoVigencia, int horasExtensaoNecessarias,
               Usuario autorAlteracao) {
        this.id = contador++;
        this.curso = curso;
        this.anoVigencia = anoVigencia;
        this.horasExtensaoNecessarias = horasExtensaoNecessarias;
        this.dataCadastro = LocalDate.now();
        this.autorAlteracao = autorAlteracao;
        this.uces = new ArrayList<>();
        this.ativa = true;
    }

    public int getId()                       { return id; }
    public Curso getCurso()                  { return curso; }
    public String getAnoVigencia()           { return anoVigencia; }
    public int getHorasExtensaoNecessarias() { return horasExtensaoNecessarias; }
    public LocalDate getDataCadastro()       { return dataCadastro; }
    public Usuario getAutorAlteracao()       { return autorAlteracao; }
    public List<UnidadeCurricular> getUces() { return uces; }
    public boolean isAtiva()                 { return ativa; }

    public void desativar() {
        this.ativa = false;
    }

    public void adicionarUce(UnidadeCurricular uce) {
        if (!uces.contains(uce)) uces.add(uce);
    }

    public boolean removerUce(UnidadeCurricular uce) {
        return uces.remove(uce);
    }

    public UnidadeCurricular buscarUcePorCodigo(String codigo) {
        for (UnidadeCurricular u : uces) {
            if (u.getCodigo().equalsIgnoreCase(codigo)) return u;
        }
        return null;
    }

    public UnidadeCurricular buscarUcePorIndice(int idx) {
        if (idx >= 0 && idx < uces.size()) return uces.get(idx);
        return null;
    }

    @Override
    public String toString() {
        String autor = (autorAlteracao != null) ? autorAlteracao.getNome() : "(desconhecido)";
        String estado = ativa ? "VIGENTE" : "HISTORICA";
        return "[" + id + "] PPC " + curso.getCodigo() + "/" + anoVigencia +
               " | " + horasExtensaoNecessarias + "h" +
               " | UCEs: " + uces.size() +
               " | Cadastrado em " + dataCadastro +
               " por " + autor +
               " | " + estado;
    }
}
