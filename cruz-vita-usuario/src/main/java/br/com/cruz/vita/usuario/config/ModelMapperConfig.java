package br.com.cruz.vita.usuario.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
	
	@Bean
	public ModelMapper obterMapper() {
		return new ModelMapper();
	}

}
