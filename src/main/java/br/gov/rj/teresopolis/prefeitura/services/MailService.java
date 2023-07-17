package br.gov.rj.teresopolis.prefeitura.services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import br.gov.rj.teresopolis.prefeitura.domain.Agendamento;
import br.gov.rj.teresopolis.prefeitura.domain.Anexo;
import br.gov.rj.teresopolis.prefeitura.domain.Pessoa;
import br.gov.rj.teresopolis.prefeitura.domain.Servico;
import br.gov.rj.teresopolis.prefeitura.exceptions.EmailNotSentException;
import br.gov.rj.teresopolis.prefeitura.repositories.AnexoRepository;
import br.gov.rj.teresopolis.prefeitura.repositories.PessoaRepository;
import br.gov.rj.teresopolis.prefeitura.repositories.ServicoRepository;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.mail.Address;
import jakarta.mail.Authenticator;
import jakarta.mail.BodyPart;
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
import jakarta.mail.util.ByteArrayDataSource;
import jakarta.transaction.Transactional;

@Service
public class MailService {

	@Autowired
	public JavaMailSender emailSender;

	@Autowired
	ServicoRepository servicoRepository;

	@Autowired
	PessoaRepository pessoaRepository;
	
	@Autowired
	AnexoRepository anexoRepository;

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

	@Transactional
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

		Transport.send(corpoEmailPessoa(agendamento, session));
		Transport.send(corpoEmailOrgao(agendamento, session));
		
	}

	private MimeMessage corpoEmailPessoa(Agendamento agendamento, Session session) {
		//Mensagem Pessoa
				MimeMessage message = new MimeMessage(session);
				try {
					message.setFrom(mailFrom);
				} catch (MessagingException e) {
					throw new EmailNotSentException("Erro ao definir remetente");
				}
				
				try {
					message.setSubject("Agendamento marcado com sucesso! - Prefeitura de Teresópolis");
				} catch (MessagingException e) {
					throw new EmailNotSentException("Erro ao definir Assunto do Email");
				}

				Optional<Servico> servico = servicoRepository.findById(agendamento.getServico().getServicoId());
				Optional<Pessoa> pessoa = pessoaRepository.findById(agendamento.getPessoa().getPessoaId());

				if (servico.isPresent() && pessoa.isPresent()) {
					agendamento.setServico(servico.get());
					agendamento.setPessoa(pessoa.get());
				} else {
					throw new EmailNotSentException("Não foi possível encontrar o destinatário do email");
				}
				
				Address[] destinatarios = new Address[1];
				
				try {
					destinatarios[0] = new InternetAddress(agendamento.getPessoa().getEmail());
					message.setRecipients(Message.RecipientType.TO, destinatarios);
				} catch (Exception e) {
					throw new EmailNotSentException("Erro ao definir email do destinatario");
				}
				
			
				try {
					// Criar o conteúdo do e-mail
					Multipart multipart = new MimeMultipart();
					multipart.addBodyPart(gerarEmailMessage(agendamento));
					multipart.addBodyPart(gerarIcs(agendamento));
					
					// Adicionar o conteúdo ao e-mail
					message.setContent(multipart);
				} catch (MessagingException e) {
					throw new EmailNotSentException("Erro ao gerar conteudo do email ");
				}
				
				
				return message;
	}
	
	private MimeMessage corpoEmailOrgao(Agendamento agendamento, Session session) {
		//Mensagem Orgão
				MimeMessage message = new MimeMessage(session);
				try {
					message.setFrom(mailFrom);
				} catch (MessagingException e) {
					throw new EmailNotSentException("Erro ao definir remetente" );
				}
				
				try {
					message.setSubject("Novo Agendamento! - Prefeitura de Teresópolis");
				} catch (MessagingException e) {
					throw new EmailNotSentException("Erro ao definir Assunto do Email" );
				}

				Optional<Servico> servico = servicoRepository.findById(agendamento.getServico().getServicoId());
				Optional<Pessoa> pessoa = pessoaRepository.findById(agendamento.getPessoa().getPessoaId());

				if (servico.isPresent() && pessoa.isPresent()) {
					agendamento.setServico(servico.get());
					agendamento.setPessoa(pessoa.get());
				} else {
					throw new EmailNotSentException("Não foi possível encontrar o destinatário do email");
				}
				
				Address[] destinatarios = new Address[1];
				
				try {
					destinatarios[0] = new InternetAddress(agendamento.getServico().getOrgao().getEmail());
					message.setRecipients(Message.RecipientType.TO, destinatarios);
				} catch (Exception e) {
					throw new EmailNotSentException("Erro ao definir email do destinatario");
				}
			
				try {
					// Criar o conteúdo do e-mail
					Multipart multipart = new MimeMultipart();
					multipart.addBodyPart(gerarEmailMessageOrgao(agendamento));
					multipart.addBodyPart(gerarIcs(agendamento));
					
					try {
						List<Anexo> anexos = anexoRepository.findAnexosByAgendamento(agendamento.getAgendamentoId());
						
						if(!(anexos.isEmpty())) {
							for(Anexo anexo : anexos) {
								byte[] dadosAnexo = anexo.getDados();
								DataSource source = new ByteArrayDataSource(dadosAnexo, "application/octet-stream");
								BodyPart bodyPart = new MimeBodyPart();
								bodyPart.setDataHandler(new DataHandler(source));
								bodyPart.setFileName(anexo.getNome());
								multipart.addBodyPart(bodyPart);
							}
						}
						
					}catch(MessagingException e) {
						throw new EmailNotSentException("Erro ao gerar anexos do email ");
					}
					
					// Adicionar o conteúdo ao e-mail
					message.setContent(multipart);
				} catch (MessagingException e) {
					throw new EmailNotSentException("Erro ao gerar conteudo do email ");
				}
				
				return message;
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

	private MimeBodyPart gerarEmailMessageOrgao(Agendamento agendamento) {
		String emailMessage;
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");
		DateTimeFormatter dateformatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		
		LocalDateTime dataHoraInicial = agendamento.getHoraInicial();
		LocalDateTime dataHoraFinal = agendamento.getHoraFinal();

		String formattedDataHoraInicial = dataHoraInicial.format(formatter);
		String formattedDataHoraFinal = dataHoraFinal.format(formatter);
		
		emailMessage = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\r\n"
				+ "<html style=\"padding:0;Margin:0\">\r\n"
				+ "<head>\r\n"
				+ "<meta charset=\"UTF-8\">\r\n"
				+ "<meta content=\"width=device-width, initial-scale=1\" name=\"viewport\">\r\n"
				+ "<meta name=\"x-apple-disable-message-reformatting\">\r\n"
				+ "<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\r\n"
				+ "<meta content=\"telephone=no\" name=\"format-detection\">\r\n"
				+ "<title>Envio para Secretaria</title><!--[if (mso 16)]>\r\n"
				+ "<style type=\"text/css\">\r\n"
				+ "a {text-decoration: none;}\r\n"
				+ "</style>\r\n"
				+ "<![endif]--><!--[if gte mso 9]><style>sup { font-size: 100% !important; }</style><![endif]--><!--[if gte mso 9]>\r\n"
				+ "<xml>\r\n"
				+ "<o:OfficeDocumentSettings>\r\n"
				+ "<o:AllowPNG></o:AllowPNG>\r\n"
				+ "<o:PixelsPerInch>96</o:PixelsPerInch>\r\n"
				+ "</o:OfficeDocumentSettings>\r\n"
				+ "</xml>\r\n"
				+ "<![endif]--><!--[if !mso]><!-- -->\r\n"
				+ "<link href=\"https://fonts.googleapis.com/css?family=Open+Sans:400,400i,700,700i\" rel=\"stylesheet\"><!--<![endif]-->\r\n"
				+ "<style type=\"text/css\">\r\n"
				+ "#outlook a {\r\n"
				+ "padding:0;\r\n"
				+ "}\r\n"
				+ ".ExternalClass {\r\n"
				+ "width:100%;\r\n"
				+ "}\r\n"
				+ ".ExternalClass,\r\n"
				+ ".ExternalClass p,\r\n"
				+ ".ExternalClass span,\r\n"
				+ ".ExternalClass font,\r\n"
				+ ".ExternalClass td,\r\n"
				+ ".ExternalClass div {\r\n"
				+ "line-height:100%;\r\n"
				+ "}\r\n"
				+ ".es-button {\r\n"
				+ "mso-style-priority:100!important;\r\n"
				+ "text-decoration:none!important;\r\n"
				+ "}\r\n"
				+ "a[x-apple-data-detectors] {\r\n"
				+ "color:inherit!important;\r\n"
				+ "text-decoration:none!important;\r\n"
				+ "font-size:inherit!important;\r\n"
				+ "font-family:inherit!important;\r\n"
				+ "font-weight:inherit!important;\r\n"
				+ "line-height:inherit!important;\r\n"
				+ "}\r\n"
				+ ".es-desk-hidden {\r\n"
				+ "display:none;\r\n"
				+ "float:left;\r\n"
				+ "overflow:hidden;\r\n"
				+ "width:0;\r\n"
				+ "max-height:0;\r\n"
				+ "line-height:0;\r\n"
				+ "mso-hide:all;\r\n"
				+ "}\r\n"
				+ "@media only screen and (max-width:600px) {p, ul li, ol li, a { line-height:150%!important } h1, h2, h3, h1 a, h2 a, h3 a { line-height:120%!important } h1 { font-size:32px!important; text-align:center } h2 { font-size:26px!important; text-align:center } h3 { font-size:20px!important; text-align:center } .es-header-body h1 a, .es-content-body h1 a, .es-footer-body h1 a { font-size:32px!important } .es-header-body h2 a, .es-content-body h2 a, .es-footer-body h2 a { font-size:26px!important } .es-header-body h3 a, .es-content-body h3 a, .es-footer-body h3 a { font-size:20px!important } .es-menu td a { font-size:16px!important } .es-header-body p, .es-header-body ul li, .es-header-body ol li, .es-header-body a { font-size:16px!important } .es-content-body p, .es-content-body ul li, .es-content-body ol li, .es-content-body a { font-size:16px!important } .es-footer-body p, .es-footer-body ul li, .es-footer-body ol li, .es-footer-body a { font-size:16px!important } .es-infoblock p, .es-infoblock ul li, .es-infoblock ol li, .es-infoblock a { font-size:12px!important } *[class=\"gmail-fix\"] { display:none!important } .es-m-txt-c, .es-m-txt-c h1, .es-m-txt-c h2, .es-m-txt-c h3 { text-align:center!important } .es-m-txt-r, .es-m-txt-r h1, .es-m-txt-r h2, .es-m-txt-r h3 { text-align:right!important } .es-m-txt-l, .es-m-txt-l h1, .es-m-txt-l h2, .es-m-txt-l h3 { text-align:left!important } .es-m-txt-r img, .es-m-txt-c img, .es-m-txt-l img { display:inline!important } .es-button-border { display:inline-block!important } a.es-button, button.es-button { font-size:16px!important; display:inline-block!important; padding:15px 30px 15px 30px!important } .es-btn-fw { border-width:10px 0px!important; text-align:center!important } .es-adaptive table, .es-btn-fw, .es-btn-fw-brdr, .es-left, .es-right { width:100%!important } .es-content table, .es-header table, .es-footer table, .es-content, .es-footer, .es-header { width:100%!important; max-width:600px!important } .es-adapt-td { display:block!important; width:100%!important } .adapt-img { width:100%!important; height:auto!important } .es-m-p0 { padding:0!important } .es-m-p0r { padding-right:0!important } .es-m-p0l { padding-left:0!important } .es-m-p0t { padding-top:0!important } .es-m-p0b { padding-bottom:0!important } .es-m-p20b { padding-bottom:20px!important } .es-mobile-hidden, .es-hidden { display:none!important } tr.es-desk-hidden, td.es-desk-hidden, table.es-desk-hidden { width:auto!important; overflow:visible!important; float:none!important; max-height:inherit!important; line-height:inherit!important } tr.es-desk-hidden { display:table-row!important } table.es-desk-hidden { display:table!important } td.es-desk-menu-hidden { display:table-cell!important } .es-menu td { width:1%!important } table.es-table-not-adapt, .esd-block-html table { width:auto!important } table.es-social { display:inline-block!important } table.es-social td { display:inline-block!important } .es-desk-hidden { display:table-row!important; width:auto!important; overflow:visible!important; max-height:inherit!important } .h-auto { height:auto!important } .es-m-p5 { padding:5px!important } .es-m-p5t { padding-top:5px!important } .es-m-p5b { padding-bottom:5px!important } .es-m-p5r { padding-right:5px!important } .es-m-p5l { padding-left:5px!important } .es-m-p10 { padding:10px!important } .es-m-p10t { padding-top:10px!important } .es-m-p10b { padding-bottom:10px!important } .es-m-p10r { padding-right:10px!important } .es-m-p10l { padding-left:10px!important } .es-m-p15 { padding:15px!important } .es-m-p15t { padding-top:15px!important } .es-m-p15b { padding-bottom:15px!important } .es-m-p15r { padding-right:15px!important } .es-m-p15l { padding-left:15px!important } .es-m-p20 { padding:20px!important } .es-m-p20t { padding-top:20px!important } .es-m-p20r { padding-right:20px!important } .es-m-p20l { padding-left:20px!important } .es-m-p25 { padding:25px!important } .es-m-p25t { padding-top:25px!important } .es-m-p25b { padding-bottom:25px!important } .es-m-p25r { padding-right:25px!important } .es-m-p25l { padding-left:25px!important } .es-m-p30 { padding:30px!important } .es-m-p30t { padding-top:30px!important } .es-m-p30b { padding-bottom:30px!important } .es-m-p30r { padding-right:30px!important } .es-m-p30l { padding-left:30px!important } .es-m-p35 { padding:35px!important } .es-m-p35t { padding-top:35px!important } .es-m-p35b { padding-bottom:35px!important } .es-m-p35r { padding-right:35px!important } .es-m-p35l { padding-left:35px!important } .es-m-p40 { padding:40px!important } .es-m-p40t { padding-top:40px!important } .es-m-p40b { padding-bottom:40px!important } .es-m-p40r { padding-right:40px!important } .es-m-p40l { padding-left:40px!important } }\r\n"
				+ "</style>\r\n"
				+ "</head>\r\n"
				+ "<body data-new-gr-c-s-check-loaded=\"14.1115.0\" data-gr-ext-installed class=\"vsc-initialized\" data-autofill-highlight=\"false\" data-new-gr-c-s-loaded=\"14.1115.0\" style=\"width:100%;font-family:'open sans', 'helvetica neue', helvetica, arial, sans-serif;-webkit-text-size-adjust:100%;-ms-text-size-adjust:100%;padding:0;Margin:0\">\r\n"
				+ "<div class=\"es-wrapper-color\" style=\"background-color:#EEEEEE\"><!--[if gte mso 9]>\r\n"
				+ "<v:background xmlns:v=\"urn:schemas-microsoft-com:vml\" fill=\"t\">\r\n"
				+ "<v:fill type=\"tile\" color=\"#eeeeee\"></v:fill>\r\n"
				+ "</v:background>\r\n"
				+ "<![endif]-->\r\n"
				+ "<table class=\"es-wrapper\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;padding:0;Margin:0;width:100%;height:100%;background-repeat:repeat;background-position:center top;background-color:#EEEEEE\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td valign=\"top\" style=\"padding:0;Margin:0\">\r\n"
				+ "<table cellpadding=\"0\" cellspacing=\"0\" class=\"es-content\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td align=\"center\" style=\"padding:0;Margin:0\">\r\n"
				+ "<table class=\"es-content-body\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:transparent;width:600px\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td align=\"left\" style=\"Margin:0;padding-left:10px;padding-right:10px;padding-top:15px;padding-bottom:15px\"><!--[if mso]><table style=\"width:580px\" cellpadding=\"0\" cellspacing=\"0\"><tr><td style=\"width:465px\" valign=\"top\"><![endif]-->\r\n"
				+ "<table class=\"es-left\" cellspacing=\"0\" cellpadding=\"0\" align=\"left\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;float:left\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td align=\"left\" style=\"padding:0;Margin:0;width:465px\">\r\n"
				+ "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td class=\"es-infoblock es-m-txt-c\" align=\"left\" style=\"padding:0;Margin:0;line-height:14px;font-size:12px;color:#CCCCCC\"><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:arial, 'helvetica\\ neue', helvetica, sans-serif;line-height:14px;color:#CCCCCC;font-size:12px\">Novo agendamento realizado pelo portal de serviços!</p></td>\r\n"
				+ "</tr>\r\n"
				+ "</table></td>\r\n"
				+ "</tr>\r\n"
				+ "</table><!--[if mso]></td><td style=\"width:20px\"></td><td style=\"width:95px\" valign=\"top\"><![endif]-->\r\n"
				+ "<table class=\"es-right\" cellspacing=\"0\" cellpadding=\"0\" align=\"right\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;float:right\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td align=\"left\" style=\"padding:0;Margin:0;width:95px\">\r\n"
				+ "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td align=\"right\" class=\"es-infoblock es-m-txt-c\" style=\"padding:0;Margin:0;line-height:14px;font-size:12px;color:#CCCCCC\"><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:'open sans', 'helvetica neue', helvetica, arial, sans-serif;line-height:14px;color:#CCCCCC;font-size:12px\"><a href=\"https://viewstripo.email\" class=\"view\" target=\"_blank\" style=\"-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;text-decoration:none;color:#CCCCCC;font-size:12px;font-family:arial, 'helvetica neue', helvetica, sans-serif\">View in browser</a></p></td>\r\n"
				+ "</tr>\r\n"
				+ "</table></td>\r\n"
				+ "</tr>\r\n"
				+ "</table><!--[if mso]></td></tr></table><![endif]--></td>\r\n"
				+ "</tr>\r\n"
				+ "</table></td>\r\n"
				+ "</tr>\r\n"
				+ "</table>\r\n"
				+ "<table class=\"es-content\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td align=\"center\" style=\"padding:0;Margin:0\">\r\n"
				+ "<table class=\"es-content-body\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#ffffff\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#FFFFFF;width:600px\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td style=\"Margin:0;padding-top:20px;padding-bottom:20px;padding-left:20px;padding-right:20px;background-color:#2d5299\" bgcolor=\"#2D5299\" align=\"left\">\r\n"
				+ "<table cellspacing=\"0\" cellpadding=\"0\" width=\"100%\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td align=\"left\" style=\"padding:0;Margin:0;width:560px\">\r\n"
				+ "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td class=\"es-m-p0l es-m-txt-c\" align=\"center\" style=\"padding:0;Margin:0;font-size:0px\"><a href=\"https://viewstripo.email\" target=\"_blank\" style=\"-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;text-decoration:none;color:#ED8E20;font-size:16px\"><img src=\"https://xoaxho.stripocdn.email/content/guids/CABINET_69b1a3497d22bf502b8ddc663cc9661982183c44c243cdfd69b90917a432e3a8/images/logopmtbranca.png\" alt style=\"display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic\" width=\"162\"></a></td>\r\n"
				+ "</tr>\r\n"
				+ "</table></td>\r\n"
				+ "</tr>\r\n"
				+ "</table></td>\r\n"
				+ "</tr>\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td align=\"left\" style=\"padding:0;Margin:0;padding-left:35px;padding-right:35px;padding-top:40px\">\r\n"
				+ "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;width:530px\">\r\n"
				+ "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td align=\"center\" style=\"Margin:0;padding-top:25px;padding-bottom:25px;padding-left:35px;padding-right:35px;font-size:0\"><a target=\"_blank\" href=\"https://viewstripo.email/\" style=\"-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;text-decoration:none;color:#ED8E20;font-size:16px\"><img src=\"https://xoaxho.stripocdn.email/content/guids/CABINET_75694a6fc3c4633b3ee8e3c750851c02/images/67611522142640957.png\" alt style=\"display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic\" width=\"120\"></a></td>\r\n"
				+ "</tr>\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td align=\"center\" style=\"padding:0;Margin:0;padding-bottom:10px\"><h2 style=\"Margin:0;line-height:36px;mso-line-height-rule:exactly;font-family:'open sans', 'helvetica neue', helvetica, arial, sans-serif;font-size:30px;font-style:normal;font-weight:bold;color:#333333\">Novo agendamento!</h2></td>\r\n"
				+ "</tr>\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td align=\"left\" style=\"padding:0;Margin:0;padding-top:15px;padding-bottom:20px\"><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:'open sans', 'helvetica neue', helvetica, arial, sans-serif;line-height:24px;color:#777777;font-size:16px\">Um novo agendamento foi realizado através do portal de serviços! Veja as informações do requerente logo abaixo</p></td>\r\n"
				+ "</tr>\r\n"
				+ "</table></td>\r\n"
				+ "</tr>\r\n"
				+ "</table></td>\r\n"
				+ "</tr>\r\n"
				+ "</table></td>\r\n"
				+ "</tr>\r\n"
				+ "</table>\r\n"
				+ "<table class=\"es-content\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td align=\"center\" style=\"padding:0;Margin:0\">\r\n"
				+ "<table class=\"es-content-body\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#ffffff\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#FFFFFF;width:600px\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td align=\"left\" style=\"padding:0;Margin:0;padding-top:20px;padding-left:35px;padding-right:35px\">\r\n"
				+ "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;width:530px\">\r\n"
				+ "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td bgcolor=\"#eeeeee\" align=\"left\" style=\"Margin:0;padding-right:5px;padding-top:10px;padding-bottom:10px;padding-left:10px\"><span style=\"font-size:18px\"></span>\r\n"
				+ "<table style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;width:500px\" class=\"cke_show_border\" cellspacing=\"1\" cellpadding=\"1\" border=\"0\" align=\"left\" role=\"presentation\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td width=\"80%\" style=\"padding:0;Margin:0;font-size:18px\"><strong>Informações do Agendamento</strong></td>\r\n"
				+ "</tr>\r\n"
				+ "</table></td>\r\n"
				+ "</tr>\r\n"
				+ "</table></td>\r\n"
				+ "</tr>\r\n"
				+ "</table></td>\r\n"
				+ "</tr>\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td align=\"left\" style=\"padding:0;Margin:0;padding-left:35px;padding-right:35px\">\r\n"
				+ "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;width:530px\">\r\n"
				+ "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td align=\"left\" bgcolor=\"#efefef\" style=\"padding:0;Margin:0;padding-left:15px\">\r\n"
				+ "<table border=\"1\" cellspacing=\"1\" cellpadding=\"1\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;width:500px\" class=\"es-table\" role=\"presentation\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td style=\"padding:0;Margin:0\"><strong>Requerente</strong></td>\r\n"
				+ "<td style=\"padding:0;Margin:0\">"+agendamento.getPessoa().getNomeRazaoSocial()+"</td>\r\n"
				+ "</tr>\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td style=\"padding:0;Margin:0\"><strong>CPF/CNPJ</strong></td>\r\n"
				+ "<td style=\"padding:0;Margin:0\">"+agendamento.getPessoa().getCpfCnpj()+"</td>\r\n"
				+ "</tr>\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td style=\"padding:0;Margin:0\"><strong>Identidade/Inscrição Municipal</strong></td>\r\n"
				+ "<td style=\"padding:0;Margin:0\">"+agendamento.getPessoa().getIdentidadeInscricaoMunicipal()+"</td>\r\n"
				+ "</tr>\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td style=\"padding:0;Margin:0\"><strong>Email</strong></td>\r\n"
				+ "<td style=\"padding:0;Margin:0\">"+agendamento.getPessoa().getEmail()+"</td>\r\n"
				+ "</tr>\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td style=\"padding:0;Margin:0\"><strong>Data de Registro</strong></td>\r\n"
				+ "<td style=\"padding:0;Margin:0\">"+(agendamento.getPessoa().getDataRegistro()).format(dateformatter)+"</td>\r\n"
				+ "</tr>\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td style=\"padding:0;Margin:0\"><strong>Telefone ou Celular</strong></td>\r\n"
				+ "<td style=\"padding:0;Margin:0\">"+agendamento.getPessoa().getTelefone()+"</td>\r\n"
				+ "</tr>\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td style=\"padding:0;Margin:0\"><strong>CEP</strong></td>\r\n"
				+ "<td style=\"padding:0;Margin:0\">"+agendamento.getPessoa().getEndereco().getCep()+"</td>\r\n"
				+ "</tr>\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td style=\"padding:0;Margin:0\"><strong>Logradouro</strong></td>\r\n"
				+ "<td style=\"padding:0;Margin:0\">"+agendamento.getPessoa().getEndereco().getLogradouro()+"</td>\r\n"
				+ "</tr>\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td style=\"padding:0;Margin:0\"><strong>Bairro</strong></td>\r\n"
				+ "<td style=\"padding:0;Margin:0\">"+agendamento.getPessoa().getEndereco().getBairro()+"</td>\r\n"
				+ "</tr>\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td style=\"padding:0;Margin:0\"><strong>Cidade</strong></td>\r\n"
				+ "<td style=\"padding:0;Margin:0\">"+agendamento.getPessoa().getEndereco().getLocalidade()+"</td>\r\n"
				+ "</tr>\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td style=\"padding:0;Margin:0\"><strong>UF</strong></td>\r\n"
				+ "<td style=\"padding:0;Margin:0\">"+agendamento.getPessoa().getEndereco().getUf()+"</td>\r\n"
				+ "</tr>\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td style=\"padding:0;Margin:0\"><strong>Número</strong></td>\r\n"
				+ "<td style=\"padding:0;Margin:0\">"+agendamento.getPessoa().getEndereco().getNumero()+"</td>\r\n"
				+ "</tr>\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td style=\"padding:0;Margin:0\"><strong>Complemento</strong></td>\r\n"
				+ "<td style=\"padding:0;Margin:0\">"+agendamento.getPessoa().getEndereco().getComplemento()+"</td>\r\n"
				+ "</tr>\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td style=\"padding:0;Margin:0\"><strong>Descrição</strong></td>\r\n"
				+ "<td style=\"padding:0;Margin:0\">"+agendamento.getDescricao()+"</td>\r\n"
				+ "</tr>\r\n"
				+ "</table><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:'open sans', 'helvetica neue', helvetica, arial, sans-serif;line-height:24px;color:#333333;font-size:16px\"><br></p></td>\r\n"
				+ "</tr>\r\n"
				+ "</table></td>\r\n"
				+ "</tr>\r\n"
				+ "</table></td>\r\n"
				+ "</tr>\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td align=\"left\" style=\"Margin:0;padding-bottom:10px;padding-top:20px;padding-left:35px;padding-right:35px\">\r\n"
				+ "<table cellspacing=\"0\" cellpadding=\"0\" width=\"100%\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td align=\"left\" style=\"padding:0;Margin:0;width:530px\">\r\n"
				+ "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td align=\"left\" style=\"padding:0;Margin:0\"><h4 style=\"Margin:0;line-height:120%;mso-line-height-rule:exactly;font-family:'open sans', 'helvetica neue', helvetica, arial, sans-serif;text-align:center\">Agendamento marcado para:&nbsp;" + formattedDataHoraInicial + " - " + formattedDataHoraFinal+ "</h4></td>\r\n"
				+ "</tr>\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td align=\"left\" style=\"padding:0;Margin:0;padding-top:5px;padding-bottom:15px\"><h5 style=\"Margin:0;line-height:17px;mso-line-height-rule:exactly;font-family:'open sans', 'helvetica neue', helvetica, arial, sans-serif;text-align:center;color:#999999;font-size:14px\">Não esqueça de anotar em sua agenda!</h5></td>\r\n"
				+ "</tr>\r\n"
				+ "</table></td>\r\n"
				+ "</tr>\r\n"
				+ "</table></td>\r\n"
				+ "</tr>\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td align=\"left\" style=\"padding:0;Margin:0;padding-top:10px;padding-bottom:15px\"><!--[if mso]><table style=\"width:600px\" cellpadding=\"0\" cellspacing=\"0\"><tr><td style=\"width:293px\" valign=\"top\"><![endif]-->\r\n"
				+ "<table class=\"es-left\" cellspacing=\"0\" cellpadding=\"0\" align=\"left\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;float:left\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td align=\"left\" style=\"padding:0;Margin:0;width:293px\">\r\n"
				+ "<table style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-position:center center\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td class=\"es-m-txt-c\" align=\"right\" style=\"padding:0;Margin:0;padding-top:15px\"><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:'open sans', 'helvetica neue', helvetica, arial, sans-serif;line-height:24px;color:#666666;font-size:16px\"><strong>Acompanhe:</strong></p></td>\r\n"
				+ "</tr>\r\n"
				+ "</table></td>\r\n"
				+ "</tr>\r\n"
				+ "</table><!--[if mso]></td><td style=\"width:15px\"></td><td style=\"width:292px\" valign=\"top\"><![endif]-->\r\n"
				+ "<table class=\"es-right\" cellspacing=\"0\" cellpadding=\"0\" align=\"right\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;float:right\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td align=\"left\" style=\"padding:0;Margin:0;width:292px\">\r\n"
				+ "<table style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-position:center center\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td class=\"es-m-txt-c\" align=\"left\" style=\"padding:0;Margin:0;padding-bottom:5px;padding-top:10px;font-size:0\">\r\n"
				+ "<table class=\"es-table-not-adapt es-social\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;padding-right:10px\"><a target=\"_blank\" href=\"https://www.facebook.com/PrefeituraTeresopolisOficial/\" style=\"-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;text-decoration:none;color:#ED8E20;font-size:16px\"><img src=\"https://xoaxho.stripocdn.email/content/assets/img/social-icons/rounded-gray/facebook-rounded-gray.png\" alt=\"Fb\" title=\"Facebook\" width=\"32\" style=\"display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic\"></a></td>\r\n"
				+ "<td valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;padding-right:10px\"><a target=\"_blank\" href=\"https://www.instagram.com/prefeiturateresopolis/\" style=\"-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;text-decoration:none;color:#ED8E20;font-size:16px\"><img src=\"https://xoaxho.stripocdn.email/content/assets/img/social-icons/rounded-gray/instagram-rounded-gray.png\" alt=\"Ig\" title=\"Instagram\" width=\"32\" style=\"display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic\"></a></td>\r\n"
				+ "<td valign=\"top\" align=\"center\" style=\"padding:0;Margin:0\"><a target=\"_blank\" href=\"https://www.youtube.com/channel/UC7N7s1Vf4L2i-9Z8uuKhIrA\" style=\"-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;text-decoration:none;color:#ED8E20;font-size:16px\"><img src=\"https://xoaxho.stripocdn.email/content/assets/img/social-icons/rounded-gray/youtube-rounded-gray.png\" alt=\"Yt\" title=\"Youtube\" width=\"32\" style=\"display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic\"></a></td>\r\n"
				+ "</tr>\r\n"
				+ "</table></td>\r\n"
				+ "</tr>\r\n"
				+ "</table></td>\r\n"
				+ "</tr>\r\n"
				+ "</table><!--[if mso]></td></tr></table><![endif]--></td>\r\n"
				+ "</tr>\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td align=\"left\" bgcolor=\"#2D5299\" style=\"padding:0;Margin:0;padding-bottom:5px;padding-top:15px;padding-left:15px;background-color:#2d5299\"><!--[if mso]><table style=\"width:585px\" cellpadding=\"0\" cellspacing=\"0\"><tr><td style=\"width:209px\" valign=\"top\"><![endif]-->\r\n"
				+ "<table cellpadding=\"0\" cellspacing=\"0\" class=\"es-left\" align=\"left\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;float:left\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td class=\"es-m-p20b\" align=\"left\" style=\"padding:0;Margin:0;width:169px\">\r\n"
				+ "<table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td align=\"center\" style=\"padding:0;Margin:0;font-size:0px\"><a target=\"_blank\" href=\"https://www.teresopolis.rj.gov.br/\" style=\"-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;text-decoration:none;color:#ED8E20;font-size:16px\"><img class=\"adapt-img\" src=\"https://xoaxho.stripocdn.email/content/guids/CABINET_69b1a3497d22bf502b8ddc663cc9661982183c44c243cdfd69b90917a432e3a8/images/logopmtbranca_YAf.png\" alt style=\"display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic\" height=\"61\"></a></td>\r\n"
				+ "</tr>\r\n"
				+ "</table></td>\r\n"
				+ "<td class=\"es-hidden\" style=\"padding:0;Margin:0;width:40px\"></td>\r\n"
				+ "</tr>\r\n"
				+ "</table><!--[if mso]></td><td style=\"width:168px\" valign=\"top\"><![endif]-->\r\n"
				+ "<table cellpadding=\"0\" cellspacing=\"0\" class=\"es-left\" align=\"left\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;float:left\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td align=\"left\" class=\"es-m-p20b\" style=\"padding:0;Margin:0;width:168px\">\r\n"
				+ "<table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td align=\"center\" style=\"padding:0;Margin:0;font-size:0px\"><a target=\"_blank\" href=\"https://www.teresopolis.rj.gov.br/estrutura/ciencia-e-tecnologia/teregovdigital/\" style=\"-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;text-decoration:none;color:#ED8E20;font-size:16px\"><img class=\"adapt-img\" src=\"https://xoaxho.stripocdn.email/content/guids/CABINET_69b1a3497d22bf502b8ddc663cc9661982183c44c243cdfd69b90917a432e3a8/images/teredigital_iGK.png\" alt style=\"display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic\" width=\"141.531\"></a></td>\r\n"
				+ "</tr>\r\n"
				+ "</table></td>\r\n"
				+ "</tr>\r\n"
				+ "</table><!--[if mso]></td><td style=\"width:40px\"></td><td style=\"width:168px\" valign=\"top\"><![endif]-->\r\n"
				+ "<table cellpadding=\"0\" cellspacing=\"0\" class=\"es-right\" align=\"right\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;float:right\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td align=\"left\" style=\"padding:0;Margin:0;width:168px\">\r\n"
				+ "<table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td align=\"center\" style=\"padding:0;Margin:0;font-size:0px\"><a target=\"_blank\" href=\"https://www.teresopolis.rj.gov.br/transparencia/esic-fisico/\" style=\"-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;text-decoration:none;color:#ED8E20;font-size:16px\"><img class=\"adapt-img\" src=\"https://xoaxho.stripocdn.email/content/guids/CABINET_69b1a3497d22bf502b8ddc663cc9661982183c44c243cdfd69b90917a432e3a8/images/logoacessoainformacaohorizontalbranca.png\" alt style=\"display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic\" height=\"45\"></a></td>\r\n"
				+ "</tr>\r\n"
				+ "</table></td>\r\n"
				+ "</tr>\r\n"
				+ "</table><!--[if mso]></td></tr></table><![endif]--></td>\r\n"
				+ "</tr>\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td align=\"left\" style=\"padding:0;Margin:0;padding-top:15px\">\r\n"
				+ "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;width:600px\">\r\n"
				+ "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td style=\"padding:0;Margin:0\">\r\n"
				+ "<table class=\"es-menu\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\r\n"
				+ "<tr class=\"links\" style=\"border-collapse:collapse\">\r\n"
				+ "<td style=\"Margin:0;padding-left:5px;padding-right:5px;padding-top:0px;padding-bottom:1px;border:0\" width=\"33.33%\" valign=\"top\" bgcolor=\"transparent\" align=\"center\"><a target=\"_blank\" href=\"https://www.teresopolis.rj.gov.br/transparencia/\" style=\"-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;text-decoration:none;display:block;font-family:'open sans', 'helvetica neue', helvetica, arial, sans-serif;color:#2d5299;font-size:14px\">Transparencia</a></td>\r\n"
				+ "<td style=\"Margin:0;padding-left:5px;padding-right:5px;padding-top:0px;padding-bottom:1px;border:0;border-left:1px solid #2d5299\" width=\"33.33%\" valign=\"top\" bgcolor=\"transparent\" align=\"center\"><a target=\"_blank\" href=\"https://www.teresopolis.rj.gov.br/\" style=\"-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;text-decoration:none;display:block;font-family:'open sans', 'helvetica neue', helvetica, arial, sans-serif;color:#2d5299;font-size:14px\">Site</a></td>\r\n"
				+ "<td style=\"Margin:0;padding-left:5px;padding-right:5px;padding-top:0px;padding-bottom:1px;border:0;border-left:1px solid #2d5299\" width=\"33.33%\" valign=\"top\" bgcolor=\"transparent\" align=\"center\"><a target=\"_blank\" href=\"https://www.teresopolis.rj.gov.br/transparencia/esic-fisico/\" style=\"-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;text-decoration:none;display:block;font-family:'open sans', 'helvetica neue', helvetica, arial, sans-serif;color:#2d5299;font-size:14px\">Contato</a></td>\r\n"
				+ "</tr>\r\n"
				+ "</table></td>\r\n"
				+ "</tr>\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td align=\"center\" style=\"padding:0;Margin:0;padding-bottom:20px;padding-left:20px;padding-right:20px;font-size:0\">\r\n"
				+ "<table width=\"100%\" height=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td style=\"padding:0;Margin:0;border-bottom:1px solid #fafafa;background:none;height:1px;width:100%;margin:0px\"></td>\r\n"
				+ "</tr>\r\n"
				+ "</table></td>\r\n"
				+ "</tr>\r\n"
				+ "</table></td>\r\n"
				+ "</tr>\r\n"
				+ "</table></td>\r\n"
				+ "</tr>\r\n"
				+ "</table></td>\r\n"
				+ "</tr>\r\n"
				+ "</table></td>\r\n"
				+ "</tr>\r\n"
				+ "</table>\r\n"
				+ "</div>\r\n"
				+ "</body>\r\n"
				+ "</html>";

		MimeBodyPart textPart = new MimeBodyPart();
		try {
			textPart.setContent(emailMessage, "text/html");
		} catch (MessagingException e) {
			e.printStackTrace();
		}

		return textPart;
	}

	private MimeBodyPart gerarEmailMessage(Agendamento agendamento) {
		String emailMessage;
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");

		LocalDateTime dataHoraInicial = agendamento.getHoraInicial();
		LocalDateTime dataHoraFinal = agendamento.getHoraFinal();

		String formattedDataHoraInicial = dataHoraInicial.format(formatter);
		String formattedDataHoraFinal = dataHoraFinal.format(formatter);

		emailMessage = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\r\n"
				+ "<html xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:o=\"urn:schemas-microsoft-com:office:office\" style=\"padding:0;Margin:0\">\r\n"
				+ "<head>\r\n"
				+ "<meta charset=\"UTF-8\">\r\n"
				+ "<meta content=\"width=device-width, initial-scale=1\" name=\"viewport\">\r\n"
				+ "<meta name=\"x-apple-disable-message-reformatting\">\r\n"
				+ "<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\r\n"
				+ "<meta content=\"telephone=no\" name=\"format-detection\">\r\n"
				+ "<title>Envio para Cliente</title><!--[if (mso 16)]>\r\n"
				+ "<style type=\"text/css\">\r\n"
				+ "a {text-decoration: none;}\r\n"
				+ "</style>\r\n"
				+ "<![endif]--><!--[if gte mso 9]><style>sup { font-size: 100% !important; }</style><![endif]--><!--[if gte mso 9]>\r\n"
				+ "<xml>\r\n"
				+ "<o:OfficeDocumentSettings>\r\n"
				+ "<o:AllowPNG></o:AllowPNG>\r\n"
				+ "<o:PixelsPerInch>96</o:PixelsPerInch>\r\n"
				+ "</o:OfficeDocumentSettings>\r\n"
				+ "</xml>\r\n"
				+ "<![endif]-->\r\n"
				+ "<style type=\"text/css\">\r\n"
				+ "#outlook a {\r\n"
				+ "padding:0;\r\n"
				+ "}\r\n"
				+ ".ExternalClass {\r\n"
				+ "width:100%;\r\n"
				+ "}\r\n"
				+ ".ExternalClass,\r\n"
				+ ".ExternalClass p,\r\n"
				+ ".ExternalClass span,\r\n"
				+ ".ExternalClass font,\r\n"
				+ ".ExternalClass td,\r\n"
				+ ".ExternalClass div {\r\n"
				+ "line-height:100%;\r\n"
				+ "}\r\n"
				+ ".es-button {\r\n"
				+ "mso-style-priority:100!important;\r\n"
				+ "text-decoration:none!important;\r\n"
				+ "}\r\n"
				+ "a[x-apple-data-detectors] {\r\n"
				+ "color:inherit!important;\r\n"
				+ "text-decoration:none!important;\r\n"
				+ "font-size:inherit!important;\r\n"
				+ "font-family:inherit!important;\r\n"
				+ "font-weight:inherit!important;\r\n"
				+ "line-height:inherit!important;\r\n"
				+ "}\r\n"
				+ ".es-desk-hidden {\r\n"
				+ "display:none;\r\n"
				+ "float:left;\r\n"
				+ "overflow:hidden;\r\n"
				+ "width:0;\r\n"
				+ "max-height:0;\r\n"
				+ "line-height:0;\r\n"
				+ "mso-hide:all;\r\n"
				+ "}\r\n"
				+ ".es-button-border:hover a.es-button, .es-button-border:hover button.es-button {\r\n"
				+ "background:#ffffff!important;\r\n"
				+ "}\r\n"
				+ ".es-button-border:hover {\r\n"
				+ "background:#ffffff!important;\r\n"
				+ "border-style:solid solid solid solid!important;\r\n"
				+ "border-color:#3d5ca3 #3d5ca3 #3d5ca3 #3d5ca3!important;\r\n"
				+ "}\r\n"
				+ "@media only screen and (max-width:600px) {p, ul li, ol li, a { line-height:150%!important } h1, h2, h3, h1 a, h2 a, h3 a { line-height:120%!important } h1 { font-size:20px!important; text-align:center } h2 { font-size:16px!important; text-align:left } h3 { font-size:20px!important; text-align:center } .es-header-body h1 a, .es-content-body h1 a, .es-footer-body h1 a { font-size:20px!important } h2 a { text-align:left } .es-header-body h2 a, .es-content-body h2 a, .es-footer-body h2 a { font-size:16px!important } .es-header-body h3 a, .es-content-body h3 a, .es-footer-body h3 a { font-size:20px!important } .es-menu td a { font-size:14px!important } .es-header-body p, .es-header-body ul li, .es-header-body ol li, .es-header-body a { font-size:10px!important } .es-content-body p, .es-content-body ul li, .es-content-body ol li, .es-content-body a { font-size:16px!important } .es-footer-body p, .es-footer-body ul li, .es-footer-body ol li, .es-footer-body a { font-size:12px!important } .es-infoblock p, .es-infoblock ul li, .es-infoblock ol li, .es-infoblock a { font-size:12px!important } *[class=\"gmail-fix\"] { display:none!important } .es-m-txt-c, .es-m-txt-c h1, .es-m-txt-c h2, .es-m-txt-c h3 { text-align:center!important } .es-m-txt-r, .es-m-txt-r h1, .es-m-txt-r h2, .es-m-txt-r h3 { text-align:right!important } .es-m-txt-l, .es-m-txt-l h1, .es-m-txt-l h2, .es-m-txt-l h3 { text-align:left!important } .es-m-txt-r img, .es-m-txt-c img, .es-m-txt-l img { display:inline!important } .es-button-border { display:block!important } a.es-button, button.es-button { font-size:14px!important; display:block!important; border-left-width:0px!important; border-right-width:0px!important } .es-btn-fw { border-width:10px 0px!important; text-align:center!important } .es-adaptive table, .es-btn-fw, .es-btn-fw-brdr, .es-left, .es-right { width:100%!important } .es-content table, .es-header table, .es-footer table, .es-content, .es-footer, .es-header { width:100%!important; max-width:600px!important } .es-adapt-td { display:block!important; width:100%!important } .adapt-img { width:100%!important; height:auto!important } .es-m-p0 { padding:0px!important } .es-m-p0r { padding-right:0px!important } .es-m-p0l { padding-left:0px!important } .es-m-p0t { padding-top:0px!important } .es-m-p0b { padding-bottom:0!important } .es-m-p20b { padding-bottom:20px!important } .es-mobile-hidden, .es-hidden { display:none!important } tr.es-desk-hidden, td.es-desk-hidden, table.es-desk-hidden { width:auto!important; overflow:visible!important; float:none!important; max-height:inherit!important; line-height:inherit!important } tr.es-desk-hidden { display:table-row!important } table.es-desk-hidden { display:table!important } td.es-desk-menu-hidden { display:table-cell!important } .es-menu td { width:1%!important } table.es-table-not-adapt, .esd-block-html table { width:auto!important } table.es-social { display:inline-block!important } table.es-social td { display:inline-block!important } .es-desk-hidden { display:table-row!important; width:auto!important; overflow:visible!important; max-height:inherit!important } .h-auto { height:auto!important } }\r\n"
				+ "</style>\r\n"
				+ "</head>\r\n"
				+ "<body data-new-gr-c-s-loaded=\"14.1115.0\" style=\"width:100%;font-family:helvetica, 'helvetica neue', arial, verdana, sans-serif;-webkit-text-size-adjust:100%;-ms-text-size-adjust:100%;padding:0;Margin:0\">\r\n"
				+ "<div class=\"es-wrapper-color\" style=\"background-color:#FAFAFA\"><!--[if gte mso 9]>\r\n"
				+ "<v:background xmlns:v=\"urn:schemas-microsoft-com:vml\" fill=\"t\">\r\n"
				+ "<v:fill type=\"tile\" color=\"#fafafa\"></v:fill>\r\n"
				+ "</v:background>\r\n"
				+ "<![endif]-->\r\n"
				+ "<table class=\"es-wrapper\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;padding:0;Margin:0;width:100%;height:100%;background-repeat:repeat;background-position:center top;background-color:#FAFAFA\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td valign=\"top\" style=\"padding:0;Margin:0\">\r\n"
				+ "<table cellpadding=\"0\" cellspacing=\"0\" class=\"es-content\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td class=\"es-adaptive\" align=\"center\" style=\"padding:0;Margin:0\">\r\n"
				+ "<table class=\"es-content-body\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:transparent;width:600px\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#ffffff\" align=\"center\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td align=\"left\" style=\"padding:10px;Margin:0\">\r\n"
				+ "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;width:580px\">\r\n"
				+ "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td align=\"center\" class=\"es-infoblock\" style=\"padding:0;Margin:0;line-height:14px;font-size:12px;color:#CCCCCC\"><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:helvetica, 'helvetica neue', arial, verdana, sans-serif;line-height:14px;color:#CCCCCC;font-size:12px\">Seu agendamento foi realizado com sucesso, anote na agenda. Clique <a href=\"https://viewstripo.email\" class=\"view\" target=\"_blank\" style=\"-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;text-decoration:none;color:#CCCCCC;font-size:12px\">aqui</a> para visualizar no navegador.</p></td>\r\n"
				+ "</tr>\r\n"
				+ "</table></td>\r\n"
				+ "</tr>\r\n"
				+ "</table></td>\r\n"
				+ "</tr>\r\n"
				+ "</table></td>\r\n"
				+ "</tr>\r\n"
				+ "</table>\r\n"
				+ "<table cellpadding=\"0\" cellspacing=\"0\" class=\"es-header\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%;background-color:transparent;background-repeat:repeat;background-position:center top\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td class=\"es-adaptive\" align=\"center\" style=\"padding:0;Margin:0\">\r\n"
				+ "<table class=\"es-header-body\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#3d5ca3;width:600px\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#3d5ca3\" align=\"center\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td style=\"Margin:0;padding-top:20px;padding-bottom:20px;padding-left:20px;padding-right:20px;background-color:#2d5299\" bgcolor=\"#2D5299\" align=\"left\">\r\n"
				+ "<table cellspacing=\"0\" cellpadding=\"0\" width=\"100%\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td align=\"left\" style=\"padding:0;Margin:0;width:560px\">\r\n"
				+ "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td class=\"es-m-p0l es-m-txt-c\" align=\"center\" style=\"padding:0;Margin:0;font-size:0px\"><a href=\"https://viewstripo.email\" target=\"_blank\" style=\"-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;text-decoration:none;color:#1376C8;font-size:14px\"><img src=\"https://xoaxho.stripocdn.email/content/guids/CABINET_69b1a3497d22bf502b8ddc663cc9661982183c44c243cdfd69b90917a432e3a8/images/logopmtbranca.png\" alt style=\"display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic\" width=\"162\"></a></td>\r\n"
				+ "</tr>\r\n"
				+ "</table></td>\r\n"
				+ "</tr>\r\n"
				+ "</table></td>\r\n"
				+ "</tr>\r\n"
				+ "</table></td>\r\n"
				+ "</tr>\r\n"
				+ "</table>\r\n"
				+ "<table class=\"es-content\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td style=\"padding:0;Margin:0;background-color:#fafafa\" bgcolor=\"#fafafa\" align=\"center\">\r\n"
				+ "<table class=\"es-content-body\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#ffffff;width:600px\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#ffffff\" align=\"center\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td style=\"padding:0;Margin:0;padding-left:20px;padding-right:20px;padding-top:40px;background-color:transparent;background-position:left top\" bgcolor=\"transparent\" align=\"left\">\r\n"
				+ "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;width:560px\">\r\n"
				+ "<table style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-position:left top\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td align=\"center\" style=\"padding:0;Margin:0;padding-top:5px;padding-bottom:5px;font-size:0px\"><img src=\"https://xoaxho.stripocdn.email/content/guids/CABINET_69b1a3497d22bf502b8ddc663cc9661982183c44c243cdfd69b90917a432e3a8/images/assignment.png\" alt style=\"display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic\" width=\"175\"></td>\r\n"
				+ "</tr>\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td align=\"center\" style=\"padding:0;Margin:0;padding-top:15px\"><h1 style=\"Margin:0;line-height:24px;mso-line-height-rule:exactly;font-family:arial, 'helvetica neue', helvetica, sans-serif;font-size:20px;font-style:normal;font-weight:normal;color:#333333\"><strong>Agendamento realizado com sucesso!</strong></h1></td>\r\n"
				+ "</tr>\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td align=\"center\" style=\"padding:0;Margin:0;padding-bottom:20px;padding-left:40px;padding-right:40px\"><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:helvetica, 'helvetica neue', arial, verdana, sans-serif;line-height:24px;color:#999999;font-size:16px\">Protocolo "+agendamento.getAgendamentoId()+"</p></td>\r\n"
				+ "</tr>\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td align=\"center\" style=\"padding:0;Margin:0;padding-left:40px;padding-right:40px\"><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:helvetica, 'helvetica neue', arial, verdana, sans-serif;line-height:24px;color:#666666;font-size:16px\">Olá,&nbsp;"+agendamento.getPessoa().getNomeRazaoSocial()+"</p></td>\r\n"
				+ "</tr>\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td align=\"center\" style=\"padding:0;Margin:0;padding-right:35px;padding-left:40px\"><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:helvetica, 'helvetica neue', arial, verdana, sans-serif;line-height:24px;color:#666666;font-size:16px\">Seu agendamento para o serviço "+ agendamento.getServico().getNome()+" foi realizado com sucesso!</p></td>\r\n"
				+ "</tr>\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td align=\"center\" style=\"padding:0;Margin:0;padding-top:25px;padding-left:40px;padding-right:40px\"><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:helvetica, 'helvetica neue', arial, verdana, sans-serif;line-height:24px;color:#666666;font-size:16px\">Está marcado para o dia <strong>"+ formattedDataHoraInicial + " - " + formattedDataHoraFinal+". Não esqueça de levar seus documentos.</strong></p></td>\r\n"
				+ "</tr>\r\n"
				+ "</table></td>\r\n"
				+ "</tr>\r\n"
				+ "</table></td>\r\n"
				+ "</tr>\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td align=\"left\" style=\"padding:0;Margin:0;padding-top:25px;padding-bottom:25px\"><!--[if mso]><table style=\"width:600px\" cellpadding=\"0\" cellspacing=\"0\"><tr><td style=\"width:293px\" valign=\"top\"><![endif]-->\r\n"
				+ "<table class=\"es-left\" cellspacing=\"0\" cellpadding=\"0\" align=\"left\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;float:left\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td align=\"left\" style=\"padding:0;Margin:0;width:293px\">\r\n"
				+ "<table style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-position:center center\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td class=\"es-m-txt-c\" align=\"right\" style=\"padding:0;Margin:0;padding-top:15px\"><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:helvetica, 'helvetica neue', arial, verdana, sans-serif;line-height:24px;color:#666666;font-size:16px\"><strong>Acompanhe:</strong></p></td>\r\n"
				+ "</tr>\r\n"
				+ "</table></td>\r\n"
				+ "</tr>\r\n"
				+ "</table><!--[if mso]></td><td style=\"width:15px\"></td><td style=\"width:292px\" valign=\"top\"><![endif]-->\r\n"
				+ "<table class=\"es-right\" cellspacing=\"0\" cellpadding=\"0\" align=\"right\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;float:right\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td align=\"left\" style=\"padding:0;Margin:0;width:292px\">\r\n"
				+ "<table style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-position:center center\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td class=\"es-m-txt-c\" align=\"left\" style=\"padding:0;Margin:0;padding-bottom:5px;padding-top:10px;font-size:0\">\r\n"
				+ "<table class=\"es-table-not-adapt es-social\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;padding-right:10px\"><a target=\"_blank\" href=\"https://www.facebook.com/PrefeituraTeresopolisOficial/\" style=\"-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;text-decoration:none;color:#0B5394;font-size:16px\"><img src=\"https://xoaxho.stripocdn.email/content/assets/img/social-icons/rounded-gray/facebook-rounded-gray.png\" alt=\"Fb\" title=\"Facebook\" width=\"32\" style=\"display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic\"></a></td>\r\n"
				+ "<td valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;padding-right:10px\"><a target=\"_blank\" href=\"https://www.instagram.com/prefeiturateresopolis/\" style=\"-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;text-decoration:none;color:#0B5394;font-size:16px\"><img src=\"https://xoaxho.stripocdn.email/content/assets/img/social-icons/rounded-gray/instagram-rounded-gray.png\" alt=\"Ig\" title=\"Instagram\" width=\"32\" style=\"display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic\"></a></td>\r\n"
				+ "<td valign=\"top\" align=\"center\" style=\"padding:0;Margin:0\"><a target=\"_blank\" href=\"https://www.youtube.com/channel/UC7N7s1Vf4L2i-9Z8uuKhIrA\" style=\"-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;text-decoration:none;color:#0B5394;font-size:16px\"><img src=\"https://xoaxho.stripocdn.email/content/assets/img/social-icons/rounded-gray/youtube-rounded-gray.png\" alt=\"Yt\" title=\"Youtube\" width=\"32\" style=\"display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic\"></a></td>\r\n"
				+ "</tr>\r\n"
				+ "</table></td>\r\n"
				+ "</tr>\r\n"
				+ "</table></td>\r\n"
				+ "</tr>\r\n"
				+ "</table><!--[if mso]></td></tr></table><![endif]--></td>\r\n"
				+ "</tr>\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td align=\"left\" style=\"Margin:0;padding-top:5px;padding-bottom:15px;padding-left:20px;padding-right:20px\">\r\n"
				+ "<table cellspacing=\"0\" cellpadding=\"0\" width=\"100%\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;width:560px\">\r\n"
				+ "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td align=\"center\" style=\"padding:0;Margin:0\"><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:helvetica, 'helvetica neue', arial, verdana, sans-serif;line-height:14px;color:#666666;font-size:14px\">Av. Feliciano Sodré, 675 - Várzea, Teresópolis - RJ, 25963-083<br><br></p></td>\r\n"
				+ "</tr>\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td align=\"center\" style=\"padding:0;Margin:0\"><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:helvetica, 'helvetica neue', arial, verdana, sans-serif;line-height:21px;color:#666666;font-size:14px\">Município de Teresópolis - CNPJ: 29.138.369/0001-47</p></td>\r\n"
				+ "</tr>\r\n"
				+ "</table></td>\r\n"
				+ "</tr>\r\n"
				+ "</table></td>\r\n"
				+ "</tr>\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td class=\"esdev-adapt-off\" align=\"left\" bgcolor=\"#2D5299\" style=\"padding:0;Margin:0;padding-bottom:5px;padding-top:15px;padding-left:15px;background-color:#2d5299\">\r\n"
				+ "<table cellpadding=\"0\" cellspacing=\"0\" class=\"esdev-mso-table\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;width:585px\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td class=\"esdev-mso-td\" valign=\"top\" style=\"padding:0;Margin:0\">\r\n"
				+ "<table cellpadding=\"0\" cellspacing=\"0\" class=\"es-left\" align=\"left\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;float:left\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td align=\"left\" style=\"padding:0;Margin:0;width:169px\">\r\n"
				+ "<table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td align=\"center\" style=\"padding:0;Margin:0;font-size:0px\"><a target=\"_blank\" href=\"https://www.teresopolis.rj.gov.br/\" style=\"-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;text-decoration:none;color:#0B5394;font-size:16px\"><img class=\"adapt-img\" src=\"https://xoaxho.stripocdn.email/content/guids/CABINET_69b1a3497d22bf502b8ddc663cc9661982183c44c243cdfd69b90917a432e3a8/images/logopmtbranca_YAf.png\" alt style=\"display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic\" height=\"61\"></a></td>\r\n"
				+ "</tr>\r\n"
				+ "</table></td>\r\n"
				+ "</tr>\r\n"
				+ "</table></td>\r\n"
				+ "<td style=\"padding:0;Margin:0;width:40px\"></td>\r\n"
				+ "<td class=\"esdev-mso-td\" valign=\"top\" style=\"padding:0;Margin:0\">\r\n"
				+ "<table cellpadding=\"0\" cellspacing=\"0\" class=\"es-left\" align=\"left\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;float:left\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td align=\"left\" style=\"padding:0;Margin:0;width:168px\">\r\n"
				+ "<table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td align=\"center\" style=\"padding:0;Margin:0;font-size:0px\"><a target=\"_blank\" href=\"https://www.teresopolis.rj.gov.br/estrutura/ciencia-e-tecnologia/teregovdigital/\" style=\"-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;text-decoration:none;color:#0B5394;font-size:16px\"><img class=\"adapt-img\" src=\"https://xoaxho.stripocdn.email/content/guids/CABINET_69b1a3497d22bf502b8ddc663cc9661982183c44c243cdfd69b90917a432e3a8/images/teredigital_iGK.png\" alt style=\"display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic\" width=\"141.531\"></a></td>\r\n"
				+ "</tr>\r\n"
				+ "</table></td>\r\n"
				+ "</tr>\r\n"
				+ "</table></td>\r\n"
				+ "<td style=\"padding:0;Margin:0;width:40px\"></td>\r\n"
				+ "<td class=\"esdev-mso-td\" valign=\"top\" style=\"padding:0;Margin:0\">\r\n"
				+ "<table cellpadding=\"0\" cellspacing=\"0\" class=\"es-right\" align=\"right\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;float:right\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td align=\"left\" style=\"padding:0;Margin:0;width:168px\">\r\n"
				+ "<table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td align=\"center\" style=\"padding:0;Margin:0;font-size:0px\"><a target=\"_blank\" href=\"https://www.teresopolis.rj.gov.br/transparencia/esic-fisico/\" style=\"-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;text-decoration:none;color:#0B5394;font-size:16px\"><img class=\"adapt-img\" src=\"https://xoaxho.stripocdn.email/content/guids/CABINET_69b1a3497d22bf502b8ddc663cc9661982183c44c243cdfd69b90917a432e3a8/images/logoacessoainformacaohorizontalbranca.png\" alt style=\"display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic\" height=\"45\"></a></td>\r\n"
				+ "</tr>\r\n"
				+ "</table></td>\r\n"
				+ "</tr>\r\n"
				+ "</table></td>\r\n"
				+ "</tr>\r\n"
				+ "</table></td>\r\n"
				+ "</tr>\r\n"
				+ "</table></td>\r\n"
				+ "</tr>\r\n"
				+ "</table>\r\n"
				+ "<table class=\"es-content\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td style=\"padding:0;Margin:0;background-color:#fafafa\" bgcolor=\"#fafafa\" align=\"center\">\r\n"
				+ "<table class=\"es-content-body\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:transparent;width:600px\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"transparent\" align=\"center\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td align=\"left\" style=\"padding:0;Margin:0;padding-top:15px\">\r\n"
				+ "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;width:600px\">\r\n"
				+ "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td style=\"padding:0;Margin:0\">\r\n"
				+ "<table class=\"es-menu\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\r\n"
				+ "<tr class=\"links\" style=\"border-collapse:collapse\">\r\n"
				+ "<td style=\"Margin:0;padding-left:5px;padding-right:5px;padding-top:0px;padding-bottom:1px;border:0\" width=\"33.33%\" valign=\"top\" bgcolor=\"transparent\" align=\"center\"><a target=\"_blank\" href=\"https://www.teresopolis.rj.gov.br/transparencia/\" style=\"-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;text-decoration:none;display:block;font-family:helvetica, 'helvetica neue', arial, verdana, sans-serif;color:#2d5299;font-size:14px\">Transparencia</a></td>\r\n"
				+ "<td style=\"Margin:0;padding-left:5px;padding-right:5px;padding-top:0px;padding-bottom:1px;border:0;border-left:1px solid #2d5299\" width=\"33.33%\" valign=\"top\" bgcolor=\"transparent\" align=\"center\"><a target=\"_blank\" href=\"https://www.teresopolis.rj.gov.br/\" style=\"-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;text-decoration:none;display:block;font-family:helvetica, 'helvetica neue', arial, verdana, sans-serif;color:#2d5299;font-size:14px\">Site</a></td>\r\n"
				+ "<td style=\"Margin:0;padding-left:5px;padding-right:5px;padding-top:0px;padding-bottom:1px;border:0;border-left:1px solid #2d5299\" width=\"33.33%\" valign=\"top\" bgcolor=\"transparent\" align=\"center\"><a target=\"_blank\" href=\"https://www.teresopolis.rj.gov.br/transparencia/esic-fisico/\" style=\"-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;text-decoration:none;display:block;font-family:helvetica, 'helvetica neue', arial, verdana, sans-serif;color:#2d5299;font-size:14px\">Contato</a></td>\r\n"
				+ "</tr>\r\n"
				+ "</table></td>\r\n"
				+ "</tr>\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td align=\"center\" style=\"padding:0;Margin:0;padding-bottom:20px;padding-left:20px;padding-right:20px;font-size:0\">\r\n"
				+ "<table width=\"100%\" height=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\r\n"
				+ "<tr style=\"border-collapse:collapse\">\r\n"
				+ "<td style=\"padding:0;Margin:0;border-bottom:1px solid #fafafa;background:none;height:1px;width:100%;margin:0px\"></td>\r\n"
				+ "</tr>\r\n"
				+ "</table></td>\r\n"
				+ "</tr>\r\n"
				+ "</table></td>\r\n"
				+ "</tr>\r\n"
				+ "</table></td>\r\n"
				+ "</tr>\r\n"
				+ "</table></td>\r\n"
				+ "</tr>\r\n"
				+ "</table></td>\r\n"
				+ "</tr>\r\n"
				+ "</table>\r\n"
				+ "</div>\r\n"
				+ "</body>\r\n"
				+ "</html>";
		
		MimeBodyPart textPart = new MimeBodyPart();
		try {
			textPart.setContent(emailMessage, "text/html");
		} catch (MessagingException e) {
			e.printStackTrace();
		}

		return textPart;
	}

}
