package br.com.cruz.vita.criptografia.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CriptografiaController {

	
	
	@PostMapping("/encrypt")
	public ResponseEntity<String> encryptPassword(@RequestBody String password){
		String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(hashedPassword);
	}
	
}
