package com.digi.uniprr.service;

import java.util.List;

import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.VO.MailQueueCountVO;
import com.digi.uniprr.VO.MailQueueVo;
import com.digi.uniprr.model.MailQueue;
import com.digi.uniprr.model.MailQueueArchive;

public interface MailQueueService {
	
	void createMailQueue(MailQueue mailQueue);

	List<MailQueue> getMailQueueUserId(IDVO id);
	
	List<MailQueueArchive> getMailQueueArchiveUserId(IDVO id);

	void getMailIsSeen(Integer id);

	List<MailQueue> getMailListMailTo(String name);
	
	List<MailQueue> getMailListMailFrom(String name);

	List<MailQueueVo> getMailByManuscript(IDVO id);
	
	MailQueueCountVO getMailQueueCount(IDVO id);
	
	String insertMailQueue(String mailFrom, String mailTo, String mailCc, Integer journalId, Integer manuscriptId, String subject, String body);

	void moveMailDataToArchive();

}
