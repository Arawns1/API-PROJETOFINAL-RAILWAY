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

import br.gov.rj.teresopolis.prefeitura.domain.Orgao;
import br.gov.rj.teresopolis.prefeitura.dto.OrgaoDTO;
import br.gov.rj.teresopolis.prefeitura.services.OrgaoService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/orgao")
public class OrgaoController {

	@Autowired
	OrgaoService orgaoService;
	@GetMapping("/lista")
	@Operation(summary= "Lista todos os Orgaos - ADM", description= "Listagem de orgaos")
	public List<Orgao> findAllId() {
		return orgaoService.listarOrgaos();
	}

	@GetMapping("/lista/{id}")
	@Operation(summary = "Encontra orgao por id", description = "Encontrar orgao por id")
	public OrgaoDTO findId(@PathVariable("id") UUID id) {
		return orgaoService.obterOrgaoPorId(id);
	}

	@PostMapping("/inserir")
	@Operation(summary = "Inserir categoria no banco de dados", description = "Listagem das categorias no banco de dados")
	public OrgaoDTO cadastrarOrgao(@Valid @RequestBody Orgao orgao) throws MessagingException {
		return orgaoService.criarOrgao(orgao);
	}
	@PutMapping("/atualizar/{id}")

	@Operation(summary= "Atualiza orgao por id", description= "Atualiza orgao")
	public OrgaoDTO atualizarOrgao(@PathVariable("id") UUID id, @RequestBody Orgao orgaoSolicitado) {
		return orgaoService.atualizarOrgao(id, orgaoSolicitado);
	}
	
	@DeleteMapping("/deletar/{id}")
	@Operation(summary = "Deletar orgao", description = "Deletar categorias")
	public void deletarOrgao(@PathVariable("id") UUID id) {
		orgaoService.excluirOrgao(id);
	}
}
