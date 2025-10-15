package com.picpaysimplificado.exceptions;

import org.springframework.http.HttpStatus;

public class InsufficientBalanceException extends BusinessException {

    private static final long serialVersionUID = 1L;

    public InsufficientBalanceException() {
        super("Saldo insuficiente para transferência", HttpStatus.BAD_REQUEST);
    }

    public InsufficientBalanceException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
