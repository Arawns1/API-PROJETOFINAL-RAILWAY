package br.gov.rj.teresopolis.prefeitura.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.gov.rj.teresopolis.prefeitura.domain.security.User;

public interface UserRepository extends JpaRepository<User, Integer> {
	Optional<User> findByUsername(String username);

	Boolean existsByUsername(String username);

	Boolean existsByEmail(String email);
}