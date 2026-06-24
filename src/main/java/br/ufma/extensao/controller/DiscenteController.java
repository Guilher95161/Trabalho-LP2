package br.ufma.extensao.controller;

import br.ufma.extensao.model.Curso;
import br.ufma.extensao.model.Discente;
import br.ufma.extensao.model.dto.DiscenteDTO;
import br.ufma.extensao.service.DiscenteService;
import br.ufma.extensao.service.exceptions.SistemaExtensaoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/discentes")
public class DiscenteController {

    @Autowired
    DiscenteService service;

    @PostMapping
    public ResponseEntity salvar(@RequestBody DiscenteDTO dto) {
        Discente discente = montar(null, dto);
        try {
            Discente salvo = service.salvar(discente);
            return new ResponseEntity(salvo, HttpStatus.CREATED);
        } catch (SistemaExtensaoException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}")
    public ResponseEntity atualizar(@PathVariable Integer id, @RequestBody DiscenteDTO dto) {
        Discente discente = montar(id, dto);
        try {
            return ResponseEntity.ok(service.atualizar(discente));
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
    public ResponseEntity buscar(@RequestParam(value = "nome", required = false) String nome,
                                 @RequestParam(value = "email", required = false) String email) {
        Discente filtro = new Discente();
        filtro.setNome(nome);
        filtro.setEmail(email);
        return ResponseEntity.ok(service.buscar(filtro));
    }

    @GetMapping("{id}/painel-horas")
    public ResponseEntity painelHoras(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(service.painelHoras(id));
        } catch (SistemaExtensaoException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private Discente montar(Integer id, DiscenteDTO dto) {
        Discente discente = new Discente();
        discente.setId(id);
        discente.setNome(dto.getNome());
        discente.setEmail(dto.getEmail());
        discente.setSenha(dto.getSenha());
        discente.setMatricula(dto.getMatricula());
        discente.setAtivo(true);
        if (dto.getHorasCumpridas() != null)
            discente.setHorasCumpridas(dto.getHorasCumpridas());
        if (dto.getCursoId() != null) {
            Curso curso = new Curso();
            curso.setId(dto.getCursoId());
            discente.setCurso(curso);
        }
        return discente;
    }
}
