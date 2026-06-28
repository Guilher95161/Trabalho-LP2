package br.ufma.extensao.config;

/**
 * Constantes da camada de seguranca (JWT). Em produção a chave viria de variável de ambiente;
 * aqui fica fixa para a demonstração. A chave precisa ter >= 64 bytes (512 bits) para HS512.
 */
public final class SecurityConstants {

    public static final String SECRET =
            "extensao-ufma-chave-secreta-de-pelo-menos-64-bytes-para-assinar-jwt-com-hs512-0123456789";
    public static final long EXPIRACAO_MS = 1000L * 60 * 60 * 8; // 8 horas
    public static final String HEADER = "Authorization";
    public static final String PREFIXO = "Bearer ";

    private SecurityConstants() {
    }
}
