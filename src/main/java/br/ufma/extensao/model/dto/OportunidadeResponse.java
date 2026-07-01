package br.ufma.extensao.model.dto;

import br.ufma.extensao.model.Discente;
import br.ufma.extensao.model.Oportunidade;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class OportunidadeResponse {

    private Integer id;
    private String titulo;
    private String descricao;
    private String modalidade;
    private String periodoRealizacao;
    private int cargaHoraria;
    private int vagas;
    private Integer responsavelId;
    private String status;
    private String motivoCancelamento;
    private List<Integer> inscritosAprovadosIds;
    private List<Integer> filaEsperaIds;

    public static OportunidadeResponse from(Oportunidade o) {
        return new OportunidadeResponse(
                o.getId(),
                o.getTitulo(),
                o.getDescricao(),
                o.getModalidade() != null ? o.getModalidade().name() : null,
                o.getPeriodoRealizacao(),
                o.getCargaHoraria(),
                o.getVagas(),
                o.getResponsavel() != null ? o.getResponsavel().getId() : null,
                o.getStatus() != null ? o.getStatus().name() : null,
                o.getMotivoCancelamento(),
                idsOuVazio(o.getInscritosAprovados()),
                idsOuVazio(o.getFilaEspera()));
    }

    public static List<OportunidadeResponse> fromList(List<Oportunidade> oportunidades) {
        return oportunidades.stream().map(OportunidadeResponse::from).toList();
    }

    private static List<Integer> idsOuVazio(List<Discente> discentes) {
        return discentes.stream().map(Discente::getId).toList();
    }
}
