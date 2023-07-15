package br.gov.rj.teresopolis.prefeitura.utils;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import br.gov.rj.teresopolis.prefeitura.domain.Agendamento;
import br.gov.rj.teresopolis.prefeitura.repositories.AgendamentoRepository;

@Service
public class ScheduleService {

	@Autowired
	AgendamentoRepository agendamentoRepository;
	
	@Scheduled(cron = "0 0 3 * * *")
	public void executarTarefa() {
	
		LocalDateTime dataAtual = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"));
		LocalDateTime dataLimite = dataAtual.minus(90, ChronoUnit.DAYS);

		
		/* System.out.println("Data buscada: " + dataLimite); */
		LocalDateTime dataInicial = dataLimite.with(LocalTime.MIN);
		
		/* System.out.println("Data inicial: " + dataInicial); */
		LocalDateTime dataFinal = dataLimite.with(LocalTime.MAX);
		
		/* System.out.println("Data final: " + dataFinal); */
		List<Agendamento> agendamentosEncontrados = agendamentoRepository.obterAgendamentoPorDia(dataInicial, dataFinal);
		
		System.out.println(agendamentosEncontrados);
		
		if(!agendamentosEncontrados.isEmpty()) {
			agendamentoRepository.deleteAll(agendamentosEncontrados);
		}
	}
}
