package br.gov.rj.teresopolis.prefeitura.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.gov.rj.teresopolis.prefeitura.domain.Servico;

@Repository
public interface ServicoRepository extends JpaRepository<Servico, UUID> {
}