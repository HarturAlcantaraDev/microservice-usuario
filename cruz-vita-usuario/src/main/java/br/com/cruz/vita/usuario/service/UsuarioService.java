package br.com.cruz.vita.usuario.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

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
		List<ResponseUsuarioDTO> dto = lista.stream().map(user -> modelMapper.map(user, ResponseUsuarioDTO.class))
				.collect(Collectors.toList());
		return dto;
	}

	/* Busca todos usuarios por email que é passado na Url */
	public String buscarPorEmail(String email) {

		return "Email possui cadastro vinculado com o cpf: " + usuarioRepository.findByEmail(email).get().getCpf();
	}

	/* busca usuarios desativados pela data de exclusao */
	public List<ResponseUsuarioDTO> buscarDesativados() {
//		LocalDateTime usuarioExcluido = usuarioModel.getDataExclusao();

		List<UsuarioModel> lista = usuarioRepository.findByDataExclusao();
		List<ResponseUsuarioDTO> dto = lista.stream().map(user -> modelMapper.map(user, ResponseUsuarioDTO.class))
				.collect(Collectors.toList());

		return dto;
	}

	/* busca usuarios desativados pela data de ativos */
	public List<ResponseUsuarioDTO> buscarAtivados() {
//		LocalDateTime usuarioExcluido = usuarioModel.getDataExclusao();

		List<UsuarioModel> lista = usuarioRepository.findByDataInclusao();
		List<ResponseUsuarioDTO> dto = lista.stream().map(user -> modelMapper.map(user, ResponseUsuarioDTO.class))
				.collect(Collectors.toList());

		return dto;
	}

	/* cadastra um novo usuario */
	public String cadastrarUsuario(UsuarioDTO usuario) {

		validarCPF(usuario.getCpf());
		verificarEmailJaExiste(usuario);
		UsuarioModel usuarioNovo = modelMapper.map(usuario, UsuarioModel.class);
		usuarioNovo.setDataInclusao(LocalDateTime.now());
		usuarioRepository.save(usuarioNovo);
		return "usuário criado com sucesso!";

	}

	/* cadastra uma lista de usuarios */
	public String cadastrarPorLote(List<UsuarioDTO> usuario) {

		for (UsuarioDTO itemLista : usuario) {

			if (verificarSeExiste(itemLista)) {

				throw new DataIntegrityViolationException(
						"O CPF: " + itemLista.getCpf() + " já existe em nosso sistema");
			}
			UsuarioModel usuarioModel = modelMapper.map(itemLista, UsuarioModel.class);
			usuarioModel.setDataInclusao(LocalDateTime.now());
			usuarioRepository.save(usuarioModel);

		}
		return "Lote cadastrado com sucesso";

	}

	/* verifica se o CPF já é existente no banco de dados */
	public Boolean verificarSeExiste(UsuarioDTO usuarioDTO) {

		if (usuarioRepository.findByCpf(usuarioDTO.getCpf()).isEmpty()) {
			return false;
		}

		return true;
	}

	/* valida se o cpf é verdadeiro ou falso */
	public static boolean validarCPF(String cpf) {
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
	public String verificarEmailJaExiste(UsuarioDTO usuarioDTO) {
		Optional<UsuarioModel> usuarioModel = usuarioRepository.findByEmail(usuarioDTO.getEmail());
		if (usuarioModel.isPresent()) {
			return "O email: " + usuarioModel.get().getEmail() + " já existe em nosso sistema";
		}
		return "";
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
