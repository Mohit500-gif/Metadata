package com.digi.uniprr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.digi.uniprr.model.UserRole;

public interface UserRoleRepo extends JpaRepository<UserRole, Integer> {

	@Query("DELETE FROM UserRole ur WHERE ur.user=:userId")
	@Modifying
	void deleteUserRole(Integer userId);

	@Query("SELECT m FROM UserRole m WHERE  YEAR(m.updatedOn) IN :year ")
	List<UserRole> getUserRoleList(List<Integer> year);

	@Query("SELECT m FROM UserRole m WHERE m.role IN :roleId ")
	List<UserRole> getUserListByRoleId(List<Integer> roleId);

	@Query("SELECT m FROM UserRole m WHERE m.user=:id AND m.role=:i")
	UserRole getRoleByUser(Integer id, Integer i);
	
	@Query("SELECT m FROM UserRole m WHERE m.user IN :userId AND m.role=:roleId ")
	List<UserRole> getUseRroleList(List<Integer> userId,Integer roleId);
	
	@Query("SELECT m FROM UserRole m WHERE m.user IN :userId AND m.isActive=1 ")
	List<UserRole> getUseRoleListByUserId(Integer userId);
	
	@Query("SELECT m FROM UserRole m WHERE m.user =:userId AND m.role=:roleId")
	UserRole getUseRoleListByRoleList(Integer userId, Integer roleId);
	
	@Modifying
	@Query("UPDATE UserRole ur SET ur.isActive =0 WHERE ur.user=:userId")
	void disableUserRole(Integer userId);
	
	@Query("SELECT u FROM UserRole u WHERE u.user=:userId AND u.isActive=1")
	UserRole getUserRoleDetails(Integer userId);

	@Query("SELECT count(*) FROM UserRole u WHERE u.user=:userId AND u.isActive=1 and u.role=4")
	Integer getAuthorRoleCount(Integer userId);

	@Query(value="Select m.role_id from user_role m WHERE m.usr_id = :userId AND m.is_active=1", nativeQuery = true)
	List<Integer> findByUserDataEmail(Integer userId);
	
}
