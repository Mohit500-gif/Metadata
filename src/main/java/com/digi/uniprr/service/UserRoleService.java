package com.digi.uniprr.service;

import java.util.List;

import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.model.User;
import com.digi.uniprr.model.UserJournal;
import com.digi.uniprr.model.UserRole;

public interface UserRoleService {

	public void create(UserRole userRole);

	public List<User> getlistById(Integer id);

	public boolean getRoleByUser(Integer id, Integer i);

	public UserJournal addUserRole(IDVO id);

	public boolean accessRole(Integer userId, Integer roleId, Integer journalId);
}
