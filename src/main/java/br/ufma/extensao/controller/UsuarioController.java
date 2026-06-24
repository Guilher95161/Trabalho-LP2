package br.ufma.extensao.controller;

import br.ufma.extensao.model.Usuario;
import br.ufma.extensao.model.dto.UsuarioDTO;
import br.ufma.extensao.service.UsuarioService;
import br.ufma.extensao.service.exceptions.SistemaExtensaoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    UsuarioService service;

    @PostMapping
    public ResponseEntity salvar(@RequestBody UsuarioDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(dto.getSenha());
        usuario.setMatricula(dto.getMatricula());
        usuario.setAtivo(true);
        try {
            Usuario salvo = service.salvar(usuario);
            return new ResponseEntity(salvo, HttpStatus.CREATED);
        } catch (SistemaExtensaoException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/autenticar")
    public ResponseEntity autenticar(@RequestBody UsuarioDTO dto) {
        try {
            service.efetuarLogin(dto.getEmail(), dto.getSenha());
            return ResponseEntity.ok(true);
        } catch (SistemaExtensaoException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}")
    public ResponseEntity atualizar(@PathVariable Integer id, @RequestBody UsuarioDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(dto.getSenha());
        usuario.setMatricula(dto.getMatricula());
        usuario.setAtivo(true);
        try {
            return ResponseEntity.ok(service.atualizar(usuario));
        } catch (SistemaExtensaoException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}/desativar")
    public ResponseEntity desativar(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(service.desativarUsuario(id));
        } catch (SistemaExtensaoException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}/reativar")
    public ResponseEntity reativar(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(service.reativarUsuario(id));
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
        Usuario filtro = new Usuario();
        filtro.setNome(nome);
        filtro.setEmail(email);
        return ResponseEntity.ok(service.buscar(filtro));
    }
}
