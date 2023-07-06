package br.gov.rj.teresopolis.prefeitura.dto;

public class AnexoURLDTO {
	
	private String fileName;
	private Integer agendamentoId;
	private String url_arquivo;
	
	public AnexoURLDTO() {
		super();
	}
	
	public AnexoURLDTO(String fileName, Integer agendamentoId, String url_arquivo) {
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
	public Integer getAgendamentoId() {
		return agendamentoId;
	}
	public void setAgendamentoId(Integer agendamentoId) {
		this.agendamentoId = agendamentoId;
	}
	public String getUrl_arquivo() {
		return url_arquivo;
	}
	public void setUrl_arquivo(String url_arquivo) {
		this.url_arquivo = url_arquivo;
	}
	
	
}
