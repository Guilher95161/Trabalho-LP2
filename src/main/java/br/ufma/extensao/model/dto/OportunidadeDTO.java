package br.ufma.extensao.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OportunidadeDTO {
    private String titulo;
    private String descricao;
    private String modalidade;
    private String periodoRealizacao;
    private Integer cargaHoraria;
    private Integer vagas;
    private Integer responsavelId;
    private String status;
}
