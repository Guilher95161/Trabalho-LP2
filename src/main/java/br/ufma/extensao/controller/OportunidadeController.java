package br.ufma.extensao.controller;

import br.ufma.extensao.model.Oportunidade;
import br.ufma.extensao.model.Usuario;
import br.ufma.extensao.model.dto.OportunidadeDTO;
import br.ufma.extensao.model.enums.ModalidadeOportunidade;
import br.ufma.extensao.model.enums.StatusOportunidade;
import br.ufma.extensao.service.OportunidadeService;
import br.ufma.extensao.service.exceptions.SistemaExtensaoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/oportunidades")
public class OportunidadeController {

    @Autowired
    OportunidadeService service;

    @PostMapping
    public ResponseEntity salvar(@RequestBody OportunidadeDTO dto) {
        Oportunidade oportunidade = montar(null, dto);
        try {
            Oportunidade salvo = service.salvar(oportunidade);
            return new ResponseEntity(salvo, HttpStatus.CREATED);
        } catch (SistemaExtensaoException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}")
    public ResponseEntity atualizar(@PathVariable Integer id, @RequestBody OportunidadeDTO dto) {
        Oportunidade oportunidade = montar(id, dto);
        try {
            return ResponseEntity.ok(service.atualizar(oportunidade));
        } catch (SistemaExtensaoException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity remover(@PathVariable Integer id) {
        try {
            service.remover(id);
            return ResponseEntity.noContent().build();
        } catch (SistemaExtensaoException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/obter")
    public ResponseEntity buscar(@RequestParam(value = "titulo", required = false) String titulo,
                                 @RequestParam(value = "status", required = false) String status) {
        Oportunidade filtro = new Oportunidade();
        filtro.setTitulo(titulo);
        if (status != null)
            filtro.setStatus(StatusOportunidade.valueOf(status));
        return ResponseEntity.ok(service.buscar(filtro));
    }

    private Oportunidade montar(Integer id, OportunidadeDTO dto) {
        Oportunidade oportunidade = new Oportunidade();
        oportunidade.setId(id);
        oportunidade.setTitulo(dto.getTitulo());
        oportunidade.setDescricao(dto.getDescricao());
        oportunidade.setPeriodoRealizacao(dto.getPeriodoRealizacao());
        if (dto.getModalidade() != null)
            oportunidade.setModalidade(ModalidadeOportunidade.valueOf(dto.getModalidade()));
        if (dto.getStatus() != null)
            oportunidade.setStatus(StatusOportunidade.valueOf(dto.getStatus()));
        if (dto.getCargaHoraria() != null)
            oportunidade.setCargaHoraria(dto.getCargaHoraria());
        if (dto.getVagas() != null)
            oportunidade.setVagas(dto.getVagas());
        if (dto.getResponsavelId() != null) {
            Usuario responsavel = new Usuario();
            responsavel.setId(dto.getResponsavelId());
            oportunidade.setResponsavel(responsavel);
        }
        return oportunidade;
    }
}
