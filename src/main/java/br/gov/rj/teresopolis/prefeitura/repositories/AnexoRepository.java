package br.gov.rj.teresopolis.prefeitura.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.gov.rj.teresopolis.prefeitura.domain.Anexo;

@Repository
public interface AnexoRepository extends JpaRepository<Anexo, UUID> {

	@Query("FROM Anexo a where a.agendamento.agendamentoId = :idAgendamento")
	List<Anexo> findAnexosByAgendamento(UUID id);
	
}