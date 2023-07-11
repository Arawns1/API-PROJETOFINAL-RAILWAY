package br.gov.rj.teresopolis.prefeitura.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.gov.rj.teresopolis.prefeitura.domain.Pessoa;
import br.gov.rj.teresopolis.prefeitura.dto.EnderecoDTO;
import br.gov.rj.teresopolis.prefeitura.dto.PessoaDTO;
import br.gov.rj.teresopolis.prefeitura.exceptions.NoSuchElementException;
import br.gov.rj.teresopolis.prefeitura.repositories.PessoaRepository;

@Service
public class PessoaService {
	@Autowired
	PessoaRepository pessoaRepository;

	ModelMapper modelMapper = new ModelMapper();

	public PessoaService(PessoaRepository pessoaRepository) {
		this.pessoaRepository = pessoaRepository;
	}

	public List<PessoaDTO> listarPessoas() {
		List<PessoaDTO> pessoasDTO = new ArrayList<>();
		for (Pessoa pessoa : pessoaRepository.findAll()) {
			PessoaDTO pessoaDTO = modelMapper.map(pessoa, PessoaDTO.class);
			pessoasDTO.add(pessoaDTO);
		}

		return pessoasDTO;
	}

	public PessoaDTO obterPessoaPorId(UUID id) {
		Pessoa pessoa = pessoaRepository.findById(id)
				.orElseThrow(() -> new NoSuchElementException("Não foi encontrada a pessoa de id= " + id));
		PessoaDTO pessoaDTO = modelMapper.map(pessoa, PessoaDTO.class);
		return pessoaDTO;
	}

	public PessoaDTO criarPessoa(Pessoa pessoa) {
		Pessoa pessoaSalvo = pessoaRepository.save(pessoa);
		EnderecoDTO enderecoDTO = modelMapper.map(pessoaSalvo.getEndereco(), EnderecoDTO.class);
		PessoaDTO pessoaDto = modelMapper.map(pessoaSalvo, PessoaDTO.class);
		pessoaDto.setEndereco(enderecoDTO);
		return pessoaDto;
	}

	public PessoaDTO atualizarPessoa(UUID id, Pessoa pessoa) {
		Optional<Pessoa> pessoaExistenteOptional = pessoaRepository.findById(id);
		if (pessoaExistenteOptional.isPresent()) {
			Pessoa pessoaExistente = pessoaExistenteOptional.get();
			modelMapper.map(pessoa, pessoaExistente);
			pessoaExistente.setPessoaId(id);
			Pessoa pessoaSalvo = pessoaRepository.save(pessoaExistente);
			return modelMapper.map(pessoaSalvo, PessoaDTO.class);
		} else {
			throw new NoSuchElementException("Não foi encontrado o pessoa de id = " + id + " para ser atualizado");
		}
	}


	public void deletarPessoa(UUID id) {
		pessoaRepository.deleteById(id);
	}
}
