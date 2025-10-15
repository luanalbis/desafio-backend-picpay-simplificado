package com.picpaysimplificado.exceptions;

import org.springframework.http.HttpStatus;

public class UnauthorizedTransactionException extends BusinessException {

    private static final long serialVersionUID = 1L;

    public UnauthorizedTransactionException() {
        super("Transferência não autorizada", HttpStatus.FORBIDDEN);
    }

    public UnauthorizedTransactionException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}
