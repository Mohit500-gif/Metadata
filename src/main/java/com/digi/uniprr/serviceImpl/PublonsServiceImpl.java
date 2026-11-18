package com.digi.uniprr.serviceImpl;

import static com.digi.uniprr.utils.AppConstants.PUBLNSTOKEN;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.digi.uniprr.VO.ReviewerPublonsCommentVO;
import com.digi.uniprr.model.Journal;
import com.digi.uniprr.repository.JournalsRepo;
import com.digi.uniprr.service.ManuScriptReviewerService;
import com.digi.uniprr.service.PublonsService;
import com.digi.uniprr.utils.PublonsCSVUtils;
import com.digi.uniprr.utils.SendMailUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PublonsServiceImpl implements PublonsService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ManuScriptReviewerService manuscriptReviewerService;

	@Autowired
	private JournalsRepo journalRepo;
	
	@Autowired
	private SendMailUtils mailUtils;

	@Value("${reports.path}")
	private String reportsPath;

	@Value("${fromMail}")
	private String fromEmail;

	@Value("${publons.email}")
	private String publonsEmail;
	
	@Value("${publons.enabled.journals}")
	private List<Integer> journalIds;
	
	@Value("${publon.emailTo}")
	private String publonsEmailTo;

	@Override
	public String getReviewerById(String id) {
		String data = "";
		String url = "https://publons.com/api/v2/academic/" + id;

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.set("Authorization", PUBLNSTOKEN);
		HttpEntity<String> entity = new HttpEntity<>("body", headers);

		try {
			ResponseEntity<String> str = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
			data = str.toString();

			XmlMapper xmlMapper = new XmlMapper();

			JsonNode jsonNode = xmlMapper.readTree(data.getBytes());
			ObjectMapper objectMapper = new ObjectMapper();

			// Get JSON as a string
			String value = objectMapper.writeValueAsString(jsonNode);
			data = value;
		} catch (Exception e) {

		}

		return data;

	}

	@Override
	public void generateAndSendComment() {
		logger.info("Get publon report for {}",journalIds);
		try {
			if(journalIds.size() > 0) {
				for(int j: journalIds) {
					logger.info("Getting publon report for Journal ID: {}",j);
					List<ReviewerPublonsCommentVO> reviewerPublonsComments = manuscriptReviewerService.findReviewerCommentsForPublons(j);
					Journal journalData = journalRepo.getJournalById(j);
					
					if (!reviewerPublonsComments.isEmpty()) {
						logger.info("Building the publon report for Journal :"+journalData.getJournalCode());
						try {
							// Delete old and generate new CSV
							PublonsCSVUtils.createCSV(reportsPath + File.separator + journalData.getJournalCode()+"_PublonsComment.csv", reviewerPublonsComments);
							String mailBody = "";
							String publonToken = journalData.getJournalMetadata().getPublonsCode();
							
							if(publonToken != null && !publonToken.trim().equalsIgnoreCase("")) {
								mailBody = "PUBLONS_TOKEN="+publonToken.trim();
							}else {
								mailBody = "Reviewer comments";
							}
							String sub = journalData.getJournalTitle()+" export";
							logger.info("Report generated successfully....-{}", journalData.getJournalCode());
							
							logger.info("Publon Subject -----------"+sub);
							logger.info("Publin File path --------------"+reportsPath + File.separator + journalData.getJournalCode()+"_PublonsComment.xlsx");
							// Email generated comment file
							boolean success = mailUtils.sendEmailWithAttachment(fromEmail, publonsEmailTo, sub, mailBody, 
									new File(reportsPath), journalData.getJournalCode()+"_PublonsComment.xlsx");

							// Update Publons sent status in DB
							if (success) {
								logger.info("Updating the sentToPublon status...");
								List<Integer> idList = new ArrayList<Integer>();
								for(ReviewerPublonsCommentVO data: reviewerPublonsComments) {
									idList.add(data.getId());
								}
								manuscriptReviewerService.updateSentToPublonsYes(j, idList);
							}else {
								logger.error("...Fail to sent an email...");
							}
						} catch (IOException e) {
							logger.error("Exception while generating publons comment details : " + e.getMessage(), e);
						}
					} else {
						logger.error("No reviewer comment found to process");
					}
				}
			}else {
				logger.error("No journal id configured in config file");
			}
		} catch (Exception e) {
			logger.error("Error in  generateAndSendComment : {} ", e);
		}
		// TODO Get last column correctly
//		List<ReviewerPublonsCommentVO> reviewerPublonsComments = manuscriptReviewerService.findReviewerCommentsForPublons();

		
	}

}
