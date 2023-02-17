package br.com.cruz.vita.usuario.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
	
	@NotBlank(message = "Este campo é obrigatório!")
	@Email(message = "Insira um e-email válido!")
	private String email;
	
	@NotBlank(message = "Este campo é obrigatório!")
	private String senha;
	
	@NotBlank(message = "Este campo é obrigatório!")
	private String cpf;
	
}
