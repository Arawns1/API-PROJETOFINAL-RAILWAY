package br.gov.rj.teresopolis.prefeitura.dto.security;

import java.util.Set;

import br.gov.rj.teresopolis.prefeitura.dto.OrgaoDTO;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SignupRequestDTO {
	@NotBlank
	@Size(min = 3, max = 20, message = "Tamanho incorreto. O valor deve estar entre 3 e 20 carac.")
	private String username;

	@NotBlank
	@Size(max = 50)
	@Email
	private String email;

	private Set<String> role;
	
	private OrgaoDTO orgao;

	@NotBlank
	@Size(min = 8, max = 120)
	private String password;

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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Set<String> getRole() {
		return this.role;
	}

	public void setRole(Set<String> role) {
		this.role = role;
	}

	public OrgaoDTO getOrgao() {
		return orgao;
	}

	public void setOrgao(OrgaoDTO orgao) {
		this.orgao = orgao;
	}
}