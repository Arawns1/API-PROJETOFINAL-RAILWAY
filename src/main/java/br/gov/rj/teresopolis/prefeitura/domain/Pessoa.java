package br.gov.rj.teresopolis.prefeitura.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.hibernate.validator.constraints.br.CNPJ;
import org.hibernate.validator.constraints.br.CPF;
import org.hibernate.validator.group.GroupSequenceProvider;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "pessoa")
@JsonIdentityInfo(
		generator = ObjectIdGenerators.PropertyGenerator.class,
		property = "pessoaId",
		scope = Pessoa.class
	)
public class Pessoa {
	@Id
    @GeneratedValue(strategy=GenerationType.UUID)
	@Column(name= "pes_cd_id")
    private UUID pessoaId;
    
    @NotBlank(message ="O telefone não pode ser nulo")
	@Column(name= "pes_tx_telefone")
    private String telefone;
    
    @NotBlank(message ="O email não pode ser nulo")
	@Column(name= "pes_tx_email")
    private String email;
    
	@Column(name= "pes_tx_nome_razao_social")
    private String nomeRazaoSocial;
    
	@NotNull(message ="A data de registro não pode ser nula")
	@PastOrPresent(message="A data de registro não pode estar no presente ou futuro")
	@Column(name= "pes_dt_data_registro")
    private LocalDate dataRegistro;
    

    @Size(max = 14, message ="O CPF ou CNPJ deve ter no máximo 14 digitos")
    @Column(name = "pes_tx_CPF_CNPJ")
    private String cpfCnpj;
    
	@Column(name= "pes_tx_Identidade_InscricaoMunicipal")
    private String identidadeInscricaoMunicipal;

	@Column(name= "pes_bl_preferencial")
    private boolean preferencial;
    
	@JsonIgnore
	@OneToMany(mappedBy="pessoa")
    private List<Agendamento> agendamentos;
    
    @ManyToOne
    @JoinColumn(name = "fk_endereco_id", referencedColumnName = "end_cd_id")
    private Endereco endereco;

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

	public List<Agendamento> getAgendamentos() {
		return agendamentos;
	}

	public void setAgendamentos(List<Agendamento> agendamentos) {
		this.agendamentos = agendamentos;
	}

	public Endereco getEndereco() {
		return endereco;
	}

	public void setEndereco(Endereco endereco) {
		this.endereco = endereco;
	}


}