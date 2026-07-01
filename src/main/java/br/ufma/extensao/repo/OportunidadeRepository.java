package br.ufma.extensao.repo;

import br.ufma.extensao.model.Oportunidade;
import br.ufma.extensao.model.enums.StatusOportunidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OportunidadeRepository extends JpaRepository<Oportunidade, Integer> {
    List<Oportunidade> findByStatus(StatusOportunidade status);
}
