package com.digi.uniprr.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digi.uniprr.model.PublonMailLog;
import com.digi.uniprr.repository.PublonMailLogRepository;

import java.io.File;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

@Service
public class PublonMailService {

    private static final Logger logger = LoggerFactory.getLogger(PublonMailService.class);

    @Autowired
    private PublonMailLogRepository mailLogRepository;

    // Example SMTP configs
    private boolean smtpAuthentication = true;
    private String smtpUser = "youremail@domain.com";
    private String smtpPassword = "yourpassword";

    private Properties getAuthenticatedMailProperties() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.yourserver.com");
        props.put("mail.smtp.port", "587");
        return props;
    }

    private Properties getMailProperties() {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.yourserver.com");
        props.put("mail.smtp.port", "25");
        return props;
    }

    public boolean sendEmailWithAttachment(String fromEmail, String toEmail, String subject, String body,
                                           File attachmentFolder, String fileName) {
        logger.info("--Start Sending Email--");
        boolean success = false;
        String errorMessage = null;

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

            // ✅ Add CC
            mimeMessage.setRecipients(Message.RecipientType.CC,
                    InternetAddress.parse("uniprr.techsupport@digiscapetech.com"));

            mimeMessage.setSubject(subject);

            Multipart multipart = new MimeMultipart();

            // ✅ Body
            MimeBodyPart textContent = new MimeBodyPart();
            textContent.setContent(body, "text/html");
            multipart.addBodyPart(textContent);

            // ✅ Attachment
            String attachmentFullPath = null;
            if (attachmentFolder != null && fileName != null) {
                File fullFile = new File(attachmentFolder, fileName);
                attachmentFullPath = fullFile.getAbsolutePath();
                if (fullFile.exists()) {
                    MimeBodyPart attachment = new MimeBodyPart();
                    DataSource source = new FileDataSource(fullFile);
                    attachment.setDataHandler(new DataHandler(source));
                    attachment.setFileName(fileName);
                    multipart.addBodyPart(attachment);
                } else {
                    logger.warn("Attachment file does not exist: {}", fullFile.getAbsolutePath());
                }
            }

            mimeMessage.setContent(multipart);

            // ✅ Try to send
            Transport.send(mimeMessage);
            logger.info("Email sent successfully");
            success = true;

            // ✅ Save to DB (success)
            saveMailLog(fromEmail, toEmail, subject, body, fileName, attachmentFolder, "SUCCESS", null);

        } catch (MessagingException e) {
            logger.error("Exception while sending email: {}", e.getMessage(), e);
            success = false;
            errorMessage = e.getMessage();

            // ✅ Save to DB (failed)
            saveMailLog(fromEmail, toEmail, subject, body, fileName, attachmentFolder, "FAILED", errorMessage);
        }

        return success;
    }

    private void saveMailLog(String fromEmail, String toEmail, String subject, String body,
                             String fileName, File attachmentFolder,
                             String status, String errorMessage) {
        try {
            PublonMailLog log = new PublonMailLog();
            log.setFromEmail(fromEmail);
            log.setToEmail(toEmail);
            log.setCcEmail("uniprr.techsupport@digiscapetech.com");
            log.setSubject(subject);
            log.setBody(body);
            log.setAttachmentName(fileName);

            if (attachmentFolder != null && fileName != null) {
                String fullPath = new File(attachmentFolder, fileName).getAbsolutePath();
                log.setAttachmentPath(fullPath);
            }

            log.setStatus(status);
            log.setErrorMessage(errorMessage);
            log.setSentDate(new Date());

            mailLogRepository.save(log);
            logger.info("Mail log saved in database with status: {}", status);
        } catch (Exception e) {
            logger.error("Error while saving mail log: {}", e.getMessage(), e);
        }
    }
}

