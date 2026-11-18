package com.digi.uniprr.repository;

import java.util.List;

import javax.persistence.Tuple;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.digi.uniprr.VO.UserDetailsVO;
import com.digi.uniprr.VO.UserExportVo;
import com.digi.uniprr.model.User;
import com.digi.uniprr.model.UserLogins;

public interface UserLoginRepo extends JpaRepository<User, Integer> {

	User findByusername(String username);

	@Query("SELECT u from User u where u.email=:email AND u.isActive=1")
	User findByEmail(String email);
	
	@Query(value = "SELECT (COUNT(*) > 0) FROM users u WHERE id = :id AND account_locked IS NULL AND is_active = 1 AND password IS NOT NULL", nativeQuery = true)
	Integer isVerifiedAccount(Integer id);
	
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query(value = "update users set account_locked = 1 where id =:id",nativeQuery = true)
	void updateAccountLocked(Integer id);

	@Query("SELECT u from User u where u.email=:email")
	User findByEmailUser(String email);

	@Modifying(clearAutomatically = true)
	@Query("update User ud set ud.password =:password where ud.username =:userName")
	void updateUserPassword(String password, String userName);

	@Query("SELECT u FROM User u WHERE u.id=:id")
	List<User> getGroupByUserId(Integer id);

	List<User> getListById(Integer id);

	@Query("SELECT u FROM User u WHERE u.email=:email AND u.password=:password")
	User getUserByEmailAndPassword(String email, String password);

	@Modifying(clearAutomatically = true)
	@Query("update User u set u.isActive= 0 where u.id=:id")
	void deleteUser(Integer id);

	@Query(value = "update users set orcid_id=null where orcid_id=:id", nativeQuery = true)
	void deleteOrcid(String id);

	@Query("SELECT u FROM User u WHERE u.isActive=1")
	List<User> findAllIsActiveUser();

	@Query("SELECT u FROM User u WHERE u.isActive=0 AND u.accountLocked=0")
	List<User> findAllIsActiveUserEnabledEditor();

	@Query("SELECT u FROM User u WHERE u.accountLocked=0")
	List<User> findlistallUserMakedReviewer();

	@Query("SELECT u FROM User u WHERE id IN (SELECT userId FROM UserJournal WHERE journalId =:journalId) AND id IN (SELECT user FROM UserRole WHERE role = 5)")
	List<User> getReviewerOnRoleByjournalId(Integer journalId);

	@Query("Select uL from UserLogins uL WHERE uL.journalId=:journalId AND uL.status=1")
	List<UserLogins> getPresentStatusReports(Integer journalId);

	@Query("SELECT u from User u where u.orcidId=:orcidId AND u.isActive=1")
	User findByOrcidId(String orcidId);

	// @Query("SELECT u FROM User u join u.userRole r join u.UserJournal j where
	// u.isActive=1 AND r.role=:roleId AND r.isActive=1 AND j.journalId=:journalId
	// AND j.isActive=1 ORDER BY u.firstName ASC")
	@Query("SELECT u FROM User u WHERE u.id IN (SELECT uj.userId FROM UserJournal uj WHERE uj.roleId=:roleId AND uj.journalId=:journalId AND uj.isActive=1) AND u.isActive=1 ORDER BY u.firstName ASC")
	List<User> getUserByJournalAndRole(Integer roleId, Integer journalId);

	// @Query("SELECT u FROM User u join u.UserJournal j on j.userId=u.id where
	// j.roleId=:roleId AND j.journalId=:journalId AND j.isActive=1 AND u.isActive=1
	// ORDER BY u.firstName ASC")
	@Query("SELECT u FROM User u WHERE u.id IN (SELECT uj.userId FROM UserJournal uj WHERE uj.roleId=:roleId AND uj.journalId=:journalId AND uj.isActive=:isActive) AND u.isActive=1 ORDER BY u.firstName ASC")
	List<User> getAllUserByJournalAndRole(Integer roleId, Integer journalId, Integer isActive);

	@Query(value = "SELECT * FROM users u INNER JOIN users_journals uj ON u.id = uj.user_id LEFT JOIN countries c on c.id = u.country_id WHERE uj.role_id=:roleId AND uj.journal_id=:journalId AND uj.is_active=:isActive AND u.is_active=1", nativeQuery = true)
	List<User> getAuthorListByJournal(Integer roleId, Integer journalId, Integer isActive, Pageable paging);

	@Query(value = "SELECT * FROM users u INNER JOIN users_journals uj ON u.id = uj.user_id  LEFT JOIN countries c on c.id = u.country_id WHERE uj.role_id=:roleId AND uj.journal_id=:journalId AND uj.is_active=:isActive AND u.is_active=1 AND (u.user_email LIKE %:name% OR u.specialization LIKE %:name% OR u.first_name LIKE %:name% OR u.last_name LIKE %:name% OR u.mobile_no LIKE %:name%)", nativeQuery = true)
	List<User> getAuthorListByJournalSearch(Integer roleId, Integer journalId, Integer isActive, String name,
			Pageable paging);

	@Query(value = "SELECT COUNT(*) FROM users u INNER JOIN users_journals uj ON u.id = uj.user_id LEFT JOIN countries c on c.id = u.country_id WHERE uj.role_id=:roleId AND uj.journal_id=:journalId AND uj.is_active=:isActive AND u.is_active=1", nativeQuery = true)
	Integer getTotalCountAuthorListByJournal(Integer roleId, Integer journalId, Integer isActive);

	@Query(value = "SELECT COUNT(*) FROM users u INNER JOIN users_journals uj ON u.id = uj.user_id  LEFT JOIN countries c on c.id = u.country_id WHERE uj.role_id=:roleId AND uj.journal_id=:journalId AND uj.is_active=:isActive AND u.is_active=1 AND (u.user_email LIKE %:name% OR u.specialization LIKE %:name% OR u.first_name LIKE %:name% OR u.last_name LIKE %:name% OR u.mobile_no LIKE %:name%)", nativeQuery = true)
	Integer getTotalSearchCountAuthorListByJournal(Integer roleId, Integer journalId, Integer isActive, String name);

	@Query("SELECT u FROM User u WHERE u.id IN (SELECT uj.userId FROM UserJournal uj WHERE uj.roleId=:roleId AND uj.journalId=:journalId AND uj.isActive=:isActive) AND u.isActive=1 ORDER BY u.firstName ASC")
	List<User> getAllUserByJournalAndRoleAndPage(Integer roleId, Integer journalId, Pageable paging, Integer isActive);

	@Query(value = "select u.salutation as title,u.first_name as firstName , u.middle_name as middleName , u.last_name as lastName, u.user_email as userEmail, r.role_name as roleName, er.role as editorRole, u.editor_access_details as responsiblities, j.journal_code as journalAcronym ,u.academic as academicAffiliations, u.institute as institution,u.researcher_id as researchId, u.orcid_id as orcidId,u.pubmed_id as pubmedId,u.scopus_id as scopusId, u.specialization, u.expertise as subSpecialization,u.address, u.city, s.name, COALESCE(c.name,'--') as countryName, u.postal_code as postalCode, u.phone_no as phoneNumber, u.fax_no as faxNumber, u.mobile_no as mobileNumber "
			+ " from users u " + " join users_journals uj on u.id = uj.user_id "
			+ " join journal j on j.journal_id = uj.journal_id " + " left join countries c on c.id = u.country_id "
			+ " left join states s on s.state_code=u.state "
			+ " left join role r on r.id = uj.role_id " + " left join editor_role_access ea on ea.user_id = u.id"
			+ " left join editor_role er on er.id = ea.editor_role_id"
			+ " where uj.role_id =:roleId and uj.journal_id =:journalId and uj.is_active=:isActive and u.is_active=:isActive ", nativeQuery = true)
	List<Tuple> exportAllUserByJournalAndRole(Integer roleId, Integer journalId, Integer isActive);

	@Query("SELECT u FROM User u WHERE u.id=:id")
	User getById(Integer id);

	@Query("SELECT count(*) FROM User u WHERE u.id IN (SELECT uj.userId FROM UserJournal uj WHERE uj.roleId=:roleId AND uj.journalId=:journalId AND uj.isActive=:isActive) AND u.isActive=1 ORDER BY u.firstName ASC")
	Integer getCount(Integer roleId, Integer journalId, Integer isActive);

	@Query("SELECT u FROM User u WHERE u.isActive=:isActive and u.countryId > 0 and (user_email =:email or ''=:email) ORDER BY u.id desc")
	List<User> findAllInActiveUser(Integer isActive, String email, Pageable paging);

	@Transactional
	@Modifying
	@Query("update User u set u.isActive= 1 where u.id=:id")
	void updateInactiveUser(Integer id);

	@Query("SELECT count(*) FROM User u WHERE u.isActive=:isActive and u.countryId > 0 and (user_email =:email or ''=:email) ")
	Integer getInactiveUserCount(Integer isActive, String email);

	@Query(value = "select * from users u, users_journals uj where user_email =:email and u.id = uj.user_id and uj.journal_id IN :journalId and uj.role_id IN :roleIds ", nativeQuery = true)
	User existsByEmail(String email, List<Integer> journalId, List<Integer> roleIds);

	@Query("SELECT u.id from User u where u.email=:email AND u.isActive=1")
	Integer findByEmailId(String email);

	@Query(value = "select u.id as userId,u.first_name as firstName , u.last_name as lastName, u.user_email as userEmail, uj.role_id as roleId  from users u join users_journals uj "
			+ " on u.id = uj.user_id  and uj.role_id =:roleId and uj.journal_id =:journalId and uj.is_active=1 and u.is_active=1", nativeQuery = true)
	List<Tuple> getUsersListByJournalAndRole(Integer roleId, Integer journalId);

	@Query(value = "select u.id as userId, u.user_email as email, uj.role_id as roleId, CONCAT(u.first_name,' ', u.last_name, ' [', u.user_email, ']') as userName  "
			+ "from users u join users_journals uj "
			+ " on u.id = uj.user_id  and uj.role_id IN :roleId and uj.journal_id =:journalId and uj.is_active=1 and u.is_active=1"
			+ " and u.user_email is not null and u.user_email <> '' and u.first_name is not null and u.first_name <> '' ", nativeQuery = true)
	List<Tuple> getUsersListByJournalAndAllRole(List<Integer> roleId, Integer journalId);

	@Query(value = "SELECT DISTINCT u.specialization " + "FROM users u "+ "JOIN users_journals uj ON u.id = uj.user_id " + "WHERE uj.journal_id = :journalId "+ "AND uj.role_id = 5 " + "AND u.specialization != ''", nativeQuery = true)
	List<String> findDistinctSpecializationsByJournalId(@Param("journalId") Long journalId);
	
	@Query(value = "SELECT DISTINCT u.expertise  " + "FROM users u "+ "JOIN users_journals uj ON u.id = uj.user_id " + "WHERE uj.journal_id = :journalId "+ "AND uj.role_id = 5 " + "AND u.expertise != ''", nativeQuery = true)
	List<String> findDistinctSubSpecializationsByJournalId(@Param("journalId") Long journalId);
}