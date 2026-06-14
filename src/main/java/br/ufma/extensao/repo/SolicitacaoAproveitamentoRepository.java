package br.ufma.extensao.repo;

import br.ufma.extensao.model.SolicitacaoAproveitamento;
import br.ufma.extensao.model.enums.StatusSolicitacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolicitacaoAproveitamentoRepository extends JpaRepository<SolicitacaoAproveitamento, Integer> {
    List<SolicitacaoAproveitamento> findByStatus(StatusSolicitacao status);
}
