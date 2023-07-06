package br.gov.rj.teresopolis.prefeitura.domain;

import java.sql.Types;

import org.hibernate.annotations.JdbcTypeCode;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "anexo")
@JsonIdentityInfo(
		generator = ObjectIdGenerators.PropertyGenerator.class,
		property = "anexoId",
		scope = Anexo.class
	)
public class Anexo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "anex_cd_id")
	private Integer anexoId;

	@Lob
	@JdbcTypeCode(Types.BINARY)
	@Column(name = "anex_tx_dados")
	private byte[] dados;

	@Column(name = "anex_tx_tipo")
	private String tipo;

	@Column(name = "anex_tx_nome")
	private String nome;

	@ManyToOne
	@JoinColumn(name = "fk_agendamento_id", referencedColumnName = "age_cd_id")
	private Agendamento agendamento;

	
	public Anexo() {
	}

	public Anexo(byte[] dados, String tipo, String nome) {
		super();
		this.dados = dados;
		this.tipo = tipo;
		this.nome = nome;
	}

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

	public Agendamento getAgendamento() {
		return agendamento;
	}

	public void setAgendamento(Agendamento agendamento) {
		this.agendamento = agendamento;
	}

}