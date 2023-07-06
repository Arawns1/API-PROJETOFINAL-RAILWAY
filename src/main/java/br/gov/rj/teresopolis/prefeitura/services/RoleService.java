package br.gov.rj.teresopolis.prefeitura.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.gov.rj.teresopolis.prefeitura.domain.security.Role;
import br.gov.rj.teresopolis.prefeitura.repositories.RoleRepository;

@Service
public class RoleService {
	@Autowired
	RoleRepository roleRepository;
	
	public Role save(Role role) {
		return roleRepository.save(role);
	}
}
