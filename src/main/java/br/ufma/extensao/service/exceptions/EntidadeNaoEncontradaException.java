package br.ufma.extensao.service.exceptions;

public class EntidadeNaoEncontradaException extends RegraNegocioRunTime {
    public EntidadeNaoEncontradaException(String msg) {
        super(msg);
    }
}
