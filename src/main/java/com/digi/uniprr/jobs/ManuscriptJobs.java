package com.digi.uniprr.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.digi.uniprr.service.ManuScriptService;

@Configuration
@EnableScheduling
public class ManuscriptJobs {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ManuScriptService manuScriptService;

	/**
	 * Not in use
	 */
	@Deprecated
	//@Scheduled(cron = "0 43 18 * * ?")
	public void validateMansucriptsForAFP() {
		log.info("----validateMansucriptsForAFP Start ----");
		long startTime = System.currentTimeMillis();

		//manuScriptService.checkManuscriptForAFP();

		long endTime = System.currentTimeMillis();
		long timeTaken = (endTime - startTime) / 60000;
		log.info("----validateMansucriptsForAFP End ----Total time taken : " + timeTaken);
		
	}

}
