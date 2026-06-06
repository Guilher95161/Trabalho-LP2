package br.ufma.model.repositorio;

import br.ufma.model.entidades.Oportunidade;
import br.ufma.model.entidades.enums.StatusOportunidade;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OportunidadeRepository extends JpaRepository<Oportunidade, Integer> {
    List<Oportunidade> findByStatus(StatusOportunidade status);
}
