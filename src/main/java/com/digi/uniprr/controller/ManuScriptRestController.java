package com.digi.uniprr.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.digi.uniprr.model.*;
import com.digi.uniprr.utils.AppConstants;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.digi.uniprr.VO.ArticleInfoAPI;
import com.digi.uniprr.VO.EICDashBoardListVO;
import com.digi.uniprr.VO.EmailTriggerVO;
import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.VO.ImpactArticleDTO;
import com.digi.uniprr.VO.JournalVO;
import com.digi.uniprr.VO.ManuScriptTrackCodeVO;
import com.digi.uniprr.VO.ManuScriptTrackDateVO;
import com.digi.uniprr.VO.ManuScriptTrackJNameVO;
import com.digi.uniprr.VO.ManuScriptTrackTitleVO;
import com.digi.uniprr.VO.ManuScriptTrackVO;
import com.digi.uniprr.VO.ManuScriptVO;
import com.digi.uniprr.VO.ManuscriptDataApiVo;
import com.digi.uniprr.VO.ManuscriptDetailForJleVO;
import com.digi.uniprr.VO.ManuscriptEditorVO;
import com.digi.uniprr.VO.ManuscriptFilterVO;
import com.digi.uniprr.VO.ManuscriptListVO;
import com.digi.uniprr.VO.ManuscriptSimilarityVo;
import com.digi.uniprr.VO.ManuscriptTypeTitleAbstractVO;
import com.digi.uniprr.VO.ManuscriptsDashboardCountVO;
import com.digi.uniprr.VO.ManuscriptsEditorDashboardCountVO;
import com.digi.uniprr.VO.TrackManuScriptVO;
import com.digi.uniprr.VO.UserVO;
import com.digi.uniprr.jdbcConfig.RawJDBC;
import com.digi.uniprr.repository.JournalManuscriptTypeChargeRepo;
import com.digi.uniprr.repository.ManuScriptRepo;
import com.digi.uniprr.repository.ManuscriptEditorRepo;
import com.digi.uniprr.repository.UserJournalRepo;
import com.digi.uniprr.repository.WorkflowRoleRepo;
import com.digi.uniprr.service.AuthoritiesHistoryService;
import com.digi.uniprr.service.CommonService;
import com.digi.uniprr.service.EmailTriggerService;
import com.digi.uniprr.service.JournalService;
import com.digi.uniprr.service.ManuScriptService;
import com.digi.uniprr.service.ManuscriptCommentService;
import com.digi.uniprr.service.StatusMasterService;
import com.digi.uniprr.service.TatEmailConfigurationService;
import com.digi.uniprr.service.UserService;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping(value = "/manuScript")
public class ManuScriptRestController {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ManuScriptService manuscriptService;

	@Autowired
	private StatusMasterService statusMasterService;

	@Autowired
	private EmailTriggerService emailTriggerService;

	@Autowired
	private JournalService journalService;

	@Autowired
	private UserService userService;

	@Autowired
	private CommonService commonService;

	@Autowired
	AuthoritiesHistoryService authoritiesHistoryService;

	@Autowired
	WorkflowRoleRepo workflowRoleRepo;

	@Autowired
	private ManuScriptRepo manuScriptRepo;

	@Autowired
	private UserJournalRepo userJournalRepo;

	@Autowired
	private JournalManuscriptTypeChargeRepo journalManuscriptTypeChargeRepo;

	@Autowired
	private TatEmailConfigurationService tatEmailConfigurationService;

	@Autowired
	private ManuscriptCommentService manuscriptCommentService;

	@Autowired

	ManuscriptEditorRepo manuscriptEditorRepo;

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public ResponseEntity<Object> createManuScript(@RequestBody ManuScript manuScript) {
		manuscriptService.createManuScript(manuScript);
		return new ResponseEntity<Object>("ManuScript created successfully", HttpStatus.CREATED);
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public List<ManuScript> getManuScriptList() {
		List<ManuScript> manuScriptList = manuscriptService.getManuScriptList();
		return manuScriptList;
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public ResponseEntity<Object> updateManuScript(@RequestBody ManuScript manuScript) {
		if (manuscriptService.isExist(manuScript.getManuScriptId())) {
			manuscriptService.updateManuScript(manuScript);
			return new ResponseEntity<Object>("Updated successfully", HttpStatus.OK);
		}
		return new ResponseEntity<Object>("Does not exist", HttpStatus.FORBIDDEN);
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public ResponseEntity<Object> deleteManuScript(@RequestBody ManuScript manuScript) {
		if (manuscriptService.isExist(manuScript.getManuScriptId())) {
			manuscriptService.deleteManuScriptById(manuScript.getManuScriptId());
			return new ResponseEntity<Object>("ManuScript deleted", HttpStatus.OK);
		}
		return new ResponseEntity<Object>("Id does not exist", HttpStatus.FORBIDDEN);
	}

	@RequestMapping(value = "/listById", method = RequestMethod.POST)
	public List<ManuScript> getManuScriptListById(@RequestParam Integer id) {

		List<ManuScript> manuScriptList = manuscriptService.getManuScriptListById(id);
		return manuScriptList;
	}

// assign stage for Editor Login
	@RequestMapping(value = "/sendBackToAuthor", method = RequestMethod.POST)
	public ResponseEntity<?> sendBackToAuthor(@RequestBody ManuScript manuScript) {

		manuscriptService.updateStage(manuScript);
		JSONObject obj = new JSONObject();
		obj.put("Status", "Assigned successfully.");
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@RequestMapping(value = "/startSubmission", method = RequestMethod.POST)
	public ResponseEntity<?> startSubmission(@RequestBody ManuScriptVO vo) {
		JSONObject obj = new JSONObject();
		ManuScript manuScriptModel = new ManuScript();
		try {
			if (vo != null && vo.getId() != null && vo.getAuthorId() != null) {
				manuScriptModel.setJournalId(vo.getId());
				manuScriptModel.setAuthorId(vo.getAuthorId());
				ManuScript manuscriptDetail = manuscriptService.createManuScript(manuScriptModel);
				JournalVO journalVO = journalService.getJournalById(vo.getId());
				obj.put("manuscriptId", manuscriptDetail.getManuScriptId());
				obj.put("manuscriptversion", manuscriptDetail.getManuscriptVersion());
				obj.put("journalId", journalVO.getJournalId());
				obj.put("journalCode", journalVO.getJournalCode());
				obj.put("journalTitle", journalVO.getJournalTitle());
				obj.put("journalShortTitle", journalVO.getJournalShortTitle());
				obj.put("chargeFlag", journalVO.getChargeFlag());
				obj.put("journalBlindType", journalVO.getJournalBlindType());

				if (journalVO.getChargeFlag() != null && journalVO.getChargeFlag().equalsIgnoreCase("yes")) {
					JournalChargeDetails charge = journalService.getJournalChargeDetailsByJournalId(vo.getId());
					if (charge != null) {
						obj.put("fixedCharges", charge.getFixedCharges());
						obj.put("colorCharges", charge.getColorCharges());
						obj.put("blackWhiteCharges", charge.getBlackWhiteCharges());
						obj.put("tableCharges", charge.getTableCharges());
						obj.put("additionalCharges", charge.getAdditionalCharges());
						obj.put("productionCharges", charge.getProductionCharges());
						obj.put("fixedChargeAmount", charge.getFixedChargeAmount());
					}
				}
			} else {
				obj.put("message", "Journal Id & author Id must not be null");
			}
			return new ResponseEntity<Object>(obj, HttpStatus.OK);

		} catch (Exception e) {
			obj.put("message", "Error");
			log.info("Error in undoForRejectedManuscript ::::" + e.getMessage());
			return new ResponseEntity<Object>(obj, HttpStatus.OK);

		}
	}

// assign stage for approveSubmission
	@RequestMapping(value = "/approveSubmission", method = RequestMethod.POST)
	public ResponseEntity<?> approveSubmission(@RequestBody ManuScript manuScript) {

		List<StatusMaster> statusMaster = statusMasterService.getStatusMasterDetailsById(manuScript.getManuScriptId());
		return new ResponseEntity<>(statusMaster, HttpStatus.OK);
	}

// assign stage for approveSubmission
	@RequestMapping(value = "/assignReviewer", method = RequestMethod.POST)
	public ResponseEntity<?> assignReviewer(@RequestBody ManuScript manuScript) {

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/trackManuScriptListById", method = RequestMethod.POST)
	public JSONObject trackManuScriptTestDone(@RequestParam Integer id) {

		JSONObject obj = null;
		List<ManuScriptTrackVO> manulList = new ArrayList<ManuScriptTrackVO>();
		List<ManuScript> manuScriptList = manuscriptService.trackManuScript(id);
		for (ManuScript ms : manuScriptList) {
			ManuScriptTrackVO vo = new ManuScriptTrackVO();
			vo.setManuScriptId(ms.getManuScriptId());
			vo.setJournalTitle(ms.getJournal().getJournalTitle());
			vo.setAuthorName(ms.getUser().getFirstName() + " " + ms.getUser().getLastName());
			vo.setSubmissionDate(ms.getSubmissionDate());
			vo.setManuScriptTitle(ms.getManuscriptTitle());
			vo.setJournalId(ms.getJournal().getJournalId());

			obj = new JSONObject();
			obj.put("ManuscriptId", ms.getManuScriptId());
			obj.put("JournalName", ms.getJournal().getJournalTitle());
			obj.put("AuthorName", ms.getUser().getFirstName() + " " + ms.getUser().getLastName());
			obj.put("Title", ms.getManuscriptTitle());
			obj.put("SubmissionDate", ms.getSubmissionDate());

// manulList.add(obj);
		}
		return obj;
	}

	@RequestMapping(value = "/trackManuScriptList", method = RequestMethod.POST)
	public List<ManuScriptTrackVO> gettrackManuScriptTestDone(@RequestParam Integer id) {

		List<ManuScriptTrackVO> manulList = new ArrayList<>();
		List<ManuScript> manuScriptList = manuscriptService.trackManuScript(id);
		for (ManuScript ms : manuScriptList) {
			ManuScriptTrackVO vo = new ManuScriptTrackVO();
			vo.setManuScriptId(ms.getManuScriptId());
			vo.setJournalTitle(ms.getJournal().getJournalTitle());
			vo.setJournalId(ms.getJournal().getJournalId());
			vo.setAuthorName(ms.getUser().getFirstName() + " " + ms.getUser().getLastName());
			vo.setManuScriptTitle(ms.getManuscriptTitle());
			vo.setSubmissionDate(ms.getSubmissionDate());
			manuScriptList = manuscriptService.gettrackManuScriptList(vo.getManuScriptId(), vo.getJournalId(),
					vo.getSubmissionDate());
			manulList.add(vo);
		}
		return manulList;
	}

	@RequestMapping(value = "/trackManuScriptDateList", method = RequestMethod.POST)
	public ResponseEntity<?> gettrackManuScriptDateTestDone(@RequestParam String submissionDateTo,
			@RequestParam String submissionDateFrom) {
		List<ManuScriptTrackDateVO> statusList = new ArrayList<ManuScriptTrackDateVO>();
		List<ManuScript> manuScriptList = manuscriptService.gettrackManuScriptListDate(submissionDateTo,
				submissionDateFrom);

		for (ManuScript ms : manuScriptList) {
			SimpleDateFormat formatter = new SimpleDateFormat();
			ManuScriptTrackDateVO vo = new ManuScriptTrackDateVO();
			vo.setManuScriptId(ms.getManuScriptId());
			vo.setJournalId(ms.getJournal());
			vo.setManuscriptCode(ms.getManuscriptCode());
			vo.setManuscriptAuthorId(ms.getUser().getId());
			vo.setStatusMaster(ms.getStatusMaster());
			vo.setManuscriptVersion(ms.getManuscriptVersion());
			vo.setSubmissionDate(ms.getSubmissionDate());
			vo.setSubmittedBy(ms.getSubmittedBy());
			vo.setCreatedBy(ms.getCreatedBy());
			vo.setCreatedOn(ms.getCreatedOn());
			vo.setUpdatedBy(ms.getUpdatedBy());
			vo.setUpdatedOn(ms.getUpdatedOn());
			vo.setManuscriptTitle(ms.getManuscriptTitle());
			vo.setSubmissionDateTo(formatter.format(ms.getSubmissionDate()));
			vo.setSubmissionDateFrom(formatter.format(ms.getSubmissionDate()));
			statusList.add(vo);
		}
		return new ResponseEntity<>(statusList, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/trackManuScriptTitleList", method = RequestMethod.POST)
	public ResponseEntity<?> gettrackManuScriptTitleTestDone(@RequestBody ManuScript manuscript) {
		List<ManuScriptTrackTitleVO> statusList = new ArrayList<ManuScriptTrackTitleVO>();
		List<ManuScript> manuScriptList = manuscriptService
				.gettrackManuScriptListTitle(manuscript.getManuscriptTitle());

		for (ManuScript ms : manuScriptList) {
			ManuScriptTrackTitleVO vo = new ManuScriptTrackTitleVO();
			vo.setManuScriptId(ms.getManuScriptId());
			vo.setJournalId(ms.getJournal());
			vo.setManuscriptCode(ms.getManuscriptCode());
			vo.setManuscriptAuthorId(ms.getUser().getId());
			vo.setStatusMaster(ms.getStatusMaster());
			vo.setManuscriptVersion(ms.getManuscriptVersion());
			vo.setSubmissionDate(ms.getSubmissionDate());
			vo.setSubmittedBy(ms.getSubmittedBy());
			vo.setCreatedBy(ms.getCreatedBy());
			vo.setCreatedOn(ms.getCreatedOn());
			vo.setUpdatedBy(ms.getUpdatedBy());
			vo.setUpdatedOn(ms.getUpdatedOn());
			vo.setManuscriptTitle(ms.getManuscriptTitle());
			statusList.add(vo);
		}
		return new ResponseEntity<>(statusList, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/trackManuScriptJTitleList", method = RequestMethod.POST)
	public ResponseEntity<?> gettrackManuScriptJtitleTestDone(@RequestParam String journalTitle) {
		List<ManuScriptTrackJNameVO> statusList = new ArrayList<>();
		List<ManuScript> manuScriptList = manuscriptService.gettrackManuScriptJtitle(journalTitle);

		for (ManuScript ms : manuScriptList) {
			ManuScriptTrackJNameVO vo = new ManuScriptTrackJNameVO();
			vo.setManuScriptId(ms.getManuScriptId());
			vo.setJournalTitle(ms.getJournal().getJournalTitle());
			vo.setAuthorName(ms.getUser().getFirstName() + " " + ms.getUser().getLastName());
			vo.setSubmissionDate(ms.getSubmissionDate());
			vo.setManuScriptTitle(ms.getManuscriptTitle());

			statusList.add(vo);
		}
		return new ResponseEntity<>(statusList, HttpStatus.CREATED);
	}

	@PostMapping("/manuscriptTitleCount")
	public ResponseEntity<?> getmanuscriptTitleCount(@RequestBody ManuscriptTypeTitleAbstractVO manuScriptVO) {
		log.info("Inside ManuscriptRestController getmanuscriptTitleCount");
		JSONObject obj = new JSONObject();
		manuScriptVO.setTitle(manuScriptVO.getTitle().trim());
		manuScriptVO.setTitle(manuScriptVO.getTitle().replaceAll("\\s+", " "));
		try {
			int manuscriptCount = manuscriptService.getManuscriptCountByTitleAndJournals(manuScriptVO);
			if (manuscriptCount > 0) {
				return new ResponseEntity<>(manuscriptCount, HttpStatus.OK);
			} else {
				obj.put("data", "Data Not Found");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Error");
			obj.put("data", "Error");
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}
	
    @PostMapping("/checkTitleSimilarity")
    public ResponseEntity<ManuscriptSimilarityVo> checkTitleSimilarity(@RequestBody ManuscriptTypeTitleAbstractVO vo) {
        ManuscriptSimilarityVo result = manuscriptService.checkTitleSimilarity(vo);
        return ResponseEntity.ok(result);
    }


	@RequestMapping(value = "/trackManuScriptCodeList", method = RequestMethod.POST)
	public ResponseEntity<?> gettrackManuScriptCodeTestDone(@RequestBody ManuScript manuscript) {
		List<ManuScriptTrackCodeVO> statusList = new ArrayList<ManuScriptTrackCodeVO>();
		List<ManuScript> manuScriptList = manuscriptService.gettrackManuScriptListCode(manuscript.getManuscriptCode());

		for (ManuScript ms : manuScriptList) {
			ManuScriptTrackCodeVO vo = new ManuScriptTrackCodeVO();
			vo.setManuScriptId(ms.getManuScriptId());
			vo.setJournalId(ms.getJournal());
			vo.setManuscriptCode(ms.getManuscriptCode());
			vo.setManuscriptAuthorId(ms.getUser().getId());
			vo.setStatusMaster(ms.getStatusMaster());
			vo.setManuscriptVersion(ms.getManuscriptVersion());
			vo.setSubmissionDate(ms.getSubmissionDate());
			vo.setSubmittedBy(ms.getSubmittedBy());
			vo.setCreatedBy(ms.getCreatedBy());
			vo.setCreatedOn(ms.getCreatedOn());
			vo.setUpdatedBy(ms.getUpdatedBy());
			vo.setUpdatedOn(ms.getUpdatedOn());

			statusList.add(vo);
		}
		return new ResponseEntity<>(statusList, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/trackManuScriptTotalList", method = RequestMethod.POST)
	public ResponseEntity<?> trackManuScriptCodeList(@RequestBody ManuscriptFilterVO manuscriptFilterVO) {
		List<TrackManuScriptVO> manuscriptList = commonService.queryManuscript(manuscriptFilterVO);
		return new ResponseEntity<>(manuscriptList, HttpStatus.OK);
	}

	@RequestMapping(value = "/getAllManuScriptTotalList", method = RequestMethod.POST)
	public ResponseEntity<?> getAllManuScriptTotalList(@RequestBody ManuscriptFilterVO manuscriptFilterVO) {
		List<TrackManuScriptVO> manuscriptList = commonService.getManuscript(manuscriptFilterVO);
		return new ResponseEntity<>(manuscriptList, HttpStatus.OK);
	}

	@RequestMapping(value = "/getByJournalIdManuScriptTotalList", method = RequestMethod.POST)
	public ResponseEntity<?> getByJournalIdManuScriptTotalList(@RequestBody ManuscriptFilterVO manuscriptFilterVO) {
		JSONObject obj = new JSONObject();
		try {
			List<TrackManuScriptVO> manuscriptList = commonService.getJournalIdManuscript(manuscriptFilterVO);
			Long totalManuscriptCount = commonService.getTotalManuscriptCount(manuscriptFilterVO);
			obj.put("manuscriptList", manuscriptList);
			obj.put("totalManuscriptCount", totalManuscriptCount);
			return new ResponseEntity<>(obj, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			obj.put("message", "Error");
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@RequestMapping(value = "/getManuscriptByStatus", method = RequestMethod.POST)
	public ResponseEntity<?> getManuscriptByStatus(@RequestBody ManuscriptListVO manuscriptVO) {
		JSONObject obj = new JSONObject();
		try {
			List<ManuscriptListVO> manuscriptList = manuscriptService.getManuscriptByStatus(manuscriptVO);
			obj.put("data", manuscriptList);
		} catch (Exception e) {
			obj.put("data", "Something Went Wrong");
			log.info("Exception :" + e);
			log.error("Exception : " + e.getMessage());
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@RequestMapping(value = "/getManuscriptByStatusForReport", method = RequestMethod.POST)
	public ResponseEntity<?> getManuscriptByStatusForReport(@RequestBody ManuscriptListVO manuscriptVO) {
		JSONObject obj = new JSONObject();
		try {
			List<ManuscriptListVO> manuscriptList = manuscriptService.getManuscriptByJournalAndStatus(manuscriptVO);
			obj.put("data", manuscriptList);
		} catch (Exception e) {
			obj.put("data", "Something Went Wrong");
			log.info("Exception :" + e);
			log.error("Exception : " + e.getMessage());
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@RequestMapping(value = "/getManuscriptByStatusForEditorTotalManuscript", method = RequestMethod.POST)
	public ResponseEntity<?> getManuscriptByStatusForEditorTotalManuscript(@RequestBody ManuscriptListVO manuscriptVO) {
		JSONObject obj = new JSONObject();
		try {
			List<ManuscriptListVO> manuscriptList = manuscriptService
					.getManuscriptByStatusForEditorTotalManuscript(manuscriptVO);
			obj.put("data", manuscriptList);
		} catch (Exception e) {
			obj.put("data", "Something Went Wrong");
			log.info("Exception :" + e);
			log.error("Exception : " + e.getMessage());
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	/************* Ravi **************/
	@RequestMapping(value = "/reviewer/joinReviewer", method = RequestMethod.POST)
	public ResponseEntity<?> joinReviewer(@RequestBody UserVO userVO) {
		JSONObject obj = new JSONObject();
		try {
			User isExist = userService.getByEmail(userVO.getEmail());
			if (isExist != null) {
				obj.put("data", "User already exist.");
				return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
			}
			userService.joinReviewer(userVO);
		} catch (Exception e) {
			obj.put("data", "Something went wrong.");
			return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
		}
		obj.put("data", "User successfully created.");
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@RequestMapping(value = "/listEnableByEditor", method = RequestMethod.GET)
	public ResponseEntity<?> listEnableByEditor() {
		JSONObject obj = new JSONObject();
		List<User> userList = userService.getUserDetailsenabledByEditor();
		if (userList != null) {
			obj.put("data", userList);
			return new ResponseEntity<>(obj, HttpStatus.OK);
		}
		obj.put("data", "Data not found");
		return new ResponseEntity<>(obj, HttpStatus.FORBIDDEN);
	}

	@RequestMapping(value = "/approvedByEditor", method = RequestMethod.POST)
	public ResponseEntity<?> approvedByEditor(@RequestBody IDVO Vo) {
		JSONObject obj = new JSONObject();
		try {
			userService.approvedByEditor(Vo.getId());
		} catch (Exception e) {
			obj.put("data", "Something went wrong.");
			return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
		}
		obj.put("data", "Mailed transfer Successful");
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@RequestMapping(value = "/updateUrl", method = RequestMethod.POST)
	public ResponseEntity<?> updateUrl(@RequestBody ManuScript manuscript) {
		log.info("Inside in manuscript update url");
		JSONObject obj = new JSONObject();
		try {
			log.info("Manuscript Update Url->" + manuscript.toString());
			if (!manuscript.getManuscriptStatusId().equals(59)) {
				manuscriptService.updateUrl(manuscript);
			} else {
				manuscriptService.updateUrlwithoutfile(manuscript);
			}

			ManuScript ms = manuScriptRepo.findByManuscriptCode(manuscript.getManuscriptCode());
			if (ms != null) {
				// Notification Mail Setup
				if (manuscript.getManuscriptStatusId() != null && manuscript.getManuscriptStatusId().equals(57)) {
					// Sending Mail To Author For Author Proofing
					EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
					emailTriggerVO.setJournalId(ms.getJournalId());
					emailTriggerVO.setUserId(ms.getAuthorId());
					emailTriggerVO.setAuthorUserId(ms.getAuthorId());
					emailTriggerVO.setManuscriptId(ms.getManuScriptId());
					emailTriggerVO.setManuscriptStatus(57);
					emailTriggerVO.setReceiverRoleId(4);
					emailTriggerVO.setTaskId(28);
					emailTriggerVO.setRoleId(4);
					log.info("Inside in AuthorRestController updateUrl For Email Trigger Data are:->"
							+ emailTriggerVO.toString());
					log.info("User Receiver Id:->" + ms.getAuthorId());
					log.info("User Receiver Role Id:->" + 4);
					log.info("User Sender Role Id:->" + 4);
					log.info("User Sender Id:->" + ms.getAuthorId());
					log.info("Task Id:->" + 28);
					String message = emailTriggerService.emailTrigger(emailTriggerVO);
					obj.put("data", message);
					log.info("Message of AuthorRestController updateUrl For Email Trigger " + message);
					log.info("End of AuthorRestController updateUrl For Email Trigger ");
				}

				// Author check mail
				if (manuscript.getManuscriptStatusId() != null && manuscript.getManuscriptStatusId().equals(62)) {
					// Sending Mail To Author For Author Proofing
					EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
					emailTriggerVO.setJournalId(ms.getJournalId());
					emailTriggerVO.setUserId(ms.getAuthorId());
					emailTriggerVO.setAuthorUserId(ms.getAuthorId());
					emailTriggerVO.setManuscriptId(ms.getManuScriptId());
					emailTriggerVO.setManuscriptStatus(62);
					emailTriggerVO.setReceiverRoleId(4);
					emailTriggerVO.setTaskId(39);
					emailTriggerVO.setRoleId(4);
					log.info("Inside in AuthorRestController updateUrl For Email Trigger Data are:->"
							+ emailTriggerVO.toString());
					log.info("User Receiver Id:->" + ms.getAuthorId());
					log.info("User Receiver Role Id:->" + 4);
					log.info("User Sender Role Id:->" + 4);
					log.info("User Sender Id:->" + ms.getAuthorId());
					log.info("Task Id:->" + 28);
					String message = emailTriggerService.emailTrigger(emailTriggerVO);
					obj.put("data", message);
					log.info("Message of AuthorRestController updateUrl For Email Trigger " + message);
					log.info("End of AuthorRestController updateUrl For Email Trigger ");
				}

				// Reminder Mail Setup
				EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
				emailTriggerVO.setManuscriptId(ms.getManuScriptId());
				emailTriggerVO.setManuscriptStatus(ms.getManuscriptStatusId());
				emailTriggerVO.setManuscriptVersion(ms.getManuscriptVersion());
				emailTriggerVO.setJournalId(ms.getJournalId());
				emailTriggerVO.setUserId(ms.getAuthorId());
				emailTriggerVO.setTaskId(28);
				emailTriggerVO.setRoleId(4);
				emailTriggerVO.setManagingEditorId(ms.getAssignedUser());
				emailTriggerVO.setReceiverRoleId(4);
				tatEmailConfigurationService.create(emailTriggerVO);

			}
		} catch (Exception e) {
			obj.put("data", "Something went wrong.");
			return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
		}
		obj.put("data", "Data updated");
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@PostMapping(value = "/updateManuscriptDetails")
	public ResponseEntity<?> updateManuscriptDetails(@RequestBody ManuscriptDataApiVo manuscriptDataApiVo) {
		log.info("Inside updateManuscriptDetails");
		log.info(AppConstants.REQUEST_METHOD+" :: "+AppConstants.POST+
				AppConstants.REQUEST_URI+" :: /manuScript/updateManuscriptDetails"+
				AppConstants.REQUEST_BODY+" :: "+manuscriptDataApiVo.toString());
		try {
			int statusId = manuscriptDataApiVo.getManuscriptStatusId();

			ManuScript manuScript = new ManuScript();
			manuScript.setManuscriptCode(manuscriptDataApiVo.getManuscriptCode());
			manuScript.setManuscriptStatusId(manuscriptDataApiVo.getManuscriptStatusId());
			manuScript.setInputFile(manuscriptDataApiVo.getInputFile());
			manuScript.setOutputFile(manuscriptDataApiVo.getOutputFile());
			manuScript.setManuscriptTaskId(String.valueOf(manuscriptDataApiVo.getManuscriptTaskId()));
			manuScript.setFileType(manuscriptDataApiVo.getFileType());
			manuScript.setCommentFromProduction(manuscriptDataApiVo.getCommentFromProduction());

			if (statusId != 59) {
				manuscriptService.updateUrl(manuScript);
			} else {
				manuscriptService.updateUrlwithoutfile(manuScript);
			}

			log.info("Inside updateManuscriptDetails manuscriptCode : " + manuscriptDataApiVo.getManuscriptCode()
					+ " and status id is : " + statusId);

			ManuScript ms = manuScriptRepo.getManuScriptCode(manuscriptDataApiVo.getManuscriptCode());
			if (ms != null) {
				log.info("Inside updateManuscriptDetails manuscript found");
				if (statusId == 57 || statusId == 62 || statusId == 61 || statusId == 65 || statusId == 33) {
					EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
					ManuscriptComment manuscriptComment = new ManuscriptComment();
					manuscriptComment.setManuscriptId(ms.getManuScriptId());
					manuscriptComment.setTaskId(manuscriptDataApiVo.getManuscriptTaskId());
					manuscriptComment.setTaskName(manuscriptDataApiVo.getManuscriptTaskName());
					manuscriptComment.setAuthorEmail(manuscriptDataApiVo.getAuthorEmail());
					manuscriptComment.setComment(manuscriptDataApiVo.getCommentFromProduction());
					if (statusId == 57) {
						emailTriggerVO.setTaskId(28);
						manuscriptComment.setUniprrTask("Author Proof");
						boolean isImpact = RawJDBC.isImpactJournalById(ms.getJournal().getJournalCode());
						List<ImpactArticleDTO> impactList = RawJDBC.fetchImpactData(manuscriptDataApiVo.getManuscriptCode());
						if (isImpact && !impactList.isEmpty() 
								&& impactList.get(0).getAuthorReviewUrl()!=null
								&& !impactList.get(0).getAuthorReviewUrl().isEmpty()){
							emailTriggerVO.setTaskId(94);
							log.info(manuscriptDataApiVo.getManuscriptCode()+" is Impact Article And task is 94");
							manuscriptComment.setUniprrTask("Author Proof Impact");
						}
					} else if (statusId == 62) {
						emailTriggerVO.setTaskId(39);
						manuscriptComment.setUniprrTask("Author Check");
					} else if (statusId == 61) {
						emailTriggerVO.setTaskId(38);
						manuscriptComment.setUniprrTask("Author Proof");
					} else if (statusId == 65) {
						emailTriggerVO.setTaskId(40);
						manuscriptComment.setUniprrTask("Author Check");
					}

					manuscriptCommentService.save(manuscriptComment);

					// Sending Mail To Author For Author check and Proofing
					emailTriggerVO.setJournalId(ms.getJournalId());
					emailTriggerVO.setUserId(ms.getAuthorId());
					emailTriggerVO.setAuthorUserId(ms.getAuthorId());
					emailTriggerVO.setManuscriptId(ms.getManuScriptId());
					emailTriggerVO.setManuscriptStatus(ms.getManuscriptStatusId());
					emailTriggerVO.setReceiverRoleId(4);
					emailTriggerVO.setRoleId(4);

					if (emailTriggerVO.getTaskId() != null && emailTriggerVO.getTaskId().intValue() != 0) {
						String message = emailTriggerService.emailTrigger(emailTriggerVO);
						log.info("Message of Email Trigger response in updateManuscriptDetails : " + message);
					}
				} else {
					// Reminder Mail Setup
					EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
					emailTriggerVO.setManuscriptId(ms.getManuScriptId());
					emailTriggerVO.setManuscriptStatus(ms.getManuscriptStatusId());
					emailTriggerVO.setManuscriptVersion(ms.getManuscriptVersion());
					emailTriggerVO.setJournalId(ms.getJournalId());
					emailTriggerVO.setUserId(ms.getAuthorId());
					emailTriggerVO.setTaskId(28);
					emailTriggerVO.setRoleId(4);
					emailTriggerVO.setManagingEditorId(ms.getAssignedUser());
					emailTriggerVO.setReceiverRoleId(4);
					tatEmailConfigurationService.create(emailTriggerVO);
				}
			} else {
				log.error("In updateManuscriptDetails, Manuscript not found");
			}
		} catch (Exception e) {
			log.error("Exception in updateManuscriptDetails :" + e.getMessage(), e);
			return new ResponseEntity<>("Failed", HttpStatus.OK);
		}

		return new ResponseEntity<>("Success", HttpStatus.OK);
	}

	@PostMapping(value = "/updateManuscriptDetailsWithoutEmail")
	public ResponseEntity<?> updateManuscriptDetailsWithoutEmail(@RequestBody ManuscriptDataApiVo manuscriptDataApiVo) {
		log.info("Inside updateManuscriptDetailsWithoutEmail");
		log.info(AppConstants.REQUEST_METHOD+" :: "+AppConstants.POST+
				AppConstants.REQUEST_URI+" :: /manuScript/updateManuscriptDetailsWithoutEmail"+
				AppConstants.REQUEST_BODY+" :: "+manuscriptDataApiVo.toString());
		try {
			int statusId = manuscriptDataApiVo.getManuscriptStatusId();

			ManuScript manuScript = new ManuScript();
			manuScript.setManuscriptCode(manuscriptDataApiVo.getManuscriptCode());
			manuScript.setManuscriptStatusId(manuscriptDataApiVo.getManuscriptStatusId());
			manuScript.setInputFile(manuscriptDataApiVo.getInputFile());
			manuScript.setOutputFile(manuscriptDataApiVo.getOutputFile());
			manuScript.setManuscriptTaskId(String.valueOf(manuscriptDataApiVo.getManuscriptTaskId()));
			manuScript.setFileType(manuscriptDataApiVo.getFileType());
			manuScript.setCommentFromProduction(manuscriptDataApiVo.getCommentFromProduction());

			if (statusId != 59) {
				manuscriptService.updateUrl(manuScript);
			} else {
				manuscriptService.updateUrlwithoutfile(manuScript);
			}

			log.info("Inside updateManuscriptDetailsWithoutEmail manuscriptCode : "
					+ manuscriptDataApiVo.getManuscriptCode() + " and status id is : " + statusId);

			ManuScript ms = manuScriptRepo.getManuScriptCode(manuscriptDataApiVo.getManuscriptCode());
			if (ms != null) {
				log.info("Inside updateManuscriptDetailsWithoutEmail manuscript found");
				if (statusId == 57 || statusId == 62 || statusId == 61 || statusId == 65 || statusId == 33) {
					ManuscriptComment manuscriptComment = new ManuscriptComment();
					manuscriptComment.setManuscriptId(ms.getManuScriptId());
					manuscriptComment.setTaskId(manuscriptDataApiVo.getManuscriptTaskId());
					manuscriptComment.setTaskName(manuscriptDataApiVo.getManuscriptTaskName());
					manuscriptComment.setAuthorEmail(manuscriptDataApiVo.getAuthorEmail());
					manuscriptComment.setComment(manuscriptDataApiVo.getCommentFromProduction());
					if (statusId == 57) {
						manuscriptComment.setUniprrTask("Author Proof");
					} else if (statusId == 62) {
						manuscriptComment.setUniprrTask("Author Check");
					} else if (statusId == 61) {
						manuscriptComment.setUniprrTask("Author Proof");
					} else if (statusId == 65) {
						manuscriptComment.setUniprrTask("Author Check");
					}
					manuscriptCommentService.save(manuscriptComment);
				}
			} else {
				log.error("In updateManuscriptDetailsWithoutEmail, Manuscript not found");
			}
		} catch (Exception e) {
			log.error("Exception in updateManuscriptDetailsWithoutEmail :" + e.getMessage(), e);
			return new ResponseEntity<>("Failed", HttpStatus.OK);
		}

		return new ResponseEntity<>("Success", HttpStatus.OK);
	}

	@PostMapping(value = "/bulkUpdateManuscriptDetails")
	public ResponseEntity<?> bulkUpdateManuscriptDetails(@RequestBody List<ManuscriptDataApiVo> manuscriptDataApiVOs) {
		log.info("Inside bulkUpdateManuscriptDetails");
		log.info(AppConstants.REQUEST_METHOD+" :: "+AppConstants.POST+
				AppConstants.REQUEST_URI+" :: /manuScript/bulkUpdateManuscriptDetails"+
				AppConstants.REQUEST_BODY+" :: "+manuscriptDataApiVOs.toString());
		try {
			for (ManuscriptDataApiVo manuscriptDataApiVo : manuscriptDataApiVOs) {
				manuscriptService.updateManuscriptDetails(manuscriptDataApiVo);

				ManuScript ms = manuScriptRepo.findByManuscriptCode(manuscriptDataApiVo.getManuscriptCode());
				if (ms != null) {
					// Reminder Mail Setup
					EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
					emailTriggerVO.setManuscriptId(ms.getManuScriptId());
					emailTriggerVO.setManuscriptStatus(ms.getManuscriptStatusId());
					emailTriggerVO.setManuscriptVersion(ms.getManuscriptVersion());
					emailTriggerVO.setJournalId(ms.getJournalId());
					emailTriggerVO.setUserId(ms.getAuthorId());
					emailTriggerVO.setTaskId(28);
					emailTriggerVO.setRoleId(4);
					emailTriggerVO.setManagingEditorId(ms.getAssignedUser());
					emailTriggerVO.setReceiverRoleId(4);
					tatEmailConfigurationService.create(emailTriggerVO);
				}
			}
		} catch (Exception e) {
			log.error("Exception in bulkUpdateManuscriptDetails : " + e.getMessage(), e);
			return new ResponseEntity<>("Failed", HttpStatus.OK);
		}

		return new ResponseEntity<>("Success", HttpStatus.OK);
	}

	@RequestMapping(value = "/getManuscriptByType", method = RequestMethod.POST)
	public ResponseEntity<?> getManuscriptByType(@RequestBody IDVO id) {
		JSONObject obj = new JSONObject();
		try {
			List<ManuscriptListVO> manuscriptList = manuscriptService.getManuscriptByType(id);
			obj.put("data", manuscriptList);
		} catch (Exception e) {
			obj.put("data", "Something went wrong.");
			return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
		}

		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@RequestMapping(value = "/getAssignedEditorbyType", method = RequestMethod.POST)
	public ResponseEntity<?> getAssignedEditorbyType(@RequestBody IDVO id) {
		JSONObject obj = new JSONObject();
		try {
			List<ManuscriptListVO> manuscriptList = manuscriptService.getAssignedEditorbyType(id);
			obj.put("data", manuscriptList);
		} catch (Exception e) {
			obj.put("data", "Something went wrong.");
			return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
		}

		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@RequestMapping(value = "/getReviewedByType", method = RequestMethod.POST)
	public ResponseEntity<?> getReviewedByType(@RequestBody IDVO id) {
		JSONObject obj = new JSONObject();
		try {
			List<ManuscriptListVO> manuscriptList = manuscriptService.getReviewedByType(id);
			obj.put("data", manuscriptList);
		} catch (Exception e) {
			obj.put("data", "Something went wrong.");
			return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
		}

		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@RequestMapping(value = "/getNoResponseByType", method = RequestMethod.POST)
	public ResponseEntity<?> getNoResponseByType(@RequestBody IDVO id) {
		JSONObject obj = new JSONObject();
		try {
			List<ManuscriptListVO> manuscriptList = manuscriptService.getNoResponseByType(id);
			obj.put("data", manuscriptList);
		} catch (Exception e) {
			obj.put("data", "Something went wrong.");
			return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
		}

		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@RequestMapping(value = "/getInReviewByType", method = RequestMethod.POST)
	public ResponseEntity<?> getInReviewByType(@RequestBody IDVO id) {
		JSONObject obj = new JSONObject();
		try {
			List<ManuscriptListVO> manuscriptList = manuscriptService.getInReviewByType(id);
			obj.put("data", manuscriptList);
		} catch (Exception e) {
			obj.put("data", "Something went wrong.");
			return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
		}

		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@RequestMapping(value = "/getFinalDecisionByType", method = RequestMethod.POST)
	public ResponseEntity<?> getFinalDecisionByType(@RequestBody IDVO id) {
		JSONObject obj = new JSONObject();
		try {
			List<ManuscriptListVO> manuscriptList = manuscriptService.getFinalDecisionByType(id);
			obj.put("data", manuscriptList);
		} catch (Exception e) {
			obj.put("data", "Something went wrong.");
			return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
		}

		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@RequestMapping(value = "/insertDataThroughUnitouch", method = RequestMethod.POST)
	public ResponseEntity<?> insertDataThroughUnitouch(@RequestBody ArticleInfoAPI articleInfoAPI) {
		log.info("Inside insertDataThroughUnitouch  :::" + articleInfoAPI);
		log.info(AppConstants.REQUEST_METHOD+" :: "+AppConstants.POST+
				AppConstants.REQUEST_URI+" :: /manuScript/insertDataThroughUnitouch"+
				AppConstants.REQUEST_BODY+" :: "+articleInfoAPI.toString());
		JSONObject obj = new JSONObject();
		String msg;
		try {
			log.info("call manuscriptService.insertDataThroughUnitouch");
			msg = manuscriptService.insertDataThroughUnitouch(articleInfoAPI);
			obj.put("data", msg);
		} catch (Exception e) {
			obj.put("data", "Something went wrong.");
			return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
		}

		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@RequestMapping(value = "/getManuscriptDetailForJle", method = RequestMethod.POST)
	public ResponseEntity<?> getManuscriptDetailForJle(@RequestBody IDVO id) {
		log.info("Inside getManuscriptDetailForJle  :::" + id.getId());
		JSONObject obj = new JSONObject();
		try {
			ManuscriptDetailForJleVO manuscriptDetailForJle = manuscriptService.getManuscriptDetailForJle(id);
			if (manuscriptDetailForJle != null && manuscriptDetailForJle.getManuscriptID() != null) {
				obj.put("Data", manuscriptDetailForJle);
				return new ResponseEntity<>(obj, HttpStatus.OK);
			} else {
				return new ResponseEntity<>("No Data Found", HttpStatus.OK);
			}
		} catch (Exception e) {
			log.info("Exception :::   " + e);
			return new ResponseEntity<>("Something went wrong", HttpStatus.CONFLICT);
		}

	}

	/**
	 * Not in use
	 */
	@Deprecated
	@RequestMapping(value = "/pushDataForJle", method = RequestMethod.POST)
	public ResponseEntity<?> pushDataForJle(@RequestBody ManuscriptDetailForJleVO manuscriptDetailForJle) {
		log.info("Inside pushDataForJle  :::" + manuscriptDetailForJle);
		JSONObject obj = new JSONObject();
		try {
			Boolean flag = manuscriptService.pushDataForJle(manuscriptDetailForJle);
			if (flag) {
				return new ResponseEntity<>(obj, HttpStatus.OK);
			} else {
				return new ResponseEntity<>("Something Went Wrong", HttpStatus.CONFLICT);
			}
		} catch (Exception e) {
			log.info("Exception :::   " + e);
			return new ResponseEntity<>("Something Went Wrong", HttpStatus.CONFLICT);
		}
	}

	@PostMapping("updateChargeComment")
	public ResponseEntity<?> updateChargeComment(@RequestBody IDVO id) {
		JSONObject obj = new JSONObject();
	    try {
	        ManuScript manuScript = manuscriptService.updateChargeComment(id);

	        if (manuScript != null && manuScript.getManuScriptId() != null) {
	            String message = sendNotificationEmails(id, manuScript);

				obj.put("message", message);
				return new ResponseEntity<>(obj, HttpStatus.OK);
	        } else {
				obj.put("data", "Something Went Wrong");
				return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
	        }
	    } catch (Exception e) {
			obj.put("data", "An error occurred while processing the request.");
			return new ResponseEntity<>(obj, HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}

	private String sendNotificationEmails(IDVO id, ManuScript manuScript) {
	    String message = "";
	    int userCountryStatus = Integer.parseInt(manuScriptRepo.getDevelopingCountryStatus(manuScript.getAuthorId()));

	    JournalManuscriptTypeCharge journalManuscriptTypeCharge = journalManuscriptTypeChargeRepo
	            .getJournalManuscriptTypeChargeByManuiscriptId(id.getManuscriptId(), manuScript.getJournalId());

	    String submissionCharge = (journalManuscriptTypeCharge != null)
	            ? journalManuscriptTypeCharge.getCountryChargeOnPublication()
	            : "";

	    if (id.getTaskId().equals(63)
//	            && (submissionCharge.equalsIgnoreCase("SameForAll")
//	                    || (submissionCharge.equalsIgnoreCase("FromDevelopingCountry") && userCountryStatus == 0))
	            && manuScript.getAuthorId() != null) {
	        // Send Email to Author for APC
	        message = sendEmail(id, manuScript, manuScript.getAuthorId(), 4, 4, 46, 0, 0, 2);
	        log.info("Email sent to author for APC payment pending: {} {} {} {}" + manuScript.getManuscriptCode(),
	                manuScript.getAuthorId(), 46,message);
	        
	        // Send Email to EIC/Editor for APC
	        id.setJournalMailTemplatesBody("");
	        id.setJournalMailTemplatesSubject("");
	        message = sendEmail(id, manuScript, manuScript.getAssignedUser(), manuScript.getAssignedUserRole(),manuScript.getAssignedUserRole(), 47,
	                manuScript.getAssignedUser(), manuScript.getAssignedUser(), manuScript.getManuscriptStatusId());
	        log.info("Email sent to EIC/Editor for APC payment acknowledgement: {} {} {} {} "
	                + manuScript.getManuscriptCode(), manuScript.getAssignedUser(), 47,message);
	    }

	    List<Integer> editorList = manuscriptEditorRepo.getListOfEditor(manuScript.getManuScriptId());
	    editorList.stream().filter(i -> !i.equals(id.getUserId())).forEach(i -> {
	        // Send Email to editors who have worked on the manuscript
	        String message1 = sendEmail(id, manuScript, i, 3, manuScript.getAssignedUserRole(), 89, i, 0, 63);
	        log.info("Email trigger to editor who has worked on manuscript: {} {} {} {}"
	                + manuScript.getManuscriptCode(), i, 89, message1);
	    });

	    if (id.getTaskId().equals(2) && manuScript.getJournalId() != null && manuScript.getAuthorId() != null
	            && manuScript.getManuScriptId() != null) {
	        // Send Email to Author for Submission payment
	        message = sendEmail(id, manuScript, manuScript.getAuthorId(), 4, 4, 42, 0, 0, 2);
	        log.info("Email sent to author for Submission payment: {} {} {} {}" + manuScript.getManuscriptCode(),
	                manuScript.getAuthorId(), 42, message);

	        // Send Email to Assign Editors and Managing Editors on that Journal
	        log.info("Inside AuthorRestController submitManuscript for Sending Email to Assign Editor and Managing Editor on that Journal");
	        List<UserJournal> userJournals = userJournalRepo.getUserRoleJournalId(manuScript.getJournalId(), 2);
	        if (userJournals != null && !userJournals.isEmpty()) {
	            userJournals.forEach(userJournal -> {
	                String message1 = sendEmail(id, manuScript, userJournal.getUserId(), 2, 4, 13, 0, userJournal.getUserId(), 2);
	                log.info("Email sent to EIC for Submission payment: {} {} {} {}"
	                        + manuScript.getManuscriptCode(), manuScript.getAuthorId(), 13, message1);
	            });
	        }
	    }

	    return message;
	}
	@RequestMapping(value = "/getAllPayedManuscript", method = RequestMethod.POST)
	public ResponseEntity<?> getAllPayedManuscript(@RequestBody ManuscriptListVO manuscriptVO) {
		JSONObject obj = new JSONObject();
		try {
			List<ManuscriptListVO> manuscriptList = manuscriptService.getAllPayedManuscript(manuscriptVO);
			obj.put("data", manuscriptList);
		} catch (Exception e) {
			obj.put("data", "Something Went Wrong");
			log.error("Exception : " + e.getMessage(), e);
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@RequestMapping(value = "/getManuscriptsDashboardCount", method = RequestMethod.POST)
	public ResponseEntity<?> getManuscriptsDashboardCount(@RequestBody EICDashBoardListVO eicDashBoardListVO) {
		JSONObject obj = new JSONObject();
		try {
			ManuscriptsDashboardCountVO manuscriptList = manuscriptService
					.getAllManuscriptsDashboardCount(eicDashBoardListVO);
			if (manuscriptList != null) {
				obj.put("status", true);
				obj.put("data", manuscriptList);
			} else {
				obj.put("status", false);
			}

		} catch (Exception e) {
			obj.put("data", "Something Went Wrong");
			log.error("Exception : " + e.getMessage(), e);
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@RequestMapping(value = "/getManuscriptsEditorDashboardCount", method = RequestMethod.POST)
	public ResponseEntity<?> getManuscriptsEditorDashboardCount(@RequestBody EICDashBoardListVO eicDashBoardListVO) {
		JSONObject obj = new JSONObject();
		try {
			ManuscriptsEditorDashboardCountVO manuscriptList = manuscriptService
					.getAllManuscriptsEditorDashboardCount(eicDashBoardListVO);
			if (manuscriptList != null) {
				obj.put("status", true);
				obj.put("data", manuscriptList);
			} else {
				obj.put("status", false);
			}

		} catch (Exception e) {
			obj.put("data", "Something Went Wrong");
			log.error("Exception : " + e.getMessage(), e);
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@PostMapping(value = "/updateManuscriptStatus")
	public ResponseEntity<?> updateManuscriptStatus(@RequestBody ManuscriptDataApiVo manuscriptDataApiVo) {
		log.info("Inside updateManuscriptStatus");
		log.info(AppConstants.REQUEST_METHOD+" :: "+AppConstants.POST+
				AppConstants.REQUEST_URI+" :: /manuScript/updateManuscriptStatus"+
				AppConstants.REQUEST_BODY+" :: "+manuscriptDataApiVo.toString());
		try {
			ManuScript manuscript = new ManuScript();
			manuscript.setManuscriptCode(manuscriptDataApiVo.getManuscriptCode());
			manuscript.setManuscriptStatusId(manuscriptDataApiVo.getManuscriptStatusId());

			manuscriptService.updateStatus(manuscript);
		} catch (Exception e) {
			log.error("Exception in updateManuscriptStatus :" + e.getMessage(), e);
			return new ResponseEntity<>("Failed", HttpStatus.OK);
		}

		return new ResponseEntity<>("Success", HttpStatus.OK);
	}

	private String sendEmail(IDVO id, ManuScript manuscript, int userId, int recevierRoleId, int roleId, int taskId,
			int editorUserId, int managingEditorId, int manuscriptStatus) {
		EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
		emailTriggerVO.setJournalId(manuscript.getJournalId());
		emailTriggerVO.setUserId(userId);
		emailTriggerVO.setAuthorUserId(manuscript.getAuthorId());
		emailTriggerVO.setManuscriptId(manuscript.getManuScriptId());
		emailTriggerVO.setManuscriptStatus(manuscriptStatus);
		emailTriggerVO.setReceiverRoleId(recevierRoleId);
		emailTriggerVO.setTaskId(taskId);
		emailTriggerVO.setRoleId(roleId);
		if (id.getJournalMailTemplatesBody() != null && !id.getJournalMailTemplatesBody().isEmpty()) {
			emailTriggerVO.setEmailTemplateBody(id.getJournalMailTemplatesBody());
		}
		if (id.getJournalMailTemplatesSubject() != null && !id.getJournalMailTemplatesSubject().isEmpty()) {
			emailTriggerVO.setEmailTemplateSubject(id.getJournalMailTemplatesSubject());
		}
		if (editorUserId > 0) {
			emailTriggerVO.setEditorUserId(editorUserId);
		}
		if (managingEditorId > 0) {
			emailTriggerVO.setManagingEditorId(managingEditorId);
		}

		String message = emailTriggerService.emailTrigger(emailTriggerVO);
		log.info("Emil Trigger" + message);
		return message;

	}

}