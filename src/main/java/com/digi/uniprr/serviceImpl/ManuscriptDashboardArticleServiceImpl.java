package com.digi.uniprr.serviceImpl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.Tuple;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.digi.uniprr.VO.AdminJournalVo;
import com.digi.uniprr.VO.AuthoritiesHistoryVo;
import com.digi.uniprr.VO.EmailTriggerVO;
import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.VO.ManagingEditorVO;
import com.digi.uniprr.VO.ManuscriptListVO;
import com.digi.uniprr.VO.ManuscriptReportDataVO;
import com.digi.uniprr.VO.ReviewerDetailsVO;
import com.digi.uniprr.VO.ReviewerReportDataVO;
import com.digi.uniprr.VO.StatusMasterVO;
import com.digi.uniprr.VO.UserVO;
import com.digi.uniprr.model.AuthoritiesHistory;
import com.digi.uniprr.model.Journal;
import com.digi.uniprr.model.ManuScript;
import com.digi.uniprr.model.ManuScriptForDashboard;
import com.digi.uniprr.model.ManuScriptReviewer;
import com.digi.uniprr.model.ManuscriptDashboardList;
import com.digi.uniprr.model.ManuscriptEditor;
import com.digi.uniprr.model.StatusMaster;
import com.digi.uniprr.model.User;
import com.digi.uniprr.repository.AuthoritiesHistoryRepo;
import com.digi.uniprr.repository.CountriesRepo;
import com.digi.uniprr.repository.JournalManuscriptTypeChargeRepo;
import com.digi.uniprr.repository.JournalPoliciesRepo;
import com.digi.uniprr.repository.JournalsRepo;
import com.digi.uniprr.repository.ManscriptScoreRepo;
import com.digi.uniprr.repository.ManuScriptAwatingPaymentRepo;
import com.digi.uniprr.repository.ManuScriptCoAuthorRepo;
import com.digi.uniprr.repository.ManuScriptFileRepo;
import com.digi.uniprr.repository.ManuScriptForDashboardRepo;
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
import com.digi.uniprr.service.AllCommentService;
import com.digi.uniprr.service.EmailTriggerService;
import com.digi.uniprr.service.ManuScriptReviewerService;
import com.digi.uniprr.service.ManuscriptDashboardArticleService;
import com.digi.uniprr.service.ManuscriptEditorService;
import com.digi.uniprr.utils.SendMailUtils;

@Service
@Transactional
public class ManuscriptDashboardArticleServiceImpl implements ManuscriptDashboardArticleService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	JournalsRepo journalsRepo;

	@Autowired
	StatusMasterRepo statusMasterRepo;

	@Autowired
	ManuScriptRepo manuScriptRepo;

	@Autowired
	ManuScriptForDashboardRepo manuScriptForDashboardRepo;

	@Autowired
	AuthoritiesHistoryRepo authoritiesHistoryRepo;

	@Autowired
	ManuscriptEditorCommentsRepo manuscriptEditorCommentsRepo;

	@Autowired
	ManuScriptReviewerRepo manuScriptReviewerRepo;

	@Autowired
	EmailTriggerService emailTriggerService;

	@Autowired
	AllCommentService allCommentService;

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
	public List<ManuscriptDashboardList> getManuscriptByStatus(ManuscriptDashboardList manuscriptVO) {
		List<ManuscriptDashboardList> newList = new ArrayList<>();
		if (manuscriptVO.getSortBy().equalsIgnoreCase("assignDate")) {
		    
		    manuscriptVO.setSortBy("manuscriptCode");
		
		}

		
		Pageable paging = PageRequest.of(manuscriptVO.getPageNo(), manuscriptVO.getLimit(),
				Sort.by(Sort.Direction.DESC, manuscriptVO.getSortBy()));
		if (manuscriptVO.getSort().equalsIgnoreCase("ASC")) {
			paging = PageRequest.of(manuscriptVO.getPageNo(), manuscriptVO.getLimit(),
					Sort.by(Sort.Direction.ASC, manuscriptVO.getSortBy()));
		}

		// for (Integer mStatus : manuscriptVO.getManuscriptStatusId()) {
		List<ManuScriptForDashboard> list = new ArrayList<>();
		if (manuscriptVO.getUserId() != null && manuscriptVO.getRoleId() != null) {
			if (manuscriptVO != null && manuscriptVO.getRoleId() != null && manuscriptVO.getSearch() != null
					&& manuscriptVO.getSearch() != "") {
				list = manuScriptForDashboardRepo.getManuscriptByJournalAndRoleSearch(
						manuscriptVO.getManuscriptStatusId(), manuscriptVO.getJournalId(), manuscriptVO.getUserId(),
						manuscriptVO.getRoleId(), paging, manuscriptVO.getSearch());
			} else {

				list = manuScriptForDashboardRepo.getManuscriptByJournalAndRole(manuscriptVO.getManuscriptStatusId(),
						manuscriptVO.getJournalId(), manuscriptVO.getUserId(), manuscriptVO.getRoleId(), paging);

			}
		} else {

			if (manuscriptVO != null && manuscriptVO.getRoleId() != null && manuscriptVO.getSearch() != null
					&& manuscriptVO.getSearch() != "") {
				list = manuScriptForDashboardRepo.getManuscriptByJournalAndRolePageSearch(
						manuscriptVO.getManuscriptStatusId(), manuscriptVO.getJournalId(), paging,
						manuscriptVO.getSearch());
			} else {

				list = manuScriptForDashboardRepo.getManuscriptByJournalAndRolePage(
						manuscriptVO.getManuscriptStatusId(), manuscriptVO.getJournalId(), paging);

			}

		}

		if (list != null) {
			for (ManuScriptForDashboard data : list) {
				// get data
				try {
					User editorNameData = userRepo.getByID(data.getAssignedUser());
					ManuscriptDashboardList manuscriptListVO = new ManuscriptDashboardList();
					if (data.getManuscriptStatusId().equals(2)) {
						int userCount = userJournalRepo.getCountOfUser(data.getJournalId(), 2);
						if (userCount > 1) {
							int editorCount = manuscriptEditorRepo.getEditorCount(data.getManuScriptId());
							Integer revCount = manuScriptReviewerRepo.getReviewerCount(data.getManuScriptId(),
									data.getJournalId());
							if (editorCount > 0 && revCount > 0) {
								manuscriptListVO.setEditorName(
										editorNameData.getFirstName() + " " + editorNameData.getLastName());
							} else {
								manuscriptListVO.setEditorName("All(" + userCount + ")");
							}

						} else if (editorNameData != null) {
							manuscriptListVO
									.setEditorName(editorNameData.getFirstName() + " " + editorNameData.getLastName());

						}
					} else {
						if (editorNameData != null) {
							manuscriptListVO
									.setEditorName(editorNameData.getFirstName() + " " + editorNameData.getLastName());
						}
					}

					manuscriptListVO.setJournalId(data.getJournalId());
					manuscriptListVO.setManuscripCode(data.getManuscriptCode());

					manuscriptListVO.setManuscripCode(data.getManuscriptCode());
					manuscriptListVO.setManuscriptAuthorId(data.getAuthorId());
					manuscriptListVO.setManuscripTitle(data.getManuscriptTitle());
					manuscriptListVO.setManuScriptId(data.getManuScriptId());
					manuscriptListVO.setManuscriptVersion(data.getManuscriptVersion());
					manuscriptListVO.setSubmissionDate(data.getSubmissionDate());
					manuscriptListVO.setAssignDate(data.getUpdatedOn());
					manuscriptListVO.setCommentaryId(data.getCommentaryId());
					//////////////////////////////////////////////////////////////////////////////
					manuscriptListVO.setEICFolder("SubmittedManuscript");
					if (manuScriptRepo.getEICFolder(data.getManuScriptId()) == null) {
						manuScriptRepo.updateEICFolder(data.getManuScriptId(), "SubmittedManuscript");
					}
					if (manuScriptRepo.getEICFolder(data.getManuScriptId()) != null && !manuScriptRepo
							.getEICFolder(data.getManuScriptId()).equalsIgnoreCase("SubmittedManuscript")) {
						manuScriptRepo.updateEICFolder(data.getManuScriptId(), "SubmittedManuscript");
					}
					///////////////////////////////////////////////////////////////////////////////
					// Adding due date
					manuscriptListVO.setDueDate(data.getDueDate());
					manuscriptListVO.setAuthorName(data.getUser().getFirstName() + " " + data.getUser().getLastName());
					if (data.getManuscriptTypeDetails() != null
							&& data.getManuscriptTypeDetails().getManuscriptType() != null
							&& !data.getManuscriptTypeDetails().getManuscriptType().isEmpty()) {
						manuscriptListVO.setManuScriptType(data.getManuscriptTypeDetails().getManuscriptType());
					}
					newList.add(manuscriptListVO);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
		// }
		return newList;
	}

	@Override
	public List<ManuscriptDashboardList> getManuscriptByStatusReport(ManuscriptDashboardList manuscriptVO) {
		List<ManuscriptDashboardList> newList = new ArrayList<>();
		if (manuscriptVO.getSortBy().equalsIgnoreCase("manuscriptType")) {
			manuscriptVO.setSortBy("manuscriptTypeDetails.manuscriptType");
		}

		if (manuscriptVO.getSortBy().equalsIgnoreCase("authorName")) {
			manuscriptVO.setSortBy("user.firstName");
		}

		Pageable paging;
		if (manuscriptVO.getSort().equalsIgnoreCase("asc")) {
			paging = PageRequest.of(manuscriptVO.getPageNo(), manuscriptVO.getLimit(),
					Sort.by(Sort.Direction.ASC, manuscriptVO.getSortBy()));
		} else {
			paging = PageRequest.of(manuscriptVO.getPageNo(), manuscriptVO.getLimit(),
					Sort.by(Sort.Direction.DESC, manuscriptVO.getSortBy()));
		}
		List<Integer> accepted = Arrays.asList(33, 57, 61, 62, 65, 69, 70);
		List<Integer> published = Arrays.asList(59);
		List<Integer> wip = Arrays.asList(33, 57, 61, 62, 65);
		List<Integer> rejected = Arrays.asList(30);
		List<Integer> withdrawn = Arrays.asList(16);

		List<ManuScriptForDashboard> list = new ArrayList<>();
		if (manuscriptVO.getReportType().equals("accepted")) {

			if (manuscriptVO != null && manuscriptVO.getSearch() != null && manuscriptVO.getSearch() != "") {
				list = manuScriptForDashboardRepo.getManuscriptByJournalAndRolePageSearchReportAccepted(accepted,
						manuscriptVO.getJournalId(), paging, manuscriptVO.getSearch());
			} else {

				list = manuScriptForDashboardRepo.getManuscriptByJournalAndRolePageReportAccepted(accepted,
						manuscriptVO.getJournalId(), paging);

			}

		} else if (manuscriptVO.getReportType().equals("published")) {

			if (manuscriptVO != null && manuscriptVO.getSearch() != null && manuscriptVO.getSearch() != "") {
				list = manuScriptForDashboardRepo.getManuscriptByJournalAndRolePageSearchReportPublished(published,
						manuscriptVO.getJournalId(), paging, manuscriptVO.getSearch());
			} else {

				list = manuScriptForDashboardRepo.getManuscriptByJournalAndRolePageReportPublished(published,
						manuscriptVO.getJournalId(), paging);

			}

		} else if (manuscriptVO.getReportType().equals("wip")) {

			if (manuscriptVO != null && manuscriptVO.getSearch() != null && manuscriptVO.getSearch() != "") {
				list = manuScriptForDashboardRepo.getManuscriptByJournalAndRolePageSearchReportWip(wip,
						manuscriptVO.getJournalId(), paging, manuscriptVO.getSearch());
			} else {

				list = manuScriptForDashboardRepo.getManuscriptByJournalAndRolePageReportWip(wip,
						manuscriptVO.getJournalId(), paging);

			}

		} else if (manuscriptVO.getReportType().equals("rejected")) {

			if (manuscriptVO != null && manuscriptVO.getSearch() != null && manuscriptVO.getSearch() != "") {
				list = manuScriptForDashboardRepo.getManuscriptByJournalAndRolePageSearchReportWip(rejected,
						manuscriptVO.getJournalId(), paging, manuscriptVO.getSearch());
			} else {

				list = manuScriptForDashboardRepo.getManuscriptByJournalAndRolePageReportWip(rejected,
						manuscriptVO.getJournalId(), paging);

			}

		} else if (manuscriptVO.getReportType().equals("withdrawn")) {

			if (manuscriptVO != null && manuscriptVO.getSearch() != null && manuscriptVO.getSearch() != "") {
				list = manuScriptForDashboardRepo.getManuscriptByJournalAndRolePageSearchReportWithdrawn(withdrawn,
						manuscriptVO.getJournalId(), paging, manuscriptVO.getSearch());
			} else {

				list = manuScriptForDashboardRepo.getManuscriptByJournalAndRolePageReportWithdrawn(withdrawn,
						manuscriptVO.getJournalId(), paging);

			}
			
		}

		if (list != null) {
			for (ManuScriptForDashboard data : list) {
				// get data
				try {
					User editorNameData = userRepo.getByID(data.getAssignedUser());
					ManuscriptDashboardList manuscriptListVO = new ManuscriptDashboardList();
					if (data.getManuscriptStatusId().equals(2)) {
						int userCount = userJournalRepo.getCountOfUser(data.getJournalId(), 2);
						if (userCount > 1) {
							int editorCount = manuscriptEditorRepo.getEditorCount(data.getManuScriptId());
							if (editorCount > 0) {
								manuscriptListVO.setEditorName(
										editorNameData.getFirstName() + " " + editorNameData.getLastName());
							} else {
								manuscriptListVO.setEditorName("All(" + userCount + ")");
							}

						} else if (editorNameData != null) {
							manuscriptListVO
									.setEditorName(editorNameData.getFirstName() + " " + editorNameData.getLastName());

						}
					} else {
						if (editorNameData != null) {
							manuscriptListVO
									.setEditorName(editorNameData.getFirstName() + " " + editorNameData.getLastName());
						}
					}

					manuscriptListVO.setJournalId(data.getJournalId());
					manuscriptListVO.setCommentForProduction(data.getCommentForProduction());
					manuscriptListVO.setManuscripCode(data.getManuscriptCode());
					manuscriptListVO.setProductionDate(data.getProductionDate());
					manuscriptListVO.setManuStatusCode(data.getStatusMaster().getStatusValue());
					manuscriptListVO.setManuscriptAuthorId(data.getAuthorId());
					manuscriptListVO.setManuscripTitle(data.getManuscriptTitle());
					manuscriptListVO.setManuScriptId(data.getManuScriptId());
					manuscriptListVO.setManuscriptVersion(data.getManuscriptVersion());
					manuscriptListVO.setSubmissionDate(data.getSubmissionDate());
					manuscriptListVO.setPublishingDate(data.getPublishingDate());
					manuscriptListVO.setIssueName(data.getIssueName());
					manuscriptListVO.setManuscriptStatusId(
							Stream.of(data.getManuscriptStatusId()).collect(Collectors.toList()));
					if (data.getManuscriptStatusId() == 30) {
						log.debug(data.getManuScriptId() + ": " + data.getManuscriptStatusId());

						List<AuthoritiesHistory> as = authoritiesHistoryRepo.getAuthoritiesHistoryByStatusIdMultiple(
								data.getManuScriptId(), data.getManuscriptStatusId());
						if (!as.isEmpty() && as.get(0).getPreviousUserId() != null) {
							User user1 = userRepo.getOne(as.get(0).getPreviousUserId());
							manuscriptListVO.setRejectedBy(user1.getFirstName() + ' ' + user1.getLastName());
							manuscriptListVO.setCurrentUserId(user1.getId());
							manuscriptListVO.setPreviousUserId(user1.getId());
						}
						if (!as.isEmpty() && as.get(0).getPreviousUserRole() != null) {
							manuscriptListVO.setCurrentUserRole(as.get(0).getPreviousUserRole());
							manuscriptListVO.setPreviousUserRole(as.get(0).getPreviousUserRole());

						}
					}

					manuscriptListVO.setManuscriptStatusBeforeReject(data.getManuscriptStatusBeforeReject());
					manuscriptListVO.setRejectedDate(data.getRejectedDate());
					manuscriptListVO.setReviewerFileName(data.getRejectedFileName());
					manuscriptListVO.setReviewerFilePath(data.getRejectedFilePath());
					manuscriptListVO.setWithdrawnDate(data.getWithdrawnDate());
					manuscriptListVO.setManuscriptStatusBeforeWithdrawn(data.getManuscriptStatusBeforeWithdrawn());
					manuscriptListVO.setWithdrawnRequestDate(data.getWithdrawnRequestDate());
					manuscriptListVO.setWithdrawalComment(data.getWithdrawalComment());
					// Adding due date
					manuscriptListVO.setDueDate(data.getDueDate());
					manuscriptListVO.setAuthorName(data.getUser().getFirstName() + " " + data.getUser().getLastName());
					if (data.getManuscriptTypeDetails().getManuscriptType() != null
							&& !data.getManuscriptTypeDetails().getManuscriptType().isEmpty()) {
						manuscriptListVO.setManuScriptType(data.getManuscriptTypeDetails().getManuscriptType());
					}
					
					if (manuscriptVO.getReportType().equals("withdrawn")) {
						
						if(manuscriptListVO.getManuscriptStatusBeforeWithdrawn() != null) {
							// Option 1
							StatusMaster sm = statusMasterRepo.getStatusMaster(manuscriptListVO.getManuscriptStatusBeforeWithdrawn());
							if(sm != null)
								manuscriptListVO.setManWidrawnStatus(sm.getStatusValueForArticleLifeCycle());
							
						}else {
							//Option 2 via AuthoritiesHistory
							List<AuthoritiesHistory> j = authoritiesHistoryRepo.getAuthoritiesHistory(data.getManuScriptId(), data.getJournalId());
							if(j.size() > 1) {
								for (int i = 0; i < j.size(); i++) {
									if(j.get(i).getStatusMaster().getStatusValueForArticleLifeCycle().equalsIgnoreCase("Manuscript Withdrawn")) {
										manuscriptListVO.setManWidrawnStatus(j.get(i-1).getStatusMaster().getStatusValueForArticleLifeCycle());
										break;
									}
								}
							}
						}

					}
					
					newList.add(manuscriptListVO);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
		// }
		return newList;
	}

	@Override
	public int getTotalSbmittedManuscriptForDashboard(ManuscriptDashboardList manuscriptVO) {
		Integer totalSbmittedManuscriptForDashboard = 0;
		if (manuscriptVO.getUserId() != null && manuscriptVO.getRoleId() != null) {
			totalSbmittedManuscriptForDashboard = manuScriptForDashboardRepo.getTotalResbmittedManuscriptForDashboard(
					manuscriptVO.getManuscriptStatusId(), manuscriptVO.getJournalId(), manuscriptVO.getUserId(),
					manuscriptVO.getRoleId());
		} else {
			totalSbmittedManuscriptForDashboard = manuScriptForDashboardRepo.getTotalSbmittedManuscriptForDashboard(
					manuscriptVO.getManuscriptStatusId(), manuscriptVO.getJournalId());
		}
		return totalSbmittedManuscriptForDashboard;
	}

	@Override
	public int getTotalFinalSbmittedManuscriptForDashboard(ManuscriptDashboardList manuscriptVO) {
		Integer totalSbmittedManuscriptForDashboard = 0;
		if (manuscriptVO.getUserId() != null && manuscriptVO.getRoleId() != null) {
			totalSbmittedManuscriptForDashboard = manuScriptForDashboardRepo.getTotalSbmittedManuscriptForDashboardRole(
					manuscriptVO.getManuscriptStatusId(), manuscriptVO.getJournalId(), manuscriptVO.getRoleId());
		} else {

			totalSbmittedManuscriptForDashboard = manuScriptForDashboardRepo.getTotalSbmittedManuscriptForDashboard(
					manuscriptVO.getManuscriptStatusId(), manuscriptVO.getJournalId());
		}

		return totalSbmittedManuscriptForDashboard;
	}
	
	public int getTotalRevisedManuscriptForDashboard(ManuscriptDashboardList manuscriptVO) {
		Integer TotalRevisedManuscriptForDashboard = 0;
		if (manuscriptVO.getUserId() != null && manuscriptVO.getRoleId() != null) {
			TotalRevisedManuscriptForDashboard = manuScriptForDashboardRepo.getTotalSbmittedManuscriptForDashboardRole(
					manuscriptVO.getManuscriptStatusId(), manuscriptVO.getJournalId(), manuscriptVO.getRoleId());
		} else {

			TotalRevisedManuscriptForDashboard = manuScriptForDashboardRepo.getTotalSbmittedManuscriptForDashboard(
					manuscriptVO.getManuscriptStatusId(), manuscriptVO.getJournalId());
		}

		return TotalRevisedManuscriptForDashboard;
	}

	@Override
	public int getTotalSbmittedManuscriptForDashboardEditor(ManuscriptDashboardList manuscriptVO) {
		Integer totalSbmittedManuscriptForDashboard = 0;
		if (manuscriptVO.getUserId() != null && manuscriptVO.getRoleId() != null) {

			totalSbmittedManuscriptForDashboard = manuScriptForDashboardRepo
					.getTotalResbmittedManuscriptForDashboardEditor(manuscriptVO.getManuscriptStatusId(),
							manuscriptVO.getUserId(), manuscriptVO.getRoleId(), manuscriptVO.getJournalId());
		} else {
			totalSbmittedManuscriptForDashboard = manuScriptForDashboardRepo.getTotalSbmittedManuscriptForDashboard(
					manuscriptVO.getManuscriptStatusId(), manuscriptVO.getJournalId());
		}
		return totalSbmittedManuscriptForDashboard;
	}

	@Override
	public int getTotalSbmittedManuscriptForDashboardSearch(ManuscriptDashboardList manuscriptVO) {

		Integer totalSbmittedManuscriptForDashboardSearch = 0;
		if (manuscriptVO.getUserId() != null && manuscriptVO.getRoleId() != null) {
			totalSbmittedManuscriptForDashboardSearch = manuScriptForDashboardRepo
					.getTotalResbmittedManuscriptForDashboardSearch(manuscriptVO.getManuscriptStatusId(),
							manuscriptVO.getJournalId(), manuscriptVO.getUserId(), manuscriptVO.getRoleId(),
							manuscriptVO.getSearch());
		} else {
			totalSbmittedManuscriptForDashboardSearch = manuScriptForDashboardRepo
					.getTotalSbmittedManuscriptForDashboardSearch(manuscriptVO.getManuscriptStatusId(),
							manuscriptVO.getJournalId(), manuscriptVO.getSearch());
		}
		return totalSbmittedManuscriptForDashboardSearch;
	}

	@Override
	public int getTotalSbmittedManuscriptForDashboarEditordSearch(ManuscriptDashboardList manuscriptVO) {

		Integer totalSbmittedManuscriptForDashboardSearch = 0;
		if (manuscriptVO.getUserId() != null && manuscriptVO.getRoleId() != null) {
			totalSbmittedManuscriptForDashboardSearch = manuScriptForDashboardRepo
					.getTotalResbmittedManuscriptForDashboardEditorSearch(manuscriptVO.getManuscriptStatusId(),
							manuscriptVO.getJournalId(), manuscriptVO.getUserId(), manuscriptVO.getRoleId(),
							manuscriptVO.getSearch());
		} else {
			totalSbmittedManuscriptForDashboardSearch = manuScriptForDashboardRepo
					.getTotalSbmittedManuscriptForDashboardSearch(manuscriptVO.getManuscriptStatusId(),
							manuscriptVO.getJournalId(), manuscriptVO.getSearch());
		}
		return totalSbmittedManuscriptForDashboardSearch;
	}

	@Override
	public List<ManuscriptDashboardList> getTotalManuscriptByStatus(ManuscriptDashboardList manuscriptVO) {

		List<ManuscriptDashboardList> newList = new ArrayList<>();
		if (manuscriptVO.getSortBy().equalsIgnoreCase("assignDate")) {
			manuscriptVO.setSortBy("updatedOn");
		}
		Pageable paging = PageRequest.of(manuscriptVO.getPageNo(), manuscriptVO.getLimit(),
				Sort.by(Sort.Direction.DESC, manuscriptVO.getSortBy()));
		if (manuscriptVO.getSort().equalsIgnoreCase("ASC")) {
			paging = PageRequest.of(manuscriptVO.getPageNo(), manuscriptVO.getLimit(),
					Sort.by(Sort.Direction.ASC, manuscriptVO.getSortBy()));
		}
		// for (Integer mStatus : manuscriptVO.getManuscriptStatusId()) {
		List<ManuScriptForDashboard> list = new ArrayList<>();
		if (manuscriptVO != null && manuscriptVO.getStatusCode().equalsIgnoreCase("All")) {
			if (manuscriptVO != null && manuscriptVO.getSearch() != null && manuscriptVO.getSearch() != "") {
				list = manuScriptForDashboardRepo.getManuscriptByStatusPageSearch(manuscriptVO.getManuscriptStatusId(),
						manuscriptVO.getJournalId(), paging, manuscriptVO.getSearch());
			} else {

				list = manuScriptForDashboardRepo.getManuscriptByStatusPage(manuscriptVO.getManuscriptStatusId(),
						manuscriptVO.getJournalId(), paging);

			}
		} else {
			list = manuScriptForDashboardRepo.getManuscriptByStatusCodeEICPage(manuscriptVO.getManuscriptStatusId(),
					manuscriptVO.getJournalId(), manuscriptVO.getStatusCode(), paging);

		}

		if (list != null) {
			for (ManuScriptForDashboard data : list) {
				// get data
				try {
					User editorNameData = userRepo.getByID(data.getAssignedUser());
					ManuscriptDashboardList manuscriptListVO = new ManuscriptDashboardList();
					if (data.getManuscriptStatusId().equals(2)
							|| data.getAssignedUserRole() == 2 && data.getManuscriptStatusId() == 58) {
						int userCount = userJournalRepo.getCountOfUser(data.getJournalId(), 2);
						if (userCount > 1) {
							int editorCount = manuscriptEditorRepo.getEditorCount(data.getManuScriptId());
							if (editorCount > 0) {
								manuscriptListVO.setEditorName(
										editorNameData.getFirstName() + " " + editorNameData.getLastName());
							} else {
								manuscriptListVO.setEditorName("All(" + userCount + ")");
							}

						} else if (editorNameData != null) {
							manuscriptListVO
									.setEditorName(editorNameData.getFirstName() + " " + editorNameData.getLastName());

						}
					} else {
						if (editorNameData != null) {
							manuscriptListVO
									.setEditorName(editorNameData.getFirstName() + " " + editorNameData.getLastName());
						}
					}

					manuscriptListVO.setJournalId(data.getJournalId());
					manuscriptListVO.setManuscripCode(data.getManuscriptCode());

					manuscriptListVO.setManuscripCode(data.getManuscriptCode());
					manuscriptListVO.setManuscriptAuthorId(data.getAuthorId());
					manuscriptListVO.setManuscripTitle(data.getManuscriptTitle());
					manuscriptListVO.setManuScriptId(data.getManuScriptId());
					manuscriptListVO.setManuscriptVersion(data.getManuscriptVersion());
					manuscriptListVO.setSubmissionDate(data.getSubmissionDate());
					manuscriptListVO.setStatusCode(data.getStatusMaster().getStatusValue());
					manuscriptListVO.setManuStatusId(data.getManuscriptStatusId());
					AuthoritiesHistory author1 = authoritiesHistoryRepo
							.getAuthoritiesHistoryForAssignDate(data.getManuScriptId(), data.getJournalId());
					manuscriptListVO.setAssignDate(author1 == null ? null : author1.getCreatedAt());
//					manuscriptListVO.setAssignDate(data.getUpdatedOn());
					//////////////////////////////////////////////////////////////////////////////
					manuscriptListVO.setEICFolder("TotalManusript");
					if (manuScriptRepo.getEICFolder(data.getManuScriptId()) == null) {
						manuScriptRepo.updateEICFolder(data.getManuScriptId(), "TotalManusript");
					}
					if (manuScriptRepo.getEICFolder(data.getManuScriptId()) != null
							&& !manuScriptRepo.getEICFolder(data.getManuScriptId()).equalsIgnoreCase("TotalManusript")
							&& !manuScriptRepo.getEICFolder(data.getManuScriptId())
									.equalsIgnoreCase("ReturnedByEditiriolStaff")
							&& !manuScriptRepo.getEICFolder(data.getManuScriptId())
									.equalsIgnoreCase("ManuscriptsInReview")
							&& !manuScriptRepo.getEICFolder(data.getManuScriptId())
									.equalsIgnoreCase("ReviewedManuscript")
							&& !manuScriptRepo.getEICFolder(data.getManuScriptId())
									.equalsIgnoreCase("RevisedManuscript")
							&& !manuScriptRepo.getEICFolder(data.getManuScriptId())
									.equalsIgnoreCase("NoResponseManuscript")
							&& !manuScriptRepo.getEICFolder(data.getManuScriptId())
									.equalsIgnoreCase("SubmittedManuscript")
							&& !manuScriptRepo.getEICFolder(data.getManuScriptId())
									.equalsIgnoreCase("ReSubmittedManuscript")) {
						manuScriptRepo.updateEICFolder(data.getManuScriptId(), "TotalManusript");
					}
					///////////////////////////////////////////////////////////////////////////////
					AuthoritiesHistory av = authoritiesHistoryRepo
							.getPreAuthoritiesHistoryByStatusId(data.getManuScriptId(), data.getJournalId());
					if(av!=null)
					{
						manuscriptListVO.setPreviousStatus(av.getManuscriptStatusId());
					}

					// Adding due date
					if (data.getManuscriptStatusId().equals(8) || data.getManuscriptStatusId().equals(66)) {
						ManuScriptReviewer reviewerDuedate = manuScriptReviewerRepo
								.getManuscriptInReviewDueDate(data.getManuScriptId(), data.getManuscriptVersion());
						if (reviewerDuedate != null && reviewerDuedate.getDueDate() != null) {
							manuscriptListVO.setAssignDate(reviewerDuedate.getAssignedDate());
							manuscriptListVO.setDueDate(reviewerDuedate.getDueDate());
						} else {
							ManuScriptReviewer reviewedDuedate = manuScriptReviewerRepo
									.getManuscriptReviewedDueDate(data.getManuScriptId(), data.getManuscriptVersion());
							if (null != reviewedDuedate) {
								manuscriptListVO.setAssignDate(reviewedDuedate.getAssignedDate());
								manuscriptListVO.setDueDate(reviewedDuedate.getDueDate());
							}

						}
					} else {
						manuscriptListVO.setDueDate(data.getDueDate());
						Date assignedDate = authoritiesHistoryRepo.getAssignedDate(data.getManuScriptId());
						if(assignedDate!=null&& (data.getManuscriptStatusId().equals(4) || data.getManuscriptStatusId().equals(46))) {
							manuscriptListVO.setAssignDate(assignedDate);
						}else {
							manuscriptListVO.setAssignDate(data.getUpdatedOn());
						}
						
					}
					manuscriptListVO.setAuthorName(data.getUser().getFirstName() + " " + data.getUser().getLastName());
					if (data.getManuscriptTypeDetails() != null
							&& data.getManuscriptTypeDetails().getManuscriptType() != null
							&& !data.getManuscriptTypeDetails().getManuscriptType().isEmpty()) {
						manuscriptListVO.setManuScriptType(data.getManuscriptTypeDetails().getManuscriptType());
					}
					
					List<Integer> disallowedStatuses = Arrays.asList(8, 66, 35, 4);
					List<Integer> pastStatuses = authoritiesHistoryRepo.findAllStatusIdsForManuscript(data.getManuScriptId());
					boolean hasDisallowedStatus = pastStatuses.stream().anyMatch(disallowedStatuses::contains);
					boolean showTechnicalModification = !hasDisallowedStatus;
					manuscriptListVO.setShowTechnicalModification(showTechnicalModification);
					
					newList.add(manuscriptListVO);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
		// }
		return newList;

	}

	@Override
	public int getTotalManuscriptStausForDashboard(ManuscriptDashboardList manuscriptVO) {
		Integer totalManuscriptForDashboard = manuScriptForDashboardRepo.getTotalManuscriptByStatusCode(
				manuscriptVO.getManuscriptStatusId(), manuscriptVO.getJournalId(), manuscriptVO.getStatusCode());
		return totalManuscriptForDashboard;
	}

	@Override
	public int getTotalManuscriptStausEditorForDashboard(ManuscriptDashboardList manuscriptVO) {
		List<ManuScriptForDashboard> totalManuscriptForDashboard = manuScriptForDashboardRepo
				.getTotalManuscriptByStatusCodeEditor(manuscriptVO.getManuscriptStatusId(), manuscriptVO.getJournalId(),
						manuscriptVO.getUserId(), manuscriptVO.getStatusCode());
		return totalManuscriptForDashboard.size();
	}

	@Override
	public int getTotalManuscriptStausEICForDashboard(ManuscriptDashboardList manuscriptVO) {
		Integer totalManuscriptForDashboard = manuScriptForDashboardRepo.getTotalManuscriptByStatusCodeEIC(
				manuscriptVO.getManuscriptStatusId(), manuscriptVO.getJournalId(), manuscriptVO.getStatusCode());
		return totalManuscriptForDashboard;
	}

	@Override
	public List<String> getStatusList(ManuscriptDashboardList manuscriptVO) {

		List<String> statusList = manuScriptForDashboardRepo
				.getManuscriptStatusList(manuscriptVO.getManuscriptStatusId(), manuscriptVO.getJournalId());
		return statusList;
	}

	@Override
	public List<ManuscriptDashboardList> getfinaldecisionByStatus(ManuscriptDashboardList manuscriptVO) {
		List<ManuscriptDashboardList> newList = new ArrayList<>();
		if (manuscriptVO.getSortBy().equalsIgnoreCase("assignDate")) {
			manuscriptVO.setSortBy("updatedOn");
		}
		Pageable paging = PageRequest.of(manuscriptVO.getPageNo(), manuscriptVO.getLimit(),
				Sort.by(Sort.Direction.DESC, manuscriptVO.getSortBy()));
		if (manuscriptVO.getSort().equalsIgnoreCase("ASC")) {
			paging = PageRequest.of(manuscriptVO.getPageNo(), manuscriptVO.getLimit(),
					Sort.by(Sort.Direction.ASC, manuscriptVO.getSortBy()));
		}

		List<ManuScriptForDashboard> list = new ArrayList<>();

		if (manuscriptVO.getStatusCode().equalsIgnoreCase("All")) {
			if (manuscriptVO != null && manuscriptVO.getSearch() != null && manuscriptVO.getSearch() != "") {
				list = manuScriptForDashboardRepo.getManuscriptByStatusPageSearch(manuscriptVO.getManuscriptStatusId(),
						manuscriptVO.getJournalId(), paging, manuscriptVO.getSearch());
			} else {
				list = manuScriptForDashboardRepo.getManuscriptByStatusPage(manuscriptVO.getManuscriptStatusId(),
						manuscriptVO.getJournalId(), paging);
			}
		} else {
			if (manuscriptVO != null && manuscriptVO.getSearch() != null && manuscriptVO.getSearch() != "") {
				list = manuScriptForDashboardRepo.getManuscriptByStatusCodeEICPageSearch(
						manuscriptVO.getManuscriptStatusId(), manuscriptVO.getJournalId(), manuscriptVO.getStatusCode(),
						paging, manuscriptVO.getSearch());
			} else {
				list = manuScriptForDashboardRepo.getManuscriptByStatusCodeEICPage(manuscriptVO.getManuscriptStatusId(),
						manuscriptVO.getJournalId(), manuscriptVO.getStatusCode(), paging);
			}
		}

		if (list != null) {
			for (ManuScriptForDashboard data : list) {
				// get data
				try {
					User editorNameData = userRepo.getByID(data.getAssignedUser());
					ManuscriptDashboardList manuscriptListVO = new ManuscriptDashboardList();
					if (data.getManuscriptStatusId().equals(2)) {
						int userCount = userJournalRepo.getCountOfUser(data.getJournalId(), 2);
						if (userCount > 1) {
							int editorCount = manuscriptEditorRepo.getEditorCount(data.getManuScriptId());
							if (editorCount > 0) {
								manuscriptListVO.setEditorName(
										editorNameData.getFirstName() + " " + editorNameData.getLastName());
							} else {
								manuscriptListVO.setEditorName("All(" + userCount + ")");
							}

						} else if (editorNameData != null) {
							manuscriptListVO
									.setEditorName(editorNameData.getFirstName() + " " + editorNameData.getLastName());

						}
					} else {
						if (editorNameData != null) {
							manuscriptListVO
									.setEditorName(editorNameData.getFirstName() + " " + editorNameData.getLastName());
						}
					}

					manuscriptListVO.setJournalId(data.getJournalId());
					manuscriptListVO.setManuscripCode(data.getManuscriptCode());

					manuscriptListVO.setManuscripCode(data.getManuscriptCode());
					manuscriptListVO.setManuscriptAuthorId(data.getAuthorId());
					manuscriptListVO.setManuscripTitle(data.getManuscriptTitle());
					manuscriptListVO.setManuScriptId(data.getManuScriptId());
					manuscriptListVO.setManuscriptVersion(data.getManuscriptVersion());
					manuscriptListVO.setSubmissionDate(data.getSubmissionDate());
					manuscriptListVO.setStatusCode(data.getStatusMaster().getStatusValue());
					manuscriptListVO.setManuStatusId(data.getManuscriptStatusId());
					manuscriptListVO.setAssignDate(data.getUpdatedOn());
					//////////////////////////////////////////////////////////////////////////////
					manuscriptListVO.setEICFolder("ReturnedByEditiriolStaff");
					if (manuScriptRepo.getEICFolder(data.getManuScriptId()) == null) {
						manuScriptRepo.updateEICFolder(data.getManuScriptId(), "ReturnedByEditiriolStaff");
					}
					if (manuScriptRepo.getEICFolder(data.getManuScriptId()) != null && !manuScriptRepo
							.getEICFolder(data.getManuScriptId()).equalsIgnoreCase("ReturnedByEditiriolStaff")) {
						manuScriptRepo.updateEICFolder(data.getManuScriptId(), "ReturnedByEditiriolStaff");
					}
					///////////////////////////////////////////////////////////////////////////////
					// Adding due date
					manuscriptListVO.setDueDate(data.getDueDate());
					manuscriptListVO.setAuthorName(data.getUser().getFirstName() + " " + data.getUser().getLastName());
					if (data.getManuscriptTypeDetails() != null
							&& data.getManuscriptTypeDetails().getManuscriptType() != null
							&& !data.getManuscriptTypeDetails().getManuscriptType().isEmpty()) {
						manuscriptListVO.setManuScriptType(data.getManuscriptTypeDetails().getManuscriptType());
					}
					
					    
					List<Integer> disallowedStatuses = Arrays.asList(8, 66, 35, 4);
					List<Integer> pastStatuses = authoritiesHistoryRepo.findAllStatusIdsForManuscript(data.getManuScriptId());
					boolean hasDisallowedStatus = pastStatuses.stream().anyMatch(disallowedStatuses::contains);
					boolean showTechnicalModification = !hasDisallowedStatus;
					manuscriptListVO.setShowTechnicalModification(showTechnicalModification);
					
					
					newList.add(manuscriptListVO);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
//	}
		return newList;
	}

	@Override
	public List<ManuscriptListVO> getAllInReviewManuscript(ManuscriptDashboardList manuscriptVO) {
		List<ManuscriptListVO> manuscriptList = new ArrayList();

		List<Tuple> statustupleList = statusMasterRepo.getStatuTuple();
		List<StatusMasterVO> statusList = statustupleList.stream()
				.map(t -> new StatusMasterVO(t.get(0, Integer.class), t.get(1, String.class)))
				.collect(Collectors.toList());
		Map<Integer, List<StatusMasterVO>> statusMap = statusList.stream()
				.collect(Collectors.groupingBy(w -> w.getStatusId()));
		List<Tuple> eicListTuple = userRepo.getUserList(manuscriptVO.getJournalId(), manuscriptVO.getRoleId());
		List<UserVO> eicList = new ArrayList<UserVO>();
		if (eicListTuple != null) {
			eicList = eicListTuple.stream()
					.map(t -> new UserVO(t.get(0, Integer.class), t.get(1, String.class), t.get(2, String.class),
							t.get(3, String.class), t.get(4, String.class), t.get(5, String.class),
							t.get(6, String.class), t.get(7, Integer.class)))
					.collect(Collectors.toList());
		}

		Map<Integer, List<UserVO>> eicMap = eicList.stream().collect(Collectors.groupingBy(w -> w.getId()));

		List<ManuscriptListVO> editorDataList = new ArrayList<ManuscriptListVO>();
		List<Tuple> tuplList = new ArrayList<>();
		if (manuscriptVO != null && manuscriptVO.getRoleId() != null && manuscriptVO.getSearch() != null
				&& manuscriptVO.getSearch() != "") {
			tuplList = manuScriptRepo.getManuscriptByJournalTuple(manuscriptVO.getJournalId(), manuscriptVO.getRoleId(),
					manuscriptVO.getSearch());

		} else {

			tuplList = manuScriptRepo.getManuscriptByJournalAndIdEICTuple(manuscriptVO.getJournalId(),
					manuscriptVO.getRoleId());
		}

		if (tuplList != null) {
			editorDataList = tuplList.stream()
					.map(t -> new ManuscriptListVO(t.get(0, Integer.class), t.get(1, String.class),
							t.get(2, Integer.class), t.get(3, String.class), t.get(4, Integer.class),
							t.get(5, String.class), t.get(6, Date.class), t.get(7, Integer.class),
							t.get(8, String.class), t.get(9, String.class), t.get(10, Integer.class)))
					.collect(Collectors.toList());
		}
		Map<Integer, List<ManuscriptListVO>> manuscriptMap = editorDataList.stream()
				.collect(Collectors.groupingBy(w -> w.getManuScriptId()));

		List<Integer> manuscriptIdList = new ArrayList();

		if (editorDataList != null && !editorDataList.isEmpty()) {
			for (ManuscriptListVO editorDataListData : editorDataList) {
				if (editorDataListData.getManuStatusId() != null) {
					manuscriptIdList.add(editorDataListData.getManuScriptId());
				}
			}

			if (manuscriptIdList.size() > 0) {
				List<Tuple> getUniqueDataByTuple = manuScriptReviewerRepo
						.getInReviewManuscriptByTuple(manuscriptVO.getJournalId(), manuscriptIdList);

				List<ReviewerReportDataVO> getUniqueDataByjournal = getUniqueDataByTuple.stream()
						.map(t -> new ReviewerReportDataVO(t.get(0, Integer.class), t.get(1, Integer.class),
								t.get(2, Integer.class), t.get(3, Integer.class),
								Integer.valueOf(t.get(4, Short.class)), t.get(5, Date.class), t.get(6, Date.class),
								t.get(7, Date.class), t.get(8, String.class)))
						.collect(Collectors.toList());

				List<Integer> manuList = getUniqueDataByjournal.stream().map(ReviewerReportDataVO::getManuscriptId)
						.collect(Collectors.toList());

				List<Tuple> getAllDataTuple = manuScriptReviewerRepo
						.getReviewedManuscriptByTuple(manuscriptVO.getJournalId(), manuList);

				List<ReviewerReportDataVO> getAllData = getAllDataTuple.stream()
						.map(t -> new ReviewerReportDataVO(t.get(0, Integer.class), t.get(1, Integer.class),
								t.get(2, Integer.class), t.get(3, Integer.class),
								Integer.valueOf(t.get(4, Short.class)), t.get(5, Date.class), t.get(6, Date.class),
								t.get(7, Date.class), t.get(8, String.class)))
						.collect(Collectors.toList());

				Map<Integer, List<ReviewerReportDataVO>> reviewerMapList = getAllData.stream()
						.collect(Collectors.groupingBy(w -> w.getManuscriptId()));

				if (getUniqueDataByjournal != null && !getUniqueDataByjournal.isEmpty()) {
					for (ReviewerReportDataVO uniqueData : getUniqueDataByjournal) {
						boolean isDueDateExpire = false;
						Integer reviewedCount = 0;
						Integer totalReviewCount = 0;
						Integer blinkerCount = 0;
						try {
							if (reviewerMapList != null && !reviewerMapList.isEmpty()) {
								for (ReviewerReportDataVO manuScriptReviewer : reviewerMapList
										.get(uniqueData.getManuscriptId())) {
									DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
									Date reviewerDueDate = df.parse(df.format(manuScriptReviewer.getDueDate()));
									Date todayDate = df.parse(df.format(new Date()));
									if (reviewerDueDate.after(todayDate) || todayDate.equals(reviewerDueDate)) {
										blinkerCount++;
									}
									try {
										if (manuScriptReviewer.getManuscriptStatus().equals(12)
												|| manuScriptReviewer.getManuscriptStatus().equals(13)
												|| manuScriptReviewer.getManuscriptStatus().equals(14)
												|| manuScriptReviewer.getManuscriptStatus().equals(15)) {
											reviewedCount++;
										}
										if (manuScriptReviewer.getManuscriptStatus().equals(12)
												|| manuScriptReviewer.getManuscriptStatus().equals(13)
												|| manuScriptReviewer.getManuscriptStatus().equals(14)
												|| manuScriptReviewer.getManuscriptStatus().equals(15)
												|| manuScriptReviewer.getManuscriptStatus().equals(8)
												|| manuScriptReviewer.getManuscriptStatus().equals(66)
												|| manuScriptReviewer.getManuscriptStatus().equals(35)
												|| manuScriptReviewer.getManuscriptStatus().equals(36)
												|| manuScriptReviewer.getManuscriptStatus().equals(60)) {
											totalReviewCount++;
										}
										if (uniqueData.getDueDate() != null) {
											Calendar c = Calendar.getInstance();
											c.setTime(new Date()); // Now use today date.
											c.add(Calendar.DATE, 2);
											String output = df.format(c.getTime());
											Date d1 = df.parse(uniqueData.getDueDate().toString());
											Date d2 = df.parse(output.toString());

											if (d1.compareTo(d2) < 0) {
												isDueDateExpire = true;
											}
										}

									} catch (ParseException e) {
										log.info(
												"Exception occured during finding the valid condition for In review manuscript"
														+ e.getMessage());
									}

								}
							}
							ManuscriptListVO manuscriptListVO = new ManuscriptListVO();
							manuscriptListVO.setReviewdCount(reviewedCount);
							manuscriptListVO.setTotalReviewCount(totalReviewCount);
							IDVO id = new IDVO();
							id.setId(uniqueData.getManuscriptId());
							id.setVersion(uniqueData.getManuscriptVersion());

							manuscriptListVO.setBlinkerCount(blinkerCount);
							manuscriptListVO.setManuScriptId(uniqueData.getManuscriptId());
							manuscriptListVO.setManuscriptVersion(uniqueData.getManuscriptVersion());
							List<ReviewerReportDataVO> reviewerDuedate = reviewerMapList
									.get(uniqueData.getManuscriptId()).stream()
									.filter(manu -> manu.getManuscriptStatus() == 8 || manu.getManuscriptStatus() == 35
											|| manu.getManuscriptStatus() == 66)
									.sorted(Comparator.comparing(ReviewerReportDataVO::getDueDate).reversed())
									.collect(Collectors.toList());
							if (reviewerDuedate != null && reviewerDuedate.size() > 0
									&& reviewerDuedate.get(0).getDueDate() != null) {
								manuscriptListVO.setAssignDate(reviewerDuedate.get(0).getAssignedDate());
								manuscriptListVO.setDueDate(reviewerDuedate.get(0).getDueDate());
							}
							// add reviewer Names start
							List<ReviewerReportDataVO> reviewerList = reviewerMapList.get(uniqueData.getManuscriptId());
							List<ReviewerDetailsVO> reviewers = reviewerList.stream().map(x -> {
								ReviewerDetailsVO r = new ReviewerDetailsVO();
								r.setName(x.getReviewerName());
								r.setStatus(statusMap.get(x.getManuscriptStatus()).get(0).getStatusValue());
								r.setDueDate(x.getDueDate());
								r.setIsActive(x.getIsActive());
								return r;
							}).collect(Collectors.toList());
							manuscriptListVO.setManuScriptReviewers(reviewers);
							// add reviewer Names end
							manuscriptListVO.setAuthorName(
									manuscriptMap.get(uniqueData.getManuscriptId()).get(0).getAuthorName());
							manuscriptListVO.setManuscriptAuthorId(manuscriptMap.get(uniqueData.getManuscriptId()).get(0).getManuscriptAuthorId());
							if (manuscriptMap.get(uniqueData.getManuscriptId()).get(0).getManuScriptType() != null) {
								manuscriptListVO.setManuScriptType(
										manuscriptMap.get(uniqueData.getManuscriptId()).get(0).getManuScriptType());
							}
							manuscriptListVO.setManuScriptId(uniqueData.getManuscriptId());
							manuscriptListVO.setManuscripCode(
									manuscriptMap.get(uniqueData.getManuscriptId()).get(0).getManuscripCode());
							manuscriptListVO.setManuscripTitle(
									manuscriptMap.get(uniqueData.getManuscriptId()).get(0).getManuscripTitle());
							manuscriptListVO.setSubmissionDate(
									manuscriptMap.get(uniqueData.getManuscriptId()).get(0).getSubmissionDate());
							manuscriptListVO.setEditorStatus(statusMap
									.get(manuscriptMap.get(uniqueData.getManuscriptId()).get(0).getManuStatusId())
									.get(0).getStatusValue());
							//////////////////////////////////////////////////////////////////////////////
							manuscriptListVO.setEIC_Folder("ManuscriptsInReview");
							if (manuscriptMap.get(uniqueData.getManuscriptId()).get(0).getEIC_Folder() == null) {
								manuScriptRepo.updateEICFolder(uniqueData.getManuscriptId(), "ManuscriptsInReview");
							} else if (!manuscriptMap.get(uniqueData.getManuscriptId()).get(0).getEIC_Folder()
									.equalsIgnoreCase("ManuscriptsInReview")) {
								manuScriptRepo.updateEICFolder(uniqueData.getManuscriptId(), "ManuscriptsInReview");
							}
							///////////////////////////////////////////////////////////////////////////////

							manuscriptListVO.setManuStatusCode(statusMap
									.get(manuscriptMap.get(uniqueData.getManuscriptId()).get(0).getManuStatusId())
									.get(0).getStatusValue());
							// manuscriptListVO.setStatusValueForAuthor(statusMap.get(manuscriptMap.get(uniqueData.getManuscriptId()).get(0).getManuscriptStatusId()).get(0).getStatusValue());
							if (isDueDateExpire) {
								manuscriptListVO.setSomeUserDeclined(0);
							}
							manuscriptListVO.setEditorName(eicMap
									.get(manuscriptMap.get(uniqueData.getManuscriptId()).get(0).getEditorUserId())
									.get(0).getFirstName()
									+ " "
									+ eicMap.get(
											manuscriptMap.get(uniqueData.getManuscriptId()).get(0).getEditorUserId())
											.get(0).getLastName());

							manuscriptList.add(manuscriptListVO);
						} catch (Exception ex) {
							log.info("Exception occured during finding the valid condition for In review manuscript"
									+ ex.getMessage());
						}

					}
				}
			}

		}

		return manuscriptList;
	}

	@Override
	public List<ManuscriptListVO> getTotalInReviewManusriptForDashboard(ManuscriptDashboardList manuscriptVO) {
		List<ManuscriptListVO> manuscriptList = new ArrayList();
		if (manuscriptVO.getRoleId() != null && manuscriptVO.getRoleId() == 3) {
			List<ManuscriptEditor> editorDataList = manuscriptEditorRepo
					.getReviewedManuscriptListEditor(manuscriptVO.getJournalId(), manuscriptVO.getUserId());
			List<Integer> manuscriptIdList = new ArrayList();
			List<Integer> manuscriptVersionList = new ArrayList();

			if (editorDataList != null && !editorDataList.isEmpty()) {
				for (ManuscriptEditor editorDataListData : editorDataList) {
					if (editorDataListData.getManuscriptStatus() != null
							&& (editorDataListData.getManuscriptStatus().equals(8)
									|| editorDataListData.getManuscriptStatus().equals(66))) {
						manuscriptIdList.add(editorDataListData.getManuscriptId());
						manuscriptVersionList.add(editorDataListData.getManuscriptVersion());
					}
				}

				List<ManuScriptReviewer> getUniqueDataByjournal = new ArrayList<ManuScriptReviewer>();
				if (manuscriptIdList != null && manuscriptIdList.size() > 0)
					getUniqueDataByjournal = manuScriptReviewerRepo
							.getInReviewManuscriptForEditor(manuscriptVO.getJournalId(), manuscriptIdList);

				if (getUniqueDataByjournal != null && !getUniqueDataByjournal.isEmpty()) {
					for (ManuScriptReviewer uniqueData : getUniqueDataByjournal) {
						boolean isDueDateExpire = false;
						Integer reviewedCount = 0;
						List<ManuScriptReviewer> gteDataList = manuScriptReviewerRepo
								.getManuscriptReviewerListByManuScriptIdAndVersion(uniqueData.getManuscriptId(),
										uniqueData.getManuscriptVersion());
						if (gteDataList != null && !gteDataList.isEmpty()) {
							for (ManuScriptReviewer manuScriptReviewer : gteDataList) {
								DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
								try {

									if (manuScriptReviewer.getManuscriptStatus().equals(12)
											|| manuScriptReviewer.getManuscriptStatus().equals(13)
											|| manuScriptReviewer.getManuscriptStatus().equals(14)
											|| manuScriptReviewer.getManuscriptStatus().equals(15)) {
										reviewedCount++;
									}
									if (manuScriptReviewer.getDueDate() != null) {
										Calendar c = Calendar.getInstance();
										c.setTime(new Date()); // Now use today date.
										c.add(Calendar.DATE, 2);
										String output = df.format(c.getTime());
										Date d1 = df.parse(manuScriptReviewer.getDueDate().toString());
										Date d2 = df.parse(output.toString());

										if (d1.compareTo(d2) < 0) {
											isDueDateExpire = true;
										}
									}

								} catch (ParseException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}

						ManuscriptListVO manuscriptListVO = new ManuscriptListVO();

						ManuscriptEditor manuscriptEditor = manuscriptEditorRepo.getAllAssignedEdiorsOnManuscriptActive(
								uniqueData.getManuscriptId(), uniqueData.getManuscriptVersion());
						if (manuscriptEditor != null && manuscriptEditor.getManagingEditorId() != null) {
							manuscriptListVO.setManagingEditorId(manuscriptEditor.getManagingEditorId());
						}
						manuscriptListVO.setReviewdCount(reviewedCount);
						manuscriptListVO.setManuScriptId(uniqueData.getManuscriptId());
						manuscriptListVO.setManuscriptAuthorId(uniqueData.getManuscript().getAuthorId());
						manuscriptListVO.setManuscriptVersion(uniqueData.getManuscript().getManuscriptVersion());
						manuscriptListVO.setDueDate(uniqueData.getDueDate());
						manuscriptListVO.setAuthorName(uniqueData.getManuscript().getUser().getFirstName() + " "
								+ uniqueData.getManuscript().getUser().getLastName());
						if (uniqueData.getManuscript() != null) {
							if (uniqueData.getManuscript().getManuscriptTypeDetails() != null && uniqueData
									.getManuscript().getManuscriptTypeDetails().getManuscriptType() != null) {
								manuscriptListVO.setManuScriptType(
										uniqueData.getManuscript().getManuscriptTypeDetails().getManuscriptType());
							}
							manuscriptListVO.setManuScriptId(uniqueData.getManuscriptId());
							manuscriptListVO.setManuscripCode(uniqueData.getManuscript().getManuscriptCode());
							manuscriptListVO.setManuscripTitle(uniqueData.getManuscript().getManuscriptTitle());
							manuscriptListVO.setSubmissionDate(uniqueData.getManuscript().getSubmissionDate());
							manuscriptListVO
									.setEditorStatus(uniqueData.getManuscript().getStatusMaster().getStatusValue());
						}
						manuscriptListVO.setManuStatusCode(uniqueData.getStatusMaster().getStatusValue());
						manuscriptListVO
								.setStatusValueForAuthor(uniqueData.getStatusMaster().getStatusValueForAuthor());
						if (isDueDateExpire) {
							manuscriptListVO.setSomeUserDeclined(0);
						}

						User usr = userRepo.getByID(uniqueData.getEditorId());
						manuscriptListVO.setEditorName(usr.getFirstName() + " " + usr.getLastName());
						manuscriptListVO
								.setStatusValueForEditor(uniqueData.getStatusMaster().getStatusValueForEditor());
						manuscriptList.add(manuscriptListVO);

					}
				}

			}

		} else {

			List<ManuScript> editorDataList = manuScriptRepo
					.getManuscriptByJournalAndIdEICAndRole(manuscriptVO.getJournalId(), manuscriptVO.getRoleId());
			List<Integer> manuscriptIdList = new ArrayList();
			List<Integer> manuscriptVersionList = new ArrayList();

			if (editorDataList != null && !editorDataList.isEmpty()) {
				for (ManuScript editorDataListData : editorDataList) {
					if (editorDataListData.getManuscriptStatusId() != null
							&& (editorDataListData.getManuscriptStatusId().equals(8)
									|| editorDataListData.getManuscriptStatusId().equals(66))) {
						manuscriptIdList.add(editorDataListData.getManuScriptId());
						manuscriptVersionList.add(editorDataListData.getManuscriptVersion());
					}
				}

				if (manuscriptIdList.size() > 0 && manuscriptVersionList.size() > 0) {
					List<ManuScriptReviewer> getUniqueDataByjournal = manuScriptReviewerRepo
							.getInReviewManuscriptForEditor(manuscriptVO.getJournalId(), manuscriptIdList);
					if (getUniqueDataByjournal != null && !getUniqueDataByjournal.isEmpty()) {
						for (ManuScriptReviewer uniqueData : getUniqueDataByjournal) {
							boolean isDueDateExpire = false;
							Integer reviewedCount = 0;
							List<ManuScriptReviewer> gteDataList = manuScriptReviewerRepo
									.getManuscriptReviewerListByManuScriptIdAndVersion(uniqueData.getManuscriptId(),
											uniqueData.getManuscriptVersion());
							if (gteDataList != null && !gteDataList.isEmpty()) {
								for (ManuScriptReviewer manuScriptReviewer : gteDataList) {
									DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
									try {

										if (manuScriptReviewer.getManuscriptStatus().equals(12)
												|| manuScriptReviewer.getManuscriptStatus().equals(13)
												|| manuScriptReviewer.getManuscriptStatus().equals(14)
												|| manuScriptReviewer.getManuscriptStatus().equals(15)) {
											reviewedCount++;
										}
										if (uniqueData.getDueDate() != null) {
											Calendar c = Calendar.getInstance();
											c.setTime(new Date()); // Now use today date.
											c.add(Calendar.DATE, 2);
											String output = df.format(c.getTime());
											Date d1 = df.parse(uniqueData.getDueDate().toString());
											Date d2 = df.parse(output.toString());

											if (d1.compareTo(d2) < 0) {
												isDueDateExpire = true;
											}
										}

									} catch (ParseException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							}
							ManuscriptListVO manuscriptListVO = new ManuscriptListVO();
							manuscriptListVO.setReviewdCount(reviewedCount);
							manuscriptListVO.setManuScriptId(uniqueData.getManuscriptId());
							manuscriptListVO.setManuscriptAuthorId(uniqueData.getManuscript().getAuthorId());
							manuscriptListVO.setManuscriptVersion(uniqueData.getManuscript().getManuscriptVersion());
							manuscriptListVO.setDueDate(uniqueData.getDueDate());
							manuscriptListVO.setAuthorName(uniqueData.getManuscript().getUser().getFirstName() + " "
									+ uniqueData.getManuscript().getUser().getLastName());
							if (uniqueData.getManuscript() != null) {
								if (uniqueData.getManuscript().getManuscriptTypeDetails() != null && uniqueData
										.getManuscript().getManuscriptTypeDetails().getManuscriptType() != null) {
									manuscriptListVO.setManuScriptType(
											uniqueData.getManuscript().getManuscriptTypeDetails().getManuscriptType());
								}
								manuscriptListVO.setManuScriptId(uniqueData.getManuscriptId());
								manuscriptListVO.setManuscripCode(uniqueData.getManuscript().getManuscriptCode());
								manuscriptListVO.setManuscripTitle(uniqueData.getManuscript().getManuscriptTitle());
								manuscriptListVO.setSubmissionDate(uniqueData.getManuscript().getSubmissionDate());
								manuscriptListVO
										.setEditorStatus(uniqueData.getManuscript().getStatusMaster().getStatusValue());
							}
							manuscriptListVO.setManuStatusCode(uniqueData.getStatusMaster().getStatusValue());
							manuscriptListVO
									.setStatusValueForAuthor(uniqueData.getStatusMaster().getStatusValueForAuthor());
							if (isDueDateExpire) {
								manuscriptListVO.setSomeUserDeclined(0);
							}
							User usr = userRepo.getByID(uniqueData.getEditorId());
							manuscriptListVO.setEditorName(usr.getFirstName() + " " + usr.getLastName());
							manuscriptListVO
									.setStatusValueForEditor(uniqueData.getStatusMaster().getStatusValueForEditor());
							manuscriptList.add(manuscriptListVO);

						}
					}
				}
			}
		}
		return manuscriptList;
	}

	@Override
	public int getTotalInReviewManusriptForDashboardSearch(ManuscriptDashboardList manuscriptVO) {
		int inReview = 0;
		List<ManuScript> editorDataList = manuScriptRepo.getManuscriptByJournalAndIdEICAndRoleByManuId(
				manuscriptVO.getJournalId(), manuscriptVO.getRoleId(), manuscriptVO.getSearch());
		List<Integer> manuscriptIdList = new ArrayList();
		List<Integer> manuscriptVersionList = new ArrayList();

		if (editorDataList != null && !editorDataList.isEmpty()) {
			for (ManuScript editorDataListData : editorDataList) {
				if (editorDataListData.getManuscriptStatusId() != null
						&& (editorDataListData.getManuscriptStatusId().equals(8)
								|| editorDataListData.getManuscriptStatusId().equals(66))) {
					manuscriptIdList.add(editorDataListData.getManuScriptId());
					manuscriptVersionList.add(editorDataListData.getManuscriptVersion());
				}
			}

			if (manuscriptIdList.size() > 0 && manuscriptVersionList.size() > 0) {
				List<ManuScriptReviewer> getUniqueDataByjournal = manuScriptReviewerRepo
						.getInReviewManuscriptForEditor(manuscriptVO.getJournalId(), manuscriptIdList);
				if (getUniqueDataByjournal != null && !getUniqueDataByjournal.isEmpty()) {
					for (ManuScriptReviewer uniqueData : getUniqueDataByjournal) {
						boolean isDueDateExpire = false;
						Integer reviewedCount = 0;
						List<ManuScriptReviewer> gteDataList = manuScriptReviewerRepo
								.getManuscriptReviewerListByManuScriptIdAndVersion(uniqueData.getManuscriptId(),
										uniqueData.getManuscriptVersion());
						if (gteDataList != null && !gteDataList.isEmpty()) {
							for (ManuScriptReviewer manuScriptReviewer : gteDataList) {
								if (manuScriptReviewer.getManuscriptStatus().equals(12)
										|| manuScriptReviewer.getManuscriptStatus().equals(13)
										|| manuScriptReviewer.getManuscriptStatus().equals(14)
										|| manuScriptReviewer.getManuscriptStatus().equals(15)) {
									reviewedCount++;
								}
								try {
									if (uniqueData.getDueDate() != null) {
										SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");
										Calendar c = Calendar.getInstance();
										c.setTime(new Date()); // Now use today date.
										c.add(Calendar.DATE, 2);
										String output = sdformat.format(c.getTime());
										Date d1 = sdformat.parse(uniqueData.getDueDate().toString());
										Date d2 = sdformat.parse(output.toString());

										if (d1.compareTo(d2) < 0) {
											isDueDateExpire = true;
										}
									}

								} catch (ParseException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}

						}
						inReview++;

					}
				}
			}
		}
		return inReview;
	}

	@Override
	public int getTotalInReviewManusriptForDashboardCount(List<ManuScriptReviewer> getUniqueDataByjournal) {

		Map<Integer, List<ManuScriptReviewer>> reviewerGroup = new HashMap<>();
		if (getUniqueDataByjournal.size() > 0) {
			reviewerGroup = getUniqueDataByjournal.stream().collect(Collectors.groupingBy(w -> w.getManuscriptId()));
		}
		return reviewerGroup.size();
	}

	@Override
	public int getTotalInReviewManusriptForDashboardCountReport(List<ManuscriptReportDataVO> editorDataList,
			List<ReviewerReportDataVO> getUniqueDataByjournal) {

		Map<String, ManuscriptReportDataVO> reviewerGroup = new HashMap<>();
		if (getUniqueDataByjournal.size() > 0 && editorDataList.size() > 0) {

			for (ManuscriptReportDataVO manuscriptReportDataVO : editorDataList) {

				for (ReviewerReportDataVO reviewerReportDataVO : getUniqueDataByjournal) {

					if (manuscriptReportDataVO.getManuscriptId().equals(reviewerReportDataVO.getManuscriptId())
							&& manuscriptReportDataVO.getManuscriptVersion()
									.equals(reviewerReportDataVO.getManuscriptVersion())) {

						reviewerGroup.put(manuscriptReportDataVO.getManuscriptCode(), manuscriptReportDataVO);

					}
				}

			}
		}

		return reviewerGroup.size();
	}

	@Override
	public List<ManuscriptListVO> getAllReviewedManuscript(ManuscriptDashboardList manuscriptVO) {
		List<ManuscriptListVO> manuscriptList = new ArrayList();

		List<Tuple> statustupleList = statusMasterRepo.getStatuTuple();
		List<StatusMasterVO> statusList = statustupleList.stream()
				.map(t -> new StatusMasterVO(t.get(0, Integer.class), t.get(1, String.class)))
				.collect(Collectors.toList());
		Map<Integer, List<StatusMasterVO>> statusMap = statusList.stream()
				.collect(Collectors.groupingBy(w -> w.getStatusId()));
		List<Tuple> eicListTuple = userRepo.getUserList(manuscriptVO.getJournalId(), manuscriptVO.getRoleId());
		List<UserVO> eicList = new ArrayList<UserVO>();
		if (eicListTuple != null) {
			eicList = eicListTuple.stream()
					.map(t -> new UserVO(t.get(0, Integer.class), t.get(1, String.class), t.get(2, String.class),
							t.get(3, String.class), t.get(4, String.class), t.get(5, String.class),
							t.get(6, String.class), t.get(7, Integer.class)))
					.collect(Collectors.toList());
		}

		Map<Integer, List<UserVO>> eicMap = eicList.stream().collect(Collectors.groupingBy(w -> w.getId()));

		List<ManuscriptListVO> editorDataList = new ArrayList<ManuscriptListVO>();

		List<Tuple> tuplList = new ArrayList<>();
		if (manuscriptVO != null && manuscriptVO.getRoleId() != null && manuscriptVO.getSearch() != null
				&& manuscriptVO.getSearch() != "") {
			tuplList = manuScriptRepo.getManuscriptByJournalTuple(manuscriptVO.getJournalId(), manuscriptVO.getRoleId(),
					manuscriptVO.getSearch());

		} else {

			tuplList = manuScriptRepo.getManuscriptByJournalAndIdEICTuple(manuscriptVO.getJournalId(),
					manuscriptVO.getRoleId());
		}

		if (tuplList != null) {
			editorDataList = tuplList.stream()
					.map(t -> new ManuscriptListVO(t.get(0, Integer.class), t.get(1, String.class),
							t.get(2, Integer.class), t.get(3, String.class), t.get(4, Integer.class),
							t.get(5, String.class), t.get(6, Date.class), t.get(7, Integer.class),
							t.get(8, String.class), t.get(9, String.class), t.get(10, Integer.class)))
					.collect(Collectors.toList());
		}

		Map<String, ManuscriptListVO> map = new HashMap<>();
		List<Integer> manuscriptIdList = new ArrayList();
		if (editorDataList != null && !editorDataList.isEmpty()) {
			for (ManuscriptListVO editorDataListData : editorDataList) {
				manuscriptIdList.add(editorDataListData.getManuScriptId());
			}

			List<Tuple> getUniqueDataByjournal = manuScriptReviewerRepo
					.getReviewedManuscriptByTuple(manuscriptVO.getJournalId(), manuscriptIdList);
			List<ReviewerReportDataVO> reviewerReportData = getUniqueDataByjournal.stream()
					.map(t -> new ReviewerReportDataVO(t.get(0, Integer.class), t.get(1, Integer.class),
							t.get(2, Integer.class), t.get(3, Integer.class), Integer.valueOf(t.get(4, Short.class)),
							t.get(5, Date.class), t.get(6, Date.class), t.get(7, Date.class), t.get(8, String.class)))
					.collect(Collectors.toList());

			Map<Integer, List<ReviewerReportDataVO>> reviewerMapList = reviewerReportData.stream()
					.collect(Collectors.groupingBy(w -> w.getManuscriptId()));

			if (editorDataList != null && !editorDataList.isEmpty() && getUniqueDataByjournal != null
					&& !getUniqueDataByjournal.isEmpty()) {
				for (ManuscriptListVO uniqueData : editorDataList) {
					int mcount = 0;
					int mvalidcount = 0;
					int delayCount = 0;
					int reviewedCount = 0;
					int totalReviewCount = 0;
					int blinkerCount = 0;
					try {
						for (ReviewerReportDataVO allData : reviewerMapList.get(uniqueData.getManuScriptId())) {
							if (uniqueData.getManuScriptId().equals(allData.getManuscriptId())
									&& allData.getManuscriptVersion().equals(uniqueData.getManuscriptVersion())) {
								mcount++;
								DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
								try {
									Date reviewerDueDate = df.parse(df.format(allData.getDueDate()));
									Date todayDate = df.parse(df.format(new Date()));
									if (reviewerDueDate.after(todayDate) || todayDate.equals(reviewerDueDate)) {
										blinkerCount++;
									}
									if (allData.getManuscriptStatus() != null && allData.getManuscriptStatus() == 12
											|| ((allData.getManuscriptStatus() == 8
													|| allData.getManuscriptStatus() == 35
													|| allData.getManuscriptStatus() == 66)
													&& reviewerDueDate.before(todayDate))
											|| allData.getManuscriptStatus() == 13
											|| allData.getManuscriptStatus() == 14
											|| allData.getManuscriptStatus() == 15
											|| allData.getManuscriptStatus() == 36
											|| allData.getManuscriptStatus() == 60) {
										mvalidcount++;
									}

									if (allData.getManuscriptStatus() != null && allData.getManuscriptStatus() == 36
											|| allData.getManuscriptStatus() == 60
											|| ((allData.getManuscriptStatus() == 8
													|| allData.getManuscriptStatus() == 35
													|| allData.getManuscriptStatus() == 66)
													&& reviewerDueDate.before(todayDate))) {
										delayCount++;
									}

									if (allData.getManuscriptStatus().equals(12)
											|| allData.getManuscriptStatus().equals(13)
											|| allData.getManuscriptStatus().equals(14)
											|| allData.getManuscriptStatus().equals(15)) {
										reviewedCount++;
									}
									if (allData.getManuscriptStatus().equals(12)
											|| allData.getManuscriptStatus().equals(13)
											|| allData.getManuscriptStatus().equals(14)
											|| allData.getManuscriptStatus().equals(15)
											|| allData.getManuscriptStatus().equals(8)
											|| allData.getManuscriptStatus().equals(35)
											|| allData.getManuscriptStatus().equals(66)
											|| allData.getManuscriptStatus().equals(36)
											|| allData.getManuscriptStatus().equals(60)) {
										totalReviewCount++;
									}

								} catch (ParseException e) {
									log.info(
											"Exception occured during finding the valid condition for Reviewed manuscript"
													+ e.getMessage());
								}
							}
						}
						if (mcount != 0 && mvalidcount != 0 && mcount == mvalidcount && delayCount != mcount) {
							ManuscriptListVO manuscriptListVO = new ManuscriptListVO();
							manuscriptListVO.setManuScriptId(uniqueData.getManuScriptId());
							manuscriptListVO.setTotalReviewCount(totalReviewCount);
							manuscriptListVO.setReviewdCount(reviewedCount);
							manuscriptListVO.setBlinkerCount(blinkerCount);
							manuscriptListVO.setManuscriptVersion(uniqueData.getManuscriptVersion());
							manuscriptListVO.setAuthorName(uniqueData.getAuthorName());
							manuscriptListVO.setManuscriptAuthorId(uniqueData.getManuscriptAuthorId());
							manuscriptListVO.setManuScriptType(uniqueData.getManuScriptType());
							manuscriptListVO.setManuscripCode(uniqueData.getManuscripCode());
							manuscriptListVO.setManuscripTitle(uniqueData.getManuscripTitle());
							manuscriptListVO.setSubmissionDate(uniqueData.getSubmissionDate());
							manuscriptListVO.setEditorStatus(
									statusMap.get(uniqueData.getManuStatusId()).get(0).getStatusValue());
							// User usr = userRepo.getByID(uniqueData.getAssignedUser());
							manuscriptListVO
									.setEditorName(eicMap.get(uniqueData.getEditorUserId()).get(0).getFirstName() + " "
											+ eicMap.get(uniqueData.getEditorUserId()).get(0).getLastName());
							manuscriptListVO.setManuStatusCode(
									statusMap.get(uniqueData.getManuStatusId()).get(0).getStatusValue());
							manuscriptListVO.setOtherReviewer("Yes");
							//////////////////////////////////////////////////////////////////////////////
							manuscriptListVO.setEIC_Folder("ReviewedManuscript");
							if (uniqueData.getEIC_Folder() == null) {
								manuScriptRepo.updateEICFolder(uniqueData.getManuScriptId(), "ReviewedManuscript");
							} else if (!uniqueData.getEIC_Folder().equalsIgnoreCase("ManuscriptsInReview")) {
								manuScriptRepo.updateEICFolder(uniqueData.getManuScriptId(), "ReviewedManuscript");
							}
							///////////////////////////////////////////////////////////////////////////////

							// add Due Date
							List<ReviewerReportDataVO> reviewerDuedate = reviewerMapList
									.get(uniqueData.getManuScriptId()).stream()
									.filter(manu -> manu.getManuscriptStatus() == 8 || manu.getManuscriptStatus() == 35
											|| manu.getManuscriptStatus() == 66)
									.sorted(Comparator.comparing(ReviewerReportDataVO::getDueDate).reversed())
									.collect(Collectors.toList());
							if (reviewerDuedate != null && reviewerDuedate.size() > 0
									&& reviewerDuedate.get(0).getDueDate() != null) {
								manuscriptListVO.setAssignDate(reviewerDuedate.get(0).getAssignedDate());
								manuscriptListVO.setDueDate(reviewerDuedate.get(0).getDueDate());
							} else {
								List<ReviewerReportDataVO> reviewDuedate = reviewerMapList
										.get(uniqueData.getManuScriptId()).stream()
										.filter(manu -> manu.getManuscriptStatus().equals(12)
												|| manu.getManuscriptStatus().equals(13)
												|| manu.getManuscriptStatus().equals(14)
												|| manu.getManuscriptStatus().equals(15))
										.sorted(Comparator.comparing(ReviewerReportDataVO::getDueDate).reversed())
										.collect(Collectors.toList());
								if (null != reviewDuedate && reviewDuedate.size() > 0) {
									manuscriptListVO.setAssignDate(reviewDuedate.get(0).getAssignedDate());
									manuscriptListVO.setDueDate(reviewDuedate.get(0).getDueDate());
								}

							}
							// add reviewer Names start
							List<ReviewerReportDataVO> reviewerList = reviewerMapList.get(uniqueData.getManuScriptId());
							List<ReviewerDetailsVO> reviewers = reviewerList.stream().map(x -> {
								ReviewerDetailsVO r = new ReviewerDetailsVO();
								r.setName(x.getReviewerName());
								r.setStatus(statusMap.get(x.getManuscriptStatus()).get(0).getStatusValue());
								r.setDueDate(x.getDueDate());
								r.setIsActive(x.getIsActive());
								return r;
							}).collect(Collectors.toList());
							manuscriptListVO.setManuScriptReviewers(reviewers); // add reviewer Names end
							map.put(manuscriptListVO.getManuscripCode(), manuscriptListVO);
						}
					} catch (Exception ex) {
						log.info("Exception occured during finding the valid condition for No response reviewer"
								+ ex.getMessage());
					}
				}
			}

		}
		if (map != null && !map.isEmpty()) {
			for (Map.Entry<String, ManuscriptListVO> entry : map.entrySet()) {
				manuscriptList.add(entry.getValue());
			}
		}
		return manuscriptList;
	}

	@Override
	public int getTotalReviewedManusriptForDashboardCount(List<ManuScript> editorDataList,
			List<ManuScriptReviewer> getAllDataByjournal) {

		Map<String, ManuScript> map = new HashMap<>();
		if (editorDataList != null && !editorDataList.isEmpty()) {
			Map<Integer, List<ManuScriptReviewer>> reviewerGroup = getAllDataByjournal.stream()
					.collect(Collectors.groupingBy(w -> w.getManuscriptId()));
			if (getAllDataByjournal != null && !getAllDataByjournal.isEmpty() && editorDataList != null
					&& !editorDataList.isEmpty()) {
				for (ManuScript uniqueData : editorDataList) {
					int mcount = 0;
					int mvalidcount = 0;
					int delayCount = 0;
					try {
						for (ManuScriptReviewer allData : reviewerGroup.get(uniqueData.getManuScriptId())) {
							if (uniqueData.getManuScriptId().equals(allData.getManuscriptId())
									&& allData.getManuscriptVersion().equals(uniqueData.getManuscriptVersion())) {
								mcount++;
								DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
								try {
									Date reviewerDueDate = df.parse(df.format(allData.getDueDate()));
									Date todayDate = df.parse(df.format(new Date()));

									if (allData.getManuscriptStatus() != null && allData.getManuscriptStatus() == 12
											|| ((allData.getManuscriptStatus() == 8
													|| allData.getManuscriptStatus() == 35
													|| allData.getManuscriptStatus() == 66)
													&& reviewerDueDate.before(todayDate))
											|| allData.getManuscriptStatus() == 13
											|| allData.getManuscriptStatus() == 14
											|| allData.getManuscriptStatus() == 15
											|| allData.getManuscriptStatus() == 36
											|| allData.getManuscriptStatus() == 60) {
										mvalidcount++;
									}

									if (allData.getManuscriptStatus() != null && allData.getManuscriptStatus() == 36
											|| allData.getManuscriptStatus() == 60
											|| ((allData.getManuscriptStatus() == 8
													|| allData.getManuscriptStatus() == 35
													|| allData.getManuscriptStatus() == 66)
													&& reviewerDueDate.before(todayDate))) {
										delayCount++;
									}
								} catch (ParseException e) {
									log.debug(e.getMessage());
								}
							}
						}
						if (mcount != 0 && mvalidcount != 0 && mcount == mvalidcount && delayCount != mcount) {

							map.put(uniqueData.getManuscriptCode(), uniqueData);

						}
					} catch (Exception e) {
						log.debug(e.getMessage());
					}
				}
			}
		}

		return map.size();
	}

	@Override
	public int getTotalReviewedManusriptForDashboardCountReport(List<ManuscriptReportDataVO> editorDataList,
			List<ReviewerReportDataVO> getAllDataByjournal) {

		Map<String, ManuscriptReportDataVO> map = new HashMap<>();
		if (editorDataList != null && !editorDataList.isEmpty()) {
			Map<Integer, List<ReviewerReportDataVO>> reviewerGroup = getAllDataByjournal.stream()
					.collect(Collectors.groupingBy(w -> w.getManuscriptId()));
			if (getAllDataByjournal != null && !getAllDataByjournal.isEmpty() && editorDataList != null
					&& !editorDataList.isEmpty()) {
				for (ManuscriptReportDataVO uniqueData : editorDataList) {
					int mcount = 0;
					int mvalidcount = 0;
					int delayCount = 0;
					try {
						for (ReviewerReportDataVO allData : reviewerGroup.get(uniqueData.getManuscriptId())) {
							if (uniqueData.getManuscriptId().equals(allData.getManuscriptId())
									&& allData.getManuscriptVersion().equals(uniqueData.getManuscriptVersion())) {
								mcount++;
								DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
								try {
									Date reviewerDueDate = df.parse(df.format(allData.getDueDate()));
									Date todayDate = df.parse(df.format(new Date()));

									if (allData.getManuscriptStatus() != null && allData.getManuscriptStatus() == 12
											|| ((allData.getManuscriptStatus() == 8
													|| allData.getManuscriptStatus() == 35
													|| allData.getManuscriptStatus() == 66)
													&& reviewerDueDate.before(todayDate))
											|| allData.getManuscriptStatus() == 13
											|| allData.getManuscriptStatus() == 14
											|| allData.getManuscriptStatus() == 15
											|| allData.getManuscriptStatus() == 36
											|| allData.getManuscriptStatus() == 60) {
										mvalidcount++;
									}

									if (allData.getManuscriptStatus() != null && allData.getManuscriptStatus() == 36
											|| allData.getManuscriptStatus() == 60
											|| ((allData.getManuscriptStatus() == 8
													|| allData.getManuscriptStatus() == 35
													|| allData.getManuscriptStatus() == 66)
													&& reviewerDueDate.before(todayDate))) {
										delayCount++;
									}
								} catch (ParseException e) {
									log.debug(e.getMessage());
								}
							}
						}
						if (mcount != 0 && mvalidcount != 0 && mcount == mvalidcount && delayCount != mcount) {

							map.put(uniqueData.getManuscriptCode(), uniqueData);

						}
					} catch (Exception e) {
						log.debug(e.getMessage());
					}
				}
			}
		}

		return map.size();
	}

	@Override
	public int getTotalReviewedManusriptForDashboardCountSearch(ManuscriptDashboardList manuscriptVO) {
		Map<String, ManuScript> map = new HashMap<>();
		List<ManuScript> editorDataList = manuScriptRepo.getManuscriptByJournalAndIdEICAndRoleByManuId(
				manuscriptVO.getJournalId(), manuscriptVO.getRoleId(), manuscriptVO.getSearch());
		List<Integer> manuscriptIdList = new ArrayList();
		if (editorDataList != null && !editorDataList.isEmpty()) {
			for (ManuScript editorDataListData : editorDataList) {
				manuscriptIdList.add(editorDataListData.getManuScriptId());
			}
			List<ManuScriptReviewer> getAllDataByjournal = manuScriptReviewerRepo
					.getReviewedManuscriptEditor(manuscriptVO.getJournalId(), manuscriptIdList);
			if (getAllDataByjournal != null && !getAllDataByjournal.isEmpty() && editorDataList != null
					&& !editorDataList.isEmpty()) {
				for (ManuScript uniqueData : editorDataList) {
					int mcount = 0;
					int mvalidcount = 0;
					int delayCount = 0;
					boolean isDueDateExpire = false;
					for (ManuScriptReviewer allData : getAllDataByjournal) {
						if (uniqueData.getManuScriptId().equals(allData.getManuscriptId())
								&& allData.getManuscriptVersion().equals(uniqueData.getManuscriptVersion())) {
							mcount++;
							DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
							try {
								Date reviewerDueDate = df.parse(df.format(allData.getDueDate()));
								Date todayDate = df.parse(df.format(new Date()));

								if (allData.getManuscriptStatus() != null && allData.getManuscriptStatus() == 12
										|| ((allData.getManuscriptStatus() == 8 || allData.getManuscriptStatus() == 35
												|| allData.getManuscriptStatus() == 66)
												&& reviewerDueDate.before(todayDate))
										|| allData.getManuscriptStatus() == 13 || allData.getManuscriptStatus() == 14
										|| allData.getManuscriptStatus() == 15 || allData.getManuscriptStatus() == 36
										|| allData.getManuscriptStatus() == 60) {
									mvalidcount++;
								}

								if (allData.getManuscriptStatus() != null && allData.getManuscriptStatus() == 36
										|| allData.getManuscriptStatus() == 60
										|| ((allData.getManuscriptStatus() == 8 || allData.getManuscriptStatus() == 35)
												&& reviewerDueDate.before(todayDate))) {
									delayCount++;
								}

								if (allData.getDueDate() != null) {
									Calendar c = Calendar.getInstance();
									c.setTime(new Date()); // Now use today date.
									c.add(Calendar.DATE, 2);
									String output = df.format(c.getTime());
									Date d1 = df.parse(allData.getDueDate().toString());
									Date d2 = df.parse(output.toString());

									if (d1.compareTo(d2) < 0) {
										isDueDateExpire = true;
									}
								}

							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					if (mcount != 0 && mvalidcount != 0 && mcount == mvalidcount && delayCount != mcount) {
						map.put(uniqueData.getManuscriptCode(), uniqueData);
					}
				}
			}

		}

		return map.size();
	}

	@Override
	public List<ManuscriptListVO> getAllNoResponseManuscript(ManuscriptDashboardList manuscriptVO) {
		List<ManuscriptListVO> manuscriptList = new ArrayList();

		List<Tuple> statustupleList = statusMasterRepo.getStatuTuple();
		List<StatusMasterVO> statusList = statustupleList.stream()
				.map(t -> new StatusMasterVO(t.get(0, Integer.class), t.get(1, String.class)))
				.collect(Collectors.toList());
		Map<Integer, List<StatusMasterVO>> statusMap = statusList.stream()
				.collect(Collectors.groupingBy(w -> w.getStatusId()));
		List<Tuple> eicListTuple = userRepo.getUserList(manuscriptVO.getJournalId(), manuscriptVO.getRoleId());
		List<UserVO> eicList = new ArrayList<UserVO>();
		if (eicListTuple != null) {
			eicList = eicListTuple.stream()
					.map(t -> new UserVO(t.get(0, Integer.class), t.get(1, String.class), t.get(2, String.class),
							t.get(3, String.class), t.get(4, String.class), t.get(5, String.class),
							t.get(6, String.class), t.get(7, Integer.class)))
					.collect(Collectors.toList());
		}

		Map<Integer, List<UserVO>> eicMap = eicList.stream().collect(Collectors.groupingBy(w -> w.getId()));
		List<ManuscriptListVO> editorDataList = new ArrayList<ManuscriptListVO>();

		List<Tuple> tuplList = new ArrayList<>();
		if (manuscriptVO != null && manuscriptVO.getRoleId() != null && manuscriptVO.getSearch() != null
				&& manuscriptVO.getSearch() != "") {
			tuplList = manuScriptRepo.getManuscriptByJournalTuple(manuscriptVO.getJournalId(), manuscriptVO.getRoleId(),
					manuscriptVO.getSearch());

		} else {

			tuplList = manuScriptRepo.getManuscriptByJournalAndIdEICTuple(manuscriptVO.getJournalId(),
					manuscriptVO.getRoleId());
		}

		if (tuplList != null) {
			editorDataList = tuplList.stream()
					.map(t -> new ManuscriptListVO(t.get(0, Integer.class), t.get(1, String.class),
							t.get(2, Integer.class), t.get(3, String.class), t.get(4, Integer.class),
							t.get(5, String.class), t.get(6, Date.class), t.get(7, Integer.class),
							t.get(8, String.class), t.get(9, String.class), t.get(10, Integer.class)))
					.collect(Collectors.toList());
		}

		Map<String, ManuscriptListVO> map = new HashMap<>();
		List<Integer> manuscriptIdList = new ArrayList();
		if (editorDataList != null && !editorDataList.isEmpty()) {
			for (ManuscriptListVO editorDataListData : editorDataList) {
				manuscriptIdList.add(editorDataListData.getManuScriptId());
			}

			List<Tuple> getUniqueDataByjournal = manuScriptReviewerRepo
					.getManuscriptByJournalEditorTuple(manuscriptVO.getJournalId(), manuscriptIdList);
			List<ReviewerReportDataVO> getAllDataByjournal = getUniqueDataByjournal.stream()
					.map(t -> new ReviewerReportDataVO(t.get(0, Integer.class), t.get(1, Integer.class),
							t.get(2, Integer.class), t.get(3, Integer.class), Integer.valueOf(t.get(4, Short.class)),
							t.get(5, Date.class), t.get(6, Date.class), t.get(7, Date.class), t.get(8, String.class)))
					.collect(Collectors.toList());

			Map<Integer, List<ReviewerReportDataVO>> reviewerGroup = getAllDataByjournal.stream()
					.collect(Collectors.groupingBy(w -> w.getManuscriptId()));

			if (getAllDataByjournal != null && !getAllDataByjournal.isEmpty()) {
				for (ManuscriptListVO uniqueData : editorDataList) {
					int mcount = 0;
					int mvalidcount = 0;
					boolean isDueDateExpire = false;
					try {
						for (ReviewerReportDataVO allData : reviewerGroup.get(uniqueData.getManuScriptId())) {
							if (allData != null && uniqueData.getManuScriptId().equals(allData.getManuscriptId())
									&& allData.getManuscriptVersion() !=null && allData.getManuscriptVersion().equals(uniqueData.getManuscriptVersion())) {

								mcount++;
								DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
								try {
									Date reviewerDueDate = df.parse(df.format(allData.getDueDate()));
									Date todayDate = df.parse(df.format(new Date()));

									if (allData.getIsActive() == 0
											|| ((allData.getManuscriptStatus() == 8
													|| allData.getManuscriptStatus() == 66
													|| allData.getManuscriptStatus() == 35)
													&& reviewerDueDate.before(todayDate))
											|| allData.getManuscriptStatus() == 60
											|| allData.getManuscriptStatus() == 36) {
										mvalidcount++;
									}

								} catch (ParseException e) {
									log.info(
											"Exception occured during finding the valid condition for No response reviewer"
													+ e.getMessage());
								}

							}
						}
						if (mcount != 0 && mvalidcount != 0 && mcount == mvalidcount) {

							ManuscriptListVO manuscriptListVO = new ManuscriptListVO();
							manuscriptListVO.setManuScriptId(uniqueData.getManuScriptId());
							manuscriptListVO.setManuscriptVersion(uniqueData.getManuscriptVersion());
							manuscriptListVO.setAuthorName(uniqueData.getAuthorName());
							manuscriptListVO.setManuscriptAuthorId(uniqueData.getManuscriptAuthorId());
							manuscriptListVO.setManuScriptType(uniqueData.getManuScriptType());

							manuscriptListVO.setManuScriptId(uniqueData.getManuScriptId());
							manuscriptListVO.setManuscripCode(uniqueData.getManuscripCode());
							manuscriptListVO.setManuscripTitle(uniqueData.getManuscripTitle());
							manuscriptListVO.setSubmissionDate(uniqueData.getSubmissionDate());

							manuscriptListVO.setEditorStatus(
									statusMap.get(uniqueData.getManuStatusId()).get(0).getStatusValue());
							////////////////////////////////////////////////////////////////////////////////////////////
							manuscriptListVO.setEIC_Folder("NoResponseManuscript");
							if (uniqueData.getEIC_Folder() == null) {
								manuScriptRepo.updateEICFolder(uniqueData.getManuScriptId(), "NoResponseManuscript");
							}
							if (uniqueData.getEIC_Folder() != null
									&& !uniqueData.getEIC_Folder().equalsIgnoreCase("NoResponseManuscript")) {
								manuScriptRepo.updateEICFolder(uniqueData.getManuScriptId(), "NoResponseManuscript");
							}
							/////////////////////////////////////////////////////////////////////////////////////////////
							if (isDueDateExpire) {
								manuscriptListVO.setSomeUserDeclined(0);
							}

							manuscriptListVO.setManuStatusCode(
									statusMap.get(uniqueData.getManuStatusId()).get(0).getStatusValue());
							// add due Date
							List<ReviewerReportDataVO> reviewerDuedate = reviewerGroup.get(uniqueData.getManuScriptId())
									.stream()
									.filter(manu -> manu.getManuscriptStatus() == 8 || manu.getManuscriptStatus() == 35
											|| manu.getManuscriptStatus() == 66)
									.sorted(Comparator.comparing(ReviewerReportDataVO::getDueDate).reversed())
									.collect(Collectors.toList());
							if (reviewerDuedate != null && reviewerDuedate.size() > 0
									&& reviewerDuedate.get(0).getDueDate() != null) {
								manuscriptListVO.setAssignDate(reviewerDuedate.get(0).getAssignedDate());
								manuscriptListVO.setDueDate(reviewerDuedate.get(0).getDueDate());
							} else {
								List<ReviewerReportDataVO> reviewDuedate = reviewerGroup
										.get(uniqueData.getManuScriptId()).stream()
										.filter(manu -> manu.getManuscriptStatus().equals(12)
												|| manu.getManuscriptStatus().equals(13)
												|| manu.getManuscriptStatus().equals(14)
												|| manu.getManuscriptStatus().equals(15)
												|| manu.getManuscriptStatus().equals(36)
												|| manu.getManuscriptStatus().equals(60)
												)
										.sorted(Comparator.comparing(ReviewerReportDataVO::getDueDate).reversed())
										.collect(Collectors.toList());
								if (null != reviewDuedate && reviewDuedate.size() > 0) {
									manuscriptListVO.setAssignDate(reviewDuedate.get(0).getAssignedDate());
									manuscriptListVO.setDueDate(reviewDuedate.get(0).getDueDate());
								}

							}
							// add reviewer Names start
							List<ReviewerReportDataVO> reviewerList = reviewerGroup.get(uniqueData.getManuScriptId());
							List<ReviewerDetailsVO> reviewers = reviewerList.stream().map(x -> {
								ReviewerDetailsVO r = new ReviewerDetailsVO();
								r.setName(x.getReviewerName());
								r.setStatus(statusMap.get(x.getManuscriptStatus()).get(0).getStatusValue());
								r.setDueDate(x.getDueDate());
								r.setIsActive(x.getIsActive());
								return r;
							}).collect(Collectors.toList());
							manuscriptListVO.setManuScriptReviewers(reviewers);
							// add reviewer Names end
							manuscriptListVO
									.setEditorName(eicMap.get(uniqueData.getEditorUserId()).get(0).getFirstName() + " "
											+ eicMap.get(uniqueData.getEditorUserId()).get(0).getLastName());
							// manuscriptListEditor.add(manuscriptListVO);
							map.put(manuscriptListVO.getManuscripCode(), manuscriptListVO);

						}

					} catch (Exception e) {
						log.info("Exception occured during finding the valid condition for No response reviewer123"
								+ e.getMessage());
					}
				}

			}
		}

		if (map != null && !map.isEmpty()) {
			for (Map.Entry<String, ManuscriptListVO> entry : map.entrySet()) {
				manuscriptList.add(entry.getValue());
			}
		}

		return manuscriptList;

	}

	@Override
	public int getAllNoResponseManusriptForDashboardCount(List<ManuScript> editorDataList,
			List<ManuScriptReviewer> getAllDataByjournal) {
		Map<String, ManuScript> map = new HashMap<>();

		if (editorDataList != null && !editorDataList.isEmpty()) {

			Map<Integer, List<ManuScriptReviewer>> reviewerGroup = getAllDataByjournal.stream()
					.collect(Collectors.groupingBy(w -> w.getManuscriptId()));
			if (getAllDataByjournal != null && !getAllDataByjournal.isEmpty()) {
				for (ManuScript uniqueData : editorDataList) {
					int mcount = 0;
					int npResponseValidCount = 0;
					try {
						for (ManuScriptReviewer allData : reviewerGroup.get(uniqueData.getManuScriptId())) {
							if (uniqueData.getManuScriptId().equals(allData.getManuscriptId())
									&& allData.getManuscriptVersion().equals(uniqueData.getManuscriptVersion())) {
								mcount++;
								DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
								try {
									Date reviewerDueDate = df.parse(df.format(allData.getDueDate()));
									Date todayDate = df.parse(df.format(new Date()));

									if (allData.getIsActive() == 0
											|| ((allData.getManuscriptStatus() == 8
													|| allData.getManuscriptStatus() == 66
													|| allData.getManuscriptStatus() == 35)
													&& reviewerDueDate.before(todayDate))
											|| allData.getManuscriptStatus() == 60
											|| allData.getManuscriptStatus() == 36) {
										npResponseValidCount++;
									}

								} catch (ParseException e) {
									log.debug(e.getMessage());
								}
							}
						}
						if (mcount != 0 && npResponseValidCount != 0 && mcount == npResponseValidCount) {

							map.put(uniqueData.getManuscriptCode(), uniqueData);
						}
					} catch (Exception e) {
						log.debug(e.getMessage());
					}
				}
			}

		}

		return map.size();

	}

	@Override
	public int getAllNoResponseManusriptForDashboardCountReport(List<ManuscriptReportDataVO> editorDataList,
			List<ReviewerReportDataVO> getAllDataByjournal) {
		Map<String, ManuscriptReportDataVO> map = new HashMap<>();

		if (editorDataList != null && !editorDataList.isEmpty()) {

			Map<Integer, List<ReviewerReportDataVO>> reviewerGroup = getAllDataByjournal.stream()
					.collect(Collectors.groupingBy(w -> w.getManuscriptId()));
			if (getAllDataByjournal != null && !getAllDataByjournal.isEmpty()) {
				for (ManuscriptReportDataVO uniqueData : editorDataList) {
					int mcount = 0;
					int npResponseValidCount = 0;
					try {
						for (ReviewerReportDataVO allData : reviewerGroup.get(uniqueData.getManuscriptId())) {
							if (uniqueData.getManuscriptId().equals(allData.getManuscriptId())
									&& allData.getManuscriptVersion().equals(uniqueData.getManuscriptVersion())) {
								mcount++;
								DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
								try {
									Date reviewerDueDate = df.parse(df.format(allData.getDueDate()));
									Date todayDate = df.parse(df.format(new Date()));

									if (allData.getIsActive() == 0
											|| ((allData.getManuscriptStatus() == 8
													|| allData.getManuscriptStatus() == 66
													|| allData.getManuscriptStatus() == 35)
													&& reviewerDueDate.before(todayDate))
											|| allData.getManuscriptStatus() == 60
											|| allData.getManuscriptStatus() == 36) {
										npResponseValidCount++;
									}

								} catch (ParseException e) {
									log.debug(e.getMessage());
								}
							}
						}
						if (mcount != 0 && npResponseValidCount != 0 && mcount == npResponseValidCount) {

							map.put(uniqueData.getManuscriptCode(), uniqueData);
						}
					} catch (Exception e) {
						log.debug(e.getMessage());
					}
				}
			}

		}

		return map.size();

	}

	@Override

	public int getAllNoResponseManusriptForDashboardSearchCount(ManuscriptDashboardList manuscriptVO) {
		Map<String, ManuScript> map = new HashMap<>();
		List<ManuScript> editorDataList = manuScriptRepo.getManuscriptByJournalAndIdEICAndRoleSearch(
				manuscriptVO.getJournalId(), manuscriptVO.getUserId(), manuscriptVO.getRoleId(),
				manuscriptVO.getSearch());
		List<Integer> manuscriptIdList = new ArrayList();
		if (editorDataList != null && !editorDataList.isEmpty()) {
			for (ManuScript editorDataListData : editorDataList) {
				manuscriptIdList.add(editorDataListData.getManuScriptId());
			}

			if (manuscriptIdList.size() > 0) {
				List<ManuScriptReviewer> getAllDataByjournal = manuScriptReviewerRepo
						.getManuscriptByJournalEditor(manuscriptVO.getJournalId(), manuscriptIdList);
				if (getAllDataByjournal != null && !getAllDataByjournal.isEmpty()) {
					for (ManuScript uniqueData : editorDataList) {
						int mcount = 0;
						int mvalidcount = 0;
						boolean isDueDateExpire = false;
						for (ManuScriptReviewer allData : getAllDataByjournal) {
							if (uniqueData.getManuScriptId().equals(allData.getManuscriptId())
									&& allData.getManuscriptVersion().equals(uniqueData.getManuscriptVersion())) {
								mcount++;
								DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
								try {
									Date reviewerDueDate = df.parse(df.format(allData.getDueDate()));
									Date todayDate = df.parse(df.format(new Date()));

									if (allData.getIsActive() == 0
											|| ((allData.getManuscriptStatus() == 8
													|| allData.getManuscriptStatus() == 66
													|| allData.getManuscriptStatus() == 35)
													&& reviewerDueDate.before(todayDate))
											|| allData.getManuscriptStatus() == 60
											|| allData.getManuscriptStatus() == 36) {
										mvalidcount++;
									}

								} catch (ParseException e) {
									log.debug(e.getMessage());
								}
							}
						}
						if (mcount != 0 && mvalidcount != 0 && mcount == mvalidcount) {

							map.put(uniqueData.getManuscriptCode(), uniqueData);

						}
					}
				}
			}

		}

		return map.size();

	}

	@Override
	public Integer getAllNoResponseManusriptForDashboardSearch(ManuscriptDashboardList manuscriptVO) {
		Integer totalAllNoResponseDashboardSearch = manuScriptForDashboardRepo
				.getTotalINManuscriptForDashboardSearch(manuscriptVO.getJournalId(), manuscriptVO.getSearch());
		return totalAllNoResponseDashboardSearch;
	}
	@Override
	public List<ManuscriptListVO> getAssignManuscriptData(ManuscriptDashboardList manuscriptVO) {
		log.info("*********************** getAssignManuscriptData  *********************");

		if (manuscriptVO.getSortBy().equalsIgnoreCase("assignDate")) {
			manuscriptVO.setSortBy("updatedOn");
		}
		if (manuscriptVO.getSortBy().equalsIgnoreCase("manuScriptType")) {
			manuscriptVO.setSortBy("manuscriptTypeDetails.manuscriptType");
		}
		Pageable paging = PageRequest.of(manuscriptVO.getPageNo(), manuscriptVO.getLimit(),
				Sort.by(Sort.Direction.DESC, manuscriptVO.getSortBy()));
		if (manuscriptVO.getSort().equalsIgnoreCase("ASC")) {
			paging = PageRequest.of(manuscriptVO.getPageNo(), manuscriptVO.getLimit(),
					Sort.by(Sort.Direction.ASC, manuscriptVO.getSortBy()));
		}
		List<ManuscriptListVO> newList = new ArrayList<>();

		if (manuscriptVO != null && manuscriptVO.getSearch() != null && manuscriptVO.getSearch() != "") {

			List<ManuScript> manuscriptList = manuScriptRepo.getAssignedEditorPaginationSearch(
					manuscriptVO.getJournalId(), manuscriptVO.getUserId(), 3, manuscriptVO.getSearch(), paging);

			log.info("UserId::{}" + manuscriptVO.getUserId());
			if (manuscriptList != null && !manuscriptList.isEmpty()) {
				for (ManuScript data : manuscriptList) {
					ManuscriptListVO manuscriptListVO = new ManuscriptListVO();
					manuscriptListVO.setJournalId(data.getJournalId());
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
					manuscriptListVO.setAssignDate(data.getUpdatedOn());
					// Adding due date
					manuscriptListVO.setDueDate(data.getDueDate());
					manuscriptListVO.setStatusValueForAuthor(data.getStatusMaster().getStatusValueForAuthor());
					manuscriptListVO.setStatusValueForEditor(data.getStatusMaster().getStatusValueForEditor());
					manuscriptListVO.setUpdatedOn(data.getUpdatedOn());
					manuscriptListVO.setManuStatusCode(data.getStatusMaster().getStatusValue());
					manuscriptListVO.setAuthorName(data.getUser().getFirstName() + " " + data.getUser().getLastName());
					manuscriptListVO.setJournalTitle(data.getJournal().getJournalTitle());
					manuscriptListVO.setJournalCode(data.getJournal().getJournalCode());
					manuscriptListVO.setManuStatusId(3);
					manuscriptListVO.setManuScriptType(data.getManuscriptTypeDetails() != null
							? data.getManuscriptTypeDetails().getManuscriptType()
							: "");
					ManuscriptEditor editor = manuscriptEditorRepo.getManuscriptEditorDeat(data.getJournalId(),
							data.getManuScriptId(), data.getManuscriptVersion(), data.getAssignedUser());
					manuscriptListVO.setManagingEditorId(editor.getManagingEditorId());
					manuscriptListVO
							.setEditorName(editor.getUser().getFirstName() + " " + editor.getUser().getLastName());
					manuscriptListVO.setEIC_Folder(manuScriptRepo.getEICFolder(data.getManuScriptId()));
					AuthoritiesHistory av = authoritiesHistoryRepo
							.getPreAuthoritiesHistoryByStatusId(data.getManuScriptId(), data.getJournalId());
					if (av != null) {
						manuscriptListVO.setPreviousStatus(av.getManuscriptStatusId());
					} else {
						manuscriptListVO.setPreviousStatus(2); // Manually added status due to missing previous entry in the article stage
						log.info("ManuscriptId: {} - Previous status manually set to 2 due to missing entry in the article stage", data.getManuScriptId());
					}
					List<Integer> disallowedStatuses = Arrays.asList(8, 66, 35, 4);
					List<Integer> pastStatuses = authoritiesHistoryRepo.findAllStatusIdsForManuscript(data.getManuScriptId());
					boolean hasDisallowedStatus = pastStatuses.stream().anyMatch(disallowedStatuses::contains);
					boolean showTechnicalModification = !hasDisallowedStatus;
					manuscriptListVO.setShowTechnicalModification(showTechnicalModification);
					newList.add(manuscriptListVO);

				}
			}
		} else {
			List<ManuScript> manuscriptList = manuScriptRepo.getAssignedEditorPagination(manuscriptVO.getJournalId(),
					manuscriptVO.getUserId(), 3, paging);
			if (manuscriptList != null && !manuscriptList.isEmpty()) {
				for (ManuScript data : manuscriptList) {
					ManuscriptListVO manuscriptListVO = new ManuscriptListVO();
					manuscriptListVO.setJournalId(data.getJournalId());
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
					manuscriptListVO.setAssignDate(data.getUpdatedOn());
					// Adding due date
					manuscriptListVO.setDueDate(data.getDueDate());
					manuscriptListVO.setStatusValueForAuthor(data.getStatusMaster().getStatusValueForAuthor());
					manuscriptListVO.setStatusValueForEditor(data.getStatusMaster().getStatusValueForEditor());
					manuscriptListVO.setUpdatedOn(data.getUpdatedOn());
					manuscriptListVO.setManuStatusCode(data.getStatusMaster().getStatusValue());
					manuscriptListVO.setAuthorName(data.getUser().getFirstName() + " " + data.getUser().getLastName());
					manuscriptListVO.setJournalTitle(data.getJournal().getJournalTitle());
					manuscriptListVO.setJournalCode(data.getJournal().getJournalCode());
					manuscriptListVO.setManuStatusId(3);
					manuscriptListVO.setManuScriptType(data.getManuscriptTypeDetails() != null
							? data.getManuscriptTypeDetails().getManuscriptType()
							: "");
					ManuscriptEditor editor = manuscriptEditorRepo.getManuscriptEditorDeat(data.getJournalId(),
							data.getManuScriptId(), data.getManuscriptVersion(), data.getAssignedUser());
					manuscriptListVO.setManagingEditorId(editor.getManagingEditorId());
					manuscriptListVO
							.setEditorName(editor.getUser().getFirstName() + " " + editor.getUser().getLastName());
					manuscriptListVO.setEIC_Folder(manuScriptRepo.getEICFolder(data.getManuScriptId()));
					AuthoritiesHistory av = authoritiesHistoryRepo
							.getPreAuthoritiesHistoryByStatusId(data.getManuScriptId(), data.getJournalId());
					if (av != null) {
						manuscriptListVO.setPreviousStatus(av.getManuscriptStatusId());
					} else {
						manuscriptListVO.setPreviousStatus(2); // Manually added status due to missing previous entry in the article stage
						log.info("ManuscriptId: {} - Previous status manually set to 2 due to missing entry in the article stage", data.getManuScriptId());

					}
					
					List<Integer> disallowedStatuses = Arrays.asList(8, 66, 35, 4);
					List<Integer> pastStatuses = authoritiesHistoryRepo.findAllStatusIdsForManuscript(data.getManuScriptId());
					boolean hasDisallowedStatus = pastStatuses.stream().anyMatch(disallowedStatuses::contains);
					boolean showTechnicalModification = !hasDisallowedStatus;
					manuscriptListVO.setShowTechnicalModification(showTechnicalModification);
					newList.add(manuscriptListVO);

				}
			}
		}
		return newList;
	}
	@Override
	public Integer getAssignManuscriptForDashboardSearch(ManuscriptDashboardList manuscriptVO) {
		Integer totalAssignedManuscriptForDashboardSearch = manuScriptForDashboardRepo
				.getAssignedManuscriptForDashboardSearch(3, manuscriptVO.getJournalId(), manuscriptVO.getSearch());
		return totalAssignedManuscriptForDashboardSearch;
	}

	@Override
	public Integer getAssignManuscriptDatacount(ManuscriptDashboardList manuscriptVO) {

		Integer totalAssignedManuscriptForDashboardSearch = manuScriptRepo
				.getAssignedEditorCount(manuscriptVO.getJournalId(), manuscriptVO.getUserId(), 3);
		return totalAssignedManuscriptForDashboardSearch;

	}

	@Override
	public Integer getAssignManuscriptDatacountSearch(ManuscriptDashboardList manuscriptVO) {

		Integer totalAssignedManuscriptForDashboardSearch = manuScriptRepo.getAssignedEditorCountSearch(
				manuscriptVO.getJournalId(), manuscriptVO.getUserId(), 3, manuscriptVO.getSearch());
		return totalAssignedManuscriptForDashboardSearch;

	}

	@Override
	public List<ManuscriptDashboardList> getManuscriptByStatusByUser(ManuscriptDashboardList manuscriptVO) {
		List<ManuscriptDashboardList> newList = new ArrayList<>();
		if (manuscriptVO.getSortBy().equalsIgnoreCase("assignDate")) {
			manuscriptVO.setSortBy("updatedOn");
		}
		if (manuscriptVO.getSortBy().equalsIgnoreCase("manuScriptType")) {
			manuscriptVO.setSortBy("manuscriptTypeDetails.manuscriptType");
		}
		Pageable paging = PageRequest.of(manuscriptVO.getPageNo(), manuscriptVO.getLimit(),
				Sort.by(Sort.Direction.DESC, manuscriptVO.getSortBy()));
		if (manuscriptVO.getSort().equalsIgnoreCase("ASC")) {
			paging = PageRequest.of(manuscriptVO.getPageNo(), manuscriptVO.getLimit(),
					Sort.by(Sort.Direction.ASC, manuscriptVO.getSortBy()));
		}

		for (Integer mStatus : manuscriptVO.getManuscriptStatusId()) {
			List<ManuScriptForDashboard> list = new ArrayList<>();
			if (manuscriptVO != null && manuscriptVO.getRoleId() != null && manuscriptVO.getSearch() != null
					&& manuscriptVO.getSearch() != "") {
				list = manuScriptForDashboardRepo.getManuscriptByJournalAndRolePageSearchByUser(mStatus,
						manuscriptVO.getJournalId(), paging, manuscriptVO.getSearch(), manuscriptVO.getUserId(),
						manuscriptVO.getRoleId());
			} else {

				list = manuScriptForDashboardRepo.getManuscriptByJournalAndRolePageByUser(mStatus,
						manuscriptVO.getJournalId(), paging, manuscriptVO.getUserId(), manuscriptVO.getRoleId());

			}

			if (list != null) {
				for (ManuScriptForDashboard data : list) {
					// get data
					try {
						ManuscriptEditor editor = manuscriptEditorRepo.getManuscriptEditorDeat(data.getJournalId(),
								data.getManuScriptId(), data.getManuscriptVersion(), data.getAssignedUser());
						ManuscriptDashboardList manuscriptListVO = new ManuscriptDashboardList();
						if (mStatus.equals(2)) {
							int userCount = userJournalRepo.getCountOfUser(data.getJournalId(), 2);
							if (userCount > 1) {
								int editorCount = manuscriptEditorRepo.getEditorCount(data.getManuScriptId());
								if (editorCount > 0) {
									manuscriptListVO.setEditorName(
											editor.getUser().getFirstName() + " " + editor.getUser().getLastName());
								} else {
									manuscriptListVO.setEditorName("All(" + userCount + ")");
								}

							} else if (editor != null) {
								manuscriptListVO.setEditorName(
										editor.getUser().getFirstName() + " " + editor.getUser().getLastName());

							}
						} else {
							if (editor != null) {
								manuscriptListVO.setEditorName(
										editor.getUser().getFirstName() + " " + editor.getUser().getLastName());
							}
						}

						manuscriptListVO.setJournalId(data.getJournalId());
						manuscriptListVO.setManuscripCode(data.getManuscriptCode());

						manuscriptListVO.setManuscripCode(data.getManuscriptCode());
						manuscriptListVO.setManuscriptAuthorId(data.getAuthorId());
						manuscriptListVO.setManuscripTitle(data.getManuscriptTitle());
						manuscriptListVO.setManuScriptId(data.getManuScriptId());
						manuscriptListVO.setManuscriptVersion(data.getManuscriptVersion());
						manuscriptListVO.setSubmissionDate(data.getSubmissionDate());
						AuthoritiesHistory author1 = authoritiesHistoryRepo
								.getAuthoritiesHistoryForAssignDate(data.getManuScriptId(), data.getJournalId());
						manuscriptListVO.setAssignDate(author1.getCreatedAt());
						if (mStatus == 26) {
							manuscriptListVO.setManuStatusId(mStatus);
							manuscriptListVO.setStatusCode(data.getStatusMaster().getStatusValue());
						}

						// Adding due date
						manuscriptListVO.setDueDate(data.getDueDate());
						manuscriptListVO
								.setAuthorName(data.getUser().getFirstName() + " " + data.getUser().getLastName());
						if (data.getManuscriptTypeDetails().getManuscriptType() != null
								&& !data.getManuscriptTypeDetails().getManuscriptType().isEmpty()) {
							manuscriptListVO.setManuScriptType(data.getManuscriptTypeDetails().getManuscriptType());
						}

						manuscriptListVO.setManagingEditorId(editor.getManagingEditorId());
						newList.add(manuscriptListVO);
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}
		}
		return newList;
	}

	@Override
	public List<ManuscriptDashboardList> getManuscriptByStatusByRole(ManuscriptDashboardList manuscriptVO) {
		List<ManuscriptDashboardList> newList = new ArrayList<>();

		// Status master list
		List<Tuple> statustupleList = statusMasterRepo.getStatuTuple();
		List<StatusMasterVO> statusList = statustupleList.stream()
				.map(t -> new StatusMasterVO(t.get(0, Integer.class), t.get(1, String.class)))
				.collect(Collectors.toList());
		Map<Integer, List<StatusMasterVO>> statusMap = statusList.stream()
				.collect(Collectors.groupingBy(w -> w.getStatusId()));
		// Editor List
		List<Tuple> eicListTuple = userRepo.getUserList(manuscriptVO.getJournalId(), manuscriptVO.getRoleId());
		List<UserVO> eicList = new ArrayList<UserVO>();
		if (eicListTuple != null) {
			eicList = eicListTuple.stream()
					.map(t -> new UserVO(t.get(0, Integer.class), t.get(1, String.class), t.get(2, String.class),
							t.get(3, String.class), t.get(4, String.class), t.get(5, String.class),
							t.get(6, String.class), t.get(7, Integer.class)))
					.collect(Collectors.toList());
		}
		Map<Integer, List<UserVO>> eicMap = eicList.stream().collect(Collectors.groupingBy(w -> w.getId()));

		// Article stage list
		List<Tuple> authorityList = authoritiesHistoryRepo.getAuthoritiesHistoryByJOurnalId(manuscriptVO.getJournalId(),
				26);
		List<AuthoritiesHistoryVo> articleStageList = authorityList.stream()
				.map(t -> new AuthoritiesHistoryVo(t.get(0, Integer.class), t.get(1, Integer.class),
						t.get(2, Integer.class), t.get(3, Date.class), t.get(4, Date.class)))
				.collect(Collectors.toList());

		for (Integer mStatus : manuscriptVO.getManuscriptStatusId()) {
			List<Tuple> list = new ArrayList<>();
			if (manuscriptVO != null && manuscriptVO.getRoleId() != null && manuscriptVO.getSearch() != null
					&& manuscriptVO.getSearch() != "") {
				list = manuScriptForDashboardRepo.getManuscriptByJournalTupleSearch(mStatus,
						manuscriptVO.getJournalId(), manuscriptVO.getSearch(), manuscriptVO.getRoleId());
			} else {

				list = manuScriptForDashboardRepo.getManuscriptByJournalAndIdEICTuple(mStatus,
						manuscriptVO.getJournalId(), manuscriptVO.getRoleId());

			}
			List<ManuscriptListVO> mlist = new ArrayList<>();
			if (list != null) {
				mlist = list.stream()
						.map(t -> new ManuscriptListVO(t.get(0, Integer.class), t.get(1, String.class),
								t.get(2, Integer.class), t.get(3, String.class), t.get(4, Integer.class),
								t.get(5, String.class), t.get(6, Date.class), t.get(7, Integer.class),
								t.get(8, String.class), t.get(9, String.class), t.get(10, Integer.class)))
						.collect(Collectors.toList());
			}

			if (mlist != null) {
				for (ManuscriptListVO data : mlist) {
					try {

						ManuscriptDashboardList manuscriptListVO = new ManuscriptDashboardList();
						manuscriptListVO.setEditorName(eicMap.get(data.getEditorUserId()).get(0).getFirstName() + " "
								+ eicMap.get(data.getEditorUserId()).get(0).getLastName());
						manuscriptListVO.setJournalId(manuscriptVO.getJournalId());
						manuscriptListVO.setManuscripCode(data.getManuscripCode());
						manuscriptListVO.setAuthorName(data.getAuthorName());
						manuscriptListVO.setManuscriptAuthorId(data.getManuscriptAuthorId());
						manuscriptListVO.setManuscripTitle(data.getManuscripTitle());
						manuscriptListVO.setManuScriptId(data.getManuScriptId());
						manuscriptListVO.setManuscriptVersion(data.getManuscriptVersion());
						manuscriptListVO.setSubmissionDate(data.getSubmissionDate());
						manuscriptListVO.setEICFolder("RevisedManuscript");
						if (data.getEIC_Folder() == null) {
							manuScriptRepo.updateEICFolder(data.getManuScriptId(), "RevisedManuscript");
						}
						if (data.getEIC_Folder() != null
								&& !data.getEIC_Folder().equalsIgnoreCase("RevisedManuscript")) {
							manuScriptRepo.updateEICFolder(data.getManuScriptId(), "RevisedManuscript");
						}
						List<AuthoritiesHistoryVo> manuscriptStage = articleStageList.stream()
								.filter(a -> (a.getManuscriptId().equals(data.getManuScriptId())
										&& a.getManuscriptVersion().equals(data.getManuscriptVersion())))
								.collect(Collectors.toList());
						manuscriptListVO.setAssignDate(manuscriptStage.get(0).getCreatedAt());
						manuscriptListVO.setManuStatusId(data.getManuStatusId());
						manuscriptListVO.setStatusCode(statusMap.get(data.getManuStatusId()).get(0).getStatusValue());
						manuscriptListVO.setDueDate(manuscriptStage.get(0).getDueDate());
						if (data.getManuScriptType() != null) {
							manuscriptListVO.setManuScriptType(data.getManuScriptType());
						}
						newList.add(manuscriptListVO);
					} catch (Exception e) {
						log.info("Exception occured during fetcching ");
					}

				}
			}
		}
		return newList;
	}

/////////////////////////////////////////////////////////////////////////////////////////
	public List<ManuscriptDashboardList> getManuscriptByStatusByRoleReSubmiited(ManuscriptDashboardList manuscriptVO) {
		List<ManuscriptDashboardList> newList = new ArrayList<>();
		if (manuscriptVO.getSortBy().equalsIgnoreCase("assignDate")) {
			manuscriptVO.setSortBy("updatedOn");
		}
		if (manuscriptVO.getSortBy().equalsIgnoreCase("manuScriptType")) {
			manuscriptVO.setSortBy("manuscriptTypeDetails.manuscriptType");
		}
		Pageable paging = PageRequest.of(manuscriptVO.getPageNo(), manuscriptVO.getLimit(),
				Sort.by(Sort.Direction.DESC, manuscriptVO.getSortBy()));
		if (manuscriptVO.getSort().equalsIgnoreCase("ASC")) {
			paging = PageRequest.of(manuscriptVO.getPageNo(), manuscriptVO.getLimit(),
					Sort.by(Sort.Direction.ASC, manuscriptVO.getSortBy()));
		}

		for (Integer mStatus : manuscriptVO.getManuscriptStatusId()) {
			List<ManuScriptForDashboard> list = new ArrayList<>();
			if (manuscriptVO != null && manuscriptVO.getRoleId() != null && manuscriptVO.getSearch() != null
					&& manuscriptVO.getSearch() != "") {
				list = manuScriptForDashboardRepo.getManuscriptByJournalAndRolePageSearchByRole(mStatus,
						manuscriptVO.getJournalId(), paging, manuscriptVO.getSearch(), manuscriptVO.getRoleId());
			} else {

				list = manuScriptForDashboardRepo.getManuscriptByJournalAndRolePageByRole(mStatus,
						manuscriptVO.getJournalId(), paging, manuscriptVO.getRoleId());

			}

			if (list != null) {
				for (ManuScriptForDashboard data : list) {
					// get data
					try {
						User editorNameData = userRepo.getByID(data.getAssignedUser());
						ManuscriptDashboardList manuscriptListVO = new ManuscriptDashboardList();
						if (mStatus.equals(2)) {
							int userCount = userJournalRepo.getCountOfUser(data.getJournalId(), 2);
							if (userCount > 1) {
								int editorCount = manuscriptEditorRepo.getEditorCount(data.getManuScriptId());
								if (editorCount > 0) {
									manuscriptListVO.setEditorName(
											editorNameData.getFirstName() + " " + editorNameData.getLastName());
								} else {
									manuscriptListVO.setEditorName("All(" + userCount + ")");
								}

							} else if (editorNameData != null) {
								manuscriptListVO.setEditorName(
										editorNameData.getFirstName() + " " + editorNameData.getLastName());

							}
						} else {
							if (editorNameData != null) {
								manuscriptListVO.setEditorName(
										editorNameData.getFirstName() + " " + editorNameData.getLastName());
							}
						}

						manuscriptListVO.setJournalId(data.getJournalId());
						manuscriptListVO.setManuscripCode(data.getManuscriptCode());

						manuscriptListVO.setManuscripCode(data.getManuscriptCode());
						manuscriptListVO.setManuscriptAuthorId(data.getAuthorId());
						manuscriptListVO.setManuscripTitle(data.getManuscriptTitle());
						manuscriptListVO.setManuScriptId(data.getManuScriptId());
						manuscriptListVO.setManuscriptVersion(data.getManuscriptVersion());
						manuscriptListVO.setSubmissionDate(data.getSubmissionDate());
						//////////////////////////////////////////////////////////////////////////////
						manuscriptListVO.setEICFolder("ReSubmittedManuscript");
						if (manuScriptRepo.getEICFolder(data.getManuScriptId()) == null) {
							manuScriptRepo.updateEICFolder(data.getManuScriptId(), "ReSubmittedManuscript");
						}
						if (manuScriptRepo.getEICFolder(data.getManuScriptId()) != null && !manuScriptRepo
								.getEICFolder(data.getManuScriptId()).equalsIgnoreCase("ReSubmittedManuscript")) {
							manuScriptRepo.updateEICFolder(data.getManuScriptId(), "ReSubmittedManuscript");
						}
						///////////////////////////////////////////////////////////////////////////////

						AuthoritiesHistory author1 = authoritiesHistoryRepo
								.getAuthoritiesHistoryForAssignDate(data.getManuScriptId(), data.getJournalId());
						manuscriptListVO.setAssignDate(author1.getCreatedAt());

//						manuscriptListVO.setAssignDate(data.getUpdatedOn());
						if (mStatus == 26) {
							manuscriptListVO.setManuStatusId(mStatus);
							manuscriptListVO.setStatusCode(data.getStatusMaster().getStatusValue());
						}

						// Adding due date
						manuscriptListVO.setDueDate(data.getDueDate());
						manuscriptListVO
								.setAuthorName(data.getUser().getFirstName() + " " + data.getUser().getLastName());
						if (data.getManuscriptTypeDetails().getManuscriptType() != null
								&& !data.getManuscriptTypeDetails().getManuscriptType().isEmpty()) {
							manuscriptListVO.setManuScriptType(data.getManuscriptTypeDetails().getManuscriptType());
						}

						List<ManuscriptEditor> me = manuscriptEditorRepo
								.getByJournalAndManuscript(data.getManuScriptId(), data.getJournalId());
						String editorName = "";
						if (me != null && !me.isEmpty()) {
							List<ManagingEditorVO> meVOList = new ArrayList<>();
							for (ManuscriptEditor meData : me) {
								User editor = userRepo.getById(meData.getEditorId());
								if (meData.getManagingEditorId() != null) {
									if (editorName.equalsIgnoreCase(""))
										editorName = editor.getFirstName() + " " + editor.getLastName();
									else
										editorName = editorName + "," + editor.getFirstName() + " "
												+ editor.getLastName();
								}
								ManagingEditorVO meVO = new ManagingEditorVO();
								meVO.setEditorId(meData.getEditorId());
								meVO.setManagingEditorId(meData.getManagingEditorId());
								meVO.setName(editor.getFirstName() + " " + editor.getLastName());
								manuscriptListVO.setManagingEditorId(meData.getManagingEditorId());
								meVOList.add(meVO);
							}
							manuscriptListVO.setEditorList(meVOList);

						}
						newList.add(manuscriptListVO);
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}
		}
		return newList;
	}

////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public Integer getTotalSbmittedManuscriptForDashboardByUser(ManuscriptDashboardList manuscriptVO) {
		Integer resubmitedManuscriptForDashboard = manuScriptForDashboardRepo
				.getTotalSbmittedManuscriptForDashboardByUser(manuscriptVO.getManuscriptStatusId(),
						manuscriptVO.getJournalId(), manuscriptVO.getUserId(), manuscriptVO.getRoleId());
		return resubmitedManuscriptForDashboard;
	}

	@Override
	public Integer getTotalManuscriptForDashboardByRole(ManuscriptDashboardList manuscriptVO) {
		Integer resubmitedManuscriptForDashboard = manuScriptForDashboardRepo
				.getTotalSbmittedManuscriptForDashboardByRole(manuscriptVO.getManuscriptStatusId(),
						manuscriptVO.getJournalId(), manuscriptVO.getRoleId());
		return resubmitedManuscriptForDashboard;
	}

	@Override
	public Integer getTotalManuscriptForEditorByUser(ManuscriptDashboardList manuscriptVO) {
		List<ManuScriptForDashboard> resubmitedManuscriptForDashboard = manuScriptForDashboardRepo
				.getTotalManuscriptForEditorByUser(manuscriptVO.getManuscriptStatusId(), manuscriptVO.getJournalId(),
						manuscriptVO.getUserId());
		return resubmitedManuscriptForDashboard.size();
	}

	@Override
	public Integer getTotalSbmittedManuscriptForDashboardSearchByUser(ManuscriptDashboardList manuscriptVO) {
		Integer resubmittedManuscriptForDashboardSearch = manuScriptForDashboardRepo
				.getTotalSbmittedManuscriptForDashboardSearchByUser(manuscriptVO.getManuscriptStatusId(),
						manuscriptVO.getJournalId(), manuscriptVO.getSearch(), manuscriptVO.getUserId(),
						manuscriptVO.getRoleId());
		return resubmittedManuscriptForDashboardSearch;
	}

	@Override
	public Integer getTotalManuscriptForDashboardSearchByRole(ManuscriptDashboardList manuscriptVO) {
		Integer resubmittedManuscriptForDashboardSearch = manuScriptForDashboardRepo
				.getTotalSbmittedManuscriptForDashboardSearchByRole(manuscriptVO.getManuscriptStatusId(),
						manuscriptVO.getJournalId(), manuscriptVO.getSearch(), manuscriptVO.getRoleId());
		return resubmittedManuscriptForDashboardSearch;
	}

	@Override
	public Integer getTotalManuscriptFoEditorSearchByUser(ManuscriptDashboardList manuscriptVO) {
		List<ManuScriptForDashboard> resubmittedManuscriptForDashboardSearch = manuScriptForDashboardRepo
				.getTotalManuscriptForEditorSearchByUser(manuscriptVO.getManuscriptStatusId(),
						manuscriptVO.getJournalId(), manuscriptVO.getSearch(), manuscriptVO.getUserId());
		return resubmittedManuscriptForDashboardSearch.size();
	}

	@Override
	public List<ManuscriptDashboardList> getTotalManuscriptByStatusByUser(ManuscriptDashboardList manuscriptVO) {

		List<ManuscriptDashboardList> newList = new ArrayList<>();
//		Pageable paging = PageRequest.of(manuscriptVO.getPageNo(), manuscriptVO.getLimit());
		if (manuscriptVO.getSortBy().equalsIgnoreCase("assignDate")) {
			manuscriptVO.setSortBy("updatedOn");
		}
		if (manuscriptVO.getSortBy().equalsIgnoreCase("manuScriptType")) {
			manuscriptVO.setSortBy("manuscriptTypeDetails.manuscriptType");
		}
		Pageable paging = PageRequest.of(manuscriptVO.getPageNo(), manuscriptVO.getLimit(),
				Sort.by(Sort.Direction.DESC, manuscriptVO.getSortBy()));
		if (manuscriptVO.getSort().equalsIgnoreCase("ASC")) {
			paging = PageRequest.of(manuscriptVO.getPageNo(), manuscriptVO.getLimit(),
					Sort.by(Sort.Direction.ASC, manuscriptVO.getSortBy()));
		}

		List<ManuScriptForDashboard> list = new ArrayList<>();
		if (manuscriptVO != null && manuscriptVO.getStatusCode().equalsIgnoreCase("All")) {
			if (manuscriptVO != null && manuscriptVO.getSearch() != null && manuscriptVO.getSearch() != "") {
				list = manuScriptForDashboardRepo.getManuscriptByStatusPageSearchByUser(
						manuscriptVO.getManuscriptStatusId(), manuscriptVO.getJournalId(), paging,
						manuscriptVO.getSearch(), manuscriptVO.getUserId());
			} else {

				list = manuScriptForDashboardRepo.getManuscriptByStatusPageByUser(manuscriptVO.getManuscriptStatusId(),
						manuscriptVO.getJournalId(), paging, manuscriptVO.getUserId());
			}
		} else {
			list = manuScriptForDashboardRepo.getManuscriptByStatusCodePageByUser(manuscriptVO.getManuscriptStatusId(),
					manuscriptVO.getJournalId(), manuscriptVO.getStatusCode(), paging, manuscriptVO.getUserId());

		}

		if (list != null) {
			for (ManuScriptForDashboard data : list) {
				// get data
				try {
					ManuscriptDashboardList manuscriptListVO = new ManuscriptDashboardList();
					manuscriptListVO.setJournalId(data.getJournalId());
					manuscriptListVO.setManuscripCode(data.getManuscriptCode());

					manuscriptListVO.setManuscripCode(data.getManuscriptCode());
					manuscriptListVO.setManuscriptAuthorId(data.getAuthorId());
					manuscriptListVO.setManuscripTitle(data.getManuscriptTitle());
					manuscriptListVO.setManuScriptId(data.getManuScriptId());
					manuscriptListVO.setManuscriptVersion(data.getManuscriptVersion());
					manuscriptListVO.setSubmissionDate(data.getSubmissionDate());
					manuscriptListVO.setStatusCode(data.getStatusMaster().getStatusValue());
					manuscriptListVO.setManuStatusId(data.getManuscriptStatusId());
					manuscriptListVO.setAssignDate(data.getUpdatedOn());

					// Adding due date
					if (data.getManuscriptStatusId().equals(8) || data.getManuscriptStatusId().equals(66)) {
						ManuScriptReviewer reviewerDuedate = manuScriptReviewerRepo
								.getManuscriptInReviewDueDate(data.getManuScriptId(), data.getManuscriptVersion());
						if (reviewerDuedate != null && reviewerDuedate.getDueDate() != null) {
							manuscriptListVO.setAssignDate(reviewerDuedate.getAssignedDate());
							manuscriptListVO.setDueDate(reviewerDuedate.getDueDate());
						} else {
							ManuScriptReviewer reviewedDuedate = manuScriptReviewerRepo
									.getManuscriptReviewedDueDate(data.getManuScriptId(), data.getManuscriptVersion());
							if (null != reviewedDuedate) {
								manuscriptListVO.setAssignDate(reviewedDuedate.getAssignedDate());
								manuscriptListVO.setDueDate(reviewedDuedate.getDueDate());
							}

						}
					} else {
						manuscriptListVO.setDueDate(data.getDueDate());
						manuscriptListVO.setAssignDate(data.getUpdatedOn());
					}
					manuscriptListVO.setAuthorName(data.getUser().getFirstName() + " " + data.getUser().getLastName());
					if (data.getManuscriptTypeDetails() != null
							&& data.getManuscriptTypeDetails().getManuscriptType() != null
							&& !data.getManuscriptTypeDetails().getManuscriptType().isEmpty()) {
						manuscriptListVO.setManuScriptType(data.getManuscriptTypeDetails().getManuscriptType());
					}

					List<ManuscriptEditor> me = manuscriptEditorRepo.getByJournalAndManuscript(data.getManuScriptId(),
							data.getJournalId());
					String editorName = "";
					if (me != null && !me.isEmpty()) {
						List<ManagingEditorVO> meVOList = new ArrayList<>();
						for (ManuscriptEditor meData : me) {
							User editor = userRepo.getOne(meData.getEditorId());
							if (meData.getManagingEditorId() != null) {
								if (editorName.equalsIgnoreCase(""))
									editorName = editor.getFirstName() + " " + editor.getLastName();
								else
									editorName = editorName + "," + editor.getFirstName() + " " + editor.getLastName();
							}
							ManagingEditorVO meVO = new ManagingEditorVO();
							meVO.setEditorId(meData.getEditorId());
							meVO.setManagingEditorId(meData.getManagingEditorId());
							meVO.setName(editor.getFirstName() + " " + editor.getLastName());
							manuscriptListVO.setManagingEditorId(meData.getManagingEditorId());
							meVOList.add(meVO);
						}
						manuscriptListVO.setEditorList(meVOList);
						manuscriptListVO.setEditorName(editorName);

					}
					newList.add(manuscriptListVO);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
		return newList;
	}

	@Override
	public List<ManuscriptDashboardList> getTotalManuscriptByStatusForEditor(ManuscriptDashboardList manuscriptVO) {
	    List<ManuscriptDashboardList> newList = new ArrayList<>();
	    
	    Map<Integer, List<StatusMasterVO>> statusMap = getStatusMap();
	    Map<Integer, List<UserVO>> eicMap = getEicMap(manuscriptVO);
	    Map<Integer, List<ReviewerReportDataVO>> manuscriptReviewerGroup = getManuscriptReviewerGroup(manuscriptVO);
	    
	    List<Tuple> list = retrieveManuscripts(manuscriptVO);
	    List<Tuple> filterList = new ArrayList<>();
	    for (Tuple tuple : list) {
			if(tuple.get(7, Integer.class).equals(3) || tuple.get(7, Integer.class).equals(50)) {
				if(tuple.get(9, Integer.class).equals(3) && manuscriptVO.getUserId().equals(tuple.get(4, Integer.class))) {
					filterList.add(tuple);
				}
				
			}else {
				filterList.add(tuple);
			}	    	
		}
	    
	    if (filterList != null) {
	        for (Tuple data : filterList) {
	            try {
	                ManuscriptDashboardList manuscriptListVO = transformTupleToManuscript(data, statusMap, eicMap, manuscriptReviewerGroup, manuscriptVO);
					List<Integer> disallowedStatuses = Arrays.asList(8, 66, 35, 4);
					List<Integer> pastStatuses = authoritiesHistoryRepo.findAllStatusIdsForManuscript(data.get(0,Integer.class));
					boolean hasDisallowedStatus = pastStatuses.stream().anyMatch(disallowedStatuses::contains);
					boolean showTechnicalModification = !hasDisallowedStatus;
					manuscriptListVO.setShowTechnicalModification(showTechnicalModification);
	                newList.add(manuscriptListVO);
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	        }
	    }
	    return newList;
	}

	private Map<Integer, List<StatusMasterVO>> getStatusMap() {
	    List<Tuple> statustupleList = statusMasterRepo.getStatuTuple();
	    List<StatusMasterVO> statusList = statustupleList.stream()
	            .map(t -> new StatusMasterVO(t.get(0, Integer.class), t.get(1, String.class)))
	            .collect(Collectors.toList());
	    return statusList.stream().collect(Collectors.groupingBy(StatusMasterVO::getStatusId));
	}

	private Map<Integer, List<UserVO>> getEicMap(ManuscriptDashboardList manuscriptVO) {
	    List<Tuple> eicListTuple = userRepo.getUserListForEditor(manuscriptVO.getJournalId());
	    return eicListTuple.stream()
	            .map(t -> new UserVO(t.get(0, Integer.class), t.get(1, String.class), t.get(2, String.class),
	                    t.get(3, String.class), t.get(4, String.class), t.get(5, String.class),
	                    t.get(6, String.class), t.get(7, Integer.class)))
	            .collect(Collectors.groupingBy(UserVO::getId));
	}

	private Map<Integer, List<ReviewerReportDataVO>> getManuscriptReviewerGroup(ManuscriptDashboardList manuscriptVO) {
	    List<Tuple> reviewerList = manuScriptReviewerRepo.getManuscriptJournalIdTuple(manuscriptVO.getJournalId());
	    return reviewerList.stream()
	            .map(t -> new ReviewerReportDataVO(t.get(0, Integer.class), t.get(1, Integer.class), t.get(2, Integer.class),
	                    t.get(3, Date.class), t.get(4, Date.class)))
	            .collect(Collectors.groupingBy(r -> r.getManuscriptId()));
	}

	private List<Tuple> retrieveManuscripts(ManuscriptDashboardList manuscriptVO) {
	    if (manuscriptVO.getStatusCode().equalsIgnoreCase("All")) {
	        if (!StringUtils.isEmpty(manuscriptVO.getSearch())) {
	            return manuScriptForDashboardRepo.getManuscriptForEditorSearchTuple(
	                    manuscriptVO.getManuscriptStatusId(), manuscriptVO.getJournalId(), manuscriptVO.getSearch(), manuscriptVO.getUserId());
	        } else {
	            return manuScriptForDashboardRepo.getManuscriptForEditorTuple(
	                    manuscriptVO.getManuscriptStatusId(), manuscriptVO.getJournalId(), manuscriptVO.getUserId());
	        }
	    } else {
	        return manuScriptForDashboardRepo.getManuscriptForEditorCodeTuple(
	                manuscriptVO.getManuscriptStatusId(), manuscriptVO.getJournalId(), manuscriptVO.getStatusCode(), manuscriptVO.getUserId());
	    }
	}

	private ManuscriptDashboardList transformTupleToManuscript(Tuple data,
	        Map<Integer, List<StatusMasterVO>> statusMap,
	        Map<Integer, List<UserVO>> eicMap,
	        Map<Integer, List<ReviewerReportDataVO>> manuscriptReviewerGroup,
	        ManuscriptDashboardList manuscriptVO) {

	    ManuscriptListVO mlistVO = new ManuscriptListVO(
	            data.get(0, Integer.class), data.get(1, String.class), data.get(2, Integer.class),
	            data.get(3, String.class), data.get(4, Integer.class), data.get(5, String.class),
	            data.get(6, Date.class), data.get(7, Integer.class), data.get(8, String.class),
	            data.get(9, Integer.class), data.get(10, Integer.class), data.get(11, Date.class),
	            data.get(12, Date.class), data.get(13, Integer.class));

	    ManuscriptDashboardList manuscriptListVO = new ManuscriptDashboardList();
	    try {
	    manuscriptListVO.setJournalId(manuscriptVO.getJournalId());
	    manuscriptListVO.setManuscripCode(mlistVO.getManuscripCode());
	    manuscriptListVO.setManuscripTitle(mlistVO.getManuscripTitle());
	    manuscriptListVO.setManuScriptId(mlistVO.getManuScriptId());
	    manuscriptListVO.setManuscriptVersion(mlistVO.getManuscriptVersion());
	    manuscriptListVO.setSubmissionDate(mlistVO.getSubmissionDate());
	    manuscriptListVO.setStatusCode(statusMap.get(mlistVO.getManuStatusId()).get(0).getStatusValue());
	    manuscriptListVO.setManuStatusId(mlistVO.getManuStatusId());
	    manuscriptListVO.setManuscriptAuthorId(mlistVO.getManuscriptAuthorId());

	    if (mlistVO.getManuStatusId().equals(8) || mlistVO.getManuStatusId().equals(66)) {
	        List<ReviewerReportDataVO> revdata = manuscriptReviewerGroup.get(mlistVO.getManuScriptId());
	        if(revdata != null) {
	        revdata = revdata.stream().sorted(Comparator.comparing(ReviewerReportDataVO::getDueDate, Comparator.nullsLast(Comparator.reverseOrder())))
	            .collect(Collectors.toList());
	        manuscriptListVO.setAssignDate(revdata.get(0).getAssignedDate());
	        manuscriptListVO.setDueDate(revdata.get(0).getDueDate());
	        }else {
		        manuscriptListVO.setDueDate(mlistVO.getDueDate());
		        manuscriptListVO.setAssignDate(mlistVO.getAssignDate());
	        }

	    } else {
	        manuscriptListVO.setDueDate(mlistVO.getDueDate());
	        manuscriptListVO.setAssignDate(mlistVO.getAssignDate());
	    }

	    manuscriptListVO.setAuthorName(mlistVO.getAuthorName());
	    manuscriptListVO.setManuScriptType(mlistVO.getManuScriptType());
	    manuscriptListVO.setManagingEditorId(mlistVO.getManagingEditorId());
	    manuscriptListVO.setEditorName(
	        eicMap.get(mlistVO.getEditorUserId()).get(0).getFirstName() + " " + eicMap.get(mlistVO.getEditorUserId()).get(0).getLastName());
	    manuscriptListVO.setActiveEditorFlag(1);
	    manuscriptListVO.setRoleId(mlistVO.getEditorRoleId());
	    manuscriptListVO.setEditorId(mlistVO.getEditorUserId());
        AuthoritiesHistory av = authoritiesHistoryRepo.getPreAuthoritiesHistoryByStatusId(
        		mlistVO.getManuScriptId(),manuscriptVO.getJournalId());
            if (av != null) {
                manuscriptListVO.setPreviousStatus(av.getManuscriptStatusId());
            }
	    }catch(Exception e) {
	    	System.out.println("Exception occured for manuscript:::"+mlistVO.getManuScriptId());
	    	e.printStackTrace();
	    }

	    return manuscriptListVO;
	}

	@Override
	public Integer getTotalManuscriptStausForDashboardByUser(ManuscriptDashboardList manuscriptVO) {
		Integer totalManuscriptForDashboard = manuScriptForDashboardRepo.getTotalManuscriptByStatusCodeByUser(
				manuscriptVO.getManuscriptStatusId(), manuscriptVO.getJournalId(), manuscriptVO.getStatusCode(),
				manuscriptVO.getUserId());
		return totalManuscriptForDashboard;
	}

	@Override
	public int getTotalManuscriptForDashboardSearchReport(ManuscriptDashboardList manuscriptVO) {

		Integer totalManuscriptForDashboardSearchReport = 0;

		List<Integer> accepted = Arrays.asList(33, 57, 61, 62, 65, 69, 70);
		List<Integer> published = Arrays.asList(59);
		List<Integer> wip = Arrays.asList(33, 57, 61, 62, 65);
		List<Integer> rejected = Arrays.asList(30);
		List<Integer> withdrawn = Arrays.asList(16);

		if (manuscriptVO.getReportType().equals("accepted")) {
			totalManuscriptForDashboardSearchReport = manuScriptForDashboardRepo
					.getTotalSbmittedManuscriptForDashboardSearch(accepted, manuscriptVO.getJournalId(),
							manuscriptVO.getSearch());

		} else if (manuscriptVO.getReportType().equals("published")) {

			totalManuscriptForDashboardSearchReport = manuScriptForDashboardRepo
					.getTotalSbmittedManuscriptForDashboardSearch(published, manuscriptVO.getJournalId(),
							manuscriptVO.getSearch());

		} else if (manuscriptVO.getReportType().equals("wip")) {

			totalManuscriptForDashboardSearchReport = manuScriptForDashboardRepo
					.getTotalSbmittedManuscriptForDashboardSearch(wip, manuscriptVO.getJournalId(),
							manuscriptVO.getSearch());
		} else if (manuscriptVO.getReportType().equals("rejected")) {

			totalManuscriptForDashboardSearchReport = manuScriptForDashboardRepo
					.getTotalRejectedManuscriptSearch(rejected, manuscriptVO.getJournalId(), manuscriptVO.getSearch());
		} else if (manuscriptVO.getReportType().equals("withdrawn")) {

			totalManuscriptForDashboardSearchReport = manuScriptForDashboardRepo.getTotalWithdrawnManuscriptSearch(
					withdrawn, manuscriptVO.getJournalId(), manuscriptVO.getSearch());
		}
		return totalManuscriptForDashboardSearchReport;
	}

	@Override
	public int getTotalManuscriptForDashboardReport(ManuscriptDashboardList manuscriptVO) {

		Integer totalManuscriptForDashboardSearchReport = 0;

		List<Integer> accepted = Arrays.asList(33, 57, 61, 62, 65, 69, 70);
		List<Integer> published = Arrays.asList(59);
		List<Integer> wip = Arrays.asList(33, 57, 61, 62, 65);
		List<Integer> rejected = Arrays.asList(30);
		List<Integer> withdrawn = Arrays.asList(16);

		if (manuscriptVO.getReportType().equals("accepted")) {
			totalManuscriptForDashboardSearchReport = manuScriptForDashboardRepo
					.getTotalResbmittedManuscriptForDashboard(accepted, manuscriptVO.getJournalId());

		} else if (manuscriptVO.getReportType().equals("published")) {

			totalManuscriptForDashboardSearchReport = manuScriptForDashboardRepo
					.getTotalResbmittedManuscriptForDashboard(published, manuscriptVO.getJournalId());

		} else if (manuscriptVO.getReportType().equals("wip")) {

			totalManuscriptForDashboardSearchReport = manuScriptForDashboardRepo
					.getTotalResbmittedManuscriptForDashboard(wip, manuscriptVO.getJournalId());
		} else if (manuscriptVO.getReportType().equals("rejected")) {

			totalManuscriptForDashboardSearchReport = manuScriptForDashboardRepo
					.getTotalResbmittedManuscriptForDashboard(rejected, manuscriptVO.getJournalId());
		} else if (manuscriptVO.getReportType().equals("withdrawn")) {

			totalManuscriptForDashboardSearchReport = manuScriptForDashboardRepo
					.getTotalWithdrawnManuscriptForDashboard(withdrawn, manuscriptVO.getJournalId());
		}
		return totalManuscriptForDashboardSearchReport;
	}

//	@Override
//	public String getNoResponseManuscriptNotification() {
//		Map<Integer, ManuScriptReviewer> map = new HashMap<>();
//		List<Integer> manuscriptIdList = new ArrayList();
//
//		List<Journal> JournalLst = journalsRepo.getActivejournal();
//		for (Journal journal : JournalLst) {
//			List<ManuScript> editorDataList = manuScriptRepo.getManuscriptForNoResponseNotf(journal.getJournalId());
//			if (editorDataList != null && !editorDataList.isEmpty()) {
//				for (ManuScript editorDataListData : editorDataList) {
//					if (editorDataListData.getManuscriptStatusId() != null
//							&& (editorDataListData.getManuscriptStatusId().equals(8)
//									|| editorDataListData.getManuscriptStatusId().equals(66))) {
//						manuscriptIdList.add(editorDataListData.getManuScriptId());
//					}
//				}
//
//				if (manuscriptIdList.size() > 0) {
//					List<ManuScriptReviewer> getAllDataByjournal = manuScriptReviewerRepo
//							.getManuscriptByJournalEditor(journal.getJournalId(), manuscriptIdList);
//					List<ManuScriptReviewer> getUniqueDataByjournal = getAllDataByjournal;
//					if (getAllDataByjournal != null && !getAllDataByjournal.isEmpty() && getUniqueDataByjournal != null
//							&& !getUniqueDataByjournal.isEmpty()) {
//						for (ManuScriptReviewer uniqueData : getUniqueDataByjournal) {
//							int mcount = 0;
//							int mvalidcount = 0;
//							boolean isDueDateExpire = false;
//							for (ManuScriptReviewer allData : getAllDataByjournal) {
//								if (uniqueData.getManuscriptId().equals(allData.getManuscriptId())
//										&& allData.getManuscriptVersion().equals(uniqueData.getManuscriptVersion())) {
//
//									mcount++;
//									DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//									try {
//										Date reviewerDueDate = df.parse(df.format(allData.getDueDate()));
//										Date todayDate = df.parse(df.format(new Date()));
//
//										if (allData.getIsActive() == 0
//												|| ((allData.getManuscriptStatus() == 8
//														|| allData.getManuscriptStatus() == 66
//														|| allData.getManuscriptStatus() == 35)
//														&& reviewerDueDate.before(todayDate))
//												|| allData.getManuscriptStatus() == 60
//												|| allData.getManuscriptStatus() == 36) {
//											mvalidcount++;
//										}
//
//										if (allData.getDueDate() != null) {
//											Calendar c = Calendar.getInstance();
//											c.setTime(new Date()); // Now use today date.
//											c.add(Calendar.DATE, 2);
//											String output = df.format(c.getTime());
//											Date d1 = df.parse(allData.getDueDate().toString());
//											Date d2 = df.parse(output.toString());
//
//											if (d1.compareTo(d2) < 0) {
//												isDueDateExpire = true;
//											}
//										}
//
//									} catch (ParseException e) {
//										log.debug("Exception occured during checking condition for No response article "
//												+ e.getMessage());
//									}
//
//								}
//							}
//							if (mcount != 0 && mvalidcount != 0 && mcount == mvalidcount) {
//
//								map.put(uniqueData.getManuscriptId(), uniqueData);
//							}
//						}
//					}
//				}
//
//			}
//		}
//		if (map != null && !map.isEmpty()) {
//
//			for (Integer manuscriptId : new ArrayList<Integer>(map.keySet())) {
//
//				ManuScript manu = manuScriptRepo.getById(manuscriptId);
//				noResponseNotification(manu);
//			}
//
//		}
//
//		return "Sucessfully sent No response reviewer notification to Editor/EIC";
//	}

	@Override
	public String getNoResponseManuscriptNotification() {
	    Map<Integer, ManuScriptReviewer> map = new HashMap<>();
	    List<Integer> manuscriptIdList = new ArrayList<>();

	    log.info("Starting process to send no response notifications...");

	    List<Journal> journalList = journalsRepo.getActivejournal();
	    log.info("Active journals count: {}", journalList.size());

	    for (Journal journal : journalList) {
	        log.info("Processing Journal ID: {}", journal.getJournalId());

	        List<ManuScript> editorDataList = manuScriptRepo.getNoResponseManuscriptsNotYetNotified(journal.getJournalId());
	        log.info("Manuscripts fetched for journal {}: {}", journal.getJournalId(), 
	                  editorDataList != null ? editorDataList.size() : 0);

	        if (editorDataList != null && !editorDataList.isEmpty()) {
	            for (ManuScript editorData : editorDataList) {
	                Integer statusId = editorData.getManuscriptStatusId();
	                if (statusId != null && (statusId.equals(8) || statusId.equals(66))) {
	                    manuscriptIdList.add(editorData.getManuScriptId());
	                }
	            }
	            log.info("Filtered manuscriptIdList for journal {}: {}", journal.getJournalId(), manuscriptIdList.size());

	            if (!manuscriptIdList.isEmpty()) {
	                List<ManuScriptReviewer> allReviewers = manuScriptReviewerRepo
	                        .getManuscriptByJournalEditor(journal.getJournalId(), manuscriptIdList);
	                List<ManuScriptReviewer> uniqueReviewers = allReviewers;

	                log.info("Reviewers fetched for journal {}: {}", journal.getJournalId(),
	                        allReviewers != null ? allReviewers.size() : 0);

	                if (allReviewers != null && !allReviewers.isEmpty() && uniqueReviewers != null && !uniqueReviewers.isEmpty()) {
	                    for (ManuScriptReviewer unique : uniqueReviewers) {
	                        int mcount = 0;
	                        int mvalidcount = 0;
	                        boolean isDueDateExpire = false;

	                        for (ManuScriptReviewer reviewer : allReviewers) {
	                            if (unique.getManuscriptId().equals(reviewer.getManuscriptId())
	                                    && unique.getManuscriptVersion().equals(reviewer.getManuscriptVersion())) {

	                                mcount++;
	                                try {
	                                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	                                    Date reviewerDueDate = df.parse(df.format(reviewer.getDueDate()));
	                                    Date todayDate = df.parse(df.format(new Date()));

	                                    if (reviewer.getIsActive() == 0
	                                            || ((reviewer.getManuscriptStatus() == 8
	                                            || reviewer.getManuscriptStatus() == 66
	                                            || reviewer.getManuscriptStatus() == 35)
	                                            && reviewerDueDate.before(todayDate))
	                                            || reviewer.getManuscriptStatus() == 60
	                                            || reviewer.getManuscriptStatus() == 36) {
	                                        mvalidcount++;
	                                    }

	                                    if (reviewer.getDueDate() != null) {
	                                        Calendar c = Calendar.getInstance();
	                                        c.setTime(new Date());
	                                        c.add(Calendar.DATE, 2);
	                                        Date upcomingDate = df.parse(df.format(c.getTime()));
	                                        Date dueDate = df.parse(df.format(reviewer.getDueDate()));

	                                        if (dueDate.compareTo(upcomingDate) < 0) {
	                                            isDueDateExpire = true;
	                                        }
	                                    }
	                                } catch (ParseException e) {
	                                    log.info("Error parsing due date for reviewer ID {}: {}", reviewer.getId(), e.getMessage());
	                                }
	                            }
	                        }

	                        log.debug("ManuscriptId: {}, mcount: {}, mvalidcount: {}, isDueDateExpire: {}",
	                                unique.getManuscriptId(), mcount, mvalidcount, isDueDateExpire);

	                        if (mcount != 0 && mvalidcount != 0 && mcount == mvalidcount) {
	                            map.put(unique.getManuscriptId(), unique);
	                            log.info("Added manuscript ID {} for notification.", unique.getManuscriptId());
	                        }
	                    }
	                }
	            }
	        }
	    }

	    if (map != null && !map.isEmpty()) {
	        log.info("Total manuscripts to notify: {}", map.size());

	        for (Integer manuscriptId : new ArrayList<>(map.keySet())) {
	            log.info("Sending notification for manuscript ID: {}", manuscriptId);

	            try {
	                ManuScript manu = manuScriptRepo.getById(manuscriptId);
	                noResponseNotification(manu);
	            } catch (Exception e) {
	                log.error("Failed to send notification for manuscript ID {}: {}", manuscriptId, e.getMessage(), e);
	            }
	        }
	    } else {
	        log.info("No eligible manuscripts found for notification.");
	    }

	    return "Sucessfully sent No response reviewer notification to Editor/EIC";
	}

	@Override
	public List<ManuScriptForDashboard> getNoResponseReminder() {
	    log.info("Entering getNoResponseReminder() method");
	    
	    List<ManuScriptForDashboard> manuscriptList = new ArrayList<>();
	    Map<Integer, ManuScriptForDashboard> map = new HashMap<>();
	    List<ManuscriptListVO> manuscriptListEditor = new ArrayList<>();
	    List<Integer> manuscriptIdList = new ArrayList<>();

	    List<Journal> JournalLst = journalsRepo.getActivejournal();
	    log.info("Total active journals found: {}", JournalLst.size());

	    for (Journal journal : JournalLst) {
	        log.info("Processing journalId: {}", journal.getJournalId());
	        
	        List<ManuScript> editorDataList = manuScriptRepo.getManuscriptForNoResponseReminder(journal.getJournalId());
	        log.info("Found {} manuscripts for journalId {} with potential no response", 
	                  editorDataList != null ? editorDataList.size() : 0, journal.getJournalId());

	        if (editorDataList != null && !editorDataList.isEmpty()) {
	            manuscriptIdList.clear();
	            for (ManuScript editorData : editorDataList) {
	                Integer statusId = editorData.getManuscriptStatusId();
	                if (statusId != null && (statusId.equals(8) || statusId.equals(66))) {
	                    log.info("Eligible manuscriptId for reminder: {}", editorData.getManuScriptId());
	                    manuscriptIdList.add(editorData.getManuScriptId());
	                }
	            }

	            if (!manuscriptIdList.isEmpty()) {
	                List<ManuScriptReviewer> getAllDataByjournal = manuScriptReviewerRepo
	                        .getManuscriptByJournalEditor(journal.getJournalId(), manuscriptIdList);
	                List<ManuScriptReviewer> getUniqueDataByjournal = getAllDataByjournal;

	                log.info("Reviewer entries fetched for journalId {}: {}", journal.getJournalId(),
	                         getAllDataByjournal != null ? getAllDataByjournal.size() : 0);

	                if (getAllDataByjournal != null && !getAllDataByjournal.isEmpty()) {
	                    for (ManuScriptReviewer uniqueData : getUniqueDataByjournal) {
	                        int mcount = 0;
	                        int mvalidcount = 0;
	                        boolean isDueDateExpire = false;

	                        for (ManuScriptReviewer allData : getAllDataByjournal) {
	                            if (uniqueData.getManuscriptId().equals(allData.getManuscriptId())
	                                    && allData.getManuscriptVersion().equals(uniqueData.getManuscriptVersion())) {

	                                mcount++;
	                                try {
	                                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	                                    Date reviewerDueDate = df.parse(df.format(allData.getDueDate()));
	                                    Date todayDate = df.parse(df.format(new Date()));

	                                    if (allData.getIsActive() == 0
	                                            || ((allData.getManuscriptStatus() == 8
	                                                || allData.getManuscriptStatus() == 66
	                                                || allData.getManuscriptStatus() == 35)
	                                                && reviewerDueDate.before(todayDate))
	                                            || allData.getManuscriptStatus() == 60
	                                            || allData.getManuscriptStatus() == 36) {
	                                        mvalidcount++;
	                                    }

	                                    if (allData.getDueDate() != null) {
	                                        Calendar c = Calendar.getInstance();
	                                        c.setTime(new Date());
	                                        c.add(Calendar.DATE, 2);
	                                        String output = df.format(c.getTime());

	                                        Date d1 = df.parse(df.format(allData.getDueDate()));
	                                        Date d2 = df.parse(output);

	                                        if (d1.compareTo(d2) < 0) {
	                                            isDueDateExpire = true;
	                                        }
	                                    }

	                                } catch (ParseException e) {
	                                    log.debug("Exception occurred while parsing dates: {}", e.getMessage(), e);
	                                }
	                            }
	                        }

	                        log.info("For manuscriptId {} - Total Count: {}, Valid Count: {}, isDueDateExpire: {}",
	                                uniqueData.getManuscriptId(), mcount, mvalidcount, isDueDateExpire);

	                        if (mcount != 0 && mvalidcount != 0 && mcount == mvalidcount) {
	                            ManuScriptForDashboard manu = manuScriptForDashboardRepo.getById(uniqueData.getManuscriptId());
	                            if (manu != null) {
	                                map.put(uniqueData.getManuscriptId(), manu);
	                                log.info("Added manuscriptId {} to final dashboard list", uniqueData.getManuscriptId());
	                            } else {
	                                log.warn("ManuScriptForDashboard not found for manuscriptId {}", uniqueData.getManuscriptId());
	                            }
	                        }
	                    }
	                } else {
	                    log.warn("No reviewer data found for manuscriptIds: {}", manuscriptIdList);
	                }
	            } else {
	                log.info("No eligible manuscriptIds found for journalId {}", journal.getJournalId());
	            }
	        } else {
	            log.info("No editor data list found for journalId {}", journal.getJournalId());
	        }
	    }

	    if (map != null && !map.isEmpty()) {
	        manuscriptList = new ArrayList<>(map.values());
	        log.info("Total manuscripts added to final list: {}", manuscriptList.size());
	    } else {
	        log.info("No manuscripts matched the no response reminder criteria.");
	    }

	    log.info("Exiting getNoResponseReminder() method");
	    return manuscriptList;
	}

	private void noResponseNotification(ManuScript mst) {

		EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
		emailTriggerVO.setJournalId(mst.getJournalId());
		emailTriggerVO.setUserId(mst.getAssignedUser());
		if (mst.getAssignedUserRole().equals(2))
			emailTriggerVO.setManagingEditorId(mst.getAssignedUser());
		else
			emailTriggerVO.setEditorUserId(mst.getAssignedUser());
		emailTriggerVO.setManagingEditorId(mst.getAssignedUser());
		emailTriggerVO.setManuscriptId(mst.getManuScriptId());
		emailTriggerVO.setManuscriptStatus(mst.getManuscriptStatusId());
		emailTriggerVO.setReceiverRoleId(mst.getAssignedUserRole());
		emailTriggerVO.setTaskId(64);
		emailTriggerVO.setRoleId(mst.getAssignedUserRole());
		String message = emailTriggerService.emailTrigger(emailTriggerVO);

		if (message.equalsIgnoreCase("E-Mail Successfully Trigger")) {
			mst.setNoResponseNotification(1);
			mst.setNoResponseNotificationDate(new Date());
			manuScriptRepo.save(mst);
		}

	}

	@Override
	public int getTotalFinalDecisionManuscriptForDashboardStatusCode(ManuscriptDashboardList manuscriptVO) {

		Integer totalFinalDecisionManuscriptForDashboard = 0;

		totalFinalDecisionManuscriptForDashboard = manuScriptForDashboardRepo
				.getTotalFinalManuscriptForDashboardStatusCodeRole(manuscriptVO.getManuscriptStatusId(),
						manuscriptVO.getJournalId(), manuscriptVO.getRoleId(), manuscriptVO.getStatusCode());

		return totalFinalDecisionManuscriptForDashboard;

	}

	@Override
	public int getTotalFinalDecisionManuscriptForDashboardSearchStatusCode(ManuscriptDashboardList manuscriptVO) {

		Integer totalFinalDecisionManuscriptForDashboardSearch = 0;
		if (manuscriptVO.getUserId() != null && manuscriptVO.getRoleId() != null) {
			totalFinalDecisionManuscriptForDashboardSearch = manuScriptForDashboardRepo
					.getTotalFinalManuscriptForDashboardStatusCodeSearch(manuscriptVO.getManuscriptStatusId(),
							manuscriptVO.getJournalId(), manuscriptVO.getUserId(), manuscriptVO.getRoleId(),
							manuscriptVO.getStatusCode(), manuscriptVO.getSearch());
		} else {
			totalFinalDecisionManuscriptForDashboardSearch = manuScriptForDashboardRepo
					.getTotalFinalManuscriptForDashboardStatusCodeSearch(manuscriptVO.getManuscriptStatusId(),
							manuscriptVO.getJournalId(), manuscriptVO.getStatusCode(), manuscriptVO.getSearch());
		}
		return totalFinalDecisionManuscriptForDashboardSearch;

	}

	@Override
	public List<ManuscriptDashboardList> sortManuscriptList(List<ManuscriptDashboardList> manuscriptList, String sortBy, String sort) {
	    Comparator<ManuscriptDashboardList> comparator = null;

	    if (sortBy.equalsIgnoreCase("manuscriptCode")) {
	        comparator = Comparator.comparing(ManuscriptDashboardList::getManuscripCode);
	    } else if (sortBy.equalsIgnoreCase("manuscriptTypeDetails.manuscriptTitle")) {
	        comparator = Comparator.comparing(ManuscriptDashboardList::getManuscripTitle);
	    } else if (sortBy.equalsIgnoreCase("manuscriptTypeDetails.manuscriptType")) {
	        comparator = Comparator.comparing(ManuscriptDashboardList::getManuScriptType);
	    } else if (sortBy.equalsIgnoreCase("assignedUser")) {
	        comparator = Comparator.comparing(ManuscriptDashboardList::getEditorName);
	    } else if (sortBy.equalsIgnoreCase("user.firstName")) {
	        comparator = Comparator.comparing(ManuscriptDashboardList::getAuthorName);
	    } else if (sortBy.equalsIgnoreCase("statusMaster.statusValue")) {
	        comparator = Comparator.comparing(ManuscriptDashboardList::getStatusCode);
	    }

	    if (comparator != null) {
	        manuscriptList.sort(sort.equalsIgnoreCase("asc") ? comparator : comparator.reversed());
	    } else if (sortBy.equalsIgnoreCase("submissionDate") || sortBy.equalsIgnoreCase("dueDate") || sortBy.equalsIgnoreCase("assignDate")) {
	        boolean isSubmissionDate = sortBy.equalsIgnoreCase("submissionDate");
	        boolean isAssignDate = sortBy.equalsIgnoreCase("assignDate");
	        manuscriptList.sort((o1, o2) -> compareDates(o1, o2, isSubmissionDate, isAssignDate, sort));
	    }

	    return manuscriptList;
	}

	
	private int compareDates(ManuscriptDashboardList o1, ManuscriptDashboardList o2, boolean isSubmissionDate, boolean assignDate, String sort) {
	    if (isSubmissionDate) {
	        return sort.equalsIgnoreCase("asc")
	                ? compareNullableDates(o1.getSubmissionDate(), o2.getSubmissionDate())
	                : compareNullableDates(o2.getSubmissionDate(), o1.getSubmissionDate());
	    } else if(assignDate){
	        return sort.equalsIgnoreCase("asc")
	                ? compareNullableDates(o1.getAssignDate(), o2.getAssignDate())
	                : compareNullableDates(o2.getAssignDate(), o1.getAssignDate());
	    }else {
	        return sort.equalsIgnoreCase("asc")
	                ? compareNullableDates(o1.getDueDate(), o2.getDueDate())
	                : compareNullableDates(o2.getDueDate(), o1.getDueDate());
	    }
	}

	private int compareNullableDates(Date date1, Date date2) {
	    if (date1 == null && date2 == null) {
	        return 0;
	    }
	    if (date1 == null) {
	        return 1;
	    }
	    if (date2 == null) {
	        return -1;
	    }
	    return date1.compareTo(date2);
	}

	@Override
	public Integer getTotalEditorDashboard(ManuscriptDashboardList manuscriptVO) {
		
		List<Tuple> list = manuScriptForDashboardRepo.getManuscriptForEditorTuple(manuscriptVO.getManuscriptStatusId(), manuscriptVO.getJournalId(), manuscriptVO.getUserId());
	    List<Tuple> filterList = new ArrayList<>();
	    for (Tuple tuple : list) {
			if(tuple.get(7, Integer.class).equals(3) || tuple.get(7, Integer.class).equals(50)) {
				if(tuple.get(9, Integer.class).equals(3) && manuscriptVO.getUserId().equals(tuple.get(4, Integer.class))) {
					filterList.add(tuple);
				}
				
			}else {
				filterList.add(tuple);
			}	    	
		}
		return filterList.size();
	}


}
