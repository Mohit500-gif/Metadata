package com.digi.uniprr.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.VO.JournalFileTypeVO;
import com.digi.uniprr.model.FeedbackQuestionMaster;
import com.digi.uniprr.model.JournalFileType;
import com.digi.uniprr.repository.FeedbackQuestionMasterRepo;
import com.digi.uniprr.repository.JournalFileTypeRepo;
import com.digi.uniprr.service.JournalFileTypeService;

@Service
@Transactional

public class JournalFileTypeServiceImpl implements JournalFileTypeService {

	@Autowired
	private JournalFileTypeRepo journalFileTypeRepo;

	@Autowired
	FeedbackQuestionMasterRepo feedbackQuestionMasterRepo;

	public List<JournalFileType> getDetails() {
		return journalFileTypeRepo.findAllFileType();
	}

	@Override
	public List<JournalFileTypeVO> getDataByJournal(IDVO id) {

		List<JournalFileTypeVO> JournalFileTypeVOList = new ArrayList();
		List<FeedbackQuestionMaster> feedbackQuestionMasterList = new ArrayList();
		List<JournalFileType> JournalFileType = journalFileTypeRepo.findAllFileType();

		List<FeedbackQuestionMaster> feedbackQuestionMaster = feedbackQuestionMasterRepo
				.getDataByJournal(id.getJournalId());

		if (JournalFileType != null && !JournalFileType.isEmpty()) {
			for (JournalFileType journalFileType2 : JournalFileType) {
				JournalFileTypeVO journalFileTypeVO = new JournalFileTypeVO();
				journalFileTypeVO.setId(journalFileType2.getId());
				journalFileTypeVO.setCreatedBy(journalFileType2.getCreatedBy());
				journalFileTypeVO.setCreatedOn(journalFileType2.getCreatedOn());
				journalFileTypeVO.setFileType(journalFileType2.getFileType());
				journalFileTypeVO.setIsActive(journalFileType2.getIsActive());
				// journalFileTypeVO.setIsMandatory(journalFileType2.);
				journalFileTypeVO.setUpdatedBy(journalFileType2.getUpdatedBy());
				journalFileTypeVO.setUpdatedOn(journalFileType2.getUpdatedOn());
				JournalFileTypeVOList.add(journalFileTypeVO);
			}
		}
		if (feedbackQuestionMaster != null && !feedbackQuestionMaster.isEmpty()) {
			for (FeedbackQuestionMaster feedbackQuestionMaster2 : feedbackQuestionMaster) {
				if (feedbackQuestionMaster2.getFormName() != null
						&& !feedbackQuestionMaster2.getFormName().trim().equalsIgnoreCase("")) {
					JournalFileTypeVO journalFileTypeVO = new JournalFileTypeVO();
					journalFileTypeVO.setId(feedbackQuestionMaster2.getId());
					journalFileTypeVO.setCreatedBy(feedbackQuestionMaster2.getCreatedBy());
					journalFileTypeVO.setCreatedOn(feedbackQuestionMaster2.getCreatedOn());
					journalFileTypeVO.setFileType(feedbackQuestionMaster2.getFormName());
					journalFileTypeVO.setIsActive(feedbackQuestionMaster2.getIsActive());
					journalFileTypeVO.setIsMandatory(feedbackQuestionMaster2.getFormIsMandatory());
					journalFileTypeVO.setUpdatedBy(feedbackQuestionMaster2.getUpdatedBy());
					journalFileTypeVO.setUpdatedOn(feedbackQuestionMaster2.getUpdatedOn());
					JournalFileTypeVOList.add(journalFileTypeVO);
				}
			}
		}
//		if(id.getJournalId()!=null && id.getJournalId()==7)
//		{
//			JournalFileTypeVOList
//					.stream()
//					.filter(journalFileTypeVO -> "First Page file/Cover Letter".equals(journalFileTypeVO.getFileType()))
//					.forEach(journalFileTypeVO -> journalFileTypeVO.setFileType("Title Page"));
//		}
		
		 if (id.getJournalId() != null && (id.getJournalId() == 7 || id.getJournalId()== 385)) {
			   JournalFileTypeVOList.removeIf(item -> item.getFileType() != null
			  && item.getFileType().equalsIgnoreCase("Supplementary files") );
			   }
			else {
				JournalFileTypeVOList.removeIf(item -> item.getFileType() != null
						  && item.getFileType().equalsIgnoreCase("Supplementary File – for Review") );
			}
	 
 
		
		return JournalFileTypeVOList;

	}

}
