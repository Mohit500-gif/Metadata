package com.digi.uniprr.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digi.uniprr.VO.DiscussionVo;
import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.model.Discussion;
import com.digi.uniprr.model.ManuScript;
import com.digi.uniprr.model.ManuScriptReviewer;
import com.digi.uniprr.repository.DiscussionRepo;
import com.digi.uniprr.repository.ManuScriptRepo;
import com.digi.uniprr.repository.ManuScriptReviewerRepo;
import com.digi.uniprr.service.DiscussionService;

@Service
@Transactional
public class DiscussionServiceImpl implements DiscussionService {

	@Autowired
	ManuScriptReviewerRepo manuScriptReviewerRepo;

	@Autowired
	DiscussionRepo discussionRepo;
	
	@Autowired
	ManuScriptRepo manuScriptRepo;

	@Override
	public void queryToEditor(DiscussionVo discussionVo) {
		ManuScriptReviewer manuScriptReviewer = manuScriptReviewerRepo.findManuscriptReviewerDetails(
				discussionVo.getManuscriptId(), discussionVo.getManuscriptVersion(), discussionVo.getJournalId(),
				discussionVo.getUserId());
		if (manuScriptReviewer != null) {
			Discussion discussion = new Discussion();
			discussion.setManuscriptId(discussionVo.getManuscriptId());
			discussion.setManuscriptVersion(discussionVo.getManuscriptVersion());
			discussion.setJournalId(discussionVo.getJournalId());
			discussion.setMessage(discussionVo.getMessage());
			discussion.setSubject(discussionVo.getSubject());
			discussion.setUserId(discussionVo.getUserId());
			discussion.setUserRole(discussionVo.getUserRole());
			discussion.setDiscussionOnRole(discussionVo.getDiscussionOnRole());
			discussion.setReceiverId(manuScriptReviewer.getEditorId());
			discussionRepo.save(discussion);
		}
	}

	@Override
	public List<DiscussionVo> getQueryById(IDVO id) {
		List<Discussion> discussionList = discussionRepo.getQueryById(id.getUserId(),id.getRoleId(),id.getTaskId());
		
		List<DiscussionVo> discussionVoList = new ArrayList();
		List<Integer> manuscriptId = new ArrayList();
		
		if(discussionList!=null && !discussionList.isEmpty()) {
			for (Discussion discussion : discussionList) {
				ManuScript manuscript = manuScriptRepo.getOne(discussion.getManuscriptId());
				DiscussionVo discussionVo = new DiscussionVo();
				discussionVo.setMessage(discussion.getMessage());
				discussionVo.setManuscriptId(discussion.getManuscriptId());
				discussionVo.setManuscriptVersion(discussion.getManuscriptVersion());
				discussionVo.setManuscriptCode(manuscript.getManuscriptCode());
				discussionVoList.add(discussionVo);
				
			}
		}
		return discussionVoList;
	}

	@Override
	public List<DiscussionVo> getOutBox(IDVO id) {
		List<Discussion> discussionList = discussionRepo.getOutBox(id.getUserId(),id.getRoleId());
		List<DiscussionVo> discussionVoList = new ArrayList();
		List<Integer> manuscriptId = new ArrayList();
		if(discussionList!=null && !discussionList.isEmpty()) {
			for (Discussion discussion : discussionList) {
				ManuScript manuscript = manuScriptRepo.getOne(discussion.getManuscriptId());
				DiscussionVo discussionVo = new DiscussionVo();
				discussionVo.setMessage(discussion.getMessage());
				discussionVo.setManuscriptId(discussion.getManuscriptId());
				discussionVo.setManuscriptVersion(discussion.getManuscriptVersion());
				discussionVo.setManuscriptCode(manuscript.getManuscriptCode());
				discussionVoList.add(discussionVo);
				
			}
		}
		return discussionVoList;
	
	}

}
