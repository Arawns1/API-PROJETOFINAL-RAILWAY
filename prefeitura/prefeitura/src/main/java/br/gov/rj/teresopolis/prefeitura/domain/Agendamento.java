package br.gov.rj.teresopolis.prefeitura.domain;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.FutureOrPresent;

@Entity
@Table(name = "agendamento")
@JsonIdentityInfo(
		generator = ObjectIdGenerators.PropertyGenerator.class,
		property = "agendamentoId",
		scope = Agendamento.class
	)
public class Agendamento {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="age_cd_id")
    private Integer agendamentoId;
	
	@Column(name="age_tx_descricao")
    private String descricao;
    
    @FutureOrPresent(message = "A data inicial não pode estar no passado")
    @Column(name="age_dt_data_hora_inicial")
    private LocalDateTime horaInicial;
    
    @FutureOrPresent(message = "A data final não pode estar no passado")
    @Column(name="age_dt_data_hora_final")
    private LocalDateTime horaFinal;
    
    @Column(name="age_dt_data_hora_agendamento")
    private LocalDateTime horaAgendamento;
    
    @ManyToOne
    @JoinColumn(name = "fk_pessoa_id", referencedColumnName = "pes_cd_id")
    private Pessoa pessoa;
    
    @ManyToOne
    @JoinColumn(name = "fk_servico_id", referencedColumnName = "serv_cd_id")
    private Servico servico;
    
	@OneToMany(mappedBy="agendamento", fetch = FetchType.EAGER)
    private List<Anexo> anexos;

	public Integer getAgendamentoId() {
		return agendamentoId;
	}

	public void setAgendamentoId(Integer agendamentoId) {
		this.agendamentoId = agendamentoId;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public LocalDateTime getHoraInicial() {
		return horaInicial;
	}

	public void setHoraInicial(LocalDateTime horaInicial) {
		this.horaInicial = horaInicial;
	}

	public LocalDateTime getHoraFinal() {
		return horaFinal;
	}

	public void setHoraFinal(LocalDateTime horaFinal) {
		this.horaFinal = horaFinal;
	}

	public LocalDateTime getHoraAgendamento() {
		return horaAgendamento;
	}

	public void setHoraAgendamento(LocalDateTime horaAgendamento) {
		this.horaAgendamento = horaAgendamento;
	}

	public List<Anexo> getAnexos() {
		return anexos;
	}

	public void setAnexos(List<Anexo> anexos) {
		this.anexos = anexos;
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public Servico getServico() {
		return servico;
	}

	public void setServico(Servico servico) {
		this.servico = servico;
	}

	@Override
	public String toString() {
		return "Agendamento [agendamentoId=" + agendamentoId + ", descricao=" + descricao + ", horaInicial="
				+ horaInicial + ", horaFinal=" + horaFinal + ", horaAgendamento=" + horaAgendamento + ", pessoa="
				+ pessoa + ", servico=" + servico + ", anexos=" + anexos + "]";
	}
	
}
