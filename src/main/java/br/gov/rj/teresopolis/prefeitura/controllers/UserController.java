package br.gov.rj.teresopolis.prefeitura.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.gov.rj.teresopolis.prefeitura.dto.security.UserDTO;
import br.gov.rj.teresopolis.prefeitura.services.UserService;
import io.swagger.v3.oas.annotations.Operation;


@RestController
@RequestMapping("/usuarios")


public class UserController {
	@Autowired
	UserService userService;
	
	@GetMapping("/lista/{id}")
	@Operation(summary= "Encontra user por id", description= "Encontrar user por id")
	public UserDTO findId(@PathVariable("id") UUID id) {
		return userService.obterUserPorId(id);
	}
	
	@GetMapping("/lista")
	@Operation(summary= "Lista todos os users - ADM", description= "Listagem de users")
	public List<UserDTO> findAllId() {
		return userService.listarUsers();
	}
	
	/*
	 * @PutMapping("/atualizar/{id}")
	 * 
	 * @Operation(summary= "Atualiza user por id", description= "Atualiza user")
	 * public User atualizarUser(@PathVariable("id") Integer id, @RequestBody User
	 * userSolicitado) { return userService.atualizarUser(id, userSolicitado); }
	 */
	
	@DeleteMapping("/deletar/{id}")
	@Operation(summary= "Deleta user", description= "Deleta user")
	public void deletarUser(@PathVariable("id") UUID id) {
		userService.deletarUser(id);
	}

}
