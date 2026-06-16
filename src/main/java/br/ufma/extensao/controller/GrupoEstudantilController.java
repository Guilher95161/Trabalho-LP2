package br.ufma.extensao.controller;

import br.ufma.extensao.model.GrupoEstudantil;
import br.ufma.extensao.model.Usuario;
import br.ufma.extensao.model.dto.GrupoEstudantilDTO;
import br.ufma.extensao.service.GrupoEstudantilService;
import br.ufma.extensao.service.exceptions.SistemaExtensaoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/grupos")
public class GrupoEstudantilController {

    @Autowired
    GrupoEstudantilService service;

    @PostMapping
    public ResponseEntity salvar(@RequestBody GrupoEstudantilDTO dto) {
        GrupoEstudantil grupo = montar(null, dto);
        try {
            GrupoEstudantil salvo = service.salvar(grupo);
            return new ResponseEntity(salvo, HttpStatus.CREATED);
        } catch (SistemaExtensaoException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}")
    public ResponseEntity atualizar(@PathVariable Integer id, @RequestBody GrupoEstudantilDTO dto) {
        GrupoEstudantil grupo = montar(id, dto);
        try {
            return ResponseEntity.ok(service.atualizar(grupo));
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
    public ResponseEntity buscar(@RequestParam(value = "nome", required = false) String nome) {
        GrupoEstudantil filtro = new GrupoEstudantil();
        filtro.setNome(nome);
        return ResponseEntity.ok(service.buscar(filtro));
    }

    private GrupoEstudantil montar(Integer id, GrupoEstudantilDTO dto) {
        GrupoEstudantil grupo = new GrupoEstudantil();
        grupo.setId(id);
        grupo.setNome(dto.getNome());
        grupo.setDescricao(dto.getDescricao());
        if (dto.getResponsavelId() != null) {
            Usuario responsavel = new Usuario();
            responsavel.setId(dto.getResponsavelId());
            grupo.setResponsavel(responsavel);
        }
        return grupo;
    }
}
