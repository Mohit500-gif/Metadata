package com.digi.uniprr.controller;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.digi.uniprr.VO.AssignEditorVO;
import com.digi.uniprr.VO.EmailTriggerVO;
import com.digi.uniprr.VO.RevisionToAuthorVO;
import com.digi.uniprr.VO.SendBackToAuthorVo;
import com.digi.uniprr.model.Journal;
import com.digi.uniprr.model.JournalManuscriptTypeCharge;
import com.digi.uniprr.model.ManuScript;
import com.digi.uniprr.model.UserJournal;
import com.digi.uniprr.model.WorkflowRoleSeq;
import com.digi.uniprr.repository.EditorRoleAccessRepo;
import com.digi.uniprr.repository.JournalsRepo;
import com.digi.uniprr.repository.ManuScriptCoAuthorRepo;
import com.digi.uniprr.repository.ManuScriptRepo;
import com.digi.uniprr.repository.UserJournalRepo;
import com.digi.uniprr.repository.UserLoginRepo;
import com.digi.uniprr.repository.UserRoleRepo;
import com.digi.uniprr.repository.WorkflowRoleRepo;
import com.digi.uniprr.service.AssignEditerService;
import com.digi.uniprr.service.CountriesService;
import com.digi.uniprr.service.EmailTriggerService;
import com.digi.uniprr.service.ManuScriptService;
import com.digi.uniprr.service.ManuscriptEditorService;
import com.digi.uniprr.service.RevisionToAuthorService;
import com.digi.uniprr.service.TatEmailConfigurationService;
import com.digi.uniprr.service.UserJournalService;
import com.digi.uniprr.service.UserRoleService;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class RevisionToAuthorRestController {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	AssignEditerService assignEditerService;

	@Autowired
	CountriesService countriesService;

	@Autowired
	UserRoleService userRoleService;

	@Autowired
	UserJournalService userJournalService;

	@Autowired
	UserRoleRepo userRoleRepo;

	@Autowired
	UserJournalRepo userJournalRepo;

	@Autowired
	JournalsRepo journalRepo;

	@Autowired
	UserLoginRepo userRepo;

	@Autowired
	EmailTriggerService emailTriggerService;

	@Autowired
	EditorRoleAccessRepo editorRoleAccessRepo;

	@Autowired
	ManuScriptCoAuthorRepo manuScriptCoAuthorRepo;

	@Autowired
	ManuScriptRepo manuScriptRepo;
	
	@Autowired
	ManuScriptService manuscriptService;

	@Autowired
	WorkflowRoleRepo workflowRoleRepo;
	
	@Autowired
	RevisionToAuthorService revisionToAuthorService;
	
	@Autowired
	ManuscriptEditorService manuscriptEditorService;
	
	@Autowired
	TatEmailConfigurationService tatEmailConfigurationService;
	
	JSONObject obj = new JSONObject();
	
	
	@PostMapping(value = "/sendBackToAuthor", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> revisionToAuthor(@ModelAttribute RevisionToAuthorVO revisionToAuthorVO, HttpServletRequest request){
		
		JSONObject obj1 = new JSONObject();
		JSONObject obj2 = new JSONObject();
		JSONObject obj3 = new JSONObject();
		List<JSONObject> jsonObjects = new ArrayList<JSONObject>();
		
		try {
			uploadSendBackToAuthor(revisionToAuthorVO, request, obj1);
			jsonObjects.add(obj1);
			create(revisionToAuthorVO, obj2);
			jsonObjects.add(obj2);
            processAuthoritiesHistory(revisionToAuthorVO, obj3);
            jsonObjects.add(obj3);
            return new ResponseEntity<>(jsonObjects, HttpStatus.OK);
			
		} catch (Exception e) {
            log.error("An error occurred: {}", e.getMessage());
            obj.put("message", "Internal Server Error");
            return new ResponseEntity<>(obj, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	private void processAuthoritiesHistory(RevisionToAuthorVO revisionToAuthorVO, JSONObject obj) {
		try {

			if (revisionToAuthorVO.getAuthoritiesHistoryVo().getPreviousUserRole() != null
					&& revisionToAuthorVO.getAuthoritiesHistoryVo().getPreviousUserRole().equals(4)
					&& revisionToAuthorVO.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(2)) {

				JournalManuscriptTypeCharge journalManuscriptTypeCharge = manuscriptService
						.getManuscriptChargeByManuscriptId(revisionToAuthorVO.getAuthoritiesHistoryVo().getManuscriptId(),
								revisionToAuthorVO.getAuthoritiesHistoryVo().getJournalId());
				ManuScript i = manuScriptRepo.getOne(revisionToAuthorVO.getAuthoritiesHistoryVo().getManuscriptId());
				int userCountryStatus = Integer.parseInt(manuScriptRepo.getDevelopingCountryStatus(i.getAuthorId()));

				// Checking payment required for article
				if (revisionToAuthorVO.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(2) && journalManuscriptTypeCharge != null) {
					if ((journalManuscriptTypeCharge.getIsChargesRequired() != null
							? journalManuscriptTypeCharge.getIsChargesRequired()
							: "").equalsIgnoreCase("Yes")
							&& (journalManuscriptTypeCharge.getChargeOnSubmission() != null
									? journalManuscriptTypeCharge.getChargeOnSubmission()
									: "").equalsIgnoreCase("Yes")
							&& (journalManuscriptTypeCharge.getChargeOnPublication() != null
									? journalManuscriptTypeCharge.getChargeOnPublication()
									: "").equalsIgnoreCase("No")
							&& (journalManuscriptTypeCharge.getCountryChargeOnSubmission() != null
									? journalManuscriptTypeCharge.getCountryChargeOnSubmission()
									: "").equalsIgnoreCase("SameForAll"))
						revisionToAuthorVO.getAuthoritiesHistoryVo().setManuscriptStatusId(64);
					if ((journalManuscriptTypeCharge.getIsChargesRequired() != null
							? journalManuscriptTypeCharge.getIsChargesRequired()
							: "").equalsIgnoreCase("Yes")
							&& (journalManuscriptTypeCharge.getChargeOnSubmission() != null
									? journalManuscriptTypeCharge.getChargeOnSubmission()
									: "").equalsIgnoreCase("Yes")
							&& (journalManuscriptTypeCharge.getChargeOnPublication() != null
									? journalManuscriptTypeCharge.getChargeOnPublication()
									: "").equalsIgnoreCase("No")
							&& (journalManuscriptTypeCharge.getCountryChargeOnSubmission() != null
									? journalManuscriptTypeCharge.getCountryChargeOnSubmission()
									: "").equalsIgnoreCase("FromDevelopingCountry")) {
						if (userCountryStatus > 0)
							revisionToAuthorVO.getAuthoritiesHistoryVo().setManuscriptStatusId(2);
						else
							revisionToAuthorVO.getAuthoritiesHistoryVo().setManuscriptStatusId(64);
					}

					if ((journalManuscriptTypeCharge.getIsChargesRequired() != null
							? journalManuscriptTypeCharge.getIsChargesRequired()
							: "").equalsIgnoreCase("Yes")
							&& (journalManuscriptTypeCharge.getChargeOnSubmission() != null
									? journalManuscriptTypeCharge.getChargeOnSubmission()
									: "").equalsIgnoreCase("Yes")
							&& (journalManuscriptTypeCharge.getChargeOnPublication() != null
									? journalManuscriptTypeCharge.getChargeOnPublication()
									: "").equalsIgnoreCase("Yes")
							&& (journalManuscriptTypeCharge.getCountryChargeOnSubmission() != null
									? journalManuscriptTypeCharge.getCountryChargeOnSubmission()
									: "").equalsIgnoreCase("SameForAll"))
						revisionToAuthorVO.getAuthoritiesHistoryVo().setManuscriptStatusId(64);
					if ((journalManuscriptTypeCharge.getIsChargesRequired() != null
							? journalManuscriptTypeCharge.getIsChargesRequired()
							: "").equalsIgnoreCase("Yes")
							&& (journalManuscriptTypeCharge.getChargeOnSubmission() != null
									? journalManuscriptTypeCharge.getChargeOnSubmission()
									: "").equalsIgnoreCase("Yes")
							&& (journalManuscriptTypeCharge.getChargeOnPublication() != null
									? journalManuscriptTypeCharge.getChargeOnPublication()
									: "").equalsIgnoreCase("Yes")
							&& (journalManuscriptTypeCharge.getCountryChargeOnSubmission() != null
									? journalManuscriptTypeCharge.getCountryChargeOnSubmission()
									: "").equalsIgnoreCase("FromDevelopingCountry")) {
						if (userCountryStatus > 0)
							revisionToAuthorVO.getAuthoritiesHistoryVo().setManuscriptStatusId(2);
						else
							revisionToAuthorVO.getAuthoritiesHistoryVo().setManuscriptStatusId(64);
					}
				}

				List<Integer> add = new ArrayList<>();
				if (revisionToAuthorVO.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(64)) {
					List<UserJournal> getAdminList = userJournalRepo.getUserRoleJournalId(i.getJournalId(), 1);
					add.add(getAdminList.get(0).getUserId());
					revisionToAuthorVO.getAuthoritiesHistoryVo().setCurrentUserId(add);
					revisionToAuthorVO.getAuthoritiesHistoryVo().setCurrentUserRole(1);

				} else {
					add.add(i.getAssignedUser());
					revisionToAuthorVO.getAuthoritiesHistoryVo().setCurrentUserId(add);
					revisionToAuthorVO.getAuthoritiesHistoryVo().setCurrentUserRole(i.getAssignedUserRole());
				}
				if (i != null) {
					// Due date for eic
					Journal getjournalDetails = journalRepo.getOne(revisionToAuthorVO.getAuthoritiesHistoryVo().getJournalId());
					if (getjournalDetails != null) {
						if (getjournalDetails.getJournalMetadata() != null) {
							if (getjournalDetails.getJournalMetadata().getWorkflowId() != null) {
								List<WorkflowRoleSeq> listOfWorkflowRoleSeq = workflowRoleRepo.getJournalWorkflow(
										getjournalDetails.getJournalMetadata().getWorkflowId(),
										getjournalDetails.getJournalId());
								if (listOfWorkflowRoleSeq != null && !listOfWorkflowRoleSeq.isEmpty()) {
									for (WorkflowRoleSeq l : listOfWorkflowRoleSeq) {

										if (l.getTaskId().equals(13)) {
											revisionToAuthorVO.getAuthoritiesHistoryVo().setDueDate(l.getTat().equals(0) ? new Date()
													: new Date().from(LocalDate.now().plusDays(l.getTat())
															.atStartOfDay(ZoneId.systemDefault()).toInstant()));
										}
									}
								}
							}
						}
					}
					String result = revisionToAuthorService.authoritiesHistory(revisionToAuthorVO);
					obj.put("data", result);
				}
			} else {
				if (revisionToAuthorVO.getAuthoritiesHistoryVo().getManuscriptStatusId() != null
						&& revisionToAuthorVO.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(46)) {
					// Requiring techincal modification
					Journal getjournalDetails = journalRepo.getOne(revisionToAuthorVO.getAuthoritiesHistoryVo().getJournalId());
					if (getjournalDetails != null) {
						if (getjournalDetails.getJournalMetadata() != null) {
							if (getjournalDetails.getJournalMetadata().getWorkflowId() != null) {
								List<WorkflowRoleSeq> listOfWorkflowRoleSeq = workflowRoleRepo.getJournalWorkflow(
										getjournalDetails.getJournalMetadata().getWorkflowId(),
										getjournalDetails.getJournalId());
								if (listOfWorkflowRoleSeq != null && !listOfWorkflowRoleSeq.isEmpty()) {
									for (WorkflowRoleSeq l : listOfWorkflowRoleSeq) {
										if (l.getTaskId().equals(11)) {
											revisionToAuthorVO.getAuthoritiesHistoryVo().setDueDate(l.getTat().equals(0) ? new Date()
													: new Date().from(LocalDate.now().plusDays(l.getTat())
															.atStartOfDay(ZoneId.systemDefault()).toInstant()));
										}
									}
								}	
							}
						}
					}
				}
				if (revisionToAuthorVO.getAuthoritiesHistoryVo().getManuscriptStatusId() != null
						&& revisionToAuthorVO.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(50)) {
					// Author submit as techincal modification
					Journal getjournalDetails = journalRepo.getOne(revisionToAuthorVO.getAuthoritiesHistoryVo().getJournalId());
					ManuScript i = manuScriptRepo.getOne(revisionToAuthorVO.getAuthoritiesHistoryVo().getManuscriptId());
					List<Integer> add = new ArrayList<>();
					add.add(i.getAssignedUser());
					revisionToAuthorVO.getAuthoritiesHistoryVo().setCurrentUserId(add);
					revisionToAuthorVO.getAuthoritiesHistoryVo().setCurrentUserRole(i.getAssignedUserRole());
					if (getjournalDetails != null) {
						if (getjournalDetails.getJournalMetadata() != null) {
							if (getjournalDetails.getJournalMetadata().getWorkflowId() != null) {
								List<WorkflowRoleSeq> listOfWorkflowRoleSeq = workflowRoleRepo.getJournalWorkflow(
										getjournalDetails.getJournalMetadata().getWorkflowId(),
										getjournalDetails.getJournalId());
								if (listOfWorkflowRoleSeq != null && !listOfWorkflowRoleSeq.isEmpty()) {
									for (WorkflowRoleSeq l : listOfWorkflowRoleSeq) {
										if (l.getTaskId().equals(20)) {
											revisionToAuthorVO.getAuthoritiesHistoryVo().setDueDate(l.getTat().equals(0) ? new Date()
													: new Date().from(LocalDate.now().plusDays(l.getTat())
															.atStartOfDay(ZoneId.systemDefault()).toInstant()));
										}
									}
								}
							}
						}
					}
				}
				if (revisionToAuthorVO.getAuthoritiesHistoryVo().getManuscriptStatusId() != null
						&& (revisionToAuthorVO.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(51)
								|| revisionToAuthorVO.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(52)
								|| revisionToAuthorVO.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(68))) {
					// Author submit as techincal modification
					Journal getjournalDetails = journalRepo.getOne(revisionToAuthorVO.getAuthoritiesHistoryVo().getJournalId());
					ManuScript manu = manuScriptRepo.getOne(revisionToAuthorVO.getAuthoritiesHistoryVo().getManuscriptId());
					revisionToAuthorVO.getAuthoritiesHistoryVo().setCurrentUserId(Arrays.asList(manu.getAssignedUser()));
					if (getjournalDetails != null) {
						if (getjournalDetails.getJournalMetadata() != null) {
							if (getjournalDetails.getJournalMetadata().getWorkflowId() != null) {
								List<WorkflowRoleSeq> listOfWorkflowRoleSeq = workflowRoleRepo.getJournalWorkflow(
										getjournalDetails.getJournalMetadata().getWorkflowId(),
										getjournalDetails.getJournalId());
								if (listOfWorkflowRoleSeq != null && !listOfWorkflowRoleSeq.isEmpty()) {
									for (WorkflowRoleSeq l : listOfWorkflowRoleSeq) {
										if (l.getTaskId().equals(8)) {
											revisionToAuthorVO.getAuthoritiesHistoryVo().setDueDate(l.getTat().equals(0) ? new Date()
													: new Date().from(LocalDate.now().plusDays(l.getTat())
															.atStartOfDay(ZoneId.systemDefault()).toInstant()));
										}
									}
								}
							}
						}
					}
				}

				if (revisionToAuthorVO.getAuthoritiesHistoryVo().getManuscriptStatusId() != null
						&& revisionToAuthorVO.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(4)) {
					// Requiring re-submssion
					Journal getjournalDetails = journalRepo.getOne(revisionToAuthorVO.getAuthoritiesHistoryVo().getJournalId());
					if (getjournalDetails != null) {
						if (getjournalDetails.getJournalMetadata() != null) {
							if (getjournalDetails.getJournalMetadata().getWorkflowId() != null) {
								List<WorkflowRoleSeq> listOfWorkflowRoleSeq = workflowRoleRepo.getJournalWorkflow(
										getjournalDetails.getJournalMetadata().getWorkflowId(),
										getjournalDetails.getJournalId());
								if (listOfWorkflowRoleSeq != null && !listOfWorkflowRoleSeq.isEmpty()) {
									for (WorkflowRoleSeq l : listOfWorkflowRoleSeq) {
										if (l.getTaskId().equals(10)) {
											revisionToAuthorVO.getAuthoritiesHistoryVo().setDueDate(l.getTat().equals(0) ? new Date()
													: new Date().from(LocalDate.now().plusDays(l.getTat())
															.atStartOfDay(ZoneId.systemDefault()).toInstant()));
										}
									}
								}
							}
						}
					}
				}
				if (revisionToAuthorVO.getAuthoritiesHistoryVo().getManuscriptStatusId() != null
						&& revisionToAuthorVO.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(26)) {
					// Author submit as re-submssion
					Journal getjournalDetails = journalRepo.getOne(revisionToAuthorVO.getAuthoritiesHistoryVo().getJournalId());
					ManuScript i = manuScriptRepo.getOne(revisionToAuthorVO.getAuthoritiesHistoryVo().getManuscriptId());
					List<Integer> add = new ArrayList<>();
					add.add(i.getAssignedUser());
					revisionToAuthorVO.getAuthoritiesHistoryVo().setCurrentUserId(add);
					revisionToAuthorVO.getAuthoritiesHistoryVo().setCurrentUserRole(i.getAssignedUserRole());
					if (getjournalDetails != null) {
						if (getjournalDetails.getJournalMetadata() != null) {
							if (getjournalDetails.getJournalMetadata().getWorkflowId() != null) {
								List<WorkflowRoleSeq> listOfWorkflowRoleSeq = workflowRoleRepo.getJournalWorkflow(
										getjournalDetails.getJournalMetadata().getWorkflowId(),
										getjournalDetails.getJournalId());
								if (listOfWorkflowRoleSeq != null && !listOfWorkflowRoleSeq.isEmpty()) {
									for (WorkflowRoleSeq l : listOfWorkflowRoleSeq) {
										if (l.getTaskId().equals(19)) {
											revisionToAuthorVO.getAuthoritiesHistoryVo().setDueDate(l.getTat().equals(0) ? new Date()
													: new Date().from(LocalDate.now().plusDays(l.getTat())
															.atStartOfDay(ZoneId.systemDefault()).toInstant()));
										}
									}
								}
							}
						}
					}
				}

				if (revisionToAuthorVO.getAuthoritiesHistoryVo().getManuscriptStatusId() != null
						&& revisionToAuthorVO.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(3)) {
					// Eic assign editor
					Journal getjournalDetails = journalRepo.getOne(revisionToAuthorVO.getAuthoritiesHistoryVo().getJournalId());
					if (getjournalDetails != null) {
						if (getjournalDetails.getJournalMetadata() != null) {
							if (getjournalDetails.getJournalMetadata().getWorkflowId() != null) {
								List<WorkflowRoleSeq> listOfWorkflowRoleSeq = workflowRoleRepo.getJournalWorkflow(
										getjournalDetails.getJournalMetadata().getWorkflowId(),
										getjournalDetails.getJournalId());
								if (listOfWorkflowRoleSeq != null && !listOfWorkflowRoleSeq.isEmpty()) {
									for (WorkflowRoleSeq l : listOfWorkflowRoleSeq) {
										if (l.getTaskId().equals(3)) {
											revisionToAuthorVO.getAuthoritiesHistoryVo().setDueDate(l.getTat().equals(0) ? new Date()
													: new Date().from(LocalDate.now().plusDays(l.getTat())
															.atStartOfDay(ZoneId.systemDefault()).toInstant()));
										}
									}
								}
							}
						}
					}
				}

				if (revisionToAuthorVO.getAuthoritiesHistoryVo().getManuscriptStatusId() != null
						&& (revisionToAuthorVO.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(12)
								|| revisionToAuthorVO.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(13)
								|| revisionToAuthorVO.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(14)
								|| revisionToAuthorVO.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(15)
								|| revisionToAuthorVO.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(36)
								|| revisionToAuthorVO.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(60))) {
					// Eic/editor final descision after reviewed
					Journal getjournalDetails = journalRepo.getOne(revisionToAuthorVO.getAuthoritiesHistoryVo().getJournalId());
					if (getjournalDetails != null) {
						if (getjournalDetails.getJournalMetadata() != null) {
							if (getjournalDetails.getJournalMetadata().getWorkflowId() != null) {
								List<WorkflowRoleSeq> listOfWorkflowRoleSeq = workflowRoleRepo.getJournalWorkflow(
										getjournalDetails.getJournalMetadata().getWorkflowId(),
										getjournalDetails.getJournalId());
								if (listOfWorkflowRoleSeq != null && !listOfWorkflowRoleSeq.isEmpty()) {
									for (WorkflowRoleSeq l : listOfWorkflowRoleSeq) {
										if (l.getTaskId().equals(8)) {
											revisionToAuthorVO.getAuthoritiesHistoryVo().setDueDate(l.getTat().equals(0) ? new Date()
													: new Date().from(LocalDate.now().plusDays(l.getTat())
															.atStartOfDay(ZoneId.systemDefault()).toInstant()));
										}
									}
								}
							}
						}
					}
				}

				if (revisionToAuthorVO.getAuthoritiesHistoryVo().getManuscriptStatusId() != null
						&& revisionToAuthorVO.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(35)) {
					// Eic/editor final descision after reviewed
					Journal getjournalDetails = journalRepo.getOne(revisionToAuthorVO.getAuthoritiesHistoryVo().getJournalId());
					if (getjournalDetails != null) {
						if (getjournalDetails.getJournalMetadata() != null) {
							if (getjournalDetails.getJournalMetadata().getWorkflowId() != null) {
								List<WorkflowRoleSeq> listOfWorkflowRoleSeq = workflowRoleRepo.getJournalWorkflow(
										getjournalDetails.getJournalMetadata().getWorkflowId(),
										getjournalDetails.getJournalId());
								if (listOfWorkflowRoleSeq != null && !listOfWorkflowRoleSeq.isEmpty()) {
									for (WorkflowRoleSeq l : listOfWorkflowRoleSeq) {
										if (l.getTaskId().equals(7)) {
											revisionToAuthorVO.getAuthoritiesHistoryVo().setDueDate(l.getTat().equals(0) ? new Date()
													: new Date().from(LocalDate.now().plusDays(l.getTat())
															.atStartOfDay(ZoneId.systemDefault()).toInstant()));
										}
									}
								}
							}
						}
					}
				}

				if (revisionToAuthorVO.getAuthoritiesHistoryVo().getManuscriptStatusId() != null
						&& revisionToAuthorVO.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(74)) {
					// Assign editor after technical check
					Journal getjournalDetails = journalRepo.getOne(revisionToAuthorVO.getAuthoritiesHistoryVo().getJournalId());
					if (getjournalDetails != null) {
						if (getjournalDetails.getJournalMetadata() != null) {
							if (getjournalDetails.getJournalMetadata().getWorkflowId() != null) {
								List<WorkflowRoleSeq> listOfWorkflowRoleSeq = workflowRoleRepo.getJournalWorkflow(
										getjournalDetails.getJournalMetadata().getWorkflowId(),
										getjournalDetails.getJournalId());
								if (listOfWorkflowRoleSeq != null && !listOfWorkflowRoleSeq.isEmpty()) {
									for (WorkflowRoleSeq l : listOfWorkflowRoleSeq) {
										if (l.getTaskId().equals(3)) {
											revisionToAuthorVO.getAuthoritiesHistoryVo().setDueDate(l.getTat().equals(0) ? new Date()
													: new Date().from(LocalDate.now().plusDays(l.getTat())
															.atStartOfDay(ZoneId.systemDefault()).toInstant()));
										}
									}
								}
							}
						}
					}
				}

				if (revisionToAuthorVO.getAuthoritiesHistoryVo().getManuscriptStatusId() != null
						&& revisionToAuthorVO.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(72)) {
					// Assign editor after technical check
					Journal getjournalDetails = journalRepo.getOne(revisionToAuthorVO.getAuthoritiesHistoryVo().getJournalId());
					if (getjournalDetails != null) {
						if (getjournalDetails.getJournalMetadata() != null) {
							if (getjournalDetails.getJournalMetadata().getWorkflowId() != null) {
								List<WorkflowRoleSeq> listOfWorkflowRoleSeq = workflowRoleRepo.getJournalWorkflowWithout(
										getjournalDetails.getJournalMetadata().getWorkflowId(),
										getjournalDetails.getJournalId());
								if (listOfWorkflowRoleSeq != null && !listOfWorkflowRoleSeq.isEmpty()) {
									for (WorkflowRoleSeq l : listOfWorkflowRoleSeq) {
										revisionToAuthorVO.getAuthoritiesHistoryVo().setDueDate(l.getTat().equals(0) ? new Date()
												: new Date().from(LocalDate.now().plusDays(l.getTat())
														.atStartOfDay(ZoneId.systemDefault()).toInstant()));
									}
								}
							}
						}
					}
				}
				
				if (revisionToAuthorVO.getAuthoritiesHistoryVo().getManuscriptStatusId() != null
						&&  revisionToAuthorVO.getAuthoritiesHistoryVo().getManuscriptStatusId().equals(73)) {
					// Assign editor after technical check
					Journal getjournalDetails = journalRepo.getOne(revisionToAuthorVO.getAuthoritiesHistoryVo().getJournalId());
					if (getjournalDetails != null) {
						if (getjournalDetails.getJournalMetadata() != null) {
							if (getjournalDetails.getJournalMetadata().getWorkflowId() != null) {
								List<WorkflowRoleSeq> listOfWorkflowRoleSeq = workflowRoleRepo.getJournalWorkflowAfter(
										getjournalDetails.getJournalMetadata().getWorkflowId(),
										getjournalDetails.getJournalId());
								if (listOfWorkflowRoleSeq != null && !listOfWorkflowRoleSeq.isEmpty()) {
									for (WorkflowRoleSeq l : listOfWorkflowRoleSeq) {
										revisionToAuthorVO.getAuthoritiesHistoryVo().setDueDate(l.getTat().equals(0) ? new Date()
												: new Date().from(LocalDate.now().plusDays(l.getTat())
														.atStartOfDay(ZoneId.systemDefault()).toInstant()));
									}
								}
							}
						}
					}
				}

				String result = revisionToAuthorService.authoritiesHistory(revisionToAuthorVO);
				obj.put("data", result);
			}

		} catch (Exception e) {
			obj.put("data", "Something went wrong");
		}	
	}
	
	private void uploadSendBackToAuthor(@ModelAttribute RevisionToAuthorVO revisionToAuthorVO,
			HttpServletRequest request,JSONObject obj) {
		try {
			ManuScript getManuscriptDetail = manuScriptRepo.getOne(revisionToAuthorVO.getSendBackToAuthorVo().getManuscriptId());
			Integer assignedUserRole = getManuscriptDetail.getAssignedUserRole();
			Integer assigneduser = getManuscriptDetail.getAssignedUser();
			
			String data = manuscriptEditorService.uploadSendBackToAuthor(revisionToAuthorVO.getSendBackToAuthorVo(), request);
			// Email Trigger
			
			//Mail Will Trigger To Eic/Editor While Sending To Author While Sending t Revision Required
			if(revisionToAuthorVO.getSendBackToAuthorVo().getRoleId()!=null && revisionToAuthorVO.getSendBackToAuthorVo().getManuscriptStatus()!=null && revisionToAuthorVO.getSendBackToAuthorVo().getManuscriptStatus().equals(4)) {
				log.info("Inside in sendBackToAuthor as Mail Will Trigger To Eic/Editor While Sending To Author");
				EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
				emailTriggerVO.setJournalId(revisionToAuthorVO.getSendBackToAuthorVo().getJournalId());
				emailTriggerVO.setUserId(revisionToAuthorVO.getSendBackToAuthorVo().getEditorId());
				if(revisionToAuthorVO.getSendBackToAuthorVo().getRoleId()!=null && revisionToAuthorVO.getSendBackToAuthorVo().getRoleId().equals(2)) {
					emailTriggerVO.setManagingEditorId(revisionToAuthorVO.getSendBackToAuthorVo().getEditorId());
				}else {
				emailTriggerVO.setEditorUserId(revisionToAuthorVO.getSendBackToAuthorVo().getEditorId());
				}
				emailTriggerVO.setManuscriptId(revisionToAuthorVO.getSendBackToAuthorVo().getManuscriptId());
				emailTriggerVO.setTaskId(34);
				emailTriggerVO.setRoleId(revisionToAuthorVO.getSendBackToAuthorVo().getRoleId());
				emailTriggerVO.setReceiverRoleId(revisionToAuthorVO.getSendBackToAuthorVo().getRoleId());
				emailTriggerVO.setManuscriptStatus(revisionToAuthorVO.getSendBackToAuthorVo().getManuscriptStatus());
				emailTriggerVO.setCommentToAuthor(revisionToAuthorVO.getSendBackToAuthorVo().getCommentsForAuthor());
				log.info(
						"Inside in sendBackToAuthor as Mail Will Trigger To Eic/Editor While Sending To Author Email Trigger Data are:->"
								+ emailTriggerVO.toString());
				log.info("User Receiver Id:->" + revisionToAuthorVO.getSendBackToAuthorVo().getEditorId());
				log.info("User Receiver Role Id:->" + revisionToAuthorVO.getSendBackToAuthorVo().getRoleId());
				log.info("User Sender Role Id:->" + revisionToAuthorVO.getSendBackToAuthorVo().getRoleId());
				log.info("User Sender Id:->" + revisionToAuthorVO.getSendBackToAuthorVo().getEditorId());
				log.info("Task Id:->" + 29);
				String message = emailTriggerService.emailTrigger(emailTriggerVO);
				obj.put("data", message);
				log.debug("Emil Trigger" + message);
				log.info("Message of sendBackToAuthor as a Mail Will Trigger To Eic/Editor While Sending To Author For Email Trigger "
						+ message);
				log.info("End of sendBackToAuthor as a Mail Will Trigger To Eic/Editor While Sending To Author For Email Trigger ");
			}
			
			//Mail Will Trigger To Eic/Editor While Sending To Author While Sending to Technical Modification 
			if(revisionToAuthorVO.getSendBackToAuthorVo().getRoleId()!=null && revisionToAuthorVO.getSendBackToAuthorVo().getManuscriptStatus()!=null && revisionToAuthorVO.getSendBackToAuthorVo().getManuscriptStatus().equals(46)) {
				log.info("Inside in sendBackToAuthor as Mail Will Trigger To Eic/Editor While Sending To Author");
				EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
				emailTriggerVO.setJournalId(revisionToAuthorVO.getSendBackToAuthorVo().getJournalId());
				emailTriggerVO.setUserId(revisionToAuthorVO.getSendBackToAuthorVo().getEditorId());
				if(revisionToAuthorVO.getSendBackToAuthorVo().getRoleId()!=null && revisionToAuthorVO.getSendBackToAuthorVo().getRoleId().equals(2)) {
					emailTriggerVO.setManagingEditorId(revisionToAuthorVO.getSendBackToAuthorVo().getEditorId());
				}else {
				emailTriggerVO.setEditorUserId(revisionToAuthorVO.getSendBackToAuthorVo().getEditorId());
				}
				emailTriggerVO.setManuscriptId(revisionToAuthorVO.getSendBackToAuthorVo().getManuscriptId());
				emailTriggerVO.setTaskId(29);
				emailTriggerVO.setRoleId(revisionToAuthorVO.getSendBackToAuthorVo().getRoleId());
				emailTriggerVO.setReceiverRoleId(revisionToAuthorVO.getSendBackToAuthorVo().getRoleId());
				emailTriggerVO.setManuscriptStatus(revisionToAuthorVO.getSendBackToAuthorVo().getManuscriptStatus());
				emailTriggerVO.setCommentToAuthor(revisionToAuthorVO.getSendBackToAuthorVo().getCommentsForAuthor());
//				if (sendBackToAuthorVo.getJournalMailTemplatesBody() != null && !sendBackToAuthorVo.getJournalMailTemplatesBody().isEmpty())
//					emailTriggerVO.setEmailTemplateBody(sendBackToAuthorVo.getJournalMailTemplatesBody());
				log.info(
						"Inside in sendBackToAuthor as Mail Will Trigger To Eic/Editor While Sending To Author Email Trigger Data are:->"
								+ emailTriggerVO.toString());
				log.info("User Receiver Id:->" + revisionToAuthorVO.getSendBackToAuthorVo().getEditorId());
				log.info("User Receiver Role Id:->" + revisionToAuthorVO.getSendBackToAuthorVo().getRoleId());
				log.info("User Sender Role Id:->" + revisionToAuthorVO.getSendBackToAuthorVo().getRoleId());
				log.info("User Sender Id:->" + revisionToAuthorVO.getSendBackToAuthorVo().getEditorId());
				log.info("Task Id:->" + 29);
				String message = emailTriggerService.emailTrigger(emailTriggerVO);
				obj.put("data", message);
				log.debug("Emil Trigger" + message);
				log.info("Message of sendBackToAuthor as a Mail Will Trigger To Eic/Editor While Sending To Author For Email Trigger "
						+ message);
				log.info("End of sendBackToAuthor as a Mail Will Trigger To Eic/Editor While Sending To Author For Email Trigger ");
			}
			
			if (getManuscriptDetail != null) {
				log.info("Inside in sendBackToAuthor For Email Trigger");
				if (revisionToAuthorVO.getSendBackToAuthorVo().getManuscriptId() != null && revisionToAuthorVO.getSendBackToAuthorVo().getEditorId() != null
						&& revisionToAuthorVO.getSendBackToAuthorVo().getJournalId() != null
						&& revisionToAuthorVO.getSendBackToAuthorVo().getManuscriptStatus().equals(4)) {
					log.info("Inside in sendBackToAuthor as a Satus of Revised Manuscripts In Draft For Email Trigger");
					EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
					emailTriggerVO.setJournalId(revisionToAuthorVO.getSendBackToAuthorVo().getJournalId());
					emailTriggerVO.setUserId(getManuscriptDetail.getAuthorId());
					emailTriggerVO.setAuthorUserId(getManuscriptDetail.getAuthorId());
					if(revisionToAuthorVO.getSendBackToAuthorVo().getRoleId()!=null && revisionToAuthorVO.getSendBackToAuthorVo().getRoleId().equals(2)) {
						emailTriggerVO.setManagingEditorId(revisionToAuthorVO.getSendBackToAuthorVo().getEditorId());
					}else {
					emailTriggerVO.setEditorUserId(revisionToAuthorVO.getSendBackToAuthorVo().getEditorId());
					}
					emailTriggerVO.setManuscriptId(revisionToAuthorVO.getSendBackToAuthorVo().getManuscriptId());
					emailTriggerVO.setTaskId(10);
					emailTriggerVO.setRoleId(revisionToAuthorVO.getSendBackToAuthorVo().getRoleId());
					emailTriggerVO.setReceiverRoleId(4);
					emailTriggerVO.setManuscriptStatus(revisionToAuthorVO.getSendBackToAuthorVo().getManuscriptStatus());
					emailTriggerVO.setCommentToAuthor(revisionToAuthorVO.getSendBackToAuthorVo().getCommentsForAuthor());
					if (revisionToAuthorVO.getSendBackToAuthorVo().getJournalMailTemplatesBody() != null && !revisionToAuthorVO.getSendBackToAuthorVo().getJournalMailTemplatesBody().isEmpty())
						emailTriggerVO.setEmailTemplateBody(revisionToAuthorVO.getSendBackToAuthorVo().getJournalMailTemplatesBody());
					if (revisionToAuthorVO.getSendBackToAuthorVo().getJournalMailTemplatesSubject()!= null && !revisionToAuthorVO.getSendBackToAuthorVo().getJournalMailTemplatesSubject().isEmpty())
						emailTriggerVO.setEmailTemplateSubject(revisionToAuthorVO.getSendBackToAuthorVo().getJournalMailTemplatesSubject());
					
					log.info(
							"Inside in sendBackToAuthor as a Satus of Revised Manuscripts In Draft For Email Trigger Data are:->"
									+ emailTriggerVO.toString());
					log.info("User Receiver Id:->" + getManuscriptDetail.getAuthorId());
					log.info("User Receiver Role Id:->" + 4);
					log.info("User Sender Role Id:->" + 3);
					log.info("User Sender Id:->" + revisionToAuthorVO.getSendBackToAuthorVo().getEditorId());
					log.info("Task Id:->" + 10);
					String message = emailTriggerService.emailTrigger(emailTriggerVO);
					obj.put("data", message);
					log.debug("Emil Trigger" + message);
					log.info("Message of sendBackToAuthor as a Satus of Revised Manuscripts In Draft For Email Trigger "
							+ message);
					log.info("End of sendBackToAuthor as a Satus of Revised Manuscripts In Draft For Email Trigger ");
				} else if (revisionToAuthorVO.getSendBackToAuthorVo().getManuscriptId() != null && revisionToAuthorVO.getSendBackToAuthorVo().getEditorId() != null
						&& revisionToAuthorVO.getSendBackToAuthorVo().getJournalId() != null
						&& revisionToAuthorVO.getSendBackToAuthorVo().getManuscriptStatus().equals(46)) {
					log.info("Inside in sendBackToAuthor as a Satus of Technical Modification For Email Trigger");
					EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
					emailTriggerVO.setJournalId(revisionToAuthorVO.getSendBackToAuthorVo().getJournalId());
					emailTriggerVO.setUserId(getManuscriptDetail.getAuthorId());
					emailTriggerVO.setAuthorUserId(getManuscriptDetail.getAuthorId());
					if(revisionToAuthorVO.getSendBackToAuthorVo().getRoleId()!=null && revisionToAuthorVO.getSendBackToAuthorVo().getRoleId().equals(2)) {
						emailTriggerVO.setManagingEditorId(revisionToAuthorVO.getSendBackToAuthorVo().getEditorId());
					}else {
					emailTriggerVO.setEditorUserId(revisionToAuthorVO.getSendBackToAuthorVo().getEditorId());
					}
					emailTriggerVO.setManuscriptId(revisionToAuthorVO.getSendBackToAuthorVo().getManuscriptId());
					emailTriggerVO.setTaskId(11);
					if (revisionToAuthorVO.getSendBackToAuthorVo().getJournalMailTemplatesBody() != null && !revisionToAuthorVO.getSendBackToAuthorVo().getJournalMailTemplatesBody().isEmpty())
						emailTriggerVO.setEmailTemplateBody(revisionToAuthorVO.getSendBackToAuthorVo().getJournalMailTemplatesBody());
					if (revisionToAuthorVO.getSendBackToAuthorVo().getJournalMailTemplatesSubject()!= null && !revisionToAuthorVO.getSendBackToAuthorVo().getJournalMailTemplatesSubject().isEmpty())
						emailTriggerVO.setEmailTemplateSubject(revisionToAuthorVO.getSendBackToAuthorVo().getJournalMailTemplatesSubject());

					emailTriggerVO.setRoleId(revisionToAuthorVO.getSendBackToAuthorVo().getRoleId());
					emailTriggerVO.setReceiverRoleId(4);
					emailTriggerVO.setManuscriptStatus(revisionToAuthorVO.getSendBackToAuthorVo().getManuscriptStatus());
					emailTriggerVO.setCommentToAuthor(revisionToAuthorVO.getSendBackToAuthorVo().getCommentsForAuthor());
					log.info(
							"Inside in sendBackToAuthor as a Satus of Technical Modification For Email Trigger Data are:->"
									+ emailTriggerVO.toString());
					log.info("User Receiver Id:->" + getManuscriptDetail.getAuthorId());
					log.info("User Receiver Role Id:->" + 4);
					log.info("User Sender Role Id:->" + 3);
					log.info("User Sender Id:->" + revisionToAuthorVO.getSendBackToAuthorVo().getEditorId());
					log.info("Task Id:->" + 11);
					String message = emailTriggerService.emailTrigger(emailTriggerVO);
					obj.put("data", message);
					log.debug("Emil Trigger" + message);
					log.info("Message of sendBackToAuthor as a Satus of Technical Modification For Email Trigger "
							+ message);
					log.info("End of sendBackToAuthor as a Satus of Technical Modification For Email Trigger ");
				}
				
				if (revisionToAuthorVO.getSendBackToAuthorVo().getRoleId().equals(2) && assignedUserRole == 3) {
					EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
					emailTriggerVO.setJournalId(getManuscriptDetail.getJournalId());
					emailTriggerVO.setUserId(assigneduser);
					emailTriggerVO.setAuthorUserId(getManuscriptDetail.getAuthorId());
					emailTriggerVO.setManuscriptId(getManuscriptDetail.getManuScriptId());
					emailTriggerVO.setManuscriptStatus(revisionToAuthorVO.getSendBackToAuthorVo().getManuscriptStatus());
					emailTriggerVO.setReceiverRoleId(assignedUserRole);
					emailTriggerVO.setTaskId(86);
					emailTriggerVO.setRoleId(assignedUserRole);
					emailTriggerVO.setEditorUserId(assigneduser);
					String message = emailTriggerService.emailTrigger(emailTriggerVO);
					obj.put("data", message);
					log.debug("Emil Trigger" + message);
				}
			}
			obj.put("data", data);
		} catch (Exception e) {
			e.printStackTrace();
			obj.put("data", "Something went wrong..");
		}
	}
	
	private void create(RevisionToAuthorVO revisionToAuthorVO,JSONObject obj) {
		try {
			if (revisionToAuthorVO.getEmailTriggerVO().getRoleId() != null && revisionToAuthorVO.getEmailTriggerVO().getRoleId().equals(2)
					&& !revisionToAuthorVO.getEmailTriggerVO().getTaskId().equals(4)) {
				ManuScript getManuScript = manuScriptRepo.getOne(revisionToAuthorVO.getEmailTriggerVO().getManuscriptId());
				if (getManuScript != null) {
					revisionToAuthorVO.getEmailTriggerVO().setReceiverRoleId(2);
					revisionToAuthorVO.getEmailTriggerVO().setUserId(getManuScript.getAssignedUser());
					tatEmailConfigurationService.create(revisionToAuthorVO.getEmailTriggerVO());
				}
			}
			// For Reviewer
			else if (revisionToAuthorVO.getEmailTriggerVO().getTaskId() != null && revisionToAuthorVO.getEmailTriggerVO().getTaskId().equals(4)) {
				if (revisionToAuthorVO.getEmailTriggerVO().getUserid() != null && !revisionToAuthorVO.getEmailTriggerVO().getUserid().isEmpty()) {
					revisionToAuthorVO.getEmailTriggerVO().getUserid().stream().forEach(i -> {
						revisionToAuthorVO.getEmailTriggerVO().setUserId(i);
						tatEmailConfigurationService.create(revisionToAuthorVO.getEmailTriggerVO());
					});
				}
			} else {
				tatEmailConfigurationService.create(revisionToAuthorVO.getEmailTriggerVO());
			}
			obj.put("data", "Added Data Sucessfully");
		} catch (Exception e) {
			obj.put("data", "Something went wrong");
		}

	}


}
