package com.picpaysimplificado.infra;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.picpaysimplificado.dtos.ExceptionDTO;
import com.picpaysimplificado.exceptions.BusinessException;

@RestControllerAdvice
public class ControllerExceptionHandler {

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ExceptionDTO> handleBusinessException(BusinessException ex) {
		String code = String.valueOf(ex.getStatus().value());
		return ResponseEntity.status(ex.getStatus()).body(new ExceptionDTO(ex.getMessage(), code));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ExceptionDTO> handleGeneralException(Exception ex) {
		return ResponseEntity.internalServerError().body(new ExceptionDTO(ex.getMessage(), "500"));
	}
}
