package com.digi.uniprr.serviceImpl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.model.User;
import com.digi.uniprr.model.UserJournal;
import com.digi.uniprr.model.UserRole;
import com.digi.uniprr.repository.UserJournalRepo;
import com.digi.uniprr.repository.UserRoleRepo;
import com.digi.uniprr.service.UserRoleService;

@Service
@Transactional
public class UserRoleServiceImpl implements UserRoleService {

	@Autowired
	UserRoleRepo userRoleRepo;
	
	@Autowired
	UserJournalRepo userJournalRepo;
	
	@Override
	public void create(UserRole userRole) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<User> getlistById(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean getRoleByUser(Integer id, Integer i) {
		boolean value = false;
		UserRole userRoole = userRoleRepo.getRoleByUser(id, i);
		if(userRoole!=null) {
			value = true;
		}
		return value;
	}

	@Override
	public UserJournal addUserRole(IDVO id) {
		UserJournal userRoole = userJournalRepo.getUserJournal(id.getUserId(), id.getJournalId(),4);
		if(userRoole!=null) {
			userRoole.setIsActive(1);
			return userJournalRepo.save(userRoole);
		}else{
			UserJournal userRole = new UserJournal();
			userRole.setUserId(id.getUserId());
			userRole.setRoleId(4);
			userRole.setJournalId(id.getJournalId());
			userRole.setIsActive(1);
			return userJournalRepo.save(userRole);
		}
		
	}

	public boolean accessRole(Integer id,Integer roleId, Integer journalId) {
		UserJournal userRole = userJournalRepo.getUserRole(id, roleId, journalId);
		if(userRole!=null) {
			return true;
		}
		else {
			return false;
		}
	}
}
