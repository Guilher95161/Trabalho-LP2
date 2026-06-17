package br.ufma.extensao.service;

import br.ufma.extensao.model.Usuario;
import br.ufma.extensao.repo.UsuarioRepository;
import br.ufma.extensao.service.exceptions.EmailJaCadastradoException;
import br.ufma.extensao.service.exceptions.EntidadeNaoEncontradaException;
import br.ufma.extensao.service.exceptions.RegraNegocioRunTime;
import br.ufma.extensao.service.exceptions.UsuarioInativoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    UsuarioRepository repository;

    public boolean efetuarLogin(String email, String senha) {
        Optional<Usuario> usr = repository.findByEmail(email);
        if (!usr.isPresent())
            throw new RegraNegocioRunTime("Erro de autenticacao. Email nao encontrado.");
        if (!usr.get().isAtivo())
            throw new UsuarioInativoException("Conta desativada. Procure o administrador.");
        if (!usr.get().getSenha().equals(senha))
            throw new RegraNegocioRunTime("Erro de autenticacao. Senha invalida.");
        return true;
    }

    @Transactional
    public Usuario salvar(Usuario usuario) {
        verificaUsuario(usuario);
        return repository.save(usuario);
    }

    @Transactional
    public Usuario atualizar(Usuario usuario) {
        verificarId(usuario);
        return repository.save(usuario);
    }

    @Transactional
    public Usuario desativarUsuario(Integer id) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Usuario nao encontrado."));
        usuario.setAtivo(false);
        return repository.save(usuario);
    }

    public void remover(Usuario usuario) {
        verificarId(usuario);
        repository.delete(usuario);
    }

    public void remover(Integer id) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Usuario nao encontrado."));
        remover(usuario);
    }

    public List<Usuario> buscar(Usuario filtro) {
        Example<Usuario> example = Example.of(filtro,
                ExampleMatcher.matching()
                        .withIgnoreCase()
                        .withIgnorePaths("ativo")
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));
        return repository.findAll(example);
    }

    private void verificarId(Usuario usuario) {
        if ((usuario == null) || (usuario.getId() == null))
            throw new RegraNegocioRunTime("Usuario invalido (sem id).");
    }

    private void verificaUsuario(Usuario usuario) {
        if (usuario == null)
            throw new RegraNegocioRunTime("Um usuario valido deve ser informado.");
        if ((usuario.getNome() == null) || (usuario.getNome().trim().equals("")))
            throw new RegraNegocioRunTime("Nome do usuario deve ser informado.");
        if ((usuario.getEmail() == null) || (usuario.getEmail().trim().equals("")))
            throw new RegraNegocioRunTime("Email deve ser informado.");
        if (emailDeOutroUsuario(usuario))
            throw new EmailJaCadastradoException("Email informado ja existe.");
        if ((usuario.getSenha() == null) || (usuario.getSenha().trim().equals("")))
            throw new RegraNegocioRunTime("Usuario deve possuir senha.");
    }

    private boolean emailDeOutroUsuario(Usuario usuario) {
        if (!repository.existsByEmail(usuario.getEmail()))
            return false;
        Optional<Usuario> dono = repository.findByEmail(usuario.getEmail());
        return usuario.getId() == null
                || dono.isEmpty()
                || !dono.get().getId().equals(usuario.getId());
    }
}
