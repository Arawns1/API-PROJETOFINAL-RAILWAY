package br.gov.rj.teresopolis.prefeitura.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.gov.rj.teresopolis.prefeitura.domain.Pessoa;
import br.gov.rj.teresopolis.prefeitura.dto.PessoaDTO;
import br.gov.rj.teresopolis.prefeitura.services.PessoaService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/pessoa")


public class PessoaController {
	@Autowired
	PessoaService pessoaService;
	
	@GetMapping("/lista/{id}")
	@Operation(summary= "Encontra pessoa por id", description= "Encontrar pessoa por id")
	public PessoaDTO findId(@PathVariable("id") UUID id) {
		return pessoaService.obterPessoaPorId(id);
	}
	
	@GetMapping("/lista")
	@Operation(summary= "Lista todos os pessoas - ADM", description= "Listagem de pessoas")
	public List<PessoaDTO> findAllId() {
		return pessoaService.listarPessoas();
	}
	
	@PostMapping("/inserir")
	@Operation(summary= "Inserir pessoa no banco de dados", description= "Inserção de pessoa no banco de dados")
	public PessoaDTO cadastrarPessoa(@Valid @RequestBody Pessoa pessoa) throws MessagingException {
		return pessoaService.criarPessoa(pessoa);
	}
	
	@PutMapping("/atualizar/{id}")
	@Operation(summary= "Atualiza pessoa por id", description= "Atualiza pessoa")
	public PessoaDTO atualizarPessoa(@PathVariable("id") UUID id, @RequestBody Pessoa pessoaSolicitado) {
		return pessoaService.atualizarPessoa(id, pessoaSolicitado);
	}
	
	@DeleteMapping("/deletar/{id}")
	@Operation(summary= "Deleta pessoa", description= "Deleta pessoa")
	public void deletarPessoa(@PathVariable("id") UUID id) {
		pessoaService.deletarPessoa(id);
	}
	
	
	
	
	
		
	
	
	
	

}
