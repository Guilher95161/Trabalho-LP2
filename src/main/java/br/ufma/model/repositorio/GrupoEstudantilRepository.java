package br.ufma.model.repositorio;

import br.ufma.model.entidades.Docente;
import br.ufma.model.entidades.GrupoEstudantil;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GrupoEstudantilRepository extends JpaRepository<GrupoEstudantil, Integer> {
    List<GrupoEstudantil> findByResponsavel(Docente responsavel);
}
