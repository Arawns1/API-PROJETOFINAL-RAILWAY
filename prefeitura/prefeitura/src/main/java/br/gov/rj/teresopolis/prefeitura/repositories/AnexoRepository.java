package br.gov.rj.teresopolis.prefeitura.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.gov.rj.teresopolis.prefeitura.domain.Anexo;

@Repository
public interface AnexoRepository extends JpaRepository<Anexo, Integer> {

	@Query("FROM Anexo a where a.agendamento.agendamentoId = :idAgendamento")
	List<Anexo> findAnexosByAgendamento(Integer idAgendamento);
	
}