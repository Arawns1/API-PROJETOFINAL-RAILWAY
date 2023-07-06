package br.gov.rj.teresopolis.prefeitura.exceptions;

public class EmailNotSentException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public EmailNotSentException(String message) {
		super(message);
	}
}
