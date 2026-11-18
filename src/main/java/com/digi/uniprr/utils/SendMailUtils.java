package com.digi.uniprr.utils;

import java.io.File;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.digi.uniprr.model.PublonMailLog;
import com.digi.uniprr.repository.PublonMailLogRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SendMailUtils {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${mail.smtp.auth}")
    private boolean smtpAuthentication;

    @Value("${mail.transport.protocol}")
    private String transportProtocol;

    @Value("${mail.smtp.starttls.enable}")
    private String starttlsEnabled;

    @Value("${mail.smtp.host}")
    private String smtpHost;

    @Value("${mail.smtp.port}")
    private String smtpPort;

    @Value("${mail.smtp.user}")
    private String smtpUser;

    @Value("${mail.smtp.password}")
    private String smtpPassword;

    @Value("${fromMail}")
    private String defaultFromEmail;

    @Autowired
    private PublonMailLogRepository mailLogRepository;  // ✅ Inject repository

    public Properties getAuthenticatedMailProperties() {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", smtpAuthentication);
        properties.put("mail.transport.protocol", transportProtocol);
        properties.put("mail.smtp.starttls.enable", starttlsEnabled);
        properties.put("mail.smtp.host", smtpHost);
        properties.put("mail.smtp.port", smtpPort);
        properties.put("mail.smtp.user", smtpUser);
        properties.put("mail.smtp.pwd", smtpPassword);
        return properties;
    }

    public Properties getMailProperties() {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", smtpHost);
        properties.put("mail.smtp.port", smtpPort);
        return properties;
    }

    public String sendEmail(String fromMailID, String toMailID, String mailSubject, String mailBody,
                            String smtpEmailUser, String smtpEmailPassword, String journalAlias) {
        logger.info("Inside In Send Mail Utils");
        try {
            Session session = Session.getDefaultInstance(getMailProperties());
            MimeMessage msg = new MimeMessage(session);
            String fromMail = (smtpEmailUser != null && !smtpEmailUser.isEmpty()) ? smtpEmailUser : defaultFromEmail;

            if (journalAlias != null && !journalAlias.isEmpty()) {
                msg.setFrom(new InternetAddress(fromMail, journalAlias));
            } else {
                msg.setFrom(new InternetAddress(fromMail));
            }
            logger.info("FROM MAIL ID  :-------->" + msg.getFrom());
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(toMailID));
            msg.setSubject(mailSubject);
            msg.setContent(mailBody, "text/html");
            msg.setSentDate(new Date());
            Transport.send(msg);
            logger.info("-----------------------------SUCCESS----------------------------------");
            return "success";
        } catch (Exception ex) {
            logger.error("Error while sending email : " + ex.getMessage(), ex);
            return "fail";
        }
    }

    public boolean sendEmailWithAttachment(String fromEmail, String toEmail, String subject, String body,
                                           File attachmentPath, String fileName) {
        logger.info("--Start--");
        boolean success = false;
        String errorMessage = null;
        String ccEmail = "uniprr.techsupport@digiscapetech.com";

        try {
            Session session;
            if (smtpAuthentication) {
                logger.info("SMTP authentication required");
                session = Session.getInstance(getAuthenticatedMailProperties(), new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(smtpUser, smtpPassword);
                    }
                });
            } else {
                session = Session.getDefaultInstance(getMailProperties());
            }

            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(fromEmail));
            mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            mimeMessage.setRecipients(Message.RecipientType.CC, InternetAddress.parse(ccEmail));
            mimeMessage.setSubject(subject);

            Multipart multipart = new MimeMultipart();

            // body part
            MimeBodyPart textContent = new MimeBodyPart();
            textContent.setContent(body, "text/html");
            multipart.addBodyPart(textContent);

            // attachment part
            String attachmentFullPath = null;
            if (attachmentPath != null && fileName != null) {
                File fullFile = new File(attachmentPath, fileName);
                attachmentFullPath = fullFile.getAbsolutePath();
                if (fullFile.exists()) {
                    MimeBodyPart attachment = new MimeBodyPart();
                    DataSource source = new FileDataSource(fullFile);
                    attachment.setDataHandler(new DataHandler(source));
                    attachment.setFileName(fileName);
                    multipart.addBodyPart(attachment);
                } else {
                    logger.warn("Attachment file not found: {}", attachmentFullPath);
                }
            }

            mimeMessage.setContent(multipart);

            Transport.send(mimeMessage);
            logger.info("Email sent successfully");
            success = true;

            // ✅ Save SUCCESS log
            saveMailLog(fromEmail, toEmail, ccEmail, subject, body, fileName, attachmentPath, "SUCCESS", null);

        } catch (Exception e) {
            logger.error("Exception while sending email : " + e.getMessage(), e);
            success = false;
            errorMessage = e.getMessage();

            // ✅ Save FAILED log
            saveMailLog(fromEmail, toEmail, ccEmail, subject, body, fileName, attachmentPath, "FAILED", errorMessage);
        }

        return success;
    }

    private void saveMailLog(String fromEmail, String toEmail, String ccEmail, String subject, String body,
                             String fileName, File attachmentFolder, String status, String errorMessage) {
        try {
            PublonMailLog logEntry = new PublonMailLog();
            logEntry.setFromEmail(fromEmail);
            logEntry.setToEmail(toEmail);
            logEntry.setCcEmail(ccEmail);
            logEntry.setSubject(subject);
            logEntry.setBody(body);
            logEntry.setAttachmentName(fileName);

            if (attachmentFolder != null && fileName != null) {
                String fullPath = new File(attachmentFolder, fileName).getAbsolutePath();
                logEntry.setAttachmentPath(fullPath);
            }

            logEntry.setStatus(status);
            logEntry.setErrorMessage(errorMessage);
            logEntry.setSentDate(new Date());

            mailLogRepository.save(logEntry);
            logger.info("Mail log saved with status: {}", status);
        } catch (Exception ex) {
            logger.error("Error while saving mail log: {}", ex.getMessage(), ex);
        }
    }
}
