package br.gov.rj.teresopolis.prefeitura.services;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.gov.rj.teresopolis.prefeitura.domain.Orgao;
import br.gov.rj.teresopolis.prefeitura.dto.OrgaoDTO;
import br.gov.rj.teresopolis.prefeitura.exceptions.NoSuchElementException;
import br.gov.rj.teresopolis.prefeitura.repositories.OrgaoRepository;

@Service
public class OrgaoService {
	@Autowired
	OrgaoRepository orgaoRepository;

	ModelMapper modelMapper = new ModelMapper();

	public OrgaoService(OrgaoRepository orgaoRepository) {
		this.orgaoRepository = orgaoRepository;
	}

	public List<Orgao> listarOrgaos() {
		return orgaoRepository.findAll();
	}

	public OrgaoDTO obterOrgaoPorId(int id) {
		Orgao orgao = orgaoRepository.findById(id)
				.orElseThrow(() -> new NoSuchElementException("Não foi encontrado o orgão de id= " + id));
		OrgaoDTO orgaoDTO = modelMapper.map(orgao, OrgaoDTO.class);
		return orgaoDTO;
	}

	public OrgaoDTO criarOrgao(Orgao orgao) {
		Orgao orgaoSalvo = orgaoRepository.save(orgao);
		return modelMapper.map(orgaoSalvo, OrgaoDTO.class);
	}

	public OrgaoDTO atualizarOrgao(Integer id, Orgao orgao) {
		Optional<Orgao> orgaoExistenteOptional = orgaoRepository.findById(id);
		if (orgaoExistenteOptional.isPresent()) {
			Orgao orgaoExistente = orgaoExistenteOptional.get();
			modelMapper.map(orgao, orgaoExistente);
			orgaoExistente.setOrgaoId(id);
			Orgao orgaoSalvo = orgaoRepository.save(orgaoExistente);
			return modelMapper.map(orgaoSalvo, OrgaoDTO.class);
		} else {
			throw new NoSuchElementException("Não foi encontrado o orgao de id = " + id + " para ser atualizado");
		}
	}

	public void excluirOrgao(int id) {
		orgaoRepository.deleteById(id);
	}
}
