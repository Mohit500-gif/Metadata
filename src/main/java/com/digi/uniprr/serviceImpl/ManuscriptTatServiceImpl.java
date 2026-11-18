package com.digi.uniprr.serviceImpl;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digi.uniprr.VO.ManuscriprtTatVO;
import com.digi.uniprr.model.JournalMetadata;
import com.digi.uniprr.model.ManuscriptTat;
import com.digi.uniprr.model.Workflow;
import com.digi.uniprr.model.WorkflowRoleSeq;
import com.digi.uniprr.repository.JournalMetadataRepo;
import com.digi.uniprr.repository.ManuscriptTatRepo;
import com.digi.uniprr.repository.WorkflowRepo;
import com.digi.uniprr.service.ManuscriptTatService;

@Service
@Transactional
public class ManuscriptTatServiceImpl implements ManuscriptTatService {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	JournalMetadataRepo journalMetadataRepo;

	@Autowired
	WorkflowRepo workflowRepo;

	@Autowired
	ManuscriptTatRepo manuscriptTatRepo;

	@Override
	public String setTatForManuscript(ManuscriprtTatVO manuscriprtTatVO) {

		try {
			JournalMetadata JournalMetadata = journalMetadataRepo.metaDatalistByJournalId(manuscriprtTatVO.getJournalId());
			Workflow workflow = workflowRepo.getOne(JournalMetadata.getWorkflowId());
			Calendar lastTatDate = Calendar.getInstance();
			//Calendar lastBufferDate = Calendar.getInstance();
			if(manuscriprtTatVO.getRoleId()!=null && manuscriprtTatVO.getRoleId() !=5) {
				if (manuscriprtTatVO.getStatus() == 1) {
					for (WorkflowRoleSeq workflowRoleSeq : workflow.getWorkflowRoleSeq()) {
						if (workflowRoleSeq.getTaskId() == 1) {
							manuscriptTatRepo.disablePreviousTat(manuscriprtTatVO.getJournalId(), manuscriprtTatVO.getManuscriptId(), manuscriprtTatVO.getManuscriptVersion());
							lastTatDate.add(Calendar.DAY_OF_MONTH, workflowRoleSeq.getTat());
							//lastBufferDate.add(Calendar.DAY_OF_MONTH, 7);
							ManuscriptTat manuscriptTat = new ManuscriptTat();
							manuscriptTat.setJournalId(manuscriprtTatVO.getJournalId());
							manuscriptTat.setManuscriptId(manuscriprtTatVO.getManuscriptId());
							manuscriptTat.setManuscriptVersion(manuscriprtTatVO.getManuscriptVersion());
							manuscriptTat.setUserId(manuscriprtTatVO.getUserId());
							manuscriptTat.setTaskId(manuscriprtTatVO.getTaskId());
							manuscriptTat.setTatLastDate(lastTatDate.getTime());
							//manuscriptTat.setBufferLastDate(lastBufferDate.getTime());
							manuscriptTat.setIsEmailSend(1);
							manuscriptTat.setIsActive(1);
							manuscriptTatRepo.save(manuscriptTat);
						}
					}
				}else if (manuscriprtTatVO.getStatus() == 2) {
					for (WorkflowRoleSeq workflowRoleSeq : workflow.getWorkflowRoleSeq()) {
						if (workflowRoleSeq.getTaskId() == 3) {
							manuscriptTatRepo.disablePreviousTat(manuscriprtTatVO.getJournalId(), manuscriprtTatVO.getManuscriptId(), manuscriprtTatVO.getManuscriptVersion());
							lastTatDate.add(Calendar.DAY_OF_MONTH, workflowRoleSeq.getTat());
							//lastBufferDate.add(Calendar.DAY_OF_MONTH, 7);
							ManuscriptTat manuscriptTat = new ManuscriptTat();
							manuscriptTat.setJournalId(manuscriprtTatVO.getJournalId());
							manuscriptTat.setManuscriptId(manuscriprtTatVO.getManuscriptId());
							manuscriptTat.setManuscriptVersion(manuscriprtTatVO.getManuscriptVersion());
							manuscriptTat.setUserId(manuscriprtTatVO.getUserId());
							manuscriptTat.setTaskId(manuscriprtTatVO.getTaskId());
							manuscriptTat.setTatLastDate(lastTatDate.getTime());
							//manuscriptTat.setBufferLastDate(lastBufferDate.getTime());
							manuscriptTat.setIsEmailSend(1);
							manuscriptTat.setIsActive(1);
							manuscriptTatRepo.save(manuscriptTat);
						}
					}
				}else if (manuscriprtTatVO.getStatus() == 8) {
					for (WorkflowRoleSeq workflowRoleSeq : workflow.getWorkflowRoleSeq()) {
						if (workflowRoleSeq.getTaskId() == 4) {
							manuscriptTatRepo.disablePreviousTat(manuscriprtTatVO.getJournalId(), manuscriprtTatVO.getManuscriptId(), manuscriprtTatVO.getManuscriptVersion());
							lastTatDate.add(Calendar.DAY_OF_MONTH, workflowRoleSeq.getTat());
							//lastBufferDate.add(Calendar.DAY_OF_MONTH, 7);
							ManuscriptTat manuscriptTat = new ManuscriptTat();
							manuscriptTat.setJournalId(manuscriprtTatVO.getJournalId());
							manuscriptTat.setManuscriptId(manuscriprtTatVO.getManuscriptId());
							manuscriptTat.setManuscriptVersion(manuscriprtTatVO.getManuscriptVersion());
							manuscriptTat.setUserId(manuscriprtTatVO.getUserId());
							manuscriptTat.setTaskId(manuscriprtTatVO.getTaskId());
							manuscriptTat.setTatLastDate(lastTatDate.getTime());
							//manuscriptTat.setBufferLastDate(lastBufferDate.getTime());
							manuscriptTat.setIsEmailSend(1);
							manuscriptTat.setIsActive(1);
							manuscriptTatRepo.save(manuscriptTat);
						}
					}
				}else if (manuscriprtTatVO.getStatus() == 35) {
					for (WorkflowRoleSeq workflowRoleSeq : workflow.getWorkflowRoleSeq()) {
						if (workflowRoleSeq.getTaskId() == 4) {
							manuscriptTatRepo.disablePreviousTat(manuscriprtTatVO.getJournalId(), manuscriprtTatVO.getManuscriptId(), manuscriprtTatVO.getManuscriptVersion());
							lastTatDate.add(Calendar.DAY_OF_MONTH, workflowRoleSeq.getTat());
							//lastBufferDate.add(Calendar.DAY_OF_MONTH, 7);
							ManuscriptTat manuscriptTat = new ManuscriptTat();
							manuscriptTat.setJournalId(manuscriprtTatVO.getJournalId());
							manuscriptTat.setManuscriptId(manuscriprtTatVO.getManuscriptId());
							manuscriptTat.setManuscriptVersion(manuscriprtTatVO.getManuscriptVersion());
							manuscriptTat.setUserId(manuscriprtTatVO.getUserId());
							manuscriptTat.setTaskId(manuscriprtTatVO.getTaskId());
							manuscriptTat.setTatLastDate(lastTatDate.getTime());
							//manuscriptTat.setBufferLastDate(lastBufferDate.getTime());
							manuscriptTat.setIsEmailSend(1);
							manuscriptTat.setIsActive(1);
							manuscriptTatRepo.save(manuscriptTat);
						}
					}
				}
			}else {
				//Implement for Reviewer
			}
			
			
		}catch(Exception e) {
			log.error("Exception : " + e.getMessage());
			return "Something Went Wrong";
		}
		return "Done";
	}

}
