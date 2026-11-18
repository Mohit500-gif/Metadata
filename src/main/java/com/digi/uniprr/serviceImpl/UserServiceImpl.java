package com.digi.uniprr.serviceImpl;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import javax.persistence.Tuple;
import javax.transaction.Transactional;

import com.digi.uniprr.repository.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.digi.uniprr.VO.AuthoritiesHistoryVo;
import com.digi.uniprr.VO.EmailTriggerVO;
import com.digi.uniprr.VO.ExpertiseVO;
import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.VO.JournalListVO;
import com.digi.uniprr.VO.LoggedUserVo;
import com.digi.uniprr.VO.ManuscriptListVO;
import com.digi.uniprr.VO.ReviewerFilterVO;
import com.digi.uniprr.VO.ReviewerSuggestedVO;
import com.digi.uniprr.VO.ReviewerVO;
import com.digi.uniprr.VO.UpdatePassword;
import com.digi.uniprr.VO.UserExportVo;
import com.digi.uniprr.VO.UserVO;
import com.digi.uniprr.controller.dao.UserDao;
import com.digi.uniprr.enums.Salutation;
import com.digi.uniprr.model.AuthoritiesHistory;
import com.digi.uniprr.model.Countries;
import com.digi.uniprr.model.EditorRoleAccess;
import com.digi.uniprr.model.Journal;
import com.digi.uniprr.model.MailQueue;
import com.digi.uniprr.model.ManuScript;
import com.digi.uniprr.model.ManuScriptReviewer;
import com.digi.uniprr.model.ManuscriptEditor;
import com.digi.uniprr.model.OtherMail;
import com.digi.uniprr.model.ReviewerAvailability;
import com.digi.uniprr.model.ReviewerSuggested;
import com.digi.uniprr.model.Role;
import com.digi.uniprr.model.User;
import com.digi.uniprr.model.UserJournal;
import com.digi.uniprr.model.UserLogins;
import com.digi.uniprr.model.UserRole;
import com.digi.uniprr.model.UserSpecilization;
import com.digi.uniprr.service.AuthoritiesHistoryService;
import com.digi.uniprr.service.CountriesService;
import com.digi.uniprr.service.EmailTriggerService;
import com.digi.uniprr.service.ManuScriptReviewerService;
import com.digi.uniprr.service.ManuScriptService;
import com.digi.uniprr.service.ManuscriptEditorService;
import com.digi.uniprr.service.UserService;
import com.digi.uniprr.utils.GroupTransformationalUtils;
import com.digi.uniprr.utils.JournalListTransformationUtils;
import com.digi.uniprr.utils.RoleTransformationalUtils;
import com.digi.uniprr.utils.SendMailUtils;
import com.digi.uniprr.utils.UserTransformationalUtils;
import com.digi.uniprr.VO.UserDetailsListVO;
import com.digi.uniprr.VO.UserDetailsVO;
import com.digi.uniprr.enums.Users;
import org.springframework.util.ObjectUtils;

@Service
@Transactional
public class UserServiceImpl implements UserService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	UserLoginRepo userLoginRepo;

	@Autowired
	AuthoritiesHistoryService authoritiesHistoryService;

	@Autowired
	AuthoritiesHistoryRepo authoritiesHistoryRepo;

	@Autowired
	Environment env;

	@Autowired
	OtherMailRepo otherMailRepo;

	@Autowired
	SendMailUtils mailUtils;

	@Autowired
	UserRoleRepo userRoleRepo;

	@Autowired
	UserGroupRepo userGroupRepo;

	@Autowired
	UserRepo userRepo;

	@Autowired
	ManuScriptRepo manuscriptRepo;

	@Autowired
	MailQueueRepo mailQueueRepo;

	@Autowired
	CountriesService countriesService;

	@Autowired
	ManuScriptReviewerService manuscriptReviewerService;

	@Autowired
	ManuscriptEditorService manuscriptEditorService;

	@Autowired
	ManuScriptService manuscriptService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	ManuscriptEditorRepo manuscriptEditorRepo;

	@Autowired
	ManuScriptReviewerRepo manuScriptReviewerRepo;

	@Autowired
	UserJournalRepo userJournalRepo;

	@Autowired
	UserSpecilizationRepo userSpecilizationRepo;

	@Autowired
	UserDao userDao;

	@Autowired
	ReviewerAvailabilityRepo reviewerAvailabilityRepo;

	@Autowired
	UserLoginsRepo userLoginsRepo;

	@Autowired
	JournalsRepo journalsRepo;

	@Autowired
	EditorRoleAccessRepo editorRoleAccessRepo;

	@Autowired
	ReviewerSuggestedRepo reviewerSuggestedRepo;

	@Autowired
	EmailTriggerService emailTriggerService;

	@Autowired
	RoleRepo roleRepo;

	@Autowired
	ManuScriptCoAuthorRepo manuScriptCoAuthorRepo;

	@Value("${tempPassword}")
	String tempPassword;

	@Value("${oauthBaseUrl}")
	String oauthBaseUrl;

	@Value("${oauthClientId}")
	String oauthClientId;

	@Value("${oauthClientSecret}")
	String oauthClientSecret;

	@Autowired
	CountriesRepo countriesRepo;

	@Override
	public void addUser(User user) {
		userLoginRepo.save(user);
	}

	@Override
	public Iterable<User> getUserDetail(User user) {
		Iterable<User> userList = userLoginRepo.findAll();
		return userList;
	}

	@Override
	public void updateUser(UserVO userVO) {
		//Added By Utkarsh
		User userData;
		if(userVO.getId()==null)
		{
			userData=userRepo.findByEmail(userVO.getEmail());
		}
		else
		{
			userData=userRepo.getById(userVO.getId());
		}

		if(userVO.getCountryId()==null)
		{
			userData.setCountryId(249);
		}
		if(userVO.getCountryId()<=0 || userVO.getCountryId()>251)
		{
			userData.setCountryId(249);
		}
		
	    boolean allowEmailUpdate = userVO.getIsCorrespondingAuthor() != null
	            && userVO.getIsCorrespondingAuthor().equals("N");

	    if (allowEmailUpdate && userVO.getEmail() != null && !userVO.getEmail().isEmpty()) {
	        // Only update if email is different
	        if (!userVO.getEmail().equalsIgnoreCase(userData.getEmail())) {
	            // Check if another user already has this email
	            User existingUser = userRepo.findByEmail(userVO.getEmail());
	            if (existingUser != null) {
	                // Email exists → update that user instead
	                userData = existingUser;
	            }
	            userData.setEmail(userVO.getEmail());
	        }
	    }


//		User userData = userRepo.getById(userVO.getId());
		userData.setPubmedId(userVO.getPubmedId());
//		if (userVO.getScopusId() != null && !userVO.getScopusId().isEmpty())
		userData.setScopusId(userVO.getScopusId());

		userData.setAfflication(userVO.getAfflication());
//		if (userVO.getAcademic() != null && !userVO.getAcademic().isEmpty())
		userData.setAcademic(userVO.getAcademic());

		userData.setResearcherId(userVO.getResearcherId());
		if (userVO.getTrialRegistrationNo() != null && !userVO.getTrialRegistrationNo().isEmpty())
			userData.setTrialRegistrationNo(userVO.getTrialRegistrationNo());

		//Added by Utkarsh
		userData.setExpertise((userVO.getExpertise() != null && !userVO.getExpertise().isEmpty()) ? userVO.getExpertise().trim() : "");
		userData.setSpecialization((userVO.getSpecialization() != null && !userVO.getSpecialization().isEmpty()) ? userVO.getSpecialization().trim() : "");
		userData.setFaxNumber(userVO.getFaxNumber());
		userData.setPhoneNumber(userVO.getPhoneNumber());
		if (userVO.getSalutation() != null)
			userData.setSalutation(userVO.getSalutation());
		if (userVO.getFirstName() != null && !userVO.getFirstName().isEmpty())
			userData.setFirstName(userVO.getFirstName());

		userData.setMiddleName(userVO.getMiddleName());
		if (userVO.getLastName() != null && !userVO.getLastName().isEmpty())
			userData.setLastName(userVO.getLastName());
		if (userVO.getUsername() != null && !userVO.getUsername().isEmpty())
			userData.setUsername(userVO.getUsername());

		userData.setInstitute(userVO.getInstitute());
		userData.setMobileNumber(userVO.getMobileNumber());

		if (userVO.getOrcidId() != null && !userVO.getOrcidId().isEmpty())
			userData.setOrcidId(userVO.getOrcidId());

		userData.setAddress(userVO.getAddress());
		userData.setCity(userVO.getCity());
		if(userVO.getMobileNumber() != null && !userVO.getMobileNumber().isEmpty())
			userData.setMobileNumber(userVO.getMobileNumber());

		if (userVO.getCountryId() != null)
			userData.setCountryId(userVO.getCountryId());
		if (userVO.getCountryCode() != null)
			userData.setCountryCode(userVO.getCountryCode());
//		if (userVO.getState() != null && !userVO.getState().isEmpty())
		userData.setState(userVO.getState());

		userData.setPostalCode(userVO.getPostalCode());
		if (userVO.getSearchArticleStatus() != null)
			userData.setSearchArticleStatus(userVO.getSearchArticleStatus());
		if (userVO.getPassword() != null && !userVO.getPassword().isEmpty() && userVO.getPassword().length() > 0)
			userData.setPassword(passwordEncoder.encode(userVO.getPassword()));
		if (userVO.getAccountLocked() != null )
			userData.setAccountLocked(userVO.getAccountLocked());
		if (StringUtils.isNotBlank(userVO.getEditorAccessDetails()))
			userData.setEditorAccessDetails(userVO.getEditorAccessDetails());
		userData.setUpdatedBy(userVO.getUpdatedBy());
		userLoginRepo.save(userData);

		if (userVO.getJournalId() != null && !userVO.getJournalId().isEmpty()) {
			for (Integer journalId : userVO.getJournalId()) {
				userJournalRepo.disableUserJournalRole(userVO.getId(), journalId);
				// editorRoleAccessRepo.disableUserEditorRoleAccess(userVO.getId());
				if (userVO.getRoleId() != null && !userVO.getRoleId().isEmpty()) {
					for (Integer roleId : userVO.getRoleId()) {
						UserJournal i = userJournalRepo.getUserJournalInfo(userVO.getId(), journalId, roleId);
						if (i != null) {
							i.setIsActive(userVO.getIsActive());
							if (roleId == 3 && userVO.getEditorRoleId() != null) {
								EditorRoleAccess editorRoleAccessData = editorRoleAccessRepo
										.getDataByUserAndRoleId(userVO.getId());
								if (editorRoleAccessData != null) {
									editorRoleAccessData.setIsActive(userVO.getIsActive());
									editorRoleAccessData.setEditorRoleId(userVO.getEditorRoleId());
									editorRoleAccessRepo.save(editorRoleAccessData);
								} else {
									EditorRoleAccess editorRoleAccess = new EditorRoleAccess();
									editorRoleAccess.setUserId(userVO.getId());
									editorRoleAccess.setIsActive(userVO.getIsActive());
									editorRoleAccess.setEditorRoleId(userVO.getEditorRoleId());
									editorRoleAccessRepo.save(editorRoleAccess);
								}
							}
							i.setUpdatedBy(userVO.getUpdatedBy());
							i.setUpdatedDate(new Date());
							userJournalRepo.save(i);
						} else {
							UserJournal add = new UserJournal();
							add.setUserId(userVO.getId());
							add.setJournalId(journalId);
							add.setRoleId(roleId);
							if (roleId == 3 && userVO.getEditorRoleId() != null) {
								EditorRoleAccess editorRoleAccessData = editorRoleAccessRepo
										.getDataByUserAndRoleId(userVO.getId());
								if (editorRoleAccessData != null) {
									editorRoleAccessData.setIsActive(userVO.getIsActive());
									editorRoleAccessData.setEditorRoleId(userVO.getEditorRoleId());
									editorRoleAccessRepo.save(editorRoleAccessData);
								} else {
									EditorRoleAccess editorRoleAccess = new EditorRoleAccess();
									editorRoleAccess.setUserId(userVO.getId());
									editorRoleAccess.setEditorRoleId(userVO.getEditorRoleId());
									editorRoleAccess.setIsActive(userVO.getIsActive());
									editorRoleAccessRepo.save(editorRoleAccess);
								}
							}
							add.setIsActive(userVO.getIsActive());
							add.setUpdatedDate(new Date());
							add.setUpdatedBy(userVO.getUpdatedBy());
							userJournalRepo.save(add);
						}
					}
				}
			}
		}

		/*
		 * if (userVO != null && userVO.getId() != null) { Integer activeUserCount = 0;
		 * activeUserCount = userJournalRepo.getAllActiveUser(userVO.getId()); if
		 * (activeUserCount.equals(0)) { User usr = userRepo.getByID(userVO.getId());
		 * usr.setIsActive(0); userRepo.save(usr); } }
		 */
	}

	@Override
	public User getByUsername(String username) {
		return userLoginRepo.findByusername(username);
	}

	@Override
	public List<User> getGroupByUserId(Integer id) {
		List<User> list = userLoginRepo.getGroupByUserId(id);
		return list;
	}

	@Override
	public boolean isUserExist(User user) {
		return getByUsername(user.getUsername()) != null;
	}

	@Override
	public User createUser(UserVO userVo) {
		boolean isEmail = false;
		if(userRepo.findByEmail(userVo.getEmail())==null)
		{
			User user = UserTransformationalUtils.mapVOToModel(userVo);
			if(userVo.getCountryId()==null)
			{
				user.setCountryId(249);
			}
			if(userVo.getCountryId()<=0 || userVo.getCountryId()>251)
			{
				user.setCountryId(249);
			}
			if (StringUtils.isBlank(user.getPassword())) {
				user.setPassword(passwordEncoder.encode(tempPassword.trim()));
			} else {
				user.setPassword(passwordEncoder.encode(user.getPassword().trim()));
			}
			// Activation Link When He is sign as an author
			if (userVo.getSignUpAsAnAuthorWithAcronym() != null	&& userVo.getSignUpAsAnAuthorWithAcronym().equalsIgnoreCase("Yes")) {
				user.setAccountLocked(0);
				// Account deacivated before click on activation link.
			}else {
				user.setAccountLocked(1);
			}
			user.setIsActive(1);

			user.setSearchArticleStatus(userVo.getSearchArticleStatus());
			if (userVo.getUsername() != null && userVo.getUsername() != "") {
				user.setUsername(userVo.getUsername());
			} else {
				user.setUsername(userVo.getEmail());
			}

			User savedUser = userLoginRepo.save(user);
			if (savedUser != null) {
				if (userVo.getJournalId() != null && !userVo.getJournalId().isEmpty() && userVo.getRoleId() != null
						&& !userVo.getRoleId().isEmpty()) {
					for (Integer journalId : userVo.getJournalId()) {
						for (Integer roleId : userVo.getRoleId()) {
							UserJournal i = userJournalRepo.getUserJournal(savedUser.getId(), journalId, roleId);
							if (i != null) {
								i.setIsActive(1);
								if (roleId == 3 && userVo.getEditorRoleId() != null) {
									EditorRoleAccess editorRoleAccessData = editorRoleAccessRepo
											.getDataByUserAndRoleId(savedUser.getId());
									if (editorRoleAccessData != null) {
										editorRoleAccessData.setIsActive(1);
										editorRoleAccessData.setEditorRoleId(userVo.getEditorRoleId());
										editorRoleAccessRepo.save(editorRoleAccessData);
									} else {
										EditorRoleAccess editorRoleAccess = new EditorRoleAccess();
										editorRoleAccess.setUserId(savedUser.getId());
										editorRoleAccess.setEditorRoleId(userVo.getEditorRoleId());
										editorRoleAccess.setIsActive(1);
										editorRoleAccessRepo.save(editorRoleAccess);
									}
								}
								userJournalRepo.save(i);
							} else {
								UserJournal userJournals = new UserJournal();
								userJournals.setJournalId(journalId);
								userJournals.setUserId(savedUser.getId());
								userJournals.setRoleId(roleId);
								if (roleId == 3 && userVo.getEditorRoleId() != null) {
									EditorRoleAccess editorRoleAccessData = editorRoleAccessRepo
											.getDataByUserAndRoleId(savedUser.getId());
									if (editorRoleAccessData != null) {
										editorRoleAccessData.setIsActive(1);
										editorRoleAccessData.setEditorRoleId(userVo.getEditorRoleId());
										editorRoleAccessRepo.save(editorRoleAccessData);
									} else {
										EditorRoleAccess editorRoleAccess = new EditorRoleAccess();
										editorRoleAccess.setUserId(savedUser.getId());
										editorRoleAccess.setEditorRoleId(userVo.getEditorRoleId());
										editorRoleAccess.setIsActive(1);
										editorRoleAccessRepo.save(editorRoleAccess);
									}
								}
								userJournals.setIsActive(1);
								userJournals.setUpdatedDate(new Date());
								userJournals.setUpdatedBy(savedUser.getId());
								userJournalRepo.save(userJournals);
							}
						}
					}
				}

				if (userVo.getExpertiseData() != null && !userVo.getExpertiseData().isEmpty()) {
					for (ExpertiseVO userSpecilizationData : userVo.getExpertiseData()) {
						for (Integer specialization : userSpecilizationData.getSpecialization()) {
							UserSpecilization userSpecilization = new UserSpecilization();
							userSpecilization.setUserId(savedUser.getId());
							userSpecilization.setExpertiseId(userSpecilizationData.getId());
							userSpecilization.setSpecializationId(specialization);
							userSpecilization.setCreatedBy(savedUser.getId());
							userSpecilizationRepo.save(userSpecilization);
						}
					}
				}
			}

			// Activation Link When He is sign as an author
			if (userVo.getSignUpAsAnAuthorWithAcronym() != null
					&& userVo.getSignUpAsAnAuthorWithAcronym().equalsIgnoreCase("Yes")) {
				if (userVo.getJournalId() != null && !userVo.getJournalId().isEmpty()) {
					Journal detailsOfJournal = journalsRepo.getJournalById(userVo.getJournalId().get(0));
					List<String> journalWebsiteUrl = new ArrayList<>();
					String token = UUID.randomUUID().toString();
					//
					List<String> roleName1 = new ArrayList<>();

					List<Role> getRoleList = roleRepo.getRoleListById(userVo.getRoleId());
					if (getRoleList != null && !getRoleList.isEmpty()) {
						getRoleList.stream().forEach(j -> {
							roleName1.add(j.getRoleName());
						});
					}
					//
					if (detailsOfJournal != null) {
						if (detailsOfJournal.getJournalMetadata() != null) {
							journalWebsiteUrl.add(detailsOfJournal.getJournalMetadata().getUrl());
						}
						try {
							sendMailForAuthorActivation(userVo.getEmail(),
									userVo.getSalutation() + "." + " " + userVo.getFirstName() + " " + userVo.getLastName(),
									token, userVo.getActivationUrl(), detailsOfJournal.getJournalTitle(), 0,
									detailsOfJournal.getJournalId(), detailsOfJournal.getJournalCode(), null, roleName1,
									journalWebsiteUrl);
						} catch (Exception ex) {
							log.info("Exception : " + ex);
						}
					}
				}
			} else if (userVo.getSignUpAsAnAuthorWithAcronym() != null
					&& userVo.getSignUpAsAnAuthorWithAcronym().equalsIgnoreCase("NO")) {
				String token = UUID.randomUUID().toString();
				List<String> journalTitle = new ArrayList<>();
				List<Journal> detailsOfJournal = journalsRepo.getJournalListById(userVo.getJournalId());
				List<String> journalWebsiteUrl = new ArrayList<>();
				if (detailsOfJournal != null && !detailsOfJournal.isEmpty()) {
					detailsOfJournal.stream().forEach(i -> {
						journalTitle.add(i.getJournalTitle());
						if (i.getJournalMetadata() != null) {
							journalWebsiteUrl.add(i.getJournalMetadata().getUrl());
						}
					});
				}
				List<String> roleName = new ArrayList<>();

				List<Role> getRoleList = roleRepo.getRoleListById(userVo.getRoleId());
				if (getRoleList != null && !getRoleList.isEmpty()) {
					getRoleList.stream().forEach(j -> {
						roleName.add(j.getRoleName());
					});
				}

				try {
					sendMailForAuthorActivation(userVo.getEmail(),
							userVo.getSalutation() + "." + " " + userVo.getFirstName() + " " + userVo.getLastName(), token,
							userVo.getActivationUrl(), "", 2, userVo.getJournalId().get(0), "", journalTitle, roleName,
							journalWebsiteUrl);

				} catch (Exception ex) {
					log.info("Exception :" + ex);
				}
			} else if (userVo.getSignUpAsAnAuthorWithAcronym() != null
					&& userVo.getSignUpAsAnAuthorWithAcronym().equalsIgnoreCase("EIC")) {
				if (userVo.getJournalId() != null && !userVo.getJournalId().isEmpty()) {
					Journal detailsOfJournal = journalsRepo.getJournalById(userVo.getJournalId().get(0));
					List<String> journalTitle = new ArrayList<>();
					List<Journal> details = journalsRepo.getJournalListById(userVo.getJournalId());
					List<String> journalWebsiteUrl = new ArrayList<>();
					if (details != null && !details.isEmpty()) {
						details.stream().forEach(i -> {
							journalTitle.add(i.getJournalTitle());
							if (i.getJournalMetadata() != null) {
								journalWebsiteUrl.add(i.getJournalMetadata().getUrl());
							}
						});
					}

					List<String> roleName = new ArrayList<>();
					List<Role> getRoleList = roleRepo.getRoleListById(userVo.getRoleId());
					if (getRoleList != null && !getRoleList.isEmpty()) {
						getRoleList.stream().forEach(j -> {
							roleName.add(j.getRoleName());
						});
					}
					String token = UUID.randomUUID().toString();

					if (detailsOfJournal != null) {
						try {
							sendMailForAuthorActivation(userVo.getEmail(),
									userVo.getSalutation() + "." + " " + userVo.getFirstName() + " " + userVo.getLastName(),
									token, userVo.getActivationUrl(), detailsOfJournal.getJournalTitle(), 1,
									userVo.getJournalId().get(0), "", journalTitle, roleName, journalWebsiteUrl);
						} catch (Exception ex) {
							log.info("Exception : " + ex);
						}
					}

				}
			}
			return savedUser;
		}
		//Added by Utkarsh
		else
		{
			User oldUser=userRepo.findByEmail(userVo.getEmail());
			userVo.setId(oldUser.getId());
			updateUser(userVo);
			User user=userRepo.findByEmail(userVo.getEmail());
			return user;
		}

	}

	public String generateCommonLangPassword() {
		String upperCaseLetters = RandomStringUtils.random(2, 65, 90, true, true);
		String lowerCaseLetters = RandomStringUtils.random(2, 97, 122, true, true);
		String numbers = RandomStringUtils.randomNumeric(2);
		String specialChar = RandomStringUtils.random(2, 33, 47, false, false);
		String totalChars = RandomStringUtils.randomAlphanumeric(2);
		String combinedChars = upperCaseLetters.concat(lowerCaseLetters).concat(numbers).concat(specialChar)
				.concat(totalChars);
		List<Character> pwdChars = combinedChars.chars().mapToObj(c -> (char) c).collect(Collectors.toList());
		Collections.shuffle(pwdChars);
		String password = pwdChars.stream().collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
				.toString();
		return password;
	}

	public String sendMailForAuthorActivation(String userEmail, String userName, String token, String activationUrl,
											  String journalTitle, Integer user, Integer journalId, String journalCode, List<String> journalName,
											  List<String> roleName, List<String> journalWebsiteUrl) {
		String password = null;
		if (user.equals(1) || user.equals(2)) {
//			password = generateCommonLangPassword();
			password = tempPassword.trim();
			userRepo.updateUserPasswordWithEmail(passwordEncoder.encode(password), userEmail);
			userRepo.updateTokenByUser(userEmail, token);
			userRepo.updateUserActivate(1, userEmail);
		} else {
			userRepo.updateTokenByUser(userEmail, token);
		}
		String fromMailID = env.getProperty("spring.mail.username");
		String toMailID = userEmail;

		String mailSubject = null;
		String mailBody = null;
		String roleiid = "other_role";
		if(roleName.contains("Reviewer"))
		{
			roleiid="reviewerid";
			activationUrl="https://review.jow.medknow.com/outside/user-verification";
			mailSubject = "Reviewer account username and password creation link";
			mailBody = activateAuthorMailTemplateReviewer(userEmail, userName, token, activationUrl, journalTitle, user,
					password, journalId, journalCode, journalName, roleName, journalWebsiteUrl);
		}
		if(roleiid.equals("other_role"))
		{
			mailSubject = "Activate your account";
			mailBody = activateAuthorMailTemplate(userEmail, userName, token, activationUrl, journalTitle, user,
					password, journalId, journalCode, journalName, roleName, journalWebsiteUrl);
		}



		Journal i = journalsRepo.getJournalById(journalId);
		OtherMail add = new OtherMail();
		add.setJournalId(journalId);
		add.setToMail(toMailID);
		add.setFromMail(fromMailID);
		add.setSubject(mailSubject);
		add.setMailBody(mailBody);
		if (i != null && i.getJournalMetadata() != null) {
			if (i.getJournalMetadata().getJournalEmail() != null
					&& !i.getJournalMetadata().getJournalEmail().isEmpty()) {
				MailQueue addMailQueue = new MailQueue();
				addMailQueue.setMailFrom(fromMailID);
				addMailQueue.setMailTo(toMailID);
				addMailQueue.setJournalId(journalId);
				addMailQueue.setManuscriptId(0);
				addMailQueue.setMailSubject(mailSubject);
				addMailQueue.setMailBody(mailBody);
				addMailQueue.setMail_send_date(new Date());
				addMailQueue.setIsActive(0);
				mailQueueRepo.saveAndFlush(addMailQueue);

				add.setStatus("Insert into mailQueue");
				add.setCreatedAt(new Date());
				otherMailRepo.save(add);

				return "success";
			} else {

				MailQueue addMailQueue = new MailQueue();
				addMailQueue.setMailFrom(fromMailID);
				addMailQueue.setMailTo(toMailID);
				addMailQueue.setJournalId(journalId);
				addMailQueue.setManuscriptId(0);
				addMailQueue.setMailSubject(mailSubject);
				addMailQueue.setMailBody(mailBody);
				addMailQueue.setMail_send_date(new Date());
				addMailQueue.setIsActive(0);
				mailQueueRepo.saveAndFlush(addMailQueue);

				add.setStatus("Insert into mailQueue");
				add.setCreatedAt(new Date());
				otherMailRepo.save(add);

			}
		} else {

			MailQueue addMailQueue = new MailQueue();
			addMailQueue.setMailFrom(fromMailID);
			addMailQueue.setMailTo(toMailID);
			addMailQueue.setJournalId(journalId);
			addMailQueue.setManuscriptId(0);
			addMailQueue.setMailSubject(mailSubject);
			addMailQueue.setMailBody(mailBody);
			addMailQueue.setMail_send_date(new Date());
			addMailQueue.setIsActive(0);
			mailQueueRepo.saveAndFlush(addMailQueue);

			add.setStatus("Insert into mailQueue");
			add.setCreatedAt(new Date());
			otherMailRepo.save(add);

		}
		String message = "Password reset URL send on mail successfully.";
		return message;
	}

	@Override
	public List<UserVO> getUserDetails() {
		List<User> userList = userLoginRepo.findAll();
		List<UserVO> userVoList = UserTransformationalUtils.mapUserListToUserVOList(userList);
		return userVoList;
	}

	@Override
	public List<User> getUserListById(Integer id) {

		return userLoginRepo.getListById(id);
	}

	@Override
	public String deleteUserById(IDVO id) {
		userJournalRepo.disableUserJournalRoleFromUserList(id.getId(), id.getJournalId(), id.getUserId(),
				id.getRoleId(), new Date(), id.getComment());
		return "deleted successfully";
	}

	@Override
	public User getByEmail(String email) {
		return userRepo.findByEmail(email);
	}

	@Override
	public User getById(Integer id) {
		return userLoginRepo.getOne(id);
	}

	@Override
	public UserVO getUserById(Integer id) {
	    try {
	        User user = userLoginRepo.findById(id).orElse(null);

	        if (user == null) {
	            log.error("User not found with id: {}", id);
	            return null;
	        }

	        return UserTransformationalUtils.mapUserModelToUserVO(user);

	    } catch (Exception e) {
	        log.error("Exception occurred while fetching user with id: {}", id, e);
	        return null;
	    }
	}
	// Author: NIC
	// Date: 2023-09-07
	// Description: Improve this method to fetching user details quickly .

	@Override
	public UserVO getUserByEmailAndPassword(String email) {
		log.info("Email parameter in getUserByEmailAndPassword::"+email);
		User rowData = userLoginRepo.findByEmail(email);

		if (rowData == null) {
			return null; // Handle the case where the user is not found
		}

		log.info("User Details object from user repo::"+rowData);
		UserVO userVO = mapUserToUserVO(rowData);
		return userVO;
	}

	private UserVO mapUserToUserVO(User user) {
		UserVO userVO = new UserVO();

		// Map all parameters from User to UserVO
		userVO.setId(user.getId());
		userVO.setOrcidId(user.getOrcidId());
		userVO.setUsername(user.getUsername());
		userVO.setPassword(user.getPassword());
		userVO.setEmail(user.getEmail());
		userVO.setEmailCC(user.getEmailCC());
		userVO.setSecondaryEmail(user.getSecondaryEmail());
		userVO.setSecondaryEmailCC(user.getSecondaryEmailCC());
		userVO.setSalutation(user.getSalutation());
		userVO.setFirstName(user.getFirstName());
		userVO.setMiddleName(user.getMiddleName());
		userVO.setLastName(user.getLastName());
		userVO.setUserType(user.getUserType());
		userVO.setInstitute(user.getInstitute());
		userVO.setState(user.getState());
		userVO.setAddress(user.getAddress());
		userVO.setAddressTwo(user.getAddressTwo());
		userVO.setAddressThree(user.getAddressThree());
		userVO.setDepartment(user.getDepartment());
		userVO.setMobileNumber(user.getMobileNumber());
		userVO.setPhoneNumber(user.getPhoneNumber());
		userVO.setFaxNumber(user.getFaxNumber());
		userVO.setIsActive(user.getIsActive());
		userVO.setCountryId(user.getCountryId());
		userVO.setCity(user.getCity());
		userVO.setPostalCode(user.getPostalCode());
		userVO.setAreaOfInterest(user.getAreaOfInterest());
		userVO.setNotificationFlag(user.getNotificationFlag());
		userVO.setPublicationName(user.getPublicationName());
		userVO.setAccountExpired(user.getAccountExpired());
		userVO.setAccountLocked(user.getAccountLocked());
		userVO.setDesignation(user.getDesignation());
		userVO.setSpecialization(user.getSpecialization());
		userVO.setExperience(user.getExperience());
		userVO.setExpertise(user.getExpertise());
		userVO.setEmailNotification(user.getEmailNotification());
		userVO.setSearchArticleStatus(user.getSearchArticleStatus());
		userVO.setSmsNotification(user.getSmsNotification());
		userVO.setEditorAccessDetails(user.getEditorAccessDetails());
		if (user.getEditorRoleAccess() != null && user.getEditorRoleAccess().getEditorRoleId() != null) {
			userVO.setEditorRoleId(user.getEditorRoleAccess().getEditorRoleId());
		}
		// Handle Role and Group mappings if needed
		if (user.getRole() != null && !user.getRole().isEmpty()) {
			userVO.setRole(RoleTransformationalUtils.mapRoleListToVOList(user.getRole()));
		}
		if (user.getGroup() != null && !user.getGroup().isEmpty()) {
			userVO.setGroup(GroupTransformationalUtils.mapGroupListToGroupVOList(user.getGroup()));
		}

		// Handle the JournalVO mapping separately
		if (user.getUserJournal() != null && !user.getUserJournal().isEmpty()) {
			List<JournalListVO> journals = user.getUserJournal().stream()
					.filter(i -> i.getIsActive() != null && i.getIsActive() == 1)
					.map(i -> mapJournalToJournalListVO(i.getJournal(), i.getRoleId()))
					.collect(Collectors.toList());
			userVO.setJournalVO(journals);
		}

		return userVO;
	}

	private JournalListVO mapJournalToJournalListVO(Journal journal, Integer roleId) {
		JournalListVO vo = new JournalListVO();

		// Map all parameters from Journal to JournalListVO
		vo.setJournalId(journal.getJournalId());
		vo.setJournalTitle(journal.getJournalTitle());
		vo.setJournalCode(journal.getJournalCode());
		vo.setJournalShortTitle(journal.getJournalShortTitle());
		vo.setJournalCountry(journal.getCountryName()); // Assuming this is the field name in Journal
		vo.setRoleId(roleId);
		vo.setIsActive(journal.getIsActive());

		// Handle the metadata mapping
		if (journal.getJournalMetadata() != null) {
			vo.setJournalBlindType(journal.getJournalMetadata().getJournalBlindType());
			vo.setIsChargingReq(journal.getJournalMetadata().getIsChargingRequired());

			if ("Yes".equalsIgnoreCase(journal.getJournalMetadata().getIsChargingRequired())) {
				if (journal.getJournalChargeDetails() != null) {
					vo.setChargeOnSubmission(journal.getJournalChargeDetails().getChargeOnSubmission());
					vo.setChargeOnPublication(journal.getJournalChargeDetails().getChargeOnPublication());
					vo.setCountryChargeOnPublication(journal.getJournalChargeDetails().getCountryChargeOnPublication());
					vo.setCountryChargeOnSubmission(journal.getJournalChargeDetails().getCountryChargeOnSubmission());
				}
			}

			if ("manuscript_type".equalsIgnoreCase(journal.getJournalMetadata().getChargeOn())) {
				vo.setIsChargeOnManuscriptType("Yes");
			} else {
				vo.setIsChargeOnManuscriptType("No");
			}

			vo.setIsSubmissionRequired(journal.getJournalMetadata().getIsSubmissionRequired());
		}

		if (journal.getJournalPolicies() != null) {
			vo.setOrcIdMandatory(journal.getJournalPolicies().getOrcIdMandatory());
		}
		return vo;
	}


	@Override
	public List<UserVO> getUserListByRoleId(IDVO id) {
		List<User> userList = userLoginRepo.getUserByJournalAndRole(id.getRoleId(), id.getJournalId());
		List<UserVO> userVoList = UserTransformationalUtils.mapUserListToUserVOListData(userList);
		return userVoList;
	}

	@Override
	public void assignUser(ManuscriptListVO manuscriptListVO) throws Exception {

		if (manuscriptListVO.getRoleId() == 5) {
			if (manuscriptListVO.getAssignToID() != null && !manuscriptListVO.getAssignToID().isEmpty()) {

				ManuScript data1 = manuscriptRepo.getById(manuscriptListVO.getManuScriptId());
				if(!data1.getAssignedUserRole().equals(manuscriptListVO.getEditorRoleId())){
					ManuscriptEditor manuscriptEditor = manuscriptEditorRepo.getAllAssignedEdiorsOnManuscriptActive(manuscriptListVO.getManuScriptId(), manuscriptListVO.getManuscriptVersion());
					if(manuscriptEditor != null && manuscriptEditor.getManuscriptStatus() != 51 && manuscriptEditor.getManuscriptStatus() != 52 && manuscriptEditor.getManuscriptStatus() != 68) {
						manuscriptEditorRepo.disableEditor(manuscriptListVO.getManuScriptId());
					}
					List<ManuScriptReviewer> reviewerList = manuScriptReviewerRepo.getInpogressReviewerList(manuscriptListVO.getManuScriptId(), manuscriptListVO.getManuscriptVersion());
					for (ManuScriptReviewer manuScriptReviewer : reviewerList) {
						manuScriptReviewer.setEditorRoleId(manuscriptListVO.getEditorRoleId());
						manuScriptReviewer.setEditorId(manuscriptListVO.getUserId());
						manuScriptReviewerRepo.save(manuScriptReviewer);
					}
				}
				for (Integer assignToId : manuscriptListVO.getAssignToID()) {
					User getUserData = userRepo.getUserData(assignToId);
					if (getUserData != null) {
						ManuscriptEditor p = manuscriptEditorRepo.getManuscriptEditorDeat(
								manuscriptListVO.getJournalId(), manuscriptListVO.getManuScriptId(),
								manuscriptListVO.getManuscriptVersion(), manuscriptListVO.getUserId());
						if (p != null) {
							p.setManuscriptStatus(manuscriptListVO.getManuStatusId());
							manuscriptEditorRepo.save(p);
						}
						ManuScriptReviewer manuscriptReviewer = manuScriptReviewerRepo.findReviewerForAssign(
								manuscriptListVO.getManuScriptId(), manuscriptListVO.getManuscriptVersion(),
								manuscriptListVO.getJournalId(), assignToId);
						if (manuscriptReviewer != null && (manuscriptReviewer.getManuscriptStatus().equals(8) || manuscriptReviewer.getManuscriptStatus().equals(66) || manuscriptReviewer.getManuscriptStatus().equals(35))) {
							UserJournal userJournal = userJournalRepo.getUserJournal(assignToId,
									manuscriptListVO.getJournalId(), 5);
							if (userJournal != null) {
								userJournal.setUpdatedBy(manuscriptListVO.getUserId());
								userJournal.setIsActive(1);
								userJournalRepo.save(userJournal);
							} else {
								UserJournal userJournalData = new UserJournal();
								userJournalData.setJournalId(manuscriptListVO.getJournalId());
								userJournalData.setUserId(assignToId);
								userJournalData.setRoleId(5);
								userJournalData.setUpdatedBy(manuscriptListVO.getUserId());
								userJournalData.setIsActive(1);
								userJournalRepo.save(userJournalData);
							}
							manuScriptReviewerRepo.updateReviewerDetails(manuscriptListVO.getManuStatusId(),
									manuscriptReviewer.getId(), manuscriptListVO.getDueDate());
						} else {
							ManuScriptReviewer manuscriptReviewerData = new ManuScriptReviewer();
							manuscriptReviewerData.setManuscriptId(manuscriptListVO.getManuScriptId());
							manuscriptReviewerData.setManuscriptVersion(manuscriptListVO.getManuscriptVersion());
							manuscriptReviewerData.setReviewerId(assignToId);
							manuscriptReviewerData.setJournalId(manuscriptListVO.getJournalId());
							manuscriptReviewerData.setEditorId(manuscriptListVO.getUserId());
							manuscriptReviewerData.setEditorRoleId(manuscriptListVO.getEditorRoleId());
							manuscriptReviewerData.setCommentsForReviewer(manuscriptListVO.getMessage());
							manuscriptReviewerData.setManuscriptStatus(manuscriptListVO.getManuStatusId());
							if (manuscriptListVO.getDueDate() == null || manuscriptListVO.getDueDate().equals("")
									|| manuscriptListVO.getDueDate().equals("NaN-NaN-NaN")) {
								manuscriptReviewerData.setDueDate(new Date().from(
										LocalDate.now().plusDays(10).atStartOfDay(ZoneId.systemDefault()).toInstant()));
							} else {
								manuscriptReviewerData.setDueDate(manuscriptListVO.getDueDate());
							}
							UserJournal userJournal = userJournalRepo.getUserJournal(assignToId,
									manuscriptListVO.getJournalId(), 5);
							if (userJournal != null) {
								userJournal.setUpdatedBy(manuscriptListVO.getUserId());
								userJournal.setIsActive(1);
								userJournalRepo.save(userJournal);
							} else {
								UserJournal userJournalData = new UserJournal();
								userJournalData.setJournalId(manuscriptListVO.getJournalId());
								userJournalData.setUserId(assignToId);
								userJournalData.setRoleId(5);
								userJournalData.setUpdatedBy(manuscriptListVO.getUserId());
								userJournalData.setIsActive(1);
								userJournalRepo.save(userJournalData);
							}
							manuscriptReviewerService.saveManuScriptReviewerDetails(manuscriptReviewerData);
						}
						if (manuscriptListVO.getManuStatusId() == 8 || manuscriptListVO.getManuStatusId() == 66) {
							manuscriptRepo.updateStage(manuscriptListVO.getManuStatusId(),
									manuscriptListVO.getManuScriptId());
							if (manuscriptListVO.getEditorRoleId() != null
									&& manuscriptListVO.getEditorRoleId().equals(2))
								manuscriptRepo.updateAssignedId(manuscriptListVO.getManuScriptId(),
										manuscriptListVO.getUserId(), 2);

						}

						// Email Trigger
						EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
						emailTriggerVO.setJournalId(manuscriptListVO.getJournalId());
						emailTriggerVO.setUserId(assignToId);
						emailTriggerVO.setReviewerUserId(assignToId);

						if (manuscriptListVO.getEditorRoleId().equals(2))
							emailTriggerVO.setManagingEditorId(manuscriptListVO.getUserId());
						else
							emailTriggerVO.setEditorUserId(manuscriptListVO.getUserId());

						emailTriggerVO.setManuscriptId(manuscriptListVO.getManuScriptId());
						emailTriggerVO.setManuscriptStatus(manuscriptListVO.getManuStatusId());
						emailTriggerVO.setReceiverRoleId(manuscriptListVO.getRoleId());
						if (manuscriptListVO.getTemplateId() != null) {
							emailTriggerVO.setTaskId(manuscriptListVO.getTemplateId());
						} else {
							emailTriggerVO.setTaskId(4);
						}
						emailTriggerVO.setRoleId(manuscriptListVO.getEditorRoleId());

						// For The Sending Url To Reviewer
						if (manuscriptListVO.getJournalMailTemplatesBody() != null && !manuscriptListVO.getJournalMailTemplatesBody().isEmpty())
							emailTriggerVO.setEmailTemplateBody(manuscriptListVO.getJournalMailTemplatesBody());
						if (manuscriptListVO.getJournalMailTemplatesSubject()!= null && !manuscriptListVO.getJournalMailTemplatesSubject().isEmpty())
							emailTriggerVO.setEmailTemplateSubject(manuscriptListVO.getJournalMailTemplatesSubject());

						emailTriggerVO.setUrlManuscript(manuscriptListVO.getManuScriptId());
						emailTriggerVO.setUrlManuscriptVersion(manuscriptListVO.getManuscriptVersion());
						emailTriggerVO.setUrlJournalId(manuscriptListVO.getJournalId());
						emailTriggerVO.setUrlReviewerId(assignToId);
						emailTriggerVO.setUrlManuscriptStatus(manuscriptListVO.getManuStatusId());
						emailTriggerVO.setAcceptByReviewerUrl(manuscriptListVO.getAcceptByReviewerUrl());
						emailTriggerVO.setRejectByReviewerUrl(manuscriptListVO.getRejectByReviewerUrl());
						emailTriggerVO.setViewByReviewerUrl(manuscriptListVO.getViewByReviewerUrl());
						emailTriggerVO.setExtendDate(manuscriptListVO.getDueDate());

						log.info("Inside in assignUser Role For Reviewer For Email Trigger Data are:->"
								+ emailTriggerVO.toString());
						log.info("User Receiver Id:->" + assignToId);
						log.info("User Receiver Role Id:->" + manuscriptListVO.getRoleId());
						log.info("User Sender Role Id:->" + manuscriptListVO.getEditorRoleId());
						log.info("User Sender Id:->" + manuscriptListVO.getUserId());
						log.info("Task Id:->" + 4);
						String message = emailTriggerService.emailTrigger(emailTriggerVO);
						log.info("Message of assignUser Role For Reviewer For Email Trigger " + message);
						log.info("End of assignUser Role For Reviewer For Email Trigger ");


					} else {
						ReviewerSuggested getReviewerSuggestedData = reviewerSuggestedRepo.getOne(assignToId);
						// Create User
						User getUserDatas = userRepo.findByEmail(getReviewerSuggestedData.getEmail());
						if (getUserDatas != null) {
							ManuscriptEditor p = manuscriptEditorRepo.getManuscriptEditorDeat(
									manuscriptListVO.getJournalId(), manuscriptListVO.getManuScriptId(),
									manuscriptListVO.getManuscriptVersion(), manuscriptListVO.getUserId());
							if (p != null) {
								p.setManuscriptStatus(manuscriptListVO.getManuStatusId());
								manuscriptEditorRepo.save(p);
							}
							ManuScriptReviewer manuscriptReviewer = manuScriptReviewerRepo
									.findManuscriptReviewerDetails(manuscriptListVO.getManuScriptId(),
											manuscriptListVO.getManuscriptVersion(), manuscriptListVO.getJournalId(),
											getUserDatas.getId());

							if (manuscriptReviewer != null) {
								UserJournal userJournal = userJournalRepo.getUserJournal(getUserDatas.getId(),
										manuscriptListVO.getJournalId(), 5);
								if (userJournal != null) {
									userJournal.setUpdatedBy(manuscriptListVO.getUserId());
									userJournal.setIsActive(1);
									userJournalRepo.save(userJournal);
								} else {
									UserJournal userJournalData = new UserJournal();
									userJournalData.setJournalId(manuscriptListVO.getJournalId());
									userJournalData.setUserId(getUserDatas.getId());
									userJournalData.setRoleId(5);
									userJournalData.setUpdatedBy(manuscriptListVO.getUserId());
									userJournalData.setIsActive(1);
									userJournalRepo.save(userJournalData);
								}
								manuScriptReviewerRepo.updateReviewerDetails(manuscriptListVO.getManuStatusId(),
										manuscriptReviewer.getId(), manuscriptListVO.getDueDate());
							} else {
								ManuScriptReviewer manuscriptReviewerData = new ManuScriptReviewer();
								manuscriptReviewerData.setManuscriptId(manuscriptListVO.getManuScriptId());
								manuscriptReviewerData.setManuscriptVersion(manuscriptListVO.getManuscriptVersion());
								manuscriptReviewerData.setReviewerId(getUserDatas.getId());
								manuscriptReviewerData.setJournalId(manuscriptListVO.getJournalId());
								manuscriptReviewerData.setEditorId(manuscriptListVO.getUserId());
								manuscriptReviewerData.setEditorRoleId(manuscriptListVO.getEditorRoleId());
								manuscriptReviewerData.setCommentsForReviewer(manuscriptListVO.getMessage());
								manuscriptReviewerData.setManuscriptStatus(manuscriptListVO.getManuStatusId());
								if (manuscriptListVO.getDueDate() == null || manuscriptListVO.getDueDate().equals("")
										|| manuscriptListVO.getDueDate().equals("NaN-NaN-NaN")) {
									manuscriptReviewerData.setDueDate(new Date().from(LocalDate.now().plusDays(10)
											.atStartOfDay(ZoneId.systemDefault()).toInstant()));
								} else {
									manuscriptReviewerData.setDueDate(manuscriptListVO.getDueDate());
								}
								UserJournal userJournal = userJournalRepo.getUserJournal(getUserDatas.getId(),
										manuscriptListVO.getJournalId(), 5);
								if (userJournal != null) {
									userJournal.setUpdatedBy(manuscriptListVO.getUserId());
									userJournal.setIsActive(1);
									userJournalRepo.save(userJournal);
								} else {
									UserJournal userJournalData = new UserJournal();
									userJournalData.setJournalId(manuscriptListVO.getJournalId());
									userJournalData.setUserId(getUserDatas.getId());
									userJournalData.setRoleId(5);
									userJournalData.setUpdatedBy(manuscriptListVO.getUserId());
									userJournalData.setIsActive(1);
									userJournalRepo.save(userJournalData);
								}
								manuscriptReviewerService.saveManuScriptReviewerDetails(manuscriptReviewerData);
							}
							if (manuscriptListVO.getManuStatusId() == 8 || manuscriptListVO.getManuStatusId() == 66) {
								manuscriptRepo.updateStage(manuscriptListVO.getManuStatusId(),
										manuscriptListVO.getManuScriptId());
								if (manuscriptListVO.getEditorRoleId() != null
										&& manuscriptListVO.getEditorRoleId().equals(2))
									manuscriptRepo.updateAssignedId(manuscriptListVO.getManuScriptId(),
											manuscriptListVO.getUserId(), 2);
							}

							// Email Trigger
							EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
							emailTriggerVO.setJournalId(manuscriptListVO.getJournalId());
							emailTriggerVO.setUserId(getUserDatas.getId());
							emailTriggerVO.setReviewerUserId(getUserDatas.getId());

							if (manuscriptListVO.getEditorRoleId().equals(2))
								emailTriggerVO.setManagingEditorId(manuscriptListVO.getUserId());
							else
								emailTriggerVO.setEditorUserId(manuscriptListVO.getUserId());

							emailTriggerVO.setManuscriptId(manuscriptListVO.getManuScriptId());
							emailTriggerVO.setManuscriptStatus(manuscriptListVO.getManuStatusId());
							emailTriggerVO.setReceiverRoleId(manuscriptListVO.getRoleId());
							if (manuscriptListVO.getTemplateId() != null) {
								emailTriggerVO.setTaskId(manuscriptListVO.getTemplateId());
							} else {
								emailTriggerVO.setTaskId(4);
							}
							emailTriggerVO.setRoleId(manuscriptListVO.getEditorRoleId());

							// For The Sending Url To Reviewer
							if (manuscriptListVO.getJournalMailTemplatesBody() != null && !manuscriptListVO.getJournalMailTemplatesBody().isEmpty())
								emailTriggerVO.setEmailTemplateBody(manuscriptListVO.getJournalMailTemplatesBody());
							if (manuscriptListVO.getJournalMailTemplatesSubject()!= null && !manuscriptListVO.getJournalMailTemplatesSubject().isEmpty())
								emailTriggerVO.setEmailTemplateSubject(manuscriptListVO.getJournalMailTemplatesSubject());
							emailTriggerVO.setUrlManuscript(manuscriptListVO.getManuScriptId());
							emailTriggerVO.setUrlManuscriptVersion(manuscriptListVO.getManuscriptVersion());
							emailTriggerVO.setUrlJournalId(manuscriptListVO.getJournalId());
							emailTriggerVO.setUrlReviewerId(getUserDatas.getId());
							emailTriggerVO.setUrlManuscriptStatus(manuscriptListVO.getManuStatusId());
							emailTriggerVO.setAcceptByReviewerUrl(manuscriptListVO.getAcceptByReviewerUrl());
							emailTriggerVO.setRejectByReviewerUrl(manuscriptListVO.getRejectByReviewerUrl());
							emailTriggerVO.setViewByReviewerUrl(manuscriptListVO.getViewByReviewerUrl());

							log.info("Inside in assignUser Role For Reviewer For Email Trigger Data are:->"
									+ emailTriggerVO.toString());
							log.info("User Receiver Id:->" + getUserDatas.getId());
							log.info("User Receiver Role Id:->" + manuscriptListVO.getRoleId());
							log.info("User Sender Role Id:->" + manuscriptListVO.getEditorRoleId());
							log.info("User Sender Id:->" + manuscriptListVO.getUserId());
							log.info("Task Id:->" + 4);
							String message = emailTriggerService.emailTrigger(emailTriggerVO);
							log.info("Message of assignUser Role For Reviewer For Email Trigger " + message);
							log.info("End of assignUser Role For Reviewer For Email Trigger ");
						} else {

							// Create New Reviewer
							User createUser = new User();
							createUser.setAccountLocked(1);
							createUser.setIsActive(1);
							createUser.setSalutation(
									Enum.valueOf(Salutation.class, getReviewerSuggestedData.getSalutation()));
							createUser.setFirstName(getReviewerSuggestedData.getFirstName());
							createUser.setLastName(getReviewerSuggestedData.getLastName());
							createUser.setUsername(getReviewerSuggestedData.getEmail());
							createUser.setEmail(getReviewerSuggestedData.getEmail());
							createUser.setSpecialization(getReviewerSuggestedData.getSpecialization());
							createUser.setExpertise(getReviewerSuggestedData.getSubject());
							createUser.setCountryId(getReviewerSuggestedData.getCountry());
							String token = UUID.randomUUID().toString();
							createUser.setPassword(passwordEncoder.encode(tempPassword.trim()));
							createUser.setToken(token);
							User savedUser = userLoginRepo.save(createUser);

							// Send Reviewer Invitation For Creating Account
							ManuScript getManuscriptData = manuscriptRepo.getOne(manuscriptListVO.getManuScriptId());
							if (getManuscriptData != null) {
								List<String> journalWebsiteUrl = new ArrayList<>();
								if (getManuscriptData.getJournal().getJournalMetadata() != null) {
									journalWebsiteUrl.add(getManuscriptData.getJournal().getJournalMetadata().getUrl());
								}

								sendMailForActivationForReviewer(getReviewerSuggestedData.getEmail(), token,
										manuscriptListVO.getSendEmailUrl(), getReviewerSuggestedData.getSalutation(),
										getReviewerSuggestedData.getFirstName(), getReviewerSuggestedData.getLastName(),
										getManuscriptData.getManuscriptTitle(),
										getManuscriptData.getJournal().getJournalTitle(),
										getManuscriptData.getJournal().getJournalId(),
										getManuscriptData.getJournal().getJournalCode(), journalWebsiteUrl);
							}

							if (savedUser != null) {
								ManuscriptEditor p = manuscriptEditorRepo.getManuscriptEditorDeat(
										manuscriptListVO.getJournalId(), manuscriptListVO.getManuScriptId(),
										manuscriptListVO.getManuscriptVersion(), manuscriptListVO.getUserId());
								if (p != null) {
									p.setManuscriptStatus(manuscriptListVO.getManuStatusId());
									manuscriptEditorRepo.save(p);
								}

								// Now Assign
								ManuScriptReviewer manuscriptReviewer = manuScriptReviewerRepo
										.findManuscriptReviewerDetails(manuscriptListVO.getManuScriptId(),
												manuscriptListVO.getManuscriptVersion(),
												manuscriptListVO.getJournalId(), savedUser.getId());

								if (manuscriptReviewer != null) {
									UserJournal userJournal = userJournalRepo.getUserJournal(savedUser.getId(),
											manuscriptListVO.getJournalId(), 5);
									if (userJournal != null) {
										userJournal.setUpdatedBy(manuscriptListVO.getUserId());
										userJournal.setIsActive(1);
										userJournalRepo.save(userJournal);
									} else {
										UserJournal userJournalData = new UserJournal();
										userJournalData.setJournalId(manuscriptListVO.getJournalId());
										userJournalData.setUserId(savedUser.getId());
										userJournalData.setRoleId(5);
										userJournalData.setUpdatedBy(manuscriptListVO.getUserId());
										userJournalData.setIsActive(1);
										userJournalRepo.save(userJournalData);
									}
									manuScriptReviewerRepo.updateReviewerDetails(manuscriptListVO.getManuStatusId(),
											manuscriptReviewer.getId(), manuscriptListVO.getDueDate());
								} else {
									ManuScriptReviewer manuscriptReviewerData = new ManuScriptReviewer();
									manuscriptReviewerData.setManuscriptId(manuscriptListVO.getManuScriptId());
									manuscriptReviewerData
											.setManuscriptVersion(manuscriptListVO.getManuscriptVersion());
									manuscriptReviewerData.setReviewerId(savedUser.getId());
									manuscriptReviewerData.setJournalId(manuscriptListVO.getJournalId());
									manuscriptReviewerData.setEditorId(manuscriptListVO.getUserId());
									manuscriptReviewerData.setEditorRoleId(manuscriptListVO.getEditorRoleId());
									manuscriptReviewerData.setCommentsForReviewer(manuscriptListVO.getMessage());
									manuscriptReviewerData.setManuscriptStatus(manuscriptListVO.getManuStatusId());
									if (manuscriptListVO.getDueDate() == null
											|| manuscriptListVO.getDueDate().equals("")
											|| manuscriptListVO.getDueDate().equals("NaN-NaN-NaN"))
										manuscriptReviewerData.setDueDate(new Date().from(LocalDate.now().plusDays(10)
												.atStartOfDay(ZoneId.systemDefault()).toInstant()));
									else
										manuscriptReviewerData.setDueDate(manuscriptListVO.getDueDate());
									UserJournal userJournal = userJournalRepo.getUserJournal(savedUser.getId(),
											manuscriptListVO.getJournalId(), 5);
									if (userJournal != null) {
										userJournal.setUpdatedBy(manuscriptListVO.getUserId());
										userJournal.setIsActive(1);
										userJournalRepo.save(userJournal);
									} else {
										UserJournal userJournalData = new UserJournal();
										userJournalData.setJournalId(manuscriptListVO.getJournalId());
										userJournalData.setUserId(savedUser.getId());
										userJournalData.setRoleId(5);
										userJournalData.setUpdatedBy(manuscriptListVO.getUserId());
										userJournalData.setIsActive(1);
										userJournalRepo.save(userJournalData);
									}
									manuscriptReviewerService.saveManuScriptReviewerDetails(manuscriptReviewerData);
								}
								if (manuscriptListVO.getManuStatusId() == 8
										|| manuscriptListVO.getManuStatusId() == 66) {
									ManuScript getmanus = manuscriptRepo.getOne(manuscriptListVO.getManuScriptId());
									if (getmanus != null) {
										getmanus.setManuscriptStatusId(manuscriptListVO.getManuStatusId());
										manuscriptRepo.save(getmanus);
									}

								}

								// Email Trigger
								EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
								emailTriggerVO.setJournalId(manuscriptListVO.getJournalId());
								emailTriggerVO.setUserId(savedUser.getId());
								emailTriggerVO.setReviewerUserId(savedUser.getId());

								if (manuscriptListVO.getEditorRoleId().equals(2))
									emailTriggerVO.setManagingEditorId(manuscriptListVO.getUserId());
								else
									emailTriggerVO.setEditorUserId(manuscriptListVO.getUserId());

								emailTriggerVO.setManuscriptId(manuscriptListVO.getManuScriptId());
								emailTriggerVO.setManuscriptStatus(manuscriptListVO.getManuStatusId());
								emailTriggerVO.setReceiverRoleId(manuscriptListVO.getRoleId());
								if (manuscriptListVO.getTemplateId() != null) {
									emailTriggerVO.setTaskId(manuscriptListVO.getTemplateId());
								} else {
									emailTriggerVO.setTaskId(4);
								}
								emailTriggerVO.setRoleId(manuscriptListVO.getEditorRoleId());

								// For The Sending Url To Reviewer
								if (manuscriptListVO.getJournalMailTemplatesBody() != null && !manuscriptListVO.getJournalMailTemplatesBody().isEmpty())
									emailTriggerVO.setEmailTemplateBody(manuscriptListVO.getJournalMailTemplatesBody());
								if (manuscriptListVO.getJournalMailTemplatesSubject()!= null && !manuscriptListVO.getJournalMailTemplatesSubject().isEmpty())
									emailTriggerVO.setEmailTemplateSubject(manuscriptListVO.getJournalMailTemplatesSubject());
								emailTriggerVO.setUrlManuscript(manuscriptListVO.getManuScriptId());
								emailTriggerVO.setUrlManuscriptVersion(manuscriptListVO.getManuscriptVersion());
								emailTriggerVO.setUrlJournalId(manuscriptListVO.getJournalId());
								emailTriggerVO.setUrlReviewerId(savedUser.getId());
								emailTriggerVO.setUrlManuscriptStatus(manuscriptListVO.getManuStatusId());
								emailTriggerVO.setAcceptByReviewerUrl(manuscriptListVO.getAcceptByReviewerUrl());
								emailTriggerVO.setRejectByReviewerUrl(manuscriptListVO.getRejectByReviewerUrl());
								emailTriggerVO.setViewByReviewerUrl(manuscriptListVO.getViewByReviewerUrl());

								log.info("Inside in assignUser Role For Reviewer For Email Trigger Data are:->"
										+ emailTriggerVO.toString());
								log.info("User Receiver Id:->" + savedUser.getId());
								log.info("User Receiver Role Id:->" + manuscriptListVO.getRoleId());
								log.info("User Sender Role Id:->" + manuscriptListVO.getEditorRoleId());
								log.info("User Sender Id:->" + manuscriptListVO.getUserId());
								log.info("Task Id:->" + 4);
								String message = emailTriggerService.emailTrigger(emailTriggerVO);
								log.info("Message of assignUser Role For Reviewer For Email Trigger " + message);
								log.info("End of assignUser Role For Reviewer For Email Trigger ");

							}
						}

					}
				}
			} else if (manuscriptListVO.getReviewerSuggestedVO() != null
					&& !manuscriptListVO.getReviewerSuggestedVO().isEmpty()) {
				String authorEmail  = manuscriptRepo.getAuthorEmail(manuscriptListVO.getManuScriptId());
				List<String> coAuthorEmailList = manuScriptCoAuthorRepo.getCoAuthorEmail(manuscriptListVO.getManuScriptId());
				coAuthorEmailList.add(authorEmail);
				int j = 0;
				for (ReviewerSuggestedVO i : manuscriptListVO.getReviewerSuggestedVO()) {
					if (i.getEmail() != null && !i.getEmail().isEmpty() && !coAuthorEmailList.contains(i.getEmail())) {
						User getUserDatas = userRepo.findByEmail(i.getEmail());
						if (getUserDatas != null) {
							ManuscriptEditor p = manuscriptEditorRepo.getManuscriptEditorDeat(
									manuscriptListVO.getJournalId(), manuscriptListVO.getManuScriptId(),
									manuscriptListVO.getManuscriptVersion(), manuscriptListVO.getUserId());
							if (p != null) {
								p.setManuscriptStatus(manuscriptListVO.getManuStatusId());
								manuscriptEditorRepo.save(p);
							}
							ManuScriptReviewer manuscriptReviewer = manuScriptReviewerRepo
									.findManuscriptReviewerDetails(manuscriptListVO.getManuScriptId(),
											manuscriptListVO.getManuscriptVersion(), manuscriptListVO.getJournalId(),
											getUserDatas.getId());

							if (manuscriptReviewer != null) {
								UserJournal userJournal = userJournalRepo.getUserJournal(getUserDatas.getId(),
										manuscriptListVO.getJournalId(), 5);
								if (userJournal != null) {
									userJournal.setUpdatedBy(manuscriptListVO.getUserId());
									userJournal.setIsActive(1);
									userJournalRepo.save(userJournal);
								} else {
									UserJournal userJournalData = new UserJournal();
									userJournalData.setJournalId(manuscriptListVO.getJournalId());
									userJournalData.setUserId(getUserDatas.getId());
									userJournalData.setRoleId(5);
									userJournalData.setUpdatedBy(manuscriptListVO.getUserId());
									userJournalData.setIsActive(1);
									userJournalRepo.save(userJournalData);


								}
								manuScriptReviewerRepo.updateReviewerDetails(manuscriptListVO.getManuStatusId(),
										manuscriptReviewer.getId(), manuscriptListVO.getDueDate());
							} else {
								ManuScriptReviewer manuscriptReviewerData = new ManuScriptReviewer();
								manuscriptReviewerData.setManuscriptId(manuscriptListVO.getManuScriptId());
								manuscriptReviewerData.setManuscriptVersion(manuscriptListVO.getManuscriptVersion());
								manuscriptReviewerData.setReviewerId(getUserDatas.getId());
								manuscriptReviewerData.setJournalId(manuscriptListVO.getJournalId());
								manuscriptReviewerData.setEditorId(manuscriptListVO.getUserId());
								manuscriptReviewerData.setEditorRoleId(manuscriptListVO.getEditorRoleId());
								manuscriptReviewerData.setCommentsForReviewer(manuscriptListVO.getMessage());
								manuscriptReviewerData.setManuscriptStatus(manuscriptListVO.getManuStatusId());
								if (manuscriptListVO.getDueDate() == null || manuscriptListVO.getDueDate().equals("")
										|| manuscriptListVO.getDueDate().equals("NaN-NaN-NaN")) {
									manuscriptReviewerData.setDueDate(new Date().from(LocalDate.now().plusDays(10)
											.atStartOfDay(ZoneId.systemDefault()).toInstant()));
								}
								else {
									manuscriptReviewerData.setDueDate(manuscriptListVO.getDueDate());
								}
								if(manuscriptReviewerData.getDueDate()==null)
								{
									manuscriptReviewerData.setDueDate(new Date().from(LocalDate.now().plusDays(10)
											.atStartOfDay(ZoneId.systemDefault()).toInstant()));
								}
								UserJournal userJournal = userJournalRepo.getUserJournal(getUserDatas.getId(),
										manuscriptListVO.getJournalId(), 5);
								if (userJournal != null) {
									userJournal.setUpdatedBy(manuscriptListVO.getUserId());
									userJournal.setIsActive(1);
									userJournalRepo.save(userJournal);
								} else {
									UserJournal userJournalData = new UserJournal();
									userJournalData.setJournalId(manuscriptListVO.getJournalId());
									userJournalData.setUserId(getUserDatas.getId());
									userJournalData.setRoleId(5);
									userJournalData.setUpdatedBy(manuscriptListVO.getUserId());
									userJournalData.setIsActive(1);
									userJournalRepo.save(userJournalData);
								}
								manuscriptReviewerData.setIsActive(1);
								manuScriptReviewerRepo.save(manuscriptReviewerData);
								ManuScriptReviewer manuScriptReviewer=manuScriptReviewerRepo
										.findManuscriptReviewerDetails(manuscriptListVO.getManuScriptId(),
												manuscriptListVO.getManuscriptVersion(),manuscriptListVO.getJournalId(),
												getUserDatas.getId());

							}
							if (manuscriptListVO.getManuStatusId() == 8 || manuscriptListVO.getManuStatusId() == 66) {
//								manuscriptRepo.updateStage(manuscriptListVO.getManuStatusId(),
//										manuscriptListVO.getManuScriptId());
								// Added code By Utkarsh
								ManuScript manuscript= manuscriptRepo.getById(manuscriptListVO.getManuScriptId());
								if(manuscriptListVO.getManuStatusId()==8)
								{
									manuscript.setManuscriptStatusId(8);
								} else{
									manuscript.setManuscriptStatusId(66);
								}
								manuscriptRepo.save(manuscript);
								if (manuscriptListVO.getEditorRoleId() != null
										&& manuscriptListVO.getEditorRoleId().equals(2))
									manuscriptRepo.updateAssignedId(manuscriptListVO.getManuScriptId(),
											manuscriptListVO.getUserId(), 2);
							}


							//earlier code
//							ReviewerSuggested authorSuggestedReviewer = reviewerSuggestedRepo.findByEmailId(i.getEmail());

							//changed code by Utkarsh
							List<ReviewerSuggested> authorSuggestedReviewer = reviewerSuggestedRepo.findByEmailIdList(i.getEmail());
							boolean status =false;
							if(!authorSuggestedReviewer.isEmpty()) {
								//Authorities History
								status=true;
								AuthoritiesHistoryVo add = new AuthoritiesHistoryVo();
								add.setManuscriptId(manuscriptListVO.getManuScriptId());
								add.setManuscriptVersion(manuscriptListVO.getManuscriptVersion());
								add.setManuscriptStatusId(manuscriptListVO.getManuStatusId());
								add.setJournalId(manuscriptListVO.getJournalId());
								List<Integer> id = new ArrayList<>();
								id.add(getUserDatas.getId());
								add.setCurrentUserId(id);
								add.setCurrentUserRole(5);
								add.setPreviousUserId(manuscriptListVO.getUserId());
								add.setPreviousUserRole(manuscriptListVO.getEditorRoleId());
								add.setDueDate(manuscriptListVO.getDueDate());
								String result = authoritiesHistoryService.authoritiesHistory(add);
								log.info("Authorities:->" + result);
							}

							// Added code By Utkarsh
							ManuScriptReviewer manuScriptReviewer=manuScriptReviewerRepo
									.findManuscriptReviewerDetails(manuscriptListVO.getManuScriptId(),
											manuscriptListVO.getManuscriptVersion(),manuscriptListVO.getJournalId(),
											getUserDatas.getId());
							if(manuScriptReviewer!=null && !status)
							{
								//Authorities History
								AuthoritiesHistoryVo add = new AuthoritiesHistoryVo();
								add.setManuscriptId(manuscriptListVO.getManuScriptId());
								add.setManuscriptVersion(manuscriptListVO.getManuscriptVersion());
								add.setManuscriptStatusId(manuscriptListVO.getManuStatusId());
								add.setJournalId(manuscriptListVO.getJournalId());
								List<Integer> id = new ArrayList<>();
								id.add(getUserDatas.getId());
								add.setCurrentUserId(id);
								add.setCurrentUserRole(5);
								add.setPreviousUserId(manuscriptListVO.getUserId());
								add.setPreviousUserRole(manuscriptListVO.getEditorRoleId());
								add.setDueDate(manuscriptListVO.getDueDate());
								String result = authoritiesHistoryService.authoritiesHistory(add);
								log.info("Authorities:->" + result);
							}

							// Email Trigger
							EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
							emailTriggerVO.setJournalId(manuscriptListVO.getJournalId());
							emailTriggerVO.setUserId(getUserDatas.getId());
							emailTriggerVO.setReviewerUserId(getUserDatas.getId());
							if (manuscriptListVO.getEditorRoleId().equals(2))
								emailTriggerVO.setManagingEditorId(manuscriptListVO.getUserId());
							else
								emailTriggerVO.setEditorUserId(manuscriptListVO.getUserId());

							emailTriggerVO.setManuscriptId(manuscriptListVO.getManuScriptId());
							emailTriggerVO.setManuscriptStatus(manuscriptListVO.getManuStatusId());
							emailTriggerVO.setReceiverRoleId(manuscriptListVO.getRoleId());
							if (manuscriptListVO.getTemplateId() != null) {
								emailTriggerVO.setTaskId(manuscriptListVO.getTemplateId());
							} else {
								emailTriggerVO.setTaskId(4);
							}
							emailTriggerVO.setRoleId(manuscriptListVO.getEditorRoleId());

							// For The Sending Url To Reviewer
							emailTriggerVO.setUrlManuscript(manuscriptListVO.getManuScriptId());
							emailTriggerVO.setUrlManuscriptVersion(manuscriptListVO.getManuscriptVersion());
							emailTriggerVO.setUrlJournalId(manuscriptListVO.getJournalId());
							emailTriggerVO.setUrlReviewerId(getUserDatas.getId());
							emailTriggerVO.setUrlManuscriptStatus(manuscriptListVO.getManuStatusId());
							emailTriggerVO.setAcceptByReviewerUrl(manuscriptListVO.getAcceptByReviewerUrl());
							emailTriggerVO.setRejectByReviewerUrl(manuscriptListVO.getRejectByReviewerUrl());
							emailTriggerVO.setViewByReviewerUrl(manuscriptListVO.getViewByReviewerUrl());
							if (manuscriptListVO.getJournalMailTemplatesBody() != null && !manuscriptListVO.getJournalMailTemplatesBody().isEmpty())
								emailTriggerVO.setEmailTemplateBody(manuscriptListVO.getJournalMailTemplatesBody());
							if (manuscriptListVO.getJournalMailTemplatesSubject()!= null && !manuscriptListVO.getJournalMailTemplatesSubject().isEmpty())
								emailTriggerVO.setEmailTemplateSubject(manuscriptListVO.getJournalMailTemplatesSubject());

							log.info("Inside in assignUser Role For Reviewer For Email Trigger Data are:->"
									+ emailTriggerVO.toString());
							log.info("User Receiver Id:->" + getUserDatas.getId());
							log.info("User Receiver Role Id:->" + manuscriptListVO.getRoleId());
							log.info("User Sender Role Id:->" + manuscriptListVO.getEditorRoleId());
							log.info("User Sender Id:->" + manuscriptListVO.getUserId());
							log.info("Task Id:->" + 4);
							String message = emailTriggerService.emailTrigger(emailTriggerVO);
							log.info("Message of assignUser Role For Reviewer For Email Trigger " + message);
							log.info("End of assignUser Role For Reviewer For Email Trigger ");
						} else {
							// Create New Reviewer
							User createUser = new User();
							createUser.setAccountLocked(1);
							createUser.setIsActive(1);
							createUser.setSalutation(Enum.valueOf(Salutation.class, i.getSalutation()));
							createUser.setFirstName(i.getFirstname());
							createUser.setLastName(i.getLastname());
							createUser.setUsername(i.getEmail());
							createUser.setSpecialization(i.getSpecialization());
							createUser.setExpertise(i.getSubject());
							if (i.getCountry() != null && !i.getCountry().isEmpty()) {
								Countries countries = countriesService.getCountryByName(i.getCountry().trim());
								if (countries != null) {
									createUser.setCountryId(countries.getId());
								}
							}
							createUser.setEmail(i.getEmail());
							String token = UUID.randomUUID().toString();
							createUser.setPassword(passwordEncoder.encode(tempPassword.trim()));
							createUser.setToken(token);
							User savedUser = userLoginRepo.save(createUser);

							// Send Reviewer Invitation For Creating Account
							ManuScript getManuscriptData = manuscriptRepo.getOne(manuscriptListVO.getManuScriptId());
							if (getManuscriptData != null) {
								List<String> journalWebsiteUrl = new ArrayList<>();
								if (getManuscriptData.getJournal().getJournalMetadata() != null) {
									journalWebsiteUrl.add(getManuscriptData.getJournal().getJournalMetadata().getUrl());
								}

								sendMailForActivationForReviewer(i.getEmail(), token,
										manuscriptListVO.getSendEmailUrl(), i.getSalutation(), i.getFirstname(),
										i.getLastname(), getManuscriptData.getManuscriptTitle(),
										getManuscriptData.getJournal().getJournalTitle(),
										getManuscriptData.getJournal().getJournalId(),
										getManuscriptData.getJournal().getJournalCode(), journalWebsiteUrl);
							}

							if (savedUser != null) {
								ManuscriptEditor p = manuscriptEditorRepo.getManuscriptEditorDeat(
										manuscriptListVO.getJournalId(), manuscriptListVO.getManuScriptId(),
										manuscriptListVO.getManuscriptVersion(), manuscriptListVO.getUserId());
								if (p != null) {
									p.setManuscriptStatus(manuscriptListVO.getManuStatusId());
									manuscriptEditorRepo.save(p);
								}

								// Now Assign
								ManuScriptReviewer manuscriptReviewer = manuScriptReviewerRepo
										.findManuscriptReviewerDetails(manuscriptListVO.getManuScriptId(),
												manuscriptListVO.getManuscriptVersion(),
												manuscriptListVO.getJournalId(), savedUser.getId());

								if (manuscriptReviewer != null) {
									UserJournal userJournal = userJournalRepo.getUserJournal(savedUser.getId(),
											manuscriptListVO.getJournalId(), 5);
									if (userJournal != null) {
										userJournal.setUpdatedBy(manuscriptListVO.getUserId());
										userJournal.setIsActive(1);
										userJournalRepo.save(userJournal);
									} else {
										UserJournal userJournalData = new UserJournal();
										userJournalData.setJournalId(manuscriptListVO.getJournalId());
										userJournalData.setUserId(savedUser.getId());
										userJournalData.setRoleId(5);
										userJournalData.setUpdatedBy(manuscriptListVO.getUserId());
										userJournalData.setIsActive(1);
										userJournalRepo.save(userJournalData);
									}
									manuScriptReviewerRepo.updateReviewerDetails(manuscriptListVO.getManuStatusId(),
											manuscriptReviewer.getId(), manuscriptListVO.getDueDate());
								} else {
									ManuScriptReviewer manuscriptReviewerData = new ManuScriptReviewer();
									manuscriptReviewerData.setManuscriptId(manuscriptListVO.getManuScriptId());
									manuscriptReviewerData
											.setManuscriptVersion(manuscriptListVO.getManuscriptVersion());
									manuscriptReviewerData.setReviewerId(savedUser.getId());
									manuscriptReviewerData.setJournalId(manuscriptListVO.getJournalId());
									manuscriptReviewerData.setEditorId(manuscriptListVO.getUserId());
									manuscriptReviewerData.setEditorRoleId(manuscriptListVO.getEditorRoleId());
									manuscriptReviewerData.setCommentsForReviewer(manuscriptListVO.getMessage());
									manuscriptReviewerData.setManuscriptStatus(manuscriptListVO.getManuStatusId());
									if (manuscriptListVO.getDueDate() == null
											|| manuscriptListVO.getDueDate().equals("")
											|| manuscriptListVO.getDueDate().equals("NaN-NaN-NaN"))
										manuscriptReviewerData.setDueDate(new Date().from(LocalDate.now().plusDays(10)
												.atStartOfDay(ZoneId.systemDefault()).toInstant()));
									else
										manuscriptReviewerData.setDueDate(manuscriptListVO.getDueDate());
									UserJournal userJournal = userJournalRepo.getUserJournal(savedUser.getId(),
											manuscriptListVO.getJournalId(), 5);
									if (userJournal != null) {
										userJournal.setUpdatedBy(manuscriptListVO.getUserId());
										userJournal.setIsActive(1);
										userJournalRepo.save(userJournal);
									} else {
										UserJournal userJournalData = new UserJournal();
										userJournalData.setJournalId(manuscriptListVO.getJournalId());
										userJournalData.setUserId(savedUser.getId());
										userJournalData.setRoleId(5);
										userJournalData.setUpdatedBy(manuscriptListVO.getUserId());
										userJournalData.setIsActive(1);
										userJournalRepo.save(userJournalData);
									}
									manuscriptReviewerService.saveManuScriptReviewerDetails(manuscriptReviewerData);


								}
								if (manuscriptListVO.getManuStatusId() == 8
										|| manuscriptListVO.getManuStatusId() == 66) {
									manuscriptRepo.updateStage(manuscriptListVO.getManuStatusId(),
											manuscriptListVO.getManuScriptId());
									if (manuscriptListVO.getEditorRoleId() != null
											&& manuscriptListVO.getEditorRoleId().equals(2))
										manuscriptRepo.updateAssignedId(manuscriptListVO.getManuScriptId(),
												manuscriptListVO.getUserId(), 2);
								}

								//Authorities History
								AuthoritiesHistoryVo add = new AuthoritiesHistoryVo();
								add.setManuscriptId(manuscriptListVO.getManuScriptId());
								add.setManuscriptVersion(manuscriptListVO.getManuscriptVersion());
								add.setManuscriptStatusId(manuscriptListVO.getManuStatusId());
								add.setJournalId(manuscriptListVO.getJournalId());
								List<Integer> id = new ArrayList<>();
								id.add(savedUser.getId());
								add.setCurrentUserId(id);
								add.setCurrentUserRole(5);
								add.setPreviousUserId(manuscriptListVO.getUserId());
								add.setPreviousUserRole(manuscriptListVO.getEditorRoleId());
								add.setDueDate(manuscriptListVO.getDueDate());
								String result = authoritiesHistoryService.authoritiesHistory(add);
								log.info("Authorities:->" + result);

								// Email Trigger
								EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
								emailTriggerVO.setJournalId(manuscriptListVO.getJournalId());
								emailTriggerVO.setUserId(savedUser.getId());
								emailTriggerVO.setReviewerUserId(savedUser.getId());
								if (manuscriptListVO.getEditorRoleId().equals(2))
									emailTriggerVO.setManagingEditorId(manuscriptListVO.getUserId());
								else
									emailTriggerVO.setEditorUserId(manuscriptListVO.getUserId());

								emailTriggerVO.setManuscriptId(manuscriptListVO.getManuScriptId());
								emailTriggerVO.setManuscriptStatus(manuscriptListVO.getManuStatusId());
								emailTriggerVO.setReceiverRoleId(manuscriptListVO.getRoleId());
								if (manuscriptListVO.getTemplateId() != null) {
									emailTriggerVO.setTaskId(manuscriptListVO.getTemplateId());
								} else {
									emailTriggerVO.setTaskId(4);
								}
								emailTriggerVO.setRoleId(manuscriptListVO.getEditorRoleId());

								// For The Sending Url To Reviewer
								emailTriggerVO.setUrlManuscript(manuscriptListVO.getManuScriptId());
								emailTriggerVO.setUrlManuscriptVersion(manuscriptListVO.getManuscriptVersion());
								emailTriggerVO.setUrlJournalId(manuscriptListVO.getJournalId());
								emailTriggerVO.setUrlReviewerId(savedUser.getId());
								emailTriggerVO.setUrlManuscriptStatus(manuscriptListVO.getManuStatusId());
								emailTriggerVO.setAcceptByReviewerUrl(manuscriptListVO.getAcceptByReviewerUrl());
								emailTriggerVO.setRejectByReviewerUrl(manuscriptListVO.getRejectByReviewerUrl());
								emailTriggerVO.setViewByReviewerUrl(manuscriptListVO.getViewByReviewerUrl());
								if (manuscriptListVO.getJournalMailTemplatesBody() != null && !manuscriptListVO.getJournalMailTemplatesBody().isEmpty())
									emailTriggerVO.setEmailTemplateBody(manuscriptListVO.getJournalMailTemplatesBody());
								if (manuscriptListVO.getJournalMailTemplatesSubject()!= null && !manuscriptListVO.getJournalMailTemplatesSubject().isEmpty())
									emailTriggerVO.setEmailTemplateSubject(manuscriptListVO.getJournalMailTemplatesSubject());

								log.info("Inside in assignUser Role For Reviewer For Email Trigger Data are:->"
										+ emailTriggerVO.toString());
								log.info("User Receiver Id:->" + savedUser.getId());
								log.info("User Receiver Role Id:->" + manuscriptListVO.getRoleId());
								log.info("User Sender Role Id:->" + manuscriptListVO.getEditorRoleId());
								log.info("User Sender Id:->" + manuscriptListVO.getUserId());
								log.info("Task Id:->" + 4);
								String message = emailTriggerService.emailTrigger(emailTriggerVO);
								log.info("Message of assignUser Role For Reviewer For Email Trigger " + message);
								log.info("End of assignUser Role For Reviewer For Email Trigger ");

							}

						}
						j++;
					}
				}

				if(j==0) {
					throw new Exception("Author/Co-author not become reviewer.");
				}
			}


		}
		else if (manuscriptListVO.getRoleId() == 3) {
			ManuScript manu = manuscriptRepo.getFindByManuScriptDetail(manuscriptListVO.getManuScriptId());
			if (manuscriptListVO.getAssignToID() != null && !manuscriptListVO.getAssignToID().isEmpty()) {
				if(manuscriptListVO.getEIC_Folder() != null && (manuscriptListVO.getEIC_Folder().equalsIgnoreCase("ReviewedManuscript") || manuscriptListVO.getEIC_Folder().equalsIgnoreCase("ReviewersInvited") || manuscriptListVO.getEIC_Folder().equalsIgnoreCase("ManuscriptInReview"))) {
					List<ManuScriptReviewer> listRev = manuScriptReviewerRepo.getmanuScriptReviewer(manuscriptListVO.getManuScriptId(), manuscriptListVO.getManuscriptVersion(),manuscriptListVO.getJournalId());
					for (ManuScriptReviewer manuScriptReviewer : listRev) {
						if(manuScriptReviewer.getManuscriptStatus().equals(8) || manuScriptReviewer.getManuscriptStatus().equals(66) || manuScriptReviewer.getManuscriptStatus().equals(35)) {
							manuScriptReviewer.setIsActive(0);
							manuScriptReviewerRepo.save(manuScriptReviewer);

							EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
							emailTriggerVO.setJournalId(manuscriptListVO.getJournalId());
							emailTriggerVO.setUserId(manuScriptReviewer.getReviewerId());
							emailTriggerVO.setReviewerUserId(manuScriptReviewer.getReviewerId());
							emailTriggerVO.setManuscriptId(manuscriptListVO.getManuScriptId());
							emailTriggerVO.setTaskId(87);
							emailTriggerVO.setReceiverRoleId(5);
							emailTriggerVO.setEditorUserId(manu.getAssignedUser());
							emailTriggerVO.setRoleId(manuscriptListVO.getRoleId());
							emailTriggerVO.setManuscriptStatus(30);
							String message = emailTriggerService.emailTrigger(emailTriggerVO);
							log.info(" Email sent to Reviewer who has not reviewed the manuscript " + message);
						}
					}
				}
				manuscriptListVO.getAssignToID().stream().forEach(i -> {

					List<ManuscriptEditor> data = manuscriptEditorRepo.getByVersion(
							manuscriptListVO.getManuScriptId(), manuscriptListVO.getManuscriptVersion());
					if (data != null && !data.isEmpty()) {
						for (ManuscriptEditor manuscriptEditor2 : data) {
							manuscriptEditor2.setIsActive(0);
							manuscriptEditorRepo.save(manuscriptEditor2);
						}
					}
					ManuscriptEditor manuscriptEditorData = new ManuscriptEditor();
					manuscriptEditorData.setManuscriptId(manuscriptListVO.getManuScriptId());
					manuscriptEditorData.setManuscriptVersion(manuscriptListVO.getManuscriptVersion());
					if(manuscriptListVO.getEditorRoleId().equals(2)) {
						manuscriptEditorData.setManagingEditorId(manuscriptListVO.getUserId());
					}else {
						manuscriptEditorData.setManagingEditorId(authoritiesHistoryRepo.getManagingEditorId(manuscriptListVO.getManuScriptId(), manuscriptListVO.getJournalId()));
					}
					manuscriptEditorData.setEditorId(i);
					manuscriptEditorData.setIsActive(1);
					manuscriptEditorData.setSendBackEic(0);
					manuscriptEditorData.setJournalId(manuscriptListVO.getJournalId());
					manuscriptEditorData.setCommentsForEditor(manuscriptListVO.getMessage());
					manuscriptEditorData.setAssignedDate(new Date());
					manuscriptEditorData.setManuscriptStatus(manuscriptListVO.getManuStatusId());
					manuscriptEditorRepo.save(manuscriptEditorData);

					if (manuscriptListVO.getManuStatusId() == 3) {
						manu.setManuscriptStatusId(manuscriptListVO.getManuStatusId());
						manu.setAssignedUser(i);
						manu.setAssignedUserRole(3);
						manuscriptListVO.setEIC_Folder(manu.getEICFolder());
						manu.setEICFolder(manuscriptListVO.getEIC_Folder());
						manuscriptRepo.save(manu);
					}

					UserJournal uj = userJournalRepo.getUserJournal(i, manuscriptListVO.getJournalId(), 3);
					if (uj != null) {
						uj.setIsActive(1);
						userJournalRepo.save(uj);
					} else {
						UserJournal userJournal = new UserJournal();
						userJournal.setJournalId(manuscriptListVO.getJournalId());
						userJournal.setUserId(i);
						userJournal.setUpdatedBy(manuscriptListVO.getUserId());
						userJournal.setIsActive(1);
						userJournal.setRoleId(3);
						userJournalRepo.save(userJournal);
					}
				});
			}
		} else {
			ManuScript manuscriptData = new ManuScript();
			manuscriptData.setManuScriptId(manuscriptListVO.getManuScriptId());
			manuscriptData.setJournalId(manuscriptListVO.getJournalId());
			manuscriptService.saveManuscriptDetails(manuscriptData);
		}

	}

	public String sendMailForActivationForReviewer(String userEmail, String token1, String activationUrl,
												   String salutation, String firstName, String lastName, String manuscriptTitle, String journalTitle,
												   Integer journalId, String journalCode, List<String> journalWebsiteUrl) {
		String token = UUID.randomUUID().toString();
		userRepo.updateTokenByUser(userEmail, token);
		String fromMailID = env.getProperty("spring.mail.username");
		String toMailID = userEmail;
		String mailSubject = "Reviewer account username and password creation link";
		String mailBody = activateUserMailTemplateForReviewer(userEmail, token, activationUrl, salutation, firstName,
				lastName, manuscriptTitle, journalTitle, journalId, journalCode, journalWebsiteUrl);
		Journal i = journalsRepo.getOne(journalId);
		OtherMail add = new OtherMail();
		add.setJournalId(journalId);
		add.setToMail(toMailID);
		add.setFromMail(fromMailID);
		add.setSubject(mailSubject);
		add.setMailBody(mailBody);
		if (i != null && i.getJournalMetadata() != null) {
			if (i.getJournalMetadata().getJournalEmail() != null
					&& !i.getJournalMetadata().getJournalEmail().isEmpty()) {

				MailQueue addMailQueue = new MailQueue();
				addMailQueue.setMailFrom(fromMailID);
				addMailQueue.setMailTo(toMailID);
				addMailQueue.setJournalId(journalId);
				addMailQueue.setManuscriptId(0);
				addMailQueue.setMailSubject(mailSubject);
				addMailQueue.setMailBody(mailBody);
				addMailQueue.setMail_send_date(new Date());
				addMailQueue.setIsActive(0);
				mailQueueRepo.saveAndFlush(addMailQueue);

				add.setStatus("Insert into mailQueue");
				add.setCreatedAt(new Date());
				otherMailRepo.save(add);

			} else {

				MailQueue addMailQueue = new MailQueue();
				addMailQueue.setMailFrom(fromMailID);
				addMailQueue.setMailTo(toMailID);
				addMailQueue.setJournalId(journalId);
				addMailQueue.setManuscriptId(0);
				addMailQueue.setMailSubject(mailSubject);
				addMailQueue.setMailBody(mailBody);
				addMailQueue.setMail_send_date(new Date());
				addMailQueue.setIsActive(0);
				mailQueueRepo.saveAndFlush(addMailQueue);

				add.setStatus("Insert into mailQueue");
				add.setCreatedAt(new Date());
				otherMailRepo.save(add);
			}
		} else {

			MailQueue addMailQueue = new MailQueue();
			addMailQueue.setMailFrom(fromMailID);
			addMailQueue.setMailTo(toMailID);
			addMailQueue.setJournalId(journalId);
			addMailQueue.setManuscriptId(0);
			addMailQueue.setMailSubject(mailSubject);
			addMailQueue.setMailBody(mailBody);
			addMailQueue.setMail_send_date(new Date());
			addMailQueue.setIsActive(0);
			mailQueueRepo.saveAndFlush(addMailQueue);

			add.setStatus("Insert into mailQueue");
			add.setCreatedAt(new Date());
			otherMailRepo.save(add);

		}
		String message = "Password reset URL send on mail successfully.";
		return message;
	}

	private String activateUserMailTemplateForReviewer(String email, String token, String activationUrl,
													   String salutation, String firstName, String lastName, String manuscriptTitle, String journalTitle,
													   Integer journalId, String journalCode, List<String> journalWebsiteUrl) {
		StringBuilder builder = new StringBuilder();
		String resetUrl = activationUrl;
		String resetPasswordURL = null;
		Journal detailsOfJournal =journalsRepo.getJournalById(journalId);
		if(detailsOfJournal !=null) {
			builder.append("<p> Dear " + salutation + "." + lastName + "</p>");
			builder.append("<p></p>");
			builder.append("<p>Greetings!</p>");
			builder.append("<p></p>");
			builder.append("<p>We are pleased to add you as a reviewer for the journal “"+ detailsOfJournal.getJournalTitle()+"” on our new manuscript management application,</p>");
			builder.append("<p>Review JOW: <a href=" +"https://review.jow.medknow.com/"+ detailsOfJournal.getJournalCode() + "  target=\"blank\">"+"https://review.jow.medknow.com/"+ detailsOfJournal.getJournalCode() + "</a></p>");
			builder.append("<p></p>");
			builder.append("<p>Your Username is: "+email+"</p>");
			builder.append("<p>Your Default Password is: "+tempPassword.trim()+"</p>");
			builder.append("<p></p>");
			builder.append("<p>Please click on the following link or copy and paste the link in your browser to create a new password for your account.</p>");
			builder.append("<p></p>");
			resetPasswordURL = resetUrl + "?token=" + token + "&email=" + email + "&journalId=" + journalId
					+ "&journalCode=" + detailsOfJournal.getJournalCode();
			builder.append("<p> (<a href=" + resetPasswordURL + "  target=\"blank\">"+ resetPasswordURL + "</a>)</p>");
			builder.append("<p></p>");
			builder.append("<p> If you have any questions or concerns regarding log in or accessing your account, kindly write an email to our techsupport team at</p>");
			builder.append("<p>wkhlrpmedknow_techsupport@wolterskluwer.com. We shall be obliged to help you.</p>");
			builder.append("<p></p>");
			builder.append("<p>Regards,</p>");
			builder.append("<p></p>");
			builder.append("<p> " + detailsOfJournal.getJournalTitle() + "</p>");
		}
		builder.append("<p>Wolters Kluwer - Medknow</p>");
		return builder.toString();
	}

//	private String activateUserMailTemplateForReviewer(String email, String token, String activationUrl,
//			String salutation, String firstName, String lastName, String manuscriptTitle, String journalTitle,
//			Integer journalId, String journalCode, List<String> journalWebsiteUrl) {
//		StringBuilder builder = new StringBuilder();
//		String resetUrl = activationUrl;
//		String resetPasswordURL = resetUrl + "?token=" + token + "&email=" + email + "&journalId=" + journalId
//				+ "&journalCode=" + journalCode;
//		builder.append("<p> Dear " + salutation + "." + lastName + "</p>");
//		builder.append("<p> Greetings! </p>");
//
//		builder.append("<p>You have been invited to " + journalTitle + " to review a manuscript entitled "
//				+ manuscriptTitle + " </p>");
//
//		builder.append(
//				"We request you to confirm your account on our new manuscript management application, Review JOW: "
//						+ journalWebsiteUrl.get(0) + "");
//
//		builder.append("<p> Your Username is :" + email + "," + "</p>");
//
//		builder.append(
//				"<p>Please click on the following link or copy and paste the link in your browser to activate your account and set a new password to log in.</p>");
//
//		builder.append("<p>LINK:</p>");
//		builder.append("<p>(<a href=" + resetPasswordURL + "  target=\"blank\">" + resetPasswordURL + "</a>)</p>");
//
//		builder.append(
//				"<p> If you have any questions or concerns regarding log in or accessing your account, kindly write an email to our WK|Medknow Tech Support ID � wkhlrpmedknow_techsupport@wolterskluwer.com.\r\n"
//						+ "We shall be obliged to help you.\r\n" + "</p>");
//
//		builder.append("<p>Regards,</p>");
//		builder.append("<p>" + journalTitle + "</p>");
//		builder.append("<p>WK| Medknow Publications</p>");
//		return builder.toString();
//	}

	public String sendMailForActivation(String userEmail, String token) {

		userRepo.updateTokenByUser(userEmail, token);

		String fromMailID = env.getProperty("spring.mail.username");
		String toMailID = userEmail;
		String mailSubject = "Activate your account";
		String mailBody = activateUserMailTemplate(userEmail, token);

		MailQueue addMailQueue = new MailQueue();
		addMailQueue.setMailFrom(fromMailID);
		addMailQueue.setMailTo(toMailID);
		addMailQueue.setJournalId(259);
		addMailQueue.setManuscriptId(0);
		addMailQueue.setMailSubject(mailSubject);
		addMailQueue.setMailBody(mailBody);
		addMailQueue.setMail_send_date(new Date());
		addMailQueue.setIsActive(0);
		mailQueueRepo.saveAndFlush(addMailQueue);

		// mailUtils.sendEmail(fromMailID, toMailID, mailSubject, mailBody, "", "", "");

		String message = "Password reset URL send on mail successfully.";

		return message;
	}

	private String activateUserMailTemplate(String email, String token) {

		StringBuilder builder = new StringBuilder();
		String resetUrl = env.getProperty("localUrl") + "/signup/userverfication";
		String resetPasswordURL = resetUrl + "?token=" + token + "&email=" + email;
		builder.append("<p> Dear User, </p>");
		builder.append("<p> Please activate your account <a href=" + resetPasswordURL + "  target=\"blank\">"
				+ resetPasswordURL + "</a>\"</p>");
		builder.append("<p> THANK YOU !! </p>");
		return builder.toString();
	}

	@Override
	public void activateLockedUser(UserVO userVO) {
		userRepo.updateUserPassword(userVO.getPassword(), userVO.getToken());
	}

	@Override
	public List<ReviewerVO> reviewerFilter(ReviewerFilterVO vo) {
		StringBuilder query = new StringBuilder(
				"SELECT u FROM User u WHERE id IN (SELECT user FROM UserRole WHERE role =5) AND ");
		int countConditions = 0;

		Boolean journalBoolean = vo.getJournalId() != null && !vo.getJournalId().isEmpty();
		Boolean expertiseBoolean = vo.getExpertiseId() != null && !vo.getExpertiseId().isEmpty();
		Boolean specializationBoolean = vo.getSpecializationId() != null && !vo.getSpecializationId().isEmpty();
		Boolean institutionBoolean = vo.getInstitutionId() != null && !vo.getInstitutionId().isEmpty();
		Boolean nameBoolean = vo.getName() != null && !vo.getName().isEmpty();

		if (vo.getJournalId() != null && !vo.getJournalId().isEmpty()) {
			query.append("(id IN (SELECT userId FROM UserJournal WHERE journalId IN :journalList) ");
			countConditions++;
		}

		if (vo.getExpertiseId() != null && !vo.getExpertiseId().isEmpty()) {
			if (countConditions > 0)
				query.append("OR ");
			else
				query.append("( ");
			query.append("id IN (SELECT userId FROM UserSpecilization WHERE expertiseId IN :expertiseList) ");
			countConditions++;
		}

		if (vo.getSpecializationId() != null && !vo.getSpecializationId().isEmpty()) {
			if (countConditions > 0)
				query.append("OR ");
			else
				query.append("( ");
			query.append("id IN (SELECT userId FROM UserSpecilization WHERE specializationId IN :specializationList) ");
			countConditions++;
		}

		if (vo.getInstitutionId() != null && !vo.getInstitutionId().isEmpty()) {
			if (countConditions > 0)
				query.append("OR ");
			query.append("institute IN :instituteList ");
			countConditions++;
		}

		if (vo.getName() != null && !vo.getName().isEmpty()) {
			if (countConditions > 0)
				query.append("OR ");
			query.append(
					"CONCAT(COALESCE(first_name,''), COALESCE(middle_name,''), COALESCE(last_name,'')) LIKE :name");
			countConditions++;
		}

		if (!(!(journalBoolean || expertiseBoolean || specializationBoolean) && (institutionBoolean || nameBoolean)))
			query.append(")");

		List<User> list = userDao.reviewerFilterImplementation(query.toString(), vo);

		List<ReviewerVO> reviewerVoList = new ArrayList<ReviewerVO>();

		for (User user : list) {
			ReviewerVO reviewerVO = new ReviewerVO();
			reviewerVO.setId(user.getId());
			reviewerVO.setFirstname(user.getFirstName());
			reviewerVO.setMiddlename(user.getFirstName());
			reviewerVO.setLastname(user.getLastName());
			reviewerVO.setEmail(user.getEmail());
			reviewerVO.setCity(user.getCity());
			reviewerVO.setState(user.getState());
			List<ReviewerAvailability> availibilityList = reviewerAvailabilityRepo
					.getReviewerAvailabilityById(user.getId());
			reviewerVO.setReviewerAvailability(availibilityList);
			reviewerVoList.add(reviewerVO);
		}

		return reviewerVoList;
	}

	@Override
	public String updatePassword(UpdatePassword updatePassword) {
		boolean status = false;
		User userDetails = userLoginRepo.getById(updatePassword.getUserId());
		status = BCrypt.checkpw(updatePassword.getCurrentPassword(), userDetails.getPassword());
		Integer updatePwdId = updatePassword.getUserId();
		Integer userDetailsId = userDetails.getId();

		if ((updatePwdId - userDetailsId) == 0 && status) {
			System.out.print("hello");
			userDetails.setPassword(passwordEncoder.encode(updatePassword.getNewPassword()));
			userLoginRepo.save(userDetails);
			String fromMailID = env.getProperty("spring.mail.username");
			String toMailID = userDetails.getEmail();
			String mailSubject = "Password Updated Successfully";
			String mailBody = updatePasswordMailBody();
			MailQueue addMailQueue = new MailQueue();
			addMailQueue.setMailFrom(fromMailID);
			addMailQueue.setMailTo(toMailID);
			addMailQueue.setJournalId(updatePassword.getJournalId());
			addMailQueue.setManuscriptId(0);
			addMailQueue.setMailSubject(mailSubject);
			addMailQueue.setMailBody(mailBody);
			addMailQueue.setMail_send_date(new Date());
			addMailQueue.setIsActive(0);
			mailQueueRepo.saveAndFlush(addMailQueue);

			// mailUtils.sendEmail(fromMailID, toMailID, mailSubject, mailBody, "", "", "");
			String message = "Password Updated Successfully.";
			return message;
		}
		return null;
	}

	public String updatePasswordMailBody() {
		StringBuilder builder = new StringBuilder();
		builder.append("<p> Dear User,</p>");
		builder.append("<p> your password has been Updated successfully </p>");
		builder.append("<p> please Login with your new Password.</p>");
		builder.append("<p> Note: If you haven't requested for password </p>");
		builder.append("<p> please contact admin at admin@digiscape.com </p>");
		builder.append("<p> Thank you.</p>");
		return builder.toString();
	}

	@Override
	public String logedUser(LoggedUserVo loggedUserVo) {
		UserLogins userLoginDetails = userLoginsRepo.loggedUser(loggedUserVo.getUserId());
		if (userLoginDetails != null && loggedUserVo.getJournalId() != null) {
			UserLogins userLoginData = userLoginsRepo.getOne(userLoginDetails.getId());
			if (loggedUserVo.getLoginStatus().equalsIgnoreCase("USER_LOGIN")) {
				userLoginData.setLoggedInDate(new Date());
				userLoginData.setCurrently(loggedUserVo.getCurrently());
				userLoginData.setIpAddress(loggedUserVo.getIpAddress());
				userLoginData.setJournalId(loggedUserVo.getJournalId());
				userLoginData.setStatus(1);
				userLoginsRepo.save(userLoginData);
				return "User Login Updated";
			}
			if (loggedUserVo.getLoginStatus().equalsIgnoreCase("ON_PROCESS")) {
				userLoginData.setCurrently(loggedUserVo.getCurrently());
				userLoginData.setIpAddress(loggedUserVo.getIpAddress());
				userLoginData.setJournalId(loggedUserVo.getJournalId());
				userLoginData.setStatus(1);
				userLoginsRepo.save(userLoginData);
				return "User On Process";
			}
			if (loggedUserVo.getLoginStatus().equalsIgnoreCase("USER_LOGOUT")) {
				userLoginData.setLoggedOutDate(new Date());
				userLoginData.setIpAddress(loggedUserVo.getIpAddress());
				userLoginData.setJournalId(loggedUserVo.getJournalId());
				userLoginData.setStatus(0);
				userLoginsRepo.save(userLoginData);
				return "User Logout Status Updated";
			}
			return "User Logout Status Updated";
		} else {
			if (loggedUserVo.getLoginStatus().equalsIgnoreCase("USER_LOGIN") && loggedUserVo.getJournalId() != null) {
				UserLogins createNewUser = new UserLogins();
				createNewUser.setUserId(loggedUserVo.getUserId());
				createNewUser.setJournalId(loggedUserVo.getJournalId());
				createNewUser.setLoggedInDate(new Date());
				createNewUser.setIpAddress(loggedUserVo.getIpAddress());
				createNewUser.setCurrently(loggedUserVo.getCurrently());
				createNewUser.setUserRole(loggedUserVo.getUserRole());
				createNewUser.setStatus(1);
				userLoginsRepo.save(createNewUser);
				return "User Detail Created";
			}
		}
		return null;

	}

	@Override
	public List<LoggedUserVo> getLogedUserDetails(LoggedUserVo loggedUserVo) {
		List<UserLogins> journalBasedUserDetails = userLoginsRepo.getLogedUserDetails(loggedUserVo.getJournalId());
		List<LoggedUserVo> addLoggedUserList = new ArrayList<>();
		if (journalBasedUserDetails != null && !journalBasedUserDetails.isEmpty()) {
			journalBasedUserDetails.stream().forEach(i -> {
				if (!i.getUserId().equals(loggedUserVo.getUserId())) {
					LoggedUserVo addLoggedUser = new LoggedUserVo();
					addLoggedUser.setId(i.getId());
					addLoggedUser.setUserId(i.getUserId());
					addLoggedUser.setUserEmail(i.getUser().getUsername());
					addLoggedUser.setUserName(i.getUser().getFirstName() + " " + i.getUser().getLastName());
					addLoggedUser.setJournalId(i.getJournalId());
					addLoggedUser.setUserRole(i.getUserRole());
					addLoggedUser.setStatus(Math.abs(i.getLoggedInDate().getMinutes() - new Date().getMinutes()));
					addLoggedUser.setIpAddress(i.getIpAddress());
					addLoggedUser.setCurrently(i.getCurrently());
					addLoggedUser.setLoggedInDate(i.getLoggedInDate());
					addLoggedUserList.add(addLoggedUser);
				}
			});
		}
		return addLoggedUserList;
	}

	@Override
	public List<UserVO> getUserByRoleId(IDVO id) {
		List<UserVO> userDataVO = new ArrayList<UserVO>();
		List<User> userData = userRepo.getAllActiveUser();
		for (User user : userData) {
			for (Role role : user.getRole()) {
				if (role.getRoleId() == id.getId()) {
					userDataVO.add(UserTransformationalUtils.mapUserModelToUserVO(user));
				}
			}
		}
		return userDataVO;
	}

	@Override
	public List<UserVO> getLockedUser(IDVO id) {
		if (id.getJournalCode() != null && !id.getJournalCode().isEmpty() && id.getRoleIdList() == null
				&& id.getRoleIdList().isEmpty()) {
			List<UserVO> addUserVOList = new ArrayList<>();
			List<Journal> listOfJounal = journalsRepo.getByJournalCode(id.getJournalCode());
			if (!listOfJounal.isEmpty() && listOfJounal != null) {
				List<Integer> listOfJournalId = new ArrayList<>();
				listOfJounal.stream().forEach(i -> {
					listOfJournalId.add(i.getJournalId());
				});
				if (!listOfJournalId.isEmpty() && listOfJournalId != null) {
					List<UserJournal> listUserJournal = userJournalRepo
							.getListOfUserJournalByJournalsId(listOfJournalId);
					if (!listUserJournal.isEmpty() && listUserJournal != null) {
						listUserJournal.stream().forEach(j -> {
							if (j.getUser().getAccountLocked() != null && j.getUser().getAccountLocked().equals(0)) {
								UserVO addUserVO = new UserVO();
								addUserVO.setAccountExpired(j.getUser().getAccountExpired());
								addUserVO.setAccountLocked(j.getUser().getAccountLocked());
								addUserVO.setAddress(j.getUser().getAddress());
								addUserVO.setAddressThree(j.getUser().getAddressThree());
								addUserVO.setAddressTwo(j.getUser().getAddressTwo());
								addUserVO.setAreaOfInterest(j.getUser().getAreaOfInterest());
								addUserVO.setCity(j.getUser().getCity());
								addUserVO.setCountryId(j.getUser().getCountryId());
								addUserVO.setDepartment(j.getUser().getDepartment());
								addUserVO.setDesignation(j.getUser().getDesignation());
								addUserVO.setEmail(j.getUser().getEmail());
								addUserVO.setEmailCC(j.getUser().getEmailCC());
								addUserVO.setEmailNotification(j.getUser().getEmailNotification());
								addUserVO.setExperience(j.getUser().getExperience());
								addUserVO.setExpertise(j.getUser().getExpertise());
								addUserVO.setFaxNumber(j.getUser().getFaxNumber());
								addUserVO.setFirstName(j.getUser().getFirstName());
								addUserVO.setId(j.getUser().getId());
								addUserVO.setInstitute(j.getUser().getInstitute());
								addUserVO.setLastName(j.getUser().getLastName());
								addUserVO.setMiddleName(j.getUser().getMiddleName());
								addUserVO.setOrcidId(j.getUser().getOrcidId());
								addUserVO.setPhoneNumber(j.getUser().getPhoneNumber());
								addUserVO.setPostalCode(j.getUser().getPostalCode());
								addUserVO.setMobileNumber(j.getUser().getMobileNumber());
								addUserVO.setProductNotification(j.getUser().getProductNotification());
								addUserVO.setPublicationName(j.getUser().getPublicationName());
								addUserVO.setSalutation(j.getUser().getSalutation());
								addUserVO.setSecondaryEmail(j.getUser().getSecondaryEmail());
								addUserVO.setSecondaryEmailCC(j.getUser().getSecondaryEmailCC());
								addUserVO.setSmsNotification(j.getUser().getSmsNotification());
								addUserVO.setSpecialization(j.getUser().getSpecialization());
								addUserVO.setState(j.getUser().getState());
								addUserVO.setUsername(j.getUser().getUsername());
								addUserVO.setUserType(j.getUser().getUserType());
								addUserVO.setEditorAccessDetails(j.getUser().getEditorAccessDetails());
								addUserVOList.add(addUserVO);
							}
						});
					}
				}
				return addUserVOList;
			}
		}
		if (id.getJournalCode() != null && !id.getJournalCode().isEmpty() && id.getRoleIdList() != null
				&& !id.getRoleIdList().isEmpty()) {
			List<UserVO> addUserVOList = new ArrayList<>();
			List<UserRole> userRoleList = userRoleRepo.getUserListByRoleId(id.getRoleIdList());
			if (userRoleList != null && !userRoleList.isEmpty()) {
				List<Journal> listOfJounal = journalsRepo.getByJournalCode(id.getJournalCode());
				List<Integer> userID = new ArrayList<>();
				userRoleList.stream().forEach(j -> {
					userID.add(j.getUser());
				});
				if (listOfJounal != null && !listOfJounal.isEmpty()) {
					List<Integer> journalId = new ArrayList<>();
					listOfJounal.stream().forEach(i -> {
						journalId.add(i.getJournalId());
					});
					if (userID != null && !userID.isEmpty() && journalId != null && !journalId.isEmpty()) {
						List<UserJournal> listUserJournal = userJournalRepo.getListOfUser(userID, journalId);
						if (!listUserJournal.isEmpty() && listUserJournal != null) {
							listUserJournal.stream().forEach(j -> {
								if (j.getUser().getAccountLocked() != null
										&& j.getUser().getAccountLocked().equals(0)) {
									UserVO addUserVO = new UserVO();
									addUserVO.setAccountExpired(j.getUser().getAccountExpired());
									addUserVO.setAccountLocked(j.getUser().getAccountLocked());
									addUserVO.setAddress(j.getUser().getAddress());
									addUserVO.setAddressThree(j.getUser().getAddressThree());
									addUserVO.setAddressTwo(j.getUser().getAddressTwo());
									addUserVO.setAreaOfInterest(j.getUser().getAreaOfInterest());
									addUserVO.setCity(j.getUser().getCity());
									addUserVO.setCountryId(j.getUser().getCountryId());
									addUserVO.setDepartment(j.getUser().getDepartment());
									addUserVO.setDesignation(j.getUser().getDesignation());
									addUserVO.setEmail(j.getUser().getEmail());
									addUserVO.setEmailCC(j.getUser().getEmailCC());
									addUserVO.setEmailNotification(j.getUser().getEmailNotification());
									addUserVO.setExperience(j.getUser().getExperience());
									addUserVO.setExpertise(j.getUser().getExpertise());
									addUserVO.setFaxNumber(j.getUser().getFaxNumber());
									addUserVO.setFirstName(j.getUser().getFirstName());
									addUserVO.setId(j.getUser().getId());
									addUserVO.setInstitute(j.getUser().getInstitute());
									addUserVO.setLastName(j.getUser().getLastName());
									addUserVO.setMiddleName(j.getUser().getMiddleName());
									addUserVO.setOrcidId(j.getUser().getOrcidId());
									addUserVO.setPhoneNumber(j.getUser().getPhoneNumber());
									addUserVO.setPostalCode(j.getUser().getPostalCode());
									addUserVO.setMobileNumber(j.getUser().getMobileNumber());
									addUserVO.setProductNotification(j.getUser().getProductNotification());
									addUserVO.setPublicationName(j.getUser().getPublicationName());
									addUserVO.setSalutation(j.getUser().getSalutation());
									addUserVO.setSecondaryEmail(j.getUser().getSecondaryEmail());
									addUserVO.setSecondaryEmailCC(j.getUser().getSecondaryEmailCC());
									addUserVO.setSmsNotification(j.getUser().getSmsNotification());
									addUserVO.setSpecialization(j.getUser().getSpecialization());
									addUserVO.setState(j.getUser().getState());
									addUserVO.setUsername(j.getUser().getUsername());
									addUserVO.setUserType(j.getUser().getUserType());
									addUserVO.setEditorAccessDetails(j.getUser().getEditorAccessDetails());
									addUserVOList.add(addUserVO);
								}
							});
						}
					}
				}
				return addUserVOList;
			}
		}
		return null;
	}

	@Transactional
	@Override
	public User joinReviewer(UserVO userVo) {
		User user = UserTransformationalUtils.mapVOToModel(userVo);
		if (user.getPassword() == null) {
			String token = UUID.randomUUID().toString();
			// user.setPassword("password");
			user.setToken(token);
		}

		user.setIsActive(0);
		//user.setAccountLocked(0);
		user.setAccountLocked(1);
		user.setUsername(userVo.getEmail());
		User savedUser = userLoginRepo.save(user);

		// role created
		UserRole userRole = new UserRole();
		userRole.setUser(savedUser.getId());
		userRole.setRole(5);
		userRoleRepo.save(userRole);

		Integer journalId = null;
		// UserJournals
		List<Journal> journal = journalsRepo.getJournalByCode(userVo.getJournalCode());
		for (int i = 0; i < journal.size(); i++) {
			journalId = journal.get(i).getJournalId();

			UserJournal userJournals = new UserJournal();
			userJournals.setJournalId(journalId);
			userJournals.setUserId(savedUser.getId());
			userJournalRepo.save(userJournals);
		}

		if (userVo.getExpertiseData() != null && !userVo.getExpertiseData().isEmpty()) {
			for (ExpertiseVO userSpecilizationData : userVo.getExpertiseData()) {
				for (Integer specialization : userSpecilizationData.getSpecialization()) {
					UserSpecilization userSpecilization = new UserSpecilization();
					userSpecilization.setUserId(savedUser.getId());
					userSpecilization.setExpertiseId(userSpecilizationData.getId());
					userSpecilization.setSpecializationId(specialization);
					userSpecilization.setCreatedBy(savedUser.getId());
					userSpecilizationRepo.save(userSpecilization);
				}
			}
		}
		return savedUser;
	}

	@Override
	public List<User> getUserDetailsenabledByEditor() {
		List<User> userList = userLoginRepo.findAllIsActiveUserEnabledEditor();
		return userList;
	}

	@Override
	public void approvedByEditor(Integer id) {
		JSONObject obj = new JSONObject();
		try {

			User user = userLoginRepo.getOne(id);
			user.setIsActive(1);
			user.setAccountLocked(1);
			userLoginRepo.save(user);

			sendMailForActivation(user.getEmail(), user.getToken());

		} catch (Exception e) {
			obj.put("data", "Something went wrong.");
		}
	}

	@Override
	public List<User> getlistUserMakedReviewer() {
		List<User> userList = userLoginRepo.findlistallUserMakedReviewer();
		return userList;
	}

	@Override
	public void approvedByEditorMakeReviewer(Integer id) {

		User user = userLoginRepo.getOne(id);
		user.setAccountLocked(1);
		User savedUser = userLoginRepo.save(user);

		UserRole userRole = new UserRole();
		userRole.setUser(savedUser.getId());
		userRole.setRole(5);
		userRoleRepo.save(userRole);

	}

	@Override
	public void applyAsReviewer(Integer id, Integer journalId) {
		User user = userRepo.getOne(id);
		if (user.getApplyAsReviewer() != null) {
			user.setApplyAsReviewer(user.getApplyAsReviewer() + 1);
		} else {
			user.setApplyAsReviewer(1);
		}
		userRepo.save(user);

		UserJournal userJournal = userJournalRepo.getUserJournalByJournal(id, journalId);
		if (userJournal == null) {
			UserJournal userJournalData = new UserJournal();
			userJournalData.setUserId(id);
			userJournalData.setJournalId(journalId);
			userJournalRepo.save(userJournalData);
		}
	}

	@Override
	public boolean getApplyForReviewer(Integer id) {
		boolean value = true;
		User user = userRepo.getOne(id);
		if (user.getApplyAsReviewer() != 1) {
			value = false;
		}
		return value;
	}

	@Override
	public String validateAuthorSignUp(IDVO id) {
		User getUserDetails = userRepo.findByEmail(id.getEmail());
		if (getUserDetails != null) {
			getUserDetails.setAccountLocked(0);
			userRepo.save(getUserDetails);
			return getUserDetails.getSalutation() + "." + " " + getUserDetails.getFirstName() + " "
					+ getUserDetails.getLastName();
		}
		return null;
	}
	
	public List<UserVO> getUsersByOrcidId(String orcidID) {
	    List<UserVO> userVOList = new ArrayList<>();
	    try {
	        orcidID = orcidID.trim();
	        List<User> users = userRepo.findAllByOrcidId(orcidID); // already returns a list
	        if (users != null && !users.isEmpty()) {
	            for (User rowData : users) {
	                UserVO userVO = new UserVO();
	                userVO.setId(rowData.getId());
	                userVO.setOrcidId(rowData.getOrcidId());
	                userVO.setUsername(rowData.getUsername());
	                userVO.setEmail(rowData.getEmail());
	                userVO.setFirstName(rowData.getFirstName());
	                userVO.setLastName(rowData.getLastName());
	                // map roles, groups, journal, etc.
	                if (rowData.getRole() != null && !rowData.getRole().isEmpty())
	                    userVO.setRole(RoleTransformationalUtils.mapRoleListToVOList(rowData.getRole()));
	                if (rowData.getGroup() != null && !rowData.getGroup().isEmpty())
	                    userVO.setGroup(GroupTransformationalUtils.mapGroupListToGroupVOList(rowData.getGroup()));
	                if (rowData.getJournal() != null)
	                    userVO.setJournalVO(JournalListTransformationUtils.mapModelListToVoList(rowData.getJournal()));

	                userVOList.add(userVO);
	            }
	        }
	    } catch (Exception ex) {
	        log.error("Error in finding user(s) by orcid id " + ex.getMessage());
	    }
	    return userVOList;
	}


	@Override
	public UserVO getUserByOrcidId(String orcidID) {
		UserVO userVO = null;
		try {
			orcidID = orcidID.trim();
			User rowData = userRepo.findAllByOrcidId(orcidID).get(0);
			if (rowData != null) {
				userVO = new UserVO();
				userVO.setId(rowData.getId());
				userVO.setOrcidId(rowData.getOrcidId());
				userVO.setUsername(rowData.getUsername());
				userVO.setPassword(rowData.getPassword());
				userVO.setEmail(rowData.getEmail());
				userVO.setEmailCC(rowData.getEmailCC());
				userVO.setSecondaryEmail(rowData.getSecondaryEmail());
				userVO.setSecondaryEmailCC(rowData.getSecondaryEmailCC());
				userVO.setSalutation(rowData.getSalutation());
				userVO.setFirstName(rowData.getFirstName());
				userVO.setMiddleName(rowData.getMiddleName());
				userVO.setLastName(rowData.getLastName());
				userVO.setUserType(rowData.getUserType());
				userVO.setInstitute(rowData.getInstitute());
				userVO.setState(rowData.getState());
				userVO.setAddress(rowData.getAddress());
				userVO.setAddressTwo(rowData.getAddressTwo());
				userVO.setAddressThree(rowData.getAddressThree());
				userVO.setDepartment(rowData.getDepartment());
				userVO.setMobileNumber(rowData.getMiddleName());
				userVO.setPhoneNumber(rowData.getPhoneNumber());
				userVO.setFaxNumber(rowData.getFaxNumber());
				userVO.setIsActive(rowData.getIsActive());
				userVO.setCountryId(rowData.getCountryId());
				userVO.setCity(rowData.getCity());
				userVO.setPostalCode(rowData.getPostalCode());
				userVO.setAreaOfInterest(rowData.getAreaOfInterest());
				userVO.setNotificationFlag(rowData.getNotificationFlag());
				userVO.setPublicationName(rowData.getPublicationName());
				userVO.setAccountExpired(rowData.getAccountExpired());
				userVO.setAccountLocked(rowData.getAccountLocked());
				userVO.setDesignation(rowData.getDesignation());
				userVO.setSpecialization(rowData.getSpecialization());
				userVO.setExperience(rowData.getExperience());
				userVO.setExpertise(rowData.getExpertise());
				userVO.setEmailNotification(rowData.getEmailNotification());
				userVO.setSmsNotification(rowData.getSmsNotification());
				userVO.setSearchArticleStatus(rowData.getSearchArticleStatus());
				userVO.setEditorAccessDetails(rowData.getEditorAccessDetails());
				if (rowData.getEditorRoleAccess() != null && rowData.getEditorRoleAccess().getEditorRoleId() != null)
					userVO.setEditorRoleId(rowData.getEditorRoleAccess().getEditorRoleId());
				if (rowData.getRole() != null && !rowData.getRole().isEmpty())
					userVO.setRole(RoleTransformationalUtils.mapRoleListToVOList(rowData.getRole()));
				if (rowData.getGroup() != null && !rowData.getGroup().isEmpty())
					userVO.setGroup(GroupTransformationalUtils.mapGroupListToGroupVOList(rowData.getGroup()));
				if (rowData.getJournal() != null)
					userVO.setJournalVO(JournalListTransformationUtils.mapModelListToVoList(rowData.getJournal()));

			}
		} catch (Exception ex) {
			log.error("Error in finding user on the basis of prcid id" + ex.getMessage());
		}
		return userVO;
	}

	@Override
	public UserVO getUserByEmailId(String email) {
		User rowData = userRepo.findByEmail(email);
		UserVO userVO = new UserVO();
		if (rowData != null) {
			userVO.setId(rowData.getId());
			userVO.setOrcidId(rowData.getOrcidId());
			userVO.setUsername(rowData.getUsername());
			userVO.setPassword(rowData.getPassword());
			userVO.setEmail(rowData.getEmail());
			userVO.setEmailCC(rowData.getEmailCC());
			userVO.setSecondaryEmail(rowData.getSecondaryEmail());
			userVO.setSecondaryEmailCC(rowData.getSecondaryEmailCC());
			userVO.setSalutation(rowData.getSalutation());
			userVO.setFirstName(rowData.getFirstName());
			userVO.setMiddleName(rowData.getMiddleName());
			userVO.setLastName(rowData.getLastName());
			userVO.setUserType(rowData.getUserType());
			userVO.setInstitute(rowData.getInstitute());
			userVO.setState(rowData.getState());
			userVO.setAddress(rowData.getAddress());
			userVO.setAddressTwo(rowData.getAddressTwo());
			userVO.setAddressThree(rowData.getAddressThree());
			userVO.setDepartment(rowData.getDepartment());
			userVO.setMobileNumber(rowData.getMiddleName());
			userVO.setPhoneNumber(rowData.getPhoneNumber());
			userVO.setFaxNumber(rowData.getFaxNumber());
			userVO.setIsActive(rowData.getIsActive());
			userVO.setCountryId(rowData.getCountryId());
			userVO.setCity(rowData.getCity());
			userVO.setPostalCode(rowData.getPostalCode());
			userVO.setAreaOfInterest(rowData.getAreaOfInterest());
			userVO.setNotificationFlag(rowData.getNotificationFlag());
			userVO.setPublicationName(rowData.getPublicationName());
			userVO.setAccountExpired(rowData.getAccountExpired());
			userVO.setAccountLocked(rowData.getAccountLocked());
			userVO.setDesignation(rowData.getDesignation());
			userVO.setSpecialization(rowData.getSpecialization());
			userVO.setExperience(rowData.getExperience());
			userVO.setExpertise(rowData.getExpertise());
			userVO.setEmailNotification(rowData.getEmailNotification());
			userVO.setSmsNotification(rowData.getSmsNotification());
			userVO.setSearchArticleStatus(rowData.getSearchArticleStatus());
			userVO.setEditorAccessDetails(rowData.getEditorAccessDetails());
			if (rowData.getEditorRoleAccess() != null && rowData.getEditorRoleAccess().getEditorRoleId() != null)
				userVO.setEditorRoleId(rowData.getEditorRoleAccess().getEditorRoleId());
			if (rowData.getRole() != null && !rowData.getRole().isEmpty())
				userVO.setRole(RoleTransformationalUtils.mapRoleListToVOList(rowData.getRole()));
			if (rowData.getGroup() != null && !rowData.getGroup().isEmpty())
				userVO.setGroup(GroupTransformationalUtils.mapGroupListToGroupVOList(rowData.getGroup()));
			if (rowData.getJournal() != null)
				userVO.setJournalVO(JournalListTransformationUtils.mapModelListToVoList(rowData.getJournal()));
			return userVO;
		}
		return null;

	}

	@Override
	public UserVO loginAuthenticateByRole(IDVO id) {
		UserRole getUserRoleData = userRoleRepo.getUseRoleListByRoleList(id.getUserId(), id.getRoleId());
		UserVO userVO = new UserVO();
		if (getUserRoleData != null) {
			User rowData = userRepo.getOne(id.getUserId());
			if (rowData != null) {
				userVO.setId(rowData.getId());
				userVO.setOrcidId(rowData.getOrcidId());
				userVO.setUsername(rowData.getUsername());
				userVO.setPassword(rowData.getPassword());
				userVO.setEmail(rowData.getEmail());
				userVO.setEmailCC(rowData.getEmailCC());
				userVO.setSecondaryEmail(rowData.getSecondaryEmail());
				userVO.setSecondaryEmailCC(rowData.getSecondaryEmailCC());
				userVO.setSalutation(rowData.getSalutation());
				userVO.setFirstName(rowData.getFirstName());
				userVO.setMiddleName(rowData.getMiddleName());
				userVO.setLastName(rowData.getLastName());
				userVO.setUserType(rowData.getUserType());
				userVO.setInstitute(rowData.getInstitute());
				userVO.setState(rowData.getState());
				userVO.setAddress(rowData.getAddress());
				userVO.setAddressTwo(rowData.getAddressTwo());
				userVO.setAddressThree(rowData.getAddressThree());
				userVO.setDepartment(rowData.getDepartment());
				userVO.setMobileNumber(rowData.getMiddleName());
				userVO.setPhoneNumber(rowData.getPhoneNumber());
				userVO.setFaxNumber(rowData.getFaxNumber());
				userVO.setIsActive(rowData.getIsActive());
				userVO.setCountryId(rowData.getCountryId());
				userVO.setCity(rowData.getCity());
				userVO.setPostalCode(rowData.getPostalCode());
				userVO.setAreaOfInterest(rowData.getAreaOfInterest());
				userVO.setNotificationFlag(rowData.getNotificationFlag());
				userVO.setPublicationName(rowData.getPublicationName());
				userVO.setAccountExpired(rowData.getAccountExpired());
				userVO.setAccountLocked(rowData.getAccountLocked());
				userVO.setDesignation(rowData.getDesignation());
				userVO.setSpecialization(rowData.getSpecialization());
				userVO.setExperience(rowData.getExperience());
				userVO.setExpertise(rowData.getExpertise());
				userVO.setEmailNotification(rowData.getEmailNotification());
				userVO.setSmsNotification(rowData.getSmsNotification());
				userVO.setEditorAccessDetails(rowData.getEditorAccessDetails());
				if (rowData.getEditorRoleAccess() != null && rowData.getEditorRoleAccess().getEditorRoleId() != null)
					userVO.setEditorRoleId(rowData.getEditorRoleAccess().getEditorRoleId());
				if (rowData.getRole() != null && !rowData.getRole().isEmpty())
					userVO.setRole(RoleTransformationalUtils.mapRoleListToVOList(rowData.getRole()));
				if (rowData.getGroup() != null && !rowData.getGroup().isEmpty())
					userVO.setGroup(GroupTransformationalUtils.mapGroupListToGroupVOList(rowData.getGroup()));
				if (rowData.getJournal() != null)
					userVO.setJournalVO(JournalListTransformationUtils.mapModelListToVoList(rowData.getJournal()));
				return userVO;
			}
		} else {
			UserRole a = new UserRole();
			a.setUser(id.getUserId());
			a.setRole(id.getRoleId());
			a.setIsActive(1);
			a.setUpdatedOn(new Date());
			userRoleRepo.save(a);
			User rowData = userRepo.getOne(id.getUserId());
			if (rowData != null) {
				userVO.setId(rowData.getId());
				userVO.setOrcidId(rowData.getOrcidId());
				userVO.setUsername(rowData.getUsername());
				userVO.setPassword(rowData.getPassword());
				userVO.setEmail(rowData.getEmail());
				userVO.setEmailCC(rowData.getEmailCC());
				userVO.setSecondaryEmail(rowData.getSecondaryEmail());
				userVO.setSecondaryEmailCC(rowData.getSecondaryEmailCC());
				userVO.setSalutation(rowData.getSalutation());
				userVO.setFirstName(rowData.getFirstName());
				userVO.setMiddleName(rowData.getMiddleName());
				userVO.setLastName(rowData.getLastName());
				userVO.setUserType(rowData.getUserType());
				userVO.setInstitute(rowData.getInstitute());
				userVO.setState(rowData.getState());
				userVO.setAddress(rowData.getAddress());
				userVO.setAddressTwo(rowData.getAddressTwo());
				userVO.setAddressThree(rowData.getAddressThree());
				userVO.setDepartment(rowData.getDepartment());
				userVO.setMobileNumber(rowData.getMiddleName());
				userVO.setPhoneNumber(rowData.getPhoneNumber());
				userVO.setFaxNumber(rowData.getFaxNumber());
				userVO.setIsActive(rowData.getIsActive());
				userVO.setCountryId(rowData.getCountryId());
				userVO.setCity(rowData.getCity());
				userVO.setPostalCode(rowData.getPostalCode());
				userVO.setAreaOfInterest(rowData.getAreaOfInterest());
				userVO.setNotificationFlag(rowData.getNotificationFlag());
				userVO.setPublicationName(rowData.getPublicationName());
				userVO.setAccountExpired(rowData.getAccountExpired());
				userVO.setAccountLocked(rowData.getAccountLocked());
				userVO.setDesignation(rowData.getDesignation());
				userVO.setSpecialization(rowData.getSpecialization());
				userVO.setExperience(rowData.getExperience());
				userVO.setExpertise(rowData.getExpertise());
				userVO.setEmailNotification(rowData.getEmailNotification());
				userVO.setSmsNotification(rowData.getSmsNotification());
				userVO.setEditorAccessDetails(rowData.getEditorAccessDetails());
				if (rowData.getEditorRoleAccess() != null && rowData.getEditorRoleAccess().getEditorRoleId() != null)
					if (rowData.getRole() != null && !rowData.getRole().isEmpty())
						userVO.setRole(RoleTransformationalUtils.mapRoleListToVOList(rowData.getRole()));
				if (rowData.getGroup() != null && !rowData.getGroup().isEmpty())
					userVO.setGroup(GroupTransformationalUtils.mapGroupListToGroupVOList(rowData.getGroup()));
				if (rowData.getJournal() != null)
					userVO.setJournalVO(JournalListTransformationUtils.mapModelListToVoList(rowData.getJournal()));
				return userVO;
			}
		}
		return null;
	}

	@Override
	public String userVerification(IDVO id) {
		User getDataValid = userRepo.getUserVerficationData(id.getEmail(), id.getToken());
		if (getDataValid != null) {
			return "token is valid";
		}
		return "token expired";
	}

	@Override
	public String userVerificationUpdationPassword(IDVO id) {
		User getDataValid = userRepo.getUserVerficationData(id.getEmail(), id.getToken());
		if (getDataValid != null) {
			getDataValid.setPassword(passwordEncoder.encode(id.getPassword()));
			getDataValid.setIsActive(1);
			getDataValid.setToken("");
			userRepo.save(getDataValid);
			return "user updation successfully";
		}
		return "token expired";
	}

	@Override
	public List<UserVO> searchUser(IDVO id) {
		List<User> user = new ArrayList();
		List<User> userDataLis = new ArrayList();
		List<UserVO> userVoList = new ArrayList();
		UserVO userVO = null;
		Pageable paging = PageRequest.of(0, 50);
		if (id.getName() != null && id.getName() != "") {
			user = userRepo.searchUserByName(id.getJournalId(), id.getRoleId(), id.getName(), paging);
		} else if (id.getEmail() != null && id.getEmail() != "") {
			user = userRepo.searchUserByEmail(id.getEmail());
		} else if (id.getCity() != null && id.getCity() != "") {
			user = userRepo.searchUserByCity(id.getCity());
		}
		for (User userData : user) {
			int isJournal = 0;
			int isRole = 0;
			if (userData.getJournal() != null && !userData.getJournal().isEmpty()) {
				userVO = UserTransformationalUtils.mapUserModelToUserVO(userData);
				userVoList.add(userVO);
			}
		}
		return userVoList;
	}

	@Override
	public List<String> getSpecialization(IDVO id) {
		List<UserVO> userVoList = new ArrayList();
		UserVO userVO = null;

		List<String> user = userRepo.getSpecializationForReviewer(id.getJournalId());
		return user;
	}

	@Override
	public List<String> getExpertise(IDVO id) {
		List<String> user = userRepo.getExpertiseReviewer(id.getJournalId(),id.getSpecialization());
		return user;
	}

	@Override
	public int getUserCountByJournalAndRole(IDVO id) {

		String alphabet = id.getAlphabet();
		if(id.getAlphabet() == null || id.getAlphabet().trim().equalsIgnoreCase("")) {
			alphabet = "$$$$$$$$";
		}
		if(id.getAlphabet() != null && id.getAlphabet().trim().equalsIgnoreCase("ALL")) {
			alphabet = "";
		}
//		id.setAlphabet(alphabet);
		if(id.getName() == null && id.getExperties() == null && id.getSpecialization() == null
				&& id.getCountryId() == null && id.getEmail() == null && id.getAlphabet() != null && !id.getAlphabet().isEmpty()){
			return userRepo.getUserByJournalByAlphabetCount(id.getJournalId(), alphabet, id.getRoleId());
		}else {
			if(id.getSpecialization() == null || id.getSpecialization().trim().equalsIgnoreCase("")) {
				id.setSpecialization("NULLL");
			}
			if(id.getExperties() == null || id.getExperties().trim().equalsIgnoreCase("")) {
				id.setExperties("NULLL");
			}
			return userRepo.getUserCountByJournalAndRole(id.getJournalId(), id.getRoleId(), id.getEmail(), alphabet, id.getSpecialization(), id.getExperties(), id.getName(), id.getCountryId());
		}

	}
	@Override
	public List<UserVO> reviewerRefineSearch(IDVO id) {
		List<UserVO> userVoList = new ArrayList();
		Pageable paging = PageRequest.of(0, 50);
		if(id.getPageNo() != null && id.getLimit() != null) {
			paging = PageRequest.of(id.getPageNo(), id.getLimit());
		}

		if (id.getSpecialization() == "")
			id.setSpecialization(null);
		if (id.getExperties() == "")
			id.setExperties(null);
		if (id.getName() == "")
			id.setName(null);
		if (id.getEmail() == "")
			id.setEmail(null);


		List<User> user = new ArrayList<User>();
		if (id.getName() == null && id.getExperties() == null && id.getSpecialization() != null
				&& id.getCountryId() != null) {
			user = userRepo.getUserByJournalBySpecialization(id.getJournalId(), id.getSpecialization(),
					id.getCountryId());

		} else if (id.getName() == null && id.getExperties() != null && id.getSpecialization() != null
				&& id.getCountryId() != null) {
			user = userRepo.getUserByJournalBySpecializationAndExperties(id.getJournalId(), id.getSpecialization(),
					id.getExperties(), id.getCountryId());
		} else if (id.getName() == null && id.getExperties() != null && id.getSpecialization() != null
				&& id.getCountryId() == null) {
			user = userRepo.getUserByJournalBySpecializationAndExpertiesI(id.getJournalId(), id.getSpecialization(),
					id.getExperties());
		}else if(id.getName() == null && id.getExperties() == null && id.getSpecialization() == null
				&& id.getCountryId() == null && id.getEmail() == null && id.getAlphabet() != null && !id.getAlphabet().isEmpty()){
			String alphabet = id.getAlphabet();
			if(id.getAlphabet() == null || id.getAlphabet().trim().equalsIgnoreCase("")) {
				alphabet = "$$$$$$$$";
			}
			if(id.getAlphabet() != null && id.getAlphabet().trim().equalsIgnoreCase("ALL")) {
				alphabet = "";
			}
//			id.setAlphabet(alphabet);
			user = userRepo.getUserByJournalByAlphabet(id.getJournalId(), alphabet, id.getRoleId(), paging);
		} else if(id.getSearch() != null && !id.getSearch().isEmpty()){
			user = userRepo.getUserByJournalByAlphabet(id.getJournalId(), id.getSearch(), id.getRoleId(), paging);
		} else {
			if(id.getSpecialization() == null || id.getSpecialization().trim().equalsIgnoreCase("")) {
				id.setSpecialization("NULLL");
			}
			if(id.getExperties() == null || id.getExperties().trim().equalsIgnoreCase("")) {
				id.setExperties("NULLL");
			}
			if(id.getName() == null || id.getName().trim().equalsIgnoreCase("")) {
				id.setName("$$$$$$$$");
			}
			String alphabet = id.getAlphabet();
			if(id.getAlphabet() == null || id.getAlphabet().trim().equalsIgnoreCase("")) {
				alphabet = "$$$$$$$$";
			}
			if(id.getAlphabet() != null && id.getAlphabet().trim().equalsIgnoreCase("ALL")) {
				alphabet = "";
			}
//			id.setAlphabet(alphabet);
			user = userRepo.getUserByJournal( id.getRoleId(), id.getJournalId(), id.getSpecialization(), id.getExperties(), id.getName(),
					id.getCountryId(), id.getEmail(), alphabet, paging);
		}


		if (user != null && !user.isEmpty()) {
			for (User userData : user) {
				UserVO userVO = null;
//				UserJournal usersJournalByJournal = null;
//				usersJournalByJournal = userJournalRepo.getUserJournal(userData.getId(), id.getJournalId(), id.getRoleId());

				List<Integer> reviewStatusCode = Arrays.asList(12,13,14,15);
				Long articlesInReviewCount = manuScriptReviewerRepo.getReviewedManuscriptForReviewer(
						reviewStatusCode, id.getJournalId(),
						userData.getId()).stream().count();
				List<ManuScriptReviewer> articlesList = manuScriptReviewerRepo.getReviewerDataById(userData.getId(), id.getJournalId());
				List<ManuScriptReviewer> articlesListWithCompleteDate = articlesList.stream().filter(obj-> obj.getCompleteDate() !=null).collect(Collectors.toList());
				Optional<ManuScriptReviewer> lastReviewedDate = articlesListWithCompleteDate.stream().filter(obj -> reviewStatusCode.contains(obj.getManuscriptStatus())).sorted(Comparator.comparing(ManuScriptReviewer::getCompleteDate).reversed()).findFirst();
				if (userData != null && userData.getIsActive() != null && userData.getIsActive() == 1) {
					userVO = UserTransformationalUtils.mapUserModelToUserVO(userData);
					userVO.setArticlesInReviewCount(articlesInReviewCount.intValue());
					userVO.setArticlesInHandCount(getArticlesInHands(id.getJournalId(),userData.getId()));
					if(lastReviewedDate.isPresent() && lastReviewedDate.get().getCompleteDate() != null)
					{
						userVO.setLastReviewedDate(lastReviewedDate.get().getCompleteDate());
					}
					userVoList.add(userVO);
				}
			}
		}
		return userVoList;
	}

	public Integer getArticlesInHands(Integer journalId, Integer reviewerId) {

		Long articlesInHandCount = manuScriptReviewerRepo.getManuscriptReviewer(35, journalId,
				reviewerId).stream().count();
		return articlesInHandCount.intValue();
	}

	@Override
	public String sendMailForAuthorAssociatedWithJournal(String userEmail, String userName, String journalActivationUrl,
														 String journalTitle, Integer userId, Integer journalId) {
		String fromMailID = env.getProperty("spring.mail.username");
		String toMailID = userEmail;
		String mailSubject = "Activate your account";
		String mailBody = activateAuthorJournalMailTemplate(userEmail, userName, journalActivationUrl, journalTitle,
				userId, journalId);
		Journal i = journalsRepo.getOne(journalId);
		if (i != null && i.getJournalMetadata() != null) {
			if (i.getJournalMetadata().getJournalEmail() != null
					&& !i.getJournalMetadata().getJournalEmail().isEmpty()) {

				MailQueue addMailQueue = new MailQueue();
				addMailQueue.setMailFrom(fromMailID);
				addMailQueue.setMailTo(toMailID);
				addMailQueue.setJournalId(journalId);
				addMailQueue.setManuscriptId(0);
				addMailQueue.setMailSubject(mailSubject);
				addMailQueue.setMailBody(mailBody);
				addMailQueue.setMail_send_date(new Date());
				addMailQueue.setIsActive(0);
				mailQueueRepo.saveAndFlush(addMailQueue);

			} else {
				MailQueue addMailQueue = new MailQueue();
				addMailQueue.setMailFrom(fromMailID);
				addMailQueue.setMailTo(toMailID);
				addMailQueue.setJournalId(journalId);
				addMailQueue.setManuscriptId(0);
				addMailQueue.setMailSubject(mailSubject);
				addMailQueue.setMailBody(mailBody);
				addMailQueue.setMail_send_date(new Date());
				addMailQueue.setIsActive(0);
				mailQueueRepo.saveAndFlush(addMailQueue);

				// mailUtils.sendEmail(fromMailID, toMailID, mailSubject, mailBody, "", "", "");
			}
		} else {
			MailQueue addMailQueue = new MailQueue();
			addMailQueue.setMailFrom(fromMailID);
			addMailQueue.setMailTo(toMailID);
			addMailQueue.setJournalId(journalId);
			addMailQueue.setManuscriptId(0);
			addMailQueue.setMailSubject(mailSubject);
			addMailQueue.setMailBody(mailBody);
			addMailQueue.setMail_send_date(new Date());
			addMailQueue.setIsActive(0);
			mailQueueRepo.saveAndFlush(addMailQueue);

			// mailUtils.sendEmail(fromMailID, toMailID, mailSubject, mailBody, "", "", "");
		}
		String message = "Password reset URL send on mail successfully.";
		return message;

	}

	private String activateAuthorJournalMailTemplate(String email, String userName, String journalActivationUrl,
													 String journalTitle, Integer userId, Integer journalId) {
		StringBuilder builder = new StringBuilder();
		String resetUrl = journalActivationUrl;
		String resetPasswordURL = resetUrl + "?userId=" + userId + "&journalId=" + journalId;
		if (journalTitle != null && !journalTitle.isEmpty()) {
			builder.append("<p> Welcome" + " " + userName + " " + "to" + " " + journalTitle + "." + "</p>");
		} else {
			builder.append("<p> Welcome" + " " + userName + "." + "</p>");
		}
		builder.append("<p> This is the second and final step to activate as an Author on this journal. </p>");
		builder.append(
				"<p> Click on the link given below or copy and paste the entire link to browser address bar. </p>");
		builder.append("<p> (<a href=" + resetPasswordURL + "  target=\"blank\">" + resetPasswordURL + "</a>)</p>");
		builder.append("<p> Your Username is :" + email + "." + "</p>");
		builder.append("<p> This will confirm your journal activation into our system.</p>");
		builder.append("<p> Thank you.</p>");
		builder.append("<p></p>");
		builder.append("<p>Best Regards</p>");
		builder.append("<p>Editor in chief</p>");
		builder.append("<p>" + journalTitle + "</p>");
		return builder.toString();
	}

	@Override
	public String userActivation(IDVO id) {
		User getDataValid = userRepo.getUserVerficationData(id.getEmail(), id.getToken());
		if (getDataValid != null) {
			getDataValid.setIsActive(1);
			getDataValid.setAccountLocked(1);
			getDataValid.setToken("");
			userRepo.save(getDataValid);
			return "user activated";
		}
		return "token expired";
	}

	@Override
	public List<UserVO> searchActiveUser(IDVO id) {
		List<User> user = new ArrayList();
		List<User> userDataLis = new ArrayList();
		List<UserVO> userVoList = new ArrayList();
		UserVO userVO = null;

		Pageable paging = PageRequest.of(0, 50);
		if (id.getName() != null && id.getName() != "") {
			user = userRepo.searchActiveUserByName(id.getJournalId(), id.getRoleId(), id.getName(), paging);
		} else if (id.getEmail() != null && id.getEmail() != "") {
//			user = userRepo.searchActiveUserByEmail(id.getEmail());
			user = userRepo.searchActiveUserByEmail(id.getJournalId(), id.getRoleId(), id.getEmail(), paging);
		} else if (id.getCity() != null && id.getCity() != "") {
			user = userRepo.searchActiveUserByCity(id.getCity());
		}
		if (user != null) {
			for (User userData : user) {
				if (userData != null && userData.getIsActive() != null && userData.getIsActive() != 0) {

					userVO = UserTransformationalUtils.mapUserModelToUserVO(userData);
					userVoList.add(userVO);
				}
			}
		}

		return userVoList;
	}

	@Override
	public List<UserVO> getUserByJournalAndRole(IDVO id) {
		List<User> userList = userLoginRepo.getUserByJournalAndRole(id.getRoleId(), id.getJournalId());
		List<UserVO> userVoList = UserTransformationalUtils.mapUserListToUserVOListDataForUser(userList,
				id.getJournalId());
		return userVoList;
	}

	@Override
	public List<UserVO> getAllUserByJournalAndRole(IDVO id) {
		if (id.getIsActive() == null) {
			id.setIsActive(1);
		}
		List<User> userList = userLoginRepo.getAllUserByJournalAndRole(id.getRoleId(), id.getJournalId(),
				id.getIsActive());
		List<UserVO> userVoList = UserTransformationalUtils.mapUserListToUserVOListDataForUser(userList,
				id.getJournalId());
		return userVoList;
	}

	@Override
	public List<UserVO> getAllUserByJournalAndRoleAndPage(IDVO id, Pageable paging) {
		if (id.getIsActive() == null) {
			id.setIsActive(1);
		}
		List<User> userList = new ArrayList();
		try {
			userList = userLoginRepo.getAllUserByJournalAndRoleAndPage(id.getRoleId(), id.getJournalId(), paging,
					id.getIsActive());
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		List<UserVO> userVoList = UserTransformationalUtils.mapUserListToUserVOListDataForUser(userList,
				id.getJournalId());

		for(UserVO vo : userVoList )
		{
			List<ManuScriptReviewer> articlesList = manuScriptReviewerRepo.getReviewerDataByIdForCompleteDate(vo.getId(), id.getJournalId());
			Integer articlesInHandCount  =  getArticlesInHands(id.getJournalId(),vo.getId());
			vo.setArticlesInHandCount(articlesInHandCount);
			List<Integer> reviewStatusCode = Arrays.asList(12,13,14,15);
			Long articlesInReviewCount = manuScriptReviewerRepo.getReviewedManuscriptForReviewer(
					reviewStatusCode, id.getJournalId(),
					vo.getId()).stream().count();
			vo.setArticlesInReviewCount(articlesInReviewCount.intValue());
			List<ManuScriptReviewer> articlesListWithCompleteDate = articlesList.stream().filter(obj-> obj.getCompleteDate() !=null).collect(Collectors.toList());
			Optional<ManuScriptReviewer> lastReviewedDate = articlesListWithCompleteDate.stream().filter(obj -> reviewStatusCode.contains(obj.getManuscriptStatus())).sorted(Comparator.comparing(ManuScriptReviewer::getCompleteDate).reversed()).findFirst();
			if(lastReviewedDate.isPresent() && lastReviewedDate.get().getCompleteDate() != null)
			{
				vo.setLastReviewedDate(lastReviewedDate.get().getCompleteDate());
			}
		}

		return userVoList;
	}

	@Override
	public List<UserExportVo> exportAllUserByJournalAndRole(IDVO id) {
		if (id.getIsActive() == null) {
			id.setIsActive(1);
		}
		List<Tuple> tupleList = userLoginRepo.exportAllUserByJournalAndRole(id.getRoleId(), id.getJournalId(),
				id.getIsActive());
		return tupleList.stream()
				.map(t -> new UserExportVo(t.get(0, String.class), t.get(1, String.class), t.get(2, String.class),
						t.get(3, String.class), t.get(4, String.class), t.get(5, String.class), t.get(6, String.class), t.get(7, String.class) != null ? t.get(7, String.class).replace("{\"data\":[", "").replace("]}", "").trim() : null, t.get(8, String.class), t.get(9, String.class),
						t.get(10, String.class), t.get(11, String.class), t.get(12, String.class), t.get(13, String.class), t.get(14, String.class), t.get(15, String.class), t.get(16, String.class),
						t.get(17, String.class), t.get(18, String.class), t.get(19, String.class), t.get(20, String.class), t.get(21, String.class), t.get(22, String.class), t.get(23, String.class), t.get(24, String.class)))
				.collect(Collectors.toList());
//		return userList;
	}

	@Override
	public UserVO getByEmailVO(String email) {
		User user = userRepo.findByEmail(email);
		UserVO userVo = null;
		if (user != null) {
			userVo = UserTransformationalUtils.mapUserModelToUserVO(user);
		}
		return userVo;
	}

	@Override
	public List<UserVO> getUniqueUserByRoleId(IDVO id) {

		List<UserVO> userDataVO = new ArrayList<UserVO>();
		List<User> userData = userRepo.getUniqueUserByRoleId(id.getRoleId());
		userDataVO = UserTransformationalUtils.mapUserListToUserVOList(userData);
		return userDataVO;

	}

	@Override
	public String authorRegistrationMailSend(UserVO userVO) {
		User isExist = userRepo.findByEmail(userVO.getEmail());
		if (isExist != null) {
			UserJournal getUserJournalData = userJournalRepo.getUserJournal(isExist.getId(),
					userVO.getJournalId().get(0), userVO.getRoleId().get(0));
			if (getUserJournalData != null) {
				getUserJournalData.setIsActive(1);
				userJournalRepo.save(getUserJournalData);
				Journal detailsOfJournal = journalsRepo.getJournalById(userVO.getJournalId().get(0));
				List<String> journalWebsiteUrl = new ArrayList<>();
				String token = UUID.randomUUID().toString();

				//
				List<String> roleName = new ArrayList<>();

				List<Role> getRoleList = roleRepo.getRoleListById(userVO.getRoleId());
				if (getRoleList != null && !getRoleList.isEmpty()) {
					getRoleList.stream().forEach(j -> {
						roleName.add(j.getRoleName());
					});
				}
				//
				if (isExist != null && detailsOfJournal != null) {
					if (detailsOfJournal.getJournalMetadata() != null) {
						journalWebsiteUrl.add(detailsOfJournal.getJournalMetadata().getUrl());
					}
					try {
						String i = sendMailForAuthorActivation(isExist.getEmail(),
								isExist.getSalutation() + "." + " " + isExist.getFirstName() + " "
										+ isExist.getLastName(),
								token, userVO.getActivationUrl(), detailsOfJournal.getJournalTitle(), 0,
								detailsOfJournal.getJournalId(), detailsOfJournal.getJournalCode(), null, roleName,
								journalWebsiteUrl);
						return i;
					} catch (Exception ex) {
						log.info("Exception : " + ex);
					}

					return "mail send successfully";
				} else {
					return "Journal is not valid";
				}

			} else {
				return "User do not have role and journal";
			}

		} else {
			return "There is no valid user";
		}
	}

	private String activateAuthorMailTemplateReviewer(String email, String userName, String token, String activationUrl,
													  String journalTitle, Integer user, String password, Integer journalId, String journalCode,
													  List<String> journalName, List<String> roleName, List<String> journalWebsiteUrl) {
		StringBuilder builder = new StringBuilder();
		String resetUrl = activationUrl;
		String resetPasswordURL = null;
		Journal detailsOfJournal =journalsRepo.getJournalById(journalId);
		if(detailsOfJournal !=null) {
			builder.append("<p> Dear" + " " + userName + " </p>");
			builder.append("<p></p>");
			builder.append("<p>Greetings!</p>");
			builder.append("<p></p>");
			builder.append("<p>We are pleased to add you as a reviewer for the journal “"+ detailsOfJournal.getJournalTitle()+"” on our new manuscript management application,</p>");
			builder.append("<p>Review JOW: <a href=" +"https://review.jow.medknow.com/"+ detailsOfJournal.getJournalCode() + "  target=\"blank\">"+"https://review.jow.medknow.com/"+ detailsOfJournal.getJournalCode() + "</a></p>");
			builder.append("<p></p>");
			builder.append("<p>Your Username is: "+email+"</p>");
			builder.append("<p>Your Default Password is: "+tempPassword.trim()+"</p>");
			builder.append("<p></p>");
			builder.append("<p>Please click on the following link or copy and paste the link in your browser to create a new password for your account.</p>");
			builder.append("<p></p>");
			resetPasswordURL = resetUrl + "?token=" + token + "&email=" + email + "&journalId=" + journalId
					+ "&journalCode=" + detailsOfJournal.getJournalCode();
			builder.append("<p> (<a href=" + resetPasswordURL + "  target=\"blank\">"+ resetPasswordURL + "</a>)</p>");
			builder.append("<p></p>");
			builder.append("<p> If you have any questions or concerns regarding log in or accessing your account, kindly write an email to our techsupport team at</p>");
			builder.append("<p>wkhlrpmedknow_techsupport@wolterskluwer.com. We shall be obliged to help you.</p>");


			builder.append("<p></p>");
			builder.append("<p>Regards,</p>");
			builder.append("<p></p>");
			builder.append("<p> " + detailsOfJournal.getJournalTitle() + "</p>");
		}
		builder.append("<p>Wolters Kluwer - Medknow</p>");
		return builder.toString();
	}

	private String activateAuthorMailTemplate(String email, String userName, String token, String activationUrl,
											  String journalTitle, Integer user, String password, Integer journalId, String journalCode,
											  List<String> journalName, List<String> roleName, List<String> journalWebsiteUrl) {
		StringBuilder builder = new StringBuilder();
		String resetUrl = activationUrl;
		String resetPasswordURL = null;
		String orcidAuthUrl ="https://orcid.org/oauth/authorize?client_id="+oauthClientId+"&response_type=code&scope=/authenticate&redirect_uri="+oauthBaseUrl+"/outside/update-user-orcid";
		if (journalTitle != null && !journalTitle.isEmpty() && user.equals(0)) {
			builder.append("<p> Dear" + " " + userName + " </p>");
			builder.append("<p></p>");
			builder.append(
					"<p> Thank you for registering on the Review JOW online submission and peer-review system for  <br>"
							+ journalTitle + "</p>");
			builder.append("<p></p>");
			builder.append("<p> This is the final step towards the verification of your account as an Author. </p>");
			builder.append("<p></p>");
			resetPasswordURL = resetUrl + "?token=" + token + "&email=" + email + "&journalId=" + journalId
					+ "&journalCode=" + journalCode;
			builder.append(
					"<p> Please click on the below link or copy paste this URL into your browser. This will confirm your registration on our application. </p>");
			builder.append("<p></p>");
			builder.append("<p><b> Account Confirmation Link:</b> (<a href=" + resetPasswordURL + "  target=\"blank\">"
					+ resetPasswordURL + "</a>)</p>");
			builder.append("<p><b>NOTE: </b> Please note this URL is only valid for 24 hours. If the link you’ve clicked has expired, then please re-generate a new verification link from the ‘Register as an author’ area on the login page.</p>");
		} else if (journalTitle != null && !journalTitle.isEmpty() && user.equals(1)) {
			builder.append("<p> Dear" + " " + userName + "." + "</p>");

		} else {
			builder.append("<p> Dear" + " " + userName + "." + "</p>");

		}
		builder.append("<p></p>");

		if (journalWebsiteUrl != null && !journalWebsiteUrl.isEmpty()) {
			int count = 1;
			builder.append("<p>Please enter the below email address as username to access the Review JOW at</p>");
			for (String i : journalWebsiteUrl) {
				builder.append(
						"<p><b>" + count + "</b>. " + "<a href=" + i + " target=\"blank\">" + i + "</a>" + "</p>");
				count++;
			}
		}

		builder.append("<p> <b>Email Address: </b>" + email + "." + "</p>");

		builder.append(
				"<p> Please use the password which was created during registration. If you do not remember your password, </p>");
		builder.append(
				"<p> please click on <b>‘Forgot Password’</b> option on the login page. You will receive a reset password link to </p>");
		builder.append("<p> generate a new password to login into your account. </p>");
		builder.append("<p></p>");
		builder.append("<p> You can change your password and other personal information by logging into the "
				+ journalTitle + "</p>");
		builder.append(
				"<p> submission website and clicking on the <b>‘Update Profile’</b> link on the menu from the dashboard.</p>");
		builder.append("<br><br><p> Once you’ve completed the verification of your author account on our application, you will be able to link your 16-digit ORCID number with your account.<br>To link your ORCID with your account, kindly use the below link to complete the linking process so that your ORCID is recorded with your account on our application.</p>");
		builder.append("<a href="+orcidAuthUrl+" target=\"_blank\">"+orcidAuthUrl+"</a>");


		builder.append("<p></p>");
		builder.append("<p>Best Regards</p>");
		builder.append("<p></p>");
		builder.append("<p> " + journalTitle + "</p>");

		builder.append("<hr>");
		builder.append("<p></p>");
		builder.append("<p><b>Please contact the Medknow Publication Tech Support Team (Email ID:</b> </p>");
		builder.append("<p><b>WKHLRPMedknow_techsupport@wolterskluwer.com.) if you have any questions. </b></p>");
		builder.append("<p></p>");
		builder.append("<hr>");

		return builder.toString();
	}


	@Override
	public void updateUserJournal(UserVO userVO) {
		UserJournal i = userJournalRepo.updateUserJournal(userVO.getId(), userVO.getJournal());
		if (i != null) {
			i.setIsActive(userVO.getIsActive());
			userJournalRepo.save(i);
		}
	}

	@Override
	public List<UserVO> searchUserByEmail(IDVO id) {
		List<User> user = new ArrayList();
		List<UserVO> userVoList = new ArrayList();
		UserVO userVO = null;
		user = userRepo.getByEmail(id.getEmail());
		if (user != null) {
			for (User userData : user) {
				if (userData.getIsActive() != null) {
					if (userData.getJournal() != null && !userData.getJournal().isEmpty() && userData.getRole() != null
							&& !userData.getRole().isEmpty()) {
						userVO = UserTransformationalUtils.mapUserModelToUserVO(userData);
						userVoList.add(userVO);
					}

				}
			}
		}
		return userVoList;
	}

	@Override
	public String eicChangeRequest(IDVO iDVO) {
		if (iDVO.getJournalId() != null && iDVO.getCurrentUserId() != null && iDVO.getPreviousUserId() != null) {
			// Deactivate Role on that journal of previous user
			UserJournal u = userJournalRepo.getUserJournal(iDVO.getPreviousUserId(), iDVO.getJournalId(), 2);
			if (u != null) {
				u.setIsActive(0);
				userJournalRepo.save(u);
			}
			// Update manuscript Table from previous userId to current userId
			manuscriptRepo.updatePreviousAssginUserToCurrentAssignUsedId(iDVO.getCurrentUserId(),
					iDVO.getPreviousUserId(), iDVO.getJournalId());
			// managing editor
			manuscriptEditorRepo.updatePreviousManagingEditor(iDVO.getCurrentUserId(), iDVO.getPreviousUserId(),
					iDVO.getJournalId());
			// manuscript reviewer
			manuScriptReviewerRepo.updatePreviousManagingEditor(iDVO.getCurrentUserId(), iDVO.getPreviousUserId(),
					iDVO.getJournalId());
			return "Eic has been changed";
		} else {
			return "Please provide data";
		}
	}

	@Override
	public String activateCurrentUser(IDVO iDVO) {
		// Activate Or Assign role to current user on this journal as a eic
		UserJournal l = userJournalRepo.getUserJournal(iDVO.getCurrentUserId(), iDVO.getJournalId(), 2);
		if (l != null) {
			l.setIsActive(1);
			userJournalRepo.save(l);
		} else {
			UserJournal p = new UserJournal();
			p.setJournalId(iDVO.getJournalId());
			p.setUserId(iDVO.getCurrentUserId());
			p.setRoleId(2);
			p.setIsActive(1);
			p.setUpdatedDate(new Date());
			p.setUpdatedBy(iDVO.getCurrentUserId());
			userJournalRepo.save(p);
		}
		return "Current User has been activated";
	}
	// Author: NIC
	// Date: 2023-09-07
	// Description: This change improves the fetching time of users(author/reviewer/editor) count.
	@Override
	public IDVO getUserCountOnAdmin() {
		IDVO add = new IDVO();
		List<Tuple> userList = userJournalRepo.userJournalWithRole();

		userList.forEach(tuple -> {

			if (tuple.get(0, Integer.class) == 2) {
				add.setCountManagingEditor(tuple.get(1, BigInteger.class).intValue());
			}
			if (tuple.get(0, Integer.class) == 3) {
				add.setCountEditor(tuple.get(1, BigInteger.class).intValue());
			}
			if (tuple.get(0, Integer.class) == 4) {
				add.setCountAuthor(tuple.get(1, BigInteger.class).intValue());
			}
			if (tuple.get(0, Integer.class) == 5) {
				add.setCountRefree(tuple.get(1, BigInteger.class).intValue());
			}
		});

		return add;
	}


	@Override
	public List<UserVO> getUserDetailsByJournalAndRole(IDVO iDVO) {
		List<UserVO> addd = new ArrayList<>();
		List<UserJournal> list = userJournalRepo.getUserRoleJournalId(iDVO.getJournalId(), iDVO.getRoleId());
		if (list != null && !list.isEmpty()) {
			list.stream().forEach(i -> {
				UserVO j = new UserVO();
				j.setId(i.getUser().getId());
				j.setFirstName(i.getUser().getFirstName());
				j.setLastName(i.getUser().getLastName());
				j.setMiddleName(i.getUser().getMiddleName());
				j.setEmail(i.getUser().getEmail());
				j.setSalutation(i.getUser().getSalutation());
				addd.add(j);
			});
		}
		return addd;
	}

	@Override
	public Integer getCount(IDVO id) {
		Integer count = userLoginRepo.getCount(id.getRoleId(), id.getJournalId(), id.getIsActive());
		return count;
	}

	@Override
	public User loginUser(UserVO userVo) {
		User rowData = userLoginRepo.findByEmail(userVo.getEmail().trim());
		return rowData;
	}

	@Override
	public UserVO userDetailsVo(User rowData) {
		UserVO userVO = new UserVO();
		if (rowData != null) {
			userVO.setId(rowData.getId());
			userVO.setOrcidId(rowData.getOrcidId());
			userVO.setUsername(rowData.getUsername());
			userVO.setPassword(rowData.getPassword());
			userVO.setEmail(rowData.getEmail());
			userVO.setEmailCC(rowData.getEmailCC());
			userVO.setSecondaryEmail(rowData.getSecondaryEmail());
			userVO.setSecondaryEmailCC(rowData.getSecondaryEmailCC());
			userVO.setSalutation(rowData.getSalutation());
			userVO.setFirstName(rowData.getFirstName());
			userVO.setMiddleName(rowData.getMiddleName());
			userVO.setLastName(rowData.getLastName());
			userVO.setUserType(rowData.getUserType());
			userVO.setInstitute(rowData.getInstitute());
			userVO.setState(rowData.getState());
			userVO.setAddress(rowData.getAddress());
			userVO.setAddressTwo(rowData.getAddressTwo());
			userVO.setAddressThree(rowData.getAddressThree());
			userVO.setDepartment(rowData.getDepartment());
			userVO.setMobileNumber(rowData.getMiddleName());
			userVO.setPhoneNumber(rowData.getPhoneNumber());
			userVO.setFaxNumber(rowData.getFaxNumber());
			userVO.setIsActive(rowData.getIsActive());
			userVO.setCountryId(rowData.getCountryId());
			userVO.setCity(rowData.getCity());
			userVO.setPostalCode(rowData.getPostalCode());
			userVO.setAreaOfInterest(rowData.getAreaOfInterest());
			userVO.setNotificationFlag(rowData.getNotificationFlag());
			userVO.setPublicationName(rowData.getPublicationName());
			userVO.setAccountExpired(rowData.getAccountExpired());
			userVO.setAccountLocked(rowData.getAccountLocked());
			userVO.setDesignation(rowData.getDesignation());
			userVO.setSpecialization(rowData.getSpecialization());
			userVO.setExperience(rowData.getExperience());
			userVO.setExpertise(rowData.getExpertise());
			userVO.setEmailNotification(rowData.getEmailNotification());
			userVO.setSmsNotification(rowData.getSmsNotification());
			userVO.setEditorAccessDetails(rowData.getEditorAccessDetails());
			if (rowData.getEditorRoleAccess() != null && rowData.getEditorRoleAccess().getEditorRoleId() != null)
				userVO.setEditorRoleId(rowData.getEditorRoleAccess().getEditorRoleId());
			if (rowData.getRole() != null && !rowData.getRole().isEmpty())
				userVO.setRole(RoleTransformationalUtils.mapRoleListToVOList(rowData.getRole()));
			if (rowData.getGroup() != null && !rowData.getGroup().isEmpty())
				userVO.setGroup(GroupTransformationalUtils.mapGroupListToGroupVOList(rowData.getGroup()));



			if (rowData.getUserJournal() != null && !rowData.getUserJournal().isEmpty()) {
				List<JournalListVO> add = new ArrayList<>();
				rowData.getUserJournal().forEach(i -> {
					if (i.getIsActive() != null && i.getIsActive().equals(1)) {
						Journal getJournal = journalsRepo.getOne(i.getJournalId());
						if (getJournal != null) {
							JournalListVO vo = new JournalListVO();
							vo.setJournalId(getJournal.getJournalId());
							vo.setJournalTitle(getJournal.getJournalTitle());
							vo.setJournalCode(getJournal.getJournalCode());
							vo.setRoleId(i.getRoleId());
							vo.setIsActive(getJournal.getIsActive());
							vo.setJournalShortTitle(getJournal.getJournalShortTitle());
							if (getJournal.getJournalMetadata() != null) {
								vo.setJournalBlindType(getJournal.getJournalMetadata().getJournalBlindType());
								vo.setIsChargingReq(getJournal.getJournalMetadata().getIsChargingRequired());
								vo.setIsSubmissionRequired(getJournal.getJournalMetadata().getIsSubmissionRequired());
							}
							if (getJournal.getJournalPolicies() != null) {
								vo.setOrcIdMandatory(getJournal.getJournalPolicies().getOrcIdMandatory());
							}
							add.add(vo);
						}
					}
				});
				userVO.setJournalVO(add);
			}
		}
		return userVO;
	}

	@Override
	public List<UserVO> getInactiveUsers(IDVO id, Pageable paging) {
		List<User> userList = userLoginRepo.findAllInActiveUser(0, id.getEmail(), paging);
		return UserTransformationalUtils.mapUserListToUserVOListData(userList);
	}

	@Override
	public Integer getInactiveUserCount(IDVO id) {
		return userLoginRepo.getInactiveUserCount(0, id.getEmail());
	}

	@Override
	public void updateInactiveUser(UserVO userVO) {
		User userData = userLoginRepo.getById(userVO.getId());
		if (userData != null) {
			userLoginRepo.updateInactiveUser(userData.getId());
		}

	}

	@Override
	public List<UserVO> getAuthorListByJournal(IDVO id, Pageable paging) {

		List<User> userList = new ArrayList<User>();
		if (id.getIsActive() == null) {
			id.setIsActive(1);
		}

		if (null != id.getSearch() && "" != id.getSearch()) {
			userList = userLoginRepo.getAuthorListByJournalSearch(id.getRoleId(), id.getJournalId(), id.getIsActive(),
					id.getSearch(), paging);
		} else {
			userList = userLoginRepo.getAuthorListByJournal(id.getRoleId(), id.getJournalId(), id.getIsActive(),
					paging);
		}

		List<UserVO> userVoList = UserTransformationalUtils.mapUserListToUserVOListDataForUser(userList,
				id.getJournalId());
		return userVoList;
	}

	@Override
	public Integer getTotalSearchCountAuthorListByJournal(Integer roleId, Integer journalId, Integer isActive,
														  String search) {

		Integer totalCount = 0;

		totalCount = userLoginRepo.getTotalSearchCountAuthorListByJournal(roleId, journalId, isActive, search);

		return totalCount;
	}

	@Override
	public Integer getTotalCountAuthorListByJournal(Integer roleId, Integer journalId, Integer isActive) {

		Integer totalCount = 0;

		totalCount = userLoginRepo.getTotalCountAuthorListByJournal(roleId, journalId, isActive);

		return totalCount;
	}
	@Override
	public UserDetailsListVO getAllUsersListByJournalAndRole(IDVO id) {
		UserDetailsListVO userDetailsListVO = new UserDetailsListVO();
		int journalId=id.getJournalId();
		List<Tuple> userList = userLoginRepo.getUsersListByJournalAndAllRole(id.getRoleIdList(), journalId);
		List<UserDetailsVO> editorList = new ArrayList<UserDetailsVO>();
		List<UserDetailsVO> authorList = new ArrayList<UserDetailsVO>();
		List<UserDetailsVO> reviewerList = new ArrayList<UserDetailsVO>();

		for (Tuple user : userList) {
			Integer roleId = user.get(2, Integer.class);
			UserDetailsVO userDetailsVOS = new UserDetailsVO(user.get(3, String.class), user.get(0, Integer.class), user.get(1, String.class), roleId);

			if (roleId.equals(Users.EDITOR.getRoleId()))
				editorList.add(userDetailsVOS);
			else if (roleId.equals(Users.AUTHOR.getRoleId()))
				authorList.add(userDetailsVOS);
			else if (roleId.equals(Users.REVIEWER.getRoleId()))
				reviewerList.add(userDetailsVOS);

		}

		userDetailsListVO.setEditors(editorList);
		userDetailsListVO.setAuthors(authorList);
		userDetailsListVO.setReviewers(reviewerList);

//        id.getRoleIdList().forEach(roleId -> {
//            List<Tuple> userList = userLoginRepo.getUsersListByJournalAndRole(roleId, journalId);
//            List<UserDetailsVO> userDetailsVOS = userList.stream().
//					filter(x -> !ObjectUtils.isEmpty(x.get(1, String.class)) && !ObjectUtils.isEmpty(x.get(3, String.class))).
//					map(user -> new UserDetailsVO(user.get(5, String.class), user.get(0, Integer.class), user.get(3, String.class),user.get(4, Integer.class))).collect(Collectors.toList());
//            if (roleId.equals(Users.EDITOR.getRoleId()))
//                userDetailsListVO.setEditors(userDetailsVOS);
//            else if (roleId.equals(Users.AUTHOR.getRoleId()))
//                userDetailsListVO.setAuthors(userDetailsVOS);
//            else if (roleId.equals(Users.REVIEWER.getRoleId()))
//                userDetailsListVO.setReviewers(userDetailsVOS);
//        });
		return userDetailsListVO;
	}

	@Override
	public void updateEditorAccess(User user)
	{
		userRepo.save(user);
	}

	@Override
	public void addNewUserRole(UserVO userVO) {
		if (userVO.getJournalId() != null && !userVO.getJournalId().isEmpty()) {
			for (Integer journalId : userVO.getJournalId()) {
				if (userVO.getRoleId() != null && !userVO.getRoleId().isEmpty()) {
					for (Integer roleId : userVO.getRoleId()) {
						UserJournal i = null;
						i = userJournalRepo.getUserJournalInfo(userVO.getId(), journalId, roleId);
						if (i != null) {
							i.setIsActive(1);
							if (roleId == 3 && userVO.getEditorRoleId() != null) {
								EditorRoleAccess editorRoleAccessData = editorRoleAccessRepo
										.getDataByUserAndRoleId(userVO.getId());
								if (editorRoleAccessData != null) {
									editorRoleAccessData.setIsActive(1);
									editorRoleAccessData.setEditorRoleId(userVO.getEditorRoleId());
									editorRoleAccessRepo.save(editorRoleAccessData);
								} else {
									EditorRoleAccess editorRoleAccess = new EditorRoleAccess();
									editorRoleAccess.setUserId(userVO.getId());
									editorRoleAccess.setIsActive(1);
									editorRoleAccess.setEditorRoleId(userVO.getEditorRoleId());
									editorRoleAccessRepo.save(editorRoleAccess);
								}
							}
							i.setUpdatedBy(userVO.getUpdatedBy());
							i.setUpdatedDate(new Date());
							userJournalRepo.save(i);
						} else {
							UserJournal add = new UserJournal();
							add.setJournalId(journalId);
							add.setRoleId(roleId);
							add.setUserId(userVO.getId());
							if (roleId == 3 && userVO.getEditorRoleId() != null) {
								EditorRoleAccess editorRoleAccessData = editorRoleAccessRepo
										.getDataByUserAndRoleId(userVO.getId());
								if (editorRoleAccessData != null) {
									editorRoleAccessData.setIsActive(1);
									editorRoleAccessData.setEditorRoleId(userVO.getEditorRoleId());
									editorRoleAccessRepo.save(editorRoleAccessData);
								} else {
									EditorRoleAccess editorRoleAccess = new EditorRoleAccess();
									editorRoleAccess.setUserId(userVO.getId());
									editorRoleAccess.setEditorRoleId(userVO.getEditorRoleId());
									editorRoleAccess.setIsActive(1);
									editorRoleAccessRepo.save(editorRoleAccess);
								}
							}
							add.setIsActive(1);
							add.setUpdatedDate(new Date());
							add.setUpdatedBy(userVO.getUpdatedBy());
							userJournalRepo.save(add);
						}
					}
				}
			}
		}

	}

	int ROLE_REVIEWER = 5;
	int ROLE_EDITOR = 3;
	int STATUS_REVIEW_ACCEPTED = 8;
	int STATUS_REVIEW_REMOVED_ACCEPTED = 36;
	int STATUS_REVIEW_REMOVED_OTHER = 60;
	public void updateUserDataOnRemoval(int journalId, int roleId, Integer userId) {
		log.info("Starting user removal process | journalId: {}, roleId: {}, userId: {}",journalId, roleId, userId);
		try {
			mailQueueRepo.deactivateUserForJournalAndRole(journalId, roleId, userId);
			log.debug("User deactivated in mail queue");

			if (roleId == ROLE_REVIEWER) {
				log.info("Role is reviewer. Updating manuscripts.");
				updateReviewerData(journalId, userId);
			}else if(roleId == ROLE_EDITOR){
				log.info("Role is editor. Updating manuscripts.");
				updateEditorData(journalId, userId);
			}

		} catch (Exception ex) {
			log.error("Error while removing user from journal | journalId: {}, userId: {}",journalId, userId, ex);
			throw ex; // rethrow to maintain transaction integrity
		}

		log.info("Completed user removal process | userId: {}", userId);
	}

	private void updateReviewerData(int journalId, int userId){
		List<AuthoritiesHistory> authDataList = authoritiesHistoryRepo.getAllReviewerAssignedManuscripts(journalId, userId);

		if (authDataList == null || authDataList.isEmpty()) {
			log.info("No manuscripts found for reviewer | userId: {}", userId);
			return;
		}

		log.info("Fetched {} manuscripts for processing", authDataList.size());

		List<Integer> status8Manuscripts = new ArrayList<>();
		List<Integer> otherManuscripts = new ArrayList<>();
		List<AuthoritiesHistory> historyToInsert = new ArrayList<>();

		for (AuthoritiesHistory auth : authDataList) {

			AuthoritiesHistory newRecord = new AuthoritiesHistory();

			boolean isStatus8 = auth.getManuscriptStatusId() == STATUS_REVIEW_ACCEPTED;

			int currentUser = isStatus8 ? auth.getPreviousUserId() : auth.getCurrentUserId();
			int currentRole = isStatus8 ? auth.getPreviousUserRole() : auth.getCurrentUserRole();
			int previousUser = isStatus8 ? auth.getCurrentUserId() : auth.getPreviousUserId();
			int previousRole = isStatus8 ? auth.getCurrentUserRole() : auth.getPreviousUserRole();

			int newStatus = isStatus8 ? STATUS_REVIEW_REMOVED_ACCEPTED : STATUS_REVIEW_REMOVED_OTHER;

			if (isStatus8) {
				status8Manuscripts.add(auth.getManuscriptId());
			} else {
				otherManuscripts.add(auth.getManuscriptId());
			}

			newRecord.setManuscriptId(auth.getManuscriptId());
			newRecord.setCurrentUserId(currentUser);
			newRecord.setCurrentUserRole(currentRole);
			newRecord.setPreviousUserId(previousUser);
			newRecord.setPreviousUserRole(previousRole);
			newRecord.setManuscriptVersion(auth.getManuscriptVersion());
			newRecord.setCommentsToEditor("Reviewer removed from this journal.");
			newRecord.setDueDate(auth.getDueDate());
			newRecord.setManuscriptStatusId(newStatus);

			historyToInsert.add(newRecord);
		}

		log.info("Prepared history records | status8: {}, others: {}", status8Manuscripts.size(), otherManuscripts.size());

		if (!status8Manuscripts.isEmpty()) {
			manuScriptReviewerRepo.updateManuscriptReviewerRejectStatus(journalId, userId, status8Manuscripts, STATUS_REVIEW_REMOVED_ACCEPTED);
		}

		if (!otherManuscripts.isEmpty()) {
			manuScriptReviewerRepo.updateManuscriptReviewerRejectStatus(journalId, userId, otherManuscripts, STATUS_REVIEW_REMOVED_OTHER);
		}

		authoritiesHistoryRepo.saveAll(historyToInsert);
		log.info("Inserted {} history records successfully", historyToInsert.size());
	}

	private void updateEditorData(int journalId, int userId) {

		log.info("Starting updateEditorData | journalId: {}, userId: {}", journalId, userId);

		List<AuthoritiesHistory> authDataList = authoritiesHistoryRepo.getAllEditorAssignedManuscripts(journalId, userId);

		if (authDataList == null || authDataList.isEmpty()) {
			log.warn("No manuscripts found for editor | journalId: {}, userId: {}", journalId, userId);
			return;
		}

		log.info("Fetched {} manuscripts for processing | journalId: {}, userId: {}", authDataList.size(), journalId, userId);

		// Update manuscripts before processing history
		manuscriptEditorRepo.updateSendBackEicForAllManuscripts(journalId, userId);
		log.info("Updated manuscripts to send back to EIC | journalId: {}, userId: {}", journalId, userId);

		List<AuthoritiesHistory> historyToInsert = new ArrayList<>();

		for (AuthoritiesHistory auth : authDataList) {

			int manuscriptId = auth.getManuscriptId();
			int previousUserId = auth.getCurrentUserId();
			int previousUserRole = auth.getCurrentUserRole();

			int currentUserId = auth.getPreviousUserId();
			int currentUserRole = 2; // Editor role
			int newStatus = 72;

			log.debug("Processing manuscriptId: {} | prevUser: {} | prevRole: {}",manuscriptId, previousUserId, previousUserRole);

			// If previous role was not Editor, fetch last assigned EIC
			if (auth.getPreviousUserRole() != 2) {
				int lastEicUserId = authoritiesHistoryRepo.getLastAssignedEIC(manuscriptId, userId);

				if (lastEicUserId > 0) {
					log.debug("Found last EIC {} for manuscriptId: {}", lastEicUserId, manuscriptId);
					currentUserId = lastEicUserId;
				} else {
					log.warn("No valid EIC found for manuscriptId: {}", manuscriptId);
				}
			}

			AuthoritiesHistory newRecord = new AuthoritiesHistory();
			newRecord.setManuscriptId(manuscriptId);
			newRecord.setCurrentUserId(currentUserId);
			newRecord.setCurrentUserRole(currentUserRole);
			newRecord.setPreviousUserId(previousUserId);
			newRecord.setPreviousUserRole(previousUserRole);
			newRecord.setManuscriptVersion(auth.getManuscriptVersion());
			newRecord.setCommentsToEIC("Editor removed from this journal.");
			newRecord.setDueDate(auth.getDueDate());
			newRecord.setManuscriptStatusId(newStatus);

			historyToInsert.add(newRecord);
		}

		log.info("Prepared {} history records for insertion | journalId: {}, userId: {}",historyToInsert.size(), journalId, userId);

		authoritiesHistoryRepo.saveAll(historyToInsert);

		log.info("Successfully inserted history records | count: {} | journalId: {}, userId: {}",historyToInsert.size(), journalId, userId);
	}
}
