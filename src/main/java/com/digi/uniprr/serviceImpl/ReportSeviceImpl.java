package com.digi.uniprr.serviceImpl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.persistence.Tuple;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digi.uniprr.VO.AcceptedArticleVo;
import com.digi.uniprr.VO.ArticleRatingVo;
import com.digi.uniprr.VO.ArticleReportVo;
import com.digi.uniprr.VO.AuthorCountryVo;
import com.digi.uniprr.VO.AuthorPerformanceVo;
import com.digi.uniprr.VO.DecisionForEditorVO;
import com.digi.uniprr.VO.EditorPerformanceVo;
import com.digi.uniprr.VO.EditorPresentDataVO;
import com.digi.uniprr.VO.GetRefreeDetailsVO;
import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.VO.IndividualAutorDetailsVO;
import com.digi.uniprr.VO.JournalDetailsVo;
import com.digi.uniprr.VO.JournalListVO;
import com.digi.uniprr.VO.LoggedUserVo;
import com.digi.uniprr.VO.ManuScriptByJournalVo;
import com.digi.uniprr.VO.ManuscriptListVO;
import com.digi.uniprr.VO.ManuscriptReportDataVO;
import com.digi.uniprr.VO.MonthlyStatisticsVO;
import com.digi.uniprr.VO.PerCountryManuscriptVo;
import com.digi.uniprr.VO.PerformanceYearListVo;
import com.digi.uniprr.VO.PerformanceYearVo;
import com.digi.uniprr.VO.PresentStatusEicVo;
import com.digi.uniprr.VO.PresentStatusForEiCVO;
import com.digi.uniprr.VO.PresentStatusVO;
import com.digi.uniprr.VO.RejectedArticlesVo;
import com.digi.uniprr.VO.ReportVO;
import com.digi.uniprr.VO.ReviewerDataWithCommentVO;
import com.digi.uniprr.VO.ReviewerOverviewVO;
import com.digi.uniprr.VO.ReviewerPerformanceDTO;
import com.digi.uniprr.VO.ReviewerReportDataVO;
import com.digi.uniprr.VO.ReviewersPerformanceVo;
import com.digi.uniprr.VO.SubmisionReportVO;
import com.digi.uniprr.VO.UserExportVo;
import com.digi.uniprr.VO.UserJournalManuscriptVo;
import com.digi.uniprr.VO.UserReportVO;
import com.digi.uniprr.VO.UserVO;
import com.digi.uniprr.enums.Salutation;
import com.digi.uniprr.model.AuthoritiesHistory;
import com.digi.uniprr.model.Countries;
import com.digi.uniprr.model.Journal;
import com.digi.uniprr.model.ManuScript;
import com.digi.uniprr.model.ManuScriptReviewer;
import com.digi.uniprr.model.ManuscriptEditor;
import com.digi.uniprr.model.ManuscriptRating;
import com.digi.uniprr.model.ManuscriptTypeDetails;
import com.digi.uniprr.model.ManuscriptTypeJournal;
import com.digi.uniprr.model.ReviewerRating;
import com.digi.uniprr.model.User;
import com.digi.uniprr.model.UserJournal;
import com.digi.uniprr.model.UserLogins;
import com.digi.uniprr.repository.AuthoritiesHistoryRepo;
import com.digi.uniprr.repository.CountriesRepo;
import com.digi.uniprr.repository.JournalsRepo;
import com.digi.uniprr.repository.ManuScriptRepo;
import com.digi.uniprr.repository.ManuScriptReviewerRepo;
import com.digi.uniprr.repository.ManuscriptEditorRepo;
import com.digi.uniprr.repository.ManuscriptRatingRepo;
import com.digi.uniprr.repository.ManuscriptTypeDetailsRepo;
import com.digi.uniprr.repository.ManuscriptTypeJournalsRepo;
import com.digi.uniprr.repository.ManuscriptTypeRepo;
import com.digi.uniprr.repository.ReviewerRatingRepo;
import com.digi.uniprr.repository.UserJournalRepo;
import com.digi.uniprr.repository.UserLoginsRepo;
import com.digi.uniprr.repository.UserRepo;
import com.digi.uniprr.repository.UserRoleRepo;
import com.digi.uniprr.service.ManuScriptReviewerService;
import com.digi.uniprr.service.ManuscriptDashboardArticleService;
import com.digi.uniprr.service.ReportSevice;
import com.digi.uniprr.VO.ManuscriptEditorVO;

@Service
@Transactional
public class ReportSeviceImpl implements ReportSevice {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	ManuScriptReviewerService manuScriptReviewerService;
	@Autowired
	ManuScriptRepo manuScriptRepo;
	@Autowired
	private UserJournalRepo userJournalRepo;
	@Autowired
	private ManuScriptReviewerRepo manuScriptReviewerRepo;
	@Autowired
	private JournalsRepo journalsRepo;
	@Autowired
	private ManuscriptTypeJournalsRepo manuscriptTypeJournalsRepo;
	@Autowired
	private ManuscriptEditorRepo manuscriptEditorRepo;
	@Autowired
	private ManuscriptTypeDetailsRepo manuscriptTypeDetailsRepo;
	@Autowired
	UserRoleRepo userRoleRepo;
	@Autowired
	UserRepo userRepo;
	@Autowired
	UserLoginsRepo userLoginsRepo;
	@Autowired
	ManuscriptRatingRepo manuscriptRatingRepo;

	@Autowired
	AuthoritiesHistoryRepo authoritiesHistoryRepo;
	
	@Autowired
	ReviewerRatingRepo reviewerRatingRepo;

	@Autowired
	CountriesRepo countriesRepo;

	@Autowired
	private ManuscriptDashboardArticleService manuscriptDashboardArticleService;
	
	private static final int BATCH_SIZE = 100;

	@Override
	public List<ReportVO> getReport(UserJournalManuscriptVo userJournalManuscriptVo) {
		List<ReportVO> report = new ArrayList<>();
		List<ManuScript> manuscriptData = manuScriptRepo.getManuscriptByUser(userJournalManuscriptVo.getUserId(),
				userJournalManuscriptVo.getManuscriptStatusId());

		for (ManuScript m : manuscriptData) {
			ReportVO reportTempData = new ReportVO();
			reportTempData.setAuthorId(m.getAuthorId());
			reportTempData.setManuscriptCode(m.getManuscriptCode());
			reportTempData.setManuscriptTitle(m.getManuscriptTitle());
			reportTempData.setManuscriptVersion(m.getManuscriptVersion());
			reportTempData.setSubmissionDate(m.getSubmissionDate());
			reportTempData.setManuscriptStatusId(m.getManuscriptStatusId());

			reportTempData.setJournalId(m.getJournal().getJournalId());
			reportTempData.setJournalCode(m.getJournal().getJournalCode());
			reportTempData.setJournalTitle(m.getJournal().getJournalTitle());
			reportTempData.setPublisherId(m.getJournal().getPublisherId());
			if (m.getUser() != null) {
				reportTempData.setAuthorName(m.getUser().getFirstName() + m.getUser().getLastName());
			}
			reportTempData.setManuscriptStatusCode(m.getStatusMaster().getStatusValue());

			report.add(reportTempData);
		}

		return report;
	}

	@Override
	public List<ManuScriptByJournalVo> getManuScriptByJournal(ManuScriptByJournalVo manuScriptByJournalVo) {
		List<UserJournal> userJournalList = userJournalRepo.getUserJournalByUserId(manuScriptByJournalVo.getUserId());
		List<ManuScriptByJournalVo> listVo = new ArrayList<>();
		if (userJournalList != null && !userJournalList.isEmpty()) {
			for (UserJournal i : userJournalList) {
				List<ManuScript> manuscript = manuScriptRepo.getManuScriptByJournalId(i.getJournalId());
				ManuScriptByJournalVo journal = new ManuScriptByJournalVo();
				journal.setJournalId(i.getJournalId());
				journal.setJournalCode(i.getJournal().getJournalCode());
				journal.setJournalTitle(i.getJournal().getJournalTitle());
				int new_Submitted_Manuscript = 0;
				int manuscript_in_Review = 0;
				int re_submitted_Manuscript = 0;
				int manuscript_Sent_for_Revision = 0;
				int decision_on_Manuscript = 0;
				int total_Manuscript = 0;
				if (manuScriptByJournalVo.getStatusId() != null)
					manuScriptByJournalVo.getStatusId().contains(2);
				for (ManuScript list : manuscript) {
					if (list.getManuscriptStatusId() == 2)
						new_Submitted_Manuscript++;
					if (list.getManuscriptStatusId() == 3 || list.getManuscriptStatusId() == 35
							|| list.getManuscriptStatusId() == 8)
						manuscript_in_Review++;
					if (list.getManuscriptStatusId() == 26)
						re_submitted_Manuscript++;
					if (list.getManuscriptStatusId() == 4)
						manuscript_Sent_for_Revision++;
					if (list.getManuscriptStatusId() == 30 || list.getManuscriptStatusId() == 33
							|| list.getManuscriptStatusId() == 37 || list.getManuscriptStatusId() == 38)
						decision_on_Manuscript++;

					if (list.getManuscriptStatusId() == 2 || list.getManuscriptStatusId() == 26
							|| list.getManuscriptStatusId() == 3 || list.getManuscriptStatusId() == 35
							|| list.getManuscriptStatusId() == 8 || list.getManuscriptStatusId() == 26
							|| list.getManuscriptStatusId() == 4 || list.getManuscriptStatusId() == 30
							|| list.getManuscriptStatusId() == 33 || list.getManuscriptStatusId() == 37
							|| list.getManuscriptStatusId() == 38)
						total_Manuscript++;
				}
				journal.setNew_Submitted_Manuscript(new_Submitted_Manuscript);
				journal.setManuscript_in_Review(manuscript_in_Review);
				journal.setRe_submitted_Manuscript(re_submitted_Manuscript);
				journal.setManuscript_Sent_for_Revision(manuscript_Sent_for_Revision);
				journal.setDecision_on_Manuscript(decision_on_Manuscript);
				journal.setTotal_Manuscript(total_Manuscript);
				journal.setCount(new_Submitted_Manuscript + manuscript_in_Review + re_submitted_Manuscript
						+ manuscript_Sent_for_Revision + decision_on_Manuscript + total_Manuscript);
				journal.setUserId(manuScriptByJournalVo.getUserId());
				listVo.add(journal);
			}
		}
		return listVo;
	}

	@Override
	public List<ReviewerOverviewVO> getReviewerOverview(Integer userId) {
		List<UserJournal> userJournalList = userJournalRepo.getUserJournalByUserId(userId);
		List<ReviewerOverviewVO> list = new ArrayList<>();
		if (userJournalList != null && !userJournalList.isEmpty()) {
			for (UserJournal i : userJournalList) {
				ReviewerOverviewVO journal = new ReviewerOverviewVO();
				journal.setJournalId(i.getJournalId());
				journal.setJournalCode(i.getJournal().getJournalCode());
				journal.setJournalTitle(i.getJournal().getJournalTitle());
				List<ManuScriptReviewer> manu = manuScriptReviewerRepo
						.getManuscriptReviewerListByJournalId(i.getJournalId());
				Integer acceptedByReviewer = 0;
				Integer rejectedByReviewer = 0;
				Integer minorRevisionReviewer = 0;
				Integer majorRevisionReviewer = 0;
				Integer acceptReviewInvitation = 0;
				Integer rejectReviewInvitation = 0;
				for (ManuScriptReviewer list1 : manu) {
					if (list1.getManuscriptStatus() == 12) {
						acceptedByReviewer++;
					} else if (list1.getManuscriptStatus() == 13) {
						rejectedByReviewer++;
					} else if (list1.getManuscriptStatus() == 14) {
						minorRevisionReviewer++;
					} else if (list1.getManuscriptStatus() == 15) {
						majorRevisionReviewer++;
					} else if (list1.getManuscriptStatus() == 35) {
						acceptReviewInvitation++;
					} else if (list1.getManuscriptStatus() == 36) {
						rejectReviewInvitation++;
					}
				}
				journal.setAccepted_By_Reviewer(acceptedByReviewer);
				journal.setRejected_By_Reviewer(rejectedByReviewer);
				journal.setMinor_Revision_Reviewer(minorRevisionReviewer);
				journal.setMajor_Revision_Reviewer(majorRevisionReviewer);
				journal.setAccept_Review_Invitation(acceptReviewInvitation);
				journal.setReject_Review_Invitation(rejectReviewInvitation);
				list.add(journal);
			}
		}
		return list;
	}

	@Override
	public List<List<ReportVO>> getReportByUserId(IDVO userId) {
		List<List<ReportVO>> testList = new ArrayList<>();

		/*
		 * if (userId.getVersion() != null && userId.getVersion() == 1) {
		 * List<ManuScript> manuscriptList = new ArrayList<>(); List<Integer> JournalId
		 * = new ArrayList<>(); List<Journal> journalList =
		 * journalsRepo.getJournalByUserId(userId.getId());
		 * 
		 * if (journalList != null && !journalList.isEmpty()) { for (Journal journal :
		 * journalList) { JournalId.add(journal.getJournalId()); }
		 * 
		 * if (JournalId != null && !JournalId.isEmpty()) { manuscriptList =
		 * manuScriptRepo.getManuscriptByJournalList(JournalId); } if (manuscriptList !=
		 * null && !manuscriptList.isEmpty()) { for (ManuScript manuscript :
		 * manuscriptList) { ReportVO reportTempData = new ReportVO();
		 * reportTempData.setAuthorId(manuscript.getAuthorId());
		 * reportTempData.setManuscriptCode(manuscript.getManuscriptCode());
		 * reportTempData.setManuscriptTitle(manuscript.getManuscriptTitle());
		 * reportTempData.setManuscriptVersion(manuscript.getManuscriptVersion());
		 * reportTempData.setSubmissionDate(manuscript.getSubmissionDate());
		 * reportTempData.setManuscriptStatusId(manuscript.getManuscriptStatusId());
		 * reportTempData.setJournalId(manuscript.getJournal().getJournalId());
		 * reportTempData.setJournalCode(manuscript.getJournal().getJournalCode());
		 * reportTempData.setJournalTitle(manuscript.getJournal().getJournalTitle());
		 * reportTempData.setPublisherId(manuscript.getJournal().getPublisherId());
		 * reportTempData.setAuthorName( manuscript.getUser().getFirstName() +
		 * manuscript.getUser().getLastName());
		 * reportTempData.setManuscriptStatusCode(manuscript.getStatusMaster().
		 * getStatusValue()); report.add(reportTempData); } } } } else
		 */ if (userId.getVersion() != null && userId.getVersion() == 2) {
			List<ManuScript> manuscriptList = new ArrayList<>();

			List<UserJournal> UserJournals = userJournalRepo.getUserJournalByUserId(userId.getId());

			if (UserJournals != null && !UserJournals.isEmpty()) {
				for (UserJournal journal : UserJournals) {
					List<ReportVO> report = new ArrayList<>();
					/*
					 * JournalId.add(journal.getJournalId()); }
					 */

					// if (JournalId != null && !JournalId.isEmpty()) {
					manuscriptList = manuScriptRepo.getManuScriptByJournalId(journal.getJournalId());
					// }
					if (manuscriptList != null && !manuscriptList.isEmpty()) {
						for (ManuScript manuscript : manuscriptList) {
							ReportVO reportTempData = new ReportVO();
							reportTempData.setAuthorId(manuscript.getAuthorId());
							reportTempData.setManuscriptCode(manuscript.getManuscriptCode());
							reportTempData.setManuscriptTitle(manuscript.getManuscriptTitle());
							reportTempData.setManuscriptVersion(manuscript.getManuscriptVersion());
							reportTempData.setSubmissionDate(manuscript.getSubmissionDate());
							reportTempData.setManuscriptStatusId(manuscript.getManuscriptStatusId());
							reportTempData.setJournalId(manuscript.getJournal().getJournalId());
							reportTempData.setJournalCode(manuscript.getJournal().getJournalCode());
							reportTempData.setJournalTitle(manuscript.getJournal().getJournalTitle());
							reportTempData.setPublisherId(manuscript.getJournal().getPublisherId());
							reportTempData.setAuthorName(
									manuscript.getUser().getFirstName() + manuscript.getUser().getLastName());
							reportTempData.setManuscriptStatusCode(manuscript.getStatusMaster().getStatusValue());
							report.add(reportTempData);
						}
					}
					testList.add(report);
				}
			}
		} /*
			 * else if (userId.getVersion() != null && userId.getVersion() == 3) {
			 * List<ManuScript> manuscriptList = new ArrayList<>(); List<Integer>
			 * ManuscriptId = new ArrayList<>();
			 * 
			 * List<ManuscriptEditor> manuscriptEditorData =
			 * manuscriptEditorRepo.getManuscriptEditorById(userId.getId());
			 * 
			 * if (manuscriptEditorData != null && !manuscriptEditorData.isEmpty()) { for
			 * (ManuscriptEditor manuscriptEditor : manuscriptEditorData) {
			 * ManuscriptId.add(manuscriptEditor.getManuscriptId()); }
			 * 
			 * if (ManuscriptId != null && !ManuscriptId.isEmpty()) { manuscriptList =
			 * manuScriptRepo.findAllById(ManuscriptId); }
			 * 
			 * if (manuscriptList != null && !manuscriptList.isEmpty()) { for (ManuScript
			 * manuscript : manuscriptList) { ReportVO reportTempData = new ReportVO();
			 * reportTempData.setAuthorId(manuscript.getAuthorId());
			 * reportTempData.setManuscriptCode(manuscript.getManuscriptCode());
			 * reportTempData.setManuscriptTitle(manuscript.getManuscriptTitle());
			 * reportTempData.setManuscriptVersion(manuscript.getManuscriptVersion());
			 * reportTempData.setSubmissionDate(manuscript.getSubmissionDate());
			 * reportTempData.setManuscriptStatusId(manuscript.getManuscriptStatusId());
			 * reportTempData.setJournalId(manuscript.getJournal().getJournalId());
			 * reportTempData.setJournalCode(manuscript.getJournal().getJournalCode());
			 * reportTempData.setJournalTitle(manuscript.getJournal().getJournalTitle());
			 * reportTempData.setPublisherId(manuscript.getJournal().getPublisherId());
			 * reportTempData.setAuthorName( manuscript.getUser().getFirstName() +
			 * manuscript.getUser().getLastName());
			 * reportTempData.setManuscriptStatusCode(manuscript.getStatusMaster().
			 * getStatusValue()); report.add(reportTempData); } } } } else if
			 * (userId.getVersion() != null && userId.getVersion() == 4) {
			 * 
			 * List<ManuScript> manuscriptList =
			 * manuScriptRepo.getManuScripAuthorId(userId.getId());
			 * 
			 * if (manuscriptList != null && !manuscriptList.isEmpty()) { for (ManuScript
			 * manuscript : manuscriptList) { ReportVO reportTempData = new ReportVO();
			 * reportTempData.setAuthorId(manuscript.getAuthorId());
			 * reportTempData.setManuscriptCode(manuscript.getManuscriptCode());
			 * reportTempData.setManuscriptTitle(manuscript.getManuscriptTitle());
			 * reportTempData.setManuscriptVersion(manuscript.getManuscriptVersion());
			 * reportTempData.setSubmissionDate(manuscript.getSubmissionDate());
			 * reportTempData.setManuscriptStatusId(manuscript.getManuscriptStatusId());
			 * reportTempData.setJournalId(manuscript.getJournal().getJournalId());
			 * reportTempData.setJournalCode(manuscript.getJournal().getJournalCode());
			 * reportTempData.setJournalTitle(manuscript.getJournal().getJournalTitle());
			 * reportTempData.setPublisherId(manuscript.getJournal().getPublisherId());
			 * reportTempData .setAuthorName(manuscript.getUser().getFirstName() +
			 * manuscript.getUser().getLastName());
			 * reportTempData.setManuscriptStatusCode(manuscript.getStatusMaster().
			 * getStatusValue()); report.add(reportTempData); } } } else if
			 * (userId.getVersion() != null && userId.getVersion() == 5) {
			 * 
			 * List<ManuScript> manuscriptList = new ArrayList<>(); List<Integer>
			 * ManuscriptId = new ArrayList<>();
			 * 
			 * List<ManuScriptReviewer> manuscriptEditorData =
			 * manuScriptReviewerRepo.getReviewerDataById(userId.getId());
			 * 
			 * if (manuscriptEditorData != null && !manuscriptEditorData.isEmpty()) { for
			 * (ManuScriptReviewer manuscriptEditor : manuscriptEditorData) {
			 * ManuscriptId.add(manuscriptEditor.getManuscriptId()); }
			 * 
			 * if (ManuscriptId != null && !ManuscriptId.isEmpty()) { manuscriptList =
			 * manuScriptRepo.findAllById(ManuscriptId); }
			 * 
			 * if (manuscriptList != null && !manuscriptList.isEmpty()) { for (ManuScript
			 * manuscript : manuscriptList) { ReportVO reportTempData = new ReportVO();
			 * reportTempData.setAuthorId(manuscript.getAuthorId());
			 * reportTempData.setManuscriptCode(manuscript.getManuscriptCode());
			 * reportTempData.setManuscriptTitle(manuscript.getManuscriptTitle());
			 * reportTempData.setManuscriptVersion(manuscript.getManuscriptVersion());
			 * reportTempData.setSubmissionDate(manuscript.getSubmissionDate());
			 * reportTempData.setManuscriptStatusId(manuscript.getManuscriptStatusId());
			 * reportTempData.setJournalId(manuscript.getJournal().getJournalId());
			 * reportTempData.setJournalCode(manuscript.getJournal().getJournalCode());
			 * reportTempData.setJournalTitle(manuscript.getJournal().getJournalTitle());
			 * reportTempData.setPublisherId(manuscript.getJournal().getPublisherId());
			 * reportTempData.setAuthorName( manuscript.getUser().getFirstName() +
			 * manuscript.getUser().getLastName());
			 * reportTempData.setManuscriptStatusCode(manuscript.getStatusMaster().
			 * getStatusValue()); report.add(reportTempData); } } }
			 * 
			 * }
			 */
		return testList;

	}

	@Override
	public List<ReviewerDataWithCommentVO> getReviewerOverviewReport(IDVO userId) {

		List<ReviewerDataWithCommentVO> reviewerDataWithCommentVOData = new ArrayList<>();

		if (userId.getVersion() != null && userId.getVersion() == 1) {
			List<Integer> JournalId = new ArrayList<>();
			List<Journal> journalList = journalsRepo.getJournalByUserId(userId.getId());

			if (journalList != null && !journalList.isEmpty()) {
				for (Journal journal : journalList) {
					JournalId.add(journal.getJournalId());
				}

				List<ManuScriptReviewer> manuScriptReviewer = manuScriptReviewerRepo
						.getManuscriptReviewerListByJournal(JournalId);
				if (manuScriptReviewer != null && !manuScriptReviewer.isEmpty()) {
					for (ManuScriptReviewer mr : manuScriptReviewer) {
						ReviewerDataWithCommentVO reviewerDataWithCommentVO = new ReviewerDataWithCommentVO();
						reviewerDataWithCommentVO.setManuscriptId(mr.getManuscriptId());
						reviewerDataWithCommentVO.setManuscriptVersion(mr.getManuscriptVersion());
						reviewerDataWithCommentVO
								.setManuscriptStatusCode(mr.getManuscript().getStatusMaster().getStatusValue());
						reviewerDataWithCommentVO.setReviewerId(mr.getReviewerId());
						reviewerDataWithCommentVO.setReviewerName(mr.getManuscript().getUser().getFirstName() + " "
								+ mr.getManuscript().getUser().getLastName());
						reviewerDataWithCommentVO.setDecisionLetter(mr.getDecisionLetter());
						reviewerDataWithCommentVO.setCommentsToAuthor(mr.getCommentsToAuthor());
						reviewerDataWithCommentVO.setCommentsToEditor(mr.getCommentsToEditor());
						reviewerDataWithCommentVO.setJournalId(mr.getJournalId());
						reviewerDataWithCommentVOData.add(reviewerDataWithCommentVO);
					}

				}
			}

		} else if (userId.getVersion() != null && userId.getVersion() == 2) {
			List<Integer> JournalId = new ArrayList<>();

			List<UserJournal> UserJournals = userJournalRepo.getUserJournalByUserId(userId.getId());

			if (UserJournals != null && !UserJournals.isEmpty()) {
				for (UserJournal journal : UserJournals) {
					JournalId.add(journal.getJournalId());
				}
				List<ManuScriptReviewer> manuScriptReviewer = manuScriptReviewerRepo
						.getManuscriptReviewerListByJournal(JournalId);

				if (manuScriptReviewer != null && !manuScriptReviewer.isEmpty()) {
					for (ManuScriptReviewer mr : manuScriptReviewer) {
						ReviewerDataWithCommentVO reviewerDataWithCommentVO = new ReviewerDataWithCommentVO();
						reviewerDataWithCommentVO.setManuscriptId(mr.getManuscriptId());
						reviewerDataWithCommentVO.setManuscriptVersion(mr.getManuscriptVersion());
						reviewerDataWithCommentVO
								.setManuscriptStatusCode(mr.getManuscript().getStatusMaster().getStatusValue());
						reviewerDataWithCommentVO.setReviewerId(mr.getReviewerId());
						reviewerDataWithCommentVO.setReviewerName(mr.getManuscript().getUser().getFirstName() + " "
								+ mr.getManuscript().getUser().getLastName());
						reviewerDataWithCommentVO.setDecisionLetter(mr.getDecisionLetter());
						reviewerDataWithCommentVO.setCommentsToAuthor(mr.getCommentsToAuthor());
						reviewerDataWithCommentVO.setCommentsToEditor(mr.getCommentsToEditor());
						reviewerDataWithCommentVO.setJournalId(mr.getJournalId());
						reviewerDataWithCommentVOData.add(reviewerDataWithCommentVO);
					}

				}
			}
		}
		return reviewerDataWithCommentVOData;
	}

	@Override
	public List<SubmisionReportVO> getSubmisionReportForEditor(IDVO userId) {
		List<UserJournal> userJournalList = userJournalRepo.getUserJournalByUserId(userId.getId());
		List<SubmisionReportVO> list = new ArrayList<>();
		List<JournalDetailsVo> journalVoList = new ArrayList<>();
		List<Integer> manuscriptId = new ArrayList<>();
		if (userJournalList != null && !userJournalList.isEmpty()) {
			userJournalList.parallelStream().forEach(i -> {
				SubmisionReportVO submissionVoList = new SubmisionReportVO();
				submissionVoList.setJournalId(i.getJournalId());
				List<ManuscriptEditor> manuscriptEditoList = manuscriptEditorRepo.getManuscriptList(i.getJournalId(),
						userId.getId());
				if (manuscriptEditoList != null && !manuscriptEditoList.isEmpty()) {
					for (ManuscriptEditor manuscriptEditor : manuscriptEditoList) {
						manuscriptId.add(manuscriptEditor.getManuscriptId());
					}
					List<ManuScript> manuscriptList = manuScriptRepo.getManuscriptList(manuscriptId);
					Integer countNumberOfOriginalSubmission = 0;
					Integer countOfResubmisson = 0;
					if (manuscriptList != null && !manuscriptList.isEmpty()) {
						for (ManuScript j : manuscriptList) {
							if (j.getManuscriptVersion() == 0) {
								countNumberOfOriginalSubmission++;
							} else {
								countOfResubmisson++;
							}
						}
						JournalDetailsVo journalVo = new JournalDetailsVo();
						journalVo.setCountOfOriginalSubmission(countNumberOfOriginalSubmission);
						journalVo.setTotalResubmission(countOfResubmisson);
						journalVoList.add(journalVo);
						submissionVoList.setJournalDetails(journalVoList);
					}
				}
				list.add(submissionVoList);
			});

		}
		return list;
	}

	// Journal Based Reports
	/******************* Accepted Articles ****************************/

	@Override
	public List<AcceptedArticleVo> getAcceptedArticlesReports(IDVO iDVO) {
		if (iDVO.getJournalIdList() != null && !iDVO.getJournalIdList().isEmpty() && iDVO.getYearList() != null
				&& !iDVO.getYearList().isEmpty()) {
			List<ManuScript> listOfAcceptedArticles = manuScriptRepo.getAcceptedArticlesList(iDVO.getJournalIdList(),
					iDVO.getYearList());
			List<AcceptedArticleVo> addAcceptedArticleVoList = new ArrayList<>();
			if (listOfAcceptedArticles != null && !listOfAcceptedArticles.isEmpty()) {
				listOfAcceptedArticles.stream().forEach(i -> {
					AcceptedArticleVo addAcceptedArticleVo = new AcceptedArticleVo();
					addAcceptedArticleVo.setManuscriptId(i.getManuScriptId());
					addAcceptedArticleVo.setManuscriptCode(i.getManuscriptCode());
					addAcceptedArticleVo.setArticleTitle(i.getManuscriptTitle());
					if (i.getManuscriptTypeDetails() != null)
						addAcceptedArticleVo.setArticleType(i.getManuscriptTypeDetails().getManuscriptType());
					addAcceptedArticleVo.setLikelyDateOfPublishing(i.getPublishingDate());
					addAcceptedArticleVo.setAcceptedDate(i.getFinalDecisionDate());
					addAcceptedArticleVoList.add(addAcceptedArticleVo);
				});
			}
			return addAcceptedArticleVoList;
		}
		return null;
	}

	/******************* Rejected Articles ****************************/
	@Override
	public List<RejectedArticlesVo> getRejectedArticleReports(IDVO iDVO) {
		if (iDVO.getJournalIdList() != null && !iDVO.getJournalIdList().isEmpty() && iDVO.getYearList() != null
				&& !iDVO.getYearList().isEmpty()) {
			List<ManuScript> listOfRejectedArticles = manuScriptRepo.getRejectedArticleList(iDVO.getJournalIdList(),
					iDVO.getYearList());
			List<RejectedArticlesVo> addRejectedArticlesVoList = new ArrayList<>();
			if (listOfRejectedArticles != null && !listOfRejectedArticles.isEmpty()) {
				listOfRejectedArticles.stream().forEach(i -> {
					List<String> addRemarks = new ArrayList<>();
					RejectedArticlesVo addRejectedArticlesVo = new RejectedArticlesVo();
					addRejectedArticlesVo.setManuscriptId(i.getManuScriptId());
					addRejectedArticlesVo.setManuscriptCode(i.getManuscriptCode());
					addRejectedArticlesVo.setArticleTitle(i.getManuscriptTitle());
					if (i.getManuscriptTypeDetails() != null)
						addRejectedArticlesVo.setArticleType(i.getManuscriptTypeDetails().getManuscriptType());
					addRejectedArticlesVo.setAuthor(i.getUser().getUsername());
					addRejectedArticlesVo.setSubmissionDate(i.getSubmissionDate());
					addRejectedArticlesVo.setRejectionDate(i.getFinalDecisionDate());
					List<ManuscriptEditor> getRemarksData = manuscriptEditorRepo.getRemarks(i.getManuScriptId(),
							i.getJournalId());
					if (getRemarksData != null && !getRemarksData.isEmpty()) {
						getRemarksData.stream().forEach(j -> {
							if (j.getCommentsForAuthor() != null)
								addRemarks.add(j.getCommentsForAuthor());
						});
					}
					addRejectedArticlesVo.setRemarks(addRemarks);
					addRejectedArticlesVoList.add(addRejectedArticlesVo);
				});
			}
			return addRejectedArticlesVoList;
		}
		return null;
	}

	/****************** Performance For Year *************************/
	@Override
	public PerformanceYearListVo getPerformanceForYearReport(IDVO iDVO) {
		PerformanceYearListVo performanceYearListVo = new PerformanceYearListVo();
		List<PerformanceYearVo> performanceYearVo = new ArrayList<>();
		int totalSubmittedArticle = 0;
		int totalAcceptedArticle = 0;
		int totalRejectedArticle = 0;
		int totalUnderReviewArticle = 0;

		try {
			if (iDVO != null && iDVO.getJournalIdList() != null && !iDVO.getJournalIdList().isEmpty()
					&& iDVO.getYearList() != null && !iDVO.getYearList().isEmpty() && iDVO.getMonth() != null) {
				log.info("Fetching the Accept Reject report data...");
				List<Tuple> tupleList = manuscriptTypeDetailsRepo.getAcceptRejectReportData(
						iDVO.getJournalIdList().get(0), iDVO.getYearList().get(0), iDVO.getMonth());
				performanceYearVo = tupleList.stream().map(t -> new PerformanceYearVo(t.get(0, String.class),
						t.get(1, BigInteger.class), t.get(2, BigDecimal.class), t.get(3, BigDecimal.class)))
						.collect(Collectors.toList());

				for (PerformanceYearVo performanceYearObj : performanceYearVo) {
					if (performanceYearObj.getSubmitted() != null && performanceYearObj.getSubmitted() != 0) {
						performanceYearObj.setAccepted(String.valueOf(performanceYearObj.getAcceptedArticle()) + "["
								+ String.valueOf(Math.round(performanceYearObj.getAcceptedArticle() * 100.0
										/ performanceYearObj.getSubmitted()))
								+ "%]");
						performanceYearObj.setRejected(String.valueOf(performanceYearObj.getRejectedArticle()) + "["
								+ String.valueOf(Math.round(performanceYearObj.getRejectedArticle() * 100.0
										/ performanceYearObj.getSubmitted()))
								+ "%]");
						performanceYearObj.setUnderReview(String.valueOf(performanceYearObj.getUnderReviewArticle())
								+ "[" + String.valueOf(Math.round(performanceYearObj.getUnderReviewArticle() * 100.0
										/ performanceYearObj.getSubmitted()))
								+ "%]");
					} else {
						performanceYearObj.setAccepted(String.valueOf(performanceYearObj.getAcceptedArticle()) + "["
								+ String.valueOf(0) + "%]");
						performanceYearObj.setRejected(String.valueOf(performanceYearObj.getRejectedArticle()) + "["
								+ String.valueOf(0) + "%]");
						performanceYearObj.setUnderReview(String.valueOf(performanceYearObj.getUnderReviewArticle())
								+ "[" + String.valueOf(0) + "%]");

					}
					totalSubmittedArticle += performanceYearObj.getSubmitted();
					totalAcceptedArticle += performanceYearObj.getAcceptedArticle();
					totalRejectedArticle += performanceYearObj.getRejectedArticle();
					totalUnderReviewArticle += performanceYearObj.getUnderReviewArticle();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Failed to fetch the Accept Reject report data {}, {}", e.getMessage(), e);
		}

		performanceYearListVo.setPerformanceYearVo(performanceYearVo);
		performanceYearListVo.setTotalSubmittedArticle(totalSubmittedArticle);
		if (totalSubmittedArticle != 0) {
			performanceYearListVo.setTotalAcceptedArticle(String.valueOf(totalAcceptedArticle) + "["
					+ String.valueOf(Math.round(totalAcceptedArticle * 100.0 / totalSubmittedArticle)) + "%]");
			performanceYearListVo.setTotalRejectedArticle(String.valueOf(totalRejectedArticle) + "["
					+ String.valueOf(Math.round(totalRejectedArticle * 100.0 / totalSubmittedArticle)) + "%]");
			performanceYearListVo.setTotalUnderReviewArticle(String.valueOf(totalUnderReviewArticle) + "["
					+ String.valueOf(Math.round(totalUnderReviewArticle * 100.0 / totalSubmittedArticle)) + "%]");
		} else {
			performanceYearListVo
					.setTotalAcceptedArticle(String.valueOf(totalAcceptedArticle) + "[" + String.valueOf(0) + "%]");
			performanceYearListVo
					.setTotalRejectedArticle(String.valueOf(totalRejectedArticle) + "[" + String.valueOf(0) + "%]");
			performanceYearListVo.setTotalUnderReviewArticle(
					String.valueOf(totalUnderReviewArticle) + "[" + String.valueOf(0) + "%]");

		}
		return performanceYearListVo;
	}
	
	@Override
	public AuthorPerformanceVo getAuthorPerformanceReport(IDVO iDVO) {

	    AuthorPerformanceVo vo = new AuthorPerformanceVo();

	    if (iDVO.getJournalIdList() == null || iDVO.getJournalIdList().isEmpty()
	            || iDVO.getYearList() == null || iDVO.getYearList().isEmpty()) {
	        return vo;
	    }

	    try {
	        Integer totalAuthors = userJournalRepo.getListOfUserJournalByJournalId(
	                iDVO.getJournalIdList(), iDVO.getYearList(), 4);
	        vo.setTotalAuthorsRegistered(totalAuthors);
			Integer numberOfAuthorsubmitManuscriptMoreThanOne = manuScriptRepo
					.getPerAuthorManuscriptSubmitDetails(iDVO.getJournalIdList(), iDVO.getYearList());
			vo.setNumberofAuthorsSubmittedMoreThanOneManuscripts(numberOfAuthorsubmitManuscriptMoreThanOne);
	 
	    } catch (Exception e) {
	        log.error("Failed to fetch totalAuthorsRegistered", e);
	    }

	    try {
	        List<Object[]> rawStats = manuScriptRepo.getAuthorPerformanceSummary(
	                iDVO.getJournalIdList(), iDVO.getYearList());
	        log.info("Author performance stats raw result: {}", rawStats);

	        if (rawStats != null && !rawStats.isEmpty()) {
	            Object[] row = rawStats.get(0);

	            if (row.length >= 5) {
	                Integer submittedAuthors = row[0] != null ? ((Number) row[0]).intValue() : 0;
//	                Integer moreThanOneSubmitCount = row[1] != null ? ((Number) row[1]).intValue() : 0;
	                Integer fromAbroadCount = row[1] != null ? ((Number) row[1]).intValue() : 0;
	                Integer originalFromAbroadCount  = row[2] != null ? ((Number) row[2]).intValue() : 0;
	                String fromAbroadPercentage  = row[3] != null ? row[3].toString() : "0";
	                String originalFromAbroadPercentage = row[4] != null ? row[4].toString() : "0";

	                vo.setNumberofAuthorsSubmittedManuscripts(submittedAuthors);
//	                vo.setNumberofAuthorsSubmittedMoreThanOneManuscripts(moreThanOneSubmitCount);
	                vo.setNumberOfOriginalArticlesFromAbroad(originalFromAbroadCount);
	                vo.setNumberOfManuscriptsFromAbroad(fromAbroadCount);
	                vo.setNumberOfMOrignalFromAbroadPercentage(originalFromAbroadPercentage);
	                vo.setNumberOfMFromAbroadPercentage(fromAbroadPercentage);
	            } else {
	                log.warn("Author performance summary row does not contain expected number of columns.");
	            }
	        }

	        else {
	            log.warn("Author performance summary returned empty or incomplete stats.");
	        }

	    } catch (Exception e) {
	        log.error("Failed to load author performance summary", e);
	    }

	    try {
	        List<PerCountryManuscriptVo> listOfCountry = manuScriptRepo
	                .getManuscriptListPerCountryAll(iDVO.getJournalIdList(), iDVO.getYearList());

	        List<AuthorCountryVo> addAuthorCountryVoList = listOfCountry.stream().map(i -> {
	            AuthorCountryVo addAuthorCountryVo = new AuthorCountryVo();
	            addAuthorCountryVo.setCountryName(i.getCountry());
	            addAuthorCountryVo.setManuscriptsSubmitted(i.getManuscriptCount().intValue());
	            return addAuthorCountryVo;
	        }).collect(Collectors.toList());

	        vo.setShow(addAuthorCountryVoList);

	    } catch (Exception e) {
	        log.error("Failed to load manuscripts per country", e);
	    }

	    try {
	        List<Object[]> rawAuthors = manuScriptRepo.getAuthorsWithMoreThanOneSubmission(
	                iDVO.getJournalIdList(), iDVO.getYearList());

	        List<IndividualAutorDetailsVO> authorDetails = new ArrayList<>();
	        for (Object[] row : rawAuthors) {
	            Integer userId = toInt(row[0]);
	            String firstName = toStr(row[1]);
	            String lastName = toStr(row[2]);
	            int count = toInt(row[3]);

	            IndividualAutorDetailsVO detail = new IndividualAutorDetailsVO();
	            detail.setAuthorName(firstName + " " + lastName);
	            detail.setSubmittedManuScripts(count);
	            authorDetails.add(detail);
	        }

	        vo.setAuthorsSubmittedMoreThanOneManuscripts(authorDetails);

	    } catch (Exception e) {
	        log.error("Failed to load authors with multiple manuscripts", e);
	    }

	    return vo;
	}

	private int toInt(Object obj) {
	    if (obj == null) return 0;
	    if (obj instanceof Number) return ((Number) obj).intValue();
	    try {
	        return Integer.parseInt(obj.toString());
	    } catch (Exception e) {
	        return 0;
	    }
	}

	private String toStr(Object obj) {
	    return (obj != null) ? obj.toString() : "";
	}



	/****************** Monthly Statistics ************************/
	@Override
	public MonthlyStatisticsVO getMonthlyStatisticsReport(IDVO iDVO) {
		MonthlyStatisticsVO addMonthlyStatisticsVO = new MonthlyStatisticsVO();

		int numberOfAuthorsRegistered = 0;
		int numberOfReviewersAdded = 0;
		int numberOfArticlesReviewed = 0;

		if (iDVO.getJournalIdList() != null && !iDVO.getJournalIdList().isEmpty() && iDVO.getYearList() != null
				&& !iDVO.getYearList().isEmpty() && iDVO.getMonth() != null) {
			// NumberOfArticleSubmitted
			List<ManuScript> manuscriptList = null;

			if (iDVO.getMonth() == 0) {
				manuscriptList = manuScriptRepo.getTotalNumberOFManuscriptSubmittedInYear(iDVO.getJournalIdList(),
						iDVO.getYearList());
			} else {
				manuscriptList = manuScriptRepo.getTotalNumberOFManuscriptSubmitted(iDVO.getJournalIdList(),
						iDVO.getYearList(), iDVO.getMonth());
			}

			List<IDVO> addIDVOList = new ArrayList<>();
			if (!manuscriptList.isEmpty() && manuscriptList != null) {
				for (ManuScript i : manuscriptList) {
					IDVO addIDVO = new IDVO();
					addIDVO.setManuscriptCode(i.getManuscriptCode());
					addIDVOList.add(addIDVO);
				}
				addMonthlyStatisticsVO.setNoOfArticlesSubmitted(manuscriptList.size());
				addMonthlyStatisticsVO.setShow(addIDVOList);
			} else {
				addMonthlyStatisticsVO.setNoOfArticlesSubmitted(0);
				addMonthlyStatisticsVO.setShow(addIDVOList);
			}

			// NumberOfAuthorRegistered
			List<User> authorList = null;
			if (iDVO.getMonth() == 0) {
				authorList = userRepo.getTotalAuthorRegisteredInYear(iDVO.getJournalIdList(), iDVO.getYearList());
			} else {
				authorList = userRepo.getTotalAuthorRegistered(iDVO.getJournalIdList(), iDVO.getYearList(),
						iDVO.getMonth());
			}

			if (!authorList.isEmpty() && authorList != null) {
				numberOfAuthorsRegistered = authorList.size();
			}
			addMonthlyStatisticsVO.setNoOfAuthorRegistered(numberOfAuthorsRegistered);

			// NumberOfReviewersAdded

			List<User> reviewerList = null;
			if (iDVO.getMonth() == 0) {
				reviewerList = userRepo.getTotalReviewerAddedInYear(iDVO.getJournalIdList(), iDVO.getYearList());
			} else {
				reviewerList = userRepo.getTotalReviewerAdded(iDVO.getJournalIdList(), iDVO.getYearList(),
						iDVO.getMonth());
			}

			if (!reviewerList.isEmpty() && reviewerList != null) {
				numberOfReviewersAdded = reviewerList.size();
			}
			addMonthlyStatisticsVO.setNoOfReviewersAdded(numberOfReviewersAdded);

			// NumberOfArticlesReviewed

			if (iDVO.getMonth() == 0) {
				numberOfArticlesReviewed = manuScriptRepo
						.getTotalNumberOFArticleReviewedInYear(iDVO.getJournalIdList(), iDVO.getYearList()).intValue();
			} else {
				numberOfArticlesReviewed = manuScriptRepo
						.getTotalNumberOFArticleReviewed(iDVO.getJournalIdList(), iDVO.getYearList(), iDVO.getMonth())
						.intValue();
			}
			addMonthlyStatisticsVO.setNoOfArticlesReviewed(numberOfArticlesReviewed);
		}
		return addMonthlyStatisticsVO;
	}

	/***************** Reviewers Performance **************************/
//	@Override
//	public ReviewersPerformanceVo getReviewersPerformanceReport(IDVO iDVO) {
//
//		ReviewersPerformanceVo addReviewersPerformanceVo = new ReviewersPerformanceVo();
//    try {   
//		List<Tuple> totalActiveReviewer = userRepo.getTotalActiveReviewer(iDVO.getJournalIdList().get(0));
//		List<UserReportVO> activeReviewerList = totalActiveReviewer.stream()
//				.map(t -> new UserReportVO(t.get(0, Integer.class), t.get(1, String.class), t.get(2, String.class),
//						t.get(3, String.class), t.get(4, String.class) != null ? t.get(4, String.class) :"Prof" , t.get(5, String.class),
//						t.get(6, String.class), t.get(7, String.class), t.get(8, String.class), t.get(9, String.class),
//						t.get(10, String.class), t.get(11, String.class), t.get(12, String.class),
//						t.get(13, String.class), t.get(14, String.class), t.get(15, String.class),
//						t.get(16, String.class)))
//				.collect(Collectors.toList());
//
//		Map<Integer, List<UserReportVO>> userIdReviewerList = activeReviewerList.stream()
//				.collect(Collectors.groupingBy(u -> u.getId()));
//		addReviewersPerformanceVo.setTotalReferees(activeReviewerList.size());
//
//		List<Integer> reviewersOnTime = new ArrayList<>();
//		List<Integer> returnedLate = new ArrayList<>();
//		List<Integer> neverReturned = new ArrayList<>();
//		List<Integer> requiredReminder = new ArrayList<>();
//
//		List<Tuple> listManuScriptReviewer = new ArrayList<>();
//
//		if (iDVO.getJournalIdList() != null && !iDVO.getJournalIdList().isEmpty() && iDVO.getYearList() != null
//				&& !iDVO.getYearList().isEmpty() && iDVO.getMonth() != null && iDVO.getMonth() != 0) {
//
//			listManuScriptReviewer = manuScriptReviewerRepo.getTotalReviewerByMonthReviewer(iDVO.getJournalIdList(), iDVO.getYearList(),
//					iDVO.getMonth());
//		} else {
//
//			listManuScriptReviewer = manuScriptReviewerRepo.getTotalReviewerByYearReviewer(iDVO.getJournalIdList(), iDVO.getYearList());
//		}
//
//		List<ReviewerReportDataVO> reviewerReportData = listManuScriptReviewer.stream()
//				.map(t -> new ReviewerReportDataVO(t.get(0, Integer.class), t.get(1, Integer.class),
//						t.get(2, Integer.class), t.get(3, Integer.class), Integer.valueOf(t.get(4, Short.class)),
//						t.get(5, Date.class), t.get(6, Date.class), t.get(7, Date.class), t.get(8, String.class)))
//				.collect(Collectors.toList());
//
//		if (reviewerReportData != null && !reviewerReportData.isEmpty()) {
//
//			Map<Integer, List<ReviewerReportDataVO>> reviewerGroup = reviewerReportData.stream()
//					.collect(Collectors.groupingBy(w -> w.getManuscriptId()));
//
//			if (reviewerGroup != null && reviewerGroup.size() > 0) {
//				addReviewersPerformanceVo
//						.setNumberOfReviewersUsedArticle(Math.round(reviewerReportData.size() / reviewerGroup.size()));
//			}
//
//			List<ReviewerReportDataVO> getReviewerRespondedData = reviewerReportData.stream()
//					.filter(s -> (s.getManuscriptStatus().equals(12) || s.getManuscriptStatus().equals(13)
//							|| s.getManuscriptStatus().equals(14) || s.getManuscriptStatus().equals(15)))
//					.collect(Collectors.toList());
//
//			Map<Integer, List<ReviewerReportDataVO>> respondedReviewerGroup = getReviewerRespondedData.stream()
//					.collect(Collectors.groupingBy(w -> w.getManuscriptId()));
//
//			if (respondedReviewerGroup != null && respondedReviewerGroup.size() > 0) {
//				addReviewersPerformanceVo.setNumberOfReviewersResponded(
//						Math.round(getReviewerRespondedData.size() / respondedReviewerGroup.size()));
//			}
//
//			for (ReviewerReportDataVO reviewerReportDataVO : reviewerReportData) {
//
//				if (reviewerReportDataVO != null && reviewerReportDataVO.getDueDate() != null
//						&& reviewerReportDataVO.getCompleteDate() != null
//						&& reviewerReportDataVO.getDueDate().after(reviewerReportDataVO.getCompleteDate()) ) {
//					if (!reviewersOnTime.contains(reviewerReportDataVO.getReviewerId())) {
//						reviewersOnTime.add(reviewerReportDataVO.getReviewerId());
//					}
//				} else if(reviewerReportDataVO.getDueDate() != null && reviewerReportDataVO.getCompleteDate() == null && reviewerReportDataVO.getDueDate().after(new Date())){
//					if (!reviewersOnTime.contains(reviewerReportDataVO.getReviewerId())) {
//						reviewersOnTime.add(reviewerReportDataVO.getReviewerId());
//					}
//				} else if (reviewerReportDataVO != null && reviewerReportDataVO.getDueDate() != null
//						&& reviewerReportDataVO.getCompleteDate() != null
//						&& reviewerReportDataVO.getDueDate().before(reviewerReportDataVO.getCompleteDate())) {
//					if (!returnedLate.contains(reviewerReportDataVO.getReviewerId())) {
//						returnedLate.add(reviewerReportDataVO.getReviewerId());
//					}
//				}
//
//				if (reviewerReportDataVO != null
//						&& (reviewerReportDataVO.getManuscriptStatus().equals(8)
//								|| reviewerReportDataVO.getManuscriptStatus().equals(66)
//								|| reviewerReportDataVO.getManuscriptStatus().equals(35))
//						&& reviewerReportDataVO.getDueDate() != null
//						&& reviewerReportDataVO.getDueDate().before(new Date())) {
//
//					if (!returnedLate.contains(reviewerReportDataVO.getReviewerId())) {
//						requiredReminder.add(reviewerReportDataVO.getReviewerId());
//					}
//				}
//
//				if (reviewerReportDataVO != null && reviewerReportDataVO.getManuscriptStatus().equals(35)
//						&& reviewerReportDataVO.getDueDate() != null
//						&& reviewerReportDataVO.getDueDate().before(new Date())) {
//
//					if (!neverReturned.contains(reviewerReportDataVO.getReviewerId())) {
//						neverReturned.add(reviewerReportDataVO.getReviewerId());
//					}
//				}
//			}
//
//			addReviewersPerformanceVo.setReviewersOnTime(reviewersOnTime.size());
//			addReviewersPerformanceVo.setReturnedLate(returnedLate.size());
//			addReviewersPerformanceVo.setNeverReturned(neverReturned.size());
//			addReviewersPerformanceVo.setRequiredReminders(requiredReminder.size());
//
//			Map<String, List<UserReportVO>> courtywiseReviewer = activeReviewerList.stream()
//					.collect(Collectors.groupingBy(w -> w.getCountry()));
//
//			List<AuthorCountryVo> addAuthorCountryVoList = new ArrayList<>();
//			courtywiseReviewer.forEach((k, v) -> {
//				AuthorCountryVo addAuthorCountryVo = new AuthorCountryVo();
//				addAuthorCountryVo.setCountryName(k);
//				addAuthorCountryVo.setManuscriptsSubmitted(v.size());
//				addAuthorCountryVoList.add(addAuthorCountryVo);
//			});
//
//			addReviewersPerformanceVo.setShow(addAuthorCountryVoList);
//
//			List<UserReportVO> userVoList = new ArrayList<>();
//
//			List<UserReportVO> userVoList1 = new ArrayList<>();
//			Map<Integer, List<ReviewerReportDataVO>> respondedManuscriptGroup = getReviewerRespondedData.stream()
//					.collect(Collectors.groupingBy(w -> w.getReviewerId()));
//
//			respondedManuscriptGroup.forEach((k,v) -> userVoList.add(userIdReviewerList.get(k).get(0)));
//
//			List<GetRefreeDetailsVO> getRefreeDetailsVODetails = new ArrayList<>();
//			
//			Map<Integer, List<ReviewerReportDataVO>> manuscriptGroup = reviewerReportData.stream()
//					.collect(Collectors.groupingBy(w -> w.getReviewerId()));
//			List<Integer> reviewerList = new ArrayList<Integer>(manuscriptGroup.keySet());
//			
//		
//			List<ReviewerRating> reviewerRatingList = reviewerRatingRepo.getRatingByReviewer(reviewerList);
//
//			Map<Integer, List<ReviewerRating>> ratingGroup = reviewerRatingList.stream()
//					.collect(Collectors.groupingBy(w -> w.getReviewerId()));
//
//			for (Map.Entry<Integer, List<ReviewerReportDataVO>> entry : manuscriptGroup.entrySet()) {
//				try {
//					if(userIdReviewerList.get(entry.getKey()) != null) {
//					userVoList1.add(userIdReviewerList.get(entry.getKey()).get(0));
//					GetRefreeDetailsVO getRefreeDetailsVO = new GetRefreeDetailsVO();
//					getRefreeDetailsVO.setRefreename(entry.getValue().get(0).getReviewerName());
//					getRefreeDetailsVO.setArticleReviewed(entry.getValue().size());
//
//					Integer days = 0;
//					List<Integer> reviewerOnTimeIndv = new ArrayList<>();
//					List<Integer> returnedLateIndv = new ArrayList<>();
//					List<Integer> neverReturnedIndv = new ArrayList<>();
//					List<Integer> requiredReminderIndv = new ArrayList<>();
//
//					for (ReviewerReportDataVO manuscriptRev : entry.getValue()) {
//						long differenceInTime = 0;
//						if(manuscriptRev.getCompleteDate() != null) {
//						differenceInTime = manuscriptRev.getCompleteDate().getTime()
//								- manuscriptRev.getAssignedDate().getTime();
//					    }
//						long differenceInDays = (differenceInTime / (1000 * 60 * 60 * 24)) % 365;
//						days = days + (int) differenceInDays;
//
//						if (manuscriptRev != null && manuscriptRev.getDueDate() != null
//								&& manuscriptRev.getCompleteDate() != null
//								&& manuscriptRev.getDueDate().after(manuscriptRev.getCompleteDate())) {
//							reviewerOnTimeIndv.add(manuscriptRev.getManuscriptId());
//
//						} else if(manuscriptRev.getDueDate() != null && manuscriptRev.getCompleteDate() == null && manuscriptRev.getDueDate().after(new Date())){
//								reviewerOnTimeIndv.add(manuscriptRev.getManuscriptId());
//							}else if (manuscriptRev != null && manuscriptRev.getDueDate() != null
//								&& manuscriptRev.getCompleteDate() != null
//								&& manuscriptRev.getDueDate().before(manuscriptRev.getCompleteDate())) {
//							returnedLateIndv.add(manuscriptRev.getManuscriptId());
//
//						}
//
//						if (manuscriptRev != null
//								&& (manuscriptRev.getManuscriptStatus().equals(8)
//										|| manuscriptRev.getManuscriptStatus().equals(66)
//										|| manuscriptRev.getManuscriptStatus().equals(35))
//								&& manuscriptRev.getDueDate() != null && manuscriptRev.getDueDate().before(new Date())) {
//
//							requiredReminderIndv.add(manuscriptRev.getManuscriptId());
//						}
//
//						if (manuscriptRev != null && manuscriptRev.getManuscriptStatus().equals(35)
//								&& manuscriptRev.getDueDate() != null && manuscriptRev.getDueDate().before(new Date())) {
//
//							neverReturnedIndv.add(manuscriptRev.getManuscriptId());
//						}
//
//					}
//
//					getRefreeDetailsVO.setTimeTaken(days);
//					getRefreeDetailsVO.setAvgReviewerOnTime(
//							String.format("%.1f", reviewerOnTimeIndv.size() * 100.0 / entry.getValue().size())
//									.replace(".0", ""));
//					getRefreeDetailsVO.setAvgReturnLate(
//							String.format("%.1f", returnedLateIndv.size() * 100.0 / entry.getValue().size())
//									.replace(".0", ""));
//					getRefreeDetailsVO.setAvgRequiredReminder(
//							String.format("%.1f", requiredReminderIndv.size() * 100.0 / entry.getValue().size())
//									.replace(".0", ""));
//					getRefreeDetailsVO.setAvgNeverReturnLate(
//							String.format("%.1f", neverReturnedIndv.size() * 100.0 / entry.getValue().size())
//									.replace(".0", ""));
//
//					List<ReviewerRating> ReviewerRatingList = ratingGroup.get(entry.getKey());
//
//					if(ReviewerRatingList != null) {
//					IntSummaryStatistics rating = ReviewerRatingList.stream()
//							.mapToInt((ReviewerRating) -> ReviewerRating.getQuality()).summaryStatistics();
//
//					IntSummaryStatistics ratingTime = ReviewerRatingList.stream()
//							.mapToInt((ReviewerRating) -> ReviewerRating.getTimeliness()).summaryStatistics();
//					getRefreeDetailsVO.setAvgRating(String.format("%.1f", rating.getAverage()));
//					getRefreeDetailsVO.setAvgRatingTime(String.format("%.1f", ratingTime.getAverage()));
//					}
//					
//					getRefreeDetailsVODetails.add(getRefreeDetailsVO);
//					
//					reviewerOnTimeIndv.clear();
//					returnedLateIndv.clear();
//					neverReturnedIndv.clear();
//					requiredReminderIndv.clear();
//				}
//
//				} catch (Exception e) {
//					log.info("Exception occured during preparing individual report for reviewer " + e.getMessage());
//				}
//				
//			}
//
//			addReviewersPerformanceVo.setUser(userVoList);
//			addReviewersPerformanceVo.setReviewerNo(userVoList1);
//			addReviewersPerformanceVo.setGetIndividualRefereePerformances(getRefreeDetailsVODetails);
//		}
//		
//    } catch(Exception ex) {
//    	
//    	ex.printStackTrace();
//    }
//
//		return addReviewersPerformanceVo;
//
//	}
	
	public ReviewersPerformanceVo getReviewersPerformanceReport(IDVO iDVO) {

	    ReviewersPerformanceVo reviewersPerformanceVo = new ReviewersPerformanceVo();
	    try {
	        List<Tuple> totalActiveReviewer = userRepo.getTotalActiveReviewer(iDVO.getJournalIdList().get(0));
	        reviewersPerformanceVo.setTotalReferees(totalActiveReviewer.size());

			if (iDVO.getJournalIdList() != null && !iDVO.getJournalIdList().isEmpty() && iDVO.getYearList() != null
					&& !iDVO.getYearList().isEmpty() && iDVO.getMonth() != null && iDVO.getMonth() != 0) {
				
				Integer noOfReviewersUsedArticle =manuScriptReviewerRepo.numberOfReviewersInvitedByMonth(iDVO.getJournalIdList(), iDVO.getYearList(),
						iDVO.getMonth());
				Integer neverReturned =manuScriptReviewerRepo.numberOfReviewsNeverReturnedByMonth(iDVO.getJournalIdList(), iDVO.getYearList(),
						iDVO.getMonth());
				Integer declined = manuScriptReviewerRepo.numberOfReviewsDeclinedByMonth(iDVO.getJournalIdList(), iDVO.getYearList(),
						iDVO.getMonth());
				Integer removed =manuScriptReviewerRepo.numberOfReviewsRemovedByMonth(iDVO.getJournalIdList(), iDVO.getYearList(),
						iDVO.getMonth());
				
		        //Total number of reviewers invited for review/re-review by month
		        reviewersPerformanceVo.setNumberOfReviewersUsedArticle(noOfReviewersUsedArticle);
		        
		         int reviewsOnTime = 0;
		         int reviewsLate = 0;
		         int totalArticlesReviewed = 0;

		         // Fetch review data
		         List<Object[]> reviewDatas = manuScriptReviewerRepo.getReviewerPerformanceMonthTotalReviewed(iDVO.getJournalIdList(), iDVO.getYearList(),iDVO.getMonth());
		         DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
		         // Process review data to determine counts
		         for (Object[] reviewData : reviewDatas) {
		             // Assuming reviewData[0] is created_at and reviewData[1] is due_date
		        	 LocalDateTime createdAtInvite = reviewData[0] != null ? LocalDateTime.parse(reviewData[0].toString(), dateTimeFormatter) : null;
		             LocalDateTime createdAtComplete = reviewData[1] != null ? LocalDateTime.parse(reviewData[1].toString(), dateTimeFormatter) : null;
		             LocalDateTime dueDate = reviewData[2] != null ? LocalDateTime.parse(reviewData[2].toString(), dateTimeFormatter) : null;

		             if (createdAtComplete != null && dueDate != null && !createdAtInvite.isAfter(createdAtComplete)) {
		                 
		                 if (createdAtComplete.toLocalDate().isBefore(dueDate.toLocalDate()) || createdAtComplete.toLocalDate().isEqual(dueDate.toLocalDate())) {
		                     reviewsOnTime++;
		                 } else {
		                     reviewsLate++;
		                 }
		                 totalArticlesReviewed++;
		             }
		         }
		         System.out.println("reviewsOnTime: "+reviewsOnTime);
		         System.out.println("reviewsLate: "+reviewsLate);
		         System.out.println("totalArticlesReviewed: "+totalArticlesReviewed);
		        //Total number of reviewers who completed review/re-review by month		       
		        reviewersPerformanceVo.setNumberOfReviewersResponded(totalArticlesReviewed);		         
		        //Total number of reviewers who completed review/re-review on/before due date by month
		        reviewersPerformanceVo.setReviewersOnTime(reviewsOnTime);
		        //Total number of Reviewers who completed review/re-review after due date by month
		        reviewersPerformanceVo.setReturnedLate(reviewsLate);
		        
		        //Total number of reviewers who were invited to review but didn’t respond by month
		        reviewersPerformanceVo.setNeverReturned(neverReturned);
		        // Total number of reviewers who declined to review by month
		        reviewersPerformanceVo.setReviewersDeclined(declined);
		        
		        List<Object[]> declinedAndRemovedData = manuScriptReviewerRepo.getDeclinedAndRemovedData(iDVO.getJournalIdList(), iDVO.getYearList(), iDVO.getMonth());
		        int validRemovedDeclineCount = 0;
	            for (Object[] data : declinedAndRemovedData) {
	                LocalDateTime declinedDate = data[0] != null ? LocalDateTime.parse(data[1].toString(), dateTimeFormatter) : null;
	                LocalDateTime removedDate = data[1] != null ? LocalDateTime.parse(data[2].toString(), dateTimeFormatter) : null;
	                if (declinedDate != null && removedDate != null && declinedDate.isBefore(removedDate)) {
	                	validRemovedDeclineCount++;
	                }
	            }
		        removed = removed-validRemovedDeclineCount;
		        reviewersPerformanceVo.setReviewersRemoved(removed);
		        
		        reviewersPerformanceVo.setReviewersRemoved(removed);
		        
		        Integer terminated =noOfReviewersUsedArticle-totalArticlesReviewed-neverReturned-declined-removed;
		        System.out.println("terminated: "+terminated);
//		        reviewersPerformanceVo.setReviewersTerminated(manuScriptReviewerRepo.numberOfReviewsTerminatedByMonth(iDVO.getJournalIdList(), iDVO.getYearList(),
//						iDVO.getMonth()));
		        reviewersPerformanceVo.setReviewersTerminated(terminated);
		        //Total number of reminders sent to the reviewer invited by month
		        reviewersPerformanceVo.setRequiredReminders(manuScriptReviewerRepo.numberOfRemindersSentByMonth(iDVO.getJournalIdList(), iDVO.getYearList(),
						iDVO.getMonth()));
		       
			} else {
				Integer noOfReviewersUsedArticle =manuScriptReviewerRepo.numberOfReviewersInvitedByYear(iDVO.getJournalIdList(), iDVO.getYearList());
				Integer neverReturned =manuScriptReviewerRepo.numberOfReviewsNeverReturnedByYear(iDVO.getJournalIdList(), iDVO.getYearList());
				Integer declined = manuScriptReviewerRepo.numberOfReviewsDeclinedByYear(iDVO.getJournalIdList(), iDVO.getYearList());
				Integer removed =manuScriptReviewerRepo.numberOfReviewsRemovedByYear(iDVO.getJournalIdList(), iDVO.getYearList());
		        //Total number of reviewers invited for review/re-review by year
		        reviewersPerformanceVo.setNumberOfReviewersUsedArticle(noOfReviewersUsedArticle);
  
		         int reviewsOnTime = 0;
		         int reviewsLate = 0;
		         int totalArticlesReviewed = 0;

		         // Fetch review data
		         List<Object[]> reviewDatas = manuScriptReviewerRepo.getReviewerPerformanceYearTotalReviewed(iDVO.getJournalIdList(), iDVO.getYearList());
		         DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
		         // Process review data to determine counts
		         for (Object[] reviewData : reviewDatas) {
		             // Assuming reviewData[0] is created_at and reviewData[1] is due_date
		        	 LocalDateTime createdAtInvite = reviewData[0] != null ? LocalDateTime.parse(reviewData[0].toString(), dateTimeFormatter) : null;
		             LocalDateTime createdAtComplete = reviewData[1] != null ? LocalDateTime.parse(reviewData[1].toString(), dateTimeFormatter) : null;
		             LocalDateTime dueDate = reviewData[2] != null ? LocalDateTime.parse(reviewData[2].toString(), dateTimeFormatter) : null;

		             if (createdAtComplete != null && dueDate != null && !createdAtInvite.isAfter(createdAtComplete)) {
		                 
		                 if (createdAtComplete.toLocalDate().isBefore(dueDate.toLocalDate()) || createdAtComplete.toLocalDate().isEqual(dueDate.toLocalDate())) {
		                     reviewsOnTime++;
		                 } else {
		                     reviewsLate++;
		                 }
		                 totalArticlesReviewed++;
		             }
		         }
		         System.out.println("reviewsOnTime: "+reviewsOnTime);
		         System.out.println("reviewsLate: "+reviewsLate);
		         System.out.println("totalArticlesReviewed: "+totalArticlesReviewed);
		        //Total number of reviewers who completed review/re-review by month		       
		        reviewersPerformanceVo.setNumberOfReviewersResponded(totalArticlesReviewed);		         
		        //Total number of reviewers who completed review/re-review on/before due date by month
		        reviewersPerformanceVo.setReviewersOnTime(reviewsOnTime);
		        //Total number of Reviewers who completed review/re-review after due date by month
		        reviewersPerformanceVo.setReturnedLate(reviewsLate);
		        
		        //Total number of reviewers who were invited to review but didn’t respond by year
		        reviewersPerformanceVo.setNeverReturned(neverReturned);
		       
		        reviewersPerformanceVo.setReviewersDeclined(declined);
		        
		        List<Object[]> declinedAndRemovedData = manuScriptReviewerRepo.getDeclinedAndRemovedData(iDVO.getJournalIdList(), iDVO.getYearList(), iDVO.getMonth());
		        int validRemovedDeclineCount = 0;
	            for (Object[] data : declinedAndRemovedData) {
	                LocalDateTime declinedDate = data[0] != null ? LocalDateTime.parse(data[1].toString(), dateTimeFormatter) : null;
	                LocalDateTime removedDate = data[1] != null ? LocalDateTime.parse(data[2].toString(), dateTimeFormatter) : null;
	                if (declinedDate != null && removedDate != null && declinedDate.isBefore(removedDate)) {
	                	validRemovedDeclineCount++;
	                }
	            }
		        removed = removed-validRemovedDeclineCount;
		        reviewersPerformanceVo.setReviewersRemoved(removed);
		        Integer terminated =noOfReviewersUsedArticle-totalArticlesReviewed-neverReturned-declined-removed;
		        System.out.println("terminated: "+terminated);
//		        reviewersPerformanceVo.setReviewersTerminated(manuScriptReviewerRepo.numberOfReviewsTerminatedByYear(iDVO.getJournalIdList(), iDVO.getYearList()));
		        reviewersPerformanceVo.setReviewersTerminated(terminated);
		        //Total number of reminders sent to the reviewer invited bY year
		        reviewersPerformanceVo.setRequiredReminders(manuScriptReviewerRepo.numberOfRemindersSentByYear(iDVO.getJournalIdList(), iDVO.getYearList()));
			}
	        
			List<UserReportVO> activeReviewerList = totalActiveReviewer.stream()
					.map(t -> new UserReportVO(t.get(0, Integer.class), t.get(1, String.class), t.get(2, String.class),
							t.get(3, String.class), t.get(4, String.class) != null ? t.get(4, String.class) :"Prof" , t.get(5, String.class),
							t.get(6, String.class), t.get(7, String.class), t.get(8, String.class), t.get(9, String.class),
							t.get(10, String.class), t.get(11, String.class), t.get(12, String.class),
							t.get(13, String.class), t.get(14, String.class), t.get(15, String.class),
							t.get(16, String.class)))
					.collect(Collectors.toList());
			
//			Map<String, List<UserReportVO>> courtywiseReviewer = activeReviewerList.stream()
//					.collect(Collectors.groupingBy(w -> w.getCountry()));
			Map<String, List<UserReportVO>> courtywiseReviewer = activeReviewerList.stream()
				    .collect(Collectors.groupingBy(w -> w.getCountry() != null ? w.getCountry() : "Unknown"));

			List<AuthorCountryVo> addAuthorCountryVoList = new ArrayList<>();
			courtywiseReviewer.forEach((k, v) -> {
				AuthorCountryVo addAuthorCountryVo = new AuthorCountryVo();
				addAuthorCountryVo.setCountryName(k);
				addAuthorCountryVo.setManuscriptsSubmitted(v.size());
				addAuthorCountryVoList.add(addAuthorCountryVo);
			});

			reviewersPerformanceVo.setShow(addAuthorCountryVoList);
			
////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			List<UserReportVO> userVoList = new ArrayList<>();
			List<Tuple> listManuScriptReviewer = new ArrayList<>();
			List<Tuple> listManuScriptReviewerResponded = new ArrayList<>();

			if (iDVO.getJournalIdList() != null && !iDVO.getJournalIdList().isEmpty() && iDVO.getYearList() != null
					&& !iDVO.getYearList().isEmpty() && iDVO.getMonth() != null && iDVO.getMonth() != 0) {

				listManuScriptReviewerResponded = manuScriptReviewerRepo.getTotalReviewerByMonthReviewerResponded(iDVO.getJournalIdList(), iDVO.getYearList(),
						iDVO.getMonth());
				listManuScriptReviewer = manuScriptReviewerRepo.getTotalReviewerByMonthReviewer(iDVO.getJournalIdList(), iDVO.getYearList(),
						iDVO.getMonth());
			} else {

				listManuScriptReviewerResponded = manuScriptReviewerRepo.getTotalReviewerByYearReviewerResponded(iDVO.getJournalIdList(), iDVO.getYearList());
				listManuScriptReviewer = manuScriptReviewerRepo.getTotalReviewerByYearReviewer(iDVO.getJournalIdList(), iDVO.getYearList());
			}

			List<ReviewerReportDataVO> reviewerReportData = listManuScriptReviewer.stream()
					.map(t -> new ReviewerReportDataVO(t.get(0, Integer.class), t.get(1, Integer.class),
							t.get(2, Integer.class), t.get(3, Integer.class), Integer.valueOf(t.get(4, Short.class)),
							t.get(5, Date.class), t.get(6, Date.class), t.get(7, Date.class), t.get(8, String.class)))
					.collect(Collectors.toList());
			List<ReviewerReportDataVO> reviewerReportDataResponded = listManuScriptReviewerResponded.stream()
					.map(t -> new ReviewerReportDataVO(t.get(0, Integer.class), t.get(1, Integer.class),
							t.get(2, Integer.class), t.get(3, Integer.class), Integer.valueOf(t.get(4, Short.class)),
							t.get(5, Date.class), t.get(6, Date.class), t.get(7, Date.class), t.get(8, String.class)))
					.collect(Collectors.toList());

			List<UserReportVO> userVoList1 = new ArrayList<>();
			Map<Integer, List<UserReportVO>> userIdReviewerList = activeReviewerList.stream()
					.collect(Collectors.groupingBy(u -> u.getId()));

			Map<Integer, List<ReviewerReportDataVO>> respondedManuscriptGroup = reviewerReportDataResponded.stream()
					.collect(Collectors.groupingBy(w -> w.getReviewerId()));

			respondedManuscriptGroup.forEach((k,v) -> userVoList.add(userIdReviewerList.get(k).get(0)));
			
			
			Map<Integer, List<ReviewerReportDataVO>> manuscriptGroup = reviewerReportData.stream()
					.collect(Collectors.groupingBy(w -> w.getReviewerId()));
//			manuscriptGroup.forEach((k,v) -> userVoList1.add(userIdReviewerList.get(k).get(0)));
			manuscriptGroup.forEach((k, v) -> {
				List<UserReportVO> reviewers = userIdReviewerList.get(k);
				if (reviewers != null && !reviewers.isEmpty() && reviewers.get(0) != null) {
					userVoList1.add(reviewers.get(0));
				}
				});


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////			

			List<ReviewerPerformanceDTO> getRefreeDetailsVODetails = getReportsForIndividualReviewers(iDVO);
			
			
			reviewersPerformanceVo.setUser(userVoList);
			reviewersPerformanceVo.setReviewerNo(userVoList1);
			reviewersPerformanceVo.setGetIndividualRefereePerformances(getRefreeDetailsVODetails);

	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }

	    return reviewersPerformanceVo;
	}

	
    public List<ReviewerPerformanceDTO> getReportsForIndividualReviewers(IDVO iDVO) {
        List<ReviewerPerformanceDTO> reports = new ArrayList<>();

        try {
            if (iDVO.getJournalIdList() != null && !iDVO.getJournalIdList().isEmpty()
                    && iDVO.getYearList() != null && !iDVO.getYearList().isEmpty()) {

                // Fetch reviewer IDs by year and month
                List<Integer> reviewerIds = fetchReviewerIds(iDVO);

                if (reviewerIds != null && !reviewerIds.isEmpty()) {
                    // Process reviewers in batches
                    for (int i = 0; i < reviewerIds.size(); i += BATCH_SIZE) {
                        int end = Math.min(i + BATCH_SIZE, reviewerIds.size());
                        List<Integer> batchReviewerIds = reviewerIds.subList(i, end);

                        // Fetch performance data for each batch of reviewers
                        List<ReviewerPerformanceDTO> batchReports = fetchPerformanceDataForReviewers(
                                iDVO.getJournalIdList(), batchReviewerIds, iDVO.getYearList(), iDVO.getMonth());

                        reports.addAll(batchReports);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("Error fetching reviewer performance reports: " + ex.getMessage());
        }

        return reports;
    }

    // Helper method to fetch reviewer IDs based on year and month
    private List<Integer> fetchReviewerIds(IDVO iDVO) {
        if (iDVO.getMonth() != null && iDVO.getMonth() != 0) {
            return manuScriptReviewerRepo.findReviewerIdsByJournalAndYearAndMonth(
                    iDVO.getJournalIdList(), iDVO.getYearList(), iDVO.getMonth());
        } else {
            return manuScriptReviewerRepo.findReviewerIdsByJournalAndYear(
                    iDVO.getJournalIdList(), iDVO.getYearList());
        }
    }

    // Fetch performance data for each batch of reviewers
    private List<ReviewerPerformanceDTO> fetchPerformanceDataForReviewers(
            List<Integer> journalIds, List<Integer> reviewerIds, List<Integer> yearList, Integer month) {

        List<ReviewerPerformanceDTO> performanceData = new ArrayList<>();

        // Fetch performance and reminders in one go
        List<Object[]> performanceResult = manuScriptReviewerRepo.fetchReviewerPerformanceDataWithReminders(
                journalIds, reviewerIds, yearList, month);
                
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Process the results and populate DTOs
        for (Object[] data : performanceResult) {
            ReviewerPerformanceDTO dto = new ReviewerPerformanceDTO();
            dto.setReviewerId((Integer) data[0]);
            dto.setRefreename((String) data[1]);  // Cast refreename to String

//         dto.setAvgDaysToCompleteReview(data[2] != null ? roundAndTrim( data[2]) : "0");  // Convert BigDecimal to String
         // Initialize counters for on-time, late, and total reviewed articles
         int reviewsOnTime = 0;
         int reviewsLate = 0;
         int totalArticlesReviewed = 0;
         long totalDaysToCompleteReview = 0;

         // Fetch review data
         List<Object[]> reviewDatas = manuScriptReviewerRepo.getReviewerPerformanceReviewed(journalIds, yearList, month, (Integer) data[0]);
         DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
         // Process review data to determine counts
         for (Object[] reviewData : reviewDatas) {
             // Assuming reviewData[0] is created_at and reviewData[1] is due_date
        	 LocalDateTime createdAtInvite = reviewData[0] != null ? LocalDateTime.parse(reviewData[0].toString(), dateTimeFormatter) : null;
             LocalDateTime createdAtComplete = reviewData[1] != null ? LocalDateTime.parse(reviewData[1].toString(), dateTimeFormatter) : null;
             LocalDateTime dueDate = reviewData[2] != null ? LocalDateTime.parse(reviewData[2].toString(), dateTimeFormatter) : null;

             if (createdAtComplete != null && dueDate != null && !createdAtInvite.isAfter(createdAtComplete)) {
            	 long daysBetween = ChronoUnit.DAYS.between(createdAtInvite.toLocalDate(), createdAtComplete.toLocalDate())+1;
                 
                 totalDaysToCompleteReview += daysBetween; // Accumulate the total days
                 // Count the reviews based on their dates
                 if (createdAtComplete.toLocalDate().isBefore(dueDate.toLocalDate()) || createdAtComplete.toLocalDate().isEqual(dueDate.toLocalDate())) {
                     reviewsOnTime++;
                 } else {
                     reviewsLate++;
                 }
                 totalArticlesReviewed++;
             }
         }
//         double totalDaysToCompleteReview = totalHoursToCompleteReview / 24.0;
         double averageDaysToCompleteReview = totalArticlesReviewed > 0 ? (double) totalDaysToCompleteReview / totalArticlesReviewed : 0.00;
         // Set the counts in the DTO
         dto.setReviewsOnTime(String.valueOf(reviewsOnTime));
         dto.setReviewsLate(String.valueOf(reviewsLate));
         dto.setTotalArticlesReviewed(String.valueOf(totalArticlesReviewed));
         
         dto.setAvgDaysToCompleteReview(averageDaysToCompleteReview != 0.0 ? roundAndTrim(averageDaysToCompleteReview) : "0");

         dto.setReviewsNeverReturned(data[2] != null ? roundAndTrim(data[2]) : "0");  // Convert BigDecimal to String
         dto.setAvgRatingQuality(data[3] != null ? roundAndTrim(data[3]) : "0");  // Convert BigDecimal to String
         dto.setAvgRatingTimeliness(data[4] != null ? roundAndTrim(data[4]) : "0");  // Convert BigDecimal to String

         
         // Fetch total reminder count
         int totalReminders = manuScriptReviewerRepo.getTotalReminderCount(journalIds, yearList, month,(Integer)data[0]);
         // Fetch distinct manuscript count
         int distinctManuscripts = manuScriptReviewerRepo.getDistinctManuscriptCount(journalIds, yearList, month,(Integer)data[0]);
         double avgReminder = totalReminders > 0 ? (double) totalReminders / distinctManuscripts : 0.00;
         
         // Convert avgRemindersSent to String if not null, otherwise default to "0"
         dto.setAvgRemindersSent(avgReminder != 0.0 ? roundAndTrim(avgReminder) : "0");

            performanceData.add(dto);
        }
        
        return performanceData;
    }
    

    private String roundAndTrim(Object data) {
        if (data == null) {
            return "0";  // Return "0" if the value is null
        }
        
        // Use BigDecimal to handle both BigInteger and BigDecimal
        BigDecimal bdValue = (data instanceof BigDecimal) ? (BigDecimal) data : new BigDecimal(data.toString());
        
        // Round to 2 decimal places
        DecimalFormat df = new DecimalFormat("#.##");
        
        // If the value is a whole number, remove decimal places
        df.setMinimumFractionDigits(0);
        df.setMaximumFractionDigits(2);
        
        return df.format(bdValue);
    }


//	@Override
//	public EditorPerformanceVo getEditorPerformanceReport(IDVO iDVO) {
//
//		if (iDVO.getJournalIdList() != null && !iDVO.getJournalIdList().isEmpty() && iDVO.getYearList() != null
//				&& !iDVO.getYearList().isEmpty() && iDVO.getMonth() != 0) {
//
//			EditorPerformanceVo addEditorPerformanceVo = new EditorPerformanceVo();
//			List<DecisionForEditorVO> articlesForDecision = new ArrayList<>();
//
//			List<ManuscriptEditor> listOfEditors = manuscriptEditorRepo.getEditorName1(iDVO.getJournalIdList(),
//					iDVO.getYearList(), iDVO.getMonth());
//			if (!listOfEditors.isEmpty()) {
//				List<ManuscriptEditor> listOfAccepted = manuscriptEditorRepo
//						.getArticlesForAccepted(iDVO.getJournalIdList(), iDVO.getYearList(), iDVO.getMonth());
//
//				Map<Integer, List<ManuscriptEditor>> editorAcceptMapList = listOfAccepted.stream()
//						.collect(Collectors.groupingBy(w -> w.getEditorId()));
//
//				List<ManuscriptEditor> listOfRejected = manuscriptEditorRepo
//						.getArticlesForRejected(iDVO.getJournalIdList(), iDVO.getYearList(), iDVO.getMonth());
//
//				Map<Integer, List<ManuscriptEditor>> editorRejectMapList = listOfRejected.stream()
//						.collect(Collectors.groupingBy(w -> w.getEditorId()));
//
//				List<ManuscriptEditor> assignmentToSuggestingReviewers = manuscriptEditorRepo
//						.getAssignmentToSuggestingReviewers(iDVO.getJournalIdList(), iDVO.getYearList(),
//								iDVO.getMonth());
//
//				Map<Integer, List<ManuscriptEditor>> editorAssignmentMapList = assignmentToSuggestingReviewers.stream()
//						.collect(Collectors.groupingBy(w -> w.getEditorId()));
//
//				List<ManuscriptEditor> assigningtoDecisionRequestToDecision = manuscriptEditorRepo
//						.getAssignmentToDecision(iDVO.getJournalIdList(), iDVO.getYearList(), iDVO.getMonth());
//
//				Map<Integer, List<ManuscriptEditor>> editorDecisionMapList = assigningtoDecisionRequestToDecision
//						.stream().collect(Collectors.groupingBy(w -> w.getEditorId()));
//
//				listOfEditors.stream().forEach(listOfEditor -> {
//					try {
//						long assignmentToSuggestingReviewersnumberOfDays = 0;
//						// Editor Name
//						DecisionForEditorVO decisionForEditorVO = new DecisionForEditorVO();
//
//						decisionForEditorVO.setEditor(
//								listOfEditor.getUser().getFirstName() + " " + listOfEditor.getUser().getLastName());
//
//						// Editor Accepted
//						if (editorAcceptMapList.get(listOfEditor.getEditorId()) != null) {
//							decisionForEditorVO.setAccepted(editorAcceptMapList != null
//									? editorAcceptMapList.get(listOfEditor.getEditorId()) != null
//											? editorAcceptMapList.get(listOfEditor.getEditorId()).size()
//											: 0
//									: 0);
//						}
//
//						// Editor Rejected
//						if (editorRejectMapList.get(listOfEditor.getEditorId()) != null) {
//							decisionForEditorVO.setRejected(editorRejectMapList != null
//									? editorRejectMapList.get(listOfEditor.getEditorId()) != null
//											? editorRejectMapList.get(listOfEditor.getEditorId()).size()
//											: 0
//									: 0);
//						}
//
//						// // DaysFromAssignmentToSuggestingReviewers
//
//						long differenceInDays = 0;
//						long differenceInTime = 0;
//
//						if (editorAssignmentMapList.get(listOfEditor.getEditorId()) != null) {
//							for (ManuscriptEditor manuscriptEditor : editorAssignmentMapList
//									.get(listOfEditor.getEditorId())) {
//								List<ManuScript> manuscriptRepo = manuScriptRepo.getmanManuScriptsdata(
//										iDVO.getJournalIdList(), manuscriptEditor.getManuscriptId(),
//										manuscriptEditor.getEditorId());
//								for (ManuScript manuScriptlist : manuscriptRepo) {
//									if (manuScriptlist.getUpdatedOn() != null
//											&& listOfEditor.getAssignedDate() != null) {
//										differenceInTime = manuScriptlist.getUpdatedOn().getTime()
//												- listOfEditor.getAssignedDate().getTime();
//									}
//									differenceInDays = (differenceInTime / (1000 * 60 * 60 * 24)) % 365;
//								}
//							}
//						}
//						assignmentToSuggestingReviewersnumberOfDays = (int) differenceInDays + (int) differenceInDays;
//
//						if (assignmentToSuggestingReviewersnumberOfDays < 0) {
//							assignmentToSuggestingReviewersnumberOfDays = 0;
//						}
//
//						decisionForEditorVO.setDaysFromAssignmentToSuggestingReviewers(
//								assignmentToSuggestingReviewersnumberOfDays);
//
//						// DaysFromAssignmentTodission
//						long suggestingReviewersDifferenceInDays = 0;
//						long suggestingReviewersDifferenceInTime = 0;
//
//						long assignmentToDessionsnumberOfDays = 0;
//
//						if (editorDecisionMapList.get(listOfEditor.getEditorId()) != null) {
//							for (ManuscriptEditor manuscriptEditor : editorDecisionMapList
//									.get(listOfEditor.getEditorId())) {
//								List<ManuScript> manuscriptRepo = manuScriptRepo.getmanManuScriptsdataDecision(
//										iDVO.getJournalIdList(), manuscriptEditor.getManuscriptId(),
//										manuscriptEditor.getEditorId());
//
//								for (ManuScript manuScriptlist : manuscriptRepo) {
//									if (manuScriptlist.getProductionDate() != null
//											&& listOfEditor.getAssignedDate() != null) {
//										suggestingReviewersDifferenceInTime = manuScriptlist.getProductionDate()
//												.getTime() - listOfEditor.getAssignedDate().getTime();
//									}
//
//									if (manuScriptlist.getFinalDecisionDate() != null
//											&& listOfEditor.getAssignedDate() != null) {
//										suggestingReviewersDifferenceInTime = manuScriptlist.getFinalDecisionDate()
//												.getTime() - listOfEditor.getAssignedDate().getTime();
//									}
//
//									if (manuScriptlist.getRejectedDate() != null
//											&& listOfEditor.getAssignedDate() != null) {
//										suggestingReviewersDifferenceInTime = manuScriptlist.getRejectedDate().getTime()
//												- listOfEditor.getAssignedDate().getTime();
//									}
//
//									suggestingReviewersDifferenceInDays = (suggestingReviewersDifferenceInTime
//											/ (1000 * 60 * 60 * 24)) % 365;
//								}
//							}
//						}
//						assignmentToDessionsnumberOfDays = (int) suggestingReviewersDifferenceInDays
//								+ (int) suggestingReviewersDifferenceInDays;
//
//						if (assignmentToDessionsnumberOfDays < 0) {
//							assignmentToDessionsnumberOfDays = 0;
//						}
//
//						decisionForEditorVO.setDaysFromDecisionRequestToDecision(assignmentToDessionsnumberOfDays);
//						articlesForDecision.add(decisionForEditorVO);
//
//						addEditorPerformanceVo.setArticlesForDecision(articlesForDecision);
//					} catch (Exception e) {
//
//						log.debug("Erroor occured during creating report for editor :::" + listOfEditor.getEditorId());
//					}
//				});
//
//				return addEditorPerformanceVo;
//			}
//
//		}
//
//		if (iDVO.getJournalIdList() != null && !iDVO.getJournalIdList().isEmpty() && iDVO.getYearList() != null
//				&& !iDVO.getYearList().isEmpty() && iDVO.getMonth() == 0) {
//			EditorPerformanceVo addEditorPerformanceVo = new EditorPerformanceVo();
//			List<DecisionForEditorVO> articlesForDecision = new ArrayList<>();
//
//			List<ManuscriptEditor> listOfEditors = manuscriptEditorRepo.getEditorNameAll1(iDVO.getJournalIdList(),
//					iDVO.getYearList());
//			if (!listOfEditors.isEmpty()) {
//				List<ManuscriptEditor> listOfAccepted = manuscriptEditorRepo
//						.getArticlesForAcceptedAll(iDVO.getJournalIdList(), iDVO.getYearList());
//				Map<Integer, List<ManuscriptEditor>> editorAcceptMapList = listOfAccepted.stream()
//						.collect(Collectors.groupingBy(w -> w.getEditorId()));
//
//				List<ManuscriptEditor> listOfRejected = manuscriptEditorRepo
//						.getArticlesForRejectedAll(iDVO.getJournalIdList(), iDVO.getYearList());
//				Map<Integer, List<ManuscriptEditor>> editorRejectMapList = listOfRejected.stream()
//						.collect(Collectors.groupingBy(w -> w.getEditorId()));
//
//				List<ManuscriptEditor> assignmentToSuggestingReviewers = manuscriptEditorRepo
//						.getAssignmentToSuggestingReviewersAll(iDVO.getJournalIdList(), iDVO.getYearList());
//				Map<Integer, List<ManuscriptEditor>> editorAssignmentMapList = assignmentToSuggestingReviewers.stream()
//						.collect(Collectors.groupingBy(w -> w.getEditorId()));
//
//				List<ManuscriptEditor> assigningtoDecisionRequestToDecision = manuscriptEditorRepo
//						.getAssignmentToDecisionAll(iDVO.getJournalIdList(), iDVO.getYearList());
//				Map<Integer, List<ManuscriptEditor>> editorDecisionMapList = assigningtoDecisionRequestToDecision
//						.stream().collect(Collectors.groupingBy(w -> w.getEditorId()));
//
//				listOfEditors.stream().forEach(listOfEditor -> {
//					try {
//						long assignmentToSuggestingReviewersnumberOfDays = 0;
//						// Editor Name
//						DecisionForEditorVO decisionForEditorVO = new DecisionForEditorVO();
//
//						decisionForEditorVO.setEditor(
//								listOfEditor.getUser().getFirstName() + " " + listOfEditor.getUser().getLastName());
//
//						// Editor Accepted
//						if (editorAcceptMapList.get(listOfEditor.getEditorId()) != null) {
//							decisionForEditorVO.setAccepted(editorAcceptMapList != null
//									? editorAcceptMapList.get(listOfEditor.getEditorId()) != null
//											? editorAcceptMapList.get(listOfEditor.getEditorId()).size()
//											: 0
//									: 0);
//						}
//
//						// Editor Rejected
//						if (editorRejectMapList.get(listOfEditor.getEditorId()) != null) {
//							decisionForEditorVO.setRejected(editorRejectMapList != null
//									? editorRejectMapList.get(listOfEditor.getEditorId()) != null
//											? editorRejectMapList.get(listOfEditor.getEditorId()).size()
//											: 0
//									: 0);
//						}
//
//						// // DaysFromAssignmentToSuggestingReviewers
//
//						long differenceInDays = 0;
//						long differenceInTime = 0;
//
//						if (editorAssignmentMapList.get(listOfEditor.getEditorId()) != null) {
//							for (ManuscriptEditor manuscriptEditor : editorAssignmentMapList
//									.get(listOfEditor.getEditorId())) {
//								List<ManuScript> manuscriptRepo = manuScriptRepo.getmanManuScriptsdata(
//										iDVO.getJournalIdList(), manuscriptEditor.getManuscriptId(),
//										manuscriptEditor.getEditorId());
//								for (ManuScript manuScriptlist : manuscriptRepo) {
//									if (manuScriptlist.getUpdatedOn() != null
//											&& listOfEditor.getAssignedDate() != null) {
//										differenceInTime = manuScriptlist.getUpdatedOn().getTime()
//												- listOfEditor.getAssignedDate().getTime();
//									}
//									differenceInDays = (differenceInTime / (1000 * 60 * 60 * 24)) % 365;
//								}
//							}
//						}
//						assignmentToSuggestingReviewersnumberOfDays = (int) differenceInDays + (int) differenceInDays;
//
//						if (assignmentToSuggestingReviewersnumberOfDays < 0) {
//							assignmentToSuggestingReviewersnumberOfDays = 0;
//						}
//
//						decisionForEditorVO.setDaysFromAssignmentToSuggestingReviewers(
//								assignmentToSuggestingReviewersnumberOfDays);
//
//						// DaysFromAssignmentTodission
//						long suggestingReviewersDifferenceInDays = 0;
//						long suggestingReviewersDifferenceInTime = 0;
//
//						long assignmentToDessionsnumberOfDays = 0;
//
//						if (editorDecisionMapList.get(listOfEditor.getEditorId()) != null) {
//							for (ManuscriptEditor manuscriptEditor : editorDecisionMapList
//									.get(listOfEditor.getEditorId())) {
//								List<ManuScript> manuscriptRepo = manuScriptRepo.getmanManuScriptsdataDecision(
//										iDVO.getJournalIdList(), manuscriptEditor.getManuscriptId(),
//										manuscriptEditor.getEditorId());
//
//								for (ManuScript manuScriptlist : manuscriptRepo) {
//									if (manuScriptlist.getProductionDate() != null
//											&& listOfEditor.getAssignedDate() != null) {
//										suggestingReviewersDifferenceInTime = manuScriptlist.getProductionDate()
//												.getTime() - listOfEditor.getAssignedDate().getTime();
//									}
//
//									if (manuScriptlist.getFinalDecisionDate() != null
//											&& listOfEditor.getAssignedDate() != null) {
//										suggestingReviewersDifferenceInTime = manuScriptlist.getFinalDecisionDate()
//												.getTime() - listOfEditor.getAssignedDate().getTime();
//									}
//
//									if (manuScriptlist.getRejectedDate() != null
//											&& listOfEditor.getAssignedDate() != null) {
//										suggestingReviewersDifferenceInTime = manuScriptlist.getRejectedDate().getTime()
//												- listOfEditor.getAssignedDate().getTime();
//									}
//
//									suggestingReviewersDifferenceInDays = (suggestingReviewersDifferenceInTime
//											/ (1000 * 60 * 60 * 24)) % 365;
//								}
//							}
//						}
//						assignmentToDessionsnumberOfDays = (int) suggestingReviewersDifferenceInDays
//								+ (int) suggestingReviewersDifferenceInDays;
//
//						if (assignmentToDessionsnumberOfDays < 0) {
//							assignmentToDessionsnumberOfDays = 0;
//						}
//
//						decisionForEditorVO.setDaysFromDecisionRequestToDecision(assignmentToDessionsnumberOfDays);
//						articlesForDecision.add(decisionForEditorVO);
//
//						addEditorPerformanceVo.setArticlesForDecision(articlesForDecision);
//					} catch (Exception e) {
//
//						log.debug("Erroor occured during creating report for editor :::" + listOfEditor.getEditorId());
//					}
//				});
//
//				return addEditorPerformanceVo;
//			}
//
//		}
//		return null;
//	}
    
	@Override
	public EditorPerformanceVo getEditorPerformanceReport(IDVO iDVO) {

		if (iDVO.getJournalIdList() != null && !iDVO.getJournalIdList().isEmpty() && iDVO.getYearList() != null
				&& !iDVO.getYearList().isEmpty()) {
			EditorPerformanceVo addEditorPerformanceVo = new EditorPerformanceVo();
			List<DecisionForEditorVO> articlesForDecision = new ArrayList<>();

			
			List<Object[]> editorData = manuscriptEditorRepo.findEditorPerformance(iDVO.getJournalIdList(),iDVO.getYearList(),iDVO.getMonth());
			
			if (!editorData.isEmpty()) {

		        editorData.forEach(row -> {
		            try {
		                // Extract data from the query result
		            	Integer editorId = ((Integer) row[0]);
		                String editorName = (String) row[1];
		                Integer accepted = ((BigInteger) row[2]).intValue();
		                Integer rejected = ((BigInteger) row[3]).intValue();
		                Integer RecommendationAccept = ((BigInteger) row[4]).intValue();
		                Integer RecommendationReject = ((BigInteger) row[5]).intValue();
		                Integer RecommendationRevision = ((BigInteger) row[6]).intValue();
		                // Create DecisionForEditorVO object and populate it
		                DecisionForEditorVO decisionForEditorVO = new DecisionForEditorVO();
		                decisionForEditorVO.setEditor(editorName);
		                decisionForEditorVO.setAccepted((int) accepted);
		                decisionForEditorVO.setRejected((int) rejected);
		                decisionForEditorVO.setRecommendationAccepted((int)RecommendationAccept);
		                decisionForEditorVO.setRecommendationRejected((int) RecommendationReject);
		                decisionForEditorVO.setRecommendationRevision((int) RecommendationRevision);
		                
		                List<Object[]> assignmentToInvitingTODecisionData  = manuscriptEditorRepo.getAssignmentToInvitingTODecisionData(iDVO.getJournalIdList(), iDVO.getYearList(),iDVO.getMonth(), editorId);

		                List<Long> daysBetweenAssignmentToInviting = extractDaysBetween(assignmentToInvitingTODecisionData, 2, 3);
		                
	                    setStatistics(decisionForEditorVO::setAvgDaysOfassignmentToInviting, decisionForEditorVO::setMaxDaysOfassignmentToInviting,
	                            decisionForEditorVO::setMinDaysOfassignmentToInviting, daysBetweenAssignmentToInviting);	               

		                List<Long> daysBetweenAssignmentToDecision = extractDaysBetween(assignmentToInvitingTODecisionData, 3, 4);
		                
	                    setStatistics(decisionForEditorVO::setAvgDaysOfassignmentToDecision, decisionForEditorVO::setMaxDaysOfassignmentToDecision,
	                            decisionForEditorVO::setMinDaysOfassignmentToDecision, daysBetweenAssignmentToDecision);
	                    
		                
		                articlesForDecision.add(decisionForEditorVO);
		                addEditorPerformanceVo.setArticlesForDecision(articlesForDecision);
		            } catch (Exception e) {
		            	System.out.println(e.getLocalizedMessage());
		                log.debug("Error occurred while processing editor report for editor name: " + row[0]);
		            }
		        });

				return addEditorPerformanceVo;
			}

		}
		return null;
	}
	
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
	
	private Long calculateDaysBetween(Timestamp startTimestamp, Timestamp endTimestamp) {
	    if (startTimestamp != null && endTimestamp != null) {
	        LocalDate startDate = startTimestamp.toLocalDateTime().toLocalDate();
	        LocalDate endDate = endTimestamp.toLocalDateTime().toLocalDate();
	        return !endDate.isBefore(startDate) ? ChronoUnit.DAYS.between(startDate, endDate) + 1 : null;
	    }
	    return null;
	}

	private void setStatistics(Consumer<Float> avgSetter, Consumer<Long> maxSetter, Consumer<Long> minSetter,
	                           List<Long> daysList) {
	    float average = (float) Math.round(
	            daysList.stream().mapToLong(Long::longValue).average().orElse(0.0) * 100) / 100;
	    avgSetter.accept(average);

	    long maxDay = daysList.stream().mapToLong(Long::longValue).max().orElse(0L);
	    maxSetter.accept(maxDay);

	    long minDay = daysList.stream().mapToLong(Long::longValue).min().orElse(0L);
	    minSetter.accept(minDay);

	    log.info("Statistics computed: Average = {}, Max = {}, Min = {}", average, maxDay, minDay);
	}


	/********* Present Status ********/
	@Override
	public PresentStatusVO getPresentStatus(IDVO iDVO) {
		PresentStatusVO presentStatusVO = new PresentStatusVO();

		List<LoggedUserVo> loginUserlist = new ArrayList<>();
		List<LoggedUserVo> workingProgressStatuslist = new ArrayList<>();
		List<LoggedUserVo> showingResultOfyearlist = new ArrayList<>();
		List<LoggedUserVo> lastLoginlist = new ArrayList<>();

		Integer newArticleSubmitted = 0;
		Integer commentsSubmittedReviewer = 0;
		Integer revisedArticleSubmitted = 0;

		// Login User
		List<UserLogins> journalBasedUserDetails = userLoginsRepo.getPresentStatusReports(iDVO.getJournalId());
		if (journalBasedUserDetails != null && !journalBasedUserDetails.isEmpty()) {
			journalBasedUserDetails.stream().forEach(i -> {
				if (!i.getUserId().equals(iDVO.getUserId())) {
					LoggedUserVo addLoggedUser = new LoggedUserVo();
					addLoggedUser.setId(i.getId());
					addLoggedUser.setUserId(i.getUserId());
					addLoggedUser.setUserEmail(i.getUser().getUsername());
					addLoggedUser.setUserName(i.getUser().getFirstName() + " " + i.getUser().getLastName());
					addLoggedUser.setJournalId(i.getJournalId());
					addLoggedUser.setUserRole(i.getUserRole());
					addLoggedUser.setStatus(i.getLoggedInDate().getMinutes() - new Date().getMinutes());
					addLoggedUser.setIpAddress(i.getIpAddress());
					addLoggedUser.setCurrently(i.getCurrently());
					addLoggedUser.setLoggedInDate(i.getLoggedInDate());
					loginUserlist.add(addLoggedUser);
					presentStatusVO.setLoginUser(loginUserlist);
				}
			});
		}

		// Last Login
		List<UserLogins> journalLastLoginDteails = userLoginsRepo.getLastloginDetails(iDVO.getJournalId());
		LoggedUserVo addLoggedUser1 = new LoggedUserVo();
		for (UserLogins ul : journalLastLoginDteails) {
			newArticleSubmitted++;
		}
		addLoggedUser1.setNewArticleSubmitted(newArticleSubmitted);

		List<UserLogins> journalLastReviewerComments = userLoginsRepo
				.getLastLoginsReviewerComments(iDVO.getJournalId());
		for (UserLogins ul : journalLastReviewerComments) {
			commentsSubmittedReviewer++;
		}
		addLoggedUser1.setCommentsSubmittedReviewer(commentsSubmittedReviewer);

		List<UserLogins> journalLastRevisedManuscript = userLoginsRepo
				.getLastLoginsRevisedManuscript(iDVO.getJournalId());
		for (UserLogins ul : journalLastRevisedManuscript) {
			revisedArticleSubmitted++;
		}
		addLoggedUser1.setRevisedArticleSubmitted(revisedArticleSubmitted);
		lastLoginlist.add(addLoggedUser1);
		presentStatusVO.setLastLogin(lastLoginlist);

		// Working in Progress Status

		return presentStatusVO;
	}

	@Override
	public List<ArticleRatingVo> getArticleRating(IDVO iDVO) {

		List<ArticleRatingVo> addArticleRatingVoData = new ArrayList<>();
		if (iDVO.getJournalIdList() != null && !iDVO.getJournalIdList().isEmpty() && iDVO.getYearList() != null
				&& !iDVO.getYearList().isEmpty() && iDVO.getRatingName() != null && !iDVO.getRatingName().isEmpty()
				&& iDVO.getRatingNumber() != null) {
			if (iDVO.getRatingName().equalsIgnoreCase("Greater Than")) {
				List<ManuscriptRating> getArticleRatingGreaterThanData = manuscriptRatingRepo
						.getArticleRatingGreaterThan(iDVO.getJournalIdList(), iDVO.getYearList(),
								iDVO.getRatingNumber());
				if (getArticleRatingGreaterThanData != null && !getArticleRatingGreaterThanData.isEmpty()) {
					getArticleRatingGreaterThanData.stream().forEach(i -> {
						ArticleRatingVo addArticleRating = new ArticleRatingVo();
						addArticleRating.setManuscriptCode(i.getManuScript().getManuscriptCode());
						addArticleRating.setArticleTitle(i.getManuScript().getManuscriptTitle());
						addArticleRating.setAuthorName(i.getManuScript().getUser().getFirstName() + " "
								+ i.getManuScript().getUser().getLastName());
						addArticleRating.setRating(i.getRating());

						addArticleRating.setAcceptedDate(i.getManuScript().getProductionDate());
						String editor_name = null;
						editor_name = manuscriptEditorRepo.getEditorNameRatingReport(
								i.getManuScript().getManuScriptId(), i.getManuScript().getJournalId());
						addArticleRating.setEditorName(editor_name);
//						if (i.getManuScript().getManuscriptEditor() != null) {
//							addArticleRating
//									.setEditorName(i.getManuScript().getManuscriptEditor().getUser().getFirstName()
//											+ " " + i.getManuScript().getManuscriptEditor().getUser().getLastName());
//							if (i.getManuScript().getManuscriptEditor().getManuscriptStatus().equals(33))
//								addArticleRating.setAcceptedDate(
//										i.getManuScript().getManuscriptEditor().getEditorDecisionDate());
//						}
						addArticleRatingVoData.add(addArticleRating);
					});
				}

			} else if (iDVO.getRatingName().equalsIgnoreCase("Equal")) {
				List<ManuscriptRating> getArticleRatingEqualTo = manuscriptRatingRepo
						.getArticleRatingEqualTo(iDVO.getJournalIdList(), iDVO.getYearList(), iDVO.getRatingNumber());
				if (getArticleRatingEqualTo != null && !getArticleRatingEqualTo.isEmpty()) {
					getArticleRatingEqualTo.stream().forEach(i -> {
						ArticleRatingVo addArticleRating = new ArticleRatingVo();
						addArticleRating.setManuscriptCode(i.getManuScript().getManuscriptCode());
						addArticleRating.setArticleTitle(i.getManuScript().getManuscriptTitle());
						addArticleRating.setAuthorName(i.getManuScript().getUser().getFirstName() + " "
								+ i.getManuScript().getUser().getLastName());
						addArticleRating.setRating(i.getRating());
						addArticleRating.setAcceptedDate(i.getManuScript().getProductionDate());
						String editor_name = null;
						editor_name = manuscriptEditorRepo.getEditorNameRatingReport(
								i.getManuScript().getManuScriptId(), i.getManuScript().getJournalId());
						addArticleRating.setEditorName(editor_name);
						addArticleRatingVoData.add(addArticleRating);
					});
				}

			} else if (iDVO.getRatingName().equalsIgnoreCase("Less Than")) {
				List<ManuscriptRating> getArticleRatingLessThan = manuscriptRatingRepo
						.getArticleRatingLessThan(iDVO.getJournalIdList(), iDVO.getYearList(), iDVO.getRatingNumber());
				if (getArticleRatingLessThan != null && !getArticleRatingLessThan.isEmpty()) {
					getArticleRatingLessThan.stream().forEach(i -> {
						ArticleRatingVo addArticleRating = new ArticleRatingVo();
						addArticleRating.setManuscriptCode(i.getManuScript().getManuscriptCode());
						addArticleRating.setArticleTitle(i.getManuScript().getManuscriptTitle());
						addArticleRating.setAuthorName(i.getManuScript().getUser().getFirstName() + " "
								+ i.getManuScript().getUser().getLastName());
						addArticleRating.setRating(i.getRating());
						addArticleRating.setAcceptedDate(i.getManuScript().getProductionDate());
						String editor_name = null;
						editor_name = manuscriptEditorRepo.getEditorNameRatingReport(
								i.getManuScript().getManuScriptId(), i.getManuScript().getJournalId());
						addArticleRating.setEditorName(editor_name);
						addArticleRatingVoData.add(addArticleRating);
					});
				}
			}
		}
		return addArticleRatingVoData;
	}

	/******************** EIC PRESENT STATUS *********************/

	@Override
	public PresentStatusForEiCVO getPresentStatusForEiC(IDVO iDVO) {
		// Add On Parent Vo
		PresentStatusForEiCVO addPresentStatusForEiCVO = new PresentStatusForEiCVO();
		if (iDVO.getJournalIdList() != null && !iDVO.getJournalIdList().isEmpty() && iDVO.getPresentDate() != null
				&& !iDVO.getPresentDate().isEmpty()) {
			// Get All The Manuscript Type On the bases of Journal
			List<ManuscriptTypeJournal> listOfManuscriptTypeJournal = manuscriptTypeJournalsRepo
					.getManuscriptTypesFromJournalIdList(iDVO.getJournalIdList());
			// Add list of PresentStatusEicVo
			List<PresentStatusEicVo> addPresentStatusEicVoList = new ArrayList<>();
			if (listOfManuscriptTypeJournal != null && !listOfManuscriptTypeJournal.isEmpty()) {
				listOfManuscriptTypeJournal.stream().forEach(i -> {

					// List Of All Manuscript Id Of Manuscript type
					List<Integer> listOfManuscriptId = new ArrayList<>();

					// Start Adding Data in simple Vo
					PresentStatusEicVo addPresentStatusEicVo = new PresentStatusEicVo();

					// Manuscript Type Add
					addPresentStatusEicVo.setArticleType(i.getManutypeName());

					// Get All the manuscript of present date and manuscript type that journal has
					List<ManuscriptTypeDetails> listOfManuscriptTypeDetails = manuscriptTypeDetailsRepo
							.getManuscriptTypeDetailsListByManuscriptTypeByPresentDate(i.getManutypeName());
					if (listOfManuscriptTypeDetails != null && !listOfManuscriptTypeDetails.isEmpty()) {
						// Adding all the manuscript Id o manuscript List
						listOfManuscriptTypeDetails.stream().forEach(j -> {
							listOfManuscriptId.add(j.getManuscriptId());
						});
						if (listOfManuscriptId != null && !listOfManuscriptId.isEmpty()) {

							/**********
							 * Submitted Manuscript******** AND ************* Technical Modification
							 * ************* AND ***************** Re-submitted manuscripts
							 ************/
							// get All the submitted manuscript AND Technical Modification AND Re-submitted
							// manuscripts
							List<ManuScript> getSubmittedManuscriptsList = manuScriptRepo
									.getSubmittedManuscriptsList(listOfManuscriptId);
							if (getSubmittedManuscriptsList != null && !getSubmittedManuscriptsList.isEmpty()) {
								Integer submittedManuscripts = 0;
								Integer technicalModification = 0;
								Integer reSubmittedManuscripts = 0;
								for (ManuScript j : getSubmittedManuscriptsList) {
									if (j.getManuscriptStatusId().equals(2)) {
										submittedManuscripts++;
									}
									if (j.getManuscriptStatusId().equals(46)) {
										technicalModification++;
									}
									if (j.getManuscriptStatusId().equals(26)) {
										reSubmittedManuscripts++;
									}
								}
								// Submitted Manuscript add
								addPresentStatusEicVo.setSubmittedManuscripts(submittedManuscripts);

								// Technical Modification add
								addPresentStatusEicVo.setTechnicalModification(technicalModification);

								// Re-submitted manuscripts add
								addPresentStatusEicVo.setReSubmittedManuscripts(reSubmittedManuscripts);
							} else {
								// Submitted Manuscript add
								addPresentStatusEicVo.setSubmittedManuscripts(0);

								// Technical Modification add
								addPresentStatusEicVo.setTechnicalModification(0);

								// Re-submitted manuscripts add
								addPresentStatusEicVo.setReSubmittedManuscripts(0);
							}

							/************* withAssignedEditors *********/

							// get all the Assigned Editor List
							List<ManuscriptEditor> getAssignedEditorList = manuscriptEditorRepo
									.getAssignedEditorList(listOfManuscriptId);
							if (getAssignedEditorList != null && !getAssignedEditorList.isEmpty()) {
								Integer withAssignedEditors = 0;
								for (ManuscriptEditor k : getAssignedEditorList) {
									withAssignedEditors++;
								}
								// Assigned Editor add
								addPresentStatusEicVo.setWithAssignedEditors(withAssignedEditors);
							} else {
								// Assigned Editor add
								addPresentStatusEicVo.setWithAssignedEditors(0);
							}

							/************* Reviewers invited - no response **********/

							// get all the Reviewers invited - no response List List<ManuScriptReviewer>
							/*
							 * getReviewersInvitedNoResponse = manuScriptReviewerRepo
							 * .getReviewersInvitedNoResponse(listOfManuscriptId); if
							 * (getReviewersInvitedNoResponse != null &&
							 * !getReviewersInvitedNoResponse.isEmpty()) { Integer
							 * reviewersInvitedNoResponse = 0; for (ManuScriptReviewer l :
							 * getReviewersInvitedNoResponse) { if (l.getManuscriptStatus().equals(8)) {
							 * reviewersInvitedNoResponse++; } }
							 * 
							 * // Reviewers invited - no response
							 * addPresentStatusEicVo.setReviewersInvitedNoResponse(
							 * reviewersInvitedNoResponse); }
							 */

							// Reviewers Invited No Response
//							List<ManuScriptReviewer> getAllDataByjournal = manuScriptReviewerRepo.getInReviewManuscriptList(3, iDVO.getJournalIdList().get(0),listOfManuscriptId);
//							if(getAllDataByjournal!=null && !getAllDataByjournal.isEmpty()) {
//								Integer reviewersInvitedNoResponse=0;
//							 for (ManuScriptReviewer data : getAllDataByjournal) {
//								 reviewersInvitedNoResponse++;
//							}
//							 addPresentStatusEicVo.setReviewersInvitedNoResponse(reviewersInvitedNoResponse);
//							}else {
							addPresentStatusEicVo.setReviewersInvitedNoResponse(0);
							/************ Manuscripts in Review *******/

							// Manuscripts In Review
//							List<ManuScript> list = manuScriptRepo.getManuscriptByStatusList(26, iDVO.getJournalIdList().get(0),listOfManuscriptId);
//							if(list!=null && !list.isEmpty()) {
//								Integer manuscriptInReview=0;
//							 for (ManuScript data : list) {
//								 manuscriptInReview++;
//							}
//							 addPresentStatusEicVo.setReviewersInvitedNoResponse(manuscriptInReview);
//							}
//							else {
							addPresentStatusEicVo.setManuscriptsInReview(0);

							/*************** Reviewed manuscripts ***********/

							// Reviewed Manuscripts
//							List<ManuScript> list1 = manuScriptRepo.getManuscriptByStatusList(50, iDVO.getJournalIdList().get(0),listOfManuscriptId);
//							if(list1!=null && !list1.isEmpty()) {
//								Integer reviewedManuscripts=0;
//							 for (ManuScript data : list1) {
//								 reviewedManuscripts++;
//							}
//							 addPresentStatusEicVo.setReviewedManuscripts(reviewedManuscripts);
//							}
//							else {
							addPresentStatusEicVo.setReviewedManuscripts(0);

							/***************** Manuscripts In Revision ***************/
							// manuscriptsInRevision
							addPresentStatusEicVo.setManuscriptsInRevision(0);

							/************ revisedManuscripts ***********/
							// revisedManuscripts
							addPresentStatusEicVo.setRevisedManuscripts(0);

							/******** manuscriptsInReRevision ********/
							// manuscriptsInReRevision
							addPresentStatusEicVo.setManuscriptsInRevision(0);

							/*********** finalDecision *********/
							// finalDecision
							addPresentStatusEicVo.setFinalDecision(0);

							/***** manuscriptsInReReview ****/
							// manuscriptsInReReview
							addPresentStatusEicVo.setManuscriptsInReReview(0);

							/******* manuscriptsInReRevision *********/
							addPresentStatusEicVo.setManuscriptsInReRevision(0);

						}
					} else {
						// Submitted Manuscript add
						addPresentStatusEicVo.setSubmittedManuscripts(0);

						// Technical Modification add
						addPresentStatusEicVo.setTechnicalModification(0);

						// Re-submitted manuscripts add
						addPresentStatusEicVo.setReSubmittedManuscripts(0);

						// Assigned Editor add
						addPresentStatusEicVo.setWithAssignedEditors(0);

						// Reviewers Invited No Response
						addPresentStatusEicVo.setReviewersInvitedNoResponse(0);

						// Manuscripts In Review
						addPresentStatusEicVo.setManuscriptsInReview(0);

						// Reviewed Manuscripts
						addPresentStatusEicVo.setReviewedManuscripts(0);

						// Reviewed Manuscripts
						addPresentStatusEicVo.setReviewedManuscripts(0);

						/***************** Manuscripts In Revision ***************/
						// manuscriptsInRevision
						addPresentStatusEicVo.setManuscriptsInRevision(0);

						/************ revisedManuscripts ***********/
						// revisedManuscripts
						addPresentStatusEicVo.setRevisedManuscripts(0);

						/******** manuscriptsInReRevision ********/
						// manuscriptsInReRevision
						addPresentStatusEicVo.setManuscriptsInRevision(0);

						/*********** finalDecision *********/
						// finalDecision
						addPresentStatusEicVo.setFinalDecision(0);

						/***** manuscriptsInReReview ****/
						// manuscriptsInReReview
						addPresentStatusEicVo.setManuscriptsInReReview(0);

						/******* manuscriptsInReRevision *********/
						addPresentStatusEicVo.setManuscriptsInReRevision(0);

					}
					addPresentStatusEicVoList.add(addPresentStatusEicVo);
				});
				addPresentStatusForEiCVO.setPresentStatusVo(addPresentStatusEicVoList);
				Integer totalSubmittedManuscripts = 0;
				Integer totalWithAssignedEditors = 0;
				Integer totalTechnicalModification = 0;
				Integer totalReSubmittedManuscripts = 0;
				Integer totalReviewersInvitedNoResponse = 0;
				Integer totalManuscriptsInReview = 0;
				Integer totalReviewedManuscripts = 0;
				Integer totalManuscriptsInRevision = 0;
				Integer totalRevisedManuscripts = 0;
				Integer totalManuscriptsInReReview = 0;
				Integer totalManuscriptsInReRevision = 0;
				Integer totalFinalDecision = 0;
				if (addPresentStatusEicVoList != null && !addPresentStatusEicVoList.isEmpty()) {
					for (PresentStatusEicVo PresentStatusEicVo : addPresentStatusEicVoList) {
						if (PresentStatusEicVo.getSubmittedManuscripts() != null)
							totalSubmittedManuscripts += PresentStatusEicVo.getSubmittedManuscripts();
						if (PresentStatusEicVo.getWithAssignedEditors() != null)
							totalWithAssignedEditors += PresentStatusEicVo.getWithAssignedEditors();
						if (PresentStatusEicVo.getTechnicalModification() != null)
							totalTechnicalModification += PresentStatusEicVo.getTechnicalModification();
						if (PresentStatusEicVo.getReSubmittedManuscripts() != null)
							totalReSubmittedManuscripts += PresentStatusEicVo.getReSubmittedManuscripts();
						if (PresentStatusEicVo.getReviewersInvitedNoResponse() != null)
							totalReviewersInvitedNoResponse += PresentStatusEicVo.getReviewersInvitedNoResponse();
						if (PresentStatusEicVo.getManuscriptsInReview() != null)
							totalManuscriptsInReview += PresentStatusEicVo.getManuscriptsInReview();
						if (PresentStatusEicVo.getReviewedManuscripts() != null)
							totalReviewedManuscripts += PresentStatusEicVo.getReviewedManuscripts();
						if (PresentStatusEicVo.getManuscriptsInRevision() != null)
							totalManuscriptsInRevision += PresentStatusEicVo.getManuscriptsInRevision();
						if (PresentStatusEicVo.getRevisedManuscripts() != null)
							totalRevisedManuscripts += PresentStatusEicVo.getRevisedManuscripts();
						if (PresentStatusEicVo.getManuscriptsInReReview() != null)
							totalManuscriptsInReReview += PresentStatusEicVo.getManuscriptsInReReview();
						if (PresentStatusEicVo.getManuscriptsInReRevision() != null)
							totalManuscriptsInReRevision += PresentStatusEicVo.getManuscriptsInReRevision();
						if (PresentStatusEicVo.getFinalDecision() != null)
							totalFinalDecision += PresentStatusEicVo.getFinalDecision();
					}
				}
				addPresentStatusForEiCVO.setTotalSubmittedManuscripts(totalSubmittedManuscripts);
				addPresentStatusForEiCVO.setTotalWithAssignedEditors(totalWithAssignedEditors);
				addPresentStatusForEiCVO.setTotalTechnicalModification(totalTechnicalModification);
				addPresentStatusForEiCVO.setTotalReSubmittedManuscripts(totalReSubmittedManuscripts);
				addPresentStatusForEiCVO.setTotalReSubmittedManuscripts(totalReSubmittedManuscripts);
				addPresentStatusForEiCVO.setTotalReviewersInvitedNoResponse(totalReviewersInvitedNoResponse);
				addPresentStatusForEiCVO.setTotalManuscriptsInReview(totalManuscriptsInReview);
				addPresentStatusForEiCVO.setTotalReviewedManuscripts(totalReviewedManuscripts);
				addPresentStatusForEiCVO.setTotalManuscriptsInRevision(totalManuscriptsInRevision);
				addPresentStatusForEiCVO.setTotalRevisedManuscripts(totalRevisedManuscripts);
				addPresentStatusForEiCVO.setTotalManuscriptsInReReview(totalManuscriptsInReReview);
				addPresentStatusForEiCVO.setTotalManuscriptsInReRevision(totalManuscriptsInReRevision);
				addPresentStatusForEiCVO.setTotalFinalDecision(totalFinalDecision);
			}
		}
		return addPresentStatusForEiCVO;
	}

	@Override
	public List<PresentStatusForEiCVO> getPresentStatusData(IDVO id) {
		/*
		 * List<PresentStatusForEiCVO> presentStatusForEiCVOList = new ArrayList();
		 * 
		 * List<ManuScript> manuScriptListDataFetch =
		 * manuScriptRepo.getManuscriptTypeByJournal(id.getJournalId());
		 * 
		 * if (manuScriptListDataFetch != null && !manuScriptListDataFetch.isEmpty()) {
		 * 
		 * for (ManuScript manuscriptTypeJournal : manuScriptListDataFetch) { Integer
		 * totalSubmittedManuscripts = 0; Integer totalWithAssignedEditors = 0; Integer
		 * totalTechnicalModification = 0; Integer totalFinalDecision = 0; Integer
		 * totalReSubmittedManuscripts = 0; Integer totalReviewersInvitedNoResponse = 0;
		 * Integer totalManuscriptsInReview = 0; Integer totalReviewedManuscripts = 0;
		 * Integer acceptForProduction = 0; Integer rejectForProduction = 0; Integer
		 * requireTechnicalModification = 0; Integer requireResubmission = 0; String
		 * manuscriptType = ""; Integer withdrawRequestedByAuthor = 0;
		 * 
		 * PresentStatusForEiCVO presentStatusForEiCVO = new PresentStatusForEiCVO();
		 * List<Integer> manuscriptId = new ArrayList(); List<Integer>
		 * manuscriptIdForReviewed = new ArrayList(); List<ManuScript> manuscriptList =
		 * new ArrayList<>(); List<ManuScript> manuscriptForReview = new ArrayList();
		 * 
		 * if (manuscriptTypeJournal.getManuscriptTypeDetails().getManuscriptType() !=
		 * null &&
		 * !manuscriptTypeJournal.getManuscriptTypeDetails().getManuscriptType().trim()
		 * .equalsIgnoreCase("")) {
		 * 
		 * manuscriptList = manuScriptRepo.getByManuscriptType(id.getJournalId(),
		 * manuscriptTypeJournal.getManuscriptTypeDetails().getManuscriptType());
		 * 
		 * for (ManuScript manuscriptListData : manuscriptList) {
		 * 
		 * if (manuscriptListData != null) {
		 * manuscriptId.add(manuscriptListData.getManuScriptId()); if
		 * (manuscriptListData.getManuscriptStatusId() != null) { if
		 * (manuscriptListData.getManuscriptStatusId().equals(8) ||
		 * manuscriptListData.getManuscriptStatusId().equals(66)) {
		 * manuscriptIdForReviewed.add(manuscriptListData.getManuScriptId());
		 * manuscriptForReview.add(manuscriptListData);
		 * 
		 * } if (manuscriptListData.getManuscriptStatusId().equals(2)) {
		 * totalSubmittedManuscripts++; } else if
		 * (manuscriptListData.getManuscriptStatusId().equals(51) ||
		 * manuscriptListData.getManuscriptStatusId().equals(52) ||
		 * manuscriptListData.getManuscriptStatusId().equals(68) ||
		 * manuscriptListData.getManuscriptStatusId().equals(54) ||
		 * manuscriptListData.getManuscriptStatusId().equals(55)) {
		 * totalFinalDecision++;
		 * 
		 * } else if (manuscriptListData.getManuscriptStatusId().equals(26)) {
		 * totalReSubmittedManuscripts++; } else if
		 * (manuscriptListData.getManuscriptStatusId().equals(50)) {
		 * totalTechnicalModification++; } else if
		 * (manuscriptListData.getManuscriptStatusId().equals(46)) {
		 * requireTechnicalModification++; } else if
		 * (manuscriptListData.getManuscriptStatusId().equals(4)) {
		 * requireResubmission++; } else if
		 * (manuscriptListData.getManuscriptStatusId().equals(3)) {
		 * totalWithAssignedEditors++; } else if
		 * (manuscriptListData.getManuscriptStatusId().equals(58)) {
		 * withdrawRequestedByAuthor++; } } } }
		 * 
		 * 
		 * List<ManuScriptReviewer> getAllDataByjournal = manuScriptReviewerRepo
		 * .getManuscriptByJournalEditor(id.getJournalId(), manuscriptIdForReviewed);
		 * 
		 * List<ManuScriptReviewer> getReviewedData = manuScriptReviewerRepo
		 * .getReviewedManuscriptEditor(id.getJournalId(), manuscriptIdForReviewed);
		 * 
		 * List<ManuScriptReviewer> getUniqueDataByjournal = manuScriptReviewerRepo
		 * .getInReviewManuscriptForEditor(id.getJournalId(), manuscriptIdForReviewed);
		 * 
		 * 
		 * totalReviewersInvitedNoResponse = manuscriptDashboardArticleService
		 * .getAllNoResponseManusriptForDashboardCount(manuscriptForReview,
		 * getAllDataByjournal);
		 * 
		 * 
		 * totalManuscriptsInReview = manuscriptDashboardArticleService
		 * .getTotalInReviewManusriptForDashboardCount(getUniqueDataByjournal);
		 * 
		 * totalReviewedManuscripts = manuscriptDashboardArticleService
		 * .getTotalReviewedManusriptForDashboardCount(manuscriptForReview,
		 * getReviewedData);
		 * 
		 * presentStatusForEiCVO.setTotalSubmittedManuscripts(totalSubmittedManuscripts)
		 * ;
		 * presentStatusForEiCVO.setTotalWithAssignedEditors(totalWithAssignedEditors);
		 * presentStatusForEiCVO.setTotalTechnicalModification(
		 * totalTechnicalModification);
		 * presentStatusForEiCVO.setTotalFinalDecision(totalFinalDecision);
		 * presentStatusForEiCVO.setTotalReSubmittedManuscripts(
		 * totalReSubmittedManuscripts); presentStatusForEiCVO
		 * .setManuscriptType(manuscriptTypeJournal.getManuscriptTypeDetails().
		 * getManuscriptType());
		 * presentStatusForEiCVO.setTotalReviewedManuscripts(totalReviewedManuscripts);
		 * presentStatusForEiCVO.setTotalReviewersInvitedNoResponse(
		 * totalReviewersInvitedNoResponse);
		 * presentStatusForEiCVO.setTotalManuscriptsInReview(totalManuscriptsInReview);
		 * presentStatusForEiCVO.setAcceptForProduction(acceptForProduction);
		 * presentStatusForEiCVO.setRejectForProduction(rejectForProduction);
		 * presentStatusForEiCVO.setRequireResubmission(requireResubmission);
		 * presentStatusForEiCVO.setRequireTechnicalModification(
		 * requireTechnicalModification);
		 * presentStatusForEiCVO.setWithdrawalReqAuthor(withdrawRequestedByAuthor);
		 * presentStatusForEiCVO.setJournalId(id.getJournalId());
		 * presentStatusForEiCVOList.add(presentStatusForEiCVO);
		 * 
		 * } } }
		 */ return null;
	}

	@Override
	public List<PresentStatusForEiCVO> getPresentStatusDataNew(IDVO id) {
		List<PresentStatusForEiCVO> presentStatusForEiCVOList = new ArrayList();
		List<Tuple> reviewerList = manuScriptReviewerRepo.getManuscriptReviewerByJournalId(id.getJournalId());
		List<ReviewerReportDataVO> reviewerReportData = reviewerList.stream()
				.map(t -> new ReviewerReportDataVO(t.get(0, Integer.class), t.get(1, Integer.class),
						t.get(2, Integer.class), t.get(3, Integer.class), Integer.valueOf(t.get(4, Short.class)),
						t.get(5, Date.class)))
				.collect(Collectors.toList());
		List<Tuple> manuScriptListDataFetch = manuScriptRepo.getManuscriptByJournal(id.getJournalId());
		List<ManuscriptReportDataVO> manuscriptReportData = manuScriptListDataFetch.stream()
				.map(t -> new ManuscriptReportDataVO(t.get(0, Integer.class), t.get(1, String.class),
						t.get(2, Integer.class), t.get(3, Integer.class), t.get(4, String.class), t.get(5, Integer.class),
						t.get(6, Integer.class),t.get(7, Date.class)))
				.collect(Collectors.toList());
		Map<String, List<ManuscriptReportDataVO>> manuscriptTypeGroup = manuscriptReportData.stream()
				.collect(Collectors.groupingBy(w -> (w.getManuscriptType() != null ? w.getManuscriptType() : "")));

		if (manuscriptTypeGroup != null && !manuscriptTypeGroup.isEmpty()) {
			manuscriptTypeGroup.forEach((key, manuscriptList) -> {
				Integer totalSubmittedManuscripts = 0;
				Integer totalWithAssignedEditors = 0;
				Integer totalTechnicalModification = 0;
				Integer totalFinalDecision = 0;
				Integer totalReSubmittedManuscripts = 0;
				Integer totalReviewersInvitedNoResponse = 0;
				Integer totalManuscriptsInReview = 0;
				Integer totalReviewedManuscripts = 0;
				Integer acceptForProduction = 0;
				Integer rejectForProduction = 0;
				Integer requireTechnicalModification = 0;
				Integer requireResubmission = 0;
				Integer withdrawRequestedByAuthor = 0;

				PresentStatusForEiCVO presentStatusForEiCVO = new PresentStatusForEiCVO();
				List<Integer> manuscriptId = new ArrayList();
				List<ManuscriptReportDataVO> manuscriptForReview = new ArrayList();
				for (ManuscriptReportDataVO manuscriptListData : manuscriptList) {

					if (manuscriptListData != null) {
						manuscriptId.add(manuscriptListData.getManuscriptId());
						if (manuscriptListData.getManuscriptStatus() != null) {
							if (manuscriptListData.getManuscriptStatus().equals(8)
									|| manuscriptListData.getManuscriptStatus().equals(66)) {
								manuscriptForReview.add(manuscriptListData);
							}
							if (manuscriptListData.getManuscriptStatus().equals(2)) {
								totalSubmittedManuscripts++;
							} else if (manuscriptListData.getManuscriptStatus().equals(51)
									|| manuscriptListData.getManuscriptStatus().equals(52)
									|| manuscriptListData.getManuscriptStatus().equals(68)
									|| manuscriptListData.getManuscriptStatus().equals(72)
									|| manuscriptListData.getManuscriptStatus().equals(73)) {
								totalFinalDecision++;

							} else if (manuscriptListData.getManuscriptStatus().equals(26)) {
								totalReSubmittedManuscripts++;
							} else if (manuscriptListData.getManuscriptStatus().equals(50)) {
								totalTechnicalModification++;
							} else if (manuscriptListData.getManuscriptStatus().equals(46)) {
								requireTechnicalModification++;
							} else if (manuscriptListData.getManuscriptStatus().equals(4)) {
								requireResubmission++;
							} else if (manuscriptListData.getManuscriptStatus().equals(3)) {
								totalWithAssignedEditors++;
							} else if (manuscriptListData.getManuscriptStatus().equals(58)) {
								withdrawRequestedByAuthor++;
							}
						}
					}
				}

				List<ReviewerReportDataVO> getReviewedData = reviewerReportData.stream()
						.filter(s -> s.getIsActive().equals(1)).collect(Collectors.toList());

				List<ReviewerReportDataVO> getUniqueDataByjournal = reviewerReportData.stream()
						.filter(s -> (s.getIsActive().equals(1)
								&& (s.getManuscriptStatus().equals(8) || s.getManuscriptStatus().equals(35)
										|| s.getManuscriptStatus().equals(66))
								&& s.getDueDate().after(new java.util.Date())))
						.collect(Collectors.toList());

				totalReviewersInvitedNoResponse = manuscriptDashboardArticleService
						.getAllNoResponseManusriptForDashboardCountReport(manuscriptForReview, reviewerReportData);

				totalManuscriptsInReview = manuscriptDashboardArticleService
						.getTotalInReviewManusriptForDashboardCountReport(manuscriptForReview, getUniqueDataByjournal);

				totalReviewedManuscripts = manuscriptDashboardArticleService
						.getTotalReviewedManusriptForDashboardCountReport(manuscriptForReview, getReviewedData);

				presentStatusForEiCVO.setTotalSubmittedManuscripts(totalSubmittedManuscripts);
				presentStatusForEiCVO.setTotalWithAssignedEditors(totalWithAssignedEditors);
				presentStatusForEiCVO.setTotalTechnicalModification(totalTechnicalModification);
				presentStatusForEiCVO.setTotalFinalDecision(totalFinalDecision);
				presentStatusForEiCVO.setTotalReSubmittedManuscripts(totalReSubmittedManuscripts);
				presentStatusForEiCVO.setManuscriptType(key);
				presentStatusForEiCVO.setTotalReviewedManuscripts(totalReviewedManuscripts);
				presentStatusForEiCVO.setTotalReviewersInvitedNoResponse(totalReviewersInvitedNoResponse);
				presentStatusForEiCVO.setTotalManuscriptsInReview(totalManuscriptsInReview);
				presentStatusForEiCVO.setAcceptForProduction(acceptForProduction);
				presentStatusForEiCVO.setRejectForProduction(rejectForProduction);
				presentStatusForEiCVO.setRequireResubmission(requireResubmission);
				presentStatusForEiCVO.setRequireTechnicalModification(requireTechnicalModification);
				presentStatusForEiCVO.setWithdrawalReqAuthor(withdrawRequestedByAuthor);
				presentStatusForEiCVO.setJournalId(id.getJournalId());
				presentStatusForEiCVOList.add(presentStatusForEiCVO);

			});
		}
		return presentStatusForEiCVOList;
	}

	@Override
	public List<ArticleReportVo> getArticleReport(ArticleReportVo articleReportVo) {
		List<ArticleReportVo> addgetArticleReport = new ArrayList<>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		String fromFormattedDate = sdf.format(articleReportVo.getFrom());
		sdf = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
		String toFormattedDate = sdf.format(articleReportVo.getTo());

		if (articleReportVo.getFrom() != null && articleReportVo.getTo() != null && articleReportVo.getJournal() != null
				&& !articleReportVo.getJournal().isEmpty()) {
			for (JournalListVO j : articleReportVo.getJournal()) {
				ArticleReportVo add = new ArticleReportVo();
				add.setJournalTitle(j.getJournalTitle());
				add.setJournalCode(j.getJournalCode());
				add.setFrom(articleReportVo.getFrom());
				add.setTo(articleReportVo.getTo());
				Integer k = manuScriptRepo.getTotalSubmissionOnJournalBetweenDate(fromFormattedDate,
						toFormattedDate, j.getJournalId());
				if (k != null) {
					add.setTotalSubmissions(k);
				} else {
					add.setTotalSubmissions(0);
				}
				Integer p = manuScriptRepo.getTotalRejectionOnJournalBetweenDate(fromFormattedDate,
						toFormattedDate, j.getJournalId());
				if (p != null) {
					add.setTotalRejections(p);
				} else {
					add.setTotalRejections(0);
				}

				Integer q = manuScriptRepo.getTotalAcceptanceOnJournalBetweenDate(fromFormattedDate,
						toFormattedDate, j.getJournalId());
				if (q != null) {
					add.setTotalAcceptance(q);
				} else {
					add.setTotalAcceptance(0);
				}
//				Integer e = manuScriptRepo.getTotalArticleInPeerReviewOnJournalBetweenDate(j.getJournalId());
				Integer e = manuScriptRepo.getTotalArticleInPeerReviewOnJournalBetweenDate(fromFormattedDate,
						toFormattedDate, j.getJournalId());
				if (e != null) {
					add.setTotalArticleInPeerReview(e);
				} else {
					add.setTotalArticleInPeerReview(0);
				}
				addgetArticleReport.add(add);
			}
		}
		return addgetArticleReport;
	}

	@Override
	public List<PresentStatusForEiCVO> getPresentStatusDataForEditor(EditorPresentDataVO edvo) {

		List<PresentStatusForEiCVO> presentStatusForEiCVOList = new ArrayList();

		List<ManuScript> manuScriptListDataFetch = manuScriptRepo.getManuscriptTypeByJournal(edvo.getJournalId());
		// get All manuscript id associated with given editor

		List<ManuscriptEditor> manuscriptEditorById = manuscriptEditorRepo.getManuscriptEditorById(edvo.getEditorId());

		List<Integer> manuscriptIdsAssosiatedWithEditor = new ArrayList<>();
		for (ManuscriptEditor manuscriptEditor : manuscriptEditorById) {
			if (manuscriptEditor != null && manuscriptEditor.getJournalId() != null
					&& manuscriptEditor.getManuscriptId() != null && manuscriptEditor.getIsActive() == 1) {
				manuscriptIdsAssosiatedWithEditor.add(manuscriptEditor.getManuscriptId());
			}
		}

		if (manuScriptListDataFetch != null && !manuScriptListDataFetch.isEmpty()) {
			for (ManuScript manuscriptTypeJournal : manuScriptListDataFetch) {

				PresentStatusForEiCVO presentStatusForEiCVO = new PresentStatusForEiCVO();
//				List<Integer> manuscriptId = new ArrayList();
				List<ManuScript> manuscriptIdForReviewed = new ArrayList();
				List<ManuScript> manuscriptList = new ArrayList<>();
				if (manuscriptTypeJournal.getManuscriptTypeDetails().getManuscriptType() != null
						&& !manuscriptTypeJournal.getManuscriptTypeDetails().getManuscriptType().trim()
								.equalsIgnoreCase("")) {

					manuscriptList = manuScriptRepo.getByManuscriptType(edvo.getJournalId(),
							manuscriptTypeJournal.getManuscriptTypeDetails().getManuscriptType());

					/*
					 * for (ManuScript manuscriptListData : manuscriptList) {
					 * manuscriptId.add(manuscriptListData.getManuScriptId()); }
					 */

					for (ManuScript manuscriptListData : manuscriptList) {

						if (manuscriptIdsAssosiatedWithEditor.contains(manuscriptListData.getManuScriptId())) {
							if (manuscriptListData.getManuscriptStatusId().equals(8)
									|| manuscriptListData.getManuscriptStatusId().equals(66)) {
								manuscriptIdForReviewed.add(manuscriptListData);
							}
						}
					}

					Integer totalSubmittedManuscripts = 0;
					Integer totalWithAssignedEditors = 0;
					Integer totalTechnicalModification = 0;
					Integer totalFinalDecision = 0;
					Integer totalReSubmittedManuscripts = 0;
					Integer totalReviewersInvitedNoResponse = 0;
					Integer totalManuscriptsInReview = 0;
					Integer totalReviewedManuscripts = 0;
					Integer acceptForProduction = 0;
					Integer rejectForProduction = 0;
					Integer requireTechnicalModification = 0;
					Integer requireResubmission = 0;
					String manuscriptType = "";
					for (ManuScript manuscriptListData : manuscriptList) {
						if (manuscriptIdsAssosiatedWithEditor.contains(manuscriptListData.getManuScriptId())) {
							if (manuscriptListData.getManuscriptStatusId() == 2) {
								totalSubmittedManuscripts++;
							} else if (manuscriptListData.getManuscriptStatusId() == 51
									|| manuscriptListData.getManuscriptStatusId() == 52
									|| manuscriptListData.getManuscriptStatusId() == 54
									|| manuscriptListData.getManuscriptStatusId() == 55) {
								totalFinalDecision++;
							} else if (manuscriptListData.getManuscriptStatusId() == 33) {
								acceptForProduction++;
							} else if (manuscriptListData.getManuscriptStatusId() == 30) {
								rejectForProduction++;
							} else if (manuscriptListData.getManuscriptStatusId() == 26) {
								totalReSubmittedManuscripts++;
							} else if (manuscriptListData.getManuscriptStatusId() == 50) {
								totalTechnicalModification++;
							} else if (manuscriptListData.getManuscriptStatusId() == 46) {
								requireTechnicalModification++;
							} else if (manuscriptListData.getManuscriptStatusId() == 4) {
								requireResubmission++;
							} else if (manuscriptListData.getManuscriptStatusId() == 3) {
								totalWithAssignedEditors++;
							}
						}
					}
//					List<ManuscriptEditor> editorData = manuscriptEditorRepo
//							.getAssignedEditorListByManuscriptListByStatus(manuscriptId);

					List<ManuscriptListVO> reviewedManuscript = manuScriptReviewerService
							.getReviewedManuscriptByManuscriptId(manuscriptIdForReviewed);
					List<ManuscriptListVO> noResponse = manuScriptReviewerService
							.getNoResponseManuscriptByManuscriptId(manuscriptIdForReviewed);
					List<ManuscriptListVO> inReview = manuScriptReviewerService
							.getInReviewManuscriptByManuscriptId(manuscriptIdForReviewed);
//					if (editorData != null && !editorData.isEmpty()) {
//						totalWithAssignedEditors = editorData.size();
//					}
					if (reviewedManuscript != null && !reviewedManuscript.isEmpty()) {
						totalReviewedManuscripts = reviewedManuscript.size();
					}
					if (noResponse != null && !noResponse.isEmpty()) {
						totalReviewersInvitedNoResponse = noResponse.size();
					}
					if (inReview != null && !inReview.isEmpty()) {
						totalManuscriptsInReview = inReview.size();
					}

					presentStatusForEiCVO.setTotalSubmittedManuscripts(totalSubmittedManuscripts);
					presentStatusForEiCVO.setTotalWithAssignedEditors(totalWithAssignedEditors);
					presentStatusForEiCVO.setTotalTechnicalModification(totalTechnicalModification);
					presentStatusForEiCVO.setTotalFinalDecision(totalFinalDecision);
					presentStatusForEiCVO.setTotalReSubmittedManuscripts(totalReSubmittedManuscripts);
					presentStatusForEiCVO
							.setManuscriptType(manuscriptTypeJournal.getManuscriptTypeDetails().getManuscriptType());
					presentStatusForEiCVO.setTotalReviewedManuscripts(totalReviewedManuscripts);
					presentStatusForEiCVO.setTotalReviewersInvitedNoResponse(totalReviewersInvitedNoResponse);
					presentStatusForEiCVO.setTotalManuscriptsInReview(totalManuscriptsInReview);
					presentStatusForEiCVO.setAcceptForProduction(acceptForProduction);
					presentStatusForEiCVO.setRejectForProduction(rejectForProduction);
					presentStatusForEiCVO.setRequireResubmission(requireResubmission);
					presentStatusForEiCVO.setRequireTechnicalModification(requireTechnicalModification);
					presentStatusForEiCVOList.add(presentStatusForEiCVO);

				}
			}
		}
		return presentStatusForEiCVOList;

	}

}
