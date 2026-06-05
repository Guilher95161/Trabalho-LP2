package br.ufma.excecao;

// quando o repositorio nao encontra o que foi pedido
public class EntidadeNaoEncontradaException extends SistemaExtensaoException {

    public EntidadeNaoEncontradaException(String message) {
        super(message);
    }
}
