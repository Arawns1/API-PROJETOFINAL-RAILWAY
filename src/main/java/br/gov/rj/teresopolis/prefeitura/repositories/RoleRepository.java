package br.gov.rj.teresopolis.prefeitura.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.gov.rj.teresopolis.prefeitura.domain.security.Role;
import br.gov.rj.teresopolis.prefeitura.domain.security.RoleEnum;

public interface RoleRepository extends JpaRepository<Role, UUID> {
	Optional<Role> findByName(RoleEnum name);
}