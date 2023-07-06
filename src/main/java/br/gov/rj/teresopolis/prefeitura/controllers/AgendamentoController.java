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

import br.gov.rj.teresopolis.prefeitura.domain.Agendamento;
import br.gov.rj.teresopolis.prefeitura.dto.AgendamentoDTO;
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
	public Agendamento findId(@PathVariable("id") Integer id) {
		return agendamentoService.obterAgendamentoPorId(id);
	}

	@PostMapping("/inserir")
	@Operation(summary = "Inserir agendamento no banco de dados", description = "Listagem dos agendamentos no banco de dados")
	public AgendamentoDTO cadastrarAgendamento(@Valid @RequestBody Agendamento agendamento) throws MessagingException {
		return agendamentoService.criarAgendamento(agendamento);
	}

	@PostMapping("/inserir/email")
	@Operation(summary = "Envio de email e salvamento de agendamento no banco de dados", description = "Envio de agendamento por email e salvo no banco de dados")
	public Agendamento cadastroEnvioEmail(@Valid @RequestBody Agendamento agendamento) throws MessagingException {
		return agendamentoService.cadastroEnvioEmail(agendamento);
	}

	@PutMapping("/atualizar/{id}")
	@Operation(summary = "Atualiza o agendamento por id", description = "Atualizar agendamento")
	public AgendamentoDTO atualizarEndereco(@PathVariable("id") Integer id,
			@RequestBody Agendamento agendamentoSolicitado) {
		return agendamentoService.atualizarAgendamento(id, agendamentoSolicitado);
	}

	@DeleteMapping("/deletar/{id}")
	@Operation(summary = "Deletar agendamento", description = "Deletar categorias")
	public void deletarAgendamento(@PathVariable("id") Integer id) {
		agendamentoService.excluirAgendamento(id);
	}

}
