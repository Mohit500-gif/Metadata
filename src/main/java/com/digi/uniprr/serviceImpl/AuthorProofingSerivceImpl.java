package com.digi.uniprr.serviceImpl;

import java.io.BufferedOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.VO.ImpactArticleDTO;
import com.digi.uniprr.VO.ImpactVo;
import com.digi.uniprr.model.ManuScript;
import com.digi.uniprr.repository.ManuScriptRepo;
import com.digi.uniprr.service.AuthorProofingService;
import com.digi.uniprr.service.HttpService;
import com.digi.uniprr.service.MailQueueService;
import com.digi.uniprr.utils.AppConstants;
import com.digi.uniprr.utils.HttpServiceRequestInfo;
import com.digi.uniprr.utils.SftpUtil;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpATTRS;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuthorProofingSerivceImpl implements AuthorProofingService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private HttpService httpService;

	@Autowired
	private ManuScriptRepo manuScriptRepo;

	@Autowired
	private MailQueueService mailQueueService;

	@Value("${uniprrFolderPath}")
	private String uniprrFolderPath;

	@Value("${enable.unitouch.integration}")
	private boolean enableUnitouchIntegration;

	@Value("${unitouch.url}")
	private String unitouchUrl;

	@Value("${spring.mail.username}")
	private String mailFrom;

	@Value("${support.email}")
	private String mailTo;

	@Value("${supportCC.email}")
	private String mailCC;

	@Value("${awsIp}")
	private String sftpIp;

	@Value("${awsPort}")
	private int sftpPort;

	@Value("${awsHost}")
	private String sftpUser;

	@Value("${awsPassword}")
	private String sftpPassword;

	@Value("${environment.type}")
	private String environmentType;

	@Override
	public String getManuscriptOutputFile(IDVO vo) {
		ManuScript getManuScript = manuScriptRepo.getById(vo.getId());
		if (getManuScript != null)
			return getManuScript.getInputFile();
		return null;
	}

	@Override
	public String uploadManuscriptInputFile(Integer manuScriptId, MultipartFile[] files, String isRecheck,
			String noChangeApproved, String comment, int mansucriptStatusId) {
		logger.info("Inside in uploadManuscriptInputFile Service Impl");
		ManuScript manuScript = manuScriptRepo.getById(manuScriptId);
		Map<String, String> filePathMap = new HashMap<>();
		if (manuScript != null) {
			logger.info("Get manuscript Data");
			if (manuScript.getOutputFile() != null && !manuScript.getOutputFile().isEmpty()) {
				String attachmentFolderPath = uniprrFolderPath + File.separator + "UniprrAuthorProofingFolder";
				File dir = new File(attachmentFolderPath);
				if (!dir.exists()) {
					dir.mkdirs();
					logger.info("Attachment Folder has been created  in uniprr");
				}

				if (files != null) {
					if (enableUnitouchIntegration) {
						MultipartFile file = null;
						File serverFile = null;
						File targetFile = null;
						ChannelSftp sftpChannel = SftpUtil.createSFTPChannel(sftpIp, sftpPort, sftpUser, sftpPassword);
						for (int i = 0; i < files.length; i++) {
							file = files[i];
							logger.info("Step1 : Uploaded file " + file.getOriginalFilename());
							serverFile = new File(dir.getAbsolutePath() + File.separator + file.getOriginalFilename());
							logger.info("Step2 : Uploaded file " + serverFile.getAbsolutePath());
							try (BufferedOutputStream stream = new BufferedOutputStream(
									new FileOutputStream(serverFile));) {
								byte[] bytes = file.getBytes();
								stream.write(bytes);
								stream.flush();
								stream.close();
								// filePathMap.put(file.getOriginalFilename(), manuScript.getOutputFile() +
								// File.separator + file.getOriginalFilename());

								String[] filenameArr = file.getOriginalFilename().split("[.]");
								String lname = filenameArr[filenameArr.length - 1];
								String newFileName = file.getOriginalFilename().replace("." + lname, "");
								if (AppConstants.ENVIRONMENT_PROD.equalsIgnoreCase(environmentType)) {
									if (sftpChannel != null) {
										SftpATTRS attrs = null;
										try {
											attrs = sftpChannel.stat(manuScript.getOutputFile() + File.separator
													+ file.getOriginalFilename());
										} catch (Exception e) {
											logger.info(sftpChannel + " : " + manuScript.getOutputFile()
													+ File.separator + file.getOriginalFilename() + " not found");
											logger.info("error occur is ::" + e);
										}
										int count = 1;
										String fileName = file.getOriginalFilename();
										while (attrs != null) {
											fileName = newFileName + count + "." + lname;
											try {
												attrs = sftpChannel
														.stat(manuScript.getOutputFile() + File.separator + fileName);
											} catch (Exception e) {
												attrs = null;
												logger.info(sftpChannel + " : " + manuScript.getOutputFile()
														+ File.separator + fileName + " not found");
											}
											count++;
										}
										sftpChannel.put(new FileInputStream(serverFile),
												manuScript.getOutputFile() + File.separator + fileName);
										filePathMap.put(fileName,
												manuScript.getOutputFile() + File.separator + fileName);

//										sftpChannel.put( new FileInputStream(serverFile), manuScript.getOutputFile()+ File.separator + file.getOriginalFilename());
									}
								} else {
									targetFile = new File(
											manuScript.getOutputFile() + File.separator + file.getOriginalFilename());
									logger.info("Locally copying file " + serverFile.toString()
											+ ", to unitouch path : " + targetFile.toString());
									// FileUtils.copyFile(serverFile, targetFile);
									int count = 1;
									while (targetFile.exists()) {
										logger.info(targetFile.getParent().toString() + File.separator + newFileName
												+ count + "." + lname);
										targetFile = new File(targetFile.getParent().toString() + File.separator
												+ newFileName + count + "." + lname);
										count++;
									}
									FileUtils.copyInputStreamToFile(new FileInputStream(serverFile), targetFile);
									filePathMap.put(targetFile.getName(),
											manuScript.getOutputFile() + File.separator + targetFile.getName());
								}
								FileUtils.forceDelete(
										new File(attachmentFolderPath + File.separator + file.getOriginalFilename()));
								logger.info("File has been deleted from attachement folder");
							} catch (Exception e) {
								logger.error(
										"------------------------------Exception due to code-------------------" + e);
								String body = " Error in moving file from UNIPRR to UNITOUCH for Manuscript  : "
										+ manuScript.getManuscriptCode() + "\nFailure Reason: " + e.getMessage();
								String subject = "Error in Author proofing for Article "
										+ manuScript.getManuscriptCode();
								Integer journalId = manuScript.getJournalId();
								Integer manuscriptId = manuScript.getManuScriptId();
								mailQueueService.insertMailQueue(mailFrom, mailTo, mailCC, journalId, manuscriptId,
										subject, body);
							}
						}
						SftpUtil.closeSftpConnection(sftpChannel);
					} else {
						MultipartFile file = null;
						File serverFolder = new File(attachmentFolderPath + File.separator + manuScriptId);
						if (!serverFolder.exists()) {
							serverFolder.mkdirs();
							logger.info("Author check/proof manuscript folder has been created in uniprr");
						}

						File serverFile = null;
						for (int i = 0; i < files.length; i++) {
							file = files[i];
							serverFile = new File(
									serverFolder.getAbsolutePath() + File.separator + file.getOriginalFilename());
							try (BufferedOutputStream stream = new BufferedOutputStream(
									new FileOutputStream(serverFile));) {
								byte[] bytes = file.getBytes();
								stream.write(bytes);
							} catch (Exception e) {
								logger.error(
										"------------------------------Exception due to code-------------------" + e);
								String body = " Error in saving Author Check/Proof file for Manuscript  : "
										+ manuScript.getManuscriptCode() + "\nFailure Reason: " + e.getMessage();
								String subject = "Error in Author proofing for Article "
										+ manuScript.getManuscriptCode();
								Integer journalId = manuScript.getJournalId();
								Integer manuscriptId = manuScript.getManuScriptId();
								mailQueueService.insertMailQueue(mailFrom, mailTo, mailCC, journalId, manuscriptId,
										subject, body);
							}
						}
					}
				}

				boolean flag = true;
				if (enableUnitouchIntegration) {
					logger.info("Before calling callUnitouch");
					flag = callUnitouch(manuScript, isRecheck, noChangeApproved, comment, filePathMap);
					logger.info("After calling callUnitouch, flag : " + flag);
				}

				if (flag) {
					manuScript.setManuscriptStatusId(mansucriptStatusId);
					manuScript.setAuthorProofDate(new Date());
					manuScriptRepo.save(manuScript);
					return "You successfully uploaded file=" + (files != null ? files.length : 0);
				} else {
					return "Failed to update status, Please contact support";
				}
			} else {
				return "There is no location to upload";
			}
		} else {
			return "There is no manuscript data";
		}
	}
	

	public boolean callUnitouch(ManuScript manuscript, String isRecheck, String noChangeApproved, String comment,
			Map<String, String> filePathMap) {
		boolean responseFlag = false;
		HttpServiceRequestInfo requestInfo = prepareHttpServiceRequestInfo(unitouchUrl + "taskComplete", manuscript,
				isRecheck, noChangeApproved, comment, filePathMap);
		String resultString = httpService.callJsonPOSTService(requestInfo);
		logger.info("callUnitouch response : " + resultString + ", for manuscript : " + manuscript.getManuscriptCode());

		if (resultString.equalsIgnoreCase("Success")) {
			responseFlag = true;
		} else {
			logger.error("Failed response unitouch URL-------------------" + resultString);

			String body = " Error in Calling UNITOUCH API for Manuscript  : " + manuscript.getManuscriptCode()
					+ "\nFailure Reason: " + resultString;
			String subject = "Error in Author proofing for Article " + manuscript.getManuscriptCode();
			Integer journalId = manuscript.getJournalId();
			Integer manuscriptId = manuscript.getManuScriptId();
			mailQueueService.insertMailQueue(mailFrom, mailTo, mailCC, journalId, manuscriptId, subject, body);
		}

		return responseFlag;
	}

	private HttpServiceRequestInfo prepareHttpServiceRequestInfo(String apiURL, ManuScript manuscript, String isRecheck,
			String noChangeApproved, String comment, Map<String, String> filePathMap) {
		HttpServiceRequestInfo requestInfo = new HttpServiceRequestInfo();
		requestInfo.setRequestUri(apiURL);
		requestInfo.setCharset(StandardCharsets.UTF_8.name());
		requestInfo.setMethodType(AppConstants.POST);

		String commentJson = JSONValue.toJSONString(comment + " Recheck : " + isRecheck);
		String filePathJson = JSONValue.toJSONString(filePathMap);

		StringBuilder jsonStringBuilder = new StringBuilder();
		jsonStringBuilder.append("{");
		jsonStringBuilder.append("\"manuscriptTaskId\":\"");
		jsonStringBuilder.append(manuscript.getManuscriptTaskId());
		jsonStringBuilder.append("\",\"isRecheck\":\"");
		jsonStringBuilder.append(isRecheck);
		jsonStringBuilder.append("\",\"noChangeApproved\":\"");
		jsonStringBuilder.append(noChangeApproved);
		jsonStringBuilder.append("\",\"comment\":");
		jsonStringBuilder.append(commentJson);
		jsonStringBuilder.append(",\"filePathMap\":");
		jsonStringBuilder.append(filePathJson);
		jsonStringBuilder.append("}");
		logger.info("Author Proofing request body : " + jsonStringBuilder);
		requestInfo.setRequestBody(jsonStringBuilder.toString());

		// String jsonInputString = "{\"manuscriptTaskId\":" +"\""
		// +manuscript.getManuscriptTaskId() +"\"" +"," +"\"isRecheck\":" +"\""
		// +isRecheck+"\"" +"," +"\"noChangeApproved\":" +"\"" +noChangeApproved+"\""
		// +"," +"\"comment\":" +"\"" +comment +" Recheck : "+isRecheck +"\"" +","
		// +"\"filePathMap\":" +obj +"}";

		return requestInfo;
	}
	
	public List<ImpactArticleDTO> callFetchImpact(String aid) {
	    List<ImpactArticleDTO> dtoList = new ArrayList<>();

	    try {
	        String url = unitouchUrl.endsWith("/") 
	                ? unitouchUrl + "fetch/" + aid 
	                : unitouchUrl + "/fetch/" + aid;

	        logger.info("Calling fetchImpact API : {}", url);

	        HttpServiceRequestInfo requestInfo = new HttpServiceRequestInfo();
	        requestInfo.setRequestUri(url);
	        requestInfo.setCharset(StandardCharsets.UTF_8.name());
	        requestInfo.setMethodType(AppConstants.GET);

	        // Call the GET service
	        String jsonResult = httpService.callGETService(requestInfo);
	        logger.info("Fetch Impact API response: {}", jsonResult);

	        if (jsonResult != null && !jsonResult.trim().isEmpty()) {
	            ObjectMapper mapper = new ObjectMapper();
	            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	            ImpactArticleDTO[] arr = mapper.readValue(jsonResult, ImpactArticleDTO[].class);
	            dtoList = Arrays.asList(arr);
	        } else {
	            logger.warn("Fetch Impact API returned empty response for aid: {}", aid);
	        }
	    } catch (Exception e) {
	        logger.error("Error calling or parsing fetchImpact API for aid: {}", aid, e);
	    }

	    return dtoList;
	}
	
	
	public List<ImpactArticleDTO> callFetchImpactBulk(List<String> aidList) {
	    List<ImpactArticleDTO> dtoList = new ArrayList<>();

	    try {
	        // Build the URL
	        String url = unitouchUrl.endsWith("/") 
	                ? unitouchUrl + "fetchBulk"
	                : unitouchUrl + "/fetchBulk/";

	        logger.info("Calling fetchImpactBulk API (POST): {}", url);

	        // Prepare request
	        HttpServiceRequestInfo requestInfo = new HttpServiceRequestInfo();
	        requestInfo.setRequestUri(url);
	        requestInfo.setCharset(StandardCharsets.UTF_8.name());
	        requestInfo.setMethodType(AppConstants.POST);

	        // Serialize the list of AIDs as JSON
	        ObjectMapper mapper = new ObjectMapper();
	        String payload = mapper.writeValueAsString(aidList);
	        requestInfo.setRequestBody(payload);

	        // Add required headers
	        Map<String, String> headers = new HashMap<>();
	        headers.put("Content-Type", "application/json");
	        requestInfo.setHeaders(headers);

	        // Call the POST service
	        String jsonResult = httpService.callJsonPOSTService(requestInfo);
	        logger.info("Fetch Impact Bulk API response: {}", jsonResult);

	        if (jsonResult != null && !jsonResult.trim().isEmpty() && !"Failed".equalsIgnoreCase(jsonResult)) {
	            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	            ImpactArticleDTO[] arr = mapper.readValue(jsonResult, ImpactArticleDTO[].class);
	            dtoList = Arrays.asList(arr);
	        } else {
	            logger.warn("Fetch Impact Bulk API returned empty or failed response");
	        }
	    } catch (Exception e) {
	        logger.error("Error calling or parsing fetchImpactBulk API", e);
	    }

	    return dtoList;
	}




}
