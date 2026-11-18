package com.digi.uniprr.serviceImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digi.uniprr.VO.DiscussionVo;
import com.digi.uniprr.VO.ManuscriptMessagingVO;
import com.digi.uniprr.model.Discussion;
import com.digi.uniprr.model.MailQueue;
import com.digi.uniprr.model.ManuScript;
import com.digi.uniprr.model.ManuscriptMessaging;
import com.digi.uniprr.model.User;
import com.digi.uniprr.repository.DiscussionRepo;
import com.digi.uniprr.repository.MailQueueRepo;
import com.digi.uniprr.repository.ManuScriptRepo;
import com.digi.uniprr.repository.ManuscriptMessagingRepo;
import com.digi.uniprr.repository.UserRepo;
import com.digi.uniprr.service.ManuscriptMessagingService;

@Service
@Transactional
public class ManuscriptMessagingServiceImpl implements ManuscriptMessagingService {

	@Autowired
	ManuscriptMessagingRepo manuscriptMessagingRepo;

	@Autowired
	DiscussionRepo discussionRepo;
	
	@Autowired
	ManuScriptRepo manuScriptRepo;
	
	@Autowired
	UserRepo userRepo;
	
	@Autowired
	MailQueueRepo mailqueueRepo;

	@Override
	public ManuscriptMessaging sendMessage(ManuscriptMessagingVO manuscriptMessagingVO) {

		ManuscriptMessaging createManuscriptMessaging = new ManuscriptMessaging();
		createManuscriptMessaging.setManuscriptId(manuscriptMessagingVO.getManuscriptId());
		createManuscriptMessaging.setManuscriptVersion(manuscriptMessagingVO.getManuscriptVersion());
		createManuscriptMessaging.setJournalId(manuscriptMessagingVO.getJournalId());
		createManuscriptMessaging.setFromUserId(manuscriptMessagingVO.getFromUserId());
		createManuscriptMessaging.setFromUserRole(manuscriptMessagingVO.getFromUserRole());
		createManuscriptMessaging.setFromUserName(manuscriptMessagingVO.getFromUserName());
		createManuscriptMessaging.setToUserId(manuscriptMessagingVO.getToUserId());
		createManuscriptMessaging.setToUserRole(manuscriptMessagingVO.getToUserRole());
		createManuscriptMessaging.setToUserName(manuscriptMessagingVO.getToUserName());
		createManuscriptMessaging.setRecomandation(manuscriptMessagingVO.getRecomandation());
		createManuscriptMessaging.setSubject(manuscriptMessagingVO.getSubject());
		createManuscriptMessaging.setCreatedAt(new Date());
		createManuscriptMessaging.setIsSeen(1);
		createManuscriptMessaging.setIsActive(1);
		createManuscriptMessaging.setMessage(manuscriptMessagingVO.getMessage());
		createManuscriptMessaging.setType(manuscriptMessagingVO.getType());
		ManuscriptMessaging data = manuscriptMessagingRepo.save(createManuscriptMessaging);
		
		MailQueue addMailQueue = new MailQueue();

		User userfrom = userRepo.getByID(manuscriptMessagingVO.getFromUserId());
		User userTo = userRepo.getByID(manuscriptMessagingVO.getToUserId());
		String userDetails = "<b>Email To :</b> "+userTo.getEmail()+"\n<b>Subject :</b> "+manuscriptMessagingVO.getSubject()+"\n";                     
		addMailQueue.setMailFrom(userfrom.getEmail());
		addMailQueue.setMailTo(userTo.getEmail());
		addMailQueue.setJournalId(manuscriptMessagingVO.getJournalId());
		addMailQueue.setManuscriptId(manuscriptMessagingVO.getManuscriptId());
		addMailQueue.setMailSubject(manuscriptMessagingVO.getSubject());
		addMailQueue.setMailBody(userDetails.concat(manuscriptMessagingVO.getMessage()));
		addMailQueue.setMail_send_date(new Date());
		addMailQueue.setIsActive(0);
		mailqueueRepo.saveAndFlush(addMailQueue);		
		return data;
	}

	@Override
	public List<ManuscriptMessagingVO> getMessage(ManuscriptMessagingVO manuscriptMessagingVO) {
		List<ManuscriptMessagingVO> addManuscriptMessagingVOList = new ArrayList<ManuscriptMessagingVO>();
		List<ManuscriptMessaging> getManuscriptMessagingData = null;

		// To User without manuscript and version
		if (manuscriptMessagingVO.getToUserId() != null && manuscriptMessagingVO.getToUserRole() != null
				&& manuscriptMessagingVO.getJournalId() != null && manuscriptMessagingVO.getManuscriptId() == null
				&& manuscriptMessagingVO.getManuscriptVersion() == null) {
			getManuscriptMessagingData = manuscriptMessagingRepo.getToMessage(manuscriptMessagingVO.getToUserId(),
					manuscriptMessagingVO.getToUserRole(), manuscriptMessagingVO.getJournalId());
		}

		// To user with manuscript and version
		if (manuscriptMessagingVO.getToUserId() != null && manuscriptMessagingVO.getToUserRole() != null
				&& manuscriptMessagingVO.getJournalId() != null && manuscriptMessagingVO.getManuscriptId() != null
				&& manuscriptMessagingVO.getManuscriptVersion() != null) {
			getManuscriptMessagingData = manuscriptMessagingRepo.getDataByToManuscript(
					manuscriptMessagingVO.getManuscriptId(), manuscriptMessagingVO.getManuscriptVersion(),
					manuscriptMessagingVO.getToUserId(), manuscriptMessagingVO.getToUserRole(),
					manuscriptMessagingVO.getJournalId());
		}
		// From user without manuscript and version
		if (manuscriptMessagingVO.getFromUserId() != null && manuscriptMessagingVO.getFromUserRole() != null
				&& manuscriptMessagingVO.getJournalId() != null && manuscriptMessagingVO.getManuscriptId() == null
				&& manuscriptMessagingVO.getManuscriptVersion() == null) {
			getManuscriptMessagingData = manuscriptMessagingRepo.getMessageFromMessage(
					manuscriptMessagingVO.getFromUserId(), manuscriptMessagingVO.getFromUserRole(),
					manuscriptMessagingVO.getJournalId());
		}
		// From user with manuscript and version
		if (manuscriptMessagingVO.getFromUserId() != null && manuscriptMessagingVO.getFromUserRole() != null
				&& manuscriptMessagingVO.getJournalId() != null && manuscriptMessagingVO.getManuscriptId() != null
				&& manuscriptMessagingVO.getManuscriptVersion() != null) {
			getManuscriptMessagingData = manuscriptMessagingRepo.getDataByFromManuscript(
					manuscriptMessagingVO.getManuscriptId(), manuscriptMessagingVO.getManuscriptVersion(),
					manuscriptMessagingVO.getFromUserId(), manuscriptMessagingVO.getFromUserRole(),
					manuscriptMessagingVO.getJournalId());
		}

		// From manuscript and manuscript version
		if( manuscriptMessagingVO.getManuscriptId()!=null && manuscriptMessagingVO.getManuscriptVersion()!=null) {
			getManuscriptMessagingData = manuscriptMessagingRepo.getManuscriptVersionData(manuscriptMessagingVO.getManuscriptId(),manuscriptMessagingVO.getManuscriptVersion());
		}
		
		
		if (getManuscriptMessagingData != null && !getManuscriptMessagingData.isEmpty()) {
			getManuscriptMessagingData.stream()
			.sorted((m1, m2) -> m2.getCreatedAt().compareTo(m1.getCreatedAt()))
			.forEach(i -> {
				ManuscriptMessagingVO addManuscriptMessagingVO = new ManuscriptMessagingVO();
				addManuscriptMessagingVO.setId(i.getId());
				addManuscriptMessagingVO.setManuscriptId(i.getManuscriptId());
				if(i.getManuscriptId()!=null) {
					ManuScript m= manuScriptRepo.getOne(i.getManuscriptId());
					if(m!=null) {
						addManuscriptMessagingVO.setManuscriptCode(m.getManuscriptCode());
					}
				}
				addManuscriptMessagingVO.setManuscriptVersion(i.getManuscriptVersion());
				addManuscriptMessagingVO.setJournalId(i.getJournalId());
				addManuscriptMessagingVO.setFromUserId(i.getFromUserId());
				addManuscriptMessagingVO.setFromUserRole(i.getFromUserRole());
				addManuscriptMessagingVO.setFromUserName(i.getFromUserName());
				addManuscriptMessagingVO.setSubject(i.getSubject());
				addManuscriptMessagingVO.setToUserId(i.getToUserId());
				addManuscriptMessagingVO.setToUserRole(i.getToUserRole());
				addManuscriptMessagingVO.setToUserName(i.getToUserName());
				addManuscriptMessagingVO.setCreatedAt(i.getCreatedAt());
				addManuscriptMessagingVO.setIsSeen(i.getIsSeen());
				addManuscriptMessagingVO.setIsActive(i.getIsActive());
				addManuscriptMessagingVO.setMessage(i.getMessage());
				addManuscriptMessagingVO.setType(i.getType());
				addManuscriptMessagingVOList.add(addManuscriptMessagingVO);
			});
		}
		return addManuscriptMessagingVOList;
	}

	@Override
	public List<DiscussionVo> getDiscussion(DiscussionVo discussionVo) {
		List<DiscussionVo> setDiscussionVo = new ArrayList<>();
		List<Discussion> getDiscussionData = discussionRepo.getDiscussionList(discussionVo.getJournalId(),
				discussionVo.getUserId(), discussionVo.getUserRole());
		if (getDiscussionData != null && !getDiscussionData.isEmpty()) {
			getDiscussionData.stream().forEach(i -> {
				DiscussionVo addDiscussionVo = new DiscussionVo();
				addDiscussionVo.setId(i.getId());
				addDiscussionVo.setJournalId(i.getJournalId());
				addDiscussionVo.setManuscriptId(i.getManuscriptId());
				if(i.getManuscriptId()!=null) {
					ManuScript m= manuScriptRepo.getOne(i.getManuscriptId());
					if(m!=null) {
						addDiscussionVo.setManuscriptCode(m.getManuscriptCode());
					}
				}
				addDiscussionVo.setManuscriptVersion(i.getManuscriptVersion());
				addDiscussionVo.setUserId(i.getUserId());
				addDiscussionVo.setUserRole(i.getUserRole());
				addDiscussionVo.setRecommendation(i.getRecommendation());
				addDiscussionVo.setDiscussionOnRole(i.getDiscussionOnRole());
				addDiscussionVo.setSubject(i.getSubject());
				addDiscussionVo.setMessage(i.getMessage());
				addDiscussionVo.setDiscussionVisibility(i.getDiscussionVisibility());
				addDiscussionVo.setCreatedDate(i.getCreatedDate());
				setDiscussionVo.add(addDiscussionVo);
			});
		}
		return setDiscussionVo;
	}

	@Override
	public String discussion(DiscussionVo discussionVo) {
		Discussion getDiscussionManuscriptdata = discussionRepo.getManuscriptDiscussion(discussionVo.getId(),
				discussionVo.getJournalId(), discussionVo.getUserId(), discussionVo.getUserRole(),
				discussionVo.getManuscriptId(), discussionVo.getManuscriptVersion());
		if (getDiscussionManuscriptdata != null) {
			getDiscussionManuscriptdata.setJournalId(discussionVo.getJournalId());
			getDiscussionManuscriptdata.setManuscriptId(discussionVo.getManuscriptId());
			getDiscussionManuscriptdata.setManuscriptVersion(discussionVo.getManuscriptVersion());
			getDiscussionManuscriptdata.setUserId(discussionVo.getUserId());
			getDiscussionManuscriptdata.setUserRole(discussionVo.getUserRole());
			getDiscussionManuscriptdata.setRecommendation(discussionVo.getRecommendation());
			getDiscussionManuscriptdata.setDiscussionOnRole(discussionVo.getDiscussionOnRole());
			getDiscussionManuscriptdata.setSubject(discussionVo.getSubject());
			getDiscussionManuscriptdata.setMessage(discussionVo.getMessage());
			getDiscussionManuscriptdata.setDiscussionVisibility(discussionVo.getDiscussionVisibility());
			discussionRepo.save(getDiscussionManuscriptdata);
			return "Discussion updated successfully";
		} else {
			Discussion addDiscussion = new Discussion();
			addDiscussion.setJournalId(discussionVo.getJournalId());
			addDiscussion.setManuscriptId(discussionVo.getManuscriptId());
			addDiscussion.setManuscriptVersion(discussionVo.getManuscriptVersion());
			addDiscussion.setUserId(discussionVo.getUserId());
			addDiscussion.setUserRole(discussionVo.getUserRole());
			addDiscussion.setRecommendation(discussionVo.getRecommendation());
			addDiscussion.setDiscussionOnRole(discussionVo.getDiscussionOnRole());
			addDiscussion.setSubject(discussionVo.getSubject());
			addDiscussion.setMessage(discussionVo.getMessage());
			addDiscussion.setDiscussionVisibility(discussionVo.getDiscussionVisibility());
			addDiscussion.setCreatedDate(new Date());
			discussionRepo.save(addDiscussion);
			return "Discussion saved successfully";
		}

	}

	@Override
	public String deleteDiscussion(DiscussionVo discussionVo) {
		discussionRepo.deleteById(discussionVo.getId());
		return "Discussion deleted successfully";
	}

	@Override
	public List<Discussion> getDataByJournalAndRole(ManuscriptMessagingVO manuscriptMessagingVO) {
		List<Discussion> discussion = null;
		if (manuscriptMessagingVO.getToUserRole() == 5) {
			discussion = discussionRepo.getDataForReviewer(manuscriptMessagingVO.getJournalId(),
					manuscriptMessagingVO.getToUserRole());
		} else {
			discussion = discussionRepo.getDataForEIC(manuscriptMessagingVO.getJournalId(),
					manuscriptMessagingVO.getToUserRole());
		}

		return discussion;
	}

	@Override
	public List<ManuscriptMessaging> getDataByManuscript(ManuscriptMessagingVO manuscriptMessagingVO) {
		List<ManuscriptMessaging> discussion = manuscriptMessagingRepo.getDataByManuscript(
				manuscriptMessagingVO.getManuscriptId(), manuscriptMessagingVO.getManuscriptVersion(),
				manuscriptMessagingVO.getToUserId(), manuscriptMessagingVO.getToUserRole());

		return discussion;
	}

}
