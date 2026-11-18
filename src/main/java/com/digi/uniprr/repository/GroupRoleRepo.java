package com.digi.uniprr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.digi.uniprr.bean.GroupRoleBean;

public interface GroupRoleRepo extends JpaRepository<GroupRoleBean, Integer> 
{
	@Query("select r from GroupRoleBean r where r.roleId=:roleId")
	public List<GroupRoleBean> getGroupByRoleId(Integer roleId);

	@Query(value= "select role_id from group_role g where g.group_id=:groupId", nativeQuery=true)
	public Integer getRoleIdByGroupId(Integer groupId);
	
	@Modifying
    @Query("delete from GroupRoleBean g where g.groupId=:id")
	public void deleteGroupRole(Integer id);
	
	@Modifying(clearAutomatically = true)
	@Query("update GroupRoleBean ud set ud.roleId =:roleId,ud.updatedBy=:updatedBy where ud.groupId =:groupId")
	void updateGroupRole(Integer groupId,Integer roleId,Integer updatedBy);

}
