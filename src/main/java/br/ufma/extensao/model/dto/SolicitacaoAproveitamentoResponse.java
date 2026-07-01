package br.ufma.extensao.model.dto;

import br.ufma.extensao.model.SolicitacaoAproveitamento;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class SolicitacaoAproveitamentoResponse {

    private Integer id;
    private Integer solicitanteId;
    private Integer certificadoId;
    private String status;
    private String parecer;
    private boolean delegadaParaComissao;
    private LocalDate dataCriacao;
    private LocalDate dataAvaliacao;

    public static SolicitacaoAproveitamentoResponse from(SolicitacaoAproveitamento s) {
        return new SolicitacaoAproveitamentoResponse(
                s.getId(),
                s.getSolicitante() != null ? s.getSolicitante().getId() : null,
                s.getCertificado() != null ? s.getCertificado().getId() : null,
                s.getStatus() != null ? s.getStatus().name() : null,
                s.getParecer(),
                s.isDelegadaParaComissao(),
                s.getDataCriacao(),
                s.getDataAvaliacao());
    }

    public static List<SolicitacaoAproveitamentoResponse> fromList(List<SolicitacaoAproveitamento> lista) {
        return lista.stream().map(SolicitacaoAproveitamentoResponse::from).toList();
    }
}
