package excecoes;

// Conta desativada tentando logar ou executar acao no sistema.
// O Admin desativa contas por motivos administrativos (egresso, problema
// disciplinar) e o sistema barra qualquer operacao ate uma eventual reativacao.
public class UsuarioInativoException extends SistemaExtensaoException {

    public UsuarioInativoException(String email) {
        super("A conta associada ao e-mail '" + email + "' esta desativada e nao pode operar no sistema.");
    }
}
