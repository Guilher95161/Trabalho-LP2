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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioService implements UserDetailsService {

    @Autowired
    UsuarioRepository repository;

    @Autowired
    PasswordEncoder passwordEncoder;

    // Usado pelo Spring Security (filtro de autorizacao) para carregar o usuario e seus papeis.
    // @Transactional para a coleção LAZY `papeis` inicializar dentro da sessão (o filtro roda
    // antes do open-in-view, então sem isso daria LazyInitializationException).
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario nao encontrado: " + email));
        List<GrantedAuthority> authorities = (usuario.getPapeis() == null)
                ? Collections.emptyList()
                : usuario.getPapeis().stream()
                        .map(papel -> new SimpleGrantedAuthority("ROLE_" + papel.getNome()))
                        .collect(Collectors.toList());
        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getSenha())
                .disabled(!usuario.isAtivo())
                .authorities(authorities)
                .build();
    }

    public boolean efetuarLogin(String email, String senha) {
        Optional<Usuario> usr = repository.findByEmail(email);
        if (!usr.isPresent())
            throw new RegraNegocioRunTime("Erro de autenticacao. Email nao encontrado.");
        if (!usr.get().isAtivo())
            throw new UsuarioInativoException("Conta desativada. Procure o administrador.");
        if (!passwordEncoder.matches(senha, usr.get().getSenha()))
            throw new RegraNegocioRunTime("Erro de autenticacao. Senha invalida.");
        return true;
    }

    @Transactional
    public Usuario salvar(Usuario usuario) {
        verificaUsuario(usuario);
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return repository.save(usuario);
    }

    @Transactional
    public Usuario atualizar(Usuario patch) {
        verificarId(patch);
        Usuario existente = repository.findById(patch.getId())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Usuario nao encontrado."));
        if (patch.getNome() != null && !patch.getNome().isBlank())
            existente.setNome(patch.getNome());
        if (patch.getEmail() != null && !patch.getEmail().isBlank()) {
            if (emailDeOutroUsuario(patch))
                throw new EmailJaCadastradoException("Email informado ja existe.");
            existente.setEmail(patch.getEmail());
        }
        if (patch.getMatricula() != null)
            existente.setMatricula(patch.getMatricula());
        if (patch.getSenha() != null && !patch.getSenha().isBlank())
            existente.setSenha(passwordEncoder.encode(patch.getSenha()));
        return repository.save(existente);
    }

    @Transactional
    public Usuario desativarUsuario(Integer id) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Usuario nao encontrado."));
        usuario.setAtivo(false);
        return repository.save(usuario);
    }

    @Transactional
    public Usuario reativarUsuario(Integer id) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Usuario nao encontrado."));
        usuario.setAtivo(true);
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

    public Usuario buscarPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Usuario nao encontrado."));
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
