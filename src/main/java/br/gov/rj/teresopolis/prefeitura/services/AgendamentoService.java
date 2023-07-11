package br.gov.rj.teresopolis.prefeitura.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.gov.rj.teresopolis.prefeitura.domain.Agendamento;
import br.gov.rj.teresopolis.prefeitura.dto.AgendamentoDTO;
import br.gov.rj.teresopolis.prefeitura.exceptions.NoSuchElementException;
import br.gov.rj.teresopolis.prefeitura.repositories.AgendamentoRepository;
import jakarta.mail.MessagingException;

@Service
public class AgendamentoService {
	@Autowired
	AgendamentoRepository agendamentoRepository;
	
	@Autowired
	MailService mailService;
	
	ModelMapper modelMapper = new ModelMapper();

	public AgendamentoService(AgendamentoRepository agendamentoRepository) {
		this.agendamentoRepository = agendamentoRepository;
	}
	
	public List<Agendamento> listarAgendamentos() {
		return agendamentoRepository.findAll();
	}

	public Agendamento obterAgendamentoPorId(UUID id) {
		Agendamento agendamento = agendamentoRepository.findById(id)
				.orElseThrow(() -> new NoSuchElementException("Não foi encontrado o agendamento de id= " + id));
		return agendamento;
	}

	public AgendamentoDTO criarAgendamento(Agendamento agendamento) {
		agendamento.setHoraAgendamento(LocalDateTime.now());
		Agendamento agendamentoSalvo = agendamentoRepository.save(agendamento);
		return modelMapper.map(agendamentoSalvo, AgendamentoDTO.class);
	}

	public AgendamentoDTO atualizarAgendamento(UUID id, Agendamento agendamento) {
		Optional<Agendamento> agendamentoExistenteOptional = agendamentoRepository.findById(id);
		if (agendamentoExistenteOptional.isPresent()) {
			Agendamento agendamentoExistente = agendamentoExistenteOptional.get();
			modelMapper.map(agendamento, agendamentoExistente);
			agendamentoExistente.setAgendamentoId(id);
			Agendamento agendamentoSalvo = agendamentoRepository.save(agendamentoExistente);
			return modelMapper.map(agendamentoSalvo, AgendamentoDTO.class);
		} else {
			throw new NoSuchElementException("Não foi encontrado o agendamento de id = " + id + " para ser atualizado");
		}
	}
	
	public Agendamento cadastroEnvioEmail(Agendamento agendamento) {
		agendamento.setHoraAgendamento(LocalDateTime.now());
		Agendamento agendamentoSalvo = agendamentoRepository.save(agendamento);
		try {
			System.out.println("Agendamento" + agendamentoSalvo);
			mailService.enviarCalendario(agendamentoSalvo);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		
		return agendamentoSalvo;
	}

	public void excluirAgendamento(UUID id) {
		agendamentoRepository.deleteById(id);
	}
}
