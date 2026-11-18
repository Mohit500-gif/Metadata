package com.digi.uniprr.serviceImpl;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digi.uniprr.VO.AuthorFeedbackVO;
import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.model.AuthorFeedback;
import com.digi.uniprr.repository.AuthorFeedbackRepo;
import com.digi.uniprr.service.AuthorFeedbackService;

@Service
@Transactional
public class AuthorFeedbackServiceImpl implements AuthorFeedbackService {

	
	@Autowired
	AuthorFeedbackRepo authorFeedbackRepo;
	
	@Override
	public void saveAuthorFeedback(AuthorFeedbackVO authorFeedbackVO) {
		boolean status = false;
		if(authorFeedbackVO!=null && authorFeedbackVO.getQuestionId()!=null) {
			for(int i =0;  i<authorFeedbackVO.getQuestionId().size(); i++) {
				AuthorFeedback authorFeedback = new AuthorFeedback();
				authorFeedback.setUserId(authorFeedbackVO.getUserId());
				authorFeedback.setJournalId(authorFeedbackVO.getJournalId());
				authorFeedback.setQuestionId(authorFeedbackVO.getQuestionId().get(i));
				authorFeedback.setAnswer(authorFeedbackVO.getAnswer().get(i));
				authorFeedback.setIsActive(1);
				authorFeedbackRepo.save(authorFeedback);
			}
		}
	}

	@Override
	public void saveAuthorFeedback(IDVO id) {
		AuthorFeedback authorFeedback = new AuthorFeedback();
		String responsDat="";
		if(id.getJournalCode()!=null && !id.getJournalCode().isEmpty()) {
			for (String data : id.getJournalCode()) {
				responsDat = responsDat.concat(data).concat("@@@");
			}
		}
		authorFeedback.setRoleId(id.getRoleId());
		authorFeedback.setJournalId(id.getJournalId());
		authorFeedback.setUserId(id.getUserId());
		authorFeedback.setResponse(responsDat);
		authorFeedback.setIsActive(1);
		authorFeedbackRepo.save(authorFeedback);
	}

	

}
