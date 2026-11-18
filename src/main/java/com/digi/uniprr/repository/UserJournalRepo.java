package com.digi.uniprr.repository;

import java.util.Date;
import java.util.List;

import javax.persistence.Tuple;
import javax.transaction.Transactional;

import com.digi.uniprr.VO.ManuScriptReviewerCommonVO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.model.UserJournal;
@Repository
@Transactional
public interface UserJournalRepo extends JpaRepository<UserJournal, Integer> {
	
    List<UserJournal> findByUserId(Integer userId);
    List<UserJournal> findByUserIdAndJournalId(Integer userId, Integer journalId);

	@Query(value = "SELECT * from users u LEFT JOIN user_role ur ON ur.usr_id=u.id LEFT JOIN users_journals uj ON uj.user_id= u.id WHERE ur.role_id=:roleId AND ur.is_active=1 AND uj.journal_id=:journalId AND uj.is_active=1", nativeQuery = true)
	List<UserJournal> getListByjournaId(Integer journalId, Integer roleId);

	@Query("Select uj from UserJournal uj where uj.userId=:id")
	List<UserJournal> getUserJournalByUserId(Integer id);
	
	@Query("Select distinct uj.roleId from UserJournal uj where uj.userId=:id and journalId=:jid and isActive=1")
	List<Integer> getUserRolesByUserId(Integer id, Integer jid);
	
	@Query("Select uj from UserJournal uj where uj.userId=:id AND uj.isActive=1 GROUP BY uj.journalId")
	List<UserJournal> getUserJournalByUserIdActive(Integer id);

	@Query("SELECT uj from UserJournal uj where uj.userId =:userId AND uj.journalId =:journalId AND uj.isActive=1")
	UserJournal getUserJournalByJournal(Integer userId, Integer journalId);
	
	@Query("SELECT uj from UserJournal uj where uj.userId =:userId AND uj.journalId =:journalId AND uj.isActive=1")
	List<UserJournal> getUsersJournalByJournal(Integer userId, Integer journalId);
	
	@Query("SELECT uj from UserJournal uj where uj.userId =:userId AND uj.journalId =:journalId AND uj.roleId=:roleId")
	UserJournal getUserJournalByJournalEntry(Integer userId, Integer journalId, Integer roleId);
	
	@Query("SELECT uj from UserJournal uj where uj.userId =:userId AND uj.journalId =:journalId")
	UserJournal getUserJournalByJournalUNique(Integer userId, Integer journalId);
	
	@Query("SELECT uj from UserJournal uj where uj.userId =:userId AND uj.journalId =:journalId AND uj.isActive=1")
	List<UserJournal> getUserJournalByJournalUNiqueList(Integer userId, Integer journalId);
	
	@Query("SELECT uj from UserJournal uj where uj.userId =:userId AND uj.journalId =:journalId AND uj.isActive=0")
	UserJournal activateUserJournal(Integer userId, Integer journalId);

	@Query("DELETE FROM UserJournal uj WHERE uj.userId=:userId")
	@Modifying
	void deleteUserJournal(Integer userId);

	@Query("Select uj from UserJournal uj where uj.journalId IN :journalId")
	List<UserJournal> getListOfUserJournalByJournalsId(List<Integer> journalId);
	
	@Query("Select COUNT(uj.id) from UserJournal uj where uj.journalId IN :journalId AND YEAR(uj.updatedDate) IN :year AND uj.roleId=:roleId AND uj.isActive=1")
	Integer getListOfUserJournalByJournalId(List<Integer> journalId,List<Integer> year,Integer roleId);

	@Query("Select uj from UserJournal uj where uj.userId IN :userId AND uj.journalId IN :journalId")
	List<UserJournal> getListOfUser(List<Integer> userId, List<Integer> journalId);
	
	@Query("Select uj from UserJournal uj where uj.journalId=:journalId AND uj.isActive=1")
	List<UserJournal> getAllUserJournalList(Integer journalId);
	
	@Query("Select uj from UserJournal uj where uj.journalId=:journalId AND uj.isActive=1 AND uj.roleId=2")
	List<UserJournal> getEICJournalList(Integer journalId);

    //@Query("Select uj from UserJournal uj where uj.journalId=:journalId AND uj.roleId=5 AND uj.isActive=1")
	//@Query("SELECT uj from UserJournal uj where uj.journalId=:journalId AND uj.isActive=1 AND uj.roleId=5  ORDER BY uj.user")
	@Query(value = "SELECT * from users_journals uj where uj.journal_id=:journalId AND uj.is_active=1 AND uj.role_id=5 ORDER BY uj.user_id LIMIT 500", nativeQuery = true)
	List<UserJournal> getAllReviewerList(Integer journalId);

	@Query("UPDATE UserJournal uj set uj.isActive=0 where uj.journalId=:journalId AND uj.userId IN :userIdData")
	@Modifying
	void deactivateAssignUser(Integer journalId, List<Integer> userIdData);
	
	@Query("UPDATE UserJournal uj set uj.isActive=0 where uj.userId =:userIdData")
	@Modifying
	void deactivateUser(Integer userIdData);

	@Query("Select uj from UserJournal uj where uj.userId =:id AND uj.journalId =:journal")
	UserJournal updateUserJournal(Integer id, Integer journal);
	
	@Query("Select uj from UserJournal uj where uj.journalId =:journalId AND uj.roleId =:roleId AND uj.isActive=1 ORDER  BY uj.id")
	List<UserJournal> getUserRoleJournalId(Integer journalId, Integer roleId);

	@Query("Select uj from UserJournal uj where uj.userId =:userId AND uj.journalId =:journal AND uj.roleId=:roleId")
	UserJournal getUserJournal(Integer userId, Integer journal,Integer roleId);
	
	@Query("Select uj from UserJournal uj where uj.userId =:userId AND uj.roleId=:roleId and uj.journalId=:journalId and isActive=1")
	UserJournal getUserRole(Integer userId,Integer roleId, Integer journalId);
	
	@Query(value="Select * from users_journals uj where uj.user_id =:userId AND uj.journal_id =:journal AND uj.role_id=:roleId", nativeQuery = true)
	UserJournal getUserJournalInfo(Integer userId, Integer journal,Integer roleId);
	
	@Modifying
	//@Query("UPDATE UserJournal ur SET ur.isActive =0,ur.updatedBy=:updateBy,ur.updatedDate=:updateDate, ur.reasonForDelete=:Comment  WHERE ur.userId=:userId AND ur.journalId =:journalId AND ur.roleId=:roleId")
	@Query("UPDATE UserJournal ur SET ur.isActive =0 WHERE ur.userId=:userId AND ur.journalId =:journalId")
	void disableUserJournalRole(Integer userId,Integer journalId);
	
	@Modifying
	@Query("UPDATE UserJournal ur SET ur.isActive =0,ur.updatedBy=:updateBy,ur.updatedDate=:updateDate, ur.reasonForDelete=:Comment  WHERE ur.userId=:userId AND ur.journalId =:journalId AND ur.roleId=:roleId")
	void disableUserJournalRoleFromUserList(Integer userId,Integer journalId, Integer updateBy , Integer roleId , Date updateDate, String Comment);
	
	@Query(value="Select uj.user_id from users_journals uj where uj.journal_id=:journalId AND uj.user_id NOT IN :userId AND uj.role_id=5 AND uj.is_active=1",nativeQuery = true)
	List<Integer> getAllReviewerIdList(Integer journalId, Integer userId);
	
	@Query(value="Select uj.user_id,uj.journal_id from users_journals uj INNER JOIN users u ON u.id=uj.user_id where uj.role_id IN(2,3) AND uj.is_active=1 AND u.is_active=1",nativeQuery = true)
	List<Tuple> getAllEditorList();
	
	@Query(value="Select uj.user_id from users_journals uj where uj.journal_id=:journalId AND uj.role_id=5 AND uj.user_id NOT IN :userId AND uj.is_active=1",nativeQuery = true)
	List<Integer> getAllReviewerList(Integer journalId, List<Integer> userId);
	
	@Query(value="SELECT COUNT(u.id)  FROM users_journals u WHERE u.journal_id=:journalId AND u.role_id=:roleId AND u.is_active=1;",nativeQuery = true)
	Integer getCountOfUser(Integer journalId,Integer roleId);

	@Query(value="SELECT COUNT(u.id)  FROM users_journals u WHERE u.role_id=:roleId AND u.is_active=1;",nativeQuery = true)
	Integer getUserCountONAllJournal(Integer roleId);
	
	@Query(value="SELECT COUNT(u.id)  FROM users_journals u WHERE u.user_id =:userId AND u.is_active=1;",nativeQuery = true)
	Integer getAllActiveUser(Integer userId);
	
	@Query("Select uj from UserJournal uj where uj.userId =:userId AND uj.journalId =:journal AND uj.roleId=:roleId AND uj.isActive=1")
	UserJournal getUserJournalForRefineSearch(Integer userId, Integer journal,Integer roleId);
	
	@Query(value = "SELECT uj.* from users_journals uj INNER JOIN users u ON u.id=uj.user_id where uj.journal_id=:journalId AND uj.is_active=1 AND u.is_active=1 AND uj.role_id=:roleId AND uj.user_id NOT IN(SELECT r.reviewer_id  FROM manuscript_reviewer r WHERE r.manuscript_id=:manuscriptId  AND r.manuscript_status IN (36,60,12,13,14,15))", nativeQuery = true)
	List<UserJournal> getDatabaseSuggestedReviewer(Integer journalId, Integer manuscriptId,Integer roleId, Pageable paging);
	
	@Query(value = "SELECT uj.* from users_journals uj INNER JOIN users u ON u.id=uj.user_id where uj.journal_id=:journalId AND uj.is_active=1 AND u.is_active=1 AND uj.role_id=:roleId AND u.specialization=:specialization AND u.country_id=:countryId AND uj.user_id NOT IN(SELECT r.reviewer_id  FROM manuscript_reviewer r WHERE r.manuscript_id=:manuscriptId  AND r.manuscript_status  IN (36,60,12,13,14,15))", nativeQuery = true)
	List<UserJournal> getDatabaseSuggestedReviewerSpecialization(Integer journalId, Integer manuscriptId, String specialization, Integer countryId,Integer roleId,  Pageable paging);

	@Query(value = "SELECT uj.* from users_journals uj INNER JOIN users u ON u.id=uj.user_id where uj.journal_id=:journalId AND uj.is_active=1 AND u.is_active=1 AND uj.role_id=:roleId AND u.specialization=:specialization AND u.country_id=:countryId AND u.expertise=:expertise AND uj.user_id NOT IN(SELECT r.reviewer_id  FROM manuscript_reviewer r WHERE r.manuscript_id=:manuscriptId AND  r.manuscript_status IN (36,60,12,13,14,15))", nativeQuery = true)
	List<UserJournal> getDatabaseSuggestedReviewerSpecializationExperties(Integer journalId, Integer manuscriptId, String specialization, Integer countryId, String expertise,Integer roleId,  Pageable paging);

	@Query(value = "SELECT uj.* from users_journals uj INNER JOIN users u ON u.id=uj.user_id where uj.journal_id=:journalId AND uj.is_active=1 AND u.is_active=1 AND uj.role_id=:roleId AND (u.user_email=:email OR u.specialization=:specialization OR u.expertise=:expertise OR CONCAT(TRIM(u.first_name),' ',TRIM(u.last_name)) LIKE %:name% OR u.first_name LIKE %:name% OR u.last_name LIKE %:name% OR u.country_id=:countryId) AND uj.user_id NOT IN(SELECT r.reviewer_id  FROM manuscript_reviewer r WHERE r.manuscript_id=:manuscriptId AND r.manuscript_status IN (36,60,12,13,14,15))", nativeQuery = true)
	List<UserJournal> getDatabaseSuggestedReviewerSearchAny(Integer journalId, Integer manuscriptId, String specialization, Integer countryId, String expertise, String name, String email,Integer roleId, Pageable paging);

	
	@Query(value = "SELECT COUNT(*) from users_journals uj INNER JOIN users u ON u.id=uj.user_id where uj.journal_id=:journalId AND uj.is_active=1 AND u.is_active=1 AND u.id NOT IN(:authorId) AND uj.role_id=:roleId AND uj.user_id NOT IN(SELECT r.reviewer_id  FROM manuscript_reviewer r WHERE r.manuscript_id=:manuscriptId  AND r.manuscript_status IN (36,60,12,13,14,15))", nativeQuery = true)
	Integer getDatabaseSuggestedReviewerCount(Integer journalId, Integer manuscriptId, Integer authorId,Integer roleId);
	
	@Query(value = "SELECT COUNT(*) from users_journals uj INNER JOIN users u ON u.id=uj.user_id where uj.journal_id=:journalId AND uj.is_active=1 AND u.is_active=1 AND u.id NOT IN(:authorId) AND uj.role_id=:roleId AND u.specialization=:specialization AND u.country_id=:countryId AND uj.user_id NOT IN(SELECT r.reviewer_id  FROM manuscript_reviewer r WHERE r.manuscript_id=:manuscriptId  AND r.manuscript_status IN (36,60,12,13,14,15))", nativeQuery = true)
	Integer getDatabaseSuggestedReviewerSpecializationCount(Integer journalId, Integer manuscriptId, String specialization, Integer countryId,Integer authorId,Integer roleId);

	@Query(value = "SELECT COUNT(*) from users_journals uj INNER JOIN users u ON u.id=uj.user_id where uj.journal_id=:journalId AND uj.is_active=1 AND u.is_active=1 AND u.id NOT IN(:authorId) AND uj.role_id=:roleId AND u.specialization=:specialization AND u.country_id=:countryId AND u.expertise=:expertise AND uj.user_id NOT IN(SELECT r.reviewer_id  FROM manuscript_reviewer r WHERE r.manuscript_id=:manuscriptId AND r.manuscript_status IN (36,60,12,13,14,15))", nativeQuery = true)
	Integer getDatabaseSuggestedReviewerSpecializationExpertiesCount(Integer journalId, Integer manuscriptId, String specialization, Integer countryId, String expertise, Integer authorId,Integer roleId);

	@Query(value = "SELECT COUNT(*) from users_journals uj INNER JOIN users u ON u.id=uj.user_id where uj.journal_id=:journalId AND uj.is_active=1 AND u.is_active=1 AND u.id NOT IN(:authorId) AND uj.role_id=:roleId AND (u.user_email=:email OR u.specialization=:specialization OR u.expertise=:expertise OR CONCAT(TRIM(u.first_name),' ',TRIM(u.last_name)) LIKE %:name% OR u.first_name LIKE %:name% OR u.last_name LIKE %:name% OR u.country_id=:countryId) AND uj.user_id NOT IN(SELECT r.reviewer_id  FROM manuscript_reviewer r WHERE r.manuscript_id=:manuscriptId AND r.manuscript_status IN (36,60,12,13,14,15))", nativeQuery = true)
	Integer getDatabaseSuggestedReviewerSearchAnyCount(Integer journalId, Integer manuscriptId, String specialization, Integer countryId, String expertise, String name, String email, Integer authorId,Integer roleId);


	@Query(value = "SELECT uj.* from users_journals uj INNER JOIN users u ON u.id=uj.user_id where uj.journal_id=:journalId AND uj.is_active=1 AND u.is_active=1 AND uj.role_id=:roleId AND TRIM(u.first_name) LIKE :alphabet% AND uj.user_id NOT IN(SELECT r.reviewer_id  FROM manuscript_reviewer r WHERE r.manuscript_id=:manuscriptId AND r.manuscript_status IN (36,60,12,13,14,15))", nativeQuery = true)
	List<UserJournal> getDatabaseSuggestedReviewerAlphabet(Integer journalId, Integer manuscriptId,String alphabet, Integer roleId,Pageable paging);
	
	@Query(value = "SELECT uj.* from users_journals uj INNER JOIN users u ON u.id=uj.user_id where uj.journal_id=:journalId AND uj.is_active=1 AND u.is_active=1 AND uj.role_id=:roleId AND u.specialization=:specialization AND u.country_id=:countryId AND TRIM(u.first_name) LIKE :alphabet% AND uj.user_id NOT IN(SELECT r.reviewer_id  FROM manuscript_reviewer r WHERE r.manuscript_id=:manuscriptId  AND r.manuscript_status IN (36,60,12,13,14,15))", nativeQuery = true)
	List<UserJournal> getDatabaseSuggestedReviewerSpecializationAlphabet(Integer journalId, Integer manuscriptId, String specialization, Integer countryId, String alphabet,Integer roleId, Pageable paging);

	@Query(value = "SELECT uj.* from users_journals uj INNER JOIN users u ON u.id=uj.user_id where uj.journal_id=:journalId AND uj.is_active=1 AND u.is_active=1 AND uj.role_id=:roleId AND u.specialization=:specialization AND u.country_id=:countryId AND u.expertise=:expertise AND TRIM(u.first_name) LIKE :alphabet% AND uj.user_id NOT IN(SELECT r.reviewer_id  FROM manuscript_reviewer r WHERE r.manuscript_id=:manuscriptId AND r.manuscript_status IN (36,60,12,13,14,15))", nativeQuery = true)
	List<UserJournal> getDatabaseSuggestedReviewerSpecializationExpertiesAlphabet(Integer journalId, Integer manuscriptId, String specialization, Integer countryId, String expertise, String alphabet,Integer roleId, Pageable paging);

	@Query(value = "SELECT uj.* from users_journals uj INNER JOIN users u ON u.id=uj.user_id where uj.journal_id=:journalId AND uj.is_active=1 AND u.is_active=1 AND uj.role_id=:roleId AND (u.user_email=:email OR u.specialization=:specialization OR u.expertise=:expertise OR CONCAT(TRIM(u.first_name),' ',TRIM(u.last_name)) LIKE %:name% OR u.first_name LIKE %:name% OR u.last_name LIKE %:name% OR u.country_id=:countryId) AND TRIM(u.first_name) LIKE :alphabet% AND uj.user_id NOT IN(SELECT r.reviewer_id  FROM manuscript_reviewer r WHERE r.manuscript_id=:manuscriptId AND r.manuscript_status IN (36,60,12,13,14,15))", nativeQuery = true)
	List<UserJournal> getDatabaseSuggestedReviewerSearchAnyAlphabet(Integer journalId, Integer manuscriptId, String specialization, Integer countryId, String expertise, String name, String email, String alphabet,Integer roleId, Pageable paging);

	
	@Query(value = "SELECT COUNT(*) from users_journals uj INNER JOIN users u ON u.id=uj.user_id where uj.journal_id=:journalId AND uj.is_active=1 AND u.is_active=1 AND u.id NOT IN(:authorId) AND uj.role_id=:roleId AND TRIM(u.first_name) LIKE :alphabet% AND uj.user_id NOT IN(SELECT r.reviewer_id  FROM manuscript_reviewer r WHERE r.manuscript_id=:manuscriptId  AND r.manuscript_status IN (36,60,12,13,14,15))", nativeQuery = true)
	Integer getDatabaseSuggestedReviewerCountAlphabet(Integer journalId, Integer manuscriptId, String alphabet, Integer authorId,Integer roleId);
	
	@Query(value = "SELECT COUNT(*) from users_journals uj INNER JOIN users u ON u.id=uj.user_id where uj.journal_id=:journalId AND uj.is_active=1 AND u.is_active=1 AND u.id NOT IN(:authorId) AND uj.role_id=:roleId AND u.specialization=:specialization AND u.country_id=:countryId AND TRIM(u.first_name) LIKE :alphabet% AND uj.user_id NOT IN(SELECT r.reviewer_id  FROM manuscript_reviewer r WHERE r.manuscript_id=:manuscriptId  AND r.manuscript_status IN (36,60,12,13,14,15))", nativeQuery = true)
	Integer getDatabaseSuggestedReviewerSpecializationCountAlphabet(Integer journalId, Integer manuscriptId, String specialization, String alphabet,Integer countryId, Integer authorId,Integer roleId);

	@Query(value = "SELECT COUNT(*) from users_journals uj INNER JOIN users u ON u.id=uj.user_id where uj.journal_id=:journalId AND uj.is_active=1 AND u.is_active=1 AND u.id NOT IN(:authorId) AND uj.role_id=:roleId AND u.specialization=:specialization AND u.country_id=:countryId AND u.expertise=:expertise AND TRIM(u.first_name) LIKE :alphabet% AND uj.user_id NOT IN(SELECT r.reviewer_id  FROM manuscript_reviewer r WHERE r.manuscript_id=:manuscriptId AND r.manuscript_status IN (36,60,12,13,14,15))", nativeQuery = true)
	Integer getDatabaseSuggestedReviewerSpecializationExpertiesCountAlphabet(Integer journalId, Integer manuscriptId, String specialization, Integer countryId, String expertise, String alphabet, Integer authorId,Integer roleId);

	@Query(value = "SELECT COUNT(*) from users_journals uj INNER JOIN users u ON u.id=uj.user_id where uj.journal_id=:journalId AND uj.is_active=1 AND u.is_active=1 AND u.id NOT IN(:authorId) AND uj.role_id=:roleId AND (u.user_email=:email OR u.specialization=:specialization OR u.expertise=:expertise OR CONCAT(TRIM(u.first_name),' ',TRIM(u.last_name)) LIKE %:name% OR u.first_name LIKE %:name% OR u.last_name LIKE %:name% OR u.country_id=:countryId) AND TRIM(u.first_name) LIKE :alphabet% AND uj.user_id NOT IN(SELECT r.reviewer_id  FROM manuscript_reviewer r WHERE r.manuscript_id=:manuscriptId  AND r.manuscript_status IN (36,60,12,13,14,15))", nativeQuery = true)
	Integer getDatabaseSuggestedReviewerSearchAnyCountAlphabet(Integer journalId, Integer manuscriptId, String specialization, Integer countryId, String expertise, String name, String email, String alphabet, Integer authorId,Integer roleId);

	@Query(value = "SELECT uj.* from users_journals uj INNER JOIN users u ON u.id=uj.user_id INNER JOIN countries cu ON cu.id=u.country_id where uj.journal_id=:journalId AND uj.is_active=1 AND u.is_active=1 AND uj.role_id=:roleId AND (u.user_email LIKE %:search% OR u.specialization LIKE %:search% OR u.expertise LIKE %:search% OR CONCAT(TRIM(u.first_name),' ',TRIM(u.last_name)) LIKE %:search% OR u.first_name LIKE %:search% OR u.last_name LIKE %:search% OR cu.name LIKE %:search%) AND uj.user_id NOT IN(SELECT r.reviewer_id  FROM manuscript_reviewer r WHERE r.manuscript_id=:manuscriptId AND  r.manuscript_status IN (36,60,12,13,14,15))", nativeQuery = true)
	List<UserJournal> getGlobalSearchReviewer(Integer roleId, Integer journalId, Integer manuscriptId, String search, Pageable paging);

	@Query(value = "SELECT COUNT(*) from users_journals uj INNER JOIN users u ON u.id=uj.user_id INNER JOIN countries cu ON cu.id=u.country_id where uj.journal_id=:journalId AND uj.is_active=1 AND u.is_active=1 AND uj.role_id=:roleId AND (u.user_email LIKE %:search% OR u.specialization LIKE %:search% OR u.expertise LIKE %:search% OR CONCAT(TRIM(u.first_name),' ',TRIM(u.last_name)) LIKE %:search% OR u.first_name LIKE %:search% OR u.last_name LIKE %:search% OR cu.name LIKE %:search%) AND uj.user_id NOT IN(SELECT r.reviewer_id  FROM manuscript_reviewer r WHERE r.manuscript_id=:manuscriptId AND  r.manuscript_status IN (36,60,12,13,14,15))", nativeQuery = true)
	Long countGlobalSearchReviewer(Integer roleId, Integer journalId, Integer manuscriptId, String search);
	
	@Query(value = "SELECT COUNT(*) from users_journals uj INNER JOIN users u ON u.id=uj.user_id INNER JOIN countries cu ON cu.id=u.country_id where uj.journal_id=:journalId AND uj.is_active=1 AND u.is_active=1 AND uj.role_id=5 AND (u.user_email LIKE %:search% OR u.specialization LIKE %:search% OR u.expertise LIKE %:search% OR CONCAT(TRIM(u.first_name),' ',TRIM(u.last_name)) LIKE %:search% OR u.first_name LIKE %:search% OR u.last_name LIKE %:search% OR cu.name LIKE %:search%) AND uj.user_id NOT IN(SELECT r.reviewer_id  FROM manuscript_reviewer r WHERE r.manuscript_id=:manuscriptId AND  r.manuscript_status IN (36,60,12,13,14,15))", nativeQuery = true)
	Integer getGlobalSearchReviewerCount(Integer journalId, Integer manuscriptId, String search);

	@Query("SELECT uj from UserJournal uj where uj.userId =:userId AND uj.journalId =:journalId AND uj.roleId =:roleId")
	UserJournal getUsersJournalDetailsByJournal(Integer userId, Integer journalId, Integer roleId);
	
	@Query(value ="select * from users u, users_journals uj where user_email =:email and u.id = uj.user_id and uj.journal_id IN :journalId and uj.role_id IN :roleIds " , nativeQuery = true)
	UserJournal existsByEmail(String email , List<Integer> journalId, List<Integer> roleIds);

	@Query(value ="select uj.is_Active from users u, users_journals uj where user_email =:email and u.id = uj.user_id and uj.journal_id IN :journalId and uj.role_id IN :roleIds " , nativeQuery = true)
	Integer existsByEmailActiveUser(String email , List<Integer> journalId, List<Integer> roleIds);

	@Query(value = "SELECT uj.* from users_journals uj INNER JOIN users u ON u.id=uj.user_id where uj.journal_id=:journalId AND uj.is_active=1 AND u.is_active=1 AND uj.role_id=5 AND u.user_email=:email", nativeQuery = true)
	List<UserJournal> getReviewerIdByEmail(Integer journalId, String email);

	@Query(value = "SELECT uj.* from users_journals uj INNER JOIN users u ON u.id=uj.user_id where uj.journal_id=:journalId AND uj.is_active=1 AND u.is_active=1 AND uj.role_id=5 AND u.specialization=:specialization AND u.expertise=:expertise AND uj.user_id NOT IN(SELECT r.reviewer_id  FROM manuscript_reviewer r WHERE r.manuscript_id=:manuscriptId  AND r.manuscript_status IN (36,60,12,13,14,15))", nativeQuery = true)
	List<UserJournal> getDatabaseSuggestedReviewerSearchSpeAndSubSpe(Integer journalId, Integer manuscriptId, String specialization, String expertise,  Pageable paging);

	@Query(value = "SELECT uj.* from users_journals uj INNER JOIN users u ON u.id=uj.user_id where uj.journal_id=:journalId AND uj.is_active=1 AND u.is_active=1 AND uj.role_id=5 AND u.specialization=:specialization  AND u.expertise=:expertise AND TRIM(u.first_name) LIKE :alphabet% AND uj.user_id NOT IN(SELECT r.reviewer_id  FROM manuscript_reviewer r WHERE r.manuscript_id=:manuscriptId AND r.manuscript_status IN (36,60,12,13,14,15))", nativeQuery = true)
	List<UserJournal> getDatabaseSuggestedReviewerSpecializationExpertiesAl(Integer journalId, Integer manuscriptId, String specialization, String expertise, String alphabet, Pageable paging);

	@Query(value = "SELECT COUNT(*) from users_journals uj INNER JOIN users u ON u.id=uj.user_id where uj.journal_id=:journalId AND uj.is_active=1 AND u.is_active=1 AND u.id NOT IN(:authorId) AND uj.role_id=5 AND u.specialization=:specialization  AND u.expertise=:expertise AND uj.user_id NOT IN(SELECT r.reviewer_id  FROM manuscript_reviewer r WHERE r.manuscript_id=:manuscriptId AND r.manuscript_status IN (36,60,12,13,14,15))", nativeQuery = true)
	Integer getDatabaseSuggestedReviewerSpecializationExpertiesCounts(Integer journalId, Integer manuscriptId, String specialization, String expertise, Integer authorId);

	@Query(value = "SELECT COUNT(*) from users_journals uj INNER JOIN users u ON u.id=uj.user_id where uj.journal_id=:journalId AND uj.is_active=1 AND u.is_active=1 AND u.id NOT IN(:authorId) AND uj.role_id=5 AND u.specialization=:specialization  AND u.expertise=:expertise AND TRIM(u.first_name) LIKE :alphabet% AND uj.user_id NOT IN(SELECT r.reviewer_id  FROM manuscript_reviewer r WHERE r.manuscript_id=:manuscriptId AND r.manuscript_status IN (36,60,12,13,14,15))", nativeQuery = true)
	Integer getDatabaseSuggestedReviewerSpecializationExpertiesCountAlphabets(Integer journalId, Integer manuscriptId, String specialization, String expertise, String alphabet, Integer authorId);

	@Query(value = "SELECT uj.* from users_journals uj INNER JOIN users u ON u.id=uj.user_id where uj.journal_id=:journalId AND uj.is_active=1 AND u.is_active=1 AND uj.role_id=:roleId AND uj.user_id NOT IN(SELECT c.user_id  FROM commentary c WHERE c.manuscript_id=:manuscriptId )", nativeQuery = true)
	List<UserJournal> getDatabaseAuthorsAndReviewers(Integer journalId, Integer roleId, Integer manuscriptId,Pageable pageable);

	@Query(value = "SELECT uj.* from users_journals uj INNER JOIN users u ON u.id=uj.user_id where uj.journal_id=:journalId AND uj.is_active=1 AND u.is_active=1 AND uj.role_id=:roleId AND u.specialization=:specialization AND uj.user_id NOT IN( SELECT c.user_id  FROM commentary c WHERE c.manuscript_id=:manuscriptId )", nativeQuery = true)
	List<UserJournal> getCommentaryReviewerSpecialization(Integer journalId, Integer manuscriptId, String specialization,Integer roleId,  Pageable paging);

	@Query(value = "SELECT uj.* from users_journals uj INNER JOIN users u ON u.id=uj.user_id where uj.journal_id=:journalId AND uj.is_active=1 AND u.is_active=1 AND uj.role_id=:roleId AND u.specialization=:specialization AND u.expertise=:expertise AND uj.user_id NOT IN ( SELECT c.user_id  FROM commentary c WHERE c.manuscript_id=:manuscriptId)", nativeQuery = true)
	List<UserJournal> getCommentaryReviewerSpecializationExperties(Integer journalId, Integer manuscriptId, String specialization,  String expertise,Integer roleId,  Pageable paging);

	@Query(value = "SELECT uj.* from users_journals uj INNER JOIN users u ON u.id=uj.user_id where uj.journal_id=:journalId AND uj.is_active=1 AND u.is_active=1 AND uj.role_id=:roleId AND u.country_id=:countryId AND uj.user_id NOT IN ( SELECT c.user_id  FROM commentary c WHERE c.manuscript_id=:manuscriptId )", nativeQuery = true)
	List<UserJournal> getCommentaryReviewerCountry(Integer journalId, Integer manuscriptId, Integer roleId, Integer countryId,  Pageable paging);

	@Query(value = "SELECT uj.* from users_journals uj INNER JOIN users u ON u.id=uj.user_id where uj.journal_id=:journalId AND uj.is_active=1 AND u.is_active=1 AND uj.role_id=:roleId AND (u.user_email=:email OR u.specialization=:specialization OR u.expertise=:expertise OR CONCAT(TRIM(u.first_name),' ',TRIM(u.last_name)) LIKE %:name% OR u.first_name LIKE %:name% OR u.last_name LIKE %:name% OR u.country_id=:countryId) AND uj.user_id NOT IN ( SELECT c.user_id  FROM commentary c WHERE c.manuscript_id=:manuscriptId )", nativeQuery = true)
	List<UserJournal> getCommentaryReviewerSearchAny(Integer journalId, Integer manuscriptId, String specialization, Integer countryId, String expertise, String name, String email,Integer roleId, Pageable paging);

	@Query(value = "SELECT uj.* from users_journals uj INNER JOIN users u ON u.id=uj.user_id where uj.journal_id=:journalId AND uj.is_active=1 AND u.is_active=1 AND uj.role_id=:roleId AND ( CONCAT(TRIM(u.first_name),' ',TRIM(u.last_name)) LIKE %:name% OR u.first_name LIKE %:name% OR u.last_name LIKE %:name%  ) AND uj.user_id NOT IN ( SELECT c.user_id  FROM commentary c WHERE c.manuscript_id=:manuscriptId )", nativeQuery = true)
	List<UserJournal> getCommentaryReviewerName(Integer journalId, Integer manuscriptId, String name, Integer roleId,  Pageable paging);

	@Query(value = "SELECT uj.* from users_journals uj INNER JOIN users u ON u.id=uj.user_id where uj.journal_id=:journalId AND uj.is_active=1 AND u.is_active=1 AND uj.role_id=:roleId AND  u.user_email LIKE %:email%  AND uj.user_id NOT IN ( SELECT c.user_id  FROM commentary c WHERE c.manuscript_id=:manuscriptId )", nativeQuery = true)
	List<UserJournal> getCommentaryReviewerEmail(Integer journalId, Integer manuscriptId, String email, Integer roleId,  Pageable paging);

	@Query(value = "SELECT uj.* from users_journals uj INNER JOIN users u ON u.id=uj.user_id INNER JOIN countries cu ON cu.id=u.country_id where uj.journal_id=:journalId AND uj.is_active=1 AND u.is_active=1 AND uj.role_id=:roleId AND (u.user_email LIKE %:search% OR u.specialization LIKE %:search% OR u.expertise LIKE %:search% OR CONCAT(TRIM(u.first_name),' ',TRIM(u.last_name)) LIKE %:search% OR u.first_name LIKE %:search% OR u.last_name LIKE %:search% OR cu.name LIKE %:search%) AND uj.user_id NOT IN(SELECT c.user_id  FROM commentary c WHERE c.manuscript_id=:manuscriptId )", nativeQuery = true)
	List<UserJournal> commentaryGlobalSearch(Integer roleId, Integer journalId, Integer manuscriptId, String search, Pageable paging);

	@Query(value = "SELECT COUNT(*) from users_journals uj INNER JOIN users u ON u.id=uj.user_id where uj.journal_id=:journalId AND uj.is_active=1 AND u.is_active=1 AND uj.role_id=:roleId AND uj.user_id NOT IN(SELECT c.user_id  FROM commentary c WHERE c.manuscript_id=:manuscriptId)", nativeQuery = true)
	Integer commentaryReviewerCount(Integer journalId, Integer manuscriptId,Integer roleId);

	@Query(value = "SELECT COUNT(*) from users_journals uj INNER JOIN users u ON u.id=uj.user_id where uj.journal_id=:journalId AND uj.is_active=1 AND u.is_active=1 AND uj.role_id=:roleId AND ( CONCAT(TRIM(u.first_name),' ',TRIM(u.last_name)) LIKE %:name% OR u.first_name LIKE %:name% OR u.last_name LIKE %:name%  ) AND uj.user_id NOT IN ( SELECT c.user_id  FROM commentary c WHERE c.manuscript_id=:manuscriptId )", nativeQuery = true)
	Integer commentaryReviewerNameCount(Integer journalId, Integer manuscriptId, String name, Integer roleId);

	@Query(value = "SELECT Count(*) from users_journals uj INNER JOIN users u ON u.id=uj.user_id where uj.journal_id=:journalId AND uj.is_active=1 AND u.is_active=1 AND uj.role_id=:roleId AND  u.user_email LIKE %:email%  AND uj.user_id NOT IN ( SELECT c.user_id  FROM commentary c WHERE c.manuscript_id=:manuscriptId )", nativeQuery = true)
	Integer commentaryReviewerEmailCount(Integer journalId, Integer manuscriptId, String email, Integer roleId);


	@Query(value = "SELECT Count(*) from users_journals uj INNER JOIN users u ON u.id=uj.user_id where uj.journal_id=:journalId AND uj.is_active=1 AND u.is_active=1 AND uj.role_id=:roleId AND u.specialization=:specialization AND uj.user_id NOT IN( SELECT c.user_id  FROM commentary c WHERE c.manuscript_id=:manuscriptId )", nativeQuery = true)
	Integer commentaryReviewerSpecializationCount(Integer journalId, Integer manuscriptId, String specialization,Integer roleId);

	@Query(value = "SELECT Count(*) from users_journals uj INNER JOIN users u ON u.id=uj.user_id where uj.journal_id=:journalId AND uj.is_active=1 AND u.is_active=1 AND uj.role_id=:roleId AND u.specialization=:specialization AND u.expertise=:expertise AND uj.user_id NOT IN ( SELECT c.user_id  FROM commentary c WHERE c.manuscript_id=:manuscriptId)", nativeQuery = true)
	Integer commentarySpecializationExpertiesCount(Integer journalId, Integer manuscriptId, String specialization,  String expertise,Integer roleId);

	@Query(value = "SELECT Count(*) from users_journals uj INNER JOIN users u ON u.id=uj.user_id where uj.journal_id=:journalId AND uj.is_active=1 AND u.is_active=1 AND uj.role_id=:roleId AND u.country_id=:countryId AND uj.user_id NOT IN ( SELECT c.user_id  FROM commentary c WHERE c.manuscript_id=:manuscriptId )", nativeQuery = true)
	Integer commentaryReviewerCountryCount(Integer journalId, Integer manuscriptId, Integer roleId, Integer countryId);

	@Query(value = "SELECT Count(*) from users_journals uj INNER JOIN users u ON u.id=uj.user_id where uj.journal_id=:journalId AND uj.is_active=1 AND u.is_active=1 AND uj.role_id=:roleId AND (u.user_email=:email OR u.specialization=:specialization OR u.expertise=:expertise OR CONCAT(TRIM(u.first_name),' ',TRIM(u.last_name)) LIKE %:name% OR u.first_name LIKE %:name% OR u.last_name LIKE %:name% OR u.country_id=:countryId) AND uj.user_id NOT IN ( SELECT c.user_id  FROM commentary c WHERE c.manuscript_id=:manuscriptId )", nativeQuery = true)
	Integer commentaryReviewerSearchAnyCount(Integer journalId, Integer manuscriptId, String specialization, Integer countryId, String expertise, String name, String email,Integer roleId);

	@Query(value="SELECT u.user_id,u.journal_id,u.role_id  FROM users_journals u WHERE u.journal_id IN (:journalId) AND u.role_id IN(2,3) AND u.is_active=1;",nativeQuery = true)
	List<Tuple> userWithRole(List<Integer> journalId);
	
	@Query(value="SELECT role_id, COUNT(*) as count FROM users_journals WHERE is_active = 1 AND role_id IN (2, 3, 4, 5) GROUP BY role_id",nativeQuery = true)
	List<Tuple> userJournalWithRole();
	

    /**
     * Check if new user already has mapping for the given journal and roles.
     */
    @Query(value = "SELECT COUNT(*) FROM users_journals " +
                   "WHERE user_id = :newUserId " +
                   "AND (:journalId IS NULL OR journal_id = :journalId) " +
                   "AND role_id IN (2,3,4,5)", nativeQuery = true)
    int existsNewUserMapping(@Param("newUserId") Integer newUserId,
                             @Param("journalId") Integer journalId);
	
    /**
     * Deactivate old user mappings (make is_active = 0)
     */
    @Modifying
    @Transactional
    @Query(value = "UPDATE users_journals " +
                   "SET is_active = 0, updated_date = NOW() " +
                   "WHERE user_id = :oldUserId " +
                   "AND (:journalId IS NULL OR journal_id = :journalId) " +
                   "AND role_id IN (2,3,4,5)", nativeQuery = true)
    void deactivateOldUserJournal(@Param("oldUserId") Integer oldUserId,
                                  @Param("journalId") Integer journalId);

    /**
     * Activate existing mapping for new user if already present (is_active = 1)
     */
    @Modifying
    @Transactional
    @Query(value = "UPDATE users_journals " +
                   "SET is_active = 1, updated_by = :adminId, updated_date = NOW() " +
                   "WHERE user_id = :newUserId " +
                   "AND (:journalId IS NULL OR journal_id = :journalId) " +
                   "AND role_id IN (2,3,4,5)", nativeQuery = true)
    void activateExistingNewUser(@Param("newUserId") Integer newUserId,
                                 @Param("journalId") Integer journalId,
                                 @Param("adminId") Integer adminId);

    /**
     * Replace old user ID with new user ID when new user has no entry
     */
    @Modifying
    @Transactional
    @Query(value = "UPDATE users_journals " +
                   "SET user_id = :newUserId, updated_by = :adminId, updated_date = NOW() " +
                   "WHERE user_id = :oldUserId " +
                   "AND (:journalId IS NULL OR journal_id = :journalId) " +
                   "AND role_id IN (2,3,4,5)", nativeQuery = true)
    void replaceOldUserWithNew(@Param("oldUserId") Integer oldUserId,
                               @Param("newUserId") Integer newUserId,
                               @Param("journalId") Integer journalId,
                               @Param("adminId") Integer adminId);
   
    @Query("SELECT uj FROM UserJournal uj " +
    	       "WHERE uj.userId = :userId AND uj.isActive = 1")    	
    List<UserJournal> findActiveByUserId(@Param("userId") Integer userId);

    
}
