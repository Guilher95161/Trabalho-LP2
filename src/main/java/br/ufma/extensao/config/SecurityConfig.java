package br.ufma.extensao.config;

import br.ufma.extensao.service.UsuarioService;
import jakarta.servlet.DispatcherType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security STATELESS com JWT (Boot 4 / Security 7).
 *
 * Liberados sem token: cadastro (`POST /api/usuarios`), login (`POST /api/usuarios/autenticar`) e o
 * console do H2. Todo o resto exige `Authorization: Bearer <token>`. A autorização por papel
 * (`hasRole`) fica para a Fase 3 — aqui basta estar autenticado.
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtService jwtService;
    private final UsuarioService usuarioService;

    public SecurityConfig(JwtService jwtService, UsuarioService usuarioService) {
        this.jwtService = jwtService;
        this.usuarioService = usuarioService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/usuarios", "/api/usuarios/autenticar", "/api/discentes").permitAll()
                        .anyRequest().authenticated())
                // o H2 console roda em frames (mesma origem)
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .addFilterBefore(new JwtAuthorizationFilter(jwtService, usuarioService),
                        UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
