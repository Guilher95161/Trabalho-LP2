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
public class CertificadoDTO {
    private String tituloAtividade;
    private Integer cargaHoraria;
    private LocalDate data;
    private Boolean aproveitamentoSolicitado;
}
