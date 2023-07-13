package br.gov.rj.teresopolis.prefeitura.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class AgendamentoRequestDTO {

	private PessoaDTO pessoaDto;
	private EnderecoDTO enderecoDto;
	private UUID servicoId;
	String descricao;
	LocalDateTime horaInicial;
	LocalDateTime horaFinal;
	private List<AnexoDTO> anexos;
	
	public AgendamentoRequestDTO(PessoaDTO pessoaDto, EnderecoDTO enderecoDto, UUID servicoId, String descricao,
			LocalDateTime horaInicial, LocalDateTime horaFinal, List<AnexoDTO> anexos) {
		super();
		this.pessoaDto = pessoaDto;
		this.enderecoDto = enderecoDto;
		this.servicoId = servicoId;
		this.descricao = descricao;
		this.horaInicial = horaInicial;
		this.horaFinal = horaFinal;
		this.anexos = anexos;
	}

	public AgendamentoRequestDTO() {
	}

	public PessoaDTO getPessoaDto() {
		return pessoaDto;
	}

	public void setPessoaDto(PessoaDTO pessoaDto) {
		this.pessoaDto = pessoaDto;
	}

	public EnderecoDTO getEnderecoDto() {
		return enderecoDto;
	}

	public void setEnderecoDto(EnderecoDTO enderecoDto) {
		this.enderecoDto = enderecoDto;
	}

	public UUID getServicoId() {
		return servicoId;
	}

	public void setServicoId(UUID servicoId) {
		this.servicoId = servicoId;
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

	public List<AnexoDTO> getAnexos() {
		return anexos;
	}

	public void setAnexos(List<AnexoDTO> anexos) {
		this.anexos = anexos;
	}
	
}
