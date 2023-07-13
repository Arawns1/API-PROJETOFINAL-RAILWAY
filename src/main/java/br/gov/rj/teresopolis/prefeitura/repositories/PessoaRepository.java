package br.gov.rj.teresopolis.prefeitura.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.gov.rj.teresopolis.prefeitura.domain.Pessoa;

@Repository
public interface PessoaRepository extends JpaRepository<Pessoa, UUID> {

	Optional<UUID> findByCpfCnpj(String cpfCnpj);

	Optional<Pessoa> findPessoaByCpfCnpj(String cpfCnpj);
}