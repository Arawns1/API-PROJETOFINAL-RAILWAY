package br.gov.rj.teresopolis.prefeitura.services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import br.gov.rj.teresopolis.prefeitura.domain.Agendamento;
import br.gov.rj.teresopolis.prefeitura.domain.Pessoa;
import br.gov.rj.teresopolis.prefeitura.domain.Servico;
import br.gov.rj.teresopolis.prefeitura.exceptions.EmailNotSentException;
import br.gov.rj.teresopolis.prefeitura.repositories.PessoaRepository;
import br.gov.rj.teresopolis.prefeitura.repositories.ServicoRepository;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

@Service
public class MailService {

	@Autowired
	public JavaMailSender emailSender;

	@Autowired
	ServicoRepository servicoRepository;

	@Autowired
	PessoaRepository pessoaRepository;

	@Value("${spring.mail.host}")
	private String mailHost;

	@Value("${spring.mail.port}")
	private String mailPort;

	@Value("${spring.mail.username}")
	private String mailUsername;

	@Value("${spring.mail.password}")
	private String mailPassword;

	@Value("${mail.from}")
	private String mailFrom;

	public MailService(JavaMailSender javaMailSender) {
		this.emailSender = javaMailSender;
	}

	public void enviarCalendario(Agendamento agendamento) throws MessagingException {

		Properties props = new Properties();
		props.put("mail.smtp.host", mailHost);
		props.put("mail.smtp.port", mailPort);
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");

		// Cria a sessão de e-mail
		Session session = Session.getInstance(props, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(mailFrom, mailPassword);
			}
		});

		MimeMessage message = new MimeMessage(session);
		message.setFrom(mailFrom);
		message.setSubject("Agendamento marcado com sucesso! - Prefeitura de Teresópolis");

		Optional<Servico> servico = servicoRepository.findById(agendamento.getServico().getServicoId());
		Optional<Pessoa> pessoa = pessoaRepository.findById(agendamento.getPessoa().getPessoaId());

		if (servico.isPresent() && pessoa.isPresent()) {
			agendamento.setServico(servico.get());
			agendamento.setPessoa(pessoa.get());
		} else {
			throw new EmailNotSentException("Não foi possível encontrar o destinatário do email");
		}

		message.setRecipient(Message.RecipientType.TO, new InternetAddress(agendamento.getPessoa().getEmail()));

		// Criar o conteúdo do e-mail
		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(gerarEmailMessage(agendamento));
		multipart.addBodyPart(gerarIcs(agendamento));

		// Adicionar o conteúdo ao e-mail
		message.setContent(multipart);

		Transport.send(message);
	}

	private MimeBodyPart gerarIcs(Agendamento agendamento) {
		// Formatar as datas e horas no formato iCalendar
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");
				String startTimeFormatted = agendamento.getHoraInicial().format(formatter);
				String endTimeFormatted = agendamento.getHoraFinal().format(formatter);
				System.out.println("DataHoraInicial: " + startTimeFormatted);
				// Gerar o conteúdo do arquivo .ics
				String icsContent = "BEGIN:VCALENDAR\n" + 
									"VERSION:2.0\n" + 
									"CALSCALE:GREGORIAN\n"+
									"PRODID:-//My Calendar//EN\n" +
									"BEGIN:VTIMEZONE\r\n" +
									"TZID:America/Sao_Paulo\r\n" +
									"LAST-MODIFIED:20050809T050000Z\r\n" +
									"BEGIN:STANDARD\r\n" +
									"DTSTART:20071104T000000\r\n" +
									"TZOFFSETFROM:-0300\r\n" +
									"TZOFFSETTO:-0300\r\n" +
									"TZNAME:BRT\r\n" +
									"END:STANDARD\r\n" +
									"BEGIN:DAYLIGHT\r\n" +
									"DTSTART:20070218T000000\r\n" +
									"TZOFFSETFROM:-0300\r\n" +
									"TZOFFSETTO:-0200\r\n" +
									"TZNAME:BRST\r\n" +
									"END:DAYLIGHT\r\n" +
									"END:VTIMEZONE\r\n"+
									"BEGIN:VEVENT\r\n"+ 
									"CREATED:"+LocalDateTime.now().format(formatter)+"\r\n" +
									"ORGANIZER;CN=Prefeitura de Teresópolis:mailto:ouvidoria@teresopolis.rj.gov.br\r\n"+
									"DTSTAMP:20140107T121503Z\r\n" +
									"UID:20f78720-d755-4de7-92e5-e41af487e4db\r\n" +
									"SUMMARY:Agendamento: "+agendamento.getServico().getNome()+"\r\n" + 
									"LOCATION: Prefeitura de Teresópolis \r\n" +
									"DESCRIPTION:Não esqueça de levar seus documentos!\r\n" +
									"DTSTART:" + startTimeFormatted + "\n" + 
									"DTEND:" + endTimeFormatted + "\n" + 
									"END:VEVENT\n"+ 
									"END:VCALENDAR";

				// Criar o anexo .ics
				MimeBodyPart calendarPart = new MimeBodyPart();
				try {
					calendarPart.setContent(icsContent, "text/calendar; method=REQUEST");
				} catch (MessagingException e) {
					e.printStackTrace();
				}
				return calendarPart;
	}

	private MimeBodyPart gerarEmailMessage(Agendamento agendamento) {
		String emailMessage;

		emailMessage = "<html>\r\n";
		emailMessage += "<body>\r\n";
		emailMessage += "<img src='https://media.discordapp.net/attachments/929069726372597815/1126557179424547007/Brasao-horizontal-azul.png?width=500&height=100'>\r\n";
		emailMessage += "<h1> Agendamento Marcado com Sucesso!</h1>\r\n";
		emailMessage += "ID do agendamento: " + agendamento.getAgendamentoId() + "\r\n";

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");

		LocalDateTime dataHoraInicial = agendamento.getHoraInicial();
		LocalDateTime dataHoraFinal = agendamento.getHoraFinal();

		String formattedDataHoraInicial = dataHoraInicial.format(formatter);
		String formattedDataHoraFinal = dataHoraFinal.format(formatter);
		emailMessage += "<br>\r\n";
		emailMessage += "Agendado para: " + agendamento.getPessoa().getNomeRazaoSocial() + "\r\n";
		emailMessage += "<br>\r\n";
		emailMessage += "Horário do agendamento: " + formattedDataHoraInicial + " - " + formattedDataHoraFinal + "\r\n";
		emailMessage += "<br>\r\n";
		emailMessage += "Serviço escolhido: " + agendamento.getServico().getNome() + "\r\n";
		emailMessage += "<br>\r\n";
		emailMessage += "Não esqueça de levar seus documentos!\r\n";
		emailMessage += "</body>\r\n";
		emailMessage += "</html>\r\n";

		MimeBodyPart textPart = new MimeBodyPart();
		try {
			textPart.setContent(emailMessage, "text/html");
		} catch (MessagingException e) {
			e.printStackTrace();
		}

		return textPart;
	}

}
