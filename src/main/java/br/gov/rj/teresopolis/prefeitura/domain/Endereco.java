package br.gov.rj.teresopolis.prefeitura.domain;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name="endereco")
@JsonIdentityInfo(
		generator = ObjectIdGenerators.PropertyGenerator.class,
		property = "enderecoId",
		scope = Endereco.class
	)
public class Endereco {

	@Id
    @GeneratedValue(strategy=GenerationType.UUID)
	@Column(name= "end_cd_id")
	private UUID enderecoId;
	

	
	@Column(name="end_tx_cep")
	@NotBlank(message="O campo CEP é obrigatório.")
	@NotNull(message="O campo CEP é obrigatório.")
	@Pattern(regexp="[0-9]{5}-[0-9]{3}", message="O CEP deve estar no formato 99999-999.")
	private String cep;
	
	@Column(name="end_tx_logradouro")
	@NotBlank(message="O campo logradouro é obrigatório.")
	@NotNull(message="O campo logradouro é obrigatório.")
	private String logradouro;
	
	@Column(name="end_tx_bairro")
	@NotBlank(message="O campo bairro é obrigatório.")
	@NotNull(message="O nome da bairro é obrigatório.")
	private String bairro;
	
	@Column(name="end_tx_cidade")
	@NotBlank(message="O campo localidade é obrigatório.")
	@NotNull(message="O campo localidade é obrigatório.")
	private String localidade;
	
	@Column(name="end_tx_uf")
	@NotBlank(message="O campo uf é obrigatório.")
	@NotNull(message="O campo uf é obrigatório.")
	private String uf;
	

	@NotBlank(message="O campo numero é obrigatório.")
	@NotNull(message="O campo numero é obrigatório.")
	@Column(name= "end_tx_num")
	private String numero;
	
	@Column(name="end_tx_complem")
	private String complemento;
	
	@JsonIgnore
	@OneToMany(mappedBy="endereco")
	private List<Pessoa> pessoas;

	public Endereco() {
		super();
	}

	public Endereco(UUID enderecoId,
			@NotBlank(message = "O campo CEP é obrigatório.") @NotNull(message = "O campo CEP é obrigatório.") @Pattern(regexp = "[0-9]{5}-[0-9]{3}", message = "O CEP deve estar no formato 99999-999.") String cep,
			@NotBlank(message = "O campo logradouro é obrigatório.") @NotNull(message = "O campo logradouro é obrigatório.") String logradouro,
			@NotBlank(message = "O campo bairro é obrigatório.") @NotNull(message = "O nome da bairro é obrigatório.") String bairro,
			@NotBlank(message = "O campo localidade é obrigatório.") @NotNull(message = "O campo localidade é obrigatório.") String localidade,
			@NotBlank(message = "O campo uf é obrigatório.") @NotNull(message = "O campo uf é obrigatório.") String uf,
			@NotBlank(message = "O campo numero é obrigatório.") @NotNull(message = "O campo numero é obrigatório.") String numero,
			String complemento, List<Pessoa> pessoas) {
		super();
		this.enderecoId = enderecoId;
		this.cep = cep;
		this.logradouro = logradouro;
		this.bairro = bairro;
		this.localidade = localidade;
		this.uf = uf;
		this.numero = numero;
		this.complemento = complemento;
		this.pessoas = pessoas;
	}

	public UUID getEnderecoId() {
		return enderecoId;
	}

	public void setEnderecoId(UUID enderecoId) {
		this.enderecoId = enderecoId;
	}

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public String getLogradouro() {
		return logradouro;
	}

	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public String getLocalidade() {
		return localidade;
	}

	public void setLocalidade(String localidade) {
		this.localidade = localidade;
	}

	public String getUf() {
		return uf;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getComplemento() {
		return complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	public List<Pessoa> getPessoas() {
		return pessoas;
	}

	public void setPessoas(List<Pessoa> pessoas) {
		this.pessoas = pessoas;
	}


	
}