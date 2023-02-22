package br.com.cruz.vita.usuario.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "cruz-vita-usuario-criptografia", url = "http://localhost:8080/")
public interface CriptografiaService {

	@PostMapping("/encrypt")
	public String encryPassaword(String password);
}
