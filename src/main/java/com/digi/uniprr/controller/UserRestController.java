
package com.digi.uniprr.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.*;

import com.digi.uniprr.config.HttpClientGenerator;
import com.digi.uniprr.utils.AppConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.digi.uniprr.VO.EmailTriggerVO;
import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.VO.LoggedUserVo;
import com.digi.uniprr.VO.ManuscriptListVO;
import com.digi.uniprr.VO.UpdatePassword;
import com.digi.uniprr.VO.UserExportVo;
import com.digi.uniprr.VO.UserVO;
import com.digi.uniprr.enums.Salutation;
import com.digi.uniprr.model.Countries;
import com.digi.uniprr.model.EditorRoleAccess;
import com.digi.uniprr.model.Journal;
import com.digi.uniprr.model.ManuScript;
import com.digi.uniprr.model.ManuScriptCoAuthor;
import com.digi.uniprr.model.User;
import com.digi.uniprr.model.UserJournal;
import com.digi.uniprr.model.UserRole;
import com.digi.uniprr.repository.EditorRoleAccessRepo;
import com.digi.uniprr.repository.JournalsRepo;
import com.digi.uniprr.repository.ManuScriptCoAuthorRepo;
import com.digi.uniprr.repository.ManuScriptRepo;
import com.digi.uniprr.repository.UserJournalRepo;
import com.digi.uniprr.repository.UserLoginRepo;
import com.digi.uniprr.repository.UserRoleRepo;
import com.digi.uniprr.service.CountriesService;
import com.digi.uniprr.service.EmailTriggerService;
import com.digi.uniprr.service.UserJournalService;
import com.digi.uniprr.service.UserRoleService;
import com.digi.uniprr.service.UserService;
import com.digi.uniprr.VO.UserDetailsListVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/user")
public class UserRestController {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	UserService userService;

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
	private PasswordEncoder passwordEncoder;

	@Autowired
	EmailTriggerService emailTriggerService;

	@Autowired
	EditorRoleAccessRepo editorRoleAccessRepo;

	@Autowired
	ManuScriptCoAuthorRepo manuScriptCoAuthorRepo;

	@Autowired
	ManuScriptRepo manuScriptRepo;

	JSONObject obj = new JSONObject();

	// Author: NIC
	// Date: 2023-09-07
	// Description: This controller improve for login authenticate time improvement
	// .
	@RequestMapping(value = "/loginAuthenticate", method = RequestMethod.POST)
	public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password) {
		JSONObject obj = new JSONObject();
		log.info("Email From Request::" + email + ", Password from reuqest::" + password);
		try {
			UserVO userDetails = userService.getUserByEmailAndPassword(email.trim());

			if (userDetails != null && userDetails.getEmail() != null) {
				log.info("Email from userDetails::" + userDetails.getEmail());
				boolean isPasswordCorrect = passwordEncoder.matches(password.trim(), userDetails.getPassword());

				if (isPasswordCorrect) {
					HttpPost httpRequest = new HttpPost(AppConstants.APP_LOGIN_URL);
					httpRequest.setHeader(AppConstants.CONTENT_TYPE, AppConstants.APPLICATION_JSON);
					httpRequest.setHeader(AppConstants.AUTHORIZATION, AppConstants.BASIC_AUTH);
					log.info("Authorization login URL::" + AppConstants.APP_LOGIN_URL + ", Content-Type::"
							+ AppConstants.APPLICATION_JSON + ", Basic Auth::" + AppConstants.BASIC_AUTH);
					String payload = "{\"msisdn\":\"" + email + "\",\"deviceId\":\"device\"}";
					log.info("Payload for Auth service::" + payload);
					httpRequest.setEntity(new StringEntity(payload));
//					HttpResponse httpResponse = HttpClientGenerator.getInstance().execute(httpRequest);
					HttpResponse httpResponse = HttpClientGenerator.httpClient.execute(httpRequest);
					HttpEntity httpEntity = httpResponse.getEntity();
					String jsonResponse = EntityUtils.toString(httpEntity);
					log.info("JSON Response from Auth::" + jsonResponse);
					String accessToken = String.valueOf(new javaxt.json.JSONObject(jsonResponse).get("object"));
					if (accessToken != null && !accessToken.isEmpty()) {
						userDetails.setAccessToken(accessToken);
					}
					Integer isAccountLocked = userDetails.getAccountLocked();
					boolean isVerified = userRepo.isVerifiedAccount(userDetails.getId()).intValue() > 0 ;
					if(isVerified) {
						isAccountLocked =1;
						userRepo.updateAccountLocked(userDetails.getId());
						log.info("account unlocked for id: "+userDetails.getId());
					}
					
					if (isAccountLocked != null && isAccountLocked > 0) {
						userDetails.setPassword(null); // Remove password for security
						obj.put("data", userDetails);
						return new ResponseEntity<>(obj, HttpStatus.OK);
					} else {
						obj.put("data", "Verify your account");
						return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
					}
				} else {
					obj.put("data", "Wrong password");
					return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
				}
			} else {
				obj.put("data", "User not found");
				return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
		}

	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ResponseEntity<?> loginUser(@RequestBody UserVO userVo) {
		boolean staus = false;
		JSONObject obj = new JSONObject();
		try {
			if (userVo.getEmail() != null && !userVo.getEmail().isEmpty() && userVo.getPassword() != null
					&& !userVo.getPassword().isEmpty() && userVo.getJournal() != null && userVo.getUserRole() != null) {
				User userDetails = userService.loginUser(userVo);
				if (userDetails != null) {
					if (userDetails.getIsActive() != null && userDetails.getIsActive().equals(1)) {
						staus = BCrypt.checkpw(userVo.getPassword().trim(), userDetails.getPassword());
						if (staus) {
							UserJournal getUserJournal = userJournalRepo.getUserJournal(userDetails.getId(),
									userVo.getJournal(), userVo.getUserRole());
							if (getUserJournal != null) {
								if (getUserJournal.getIsActive() != null && getUserJournal.getIsActive().equals(1)) {
									UserVO i = userService.userDetailsVo(userDetails);
									i.setPassword("");
									obj.put("data", i);
									return new ResponseEntity<>(obj, HttpStatus.OK);
								} else {
									obj.put("data", "User is not active on this journal");
									return new ResponseEntity<>(obj, HttpStatus.OK);
								}
							} else {
								obj.put("data", "User is not associated on this journal with this role");
								return new ResponseEntity<>(obj, HttpStatus.OK);
							}
						} else {
							obj.put("data", "Wrong password");
							return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
						}
					} else {
						obj.put("data", "User is not activated yet");
						return new ResponseEntity<>(obj, HttpStatus.OK);
					}
				} else {
					obj.put("data", "User not found");
					return new ResponseEntity<>(obj, HttpStatus.OK);
				}
			} else {
				obj.put("data", "Please provide email , password , journal and role");
				return new ResponseEntity<>(obj, HttpStatus.OK);
			}
		} catch (Exception e) {
			e.printStackTrace();
			obj.put("data", "Something went wrong");
			return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
		}
	}

	@RequestMapping(value = "/loginAuthenticateFromThirdParty", method = RequestMethod.POST)
	public ResponseEntity<?> loginAuthenticateFromThirdParty(@RequestBody UserVO userVo) {
		boolean staus = false;
		JSONObject obj = new JSONObject();
		try {
			UserVO userDetails = userService.getUserByEmailAndPassword(userVo.getEmail().trim());
			if (userDetails != null && userDetails.getEmail() != null) {
				staus = BCrypt.checkpw(userVo.getPassword().trim(), userDetails.getPassword());
				if (staus) {
					userDetails.setPassword(userDetails.getPassword());
					obj.put("data", userDetails);
					return new ResponseEntity<>(obj, HttpStatus.OK);
				} else {
					obj.put("data", "Wrong password");
					return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
				}
			} else {
				obj.put("data", "User not found");
				return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
			}
		} catch (Exception e) {
			e.printStackTrace();
			obj.put("data", "Something went wrong");
			return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
		}

	}

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public ResponseEntity<?> createUser(@RequestBody UserVO userVO) {
		log.info(AppConstants.REQUEST_METHOD + " :: " + AppConstants.POST + AppConstants.REQUEST_URI
				+ " :: /user/create" + AppConstants.REQUEST_BODY + " ::" + userVO.toString());
		JSONObject obj = new JSONObject();
		try {
			User isExist = userRepo.findByEmailUser(userVO.getEmail());
			if (isExist != null && isExist.getIsActive().equals(0)) {
				isExist.setIsActive(1); // Activate the user
				userRepo.save(isExist);// Save the updated user object to the database
				log.info(" Activating the user");

				obj.put("data", "User account is now activated.");
			}

			 if (isExist != null && isExist.getIsActive().equals(1)) {
				if (userVO.getJournalId() != null && !userVO.getJournalId().isEmpty() && userVO.getRoleId() != null
						&& !userVO.getRoleId().isEmpty()) {
					for (Integer journalId : userVO.getJournalId()) {
						for (Integer roleId : userVO.getRoleId()) {
							UserJournal i = userJournalRepo.getUserJournal(isExist.getId(), journalId, roleId);
							if (i != null) {
								i.setIsActive(1);
								if (roleId == 3 && userVO.getEditorRoleId() != null) {
									EditorRoleAccess editorRoleAccessData = editorRoleAccessRepo
											.getDataByUserAndRoleId(isExist.getId());
									if (editorRoleAccessData != null) {
										editorRoleAccessData.setIsActive(1);
										editorRoleAccessData.setEditorRoleId(userVO.getEditorRoleId());
										editorRoleAccessRepo.save(editorRoleAccessData);
									} else {
										EditorRoleAccess editorRoleAccess = new EditorRoleAccess();
										editorRoleAccess.setUserId(isExist.getId());
										editorRoleAccess.setIsActive(1);
										editorRoleAccess.setEditorRoleId(userVO.getEditorRoleId());
										editorRoleAccessRepo.save(editorRoleAccess);
									}
								}
								i.setUpdatedBy(userVO.getUpdatedBy());
								userJournalRepo.save(i);
								obj.put("data", "User already part of this journal.");
							} else {
								UserJournal userJournals = new UserJournal();
								userJournals.setJournalId(journalId);
								userJournals.setUserId(isExist.getId());
								userJournals.setRoleId(roleId);
								if (roleId == 3 && userVO.getEditorRoleId() != null) {
									EditorRoleAccess editorRoleAccessData = editorRoleAccessRepo
											.getDataByUserAndRoleId(isExist.getId());
									if (editorRoleAccessData != null) {
										editorRoleAccessData.setIsActive(1);
										editorRoleAccessData.setEditorRoleId(userVO.getEditorRoleId());
										editorRoleAccessRepo.save(editorRoleAccessData);
									} else {
										EditorRoleAccess editorRoleAccess = new EditorRoleAccess();
										editorRoleAccess.setUserId(isExist.getId());
										editorRoleAccess.setEditorRoleId(userVO.getEditorRoleId());
										editorRoleAccess.setIsActive(1);
										editorRoleAccessRepo.save(editorRoleAccess);
									}
								}
								userJournals.setIsActive(1);
								userJournals.setUpdatedDate(new Date());
								userJournals.setUpdatedBy(userVO.getUpdatedBy());
								userJournalRepo.save(userJournals);
								obj.put("data", "User is now associated with this journal.");
							}
						}
					}
				}
				if (StringUtils.isNotBlank(userVO.getPubmedId()))
					isExist.setPubmedId(userVO.getPubmedId());
				if (StringUtils.isNotBlank(userVO.getScopusId()))
					isExist.setScopusId(userVO.getScopusId());
				if (StringUtils.isNotBlank(userVO.getAfflication()))
					isExist.setAfflication(userVO.getAfflication());
				if (StringUtils.isNotBlank(userVO.getAcademic()))
					isExist.setAcademic(userVO.getAcademic());
				if (StringUtils.isNotBlank(userVO.getResearcherId()))
					isExist.setResearcherId(userVO.getResearcherId());
				if (StringUtils.isNotBlank(userVO.getTrialRegistrationNo()))
					isExist.setTrialRegistrationNo(userVO.getTrialRegistrationNo());
				if (StringUtils.isNotBlank(userVO.getExpertise()))
					isExist.setExpertise(userVO.getExpertise());
				if (StringUtils.isNotBlank(userVO.getSpecialization()))
					isExist.setSpecialization(userVO.getSpecialization());
				if (StringUtils.isNotBlank(userVO.getFaxNumber()))
					isExist.setFaxNumber(userVO.getFaxNumber());
				if (StringUtils.isNotBlank(userVO.getPhoneNumber()))
					isExist.setPhoneNumber(userVO.getPhoneNumber());
				if (userVO.getSalutation() != null)
					isExist.setSalutation(userVO.getSalutation());
				if (StringUtils.isNotBlank(userVO.getFirstName()))
					isExist.setFirstName(userVO.getFirstName());
				if (StringUtils.isNotBlank(userVO.getMiddleName()))
					isExist.setMiddleName(userVO.getMiddleName());
				if (StringUtils.isNotBlank(userVO.getLastName()))
					isExist.setLastName(userVO.getLastName());
				if (StringUtils.isNotBlank(userVO.getUsername()))
					isExist.setUsername(userVO.getUsername());
				if (StringUtils.isNotBlank(userVO.getInstitute()))
					isExist.setInstitute(userVO.getInstitute());
				if (StringUtils.isNotBlank(userVO.getMobileNumber()))
					isExist.setMobileNumber(userVO.getMobileNumber());
				if (StringUtils.isNotBlank(userVO.getOrcidId()))
					isExist.setOrcidId(userVO.getOrcidId());
				if (StringUtils.isNotBlank(userVO.getAddress()))
					isExist.setAddress(userVO.getAddress());
				if (StringUtils.isNotBlank(userVO.getCity()))
					isExist.setCity(userVO.getCity());
				if (userVO.getCountryId() != null)
					isExist.setCountryId(userVO.getCountryId());
				if (userVO.getCountryCode() != null)
					isExist.setCountryCode(userVO.getCountryCode());
				if (StringUtils.isNotBlank(userVO.getState()))
					isExist.setState(userVO.getState());
				if (StringUtils.isNotBlank(userVO.getPostalCode()))
					isExist.setPostalCode(userVO.getPostalCode());
				if (StringUtils.isNotBlank(userVO.getEditorAccessDetails()))
					isExist.setEditorAccessDetails(userVO.getEditorAccessDetails());
				log.info("-------------------Old User has been Updated------------------>");
				userRepo.save(isExist);

			} else if (isExist == null) {
				userService.createUser(userVO);
				obj.put("data", "User successfully created.");
				return new ResponseEntity<>(obj, HttpStatus.CREATED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			obj.put("data", "Something went wrong.");
			return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@RequestMapping(value = "/activateAccount", method = RequestMethod.POST)
	public ResponseEntity<?> activateLockedAccount(@RequestBody UserVO userVO) {
		JSONObject obj = new JSONObject();
		if (userVO.getPassword() != null && userVO.getToken() != null) {
			userService.activateLockedUser(userVO);
			obj.put("data", "Password reset successfully.");
		} else {
			obj.put("data", "Data not matched.");
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@RequestMapping(value = "/eicChangeRequest", method = RequestMethod.POST)
	public ResponseEntity<?> eicChangeRequest(@RequestBody IDVO iDVO) {
		JSONObject obj = new JSONObject();
		try {
			String i = userService.eicChangeRequest(iDVO);
			String p = userService.activateCurrentUser(iDVO);
			obj.put("data", i);
			obj.put("message", p);
		} catch (Exception e) {
			obj.put("data", "Something went wrong");
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public ResponseEntity<?> updateUser(@RequestBody UserVO userVO) {
		JSONObject obj = new JSONObject();
		try {
			HashSet<Integer> existingUserRoles = new HashSet<>();

			if (userVO != null
			        && userVO.getRoleId() != null
			        && !userVO.getRoleId().isEmpty()) {

			    UserVO existingUser = userService.getUserById(userVO.getId());

			    if (existingUser != null
			            && existingUser.getRoleId() != null
			            && !existingUser.getRoleId().isEmpty()) {

			        existingUserRoles.addAll(existingUser.getRoleId());

			        HashSet<Integer> newUserRoles = new HashSet<>(userVO.getRoleId());

			        existingUserRoles.removeAll(newUserRoles);

			    } else {
			        log.warn("Existing user roles not found for userId: {}", userVO.getId());
			    }
			}

			userService.updateUser(userVO);
			if (!existingUserRoles.isEmpty()) {

				log.info("Starting removal process for userId: {}", userVO.getId());
				log.info("Journal IDs: {}", userVO.getJournalId());
				log.info("Existing Roles: {}", existingUserRoles);

				for (int currJournalId : userVO.getJournalId()) {
					log.debug("Processing Journal ID: {}", currJournalId);

					for (int roleId : existingUserRoles) {
						log.debug("Removing roleId: {} for userId: {} in journalId: {}",
								roleId, userVO.getId(), currJournalId);

						try {
							userService.updateUserDataOnRemoval(currJournalId, roleId, userVO.getId());
							log.debug("Successfully removed roleId: {} for journalId: {}", roleId, currJournalId);
						} catch (Exception e) {
							log.error("Error removing roleId: {} for userId: {} in journalId: {}",
									roleId, userVO.getId(), currJournalId, e);
						}
					}
				}

				log.info("Completed removal process for userId: {}", userVO.getId());

			} else {
				log.info("No existing roles found for userId: {}", userVO.getId());
			}

			// updating corresponding author details
			ManuScriptCoAuthor manuScriptCoAuthor = manuScriptCoAuthorRepo
					.getManuScriptCoAuthorDetails(userVO.getCoAuthorID(), userVO.getId());
			if (manuScriptCoAuthor != null) {
				ManuScript getManuScript = manuScriptRepo.getById(manuScriptCoAuthor.getCoAuthorManuscriptId());
				List<ManuScriptCoAuthor> list = manuScriptCoAuthorRepo
						.getCorrespondingAuthorListFromManuscriptId(getManuScript.getManuScriptId());
				Journal journalData = journalRepo.getOne(getManuScript.getJournalId());

				if (StringUtils.isNotBlank(userVO.getIsCorrespondingAuthor())
						&& StringUtils.isNotBlank(manuScriptCoAuthor.getIsCorrespondingAuthor())
						&& userVO.getIsCorrespondingAuthor().equals("Y")
						&& manuScriptCoAuthor.getIsCorrespondingAuthor().equals("Y")) {
					// bypass if user already corresponding and from UI user updating the same again
				} else {
					if (userVO.getIsCorrespondingAuthor() != null
							&& StringUtils.isNotBlank(manuScriptCoAuthor.getIsCorrespondingAuthor())
							&& userVO.getIsCorrespondingAuthor().equals("Y")) {
						if (journalData.getCorrespondingCount() != null
								&& list.size() < journalData.getCorrespondingCount()) {
							manuScriptCoAuthorRepo.updateCorrespondingAuthor(userVO.getIsCorrespondingAuthor(),
									userVO.getCoAuthorID());
							obj.put("message", "Data Sucessfully Updated...");
						} else {
							obj.put("message", "Please update corresponding count in Journal Configuraion-"
									+ journalData.getCorrespondingCount());
						}
					} else {
						manuScriptCoAuthorRepo.updateCorrespondingAuthor(userVO.getIsCorrespondingAuthor(),
								userVO.getCoAuthorID());
						obj.put("message", "Data Sucessfully Updated");
					}
				}
			}
			obj.put("message", "Data Sucessfully Updated");
		} catch (Exception e) {
			log.info("Exception" + e);
			e.printStackTrace();
			obj.put("message", "Something Went Wrong");
			return new ResponseEntity<>(obj, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@RequestMapping(value = "/updateUserJournal", method = RequestMethod.POST)
	public ResponseEntity<?> updateUserJournal(@RequestBody UserVO userVO) {
		JSONObject obj = new JSONObject();
		try {
			userService.updateUserJournal(userVO);
			obj.put("message", "Data Sucessfully Updated");
		} catch (Exception e) {
			log.info("Exception" + e);
			e.printStackTrace();
			obj.put("message", "Something Went Wrong");
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ResponseEntity<?> getUserList() {
		JSONObject obj = new JSONObject();
		List<UserVO> userList = userService.getUserDetails();
		if (userList != null) {
			obj.put("data", userList);
			return new ResponseEntity<>(obj, HttpStatus.OK);
		}
		obj.put("data", "Data not found");
		return new ResponseEntity<>(obj, HttpStatus.FORBIDDEN);
	}

	@RequestMapping(value = "/getById", method = RequestMethod.POST)
	public ResponseEntity<?> getUserById(@RequestBody IDVO vo) {
		UserVO userVo = userService.getUserById(vo.getId());
		return new ResponseEntity<>(userVo, HttpStatus.OK);
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public ResponseEntity<?> deleteUser(@RequestBody IDVO id) {
		userService.deleteUserById(id);
		obj.put("message", "deleted successfully");
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@RequestMapping(value = "/getUserByRoles", method = RequestMethod.POST)
	public List<UserVO> getUsersByRoles(@RequestBody IDVO id) {
		List<UserVO> voList = userService.getUserListByRoleId(id);
		return voList;
	}

	@RequestMapping(value = "/assignUser", method = RequestMethod.POST)
	public ResponseEntity<?> assignUser(@RequestBody ManuscriptListVO manuscriptListVO) {
		log.info("Inside UserRestController assignUser");
		JSONObject obj = new JSONObject();
		try {
			ManuScript getManuScript = manuScriptRepo.getById(manuscriptListVO.getManuScriptId());
			Integer assignedUserRole = getManuScript.getAssignedUserRole();
			Integer assigneduser = getManuScript.getAssignedUser();
			userService.assignUser(manuscriptListVO);
			obj.put("data", "Assigned successfully");

			// Email Trigger to Eic/Editor While Assigning Reviewer
			if (manuscriptListVO.getManuStatusId() != null && manuscriptListVO.getManuStatusId().equals(8)) {
				EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
				emailTriggerVO.setJournalId(manuscriptListVO.getJournalId());
				emailTriggerVO.setUserId(manuscriptListVO.getUserId());
				if (manuscriptListVO.getEditorRoleId().equals(2))
					emailTriggerVO.setManagingEditorId(manuscriptListVO.getUserId());
				else
					emailTriggerVO.setEditorUserId(manuscriptListVO.getUserId());
				emailTriggerVO.setManagingEditorId(manuscriptListVO.getUserId());
				emailTriggerVO.setManuscriptId(manuscriptListVO.getManuScriptId());
				emailTriggerVO.setManuscriptStatus(manuscriptListVO.getManuStatusId());
				emailTriggerVO.setReceiverRoleId(manuscriptListVO.getEditorRoleId());
				emailTriggerVO.setTaskId(31);
				emailTriggerVO.setRoleId(manuscriptListVO.getEditorRoleId());
				log.info("Inside in Email Trigger to Eic/Editor While Assigning Reviewer For Email Trigger Data are:->"
						+ emailTriggerVO.toString());
				log.info("User Receiver Id:->" + manuscriptListVO.getUserId());
				log.info("User Receiver Role Id:->" + manuscriptListVO.getEditorRoleId());
				log.info("User Sender Role Id:->" + manuscriptListVO.getEditorRoleId());
				log.info("User Sender Id:->" + manuscriptListVO.getUserId());
				log.info("Task Id:->" + 31);
				String message = emailTriggerService.emailTrigger(emailTriggerVO);
				obj.put("data", message);
				obj.put("data", "Assigned successfully");
				log.info(
						"Message of Email Trigger to Eic/Editor While Assigning Reviewer For Email Trigger " + message);
				log.info("End of assignUser Email Trigger to Eic/Editor While Assigning Reviewer For Email Trigger ");
			}

			// Email Trigger to Eic/Editor While Assigning Reviewer
			if (manuscriptListVO.getManuStatusId() != null && manuscriptListVO.getManuStatusId().equals(66)) {
				EmailTriggerVO emailTriggerVO1 = new EmailTriggerVO();
				emailTriggerVO1.setJournalId(manuscriptListVO.getJournalId());
				emailTriggerVO1.setUserId(manuscriptListVO.getUserId());
				emailTriggerVO1.setManagingEditorId(manuscriptListVO.getUserId());
				emailTriggerVO1.setManuscriptId(manuscriptListVO.getManuScriptId());
				emailTriggerVO1.setManuscriptStatus(manuscriptListVO.getManuStatusId());
				emailTriggerVO1.setReceiverRoleId(manuscriptListVO.getEditorRoleId());
				emailTriggerVO1.setTaskId(69);
				emailTriggerVO1.setRoleId(manuscriptListVO.getEditorRoleId());
				log.info("User Receiver Id:->" + manuscriptListVO.getUserId());
				log.info("User Receiver Role Id:->" + 2);
				log.info("User Sender Role Id:->" + 2);
				log.info("User Sender Id:->" + manuscriptListVO.getUserId());
				log.info("Task Id:->" + 30);
				String message1 = emailTriggerService.emailTrigger(emailTriggerVO1);
				log.info("Message of assignUser Role For Editor/EIC For Email Trigger " + message1);

			}

			// Email Trigger
			if (manuscriptListVO.getRoleId() != null && manuscriptListVO.getRoleId().equals(3)) {
				if (manuscriptListVO.getUserId() != null) {
					// Mail will trigger to Default Editor-in-Chief when manuscript is sent for
					// Assign Editor action
					EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
					emailTriggerVO.setJournalId(manuscriptListVO.getJournalId());
					emailTriggerVO.setUserId(manuscriptListVO.getUserId());
					emailTriggerVO.setManuscriptId(manuscriptListVO.getManuScriptId());
					emailTriggerVO.setManuscriptStatus(manuscriptListVO.getManuStatusId());
					emailTriggerVO.setCommentToEditor(manuscriptListVO.getMessage());
					emailTriggerVO.setReceiverRoleId(manuscriptListVO.getEditorRoleId());
					emailTriggerVO.setRoleId(manuscriptListVO.getEditorRoleId());
					emailTriggerVO.setManagingEditorId(manuscriptListVO.getUserId());
					emailTriggerVO.setTaskId(30);
					log.info(
							"Inside in assignUser Role For Editor Mail will trigger to Default Editor-in-Chief when manuscript is sent for Assign Editor action For Email Trigger Data are:->"
									+ emailTriggerVO.toString());
					log.info("Task Id:->" + 30);
					String message = emailTriggerService.emailTrigger(emailTriggerVO);
					obj.put("data", message);
					obj.put("data", "Assigned successfully");
					log.info(
							"End of assignUser Role Mail will trigger to Default Editor when manuscript is sent for Assign Editor action For Editor For Email Trigger ");
				}

				log.info("Inside in assignUser Role For Editor For Email Trigger");
				if (manuscriptListVO.getManuScriptId() != null && manuscriptListVO.getUserId() != null
						&& manuscriptListVO.getJournalId() != null && manuscriptListVO.getAssignToID() != null
						&& !manuscriptListVO.getAssignToID().isEmpty()) {
					manuscriptListVO.getAssignToID().stream().forEach(i -> {
						EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
						emailTriggerVO.setJournalId(manuscriptListVO.getJournalId());
						if (manuscriptListVO.getJournalMailTemplatesBody() != null
								&& !manuscriptListVO.getJournalMailTemplatesBody().isEmpty())
							emailTriggerVO.setEmailTemplateBody(manuscriptListVO.getJournalMailTemplatesBody());
						if (manuscriptListVO.getJournalMailTemplatesSubject() != null
								&& !manuscriptListVO.getJournalMailTemplatesSubject().isEmpty())
							emailTriggerVO.setEmailTemplateSubject(manuscriptListVO.getJournalMailTemplatesSubject());

						emailTriggerVO.setUserId(i);
						emailTriggerVO.setEditorUserId(i);
						emailTriggerVO.setManuscriptId(manuscriptListVO.getManuScriptId());
						emailTriggerVO.setManuscriptStatus(manuscriptListVO.getManuStatusId());
						emailTriggerVO.setReceiverRoleId(manuscriptListVO.getRoleId());
						emailTriggerVO.setCommentToEditor(manuscriptListVO.getMessage());
						emailTriggerVO.setTaskId(3);
						emailTriggerVO.setRoleId(manuscriptListVO.getEditorRoleId());
						if (manuscriptListVO.getEditorRoleId().equals(2)) {
							emailTriggerVO.setManagingEditorId(manuscriptListVO.getUserId());
						}
						log.info("Inside in assignUser Role For Editor For Email Trigger Data are:->"
								+ emailTriggerVO.toString());
						log.info("Task Id:->" + 3);
						String message = emailTriggerService.emailTrigger(emailTriggerVO);
						obj.put("data", message);
						obj.put("data", "Assigned successfully");
						log.info("End of assignUser Role For Editor For Email Trigger ");
					});

				}
			}

			if ((((manuscriptListVO.getEditorRoleId() != null && manuscriptListVO.getEditorRoleId().equals(2))
					|| (manuscriptListVO.getRoleId() != null && manuscriptListVO.getRoleId().equals(2)))
					&& assignedUserRole == 3)
					|| ((manuscriptListVO.getEditorRoleId() != null && manuscriptListVO.getEditorRoleId().equals(3))
							|| (manuscriptListVO.getRoleId() != null && manuscriptListVO.getRoleId().equals(3)))
							&& assignedUserRole == 3 && !manuscriptListVO.getUserId().equals(assigneduser)) {
				EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
				emailTriggerVO.setJournalId(manuscriptListVO.getJournalId());
				emailTriggerVO.setUserId(assigneduser);
				emailTriggerVO.setAuthorUserId(getManuScript.getAuthorId());
				emailTriggerVO.setManuscriptId(manuscriptListVO.getManuScriptId());
				emailTriggerVO.setManuscriptStatus(manuscriptListVO.getManuStatusId());
				emailTriggerVO.setReceiverRoleId(assignedUserRole);
				emailTriggerVO.setTaskId(86);
				emailTriggerVO.setRoleId(assignedUserRole);
				emailTriggerVO.setEditorUserId(assigneduser);
				String message = emailTriggerService.emailTrigger(emailTriggerVO);
				obj.put("data", message);
				obj.put("data", "Assigned successfully");
				log.debug("Emil Trigger" + message);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Some Error occured");
			obj.put("message", e.getMessage());
			return new ResponseEntity<>(obj, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		log.info("End of UserRestController assignUser");
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@RequestMapping(value = "/userDetailsByEmail", method = RequestMethod.POST)
	public ResponseEntity<?> getUserDetailsByEmail(@RequestBody User user) {

		try {
			UserVO userDetails = userService.getByEmailVO(user.getEmail());
			if (userDetails != null) {
				userDetails.setPassword("");
				obj.put("data", userDetails);
			} else {
				obj.put("data", "");
			}

		} catch (Exception e) {
			log.error("Exception : " + e.getMessage());
			e.printStackTrace();
			obj.put("data", "Something went wrong");
			return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@RequestMapping(value = "/updatePassword", method = RequestMethod.POST)
	public ResponseEntity<?> updatePassword(@RequestBody UpdatePassword updatePassword) {
		try {
			String userDetails = userService.updatePassword(updatePassword);
			if (userDetails != null) {
				obj.put("data", userDetails);
			} else {
				obj.put("data", "Incorrect Current Password please Provide correct Password");
			}
			return new ResponseEntity<>(obj, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Exception : " + e.getMessage());
			obj.put("data", "Something went wrong");
			return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
		}
	}

	// User Login Details
	@PostMapping("logedUser")
	public ResponseEntity<?> logedUser(@RequestBody LoggedUserVo loggedUserVo) {
		JSONObject obj = new JSONObject();
		try {
			String data = userService.logedUser(loggedUserVo);
			obj.put("data", data);
		} catch (Exception e) {
			e.printStackTrace();
			obj.put("data", "Something Went Wrong");
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@PostMapping("getLogedUserDetails")
	public ResponseEntity<?> getLogedUserDetails(@RequestBody LoggedUserVo loggedUserVo) {
		JSONObject obj = new JSONObject();
		try {
			List<LoggedUserVo> data = userService.getLogedUserDetails(loggedUserVo);
			if (data != null && !data.isEmpty())
				obj.put("data", data);
			else
				obj.put("data", "data not found");
		} catch (Exception e) {
			e.printStackTrace();
			obj.put("data", "Something Went Wrong");
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@PostMapping("getUserByRoleId")
	public ResponseEntity<?> getUserByRoleId(@RequestBody IDVO id) {
		try {
			List<UserVO> userData = userService.getUserByRoleId(id);
			if (userData != null && !userData.isEmpty()) {
				obj.put("data", userData);
			} else {
				obj.put("data", "Data Not Found");
			}
		} catch (Exception e) {
			obj.put("data", "Something Went Wrong");
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@PostMapping("getUniqueUserByRoleId")
	public ResponseEntity<?> getUniqueUserByRoleId(@RequestBody IDVO id) {
		try {
			List<UserVO> userData = userService.getUniqueUserByRoleId(id);
			if (userData != null && !userData.isEmpty()) {
				obj.put("data", userData);
			} else {
				obj.put("data", "Data Not Found");
			}
		} catch (Exception e) {
			obj.put("data", "Something Went Wrong");
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@GetMapping("getUserCountOnAdmin")
	public ResponseEntity<?> getUserCountOnAdmin() {
		log.info(AppConstants.REQUEST_METHOD + " :: " + AppConstants.GET + AppConstants.REQUEST_URI
				+ " :: /user/getUserCountOnAdmin");
		try {
			IDVO userData = userService.getUserCountOnAdmin();
			if (userData != null) {
				obj.put("data", userData);
			} else {
				obj.put("data", "Data Not Found");
			}
		} catch (Exception e) {
			obj.put("data", "Something Went Wrong");
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@PostMapping("getUserDetailsByJournalAndRole")
	public ResponseEntity<?> getUserDetailsByJournalAndRole(@RequestBody IDVO IDVO) {
		try {
			List<UserVO> userData = userService.getUserDetailsByJournalAndRole(IDVO);
			if (userData != null && !userData.isEmpty()) {
				obj.put("data", userData);
			} else {
				obj.put("data", "Data Not Found");
			}
		} catch (Exception e) {
			obj.put("data", "Something Went Wrong");
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@PostMapping("getLockedUser")
	public ResponseEntity<?> getLockedUser(@RequestBody IDVO id) {
		try {
			List<UserVO> userDetails = userService.getLockedUser(id);
			if (userDetails != null && !userDetails.isEmpty()) {
				obj.put("data", userDetails);
				return new ResponseEntity<>(obj, HttpStatus.OK);
			} else {
				obj.put("data", "data not found");
				return new ResponseEntity<>(obj, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			obj.put("data", "Something went wrong");
			return new ResponseEntity<>(obj, HttpStatus.EXPECTATION_FAILED);
		}
	}

	@PostMapping("applyAsReviewer")
	public ResponseEntity<?> applyAsReviewer(@RequestBody IDVO id) {
		try {
			boolean isTrue = userRoleService.getRoleByUser(id.getId(), 5);
			UserJournal userJournal = userJournalService.getUserJournalByJournal(id.getId(), id.getJournalId());
			boolean alreadyApply = userService.getApplyForReviewer(id.getId());
			if (isTrue || userJournal != null) {
				obj.put("data", "Already Associated with this role");
			} else if (alreadyApply) {
				obj.put("data", "Already Applyed");
			} else {
				userService.applyAsReviewer(id.getId(), id.getJournalId());
				obj.put("data", "Applyed successfully");
			}
		} catch (Exception e) {
			obj.put("data", "Something went wrong");
		}
		return new ResponseEntity<>(obj, HttpStatus.EXPECTATION_FAILED);
	}

	@PostMapping("validateAuthorSignUp")
	public ResponseEntity<?> validateAuthorSignUp(@RequestBody IDVO id) {
		try {
			String message = userService.validateAuthorSignUp(id);
			if (message != null && !message.isEmpty()) {
				obj.put("data", message);
				return new ResponseEntity<>(obj, HttpStatus.OK);
			} else {
				obj.put("data", "Link has been expired");
				return new ResponseEntity<>(obj, HttpStatus.OK);
			}
		} catch (Exception e) {
			obj.put("data", "Something went wrong");
			return new ResponseEntity<>(obj, HttpStatus.EXPECTATION_FAILED);
		}
	}

	@PostMapping("authorRegistrationMailSend")
	public ResponseEntity<?> authorRegistrationMailSend(@RequestBody UserVO userVO) {
		log.info(AppConstants.REQUEST_METHOD + " : POST" + AppConstants.REQUEST_METHOD
				+ " : /user/authorRegistrationMailSend" + AppConstants.REQUEST_BODY + " : " + userVO.toString());
		try {
			String message = userService.authorRegistrationMailSend(userVO);
			obj.put("data", message);
			return new ResponseEntity<>(obj, HttpStatus.OK);
		} catch (Exception e) {
			obj.put("data", "Something went wrong");
			return new ResponseEntity<>(obj, HttpStatus.EXPECTATION_FAILED);
		}
	}

	@RequestMapping(value = "/loginAuthenticateByOrcid", method = RequestMethod.POST)
	public ResponseEntity<?> loginAuthenticateByOrcid(@RequestParam String orcidId) {
		JSONObject obj = new JSONObject();
		try {
			UserVO userDetails = userService.getUserByOrcidId(orcidId);
			if (userDetails != null) {
				obj.put("data", userDetails);
				return new ResponseEntity<>(obj, HttpStatus.OK);
			} else {
				obj.put("data", "User not found");
				return new ResponseEntity<>(obj, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			obj.put("data", "Something went wrong");
			return new ResponseEntity<>(obj, HttpStatus.EXPECTATION_FAILED);
		}
	}

	@RequestMapping(value = "/loginAuthenticateByEmail", method = RequestMethod.POST)
	public ResponseEntity<?> loginAuthenticateByEmail(@RequestParam String email, @RequestParam String phoneNumber) {
		JSONObject obj = new JSONObject();
		try {
			UserVO userDetails = userService.getUserByEmailId(email);
			if (userDetails != null) {
				obj.put("data", userDetails);
				return new ResponseEntity<>(obj, HttpStatus.OK);
			} else {
				obj.put("data", "User not found");
				return new ResponseEntity<>(obj, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			obj.put("data", "Something went wrong");
			return new ResponseEntity<>(obj, HttpStatus.EXPECTATION_FAILED);
		}
	}

	@RequestMapping(value = "/loginAuthenticateByRole", method = RequestMethod.POST)
	public ResponseEntity<?> loginAuthenticateByRole(@RequestBody IDVO id) {
		JSONObject obj = new JSONObject();
		try {
			UserVO userDetails = userService.loginAuthenticateByRole(id);
			if (userDetails != null) {
				obj.put("data", userDetails);
				return new ResponseEntity<>(obj, HttpStatus.OK);
			} else {
				obj.put("data", "Thank you for applying");
				return new ResponseEntity<>(obj, HttpStatus.OK);
			}
		} catch (Exception e) {
			e.printStackTrace();
			obj.put("data", "Something went wrong");
			return new ResponseEntity<>(obj, HttpStatus.EXPECTATION_FAILED);
		}
	}

	@RequestMapping(value = "/userVerification", method = RequestMethod.POST)
	public ResponseEntity<?> userVerification(@RequestBody IDVO id) {
		JSONObject obj = new JSONObject();
		try {
			String userDetails = userService.userVerification(id);
			obj.put("data", userDetails);
			return new ResponseEntity<>(obj, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			obj.put("data", "Something went wrong");
			return new ResponseEntity<>(obj, HttpStatus.EXPECTATION_FAILED);
		}
	}

	@RequestMapping(value = "/userActivation", method = RequestMethod.POST)
	public ResponseEntity<?> userActivation(@RequestBody IDVO id) {
		log.info(AppConstants.REQUEST_METHOD + " :: " + AppConstants.POST + AppConstants.REQUEST_URI
				+ " :: /user/userActivation" + AppConstants.REQUEST_BODY + " ::" + id.toString());
		JSONObject obj = new JSONObject();
		try {
			String userDetails = userService.userActivation(id);
			obj.put("data", userDetails);
			return new ResponseEntity<>(obj, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			obj.put("data", "Something went wrong");
			return new ResponseEntity<>(obj, HttpStatus.EXPECTATION_FAILED);
		}
	}

	@RequestMapping(value = "/userVerificationUpdationPassword", method = RequestMethod.POST)
	public ResponseEntity<?> userVerificationUpdationPassword(@RequestBody IDVO id) {
		log.info(AppConstants.REQUEST_METHOD + " :: " + AppConstants.POST + AppConstants.REQUEST_URI
				+ " :: /user/userVerificationUpdationPassword" + AppConstants.REQUEST_BODY + " ::" + id.toString());
		JSONObject obj = new JSONObject();
		try {
			String userDetails = userService.userVerificationUpdationPassword(id);
			obj.put("data", userDetails);
			return new ResponseEntity<>(obj, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			obj.put("data", "Something went wrong");
			return new ResponseEntity<>(obj, HttpStatus.EXPECTATION_FAILED);
		}
	}

	@RequestMapping(value = "/searchUser", method = RequestMethod.POST)
	public ResponseEntity<?> searchUser(@RequestBody IDVO id) {
		JSONObject obj = new JSONObject();
		try {
			List<UserVO> data = userService.searchUser(id);
			if (data != null && !data.isEmpty()) {
				obj.put("data", data);
			} else {
				obj.put("data", "Data not found");
			}
			return new ResponseEntity<>(obj, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			obj.put("data", "Something went wrong");
			return new ResponseEntity<>(obj, HttpStatus.EXPECTATION_FAILED);
		}
	}

	@RequestMapping(value = "/searchActiveUser", method = RequestMethod.POST)
	public ResponseEntity<?> searchActiveUser(@RequestBody IDVO id) {
		JSONObject obj = new JSONObject();
		try {
			List<UserVO> data = userService.searchActiveUser(id);
			if (data != null && !data.isEmpty()) {
				obj.put("data", data);
			} else {
				obj.put("data", "Data not found");
			}
			return new ResponseEntity<>(obj, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			obj.put("data", "Something went wrong");
			return new ResponseEntity<>(obj, HttpStatus.EXPECTATION_FAILED);
		}
	}

	@RequestMapping(value = "/getSpecialization", method = RequestMethod.POST)
	public ResponseEntity<?> getSpecialization(@RequestBody IDVO id) {
		JSONObject obj = new JSONObject();
		try {
			List<String> data = userService.getSpecialization(id);
			if (data != null && !data.isEmpty()) {
				obj.put("data", data);
			} else {
				obj.put("data", "Data not found");
			}
			return new ResponseEntity<>(obj, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			obj.put("data", "Something went wrong");
			return new ResponseEntity<>(obj, HttpStatus.EXPECTATION_FAILED);
		}
	}

	@RequestMapping(value = "/getExpertise", method = RequestMethod.POST)
	public ResponseEntity<?> getExpertise(@RequestBody IDVO id) {
		JSONObject obj = new JSONObject();
		try {
			List<String> data = userService.getExpertise(id);
			if (data != null && !data.isEmpty()) {
				obj.put("data", data);
			} else {
				obj.put("data", "Data not found");
			}
			return new ResponseEntity<>(obj, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			obj.put("data", "Something went wrong");
			return new ResponseEntity<>(obj, HttpStatus.EXPECTATION_FAILED);
		}
	}

	@RequestMapping(value = "/reviewerRefineSearch", method = RequestMethod.POST)
	public ResponseEntity<?> reviewerRefineSearch(@RequestBody IDVO id) {
		JSONObject obj = new JSONObject();
		try {
			List<UserVO> data = userService.reviewerRefineSearch(id);
			int count = userService.getUserCountByJournalAndRole(id);
			obj.put("totalCount", count);
			if (data != null && !data.isEmpty()) {
				obj.put("data", data);
			} else {
				obj.put("data", "Data not found");
			}
			return new ResponseEntity<>(obj, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			obj.put("data", "Something went wrong");
			return new ResponseEntity<>(obj, HttpStatus.EXPECTATION_FAILED);
		}
	}

	@GetMapping("TestDataUpload")
	public ResponseEntity<?> TestDataUpload() {

		String str = "Test";

		try {
			FileWriter myWriter = new FileWriter("E:\\outputxml.txt");
			UserVO userVO = new UserVO();

			File file = new File("E:\\a.xlsx"); // creating a new file instance
			FileInputStream fis = new FileInputStream(file); // obtaining bytes from the file
			// creating Workbook instance that refers to .xlsx file
			XSSFWorkbook wb = new XSSFWorkbook(fis);
			XSSFSheet sheet = wb.getSheetAt(0); // creating a Sheet object to retrieve object
			Iterator<Row> itr = sheet.iterator(); // iterating over excel file
			int j = 0;
			boolean status = false;
			while (itr.hasNext()) {
				Row row = itr.next();
				Iterator<Cell> cellIterator = row.cellIterator(); // iterating over each column
				int i = 0;
				if (j != 0) {
					while (cellIterator.hasNext()) {
						Cell cell = cellIterator.next();
						if (i == 0) {
							if (cell.toString().equalsIgnoreCase("Journal of Microscopy and Ultrastructure")) {
								List JournalId = new ArrayList();
								JournalId.add(31357);
								userVO.setJournalId(JournalId);
							}
						} else if (i == 1) {
							if (cell.toString().equalsIgnoreCase("Author")) {
								List roleId = new ArrayList();
								roleId.add(4);
								userVO.setRoleId(roleId);
							} else if (cell.toString().equalsIgnoreCase("Referee")) {
								List roleId = new ArrayList();
								roleId.add(5);
								userVO.setRoleId(roleId);
							} else if (cell.toString().equalsIgnoreCase("Section Editor")) {
								List roleId = new ArrayList();
								roleId.add(3);
								userVO.setRoleId(roleId);
								userVO.setEditorRoleId(13);
							} else if (cell.toString().equalsIgnoreCase("Editor")) {
								List roleId = new ArrayList();
								roleId.add(3);
								userVO.setRoleId(roleId);
								userVO.setEditorRoleId(13);
							} else if (cell.toString().equalsIgnoreCase("Managing Editor")) {
								List roleId = new ArrayList();
								roleId.add(2);
								userVO.setRoleId(roleId);
								userVO.setEditorRoleId(12);
							} else if (cell.toString().equalsIgnoreCase("Editor-In-Chief")) {
								List roleId = new ArrayList();
								roleId.add(3);
								userVO.setRoleId(roleId);
								userVO.setEditorRoleId(12);
							} else if (cell.toString().equalsIgnoreCase("Associate Editor")) {
								List roleId = new ArrayList();
								roleId.add(3);
								userVO.setRoleId(roleId);
								userVO.setEditorRoleId(13);
							} else if (cell.toString().equalsIgnoreCase("Admin")) {
								List roleId = new ArrayList();
								roleId.add(2);
								userVO.setRoleId(roleId);
							}
						} else if (i == 2) {
							if (cell.toString().equalsIgnoreCase("NOT AVAILABLE")) {

							} else {
								userVO.setUsername(cell.toString());
							}
						} else if (i == 3) {
							if (cell.toString().equalsIgnoreCase("NOT AVAILABLE")) {

							} else {
								userVO.setEmail(cell.toString());
								userVO.setEmailCC(cell.toString());
							}
						} else if (i == 4) {
							if (cell.toString().equalsIgnoreCase("NOT AVAILABLE")) {

							} else {
								userVO.setPhoneNumber(cell.toString());
							}
						} else if (i == 5) {
							if (cell.toString().equalsIgnoreCase("NOT AVAILABLE")) {
								userVO.setSalutation(Enum.valueOf(Salutation.class, "Mx"));
							} else {

								userVO.setSalutation(
										Enum.valueOf(Salutation.class, cell.toString().replace(".", "").trim()));
							}
						} else if (i == 6) {
							if (cell.toString().equalsIgnoreCase("NOT AVAILABLE")) {

							} else {
								userVO.setFirstName(cell.toString());
							}
						} else if (i == 7) {
							if (cell.toString().equalsIgnoreCase("NOT AVAILABLE")) {

							} else {
								userVO.setLastName(cell.toString());
							}
						} else if (i == 8) {
							if (cell.toString().equalsIgnoreCase("NOT AVAILABLE")) {

							} else {
								userVO.setAddress(cell.toString());
							}
						} else if (i == 9) {
							if (cell.toString().equalsIgnoreCase("NOT AVAILABLE")) {

							} else {
								userVO.setMiddleName(cell.toString());
							}
						} else if (i == 10) {
							if (cell.toString().equalsIgnoreCase("NOT AVAILABLE")) {

							} else {
								// userVO.setEmail(cell.toString());
								Countries countries = countriesService.getCountryByName(cell.toString().trim());
								if (countries != null) {
									userVO.setCountryId(countries.getId());
								}
							}
						} else if (i == 11) {
							if (cell.toString().equalsIgnoreCase("NOT AVAILABLE")) {

							} else {
								userVO.setCity(cell.toString());
							}
						} else if (i == 12) {
							if (cell.toString().equalsIgnoreCase("NOT AVAILABLE")) {

							} else {

								userVO.setPhoneNumber(cell.toString());
							}
						} else if (i == 13) {
							if (cell.toString().equalsIgnoreCase("NOT AVAILABLE")) {

							} else {
								userVO.setFaxNumber(cell.toString());
							}
						} else if (i == 14) {
							if (cell.toString().equalsIgnoreCase("NOT AVAILABLE")) {

							} else {

								userVO.setState(cell.toString());
							}
						} else if (i == 15) {
							if (cell.toString().equalsIgnoreCase("NOT AVAILABLE")) {

							} else {
								userVO.setPostalCode(cell.toString().replace(".0", "").trim());
							}
						} else if (i == 16) {
							if (cell.toString().equalsIgnoreCase("NOT AVAILABLE")) {

							} else {
							}
						} else if (i == 17) {
							if (cell.toString().equalsIgnoreCase("NOT AVAILABLE")) {

							} else {
							}
						} else if (i == 18) {
							if (cell.toString().equalsIgnoreCase("NOT AVAILABLE")) {

							} else {
							}
						} else if (i == 19) {
							if (cell.toString().equalsIgnoreCase("NOT AVAILABLE")) {

							} else {
								userVO.setSpecialization(cell.toString());
							}
						} else if (i == 20) {
							if (cell.toString().equalsIgnoreCase("NOT AVAILABLE")) {

							} else {
								userVO.setExpertise(cell.toString());
							}
						} else if (i == 21) {
							if (cell.toString().equalsIgnoreCase("NOT AVAILABLE")) {

							} else {
								userVO.setInstitute(cell.toString());
							}
						}
						i++;
					}
					try {
						User isExist = userRepo.findByEmailUser(userVO.getEmail());
						if (isExist != null && isExist.getIsActive().equals(1)) {
							if (userVO.getRoleId() != null && !userVO.getRoleId().isEmpty()
									&& userVO.getJournalId() != null && !userVO.getJournalId().isEmpty()) {
								userVO.getRoleId().stream().forEach(ik -> {
									UserRole getUserRoleData = userRoleRepo.getUseRoleListByRoleList(isExist.getId(),
											ik);
									if (getUserRoleData != null) {
										getUserRoleData.setIsActive(1);
										userRoleRepo.save(getUserRoleData);
										obj.put("data", "User already part of this role.");

									} else {
										UserRole data = new UserRole();
										data.setUser(isExist.getId());
										data.setRole(ik);
										data.setUpdatedBy(isExist.getId());
										data.setIsActive(1);
										userRoleRepo.save(data);
										obj.put("data", "Role has been assigned to this user.");
									}

								});

								userVO.getJournalId().stream().forEach(jk -> {
									UserJournal getUserJournalData = userJournalRepo
											.getUserJournalByJournalUNique(isExist.getId(), jk);
									if (getUserJournalData != null && getUserJournalData.getIsActive() != null
											&& getUserJournalData.getIsActive().equals(1)) {
										obj.put("data", "User already part of this journal.");
									} else if (getUserJournalData != null && getUserJournalData.getIsActive() != null
											&& getUserJournalData.getIsActive().equals(0)) {
										getUserJournalData.setIsActive(1);
										userJournalRepo.save(getUserJournalData);
										obj.put("data",
												"User Journal account is not activated yet please go and check mail and activate your account.");
									} else {
										Journal journalData = journalRepo.getOne(jk);
										if (journalData != null) {
											UserJournal getUserJournalData1 = new UserJournal();

											getUserJournalData1.setUserId(isExist.getId());
											getUserJournalData1.setJournalId(jk);
											getUserJournalData1.setIsActive(1);
											getUserJournalData1.setUpdatedBy(isExist.getId());
											userJournalService.saveUserJournal(getUserJournalData1);
											// userService.sendMailForAuthorAssociatedWithJournal(userVO.getEmail(),
											// userVO.getSalutation() + "." + " " + userVO.getFirstName() + " " +
											// userVO.getLastName(), userVO.getJournalActivationUrl(),
											// journalData.getJournalTitle(), isExist.getId(),
											// userVO.getJournalId().get(0));
											obj.put("data", "Journal is associated with this user please check mail.");
										} else {
											obj.put("data", "Journal is not valid.");
										}
									}
								});

							} else {
								obj.put("data", "User already exist.");
							}
							// return new ResponseEntity<>(obj, HttpStatus.OK);
						} else if (isExist != null && isExist.getIsActive().equals(0)) {
							obj.put("data",
									"User account is not activated yet please go and check mail and activate your account.");
							// return new ResponseEntity<>(obj, HttpStatus.OK);
						} else if (isExist == null) {
							userService.createUser(userVO);
						}
					} catch (Exception e) {
						e.printStackTrace();
						obj.put("data", "Something went wrong.");
						// return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
					}
				}

				myWriter.write(userVO.toString());
				myWriter.write(System.getProperty("line.separator"));
				log.debug("");
				log.debug("One Row COmplete" + j);
				j++;
			}
			myWriter.close();
			log.debug("Journal All Set");
		} catch (Exception e) {
			e.printStackTrace();
		}

		JSONObject obj = new JSONObject();
		if (str != null && str != "") {
			obj.put("data", str);
		} else {
			obj.put("data", "Data Not Found");
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);

	}

	@RequestMapping(value = "/getUserByJournalAndRole", method = RequestMethod.POST)
	public ResponseEntity<?> getUserByJournalAndRole(@RequestBody IDVO id) {
		JSONObject obj = new JSONObject();
		List<UserVO> userList = userService.getUserByJournalAndRole(id);
		if (userList != null) {
			obj.put("data", userList);
			return new ResponseEntity<>(obj, HttpStatus.OK);
		}
		obj.put("data", "Data not found");
		return new ResponseEntity<>(obj, HttpStatus.FORBIDDEN);
	}

	@RequestMapping(value = "/getAllUserByJournalAndRole", method = RequestMethod.POST)
	public ResponseEntity<?> getAllUserByJournalAndRole(@RequestBody IDVO id) {
		JSONObject obj = new JSONObject();
		List<UserVO> userList = userService.getAllUserByJournalAndRole(id);
		if (userList != null) {
			obj.put("data", userList);
			return new ResponseEntity<>(obj, HttpStatus.OK);
		}
		obj.put("data", "Data not found");
		return new ResponseEntity<>(obj, HttpStatus.FORBIDDEN);
	}

	@RequestMapping(value = "/getAuthorListByJournal", method = RequestMethod.POST)
	public ResponseEntity<?> getAuthorListByJournal(@RequestBody IDVO id) {
		JSONObject obj = new JSONObject();
		Pageable paging = PageRequest.of(id.getPageNo(), id.getLimit());
		List<UserVO> userList = userService.getAuthorListByJournal(id, paging);
		int totalCount = 0;
		int searchCount = 0;
		if (id.getIsActive() == null) {
			id.setIsActive(1);
		}

		if (null != id.getSearch() && "" != id.getSearch()) {
			totalCount = userService.getTotalSearchCountAuthorListByJournal(id.getRoleId(), id.getJournalId(),
					id.getIsActive(), id.getSearch());
			if (null != userList)
				searchCount = userList.size();
		} else {
			totalCount = userService.getTotalCountAuthorListByJournal(id.getRoleId(), id.getJournalId(),
					id.getIsActive());
			if (null != userList)
				searchCount = userList.size();
		}

		if (id.getSortBy().equalsIgnoreCase("firstName")) {

			Comparator<UserVO> compareByfName = (UserVO o1,
					UserVO o2) -> ((o1 != null && o1.getFirstName() != null) ? o1.getFirstName() : "")
							.compareTo(((o1 != null && o2.getFirstName() != null) ? o2.getFirstName() : ""));

			if (id.getSort().equalsIgnoreCase("asc"))
				Collections.sort(userList, compareByfName);
			else
				Collections.sort(userList, compareByfName.reversed());

		}

		if (id.getSortBy().equalsIgnoreCase("lastName")) {

			Comparator<UserVO> compareBylName = (UserVO o1,
					UserVO o2) -> ((o1 != null && o1.getLastName() != null) ? o1.getLastName() : "")
							.compareTo(((o1 != null && o2.getLastName() != null) ? o2.getLastName() : ""));

			if (id.getSort().equalsIgnoreCase("asc"))
				Collections.sort(userList, compareBylName);
			else
				Collections.sort(userList, compareBylName.reversed());

		}

		if (id.getSortBy().equalsIgnoreCase("email")) {

			Comparator<UserVO> compareByEmail = (UserVO o1,
					UserVO o2) -> ((o1 != null && o1.getEmail() != null) ? o1.getEmail() : "")
							.compareTo(((o1 != null && o2.getEmail() != null) ? o2.getEmail() : ""));

			if (id.getSort().equalsIgnoreCase("asc"))
				Collections.sort(userList, compareByEmail);
			else
				Collections.sort(userList, compareByEmail.reversed());

		}

		if (id.getSortBy().equalsIgnoreCase("country")) {

			Comparator<UserVO> compareByCountry = (UserVO o1,
					UserVO o2) -> ((o1 != null && o1.getCountry() != null) ? o1.getCountry() : "")
							.compareTo(((o1 != null && o2.getCountry() != null) ? o2.getCountry() : ""));

			if (id.getSort().equalsIgnoreCase("asc"))
				Collections.sort(userList, compareByCountry);
			else
				Collections.sort(userList, compareByCountry.reversed());

		}

		if (id.getSortBy().equalsIgnoreCase("specialization")) {

			Comparator<UserVO> compareBySpec = (UserVO o1,
					UserVO o2) -> ((o1 != null && o1.getSpecialization() != null) ? o1.getSpecialization() : "")
							.compareTo(((o1 != null && o2.getSpecialization() != null) ? o2.getSpecialization() : ""));

			if (id.getSort().equalsIgnoreCase("asc"))
				Collections.sort(userList, compareBySpec);
			else
				Collections.sort(userList, compareBySpec.reversed());

		}

		if (id.getSortBy().equalsIgnoreCase("mobileNo")) {

			Comparator<UserVO> compareByMobile = (UserVO o1,
					UserVO o2) -> ((o1 != null && o1.getMobileNumber() != null) ? o1.getMobileNumber() : "")
							.compareTo(((o1 != null && o2.getMobileNumber() != null) ? o2.getMobileNumber() : ""));

			if (id.getSort().equalsIgnoreCase("asc"))
				Collections.sort(userList, compareByMobile);
			else
				Collections.sort(userList, compareByMobile.reversed());

		}

		if (userList != null) {
			obj.put("data", userList);
			obj.put("totalCount", totalCount);
			obj.put("searchCount", searchCount);
			return new ResponseEntity<>(obj, HttpStatus.OK);
		}
		obj.put("data", "Data not found");
		obj.put("totalCount", totalCount);
		obj.put("searchCount", searchCount);
		return new ResponseEntity<>(obj, HttpStatus.FORBIDDEN);
	}

	@RequestMapping(value = "/exportAllUserByJournalAndRole", method = RequestMethod.POST)
	public ResponseEntity<?> exportAllUserByJournalAndRole(@RequestBody IDVO id) {
		JSONObject obj = new JSONObject();
		List<UserExportVo> userList = userService.exportAllUserByJournalAndRole(id);
		if (userList != null) {
			obj.put("data", userList);
			return new ResponseEntity<>(obj, HttpStatus.OK);
		}
		obj.put("data", "Data not found");
		return new ResponseEntity<>(obj, HttpStatus.FORBIDDEN);
	}

	@RequestMapping(value = "/getAllUserByJournalAndRoleAndPage", method = RequestMethod.POST)
	public ResponseEntity<?> getAllUserByJournalAndRoleAndPage(@RequestBody IDVO id) {
		JSONObject obj = new JSONObject();
		if (id != null && id.getCurrentUserId() != null && id.getPreviousUserId() != null) {
			Pageable paging = PageRequest.of(id.getCurrentUserId(), id.getPreviousUserId());
			// Pageable paging = PageRequest.of(1, 10);
			List<UserVO> userList = userService.getAllUserByJournalAndRoleAndPage(id, paging);
			Integer count = userService.getCount(id);
			if (userList != null) {
				obj.put("data", userList);
				obj.put("totalUsersLength", count);
				return new ResponseEntity<>(obj, HttpStatus.OK);
			}
		} else {
			obj.put("data", "Data not found");
		}
		obj.put("data", "Data not found");
		return new ResponseEntity<>(obj, HttpStatus.FORBIDDEN);
	}

	@RequestMapping(value = "/searchUserByEmail", method = RequestMethod.POST)
	public ResponseEntity<?> searchUserByEmail(@RequestBody IDVO id) {
		JSONObject obj = new JSONObject();
		try {
			List<UserVO> data = userService.searchUserByEmail(id);
			if (data != null && !data.isEmpty()) {
				obj.put("data", data);
			} else {
				obj.put("data", "Data not found");
			}
			return new ResponseEntity<>(obj, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			obj.put("data", "Something went wrong");
			return new ResponseEntity<>(obj, HttpStatus.EXPECTATION_FAILED);
		}
	}

	@RequestMapping(value = "/getInactiveUsers", method = RequestMethod.POST)
	public ResponseEntity<?> getInactiveUsers(@RequestBody IDVO id) {
		JSONObject obj = new JSONObject();
		if (id.getCurrentUserId() != null && id.getPreviousUserId() != null) {
			Pageable paging = PageRequest.of(id.getCurrentUserId(), id.getPreviousUserId());
			List<UserVO> userList = userService.getInactiveUsers(id, paging);
			Integer count = userService.getInactiveUserCount(id);
			if (userList != null) {
				obj.put("data", userList);
				obj.put("totalUsersLength", count);
				return new ResponseEntity<>(obj, HttpStatus.OK);
			}
		} else {
			obj.put("data", "Data not found");
		}
		obj.put("data", "Data not found");
		return new ResponseEntity<>(obj, HttpStatus.FORBIDDEN);
	}

	@RequestMapping(value = "/updateInactiveUser", method = RequestMethod.POST)
	public ResponseEntity<?> updateInactiveUser(@RequestBody UserVO userVO) {
		JSONObject obj = new JSONObject();
		try {
			userService.updateInactiveUser(userVO);
			obj.put("message", "Data Sucessfully Updated");
		} catch (Exception e) {
			log.info("Exception" + e);
			e.printStackTrace();
			obj.put("message", "Something Went Wrong");
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@RequestMapping(value = "/checkUserByJournal", method = RequestMethod.POST)
	public ResponseEntity<?> checkUserByJournal(@RequestBody UserVO userVo) {
		log.info(AppConstants.REQUEST_METHOD + " :: " + AppConstants.POST + AppConstants.REQUEST_URI
				+ " :: /user/checkUserByJournal" + AppConstants.REQUEST_BODY + " ::" + userVo.toString());
		JSONObject obj = new JSONObject();
		try {
			if (userVo.getEmail() != null && !userVo.getEmail().isEmpty() && userVo.getJournal() != null) {
				User userDetails = userService.loginUser(userVo);
				if (userDetails != null) {
					if (userDetails.getIsActive() != null && userDetails.getIsActive().equals(1)) {
						UserJournal getUserJournal = userJournalRepo.getUserJournal(userDetails.getId(),
								userVo.getJournal(), 4);
						if (getUserJournal != null && getUserJournal.getIsActive() == 0) {
							obj.put("data", "User is associated on this journal but it is inactive");
							return new ResponseEntity<>(obj, HttpStatus.OK);
						}
						if (getUserJournal == null) {
							obj.put("data", "User is not associated on this journal");
							return new ResponseEntity<>(obj, HttpStatus.OK);
						}
					}
				}
			}
			obj.put("data", "User not available or already mapped");
			return new ResponseEntity<>(obj, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			obj.put("data", "Something went wrong");
			return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
		}
	}

	@RequestMapping(value = "/mapUserByJournal", method = RequestMethod.POST)
	public ResponseEntity<?> mapUserByJournal(@RequestBody UserVO userVo) {
		log.info(AppConstants.REQUEST_METHOD + " :: " + AppConstants.POST + AppConstants.REQUEST_URI
				+ " :: /user/mapUserByJournal" + AppConstants.REQUEST_BODY + " :: " + userVo.toString());
		JSONObject obj = new JSONObject();
		try {
			if (userVo.getEmail() != null && !userVo.getEmail().isEmpty() && userVo.getJournal() != null) {
				User userDetails = userService.loginUser(userVo);
				if (userDetails != null) {
					if (userDetails.getIsActive() != null && userDetails.getIsActive().equals(1)) {
						UserJournal getUserJournal = userJournalRepo.getUserJournal(userDetails.getId(),
								userVo.getJournal(), 4);
						if (getUserJournal == null) {
							UserJournal userJournals = new UserJournal();
							userJournals.setJournalId(userVo.getJournal());
							userJournals.setUserId(userDetails.getId());
							userJournals.setRoleId(4);
							userJournals.setIsActive(1);
							userJournals.setUpdatedDate(new Date());
							userJournals.setUpdatedBy(userDetails.getUpdatedBy());
							userJournalRepo.save(userJournals);
							obj.put("data", "User is now associated with this journal.");
							return new ResponseEntity<>(obj, HttpStatus.OK);
						} else {
							obj.put("data", "User is already mapped with this journal.");
							return new ResponseEntity<>(obj, HttpStatus.OK);
						}
					} else {
						obj.put("data", "Something went wrong");
						return new ResponseEntity<>(obj, HttpStatus.OK);
					}
				} else {
					obj.put("data", "User not found");
					return new ResponseEntity<>(obj, HttpStatus.OK);
				}
			} else {
				obj.put("data", "Please provide email , password , journal and role");
				return new ResponseEntity<>(obj, HttpStatus.OK);
			}
		} catch (Exception e) {
			e.printStackTrace();
			obj.put("data", "Something went wrong");
			return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
		}
	}

	@RequestMapping(value = "/getAllUsersListByJournalAndRole", method = RequestMethod.POST)
	public ResponseEntity<?> getAllUsersListByJournalAndRole(@RequestBody IDVO id) {
		JSONObject obj = new JSONObject();
		UserDetailsListVO userList = userService.getAllUsersListByJournalAndRole(id);
		if (userList != null) {
			obj.put("data", userList);
			return new ResponseEntity<>(obj, HttpStatus.OK);
		}
		obj.put("data", "Data not found");
		return new ResponseEntity<>(obj, HttpStatus.FORBIDDEN);
	}

	@RequestMapping(value = "/updateEditorAccess", method = RequestMethod.POST)
	public ResponseEntity<?> updateEditorAccess(@RequestBody IDVO userVo) {
		JSONObject obj = new JSONObject();
		try {
			User vo = userService.getByEmail(userVo.getEmail());
			if (userVo.getRoleId() == 3 && vo != null) {
				vo.setEditorAccessDetails(userVo.getEditorAccessDetails());
				vo.setUpdatedDate(new Date());
				userService.updateEditorAccess(vo);
			}

			obj.put("result", "Access details for editor updated successfully");
			return new ResponseEntity<>(obj, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			obj.put("result", "failed");
			return new ResponseEntity<>(obj, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/updateOrcid", method = RequestMethod.POST)
	public ResponseEntity<?> updateOrcid(@RequestBody UserVO userVO) {
		JSONObject obj = new JSONObject();
		log.info(AppConstants.REQUEST_METHOD + " :: " + AppConstants.POST + AppConstants.REQUEST_URI
				+ " :: /user/updateOrcid" + AppConstants.REQUEST_BODY + " :: " + userVO.toString());
		try {
			User isExist = userRepo.findByEmailUser(userVO.getEmail());
			if (isExist != null) {
				try {
					userRepo.deleteOrcid(userVO.getOrcidId());
				} catch (Exception ex) {
					log.info("User not exist:: " + ex.getMessage());
				}
				isExist.setOrcidId(userVO.getOrcidId());
				userRepo.save(isExist);
				obj.put("data", "Updated Successfully");
			} else {
				obj.put("data", "User Not Exist");
			}
		} catch (Exception e) {
			e.printStackTrace();
			obj.put("data", "Something went wrong.");
			return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
		}

		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@RequestMapping(value = "/sendOrcidAuthEmailToUser", method = RequestMethod.POST)
	public ResponseEntity<?> updateOrcid(@RequestBody IDVO idvo) {
		JSONObject obj = new JSONObject();
		try {
			User isExist = userRepo.findByEmailUser(idvo.getEmail());
			if (isExist != null) {
				String data = emailTriggerService.sendOrcidAuthEmail(idvo);
				obj.put("data", "Sent Successfully");
			} else {
				obj.put("data", "User Not Exist");
			}
		} catch (Exception e) {
			e.printStackTrace();
			obj.put("data", "Something went wrong.");
			return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
		}

		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@RequestMapping(value = "/getSpecializationsByJournalId", method = RequestMethod.POST)
	public ResponseEntity<?> getSpecializationsByJournalId(@RequestBody Map<String, Object> request) {
		JSONObject obj = new JSONObject();

		try {

			Long journalId = Long.valueOf(String.valueOf(request.get("journal_id")));

			List<String> specializations = userRepo.findDistinctSpecializationsByJournalId(journalId);
			if (specializations != null && !specializations.isEmpty()) {
				obj.put("specializations", specializations); // Add the list of specializations to the response
				return new ResponseEntity<>(obj, HttpStatus.OK);
			} else {
				obj.put("message", "No specializations found.");
				return new ResponseEntity<>(obj, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			obj.put("message", "Something went wrong.");
			return new ResponseEntity<>(obj, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/getSubSpecializationsByJournalId", method = RequestMethod.POST)
	public ResponseEntity<?> getSubSpecializationsByJournalId(@RequestBody Map<String, Object> request) {
		JSONObject obj = new JSONObject();

		try {

			Long journalId = Long.valueOf(String.valueOf(request.get("journal_id")));

			List<String> subSpecializations = userRepo.findDistinctSubSpecializationsByJournalId(journalId);
			if (subSpecializations != null && !subSpecializations.isEmpty()) {
				obj.put("subSpecializations", subSpecializations); // Add the list of specializations to the response
				return new ResponseEntity<>(obj, HttpStatus.OK);
			} else {
				obj.put("message", "No specializations found.");
				return new ResponseEntity<>(obj, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			obj.put("message", "Something went wrong.");
			return new ResponseEntity<>(obj, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
