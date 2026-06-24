package br.ufma.extensao.repo;

import br.ufma.extensao.model.GrupoEstudantil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GrupoEstudantilRepository extends JpaRepository<GrupoEstudantil, Integer> {
    List<GrupoEstudantil> findByNome(String nome);

    // grupos em que o usuario e o responsavel OU um dos membros
    @Query("select distinct g from GrupoEstudantil g left join g.membros m " +
            "where g.responsavel.id = :usuarioId or m.discente.id = :usuarioId")
    List<GrupoEstudantil> findByUsuario(@Param("usuarioId") Integer usuarioId);
}
