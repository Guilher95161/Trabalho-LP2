package excecoes;

// base das excecoes do dominio - catch generico no menu pega qualquer uma
public class SistemaExtensaoException extends RuntimeException {

    public SistemaExtensaoException(String message) {
        super(message);
    }
}
