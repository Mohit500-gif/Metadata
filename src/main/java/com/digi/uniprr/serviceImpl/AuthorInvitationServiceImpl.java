package com.digi.uniprr.serviceImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.model.AuthorInvitation;
import com.digi.uniprr.model.MailQueue;
import com.digi.uniprr.repository.AuthorInvitationRepo;
import com.digi.uniprr.repository.MailQueueRepo;
import com.digi.uniprr.repository.UserRepo;
import com.digi.uniprr.service.AuthorInvitationService;
import com.digi.uniprr.utils.SendMailUtils;

@Service
@Transactional
public class AuthorInvitationServiceImpl implements AuthorInvitationService {

	@Autowired
	AuthorInvitationRepo authorInvitationRepo;

	@Autowired
	Environment env;

	@Autowired
	SendMailUtils mailUtils;

	@Autowired
	UserRepo userRepo;
	
	@Autowired
	MailQueueRepo mailQueueRepo;

	@Override
	public List<AuthorInvitation> getAuthorInvitationByUserId(IDVO id) {
		List<AuthorInvitation> authorInvitation = new ArrayList<AuthorInvitation>();
		if (id.getId() != null) {
			authorInvitation = authorInvitationRepo.getAuthorInvitationByUserId(id.getId());
		} else if (id.getName() != null) {
			authorInvitation = authorInvitationRepo.getAuthorInvitationByReceiver(id.getName().trim());
		}
		return authorInvitation;
	}

	@Override
	public void createAuthorInvitation(AuthorInvitation authorInvitation) {
		authorInvitation.setIsSeen(0);
		authorInvitation.setIsActive(1);
		authorInvitationRepo.save(authorInvitation);
		sendMailForAuthorInvitation(authorInvitation.getReceiver(), authorInvitation);
	}

	@Override
	public void readInvitation(IDVO id) {
		authorInvitationRepo.readIvitatio(id.getId());
	}

	public void sendMailForAuthorInvitation(String userEmail, AuthorInvitation authorInvitation) {
		String fromMailID = env.getProperty("spring.mail.username");
		String toMailID = userEmail;
		String mailSubject = authorInvitation.getSubject();
		String mailBody = authorInvitation.getMessage();
		MailQueue addMailQueue = new MailQueue();
		addMailQueue.setMailFrom(fromMailID);
		addMailQueue.setMailTo(toMailID);
		addMailQueue.setJournalId(259);
		addMailQueue.setManuscriptId(0);
		addMailQueue.setMailSubject(mailSubject);
		addMailQueue.setMailBody(mailBody);
		addMailQueue.setMail_send_date(new Date());
		addMailQueue.setIsActive(0);
		mailQueueRepo.saveAndFlush(addMailQueue);

	//	mailUtils.sendEmail(fromMailID, toMailID, mailSubject, mailBody,"","","");

	}
}
