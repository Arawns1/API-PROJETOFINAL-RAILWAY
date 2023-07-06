package br.gov.rj.teresopolis.prefeitura.exceptions;

public class InvalidServiceException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public InvalidServiceException(String msg) {
		super(msg);
	}
}