package br.com.cruz.vita.usuario.exception;

import javax.validation.ConstraintViolationException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ApplicationExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<String> handlerException(Exception e) {

		return new ResponseEntity<String>("ConstraintViolationException", HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(NullPointerException.class)
	public ResponseEntity<String> tratarNullPointerException(Exception e) {

		return new ResponseEntity<String>("NullPointerException", HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity tratarErroValidacao(Exception e, String msg) {

		return new ResponseEntity<String>(msg, HttpStatus.BAD_REQUEST);
	}

}
