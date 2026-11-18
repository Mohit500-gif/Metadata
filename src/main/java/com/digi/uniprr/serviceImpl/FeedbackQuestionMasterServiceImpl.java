package com.digi.uniprr.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digi.uniprr.VO.AuthorFeedbackVO;
import com.digi.uniprr.VO.FeedbackQuestionMasterVO;
import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.VO.ManuscriptListVO;
import com.digi.uniprr.model.AuthorFeedback;
import com.digi.uniprr.model.FeedbackQuestionMaster;
import com.digi.uniprr.model.ManuScript;
import com.digi.uniprr.model.ManuscriptTypeDetails;
import com.digi.uniprr.model.User;
import com.digi.uniprr.repository.AuthorFeedbackRepo;
import com.digi.uniprr.repository.FeedbackQuestionMasterRepo;
import com.digi.uniprr.repository.ManuScriptRepo;
import com.digi.uniprr.repository.ManuscriptTypeDetailsRepo;
import com.digi.uniprr.repository.UserRepo;
import com.digi.uniprr.service.FeedbackQuestionMasterService;

@Service
@Transactional
public class FeedbackQuestionMasterServiceImpl implements FeedbackQuestionMasterService {

	@Autowired
	FeedbackQuestionMasterRepo feedbackQuestionMasterRepo;

	@Autowired
	ManuscriptTypeDetailsRepo manuscriptTypeDetailsRepo;

	@Autowired
	AuthorFeedbackRepo authorFeedbackRepo;

	@Autowired
	UserRepo userRepo;

	@Autowired
	ManuScriptRepo manuScriptRepo;

	@Override
	public List<FeedbackQuestionMasterVO> getFeedbackList() {
		List<FeedbackQuestionMasterVO> FeedbackQuestionMasterVO = new ArrayList<FeedbackQuestionMasterVO>();
		List<FeedbackQuestionMaster> data = feedbackQuestionMasterRepo.getActiveData();
		if (data != null && !data.isEmpty()) {
			for (FeedbackQuestionMaster feedbackQuestionMaster : data) {
				List<String> ansList = new ArrayList<>();
				List<String> manuscriptType = new ArrayList<>();
				FeedbackQuestionMasterVO feedbackQuestionData = new FeedbackQuestionMasterVO();
				if (feedbackQuestionMaster.getAnswer() != null) {
					String str[] = feedbackQuestionMaster.getAnswer().split("@@@");
					for (String ansData : str) {
						ansList.add(ansData);
					}
				}
				if (feedbackQuestionMaster.getManuscriptType() != null) {
					String str[] = feedbackQuestionMaster.getManuscriptType().split("@@@");
					for (String ansData : str) {
						manuscriptType.add(ansData);
					}
				}

				feedbackQuestionData.setId(feedbackQuestionMaster.getId());
				feedbackQuestionData.setQuestion(feedbackQuestionMaster.getQuestions());
				feedbackQuestionData.setQuestionType(feedbackQuestionMaster.getQuestionType());
				feedbackQuestionData.setUserId(feedbackQuestionMaster.getCreatedBy());
				feedbackQuestionData.setJournalId(feedbackQuestionMaster.getJournalId());
				feedbackQuestionData.setRole(feedbackQuestionMaster.getRole());
				feedbackQuestionData.setType(feedbackQuestionMaster.getType());
				feedbackQuestionData.setUploadForm(feedbackQuestionMaster.getUploadForm());
				feedbackQuestionData.setFormName(feedbackQuestionMaster.getFormName());
				feedbackQuestionData.setFormIsMandatory(feedbackQuestionMaster.getFormIsMandatory());
				feedbackQuestionData.setRevisedManuscript(feedbackQuestionMaster.getRevisedManuscript());
				feedbackQuestionData.setAnswer(ansList);
				feedbackQuestionData.setManuscriptType(manuscriptType);

				FeedbackQuestionMasterVO.add(feedbackQuestionData);
			}
		}
		return FeedbackQuestionMasterVO;
	}

	@Override
	public List<FeedbackQuestionMasterVO> getFeedbackListById(Integer id) {

		List<FeedbackQuestionMasterVO> FeedbackQuestionMasterVO = new ArrayList<FeedbackQuestionMasterVO>();
		List<FeedbackQuestionMaster> data = feedbackQuestionMasterRepo.getFeedbackListById(id);
		if (data != null && !data.isEmpty()) {
			for (FeedbackQuestionMaster feedbackQuestionMaster : data) {
				List<String> ansList = new ArrayList<>();
				List<String> manuscriptType = new ArrayList<>();
				FeedbackQuestionMasterVO feedbackQuestionData = new FeedbackQuestionMasterVO();
				if (feedbackQuestionMaster.getAnswer() != null) {
					String str[] = feedbackQuestionMaster.getAnswer().split("@@@");
					for (String ansData : str) {
						ansList.add(ansData);
					}
				}
				if (feedbackQuestionMaster.getManuscriptType() != null) {
					String str[] = feedbackQuestionMaster.getManuscriptType().split("@@@");
					for (String ansData : str) {
						manuscriptType.add(ansData);
					}
				}

				feedbackQuestionData.setId(feedbackQuestionMaster.getId());
				feedbackQuestionData.setQuestion(feedbackQuestionMaster.getQuestions());
				feedbackQuestionData.setQuestionType(feedbackQuestionMaster.getQuestionType());
				feedbackQuestionData.setUserId(feedbackQuestionMaster.getCreatedBy());
				feedbackQuestionData.setJournalId(feedbackQuestionMaster.getJournalId());
				feedbackQuestionData.setRole(feedbackQuestionMaster.getRole());
				feedbackQuestionData.setType(feedbackQuestionMaster.getType());
				feedbackQuestionData.setFormName(feedbackQuestionMaster.getFormName());
				feedbackQuestionData.setUploadForm(feedbackQuestionMaster.getUploadForm());
				feedbackQuestionData.setFormIsMandatory(feedbackQuestionMaster.getFormIsMandatory());
				feedbackQuestionData.setRevisedManuscript(feedbackQuestionMaster.getRevisedManuscript());
				feedbackQuestionData.setAnswer(ansList);
				feedbackQuestionData.setManuscriptType(manuscriptType);
				feedbackQuestionData.setComment(feedbackQuestionMaster.getComment());

				FeedbackQuestionMasterVO.add(feedbackQuestionData);
			}
		}
		return FeedbackQuestionMasterVO;
	}

	@Override
	public List<ManuscriptListVO> getCheckListById(Integer id, Integer manuscript_id) {
		List<ManuscriptListVO> manuscriptListVO = new ArrayList<ManuscriptListVO>();
		if (manuscript_id > 0) {
			ManuscriptListVO manuscriptList = new ManuscriptListVO();
			ManuScript checklist = manuScriptRepo.getChecklistOnDatabase(id, manuscript_id);
			if (checklist.getManuscriptChecklistResponse() == null
					|| checklist.getManuscriptChecklistResponse() == "") {
				manuscriptListVO.isEmpty();
			} else {
				manuscriptList.setManuscriptChecklistResponse(checklist.getManuscriptChecklistResponse());
				manuscriptListVO.add(manuscriptList);
			}
		}
		return manuscriptListVO;
	}

	@Override
	public void createQuestions(List<FeedbackQuestionMasterVO> feedbackQuestionMasterVO) {
		String ansData = "";
		String manuscriptTypeData = "";
		if (feedbackQuestionMasterVO != null && !feedbackQuestionMasterVO.isEmpty()) {
			for (FeedbackQuestionMasterVO data : feedbackQuestionMasterVO) {
				FeedbackQuestionMaster feedbackQuestionMaster = new FeedbackQuestionMaster();
				if (data.getAnswer() != null && !data.getAnswer().isEmpty()) {
					for (String answerList : data.getAnswer()) {
						ansData = ansData.concat(answerList).concat("@@@");
					}
				}
				if (data.getManuscriptType() != null && !data.getManuscriptType().isEmpty()) {
					for (String answerList : data.getManuscriptType()) {
						manuscriptTypeData = manuscriptTypeData.concat(answerList).concat("@@@");
					}
				}
				if (data.getUserId() != null) {
					feedbackQuestionMaster.setCreatedBy(data.getUserId());
				}
				feedbackQuestionMaster.setQuestions(data.getQuestion());
				feedbackQuestionMaster.setAnswer(ansData);
				feedbackQuestionMaster.setManuscriptType(manuscriptTypeData);
				feedbackQuestionMaster.setQuestionType(data.getQuestionType());
				feedbackQuestionMaster.setRole(data.getRole());
				feedbackQuestionMaster.setFormName(data.getFormName());
				feedbackQuestionMaster.setJournalId(data.getJournalId());
				feedbackQuestionMaster.setType(data.getType());
				feedbackQuestionMaster.setUploadForm(data.getUploadForm());
				feedbackQuestionMaster.setFormIsMandatory(data.getFormIsMandatory());
				feedbackQuestionMaster.setRevisedManuscript(data.getRevisedManuscript());
				feedbackQuestionMaster.setComment(data.getComment());
				feedbackQuestionMaster.setIsActive(1);

				feedbackQuestionMasterRepo.save(feedbackQuestionMaster);
			}
		}

	}

	@Override
	public void updateeQuestions(FeedbackQuestionMasterVO data) {
		String ansData = "";
		String manuscriptTypeData = "";
		FeedbackQuestionMaster feedbackQuestionMaster = feedbackQuestionMasterRepo.getOne(data.getId());
		if (data.getAnswer() != null && !data.getAnswer().isEmpty()) {
			for (String answerList : data.getAnswer()) {
				ansData = ansData.concat(answerList).concat("@@@");
			}
		}
		if (data.getManuscriptType() != null && !data.getManuscriptType().isEmpty()) {
			for (String answerList : data.getManuscriptType()) {
				manuscriptTypeData = manuscriptTypeData.concat(answerList).concat("@@@");
			}
		}
		if (data.getUserId() != null) {
			feedbackQuestionMaster.setUpdatedBy(data.getUserId());
		}
		feedbackQuestionMaster.setQuestions(data.getQuestion());
		feedbackQuestionMaster.setAnswer(ansData);
		feedbackQuestionMaster.setManuscriptType(manuscriptTypeData);
		feedbackQuestionMaster.setQuestionType(data.getQuestionType());
		feedbackQuestionMaster.setRole(data.getRole());
		feedbackQuestionMaster.setFormName(data.getFormName());
		feedbackQuestionMaster.setJournalId(data.getJournalId());
		feedbackQuestionMaster.setType(data.getType());
		feedbackQuestionMaster.setUploadForm(data.getUploadForm());
		feedbackQuestionMaster.setFormIsMandatory(data.getFormIsMandatory());
		feedbackQuestionMaster.setRevisedManuscript(data.getRevisedManuscript());
		feedbackQuestionMaster.setComment(data.getComment());
		feedbackQuestionMaster.setIsActive(1);

		feedbackQuestionMasterRepo.save(feedbackQuestionMaster);

	}

	@Override
	public void deleteQuestions(FeedbackQuestionMasterVO feedbackQuestionMasterVO) {
		FeedbackQuestionMaster feedbackQuestionMaster = feedbackQuestionMasterRepo
				.getOne(feedbackQuestionMasterVO.getId());
		feedbackQuestionMaster.setIsActive(0);
		feedbackQuestionMasterRepo.save(feedbackQuestionMaster);

	}

	@Override
	public List<FeedbackQuestionMasterVO> geMasterList() {

		List<FeedbackQuestionMasterVO> FeedbackQuestionMasterVO = new ArrayList<FeedbackQuestionMasterVO>();
		List<FeedbackQuestionMaster> data = feedbackQuestionMasterRepo.geMasterList();
		if (data != null && !data.isEmpty()) {
			for (FeedbackQuestionMaster feedbackQuestionMaster : data) {
				List<String> ansList = new ArrayList<>();
				List<String> manuscriptType = new ArrayList<>();
				FeedbackQuestionMasterVO feedbackQuestionData = new FeedbackQuestionMasterVO();
				if (feedbackQuestionMaster.getAnswer() != null) {
					String str[] = feedbackQuestionMaster.getAnswer().split("@@@");
					for (String ansData : str) {
						ansList.add(ansData);
					}
				}
				if (feedbackQuestionMaster.getManuscriptType() != null) {
					String str[] = feedbackQuestionMaster.getManuscriptType().split("@@@");
					for (String ansData : str) {
						manuscriptType.add(ansData);
					}
				}

				feedbackQuestionData.setId(feedbackQuestionMaster.getId());
				feedbackQuestionData.setQuestion(feedbackQuestionMaster.getQuestions());
				feedbackQuestionData.setQuestionType(feedbackQuestionMaster.getQuestionType());
				feedbackQuestionData.setUserId(feedbackQuestionMaster.getCreatedBy());
				feedbackQuestionData.setJournalId(feedbackQuestionMaster.getJournalId());
				feedbackQuestionData.setRole(feedbackQuestionMaster.getRole());
				feedbackQuestionData.setFormName(feedbackQuestionMaster.getFormName());
				feedbackQuestionData.setType(feedbackQuestionMaster.getType());
				feedbackQuestionData.setUploadForm(feedbackQuestionMaster.getUploadForm());
				feedbackQuestionData.setFormIsMandatory(feedbackQuestionMaster.getFormIsMandatory());
				feedbackQuestionData.setRevisedManuscript(feedbackQuestionMaster.getRevisedManuscript());
				feedbackQuestionData.setAnswer(ansList);
				feedbackQuestionData.setManuscriptType(manuscriptType);

				FeedbackQuestionMasterVO.add(feedbackQuestionData);
			}
		}
		return FeedbackQuestionMasterVO;

	}

	@Override
	public List<FeedbackQuestionMasterVO> listByJournalAndRole(IDVO id) {

		List<FeedbackQuestionMasterVO> FeedbackQuestionMasterVO = new ArrayList<FeedbackQuestionMasterVO>();
		String role = "";
		if (id.getRoleId() == 4) {
			role = "AUTHOR";
		} else if (id.getRoleId() == 5) {
			role = "REVIEWER";
		}
		List<FeedbackQuestionMaster> data = feedbackQuestionMasterRepo.listByJournalAndRole(id.getJournalId(), role);
		if (data != null && !data.isEmpty()) {
			for (FeedbackQuestionMaster feedbackQuestionMaster : data) {
				List<String> ansList = new ArrayList<>();
				List<String> manuscriptType = new ArrayList<>();
				FeedbackQuestionMasterVO feedbackQuestionData = new FeedbackQuestionMasterVO();
				if (feedbackQuestionMaster.getAnswer() != null) {
					String str[] = feedbackQuestionMaster.getAnswer().split("@@@");
					for (String ansData : str) {
						ansList.add(ansData);
					}
				}
				if (feedbackQuestionMaster.getManuscriptType() != null) {
					String str[] = feedbackQuestionMaster.getManuscriptType().split("@@@");
					for (String ansData : str) {
						manuscriptType.add(ansData);
					}
				}

				feedbackQuestionData.setId(feedbackQuestionMaster.getId());
				feedbackQuestionData.setQuestion(feedbackQuestionMaster.getQuestions());
				feedbackQuestionData.setQuestionType(feedbackQuestionMaster.getQuestionType());
				feedbackQuestionData.setUserId(feedbackQuestionMaster.getCreatedBy());
				feedbackQuestionData.setJournalId(feedbackQuestionMaster.getJournalId());
				feedbackQuestionData.setFormName(feedbackQuestionMaster.getFormName());
				feedbackQuestionData.setRole(feedbackQuestionMaster.getRole());
				feedbackQuestionData.setType(feedbackQuestionMaster.getType());
				feedbackQuestionData.setUploadForm(feedbackQuestionMaster.getUploadForm());
				feedbackQuestionData.setFormIsMandatory(feedbackQuestionMaster.getFormIsMandatory());
				feedbackQuestionData.setRevisedManuscript(feedbackQuestionMaster.getRevisedManuscript());
				feedbackQuestionData.setAnswer(ansList);
				feedbackQuestionData.setManuscriptType(manuscriptType);

				FeedbackQuestionMasterVO.add(feedbackQuestionData);
			}
		}
		return FeedbackQuestionMasterVO;

	}

	@Override
	public List<FeedbackQuestionMasterVO> getQuestionByManuscriptType(IDVO id) {
		List<FeedbackQuestionMasterVO> feedbackList = new ArrayList();
		ManuscriptTypeDetails manuscriptTypeDetails = manuscriptTypeDetailsRepo
				.getDetailsFromManuscriptIdForBundaling(id.getManuscriptId(), id.getManuscriptVersion());
		List<FeedbackQuestionMaster> data;
		if (id.getManuscriptStatus() != null && id.getManuscriptStatus() != "") {
			data = feedbackQuestionMasterRepo.getFeedbackListByIdReviewer(id.getJournalId(), id.getManuscriptStatus());
		} else {
			data = feedbackQuestionMasterRepo.getFeedbackListByIdReviewerForNonRevision(id.getJournalId());
		}

		if (manuscriptTypeDetails != null && !manuscriptTypeDetails.equals("") && data != null && !data.isEmpty()) {
			for (FeedbackQuestionMaster feedbackQuestionMaster : data) {
				String type[] = feedbackQuestionMaster.getManuscriptType().split("@@@");
				for (String typeName : type) {
					if (typeName.trim().equalsIgnoreCase(manuscriptTypeDetails.getManuscriptType().trim())) {
						List<String> ansList = new ArrayList<>();
						List<String> manuscriptType = new ArrayList<>();
						FeedbackQuestionMasterVO feedbackQuestionData = new FeedbackQuestionMasterVO();
						if (feedbackQuestionMaster.getAnswer() != null) {
							String str[] = feedbackQuestionMaster.getAnswer().split("@@@");
							for (String ansData : str) {
								ansList.add(ansData);
							}
						}
						if (feedbackQuestionMaster.getManuscriptType() != null) {
							String str[] = feedbackQuestionMaster.getManuscriptType().split("@@@");
							for (String ansData : str) {
								manuscriptType.add(ansData);
							}
						}

						feedbackQuestionData.setId(feedbackQuestionMaster.getId());
						feedbackQuestionData.setQuestion(feedbackQuestionMaster.getQuestions());
						feedbackQuestionData.setQuestionType(feedbackQuestionMaster.getQuestionType());
						feedbackQuestionData.setUserId(feedbackQuestionMaster.getCreatedBy());
						feedbackQuestionData.setJournalId(feedbackQuestionMaster.getJournalId());
						feedbackQuestionData.setFormName(feedbackQuestionMaster.getFormName());
						feedbackQuestionData.setRole(feedbackQuestionMaster.getRole());
						feedbackQuestionData.setType(feedbackQuestionMaster.getType());
						feedbackQuestionData.setUploadForm(feedbackQuestionMaster.getUploadForm());
						feedbackQuestionData.setFormIsMandatory(feedbackQuestionMaster.getFormIsMandatory());
						feedbackQuestionData.setRevisedManuscript(feedbackQuestionMaster.getRevisedManuscript());
						feedbackQuestionData.setAnswer(ansList);
						feedbackQuestionData.setManuscriptType(manuscriptType);

						feedbackList.add(feedbackQuestionData);
					}
				}
			}
		}
		return feedbackList;
	}

	@Override
	public List<AuthorFeedbackVO> getAuthorFeedback(IDVO id) {
		List<AuthorFeedbackVO> add = new ArrayList<>();
		List<AuthorFeedback> p = authorFeedbackRepo.getAuthorFeedback(id.getJournalId(), id.getRoleId());
		if (p != null && !p.isEmpty()) {
			p.stream().forEach(i -> {
				AuthorFeedbackVO j = new AuthorFeedbackVO();
				List<String> ansFinalList = new ArrayList();
				j.setId(i.getId());
				j.setResponse(i.getResponse());
				j.setJournalId(i.getJournalId());
				j.setCreatedDate(i.getCreatedOn());
				j.setUserId(i.getUserId());
				String[] ansList = i.getResponse().split("@@@");
				for (String ansData : ansList) {
					ansFinalList.add(ansData);
				}
				j.setAnswer(ansFinalList);
				User l = userRepo.getOne(i.getUserId());
				if (l != null) {
					j.setSalutation(l.getSalutation());
					j.setFirstName(l.getFirstName());
					j.setMiddleName(l.getMiddleName());
					j.setLastName(l.getLastName());
				}
				add.add(j);
			});
		}
		return add;
	}

}
