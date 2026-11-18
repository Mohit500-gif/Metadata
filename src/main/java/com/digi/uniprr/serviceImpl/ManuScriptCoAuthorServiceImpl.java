package com.digi.uniprr.serviceImpl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.VO.ManuscriptCoAuthorVO;
import com.digi.uniprr.model.MailQueue;
import com.digi.uniprr.model.ManuScript;
import com.digi.uniprr.model.ManuScriptCoAuthor;
import com.digi.uniprr.model.User;
import com.digi.uniprr.model.UserJournal;
import com.digi.uniprr.repository.MailQueueRepo;
import com.digi.uniprr.repository.ManuScriptCoAuthorRepo;
import com.digi.uniprr.repository.ManuScriptRepo;
import com.digi.uniprr.repository.UserJournalRepo;
import com.digi.uniprr.repository.UserRepo;
import com.digi.uniprr.service.HttpService;
import com.digi.uniprr.service.ManuScrpitCoAuthorService;
import com.digi.uniprr.utils.AppConstants;
import com.digi.uniprr.utils.HttpServiceRequestInfo;

@Service
@Transactional
public class ManuScriptCoAuthorServiceImpl implements ManuScrpitCoAuthorService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ManuScriptCoAuthorRepo manuScriptCoAuhor;

	@Autowired
	private Environment env;

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private ManuScriptRepo manuScriptRepo;

	@Autowired
	private UserJournalRepo userJournalRepo;
	
	@Autowired
	private MailQueueRepo mailQueueRepo;
	
	@Autowired
	private HttpService httpService;
	
	@Value("${enable.unitouch.integration}")
	private boolean enableUnitouchIntegration;
	
	@Value("${unitouch.url}")
	private String unitouchUrl;

	@Override
	public void createCoAuthor(ManuscriptCoAuthorVO manuscriptCoAuthorVO) {
		ManuScript getManuScript = manuScriptRepo.getOne(manuscriptCoAuthorVO.getManuscriptId());
		if (getManuScript != null) {
			User user;
			if(manuscriptCoAuthorVO.getId() != null && manuscriptCoAuthorVO.getId() > 0) {
				user = userRepo.getById(manuscriptCoAuthorVO.getId());
			}else {
				user = userRepo.findByEmail(manuscriptCoAuthorVO.getEmail());
			}
			if (user != null) {
				UserJournal i = userJournalRepo.getUserJournal(user.getId(), getManuScript.getJournalId(), 4);
				if (i != null) {
					i.setIsActive(1);
					userJournalRepo.save(i);
				} else {
					UserJournal userJournals = new UserJournal();
					userJournals.setJournalId(getManuScript.getJournalId());
					userJournals.setUserId(user.getId());
					userJournals.setRoleId(4);
					userJournals.setIsActive(1);
					userJournals.setUpdatedDate(new Date());
					userJournals.setUpdatedBy(getManuScript.getAuthorId());
					userJournalRepo.save(userJournals);
				}
				if (manuscriptCoAuthorVO.getOrcidId() != null && !manuscriptCoAuthorVO.getOrcidId().isEmpty()) {
					user.setOrcidId(manuscriptCoAuthorVO.getOrcidId());
					userRepo.save(user);
				}

				ManuScriptCoAuthor manuScriptCoAuthor = new ManuScriptCoAuthor();
				manuScriptCoAuthor.setCoAuthorId(user.getId());
				manuScriptCoAuthor.setCoAuthorManuscriptId(manuscriptCoAuthorVO.getManuscriptId());
				manuScriptCoAuthor.setAuthorOrder(manuscriptCoAuthorVO.getOrder());
				manuScriptCoAuthor.setCorrespondingAuthor(manuscriptCoAuthorVO.getCorrespondingAuthor());
				manuScriptCoAuthor.setCoAuthorStatus(1);
				manuScriptCoAuthor.setIsEmailSent(0);
				manuScriptCoAuthor.setCoAuthorType(0);
				// 12/12/2021 - Added for co-author
				manuScriptCoAuthor.setIsCorrespondingAuthor(manuscriptCoAuthorVO.getIsCorrespondingAuthor().isEmpty() ? "N" : manuscriptCoAuthorVO.getIsCorrespondingAuthor());
				manuScriptCoAuhor.save(manuScriptCoAuthor);
			}
		}

	}

	@Override
	public List<User> isExistByMail(String email) {
		// TODO Auto-generated method stub
		return null;
	}

	public String sendMailForActivation(String userEmail, String salutation, String firstName, String lastName,
			String manuscriptTitle, String journalTitle) {
		String fromMailID = env.getProperty("spring.mail.username");
		String toMailID = userEmail;
		String mailSubject = "Notification for Co-Author";
		String mailBody = activateUserMailTemplate(userEmail, salutation, firstName, lastName, manuscriptTitle,
				journalTitle);
		MailQueue addMailQueue = new MailQueue();
		addMailQueue.setMailFrom(fromMailID);
		addMailQueue.setMailTo(toMailID);
		addMailQueue.setJournalId(259);
		addMailQueue.setManuscriptId(0);
		addMailQueue.setMailSubject(mailSubject);
		addMailQueue.setMailBody(mailBody);
		addMailQueue.setMail_send_date(new Date());
		addMailQueue.setIsActive(0);
		mailQueueRepo.saveAndFlush(addMailQueue);
		
	//	mailUtils.sendEmail(fromMailID, toMailID, mailSubject, mailBody, "", "", "");
		String message = "Mail send successfully.";
		return message;
	}

	private String activateUserMailTemplate(String email, String salutation, String firstName, String lastName,
			String manuscriptTitle, String journalTitle) {
		StringBuilder builder = new StringBuilder();
		builder.append("<p> Dear " + salutation + "." + firstName + " " + lastName + ",</p>");
		builder.append("<p> You are a co-author for  article with a title: " + manuscriptTitle
				+ " on a journal with a journal name :" + journalTitle + ".</p>");
		builder.append("<p> THANK YOU.</p>");
		builder.append("<p></p>");
		builder.append("<p>Best Regards</p>");
		builder.append("<p>Editor in chief</p>");
		builder.append("<p>" + journalTitle + "</p>");
		return builder.toString();
	}

//	@Override
//	public String updateCopyright(IDVO id) {
//		ManuScriptCoAuthor getManuScriptCoAuthorData = manuScriptCoAuhor.getById(id.getId());
//		
//		if (getManuScriptCoAuthorData != null) {
//			//			manuScriptCoAuhor.save(getManuScriptCoAuthorData);
//			User user = userRepo.getById(getManuScriptCoAuthorData.getCoAuthorId());
//			ManuScript mannuscript = manuScriptRepo.getById(getManuScriptCoAuthorData.getCoAuthorManuscriptId());
//			
//			if(user!=null && mannuscript!=null) {
//				if (mannuscript.getManuscriptStatusId() == 33 || mannuscript.getManuscriptStatusId() == 57
//						|| mannuscript.getManuscriptStatusId() == 61 || mannuscript.getManuscriptStatusId() == 62 || mannuscript.getManuscriptStatusId() == 65) {
//					getManuScriptCoAuthorData.setCopyrightAgreement(id.getCopyrightAgreement());
//					getManuScriptCoAuthorData.setCopyrightAgreementContent(id.getCopyrightAgreementContent());
//					boolean flag = true;
//					if(enableUnitouchIntegration) {
//						flag = updateCopyrightInUnitouch(mannuscript.getManuscriptCode(), id.getCopyrightAgreement(), user.getEmail());
//					} 
//					
//					if (flag) {
//						manuScriptCoAuhor.save(getManuScriptCoAuthorData);
//						return "co author copyright submit";
//					} else {
//						return "Error in calling unitouch";
//					}
//				} else {
//					
//					if(getManuScriptCoAuthorData.getCopyrightAgreement().equals("Yes"))
//					{
//						return "Already co author copyright submit";
//					}
//					else
//					{ 
//						getManuScriptCoAuthorData.setCopyrightAgreement(id.getCopyrightAgreement());
//					    getManuScriptCoAuthorData.setCopyrightAgreementContent(id.getCopyrightAgreementContent());
//						manuScriptCoAuhor.save(getManuScriptCoAuthorData);
//						return "co author copyright submit";
//					}
//				}
//			}
//			return "There is some issue with article id or user id";
//		}
//		return "There is no co author to submit";
//	}

	
	public String updateCopyright(IDVO id) {
        log.info("Starting the update process for copyright agreement. ID: " + id.getId());

        // Normalise the co-author's answer to "Yes" / "No" so it is never saved as NULL/empty
        String rawAnswer = id.getCopyrightAgreement() == null ? "" : id.getCopyrightAgreement().trim().toLowerCase();
        if (rawAnswer.equals("yes") || rawAnswer.equals("y") || rawAnswer.equals("true") || rawAnswer.equals("1"))
            id.setCopyrightAgreement("Yes");
        else
            id.setCopyrightAgreement("No");
        
        ManuScriptCoAuthor getManuScriptCoAuthorData = manuScriptCoAuhor.getById(id.getId());
        if (getManuScriptCoAuthorData != null) {
            log.info("Found ManuscriptCoAuthor data for ID: " + id.getId());
            
            User user = userRepo.getById(getManuScriptCoAuthorData.getCoAuthorId());
            ManuScript mannuscript = manuScriptRepo.getById(getManuScriptCoAuthorData.getCoAuthorManuscriptId());
            
            if (user != null && mannuscript != null) {
                log.info("User and Manuscript found. Manuscript Status ID: " + mannuscript.getManuscriptStatusId());
                
                if (mannuscript.getManuscriptStatusId() == 33 || mannuscript.getManuscriptStatusId() == 57
                        || mannuscript.getManuscriptStatusId() == 61 || mannuscript.getManuscriptStatusId() == 62 || mannuscript.getManuscriptStatusId() == 65) {
                    
                    log.info("Manuscript is in valid status. Updating copyright agreement.");
                    
                    getManuScriptCoAuthorData.setCopyrightAgreement(id.getCopyrightAgreement());
                    getManuScriptCoAuthorData.setCopyrightAgreementContent(id.getCopyrightAgreementContent());
                    boolean flag = true;
                    
                    if (enableUnitouchIntegration) {
                        log.info("Unitouch integration enabled. Calling updateCopyrightInUnitouch...");
                        flag = updateCopyrightInUnitouch(mannuscript.getManuscriptCode(), id.getCopyrightAgreement(), user.getEmail());
                    } 
                    
                    if (flag) {
                        log.info("Successfully updated copyright in Unitouch and local data.");
                        manuScriptCoAuhor.save(getManuScriptCoAuthorData);
                        return "co author copyright submit";
                    } else {
                        log.error("Error in calling Unitouch.");
                        return "Error in calling unitouch";
                    }
                } else {
                    log.warn("Manuscript status is invalid for copyright agreement update.");
                    
                    if (getManuScriptCoAuthorData.getCopyrightAgreement()!=null && getManuScriptCoAuthorData.getCopyrightAgreement().equals("Yes")) {
                        log.info("Copyright agreement already submitted for this co-author.");
                        return "Already co author copyright submit";
                    } else {
                        log.info("Updating copyright agreement and saving.");
                        getManuScriptCoAuthorData.setCopyrightAgreement(id.getCopyrightAgreement());
                        getManuScriptCoAuthorData.setCopyrightAgreementContent(id.getCopyrightAgreementContent());
						boolean flag = true;

						if (enableUnitouchIntegration) {
							log.info("Unitouch integration enabled. Calling updateCopyrightInUnitouch...");
							flag = updateCopyrightInUnitouch(mannuscript.getManuscriptCode(), id.getCopyrightAgreement(), user.getEmail());
						}
						if (flag) {
							log.info("Successfully updated copyright in Unitouch and local data.");
							manuScriptCoAuhor.save(getManuScriptCoAuthorData);
							return "co author copyright submit";
						} else {
							log.error("Error in calling Unitouch.");
							return "Error in calling unitouch";
						}
//						manuScriptCoAuhor.save(getManuScriptCoAuthorData);
//                        return "co author copyright submit";
                    }
                }
            } else {
                log.warn("User or Manuscript not found for co-author.");
                return "There is some issue with article id or user id";
            }
        } else {
            log.warn("No co-author data found for ID: " + id.getId());
            return "There is no co author to submit";
        }
    }

	public boolean updateCopyrightInUnitouch(String manuscriptId, String copyRight, String authorEmail) {
		boolean responseFlag = false;
		HttpServiceRequestInfo requestInfo = prepareHttpServiceRequestInfo(unitouchUrl +  "updateCopyright", manuscriptId, copyRight, authorEmail);
		String resultString = httpService.callJsonPOSTService(requestInfo);
		log.info("callUnitouch updateCopyright response : " + resultString + ", for manuscript : " + manuscriptId);
		if(resultString!=null) {
			JSONParser parser = new JSONParser();
			JSONObject myResponse = null;
			try {
				myResponse = (JSONObject) parser.parse(resultString.toString());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String msg = (String) myResponse.get("msg");
		     if(msg.equalsIgnoreCase("SUCCESS")) {
		    	 responseFlag = true;
		     }
		     	log.info("Inside updateCopyrightInUnitouch() ::Manuscript Code :: Status"+msg);
		}
		
//		if(resultString.equalsIgnoreCase("SUCCESS")) {
//			responseFlag = true;
//		}
		
		return responseFlag;
	}
	
	
	private HttpServiceRequestInfo prepareHttpServiceRequestInfo(String apiURL, String manuscriptId, String copyRight, String authorEmail) {
		HttpServiceRequestInfo requestInfo = new HttpServiceRequestInfo();
		requestInfo.setRequestUri(apiURL);
		requestInfo.setCharset(StandardCharsets.UTF_8.name());
		requestInfo.setMethodType(AppConstants.POST);
		
		String jsonInputString = "{\"manuScriptId\":" +"\"" +manuscriptId +"\"" +"," +"\"copyrightStatus\":" +"\"" +copyRight +"\"" +"," +"\"authorEmail\":" +"\"" +authorEmail +"\"" +"}";
		log.info("jsonInputString   :" + jsonInputString);
		
		requestInfo.setRequestBody(jsonInputString);
		
		return requestInfo;
	}
}
