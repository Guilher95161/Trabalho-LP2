package servicos;

import entidades.GrupoEstudantil;
import repositorio.RepositorioCentral;

import java.util.List;

public class GrupoService {

    private final RepositorioCentral repositorio;

    public GrupoService(RepositorioCentral repositorio) {
        this.repositorio = repositorio;
    }

    public void criarGrupo(GrupoEstudantil g) {
        repositorio.salvarGrupo(g);
    }

    public List<GrupoEstudantil> listarTodos() {
        return repositorio.findAllGrupos();
    }

    public GrupoEstudantil buscarPorId(int id) {
        return repositorio.findGrupoById(id);
    }
}