package br.gov.rj.teresopolis.prefeitura.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.gov.rj.teresopolis.prefeitura.domain.Agendamento;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, UUID> {
	  
	@Query("FROM Agendamento ag where ag.horaFinal >= :dataInicial and ag.horaFinal <= :dataFinal") 
	List<Agendamento> obterAgendamentoPorDia(LocalDateTime dataInicial, LocalDateTime dataFinal);

}