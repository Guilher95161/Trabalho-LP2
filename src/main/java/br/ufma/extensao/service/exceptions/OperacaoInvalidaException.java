package br.ufma.extensao.service.exceptions;

public class OperacaoInvalidaException extends RegraNegocioRunTime {
    public OperacaoInvalidaException(String msg) {
        super(msg);
    }
}
