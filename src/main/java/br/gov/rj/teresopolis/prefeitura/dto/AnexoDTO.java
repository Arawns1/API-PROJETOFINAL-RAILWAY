package br.gov.rj.teresopolis.prefeitura.dto;

public class AnexoDTO {
	private Integer anexoId;
	private byte[] dados;
	private String tipo;
	private String nome;
	private Integer agendamentoId;
	
	public Integer getAnexoId() {
		return anexoId;
	}
	public void setAnexoId(Integer anexoId) {
		this.anexoId = anexoId;
	}
	public byte[] getDados() {
		return dados;
	}
	public void setDados(byte[] dados) {
		this.dados = dados;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public Integer getAgendamentoId() {
		return agendamentoId;
	}
	public void setAgendamentoId(Integer agendamentoId) {
		this.agendamentoId = agendamentoId;
	}
	
}
