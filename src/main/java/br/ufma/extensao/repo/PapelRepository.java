package br.ufma.extensao.repo;

import br.ufma.extensao.model.Papel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PapelRepository extends JpaRepository<Papel, Integer> {
    Optional<Papel> findByNome(String nome);
}
