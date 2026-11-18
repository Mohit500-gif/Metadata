package com.digi.uniprr.serviceImpl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import com.digi.uniprr.model.*;
import com.digi.uniprr.repository.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.digi.uniprr.VO.EmailTriggerVO;
import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.VO.ManuscriptListVO;
import com.digi.uniprr.service.EmailTriggerService;
import com.digi.uniprr.utils.EmailTriggerTemplateUtils;
import com.digi.uniprr.utils.SendMailUtils;

@Service
@Transactional
public class EmailTriggerServiceImpl implements EmailTriggerService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TaskEmailMapRepo taskEmailMapRepo;

    @Autowired
    TaskEmailMapReminderRepo taskEmailMapReminderRepo;

    @Autowired
    ManuScriptRepo manuScriptRepo;

    @Autowired
    UserRepo userRepo;

    @Autowired
    WorkflowRoleRepo workflowRoleRepo;

    @Autowired
    TatEmailConfigurationRepo tatEmailConfigurationRepo;

    @Autowired
    SendMailUtils mailUtils;

    @Autowired
    EmailTriggerTemplateUtils emailTriggerTemplateUtils;

    @Autowired
    Environment env;

    @Autowired
    MailQueueRepo mailQueueRepo;

    @Autowired
    JournalsRepo journalRepo;

    @Autowired
    ManuScriptReviewerRepo manuScriptReviewerRepo;

    @Autowired
    ManuScriptCoAuthorRepo manuScriptCoAuthorRepo;


    @Override
    public String emailTrigger(EmailTriggerVO emailTriggerVO) {
        TaskEmailMap taskEmailMapDetails = null;
        String fromMailID = null;
        String toMailID = null;
        String mailSubject = null;
        String mailBody = null;
        User userDetail = null;
        List<String> mailCcTemp = new ArrayList<>();
        String mailCcString = null;
        try {
            taskEmailMapDetails = taskEmailMapRepo.getTaskEmailMapDetails(emailTriggerVO.getTaskId(), emailTriggerVO.getJournalId());
            if (taskEmailMapDetails != null) {
                if (emailTriggerVO.getUserId() != null && taskEmailMapDetails.getJournalMailTemplates().getEmailTemplateForm().equalsIgnoreCase("NOTIFICATION")) {
                    //Receiver Mail
                    userDetail = userRepo.getById(emailTriggerVO.getUserId());
                    if (userDetail != null && userDetail.getEmail() != null) {
                        if (taskEmailMapDetails.getJournalMailTemplates().getJournal().getJournalMetadata() != null) {
                            if (taskEmailMapDetails.getJournalMailTemplates().getJournal().getJournalMetadata().getJournalEmail() != null && !taskEmailMapDetails.getJournalMailTemplates().getJournal().getJournalMetadata().getJournalEmail().isEmpty()) {
                                fromMailID = taskEmailMapDetails.getJournalMailTemplates().getJournal().getJournalMetadata().getJournalEmail();
                            } else {
                                fromMailID = env.getProperty("fromMail");
                            }
                        } else {
                            fromMailID = env.getProperty("fromMail");
                        }
                        toMailID = userDetail.getEmail();
                        mailSubject = emailTriggerTemplateUtils.getSubject(emailTriggerVO, taskEmailMapDetails);
                        mailBody = emailTriggerTemplateUtils.getAllData(emailTriggerVO, taskEmailMapDetails);
                        log.info("Mailbody before Remove duplicate Salutation: \n" + mailBody);
                        mailBody = mailBody.replaceAll("\\b(\\w+)(\\s+\\1)+\\b", "$1");

                        mailBody = mailBody.replaceAll("\\bfor\\s+(Dr|Ms|Prof|Ms|Mrs|Mx|Miss|Asst)\\b", "");
                        log.info("Mailbody after Remove duplicate Salutation: \n" + mailBody);
                        //Entry in Mail Queue
                        MailQueue addMailQueue = new MailQueue();
                        addMailQueue.setIsActive(emailTriggerVO.getIsActive());
                        addMailQueue.setIsSeen(0);
                        addMailQueue.setTaskId(emailTriggerVO.getTaskId());
                        addMailQueue.setJournalId(emailTriggerVO.getJournalId());
                        addMailQueue.setMail_send_date(new Date());
                        addMailQueue.setMailBody(mailBody);
                        addMailQueue.setMailTo(toMailID);
                        if ((emailTriggerVO.getTaskId() == 46
                                || emailTriggerVO.getTaskId() == 10
                                || emailTriggerVO.getTaskId() == 11
                                || emailTriggerVO.getTaskId() == 18
                                || emailTriggerVO.getTaskId() == 7
                                || emailTriggerVO.getTaskId() == 82
                                || emailTriggerVO.getTaskId() == 4)
                                && taskEmailMapDetails.getJournalMailTemplates() != null) {
                            String ccMailId = taskEmailMapDetails.getJournalMailTemplates().getMailcc();
                            addMailQueue.setMailCC(ccMailId);
                        }
                        addMailQueue.setMailSubject(mailSubject);
                        addMailQueue.setMailTOUserId(userDetail.getId());
                        addMailQueue.setMailToRole(emailTriggerVO.getReceiverRoleId());
                        addMailQueue.setIsActive(0);
                        ManuScript getManuScript = manuScriptRepo.getById(emailTriggerVO.getManuscriptId());
                        Integer manuId = getManuScript.getManuScriptId();
                        ;
                        if (getManuScript != null) {
                            addMailQueue.setManuscriptId(getManuScript.getManuScriptId());
                            addMailQueue.setManuscriptVersion(getManuScript.getManuscriptVersion());
                        }
                        addMailQueue.setMailFrom(fromMailID);
                        addMailQueue.setManuscriptStatus(emailTriggerVO.getManuscriptStatus());

                        //AIAN Requirements starts
                        // 14 for tech mod submit to author, 11 for tech mod notf to author, 10 for revision notf to author
                        // 45 for payment submission charges to auth, 42 for after approving payment to author,
                        // 46 for author side fee notf to auth, 17 for AFP, 38 for auth proof going to unitouch
                        // 28 for manuscript back to auth proof in uniprr, 9 for revision done
                        // 39 for author check back to uniprr, 35 -On Withdrawal request Confirmation Task - To Authors
                        // 84 - EIC rejects Author Withdrawal Request
                        // 16 - EIC Accept Author Withdrawal Request
                        // 18 - Reject For Publication
                        // 40 - Author Check Proofing Complete
                        // 43 - payment approved mail to author
                        if ((emailTriggerVO.getJournalId().equals(1819) || emailTriggerVO.getJournalId().equals(259))
                                && (emailTriggerVO.getTaskId().equals(2)
                                || emailTriggerVO.getTaskId().equals(45)
                                || emailTriggerVO.getTaskId().equals(42)
                                || emailTriggerVO.getTaskId().equals(11)
                                || emailTriggerVO.getTaskId().equals(14)
                                || emailTriggerVO.getTaskId().equals(10)
                                || emailTriggerVO.getTaskId().equals(46)
                                || emailTriggerVO.getTaskId().equals(17)
                                || emailTriggerVO.getTaskId().equals(38)
                                || emailTriggerVO.getTaskId().equals(28)
                                || emailTriggerVO.getTaskId().equals(9)
                                || emailTriggerVO.getTaskId().equals(39)
                                || emailTriggerVO.getTaskId().equals(35)
                                || emailTriggerVO.getTaskId().equals(84)
                                || emailTriggerVO.getTaskId().equals(16)
                                || emailTriggerVO.getTaskId().equals(40)
                                || emailTriggerVO.getTaskId().equals(18)
                                || emailTriggerVO.getTaskId().equals(43))
                                && emailTriggerVO.getReceiverRoleId().equals(4)) {
                            List<ManuScriptCoAuthor> manuscriptCorrespondingAuthors = manuScriptCoAuthorRepo
                                    .getCorrespondingAuthorListFromManuscriptId(emailTriggerVO.getManuscriptId());
                            manuscriptCorrespondingAuthors
                                    .removeIf(manuScriptCoAuthor -> manuScriptCoAuthor
                                            .getCoAuthorId()
                                            .equals(getManuScript.getAuthorId()));
                            if (!manuscriptCorrespondingAuthors.isEmpty()) {
                                for (ManuScriptCoAuthor manuScriptCoAuthor : manuscriptCorrespondingAuthors) {
                                    mailCcTemp.add(manuScriptCoAuthor.getUser().getEmail());
                                }
                            }
                        }
                        if (!mailCcTemp.isEmpty()) {
                            mailCcString = String.join(",", mailCcTemp);
                            if (addMailQueue.getMailCC() != null && !addMailQueue.getMailCC().isEmpty()) {
                                String mailCcFinal = addMailQueue.getMailCC().concat(",").concat(mailCcString);
                                addMailQueue.setMailCC(mailCcFinal);
                            } else {
                                addMailQueue.setMailCC(mailCcString);
                            }
                        }
                        // AIAN Requirements ends


                        mailQueueRepo.save(addMailQueue);
                        return "E-Mail Successfully Trigger";
                    }
                    return "Provided user is not valid";
                } else {

                    return "Provided user is not valid";
                }
            } else {
                Journal journal = journalRepo.getJournalById(emailTriggerVO.getJournalId());
                log.error("[CRTICAL *** Unable to find email template for " + journal.getJournalTitle() + ". Email can not be sent. Please contact admin.]");
                return "Unable to find email template for " + journal.getJournalTitle() + ". Email can not be sent. Please contact admin.";
            }

        } catch (Exception e) {
            e.printStackTrace();

            return "Error:->" + e;
        }

    }


    @Override
    public String sendContactSupportMail(IDVO idvo) {

        String userDetails = "<b>Email From :</b> " + idvo.getFromMail() + "         <b>Email To :</b> " +
                idvo.getToMail() + "\n<b>Subject :</b> " + idvo.getSubject() + "\n -------------------------------------------------------------------------\n";

        MailQueue addMailQueue = new MailQueue();
        addMailQueue.setMailFrom(idvo.getFromMail());
        addMailQueue.setMailTo(idvo.getToMail());
        addMailQueue.setJournalId(idvo.getJournalId());
        addMailQueue.setManuscriptId(0);
        addMailQueue.setMailSubject(idvo.getSubject());
        addMailQueue.setMailBody(userDetails.concat(idvo.getMailBody()));
        addMailQueue.setMail_send_date(new Date());
        addMailQueue.setIsActive(0);
        mailQueueRepo.saveAndFlush(addMailQueue);
        return "Success";
    }


    @Override
    public String resendReviewerInvitationMail(ManuscriptListVO manuscriptListVO) {
        TaskEmailMap taskEmailMapDetails = null;
        String fromMailID = null;
        String toMailID = null;
        String mailSubject = null;
        String mailBody = null;
        User userDetail = null;

        log.info("Entering resendReviewerInvitationMail for ManuscriptId: {}, UserId: {}",
                manuscriptListVO.getManuScriptId(), manuscriptListVO.getUserId());

        try {
            EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
            emailTriggerVO.setJournalId(manuscriptListVO.getJournalId());
            emailTriggerVO.setUserId(manuscriptListVO.getUserId());
            emailTriggerVO.setReviewerUserId(manuscriptListVO.getUserId());

            if (manuscriptListVO.getEditorRoleId().equals(2)) {
                emailTriggerVO.setManagingEditorId(manuscriptListVO.getEditorUserId());
                log.info("Setting Managing Editor Id: {}", manuscriptListVO.getEditorUserId());
            } else {
                emailTriggerVO.setEditorUserId(manuscriptListVO.getEditorUserId());
                log.info("Setting Editor User Id: {}", manuscriptListVO.getEditorUserId());
            }

            emailTriggerVO.setManuscriptId(manuscriptListVO.getManuScriptId());
            emailTriggerVO.setManuscriptStatus(manuscriptListVO.getManuStatusId());
            emailTriggerVO.setReceiverRoleId(manuscriptListVO.getRoleId());

            if (manuscriptListVO.getTemplateId() != null) {
                emailTriggerVO.setTaskId(manuscriptListVO.getTemplateId());
            } else {
                emailTriggerVO.setTaskId(4);
                log.info("TemplateId is null, defaulting TaskId to 4");
            }

            emailTriggerVO.setRoleId(manuscriptListVO.getEditorRoleId());

            // URL Setup
            emailTriggerVO.setUrlManuscript(manuscriptListVO.getManuScriptId());
            emailTriggerVO.setUrlManuscriptVersion(manuscriptListVO.getManuscriptVersion());
            emailTriggerVO.setUrlJournalId(manuscriptListVO.getJournalId());
            emailTriggerVO.setUrlReviewerId(manuscriptListVO.getUserId());
            emailTriggerVO.setUrlManuscriptStatus(manuscriptListVO.getManuStatusId());
            emailTriggerVO.setAcceptByReviewerUrl(manuscriptListVO.getAcceptByReviewerUrl());
            emailTriggerVO.setRejectByReviewerUrl(manuscriptListVO.getRejectByReviewerUrl());
            emailTriggerVO.setViewByReviewerUrl(manuscriptListVO.getViewByReviewerUrl());

            if (manuscriptListVO.getJournalMailTemplatesBody() != null
                    && !manuscriptListVO.getJournalMailTemplatesBody().isEmpty()) {
                emailTriggerVO.setEmailTemplateBody(manuscriptListVO.getJournalMailTemplatesBody());
                log.info("Custom email template body provided");
            }

            log.info("Fetching TaskEmailMap for TaskId: {}, JournalId: {}",
                    emailTriggerVO.getTaskId(), emailTriggerVO.getJournalId());

            taskEmailMapDetails = taskEmailMapRepo.getTaskEmailMapDetails(
                    emailTriggerVO.getTaskId(), emailTriggerVO.getJournalId());

            if (taskEmailMapDetails != null
                    && emailTriggerVO.getUserId() != null
                    && taskEmailMapDetails.getJournalMailTemplates()
                    .getEmailTemplateForm().equalsIgnoreCase("NOTIFICATION")) {

                log.info("Valid TaskEmailMap found. Proceeding to send email.");

                userDetail = userRepo.getOne(emailTriggerVO.getUserId());

                if (userDetail != null && userDetail.getEmail() != null) {

                    log.info("User found with Email: {}", userDetail.getEmail());

                    if (taskEmailMapDetails.getJournalMailTemplates()
                            .getJournal().getJournalMetadata() != null) {

                        if (taskEmailMapDetails.getJournalMailTemplates()
                                .getJournal().getJournalMetadata().getJournalEmail() != null
                                && !taskEmailMapDetails.getJournalMailTemplates()
                                .getJournal().getJournalMetadata().getJournalEmail().isEmpty()) {

                            fromMailID = taskEmailMapDetails.getJournalMailTemplates()
                                    .getJournal().getJournalMetadata().getJournalEmail();
                        } else {
                            fromMailID = env.getProperty("fromMail");
                            log.info("Journal email not found, using default fromMail");
                        }
                    } else {
                        fromMailID = env.getProperty("fromMail");
                        log.info("Journal metadata not found, using default fromMail");
                    }

                    toMailID = userDetail.getEmail();

                    log.info("Preparing email. From: {}, To: {}", fromMailID, toMailID);

                    mailSubject = emailTriggerTemplateUtils.getSubject(emailTriggerVO, taskEmailMapDetails);
                    mailBody = emailTriggerTemplateUtils.getAllData(emailTriggerVO, taskEmailMapDetails);

                    log.info("Sending email...");
                    String message = mailUtils.sendEmail(
                            fromMailID, toMailID, mailSubject, mailBody, fromMailID, "", "");

                    MailQueue addMailQueue = new MailQueue();
                    addMailQueue.setIsSeen(0);
                    addMailQueue.setJournalId(emailTriggerVO.getJournalId());
                    addMailQueue.setMail_send_date(new Date());
                    addMailQueue.setMailBody(mailBody);
                    addMailQueue.setMailTo(toMailID);
                    addMailQueue.setMailSubject(mailSubject);
                    addMailQueue.setMailTOUserId(userDetail.getId());
                    addMailQueue.setMailToRole(emailTriggerVO.getReceiverRoleId());

                    ManuScript getManuScript = manuScriptRepo.getOne(emailTriggerVO.getManuscriptId());
                    if (getManuScript != null) {
                        addMailQueue.setManuscriptId(getManuScript.getManuScriptId());
                        addMailQueue.setManuscriptVersion(getManuScript.getManuscriptVersion());
                    }

                    addMailQueue.setMailFrom(fromMailID);
                    addMailQueue.setManuscriptStatus(emailTriggerVO.getManuscriptStatus());

                    if ("success".equalsIgnoreCase(message)) {
                        log.info("Email sent successfully to {}", toMailID);

                        addMailQueue.setIsActive(1);
                        mailQueueRepo.save(addMailQueue);

                        return "success";
                    } else {
                        log.error("Email sending failed for {}", toMailID);

                        addMailQueue.setIsActive(0);
                        mailQueueRepo.save(addMailQueue);

                        return "failure";
                    }

                } else {
                    log.error("User not found or email is null for UserId: {}", emailTriggerVO.getUserId());
                    return "failure";
                }

            } else {
                log.error("No valid email template found for TaskId: {}, JournalId: {}",
                        emailTriggerVO.getTaskId(), emailTriggerVO.getJournalId());
                return "There is no email template";
            }

        } catch (Exception e) {
            log.error("Exception in resendReviewerInvitationMail for ManuscriptId: {}",
                    manuscriptListVO.getManuScriptId(), e);
            return "failure";
        }
    }


    @Override
    public String emailTriggerForReminder(EmailTriggerVO emailTriggerVO) {

        TaskEmailMapReminder taskEmailMapDetails = null;
        String fromMailID = null;
        String toMailID = null;
        String mailSubject = null;
        String mailBody = null;
        User userDetail = null;
        List<String> mailCcTemp = new ArrayList<>();
        String mailCcString = null;

        log.info("Entering emailTriggerForReminder for ManuscriptId: {}, UserId: {}, TaskId: {}",
                emailTriggerVO.getManuscriptId(), emailTriggerVO.getUserId(), emailTriggerVO.getTaskId());

        try {

            log.info("Fetching TaskEmailMapReminder for TaskId: {}, JournalId: {}",
                    emailTriggerVO.getTaskId(), emailTriggerVO.getJournalId());

            taskEmailMapDetails = taskEmailMapReminderRepo.getTaskEmailMapDetails(
                    emailTriggerVO.getTaskId(), emailTriggerVO.getJournalId());

            if (taskEmailMapDetails != null
                    && emailTriggerVO.getUserId() != null
                    && taskEmailMapDetails.getJournalMailTemplates()
                    .getEmailTemplateForm().equalsIgnoreCase("REMINDER")) {

                log.info("Valid reminder template found. Proceeding with email trigger.");

                userDetail = userRepo.getByID(emailTriggerVO.getUserId());

                if (userDetail != null && userDetail.getEmail() != null) {

                    log.info("User found with email: {}", userDetail.getEmail());

                    if (taskEmailMapDetails.getJournalMailTemplates()
                            .getJournal().getJournalMetadata() != null) {

                        if (taskEmailMapDetails.getJournalMailTemplates()
                                .getJournal().getJournalMetadata().getJournalEmail() != null
                                && !taskEmailMapDetails.getJournalMailTemplates()
                                .getJournal().getJournalMetadata().getJournalEmail().isEmpty()) {

                            fromMailID = taskEmailMapDetails.getJournalMailTemplates()
                                    .getJournal().getJournalMetadata().getJournalEmail();

                        } else {
                            fromMailID = env.getProperty("fromMail");
                            log.info("Journal email not found, using default fromMail");
                        }

                    } else {
                        fromMailID = env.getProperty("fromMail");
                        log.info("Journal metadata not found, using default fromMail");
                    }

                    toMailID = userDetail.getEmail();

                    // Subject override
                    if (emailTriggerVO.getEmailTemplateSubject() != null
                            && !emailTriggerVO.getEmailTemplateSubject().isEmpty()) {

                        taskEmailMapDetails.getJournalMailTemplates()
                                .setMailSubject(emailTriggerVO.getEmailTemplateSubject());

                        log.info("Overriding email subject from UI");
                    }

                    if (emailTriggerVO.getReceiverRoleId() == 5) {
                        emailTriggerVO.setReviewerUserId(emailTriggerVO.getUserId());
                        log.info("Receiver is reviewer. ReviewerUserId set.");
                    }

                    mailSubject = emailTriggerTemplateUtils
                            .getSubjectForReminder(emailTriggerVO, taskEmailMapDetails);

                    if (emailTriggerVO.getReceiverRoleId() == 5) {
                        log.info("Fetching reviewer due date");
                        Date dueDate = manuScriptReviewerRepo
                                .getmanuScriptReviewerForDueDate(
                                        emailTriggerVO.getManuscriptId(),
                                        emailTriggerVO.getJournalId(),
                                        emailTriggerVO.getManuscriptVersion(),
                                        emailTriggerVO.getUserId());

                        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                        dueDate = df.parse(df.format(dueDate));
                        emailTriggerVO.setReviewerDueDate(dueDate);
                    }

                    mailBody = emailTriggerTemplateUtils
                            .getAllDataForReminder(emailTriggerVO, taskEmailMapDetails);

                    log.info("Preparing MailQueue entry");

                    MailQueue addMailQueue = new MailQueue();
                    addMailQueue.setIsActive(emailTriggerVO.getIsActive());
                    addMailQueue.setIsSeen(0);
                    addMailQueue.setTaskId(emailTriggerVO.getTaskId());
                    addMailQueue.setJournalId(emailTriggerVO.getJournalId());
                    addMailQueue.setMail_send_date(new Date());
                    addMailQueue.setMailBody(mailBody);
                    addMailQueue.setMailSubject(mailSubject);

                    if (emailTriggerVO.getTaskId() == 50) {

                        log.info("TaskId 50 detected. Fetching author details.");

                        Integer manuscriptId = emailTriggerVO.getManuscriptId();
                        if (manuscriptId != null) {

                            ManuScript manuscriptDetail = manuScriptRepo.getById(manuscriptId);

                            if (manuscriptDetail != null) {
                                userDetail = userRepo.getOne(manuscriptDetail.getAuthorId());
                                addMailQueue.setMailTo(userDetail.getEmail());
                                addMailQueue.setMailTOUserId(userDetail.getId());
                            }
                        }

                        addMailQueue.setMailToRole(emailTriggerVO.getRoleId());

                    } else {

                        addMailQueue.setMailToRole(emailTriggerVO.getReceiverRoleId());
                        addMailQueue.setMailTo(toMailID);
                        addMailQueue.setMailTOUserId(userDetail.getId());
                    }

                    addMailQueue.setIsActive(0);
                    addMailQueue.setModuleName(emailTriggerVO.getModule_name());

                    ManuScript getManuScript = manuScriptRepo.getOne(emailTriggerVO.getManuscriptId());

                    if (getManuScript != null) {
                        addMailQueue.setManuscriptId(getManuScript.getManuScriptId());
                        addMailQueue.setManuscriptVersion(getManuScript.getManuscriptVersion());
                    }

                    addMailQueue.setMailFrom(fromMailID);

                    if (emailTriggerVO.getManuscriptStatus() != null
                            && !(emailTriggerVO.getManuscriptStatus() == 0)) {

                        addMailQueue.setManuscriptStatus(emailTriggerVO.getManuscriptStatus());
                    } else {
                        addMailQueue.setManuscriptStatus(emailTriggerVO.getManuscriptStatusId());
                    }

                    // AIAN CC logic
                    if ((emailTriggerVO.getJournalId().equals(1819)
                            || emailTriggerVO.getJournalId().equals(259))
                            && (emailTriggerVO.getTaskId().equals(67)
                            || emailTriggerVO.getTaskId().equals(11)
                            || emailTriggerVO.getTaskId().equals(10)
                            || emailTriggerVO.getTaskId().equals(68)
                            || emailTriggerVO.getTaskId().equals(39)
                            || emailTriggerVO.getTaskId().equals(28))
                            && emailTriggerVO.getReceiverRoleId().equals(4)) {

                        log.info("Applying AIAN CC logic");

                        List<ManuScriptCoAuthor> manuscriptCorrespondingAuthors =
                                manuScriptCoAuthorRepo
                                        .getCorrespondingAuthorListFromManuscriptId(
                                                emailTriggerVO.getManuscriptId());

                        manuscriptCorrespondingAuthors.removeIf(manuScriptCoAuthor ->
                                manuScriptCoAuthor.getCoAuthorId()
                                        .equals(getManuScript.getAuthorId()));

                        if (!manuscriptCorrespondingAuthors.isEmpty()) {
                            for (ManuScriptCoAuthor manuScriptCoAuthor : manuscriptCorrespondingAuthors) {
                                mailCcTemp.add(manuScriptCoAuthor.getUser().getEmail());
                            }
                        }
                    }

                    if (!mailCcTemp.isEmpty()) {
                        mailCcString = String.join(",", mailCcTemp);

                        if (addMailQueue.getMailCC() != null
                                && !addMailQueue.getMailCC().isEmpty()) {

                            String mailCcFinal = addMailQueue.getMailCC()
                                    .concat(",").concat(mailCcString);

                            addMailQueue.setMailCC(mailCcFinal);
                        } else {
                            addMailQueue.setMailCC(mailCcString);
                        }

                        log.info("CC emails added: {}", mailCcString);
                    }

                    mailQueueRepo.save(addMailQueue);

                    log.info("MailQueue saved successfully for UserId: {}", userDetail.getId());

                    return "E-Mail Successfully Trigger";

                } else {
                    log.error("Invalid user or email is null for UserId: {}", emailTriggerVO.getUserId());
                    return "Provided user is not valid";
                }

            } else {
                log.error("Invalid template or userId null for TaskId: {}, JournalId: {}",
                        emailTriggerVO.getTaskId(), emailTriggerVO.getJournalId());
                return "Provided user is not valid";
            }

        } catch (Exception e) {
            log.error("Exception in emailTriggerForReminder for ManuscriptId: {}",
                    emailTriggerVO.getManuscriptId(), e);
            return "Error:->" + e;
        }
    }

    @Override
    public String sendOrcidAuthEmail(IDVO idvo) {

        String userDetails = "<b>Email From :</b> " + idvo.getFromMail() + "         <b>Email To :</b> " +
                idvo.getToMail() + "\n<b>Subject :</b> " + idvo.getSubject() + "\n -------------------------------------------------------------------------\n";
        MailQueue addMailQueue = new MailQueue();
        addMailQueue.setMailFrom(env.getProperty("fromMail"));
        addMailQueue.setMailTo(idvo.getEmail());
        addMailQueue.setJournalId(idvo.getJournalId());
        addMailQueue.setManuscriptId(0);
        addMailQueue.setMailSubject(idvo.getSubject());
        addMailQueue.setMailBody(idvo.getMailBody());
        addMailQueue.setMail_send_date(new Date());
        addMailQueue.setIsActive(0);
        mailQueueRepo.saveAndFlush(addMailQueue);
        return "Success";
    }

}
