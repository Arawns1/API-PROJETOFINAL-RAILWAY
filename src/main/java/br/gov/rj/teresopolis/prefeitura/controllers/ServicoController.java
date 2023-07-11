package br.gov.rj.teresopolis.prefeitura.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.gov.rj.teresopolis.prefeitura.domain.Servico;
import br.gov.rj.teresopolis.prefeitura.dto.ServicoDTO;
import br.gov.rj.teresopolis.prefeitura.services.ServicoService;
import io.swagger.v3.oas.annotations.Operation;


@RestController
@RequestMapping("/servico")


public class ServicoController {
	@Autowired
	ServicoService servicoService;
	
	@GetMapping("/lista/{id}")
	@Operation(summary= "Encontra servico por id", description= "Encontrar servico por id")
	public ServicoDTO findId(@PathVariable("id") UUID id) {
		return servicoService.obterServicoPorId(id);
	}
	
	@GetMapping("/lista")
	@Operation(summary= "Lista todos os servicos - ADM", description= "Listagem de servicos")
	public List<ServicoDTO> findAllId() {
		return servicoService.listarServicos();
	}
	
	@Operation(summary = "Salva o servico no banco de dados", description = "Ao passar os imagens e o json do servico a imagem é salvo")
	@PostMapping(path = "/inserir", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE,
			MediaType.APPLICATION_JSON_VALUE })
	
	public ServicoDTO cadastrarServico(@RequestPart("imagem") MultipartFile file,
							    @RequestPart("servico") String servico) {
			return servicoService.criarServico(file, servico);
	}
	
	@PutMapping("/atualizar/{id}")
	@Operation(summary= "Atualiza servico por id", description= "Atualiza servico")
	public ServicoDTO atualizarServico(@PathVariable("id") UUID id, @RequestBody Servico servicoSolicitado) {
		return servicoService.atualizarServico(id, servicoSolicitado);
	}
	
	@DeleteMapping("/deletar/{id}")
	@Operation(summary= "Deleta servico", description= "Deleta servico")
	public void deletarServico(@PathVariable("id") UUID id) {
		servicoService.deletarServico(id);
	}
	
	
	
	
	
		
	
	
	
	

}
