package br.gov.rj.teresopolis.prefeitura.dto;

import java.util.UUID;

public class AgendamentoResponseDTO {
	private UUID agendamentoId;
	private String tipoServiço;
	private String nomeRazaoSocial;
	private String cpfCnpj;
	private String dataAgendamento;
	private String horaAgendamento;
	
	public AgendamentoResponseDTO(UUID agendamentoId, String tipoServiço, String nomeRazaoSocial, String cpfCnpj,
			String dataAgendamento, String horaAgendamento) {
		super();
		this.agendamentoId = agendamentoId;
		this.tipoServiço = tipoServiço;
		this.nomeRazaoSocial = nomeRazaoSocial;
		this.cpfCnpj = cpfCnpj;
		this.dataAgendamento = dataAgendamento;
		this.horaAgendamento = horaAgendamento;
	}
	
	public AgendamentoResponseDTO() {
		super();
	}

	public UUID getAgendamentoId() {
		return agendamentoId;
	}
	public void setAgendamentoId(UUID agendamentoId) {
		this.agendamentoId = agendamentoId;
	}
	public String getTipoServiço() {
		return tipoServiço;
	}
	public void setTipoServiço(String tipoServiço) {
		this.tipoServiço = tipoServiço;
	}
	public String getNomeRazaoSocial() {
		return nomeRazaoSocial;
	}
	public void setNomeRazaoSocial(String nomeRazaoSocial) {
		this.nomeRazaoSocial = nomeRazaoSocial;
	}
	public String getCpfCnpj() {
		return cpfCnpj;
	}
	public void setCpfCnpj(String cpfCnpj) {
		this.cpfCnpj = cpfCnpj;
	}
	public String getDataAgendamento() {
		return dataAgendamento;
	}
	public void setDataAgendamento(String dataAgendamento) {
		this.dataAgendamento = dataAgendamento;
	}
	public String getHoraAgendamento() {
		return horaAgendamento;
	}
	public void setHoraAgendamento(String horaAgendamento) {
		this.horaAgendamento = horaAgendamento;
	}
	
}
