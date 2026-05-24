package excecoes;

// Raiz da arvore de excecoes do dominio. Capturar essa classe pega qualquer
// erro de negocio do sistema - util quando se quer um catch generico no menu.
// Herda de RuntimeException porque sao erros de negocio - obrigar try/catch
// em toda chamada de service so ia poluir o codigo.
public class SistemaExtensaoException extends RuntimeException {

    public SistemaExtensaoException(String message) {
        super(message);
    }
}
