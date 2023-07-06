package br.gov.rj.teresopolis.prefeitura.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.UnknownHttpStatusCodeException;

import br.gov.rj.teresopolis.prefeitura.domain.Endereco;
import br.gov.rj.teresopolis.prefeitura.dto.EnderecoDTO;
import br.gov.rj.teresopolis.prefeitura.dto.RequestEnderecoViaCepDTO;
import br.gov.rj.teresopolis.prefeitura.dto.ViaCepDTO;
import br.gov.rj.teresopolis.prefeitura.exceptions.CEPNotFoundException;
import br.gov.rj.teresopolis.prefeitura.exceptions.EnderecoNotFoundException;
import br.gov.rj.teresopolis.prefeitura.exceptions.NoSuchElementException;
import br.gov.rj.teresopolis.prefeitura.repositories.EnderecoRepository;
import jakarta.validation.Valid;

@Service
public class EnderecoService {

	ModelMapper modelMapper = new ModelMapper();

	@Autowired
	EnderecoRepository enderecoRepository;

	public EnderecoService(EnderecoRepository enderecoRepository) {
		this.enderecoRepository = enderecoRepository;
	}

	public EnderecoDTO criarEndereco(Endereco endereco) {
		Endereco enderecoSalvo = enderecoRepository.save(endereco);
		return modelMapper.map(enderecoSalvo, EnderecoDTO.class);
	}
	

	public EnderecoDTO atualizarEndereco(Integer id, Endereco endereco) {
		Optional<Endereco> enderecoExistenteOptional = enderecoRepository.findById(id);
		if (enderecoExistenteOptional.isPresent()) {
			Endereco enderecoExistente = enderecoExistenteOptional.get();
			modelMapper.map(endereco, enderecoExistente);
			enderecoExistente.setEnderecoId(id);
			Endereco enderecoSalvo = enderecoRepository.save(enderecoExistente);
			return modelMapper.map(enderecoSalvo, EnderecoDTO.class);
		} else {
			throw new NoSuchElementException("Não foi encontrado o endereco de id = " + id + " para ser atualizado");
		}
	}

	public EnderecoDTO obterEnderecoPorId(int id) {
		Endereco endereco = enderecoRepository.findById(id)
				.orElseThrow(() -> new EnderecoNotFoundException(id));
		EnderecoDTO enderecoDTO = modelMapper.map(endereco, EnderecoDTO.class);
		return enderecoDTO;
	}

	public List<EnderecoDTO> listarEnderecos() {
		List<EnderecoDTO> enderecosDTO = new ArrayList<>();
		for (Endereco endereco : enderecoRepository.findAll()) {
			EnderecoDTO enderecoDTO = modelMapper.map(endereco, EnderecoDTO.class);
			enderecosDTO.add(enderecoDTO);
		}

		return enderecosDTO;
	}

	public void deletarEndereco(Integer id) {
		Optional<Endereco> enderecoExistenteOptional = enderecoRepository.findById(id);
		if (enderecoExistenteOptional.isPresent()) {
			Endereco enderecoExistente = enderecoExistenteOptional.get();
			enderecoRepository.delete(enderecoExistente);
		} else {
			throw new IllegalArgumentException("Endereço não existente");
		}
	}

	
	public EnderecoDTO criarEnderecoViaCep(@Valid RequestEnderecoViaCepDTO endereco) {
		return buscaCep(endereco);
	}
	
	private EnderecoDTO buscaCep(RequestEnderecoViaCepDTO requestEnderecoViaCepDTO) {
		RestTemplate restTemplate = new RestTemplate();
		String uri = "http://viacep.com.br/ws/{cep}/json";
		Map<String, String> params = new HashMap<>();
		params.put("cep", requestEnderecoViaCepDTO.getCep());
		
		Endereco endereco = new Endereco();
		try {
			ViaCepDTO responseViaCep = restTemplate.getForObject(uri, ViaCepDTO.class, params);
			if (responseViaCep == null || responseViaCep.getErro()) {
				throw new CEPNotFoundException(requestEnderecoViaCepDTO.getCep());
			}
			endereco.setCep(responseViaCep.getCep());
			endereco.setLogradouro(responseViaCep.getLogradouro());
			endereco.setBairro(responseViaCep.getBairro());
			endereco.setLocalidade(responseViaCep.getLocalidade());
			endereco.setUf(responseViaCep.getUf());
			endereco.setComplemento(requestEnderecoViaCepDTO.getComplemento());
			endereco.setNumero(requestEnderecoViaCepDTO.getNumero());
			enderecoRepository.save(endereco);
			
		} catch (HttpClientErrorException | HttpServerErrorException | UnknownHttpStatusCodeException e) {
			throw new CEPNotFoundException(requestEnderecoViaCepDTO.getCep());
		}
		
		return modelMapper.map(endereco, EnderecoDTO.class);
  }
	
}
