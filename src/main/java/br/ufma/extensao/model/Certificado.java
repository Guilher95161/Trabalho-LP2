package br.ufma.extensao.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "certificado")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Certificado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_certificado")
    private Integer id;

    @Column(name = "titulo_atividade")
    private String tituloAtividade;

    @Column(name = "carga_horaria")
    private int cargaHoraria;

    @Column(name = "data_emissao")
    private LocalDate data;

    @Column(name = "aproveitamento_solicitado")
    private boolean aproveitamentoSolicitado;
}
