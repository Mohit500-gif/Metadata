package com.digi.uniprr.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digi.uniprr.model.logging.ErrorLog;
import com.digi.uniprr.repository.ErrorLogRepo;
import com.digi.uniprr.service.ErrorLogService;

@Service
public class ErrorLogServiceImpl implements ErrorLogService {
	
	@Autowired
	private ErrorLogRepo errorLogRepo;

	@Override
	public void save(ErrorLog errroLog) {
		errorLogRepo.save(errroLog);	
	}


}
