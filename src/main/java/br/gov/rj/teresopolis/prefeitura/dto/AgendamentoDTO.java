package br.gov.rj.teresopolis.prefeitura.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class AgendamentoDTO {
	
	UUID agendamentoId;
	String descricao;
	LocalDateTime horaInicial;
	LocalDateTime horaFinal;
	PessoaDTO pessoa;
	ServicoDTO servico;
	List<AnexoDTO>anexos;
	
	public AgendamentoDTO(UUID agendamentoId, String descricao, LocalDateTime horaInicial, LocalDateTime horaFinal,
			PessoaDTO pessoa, ServicoDTO servico, List<AnexoDTO> anexos) {
		super();
		this.agendamentoId = agendamentoId;
		this.descricao = descricao;
		this.horaInicial = horaInicial;
		this.horaFinal = horaFinal;
		this.pessoa = pessoa;
		this.servico = servico;
		this.anexos = anexos;
	}
	
	public AgendamentoDTO() {
	}

	public UUID getAgendamentoId() {
		return agendamentoId;
	}
	public void setAgendamentoId(UUID agendamentoId) {
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
	public PessoaDTO getPessoa() {
		return pessoa;
	}
	public void setPessoa(PessoaDTO pessoa) {
		this.pessoa = pessoa;
	}
	public ServicoDTO getServico() {
		return servico;
	}
	public void setServico(ServicoDTO servico) {
		this.servico = servico;
	}
	public List<AnexoDTO> getAnexos() {
		return anexos;
	}
	public void setAnexos(List<AnexoDTO> anexos) {
		this.anexos = anexos;
	}
	
	
}
