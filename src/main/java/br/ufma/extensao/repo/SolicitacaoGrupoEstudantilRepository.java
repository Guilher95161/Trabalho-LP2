package br.ufma.extensao.repo;

import br.ufma.extensao.model.SolicitacaoGrupoEstudantil;
import br.ufma.extensao.model.enums.StatusSolicitacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolicitacaoGrupoEstudantilRepository extends JpaRepository<SolicitacaoGrupoEstudantil, Integer> {
    List<SolicitacaoGrupoEstudantil> findByStatus(StatusSolicitacao status);
}
