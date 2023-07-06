package br.gov.rj.teresopolis.prefeitura.domain;

import java.sql.Types;
import java.util.Arrays;

import org.hibernate.annotations.JdbcTypeCode;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "imagem")
@JsonIdentityInfo(
		generator = ObjectIdGenerators.PropertyGenerator.class,
		property = "imagemId",
		scope = Imagem.class
	)
public class Imagem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "img_cd_id")
	private Integer imagemId;

	@Lob
	@JdbcTypeCode(Types.BINARY)
	@Column(name = "img_tx_dados")
	private byte[] dados;

	@Column(name = "img_tx_tipo")
	private String tipo;

	@Column(name = "img_tx_nome")
	private String nome;

	@OneToOne(mappedBy="imagem", cascade=CascadeType.REMOVE)
	private Servico servico;

	public Imagem(byte[] dados, String tipo, String nome) {
		super();
		this.dados = dados;
		this.tipo = tipo;
		this.nome = nome;
	}

	public Imagem() {
		super();
	}

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

	public Servico getServico() {
		return servico;
	}

	public void setServico(Servico servico) {
		this.servico = servico;
	}

	@Override
	public String toString() {
		return "Imagem [imagemId=" + imagemId + ", dados=" + Arrays.toString(dados) + ", tipo=" + tipo + ", nome="
				+ nome + "]";
	}
}