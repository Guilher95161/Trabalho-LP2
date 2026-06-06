package br.ufma.model.repositorio;

import br.ufma.model.entidades.SolicitacaoAproveitamento;
import br.ufma.model.entidades.enums.StatusSolicitacao;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SolicitacaoAproveitamentoRepository extends JpaRepository<SolicitacaoAproveitamento, Integer> {
    List<SolicitacaoAproveitamento> findByStatus(StatusSolicitacao status);
}
