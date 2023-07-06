package br.gov.rj.teresopolis.prefeitura.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import br.gov.rj.teresopolis.prefeitura.domain.Imagem;
import br.gov.rj.teresopolis.prefeitura.domain.Servico;
import br.gov.rj.teresopolis.prefeitura.domain.TipoServicoEnum;
import br.gov.rj.teresopolis.prefeitura.dto.ImagemDTO;
import br.gov.rj.teresopolis.prefeitura.dto.ServicoDTO;
import br.gov.rj.teresopolis.prefeitura.exceptions.InvalidServiceException;
import br.gov.rj.teresopolis.prefeitura.exceptions.NoSuchElementException;
import br.gov.rj.teresopolis.prefeitura.repositories.ImagemRepository;
import br.gov.rj.teresopolis.prefeitura.repositories.ServicoRepository;

@Service
public class ServicoService {
	@Autowired
	ServicoRepository servicoRepository;
	
	@Autowired
	ImagemRepository imagemRepository;

	ModelMapper modelMapper = new ModelMapper();

	public ServicoService(ServicoRepository servicoRepository) {
		this.servicoRepository = servicoRepository;
	}

	public List<ServicoDTO> listarServicos() {
		List<ServicoDTO> servicosDTO = new ArrayList<>();
		for (Servico servico : servicoRepository.findAll()) {
			ServicoDTO servicoDTO = modelMapper.map(servico, ServicoDTO.class);
			
			Optional<Imagem> imagem = imagemRepository.findById(servico.getImagem().getImagemId());
			
			if(imagem.isPresent()) {
				ImagemDTO imagemDTO = modelMapper.map(imagem.get(), ImagemDTO.class);
				servicoDTO.setImagemDTO(imagemDTO);
			}
			
			servicosDTO.add(servicoDTO);
		}
		return servicosDTO;
	}

	public ServicoDTO obterServicoPorId(Integer id) {
		Servico servico = servicoRepository.findById(id)
				.orElseThrow(() -> new NoSuchElementException("Não foi encontrada a servico de id= " + id));
		
		ServicoDTO servicoDTO = modelMapper.map(servico, ServicoDTO.class);
		Optional<Imagem> imagem = imagemRepository.findById(servico.getImagem().getImagemId());
		
		if(imagem.isPresent()) {
			ImagemDTO imagemDTO = modelMapper.map(imagem.get(), ImagemDTO.class);
			servicoDTO.setImagemDTO(imagemDTO);
			System.out.println("imagemDTO: " + imagemDTO);
		}
		
		return servicoDTO;
	}

	public ServicoDTO criarServico(MultipartFile file, String servicoJson) {
		
		String tipoImagem = file.getContentType();
		byte[] dados = null;
		try {
			dados = file.getBytes();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String nomeImagem = file.getOriginalFilename();
		Imagem imagem = new Imagem(dados, tipoImagem, nomeImagem);
		Servico servico = convertServicoFromStringJson(servicoJson);
		
		boolean tipoServicoValido = false;

		try {
			for (TipoServicoEnum tipo : TipoServicoEnum.values()) {
				if (TipoServicoEnum.valueOf(servico.getTipoServico()) == tipo) {
					tipoServicoValido = true;
					break;
				}
			}
		}
		catch (Exception e) {
			 throw new InvalidServiceException("Erro: Tipo de Servico inválido.");
		}

	    if (!tipoServicoValido) {
	        throw new InvalidServiceException("Erro: Tipo de Servico inválido.");
	    }
		
		Imagem imagemSalva = imagemRepository.save(imagem);
		servico.setImagem(imagemSalva);
		Servico servicoSalvo = servicoRepository.save(servico);
		
		ImagemDTO imagemDTO = modelMapper.map(imagemSalva,ImagemDTO.class);
		ServicoDTO servicoDTO = modelMapper.map(servicoSalvo, ServicoDTO.class);
		servicoDTO.setImagemDTO(imagemDTO);
		return servicoDTO;
	}
	
	
	public ServicoDTO atualizarServico(Integer id, Servico servico) {
		Optional<Servico> servicoExistenteOptional = servicoRepository.findById(id);
		if (servicoExistenteOptional.isPresent()) {
			Servico servicoExistente = servicoExistenteOptional.get();
			modelMapper.map(servico, servicoExistente);
			servicoExistente.setServicoId(id);
			Servico servicoSalvo = servicoRepository.save(servicoExistente);
			return modelMapper.map(servicoSalvo, ServicoDTO.class);
		} else {
			throw new NoSuchElementException("Não foi encontrado o servico de id = " + id + " para ser atualizado");
		}
	}

	public void deletarServico(int id) {
		servicoRepository.deleteById(id);
	}


	
	
	private Servico convertServicoFromStringJson(String servicoJson) {
		Servico servico = new Servico();

		try {
			ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
					false);

			objectMapper.registerModule(new JavaTimeModule());
			servico = objectMapper.readValue(servicoJson, Servico.class);
		} catch (IOException err) {
			System.out.printf("Ocorreu um erro ao tentar converter a string json para um instância de Servico",
					err.toString());
		}

		return servico;
	}
	
}
