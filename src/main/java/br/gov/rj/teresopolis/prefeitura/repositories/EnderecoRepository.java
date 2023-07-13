package br.gov.rj.teresopolis.prefeitura.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.gov.rj.teresopolis.prefeitura.domain.Endereco;

@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, UUID> {
	
	Optional<UUID>findIdByCep(String CEP);
	
	Optional<Endereco> findByCep(String CEP);
}