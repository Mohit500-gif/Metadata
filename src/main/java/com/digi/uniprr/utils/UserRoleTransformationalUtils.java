package com.digi.uniprr.utils;

import java.util.ArrayList;
import java.util.List;

import com.digi.uniprr.VO.RoleVO;
import com.digi.uniprr.model.UserJournal;
import com.digi.uniprr.model.UserRole;

public class UserRoleTransformationalUtils {
	
	public static RoleVO mapModelToVO(UserJournal journal) {
		RoleVO vo = new RoleVO();
		if(journal.getRoleId()==null) {
			System.out.println("userId : "+journal.getUserId());
			return null;
		}
		vo.setRoleId(journal.getRoleId());
		vo.setRoleName(journal.getRole_id().getRoleName());
		return vo;
	}
	
	public static RoleVO mapModelToVO(UserRole userRole) {
		RoleVO vo = new RoleVO();
		vo.setRoleId(userRole.getRole());
		vo.setRoleName(userRole.getRoleId().getRoleName());
		return vo;
	}
	
	public static List<RoleVO> mapRoleListToVOList1(List<UserRole> userRoleList) {
		List<RoleVO> voList = new ArrayList<RoleVO>();
		for (UserRole userRole : userRoleList) 
			if(userRole.getIsActive() !=null && userRole.getIsActive()==1)
			voList.add(mapModelToVO(userRole));
		return voList;
	}
	
	public static List<RoleVO> mapRoleListToVOList(List<UserJournal> userRoleList,Integer journalId) {
		List<RoleVO> voList = new ArrayList<RoleVO>();
		for (UserJournal journal : userRoleList) 
			if(journal.getIsActive() !=null && journal.getIsActive()==1 && journal.getJournalId().equals(journalId))
			voList.add(mapModelToVO(journal));
		return voList;
	}
	
	
}
