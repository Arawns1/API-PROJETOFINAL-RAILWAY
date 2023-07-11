package br.gov.rj.teresopolis.prefeitura.exceptions;

import java.util.UUID;

public class NoSuchElementException extends RuntimeException {
private static final long serialVersionUID = 1L;
	
	public NoSuchElementException(String message) {
		super(message);
	}
	
	public NoSuchElementException(UUID id, String entidade) {
		super("Não foi encontrado(a) " + entidade + " com o id = "+id);
	}
}
