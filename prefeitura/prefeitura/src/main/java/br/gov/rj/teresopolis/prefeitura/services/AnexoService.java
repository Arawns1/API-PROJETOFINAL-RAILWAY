package br.gov.rj.teresopolis.prefeitura.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import br.gov.rj.teresopolis.prefeitura.domain.Agendamento;
import br.gov.rj.teresopolis.prefeitura.domain.Anexo;
import br.gov.rj.teresopolis.prefeitura.dto.AnexoDTO;
import br.gov.rj.teresopolis.prefeitura.exceptions.AgendamentoNotFoundException;
import br.gov.rj.teresopolis.prefeitura.exceptions.NoSuchElementException;
import br.gov.rj.teresopolis.prefeitura.repositories.AgendamentoRepository;
import br.gov.rj.teresopolis.prefeitura.repositories.AnexoRepository;

@Service
public class AnexoService {
	@Autowired
	AnexoRepository anexoRepository;

	@Autowired
	AgendamentoRepository agendamentoRepository;

	ModelMapper modelMapper = new ModelMapper();

	public AnexoService(AnexoRepository anexoRepository) {
		this.anexoRepository = anexoRepository;
	}

	public List<AnexoDTO> listarTodosAnexos() {
		List<AnexoDTO> anexosDtoList = new ArrayList<>();

		for (Anexo anexo : anexoRepository.findAll()) {
			AnexoDTO anexoDto = modelMapper.map(anexo, AnexoDTO.class);
			anexoDto.setAgendamentoId(anexo.getAgendamento().getAgendamentoId());
			anexosDtoList.add(anexoDto);
		}
		return anexosDtoList;
	}

	public String saveAllAnexosList(List<Anexo> anexoList, String agendamento) {
		Agendamento agendamentoConverted = convertAgendamentoFromStringJson(agendamento);
		Agendamento agendamentoSalvo = null;
		if (agendamentoRepository.findById(agendamentoConverted.getAgendamentoId()) != null) {
			agendamentoSalvo = agendamentoRepository.findById(agendamentoConverted.getAgendamentoId()).get();
		} else {
			throw new AgendamentoNotFoundException(agendamentoConverted.getAgendamentoId());
		}
		for (Anexo anexo : anexoList) {
			anexo.setAgendamento(agendamentoSalvo);
			anexoRepository.save(anexo);
		}
		return "Anexos salvos com sucesso!";
	}

	public AnexoDTO obterAnexoPorId(int id) {
		Optional<Anexo> anexoEncontrado = anexoRepository.findById(id);
		if (anexoEncontrado.isPresent()) {
			AnexoDTO anexoDto = modelMapper.map(anexoEncontrado.get(), AnexoDTO.class);
			anexoDto.setAgendamentoId(anexoEncontrado.get().getAgendamento().getAgendamentoId());
			return anexoDto;
		} else {
			throw new NoSuchElementException("Não foi encontrado um Anexo com o id = " + id);
		}
	}

	public void excluirAnexo(int id) {
		anexoRepository.deleteById(id);
	}
	
	public List<AnexoDTO> obterAnexosByAgendamento(Integer id){
		List<Anexo> anexos = anexoRepository.findAnexosByAgendamento(id);
		
		List<AnexoDTO> anexosDtoList = new ArrayList<>();

		for (Anexo anexo : anexos) {
			AnexoDTO anexoDto = modelMapper.map(anexo, AnexoDTO.class);
			anexoDto.setAgendamentoId(anexo.getAgendamento().getAgendamentoId());
			anexosDtoList.add(anexoDto);
		}
		return anexosDtoList;
	}
	
	
	
	
	private Agendamento convertAgendamentoFromStringJson(String agendamentoJson) {
		Agendamento agendamento = new Agendamento();

		try {
			ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
					false);

			objectMapper.registerModule(new JavaTimeModule());
			agendamento = objectMapper.readValue(agendamentoJson, Agendamento.class);
		} catch (IOException err) {
			System.out.printf("Ocorreu um erro ao tentar converter a string json para um instância de Agendamento",
					err.toString());
		}

		return agendamento;
	}
}
