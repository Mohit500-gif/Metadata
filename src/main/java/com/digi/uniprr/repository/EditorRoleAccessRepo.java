package com.digi.uniprr.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.digi.uniprr.model.EditorRoleAccess;

public interface EditorRoleAccessRepo extends JpaRepository<EditorRoleAccess, Integer>{

	@Query("Select e from EditorRoleAccess e where e.userId=:id")
	EditorRoleAccess getDataByUserAndRoleId(Integer id);
	
	@Modifying
	@Query("UPDATE EditorRoleAccess ur SET ur.isActive =0 WHERE ur.userId=:userId")
	void disableUserEditorRoleAccess(Integer userId);

}
