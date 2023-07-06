package br.gov.rj.teresopolis.prefeitura.exceptions;

public class NoSuchElementException extends RuntimeException {
private static final long serialVersionUID = 1L;
	
	public NoSuchElementException(String message) {
		super(message);
	}
	
	public NoSuchElementException(Integer id, String entidade) {
		super("Não foi encontrado(a) " + entidade + " com o id = "+id);
	}
}
