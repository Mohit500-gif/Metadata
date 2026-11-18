package com.digi.uniprr.service;

import java.util.List;

import com.digi.uniprr.VO.UserDetailsListVO;
import org.springframework.data.domain.Pageable;

import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.VO.LoggedUserVo;
import com.digi.uniprr.VO.ManuscriptListVO;
import com.digi.uniprr.VO.ReviewerFilterVO;
import com.digi.uniprr.VO.ReviewerVO;
import com.digi.uniprr.VO.UpdatePassword;
import com.digi.uniprr.VO.UserExportVo;
import com.digi.uniprr.VO.UserVO;
import com.digi.uniprr.model.User;

public interface UserService {

	public void addUser(User user);

	public Iterable<User> getUserDetail(User user);

	void updateUser(UserVO userVO);

	User getByUsername(String username);

	public User getByEmail(String email);

	public boolean isUserExist(User user);

	public List<User> getGroupByUserId(Integer id);

	public User createUser(UserVO userVo);

	public void addNewUserRole(UserVO userVo);

	public List<UserVO> getUserDetails();

	public List<User> getUserListById(Integer id);

	public String deleteUserById(IDVO id);

	public User getById(Integer id);
	
	public UserVO getUserById(Integer id);

	public UserVO getUserByEmailAndPassword(String email);

	public List<UserVO> getUserListByRoleId(IDVO id);

	public void assignUser(ManuscriptListVO manuscriptListVO) throws Exception;

	public void activateLockedUser(UserVO userVO);
	
	/*******Reviewer Filter********/
	public List<ReviewerVO> reviewerFilter(ReviewerFilterVO vo);
	
	public String updatePassword(UpdatePassword updatePassword);
	
	/**************User Logged****************/
	
	public String logedUser(LoggedUserVo loggedUserVo);
	
	public List<LoggedUserVo> getLogedUserDetails(LoggedUserVo loggedUserVo);

	public List<UserVO> getUserByRoleId(IDVO id);
	
	public List<UserVO> getLockedUser(IDVO id);
	
	/***** Aprroved ****/
	
	User joinReviewer(UserVO userVo);

	List<User> getUserDetailsenabledByEditor();

	public void approvedByEditor(Integer id);

	public List<User> getlistUserMakedReviewer();

	public void approvedByEditorMakeReviewer(Integer id);

	public void applyAsReviewer(Integer id, Integer journalId);

	public boolean getApplyForReviewer(Integer id);
	
	public String validateAuthorSignUp(IDVO id);
	
	/**************Orcid ID Authenticate*****************/
	public UserVO getUserByOrcidId(String orcidID);
	
	public UserVO getUserByEmailId(String email);
	
	public UserVO loginAuthenticateByRole(IDVO id);
	
	public String userVerification(IDVO id);
	
	public String userVerificationUpdationPassword(IDVO id);

	public List<UserVO> searchUser(IDVO id);

	public List<String> getSpecialization(IDVO id);

	public List<String> getExpertise(IDVO id);

	public List<UserVO> reviewerRefineSearch(IDVO id);
	
	public int getUserCountByJournalAndRole(IDVO id);
	
	String sendMailForAuthorAssociatedWithJournal(String userEmail, String userName,String journalActivationUrl,String journalTitle,Integer userId,Integer journalId);

	public String userActivation(IDVO id);

	public List<UserVO> searchActiveUser(IDVO id);

	public List<UserVO> getUserByJournalAndRole(IDVO id);

	public UserVO getByEmailVO(String email);

	public List<UserVO> getUniqueUserByRoleId(IDVO id);

	public String authorRegistrationMailSend(UserVO userVO);

	public void updateUserJournal(UserVO userVO);

	public List<UserVO> getAllUserByJournalAndRole(IDVO id);
	
	public List<UserExportVo> exportAllUserByJournalAndRole(IDVO id);

	public List<UserVO> getAuthorListByJournal(IDVO id, Pageable paging);

	Integer getTotalSearchCountAuthorListByJournal(Integer roleId, Integer journalId, Integer isActive, String seach);

	Integer getTotalCountAuthorListByJournal(Integer roleId, Integer journalId, Integer isActive);
	
	public List<UserVO> getAllUserByJournalAndRoleAndPage(IDVO id, Pageable paging);

	public List<UserVO> searchUserByEmail(IDVO id);

	public String eicChangeRequest(IDVO iDVO);

	public String activateCurrentUser(IDVO iDVO);

	public IDVO getUserCountOnAdmin();

	public List<UserVO> getUserDetailsByJournalAndRole(IDVO iDVO);

	public Integer getCount(IDVO id);

	public User loginUser(UserVO userVo);

	public UserVO userDetailsVo(User rowData);

	public List<UserVO> getInactiveUsers(IDVO id, Pageable paging);

	public void updateInactiveUser(UserVO userVO);

	public Integer getInactiveUserCount(IDVO id);

    public UserDetailsListVO getAllUsersListByJournalAndRole(IDVO id);

    public void updateEditorAccess(User user);
    
    public List<UserVO> getUsersByOrcidId(String orcidID);

    void updateUserDataOnRemoval(int currJournalId, int roleId, Integer id);

}
