package com.digi.uniprr.utils;

import java.text.SimpleDateFormat;
import java.util.*;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.digi.uniprr.repository.ManuscriptEditorRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.digi.uniprr.repository.AuthoritiesHistoryRepo;
import com.digi.uniprr.repository.ManuScriptRepo;

@Service
public class ScheduledTasks {
	
	   @Autowired
	   AuthoritiesHistoryRepo authoritiesHistoryRepo;
	   
	   @Autowired
	   ManuScriptRepo manuScriptRepo;

	   @Autowired
	   ManuscriptEditorRepo manuscriptEditorRepo;

	   @Scheduled(cron = "0 0 0 * * *") 
	    public void checkAndSendAlertMailForAssignStatus() {
	        Set<Integer> missingIds = checkForMissingEntries(3);
	        if (!missingIds.isEmpty()) {
	            sendAlertMail(missingIds);
	        }
	    }
	   
	   @Scheduled(cron = "0 0 0 * * *") 
	    public void checkAndSendAlertMailForSubmissionStatus() {   
	        Set<Integer> missingIds = checkForMissingEntries(2);

	        if (!missingIds.isEmpty()) {
	            sendAlertMail(missingIds);
	        }
	    }

	    private Set<Integer> checkForMissingEntries(int status) {
	        Set<Integer> missingIds = new HashSet<>();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date todayDate=new Date();
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH,+1);
			Date before = cal.getTime();
			String nextDay = dateFormat.format(before);
			String today=dateFormat.format(todayDate);
			nextDay=nextDay.concat(AppConstants.ZERO_TIME);
			today=today.concat(AppConstants.ZERO_TIME);

			System.out.println("today "+today);
			System.out.println("next "+nextDay);

	        List<Integer> manuscriptIds = manuScriptRepo.findManuscriptIdsByStatus(status, today, nextDay);
	        List<Integer> authoritiesHistoryIds = authoritiesHistoryRepo.findManuscriptIds(status,today,nextDay);

	        for (Integer manuscriptId : manuscriptIds) {
	        	if (!authoritiesHistoryIds.contains(manuscriptId)) {
	                missingIds.add(manuscriptId);
	            }
	        }

			if(status==3)
			{
				List<Integer> assignEditorIds=manuscriptEditorRepo.getAssignedEditorListForSchedule(today,nextDay);
				for (Integer manuscriptId : manuscriptIds) {
					if (!assignEditorIds.contains(manuscriptId)) {
						missingIds.add(manuscriptId);
					}
				}
			}

	        System.out.println("miss ids:   "+missingIds);
	        return missingIds;
	    }

	    public void sendAlertMail(Object missingData) {
			Properties props = new Properties();
			props.put("mail.smtp.host", "email-smtp.us-east-1.amazonaws.com");
			props.put("mail.smtp.port", "587");
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");

			props.put("mail.smtp.user", "AKIAZIRGP356SAZG222F");
			props.put("mail.smtp.password", "BD3xeZ4MFbHzzqCtlr3GtK3tNMC9YblVGhoD4BEd0lVf");

			Session session = Session.getInstance(props);
			String[] mailTo = {"naresh.kumar@digiscapetech.com"};
			String mailFrom = "uniprr.td@journalonweb.com";
			String mailSubject ="Alert: Missing Manuscript Entry(s)";
			String mailBody;
	        if (missingData instanceof Integer) {
	            Integer missingId = (Integer) missingData;
	            mailBody = "<html><body><p>The manuscript ID " + missingId + " is missing.</p></body></html>";
	        } else if (missingData instanceof List<?>) {
	            // If list of IDs is missing
	            List<Integer> missingIds = (List<Integer>) missingData;
	            StringBuilder body = new StringBuilder("<html><body><p>The following manuscript IDs are missing:<br>");
	            for (Integer id : missingIds) {
	                body.append(id).append("<br>");
	            }
	            body.append("</p></body></html>");
	            mailBody = (body.toString());
	        } else {
	            throw new IllegalArgumentException("Invalid type of missing data.");
	        }

			try {
			    Message message = new MimeMessage(session);
			    message.setFrom(new InternetAddress(mailFrom));

			    InternetAddress[] toAddresses = new InternetAddress[mailTo.length];
	            for (int i = 0; i < mailTo.length; i++) {
	                toAddresses[i] = new InternetAddress(mailTo[i]);
	            }
	            message.setRecipients(Message.RecipientType.TO, toAddresses);

	 
			    message.setSubject(mailSubject);
			    message.setContent(mailBody,"text/html");

			    Transport transport = session.getTransport("smtp");
			    transport.connect("email-smtp.us-east-1.amazonaws.com", "AKIAZIRGP356SAZG222F", "BD3xeZ4MFbHzzqCtlr3GtK3tNMC9YblVGhoD4BEd0lVf");
			    transport.sendMessage(message, message.getAllRecipients());

			    System.out.println("Email sent successfully");

			} catch (MessagingException e) {
			    e.printStackTrace();
			}
	    }

}
