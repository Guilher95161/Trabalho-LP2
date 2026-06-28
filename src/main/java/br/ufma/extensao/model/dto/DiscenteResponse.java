package br.ufma.extensao.model.dto;

import br.ufma.extensao.model.Discente;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class DiscenteResponse {

    private Integer id;
    private String nome;
    private String matricula;
    private String email;
    private boolean ativo;
    private int horasCumpridas;
    private Integer cursoId;
    private List<String> papeis;

    public static DiscenteResponse from(Discente d) {
        List<String> nomePapeis = (d.getPapeis() != null)
                ? d.getPapeis().stream().map(p -> p.getNome()).toList()
                : List.of();
        return new DiscenteResponse(
                d.getId(), d.getNome(), d.getMatricula(), d.getEmail(), d.isAtivo(),
                d.getHorasCumpridas(),
                d.getCurso() != null ? d.getCurso().getId() : null,
                nomePapeis);
    }

    public static List<DiscenteResponse> fromList(List<Discente> discentes) {
        return discentes.stream().map(DiscenteResponse::from).toList();
    }
}
