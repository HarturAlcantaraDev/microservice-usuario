package br.com.cruz.vita.usuario.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ApplicationExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(Exception.class)
	public ResponseEntity<DefaultError> tratarNullPointerException(Exception ex) {

		DefaultError erro = new DefaultError(HttpStatus.BAD_REQUEST.value(), "Erro ao processar sua requisição");
		return new ResponseEntity<DefaultError>(erro, HttpStatus.BAD_REQUEST);

	}
}
