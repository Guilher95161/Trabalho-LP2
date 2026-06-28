package br.ufma.extensao.model.dto;

import br.ufma.extensao.model.GrupoEstudantil;
import br.ufma.extensao.model.MembroGrupo;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class GrupoEstudantilResponse {

    private Integer id;
    private String nome;
    private String descricao;
    private Integer responsavelId;
    private List<MembroGrupoResponse> membros;

    @Getter
    @AllArgsConstructor
    public static class MembroGrupoResponse {
        private Integer discenteId;
        private String cargo;
        private LocalDate dataEntrada;

        public static MembroGrupoResponse from(MembroGrupo m) {
            return new MembroGrupoResponse(
                    m.getDiscente() != null ? m.getDiscente().getId() : null,
                    m.getCargo() != null ? m.getCargo().name() : null,
                    m.getDataEntrada());
        }
    }

    public static GrupoEstudantilResponse from(GrupoEstudantil g) {
        List<MembroGrupoResponse> membrosResp = (g.getMembros() != null)
                ? g.getMembros().stream().map(MembroGrupoResponse::from).toList()
                : List.of();
        return new GrupoEstudantilResponse(
                g.getId(),
                g.getNome(),
                g.getDescricao(),
                g.getResponsavel() != null ? g.getResponsavel().getId() : null,
                membrosResp);
    }

    public static List<GrupoEstudantilResponse> fromList(List<GrupoEstudantil> grupos) {
        return grupos.stream().map(GrupoEstudantilResponse::from).toList();
    }
}
