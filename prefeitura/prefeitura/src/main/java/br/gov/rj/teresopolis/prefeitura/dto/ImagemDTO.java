package br.gov.rj.teresopolis.prefeitura.dto;

public class ImagemDTO {
	private Integer imagemId;
	private byte[] dados;
	private String tipo;
	private String nome;
	
	public Integer getImagemId() {
		return imagemId;
	}

	public void setImagemId(Integer imagemId) {
		this.imagemId = imagemId;
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

}
