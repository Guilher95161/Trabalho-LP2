package entidades;

// requisito de horas de extensao do PPC - art. 1 das normas de extensao
public class UnidadeCurricular {
    private static int contador = 1;

    private int id;
    private int cargaHoraria;

    public UnidadeCurricular(int cargaHoraria) {
        this.id = contador++;
        this.cargaHoraria = cargaHoraria;
    }

    public int getId()           { return id; }
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
        return this.id == outra.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
