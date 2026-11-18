package com.digi.uniprr.service;

import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.digi.uniprr.integration.UnitouchClient;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AsyncEmailUpdateService {

	@Async("asyncExecutor")
	public void asyncUnitouchSync(UnitouchClient unitouchClient,
	                              String oldEmail,
	                              String newEmail,
	                              List<String> manuscriptCodes) {
	    try {
	        logger.info("Unitouch Sync START");
	        unitouchClient.updateEmailInUnitouch(oldEmail, newEmail, manuscriptCodes);
	        logger.info("Unitouch Sync END");
	    } catch (Exception e) {
	        logger.error("UniTouch sync failed: {}", e.getMessage());
	    }
	}

}

