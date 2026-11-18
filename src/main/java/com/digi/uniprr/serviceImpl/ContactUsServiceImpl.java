package com.digi.uniprr.serviceImpl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.digi.uniprr.VO.ContactUsVO;
import com.digi.uniprr.model.ContactUs;
import com.digi.uniprr.model.MailQueue;
import com.digi.uniprr.model.User;
import com.digi.uniprr.repository.ContactUsRepo;
import com.digi.uniprr.repository.MailQueueRepo;
import com.digi.uniprr.service.ContactUsService;

@Transactional
@Service
public class ContactUsServiceImpl implements ContactUsService {
	
	@Autowired
	ContactUsRepo contactUsRepo;
	
	@Autowired
	MailQueueRepo mailqueueRepo;

	@Override
	public String saveContactus(ContactUsVO contact) {
		
		ContactUs contactUs = new ContactUs();
		
		String status = "";
		 
		if(contact != null) {
			String userDetails = "Name : "+contact.getTitle()+" "+contact.getFirstName()+" "+contact.getLastName()+"         Role Type : "+
		            contact.getRoleType()+"\nJournal Name : "+contact.getJournalName()+"         Subject : "+ contact.getSubject()+"\nEmail Id : "+contact.getEmail()+"\n -------------------------------------------------------------------------\n";                     

			
			contactUs.setTitle(contact.getTitle());
			contactUs.setRoleType(contact.getRoleType());
			contactUs.setFirstName(contact.getFirstName());
			contactUs.setLastName(contact.getLastName());
			contactUs.setEmail(contact.getEmail());
			contactUs.setSubject(contact.getSubject());
			contactUs.setJournalName(contact.getJournalName());
			contactUs.setMessage(userDetails+contact.getMessage());
		
			contactUsRepo.saveAndFlush(contactUs);
			
			MailQueue addMailQueue = new MailQueue();
			addMailQueue.setMailFrom(contact.getEmail());
			addMailQueue.setMailTo("WKHLRPMedknow_techsupport@wolterskluwer.com");
			addMailQueue.setJournalId(contact.getJournalId());
			addMailQueue.setManuscriptId(0);
			addMailQueue.setMailSubject(contact.getSubject());
			addMailQueue.setMailBody(userDetails+contact.getMessage());
			addMailQueue.setMail_send_date(new Date());
			addMailQueue.setIsActive(0);
			mailqueueRepo.saveAndFlush(addMailQueue);	
			
			status="Success";
		
		} else {
			
			status="Fail";
		}
		
		return status; 
	}

}
