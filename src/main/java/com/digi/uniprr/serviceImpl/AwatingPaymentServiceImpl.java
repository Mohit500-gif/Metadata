package com.digi.uniprr.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.digi.uniprr.VO.ManagingEditorVO;
import com.digi.uniprr.VO.ManuscriptListVO;
import com.digi.uniprr.VO.ManuscriptManagingEditorVO;
import com.digi.uniprr.model.ManuScript;
import com.digi.uniprr.model.ManuscriptEditor;
import com.digi.uniprr.model.ManuscriptManagingEditor;
import com.digi.uniprr.model.ManuscriptTypeDetails;
import com.digi.uniprr.model.StatusMaster;
import com.digi.uniprr.model.User;
import com.digi.uniprr.repository.AuthoritiesHistoryRepo;
import com.digi.uniprr.repository.CountriesRepo;
import com.digi.uniprr.repository.JournalManuscriptTypeChargeRepo;
import com.digi.uniprr.repository.JournalPoliciesRepo;
import com.digi.uniprr.repository.JournalsRepo;
import com.digi.uniprr.repository.KeywordsRepo;
import com.digi.uniprr.repository.ManscriptScoreRepo;
import com.digi.uniprr.repository.ManuScriptAwatingPaymentRepo;
import com.digi.uniprr.repository.ManuScriptCoAuthorRepo;
import com.digi.uniprr.repository.ManuScriptFileRepo;
import com.digi.uniprr.repository.ManuScriptForStatusRepo;
import com.digi.uniprr.repository.ManuScriptKeywordsRepo;
import com.digi.uniprr.repository.ManuScriptRepo;
import com.digi.uniprr.repository.ManuScriptReviewerRepo;
import com.digi.uniprr.repository.ManuscriptEditorCommentRepo;
import com.digi.uniprr.repository.ManuscriptEditorCommentsRepo;
import com.digi.uniprr.repository.ManuscriptEditorRepo;
import com.digi.uniprr.repository.ManuscriptJournalSubjectRepo;
import com.digi.uniprr.repository.ManuscriptManagingEditorRepo;
import com.digi.uniprr.repository.ManuscriptReviewerCommentsRepo;
import com.digi.uniprr.repository.ManuscriptTypeDetailsRepo;
import com.digi.uniprr.repository.ManuscriptTypeJournalsRepo;
import com.digi.uniprr.repository.ManuscriptVisualizationRepo;
import com.digi.uniprr.repository.OtherMailRepo;
import com.digi.uniprr.repository.RoleRepo;
import com.digi.uniprr.repository.StatusMasterRepo;
import com.digi.uniprr.repository.UserJournalRepo;
import com.digi.uniprr.repository.UserLoginRepo;
import com.digi.uniprr.repository.UserRepo;
import com.digi.uniprr.service.AwatingPaymentService;
import com.digi.uniprr.service.EmailTriggerService;
import com.digi.uniprr.service.ManuScriptReviewerService;
import com.digi.uniprr.service.ManuScriptService;
import com.digi.uniprr.service.ManuscriptEditorService;
import com.digi.uniprr.utils.SendMailUtils;
@Service
@Transactional
public class AwatingPaymentServiceImpl implements AwatingPaymentService {
	
	@Value("${uniprrFolderPath}")
	private String UPLOAD_FOLDER;

	@Value("${awsFileIp}")
	private String AWS_FILEPATH;

	@Autowired
	JournalsRepo journalsRepo;
	
	@Autowired
	StatusMasterRepo statusMasterRepo;
	

	@Autowired
	KeywordsRepo keywordsRepo;

	@Autowired
	ManuScriptKeywordsRepo manuscriptKeywordsRepo;

	@Autowired
	ManuScriptRepo manuScriptRepo;

	@Autowired
	ManuScriptForStatusRepo manuScriptForStatusRepo;

	@Autowired
	AuthoritiesHistoryRepo authoritiesHistoryRepo;

	@Autowired
	ManuscriptEditorCommentsRepo manuscriptEditorCommentsRepo;

	@Autowired
	ManuScriptReviewerRepo manuScriptReviewerRepo;

	@Autowired
	EmailTriggerService emailTriggerService;

	@Autowired
	RoleRepo roleRepo;

	@Autowired
	ManuscriptEditorService manuscriptEditorService;

	@Autowired
	ManuScriptReviewerService manuScriptReviewerService;

	@Autowired
	ManuScriptAwatingPaymentRepo manuScriptAwatingPaymentRepo;

	@Autowired
	UserRepo userRepo;

	@Autowired
	ManuscriptTypeDetailsRepo manuscriptTypeDetailsRepo;

	@Autowired
	ManuScriptCoAuthorRepo manuScriptCoAuthorRepo;

	@Autowired
	ManuscriptTypeJournalsRepo manuscriptTypeJournalsRepo;

	@Autowired
	ManuscriptManagingEditorRepo manuscriptManagingEditorRepo;

	@Autowired
	ManuscriptEditorRepo manuscriptEditorRepo;

	@Autowired
	ManuscriptReviewerCommentsRepo manuscriptReviewerCommentsRepo;

	@Autowired
	CountriesRepo countriesRepo;

	@Autowired
	ManscriptScoreRepo manscriptScoreRepo;

	@Autowired
	ManuScriptFileRepo manuScriptFileRepo;

	@Autowired
	Environment env;

	@Autowired
	SendMailUtils mailUtils;

	@Autowired
	ManuscriptVisualizationRepo manuscriptVisualizationRepo;

	@Autowired
	JournalManuscriptTypeChargeRepo journalManuscriptTypeChargeRepo;

	@Autowired
	ManuscriptJournalSubjectRepo manuscriptJournalSubjectRepo;

	@Autowired
	UserJournalRepo userJournalRepo;

	@Autowired
	JournalPoliciesRepo journalPoliciesRepo;

	@Autowired
	OtherMailRepo otherMailRepo;

	@Autowired
	Environment environment;

	@Autowired
	ManuscriptEditorCommentRepo manuscriptEditorCommentRepo;
    @Autowired
    UserLoginRepo userLoginRepo;

	JSONObject obj = new JSONObject();


	@Override
	public List<ManuscriptListVO> getAwatingPaymentManuscript(ManuscriptListVO manuscriptVO) {

		List<ManuscriptListVO> newList = new ArrayList<>();

		List<ManuScript> list = new ArrayList<>();
		Pageable paging = PageRequest.of(manuscriptVO.getPageNo(), manuscriptVO.getLimit());

       if(manuscriptVO.getSearch() != null && manuscriptVO.getSearch() != "")
   	       list = manuScriptAwatingPaymentRepo.getAllPayedManuscriptSearch(manuscriptVO.getManuscriptStatusId(), manuscriptVO.getSearch(),paging);
       else
           list=manuScriptAwatingPaymentRepo.getAllPayedManuscript(manuscriptVO.getManuscriptStatusId(), paging);

		if (list != null ) {
			for (ManuScript data : list) {

				ManuscriptListVO manuscriptListVO = new ManuscriptListVO();
				manuscriptListVO.setJournalId(data.getJournalId());
				manuscriptListVO.setManagingEditorId(data.getAssignedUser());
				manuscriptListVO.setCommentForProduction(data.getCommentForProduction());
				manuscriptListVO.setManuscripCode(data.getManuscriptCode());
				manuscriptListVO.setProductionDate(data.getProductionDate());
				manuscriptListVO.setManuscripCode(data.getManuscriptCode());
				manuscriptListVO.setManuscriptAuthorId(data.getAuthorId());
				manuscriptListVO.setManuscripTitle(data.getManuscriptTitle());
				manuscriptListVO.setManuScriptId(data.getManuScriptId());
				manuscriptListVO.setManuscriptVersion(data.getManuscriptVersion());
				manuscriptListVO.setSubmissionDate(data.getSubmissionDate());
				manuscriptListVO.setSubmittedBy(data.getSubmittedBy());
				manuscriptListVO.setCreatedBy(data.getCreatedBy());
				manuscriptListVO.setCreatedOn(data.getCreatedOn());
				manuscriptListVO.setUpdatedBy(data.getUpdatedBy());
				manuscriptListVO.setUpdatedOn(data.getUpdatedOn());
				manuscriptListVO.setManuStatusCode(data.getStatusMaster().getStatusValue());
				manuscriptListVO.setStatusValueForAuthor(data.getStatusMaster().getStatusValueForAuthor());
				manuscriptListVO.setStatusValueForEditor(data.getStatusMaster().getStatusValueForEditor());
				manuscriptListVO.setAuthorName(data.getUser().getFirstName() + " " + data.getUser().getLastName());
				manuscriptListVO.setJournalTitle(data.getJournal().getJournalTitle());
				manuscriptListVO.setJournalCode(data.getJournal().getJournalCode());
				manuscriptListVO.setManuStatusId(data.getManuscriptStatusId());
				manuscriptListVO.setWithdrawalComment(data.getWithdrawalComment());
				manuscriptListVO.setManagingEditorId(data.getAssignedUser());
				manuscriptListVO.setRoleId(data.getAssignedUserRole());
			    manuscriptListVO.setManuScriptType(data.getManuscriptTypeDetails().getManuscriptType());
				newList.add(manuscriptListVO);
			}
		}

		return newList;
	
	}


	@Override
	public int getTotalAwatingPaymentManuscript(ManuscriptListVO manuscriptVO) {

		Integer totalAwatingPaymentManuscript = manuScriptAwatingPaymentRepo.getTotalAwatingPaymentManuscript(manuscriptVO.getManuscriptStatusId());
		return totalAwatingPaymentManuscript;
	}


	@Override
	public int getTotalAwatingPaymentManuscriptSearch(ManuscriptListVO manuscriptVO) {
		
		Integer totalAwatingPaymentManuscript = manuScriptAwatingPaymentRepo.getTotalAwatingPaymentManuscriptSearch(manuscriptVO.getManuscriptStatusId(),manuscriptVO.getSearch());
		return totalAwatingPaymentManuscript;
	}

}
