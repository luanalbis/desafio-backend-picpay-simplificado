package com.picpaysimplificado.exceptions;

import org.springframework.http.HttpStatus;

public class UserNotAllowedException extends BusinessException {

    private static final long serialVersionUID = 1L;

    public UserNotAllowedException() {
        super("Usuário não autorizado a realizar esta operação", HttpStatus.FORBIDDEN);
    }

    public UserNotAllowedException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}
