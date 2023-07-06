package br.gov.rj.teresopolis.prefeitura.exceptions;

public class CEPNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public CEPNotFoundException(String cep) {
		super("Não foi encontrado o endereço com o cep = " + cep);
	}
}
