package com.digi.uniprr.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.digi.uniprr.model.UserLoginStatus;
import com.digi.uniprr.repository.UserLoginStatusRepo;
import com.digi.uniprr.service.UserLoginStatusService;

@Service
@Transactional
public class UserLoginStatusServiceImpl implements UserLoginStatusService {

	@Autowired
	UserLoginStatusRepo userLoginStatusRepo;
	
	@Override
	public String userLoginStatus(UserLoginStatus userLoginStatus) {
		userLoginStatusRepo.save(userLoginStatus);
		return "successfully added";
	}

}
