package br.gov.rj.teresopolis.prefeitura.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.rj.teresopolis.prefeitura.domain.Agendamento;
import br.gov.rj.teresopolis.prefeitura.domain.Anexo;
import br.gov.rj.teresopolis.prefeitura.domain.Endereco;
import br.gov.rj.teresopolis.prefeitura.domain.Pessoa;
import br.gov.rj.teresopolis.prefeitura.domain.Servico;
import br.gov.rj.teresopolis.prefeitura.dto.AgendamentoDTO;
import br.gov.rj.teresopolis.prefeitura.dto.AgendamentoRequestDTO;
import br.gov.rj.teresopolis.prefeitura.dto.AgendamentoResponseDTO;
import br.gov.rj.teresopolis.prefeitura.dto.PessoaDTO;
import br.gov.rj.teresopolis.prefeitura.dto.ServicoDTO;
import br.gov.rj.teresopolis.prefeitura.exceptions.NoSuchElementException;
import br.gov.rj.teresopolis.prefeitura.repositories.AgendamentoRepository;
import br.gov.rj.teresopolis.prefeitura.repositories.AnexoRepository;
import br.gov.rj.teresopolis.prefeitura.repositories.EnderecoRepository;
import br.gov.rj.teresopolis.prefeitura.repositories.PessoaRepository;
import br.gov.rj.teresopolis.prefeitura.repositories.ServicoRepository;
import jakarta.mail.MessagingException;

@Service
public class AgendamentoService {
	@Autowired
	AgendamentoRepository agendamentoRepository;

	@Autowired
	EnderecoRepository enderecoRepository;

	@Autowired
	PessoaRepository pessoaRepository;

	@Autowired
	ServicoRepository servicoRepository;
	
	@Autowired
	AnexoRepository anexoRepository;
	
	@Autowired
	MailService mailService;

	ModelMapper modelMapper = new ModelMapper();

	public AgendamentoService(AgendamentoRepository agendamentoRepository) {
		this.agendamentoRepository = agendamentoRepository;
	}

	public List<Agendamento> listarAgendamentos() {
		return agendamentoRepository.findAll();
	}

	public Agendamento obterAgendamentoPorId(UUID id) {
		Agendamento agendamento = agendamentoRepository.findById(id)
				.orElseThrow(() -> new NoSuchElementException("Não foi encontrado o agendamento de id= " + id));
		return agendamento;
	}

	public AgendamentoDTO criarAgendamento(Agendamento agendamento) {
		agendamento.setHoraAgendamento(LocalDateTime.now());
		Agendamento agendamentoSalvo = agendamentoRepository.save(agendamento);
		AgendamentoDTO agendamentoDTO = new AgendamentoDTO();
		ServicoDTO servico = modelMapper.map(agendamentoSalvo.getServico(), ServicoDTO.class);
		PessoaDTO pessoa = modelMapper.map(agendamentoSalvo.getPessoa(), PessoaDTO.class);
		agendamentoDTO.setServico(servico);
		agendamentoDTO.setPessoa(pessoa);
		return agendamentoDTO;
	}

	public AgendamentoDTO atualizarAgendamento(UUID id, Agendamento agendamento) {
		Optional<Agendamento> agendamentoExistenteOptional = agendamentoRepository.findById(id);
		if (agendamentoExistenteOptional.isPresent()) {
			Agendamento agendamentoExistente = agendamentoExistenteOptional.get();
			modelMapper.map(agendamento, agendamentoExistente);
			agendamentoExistente.setAgendamentoId(id);
			Agendamento agendamentoSalvo = agendamentoRepository.save(agendamentoExistente);
			return modelMapper.map(agendamentoSalvo, AgendamentoDTO.class);
		} else {
			throw new NoSuchElementException("Não foi encontrado o agendamento de id = " + id + " para ser atualizado");
		}
	}

	public Agendamento cadastroEnvioEmail(Agendamento agendamento) {
		agendamento.setHoraAgendamento(LocalDateTime.now());
		Agendamento agendamentoSalvo = agendamentoRepository.save(agendamento);
		try {
			System.out.println("Agendamento" + agendamentoSalvo);
			mailService.enviarCalendario(agendamentoSalvo);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		return agendamentoSalvo;
	}

	public void excluirAgendamento(UUID id) {
		agendamentoRepository.deleteById(id);
	}

	public List<Agendamento> obterAgendamentoPorDia(String dia) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		DateTimeFormatter LocalDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		LocalDateTime localDateTime = null;
		try {
			Date data = sdf.parse(dia);
			localDateTime = data.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		} catch (ParseException e) {
			throw new NoSuchElementException("O formato da data é inválido");
		}
		LocalDateTime dataInicial = localDateTime.with(LocalTime.MIN);
		LocalDateTime dataFinal = localDateTime.with(LocalTime.MAX);

		return agendamentoRepository.obterAgendamentoPorDia(dataInicial, dataFinal);
	}

	@Transactional
	public AgendamentoResponseDTO criarAgendamentoDto(AgendamentoRequestDTO agendamentoRequestDto) {
		
		Pessoa pessoa = vericaPessoaEndereco(agendamentoRequestDto);
		Servico servico = verificaServico(agendamentoRequestDto);
		
		Agendamento agendamento = new Agendamento();
		agendamento.setDescricao(agendamentoRequestDto.getDescricao());
		agendamento.setHoraAgendamento(LocalDateTime.now());
		agendamento.setHoraInicial(agendamentoRequestDto.getHoraInicial());
		agendamento.setHoraFinal(agendamentoRequestDto.getHoraFinal());
		agendamento.setPessoa(pessoa);
		agendamento.setServico(servico);
		
		Agendamento agendamentoSalvo = agendamentoRepository.save(agendamento);
		
		if(!(agendamentoRequestDto.getAnexos() == null)) {
			List<Anexo> anexosList = agendamentoRequestDto.getAnexos()
					.stream()
					.map(anexo -> {
						Anexo anexoModificado = modelMapper.map(anexo, Anexo.class);
						anexoModificado.setAnexoId(agendamentoSalvo.getAgendamentoId());
						return anexoModificado;
					})
					.collect(Collectors.toList());
			anexoRepository.saveAll(anexosList);
		}
		
		String formattedText;
		if (agendamentoSalvo.getPessoa().getCpfCnpj().length() == 11) {
		    // É um CPF
		    formattedText = agendamentoSalvo.getPessoa().getCpfCnpj().replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
		} else if (agendamentoSalvo.getPessoa().getCpfCnpj().length() == 14) {
		    // É um CNPJ
		    formattedText = agendamentoSalvo.getPessoa().getCpfCnpj().replaceAll("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})", "$1.$2.$3/$4-$5");
		} else {
		    // Caso contrário, não é um CPF nem CNPJ válido
		    formattedText = agendamentoSalvo.getPessoa().getCpfCnpj();
		}
		
		
		DateTimeFormatter dataFormatada = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		DateTimeFormatter horaFormatada = DateTimeFormatter.ofPattern("HH:mm");
		
		String data = (agendamentoSalvo.getHoraInicial()).format(dataFormatada);
		String hora = (agendamentoSalvo.getHoraInicial()).format(horaFormatada);
		
		
		AgendamentoResponseDTO response = new AgendamentoResponseDTO();
		
		return new AgendamentoResponseDTO(agendamentoSalvo.getAgendamentoId(), 
										  agendamentoSalvo.getServico().getNome(), 
										  agendamentoSalvo.getPessoa().getNomeRazaoSocial(),
										  formattedText,
										  data,
										  hora);
	}
	
	
	/**
	 * Esta função verifica se a pessoa ou endereço já existe no banco. 
	 * Dependendo da existência ou não da pessoa ou endereço realiza a criação ou alteração
	 * dos mesmos.
	 * @param agendamentoRequestDto
	 * @return Pessoa
	 */
	private Pessoa vericaPessoaEndereco(AgendamentoRequestDTO agendamentoRequestDto) {
		
		Optional<Pessoa> pessoa = pessoaRepository.findPessoaByCpfCnpj(agendamentoRequestDto.getPessoaDto().getCpfCnpj());
		Optional<Endereco> endereco;
		
		//Se a pessoa existe verifica se o endereço é o mesmo e se já está vinculado a ela
		if (pessoa.isPresent()) {
			endereco = enderecoRepository.findById(pessoa.get().getEndereco().getEnderecoId());
		
			if (endereco.isPresent()) {
				return pessoa.get();
			}
			//Se a pessoa existe, mas o endereço não. Salva o novo endereço e atualiza a pessoa.
			else {
				Endereco enderecoConvertido = modelMapper.map(agendamentoRequestDto.getEnderecoDto(), Endereco.class);
				Endereco enderecoSalvo = enderecoRepository.save(enderecoConvertido);
				pessoa.get().setEndereco(enderecoSalvo);
				return pessoaRepository.save(pessoa.get());
			}
		}
		//Se a pessoa não existe.
		else {
			Pessoa pessoaConvertida = modelMapper.map(agendamentoRequestDto.getPessoaDto(), Pessoa.class);
			pessoaConvertida.setPreferencial(agendamentoRequestDto.getPessoaDto().isPreferencial());
			endereco = enderecoRepository.findByCep(agendamentoRequestDto.getEnderecoDto().getCep());
			
			//Se a Pessoa e o endereço não existem
			if (endereco.isEmpty()) {
				Endereco enderecoConvertido = modelMapper.map(agendamentoRequestDto.getEnderecoDto(), Endereco.class);
				enderecoRepository.save(enderecoConvertido);
				pessoaConvertida.setEndereco(enderecoConvertido);
			}
			//Caso o endereço já exista
			else {
				pessoaConvertida.setEndereco(endereco.get());
			}
			return pessoaRepository.save(pessoaConvertida);
		}
	}

	/**
	 * Esta função verifica se o serviço recebido existe no banco de dados e retorna
	 * caso exista.
	 * @param agendamentoRequestDto
	 * @return
	 */
	private Servico verificaServico(AgendamentoRequestDTO agendamentoRequestDto) {
		Optional<Servico> servico = servicoRepository.findById(agendamentoRequestDto.getServicoId());
		if(servico.isPresent()) {
			return servico.get();
		}
		else {
			throw new NoSuchElementException("Erro ao verificar Serviço. Serviço não encontrado id="+ agendamentoRequestDto.getServicoId());
		}
	}
}
