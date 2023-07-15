package br.gov.rj.teresopolis.prefeitura.dto;

import java.util.UUID;

public class ServicoDTO {
	private UUID servicoId;
	private String nome;
	private String tipoServico;
	private ImagemDTO imagemDTO;
	private boolean status;

	public UUID getServicoId() {
		return servicoId;
	}

	public void setServicoId(UUID servicoId) {
		this.servicoId = servicoId;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getTipoServico() {
		return tipoServico;
	}

	public void setTipoServico(String tipoServico) {
		this.tipoServico = tipoServico;
	}

	public ImagemDTO getImagemDTO() {
		return imagemDTO;
	}

	public void setImagemDTO(ImagemDTO imagemDTO) {
		this.imagemDTO = imagemDTO;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}
}
