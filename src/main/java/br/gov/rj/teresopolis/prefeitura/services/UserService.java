package br.gov.rj.teresopolis.prefeitura.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.gov.rj.teresopolis.prefeitura.domain.security.User;
import br.gov.rj.teresopolis.prefeitura.dto.OrgaoDTO;
import br.gov.rj.teresopolis.prefeitura.dto.security.UserDTO;
import br.gov.rj.teresopolis.prefeitura.exceptions.NoSuchElementException;
import br.gov.rj.teresopolis.prefeitura.repositories.UserRepository;
import jakarta.transaction.Transactional;

@Service
public class UserService {
	@Autowired
	UserRepository userRepository;

	ModelMapper modelMapper = new ModelMapper();
	
	@Transactional
	public User findByUsername(String username) {
		return userRepository.findByUsername(username).orElse(null);
	}

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Transactional
	public List<UserDTO> listarUsers() {
		List<UserDTO> usersDTO = new ArrayList<>();
		for (User user : userRepository.findAll()) {
			UserDTO userDto = new UserDTO();
			userDto.setId(user.getId());
			userDto.setUsername(user.getUsername());
			userDto.setEmail(user.getEmail());

			Set<String> rolesString = user.getRoles().stream().map(role -> role.getName().name())
					.collect(Collectors.toSet());

			userDto.setRoles(rolesString);

			OrgaoDTO orgaoDto = modelMapper.map(user.getOrgao(), OrgaoDTO.class);
			userDto.setOrgao(orgaoDto);
			usersDTO.add(userDto);
		}
		return usersDTO;
	}

	@Transactional
	public UserDTO obterUserPorId(UUID id) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new NoSuchElementException("Não foi encontrado o user de id= " + id));

		UserDTO userDto = new UserDTO();
		userDto.setId(user.getId());
		userDto.setUsername(user.getUsername());
		userDto.setEmail(user.getEmail());

		Set<String> rolesString = user.getRoles().stream().map(role -> role.getName().name())
				.collect(Collectors.toSet());
		userDto.setRoles(rolesString);
		OrgaoDTO orgaoDto = modelMapper.map(user.getOrgao(), OrgaoDTO.class);
		userDto.setOrgao(orgaoDto);

		return userDto;
	}

	/*
	 * @Transactional public User atualizarUser(Integer id, User user) {
	 * Optional<User> userExistenteOptional = userRepository.findById(id); if
	 * (userExistenteOptional.isPresent()) {
	 * System.out.println(userExistenteOptional);
	 * 
	 * User userExistente = userExistenteOptional.get();
	 * 
	 * userExistente.setId(id); userExistente.setUsername(user.getUsername());
	 * userExistente.setEmail(user.getEmail());
	 * userExistente.setRoles(user.getRoles());
	 * userExistente.setOrgao(user.getOrgao()); User userSalvo =
	 * userRepository.save(userExistente);
	 * 
	 * 
	 * return new User(); } else { throw new
	 * NoSuchElementException("Não foi encontrado o user de id = " + id +
	 * " para ser atualizado"); } }
	 */

	public void deletarUser(UUID id) {
		userRepository.deleteById(id);
	}

}
