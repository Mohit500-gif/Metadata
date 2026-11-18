package com.digi.uniprr.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.digi.uniprr.VO.EmailTriggerVO;
import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.VO.ManuScriptVO;
import com.digi.uniprr.VO.ManuscriptDataApiVo;
import com.digi.uniprr.VO.ManuscriptEditorVO;
import com.digi.uniprr.VO.ManuscriptListVO;
import com.digi.uniprr.model.AuthoritiesHistory;
import com.digi.uniprr.model.DevelopedCountriesByManuscriptType;
import com.digi.uniprr.model.JournalManuscriptTypeCharge;
import com.digi.uniprr.model.ManscriptScore;
import com.digi.uniprr.model.ManuScript;
import com.digi.uniprr.model.ManuscriptVisualization;
import com.digi.uniprr.model.UserJournal;
import com.digi.uniprr.repository.AuthoritiesHistoryRepo;
import com.digi.uniprr.repository.DevelopedCountriesByManuscriptTypeRepo;
import com.digi.uniprr.repository.MailQueueRepo;
import com.digi.uniprr.repository.ManuScriptRepo;
import com.digi.uniprr.repository.ManuscriptEditorRepo;
import com.digi.uniprr.repository.UserJournalRepo;
import com.digi.uniprr.service.EmailTriggerService;
import com.digi.uniprr.service.ManuScriptService;
import com.digi.uniprr.utils.AppConstants;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping(value = "/manuscript")
public class ManuScriptController {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ManuScriptService manuScriptService;

	@Autowired
	private UserJournalRepo userJournalRepo;

	@Autowired
	private ManuscriptEditorRepo manuscriptEditorRepo;
	@Autowired
	private ManuScriptRepo manuScriptRepo;

	@Autowired
	private EmailTriggerService emailTriggerService;

	@Autowired
	private AuthoritiesHistoryRepo authoritiesHistoryRepo;
	
	@Autowired
	MailQueueRepo mailQueueRepo;

	@Autowired
	DevelopedCountriesByManuscriptTypeRepo developedCountriesByManuscriptTypeRepo;

	@RequestMapping(value = "/createManuScript", method = RequestMethod.POST)
	public ResponseEntity<Object> createManuScript(@RequestBody ManuScript manuScript) {
		log.info("Inside In manuscript createManuScript");
		if (manuScriptService.isExist(manuScript.getManuScriptId())) {
			return new ResponseEntity<Object>("Id already exist", HttpStatus.CONFLICT);
		}
		manuScriptService.createManuScript(manuScript);
		return new ResponseEntity<Object>("ManuScript created successfully", HttpStatus.CREATED);
	}

	@RequestMapping(value = "/getManuScriptList", method = RequestMethod.GET)
	public List<ManuScript> getManuScriptList() {
		log.info("Inside In manuscript getManuScriptList");
		List<ManuScript> manuScriptList = manuScriptService.getManuScriptList();
		return manuScriptList;
	}

	@RequestMapping(value = "/updateManuScript", method = RequestMethod.GET)
	public ResponseEntity<Object> updateManuScript(@RequestBody ManuScript manuScript) {
		log.info("Inside In manuscript updateManuScript");
		if (manuScriptService.isExist(manuScript.getManuScriptId())) {
			manuScriptService.updateManuScript(manuScript);
			return new ResponseEntity<Object>("Updated successfully", HttpStatus.OK);
		}
		return new ResponseEntity<Object>("Does not exist", HttpStatus.FORBIDDEN);
	}

	@RequestMapping(value = "/deleteManuScript", method = RequestMethod.POST)
	public ResponseEntity<Object> deleteManuScript(@RequestBody ManuScript manuScript) {
		log.info("Inside In manuscript deleteManuScript");
		if (manuScriptService.isExist(manuScript.getManuScriptId())) {
			manuScriptService.deleteManuScriptById(manuScript.getManuScriptId());
			return new ResponseEntity<Object>("ManuScript deleted", HttpStatus.OK);
		}
		return new ResponseEntity<Object>("Id does not exist", HttpStatus.FORBIDDEN);
	}

	@RequestMapping(value = "/getManuScriptListById", method = RequestMethod.POST)
	public List<ManuScript> getManuScriptListById(@RequestParam Integer id) {
		log.info("Inside In manuscript getManuScriptListById");
		List<ManuScript> manuScriptList = manuScriptService.getManuScriptListById(id);
		return manuScriptList;
	}

	@PostMapping("getManuscriptDataByManuscriptId")
	public ResponseEntity<?> getManuscriptDataByManuscriptId(@RequestBody IDVO id) {
		log.info(AppConstants.REQUEST_METHOD + " :: " + AppConstants.POST + AppConstants.REQUEST_URI
				+ " :: /manuscript/getManuscriptDataByManuscriptId" + AppConstants.REQUEST_BODY + " ::"
				+ id.toString());
		log.info("Inside In manuscript getManuscriptData");
		JSONObject obj = new JSONObject();
		try {
			ManuScriptVO manuScriptList = manuScriptService.getManuscriptData(id);
			if (manuScriptList != null) {
				obj.put("data", manuScriptList);
				return new ResponseEntity<Object>(obj, HttpStatus.OK);
			} else {
				log.info("Data Not Found");
				obj.put("data", "Data Not Found");
				return new ResponseEntity<Object>(obj, HttpStatus.OK);
			}

		} catch (Exception e) {
			log.info("Error");
			obj.put("data", "Error");
			return new ResponseEntity<Object>(obj, HttpStatus.OK);
		}

	}

	@PostMapping("getManuscripDataForReviwer")
	public ResponseEntity<?> getManuscripDataForReviwer(@RequestBody IDVO id) {
		log.info(AppConstants.REQUEST_METHOD + " :: " + AppConstants.POST + AppConstants.REQUEST_URI
				+ " :: /manuscript/getManuscripDataForReviwer" + AppConstants.REQUEST_BODY + " ::" + id.toString());
		log.info("Inside In manuscript getManuscriptData");
		JSONObject obj = new JSONObject();
		try {
			ManuScriptVO manuScriptList = manuScriptService.getManuscriptDataForReviewer(id);
			if (manuScriptList != null) {
				obj.put("data", manuScriptList);
				return new ResponseEntity<Object>(obj, HttpStatus.OK);
			} else {
				log.info("Data Not Found");
				obj.put("data", "Data Not Found");
				return new ResponseEntity<Object>(obj, HttpStatus.OK);
			}

		} catch (Exception e) {
			log.info("Error");
			obj.put("data", "Error");
			return new ResponseEntity<Object>(obj, HttpStatus.OK);
		}

	}

	@RequestMapping(value = "/trackManuScript", method = RequestMethod.POST)
	public List<ManuScript> trackManuScript(@RequestParam Integer id) {
		log.info("Inside In manuscript trackManuScript");
		List<ManuScript> manuScriptList = manuScriptService.getManuScriptListById(id);
		return manuScriptList;
	}

	@RequestMapping(value = "/startSubmission", method = RequestMethod.POST)
	public ResponseEntity<?> startSubmission(@RequestBody IDVO vo) {
		log.info("Inside In manuscript startSubmission");
		JSONObject obj = new JSONObject();
		try {
			ManuScript manuScriptModel = new ManuScript();
			if (vo != null && vo.getId() != null)
				manuScriptModel.setJournalId(vo.getId());
			ManuScript manuscript = manuScriptService.createManuScript(manuScriptModel);
			obj.put("manuscriptId", manuscript.getManuScriptId());
			obj.put("manuscriptCode", manuscript.getManuscriptCode());
			return new ResponseEntity<Object>(obj, HttpStatus.OK);
		} catch (Exception e) {
			obj.put("message", "Error");
			log.info("Error in undoForRejectedManuscript ::::" + e.getMessage());
			return new ResponseEntity<Object>(obj, HttpStatus.OK);
		}
	}

	@PostMapping("undoForRejectedManuscript")
	public ResponseEntity<?> undoForRejectedManuscript(@RequestBody ManuscriptEditorVO manuscriptEditorVO) {
		log.info("Inside In manuscript undoForRejectedManuscript");
		JSONObject obj = new JSONObject();
		try {
			ManuScript getManuScriptData = manuScriptRepo.getOne(manuscriptEditorVO.getManuscriptId());
			String manu = manuScriptService.undoForRejectedManuscript(manuscriptEditorVO, getManuScriptData);
			obj.put("message", manu);

			// Email trigger to author Reject author withdrwan by EIC

			return new ResponseEntity<Object>(obj, HttpStatus.OK);
		} catch (Exception e) {
			obj.put("message", "Error");
			log.info("Error in undoForRejectedManuscript ::::" + e.getMessage());
			return new ResponseEntity<Object>(obj, HttpStatus.OK);
		}

	}

	@PostMapping("rejectForWithdrawn")
	public ResponseEntity<?> rejectForWithdrawn(@RequestBody ManuscriptEditorVO manuscriptEditorVO) {
		log.info("Inside manuscript rejectForWithdrawn");
		JSONObject responseObj = new JSONObject();

		try {
			ManuScript manuscript = manuScriptRepo.findById(manuscriptEditorVO.getManuscriptId())
					.orElseThrow(() -> new IllegalArgumentException("Manuscript not found"));

			String rejectionMessage = manuScriptService.rejectForWithdrwan(manuscriptEditorVO, manuscript);
			responseObj.put("message", rejectionMessage);

			if (rejectionMessage.equalsIgnoreCase("Withdrawn Rejected Successfully")) {
				String emailMessage = sentRejectWithdrawnEmail(manuscriptEditorVO, manuscript);
				responseObj.put("emailData", emailMessage);
				return new ResponseEntity<>(responseObj, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(responseObj, HttpStatus.INTERNAL_SERVER_ERROR);
			}

		} catch (IllegalArgumentException ex) {
			responseObj.put("message", "Manuscript not found");
			log.error("Error processing manuscript rejection:", ex);
			return new ResponseEntity<>(responseObj, HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			responseObj.put("message", "Error");
			log.error("Error processing manuscript rejection:", e);
			return new ResponseEntity<>(responseObj, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("updateManuScriptEditor")
	public ResponseEntity<?> updateManuScriptEditor(@RequestBody ManuscriptEditorVO manuscriptEditorVO) {
		log.info("Inside updateManuScriptEditor");
		log.info(AppConstants.REQUEST_METHOD + " :: " + AppConstants.POST + AppConstants.REQUEST_URI
				+ " :: /manuscript/updateManuScriptEditor" + AppConstants.REQUEST_BODY + " :: "
				+ manuscriptEditorVO.toString());
		JSONObject obj = new JSONObject();

		try {
			if (manuscriptEditorVO != null && manuscriptEditorVO.getStatusCode() != null) {
				if (manuscriptEditorVO.getStatusCode().equals(33)) {
					String withdrawnAllReviewerInvitation = manuScriptService
							.withdrawnAllReviewerInvitation(manuscriptEditorVO);
					obj.put("data", withdrawnAllReviewerInvitation);
				}
			} else {
				log.warn("Invalid manuscriptEditorVO or StatusCode is null");
			}

			String message = sendManuscriptAfpEmail(manuscriptEditorVO);
			if (message.equalsIgnoreCase("Manuscript Updated Successfully")) {
				obj.put("data", message);
				return new ResponseEntity<>(obj, HttpStatus.OK);
			} else {
				log.error("Error in updateManuScriptEditor for manuscript :{} " + manuscriptEditorVO.getManuscriptId());
				obj.put("data", message);
				return new ResponseEntity<>(obj, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (Exception e) {
			log.error("Error in updateManuScriptEditor: " + e.getMessage(), e);
			obj.put("message", "Error");
			return new ResponseEntity<>(obj, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private String sendManuscriptAfpEmail(ManuscriptEditorVO manuscriptEditorVO) {
		try {
			ManuScript manuscript = manuScriptRepo.getById(manuscriptEditorVO.getManuscriptId());

			if (manuscript == null) {
				log.error("Manuscript not found for ID: " + manuscriptEditorVO.getManuscriptId());
				return "Manuscript not found.";
			}

			JournalManuscriptTypeCharge journalManuscriptTypeCharge = manuScriptService
					.getManuscriptChargeByManuscriptId(manuscriptEditorVO.getManuscriptId(), manuscript.getJournalId());
			String manu = manuScriptService.updateManuScriptEditor(manuscriptEditorVO, journalManuscriptTypeCharge);

			if (!manu.equalsIgnoreCase("Manuscript Updated Successfully")) {
				log.error("ManuScript update failed with status: " + manu);
				return manu;
			}

			int manuscriptER = 0;
			String message = "";

			if (manuscriptEditorVO.getStatusCode() == 58) {
				AuthoritiesHistory userObject = authoritiesHistoryRepo
						.getManagingEditorObject(manuscriptEditorVO.getManuscriptId(), manuscript.getJournalId());

				if (userObject == null) {
					manuscriptER = manuscript.getAssignedUser();
				} else {
					manuscriptER = userObject.getPreviousUserId();
				}

				if (manuscriptER != 0) {
					message = sentEmail(manuscriptEditorVO, manuscript, manuscriptER, 2, 4, 15, 0, manuscriptER);
					log.info("Email Trigger: " + message);
				} else {
					ManuScript i = manuScriptRepo.getById(manuscriptEditorVO.getManuscriptId());

					if (i != null) {
						List<UserJournal> j = userJournalRepo.getUserRoleJournalId(i.getJournalId(), 2);

						if (j != null && !j.isEmpty()) {
							j.forEach(p -> {
								sentEmail(manuscriptEditorVO, manuscript, p.getUserId(), 2, 4, 15, 0, p.getUserId());
							});
						}
					}
				}

				// Author Withdrawal request Author Acknowledgement
				message = sentEmail(manuscriptEditorVO, manuscript, manuscript.getAuthorId(), 4, 4, 35, 0,
						manuscriptER);
				log.info("Email Trigger: " + message);
			}

			int userCountryStatus = Integer
					.parseInt(manuScriptRepo.getDevelopingCountryStatus(manuscript.getAuthorId()));

			if (manuscriptEditorVO.getStatusCode() == 34) {
				// Email trigger for author as they deleted their manuscript
				message = sentEmail(manuscriptEditorVO, manuscript, manuscript.getAuthorId(), 4, 4, 12, 0, 0);
				log.info("Email Trigger: " + message);
			}

			if (manuscriptEditorVO.getStatusCode() == 16) {
				// Email trigger for Author accept author withdrawn and sending email
				message = sentEmail(manuscriptEditorVO, manuscript, manuscript.getAuthorId(), 4,
						manuscriptEditorVO.getRoleId(), 16, manuscriptEditorVO.getUserId(),
						manuscriptEditorVO.getUserId());
				log.info("Email Trigger: " + message);

				// Email trigger for Editor accept author withdrawn and sending email
				getBlankEmailTemplateSubject(manuscriptEditorVO);
				message = sentEmail(manuscriptEditorVO, manuscript, manuscriptEditorVO.getUserId(), 2,
						manuscriptEditorVO.getRoleId(), 83, manuscriptEditorVO.getUserId(),
						manuscriptEditorVO.getUserId());
				log.info("Email Trigger: " + message);
			}

			// Email trigger When Eic and editor Accept for production and send it email to
			String publicationCountryCharge = (journalManuscriptTypeCharge != null
					&& journalManuscriptTypeCharge.getCountryChargeOnPublication() != null)
							? journalManuscriptTypeCharge.getCountryChargeOnPublication()
							: "";
			String chargeRequired = (journalManuscriptTypeCharge != null
					&& journalManuscriptTypeCharge.getIsChargesRequired() != null)
							? journalManuscriptTypeCharge.getIsChargesRequired()
							: "";
			String chargeOnPublication = (journalManuscriptTypeCharge != null
					&& journalManuscriptTypeCharge.getChargeOnPublication() != null)
							? journalManuscriptTypeCharge.getChargeOnPublication()
							: "";
			String waveOff = (manuscriptEditorVO.getWaveOff() != null) ? manuscriptEditorVO.getWaveOff() : "";

//	        if (manuscriptEditorVO.getStatusCode() == 33 && "Yes".equalsIgnoreCase(chargeRequired) && "Yes".equalsIgnoreCase(chargeOnPublication)
//	                && ("SameForAll".equalsIgnoreCase(publicationCountryCharge)
//					|| ("FromDevelopingCountry".equalsIgnoreCase(publicationCountryCharge) && userCountryStatus == 0))
//	                && !"Yes".equalsIgnoreCase(waveOff) && StringUtils.isEmpty(manuscriptEditorVO.getPaymentDecline() !=null ? manuscriptEditorVO.getPaymentDecline().trim() : "")) {
			if (manuscriptEditorVO.getStatusCode() == 33 && "Yes".equalsIgnoreCase(chargeRequired)
					&& "Yes".equalsIgnoreCase(chargeOnPublication)
					&& "SameForAll".equalsIgnoreCase(publicationCountryCharge) && !"Yes".equalsIgnoreCase(waveOff)
					&& StringUtils.isEmpty(manuscriptEditorVO.getPaymentDecline() != null
							? manuscriptEditorVO.getPaymentDecline().trim()
							: "")) {
				/*
				 * // APC payment email sent to Author message = sentEmail(manuscriptEditorVO,
				 * manuscript, manuscript.getAuthorId(), 4, manuscriptEditorVO.getRoleId(), 43,
				 * manuscriptEditorVO.getUserId(), manuscriptEditorVO.getUserId());
				 * log.info("Email Trigger for APC: " + message);
				 * 
				 * // APC payment email sent to Editors
				 * getBlankEmailTemplateSubject(manuscriptEditorVO); message =
				 * sentEmail(manuscriptEditorVO, manuscript, manuscript.getAssignedUser(),
				 * manuscriptEditorVO.getRoleId(), manuscriptEditorVO.getRoleId(), 44,
				 * manuscript.getAssignedUser(), manuscript.getAssignedUser());
				 * log.info("Email Trigger: " + message);
				 */
				
			
				// Log the start of sending APC payment email to Author
				log.info("Starting APC payment email process for Author: Manuscript ID: {}, Author ID: {}, Role ID: {}, Task ID: 43, User ID: {}",
				     manuscript.getAuthorId(), manuscriptEditorVO.getRoleId(), manuscriptEditorVO.getUserId());

				// APC payment email sent to Author
				message = sentEmail(manuscriptEditorVO, manuscript, manuscript.getAuthorId(), 4,
				    manuscriptEditorVO.getRoleId(), 43, manuscriptEditorVO.getUserId(),
				    manuscriptEditorVO.getUserId());

				// Log the response after triggering the email to the Author
				log.info("Email Trigger for APC sent to Author - Response: {}", message);

				// Log the start of sending APC payment email to Editors
				log.info("Starting APC payment email process for Editors: Manuscript ID: {}, Assigned User ID: {}, Role ID: {}, Task ID: 44, User ID: {}",
				     manuscript.getAssignedUser(), manuscriptEditorVO.getRoleId(), manuscript.getAssignedUser());

				// Prepare blank email template subject
				getBlankEmailTemplateSubject(manuscriptEditorVO);
				log.debug("Prepared blank email subject for Editors: Subject - {}", manuscriptEditorVO.getJournalMailTemplatesSubject());

				// APC payment email sent to Editors
				message = sentEmail(manuscriptEditorVO, manuscript, manuscript.getAssignedUser(),
				    manuscriptEditorVO.getRoleId(), manuscriptEditorVO.getRoleId(), 44,
				    manuscript.getAssignedUser(), manuscript.getAssignedUser());

				// Log the response after triggering the email to the Editors
				log.info("Email Trigger for APC sent to Editors - Response: {}", message);
} else if (manuscriptEditorVO.getStatusCode() == 33 && "Yes".equalsIgnoreCase(chargeRequired)
					&& "Yes".equalsIgnoreCase(chargeOnPublication)
					&& "FromDevelopingCountry".equalsIgnoreCase(publicationCountryCharge)
					&& !"Yes".equalsIgnoreCase(waveOff)
					&& StringUtils.isEmpty(manuscriptEditorVO.getPaymentDecline() != null
							? manuscriptEditorVO.getPaymentDecline().trim()
							: "")) {

				if (userCountryStatus > 0) {
					DevelopedCountriesByManuscriptType developedCountriesByManuscriptType = developedCountriesByManuscriptTypeRepo
							.getByJournalAndManuscriptType(journalManuscriptTypeCharge.getJournalId(),
									journalManuscriptTypeCharge.getManuscriptTypeId());

					if (developedCountriesByManuscriptType != null && developedCountriesByManuscriptType
							.getPaymentCountriesOnPublishing().equals("India,Others")) {
						if (manuscript != null && manuscriptEditorVO.getManuscriptId() != null
								&& manuscriptEditorVO.getStatusCode() == 33 && manuscriptEditorVO.getUserId() != null
								&& manuscriptEditorVO.getRoleId() != null) {
							// Email trigger to Author when EIC/Editor Accept for production and send it
							// email to
							message = sentEmail(manuscriptEditorVO, manuscript, manuscript.getAuthorId(), 4,
									manuscriptEditorVO.getRoleId(), 17, manuscriptEditorVO.getUserId(),
									manuscriptEditorVO.getUserId());
							log.info("Email Trigger: " + message);

							// Email trigger to editor When Eic and editor Accept for production and send it
							// email to
							getBlankEmailTemplateSubject(manuscriptEditorVO);
							message = sentEmail(manuscriptEditorVO, manuscript, manuscriptEditorVO.getUserId(),
									manuscriptEditorVO.getRoleId(), manuscriptEditorVO.getRoleId(), 36,
									manuscriptEditorVO.getUserId(), manuscriptEditorVO.getUserId());
							log.info("Email Trigger: " + message);
						}
					} else {
						/*
						 * // APC payment email sent to Author message = sentEmail(manuscriptEditorVO,
						 * manuscript, manuscript.getAuthorId(), 4, manuscriptEditorVO.getRoleId(), 43,
						 * manuscriptEditorVO.getUserId(), manuscriptEditorVO.getUserId());
						 * log.info("Email Trigger for APC: " + message);
						 * 
						 * // APC payment email sent to Editors
						 * getBlankEmailTemplateSubject(manuscriptEditorVO); message =
						 * sentEmail(manuscriptEditorVO, manuscript, manuscript.getAssignedUser(),
						 * manuscriptEditorVO.getRoleId(), manuscriptEditorVO.getRoleId(), 44,
						 * manuscript.getAssignedUser(), manuscript.getAssignedUser());
						 * log.info("Email Trigger: " + message);
						 */

						// Log the start of sending the APC payment email to the Author
						log.info(
								"Triggering APC payment email for Author: Manuscript ID: {}, Author ID: {}, Role ID: {}, Task ID: 43, User ID: {}",
								manuscript.getAuthorId(), manuscriptEditorVO.getRoleId(),
								manuscriptEditorVO.getUserId());

						// APC payment email sent to Author
						message = sentEmail(manuscriptEditorVO, manuscript, manuscript.getAuthorId(), 4,
								manuscriptEditorVO.getRoleId(), 43, manuscriptEditorVO.getUserId(),
								manuscriptEditorVO.getUserId());
						log.info("Email Trigger for APC sent to Author - Message: {}", message);

						// Log the start of sending the APC payment email to the Editors
						log.info(
								"Triggering APC payment email for Editors: Manuscript ID: {}, Assigned User ID: {}, Role ID: {}, Task ID: 44, Editor User ID: {}",
								manuscript.getAssignedUser(), manuscriptEditorVO.getRoleId(),
								manuscript.getAssignedUser());

						// APC payment email sent to Editors
						getBlankEmailTemplateSubject(manuscriptEditorVO); // This might not return a value, but we
																			// assume it sets up the subject
						message = sentEmail(manuscriptEditorVO, manuscript, manuscript.getAssignedUser(),
								manuscriptEditorVO.getRoleId(), manuscriptEditorVO.getRoleId(), 44,
								manuscript.getAssignedUser(), manuscript.getAssignedUser());
						log.info("Email Trigger for APC sent to Editors - Message: {}", message);

					}

				} else {
					if (manuscript != null && manuscriptEditorVO.getManuscriptId() != null
							&& manuscriptEditorVO.getStatusCode() == 33 && manuscriptEditorVO.getUserId() != null
							&& manuscriptEditorVO.getRoleId() != null) {
						// Email trigger to Author when EIC/Editor Accept for production and send it
						// email to
						message = sentEmail(manuscriptEditorVO, manuscript, manuscript.getAuthorId(), 4,
								manuscriptEditorVO.getRoleId(), 17, manuscriptEditorVO.getUserId(),
								manuscriptEditorVO.getUserId());
						log.info("Email Trigger: " + message);

						// Email trigger to editor When Eic and editor Accept for production and send it
						// email to
						getBlankEmailTemplateSubject(manuscriptEditorVO);
						message = sentEmail(manuscriptEditorVO, manuscript, manuscriptEditorVO.getUserId(),
								manuscriptEditorVO.getRoleId(), manuscriptEditorVO.getRoleId(), 36,
								manuscriptEditorVO.getUserId(), manuscriptEditorVO.getUserId());
						log.info("Email Trigger: " + message);
					}
				}

			} else {
				if (manuscript != null && manuscriptEditorVO.getManuscriptId() != null
						&& manuscriptEditorVO.getStatusCode() == 33 && manuscriptEditorVO.getUserId() != null
						&& manuscriptEditorVO.getRoleId() != null) {
					// Email trigger to Author when EIC/Editor Accept for production and send it
					// email to
					message = sentEmail(manuscriptEditorVO, manuscript, manuscript.getAuthorId(), 4,
							manuscriptEditorVO.getRoleId(), 17, manuscriptEditorVO.getUserId(),
							manuscriptEditorVO.getUserId());
					log.info("Email Trigger: " + message);

					// Email trigger to editor When Eic and editor Accept for production and send it
					// email to
					getBlankEmailTemplateSubject(manuscriptEditorVO);
					message = sentEmail(manuscriptEditorVO, manuscript, manuscriptEditorVO.getUserId(),
							manuscriptEditorVO.getRoleId(), manuscriptEditorVO.getRoleId(), 36,
							manuscriptEditorVO.getUserId(), manuscriptEditorVO.getUserId());
					log.info("Email Trigger: " + message);
				}
			}

			if (manuscriptEditorVO.getStatusCode() == 30) {
				// Email trigger When Eic and editor Reject for production and send it email to
				message = sentEmail(manuscriptEditorVO, manuscript, manuscript.getAuthorId(), 4,
						manuscriptEditorVO.getRoleId(), 18, manuscriptEditorVO.getUserId(),
						manuscriptEditorVO.getUserId());
				log.info("Email Trigger: " + message);

				// Email trigger When Eic and editor Reject for production and send it email to
				getBlankEmailTemplateSubject(manuscriptEditorVO);
				message = sentEmail(manuscriptEditorVO, manuscript, manuscriptEditorVO.getUserId(),
						manuscriptEditorVO.getRoleId(), manuscriptEditorVO.getRoleId(), 37,
						manuscriptEditorVO.getUserId(), manuscriptEditorVO.getUserId());
				log.info("Email Trigger: " + message);
			}

			if ((manuscriptEditorVO.getRoleId() != null && manuscriptEditorVO.getRoleId() == 2)
					&& manuscript.getAssignedUserRole() == 3) {
				getBlankEmailTemplateSubject(manuscriptEditorVO);
				message = sentEmail(manuscriptEditorVO, manuscript, manuscript.getAssignedUser(),
						manuscript.getAssignedUserRole(), manuscript.getAssignedUserRole(), 86,
						manuscript.getAssignedUser(), 0);
				log.info("Email sent to editor whose authority has been taken by EIC: " + message);
			}

//			if (manuscriptEditorVO.getStatusCode() == 33 || manuscriptEditorVO.getStatusCode() == 30
//					|| manuscriptEditorVO.getStatusCode() == 63) {
//				List<Integer> editorList = manuscriptEditorRepo.getListOfEditor(manuscript.getManuScriptId());
//				getBlankEmailTemplateSubject(manuscriptEditorVO);
//				editorList.stream().filter(i -> !i.equals(manuscriptEditorVO.getUserId())).forEach(i -> {
//					sentEmail(manuscriptEditorVO, manuscript, i, 3, manuscript.getAssignedUserRole(), 89, i, 0);
//				});
//			}
			
			if (manuscriptEditorVO.getStatusCode() == 33 || manuscriptEditorVO.getStatusCode() == 30 
			        || manuscriptEditorVO.getStatusCode() == 63) {
			    List<Integer> editorList = manuscriptEditorRepo.getListOfEditor(manuscript.getManuScriptId());
			    
			    getBlankEmailTemplateSubject(manuscriptEditorVO);

			    editorList.stream()
			        .filter(i -> !i.equals(manuscriptEditorVO.getUserId()))
			        .forEach(i -> {
			            boolean isEmailSent = mailQueueRepo.existsByManuscriptIdAndMailToUserIdAndTaskId(manuscript.getManuScriptId(), i, 89);
			            if (!isEmailSent) {
			                sentEmail(manuscriptEditorVO, manuscript, i, 3, manuscript.getAssignedUserRole(), 89, i, 0);
			            }else {
			            	log.info("Already mail sent for manuscript: "+manuscript.getManuScriptId()+" userId: "+i+" taskid: 89");
			            }
			        });
			}


			return "Manuscript Updated Successfully";
		} catch (Exception e) {
			log.error("Error while sending manuscript AFP email: " + e.getMessage());
			return "An error occurred while processing the request.";
		}
	}

	public ManuscriptEditorVO getBlankEmailTemplateSubject(ManuscriptEditorVO manuscriptEditor) {
		manuscriptEditor.setJournalMailTemplatesBody("");
		manuscriptEditor.setJournalMailTemplatesSubject("");
		return manuscriptEditor;
	}

	@PostMapping(value = "/withdrwalUnitouchToUniprr")
	public ResponseEntity<?> updateManuscriptDetailsWithoutEmail(@RequestBody ManuscriptDataApiVo manuscriptDataApiVo) {
		log.info("Inside withdrwalUnitouchToUniprr...." + manuscriptDataApiVo.getManuscriptCode());
		log.info(AppConstants.REQUEST_METHOD + " :: " + AppConstants.POST + AppConstants.REQUEST_URI
				+ " :: /manuscript/withdrwalUnitouchToUniprr" + AppConstants.REQUEST_BODY + " :: "
				+ manuscriptDataApiVo);
		try {
			ManuScript mvo = manuScriptRepo.getManuscriptByManuscriptCode(manuscriptDataApiVo.getManuscriptCode());
			if (mvo != null) {
				mvo.setManuscriptStatusId(16);
				mvo.setWithdrawnDate(new Date());
				log.debug("------------------------------------------------------" + new Date());
				manuScriptRepo.saveAndFlush(mvo);
			}
		} catch (Exception e) {
			log.error("Exception in withdrwalUnitouchToUniprr :" + e.getMessage(), e);
			return new ResponseEntity<>("Failed", HttpStatus.OK);
		}

		return new ResponseEntity<>("Success", HttpStatus.OK);
	}

	@PostMapping("updateManuScriptEditorForRejection")
	public ResponseEntity<?> updateManuScriptEditorForRejection(@ModelAttribute ManuscriptEditorVO manuscriptEditorVO) {
		log.info("Inside In manuscript updateManuScriptEditor");
		JSONObject obj = new JSONObject();
		try {
			ManuScript getManuScriptData = manuScriptRepo.getById(manuscriptEditorVO.getManuscriptId());
			int userCountryStatus = Integer
					.parseInt(manuScriptRepo.getDevelopingCountryStatus(getManuScriptData.getAuthorId()));
			JournalManuscriptTypeCharge journalManuscriptTypeCharge = manuScriptService
					.getManuscriptChargeByManuscriptId(manuscriptEditorVO.getManuscriptId(),
							getManuScriptData.getJournalId());
			String manu = manuScriptService.updateManuScriptEditor(manuscriptEditorVO, journalManuscriptTypeCharge);
			if (manuscriptEditorVO.getStatusCode() != null && manuscriptEditorVO.getStatusCode().equals(33)) {
				String withdrawnAllReviewerInvitation = manuScriptService
						.withdrawnAllReviewerInvitation(manuscriptEditorVO);
				obj.put("data", withdrawnAllReviewerInvitation);
			}
			obj.put("message", manu);

			if (manuscriptEditorVO.getManuscriptId() != null
					&& manu.equalsIgnoreCase("ManuScript Updated Successfully")) {
				// Email trigger for author as he deleted his manuscript
				if (getManuScriptData != null && manuscriptEditorVO.getManuscriptId() != null
						&& manuscriptEditorVO.getStatusCode() != null
						&& manuscriptEditorVO.getStatusCode().equals(34)) {
					EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
					emailTriggerVO.setJournalId(getManuScriptData.getJournalId());
					emailTriggerVO.setUserId(getManuScriptData.getAuthorId());
					emailTriggerVO.setAuthorUserId(getManuScriptData.getAuthorId());
					emailTriggerVO.setManuscriptId(manuscriptEditorVO.getManuscriptId());
					emailTriggerVO.setManuscriptStatus(manuscriptEditorVO.getStatusCode());
					emailTriggerVO.setReceiverRoleId(4);
					emailTriggerVO.setTaskId(12);
					emailTriggerVO.setRoleId(4);
					if (manuscriptEditorVO.getJournalMailTemplatesBody() != null
							&& !manuscriptEditorVO.getJournalMailTemplatesBody().isEmpty())
						emailTriggerVO.setEmailTemplateBody(manuscriptEditorVO.getJournalMailTemplatesBody());
					if (manuscriptEditorVO.getJournalMailTemplatesSubject() != null
							&& !manuscriptEditorVO.getJournalMailTemplatesSubject().isEmpty())
						emailTriggerVO.setEmailTemplateSubject(manuscriptEditorVO.getJournalMailTemplatesSubject());
					String message = emailTriggerService.emailTrigger(emailTriggerVO);
					obj.put("data", message);
					log.info("Emil Trigger" + message);
				}

				// Email Trigger For Author withdrawn manuscrptand send it to the managing
				// editor
				if (getManuScriptData != null && manuscriptEditorVO.getManuscriptId() != null
						&& manuscriptEditorVO.getStatusCode() != null && manuscriptEditorVO.getStatusCode().equals(58)
						&& getManuScriptData.getAssignedUser() != null) {
					EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
					emailTriggerVO.setJournalId(getManuScriptData.getJournalId());
					emailTriggerVO.setUserId(getManuScriptData.getAssignedUser());
					emailTriggerVO.setManuscriptId(manuscriptEditorVO.getManuscriptId());
					emailTriggerVO.setManuscriptStatus(manuscriptEditorVO.getStatusCode());
					emailTriggerVO.setReceiverRoleId(2);
					emailTriggerVO.setTaskId(15);
					emailTriggerVO.setRoleId(4);
					if (manuscriptEditorVO.getJournalMailTemplatesBody() != null
							&& !manuscriptEditorVO.getJournalMailTemplatesBody().isEmpty())
						emailTriggerVO.setEmailTemplateBody(manuscriptEditorVO.getJournalMailTemplatesBody());
					if (manuscriptEditorVO.getJournalMailTemplatesSubject() != null
							&& !manuscriptEditorVO.getJournalMailTemplatesSubject().isEmpty())
						emailTriggerVO.setEmailTemplateSubject(manuscriptEditorVO.getJournalMailTemplatesSubject());
					emailTriggerVO.setManagingEditorId(getManuScriptData.getAssignedUser());
					String message = emailTriggerService.emailTrigger(emailTriggerVO);
					obj.put("data", message);
					log.info("Emil Trigger" + message);
				}

				// Author Withdrawl request Author Acknowledgement
				if (getManuScriptData != null && manuscriptEditorVO.getManuscriptId() != null
						&& manuscriptEditorVO.getStatusCode() != null
						&& manuscriptEditorVO.getStatusCode().equals(58)) {
					EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
					emailTriggerVO.setJournalId(getManuScriptData.getJournalId());
					emailTriggerVO.setUserId(getManuScriptData.getAuthorId());
					emailTriggerVO.setManuscriptId(manuscriptEditorVO.getManuscriptId());
					emailTriggerVO.setManuscriptStatus(manuscriptEditorVO.getStatusCode());
					emailTriggerVO.setReceiverRoleId(4);
					emailTriggerVO.setTaskId(35);
					emailTriggerVO.setRoleId(4);
					if (manuscriptEditorVO.getJournalMailTemplatesBody() != null
							&& !manuscriptEditorVO.getJournalMailTemplatesBody().isEmpty())
						emailTriggerVO.setEmailTemplateBody(manuscriptEditorVO.getJournalMailTemplatesBody());
					if (manuscriptEditorVO.getJournalMailTemplatesSubject() != null
							&& !manuscriptEditorVO.getJournalMailTemplatesSubject().isEmpty())
						emailTriggerVO.setEmailTemplateSubject(manuscriptEditorVO.getJournalMailTemplatesSubject());
					String message = emailTriggerService.emailTrigger(emailTriggerVO);
					obj.put("data", message);
					log.info("Emil Trigger" + message);
				}

				// Email trigger for managing editor accept author withdrwan and sending email
				// to author
				if (getManuScriptData != null && manuscriptEditorVO.getManuscriptId() != null
						&& manuscriptEditorVO.getStatusCode() != null
						&& manuscriptEditorVO.getStatusCode().equals(16)) {
					EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
					emailTriggerVO.setJournalId(getManuScriptData.getJournalId());
					emailTriggerVO.setUserId(getManuScriptData.getAuthorId());
					emailTriggerVO.setManuscriptId(manuscriptEditorVO.getManuscriptId());
					emailTriggerVO.setManuscriptStatus(manuscriptEditorVO.getStatusCode());
					if (emailTriggerVO.getRoleId() != null && emailTriggerVO.getRoleId().equals(2)) {
						emailTriggerVO.setManagingEditorId(emailTriggerVO.getUserId());
					} else {
						emailTriggerVO.setEditorUserId(emailTriggerVO.getUserId());
					}
					emailTriggerVO.setReceiverRoleId(4);
					emailTriggerVO.setTaskId(16);
					if (manuscriptEditorVO.getJournalMailTemplatesBody() != null
							&& !manuscriptEditorVO.getJournalMailTemplatesBody().isEmpty())
						emailTriggerVO.setEmailTemplateBody(manuscriptEditorVO.getJournalMailTemplatesBody());
					if (manuscriptEditorVO.getJournalMailTemplatesSubject() != null
							&& !manuscriptEditorVO.getJournalMailTemplatesSubject().isEmpty())
						emailTriggerVO.setEmailTemplateSubject(manuscriptEditorVO.getJournalMailTemplatesSubject());
					emailTriggerVO.setRoleId(emailTriggerVO.getRoleId());
					String message = emailTriggerService.emailTrigger(emailTriggerVO);
					obj.put("data", message);
					log.info("Emil Trigger" + message);
				}

				// Email trigger for managing editor reject author withdrwan and sending email
				// to author

				// Email trigger When Eic and editor Accept for production and send it email to
				// Author

				String publicationCountryCharge = journalManuscriptTypeCharge != null
						&& journalManuscriptTypeCharge.getCountryChargeOnPublication() != null
								? journalManuscriptTypeCharge.getCountryChargeOnPublication()
								: "";
				if (manuscriptEditorVO.getStatusCode().equals(33) && journalManuscriptTypeCharge != null
						&& (journalManuscriptTypeCharge.getIsChargesRequired() != null
								? journalManuscriptTypeCharge.getIsChargesRequired()
								: "").equalsIgnoreCase("Yes")
						&& (journalManuscriptTypeCharge.getChargeOnPublication() != null
								? journalManuscriptTypeCharge.getChargeOnPublication()
								: "").equalsIgnoreCase("Yes")
						&& (publicationCountryCharge.equalsIgnoreCase("SameForAll")
								|| (publicationCountryCharge.equalsIgnoreCase("FromDevelopingCountry")
										&& userCountryStatus == 0))) {
					AuthoritiesHistory authoritiesHistory = authoritiesHistoryRepo
							.getAuthoritiesHistoryByStatusById(manuscriptEditorVO.getManuscriptId(), 33);
					EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
					emailTriggerVO.setJournalId(getManuScriptData.getJournalId());
					emailTriggerVO.setUserId(getManuScriptData.getAuthorId());
					emailTriggerVO.setAuthorUserId(getManuScriptData.getAuthorId());
					emailTriggerVO.setManuscriptId(manuscriptEditorVO.getManuscriptId());
					emailTriggerVO.setManuscriptStatus(manuscriptEditorVO.getStatusCode());
					emailTriggerVO.setReceiverRoleId(4);
					emailTriggerVO.setCommentToAuthor(manuscriptEditorVO.getComments());
					emailTriggerVO.setTaskId(43);

					if (manuscriptEditorVO.getRoleId().equals(2)) {
						emailTriggerVO.setRoleId(manuscriptEditorVO.getRoleId());
						emailTriggerVO.setManagingEditorId(authoritiesHistory.getPreviousUserId());
					} else if (manuscriptEditorVO.getRoleId().equals(3)) {
						emailTriggerVO.setRoleId(manuscriptEditorVO.getRoleId());
						emailTriggerVO.setEditorUserId(manuscriptEditorVO.getUserId());
					}
					String message = emailTriggerService.emailTrigger(emailTriggerVO);
					obj.put("data", message);
					log.info("Emil Trigger" + message);

					EmailTriggerVO emailTriggerVO1 = new EmailTriggerVO();
					emailTriggerVO1.setJournalId(getManuScriptData.getJournalId());
					emailTriggerVO1.setUserId(authoritiesHistory.getPreviousUserId());
					emailTriggerVO1.setAuthorUserId(getManuScriptData.getAuthorId());
					emailTriggerVO1.setManuscriptId(manuscriptEditorVO.getManuscriptId());
					emailTriggerVO1.setManuscriptStatus(manuscriptEditorVO.getStatusCode());
					emailTriggerVO1.setReceiverRoleId(manuscriptEditorVO.getRoleId());
					emailTriggerVO1.setCommentToAuthor(manuscriptEditorVO.getComments());
					emailTriggerVO1.setTaskId(44);
					if (manuscriptEditorVO.getRoleId().equals(2)) {
						emailTriggerVO1.setRoleId(manuscriptEditorVO.getRoleId());
						emailTriggerVO1.setManagingEditorId(authoritiesHistory.getPreviousUserId());
					} else if (manuscriptEditorVO.getRoleId().equals(3)) {
						emailTriggerVO1.setRoleId(manuscriptEditorVO.getRoleId());
						emailTriggerVO1.setEditorUserId(manuscriptEditorVO.getUserId());
					}
					String message1 = emailTriggerService.emailTrigger(emailTriggerVO1);
					obj.put("data", message1);
					log.info("Emil Trigger" + message1);

				} else {

					if (getManuScriptData != null && manuscriptEditorVO.getManuscriptId() != null
							&& manuscriptEditorVO.getStatusCode().equals(33) && manuscriptEditorVO.getUserId() != null
							&& manuscriptEditorVO.getRoleId() != null) {
						EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
						emailTriggerVO.setJournalId(getManuScriptData.getJournalId());
						emailTriggerVO.setUserId(getManuScriptData.getAuthorId());
						emailTriggerVO.setAuthorUserId(getManuScriptData.getAuthorId());
						emailTriggerVO.setManuscriptId(manuscriptEditorVO.getManuscriptId());
						emailTriggerVO.setManuscriptStatus(manuscriptEditorVO.getStatusCode());
						emailTriggerVO.setReceiverRoleId(4);
						emailTriggerVO.setCommentToAuthor(manuscriptEditorVO.getComments());
						emailTriggerVO.setTaskId(17);
						if (manuscriptEditorVO.getRoleId().equals(2)) {
							emailTriggerVO.setRoleId(manuscriptEditorVO.getRoleId());
							emailTriggerVO.setManagingEditorId(manuscriptEditorVO.getUserId());
						} else if (manuscriptEditorVO.getRoleId().equals(3)) {
							emailTriggerVO.setRoleId(manuscriptEditorVO.getRoleId());
							emailTriggerVO.setEditorUserId(manuscriptEditorVO.getUserId());
						}
						String message = emailTriggerService.emailTrigger(emailTriggerVO);
						obj.put("data", message);
						log.info("Emil Trigger" + message);
					}

					// Email trigger When Eic and editor Accept for production and send it email to
					// EIC/Editor Acknowledgement
					if (getManuScriptData != null && manuscriptEditorVO.getManuscriptId() != null
							&& manuscriptEditorVO.getStatusCode().equals(33) && manuscriptEditorVO.getUserId() != null
							&& manuscriptEditorVO.getRoleId() != null) {
						EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
						emailTriggerVO.setJournalId(getManuScriptData.getJournalId());
						emailTriggerVO.setUserId(manuscriptEditorVO.getUserId());
						emailTriggerVO.setAuthorUserId(getManuScriptData.getAuthorId());
						emailTriggerVO.setManuscriptId(manuscriptEditorVO.getManuscriptId());
						emailTriggerVO.setManuscriptStatus(manuscriptEditorVO.getStatusCode());
						emailTriggerVO.setReceiverRoleId(manuscriptEditorVO.getRoleId());
						emailTriggerVO.setCommentToAuthor(manuscriptEditorVO.getComments());
						emailTriggerVO.setTaskId(36);
						if (manuscriptEditorVO.getRoleId().equals(2)) {
							emailTriggerVO.setRoleId(manuscriptEditorVO.getRoleId());
							emailTriggerVO.setManagingEditorId(manuscriptEditorVO.getUserId());
						} else if (manuscriptEditorVO.getRoleId().equals(3)) {
							emailTriggerVO.setRoleId(manuscriptEditorVO.getRoleId());
							emailTriggerVO.setEditorUserId(manuscriptEditorVO.getUserId());
						}
						String message = emailTriggerService.emailTrigger(emailTriggerVO);
						obj.put("data", message);
						log.info("Emil Trigger" + message);
					}
				}

				// Email trigger When Eic and editor Reject for production and send it email to
				// Author
				if (getManuScriptData != null && manuscriptEditorVO.getManuscriptId() != null
						&& manuscriptEditorVO.getStatusCode().equals(30) && manuscriptEditorVO.getUserId() != null
						&& manuscriptEditorVO.getRoleId() != null) {
					EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
					emailTriggerVO.setJournalId(getManuScriptData.getJournalId());
					emailTriggerVO.setUserId(getManuScriptData.getAuthorId());
					emailTriggerVO.setAuthorUserId(getManuScriptData.getAuthorId());
					emailTriggerVO.setManuscriptId(manuscriptEditorVO.getManuscriptId());
					emailTriggerVO.setManuscriptStatus(manuscriptEditorVO.getStatusCode());
					emailTriggerVO.setCommentToAuthor(manuscriptEditorVO.getComments());
					emailTriggerVO.setReceiverRoleId(4);
					emailTriggerVO.setTaskId(18);
					if (manuscriptEditorVO.getRoleId().equals(2)) {
						emailTriggerVO.setRoleId(manuscriptEditorVO.getRoleId());
						emailTriggerVO.setManagingEditorId(manuscriptEditorVO.getUserId());
					} else if (manuscriptEditorVO.getRoleId().equals(3)) {
						emailTriggerVO.setRoleId(manuscriptEditorVO.getRoleId());
						emailTriggerVO.setEditorUserId(manuscriptEditorVO.getUserId());
					}
					if (manuscriptEditorVO.getJournalMailTemplatesBody() != null
							&& !manuscriptEditorVO.getJournalMailTemplatesBody().isEmpty())
						emailTriggerVO.setEmailTemplateBody(manuscriptEditorVO.getJournalMailTemplatesBody());
					if (manuscriptEditorVO.getJournalMailTemplatesSubject() != null
							&& !manuscriptEditorVO.getJournalMailTemplatesSubject().isEmpty())
						emailTriggerVO.setEmailTemplateSubject(manuscriptEditorVO.getJournalMailTemplatesSubject());

					String message = emailTriggerService.emailTrigger(emailTriggerVO);
					obj.put("data", message);
					log.info("Emil Trigger" + message);
				}

				// Email trigger When Eic and editor Reject for production and send it email to
				// EIC/Editor
				if (getManuScriptData != null && manuscriptEditorVO.getManuscriptId() != null
						&& manuscriptEditorVO.getStatusCode().equals(30) && manuscriptEditorVO.getUserId() != null
						&& manuscriptEditorVO.getRoleId() != null) {
					EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
					emailTriggerVO.setJournalId(getManuScriptData.getJournalId());
					emailTriggerVO.setUserId(manuscriptEditorVO.getUserId());
					emailTriggerVO.setAuthorUserId(getManuScriptData.getAuthorId());
					emailTriggerVO.setManuscriptId(manuscriptEditorVO.getManuscriptId());
					emailTriggerVO.setManuscriptStatus(manuscriptEditorVO.getStatusCode());
					emailTriggerVO.setReceiverRoleId(manuscriptEditorVO.getRoleId());
					emailTriggerVO.setCommentToAuthor(manuscriptEditorVO.getComments());
					emailTriggerVO.setTaskId(37);
					if (manuscriptEditorVO.getRoleId().equals(2)) {
						emailTriggerVO.setRoleId(manuscriptEditorVO.getRoleId());
						emailTriggerVO.setManagingEditorId(manuscriptEditorVO.getUserId());
					} else if (manuscriptEditorVO.getRoleId().equals(3)) {
						emailTriggerVO.setRoleId(manuscriptEditorVO.getRoleId());
						emailTriggerVO.setEditorUserId(manuscriptEditorVO.getUserId());
					}

					String message = emailTriggerService.emailTrigger(emailTriggerVO);
					obj.put("data", message);
					log.info("Emil Trigger" + message);
				}

				if (manuscriptEditorVO.getRoleId().equals(2) && getManuScriptData.getAssignedUserRole().equals(3)) {
					EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
					emailTriggerVO.setJournalId(getManuScriptData.getJournalId());
					emailTriggerVO.setUserId(getManuScriptData.getAssignedUser());
					emailTriggerVO.setAuthorUserId(getManuScriptData.getAuthorId());
					emailTriggerVO.setManuscriptId(getManuScriptData.getManuScriptId());
					emailTriggerVO.setManuscriptStatus(getManuScriptData.getManuscriptStatusId());
					emailTriggerVO.setReceiverRoleId(getManuScriptData.getAssignedUserRole());
					emailTriggerVO.setTaskId(86);
					emailTriggerVO.setRoleId(getManuScriptData.getAssignedUserRole());
					emailTriggerVO.setEditorUserId(getManuScriptData.getAssignedUser());
					String message = emailTriggerService.emailTrigger(emailTriggerVO);
					obj.put("data", message);
					log.info("Email sent to editor whose authority has been taken by EIC " + message);
				}
				List<Integer> editorList = manuscriptEditorRepo.getListOfEditor(getManuScriptData.getManuScriptId());
				editorList.stream().forEach(i -> {
					if (!i.equals(manuscriptEditorVO.getUserId())) {
						EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
						emailTriggerVO.setJournalId(getManuScriptData.getJournalId());
						emailTriggerVO.setUserId(i);
						emailTriggerVO.setAuthorUserId(getManuScriptData.getAuthorId());
						emailTriggerVO.setManuscriptId(getManuScriptData.getManuScriptId());
						emailTriggerVO.setManuscriptStatus(30);
						emailTriggerVO.setReceiverRoleId(3);
						emailTriggerVO.setTaskId(89);
						emailTriggerVO.setRoleId(getManuScriptData.getAssignedUser());
						emailTriggerVO.setEditorUserId(i);
						String message = emailTriggerService.emailTrigger(emailTriggerVO);
						obj.put("data", message);
						log.info("Email sent to editor who has worked on manuscript " + message);
					}
				});

			}
			return new ResponseEntity<Object>(obj, HttpStatus.OK);
		} catch (Exception e) {
			obj.put("message", "Error");
			e.printStackTrace();
			return new ResponseEntity<Object>(obj, HttpStatus.OK);
		}

	}

	@PostMapping("getUniqueManuScript")
	public ResponseEntity<?> getUniqueManuScript(@RequestParam("journalId") Integer journalId) {
		log.info("Inside In manuscript getUniqueManuScript");
		return new ResponseEntity<Object>(manuScriptService.getUniqueManuScript(journalId), HttpStatus.OK);
	}

	@PostMapping("getManuScriptScoreCardDeatils")
	public ResponseEntity<?> getManuScriptScoreCardDeatils(@RequestBody IDVO idvo) {
		log.info("Inside In manuscript getUniqueManuScript");
		JSONObject obj = new JSONObject();
		try {

			ManscriptScore scoreDeatils = manuScriptService.getManuScriptScoreCardDeatils(idvo);
			if (scoreDeatils != null) {
				obj.put("data", scoreDeatils);
			} else {
				obj.put("message", "Data Not Found");
			}

		} catch (Exception e) {
			obj.put("message", "Error");
		}
		return new ResponseEntity<Object>(obj, HttpStatus.OK);
	}

	@PostMapping("getManuScriptVisualizationDeatils")
	public ResponseEntity<?> getManuScriptVisualizationDeatils(@RequestBody IDVO idvo) {
		log.info("Inside In manuscript getUniqueManuScript");
		JSONObject obj = new JSONObject();
		try {
			ManuscriptVisualization manuscriptVisualization = manuScriptService.getManuScriptVisualizationDeatils(idvo);
			if (manuscriptVisualization != null) {
				obj.put("data", manuscriptVisualization);
			} else {
				obj.put("message", "Data Not Found");
			}

		} catch (Exception e) {
			obj.put("message", "Error");
		}
		return new ResponseEntity<Object>(obj, HttpStatus.OK);
	}

	@PostMapping("getManuscriptStatusListForAuthor")
	public ResponseEntity<?> getManuscriptStatusListForAuthor(@RequestBody IDVO idvo) {
		log.info("Inside In manuscript getUniqueManuScript");
		JSONObject obj = new JSONObject();
		try {
			List<ManuscriptListVO> listOfManuscript = manuScriptService.getManuscriptStatusListForAuthor(idvo);
			if (listOfManuscript != null && !listOfManuscript.isEmpty()) {
				obj.put("data", listOfManuscript);
				return new ResponseEntity<Object>(obj, HttpStatus.OK);
			} else {
				obj.put("data", "Data Not Found");
				return new ResponseEntity<Object>(obj, HttpStatus.OK);
			}

		} catch (Exception e) {
			obj.put("data", "Error");
			return new ResponseEntity<Object>(obj, HttpStatus.EXPECTATION_FAILED);
		}
	}

	@PostMapping("getManuscriptData")
	public ResponseEntity<?> getManuscriptData(@RequestBody IDVO id, HttpServletRequest request) {
		JSONObject obj = new JSONObject();

		try {
			log.info("Inside getManuscriptData");
			ManuscriptListVO manuscriptListVO = manuScriptService.getManuscriptDataIdAndVersion(id.getManuId(),
					id.getVersion(), id.getComment());

			if (StringUtils.isBlank(manuscriptListVO.getManuscripCode())) {
				obj.put("data", "Data Not Found");
			} else if (StringUtils.isNotBlank(manuscriptListVO.getManuscripCode())) {
				boolean xmlResult = manuScriptService.craeatedXmlFileCreate(manuscriptListVO,
						AppConstants.ACCEPT_FOR_PRODUCTION);
				if (xmlResult) {
					obj.put("data", "Xml created");
				} else {
					obj.put("data", "Something Went Wrong");
				}
			}
		} catch (Exception e) {
			String errorMessage = "An error occurred in getManuscriptData: " + e.getMessage();
			log.error(errorMessage);
			obj.put("data", "Error: " + e.getMessage());
		}

		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	/**
	 * This action is intended to be used to manually trigger the process of "Accept
	 * for Production"
	 * 
	 * @param id
	 * @param request
	 * @return
	 */
	@PostMapping("submitManuscritForAFP")
	public ResponseEntity<?> submitManuscritForAFP(@RequestBody IDVO id, HttpServletRequest request) {
		JSONObject obj = new JSONObject();
		log.info("Inside getManuscriptData");
		ManuscriptListVO manuscriptListVO = manuScriptService.getManuscriptDataIdAndVersion(id.getManuId(),
				id.getVersion(), null);
		if (StringUtils.isBlank(manuscriptListVO.getManuscripCode())) {
			obj.put("data", "Data Not Found");
		} else if (StringUtils.isNotBlank(manuscriptListVO.getManuscripCode())) {
			boolean xmlResult = manuScriptService.submitForAFP(manuscriptListVO, AppConstants.ACCEPT_FOR_PRODUCTION);
			if (xmlResult) {
				ManuscriptEditorVO manuscriptEditorVO = new ManuscriptEditorVO();
				manuscriptEditorVO.setManuscriptId(manuscriptListVO.getManuScriptId());
				manuscriptEditorVO.setManuscriptVersion(manuscriptListVO.getManuscriptVersion());
				manuscriptEditorVO.setUserId(manuscriptListVO.getEditorUserId());
				manuscriptEditorVO.setRoleId(manuscriptListVO.getEditorRoleId());
				manuscriptEditorVO.setStatusCode(33);
				manuscriptEditorVO.setComments("Accept for Production");

				manuScriptService.withdrawnAllReviewerInvitation(manuscriptEditorVO);

				String message = sendManuscriptAfpEmail(manuscriptEditorVO);
				obj.put("message", message);
			} else {
				obj.put("data", "Accept for Production failed");
			}
		}

		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@PostMapping("getManuscriptDataForJle")
	public ResponseEntity<?> getManuscriptDataForJle(@RequestBody IDVO id, HttpServletRequest request) {
		log.info("Inside getManuscriptData");
		JSONObject obj = new JSONObject();

		ManuscriptListVO manuscriptListVO = manuScriptService.getManuscriptDataIdAndVersion(id.getManuId(),
				id.getVersion(), null);
		if (StringUtils.isBlank(manuscriptListVO.getManuscripCode())) {
			obj.put("data", "Data Not Found");
		} else if (StringUtils.isNotBlank(manuscriptListVO.getManuscripCode())) {
			boolean xmlResult = manuScriptService.craeatedXmlFileCreateForJle(manuscriptListVO, AppConstants.FOR_JLE);
			if (xmlResult) {
				obj.put("data", "Xml created");
			} else {
				obj.put("data", "Something Went Wrong");
			}
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@PostMapping("getManuscriptZipData")
	public ResponseEntity<?> getManuscriptZipData(@RequestBody IDVO id, HttpServletRequest request) {
		log.info(id.getManuId() + "-------- " + id.getVersion());
		JSONObject obj = new JSONObject();

		ManuscriptListVO manuscriptListVO = manuScriptService.getManuscriptDataIdAndVersionZipfile(id.getManuId(),
				id.getVersion());
		if (StringUtils.isBlank(manuscriptListVO.getManuscripCode())) {
			obj.put("data", "Data Not Found");
		} else if (StringUtils.isNotBlank(manuscriptListVO.getManuscripCode())) {
			boolean zipResult = manuScriptService.craeatedZipFileCreate(manuscriptListVO);
			if (zipResult) {
				obj.put("data", "Zip created");
			} else {
				obj.put("data", "Something Went Wrong");
			}
		}

		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@PostMapping("getWithdrawnCount")
	public ResponseEntity<?> getWithdrawnCount(@RequestBody IDVO id) {
		JSONObject obj = new JSONObject();
		try {
			Integer mailQueueCount = manuScriptService.getWithdrawnCount(id);
			if (mailQueueCount != null) {
				obj.put("status", true);
				obj.put("withdrawnCount", mailQueueCount);
			} else {
				obj.put("status", false);
			}

		} catch (Exception e) {
			obj.put("data", "Something went wrong");
			return new ResponseEntity<>(obj, HttpStatus.OK);
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}
	
	@PostMapping("getManuscriptStatus")
	public  Integer getManuscriptStatus(@RequestBody IDVO id, HttpServletRequest request){
		log.info("Inside In manuscript getUniqueManuScript");
		Integer status =manuScriptRepo.getManuscriptStatus(id.getManuscriptCode());
		log.info("Manuscript Status is :"+status+" for manuscript code : "+id.getManuscriptCode());
		return status;
	}

	private String sentRejectWithdrawnEmail(ManuscriptEditorVO manuscriptEditorVO, ManuScript manuscript) {
		EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
		emailTriggerVO.setJournalId(manuscript.getJournalId());
		emailTriggerVO.setManuscriptId(manuscriptEditorVO.getManuscriptId());
		emailTriggerVO.setRoleId(manuscriptEditorVO.getRoleId());
		String emailMessage = "";
		if ((manuscriptEditorVO.getRoleId().equals(3) || manuscriptEditorVO.getRoleId().equals(2))
				&& manuscriptEditorVO.getTaskId().equals(84)) {
			// For another specific task
			if (manuscriptEditorVO.getRoleId().equals(2)) {
				emailTriggerVO.setManagingEditorId(manuscript.getAssignedUser());
			} else {
				emailTriggerVO.setEditorUserId(manuscriptEditorVO.getUserId());
			}
			emailTriggerVO.setUserId(manuscriptEditorVO.getUserId());
			emailTriggerVO.setManuscriptStatus(manuscriptEditorVO.getStatusCode());
			emailTriggerVO.setReceiverRoleId(2);
			emailTriggerVO.setTaskId(85);
			emailMessage = emailTriggerService.emailTrigger(emailTriggerVO);

		}
		if ((manuscriptEditorVO.getRoleId().equals(3) || manuscriptEditorVO.getRoleId().equals(2))
				&& manuscriptEditorVO.getTaskId().equals(84)) {
			// For specific tasks
			if (manuscriptEditorVO.getRoleId().equals(2)) {
				emailTriggerVO.setManagingEditorId(emailTriggerVO.getUserId());
			} else {
				emailTriggerVO.setEditorUserId(emailTriggerVO.getUserId());
			}
			emailTriggerVO.setUserId(manuscript.getAuthorId());
			emailTriggerVO.setManuscriptStatus(manuscriptEditorVO.getStatusCode());
			if (manuscriptEditorVO.getJournalMailTemplatesBody() != null
					&& !manuscriptEditorVO.getJournalMailTemplatesBody().isEmpty()) {
				emailTriggerVO.setEmailTemplateBody(manuscriptEditorVO.getJournalMailTemplatesBody());
			}
			emailTriggerVO.setReceiverRoleId(4);
			emailTriggerVO.setTaskId(84);
			emailMessage = emailTriggerService.emailTrigger(emailTriggerVO);

		}

		if (manuscriptEditorVO.getRoleId().equals(4) && manuscriptEditorVO.getTaskId().equals(91)) {
			// For specific tasks
			if (manuscriptEditorVO.getRoleId().equals(2)) {
				emailTriggerVO.setManagingEditorId(emailTriggerVO.getUserId());
			} else {
				emailTriggerVO.setEditorUserId(emailTriggerVO.getUserId());
			}
			emailTriggerVO.setUserId(manuscript.getAuthorId());
			emailTriggerVO.setManuscriptStatus(manuscriptEditorVO.getStatusCode());
			emailTriggerVO.setReceiverRoleId(4);
			emailTriggerVO.setTaskId(90);
			emailMessage = emailTriggerService.emailTrigger(emailTriggerVO);

		}
		if (manuscriptEditorVO.getRoleId().equals(4) && manuscriptEditorVO.getTaskId().equals(91)) {
			// For another specific task
			AuthoritiesHistory userObject = authoritiesHistoryRepo
					.getManagingEditorObject(manuscriptEditorVO.getManuscriptId(), manuscript.getJournalId());
			Integer userId = 0;
			if (userObject == null) {
				userId = manuscript.getAssignedUser();
			} else {
				userId = userObject.getPreviousUserId();
			}
			if (manuscriptEditorVO.getRoleId().equals(2)) {
				emailTriggerVO.setManagingEditorId(manuscript.getAssignedUser());
			} else {
				emailTriggerVO.setEditorUserId(userId);
			}
			if (manuscriptEditorVO.getJournalMailTemplatesBody() != null
					&& !manuscriptEditorVO.getJournalMailTemplatesBody().isEmpty()) {
				emailTriggerVO.setEmailTemplateBody(manuscriptEditorVO.getJournalMailTemplatesBody());
			}
			emailTriggerVO.setUserId(userId);
			emailTriggerVO.setManuscriptStatus(manuscriptEditorVO.getStatusCode());
			emailTriggerVO.setReceiverRoleId(2);
			emailTriggerVO.setTaskId(91);
			emailMessage = emailTriggerService.emailTrigger(emailTriggerVO);

		}

		return emailMessage;
	}

	/*
	 * private String sentEmail(ManuscriptEditorVO manuscriptEditorVO, ManuScript
	 * manuscript, int userId, int recevierRoleId, int roleId, int taskId, int
	 * editorUserId, int managingEditorId) { EmailTriggerVO emailTriggerVO = new
	 * EmailTriggerVO(); emailTriggerVO.setJournalId(manuscript.getJournalId());
	 * emailTriggerVO.setUserId(userId);
	 * emailTriggerVO.setAuthorUserId(manuscript.getAuthorId());
	 * emailTriggerVO.setManuscriptId(manuscriptEditorVO.getManuscriptId());
	 * emailTriggerVO.setManuscriptStatus(manuscriptEditorVO.getStatusCode());
	 * emailTriggerVO.setReceiverRoleId(recevierRoleId);
	 * emailTriggerVO.setTaskId(taskId); emailTriggerVO.setRoleId(roleId);
	 * emailTriggerVO.setCommentToAuthor(manuscriptEditorVO.getComments()); if
	 * (manuscriptEditorVO.getJournalMailTemplatesBody() != null &&
	 * !manuscriptEditorVO.getJournalMailTemplatesBody().isEmpty()) {
	 * emailTriggerVO.setEmailTemplateBody(manuscriptEditorVO.
	 * getJournalMailTemplatesBody()); } if
	 * (manuscriptEditorVO.getJournalMailTemplatesSubject() != null &&
	 * !manuscriptEditorVO.getJournalMailTemplatesSubject().isEmpty()) {
	 * emailTriggerVO.setEmailTemplateSubject(manuscriptEditorVO.
	 * getJournalMailTemplatesSubject()); } if (editorUserId > 0) {
	 * emailTriggerVO.setEditorUserId(editorUserId); } if (managingEditorId > 0) {
	 * emailTriggerVO.setManagingEditorId(managingEditorId); }
	 * emailTriggerVO.setCommentToAuthor(manuscriptEditorVO.getComments());
	 * 
	 * String message = emailTriggerService.emailTrigger(emailTriggerVO);
	 * log.info("Emil Trigger" + message); return message;
	 * 
	 * }
	 */
	private String sentEmail(ManuscriptEditorVO manuscriptEditorVO, ManuScript manuscript, int userId,
			int recevierRoleId, int roleId, int taskId, int editorUserId, int managingEditorId) {

		// Log input parameters to trace the incoming request
		log.info(
				"Sending email with the following details: Task ID: {}, User ID: {}, Receiver Role ID: {}, Role ID: {}, Manuscript ID: {}, Editor User ID: {}, Managing Editor ID: {}",
				taskId, userId, recevierRoleId, roleId, manuscriptEditorVO.getManuscriptId(), editorUserId,
				managingEditorId);

		EmailTriggerVO emailTriggerVO = new EmailTriggerVO();

		emailTriggerVO.setJournalId(manuscript.getJournalId());

		emailTriggerVO.setUserId(userId);

		emailTriggerVO.setAuthorUserId(manuscript.getAuthorId());

		emailTriggerVO.setManuscriptId(manuscriptEditorVO.getManuscriptId());

		emailTriggerVO.setManuscriptStatus(manuscriptEditorVO.getStatusCode());

		emailTriggerVO.setReceiverRoleId(recevierRoleId);

		emailTriggerVO.setTaskId(taskId);

		emailTriggerVO.setRoleId(roleId);

		emailTriggerVO.setCommentToAuthor(manuscriptEditorVO.getComments());

		// Check and set the email body template, log accordingly
		if (manuscriptEditorVO.getJournalMailTemplatesBody() != null
				&& !manuscriptEditorVO.getJournalMailTemplatesBody().isEmpty()) {
			emailTriggerVO.setEmailTemplateBody(manuscriptEditorVO.getJournalMailTemplatesBody());
			log.debug("Setting Email Template Body: {}", manuscriptEditorVO.getJournalMailTemplatesBody().substring(0,
					Math.min(50, manuscriptEditorVO.getJournalMailTemplatesBody().length()))); // Truncated for
																								// readability
		} else {
			log.warn("Email Template Body is null or empty.");
		}

		// Check and set the email subject template, log accordingly
		if (manuscriptEditorVO.getJournalMailTemplatesSubject() != null
				&& !manuscriptEditorVO.getJournalMailTemplatesSubject().isEmpty()) {
			emailTriggerVO.setEmailTemplateSubject(manuscriptEditorVO.getJournalMailTemplatesSubject());
			log.debug("Setting Email Template Subject: {}", manuscriptEditorVO.getJournalMailTemplatesSubject());
		} else {
			log.warn("Email Template Subject is null or empty.");
		}

		// Set editor and managing editor IDs if valid
		if (editorUserId > 0) {
			emailTriggerVO.setEditorUserId(editorUserId);
			log.debug("Setting Editor User ID: {}", editorUserId);
		}

		if (managingEditorId > 0) {
			emailTriggerVO.setManagingEditorId(managingEditorId);
			log.debug("Setting Managing Editor ID: {}", managingEditorId);
		}

		// Log the comment to the author
		emailTriggerVO.setCommentToAuthor(manuscriptEditorVO.getComments());
		log.debug("Final Comment to Author: {}", manuscriptEditorVO.getComments());

		// Call the email trigger service and log the response
		String message = emailTriggerService.emailTrigger(emailTriggerVO);
		log.info("Email Trigger Response: {}", message);

		return message;
	}
}
