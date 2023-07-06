package br.gov.rj.teresopolis.prefeitura.dto;

import java.time.LocalDateTime;
import java.util.List;

public class AgendamentoDTO {
	
	Integer agendamentoId;
	String descricao;
	LocalDateTime horaInicial;
	LocalDateTime horaFinal;
	PessoaDTO pessoa;
	ServicoDTO servico;
	List<AnexoDTO>anexos;
	
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
