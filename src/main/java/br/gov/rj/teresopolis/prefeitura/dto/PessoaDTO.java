package br.gov.rj.teresopolis.prefeitura.dto;

import java.time.LocalDate;
import java.util.UUID;

public class PessoaDTO {
	private UUID pessoaId;
	private String telefone;
	private String email;
	private String nomeRazaoSocial;
	private LocalDate dataRegistro;
	private String cpfCnpj;
	private String identidadeInscricaoMunicipal;
	private boolean preferencial;
	private EnderecoDTO endereco;
	
	
	public UUID getPessoaId() {
		return pessoaId;
	}
	public void setPessoaId(UUID pessoaId) {
		this.pessoaId = pessoaId;
	}
	public String getTelefone() {
		return telefone;
	}
	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getNomeRazaoSocial() {
		return nomeRazaoSocial;
	}
	public void setNomeRazaoSocial(String nomeRazaoSocial) {
		this.nomeRazaoSocial = nomeRazaoSocial;
	}
	public LocalDate getDataRegistro() {
		return dataRegistro;
	}
	public void setDataRegistro(LocalDate dataRegistro) {
		this.dataRegistro = dataRegistro;
	}
	public String getCpfCnpj() {
		return cpfCnpj;
	}
	public void setCpfCnpj(String cpfCnpj) {
		this.cpfCnpj = cpfCnpj;
	}
	public String getIdentidadeInscricaoMunicipal() {
		return identidadeInscricaoMunicipal;
	}
	public void setIdentidadeInscricaoMunicipal(String identidadeInscricaoMunicipal) {
		this.identidadeInscricaoMunicipal = identidadeInscricaoMunicipal;
	}
	public boolean isPreferencial() {
		return preferencial;
	}
	public void setPreferencial(boolean preferencial) {
		this.preferencial = preferencial;
	}
	public EnderecoDTO getEndereco() {
		return endereco;
	}
	public void setEndereco(EnderecoDTO endereco) {
		this.endereco = endereco;
	}
	
	
}
