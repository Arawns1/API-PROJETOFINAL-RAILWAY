package br.gov.rj.teresopolis.prefeitura.exceptions;

public class AgendamentoNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public AgendamentoNotFoundException(Integer id) {
		super("Não foi encontrado o agendamento com o id = " + id);
	}
}


