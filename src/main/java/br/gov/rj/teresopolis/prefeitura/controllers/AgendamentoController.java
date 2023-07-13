package br.gov.rj.teresopolis.prefeitura.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

import br.gov.rj.teresopolis.prefeitura.domain.Agendamento;
import br.gov.rj.teresopolis.prefeitura.domain.Anexo;
import br.gov.rj.teresopolis.prefeitura.dto.AgendamentoDTO;
import br.gov.rj.teresopolis.prefeitura.dto.AgendamentoRequestDTO;
import br.gov.rj.teresopolis.prefeitura.dto.AgendamentoResponseDTO;
import br.gov.rj.teresopolis.prefeitura.dto.security.MessageResponseDTO;
import br.gov.rj.teresopolis.prefeitura.services.AgendamentoService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/agendamento")
public class AgendamentoController {

	@Autowired
	AgendamentoService agendamentoService;

	@GetMapping("/lista")
	@Operation(summary = "Lista todos os agendamentos - ADM", description = "Listagem de agendamentos")
	public List<Agendamento> findAllId() {
		return agendamentoService.listarAgendamentos();
	}

	@GetMapping("/lista/{id}")
	@Operation(summary = "Encontra agendamento por id", description = "Encontrar agendamento por id")
	public Agendamento findId(@PathVariable("id") UUID id) {
		return agendamentoService.obterAgendamentoPorId(id);
	}

	@GetMapping("/lista/dia/{dia}") // 2023-07-01
	@Operation(summary = "Encontra agendamento por dia", description = "Encontrar agendamento por dia")
	public List<Agendamento> findId(@PathVariable("dia") String dia) {
		return agendamentoService.obterAgendamentoPorDia(dia);
	}

	@PostMapping("/inserir")
	@Operation(summary = "Inserir agendamento no banco de dados", description = "Inserir os agendamentos no banco de dados")
	public AgendamentoDTO cadastrarAgendamento(@Valid @RequestBody Agendamento agendamento) throws MessagingException {
		return agendamentoService.criarAgendamento(agendamento);
	}
	
	@PostMapping(path="/inserir/dto", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE,
			MediaType.APPLICATION_JSON_VALUE })
	@Operation(summary = "Cria um novo agendamento no banco recebendo um DTO", description = "Inserir agendamento no banco de dados passando todos os dados em apenas uma requisição")
	public AgendamentoResponseDTO cadastrarAgendamentoDTO(@RequestPart("dados") String agendamentoRequestDto, 
														  @RequestPart("files") Optional<MultipartFile[]> files ) {
		List<Anexo> anexosList = new ArrayList<Anexo>();
		
		if(files.isPresent()) {
			for (MultipartFile file : files.get()) {
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
		}
		
		
		
		return agendamentoService.criarAgendamentoDto(agendamentoRequestDto, anexosList);
	}
	
	
	@PostMapping("/inserir/email")
	@Operation(summary = "Envio de email e salvamento de agendamento no banco de dados", description = "Envio de agendamento por email e salvo no banco de dados")
	public Agendamento cadastroEnvioEmail(@Valid @RequestBody Agendamento agendamento) throws MessagingException {
		return agendamentoService.cadastroEnvioEmail(agendamento);
	}

	@PutMapping("/atualizar/{id}")
	@Operation(summary = "Atualiza o agendamento por id", description = "Atualizar agendamento")
	public AgendamentoDTO atualizarEndereco(@PathVariable("id") UUID id,
			@RequestBody Agendamento agendamentoSolicitado) {
		return agendamentoService.atualizarAgendamento(id, agendamentoSolicitado);
	}

	@DeleteMapping("/deletar/{id}")
	@Operation(summary = "Deletar agendamento", description = "Deletar categorias")
	public void deletarAgendamento(@PathVariable("id") UUID id) {
		agendamentoService.excluirAgendamento(id);
	}

}
