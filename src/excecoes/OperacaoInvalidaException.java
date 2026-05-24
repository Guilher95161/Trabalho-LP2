package excecoes;

// A acao existe, mas o estado atual nao permite executar agora.
// Exemplos comuns: encerrar oportunidade ja encerrada, deferir solicitacao
// que ja foi avaliada, cadastrar uma versao de PPC com ano que ja existe.
public class OperacaoInvalidaException extends SistemaExtensaoException {

    public OperacaoInvalidaException(String message) {
        super(message);
    }
}
