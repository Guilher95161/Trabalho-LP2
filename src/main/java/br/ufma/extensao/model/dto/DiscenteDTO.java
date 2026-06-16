package br.ufma.extensao.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DiscenteDTO {
    private String nome;
    private String email;
    private String senha;
    private String matricula;
    private Integer horasCumpridas;
    private Integer cursoId;
}
