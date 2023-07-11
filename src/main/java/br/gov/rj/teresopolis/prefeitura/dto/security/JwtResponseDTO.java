package br.gov.rj.teresopolis.prefeitura.dto.security;

import java.util.List;

import br.gov.rj.teresopolis.prefeitura.dto.OrgaoDTO;

public class JwtResponseDTO {
	private String token;
	private String type = "Bearer";
	/*
	 * private Integer id; private String username; private String email; private
	 * List<String> roles; private OrgaoDTO orgao;
	 */

	/*
	 * public JwtResponseDTO(String accessToken, OrgaoDTO orgao, Integer id, String
	 * username, String email, List<String> roles) { this.token = accessToken;
	 * this.id = id; this.username = username; this.email = email; this.roles =
	 * roles; this.orgao = orgao; }
	 */
	
	public JwtResponseDTO(String accessToken) {
		this.token = accessToken;
	}



	public String getAccessToken() {
		return token;
	}

	public void setAccessToken(String accessToken) {
		this.token = accessToken;
	}

	public String getTokenType() {
		return type;
	}

	public void setTokenType(String tokenType) {
		this.type = tokenType;
	}

	/*
	 * public Integer getId() { return id; }
	 * 
	 * public void setId(Integer id) { this.id = id; }
	 * 
	 * public String getEmail() { return email; }
	 * 
	 * public void setEmail(String email) { this.email = email; }
	 * 
	 * public String getUsername() { return username; }
	 * 
	 * public void setUsername(String username) { this.username = username; }
	 * 
	 * public List<String> getRoles() { return roles; }
	 * 
	 * public OrgaoDTO getOrgao() { return orgao; }
	 * 
	 * public void setOrgao(OrgaoDTO orgao) { this.orgao = orgao; }
	 */

}