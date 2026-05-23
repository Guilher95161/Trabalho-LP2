package entidades;

/**
 * RF0009 - Unidade Curricular de Extensao (UCE).
 * Cada PPC pode ter sua propria lista de UCEs com cargas horarias especificas.
 */
public class UnidadeCurricular {
    private static int contador = 1;

    private int id;
    private String codigo;       // ex: "EXT0001"
    private String nome;         // ex: "Atividades de Extensao I"
    private int cargaHoraria;    // horas que essa UCE vale

    public UnidadeCurricular(String codigo, String nome, int cargaHoraria) {
        this.id = contador++;
        this.codigo = codigo;
        this.nome = nome;
        this.cargaHoraria = cargaHoraria;
    }

    public int getId()           { return id; }
    public String getCodigo()    { return codigo; }
    public String getNome()      { return nome; }
    public int getCargaHoraria() { return cargaHoraria; }

    public void setNome(String nome)            { this.nome = nome; }
    public void setCargaHoraria(int ch)         { this.cargaHoraria = ch; }

    @Override
    public String toString() {
        return "[" + id + "] " + codigo + " - " + nome + " (" + cargaHoraria + "h)";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UnidadeCurricular)) return false;
        UnidadeCurricular outra = (UnidadeCurricular) o;
        return codigo != null && codigo.equalsIgnoreCase(outra.codigo);
    }

    @Override
    public int hashCode() {
        return codigo == null ? 0 : codigo.toLowerCase().hashCode();
    }
}
