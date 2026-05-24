package excecoes;

// Quando um buscarPor... no repositorio nao acha nada. Antes esses casos
// retornavam null e o chamador tinha que conferir - agora ja chega o erro
// pronto, com mensagem citando o ID que falhou.
public class EntidadeNaoEncontradaException extends SistemaExtensaoException {

    public EntidadeNaoEncontradaException(String message) {
        super(message);
    }
}
