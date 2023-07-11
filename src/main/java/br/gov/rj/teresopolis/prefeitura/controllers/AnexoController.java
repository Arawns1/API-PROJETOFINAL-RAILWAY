package br.gov.rj.teresopolis.prefeitura.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.gov.rj.teresopolis.prefeitura.domain.Anexo;
import br.gov.rj.teresopolis.prefeitura.dto.AnexoDTO;
import br.gov.rj.teresopolis.prefeitura.services.AgendamentoService;
import br.gov.rj.teresopolis.prefeitura.services.AnexoService;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/anexo")
public class AnexoController {
	@Autowired
	AnexoService anexoService;

	@Autowired
	AgendamentoService agendamentoService;

	@GetMapping("/lista")
	@Operation(summary = "Lista todos os anexos - ADM", description = "Listagem de anexos")
	public List<AnexoDTO> findAllId() {
		return anexoService.listarTodosAnexos();
	}

	@GetMapping("/lista/{id}")
	@Operation(summary = "Encontra anexo por id", description = "Encontrar anexo por id")
	public AnexoDTO findId(@PathVariable("id") UUID id) {
		return anexoService.obterAnexoPorId(id);
	}
	
	@GetMapping("/lista/agendamento/{id}")
	@Operation(summary = "Encontra anexo por id do agendamento", description = "Encontrar anexo por id do agendamento")
	public List<AnexoDTO> obterAnexosByAgendamento(@PathVariable("id") UUID id) {
		return anexoService.obterAnexosByAgendamento(id);
	}

	@Operation(summary = "Salva os anexos no banco de dados", description = "Ao passar os anexos e o json do agendamento o anexo é salvo")
	@PostMapping(path = "/inserir", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE,
			MediaType.APPLICATION_JSON_VALUE })
	
	public String uploadAnexos(@RequestPart("files") MultipartFile[] files,
							   @RequestPart("agendamento") String agendamento) {
			List<Anexo> anexosList = new ArrayList<Anexo>();

			for (MultipartFile file : files) {
				String fileContentType = file.getContentType();
				
				byte[] sourceFileContent = null;
				
				try {
					sourceFileContent = file.getBytes();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				String fileName = file.getOriginalFilename();
				Anexo fileModal = new Anexo(sourceFileContent, fileContentType, fileName);
				anexosList.add(fileModal);
			}
			
			return anexoService.saveAllAnexosList(anexosList, agendamento);
	}

	@DeleteMapping("/deletar/{id}")
	@Operation(summary = "Deletar anexo", description = "Deletar categorias")
	public void deletarAnexo(@PathVariable("id") UUID id) {
		anexoService.excluirAnexo(id);
	}

}
