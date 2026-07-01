package br.ufma.extensao.model.dto;

import br.ufma.extensao.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class UsuarioResponse {

    private Integer id;
    private String nome;
    private String matricula;
    private String email;
    private boolean ativo;
    private List<String> papeis;

    public static UsuarioResponse from(Usuario u) {
        List<String> nomePapeis = (u.getPapeis() != null)
                ? u.getPapeis().stream().map(p -> p.getNome()).toList()
                : List.of();
        return new UsuarioResponse(u.getId(), u.getNome(), u.getMatricula(),
                u.getEmail(), u.isAtivo(), nomePapeis);
    }

    public static List<UsuarioResponse> fromList(List<Usuario> usuarios) {
        return usuarios.stream().map(UsuarioResponse::from).toList();
    }
}
