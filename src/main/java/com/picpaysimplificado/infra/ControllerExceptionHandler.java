package com.picpaysimplificado.infra;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.picpaysimplificado.dtos.ExceptionDTO;
import com.picpaysimplificado.exceptions.BusinessException;

@RestControllerAdvice
public class ControllerExceptionHandler {

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ExceptionDTO> handleBusinessException(BusinessException ex) {
		String code = String.valueOf(ex.getStatus().value());
		return ResponseEntity.status(ex.getStatus()).body(new ExceptionDTO(ex.getMessage(), code));
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ExceptionDTO> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
		String message = String.format("O parâmetro '%s' é inválido: %s", ex.getName(), ex.getValue());
		return ResponseEntity.badRequest().body(new ExceptionDTO(message, "400"));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getFieldErrors().forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ExceptionDTO> handleGeneralException(Exception ex) {
		return ResponseEntity.internalServerError().body(new ExceptionDTO(ex.getMessage(), "500"));
	}
}
