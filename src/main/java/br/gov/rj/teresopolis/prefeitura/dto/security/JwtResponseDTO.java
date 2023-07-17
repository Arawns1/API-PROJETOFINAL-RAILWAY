package br.gov.rj.teresopolis.prefeitura.dto.security;

public class JwtResponseDTO {
	private String token;
	private String type = "Bearer";
	
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

}