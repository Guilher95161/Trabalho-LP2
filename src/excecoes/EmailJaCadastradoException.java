package excecoes;

// Email duplicado no cadastro. Ele e a chave de login, entao nao pode repetir.
public class EmailJaCadastradoException extends SistemaExtensaoException {

    public EmailJaCadastradoException(String email) {
        super("O e-mail '" + email + "' ja esta cadastrado no sistema.");
    }
}
