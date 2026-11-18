package com.digi.uniprr.repository;

import java.util.List;
import java.util.Optional;

import javax.persistence.Tuple;
import javax.transaction.Transactional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.digi.uniprr.VO.UsersDupVo;
import com.digi.uniprr.model.User;

@Repository
@Transactional
public interface UserRepo extends JpaRepository<User, Integer> {
	
	@Query(value = "SELECT * FROM users WHERE user_email=:email", nativeQuery = true)
	Optional<User> getEmail(String email); 

	@Query("SELECT u FROM User u WHERE u.email=:email")
	List<User> getByEmail(String email);

	User findByEmail(String email);
	
	@Query("SELECT u FROM User u WHERE u.id=:id")
	User getUser(Integer id);

	@Modifying(clearAutomatically = true)
	@Query("update User ud set ud.password =:password where ud.token =:token")
	void updateUserPassword(String password, String token);

	@Modifying(clearAutomatically = true)
	@Query("update User ud set ud.password =:password where ud.email =:email")
	void updateUserPasswordWithEmail(String password, String email);

	@Modifying(clearAutomatically = true)
	@Query("update User ud set ud.isActive =:isActive where ud.email =:email")
	void updateUserActivate(Integer isActive, String email);

	@Query("SELECT u FROM User u WHERE u.id=:user_id")
	List<User> getGroupByUserId(Integer user_id);

	@Modifying(clearAutomatically = true)
	@Query("update User ud set ud.token =:token where ud.email =:userEmail")
	void updateTokenByUser(String userEmail, String token);

	@Query("SELECT u FROM User u WHERE u.token=:token")
	List<User> findUserDetailsByToken(String token);

	@Modifying(clearAutomatically = true)
	@Query("update User ud set ud.password =:password where ud.token =:token")
	void updatePasswordByToken(String password, String token);

	@Modifying(clearAutomatically = true)
	@Query("update User ud set ud.token =:blank where ud.token =:token")
	void deleteTokenByUser(String token, String blank);

	@Query(value = "SELECT * FROM users WHERE id IN (SELECT usr_id FROM user_role where role_id in (:roleId) )", nativeQuery = true)
	List<User> getUserListByRoleId(List<Integer> roleId);

	@Query(value = "SELECT * from users u LEFT JOIN user_role ur ON ur.usr_id=u.id LEFT JOIN users_journals uj ON uj.user_id= u.id WHERE ur.role_id=:roleId order by u.first_name ASC", nativeQuery = true)
	List<User> getListByRoleId(Integer roleId);
	
	@Query(value = "SELECT u.id,u.first_name, u.last_name,u.user_email,u.department,u.specialization,u.expertise,uj.is_active from users u LEFT JOIN user_role ur ON ur.usr_id=u.id LEFT JOIN users_journals uj ON uj.user_id= u.id WHERE ur.role_id=:roleId order by u.first_name ASC", nativeQuery = true)
	List<Tuple> getUserListByRoleId(Integer roleId);

	@Query(value = "SELECT * from users u LEFT JOIN users_journals uj ON uj.user_id= u.id WHERE uj.role_id=:roleId AND uj.journal_id=:journalId AND uj.is_active=1 order by u.first_name ASC", nativeQuery = true)
	List<User> getListByjournaId(Integer journalId, Integer roleId);
	
	@Query(value = "SELECT u.id,u.first_name, u.last_name,u.user_email,u.department,u.specialization,u.expertise,uj.is_active from users u LEFT JOIN users_journals uj ON uj.user_id= u.id WHERE uj.role_id=:roleId AND uj.journal_id=:journalId order by u.first_name ASC", nativeQuery = true)
	List<Tuple> getUserList(Integer journalId, Integer roleId);
	
	@Query(value = "SELECT u.id,u.first_name, u.last_name,u.user_email,u.department,u.specialization,u.expertise,uj.is_active from users u LEFT JOIN users_journals uj ON uj.user_id= u.id WHERE uj.role_id IN(2,3) AND uj.journal_id=:journalId order by u.first_name ASC", nativeQuery = true)
	List<Tuple> getUserListForEditor(Integer journalId);

	@Query("SELECT u from User u where u.isActive=1")
	List<User> getAllActiveUser();

	@Query("SELECT u.institute from User u")
	List<String> getInstitutionByName();

	@Query("SELECT u FROM User u WHERE id IN (SELECT userId FROM UserJournal WHERE journalId =:journalId) AND id IN (SELECT user FROM UserRole WHERE role = 2)")
	List<User> getAssignAdmin(Integer journalId);

	@Modifying
	@Query("DELETE FROM UserJournal WHERE userId IN (SELECT user FROM UserRole WHERE role IN(2)) AND journalId =:journalId")
	void deleteExistingManagingEditorsForJournal(Integer journalId);

	@Modifying
	@Query("DELETE FROM UserJournal WHERE userId IN (SELECT user FROM UserRole WHERE role IN(3)) AND journalId =:journalId")
	void deleteExistingEditorsForJournal(Integer journalId);

	@Query("Select u from User u where u.id IN :coAuthorId AND u.isActive=1")
	List<User> getAllActiveUserByUserList(List<Integer> coAuthorId);
		
	/***** Author registered ******/
	//@Query(value = "SELECT * from users u LEFT JOIN user_role ur ON ur.usr_id=u.id LEFT JOIN users_journals uj ON uj.user_id= u.id WHERE ur.role_id=4 AND uj.journal_id=:journalId AND YEAR(u.created_date) IN :year AND MONTH(u.created_date)=:month", nativeQuery = true)
	@Query(value = "SELECT * from users u LEFT JOIN users_journals uj ON uj.user_id= u.id WHERE uj.role_id=4 AND uj.journal_id=:journalId AND YEAR(u.created_date) IN :year AND MONTH(u.created_date)=:month", nativeQuery = true)
	List<User> getTotalAuthorRegistered(List<Integer> journalId, List<Integer> year, Integer month);
	
	//@Query(value = "SELECT * from users u LEFT JOIN user_role ur ON ur.usr_id=u.id LEFT JOIN users_journals uj ON uj.user_id= u.id WHERE ur.role_id=4 AND uj.journal_id=:journalId AND YEAR(u.created_date) IN :year ", nativeQuery = true)
	@Query(value = "SELECT * from users u LEFT JOIN users_journals uj ON uj.user_id= u.id WHERE uj.role_id=4 AND uj.journal_id=:journalId AND YEAR(u.created_date) IN :year ", nativeQuery = true)
	List<User> getTotalAuthorRegisteredInYear(List<Integer> journalId, List<Integer> year);
	
	/***** Reviewer Added ********/
	//@Query(value = "SELECT * from users u LEFT JOIN user_role ur ON ur.usr_id=u.id LEFT JOIN users_journals uj ON uj.user_id= u.id WHERE ur.role_id=5 AND uj.journal_id=:journalId AND YEAR(u.created_date) IN :year AND MONTH(u.created_date)=:month", nativeQuery = true)
	@Query(value = "SELECT * from users u LEFT JOIN users_journals uj ON uj.user_id= u.id WHERE uj.role_id=5 AND uj.journal_id=:journalId AND YEAR(u.created_date) IN :year AND MONTH(u.created_date)=:month", nativeQuery = true)
	List<User> getTotalReviewerAdded(List<Integer> journalId, List<Integer> year, Integer month);
	
	//@Query(value = "SELECT * from users u LEFT JOIN user_role ur ON ur.usr_id=u.id LEFT JOIN users_journals uj ON uj.user_id= u.id WHERE ur.role_id=5 AND uj.journal_id=:journalId AND YEAR(u.created_date) IN :year ", nativeQuery = true)
	@Query(value = "SELECT * from users u LEFT JOIN users_journals uj ON uj.user_id= u.id WHERE uj.role_id=5 AND uj.journal_id=:journalId AND YEAR(u.created_date) IN :year ", nativeQuery = true)
	List<User> getTotalReviewerAddedInYear(List<Integer> journalId, List<Integer> year );
	
//	@Query(value = "SELECT u.id,u.orcid_id,u.user_name,u.user_email,u.salutation,u.first_name,u.middle_name,u.last_name,u.institute,u.address,u.phone_no,u.mobile_no,u.fax_no,c.name,u.city,u.specialization,u.expertise from users u INNER JOIN users_journals uj ON uj.user_id= u.id INNER JOIN countries c ON c.id= u.country_id WHERE uj.role_id=5 AND uj.journal_id=:journalId order by u.id ASC", nativeQuery = true)
//	List<Tuple> getTotalActiveReviewer(Integer journalId);
	
	@Query(value = "SELECT DISTINCT u.id,u.orcid_id,u.user_name,u.user_email,u.salutation,u.first_name,u.middle_name,u.last_name,u.institute,u.address,u.phone_no,u.mobile_no,u.fax_no,c.name,u.city,u.specialization,u.expertise from users u INNER JOIN users_journals uj ON uj.user_id= u.id Left JOIN countries c ON c.id= u.country_id left join manuscript_reviewer mr on uj.user_id = mr.reviewer_id WHERE (uj.is_active = 1 OR (uj.is_active = 0 AND mr.reviewer_id IS NOT NULL)) AND uj.role_id=5 AND uj.journal_id=:journalId order by u.id ASC", nativeQuery = true)
	List<Tuple> getTotalActiveReviewer(Integer journalId);
	
	@Query(value = "SELECT u.id,u.orcid_id,u.user_name,u.user_email,u.salutation,u.first_name,u.middle_name,u.last_name,u.institute,u.address,u.phone_no,u.mobile_no,u.fax_no,c.name,u.city,u.specialization,u.expertise, u.editor_access_details from users u INNER JOIN users_journals uj ON uj.user_id= u.id INNER JOIN countries c ON c.id= u.country_id WHERE uj.role_id=:roleId AND uj.is_active=1 order by u.first_name ASC", nativeQuery = true)
	List<Tuple> getUserByRoleId(Integer roleId);
	
	@Query(value = "SELECT u.id,u.orcid_id,u.user_name,u.user_email,u.salutation,u.first_name,u.middle_name,u.last_name,u.institute,u.address,u.phone_no,u.mobile_no,u.fax_no,c.name,u.city,u.specialization,u.expertise, u.editor_access_details from users u INNER JOIN users_journals uj ON uj.user_id= u.id INNER JOIN countries c ON c.id= u.country_id WHERE uj.role_id=:roleId AND uj.journal_id=:journalId AND uj.is_active=1 order by u.first_name ASC", nativeQuery = true)
	List<Tuple> getUserJournalRoleList(Integer journalId, Integer roleId);

	@Query("SELECT m FROM User m WHERE m.id=:id")
	User getUserData(Integer id);

	List<User> findAllByOrcidId(String orcidID);

	@Query("SELECT m FROM User m WHERE m.email=:email AND m.token=:token")
	User getUserVerficationData(String email, String token);

	@Query(value="SELECT u.* from users u INNER JOIN users_journals uj ON uj.user_id=u.id where uj.journal_id=:journalId AND uj.is_active=1 AND u.is_active=1 AND uj.role_id=:roleId AND (TRIM(u.first_name) LIKE %:name% OR TRIM(u.middle_name) LIKE %:name%  OR TRIM(u.last_name) LIKE %:name% OR CONCAT(TRIM(u.first_name) , ' ', TRIM(u.middle_name), ' ', TRIM(u.last_name)) LIKE %:name% OR CONCAT(TRIM(u.first_name) ,' ', TRIM(u.last_name)) LIKE %:name%)", nativeQuery=true)
	List<User> searchUserByName(Integer journalId, Integer roleId, String name, Pageable paging);

	@Query("SELECT m FROM User m WHERE m.email=:email")
	List<User> searchUserByEmail(String email);

	@Query("SELECT m FROM User m WHERE m.city=:city AND m.isActive=1")
	List<User> searchUserByCity(String city);

	@Query("SELECT m FROM User m WHERE m.specialization IN :keywordList OR m.expertise IN :keywordList AND m.isActive=1")
	List<User> getUserBySpecializationAndSubject(List<String> keywordList);

	@Query("SELECT m FROM User m INNER JOIN fetch m.journal u WHERE u.journalId=:journalId AND m.isActive=1 GROUP BY m.specialization")
	List<User> getSpecialization(Integer journalId);
	
	@Query(value="SELECT TRIM(u.specialization) FROM users u INNER JOIN users_journals uj ON uj.user_id=u.id WHERE uj.journal_id=:journalId AND u.is_active=1 AND uj.is_active=1 AND uj.role_id=5 AND u.specialization!=\"\" GROUP BY u.specialization ORDER BY TRIM(u.specialization) ASC", nativeQuery=true)
	List<String> getSpecializationForReviewer(Integer journalId);

	@Query("SELECT m FROM User m INNER JOIN fetch m.journal u WHERE u.journalId=:journalId AND m.isActive=1 GROUP BY m.expertise")
	List<User> getExpertise(Integer journalId);
	
//	@Query(value="SELECT TRIM(u.expertise) FROM users u INNER JOIN users_journals uj ON uj.user_id=u.id WHERE uj.journal_id=:journalId AND u.is_active=1 AND uj.role_id=5 AND u.expertise!=\"\" GROUP BY u.expertise ORDER BY TRIM(u.expertise) ASC", nativeQuery=true)
//	List<String> getExpertiseReviewer(Integer journalId);
	@Query(value="SELECT TRIM(u.expertise) FROM users u INNER JOIN users_journals uj ON uj.user_id=u.id WHERE uj.journal_id=:journalId AND u.is_active=1 AND uj.is_active=1 AND u.specialization=:specialization AND uj.role_id=5 AND u.expertise!=\"\" GROUP BY u.expertise ORDER BY TRIM(u.expertise) ASC", nativeQuery=true)
	List<String> getExpertiseReviewer(Integer journalId,String specialization);

//	@Query("SELECT m FROM User m INNER JOIN fetch m.journal u WHERE (m.firstName LIKE :alphabet% OR m.email=:email OR m.specialization=:specialization OR m.expertise=:expertise OR m.firstName LIKE %:name% OR m.lastName LIKE %:name% OR m.countryId=:countryId) AND u.journalId=:journalId AND m.isActive=1 and u.isActive=1 GROUP BY m.id")
	@Query(value="SELECT u.* from users u INNER JOIN users_journals uj ON uj.user_id=u.id and uj.is_active = 1 and uj.role_id =:roleId and uj.journal_id =:journalId where (u.user_email = :email OR u.specialization =:specialization OR u.expertise=:expertise OR u.first_name LIKE %:name% OR u.last_name LIKE %:name% OR u.country_id =:countryId) AND TRIM(u.first_name) LIKE :alphabet% AND u.is_active=1 ", nativeQuery=true)
	List<User> getUserByJournal(Integer roleId, Integer journalId, String specialization, String expertise, String name, Integer countryId, String email, String alphabet, Pageable paging);
	
//	@Query("SELECT m FROM User m INNER JOIN fetch m.journal u WHERE (m.firstName LIKE :alphabet% ) AND u.journalId=:journalId AND m.isActive=1 and u.isActive=1")
	@Query(value="SELECT u.* from users u INNER JOIN users_journals uj ON uj.user_id=u.id and uj.is_active = 1 and uj.role_id =:roleId and uj.journal_id =:journalId where (u.first_name like :alphabet%) AND u.is_active=1 ", nativeQuery=true)
	List<User> getUserByJournalByAlphabet(Integer journalId, String alphabet, Integer roleId, Pageable paging);
	
	@Query(value="SELECT count(1) from users u INNER JOIN users_journals uj ON uj.user_id=u.id and uj.is_active = 1 and uj.role_id =:roleId and uj.journal_id =:journalId where (u.first_name like :alphabet%) AND u.is_active=1 ", nativeQuery=true)
	int getUserByJournalByAlphabetCount(Integer journalId, String alphabet, Integer roleId);
	
	@Query("SELECT m FROM User m INNER JOIN fetch m.journal u WHERE m.specialization=:specialization AND m.countryId=:countryId AND u.journalId=:journalId AND m.isActive=1 GROUP BY m.id")
	List<User> getUserByJournalBySpecialization(Integer journalId, String specialization, Integer countryId);
	
	@Query("SELECT m FROM User m INNER JOIN fetch m.journal u WHERE m.specialization=:specialization AND m.expertise=:expertise AND m.countryId=:countryId AND u.journalId=:journalId AND m.isActive=1 and u.isActive=1 GROUP BY m.id")
	List<User> getUserByJournalBySpecializationAndExperties(Integer journalId, String specialization,String expertise, Integer countryId);
	
	@Query("SELECT m FROM User m INNER JOIN fetch m.journal u WHERE m.specialization=:specialization AND m.expertise=:expertise AND u.journalId=:journalId AND m.isActive=1 and u.isActive=1 GROUP BY m.id")
	List<User> getUserByJournalBySpecializationAndExpertiesI(Integer journalId, String specialization,String expertise);

	@Query(value="SELECT count(1) from users u INNER JOIN users_journals uj ON uj.user_id=u.id and uj.is_active = 1 and uj.role_id =:roleId and uj.journal_id =:journalId where (u.user_email = :email OR u.specialization =:specialization OR u.expertise=:expertise OR u.first_name LIKE %:name% OR u.last_name LIKE %:name% OR u.country_id =:countryId) AND TRIM(u.first_name) LIKE :alphabet% AND u.is_active=1 ", nativeQuery=true)
	int getUserCountByJournalAndRole(Integer journalId, Integer roleId, String email, String alphabet, String specialization, String expertise, String name, Integer countryId);

	@Query(value="SELECT u.* from users u INNER JOIN users_journals uj ON uj.user_id=u.id where uj.journal_id=:journalId AND uj.is_active=1 AND u.is_active=1 AND uj.role_id=:roleId AND (TRIM(u.first_name) LIKE %:name% OR TRIM(u.middle_name) LIKE %:name%  OR TRIM(u.last_name) LIKE %:name% OR CONCAT(TRIM(u.first_name) , ' ', TRIM(u.middle_name), ' ', TRIM(u.last_name)) LIKE %:name% OR CONCAT(TRIM(u.first_name) ,' ', TRIM(u.last_name)) LIKE %:name%)", nativeQuery=true)
	List<User> searchActiveUserByName(Integer journalId, Integer roleId, String name, Pageable paging);

//	@Query("SELECT m FROM User m WHERE m.email=:email AND m.isActive=1")
	@Query(value="SELECT u.* from users u INNER JOIN users_journals uj ON uj.user_id=u.id where u.user_email=:email AND uj.journal_id=:journalId AND uj.is_active=1 AND u.is_active=1 AND uj.role_id=:roleId ", nativeQuery=true)
	List<User> searchActiveUserByEmail(Integer journalId, Integer roleId, String email, Pageable paging);

	@Query("SELECT m FROM User m WHERE m.city=:city AND m.isActive=1")
	List<User> searchActiveUserByCity(String city);
	
	@Query("SELECT u from User u where u.id=:id")
	User getByID(Integer id);
	
	@Query("SELECT u FROM User u WHERE u.id IN (SELECT uj.userId FROM UserJournal uj WHERE uj.roleId=:roleId AND uj.isActive=1) AND u.isActive=1 ORDER BY u.firstName ASC")
	List<User> getUniqueUserByRoleId(Integer roleId);
	
	@Query("SELECT m FROM User m WHERE (m.specialization IN :keywordList OR m.expertise IN :keywordList) AND m.id IN :userId AND m.isActive=1")
	List<User> getUserBySpecializationAndSubjectList(List<String> keywordList, List<Integer> userId);
	
	@Query(value = "SELECT * FROM users u INNER JOIN manuscript_editor me ON me.editor_id= u.id WHERE me.journal_id=:journalId AND me.manuscript_id=:manuscriptId GROUP BY editor_id;", nativeQuery = true)
	List<User> getAssignedEditorListById(Integer journalId, Integer manuscriptId);
	
	@Query("SELECT new com.digi.uniprr.VO.UsersDupVo(m.id,m.firstName,m.middleName,m.lastName,m.email,m.specialization,m.isActive,m.countryId,m.countries,m.expertise,m.institute) FROM User m WHERE (m.specialization IN :keywordList OR m.expertise IN :keywordList) AND m.id IN :userId AND m.isActive=1 ")
	List<UsersDupVo> getUserBySpecializationAndSubjectVoList(List<String> keywordList, List<Integer> userId);
	
//	@Query(value ="select * from manuscript m JOIN manuscript_reviewer mr on m.manuscript_id=mr.manuscript_id WHERE m.journal_id IN :journalId AND mr.reviewer_id IN (SELECT u.user_id FROM users_journals u WHERE u.is_active=1 AND u.role_id=5) AND YEAR(m.submission_date) IN :year  and mr.manuscript_status IN (12,13,14,15,30,35,36,44,60) GROUP BY mr.reviewer_id ORDER BY mr.reviewer_id ",nativeQuery = true)
	@Query(value ="SELECT us.* FROM manuscript m JOIN manuscript_reviewer mr ON m.manuscript_id=mr.manuscript_id JOIN users us ON us.id = mr.reviewer_id JOIN users_journals uj ON uj.user_id = mr.reviewer_id"
			+ " WHERE m.journal_id IN :journalId AND uj.is_active=1 AND us.is_active=1 AND uj.journal_id IN :journalId"
			+ " AND YEAR(mr.complete_date) IN :year AND mr.manuscript_status IN "
			+ " (12,13,14,15)"
			+ " GROUP BY mr.reviewer_id"
			+ " ORDER BY mr.reviewer_id ",nativeQuery = true)
	List<User> getNumberOfReviewersRespondedAll(List<Integer> journalId, List<Integer> year);
	
//	@Query(value ="SELECT * FROM manuscript m JOIN manuscript_reviewer mr on m.manuscript_id=mr.manuscript_id WHERE m.journal_id IN :journalId AND mr.reviewer_id IN (SELECT u.user_id FROM users_journals u WHERE u.is_active=1 AND u.role_id=5)  and Year(m.submission_date) IN :year  group by mr.reviewer_id ORDER BY mr.reviewer_id",nativeQuery = true)
	@Query(value ="SELECT us.* FROM manuscript m "
			+ " JOIN manuscript_reviewer mr on m.manuscript_id=mr.manuscript_id "
			+ " JOIN users us ON us.id = mr.reviewer_id "
			+ " JOIN users_journals uj ON uj.user_id = mr.reviewer_id "
			+ " WHERE m.journal_id IN :journalId AND uj.is_active=1 AND us.is_active=1 AND uj.journal_id IN :journalId "
			+ " and Year(mr.assigned_date) IN :year AND mr.manuscript_status IN (12,13,14,15,8,66,33,36)  group by mr.reviewer_id ORDER BY "
			+ " mr.reviewer_id",nativeQuery = true)
	List<User> getNumberOFReviewerUsedNumberAll(List<Integer> journalId,List<Integer> year);
	
	
	
//	@Query(value ="SELECT * FROM manuscript m JOIN manuscript_reviewer mr on m.manuscript_id=mr.manuscript_id WHERE m.journal_id IN :journalId AND mr.reviewer_id IN (SELECT u.user_id FROM users_journals u WHERE u.is_active=1 AND u.role_id=5) and Year(m.submission_date) IN :year AND MONTH(m.submission_date)=:month group by mr.reviewer_id ORDER BY mr.reviewer_id",nativeQuery = true)
	@Query(value ="SELECT us.* FROM manuscript m "
			+ " JOIN manuscript_reviewer mr on m.manuscript_id=mr.manuscript_id"
			+ " JOIN users us ON us.id = mr.reviewer_id"
			+ " JOIN users_journals uj ON uj.user_id = mr.reviewer_id"
			+ " WHERE m.journal_id IN :journalId AND uj.is_active=1 AND us.is_active=1 AND uj.journal_id IN :journalId"
			+ " and Year(mr.assigned_date) IN :year AND MONTH(mr.assigned_date)=:month AND mr.manuscript_status IN (12,13,14,15,8,66,33,36) group by mr.reviewer_id ORDER BY "
			+ " mr.reviewer_id",nativeQuery = true)
	List<User> getNumberOFReviewerUsedNumber(List<Integer> journalId,List<Integer> year,Integer month);

//	@Query(value ="select * from manuscript m JOIN manuscript_reviewer mr on m.manuscript_id=mr.manuscript_id WHERE m.journal_id IN :journalId AND mr.reviewer_id IN (SELECT u.user_id FROM users_journals u WHERE u.is_active=1 AND u.role_id=5)  AND YEAR(m.submission_date) IN :year AND MONTH(m.submission_date)=:month and mr.manuscript_status IN (12,13,14,15) GROUP BY mr.reviewer_id ORDER BY mr.reviewer_id",nativeQuery = true)
	@Query(value ="SELECT us.* FROM manuscript m JOIN manuscript_reviewer mr ON m.manuscript_id=mr.manuscript_id JOIN users us ON us.id = mr.reviewer_id JOIN users_journals uj ON uj.user_id = mr.reviewer_id"
			+ " WHERE m.journal_id IN :journalId AND uj.is_active=1 AND us.is_active=1 AND uj.journal_id IN :journalId"
			+ " AND YEAR(mr.complete_date) IN :year AND MONTH(mr.complete_date)=:month AND mr.manuscript_status IN "
			+ " (12,13,14,15)"
			+ " GROUP BY mr.reviewer_id"
			+ " ORDER BY mr.reviewer_id ",nativeQuery = true)
	List<User> getNumberOfReviewersResponded(List<Integer> journalId, List<Integer> year,Integer month);
	
	@Query("SELECT new com.digi.uniprr.VO.UsersDupVo(m.id,m.firstName,m.middleName,m.lastName,m.email,m.specialization,m.isActive,m.countryId,m.countries,m.expertise,m.institute) FROM User m WHERE (m.specialization IN :keywordList OR m.expertise IN :keywordList) AND m.id IN :userId AND m.isActive=1 ")
	List<UsersDupVo> getUserBySpecializationAndSubjectVoList(List<String> keywordList, List<Integer> userId,Pageable paging);
	
	@Query("SELECT count(m.id) FROM User m WHERE (m.specialization IN :keywordList OR m.expertise IN :keywordList) AND m.id IN :userId AND m.isActive=1 ")
	int getUserBySpecializationAndSubjectVoListCount(List<String> keywordList, List<Integer> userId);
	

	@Query("SELECT new com.digi.uniprr.VO.UsersDupVo(m.id,m.firstName,m.middleName,m.lastName,m.email,m.specialization,m.isActive,m.countryId,m.countries,m.expertise,m.institute) FROM User m JOIN Countries c ON m.countryId = c.id  WHERE (m.specialization IN :keywordList OR m.expertise IN :keywordList) AND m.id IN :userId AND m.isActive=1 AND  (m.email LIKE %:search% OR m.specialization LIKE %:search% OR m.expertise LIKE %:search% OR CONCAT(TRIM(m.firstName),' ',TRIM(m.lastName)) LIKE %:search% OR m.firstName LIKE %:search% OR m.lastName LIKE %:search% OR c.name LIKE %:search%)  ")
	List<UsersDupVo> getUserBySpecializationAndSubjectVoList(List<String> keywordList, List<Integer> userId,Pageable paging,String search);
	
	@Query("SELECT count(m.id) FROM User m JOIN Countries c ON m.countryId = c.id WHERE (m.specialization IN :keywordList OR m.expertise IN :keywordList) AND m.id IN :userId AND m.isActive=1 AND  (m.email LIKE %:search% OR m.specialization LIKE %:search% OR m.expertise LIKE %:search% OR CONCAT(TRIM(m.firstName),' ',TRIM(m.lastName)) LIKE %:search% OR m.firstName LIKE %:search% OR m.lastName LIKE %:search% OR c.name LIKE %:search%) ")
	int getUserBySpecializationAndSubjectVoListCount(List<String> keywordList, List<Integer> userId,String search);
	
	@Query("SELECT new com.digi.uniprr.VO.UsersDupVo(m.id,m.firstName,m.middleName,m.lastName,m.email,m.specialization,m.isActive,m.countryId,m.countries,m.expertise,m.institute)  FROM User m JOIN Countries c ON m.countryId = c.id  WHERE (m.specialization IN :keywordList OR m.expertise IN :keywordList) AND m.id IN :userId AND m.isActive=1 AND  (m.email LIKE %:search% OR m.specialization LIKE %:search% OR m.expertise LIKE %:search% OR CONCAT(TRIM(m.firstName),' ',TRIM(m.lastName)) LIKE %:search% OR m.firstName LIKE %:search% OR m.lastName LIKE %:search% OR c.name LIKE %:search%) ORDER BY c.name ASC")
	List<UsersDupVo> getUserBySpecializationAndSubjectVoListCountrySortAsc(List<String> keywordList, List<Integer> userId,Pageable paging,String search);
	
	@Query("SELECT new com.digi.uniprr.VO.UsersDupVo(m.id,m.firstName,m.middleName,m.lastName,m.email,m.specialization,m.isActive,m.countryId,m.countries,m.expertise,m.institute)  FROM User m JOIN Countries c ON m.countryId = c.id  WHERE (m.specialization IN :keywordList OR m.expertise IN :keywordList) AND m.id IN :userId AND m.isActive=1 AND  (m.email LIKE %:search% OR m.specialization LIKE %:search% OR m.expertise LIKE %:search% OR CONCAT(TRIM(m.firstName),' ',TRIM(m.lastName)) LIKE %:search% OR m.firstName LIKE %:search% OR m.lastName LIKE %:search% OR c.name LIKE %:search%) ORDER BY c.name DESC")
	List<UsersDupVo> getUserBySpecializationAndSubjectVoListCountrySortDesc(List<String> keywordList, List<Integer> userId,Pageable paging,String search);
	
	@Query("SELECT new com.digi.uniprr.VO.UsersDupVo(m.id,m.firstName,m.middleName,m.lastName,m.email,m.specialization,m.isActive,m.countryId,m.countries,m.expertise,m.institute)  FROM User m JOIN Countries c ON m.countryId = c.id  WHERE (m.specialization IN :keywordList OR m.expertise IN :keywordList) AND m.id IN :userId AND m.isActive=1 ORDER BY c.name ASC ")
	List<UsersDupVo> getUserBySpecializationAndSubjectVoListCountrySortAsc(List<String> keywordList, List<Integer> userId,Pageable paging);
	
	@Query("SELECT new com.digi.uniprr.VO.UsersDupVo(m.id,m.firstName,m.middleName,m.lastName,m.email,m.specialization,m.isActive,m.countryId,m.countries,m.expertise,m.institute)  FROM User m JOIN Countries c ON m.countryId = c.id  WHERE (m.specialization IN :keywordList OR m.expertise IN :keywordList) AND m.id IN :userId AND m.isActive=1 ORDER BY c.name DESC ")
	List<UsersDupVo> getUserBySpecializationAndSubjectVoListCountrySortDesc(List<String> keywordList, List<Integer> userId,Pageable paging);

	@Modifying(clearAutomatically = true)
	@Query("update User ud set ud.editorAccessDetails =:editorAccessDetails where ud.id =:userId and ud.email =:email")
	void updateEditorAccess(Integer userId, String email, String editorAccessDetails);
	
	@Query(value="SELECT u.id,u.user_email,u.account_locked, u.password  FROM users u WHERE u.user_email=:email", nativeQuery = true)
	Tuple getByEmailTuple(String email);

	@Query(value = "SELECT role_id FROM users_journals WHERE user_id = :userId AND journal_id = :journalId AND role_id IN (2,3) LIMIT 1", nativeQuery = true)
	Integer getEditorRoleForJournal(@Param("userId") Integer userId, @Param("journalId") Integer journalId);

}
	