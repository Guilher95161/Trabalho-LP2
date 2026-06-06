package br.ufma.model.repositorio;

import br.ufma.model.entidades.Discente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscenteRepository extends JpaRepository<Discente, Integer> {
}
