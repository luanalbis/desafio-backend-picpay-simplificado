package com.picpaysimplificado.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidTransactionAmountException extends BusinessException {

    private static final long serialVersionUID = 1L;

    public InvalidTransactionAmountException() {
        super("Valor da transação menor ou igual a zero", HttpStatus.BAD_REQUEST);
    }

    public InvalidTransactionAmountException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}