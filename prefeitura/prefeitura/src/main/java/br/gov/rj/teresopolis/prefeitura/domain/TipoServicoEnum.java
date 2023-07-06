package br.gov.rj.teresopolis.prefeitura.domain;

public enum TipoServicoEnum {
    PF(1, "Pessoa fisica"),
    PJ(2, "Pessoa Juridica");
	
	private final int valor;
    private final String tipoServico;

    private TipoServicoEnum(int valor, String tipoServico) {
        this.valor = valor;
        this.tipoServico = tipoServico;
    }
}