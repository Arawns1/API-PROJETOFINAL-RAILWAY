package br.gov.rj.teresopolis.prefeitura.utils;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.spi.group.DefaultGroupSequenceProvider;

import br.gov.rj.teresopolis.prefeitura.domain.Pessoa;

public class PessoaGroupSequenceProvider implements DefaultGroupSequenceProvider<Pessoa> {

	@Override
	public List<Class<?>> getValidationGroups(Pessoa object) {
		 List<Class<?>> groups = new ArrayList<>();
		 groups.add(Pessoa.class);
		 
		 if (object != null) {
		      if (object.getCpfCnpj().length() > 11) {
		        groups.add(PessoaJuridica.class);
		      } else {
		        groups.add(PessoaFisica.class);
		      }
		    }
		return groups;
	}

}
