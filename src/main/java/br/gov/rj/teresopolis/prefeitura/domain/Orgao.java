package br.gov.rj.teresopolis.prefeitura.domain;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import br.gov.rj.teresopolis.prefeitura.domain.security.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "orgao")
/*
 * @JsonIdentityInfo( generator = ObjectIdGenerators.PropertyGenerator.class,
 * property = "orgaoId", scope = Orgao.class )
 */
public class Orgao {
	
	@Id
    @GeneratedValue(strategy = GenerationType.UUID)
	@Column(name= "org_cd_id")
    private UUID orgaoId;

	@NotBlank(message="O orgão não pode ser nulo")
	@Column(name= "org_tx_nome")
    private String nome;
	
	@NotBlank(message="O email não pode ser nulo")
	@Column(name= "org_tx_email")
    private String email;
	
	@JsonIgnore
	@OneToMany(mappedBy="orgao")
	private List<Servico> servicos;

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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<Servico> getServicos() {
		return servicos;
	}

	public void setServicos(List<Servico> servicos) {
		this.servicos = servicos;
	}

}