package com.digi.uniprr.serviceImpl;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.persistence.Tuple;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.digi.uniprr.VO.AdminJournalVo;
import com.digi.uniprr.VO.ArticleDetailsVO;
import com.digi.uniprr.VO.AuthorTakenTimeVO;
import com.digi.uniprr.VO.EditorTakenTimeVO;
import com.digi.uniprr.VO.ExternalArticleDetailsVO;
import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.VO.JournalDetailsVo;
import com.digi.uniprr.VO.JournalListVO;
import com.digi.uniprr.VO.JournalPerfomanceForAdminVO;
import com.digi.uniprr.VO.JournalPerfomanceForAuthorVO;
import com.digi.uniprr.VO.JournalPerfomanceForEditorVO;
import com.digi.uniprr.VO.JournalPerfomanceForReviewerVO;
import com.digi.uniprr.VO.JournalPerformanceVo;
import com.digi.uniprr.VO.JournalPoliciesVO;
import com.digi.uniprr.VO.JournalVO;
import com.digi.uniprr.VO.ManuscriptListVO;
import com.digi.uniprr.VO.ManuscriptReportDataVO;
import com.digi.uniprr.VO.ReviewersTakenTimeVO;
import com.digi.uniprr.VO.UserVO;
import com.digi.uniprr.model.AuthoritiesHistory;
import com.digi.uniprr.model.Journal;
import com.digi.uniprr.model.JournalChargeDetails;
import com.digi.uniprr.model.JournalChargeFlagHistory;
import com.digi.uniprr.model.JournalMetadata;
import com.digi.uniprr.model.JournalPolicies;
import com.digi.uniprr.model.ManuScript;
import com.digi.uniprr.model.ManuscriptTypeJournal;
import com.digi.uniprr.model.User;
import com.digi.uniprr.model.UserJournal;
import com.digi.uniprr.repository.JournalChargeDetailsRepo;
import com.digi.uniprr.repository.JournalChargeFlagHistoryRepo;
import com.digi.uniprr.repository.JournalMetadataRepo;
import com.digi.uniprr.repository.JournalPoliciesRepo;
import com.digi.uniprr.repository.JournalsRepo;
import com.digi.uniprr.repository.ManuScriptRepo;
import com.digi.uniprr.repository.ManuScriptReviewerRepo;
import com.digi.uniprr.repository.ManuscriptTypeJournalsRepo;
import com.digi.uniprr.repository.UserJournalRepo;
import com.digi.uniprr.repository.UserRepo;
import com.digi.uniprr.service.HttpService;
import com.digi.uniprr.service.JournalService;
import com.digi.uniprr.utils.AppConstants;
import com.digi.uniprr.utils.HttpServiceRequestInfo;
import com.digi.uniprr.utils.JournalListTransformationUtils;
import com.digi.uniprr.utils.JournalTransformationUtils;

@Service
@Transactional
public class JournalServiceImpl implements JournalService {
	@Autowired
	JournalsRepo journalsRepo;
	
	@Autowired
	ManuscriptTypeJournalsRepo manuscriptTypeJournalsRepo;

	@Autowired
	JournalMetadataRepo journalMetadataRepo;

	@Autowired
	JournalChargeDetailsRepo journalChargeDetailsRepo;

	@Autowired
	ManuScriptReviewerRepo manuScriptReviewerRepo;

	@Autowired
	ManuScriptRepo manuScriptRepo;

	@Autowired
	UserRepo userRepo;

	@Autowired
	JournalPoliciesRepo journalPoliciesRepo;
	
	@Autowired
	UserJournalRepo userJournalRepo;
	
	@Value("${defaultJournalCurrentIssueLink}")
	String defaultJournalCurrentIssueLink;
	
	@Value("${defaultJournalAboutLink}")
	String defaultJournalAboutLink;
	
	@Value("${defaultJournalAuthorInstructionsLink}")
	String defaultJournalAuthorInstructionsLink;
	
	@Value("${defaultThumbnailPath}")
	String defaultThumbnailPath;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Value("${unitouch.url}")
	private String unitouchUrl;

	@Autowired
	private HttpService httpService;
	
    @Autowired
    private JournalChargeFlagHistoryRepo historyRepo;

	@Override
	public String createJournal(Journal journals) {
		journalsRepo.save(journals);
		return "Journal updated";
	}

	@Override
	public List<JournalListVO> getJournalDetails() {
		List<Journal> list = journalsRepo.getActiveJournals();
		List<JournalListVO> voList = JournalListTransformationUtils.mapModelListToVoList(list);
		return voList;
	}
	
	@Override
	public List<AdminJournalVo> getJournalList() {
		List<AdminJournalVo> voJournalList = new ArrayList<>();
		try {
			List<Tuple> journalList = journalsRepo.getActiveJournalList();
			if (journalList != null) {
				voJournalList = journalList.stream().map(t -> new AdminJournalVo(t.get(0, String.class), t.get(1, String.class), t.get(2, Integer.class)))
					.collect(Collectors.toList());
				}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return voJournalList;
	}

	@Override
	public String updateJournal(Journal journals) {
		journalsRepo.save(journals);
		return "Journal created";
	}

	@Override
	public String deleteJournalById(Integer id) {
		journalsRepo.deleteById(id);
		return "Journal deleted";
	}

	@Override
	public boolean isIdExist(Integer id) {

		return journalsRepo.existsById(id);
	}

	@Override
	public JournalVO getJournalById(Integer id) {
		JournalVO journalVo = null;
		Journal journalDetail = journalsRepo.getJournalById(id);
		if (journalDetail != null) {
			journalVo = JournalTransformationUtils.mapJournalModelToJournalVo(journalDetail,defaultJournalCurrentIssueLink , defaultJournalAboutLink, defaultJournalAuthorInstructionsLink, defaultThumbnailPath);
		}

		return journalVo;
	}

	@Override
	public JournalChargeDetails getJournalChargeDetailsByJournalId(Integer id) {
		return journalChargeDetailsRepo.getJournalChargeDetailsByJournal(id);
	}

//	@Override
//	public JournalPerformanceVo getJournalPerfomance(JournalPerformanceVo journalPerfomanceVO) {
//
//		JournalPerformanceVo addJournalPerformanceVo = new JournalPerformanceVo();
//		addJournalPerformanceVo.setJournalId(journalPerfomanceVO.getJournalId());
//		addJournalPerformanceVo.setYear(journalPerfomanceVO.getYear());
//		addJournalPerformanceVo.setRoleId(journalPerfomanceVO.getRoleId());
//		
//		// Journal Performance
//		if (journalPerfomanceVO.getRoleId() != null && journalPerfomanceVO.getRoleId().equals(3)
//				&& journalPerfomanceVO.getJournalId() != null && journalPerfomanceVO.getYear() != null) {
//			JournalPerfomanceForEditorVO addJournalPerfomanceForEditor = new JournalPerfomanceForEditorVO();
//		
//			List<ManuScript> listArticlesSubmitted = manuScriptRepo
//					.getArticlesSubmittedOnJournal(journalPerfomanceVO.getJournalId(), journalPerfomanceVO.getYear());
//			if (listArticlesSubmitted != null && !listArticlesSubmitted.isEmpty())
//				addJournalPerfomanceForEditor.setNumberOfArticlesSubmitted(listArticlesSubmitted.size());
//			else
//				addJournalPerfomanceForEditor.setNumberOfArticlesSubmitted(0); //
//
//			List<ArticleDetailsVO> listArticleDetailsVO = new ArrayList<>();
//			listArticlesSubmitted.stream().forEach(articleSubmitted -> {
//				ArticleDetailsVO articleDetailsVO = new ArticleDetailsVO();
////				articleDetailsVO.setId(articleSubmitted.getManuScriptId());
//				articleDetailsVO.setId(articleSubmitted.getManuscriptCode());
//				articleDetailsVO.setArticleType(articleSubmitted.getManuscriptTitle());
//				listArticleDetailsVO.add(articleDetailsVO);
//			});
//			addJournalPerformanceVo.setArticleDetails(listArticleDetailsVO);
//
//			float daysTakenByEditorForDecision = 0;
//			float daysTakenByEditorForDecisionOnRevisonMax=0;
//			float daysTakenByEditorForDecisionOnRevisonMin=0;
//			
//			daysTakenByEditorForDecisionOnRevisonMax = manuScriptReviewerRepo.getEditorsTakenTimeMax(journalPerfomanceVO.getJournalId(), journalPerfomanceVO.getYear());
//			daysTakenByEditorForDecisionOnRevisonMin = manuScriptReviewerRepo.getEditorsTakenTimeMin(journalPerfomanceVO.getJournalId(), journalPerfomanceVO.getYear());
//			
//			if(daysTakenByEditorForDecisionOnRevisonMax <0)
//			{
//				daysTakenByEditorForDecisionOnRevisonMax= 0;
//			}
//			if(daysTakenByEditorForDecisionOnRevisonMin <0)
//			{
//				daysTakenByEditorForDecisionOnRevisonMin= 0;
//			}
//			
//			
//			EditorTakenTimeVO TakenByEditorForDecision = manuScriptReviewerRepo
//					.getEditorsTakenTime(journalPerfomanceVO.getJournalId(), journalPerfomanceVO.getYear());
//			if (TakenByEditorForDecision != null) {
//				if (TakenByEditorForDecision.getTotalsum() != null
//						&& TakenByEditorForDecision.getManuscriptCount() != null)
//					daysTakenByEditorForDecision = (float) (TakenByEditorForDecision.getTotalsum()
//							/ TakenByEditorForDecision.getManuscriptCount());
//			}
//			addJournalPerfomanceForEditor.setDaysTakenByEditorForDecisionOnRevison(daysTakenByEditorForDecision);// 7
//			addJournalPerfomanceForEditor.setDaysTakenByEditorForDecisionOnRevisonMax(daysTakenByEditorForDecisionOnRevisonMax);
//			addJournalPerfomanceForEditor.setDaysTakenByEditorForDecisionOnRevisonMin(daysTakenByEditorForDecisionOnRevisonMin);
//
//			
//
//			float addDaysTakenByReviewers = 0;
//			float daysTakenByReviewersMax = 0;
//			float daysTakenByReviewersMin = 0;
//			
//			daysTakenByReviewersMax = manuScriptReviewerRepo.getDaysTakenByReviewersMax(journalPerfomanceVO.getJournalId(), journalPerfomanceVO.getYear());
//			daysTakenByReviewersMin = manuScriptReviewerRepo.getDaysTakenByReviewersMin(journalPerfomanceVO.getJournalId(), journalPerfomanceVO.getYear());
//			
//			if(daysTakenByReviewersMax <0)
//			{
//				daysTakenByReviewersMax= 0;
//			}
//			if(daysTakenByReviewersMin <0)
//			{
//				daysTakenByReviewersMin= 0;
//			}
//			
//			
//			ReviewersTakenTimeVO daysTakenByReviewers = manuScriptReviewerRepo
//					.getDaysTakenByReviewers(journalPerfomanceVO.getJournalId(), journalPerfomanceVO.getYear());
//			if (daysTakenByReviewers != null) {
//				if (daysTakenByReviewers.getTotalsum() != null && daysTakenByReviewers.getManuscriptCount() != null)
//					addDaysTakenByReviewers = (float) (daysTakenByReviewers.getTotalsum()
//							/ daysTakenByReviewers.getManuscriptCount());
//			}
//			addJournalPerfomanceForEditor.setDaysTakenByReviewers(addDaysTakenByReviewers); //
//			addJournalPerfomanceForEditor.setDaysTakenByReviewersMax(daysTakenByReviewersMax);
//			addJournalPerfomanceForEditor.setDaysTakenByReviewersMin(daysTakenByReviewersMin);
//
//			float daysTakenByEditorToTakeDecision = 0;
//			float daysTakenByEditorToTakeDecisionMax=0;
//			float daysTakenByEditorToTakeDecisionMin=0;
//			
//			daysTakenByEditorToTakeDecisionMax = manuScriptReviewerRepo.getEditorsTakenTimeMax(journalPerfomanceVO.getJournalId(), journalPerfomanceVO.getYear());
//			daysTakenByEditorToTakeDecisionMin = manuScriptReviewerRepo.getEditorsTakenTimeMin(journalPerfomanceVO.getJournalId(), journalPerfomanceVO.getYear());
//			if(daysTakenByEditorToTakeDecisionMax <0)
//			{
//				daysTakenByEditorToTakeDecisionMax= 0;
//			}
//			if(daysTakenByEditorToTakeDecisionMin <0)
//			{
//				daysTakenByEditorToTakeDecisionMin= 0;
//			}
//			
//			
//			EditorTakenTimeVO TakenByEditorToTakeDecision = manuScriptReviewerRepo
//					.getEditorsTakenTime(journalPerfomanceVO.getJournalId(), journalPerfomanceVO.getYear());
//			if (TakenByEditorToTakeDecision != null) {
//				if (TakenByEditorToTakeDecision.getTotalsum() != null
//						&& TakenByEditorToTakeDecision.getManuscriptCount() != null)
//					daysTakenByEditorToTakeDecision = (float) (TakenByEditorToTakeDecision.getTotalsum()
//							/ TakenByEditorToTakeDecision.getManuscriptCount());
//			}
//			addJournalPerfomanceForEditor.setDaysForEditorToTakeDecision(daysTakenByEditorToTakeDecision);
//
//			List<ManuScript> totalNumberOfArticleReviewed = manuScriptRepo
//					.getTotalNumberOFArticleReview(journalPerfomanceVO.getJournalId(), journalPerfomanceVO.getYear());
//			if (totalNumberOfArticleReviewed != null && !totalNumberOfArticleReviewed.isEmpty())
//				addJournalPerfomanceForEditor
//						.setNumberOfArticlesRereviewedExternally(totalNumberOfArticleReviewed.size());
//			else
//				addJournalPerfomanceForEditor.setNumberOfArticlesRereviewedExternally(0); //
//
//			List<ExternalArticleDetailsVO> listExternalArticleDetailsVO = new ArrayList<>();
//			totalNumberOfArticleReviewed.stream().forEach(externalarticleSubmitted -> {
//				ExternalArticleDetailsVO externalArticleDetailsVO = new ExternalArticleDetailsVO();
////				externalArticleDetailsVO.setId(externalarticleSubmitted.getManuScriptId());
//				externalArticleDetailsVO.setId(externalarticleSubmitted.getManuscriptCode());
//				externalArticleDetailsVO.setArticleType(externalarticleSubmitted.getManuscriptTitle());
//				listExternalArticleDetailsVO.add(externalArticleDetailsVO);
//			});
//			addJournalPerformanceVo.setExternalArticleDetails(listExternalArticleDetailsVO);
//
//			float addNumberOfDaysTaken = 0;
//			float DaysInitialReviewAndSendingToReviewersMax=0;
//			float DaysInitialReviewAndSendingToReviewersMin=0;
//			
//			DaysInitialReviewAndSendingToReviewersMax = manuScriptReviewerRepo.getReviewersTakenTimeMax(journalPerfomanceVO.getJournalId(), journalPerfomanceVO.getYear());
//			DaysInitialReviewAndSendingToReviewersMin = manuScriptReviewerRepo.getReviewersTakenTimeMin(journalPerfomanceVO.getJournalId(), journalPerfomanceVO.getYear());
//			if(DaysInitialReviewAndSendingToReviewersMax <0)
//			{
//				DaysInitialReviewAndSendingToReviewersMax= 0;
//			}
//			if(DaysInitialReviewAndSendingToReviewersMin <0)
//			{
//				DaysInitialReviewAndSendingToReviewersMin= 0;
//			}
//			
//			
//			ReviewersTakenTimeVO reviewerTimeData = manuScriptReviewerRepo
//					.getReviewersTakenTime(journalPerfomanceVO.getJournalId(), journalPerfomanceVO.getYear());
//			if (reviewerTimeData != null) {
//				if (reviewerTimeData.getTotalsum() != null && reviewerTimeData.getManuscriptCount() != null)
//					addNumberOfDaysTaken = (float) (reviewerTimeData.getTotalsum()
//							/ reviewerTimeData.getManuscriptCount());
//			}
//			addJournalPerfomanceForEditor.setDaysInitialReviewAndSendingToReviewers(addNumberOfDaysTaken); //
//			addJournalPerfomanceForEditor.setDaysInitialReviewAndSendingToReviewersMax(DaysInitialReviewAndSendingToReviewersMax);
//			addJournalPerfomanceForEditor.setDaysInitialReviewAndSendingToReviewersMin(DaysInitialReviewAndSendingToReviewersMin);
//
//			float daysTakenByAuthorsForFirstRevision = 0;
//			float daysTakenByAuthorsForFirstRevisionMax = 0;
//			float daysTakenByAuthorsForFirstRevisionMin = 0;
//			
//			daysTakenByAuthorsForFirstRevisionMax = manuScriptReviewerRepo.getAuthorTakenTimeMax(journalPerfomanceVO.getJournalId(), journalPerfomanceVO.getYear());
//			daysTakenByAuthorsForFirstRevisionMin = manuScriptReviewerRepo.getAuthorTakenTimeMin(journalPerfomanceVO.getJournalId(), journalPerfomanceVO.getYear());
//			
//			if(daysTakenByAuthorsForFirstRevisionMax <0)
//			{
//				daysTakenByAuthorsForFirstRevisionMax= 0;
//			}
//			if(daysTakenByAuthorsForFirstRevisionMin <0)
//			{
//				daysTakenByAuthorsForFirstRevisionMin= 0;
//			}
//			
//			addJournalPerfomanceForEditor.setDaysTakenByAuthorsForFirstRevisionMax(daysTakenByAuthorsForFirstRevisionMax);
//			addJournalPerfomanceForEditor.setDaysTakenByAuthorsForFirstRevisionMin(daysTakenByAuthorsForFirstRevisionMin);
//			
//
//			AuthorTakenTimeVO daysTakenAuthorToRevision = manuScriptReviewerRepo
//					.getAuthorTakenTime(journalPerfomanceVO.getJournalId(), journalPerfomanceVO.getYear());
//			if (daysTakenAuthorToRevision != null) {
//				if (daysTakenAuthorToRevision.getTotalsum() != null
//						&& daysTakenAuthorToRevision.getManuscriptCount() != null)
//					daysTakenByAuthorsForFirstRevision = (float) (daysTakenAuthorToRevision.getTotalsum()
//							/ daysTakenAuthorToRevision.getManuscriptCount());
//			}
//
//			addJournalPerfomanceForEditor.setDaysTakenByAuthorsForFirstRevision(daysTakenByAuthorsForFirstRevision);
//			
//			addJournalPerfomanceForEditor
//					.setDaysTakenByReviewersForRereview(addNumberOfDaysTaken + addDaysTakenByReviewers);
//			
//		
//			float daysTakenByReviewersForRereviewMax = 0;
//			float daysTakenByReviewersForRereviewMin = 0;
//			
//			daysTakenByReviewersForRereviewMax = (DaysInitialReviewAndSendingToReviewersMax + daysTakenByReviewersMax);
//			daysTakenByReviewersForRereviewMin = (DaysInitialReviewAndSendingToReviewersMin +daysTakenByReviewersMin);
//			
//			if(daysTakenByReviewersForRereviewMax <0)
//			{
//				daysTakenByReviewersForRereviewMax= 0;
//			}
//			if(daysTakenByReviewersForRereviewMin <0)
//			{
//				daysTakenByReviewersForRereviewMin= 0;
//			}
//			
//			addJournalPerfomanceForEditor.setDaysTakenByReviewersForRereviewMax(daysTakenByReviewersForRereviewMax);
//			addJournalPerfomanceForEditor.setDaysTakenByReviewersForRereviewMin(daysTakenByReviewersForRereviewMin);
//			
//			
//
//			addJournalPerfomanceForEditor.setDaysForDecisionOnRereviewByEditors(daysTakenByEditorToTakeDecision + daysTakenByEditorForDecision);
//			
//			float daysForDecisionOnRereviewByEditorsMax=0;
//			float daysForDecisionOnRereviewByEditorsMin=0;
//			
//			daysForDecisionOnRereviewByEditorsMax = (daysTakenByEditorToTakeDecisionMax + daysTakenByEditorForDecisionOnRevisonMax);
//			daysForDecisionOnRereviewByEditorsMin = (daysTakenByEditorToTakeDecisionMin + daysTakenByEditorForDecisionOnRevisonMin);
//			
//			if(daysForDecisionOnRereviewByEditorsMax <0)
//			{
//				daysForDecisionOnRereviewByEditorsMax= 0;
//			}
//			if(daysForDecisionOnRereviewByEditorsMin <0)
//			{
//				daysForDecisionOnRereviewByEditorsMin= 0;
//			}
//			
//			addJournalPerfomanceForEditor.setDaysForDecisionOnRereviewByEditorsMax(daysForDecisionOnRereviewByEditorsMax);
//			addJournalPerfomanceForEditor.setDaysForDecisionOnRereviewByEditorsMin(daysForDecisionOnRereviewByEditorsMin);
//			
//
//			addJournalPerfomanceForEditor.setDaysFromRevisionReceiptToRevisionDecision(
//					daysTakenByEditorToTakeDecision + daysTakenByEditorForDecision + addDaysTakenByReviewers);
//			
//			float daysFromRevisionReceiptToRevisionDecisionMax=0;
//			float daysFromRevisionReceiptToRevisionDecisionMin=0;
//			
//			daysFromRevisionReceiptToRevisionDecisionMax = (daysTakenByEditorToTakeDecisionMax + daysTakenByEditorForDecisionOnRevisonMax + daysTakenByReviewersMax);
//			daysFromRevisionReceiptToRevisionDecisionMin = (daysTakenByEditorToTakeDecisionMin + daysTakenByEditorForDecisionOnRevisonMin + daysTakenByReviewersMin);
//			
//			if(daysFromRevisionReceiptToRevisionDecisionMax <0)
//			{
//				daysFromRevisionReceiptToRevisionDecisionMax= 0;
//			}
//			if(daysFromRevisionReceiptToRevisionDecisionMin <0)
//			{
//				daysFromRevisionReceiptToRevisionDecisionMin= 0;
//			}
//				
//			addJournalPerfomanceForEditor.setDaysFromRevisionReceiptToRevisionDecisionMax(daysFromRevisionReceiptToRevisionDecisionMax);
//			addJournalPerfomanceForEditor.setDaysFromRevisionReceiptToRevisionDecisionMin(daysFromRevisionReceiptToRevisionDecisionMin);
//			addJournalPerfomanceForEditor.setDaysFromRevisionReceiptToFinalDecision(addNumberOfDaysTaken+ addDaysTakenByReviewers + daysTakenByEditorToTakeDecision + 
//					daysTakenByEditorForDecision);
//
//			float daysFromRevisionReceiptToFinalDecisionMax = 0;
//			float daysFromRevisionReceiptToFinalDecisionMin = 0;
//			
//		
//			daysFromRevisionReceiptToFinalDecisionMax = (DaysInitialReviewAndSendingToReviewersMax + daysTakenByReviewersMax + daysTakenByEditorToTakeDecisionMax + 
//					daysTakenByEditorForDecisionOnRevisonMax);
//			daysFromRevisionReceiptToFinalDecisionMin = (DaysInitialReviewAndSendingToReviewersMin + daysTakenByReviewersMin + daysTakenByEditorToTakeDecisionMin + 
//					daysTakenByEditorForDecisionOnRevisonMin);
//			
//			if(daysFromRevisionReceiptToFinalDecisionMax <0)
//			{
//				daysFromRevisionReceiptToFinalDecisionMax= 0;
//			}
//			if(daysFromRevisionReceiptToFinalDecisionMin <0)
//			{
//				daysFromRevisionReceiptToFinalDecisionMin= 0;
//			}
//				
//			addJournalPerfomanceForEditor.setDaysFromRevisionReceiptToFinalDecisionMax(daysFromRevisionReceiptToFinalDecisionMax);
//			addJournalPerfomanceForEditor.setDaysFromRevisionReceiptToFinalDecisionMin(daysFromRevisionReceiptToFinalDecisionMin);
//			
//			
//			addJournalPerfomanceForEditor
//					.setDaysFromFirstSubmissionToAcceptance(daysTakenByEditorToTakeDecision + addDaysTakenByReviewers);
//			
//			
//			float daysFromFirstSubmissionToAcceptanceMax=0;
//			float daysFromFirstSubmissionToAcceptanceMin=0;
//			
//			daysFromFirstSubmissionToAcceptanceMax = (daysTakenByEditorToTakeDecisionMax + daysTakenByReviewersMax);
//			daysFromFirstSubmissionToAcceptanceMin = (daysTakenByEditorToTakeDecisionMin + daysTakenByReviewersMin);
//			
//			if(daysFromFirstSubmissionToAcceptanceMax <0)
//			{
//				daysFromFirstSubmissionToAcceptanceMax= 0;
//			}
//			if(daysFromFirstSubmissionToAcceptanceMin <0)
//			{
//				daysFromFirstSubmissionToAcceptanceMin= 0;
//			}
//			
//			addJournalPerfomanceForEditor.setDaysFromFirstSubmissionToAcceptanceMax(daysFromFirstSubmissionToAcceptanceMax);
//			addJournalPerfomanceForEditor.setDaysFromFirstSubmissionToAcceptanceMin(daysFromFirstSubmissionToAcceptanceMin);
//			
//			
//
//			addJournalPerfomanceForEditor.setDaysFromAcceptanceToPublication(
//					addNumberOfDaysTaken + addDaysTakenByReviewers + daysTakenByEditorToTakeDecision);
//
//			addJournalPerfomanceForEditor
//					.setTotalDaysFromSubmissionToFirstDecision(addNumberOfDaysTaken + addDaysTakenByReviewers);
//			
//			
//			float totalDaysFromSubmissionToFirstDecisionMax=0;
//			float totalDaysFromSubmissionToFirstDecisionMin=0;
//			
//			
//			totalDaysFromSubmissionToFirstDecisionMax = (DaysInitialReviewAndSendingToReviewersMax + daysTakenByReviewersMax);
//			totalDaysFromSubmissionToFirstDecisionMin = (DaysInitialReviewAndSendingToReviewersMin + daysTakenByReviewersMin);
//			
//			if(totalDaysFromSubmissionToFirstDecisionMax <0)
//			{
//				totalDaysFromSubmissionToFirstDecisionMax= 0;
//			}
//			if(totalDaysFromSubmissionToFirstDecisionMin <0)
//			{
//				totalDaysFromSubmissionToFirstDecisionMin= 0;
//			}
//			
//			addJournalPerfomanceForEditor.setTotalDaysFromSubmissionToFirstDecisionMax(totalDaysFromSubmissionToFirstDecisionMax);
//			addJournalPerfomanceForEditor.setTotalDaysFromSubmissionToFirstDecisionMin(totalDaysFromSubmissionToFirstDecisionMin);
//
//			addJournalPerformanceVo.setJournalPerfomanceForEditor(addJournalPerfomanceForEditor);
//		}
//
//		
//		float submissionToAcceptanceMax = 0;
//		float submissionToAcceptanceMin = 0;
//		
//		submissionToAcceptanceMax = manuScriptRepo.getTotalNumberOFDaysSubmissionToAcceptanceMax(journalPerfomanceVO.getJournalId(),journalPerfomanceVO.getYear());
//		submissionToAcceptanceMin = manuScriptRepo.getTotalNumberOFDaysSubmissionToAcceptanceMin(journalPerfomanceVO.getJournalId(),journalPerfomanceVO.getYear());
//		if(submissionToAcceptanceMax <0)
//		{
//			submissionToAcceptanceMax= 0;
//		}
//		if(submissionToAcceptanceMin <0)
//		{
//			submissionToAcceptanceMin= 0;
//		}
//		
//		addJournalPerformanceVo.setSubmissionToAcceptanceMax(submissionToAcceptanceMax);
//		addJournalPerformanceVo.setSubmissionToAcceptanceMin(submissionToAcceptanceMin);
//		
//		
//		Double submissionToAcceptance = manuScriptRepo
//				.getTotalNumberOFDaysSubmissionToAcceptance(journalPerfomanceVO.getJournalId(),journalPerfomanceVO.getYear());
//		
//		if (submissionToAcceptance == null) {
//			addJournalPerformanceVo.setDaysFirstSubmissionToAcceptance("0");
//		} else {
//			
//			addJournalPerformanceVo.setDaysFirstSubmissionToAcceptance(String. format("%.1f", submissionToAcceptance));
//		}
//		
//		float acceptanceToPublicationMax = 0;
//		float acceptanceToPublicationMin = 0;
//		
//		acceptanceToPublicationMax = manuScriptRepo.getTotalNumberOFDaysAcceptanceToPublicationMax(journalPerfomanceVO.getJournalId(),journalPerfomanceVO.getYear());
//		acceptanceToPublicationMin = manuScriptRepo.getTotalNumberOFDaysAcceptanceToPublicationMin(journalPerfomanceVO.getJournalId(),journalPerfomanceVO.getYear());
//		
//		
//		if(acceptanceToPublicationMax <0)
//		{
//			acceptanceToPublicationMax= 0;
//		}
//		
//		if(acceptanceToPublicationMin <0)
//		{
//			acceptanceToPublicationMin= 0;
//		}
//		
//		addJournalPerformanceVo.setAcceptanceToPublicationMax(acceptanceToPublicationMax);
//		addJournalPerformanceVo.setAcceptanceToPublicationMin(acceptanceToPublicationMin);
//		
//		
//
//		Double acceptanceToPublication = manuScriptRepo
//				.getTotalNumberOFDaysAcceptanceToPublication(journalPerfomanceVO.getJournalId(),journalPerfomanceVO.getYear());
//		if (acceptanceToPublication == null) {
//			addJournalPerformanceVo.setDaysFromAcceptanceToPublication("0");
//
//		} else {
//			addJournalPerformanceVo.setDaysFromAcceptanceToPublication(String. format("%.1f", acceptanceToPublication));
//
//		}
//		
//		
//
//		return addJournalPerformanceVo;
//	}
	
	@Override
	public JournalPerformanceVo getJournalPerfomance(JournalPerformanceVo journalPerfomanceVO) {

		JournalPerformanceVo addJournalPerformanceVo = new JournalPerformanceVo();
		addJournalPerformanceVo.setJournalId(journalPerfomanceVO.getJournalId());
		addJournalPerformanceVo.setYear(journalPerfomanceVO.getYear());
		addJournalPerformanceVo.setRoleId(journalPerfomanceVO.getRoleId());
		
		// Journal Performance
		if (journalPerfomanceVO.getRoleId() != null && journalPerfomanceVO.getRoleId().equals(3)
				&& journalPerfomanceVO.getJournalId() != null && journalPerfomanceVO.getYear() != null) {
			JournalPerfomanceForEditorVO addJournalPerfomanceForEditor = new JournalPerfomanceForEditorVO();
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			//1
			List<Object[]> listArticlesSubmitted = manuScriptRepo
					.TotalNumberOfArticlesSubmitted(journalPerfomanceVO.getJournalId(), journalPerfomanceVO.getYear());
			if (listArticlesSubmitted != null && !listArticlesSubmitted.isEmpty())
				addJournalPerfomanceForEditor.setNumberOfArticlesSubmitted(listArticlesSubmitted.size());
			else
				addJournalPerfomanceForEditor.setNumberOfArticlesSubmitted(0); //

			List<ArticleDetailsVO> listArticleDetailsVO = new ArrayList<>();
			listArticlesSubmitted.forEach(articleSubmitted -> {
	            String manuscriptCode = (String) articleSubmitted[0];
	            String manuscriptTitle = (String) articleSubmitted[1];
				ArticleDetailsVO articleDetailsVO = new ArticleDetailsVO();
				articleDetailsVO.setId(manuscriptCode);
				articleDetailsVO.setArticleType(manuscriptTitle);
				listArticleDetailsVO.add(articleDetailsVO);
			});
			addJournalPerformanceVo.setArticleDetails(listArticleDetailsVO);
			////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			//2. Submission to Invitation Statistics
	        populateSubmissionToInvitationStats(addJournalPerfomanceForEditor, journalPerfomanceVO);
	        //3. Invitation to Completion Statistics
	        populateInviteToCompleteStats(addJournalPerfomanceForEditor, journalPerfomanceVO);
	        //4. Completion to First Decision Statistics
	        populateCompletionToDecisionStats(addJournalPerfomanceForEditor, journalPerfomanceVO);
	        //5. Submission to First Decision Statistics
	        populateSubmissionToFirstDecisionStats(addJournalPerfomanceForEditor, journalPerfomanceVO);
	        //6 Authors for first revision to complete 
	        populateAuthorRevisionToCompletion(addJournalPerfomanceForEditor, addJournalPerformanceVo);
	        //7 complete to next decision
	        populateAuthorCompletionToNextDescion(addJournalPerfomanceForEditor, addJournalPerformanceVo);
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	 
	        
	        
	        
	        List<Object[]> totalNumberOfArticleReviewed = manuScriptRepo
	                .getTotalNumberOFArticleReview(journalPerfomanceVO.getJournalId(), journalPerfomanceVO.getYear());

	        if (totalNumberOfArticleReviewed != null && !totalNumberOfArticleReviewed.isEmpty()) {
	            addJournalPerfomanceForEditor.setNumberOfArticlesRereviewedExternally(totalNumberOfArticleReviewed.size());
	        } else {
	            addJournalPerfomanceForEditor.setNumberOfArticlesRereviewedExternally(0);
	        }

	        List<ExternalArticleDetailsVO> listExternalArticleDetailsVO = new ArrayList<>();
	        totalNumberOfArticleReviewed.forEach(record -> {
	            String manuscriptCode = (String) record[1];
	            String manuscriptTitle = (String) record[2];

	            ExternalArticleDetailsVO externalArticleDetailsVO = new ExternalArticleDetailsVO();
	            externalArticleDetailsVO.setId(manuscriptCode); // Using manuscriptCode as ID
	            externalArticleDetailsVO.setArticleType(manuscriptTitle);
	            listExternalArticleDetailsVO.add(externalArticleDetailsVO);
	        });

	        addJournalPerformanceVo.setExternalArticleDetails(listExternalArticleDetailsVO);
//////////////////////////////////////////////////////////////////////////////////////////////////////	
	        //9.re-review to complete
	        populateReReviewComplete(addJournalPerfomanceForEditor, addJournalPerformanceVo);
///////////////////////////////////////////////////////////////////////////////////////////////////////////
	        //11
	        populateSecondRevisionCompletionToFinalDecision(addJournalPerfomanceForEditor, addJournalPerformanceVo);
/////////////////////////////////////////////////////////////////////////////////////////////////
	        //12.
	        populateSubmissionToFinalDecision(addJournalPerfomanceForEditor, addJournalPerformanceVo);
//////////////////////////////////////////////////////////////////////////////////////////////////	        	

			float addDaysTakenByReviewers = 0;
			float daysTakenByReviewersMax = 0;
			float daysTakenByReviewersMin = 0;
			
			daysTakenByReviewersMax = manuScriptReviewerRepo.getDaysTakenByReviewersMax(journalPerfomanceVO.getJournalId(), journalPerfomanceVO.getYear());
			daysTakenByReviewersMin = manuScriptReviewerRepo.getDaysTakenByReviewersMin(journalPerfomanceVO.getJournalId(), journalPerfomanceVO.getYear());
			
			if(daysTakenByReviewersMax <0)
			{
				daysTakenByReviewersMax= 0;
			}
			if(daysTakenByReviewersMin <0)
			{
				daysTakenByReviewersMin= 0;
			}
			
			


			float daysTakenByEditorToTakeDecision = 0;
			float daysTakenByEditorToTakeDecisionMax=0;
			float daysTakenByEditorToTakeDecisionMin=0;
			
			daysTakenByEditorToTakeDecisionMax = manuScriptReviewerRepo.getEditorsTakenTimeMax(journalPerfomanceVO.getJournalId(), journalPerfomanceVO.getYear());
			daysTakenByEditorToTakeDecisionMin = manuScriptReviewerRepo.getEditorsTakenTimeMin(journalPerfomanceVO.getJournalId(), journalPerfomanceVO.getYear());
			if(daysTakenByEditorToTakeDecisionMax <0)
			{
				daysTakenByEditorToTakeDecisionMax= 0;
			}
			if(daysTakenByEditorToTakeDecisionMin <0)
			{
				daysTakenByEditorToTakeDecisionMin= 0;
			}
			
			
			EditorTakenTimeVO TakenByEditorToTakeDecision = manuScriptReviewerRepo
					.getEditorsTakenTime(journalPerfomanceVO.getJournalId(), journalPerfomanceVO.getYear());
			if (TakenByEditorToTakeDecision != null) {
				if (TakenByEditorToTakeDecision.getTotalsum() != null
						&& TakenByEditorToTakeDecision.getManuscriptCount() != null)
					daysTakenByEditorToTakeDecision = (float) (TakenByEditorToTakeDecision.getTotalsum()
							/ TakenByEditorToTakeDecision.getManuscriptCount());
			}
			addJournalPerfomanceForEditor.setDaysForEditorToTakeDecision(daysTakenByEditorToTakeDecision);



			float addNumberOfDaysTaken = 0;
			float DaysInitialReviewAndSendingToReviewersMax=0;
			float DaysInitialReviewAndSendingToReviewersMin=0;
			
			DaysInitialReviewAndSendingToReviewersMax = manuScriptReviewerRepo.getReviewersTakenTimeMax(journalPerfomanceVO.getJournalId(), journalPerfomanceVO.getYear());
			DaysInitialReviewAndSendingToReviewersMin = manuScriptReviewerRepo.getReviewersTakenTimeMin(journalPerfomanceVO.getJournalId(), journalPerfomanceVO.getYear());
			if(DaysInitialReviewAndSendingToReviewersMax <0)
			{
				DaysInitialReviewAndSendingToReviewersMax= 0;
			}
			if(DaysInitialReviewAndSendingToReviewersMin <0)
			{
				DaysInitialReviewAndSendingToReviewersMin= 0;
			}
			
			
			ReviewersTakenTimeVO reviewerTimeData = manuScriptReviewerRepo
					.getReviewersTakenTime(journalPerfomanceVO.getJournalId(), journalPerfomanceVO.getYear());
			if (reviewerTimeData != null) {
				if (reviewerTimeData.getTotalsum() != null && reviewerTimeData.getManuscriptCount() != null)
					addNumberOfDaysTaken = (float) (reviewerTimeData.getTotalsum()
							/ reviewerTimeData.getManuscriptCount());
			}

			
			float daysTakenByEditorForDecisionOnRevisonMax=0;
			float daysTakenByEditorForDecisionOnRevisonMin=0;
			float daysTakenByEditorForDecision=0;

			addJournalPerfomanceForEditor.setDaysForDecisionOnRereviewByEditors(daysTakenByEditorToTakeDecision + daysTakenByEditorForDecision);
			
			float daysForDecisionOnRereviewByEditorsMax=0;
			float daysForDecisionOnRereviewByEditorsMin=0;
			
			daysForDecisionOnRereviewByEditorsMax = (daysTakenByEditorToTakeDecisionMax + daysTakenByEditorForDecisionOnRevisonMax);
			daysForDecisionOnRereviewByEditorsMin = (daysTakenByEditorToTakeDecisionMin + daysTakenByEditorForDecisionOnRevisonMin);
			
			if(daysForDecisionOnRereviewByEditorsMax <0)
			{
				daysForDecisionOnRereviewByEditorsMax= 0;
			}
			if(daysForDecisionOnRereviewByEditorsMin <0)
			{
				daysForDecisionOnRereviewByEditorsMin= 0;
			}
			
			addJournalPerfomanceForEditor.setDaysForDecisionOnRereviewByEditorsMax(daysForDecisionOnRereviewByEditorsMax);
			addJournalPerfomanceForEditor.setDaysForDecisionOnRereviewByEditorsMin(daysForDecisionOnRereviewByEditorsMin);
			

			
			float daysFromRevisionReceiptToRevisionDecisionMax=0;
			float daysFromRevisionReceiptToRevisionDecisionMin=0;
			
			daysFromRevisionReceiptToRevisionDecisionMax = (daysTakenByEditorToTakeDecisionMax + daysTakenByEditorForDecisionOnRevisonMax + daysTakenByReviewersMax);
			daysFromRevisionReceiptToRevisionDecisionMin = (daysTakenByEditorToTakeDecisionMin + daysTakenByEditorForDecisionOnRevisonMin + daysTakenByReviewersMin);
			
			if(daysFromRevisionReceiptToRevisionDecisionMax <0)
			{
				daysFromRevisionReceiptToRevisionDecisionMax= 0;
			}
			if(daysFromRevisionReceiptToRevisionDecisionMin <0)
			{
				daysFromRevisionReceiptToRevisionDecisionMin= 0;
			}
				
			
			addJournalPerfomanceForEditor
					.setDaysFromFirstSubmissionToAcceptance(daysTakenByEditorToTakeDecision + addDaysTakenByReviewers);
			
			
			float daysFromFirstSubmissionToAcceptanceMax=0;
			float daysFromFirstSubmissionToAcceptanceMin=0;
			
			daysFromFirstSubmissionToAcceptanceMax = (daysTakenByEditorToTakeDecisionMax + daysTakenByReviewersMax);
			daysFromFirstSubmissionToAcceptanceMin = (daysTakenByEditorToTakeDecisionMin + daysTakenByReviewersMin);
			
			if(daysFromFirstSubmissionToAcceptanceMax <0)
			{
				daysFromFirstSubmissionToAcceptanceMax= 0;
			}
			if(daysFromFirstSubmissionToAcceptanceMin <0)
			{
				daysFromFirstSubmissionToAcceptanceMin= 0;
			}
			
			addJournalPerfomanceForEditor.setDaysFromFirstSubmissionToAcceptanceMax(daysFromFirstSubmissionToAcceptanceMax);
			addJournalPerfomanceForEditor.setDaysFromFirstSubmissionToAcceptanceMin(daysFromFirstSubmissionToAcceptanceMin);
			
			

			addJournalPerfomanceForEditor.setDaysFromAcceptanceToPublication(
					addNumberOfDaysTaken + addDaysTakenByReviewers + daysTakenByEditorToTakeDecision);


			addJournalPerformanceVo.setJournalPerfomanceForEditor(addJournalPerfomanceForEditor);
		}

		//13
		populateSubmissionToAcceptance(addJournalPerformanceVo);
		//14
		
		populateAcceptanceToPublication(addJournalPerformanceVo);
		//14
		populateSubmissionToPublication(addJournalPerformanceVo);
		
		

		return addJournalPerformanceVo;
	}
	
	//  Method: Submission to Invitation Statistics
	private void populateSubmissionToInvitationStats(JournalPerfomanceForEditorVO editorPerformance,
	                                                  JournalPerformanceVo journalPerfomanceVO) {
	    List<Object[]> data = manuScriptRepo.NumberOfDaysFromSubmissionToInvitingReviewers(
	            journalPerfomanceVO.getJournalId(), journalPerfomanceVO.getYear());

	    List<Long> daysBetween = extractDaysBetween(data, 1, 2);
	    
	    setStatistics(editorPerformance::setDaysInitialReviewAndSendingToReviewers,
	            editorPerformance::setDaysInitialReviewAndSendingToReviewersMax,
	            editorPerformance::setDaysInitialReviewAndSendingToReviewersMin,
	            daysBetween);
	}
	
	//  Method: Invitation to Completion Statistics
	private void populateInviteToCompleteStats(JournalPerfomanceForEditorVO editorPerformance,
	                                           JournalPerformanceVo journalPerfomanceVO) {
	    List<Object[]> data = manuScriptRepo.getInviteAndCompleteDates(
	            journalPerfomanceVO.getJournalId(), journalPerfomanceVO.getYear());

	    List<Long> daysBetween = extractDaysBetween(data, 1, 2);

	    setStatistics(editorPerformance::setDaysTakenByReviewers,
	            editorPerformance::setDaysTakenByReviewersMax,
	            editorPerformance::setDaysTakenByReviewersMin,
	            daysBetween);
	}
	
	//  Method: Completion to First Decision Statistics
	private void populateCompletionToDecisionStats(JournalPerfomanceForEditorVO editorPerformance,
	                                               JournalPerformanceVo journalPerfomanceVO) {
	    List<Object[]> data = manuScriptRepo.getFilteredDecisionData(
	            journalPerfomanceVO.getJournalId(), journalPerfomanceVO.getYear());
	    
	    List<Long> daysBetween = extractDaysBetween(data, 1, 2);

	    setStatistics(editorPerformance::setDaysFromRevisionReceiptToRevisionDecision,
	            editorPerformance::setDaysFromRevisionReceiptToRevisionDecisionMax,
	            editorPerformance::setDaysFromRevisionReceiptToRevisionDecisionMin,
	            daysBetween);
	}
	
	// Method : Submission to First Decision Statistics
	private void populateSubmissionToFirstDecisionStats(JournalPerfomanceForEditorVO editorPerformance,
	                                                    JournalPerformanceVo journalPerformanceVO) {
	    List<Object[]> decisionData = manuScriptRepo.getSubmissionToFirstDecisionData(
	            journalPerformanceVO.getJournalId(),
	            journalPerformanceVO.getYear()
	    );

	    List<Long> daysBetween = extractDaysBetween(decisionData, 1, 2);

	    setStatistics(editorPerformance::setTotalDaysFromSubmissionToFirstDecision,
	            editorPerformance::setTotalDaysFromSubmissionToFirstDecisionMax,
	            editorPerformance::setTotalDaysFromSubmissionToFirstDecisionMin,
	            daysBetween);
	}
	
//	private void populateAuthorRevisionToCompletion(JournalPerfomanceForEditorVO editorPerformance,
//            JournalPerformanceVo journalPerformanceVO) {
//		   
//		   List<Object[]> firstRevisionData = manuScriptRepo.findFirstRevision(journalPerformanceVO.getJournalId());
//	       Map<Integer, LocalDateTime> startedMap = firstRevisionData.stream()
//	                .collect(Collectors.toMap(
//	                    obj -> (Integer) obj[0], 
//	                    obj -> ((Timestamp) obj[2]).toLocalDateTime()
//	                    ,(existing, replacement) -> existing
//	                ));
//		   List<Object[]> firstRevisionCompleteData = manuScriptRepo.findFirstRevisionCompletedByYear(journalPerformanceVO.getJournalId(),
//		            journalPerformanceVO.getYear());
//		   Map<Integer, LocalDateTime> completedMap = firstRevisionCompleteData.stream()
//		            .collect(Collectors.toMap(
//		                obj -> (Integer) obj[0], 
//		                obj -> ((Timestamp) obj[2]).toLocalDateTime()
//		                ,(existing, replacement) -> existing
//		            ));
//	        
//		    List<Long> dayDifferences = new ArrayList<>();
//		    startedMap.forEach((manuscriptId, startDate) -> {
//		        if (completedMap.containsKey(manuscriptId)) {
//		            LocalDateTime endDate = completedMap.get(manuscriptId);
//		            long differenceDays = Duration.between(startDate, endDate).toDays()+1;
//		            dayDifferences.add(differenceDays);
//		        }
//		    });
//
//		   setStatistics(editorPerformance::setDaysTakenByAuthorsForFirstRevision,
//		            editorPerformance::setDaysTakenByAuthorsForFirstRevisionMax,
//		            editorPerformance::setDaysTakenByAuthorsForFirstRevisionMin,
//		            dayDifferences);
//	}
	
	private void populateAuthorRevisionToCompletion(JournalPerfomanceForEditorVO editorPerformance,
		JournalPerformanceVo journalPerformanceVO) {

		List<Object[]> firstRevisionData = manuScriptRepo.findFirstRevision(journalPerformanceVO.getJournalId());

		Map<Integer, LocalDateTime> startedMap = firstRevisionData.stream()
		.filter(obj -> obj[0] != null && obj[2] != null).collect(Collectors.toMap(obj -> (Integer) obj[0],obj -> ((Timestamp) obj[2]).toLocalDateTime(), (existing, replacement) -> existing));

		List<Object[]> firstRevisionCompleteData = manuScriptRepo
		.findFirstRevisionCompletedByYear(journalPerformanceVO.getJournalId(), journalPerformanceVO.getYear());

		Map<Integer, LocalDateTime> completedMap = firstRevisionCompleteData.stream()
		.filter(obj -> obj[0] != null && obj[2] != null).collect(Collectors.toMap(obj -> (Integer) obj[0],obj -> ((Timestamp) obj[2]).toLocalDateTime(), (existing, replacement) -> existing));

		List<Long> dayDifferences = new ArrayList<>();

		startedMap.forEach((manuscriptId, startDate) -> {
		LocalDateTime endDate = completedMap.get(manuscriptId);

		if (endDate != null && endDate.isAfter(startDate)) {
		long differenceDays = Duration.between(startDate, endDate).toDays() + 1;
		dayDifferences.add(differenceDays);}});

		setStatistics(editorPerformance::setDaysTakenByAuthorsForFirstRevision,
		editorPerformance::setDaysTakenByAuthorsForFirstRevisionMax,
		editorPerformance::setDaysTakenByAuthorsForFirstRevisionMin, dayDifferences);
		
		}
	
	
//	private void populateAuthorCompletionToNextDescion(JournalPerfomanceForEditorVO editorPerformance,
//            JournalPerformanceVo journalPerformanceVO) {
//		
//        List<Object[]> firstRevisionData = manuScriptRepo.findFirstRevisionCompleted(journalPerformanceVO.getJournalId());
//        List<Object[]> firstDecisionBeforeYearData = manuScriptRepo.findFirstDecisionBeforeYear(journalPerformanceVO.getJournalId(),
//	            journalPerformanceVO.getYear());
//        List<Object[]> firstDecisionInYearData = manuScriptRepo.findFirstDecisionInYear(journalPerformanceVO.getJournalId(),
//	            journalPerformanceVO.getYear());
//
//        List<Long> daysDifferences = new ArrayList<>();
//        Map<Integer, LocalDateTime> firstRevisionMap = new HashMap<>();
//        Map<Integer, LocalDateTime> firstDecisionBeforeYearMap = new HashMap<>();
//        Map<Integer, LocalDateTime> firstDecisionInYearMap = new HashMap<>();
//
//        for (Object[] data : firstRevisionData) {
//            Integer manuscriptId = (Integer) data[0];
//            LocalDateTime firstRevisionDate = ((java.sql.Timestamp) data[1]).toLocalDateTime();
//            firstRevisionMap.put(manuscriptId, firstRevisionDate);
//        }
//
//        for (Object[] data : firstDecisionBeforeYearData) {
//            Integer manuscriptId = (Integer) data[0];
//            LocalDateTime firstDecisionDate = ((java.sql.Timestamp) data[1]).toLocalDateTime();
//            firstDecisionBeforeYearMap.put(manuscriptId, firstDecisionDate);
//        }
//
//        for (Object[] data : firstDecisionInYearData) {
//        	Integer manuscriptId = (Integer) data[0];
//            LocalDateTime firstDecisionDate = ((java.sql.Timestamp) data[1]).toLocalDateTime();
//            firstDecisionInYearMap.put(manuscriptId, firstDecisionDate);
//        }
//
//        for (Integer manuscriptId : firstRevisionMap.keySet()) {
//            LocalDateTime firstRevisionDate = firstRevisionMap.get(manuscriptId);
//            LocalDateTime firstDecisionBeforeYearDate = firstDecisionBeforeYearMap.get(manuscriptId);
//            LocalDateTime firstDecisionInYearDate = firstDecisionInYearMap.get(manuscriptId);
//
//            if (firstDecisionInYearDate != null && firstDecisionInYearDate.getYear() == journalPerformanceVO.getYear() &&
//                firstDecisionInYearDate.isAfter(firstRevisionDate)) {
//
//                if (firstDecisionBeforeYearDate != null && firstDecisionBeforeYearDate.getYear() < journalPerformanceVO.getYear()) {
//                } else if (firstDecisionInYearDate.getYear() == journalPerformanceVO.getYear()) {
//                    long daysBetween = Duration.between(firstRevisionDate, firstDecisionInYearDate).toDays()+1;
//                    daysDifferences.add(daysBetween);
//                }
//            }
//        }
//        
//		    setStatistics(editorPerformance::setDaysTakenByEditorForDecisionOnRevison,
//		            editorPerformance::setDaysTakenByEditorForDecisionOnRevisonMax,
//		            editorPerformance::setDaysTakenByEditorForDecisionOnRevisonMin,
//		            daysDifferences);
//
//	}
	
	
	private void populateAuthorCompletionToNextDescion(JournalPerfomanceForEditorVO editorPerformance,
            JournalPerformanceVo journalPerformanceVO) {

        List<Object[]> firstRevisionData = manuScriptRepo.findFirstRevisionCompleted(journalPerformanceVO.getJournalId());
        List<Object[]> firstDecisionBeforeYearData = manuScriptRepo.findFirstDecisionBeforeYear(journalPerformanceVO.getJournalId(),
            journalPerformanceVO.getYear());
        List<Object[]> firstDecisionInYearData = manuScriptRepo.findFirstDecisionInYear(journalPerformanceVO.getJournalId(),
          journalPerformanceVO.getYear());

        List<Long> daysDifferences = new ArrayList<>();
        Map<Integer, LocalDateTime> firstRevisionMap = new HashMap<>();
        Map<Integer, LocalDateTime> firstDecisionBeforeYearMap = new HashMap<>();
        Map<Integer, LocalDateTime> firstDecisionInYearMap = new HashMap<>();

        /*
        for (Object[] data : firstRevisionData) {
            Integer manuscriptId = (Integer) data[0];
            LocalDateTime firstRevisionDate = ((java.sql.Timestamp) data[1]).toLocalDateTime();
            firstRevisionMap.put(manuscriptId, firstRevisionDate);
        }

        for (Object[] data : firstDecisionBeforeYearData) {
            Integer manuscriptId = (Integer) data[0];
            LocalDateTime firstDecisionDate = ((java.sql.Timestamp) data[1]).toLocalDateTime();
            firstDecisionBeforeYearMap.put(manuscriptId, firstDecisionDate);
        }

        for (Object[] data : firstDecisionInYearData) {
          Integer manuscriptId = (Integer) data[0];
            LocalDateTime firstDecisionDate = ((java.sql.Timestamp) data[1]).toLocalDateTime();
            firstDecisionInYearMap.put(manuscriptId, firstDecisionDate);
        }
        */
        //start mine

        for (Object[] data : firstRevisionData) {
        	if (data[0] != null && data[1] != null) {
        		Integer manuscriptId = (Integer) data[0];
        		LocalDateTime firstRevisionDate = ((Timestamp) data[1]).toLocalDateTime();
        		firstRevisionMap.put(manuscriptId, firstRevisionDate);
        		}
        	}


        for (Object[] data : firstDecisionBeforeYearData) {
        	if (data[0] != null && data[1] != null) {
        		Integer manuscriptId = (Integer) data[0];
        		LocalDateTime firstDecisionDate = ((Timestamp) data[1]).toLocalDateTime();
        		firstDecisionBeforeYearMap.put(manuscriptId, firstDecisionDate);
        		}
        	}


        for (Object[] data : firstDecisionInYearData) {
        	if (data[0] != null && data[1] != null) {
        		Integer manuscriptId = (Integer) data[0];
        		LocalDateTime firstDecisionDate = ((Timestamp) data[1]).toLocalDateTime();
        		firstDecisionInYearMap.put(manuscriptId, firstDecisionDate);
        		}
        	}


        //end mine

        for (Integer manuscriptId : firstRevisionMap.keySet()) {
            LocalDateTime firstRevisionDate = firstRevisionMap.get(manuscriptId);
            LocalDateTime firstDecisionBeforeYearDate = firstDecisionBeforeYearMap.get(manuscriptId);
            LocalDateTime firstDecisionInYearDate = firstDecisionInYearMap.get(manuscriptId);

            if (firstDecisionInYearDate != null && firstDecisionInYearDate.getYear() == journalPerformanceVO.getYear() &&
                firstDecisionInYearDate.isAfter(firstRevisionDate)) {

                if (firstDecisionBeforeYearDate != null && firstDecisionBeforeYearDate.getYear() < journalPerformanceVO.getYear()) {
                } else if (firstDecisionInYearDate.getYear() == journalPerformanceVO.getYear()) {
                    long daysBetween = Duration.between(firstRevisionDate, firstDecisionInYearDate).toDays()+1;
                    daysDifferences.add(daysBetween);
                }
            }
        }


        setStatistics(editorPerformance::setDaysTakenByEditorForDecisionOnRevison,
           editorPerformance::setDaysTakenByEditorForDecisionOnRevisonMax,
           editorPerformance::setDaysTakenByEditorForDecisionOnRevisonMin,
          daysDifferences);
        
	}

	private void populateReReviewComplete(JournalPerfomanceForEditorVO editorPerformance,
            JournalPerformanceVo journalPerformanceVO) {
		System.out.println("ReReview TO complete");
		List<Object[]> reReviewData = manuScriptRepo.getReReviewsComplete(journalPerformanceVO.getJournalId(),
	            journalPerformanceVO.getYear());
		
		List<Long> daysForReReviewComplete = extractDaysBetween(reReviewData, 1, 2);
		
	    setStatistics(editorPerformance::setDaysTakenByReviewersForRereview,
	            editorPerformance::setDaysTakenByReviewersForRereviewMax,
	            editorPerformance::setDaysTakenByReviewersForRereviewMin,
	            daysForReReviewComplete);
		
	}
	
	private void populateSecondRevisionCompletionToFinalDecision(JournalPerfomanceForEditorVO editorPerformance,
            JournalPerformanceVo journalPerformanceVO) {
		List<Object[]> SecondRevisionCompletionToFinalDecisionData = manuScriptRepo.getSecondRevisionCompletionToFinalDecision(journalPerformanceVO.getJournalId(),
	            journalPerformanceVO.getYear());
		
		List<Long> daysForSecondRevisionCompletionToFinalDecision = extractDaysBetween(SecondRevisionCompletionToFinalDecisionData, 1, 2);
		
	    setStatistics(editorPerformance::setDaysFromRevisionReceiptToFinalDecision,
	            editorPerformance::setDaysFromRevisionReceiptToFinalDecisionMax,
	            editorPerformance::setDaysFromRevisionReceiptToFinalDecisionMin,
	            daysForSecondRevisionCompletionToFinalDecision);
		
	}
	
	private void populateSubmissionToFinalDecision(JournalPerfomanceForEditorVO editorPerformance,
            JournalPerformanceVo journalPerformanceVO) {
		List<Object[]> submissionToFinalDecisionData = manuScriptRepo.getSubmissionToFinalDecision(journalPerformanceVO.getJournalId(),
	            journalPerformanceVO.getYear());
		
		List<Long> daysForsubmissionToFinalDecisionData =extractDaysBetween(submissionToFinalDecisionData,1,2);
	    setStatistics(editorPerformance::setDaysFromsubmissionToFinalDecision,
	            editorPerformance::setDaysFromsubmissionToFinalDecisionMax,
	            editorPerformance::setDaysFromsubmissionToFinalDecisionMin,
	            daysForsubmissionToFinalDecisionData);
		
	}
	
	private void populateSubmissionToAcceptance(JournalPerformanceVo journalPerfomanceVO) {
		
		List<Object[]> submissionToAcceptanceData = manuScriptRepo.getSubmissionToAcceptance(journalPerfomanceVO.getJournalId(),
				journalPerfomanceVO.getYear());
		
		List<Long> daysForSubmissionToAcceptance = extractDaysBetween(submissionToAcceptanceData,1,2);
	    setStatistics(journalPerfomanceVO::setDaysFirstSubmissionToAcceptance,
	    		journalPerfomanceVO::setSubmissionToAcceptanceMax,
	    		journalPerfomanceVO::setSubmissionToAcceptanceMin,
	    		daysForSubmissionToAcceptance);
		
	}
	
	private void populateAcceptanceToPublication(JournalPerformanceVo journalPerfomanceVO) {
		List<Object[]> acceptanceToPublicationData = manuScriptRepo.getAcceptanceToPublication(journalPerfomanceVO.getJournalId(),
				journalPerfomanceVO.getYear());
		List<Long> daysForAcceptanceToPublication = extractDaysBetween(acceptanceToPublicationData,1,2);
	    setStatistics(journalPerfomanceVO::setDaysFromAcceptanceToPublication,
	    		journalPerfomanceVO::setAcceptanceToPublicationMax,
	    		journalPerfomanceVO::setAcceptanceToPublicationMin,
	    		daysForAcceptanceToPublication);
		
	}
	
	private void populateSubmissionToPublication(JournalPerformanceVo journalPerfomanceVO) {
		List<Object[]> submissionToPublicationData = manuScriptRepo.getSubmissionToPublication(journalPerfomanceVO.getJournalId(),
				journalPerfomanceVO.getYear());
		List<Long> daysForSubmissionToPublication = extractDaysBetween(submissionToPublicationData,1,2);
	    setStatistics(journalPerfomanceVO::setDaysFromSubmissionToPublication,
	    		journalPerfomanceVO::setSubmissionToPublicationMax,
	    		journalPerfomanceVO::setSubmissionToPublicationMin,
	    		daysForSubmissionToPublication);
		
	}
	
	// Calculate Days Between Two Timestamps
	private Long calculateDaysBetween(Timestamp startTimestamp, Timestamp endTimestamp) {
	    if (startTimestamp != null && endTimestamp != null) {
	        LocalDate startDate = startTimestamp.toLocalDateTime().toLocalDate();
	        LocalDate endDate = endTimestamp.toLocalDateTime().toLocalDate();
	        return !endDate.isBefore(startDate) ? ChronoUnit.DAYS.between(startDate, endDate) + 1 : null;
	    }
	    return null;
	}
	
	
	// Extract Days Between Dates
	private List<Long> extractDaysBetween(List<Object[]> data, int startIdx, int endIdx) {
	    return data.stream()
	            .map(d -> calculateDaysBetween(toTimestamp(d[startIdx]), toTimestamp(d[endIdx])))
	            .filter(Objects::nonNull)
	            .collect(Collectors.toList());
	}
	private Timestamp toTimestamp(Object dateObject) {
	    if (dateObject instanceof Timestamp) {
	        return (Timestamp) dateObject;
	    } else if (dateObject instanceof java.sql.Date) {
	        return new Timestamp(((java.sql.Date) dateObject).getTime());
	    }
	    return null; 
	}
	
	// Set Statistics
	private void setStatistics(Consumer<Float> avgSetter, Consumer<Long> maxSetter, Consumer<Long> minSetter,
	                           List<Long> daysList) {
	    // Calculate average
        float average = (float) Math.round(
                daysList.stream().mapToLong(Long::longValue).average().orElse(0.0) * 100) / 100;
        avgSetter.accept(average);

        // Calculate max
        long maxDay = daysList.stream().mapToLong(Long::longValue).max().orElse(0L);
        maxSetter.accept(maxDay);

        // Calculate min
        long minDay = daysList.stream().mapToLong(Long::longValue).min().orElse(0L);
        minSetter.accept(minDay);

        // Log the values
        logger.info("Statistics computed: Average = {}, Max = {}, Min = {}", average, maxDay, minDay);

	}

	@Override
	public Journal getjournalById(Integer journalId) {

		return journalsRepo.getOne(journalId);
	}
	// Author: NIC
	// Date: 2023-09-07
	// Description: This method improve for fetching time of journal details.
	@Override
	public List<JournalDetailsVo> getJournalAdmin(JournalDetailsVo jdetails) {
	    if (jdetails == null) {
	        return Collections.emptyList(); // Handle null input gracefully
	    }

	    Pageable paging = PageRequest.of(jdetails.getPageNo(), jdetails.getLimit());
	    List<AdminJournalVo> journal;

	    if (StringUtils.isNotEmpty(jdetails.getSearch())) {
	        journal = journalsRepo.getActiveJournalListSearch(jdetails.getSearch(), paging);
	    } else {
	        journal = journalsRepo.getActiveJournalList(paging);
	    }
	    
	    List<Integer> journalIdList = journal.stream().map(AdminJournalVo::getJournalId).collect(Collectors.toList());

	    List<ManuscriptReportDataVO> manuscriptReportData = manuScriptRepo.getManuscriptForAdmin(journalIdList)
	            .stream()
	            .map(t -> new ManuscriptReportDataVO(t.get(0, Integer.class), t.get(1, Integer.class), t.get(2, Integer.class)))
	            .collect(Collectors.toList());

	    Map<Integer, List<ManuscriptReportDataVO>> journalManuscriptMap = manuscriptReportData
	            .stream()
	            .collect(Collectors.groupingBy(ManuscriptReportDataVO::getJournalId));

	    List<Tuple> userList = userJournalRepo.userWithRole(journalIdList);
	    List<UserVO> userRoleList = userList
	            .stream()
	            .map(t -> new UserVO(t.get(0, Integer.class), t.get(1, Integer.class), t.get(2, Integer.class)))
	            .collect(Collectors.toList());

	    Map<Integer, List<UserVO>> userRoleMap = userRoleList
	            .stream()
	            .collect(Collectors.groupingBy(UserVO::getJournal));

	    List<JournalDetailsVo> journalData = new ArrayList<>();

	    if (journal != null) {
	        for (AdminJournalVo journal1 : journal) {
	            JournalDetailsVo tempData = createJournalDetails(journal1, journalManuscriptMap, userRoleMap);
	            journalData.add(tempData);
	        }
	    }

	    return journalData;
	}

	private JournalDetailsVo createJournalDetails(
	        AdminJournalVo journal,
	        Map<Integer, List<ManuscriptReportDataVO>> journalManuscriptMap,
	        Map<Integer, List<UserVO>> userRoleMap) {

	    JournalDetailsVo tempData = new JournalDetailsVo();
	    tempData.setJournalTitle(journal.getJournalTitle());
	    tempData.setJournalId(journal.getJournalId());
	    tempData.setJournalCode(journal.getJournalCode());

	    List<ManuscriptReportDataVO> manuscriptStatusList = journalManuscriptMap.getOrDefault(journal.getJournalId(), Collections.emptyList());
	    Map<Integer, List<ManuscriptReportDataVO>> manuscriptStatusMap = manuscriptStatusList
	            .stream()
	            .collect(Collectors.groupingBy(ManuscriptReportDataVO::getManuscriptStatus));

	    tempData.setTotalManuscript(manuscriptStatusList.size());
	    tempData.setAcceptedManuscript(manuscriptStatusMap.getOrDefault(33, Collections.emptyList()).size());
	    tempData.setRejectedManuscript(manuscriptStatusMap.getOrDefault(30, Collections.emptyList()).size());
	    tempData.setInReviewManuscript(tempData.getTotalManuscript() - tempData.getAcceptedManuscript() - tempData.getRejectedManuscript());

	    List<UserVO> userRoleList = userRoleMap.getOrDefault(journal.getJournalId(), Collections.emptyList());
	    Map<Integer, List<UserVO>> roleMap = userRoleList
	            .stream()
	            .collect(Collectors.groupingBy(UserVO::getUserRole));

	    tempData.setManagingeditor(roleMap.getOrDefault(2, Collections.emptyList()).size());
	    tempData.setEditor(roleMap.getOrDefault(3, Collections.emptyList()).size());

	    return tempData;
	}

	@Override
	public List<JournalVO> getByJournalCode(IDVO id) {
		List<JournalVO> addJournalVo = new ArrayList<>();
		List<Journal> listJournal = journalsRepo.getByJournalCode(id.getJournalCode());
		if (listJournal != null && !listJournal.isEmpty()) {
			addJournalVo = JournalTransformationUtils.mapJournalModelToVO(listJournal,defaultJournalCurrentIssueLink , defaultJournalAboutLink, defaultJournalAuthorInstructionsLink, defaultThumbnailPath);
		}
		return addJournalVo;
	}

	@Override
	public JournalPoliciesVO getOperationalPolices(JournalPoliciesVO journalPoliciesVO) {
		JournalPoliciesVO addJournalPoliciesVO =new JournalPoliciesVO();
		if (journalPoliciesVO.getJournalId() != null) {
			JournalPolicies getJournalPolicies = journalPoliciesRepo.getJournalPoliciesDetails(journalPoliciesVO.getJournalId());
			if (getJournalPolicies != null) {
				addJournalPoliciesVO.setJournalId(getJournalPolicies.getJournalId());
				addJournalPoliciesVO.setId(getJournalPolicies.getId());
				addJournalPoliciesVO.setApcSubmissionCharge(getJournalPolicies.getApcSubmissionCharge());
				addJournalPoliciesVO.setAuthorProfileTime(getJournalPolicies.getAuthorProfileTime());
				addJournalPoliciesVO.setCheckListTechnicalModificationRevision(getJournalPolicies.getCheckListTechnicalModificationRevision());
				addJournalPoliciesVO.setCopyrightForm(getJournalPolicies.getCopyrightForm());
				addJournalPoliciesVO.setFirstPageTechnicalModificationRevision(getJournalPolicies.getFirstPageTechnicalModificationRevision());
				addJournalPoliciesVO.setInvitedReviewers(getJournalPolicies.getInvitedReviewers());
				addJournalPoliciesVO.setManuscriptsStatusMail(getJournalPolicies.getManuscriptsStatusMail());
				addJournalPoliciesVO.setOrcIdMandatory(getJournalPolicies.getOrcIdMandatory());
				addJournalPoliciesVO.setReminderFrequencyAuthor(getJournalPolicies.getReminderFrequencyAuthor());
				addJournalPoliciesVO.setReminderFrequencyReviewers(getJournalPolicies.getReminderFrequencyReviewers());
				addJournalPoliciesVO.setReminderFrequencyEicEditor(getJournalPolicies.getReminderFrequencyEicEditor());
				addJournalPoliciesVO.setJournalArticleAutoWithdrawWeeks(getJournalPolicies.getJournalArticleAutoWithdrawWeeks());
				addJournalPoliciesVO.setSubmissionRequiresCme(getJournalPolicies.getSubmissionRequiresCme());
				addJournalPoliciesVO.setWithdrawManuscript(getJournalPolicies.getWithdrawManuscript());
				addJournalPoliciesVO.setOtherReviewersFlag(getJournalPolicies.getOtherReviewersFlag());
				addJournalPoliciesVO.setModifyAuthorDetailsTechnicalModificationRevision(getJournalPolicies.getModifyAuthorDetailsTechnicalModificationRevision());
				addJournalPoliciesVO.setCopyrightMandatoryForAllAuthors(getJournalPolicies.getCopyrightMandatoryForAllAuthors());
			}
			JournalMetadata metadata = journalMetadataRepo.metaDatalistByJournalId(journalPoliciesVO.getJournalId());
			if (metadata!=null) {
				addJournalPoliciesVO.setBlindType(metadata.getJournalBlindType());
			}
		}
		return addJournalPoliciesVO;
	}

	@Override
	public String setupOperationalPolices(JournalPoliciesVO journalPoliciesVO) {
		
	    if (journalPoliciesVO == null || journalPoliciesVO.getJournalId() == null) {
	        return null;
	    }

	    if (journalPoliciesVO.getReminderFrequencyAuthor() == null) {
	        journalPoliciesVO.setReminderFrequencyAuthor(0);
	    }

	    if (journalPoliciesVO.getReminderFrequencyEicEditor() == null) {
	        journalPoliciesVO.setReminderFrequencyEicEditor(0);
	    }

	    if (journalPoliciesVO.getReminderFrequencyReviewers() == null) {
	        journalPoliciesVO.setReminderFrequencyReviewers(0);
	    }
		
		if (journalPoliciesVO.getJournalId() != null) {
			JournalPolicies getJournalPolicies = journalPoliciesRepo.getJournalPoliciesDetails(journalPoliciesVO.getJournalId());
			if(getJournalPolicies != null) {
				getJournalPolicies.setApcSubmissionCharge(journalPoliciesVO.getApcSubmissionCharge());
				getJournalPolicies.setAuthorProfileTime(journalPoliciesVO.getAuthorProfileTime());
				getJournalPolicies.setCheckListTechnicalModificationRevision(journalPoliciesVO.getCheckListTechnicalModificationRevision());
				getJournalPolicies.setCopyrightForm(journalPoliciesVO.getCopyrightForm());
				getJournalPolicies.setFirstPageTechnicalModificationRevision(journalPoliciesVO.getFirstPageTechnicalModificationRevision());
				getJournalPolicies.setInvitedReviewers(journalPoliciesVO.getInvitedReviewers());
				getJournalPolicies.setManuscriptsStatusMail(journalPoliciesVO.getManuscriptsStatusMail());
				getJournalPolicies.setOrcIdMandatory(journalPoliciesVO.getOrcIdMandatory());
				getJournalPolicies.setReminderFrequencyAuthor(journalPoliciesVO.getReminderFrequencyAuthor());
				getJournalPolicies.setReminderFrequencyReviewers(journalPoliciesVO.getReminderFrequencyReviewers());
				getJournalPolicies.setReminderFrequencyEicEditor(journalPoliciesVO.getReminderFrequencyEicEditor());
				getJournalPolicies.setJournalArticleAutoWithdrawWeeks(journalPoliciesVO.getJournalArticleAutoWithdrawWeeks());
				getJournalPolicies.setSubmissionRequiresCme(journalPoliciesVO.getSubmissionRequiresCme());
				getJournalPolicies.setWithdrawManuscript(journalPoliciesVO.getWithdrawManuscript());
				getJournalPolicies.setUpdatedBy(journalPoliciesVO.getCreatedBy());
				getJournalPolicies.setOtherReviewersFlag(journalPoliciesVO.getOtherReviewersFlag());
				getJournalPolicies.setUpdatedAt(new Date());
				getJournalPolicies.setModifyAuthorDetailsTechnicalModificationRevision(journalPoliciesVO.getModifyAuthorDetailsTechnicalModificationRevision());
				getJournalPolicies.setCopyrightMandatoryForAllAuthors(journalPoliciesVO.getCopyrightMandatoryForAllAuthors());
				journalPoliciesRepo.save(getJournalPolicies);
			}else {
				JournalPolicies addJournalPolicies = new JournalPolicies();
				addJournalPolicies.setJournalId(journalPoliciesVO.getJournalId());
				addJournalPolicies.setApcSubmissionCharge(journalPoliciesVO.getApcSubmissionCharge());
				addJournalPolicies.setAuthorProfileTime(journalPoliciesVO.getAuthorProfileTime());
				addJournalPolicies.setCheckListTechnicalModificationRevision(journalPoliciesVO.getCheckListTechnicalModificationRevision());
				addJournalPolicies.setCopyrightForm(journalPoliciesVO.getCopyrightForm());
				addJournalPolicies.setFirstPageTechnicalModificationRevision(journalPoliciesVO.getFirstPageTechnicalModificationRevision());
				addJournalPolicies.setInvitedReviewers(journalPoliciesVO.getInvitedReviewers());
				addJournalPolicies.setManuscriptsStatusMail(journalPoliciesVO.getManuscriptsStatusMail());
				addJournalPolicies.setOrcIdMandatory(journalPoliciesVO.getOrcIdMandatory());
				addJournalPolicies.setReminderFrequencyAuthor(journalPoliciesVO.getReminderFrequencyAuthor());
				addJournalPolicies.setReminderFrequencyReviewers(journalPoliciesVO.getReminderFrequencyReviewers());
				addJournalPolicies.setReminderFrequencyEicEditor(journalPoliciesVO.getReminderFrequencyEicEditor());
				addJournalPolicies.setJournalArticleAutoWithdrawWeeks(journalPoliciesVO.getJournalArticleAutoWithdrawWeeks());
				addJournalPolicies.setSubmissionRequiresCme(journalPoliciesVO.getSubmissionRequiresCme());
				addJournalPolicies.setWithdrawManuscript(journalPoliciesVO.getWithdrawManuscript());
				addJournalPolicies.setCreatedBy(journalPoliciesVO.getCreatedBy());
				addJournalPolicies.setOtherReviewersFlag(journalPoliciesVO.getOtherReviewersFlag());
				addJournalPolicies.setCreatedAt(new Date());
				addJournalPolicies.setModifyAuthorDetailsTechnicalModificationRevision(journalPoliciesVO.getModifyAuthorDetailsTechnicalModificationRevision());
				addJournalPolicies.setCopyrightMandatoryForAllAuthors(journalPoliciesVO.getCopyrightMandatoryForAllAuthors());
				journalPoliciesRepo.save(addJournalPolicies);
			}
			JournalMetadata metaData = journalMetadataRepo.metaDatalistByJournalId(journalPoliciesVO.getJournalId());
			if(metaData !=null) {
				metaData.setJournalBlindType(journalPoliciesVO.getBlindType());
				metaData.setUpdatedBy(journalPoliciesVO.getCreatedBy());
				metaData.setUpdatedOn(new Date());
				journalMetadataRepo.save(metaData);
			}else {
				JournalMetadata jMeta = new JournalMetadata();
				jMeta.setJournalId(journalPoliciesVO.getJournalId());
				jMeta.setJournalBlindType(journalPoliciesVO.getBlindType());
				jMeta.setCreatedBy(journalPoliciesVO.getCreatedBy());
				jMeta.setCreatedOn(new Date());
				journalMetadataRepo.save(jMeta);
			}
			return "Data has been saved";
		}
		return null;
	}

	@Override
	public List<IDVO> getByJournalEic(IDVO id) {
		List<IDVO> addIDVO=new ArrayList<>();
		List<UserJournal> getJournalData=userJournalRepo.getUserRoleJournalId(id.getId(),2);
		if(getJournalData!=null && !getJournalData.isEmpty()) {
			getJournalData.stream().forEach(i->{
				User u=userRepo.getOne(i.getUserId());
				if(u!=null) {
				IDVO addIDVOs=new IDVO();
				addIDVOs.setUserId(u.getId());
				addIDVOs.setEmail(u.getEmail());
				addIDVOs.setName(u.getSalutation()+"."+u.getFirstName()+" "+u.getLastName());
				addIDVOs.setRoleId(2);
				addIDVO.add(addIDVOs);
				}
			});
		}
		return addIDVO;
	}

	@Override
	public List<ManuscriptTypeJournal> getManuscriptTypeByJournal(Integer journalId) {
		return manuscriptTypeJournalsRepo.getManuscriptTypesFromJournalId(journalId);
	}

	@Override
	public Journal getJournalByAcronym(String name) {
		return journalsRepo.getJournalByCodeForUnitouch(name);
	}

	@Override
	public IDVO updateManuscriptId(String name) {
		Journal journal = journalsRepo.getJournalByCodeForUnitouch(name);
		Journal journalData = new Journal();
		IDVO data = new IDVO();
		if(journal!=null) {
			journal.setJournalManuscriptCount(journal.getJournalManuscriptCount()+1);
			journalData = journalsRepo.save(journal);
			data.setManuscriptStatus(journalData.getJournalMetadata().getJournalDOI() +journalData.getJournalCode() +"." +journalData.getJournalCode() +"_" +journalData.getJournalManuscriptCount() +"_" +journalData.getJournalManuscriptYear());
			data.setManuscriptCode(journalData.getJournalCode() +"_" +journalData.getJournalManuscriptCount() +"_" +journalData.getJournalManuscriptYear());
		}
		
		return data;
	}

	@Override
	public List<JournalListVO> getAllJournalList() {
		List<Journal> list = journalsRepo.getAllJournalList();
		List<JournalListVO> voList = JournalListTransformationUtils.mapModelListToVoListForAllJournal(list);
		return voList;
	}
	
	private HttpServiceRequestInfo prepareHttpServiceRequestInfo(String apiURL, String journalCode) {
		HttpServiceRequestInfo requestInfo = new HttpServiceRequestInfo();
		requestInfo.setRequestUri(apiURL);
		requestInfo.setCharset(StandardCharsets.UTF_8.name());
		requestInfo.setMethodType(AppConstants.POST);

		StringBuilder jsonStringBuilder = new StringBuilder();
		jsonStringBuilder.append("{");
		jsonStringBuilder.append("\"journalAcronym\":\"");
		jsonStringBuilder.append(journalCode);
		jsonStringBuilder.append("\"}");
		logger.info("Unitouch updateArticle request body : " + jsonStringBuilder);
		requestInfo.setRequestBody(jsonStringBuilder.toString());

		return requestInfo;
	}

	@Override
	public String updateJournalActivation(JournalListVO journalListVO, boolean isUnitouchTrue) {
		Journal getJournal = null;
		if(isUnitouchTrue) {
			getJournal=journalsRepo.getOne(journalListVO.getJournalId());
		}else {
			getJournal=journalsRepo.getJournalByCodeValidateAcron(journalListVO.getJournalCode());
		}
		if(getJournal!=null) {
			getJournal.setIsActive(journalListVO.getIsActive());
			journalsRepo.save(getJournal);
			// UN-4811 hitesh 17-09-2023 //
			if(isUnitouchTrue) {
				// Call Unitouch to deactivate the same journal //
				logger.info("Calling Unitouch updateJournalActivation");
				if(journalListVO.getIsActive() == 0) {
					String uri = unitouchUrl + "/deactivateJournalByAbbrv";

					HttpServiceRequestInfo requestInfo = prepareHttpServiceRequestInfo(uri, getJournal.getJournalCode());
					String resultString = httpService.callJsonPOSTService(requestInfo);

					logger.info("Response from Unitouch API JournalController.deactivateJournalByAbbrv: " + resultString);
				}else {
					String uri = unitouchUrl + "/activateJournalByAbbrv";

					HttpServiceRequestInfo requestInfo = prepareHttpServiceRequestInfo(uri, getJournal.getJournalCode());
					String resultString = httpService.callJsonPOSTService(requestInfo);

					logger.info("Response from Unitouch API JournalController.activateJournalByAbbrv: " + resultString);
				}
			}
			
			return "Journal updated successfully";
		}
		return "Journal is not valid";
	}
	
	@Override
	public String updateJournalValidateAcro(JournalListVO journalListVO) {
		Journal getJournal=journalsRepo.getJournalByCodeValidateAcron(journalListVO.getJournalCode());
		String str= "Not found";
		if(getJournal!=null)
		{
			str="Journal Code:- " +getJournal.getJournalCode()+", Journal Name:- "+getJournal.getJournalTitle();
		}
		
		return str;
	}
	
	@Override
	public boolean isJournalExistsByCode(JournalListVO journalListVO) {
		Journal getJournal=journalsRepo.getJournalByCodeValidateAcron(journalListVO.getJournalCode());
		boolean exsits= false;
		if(getJournal!=null){
			exsits=true;
		}
		return exsits;
	}

	@Override
	public int journalCountSearch(JournalDetailsVo jdetails) {
		int journalCount = journalsRepo.journalCountSearch(jdetails.getSearch());
		return journalCount;
	}
	
	@Override
	public int journalCount() {
		int journalCount = journalsRepo.journalCount();
		return journalCount;
	}

	@Override
	public String updateJournalReviewerSetting(Journal journal) {
		journalsRepo.updateJournalReviewerSetting(journal.getReviewerSuggestion(), journal.getReviewerCount(), journal.getJournalId());
		return "Updated successfully";
	}

	@Override
	public String updateCoAuthorCount(Journal journal) {

		journalsRepo.updateCoAuthorCount(journal.getCorrespondingCount(), journal.getJournalId());
		return "Updated successfully";
	}
	
	@Override
	public JournalMetadata getJournalMetadata(int journalId) {
		return journalMetadataRepo.metaDatalistByJournalId(journalId);
	}

	@Override
	public String updateJournalCharge(int journalId, String chargeStatus, String chargeOn) {
		Journal journal = journalsRepo.getById(journalId);
		journal.setChargeFlag(chargeStatus);
		journalsRepo.save(journal);
		// save journal metadata for chargeOn
		JournalMetadata journalMetaData = journalMetadataRepo.metaDatalistByJournalId(journalId);
		journalMetaData.setChargeOn(chargeOn);
		journalMetaData.setIsChargingRequired(chargeStatus);
		journalMetadataRepo.save(journalMetaData);
		
	    JournalChargeFlagHistory history = new JournalChargeFlagHistory(journalId, chargeStatus);
	    historyRepo.save(history);
		
		return "Updated journal charge status successfully";
	}
}