package br.gov.rj.teresopolis.prefeitura.dto;

import java.util.UUID;

public class OrgaoDTO {
	private UUID orgaoId;
	private String nome;
	
	public UUID getOrgaoId() {
		return orgaoId;
	}
	public void setOrgaoId(UUID orgaoId) {
		this.orgaoId = orgaoId;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	@Override
	public String toString() {
		return "orgaoId=" + orgaoId + ", nome=" + nome;
	}
	
	
}
