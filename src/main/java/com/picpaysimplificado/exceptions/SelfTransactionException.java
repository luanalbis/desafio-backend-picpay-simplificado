package com.picpaysimplificado.exceptions;

import org.springframework.http.HttpStatus;

public class SelfTransactionException extends BusinessException {

	private static final long serialVersionUID = 1L;

	public SelfTransactionException() {
		super("Não é possível enviar uma transação para o mesmo usuário.", HttpStatus.BAD_REQUEST);
	}

	public SelfTransactionException(String message) {
		super(message, HttpStatus.BAD_REQUEST);
	}
}
