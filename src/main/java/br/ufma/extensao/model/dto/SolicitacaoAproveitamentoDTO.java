package br.ufma.extensao.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SolicitacaoAproveitamentoDTO {
    private Integer solicitanteId;
    private Integer certificadoId;
    private String status;
    private String parecer;
    private Boolean delegadaParaComissao;
    private LocalDate dataCriacao;
    private LocalDate dataAvaliacao;
}
