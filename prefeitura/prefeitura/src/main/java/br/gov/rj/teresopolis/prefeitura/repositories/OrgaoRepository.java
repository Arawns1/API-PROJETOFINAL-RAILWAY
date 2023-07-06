package br.gov.rj.teresopolis.prefeitura.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.gov.rj.teresopolis.prefeitura.domain.Orgao;

@Repository
public interface OrgaoRepository extends JpaRepository<Orgao, Integer> {
}