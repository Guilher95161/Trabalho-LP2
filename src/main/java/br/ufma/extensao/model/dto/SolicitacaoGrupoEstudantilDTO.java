package br.ufma.extensao.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SolicitacaoGrupoEstudantilDTO {
    private Integer solicitanteId;
    private String nomeGrupo;
    private String descricao;
    private Integer docenteResponsavelId;
    private String status;
}
