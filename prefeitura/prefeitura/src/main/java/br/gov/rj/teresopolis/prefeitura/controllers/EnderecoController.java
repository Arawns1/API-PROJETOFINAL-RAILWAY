package br.gov.rj.teresopolis.prefeitura.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.gov.rj.teresopolis.prefeitura.domain.Endereco;
import br.gov.rj.teresopolis.prefeitura.dto.EnderecoDTO;
import br.gov.rj.teresopolis.prefeitura.dto.RequestEnderecoViaCepDTO;
import br.gov.rj.teresopolis.prefeitura.services.EnderecoService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/endereco")


public class EnderecoController {
	@Autowired
	EnderecoService enderecoService;
	
	@GetMapping("/lista/{id}")
	@Operation(summary= "Encontra endereço por id", description= "Encontrar endereço por id")
	public EnderecoDTO findId(@PathVariable("id") Integer id) {
		return enderecoService.obterEnderecoPorId(id);
	}
	
	@GetMapping("/lista")
	@Operation(summary= "Lista todos os endereços - ADM", description= "Listagem de endereços")
	public List<EnderecoDTO> findAllId() {
		return enderecoService.listarEnderecos();
	}
	
	@PostMapping("/inserir")
	@Operation(summary= "Inserir endereço no banco de dados", description= "Inserção de endereço no banco de dados")
	public EnderecoDTO cadastrarEndereco(@Valid @RequestBody Endereco endereco) throws MessagingException {
		return enderecoService.criarEndereco(endereco);
	}
	
	
	@PostMapping("/inserir/viacep")
	@Operation(summary= "Inserir endereço no banco de dados usando o via cep", description= "Inserção de endereço no banco de dados usando o via cep")
	public EnderecoDTO cadastrarEnderecoViaCep(@Valid @RequestBody RequestEnderecoViaCepDTO endereco) throws MessagingException {
		return enderecoService.criarEnderecoViaCep(endereco);
	}
	
	
	@PutMapping("/atualizar/{id}")
	@Operation(summary= "Atualiza endereço por id", description= "Atualiza endereço")
	public EnderecoDTO atualizarEndereco(@PathVariable("id") Integer id, @RequestBody Endereco enderecoSolicitado) {
		return enderecoService.atualizarEndereco(id, enderecoSolicitado);
	}
	
	@DeleteMapping("/deletar/{id}")
	@Operation(summary= "Deleta endereço", description= "Deleta endereço")
	public void deletarEndereco(@PathVariable("id") Integer id) {
		enderecoService.deletarEndereco(id);
	}
	
	
	
	
	
		
	
	
	
	

}
