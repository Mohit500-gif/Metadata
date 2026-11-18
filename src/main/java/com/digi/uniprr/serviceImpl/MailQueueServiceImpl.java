package com.digi.uniprr.serviceImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.VO.MailQueueCountVO;
import com.digi.uniprr.VO.MailQueueVo;
import com.digi.uniprr.model.MailQueue;
import com.digi.uniprr.model.MailQueueArchive;
import com.digi.uniprr.repository.MailQueueArchiveRepo;
import com.digi.uniprr.repository.MailQueueRepo;
import com.digi.uniprr.service.MailQueueService;

@Service
@Transactional
public class MailQueueServiceImpl implements MailQueueService {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	MailQueueRepo mailQueueRepo;
	
	@Autowired
	MailQueueArchiveRepo mailQueueArchiveRepo;
	
	@Value("${totalMonthToBeArchived}")
	int totalMonthToBeArchived;

	@Override
	public void createMailQueue(MailQueue mailQueue) {
		mailQueue.setIsActive(0);
		mailQueue.setIsSeen(0);
		mailQueueRepo.save(mailQueue);	
	}

	@Override
	public List<MailQueue> getMailQueueUserId(IDVO id) {
		List<MailQueue> mailQueueData = mailQueueRepo.getMailQueueUserId(id.getId(),id.getRoleId(),id.getJournalId(),id.getIsActive());
		return mailQueueData;
	}
	
	@Override
	public List<MailQueueArchive> getMailQueueArchiveUserId(IDVO id) {
		List<MailQueueArchive> mailQueueData = mailQueueArchiveRepo.getMailQueueArchiveUserId(id.getId(),id.getRoleId(),id.getJournalId(),id.getIsActive());
		return mailQueueData;
	}

	@Override
	public void getMailIsSeen(Integer id) {
	     mailQueueRepo.getMailIsSeen(id);
	}

	@Override
	public List<MailQueue> getMailListMailTo(String name) {
		List<MailQueue> mailTo = mailQueueRepo.getMailListMailTo(name);
		return mailTo;
	}
	
	@Override
	public List<MailQueue> getMailListMailFrom(String name) {
		List<MailQueue> mailFrom = mailQueueRepo.getMailListMailFrom(name);
		return mailFrom;
	}

	@Override
	public List<MailQueueVo> getMailByManuscript(IDVO id) {
		List<MailQueueVo> add=new ArrayList<>();
		List<MailQueue> mailFrom = mailQueueRepo.getMailByManuscriptForAll(id.getManuscriptId());
		List<MailQueueArchive> mailArchiveFrom = mailQueueArchiveRepo.getMailByManuscriptForAll(id.getManuscriptId());
		if(mailFrom!=null && !mailFrom.isEmpty()) {
			mailFrom.stream().forEach(i->{
				MailQueueVo a=new MailQueueVo();
				a.setId(i.getId());
				a.setJournalId(i.getJournalId());
				a.setManuscriptId(i.getManuscriptId());
				a.setManuscriptVersion(i.getManuscriptVersion());
				a.setMailToRole(i.getMailToRole());
				a.setManuscriptStatus(i.getManuscriptStatus());
				a.setMailTOUserId(i.getMailTOUserId());
				a.setMailTo(i.getMailTo());
				a.setMailFrom(i.getMailFrom());
				a.setMailSubject(i.getMailSubject());
				a.setMailBody(i.getMailBody());
				a.setMailCC(i.getMailCC());
				a.setMailBCC(i.getMailBCC());
				a.setIsSeen(i.getIsSeen());
				a.setIsActive(i.getIsActive());
				a.setMail_send_date(i.getMail_send_date());
				add.add(a);
			});
		}
		if(mailArchiveFrom!=null && !mailArchiveFrom.isEmpty()) {
			mailArchiveFrom.stream().forEach(i->{
				MailQueueVo a=new MailQueueVo();
				a.setId(i.getId());
				a.setJournalId(i.getJournalId());
				a.setManuscriptId(i.getManuscriptId());
				a.setManuscriptVersion(i.getManuscriptVersion());
				a.setMailToRole(i.getMailToRole());
				a.setManuscriptStatus(i.getManuscriptStatus());
				a.setMailTOUserId(i.getMailTOUserId());
				a.setMailTo(i.getMailTo());
				a.setMailFrom(i.getMailFrom());
				a.setMailSubject(i.getMailSubject());
				a.setMailBody(i.getMailBody());
				a.setMailCC(i.getMailCC());
				a.setMailBCC(i.getMailBCC());
				a.setIsSeen(i.getIsSeen());
				a.setIsActive(i.getIsActive());
				a.setMail_send_date(i.getMail_send_date());
				add.add(a);
			});
		}
		
		return add;
	}

	@Override
	public MailQueueCountVO getMailQueueCount(IDVO id) {
		MailQueueCountVO mailQueueCountVO = new MailQueueCountVO();
		mailQueueCountVO.setCount(mailQueueRepo.getMailQueueUserIdAndIsNotSeen(id.getId(), id.getRoleId(), id.getJournalId(), 1).size());
		return mailQueueCountVO;
	}

	@Override
	public String insertMailQueue(String mailFrom, String mailTo, String MailCc, Integer journalId, Integer manuscripId,
			String subject, String body) {
		MailQueue addMailQueue = new MailQueue();
		addMailQueue.setMailFrom(mailFrom);
		addMailQueue.setMailTo(mailTo);
		addMailQueue.setMailCC(MailCc);
		addMailQueue.setJournalId(journalId);
		addMailQueue.setManuscriptId(manuscripId);
		addMailQueue.setMailSubject(subject);
		addMailQueue.setMailBody(body);
		addMailQueue.setMail_send_date(new Date());
		addMailQueue.setIsActive(0);
		mailQueueRepo.saveAndFlush(addMailQueue);

		return "SUCCESS";
	}

	@Override
	@Transactional
	public void moveMailDataToArchive() {
	    Date getForDate = DateUtils.addMonths(new Date(), totalMonthToBeArchived*-1);
	    List<Integer> deleteMailIdsList = new ArrayList<>();
	    List<MailQueueArchive> mailArchiveList = new ArrayList<>();
		try {
			log.info(".......Fetching the Emails for Archive.......");
			List<MailQueue> mailQueueList = mailQueueRepo.getMailListForArchive(getForDate);
			log.info("Total Email for Archive : {}", mailQueueList.size());
			
			for (MailQueue mailQueue : mailQueueList) {
				MailQueueArchive mailQueueArchive = new MailQueueArchive();
				mailQueueArchive.setId(mailQueue.getId());
				mailQueueArchive.setJournalId(mailQueue.getJournalId());
				mailQueueArchive.setManuscriptId(mailQueue.getManuscriptId());
				mailQueueArchive.setManuscriptVersion(mailQueue.getManuscriptVersion());
				mailQueueArchive.setMailToRole(mailQueue.getMailToRole());
				mailQueueArchive.setTaskId(mailQueue.getTaskId());
				mailQueueArchive.setManuscriptStatus(mailQueue.getManuscriptStatus());
				mailQueueArchive.setMailTOUserId(mailQueue.getMailTOUserId());
				mailQueueArchive.setMailFrom(mailQueue.getMailFrom());
				mailQueueArchive.setMailTo(mailQueue.getMailTo());
				mailQueueArchive.setMailSubject(mailQueue.getMailSubject());
				mailQueueArchive.setMailBody(mailQueue.getMailBody());
				mailQueueArchive.setMailCC(mailQueue.getMailCC());
				mailQueueArchive.setMailBCC(mailQueue.getMailBCC());
				mailQueueArchive.setMail_send_date(mailQueue.getMail_send_date());
				mailQueueArchive.setIsSeen(mailQueue.getIsSeen());
				mailQueueArchive.setIsActive(mailQueue.getIsActive());
				mailQueueArchive.setModuleName(mailQueue.getModuleName());
				
				mailArchiveList.add(mailQueueArchive);
			}
			
			
			mailArchiveList = mailQueueArchiveRepo.saveAll(mailArchiveList);
			log.info("Total Email Archived : {}", mailQueueList.size());
			
			mailArchiveList.stream().forEach(e -> deleteMailIdsList.add(e.getId()));
			if(!deleteMailIdsList.isEmpty()) {
				log.info(".......Started deleting the records from mail_queue.......");
				mailQueueRepo.deleteAllById(deleteMailIdsList);
				log.info("Total Email records deleted from mail_queue : {}", deleteMailIdsList.size());
			}
		} catch (Exception e) {
			log.error("Mail Archived failed {} - {}",e.getMessage(), e);
			e.printStackTrace();
		}
	}

}
