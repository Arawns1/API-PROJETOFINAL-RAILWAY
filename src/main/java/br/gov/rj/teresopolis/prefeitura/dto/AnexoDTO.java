package br.gov.rj.teresopolis.prefeitura.dto;

import java.util.UUID;

public class AnexoDTO {
	private UUID anexoId;
	private byte[] dados;
	private String tipo;
	private String nome;
	private UUID agendamentoId;
	
	public AnexoDTO(UUID anexoId, byte[] dados, String tipo, String nome, UUID agendamentoId) {
		super();
		this.anexoId = anexoId;
		this.dados = dados;
		this.tipo = tipo;
		this.nome = nome;
		this.agendamentoId = agendamentoId;
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
	public UUID getAnexoId() {
		return anexoId;
	}
	public void setAnexoId(UUID anexoId) {
		this.anexoId = anexoId;
	}
	public UUID getAgendamentoId() {
		return agendamentoId;
	}
	public void setAgendamentoId(UUID agendamentoId) {
		this.agendamentoId = agendamentoId;
	}
	
}
