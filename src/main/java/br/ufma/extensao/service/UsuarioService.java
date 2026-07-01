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

    /**
     * Carrega o usuario e seus papeis para o Spring Security (usado pelo filtro de autorizacao).
     * @param email email do usuario que esta autenticando
     * @return os dados do usuario (UserDetails) com os papeis como ROLE_*
     * @throws UsernameNotFoundException se nao existir usuario com esse email
     */
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

    /**
     * Confere email e senha para autenticar o usuario (senha comparada com BCrypt).
     * @param email email informado no login
     * @param senha senha em texto puro informada no login
     * @return true se as credenciais forem validas
     * @throws RegraNegocioRunTime se o email nao existir ou a senha estiver errada
     * @throws UsuarioInativoException se a conta estiver desativada
     */
    public boolean efetuarLogin(String email, String senha) {
        Optional<Usuario> usr = repository.findByEmail(email);
        if (usr.isEmpty())
            throw new RegraNegocioRunTime("Erro de autenticacao. Email nao encontrado.");
        if (!usr.get().isAtivo())
            throw new UsuarioInativoException("Conta desativada. Procure o administrador.");
        if (!passwordEncoder.matches(senha, usr.get().getSenha()))
            throw new RegraNegocioRunTime("Erro de autenticacao. Senha invalida.");
        return true;
    }

    /**
     * Salva um novo usuario, validando os campos e codificando a senha com BCrypt.
     * @param usuario usuario a ser cadastrado
     * @return o usuario salvo (com id gerado)
     * @throws RegraNegocioRunTime se nome, email ou senha forem invalidos
     * @throws EmailJaCadastradoException se ja existir usuario com esse email
     */
    @Transactional
    public Usuario salvar(Usuario usuario) {
        verificarUsuario(usuario);
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return repository.save(usuario);
    }

    /**
     * Atualiza os campos informados de um usuario existente (so altera o que vier preenchido).
     * @param patch usuario com o id e os campos a atualizar
     * @return o usuario atualizado
     * @throws RegraNegocioRunTime se o id nao for informado
     * @throws EntidadeNaoEncontradaException se nao existir usuario com esse id
     * @throws EmailJaCadastradoException se o novo email pertencer a outro usuario
     */
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

    /**
     * Desativa a conta do usuario (login passa a ser bloqueado).
     * @param id id do usuario
     * @return o usuario ja desativado
     * @throws EntidadeNaoEncontradaException se nao existir usuario com esse id
     */
    @Transactional
    public Usuario desativarUsuario(Integer id) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Usuario nao encontrado."));
        usuario.setAtivo(false);
        return repository.save(usuario);
    }

    /**
     * Reativa a conta de um usuario que estava desativado.
     * @param id id do usuario
     * @return o usuario ja reativado
     * @throws EntidadeNaoEncontradaException se nao existir usuario com esse id
     */
    @Transactional
    public Usuario reativarUsuario(Integer id) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Usuario nao encontrado."));
        usuario.setAtivo(true);
        return repository.save(usuario);
    }

    /**
     * Remove o usuario informado.
     * @param usuario usuario a remover (precisa ter id)
     * @throws RegraNegocioRunTime se o id nao for informado
     */
    public void remover(Usuario usuario) {
        verificarId(usuario);
        repository.delete(usuario);
    }

    /**
     * Remove o usuario pelo id.
     * @param id id do usuario a remover
     * @throws EntidadeNaoEncontradaException se nao existir usuario com esse id
     */
    public void remover(Integer id) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Usuario nao encontrado."));
        remover(usuario);
    }

    /**
     * Busca usuarios usando o filtro como exemplo (contains e ignorando maiusculas/minusculas).
     * @param filtro usuario com os campos que servem de criterio de busca
     * @return a lista de usuarios que casam com o filtro
     */
    public List<Usuario> buscar(Usuario filtro) {
        Example<Usuario> example = Example.of(filtro,
                ExampleMatcher.matching()
                        .withIgnoreCase()
                        .withIgnorePaths("ativo")
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));
        return repository.findAll(example);
    }

    /**
     * Busca um usuario pelo id.
     * @param id id do usuario
     * @return o usuario encontrado
     * @throws EntidadeNaoEncontradaException se nao existir usuario com esse id
     */
    public Usuario buscarPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Usuario nao encontrado."));
    }

    private void verificarId(Usuario usuario) {
        if ((usuario == null) || (usuario.getId() == null))
            throw new RegraNegocioRunTime("Usuario invalido (sem id).");
    }

    private void verificarUsuario(Usuario usuario) {
        if (usuario == null)
            throw new RegraNegocioRunTime("Um usuario valido deve ser informado.");
        if ((usuario.getNome() == null) || (usuario.getNome().isBlank()))
            throw new RegraNegocioRunTime("Nome do usuario deve ser informado.");
        if ((usuario.getEmail() == null) || (usuario.getEmail().isBlank()))
            throw new RegraNegocioRunTime("Email deve ser informado.");
        if (emailDeOutroUsuario(usuario))
            throw new EmailJaCadastradoException("Email informado ja existe.");
        if ((usuario.getSenha() == null) || (usuario.getSenha().isBlank()))
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
