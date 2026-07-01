package br.ufma.extensao.repo;

import br.ufma.extensao.model.Discente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscenteRepository extends JpaRepository<Discente, Integer> {
}
