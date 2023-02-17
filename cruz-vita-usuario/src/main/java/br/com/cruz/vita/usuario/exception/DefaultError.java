package br.com.cruz.vita.usuario.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DefaultError {
	
	private Integer code;
	private String message;

}
