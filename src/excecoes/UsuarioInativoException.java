package excecoes;

// conta desativada tentando operar no sistema
public class UsuarioInativoException extends SistemaExtensaoException {

    public UsuarioInativoException(String email) {
        super("A conta associada ao e-mail '" + email + "' esta desativada e nao pode operar no sistema.");
    }
}
