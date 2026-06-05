package br.ufma.excecao;

public class EmailJaCadastradoException extends SistemaExtensaoException {

    public EmailJaCadastradoException(String email) {
        super("O e-mail '" + email + "' ja esta cadastrado no sistema.");
    }
}
