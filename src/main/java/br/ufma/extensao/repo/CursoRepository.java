package br.ufma.extensao.repo;

import br.ufma.extensao.model.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Integer> {
    // todas as versoes (o nome se repete: nova linha de Curso a cada mudanca de requisito)
    List<Curso> findByNome(String nome);

    // versao vigente = a linha mais recente daquele nome
    Optional<Curso> findFirstByNomeOrderByIdDesc(String nome);
}
