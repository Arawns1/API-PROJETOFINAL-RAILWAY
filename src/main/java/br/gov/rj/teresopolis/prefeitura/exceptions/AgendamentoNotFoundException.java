package br.gov.rj.teresopolis.prefeitura.exceptions;

import java.util.UUID;

public class AgendamentoNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public AgendamentoNotFoundException(UUID uuid) {
		super("Não foi encontrado o agendamento com o id = " + uuid);
	}
}


