package br.ufma.extensao.service.exceptions;

public class UsuarioInativoException extends RegraNegocioRunTime {
    public UsuarioInativoException(String msg) {
        super(msg);
    }
}
