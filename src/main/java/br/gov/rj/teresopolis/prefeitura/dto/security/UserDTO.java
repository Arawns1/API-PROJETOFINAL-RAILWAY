package br.gov.rj.teresopolis.prefeitura.dto.security;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import br.gov.rj.teresopolis.prefeitura.dto.OrgaoDTO;

public class UserDTO {
	private UUID id;
	private String username;
	private String email;
	private Set<String> roles = new HashSet<>();
	private OrgaoDTO orgao;
	
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Set<String> getRoles() {
		return roles;
	}
	public void setRoles(Set<String> roles) {
		this.roles = roles;
	}
	public OrgaoDTO getOrgao() {
		return orgao;
	}
	public void setOrgao(OrgaoDTO orgao) {
		this.orgao = orgao;
	}
}