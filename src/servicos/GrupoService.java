package servicos;

import entidades.GrupoEstudantil;
import repositorio.RepositorioCentral;

import java.util.List;

/**
 * Contém todas as regras de negócio relacionadas à gestão de grupos estudantis.
 */
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

    /**
     * Busca um grupo pelo seu ID.
     *
     * @return o {@link GrupoEstudantil} encontrado, ou {@code null} se não existir.
     */
    public GrupoEstudantil buscarPorId(int id) {
        return repositorio.findGrupoById(id).orElse(null);
    }
}