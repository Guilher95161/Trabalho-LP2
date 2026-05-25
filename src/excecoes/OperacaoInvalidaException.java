package excecoes;

// acao valida mas o estado atual nao deixa executar agora
public class OperacaoInvalidaException extends SistemaExtensaoException {

    public OperacaoInvalidaException(String message) {
        super(message);
    }
}
