package br.ufma.model.repositorio;

import br.ufma.model.entidades.Docente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocenteRepository extends JpaRepository<Docente, Integer> {
}
