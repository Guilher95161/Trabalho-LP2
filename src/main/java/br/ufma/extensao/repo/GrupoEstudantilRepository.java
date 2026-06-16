package br.ufma.extensao.repo;

import br.ufma.extensao.model.GrupoEstudantil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GrupoEstudantilRepository extends JpaRepository<GrupoEstudantil, Integer> {
    List<GrupoEstudantil> findByNome(String nome);
}
