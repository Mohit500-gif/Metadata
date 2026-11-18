package com.digi.uniprr.serviceImpl;

import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.digi.uniprr.service.ArticleSyncService;
import com.digi.uniprr.service.HttpService;
import com.digi.uniprr.utils.AppConstants;
import com.digi.uniprr.utils.HttpServiceRequestInfo;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ArticleSyncServiceImpl implements ArticleSyncService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Value("${unitouch.url}")
	private String unitouchUrl;

	@Autowired
	private HttpService httpService;

	/**
	 * Method to call the Unitouch API to sync the linking details
	 * 
	 * @param manuscriptCode       - Current manuscriptCode - Could be of original
	 *                             Manuscript, Commentary, Letter to Editor etc.
	 * @param linkedManuscriptCode - Linked manuscriptCode - Could be of original
	 *                             Manuscript, Commentary, Letter to Editor
	 *                             depending on what is is manuscriptCode used for.
	 * @param manuscriptType
	 *
	 *                             Change history
	 * 
	 *                             <pre>
	 * 11-09-2022	initial version
	 *                             </pre>
	 */
	@Override
	public void syncArticleDetails(String manuscriptCode, String linkedManuscriptCode, String manuscriptType) {
		logger.info("AuthorRestController.syncArticleDetails start --- mainManuscriptCode : " + manuscriptCode
				+ ", linkedManuscriptCode : " + linkedManuscriptCode + ",manuscriptType :" + manuscriptType);
		String uri = unitouchUrl + "/updateArticle";

		HttpServiceRequestInfo requestInfo = prepareHttpServiceRequestInfo(uri, manuscriptCode, linkedManuscriptCode,
				manuscriptType);
		String resultString = httpService.callJsonPOSTService(requestInfo);

		logger.info("Response from Unitouch API AuthorRestController.updateArticle: " + resultString);
	}

	private HttpServiceRequestInfo prepareHttpServiceRequestInfo(String apiURL, String manuscriptCode,
			String linkedManuscriptCode, String manuscriptType) {
		HttpServiceRequestInfo requestInfo = new HttpServiceRequestInfo();
		requestInfo.setRequestUri(apiURL);
		requestInfo.setCharset(StandardCharsets.UTF_8.name());
		requestInfo.setMethodType(AppConstants.POST);

		StringBuilder jsonStringBuilder = new StringBuilder();
		jsonStringBuilder.append("{");
		jsonStringBuilder.append("\"manuscriptCode\":\"");
		jsonStringBuilder.append(manuscriptCode);
		jsonStringBuilder.append("\",\"linkedManuscriptCode\":\"");
		jsonStringBuilder.append(linkedManuscriptCode);
		jsonStringBuilder.append("\",\"manuscriptType\":\"");
		jsonStringBuilder.append(manuscriptType);
		jsonStringBuilder.append("\"}");
		logger.info("Unitouch updateArticle request body : " + jsonStringBuilder);
		requestInfo.setRequestBody(jsonStringBuilder.toString());

		return requestInfo;
	}

}
