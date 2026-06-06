package br.ufma.model.repositorio;

import br.ufma.model.entidades.Curso;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CursoRepository extends JpaRepository<Curso, Integer> {
    Optional<Curso> findByCodigo(String codigo);
}
