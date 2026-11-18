package com.digi.uniprr.service;

import java.util.List;

import com.digi.uniprr.model.Role;
import com.digi.uniprr.model.UserRole;

public interface OrcidService {
	
	String getOrcidDetail(String id);

	String getOrcidID(String name, String readOrcid);

	List<Role> getRolesByOrcid(Integer userId, Integer jid);
}
