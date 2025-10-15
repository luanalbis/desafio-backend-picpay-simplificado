package com.picpaysimplificado.exceptions;

import org.springframework.http.HttpStatus;

public class DuplicateUserException extends BusinessException {

	private static final long serialVersionUID = 1L;

	public DuplicateUserException() {
		super("Usuário já cadastrado", HttpStatus.BAD_REQUEST);
	}

	public DuplicateUserException(String message) {
		super(message, HttpStatus.BAD_REQUEST);
	}
}
