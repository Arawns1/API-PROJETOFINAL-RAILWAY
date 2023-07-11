package br.gov.rj.teresopolis.prefeitura.controllers;

import java.io.IOException;
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

import br.gov.rj.teresopolis.prefeitura.domain.Imagem;
import br.gov.rj.teresopolis.prefeitura.dto.ImagemDTO;
import br.gov.rj.teresopolis.prefeitura.services.ImagemService;
import br.gov.rj.teresopolis.prefeitura.services.ServicoService;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/imagem")
public class ImagemController {

	@Autowired
	ImagemService imagemService;

	@Autowired
	ServicoService servicoService;

	@GetMapping("/lista")
	@Operation(summary = "Lista todos os imagens - ADM", description = "Listagem de imagens")
	public List<ImagemDTO> findAllId() {
		return imagemService.listarTodasImagens();
	}

	@GetMapping("/lista/{id}")
	@Operation(summary = "Encontra imagem por id", description = "Encontrar imagem por id")
	public ImagemDTO findId(@PathVariable("id") UUID id) {
		return imagemService.obterImagemPorId(id);
	}

	@Operation(summary = "Salva as imagens no banco de dados", description = "Ao passar os imagens e o json do servico a imagem é salvo")
	@PostMapping(path = "/inserir", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE})

	public Imagem uploadImagem(@RequestPart("imagem") MultipartFile file) {

		String tipoImagem = file.getContentType();
		byte[] dados = null;
		try {
			dados = file.getBytes();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String nomeImagem = file.getOriginalFilename();
		Imagem imagem = new Imagem(dados, tipoImagem, nomeImagem);

		return imagemService.saveImagem(imagem);
	}

	@DeleteMapping("/deletar/{id}")
	@Operation(summary = "Deletar imagem", description = "Deletar categorias")
	public void deletarImagem(@PathVariable("id") UUID id) {
		imagemService.excluirImagem(id);
	}

}
