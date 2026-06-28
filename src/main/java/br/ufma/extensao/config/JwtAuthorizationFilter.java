package br.ufma.extensao.config;

import br.ufma.extensao.service.UsuarioService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Lê o header `Authorization: Bearer <token>`, valida o JWT e popula o SecurityContext com o
 * usuário (e seus papéis). NÃO é @Component de propósito: é instanciado no SecurityConfig para
 * não ser registrado também na cadeia de filtros do servlet (evita execução dupla).
 */
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UsuarioService usuarioService;

    public JwtAuthorizationFilter(JwtService jwtService, UsuarioService usuarioService) {
        this.jwtService = jwtService;
        this.usuarioService = usuarioService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader(SecurityConstants.HEADER);
        if (header != null && header.startsWith(SecurityConstants.PREFIXO)
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            String token = header.substring(SecurityConstants.PREFIXO.length());
            if (jwtService.isValido(token)) {
                UserDetails usuario = usuarioService.loadUserByUsername(jwtService.getEmail(token));
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        chain.doFilter(request, response);
    }
}
