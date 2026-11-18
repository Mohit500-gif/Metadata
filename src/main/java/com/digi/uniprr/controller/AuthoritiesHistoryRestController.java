package com.digi.uniprr.controller;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.digi.uniprr.model.*;
import com.digi.uniprr.repository.*;
import com.digi.uniprr.service.UserService;
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
import org.springframework.web.bind.annotation.RestController;
import com.digi.uniprr.VO.AuthoritiesHistoryVo;
import com.digi.uniprr.service.AuthoritiesHistoryService;
import com.digi.uniprr.service.ManuScriptService;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class AuthoritiesHistoryRestController {

	@Autowired
	AuthoritiesHistoryService authoritiesHistoryService;

	@Autowired
	ManuScriptRepo manuScriptRepo;

	@Autowired
	JournalsRepo journalsRepo;

	@Autowired
	UserJournalRepo userJournalRepo;

	@Autowired
	ManuScriptService manuscriptService;

	@Autowired
	WorkflowRoleRepo workflowRoleRepo;

    @Autowired
    UserService userService;

    @Autowired
    DevelopedCountriesByManuscriptTypeRepo developedCountriesByManuscriptTypeRepo;

	private final Logger log = LoggerFactory.getLogger(this.getClass());


	@PostMapping("authoritiesHistory")
	public ResponseEntity<?> authoritiesHistory(@RequestBody AuthoritiesHistoryVo authoritiesHistoryVo) {
		log.info(AppConstants.REQUEST_METHOD+" :: "+AppConstants.POST+
				AppConstants.REQUEST_URI+" :: /authoritiesHistory/"+
				AppConstants.REQUEST_BODY+" ::"+authoritiesHistoryVo.toString());
		JSONObject obj = new JSONObject();
		try {

			if (authoritiesHistoryVo.getPreviousUserRole() != null
					&& authoritiesHistoryVo.getPreviousUserRole().equals(4)
					&& authoritiesHistoryVo.getManuscriptStatusId().equals(2)) {

				JournalManuscriptTypeCharge journalManuscriptTypeCharge = manuscriptService
						.getManuscriptChargeByManuscriptId(authoritiesHistoryVo.getManuscriptId(),
								authoritiesHistoryVo.getJournalId());
				ManuScript i = manuScriptRepo.getOne(authoritiesHistoryVo.getManuscriptId());
				int userCountryStatus = Integer.parseInt(manuScriptRepo.getDevelopingCountryStatus(i.getAuthorId()));
				Integer manuscriptStatusId = i.getManuscriptStatusId();
				log.info("manuscript status in manuscript table is : "+manuscriptStatusId);
				// Checking payment required for article
				if (authoritiesHistoryVo.getManuscriptStatusId().equals(2) && journalManuscriptTypeCharge != null) {
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
						authoritiesHistoryVo.setManuscriptStatusId(64);
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
//						if (userCountryStatus > 0)
//							authoritiesHistoryVo.setManuscriptStatusId(2);
//						else
//							authoritiesHistoryVo.setManuscriptStatusId(64);

                        if (userCountryStatus > 0)
                        {
                            User authorOfManuscript= userService.getById(i.getAuthorId());
                            DevelopedCountriesByManuscriptType developedCountriesByManuscriptType=developedCountriesByManuscriptTypeRepo.
                                    getByJournalAndManuscriptType(journalManuscriptTypeCharge.getJournalId(),journalManuscriptTypeCharge.getManuscriptTypeId());

                            String paymentCountriesOnSubmission= developedCountriesByManuscriptType.getPaymentCountriesOnSubmission();

                            if(paymentCountriesOnSubmission.trim().equals("India,Others"))
                            {
                                authoritiesHistoryVo.setManuscriptId(2);
                            } else if (paymentCountriesOnSubmission.trim().equals("India")
                                    && authorOfManuscript.getCountries().getId().equals(101)) {
                                authoritiesHistoryVo.setManuscriptStatusId(2);
                            } else if (paymentCountriesOnSubmission.trim().equals("Others")
                                    && !authorOfManuscript.getCountries().getId().equals(101)) {
                                authoritiesHistoryVo.setManuscriptStatusId(2);
                            }
                            else {
                                authoritiesHistoryVo.setManuscriptStatusId(64);
                            }
                        }
                        else{
                            authoritiesHistoryVo.setManuscriptStatusId(2);
                        }
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
						authoritiesHistoryVo.setManuscriptStatusId(64);
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
//						if (userCountryStatus > 0)
//							authoritiesHistoryVo.setManuscriptStatusId(2);
//						else
//							authoritiesHistoryVo.setManuscriptStatusId(64);


                        if (userCountryStatus > 0)
                        {
                            User authorOfManuscript= userService.getById(i.getAuthorId());
                            DevelopedCountriesByManuscriptType developedCountriesByManuscriptType=developedCountriesByManuscriptTypeRepo.
                                    getByJournalAndManuscriptType(journalManuscriptTypeCharge.getJournalId(),journalManuscriptTypeCharge.getManuscriptTypeId());

                            String paymentCountriesOnSubmission= developedCountriesByManuscriptType.getPaymentCountriesOnSubmission();

                            if(paymentCountriesOnSubmission.trim().equals("India,Others"))
                            {
                                authoritiesHistoryVo.setManuscriptStatusId(2);
                            } else if (paymentCountriesOnSubmission.trim().equals("India")
                                    && authorOfManuscript.getCountries().getId().equals(101)) {
                                authoritiesHistoryVo.setManuscriptStatusId(2);
                            } else if (paymentCountriesOnSubmission.trim().equals("Others")
                                    && !authorOfManuscript.getCountries().getId().equals(101)) {
                                authoritiesHistoryVo.setManuscriptStatusId(2);
                            }
                            else {
                                authoritiesHistoryVo.setManuscriptStatusId(64);
                            }
                        }
                        else{
                            authoritiesHistoryVo.setManuscriptStatusId(2);
                        }
					}
					
					if(manuscriptStatusId==64) {
						log.info("Set the manuscript status 64 in the authoritiesHistory, if the manuscript status in the manuscript table is 64");
						authoritiesHistoryVo.setManuscriptStatusId(64);
					}
				}

				List<Integer> add = new ArrayList<>();
				if (authoritiesHistoryVo.getManuscriptStatusId().equals(64)) {
					List<UserJournal> getAdminList = userJournalRepo.getUserRoleJournalId(i.getJournalId(), 1);
					add.add(getAdminList.get(0).getUserId());
					authoritiesHistoryVo.setCurrentUserId(add);
					authoritiesHistoryVo.setCurrentUserRole(1);

				} else {
					add.add(i.getAssignedUser());
					authoritiesHistoryVo.setCurrentUserId(add);
					authoritiesHistoryVo.setCurrentUserRole(i.getAssignedUserRole());
				}
				if (i != null) {
					// Due date for eic
					Journal getjournalDetails = journalsRepo.getOne(authoritiesHistoryVo.getJournalId());
					if (getjournalDetails != null) {
						if (getjournalDetails.getJournalMetadata() != null) {
							if (getjournalDetails.getJournalMetadata().getWorkflowId() != null) {
								List<WorkflowRoleSeq> listOfWorkflowRoleSeq = workflowRoleRepo.getJournalWorkflow(
										getjournalDetails.getJournalMetadata().getWorkflowId(),
										getjournalDetails.getJournalId());
								if (listOfWorkflowRoleSeq != null && !listOfWorkflowRoleSeq.isEmpty()) {
									for (WorkflowRoleSeq l : listOfWorkflowRoleSeq) {

										if (l.getTaskId().equals(13)) {
											authoritiesHistoryVo.setDueDate(l.getTat().equals(0) ? new Date()
													: new Date().from(LocalDate.now().plusDays(l.getTat())
															.atStartOfDay(ZoneId.systemDefault()).toInstant()));
										}
									}
								}
							}
						}
					}
					String result = authoritiesHistoryService.authoritiesHistory(authoritiesHistoryVo);
					obj.put("data", result);
				}
			} else {
				if (authoritiesHistoryVo.getManuscriptStatusId() != null
						&& authoritiesHistoryVo.getManuscriptStatusId().equals(46)) {
					// Requiring techincal modification
					Journal getjournalDetails = journalsRepo.getOne(authoritiesHistoryVo.getJournalId());
					if (getjournalDetails != null) {
						if (getjournalDetails.getJournalMetadata() != null) {
							if (getjournalDetails.getJournalMetadata().getWorkflowId() != null) {
								List<WorkflowRoleSeq> listOfWorkflowRoleSeq = workflowRoleRepo.getJournalWorkflow(
										getjournalDetails.getJournalMetadata().getWorkflowId(),
										getjournalDetails.getJournalId());
								if (listOfWorkflowRoleSeq != null && !listOfWorkflowRoleSeq.isEmpty()) {
									for (WorkflowRoleSeq l : listOfWorkflowRoleSeq) {
										if (l.getTaskId().equals(11)) {
											authoritiesHistoryVo.setDueDate(l.getTat().equals(0) ? new Date()
													: new Date().from(LocalDate.now().plusDays(l.getTat())
															.atStartOfDay(ZoneId.systemDefault()).toInstant()));
										}
									}
								}
							}
						}
					}
				}
				if (authoritiesHistoryVo.getManuscriptStatusId() != null
						&& authoritiesHistoryVo.getManuscriptStatusId().equals(50)) {
					// Author submit as techincal modification
					Journal getjournalDetails = journalsRepo.getOne(authoritiesHistoryVo.getJournalId());
					ManuScript i = manuScriptRepo.getOne(authoritiesHistoryVo.getManuscriptId());
					List<Integer> add = new ArrayList<>();
					add.add(i.getAssignedUser());
					authoritiesHistoryVo.setCurrentUserId(add);
					authoritiesHistoryVo.setCurrentUserRole(i.getAssignedUserRole());
					if (getjournalDetails != null) {
						if (getjournalDetails.getJournalMetadata() != null) {
							if (getjournalDetails.getJournalMetadata().getWorkflowId() != null) {
								List<WorkflowRoleSeq> listOfWorkflowRoleSeq = workflowRoleRepo.getJournalWorkflow(
										getjournalDetails.getJournalMetadata().getWorkflowId(),
										getjournalDetails.getJournalId());
								if (listOfWorkflowRoleSeq != null && !listOfWorkflowRoleSeq.isEmpty()) {
									for (WorkflowRoleSeq l : listOfWorkflowRoleSeq) {
										if (l.getTaskId().equals(20)) {
											authoritiesHistoryVo.setDueDate(l.getTat().equals(0) ? new Date()
													: new Date().from(LocalDate.now().plusDays(l.getTat())
															.atStartOfDay(ZoneId.systemDefault()).toInstant()));
										}
									}
								}
							}
						}
					}
				}
				if (authoritiesHistoryVo.getManuscriptStatusId() != null
						&& (authoritiesHistoryVo.getManuscriptStatusId().equals(51)
								|| authoritiesHistoryVo.getManuscriptStatusId().equals(52)
								|| authoritiesHistoryVo.getManuscriptStatusId().equals(68))) {
					// Author submit as techincal modification
					Journal getjournalDetails = journalsRepo.getOne(authoritiesHistoryVo.getJournalId());
					ManuScript manu = manuScriptRepo.getOne(authoritiesHistoryVo.getManuscriptId());
					authoritiesHistoryVo.setCurrentUserId(Arrays.asList(manu.getAssignedUser()));
					if (getjournalDetails != null) {
						if (getjournalDetails.getJournalMetadata() != null) {
							if (getjournalDetails.getJournalMetadata().getWorkflowId() != null) {
								List<WorkflowRoleSeq> listOfWorkflowRoleSeq = workflowRoleRepo.getJournalWorkflow(
										getjournalDetails.getJournalMetadata().getWorkflowId(),
										getjournalDetails.getJournalId());
								if (listOfWorkflowRoleSeq != null && !listOfWorkflowRoleSeq.isEmpty()) {
									for (WorkflowRoleSeq l : listOfWorkflowRoleSeq) {
										if (l.getTaskId().equals(8)) {
											authoritiesHistoryVo.setDueDate(l.getTat().equals(0) ? new Date()
													: new Date().from(LocalDate.now().plusDays(l.getTat())
															.atStartOfDay(ZoneId.systemDefault()).toInstant()));
										}
									}
								}
							}
						}
					}
				}

				if (authoritiesHistoryVo.getManuscriptStatusId() != null
						&& authoritiesHistoryVo.getManuscriptStatusId().equals(4)) {
					// Requiring re-submssion
					Journal getjournalDetails = journalsRepo.getOne(authoritiesHistoryVo.getJournalId());
					if (getjournalDetails != null) {
						if (getjournalDetails.getJournalMetadata() != null) {
							if (getjournalDetails.getJournalMetadata().getWorkflowId() != null) {
								List<WorkflowRoleSeq> listOfWorkflowRoleSeq = workflowRoleRepo.getJournalWorkflow(
										getjournalDetails.getJournalMetadata().getWorkflowId(),
										getjournalDetails.getJournalId());
								if (listOfWorkflowRoleSeq != null && !listOfWorkflowRoleSeq.isEmpty()) {
									for (WorkflowRoleSeq l : listOfWorkflowRoleSeq) {
										if (l.getTaskId().equals(10)) {
											authoritiesHistoryVo.setDueDate(l.getTat().equals(0) ? new Date()
													: new Date().from(LocalDate.now().plusDays(l.getTat())
															.atStartOfDay(ZoneId.systemDefault()).toInstant()));
										}
									}
								}
							}
						}
					}
				}
				if (authoritiesHistoryVo.getManuscriptStatusId() != null
						&& authoritiesHistoryVo.getManuscriptStatusId().equals(26)) {
					// Author submit as re-submssion
					Journal getjournalDetails = journalsRepo.getOne(authoritiesHistoryVo.getJournalId());
					ManuScript i = manuScriptRepo.getOne(authoritiesHistoryVo.getManuscriptId());
					List<Integer> add = new ArrayList<>();
					add.add(i.getAssignedUser());
					authoritiesHistoryVo.setCurrentUserId(add);
					authoritiesHistoryVo.setCurrentUserRole(i.getAssignedUserRole());
					if (getjournalDetails != null) {
						if (getjournalDetails.getJournalMetadata() != null) {
							if (getjournalDetails.getJournalMetadata().getWorkflowId() != null) {
								List<WorkflowRoleSeq> listOfWorkflowRoleSeq = workflowRoleRepo.getJournalWorkflow(
										getjournalDetails.getJournalMetadata().getWorkflowId(),
										getjournalDetails.getJournalId());
								if (listOfWorkflowRoleSeq != null && !listOfWorkflowRoleSeq.isEmpty()) {
									for (WorkflowRoleSeq l : listOfWorkflowRoleSeq) {
										if (l.getTaskId().equals(19)) {
											authoritiesHistoryVo.setDueDate(l.getTat().equals(0) ? new Date()
													: new Date().from(LocalDate.now().plusDays(l.getTat())
															.atStartOfDay(ZoneId.systemDefault()).toInstant()));
										}
									}
								}
							}
						}
					}
				}

				if (authoritiesHistoryVo.getManuscriptStatusId() != null
						&& authoritiesHistoryVo.getManuscriptStatusId().equals(3)) {
					// Eic assign editor
					Journal getjournalDetails = journalsRepo.getOne(authoritiesHistoryVo.getJournalId());
					if (getjournalDetails != null) {
						if (getjournalDetails.getJournalMetadata() != null) {
							if (getjournalDetails.getJournalMetadata().getWorkflowId() != null) {
								List<WorkflowRoleSeq> listOfWorkflowRoleSeq = workflowRoleRepo.getJournalWorkflow(
										getjournalDetails.getJournalMetadata().getWorkflowId(),
										getjournalDetails.getJournalId());
								if (listOfWorkflowRoleSeq != null && !listOfWorkflowRoleSeq.isEmpty()) {
									for (WorkflowRoleSeq l : listOfWorkflowRoleSeq) {
										if (l.getTaskId().equals(3)) {
											authoritiesHistoryVo.setDueDate(l.getTat().equals(0) ? new Date()
													: new Date().from(LocalDate.now().plusDays(l.getTat())
															.atStartOfDay(ZoneId.systemDefault()).toInstant()));
										}
									}
								}
							}
						}
					}
				}

				if (authoritiesHistoryVo.getManuscriptStatusId() != null
						&& (authoritiesHistoryVo.getManuscriptStatusId().equals(12)
								|| authoritiesHistoryVo.getManuscriptStatusId().equals(13)
								|| authoritiesHistoryVo.getManuscriptStatusId().equals(14)
								|| authoritiesHistoryVo.getManuscriptStatusId().equals(15)
								|| authoritiesHistoryVo.getManuscriptStatusId().equals(36)
								|| authoritiesHistoryVo.getManuscriptStatusId().equals(60))) {
					// Eic/editor final descision after reviewed
					Journal getjournalDetails = journalsRepo.getOne(authoritiesHistoryVo.getJournalId());
					if (getjournalDetails != null) {
						if (getjournalDetails.getJournalMetadata() != null) {
							if (getjournalDetails.getJournalMetadata().getWorkflowId() != null) {
								List<WorkflowRoleSeq> listOfWorkflowRoleSeq = workflowRoleRepo.getJournalWorkflow(
										getjournalDetails.getJournalMetadata().getWorkflowId(),
										getjournalDetails.getJournalId());
								if (listOfWorkflowRoleSeq != null && !listOfWorkflowRoleSeq.isEmpty()) {
									for (WorkflowRoleSeq l : listOfWorkflowRoleSeq) {
										if (l.getTaskId().equals(8)) {
											authoritiesHistoryVo.setDueDate(l.getTat().equals(0) ? new Date()
													: new Date().from(LocalDate.now().plusDays(l.getTat())
															.atStartOfDay(ZoneId.systemDefault()).toInstant()));
										}
									}
								}
							}
						}
					}
				}

				if (authoritiesHistoryVo.getManuscriptStatusId() != null
						&& authoritiesHistoryVo.getManuscriptStatusId().equals(35)) {
					// Eic/editor final descision after reviewed
					Journal getjournalDetails = journalsRepo.getOne(authoritiesHistoryVo.getJournalId());
					if (getjournalDetails != null) {
						if (getjournalDetails.getJournalMetadata() != null) {
							if (getjournalDetails.getJournalMetadata().getWorkflowId() != null) {
								List<WorkflowRoleSeq> listOfWorkflowRoleSeq = workflowRoleRepo.getJournalWorkflow(
										getjournalDetails.getJournalMetadata().getWorkflowId(),
										getjournalDetails.getJournalId());
								if (listOfWorkflowRoleSeq != null && !listOfWorkflowRoleSeq.isEmpty()) {
									for (WorkflowRoleSeq l : listOfWorkflowRoleSeq) {
										if (l.getTaskId().equals(7)) {
											authoritiesHistoryVo.setDueDate(l.getTat().equals(0) ? new Date()
													: new Date().from(LocalDate.now().plusDays(l.getTat())
															.atStartOfDay(ZoneId.systemDefault()).toInstant()));
										}
									}
								}
							}
						}
					}
				}

				if (authoritiesHistoryVo.getManuscriptStatusId() != null
						&& authoritiesHistoryVo.getManuscriptStatusId().equals(74)) {
					// Assign editor after technical check
					Journal getjournalDetails = journalsRepo.getOne(authoritiesHistoryVo.getJournalId());
					if (getjournalDetails != null) {
						if (getjournalDetails.getJournalMetadata() != null) {
							if (getjournalDetails.getJournalMetadata().getWorkflowId() != null) {
								List<WorkflowRoleSeq> listOfWorkflowRoleSeq = workflowRoleRepo.getJournalWorkflow(
										getjournalDetails.getJournalMetadata().getWorkflowId(),
										getjournalDetails.getJournalId());
								if (listOfWorkflowRoleSeq != null && !listOfWorkflowRoleSeq.isEmpty()) {
									for (WorkflowRoleSeq l : listOfWorkflowRoleSeq) {
										if (l.getTaskId().equals(3)) {
											authoritiesHistoryVo.setDueDate(l.getTat().equals(0) ? new Date()
													: new Date().from(LocalDate.now().plusDays(l.getTat())
															.atStartOfDay(ZoneId.systemDefault()).toInstant()));
										}
									}
								}
							}
						}
					}
				}

				if (authoritiesHistoryVo.getManuscriptStatusId() != null
						&& authoritiesHistoryVo.getManuscriptStatusId().equals(72)) {
					// Assign editor after technical check
					Journal getjournalDetails = journalsRepo.getOne(authoritiesHistoryVo.getJournalId());
					if (getjournalDetails != null) {
						if (getjournalDetails.getJournalMetadata() != null) {
							if (getjournalDetails.getJournalMetadata().getWorkflowId() != null) {
								List<WorkflowRoleSeq> listOfWorkflowRoleSeq = workflowRoleRepo.getJournalWorkflowWithout(
										getjournalDetails.getJournalMetadata().getWorkflowId(),
										getjournalDetails.getJournalId());
								if (listOfWorkflowRoleSeq != null && !listOfWorkflowRoleSeq.isEmpty()) {
									for (WorkflowRoleSeq l : listOfWorkflowRoleSeq) {
										authoritiesHistoryVo.setDueDate(l.getTat().equals(0) ? new Date()
												: new Date().from(LocalDate.now().plusDays(l.getTat())
														.atStartOfDay(ZoneId.systemDefault()).toInstant()));
									}
								}
							}
						}
					}
				}
				
				if (authoritiesHistoryVo.getManuscriptStatusId() != null
						&&  authoritiesHistoryVo.getManuscriptStatusId().equals(73)) {
					// Assign editor after technical check
					Journal getjournalDetails = journalsRepo.getOne(authoritiesHistoryVo.getJournalId());
					if (getjournalDetails != null) {
						if (getjournalDetails.getJournalMetadata() != null) {
							if (getjournalDetails.getJournalMetadata().getWorkflowId() != null) {
								List<WorkflowRoleSeq> listOfWorkflowRoleSeq = workflowRoleRepo.getJournalWorkflowAfter(
										getjournalDetails.getJournalMetadata().getWorkflowId(),
										getjournalDetails.getJournalId());
								if (listOfWorkflowRoleSeq != null && !listOfWorkflowRoleSeq.isEmpty()) {
									for (WorkflowRoleSeq l : listOfWorkflowRoleSeq) {
										authoritiesHistoryVo.setDueDate(l.getTat().equals(0) ? new Date()
												: new Date().from(LocalDate.now().plusDays(l.getTat())
														.atStartOfDay(ZoneId.systemDefault()).toInstant()));
									}
								}
							}
						}
					}
				}

				String result = authoritiesHistoryService.authoritiesHistory(authoritiesHistoryVo);
				obj.put("data", result);
			}
			return new ResponseEntity<Object>(obj, HttpStatus.OK);

		} catch (Exception e) {
			obj.put("data", "Something went wrong");
			return new ResponseEntity<Object>(obj, HttpStatus.OK);
		}

	}

	@PostMapping("getAuthoritiesHistory")
	public ResponseEntity<?> getAuthoritiesHistory(@RequestBody AuthoritiesHistoryVo authoritiesHistoryVo) {
		JSONObject obj = new JSONObject();
		try {
			List<AuthoritiesHistoryVo> result = authoritiesHistoryService.getAuthoritiesHistory(authoritiesHistoryVo);
			if (result != null && !result.isEmpty())
				obj.put("data", result);
			else
				obj.put("data", "There is no data");
			return new ResponseEntity<Object>(obj, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			obj.put("data", "Something went wrong");
			return new ResponseEntity<Object>(obj, HttpStatus.OK);
		}

	}
}
