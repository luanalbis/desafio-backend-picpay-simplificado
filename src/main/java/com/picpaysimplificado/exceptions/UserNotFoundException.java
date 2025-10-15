package com.picpaysimplificado.exceptions;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BusinessException {

	private static final long serialVersionUID = 1L;

	public UserNotFoundException() {
		super("Usuário não encontrado", HttpStatus.NOT_FOUND);
	}

	public UserNotFoundException(String message) {
		super(message, HttpStatus.NOT_FOUND);
	}
}
