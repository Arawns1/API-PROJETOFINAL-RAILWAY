package br.gov.rj.teresopolis.prefeitura.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.gov.rj.teresopolis.prefeitura.domain.security.Role;
import br.gov.rj.teresopolis.prefeitura.domain.security.RoleEnum;

public interface RoleRepository extends JpaRepository<Role, Integer> {
	Optional<Role> findByName(RoleEnum name);
}