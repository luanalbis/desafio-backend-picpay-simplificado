package com.picpaysimplificado.exceptions;

import org.springframework.http.HttpStatus;

public class NotificationFailedException extends BusinessException {

    private static final long serialVersionUID = 1L;

    public NotificationFailedException() {
        super("Serviço de notificação indisponível", HttpStatus.BAD_GATEWAY);
    }

    public NotificationFailedException(String message) {
        super(message, HttpStatus.BAD_GATEWAY);
    }
}
