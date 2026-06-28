package br.ufma.extensao.controller;

import br.ufma.extensao.model.Curso;
import br.ufma.extensao.model.dto.CursoDTO;
import br.ufma.extensao.service.CursoService;
import br.ufma.extensao.service.exceptions.SistemaExtensaoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cursos")
@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COORDENADOR')")
public class CursoController {

    @Autowired
    CursoService service;

    @PostMapping
    public ResponseEntity salvar(@RequestBody CursoDTO dto) {
        Curso curso = new Curso();
        curso.setNome(dto.getNome());
        curso.setCurriculo(dto.getCurriculo());
        curso.setCargaHoraria(dto.getCargaHoraria());
        try {
            Curso salvo = service.salvar(curso);
            return new ResponseEntity(salvo, HttpStatus.CREATED);
        } catch (SistemaExtensaoException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}")
    public ResponseEntity atualizar(@PathVariable Integer id, @RequestBody CursoDTO dto) {
        Curso curso = new Curso();
        curso.setId(id);
        curso.setNome(dto.getNome());
        curso.setCurriculo(dto.getCurriculo());
        curso.setCargaHoraria(dto.getCargaHoraria());
        try {
            return ResponseEntity.ok(service.atualizar(curso));
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
        Curso filtro = new Curso();
        filtro.setNome(nome);
        return ResponseEntity.ok(service.buscar(filtro));
    }
}
