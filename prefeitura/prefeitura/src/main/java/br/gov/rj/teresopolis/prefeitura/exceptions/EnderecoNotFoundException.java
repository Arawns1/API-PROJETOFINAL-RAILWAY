package br.gov.rj.teresopolis.prefeitura.exceptions;

public class EnderecoNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public EnderecoNotFoundException(Integer id) {
		super("Não foi encontrado Endereço com o id = " + id);
	}
}
