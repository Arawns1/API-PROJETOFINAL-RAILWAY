package br.gov.rj.teresopolis.prefeitura.domain;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import br.gov.rj.teresopolis.prefeitura.domain.security.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "servico")
/*
 * @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
 * property = "servicoId", scope = Servico.class)
 */
public class Servico {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "serv_cd_id")
	private UUID servicoId;

	@Column(name = "serv_tx_nome")
	private String nome;

	@Column(name = "serv_tx_tipo_servico")
	private String tipoServico;

	@Column(name = "serv_bool_status")
	private boolean status;

	@ManyToOne
	@JoinColumn(name = "fk_orgao_id", referencedColumnName = "org_cd_id")
	private Orgao orgao;

	@JsonIgnore
	@OneToMany(mappedBy = "servico")
	private List<Agendamento> agendamentos;

	@OneToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "fk_imagem_id", referencedColumnName = "img_cd_id")
	private Imagem imagem;

	@ManyToOne
	@JoinColumn(name = "fk_usuario_id", referencedColumnName = "usu_cd_id")
	private User usuario;

	public UUID getServicoId() {
		return servicoId;
	}

	public void setServicoId(UUID servicoId) {
		this.servicoId = servicoId;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getTipoServico() {
		return tipoServico;
	}

	public void setTipoServico(String tipoServico) {
		this.tipoServico = tipoServico;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public Orgao getOrgao() {
		return orgao;
	}

	public void setOrgao(Orgao orgao) {
		this.orgao = orgao;
	}

	public List<Agendamento> getAgendamentos() {
		return agendamentos;
	}

	public void setAgendamentos(List<Agendamento> agendamentos) {
		this.agendamentos = agendamentos;
	}

	public Imagem getImagem() {
		return imagem;
	}

	public void setImagem(Imagem imagem) {
		this.imagem = imagem;
	}

	public User getUsuario() {
		return usuario;
	}

	public void setUsuario(User usuario) {
		this.usuario = usuario;
	}

	@Override
	public String toString() {
		return "Servico [servicoId=" + servicoId + ", nome=" + nome + ", tipoServico=" + tipoServico + ", orgao="
				+ orgao + "]";
	}

}