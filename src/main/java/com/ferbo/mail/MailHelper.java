package com.ferbo.mail;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.naming.NamingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ferbo.mail.beans.Adjunto;
import com.ferbo.mail.beans.Correo;
import com.ferbo.tools.JndiManager;
import com.ferbo.tools.MailException;
import com.ferbo.tools.SmtpAuthenticator;

public class MailHelper {
    
    public static final String JNDI_MAIL_FACTURACION = "mail/facturacion";
    public static final String JNDI_MAIL_INVENTARIO = "mail/inventarios";
    public static final String JNDI_MAIL_AVISOS = "mail/notificaciones";
    
	private static Logger log = LogManager.getLogger(MailHelper.class);
	private List<Correo> alTo = null;
	private List<Correo> alCC = null;
	private List<Correo> alBCC = null;
	private List<Adjunto> alAttachtments = null;
	private List<InternetAddress> alReplyTo = null;
	private String mailBody = null;
	private String subject = null;
	private Transport transport = null;
	private Session session = null;
	private List<Message> messageList = null;
	
	private JndiManager dsManager = null;
    String      replyToName = null;
    String      replyToMail = null;
	Properties serverProperties = null;
	
	public MailHelper() {
		this.dsManager = new JndiManager();
		this.newMessage();
	}
	
	public MailHelper(String jndiName) {
	    this.newMessage();
        Session session = null;
        String      user = null;
        String      password = null;
        SmtpAuthenticator authenticator = null;
        
        try {
        	this.dsManager = new JndiManager();
            this.serverProperties = this.dsManager.getServerProperties(jndiName);
            user = this.serverProperties.getProperty("mail.smtp.user");
            password = this.serverProperties.getProperty("mail.smtp.password");
            this.replyToName = this.serverProperties.getProperty("mail.replayTo.name");//Propiedad no estándar de java mail session
            this.replyToMail = this.serverProperties.getProperty("mail.replayTo.mail");//Propiedad no estándar de java mail session
            
            log.debug("Estableciendo autenticador...");
            authenticator = new SmtpAuthenticator();
            authenticator.setUserName(user);
            authenticator.setPassword(password);
            session = Session.getInstance(this.serverProperties, authenticator);
            this.session = session;
            this.transport = session.getTransport("smtp");
            this.messageList = new ArrayList<Message>();
            this.newMessage();
        } catch (NamingException | NoSuchProviderException ex) {
            log.error("Problema al generar el objeto MailHelper... ", ex);
        }
	}
	
	public MailHelper(Session session) {
	    this.newMessage();
	    this.dsManager = new JndiManager();
        this.session = session;
        this.messageList = new ArrayList<Message>();
	}
	
	public void newMessage() {
	    this.alTo = new ArrayList<Correo>();
        this.alCC = new ArrayList<Correo>();
        this.alBCC = new ArrayList<Correo>();
        this.alReplyTo = new ArrayList<InternetAddress>();
        this.alAttachtments = new ArrayList<Adjunto>();
	}
    
	public void addTo(Correo bean) {
		this.alTo.add(bean);
	}
	
	public void addCC(Correo bean) {
		this.alCC.add(bean);
	}
	
	public void addBCC(Correo bean) {
		this.alBCC.add(bean);
	}
	
	public void addAttachment(Adjunto bean) {
		this.alAttachtments.add(bean);
	}
	
	public List<Adjunto> getAttachmentList() {
	    return this.alAttachtments;
	}
	
	public void sendMessage() throws MailException {
		String defaultJndiName = JNDI_MAIL_AVISOS;
		this.sendMessage(defaultJndiName);
	}
	
	public void addMessage() {
	    
	    Message message = null;
	    String user = null;
	    InternetAddress[]   aReplyTo = null;
	    String      replyToName = null;
        String      replyToMail = null;
        
        try {
            message = new MimeMessage(this.session);
            message.setFrom(new InternetAddress(this.serverProperties.getProperty("mail.from"), this.serverProperties.getProperty("mail.from.name")));
            message.setSubject(this.subject);
            message.setContent(this.mailBody, "text/html; charset=UTF-8");
            
            if(replyToMail != null || "".equalsIgnoreCase(replyToMail) == false) {
                this.alReplyTo.add(new InternetAddress(replyToMail, replyToName));
            }
            
            if(this.alReplyTo.size() > 0) {
                aReplyTo = new InternetAddress[this.alReplyTo.size()];
                aReplyTo = (InternetAddress[]) this.alReplyTo.toArray(aReplyTo);
                message.setReplyTo(aReplyTo);
                log.info("Agregando cuenta responder a: " + aReplyTo);
            }
            
            user = this.serverProperties.getProperty("mail.smtp.user");
            
            if(aReplyTo == null || aReplyTo.length > 0) {
                message.addHeader("Disposition-Notification-To", aReplyTo[0].getAddress());
            } else {
                message.addHeader("Disposition-Notification-To", user);
            }
            log.info("Estableciendo solicitud de acuse de recibo: " + message.getHeader("Disposition-Notification-To"));
            
            for(Correo bean : this.alTo) {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(bean.getMail(), bean.getNombreBuzon()));
                log.info("Buzón: " + bean);
            }
            
            for(Correo bean : this.alCC) {
                message.addRecipient(Message.RecipientType.CC, new InternetAddress(bean.getMail(), bean.getNombreBuzon()));
                log.info("Buzón: " + bean);
            }
            
            for(Correo bean : this.alBCC) {
                message.addRecipient(Message.RecipientType.BCC, new InternetAddress(bean.getMail(), bean.getNombreBuzon()));
                log.info("Buzón: " + bean);
            }
            
            BodyPart messageBodyPart = new MimeBodyPart(); 
            messageBodyPart.setText(this.mailBody);
            messageBodyPart.setContent(this.mailBody, "text/html; charset=UTF-8");
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            
            for(Adjunto bean : this.alAttachtments) {
                log.info("Archivo: " + bean);
                DataSource dataSource = new ByteArrayDataSource(bean.getContenido(), bean.getTipoArchivo());
                MimeBodyPart fileBodyPart = new MimeBodyPart();
                fileBodyPart.setDataHandler(new DataHandler(dataSource));
                fileBodyPart.setFileName(bean.getNombreArchivo());
                multipart.addBodyPart(fileBodyPart);
            }
            
            message.setContent(multipart);
            message.saveChanges();
            this.messageList.add(message);
            
        } catch (UnsupportedEncodingException | MessagingException e) {
            log.error("Problema para agregar el mensaje de correo electrónico a la lista de mensajes...", e);
        }
        
	}
	
	public List<Message> sendMessages() {
		List<Message> notSentList = null;
		
	    Address[] buzones = null;
	    String user = null;
	    String password = null;
	    String msg = null;
	    try {
	    	notSentList = new ArrayList<Message>();
	    	
	        user = this.serverProperties.getProperty("mail.smtp.user");
            password = this.serverProperties.getProperty("mail.smtp.password");
            
            if(this.messageList.size() > 1)
                msg = String.format("Procesando envio masivo de correos electrónicos: %s mensajes...", this.messageList.size());
            else if (this.messageList.size() == 1)
                msg = String.format("Procesando envio masivo de correos electrónicos: %s mensaje...", 1);
            else
                msg = "No hay mensajes para envío masivo de correo electrónico.";
            
            log.info(msg);
            
	        this.transport = this.session.getTransport("smtp");
	        this.transport.connect(user, password);
	        
            for(Message message : this.messageList) {
            	try {
            		buzones = message.getAllRecipients();
                    this.transport.sendMessage(message, buzones);
                    log.info("--> Mensaje enviado : {}", message.getSubject());
                } catch (MessagingException ex ) {
                	log.error("Error de envío para destinatario(s): " + Arrays.toString(buzones), ex);
                    handleSendFailedException(ex, buzones);
                    notSentList.add(message);
                }
            }
            
            log.info(String.format("El envío masivo de correos electrónicos ha concluido (%d mensajes).", this.messageList.size()));
            if(notSentList.size() > 0) {
            	log.info("Existen {} mensajes sin enviar...", notSentList.size());
            }
        } catch (MessagingException ex) {
            log.error("Problema para enviar el batch de correos electrónicos...", ex);
        } finally {
            close(transport);
        }
	    
	    return notSentList;
	}
	
	/**
	 * Maneja errores de envío por destinatario.
	 */
	private void handleSendFailedException(MessagingException sfe, Address[] buzones) {
	    Exception ne = sfe.getNextException();

	    if (ne instanceof com.sun.mail.smtp.SMTPAddressFailedException) {
	        com.sun.mail.smtp.SMTPAddressFailedException smtpEx = (com.sun.mail.smtp.SMTPAddressFailedException) ne;

	        int code = smtpEx.getReturnCode();
	        String message = smtpEx.getMessage();
	        
	        log.warn("Code: {} - Message: {}", code, message);

	        if (code == 450 && message.contains("ReceivingRate")) {
	            log.warn("Destinatario saturado (ReceivingRate): {} -> Reintentar más tarde.", Arrays.toString(buzones));
	            return;
	        }
	    }

	    log.error("Error de envío (SendFailedException) para " + Arrays.toString(buzones), sfe);
	}
	
	private void close(Transport transport) {
	    try {
	        if(transport != null)
	            transport.close();
        } catch (MessagingException ex) {
            log.error("Problema para cerrar el objeto Transport (para envío de correos)...", ex);
        } finally {
            transport = null;
        }
	}
	
	public void sendMessage(String jndiName)
	throws MailException {
	    Session     session = null;
        Properties  props = null;
        String      user = null;
        String      password = null;
        String      replyToName = null;
        String      replyToMail = null;
        InternetAddress[]   aReplyTo = null;
        
        try {
            log.info("Enviando notificacion por correo electronico..");
            
            props = this.dsManager.getServerProperties(jndiName);
            
            user = props.getProperty("mail.smtp.user");
            password = props.getProperty("mail.smtp.password");
            replyToName = props.getProperty("mail.replayTo.name");//Propiedad no estándar de java mail session
            replyToMail = props.getProperty("mail.replayTo.mail");//Propiedad no estándar de java mail session
            
            log.debug("Estableciendo autenticador...");
            SmtpAuthenticator authenticator = new SmtpAuthenticator();
            authenticator.setUserName(user);
            authenticator.setPassword(password);
            session = Session.getInstance(props, authenticator);
            
            log.info("Creando mensaje...");
            
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(props.getProperty("mail.from"), props.getProperty("mail.from.name")));
            message.setSubject(this.subject);
            message.setContent(this.mailBody, "text/html; charset=UTF-8");
            
            if(replyToMail != null || "".equalsIgnoreCase(replyToMail) == false) {
                this.alReplyTo.add(new InternetAddress(replyToMail, replyToName));
            }
            
            if(this.alReplyTo.size() > 0) {
                aReplyTo = new InternetAddress[this.alReplyTo.size()];
                aReplyTo = (InternetAddress[]) this.alReplyTo.toArray(aReplyTo);
                message.setReplyTo(aReplyTo);
                log.info("Agregando cuenta responder a: " + aReplyTo);
            }
            
            if(aReplyTo == null || aReplyTo.length == 0) {
                message.addHeader("Disposition-Notification-To", user);
            } else {
                message.addHeader("Disposition-Notification-To", aReplyTo[0].getAddress());
            }
            log.info("Estableciendo solicitud de acuse de recibo: " + message.getHeader("Disposition-Notification-To"));
            
            for(Correo bean : alTo) {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(bean.getMail(), bean.getNombreBuzon()));
                log.info("Buzón: " + bean);
            }
            
            for(Correo bean : alCC) {
                message.addRecipient(Message.RecipientType.CC, new InternetAddress(bean.getMail(), bean.getNombreBuzon()));
                log.info("Buzón: " + bean);
            }
            
            for(Correo bean : alBCC) {
                message.addRecipient(Message.RecipientType.BCC, new InternetAddress(bean.getMail(), bean.getNombreBuzon()));
                log.info("Buzón: " + bean);
            }
            
            BodyPart messageBodyPart = new MimeBodyPart(); 
            messageBodyPart.setText(this.mailBody);
            messageBodyPart.setContent(this.mailBody, "text/html; charset=UTF-8");
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            
            for(Adjunto bean : alAttachtments) {
                log.info("Archivo: " + bean);
                DataSource dataSource =  new ByteArrayDataSource(bean.getContenido(), bean.getTipoArchivo());
                MimeBodyPart fileBodyPart = new MimeBodyPart();
                fileBodyPart.setDataHandler(new DataHandler(dataSource));
                fileBodyPart.setFileName(bean.getNombreArchivo());
                multipart.addBodyPart(fileBodyPart);
            }
            
            message.setContent(multipart);
            
            log.info("Enviando mensaje...");
            Transport.send(message);
            log.info("Notificacion enviada por correo electronico.");
            
        } catch (MessagingException ex) {
            String message = "Ocurrio un problema con el envío de correo.";
            log.error(message, ex);
            throw new MailException(message);
        } catch (Exception ex) {
            String message = "Ocurrio un problema con el envío de correo.";
            log.error(message, ex);
            throw new MailException(message);
        }
	}
	
	public String getMailBody() {
		return mailBody;
	}

	public void setMailBody(String mailBody) {
		this.mailBody = mailBody;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}
}