package com.digi.uniprr.jobs;

import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.digi.uniprr.service.PublonsService;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableScheduling
@Slf4j
public class PublonsJob {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PublonsService publonsService;

	@Scheduled(cron = "${publons.cron}")
	public void publonsCron() {
		logger.info("----PublonsComment cron Start ----");
		long startTime = System.currentTimeMillis();

		publonsService.generateAndSendComment();

		long endTime = System.currentTimeMillis();
		long timeTaken = (endTime - startTime) / 60000;
		logger.info("----PublonsComment cron  ----Total time taken : " + timeTaken);

	}

}
