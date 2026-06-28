package br.ufma.extensao.model.dto;

import br.ufma.extensao.model.SolicitacaoGrupoEstudantil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SolicitacaoGrupoEstudantilResponse {

    private Integer id;
    private Integer solicitanteId;
    private String nomeGrupo;
    private String descricao;
    private Integer docenteResponsavelId;
    private String status;

    public static SolicitacaoGrupoEstudantilResponse from(SolicitacaoGrupoEstudantil s) {
        return new SolicitacaoGrupoEstudantilResponse(
                s.getId(),
                s.getSolicitante() != null ? s.getSolicitante().getId() : null,
                s.getNomeGrupo(),
                s.getDescricao(),
                s.getDocenteResponsavel() != null ? s.getDocenteResponsavel().getId() : null,
                s.getStatus() != null ? s.getStatus().name() : null);
    }

    public static List<SolicitacaoGrupoEstudantilResponse> fromList(List<SolicitacaoGrupoEstudantil> lista) {
        return lista.stream().map(SolicitacaoGrupoEstudantilResponse::from).toList();
    }
}
