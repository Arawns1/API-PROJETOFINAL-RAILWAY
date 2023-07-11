package br.gov.rj.teresopolis.prefeitura.dto;

import java.util.UUID;

public class AnexoURLDTO {
	
	private String fileName;
	private UUID agendamentoId;
	private String url_arquivo;
	
	public AnexoURLDTO() {
		super();
	}
	
	public AnexoURLDTO(String fileName, UUID agendamentoId, String url_arquivo) {
		super();
		this.fileName = fileName;
		this.agendamentoId = agendamentoId;
		this.url_arquivo = url_arquivo;
	}

	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public UUID getAgendamentoId() {
		return agendamentoId;
	}

	public void setAgendamentoId(UUID agendamentoId) {
		this.agendamentoId = agendamentoId;
	}

	public String getUrl_arquivo() {
		return url_arquivo;
	}
	public void setUrl_arquivo(String url_arquivo) {
		this.url_arquivo = url_arquivo;
	}
	
	
}
