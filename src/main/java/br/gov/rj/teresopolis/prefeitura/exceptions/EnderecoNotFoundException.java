package br.gov.rj.teresopolis.prefeitura.exceptions;

import java.util.UUID;

public class EnderecoNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public EnderecoNotFoundException(UUID id) {
		super("Não foi encontrado Endereço com o id = " + id);
	}
}
