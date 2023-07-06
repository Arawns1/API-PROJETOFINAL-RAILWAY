package br.gov.rj.teresopolis.prefeitura.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import br.gov.rj.teresopolis.prefeitura.domain.Imagem;
import br.gov.rj.teresopolis.prefeitura.dto.ImagemDTO;
import br.gov.rj.teresopolis.prefeitura.exceptions.NoSuchElementException;
import br.gov.rj.teresopolis.prefeitura.repositories.AgendamentoRepository;
import br.gov.rj.teresopolis.prefeitura.repositories.ImagemRepository;

@Service
public class ImagemService {
	@Autowired
	ImagemRepository imagemRepository;

	@Autowired
	AgendamentoRepository agendamentoRepository;

	ModelMapper modelMapper = new ModelMapper();

	public ImagemService(ImagemRepository imagemRepository) {
		this.imagemRepository = imagemRepository;
	}

	public List<ImagemDTO> listarTodasImagens() {
		List<ImagemDTO> imagemsDtoList = new ArrayList<>();
		for (Imagem imagem : imagemRepository.findAll()) {
			ImagemDTO imagemDto = modelMapper.map(imagem, ImagemDTO.class);
			imagemsDtoList.add(imagemDto);
		}
		return imagemsDtoList;
	}

	public Imagem saveImagem(Imagem imagem) {
		return imagemRepository.save(imagem);
	}
	
	public ImagemDTO obterImagemPorId(int id) {
		Optional<Imagem> imagemEncontrado = imagemRepository.findById(id);
		if (imagemEncontrado.isPresent()) {
			ImagemDTO imagemDto = modelMapper.map(imagemEncontrado.get(), ImagemDTO.class);
			return imagemDto;
		} else {
			throw new NoSuchElementException("Não foi encontrado um Imagem com o id = " + id);
		}
	}

	public void excluirImagem(int id) {
		imagemRepository.deleteById(id);
	}
	
}
