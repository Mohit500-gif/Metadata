package com.digi.uniprr.utils;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digi.uniprr.VO.UserVO;
import com.digi.uniprr.model.Role;
import com.digi.uniprr.model.User;

public class UserTransformationalUtils {
	
	private static final Logger log =
	        LoggerFactory.getLogger(UserTransformationalUtils.class);
	
	public static User mapVOToModel(UserVO userVo) {
		
		User user = new User();
		
		user.setAccountExpired(userVo.getAccountExpired());
		user.setAccountLocked(userVo.getAccountLocked());
		user.setAddress(userVo.getAddress());
		user.setPubmedId(userVo.getPubmedId());
		user.setScopusId(userVo.getScopusId());
		user.setAfflication(userVo.getAfflication());
		user.setAcademic(userVo.getAcademic());
		user.setAddressThree(userVo.getAddressThree());
		user.setAddressTwo(userVo.getAddressTwo());
		user.setAreaOfInterest(userVo.getAreaOfInterest());
		user.setResearcherId(userVo.getResearcherId());
		user.setTrialRegistrationNo(userVo.getTrialRegistrationNo());
		user.setCity(userVo.getCity());
		user.setCountryId(userVo.getCountryId());
		user.setDepartment(userVo.getDepartment());
		user.setDesignation(userVo.getDesignation());
		user.setEmail(userVo.getEmail().trim());
		user.setEmailCC(userVo.getEmailCC());
		user.setEmailNotification(userVo.getEmailNotification());
		user.setExperience(userVo.getExperience());
		user.setExpertise(userVo.getExpertise());
		user.setFaxNumber(userVo.getFaxNumber());
		user.setFirstName(userVo.getFirstName());
		user.setId(userVo.getId());
		user.setInstitute(userVo.getInstitute());
		user.setIsActive(userVo.getIsActive());
		user.setLastName(userVo.getLastName());
		user.setMiddleName(userVo.getMiddleName());
		user.setMobileNumber(userVo.getMobileNumber());
		user.setNotificationFlag(userVo.getNotificationFlag());
		user.setOrcidId(userVo.getOrcidId());
		if(userVo.getPassword()!=null && userVo.getPassword()!="") {
			user.setPassword(userVo.getPassword().trim());
		}
		user.setPhoneNumber(userVo.getPhoneNumber());
		user.setPostalCode(userVo.getPostalCode());
		user.setPublicationName(userVo.getPublicationName());
		user.setSalutation(userVo.getSalutation());
		user.setSecondaryEmail(userVo.getSecondaryEmail());
		user.setSecondaryEmailCC(userVo.getSecondaryEmailCC());
		user.setSmsNotification(userVo.getSmsNotification());
		user.setMarketingNotification(userVo.getMarketingNotification());
		user.setProductNotification(userVo.getProductNotification());
		user.setSpecialization(userVo.getSpecialization());
		user.setState(userVo.getState());
		user.setToken(userVo.getToken());
		user.setUsername(userVo.getUsername());
		user.setUserType(userVo.getUserType());
		user.setSpecialRemark(userVo.getSpecialRemark());
		user.setFiveBestPapers(userVo.getFiveBestPapers());
		user.setUserQualification(userVo.getUserQualification());
		user.setNumberOfArticles(userVo.getNumberOfArticles());
		user.setEditorAccessDetails(userVo.getEditorAccessDetails());
		user.setCountryCode(userVo.getCountryCode());
		return user;
	}
	
	public static UserVO mapUserModelToUserVO(User user) {
		UserVO userVO = new UserVO();
		
		userVO.setId(user.getId());
		userVO.setOrcidId(user.getOrcidId());
		userVO.setTrialRegistrationNo(user.getTrialRegistrationNo());
		userVO.setResearcherId(user.getResearcherId());
		userVO.setPubmedId(user.getPubmedId());
		userVO.setScopusId(user.getScopusId());
		userVO.setAfflication(user.getAfflication());
		userVO.setAcademic(user.getAcademic());
		if(user.getCountries()!=null) {
		userVO.setCountry(user.getCountries().getName());
		}
		userVO.setUsername(user.getUsername());
		//userVO.setPassword(user.getPassword());
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
		userVO.setSpecialization(user.getSpecialization());
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
		userVO.setSmsNotification(user.getSmsNotification());
		userVO.setRole(RoleTransformationalUtils.mapRoleListToVOList(user.getRole()));
	    List<Integer> roleIds = new ArrayList<>();
	    if (user.getRole() != null) {
	        for (Role role : user.getRole()) {
	            roleIds.add(role.getRoleId());
	        }
	    }
	    userVO.setRoleId(roleIds);
		userVO.setGroup(GroupTransformationalUtils.mapGroupListToGroupVOList(user.getGroup()));
		userVO.setJournalVO(UserJournalListTransformationUtils.mapModelListToVoList(user.getUserJournal()));
		userVO.setSpecialRemark(user.getSpecialRemark());
		userVO.setFiveBestPapers(user.getFiveBestPapers());
		userVO.setUserQualification(user.getUserQualification());
		userVO.setNumberOfArticles(user.getNumberOfArticles());
		userVO.setEditorAccessDetails(user.getEditorAccessDetails());
		userVO.setCountryCode(user.getCountryCode());
		if(user.getEditorRoleAccess()!=null) {
			userVO.setEditorRoleId(user.getEditorRoleAccess().getEditorRoleId());
			List<String> data = new ArrayList();
			if(user.getEditorRoleAccess().getEditorAccess()!=null) {
				String str = user.getEditorRoleAccess().getEditorAccess();
				String [] strArray = str.split(",");
				for(String strData : strArray) {
					data.add(strData);
				}
			}
			userVO.setEditorRoleAccess(data);
		}
		return userVO;
	}
	
	public static List<User> mapUserVOListToUserList(List<UserVO> voList) {
		List<User> userList = new ArrayList<User>();
		for (UserVO userVO : voList) 
			userList.add(mapVOToModel(userVO));
		
		return userList;
	}
	
	public static List<UserVO> mapUserListToUserVOList(List<User> userList) {
		List<UserVO> voList = new ArrayList<UserVO>();
		for (User user : userList)
			voList.add(mapUserModelToUserVO(user));
		
		return voList;
	}
	
	public static List<UserVO> mapUserListToUserVOListData(List<User> userList) {
		List<UserVO> voList = new ArrayList<UserVO>();
		for (User user : userList)
			voList.add(mapUserModelToUserVOData(user));
		
		return voList;
	}
	
	public static List<UserVO> mapUserListToUserVOListDataForUser(List<User> userList, Integer journalId) {
		List<UserVO> voList = new ArrayList<UserVO>();
		for (User user : userList)
			voList.add(mapUserModelToUserVODataForUser(user,journalId));
		
		return voList;
	}


	public static UserVO mapUserModelToUserVOData(User user) {

	    UserVO userVO = new UserVO();

	    try {

	        userVO.setId(user.getId());
	        userVO.setOrcidId(user.getOrcidId());
	        userVO.setTrialRegistrationNo(user.getTrialRegistrationNo());
	        userVO.setResearcherId(user.getResearcherId());
	        userVO.setPubmedId(user.getPubmedId());
	        userVO.setScopusId(user.getScopusId());
	        userVO.setAfflication(user.getAfflication());
	        userVO.setAcademic(user.getAcademic());
	        userVO.setUsername(user.getUsername());
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

	        // Added logs for UserJournal
	        if (user.getUserJournal() == null) {
	            log.error(
	                    "UserJournal is NULL for UserId={}, Username={}, Email={}",
	                    user.getId(),
	                    user.getUsername(),
	                    user.getEmail());

	            userVO.setIsActive(0);
	        } else if (user.getUserJournal().isEmpty()) {
	            log.error(
	                    "UserJournal is EMPTY for UserId={}, Username={}, Email={}",
	                    user.getId(),
	                    user.getUsername(),
	                    user.getEmail());

	            userVO.setIsActive(0);
	        } else {
	            log.info(
	                    "UserJournal size={} for UserId={}",
	                    user.getUserJournal().size(),
	                    user.getId());

	            userVO.setIsActive(
	                    user.getUserJournal().get(0).getIsActive());
	        }

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
	        userVO.setSmsNotification(user.getSmsNotification());

	        userVO.setRole(
	                UserRoleTransformationalUtils
	                        .mapRoleListToVOList1(user.getUserRole()));

	        userVO.setGroup(
	                GroupTransformationalUtils
	                        .mapGroupListToGroupVOList(user.getGroup()));

	        userVO.setJournalVO(
	                UserJournalListTransformationUtils
	                        .mapModelListToVoList(user.getUserJournal()));

	        userVO.setSpecialRemark(user.getSpecialRemark());
	        userVO.setFiveBestPapers(user.getFiveBestPapers());
	        userVO.setUserQualification(user.getUserQualification());
	        userVO.setNumberOfArticles(user.getNumberOfArticles());
	        userVO.setCountryCode(user.getCountryCode());

	        if (user.getEditorRoleAccess() != null) {
	            userVO.setEditorRoleId(
	                    user.getEditorRoleAccess().getEditorRoleId());

	            List<String> data = new ArrayList<>();

	            if (user.getEditorRoleAccess().getEditorAccess() != null) {
	                String str =
	                        user.getEditorRoleAccess().getEditorAccess();

	                String[] strArray = str.split(",");

	                for (String strData : strArray) {
	                    data.add(strData);
	                }
	            }

	            userVO.setEditorRoleAccess(data);
	        }

	        userVO.setSearchArticleStatus(user.getSearchArticleStatus());

	        if (user.getIsActive() == 0) {
	            userVO.setSomeUserStatus(1);
	        }

	    } catch (Exception e) {
	        log.error(
	                "Error while mapping User to UserVO. UserId={}, Username={}, Email={}",
	                user != null ? user.getId() : null,
	                user != null ? user.getUsername() : null,
	                user != null ? user.getEmail() : null,
	                e);
	        throw e;
	    }

	    return userVO;
	}
	
	public static UserVO mapUserModelToUserVODataForUser(User user, Integer journalId) {
		UserVO userVO = new UserVO();
		
		userVO.setId(user.getId());
		userVO.setOrcidId(user.getOrcidId());
		userVO.setTrialRegistrationNo(user.getTrialRegistrationNo());
		userVO.setResearcherId(user.getResearcherId());
		userVO.setPubmedId(user.getPubmedId());
		userVO.setScopusId(user.getScopusId());
		userVO.setAfflication(user.getAfflication());
		userVO.setAcademic(user.getAcademic());
	
		userVO.setUsername(user.getUsername());
		//userVO.setPassword(user.getPassword());
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
		userVO.setSmsNotification(user.getSmsNotification());
		userVO.setRole(UserRoleTransformationalUtils.mapRoleListToVOList(user.getUserJournal(),journalId));
		userVO.setGroup(GroupTransformationalUtils.mapGroupListToGroupVOList(user.getGroup()));
		userVO.setJournalVO(UserJournalListTransformationUtils.mapModelListToVoListForUser(user.getUserJournal(), journalId));
		userVO.setSpecialRemark(user.getSpecialRemark());
		userVO.setFiveBestPapers(user.getFiveBestPapers());
		userVO.setUserQualification(user.getUserQualification());
		userVO.setSearchArticleStatus(user.getSearchArticleStatus());
		userVO.setEditorAccessDetails(user.getEditorAccessDetails());
		userVO.setCountryCode(user.getCountryCode());
		if(user.getEditorRoleAccess()!=null) {
			userVO.setEditorRoleId(user.getEditorRoleAccess().getEditorRoleId());
			List<String> data = new ArrayList();
			if(user.getEditorRoleAccess().getEditorAccess()!=null) {
				String str = user.getEditorRoleAccess().getEditorAccess();
				String [] strArray = str.split(",");
				for(String strData : strArray) {
					data.add(strData);
				}
			}
			userVO.setEditorRoleAccess(data);
		}
		if(user.getIsActive()==0) {
			userVO.setSomeUserStatus(1);
		}
		return userVO;
	}

}
