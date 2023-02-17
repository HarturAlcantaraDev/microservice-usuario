package br.com.cruz.vita.usuario.service;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.text.MaskFormatter;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import br.com.caelum.stella.validation.CPFValidator;
import br.com.cruz.vita.usuario.dto.ResponseUsuarioDTO;
import br.com.cruz.vita.usuario.dto.UsuarioDTO;
import br.com.cruz.vita.usuario.model.UsuarioModel;
import br.com.cruz.vita.usuario.repository.UsuarioRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Service
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private ModelMapper modelMapper;

	/* listar todos os usuarios através do metodo findAll da JpaRepository */
	public List<ResponseUsuarioDTO> listarUsuario() {
		List<UsuarioModel> lista = usuarioRepository.findAll();
		List<ResponseUsuarioDTO> listaResposta = lista.stream()
				.map(user -> modelMapper.map(user, ResponseUsuarioDTO.class)).collect(Collectors.toList());
		return listaResposta;
	}

	/* Busca todos usuarios por email que é passado na Url */
	public String buscarPorEmail(String email) {
		
		if (verificarEmailJaExiste(email)) {
			return "usuario não encontrado";
		} else {
			return "Email possui cadastro vinculado com o cpf: " + usuarioRepository.findByEmail(email).get().getCpf();
		}

	}

	/* busca usuarios desativados pela data de exclusao */
	public List<ResponseUsuarioDTO> buscarDesativados() {

		List<UsuarioModel> lista = usuarioRepository.findByDataExclusao();
		List<ResponseUsuarioDTO> listaResposta = lista.stream()
				.map(user -> modelMapper.map(user, ResponseUsuarioDTO.class)).collect(Collectors.toList());

		return listaResposta;
	}

	/* busca usuarios desativados pela data de ativos */
	public List<ResponseUsuarioDTO> buscarAtivados() {

		List<UsuarioModel> lista = usuarioRepository.findByDataInclusao();
		List<ResponseUsuarioDTO> listaResposta = lista.stream()
				.map(user -> modelMapper.map(user, ResponseUsuarioDTO.class)).collect(Collectors.toList());

		return listaResposta;
	}

	/* cadastra um novo usuario */
	public String cadastrarUsuario(UsuarioDTO usuario) {

		if (verificarEmailJaExiste(usuario.getEmail()) && verificarSeCPFJaExiste(usuario.getCpf())
				&& validarCPF(usuario.getCpf())) {
			UsuarioModel usuarioNovo = modelMapper.map(usuario, UsuarioModel.class);
			usuarioNovo.setDataInclusao(LocalDateTime.now());
			usuarioNovo.setCpf(formatarCpf(usuario.getCpf()));
			usuarioRepository.save(usuarioNovo);
			
			return "usuário criado com sucesso!";
			
		} else {

			return "usuario " + usuario.getCpf() + " já existente em nosso sistema!";
		}

	}

	/* cadastra uma lista de usuarios */
	public ResponseEntity<String> cadastrarPorLote(List<UsuarioDTO> usuario) {
		
		try {
			
			for (UsuarioDTO itemLista : usuario) {
				
				if (verificarEmailJaExiste(itemLista.getEmail()) && verificarSeCPFJaExiste(itemLista.getCpf())
						&& validarCPF(itemLista.getCpf())) {
					UsuarioModel usuarioModel = modelMapper.map(itemLista, UsuarioModel.class);
					usuarioModel.setDataInclusao(LocalDateTime.now());
					usuarioModel.setCpf(formatarCpf(itemLista.getCpf()));
					usuarioRepository.save(usuarioModel);
				
				} else {
					
					return ResponseEntity.status(HttpStatus.CREATED)
							.body("usuario " + itemLista.getCpf() + " já existente em nosso sistema!");
				}
			}
			
			return ResponseEntity.status(HttpStatus.CREATED).body("Lote cadastrado com sucesso");

		} catch (DataIntegrityViolationException ex) {

			return ResponseEntity.status(HttpStatus.CREATED).body("Cadastro já existente");
		}
	}

	/* verifica se o CPF já é existente no banco de dados */
	public Boolean verificarSeCPFJaExiste(String cpf) {

		if (usuarioRepository.findByCpf(cpf).isPresent()) {
			return false;
		} else {

			return true;
		}
	}

	/* formatar cpf */
	public String formatarCpf(String cpf) {
		
		if (cpf == null || cpf.length() != 11) {
			return "CPF inválido";
		}

		try {
			MaskFormatter mask = new MaskFormatter("###.###.###-##");
			mask.setValueContainsLiteralCharacters(false);
			return mask.valueToString(cpf);

		} catch (ParseException ep) {
			ep.printStackTrace();
			return "Erro ao formatar CPF";
		}
	}

	/* valida se o cpf é verdadeiro ou falso */
	public Boolean validarCPF(String cpf) {
		
		CPFValidator cpfValidator = new CPFValidator();
		try {
			cpfValidator.assertValid(cpf);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/* verifica se o email existe em nosso banco de dados */
	public Boolean verificarEmailJaExiste(String email) {
		
		if (usuarioRepository.findByEmail(email).isPresent()) {
			return false;
		} else {
			return true;
		}
	}

	/* atualiza o usuario através do email passado e retorna uma mensagem */
	public String atualizarViaEmail(UsuarioDTO usuario, String email) {
		
		UsuarioModel buscaEmail = usuarioRepository.findByEmail(email).get();
		UsuarioModel usuarioModel = modelMapper.map(usuario, UsuarioModel.class);
		usuarioModel.setId(buscaEmail.getId());
		String cpfUsuario = usuarioModel.getCpf();
		usuarioRepository.save(usuarioModel);

		return "Usuário vinculado ao cpf " + cpfUsuario + " atualizado com sucesso.";
	}

	/* exclui de uma vez por todas o usuario do banco de dados */
	public String excluirPorEmail(String email) {
		
		UsuarioModel buscaEmail = usuarioRepository.findByEmail(email).get();
		String cpfUsuario = buscaEmail.getCpf();
		usuarioRepository.delete(buscaEmail);
		
		return "usuário vinculado ao cpf " + cpfUsuario + " excluido com sucesso";
	}

	/* exclusão logica de usuario */
	public String deletarPorEmail(String email) {
		
		UsuarioModel buscaEmail = usuarioRepository.findByEmail(email).get();
//		DateTimeFormatter formatarDataEHora = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
		buscaEmail.setDataExclusao(LocalDateTime.now());
		String cpfUsuario = buscaEmail.getCpf();
		usuarioRepository.save(buscaEmail);

		return "usuário vinculado ao cpf " + cpfUsuario + " deletado com sucesso!";
	}

}
