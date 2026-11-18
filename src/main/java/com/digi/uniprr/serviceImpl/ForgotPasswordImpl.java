package com.digi.uniprr.serviceImpl;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.model.Journal;
import com.digi.uniprr.model.MailQueue;
import com.digi.uniprr.model.Role;
import com.digi.uniprr.model.OtherMail;
import com.digi.uniprr.model.User;
import com.digi.uniprr.model.UserJournal;
import com.digi.uniprr.repository.JournalsRepo;
import com.digi.uniprr.repository.MailQueueRepo;
import com.digi.uniprr.repository.RoleRepo;
import com.digi.uniprr.repository.UserJournalRepo;
import com.digi.uniprr.repository.OtherMailRepo;
import com.digi.uniprr.repository.UserRepo;
import com.digi.uniprr.service.ForgotPassword;
import com.digi.uniprr.utils.SendMailUtils;


@Service
@Transactional
public class ForgotPasswordImpl implements ForgotPassword {

	@Autowired
	SendMailUtils mailUtils;

	@Autowired
	UserRepo userRepo;

	@Autowired
	Environment env;
	
	@Autowired
	JournalsRepo journalsRepo;
	
	@Autowired
	OtherMailRepo otherMailRepo;

	@Autowired
	UserJournalRepo userJournalRepo;
	
	@Autowired
	RoleRepo roleRepo;
	
	@Autowired
	MailQueueRepo mailQueueRepo;

	@Override
	public String sendMailForPassword(String userEmail, String token,String userName,String forgetPasswordUrl,Integer journalId,Integer userId) {
			userRepo.updateTokenByUser(userEmail,token);
			String fromMailID = env.getProperty("spring.mail.username");
			String toMailID = userEmail;
			String mailSubject = "Forgot Password";
			Journal i= journalsRepo.getById(journalId);
			String mailBody = ForgotPasswaordMailTemplate(token,userName,userEmail,forgetPasswordUrl,userId, i.getJournalCode());
			
			OtherMail add=new OtherMail();
			add.setJournalId(journalId);
			add.setToMail(toMailID);
			add.setFromMail(fromMailID);
			add.setSubject(mailSubject);
			add.setMailBody(mailBody);
			if(i!=null && i.getJournalMetadata()!=null) {
				if(i.getJournalMetadata().getJournalEmail()!=null && !i.getJournalMetadata().getJournalEmail().isEmpty()) {
					MailQueue addMailQueue = new MailQueue();
					addMailQueue.setMailFrom(fromMailID);
					addMailQueue.setMailTo(toMailID);
					addMailQueue.setJournalId(journalId);
					addMailQueue.setManuscriptId(0);
					addMailQueue.setMailSubject(mailSubject);
					addMailQueue.setMailBody(mailBody);
					addMailQueue.setMail_send_date(new Date());
					addMailQueue.setIsActive(0);
					mailQueueRepo.saveAndFlush(addMailQueue);
					
					add.setStatus("Insert into mailQueue");
					add.setCreatedAt(new Date());
	                otherMailRepo.save(add);
					
//					String a=mailUtils.sendEmail(fromMailID, toMailID, mailSubject, mailBody,i.getJournalMetadata().getJournalEmail(),i.getJournalMetadata().getJournalEmailPassword(),i.getJournalMetadata().getJournalAlias());
//					if (a != null && !a.isEmpty() && a.equalsIgnoreCase("success")) {
//		                add.setStatus("success");
//		                add.setCreatedAt(new Date());
//		                otherMailRepo.save(add);
//						} else {
//							add.setStatus("fail");
//							add.setCreatedAt(new Date());
//			                otherMailRepo.save(add);
//						}
				}else {
					MailQueue addMailQueue = new MailQueue();
					addMailQueue.setMailFrom(fromMailID);
					addMailQueue.setMailTo(toMailID);
					addMailQueue.setJournalId(journalId);
					addMailQueue.setManuscriptId(0);
					addMailQueue.setMailSubject(mailSubject);
					addMailQueue.setMailBody(mailBody);
					addMailQueue.setMail_send_date(new Date());
					addMailQueue.setIsActive(0);
					mailQueueRepo.saveAndFlush(addMailQueue);
					
					add.setStatus("Insert into mailQueue");
					add.setCreatedAt(new Date());
	                otherMailRepo.save(add);
//					String a=mailUtils.sendEmail(fromMailID, toMailID, mailSubject, mailBody,"","","");
//					if (a != null && !a.isEmpty() && a.equalsIgnoreCase("success")) {
//		                add.setStatus("success");
//		                add.setCreatedAt(new Date());
//		                otherMailRepo.save(add);
//						} else {
//							add.setStatus("fail");
//							add.setCreatedAt(new Date());
//			                otherMailRepo.save(add);
//						}
				}
			}else {
				MailQueue addMailQueue = new MailQueue();
				addMailQueue.setMailFrom(fromMailID);
				addMailQueue.setMailTo(toMailID);
				addMailQueue.setJournalId(journalId);
				addMailQueue.setManuscriptId(0);
				addMailQueue.setMailSubject(mailSubject);
				addMailQueue.setMailBody(mailBody);
				addMailQueue.setMail_send_date(new Date());
				addMailQueue.setIsActive(0);
				mailQueueRepo.saveAndFlush(addMailQueue);
				
				add.setStatus("Insert into mailQueue");
				add.setCreatedAt(new Date());
                otherMailRepo.save(add);				
//			String a=mailUtils.sendEmail(fromMailID, toMailID, mailSubject, mailBody,"","","");
//			if (a != null && !a.isEmpty() && a.equalsIgnoreCase("success")) {
//                add.setStatus("success");
//                add.setCreatedAt(new Date());
//                otherMailRepo.save(add);
//				} else {
//					add.setStatus("fail");
//					add.setCreatedAt(new Date());
//	                otherMailRepo.save(add);
//				}
			}
			String message = "Password reset URL send on registred mail successfully.";
			return message;
	}

	
	private String ForgotPasswaordMailTemplate(String token,String userName,String email,String forgetPasswordUrl,Integer userId,String journalCode) {
		StringBuilder builder = new StringBuilder();
		String resetPasswordURL=forgetPasswordUrl + "?token=" + token + "&email=" + email+ "&journal=" + journalCode;
		builder.append("<p> Dear" + " " + userName + "." + "</p>");
		builder.append("<p></p>");
		builder.append("<p> Greetings from Medknow Publications!  </p>");
		builder.append("<p></p>");
		builder.append("<p> A request has been received to change the password for your account on Review JOW website.  </p>");
		builder.append("<p></p>");
		builder.append("<p> To reset your password, please click on the below link or copy paste this URL into your browser: </p>");
		builder.append("<p> <b>Reset Password Link:</b> (<a href=" + resetPasswordURL + "  target=\"blank\">" + resetPasswordURL + "</a>)</p>");
		builder.append("<p><b>NOTE: </b> Please note this URL is only valid for 24 hours. If the link you’ve clicked has expired, then please re-generate a new reset password link using the ‘Forgot Password’ option from the login area.</p>");
		builder.append("<p></p>");
		builder.append("<p><b> Email Address (username):</b> " + email  + "</p>");
		builder.append("<p></p>");
		builder.append("<p>It is advisable to create a strong and complex password for security purpose with following </p>");
		builder.append("<p>characteristics:</p>");
		builder.append("<p><ul><li>At least 8 characters—the more characters, the better</li></p>");
		builder.append("<p><li>A mixture of both uppercase and lowercase letters</li></p>");
		builder.append("<p><li>A mixture of letters and numbers</li></p>");
		builder.append("<p><li>Inclusion of at least one special character, e.g. [! @ # ? ]</li></ul></p>");
		
		builder.append("<p></p>");
		List<UserJournal> k= userJournalRepo.getUserJournalByUserIdActive(userId);
		if(k!=null && !k.isEmpty()) {
			int count =1;
			builder.append("<p><b>Roles and Journals associated to your account: </b> You have an access to the following Journal with respective roles:</p>");
			for(UserJournal i: k) {
				builder.append("<p><b>" + count + "</b>. " + i.getJournal().getJournalTitle() + "</p>");
				builder.append("<p>" + "<a href=" +i.getJournal().getJournalMetadata().getUrl() + " target=\"blank\">" +i.getJournal().getJournalMetadata().getUrl()+"</a>" + "</p>");
				builder.append("<ul>");
				List<UserJournal> getUserJournalDeatils=userJournalRepo.getUserJournalByJournalUNiqueList(userId, i.getJournalId());
				if(getUserJournalDeatils!=null && !getUserJournalDeatils.isEmpty()) {
					int roleCount=1;
					for(UserJournal o:getUserJournalDeatils) {
						Role d=roleRepo.getById(o.getRoleId());
						if(d!=null)
						builder.append("<p><b>" + roleCount + "</b>. " + d.getRoleName() + "</p>");
						roleCount++;
					}
				}
				builder.append("</ul>");
				count++;
			}
		}
		
		builder.append("<p></p>");
		builder.append("<p> Using the same password, you can access all accounts & roles associated with your email address across medknow Journals on Review JOW.</p>");
		builder.append("<p></p>");
		builder.append("<p> If you are still unable to access your account, please contact Medknow Tech Support Team at </p>");
		builder.append("<p> WKHLRPMedknow_techsupport@wolterskluwer.com. </p>");
		builder.append("<p></p>");
		builder.append("<p> If you didn't request a password reset, please ignore this message; your password will not be changed.</p>");
		builder.append("<p> You can also visit the <b>‘Login Help’</b> section on the log in page for more information.</p>");
		
		builder.append("<p></p>");
		builder.append("<p> Thank you.</p>");
		builder.append("<p> Best Regards </p>");
		builder.append("<p></p>");
		builder.append("<p> Medknow Publications| Wolters Kluwer Pvt. Ltd. </p>");
		builder.append("<p><hr></p>");
		builder.append("<p></p>");
		builder.append("<p><b>Please contact the Medknow Publication Tech Support Team (Email ID: </b></p>");
		builder.append("<p><b>WKHLRPMedknow_techsupport@wolterskluwer.com.) if you have any questions.</b> </p>");
		
		return builder.toString();

	}
//	private String ForgotPasswaordMailTemplate(String token,String userName,String email,String forgetPasswordUrl,Integer userId) {
//		StringBuilder builder = new StringBuilder();
//		String resetPasswordURL=forgetPasswordUrl + "?token=" + token + "&email=" + email;
//		builder.append("<p> Welcome" + " " + userName + "." + "</p>");
//		builder.append("<p> Click on the link given below or copy and paste the entire link to browser address bar to reset your password. </p>");
//		builder.append("<p> (<a href=" + resetPasswordURL + "  target=\"blank\">" + resetPasswordURL + "</a>)</p>");
//		builder.append("<p> Your Username is :" + email + "." + "</p>");
//		List<UserJournal> k= userJournalRepo.getUserJournalByUserIdActive(userId);
//		if(k!=null && !k.isEmpty()) {
//			int count =1;
//			builder.append("<p><b>You have an access to the following Journal with respective roles:</b></p>");
//			for(UserJournal i: k) {
//				builder.append("<p><b>" + count + "</b>. " + i.getJournal().getJournalTitle() + "</p>");
//				builder.append("<p>" + "<a href=" +i.getJournal().getJournalMetadata().getUrl() + " target=\"blank\">" +i.getJournal().getJournalMetadata().getUrl()+"</a>" + "</p>");
//				builder.append("<ul>");
//				List<UserJournal> getUserJournalDeatils=userJournalRepo.getUserJournalByJournalUNiqueList(userId, i.getJournalId());
//				if(getUserJournalDeatils!=null && !getUserJournalDeatils.isEmpty()) {
//					int roleCount=1;
//					for(UserJournal o:getUserJournalDeatils) {
//						Role d=roleRepo.getOne(o.getRoleId());
//						if(d!=null)
//						builder.append("<p><b>" + roleCount + "</b>. " + d.getRoleName() + "</p>");
//						roleCount++;
//					}
//				}
//				builder.append("</ul>");
//				count++;
//			}
//		}
//		builder.append("<p> Thank you.</p>");
//		builder.append("<p></p>");
//		builder.append("<p>Best Regards</p>");
//		builder.append("<p>Editor in chief</p>");
//		return builder.toString();
//
//	}

	@Override
	public List<User> findUserDetailsByToken(String token) {
		return userRepo.findUserDetailsByToken(token);
	}

	@Override
	public void saveUpdatedPassword(String token, String password) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String encodedPassword = encoder.encode(password);
		userRepo.updateUserPassword(encodedPassword, token);
		userRepo.deleteTokenByUser(token,"");
		
		
	}

	@Override
	public String getResetPasswordLink(IDVO idvo) {
		if(idvo.getEmail()!=null && !idvo.getEmail().isEmpty() && idvo.getToken()!=null && !idvo.getToken().isEmpty()) {
			User existingUser = userRepo.findByEmail(idvo.getEmail());
			String resetPasswordURL=null;
			Journal i= journalsRepo.getById(idvo.getJournalId());
			if(existingUser!=null) {
				if(existingUser.getToken()!=null && !existingUser.getToken().isEmpty()) {
					resetPasswordURL=idvo.getToken() + "?token=" + existingUser.getToken() + "&email=" + idvo.getEmail()+ "&journal=" + i.getJournalCode();
					return resetPasswordURL;
				}else {
					String token = UUID.randomUUID().toString();
					existingUser.setToken(token);
					userRepo.save(existingUser);
					resetPasswordURL=idvo.getToken() + "?token=" + token + "&email=" + idvo.getEmail()+ "&journal=" + i.getJournalCode();
					return resetPasswordURL;
				}
			}else{
				return "User not found";
			}
			
		}else {
			return "Please provide email id";
		}
		
	}

	

}