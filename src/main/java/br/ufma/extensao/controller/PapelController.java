package br.ufma.extensao.controller;

import br.ufma.extensao.model.Papel;
import br.ufma.extensao.model.dto.PapelDTO;
import br.ufma.extensao.service.PapelService;
import br.ufma.extensao.service.exceptions.SistemaExtensaoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/papeis")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class PapelController {

    @Autowired
    PapelService service;

    @PostMapping
    public ResponseEntity salvar(@RequestBody PapelDTO dto) {
        Papel papel = new Papel();
        papel.setNome(dto.getNome());
        try {
            Papel salvo = service.salvar(papel);
            return new ResponseEntity(salvo, HttpStatus.CREATED);
        } catch (SistemaExtensaoException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}")
    public ResponseEntity atualizar(@PathVariable Integer id, @RequestBody PapelDTO dto) {
        Papel papel = new Papel();
        papel.setId(id);
        papel.setNome(dto.getNome());
        try {
            return ResponseEntity.ok(service.atualizar(papel));
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

    @GetMapping("{id}")
    public ResponseEntity buscarPorId(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(service.buscarPorId(id));
        } catch (SistemaExtensaoException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/obter")
    public ResponseEntity buscar(@RequestParam(value = "nome", required = false) String nome) {
        Papel filtro = new Papel();
        filtro.setNome(nome);
        return ResponseEntity.ok(service.buscar(filtro));
    }
}
