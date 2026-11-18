package com.digi.uniprr.serviceImpl;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.persistence.Tuple;
import javax.transaction.Transactional;

import com.digi.uniprr.model.*;
import com.digi.uniprr.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.stereotype.Service;

import com.digi.uniprr.VO.EmailTriggerVO;
import com.digi.uniprr.VO.ImpactArticleDTO;
import com.digi.uniprr.VO.ManuscriptListVO;
import com.digi.uniprr.VO.UserJournalVO;
import com.digi.uniprr.jdbcConfig.RawJDBC;
import com.digi.uniprr.service.AuthorProofingService;
import com.digi.uniprr.service.CheckPlagiarismService;
import com.digi.uniprr.service.ManuscriptDashboardArticleService;
import com.digi.uniprr.service.ReminderService;
import com.digi.uniprr.utils.EmailTriggerTemplateUtils;
import com.digi.uniprr.utils.SendMailUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class ReminderServiceImpl implements ReminderService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());


    @Autowired
    JournalPoliciesRepo journalPoliciesRepo;
    @Autowired
    ManuScriptRepo manuScriptRepo;

    @Autowired
	private TaskEmailMapRepo taskEmailMapRepo;
    
    @Autowired
    ManuscriptTatRepo manuscriptTatRepo;

    @Autowired
    EmailTriggerTemplateUtils emailTriggerTemplateUtils;

    @Autowired
    MailQueueRepo mailQueueRepo;

    @Autowired
    ManuscriptDashboardArticleService manuscriptDashboardArticleService;

    @Autowired
    Environment env;
    @Autowired
    SendMailUtils mailUtils;
    @Autowired
    TatEmailConfigurationRepo tatEmailConfigurationRepo;
    @Autowired
    TaskEmailMapReminderRepo taskEmailMapReminderRepo;
    @Autowired
    UserRepo userRepo;
    @Autowired
    ManuScriptReviewerRepo manuScriptReviewerRepo;
    @Autowired
    ReminderService reminderService;
    @Autowired
    AuthoritiesHistoryRepo authoritiesHistoryRepo;
    @Autowired
    ReminderTrackerRepo reminderTrackerRepo;
    @Autowired
    JournalMetadataRepo journalMetadataRepo;
    @Autowired
    WorkflowRoleRepo workflowRoleRepo;
    @Autowired
    JournalsRepo journalRepo;
    @Autowired
    ManuscriptEditorRepo manuscriptEditorRepo;
    @Autowired
    UserJournalRepo userJournalRepo;
    @Autowired
    ManuScriptCoAuthorRepo manuScriptCoAuthorRepo;
    @Autowired
    AuthorProofingService authorProofingService;
    @Value("${server.status}")
    private String serverENV;
    
    private static final List<Integer> AUTO_WITHDRAW_STATUSES = Arrays.asList(4,46);
    private static final int WARNING_REMINDER_NO = 10;   // Existing reminder count = 10, next is 11th reminder (Week 11)
    private static final int FINAL_WARNING_REMINDER_NO = 11; // Existing reminder count = 11, next is 12th reminder (Week 12)
    private static final int FINAL_DEADLINE_DAYS = 14;   // 2 weeks from Week 11 warning
    private static final int TECHNICAL_MODIFICATION = 46;
	private static final int REVISION = 4;
	private static final int WITHDRAWN_STATUS = 16;
	private static final int AUTHOR_ROLE_ID = 4;
	private static final int EDITOR_ROLE_ID = 2;


    @Override
    public JournalPolicies getReminderPolicies(Integer journalId) {
        JournalPolicies journalPolicies = journalPoliciesRepo.getJournalPoliciesDetails(journalId);
        return journalPolicies;
    }

    @Override
    public List<ManuScript> getAuthorReminderArticles() {

        List<Integer> statusId = Arrays.asList(63, 64, 4, 46, 57, 62);
        List<ManuScript> manuscripts = manuScriptRepo.getAllManuscriptForAuthorByUserRole(statusId);
        return manuscripts;
    }

    @Override
    public List<ManuScript> getEditorReminderArticles() {
        List<Integer> statusId = new ArrayList<Integer>();
        statusId.add(2);
        statusId.add(3);
        statusId.add(50);
        statusId.add(26);
        List<ManuScript> manuscripts = manuScriptRepo.getAllManuscriptForAuthorByUserRole(statusId);
        return manuscripts;
    }

    @Override
    public List<ManuScript> getEICReminderArticles() {
        Integer roleId = 2;
        List<Integer> statusId = new ArrayList<Integer>();
        statusId.add(2);
        statusId.add(3);
        statusId.add(46);
        List<ManuScript> manuscripts = manuScriptRepo.getAllManuscriptByUserRole(roleId, statusId);
        return manuscripts;
    }

    @Override
    public List<ManuScript> getReviewerReminderArticles() {
        List<Integer> statusIdReviewer = Arrays.asList(8, 66);
        List<Integer> roleId = Arrays.asList(2, 3);
        List<ManuScript> manuscripts = manuScriptRepo.getAllManuscriptForReviewerByUserRole(statusIdReviewer, roleId);
        return manuscripts;
    }

    @Override
    public String autoReminderForEditor() {
        List<ManuScript> editorReminderArticles = reminderService.getEditorReminderArticles();
        log.info("Editor-  Articles reminder size............: " + editorReminderArticles.size());

        for (ManuScript editorManuScript : editorReminderArticles) {
            try {
                int manuscriptId = editorManuScript.getManuScriptId();
                int manuscriptVersion = editorManuScript.getManuscriptVersion();
                int manuscriptStatus = editorManuScript.getManuscriptStatusId();
                int userRole = editorManuScript.getAssignedUserRole();
                Integer assignedUser = editorManuScript.getAssignedUser();
                if (userRole == 2) {
                    List<UserJournal> i = userJournalRepo.getUserRoleJournalId(editorManuScript.getJournalId(), 2);
                    List<Integer> activeUser = new ArrayList<>();
                    activeUser = i.stream().map(j -> j.getUserId()).collect(Collectors.toList());
                    if (activeUser != null && activeUser.size() > 0 && !activeUser.contains(assignedUser)) {
                        assignedUser = activeUser.get(0);
                        editorManuScript.setAssignedUser(assignedUser);
                        manuScriptRepo.save(editorManuScript);
                    }
                }
                int journalId = editorManuScript.getJournalId();
                JournalPolicies reminderPolicies = null;
                int taskId = 0;
                try {
                    reminderPolicies = journalPoliciesRepo.getJournalPoliciesDetails(editorManuScript.getJournalId());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                log.info("Revision reminder for journalId: {}, ManuscriptId: {}, ManuscriptStatus:{}", journalId, manuscriptId, manuscriptStatus);

                if (manuscriptStatus == 2) {
                    taskId = 13;
                } else if (manuscriptStatus == 3) {
                    taskId = 3;
                } else if (manuscriptStatus == 50) {
                    taskId = 20;
                } else if (manuscriptStatus == 26) {
                    taskId = 19;
                }else if (manuscriptStatus == 24) {
                    taskId = 19;
                }
                Date dueDate = null;

                int reminderPolicyForEicEditor = 0;
                if (reminderPolicies != null && reminderPolicies.getReminderFrequencyEicEditor() != null) {
                    reminderPolicyForEicEditor = reminderPolicies.getReminderFrequencyEicEditor();
                } else {
                    continue;
                }

                Date dateobj = new Date();

                DateFormat df = new SimpleDateFormat("MMM dd, yyyy");

                Date createdAt = new Date();
                if (editorManuScript != null && editorManuScript.getDueDate() != null) {
                    createdAt = editorManuScript.getDueDate();
                } else {
                    createdAt = editorManuScript.getUpdatedOn();
                }
                log.info("Editor-- Reminder policies.no. of days: {}, Due Date: {} " , reminderPolicyForEicEditor, createdAt);
                ReminderTracker reminderTrackerData = null;
                reminderTrackerData = reminderTrackerRepo.getReminderTrackerData(journalId, manuscriptId, assignedUser,
                        manuscriptStatus);
                Date startDate = null;
                Integer reminderNo = 0;
                Date lastReminderSent = null;
                if (reminderTrackerData != null) {
                    lastReminderSent = reminderTrackerData.getCreatedDate();// get date from tracker table if data
                    // is present in tracker table
                    reminderNo = reminderTrackerData.getReminderNo();
                    log.info("Editor-  Last reminder Sent..: " + lastReminderSent + " Reminder No.. " + reminderNo);
                }
                startDate = createdAt;
                WorkflowRoleSeq byWorkflowIdAndTaskId = null;
                byWorkflowIdAndTaskId = workflowRoleRepo.getJournalWorkflowIdTaskId(taskId, journalId);
                if (byWorkflowIdAndTaskId != null) {
                    Integer tat = byWorkflowIdAndTaskId.getTat();

                    long ltime = startDate.getTime() + tat * 24 * 60 * 60 * 1000;

                    dueDate = new Date(ltime);
                }

                log.info("Due Date: {}", dueDate);

                int flow = 0;
                Date todaysDateObj = df.parse(df.format(dateobj));
                Date startDateObj = df.parse(df.format(startDate));
                if (dueDate != null) {

                    Date dueDateObj = df.parse(df.format(dueDate));
                    long diff = todaysDateObj.getTime() - startDateObj.getTime();
                    int diffDays = (int) (diff / (24 * 60 * 60 * 1000));// difference in days

                    if (todaysDateObj.after(dueDateObj) && (diffDays >= reminderPolicyForEicEditor)) {
                        // email reminder sent to enduser
                        if (lastReminderSent != null) {
                            long lastReminderSendDiff = todaysDateObj.getTime() - lastReminderSent.getTime();// new
                            int lastReminderSendDiffDays = (int) (lastReminderSendDiff / (24 * 60 * 60 * 1000));// new

                            if (lastReminderSendDiffDays >= reminderPolicyForEicEditor) {
                                flow = 1;
                                log.info("Editor with reminder flow1...: ");
                            }
                        } else {
                            flow = 1;
                            log.info("Editor without reminder flow1...: ");
                        }

                    } else if ((todaysDateObj.before(dueDateObj) && todaysDateObj.after(startDateObj)
                            && (diffDays >= reminderPolicyForEicEditor))
                            || (todaysDateObj.equals(dueDateObj) && (diffDays >= reminderPolicyForEicEditor))) {
                        // notification only shown to user (not sent any email)
                        if (lastReminderSent != null) {
                            long lastReminderSendDiff = todaysDateObj.getTime() - lastReminderSent.getTime();// new
                            int lastReminderSendDiffDays = (int) (lastReminderSendDiff / (24 * 60 * 60 * 1000));// new

                            if (lastReminderSendDiffDays >= reminderPolicyForEicEditor) {
                                flow = 2;
                                log.info("Editor with reminder flow2...: ");
                            }
                        } else {
                            flow = 2;
                            log.info("Editor without reminder flow2...: ");
                        }
                    }
                }
                if (flow == 1 || flow == 2) {

                    TaskEmailMapReminder getTaskEmailMapReminderData = null;
                    getTaskEmailMapReminderData = taskEmailMapReminderRepo.getTaskEmailMapDetails(taskId, journalId);// what
                    // will
                    // be
                    // the
                    // task
                    // id
                    // eg.
                    // 4
                    // ##
                    if (getTaskEmailMapReminderData != null) {
                        String toMailID = "";
                        String mailSubject = "";
                        String mailBody = "";
                        String ccMailId = "";
                        if ((getTaskEmailMapReminderData.getJournalMailTemplates().getEmailTemplateForm()
                                .equalsIgnoreCase("REMINDER")
                                || getTaskEmailMapReminderData.getJournalMailTemplates().getEmailTemplateForm()
                                .equalsIgnoreCase("NOTIFICATION"))
                                && getTaskEmailMapReminderData.getJournalMailTemplates().getIsActive()
                                .equalsIgnoreCase("yes")) {
                            EmailTriggerVO addEmailTriggerVOData = new EmailTriggerVO();
                            addEmailTriggerVOData.setJournalId(journalId);
                            addEmailTriggerVOData.setManuscriptId(manuscriptId);
                            addEmailTriggerVOData.setManuscriptVersion(manuscriptVersion);
                            addEmailTriggerVOData.setManuscriptVersion(manuscriptVersion);
                            addEmailTriggerVOData.setEditorUserId(assignedUser);// Not correct
                            User userDetail = userRepo.getByID(assignedUser);
                            if (userDetail != null) {
                                if (getTaskEmailMapReminderData.getJournalMailTemplates() != null) {
                                    ccMailId = getTaskEmailMapReminderData.getJournalMailTemplates().getMailcc();
                                }
                                toMailID = userDetail.getEmail();
                                addEmailTriggerVOData.setReceiverRoleId(userRole);
                                mailSubject = emailTriggerTemplateUtils.getSubjectForReminder(addEmailTriggerVOData,
                                        getTaskEmailMapReminderData);

                                mailBody = emailTriggerTemplateUtils.getAllDataForReminder(addEmailTriggerVOData,
                                        getTaskEmailMapReminderData);

                                MailQueue addMailQueue = new MailQueue();
                                addMailQueue.setIsSeen(0);
                                addMailQueue.setJournalId(addEmailTriggerVOData.getJournalId());
                                addMailQueue.setMail_send_date(new Date());
                                addMailQueue.setMailBody(mailBody);
                                addMailQueue.setMailTo(toMailID);
                                addMailQueue.setMailCC(ccMailId);
                                addMailQueue.setMailSubject(mailSubject);
                                addMailQueue.setMailTOUserId(userDetail.getId());
                                addMailQueue.setMailToRole(addEmailTriggerVOData.getReceiverRoleId());
                                addMailQueue.setIsActive(0);
                                addMailQueue.setManuscriptId(manuscriptId);
                                addMailQueue.setManuscriptVersion(manuscriptVersion);
                                addMailQueue.setMailFrom(
                                        editorManuScript.getJournal().getJournalMetadata().getJournalEmail());
                                addMailQueue.setManuscriptStatus(manuscriptStatus);
                                addMailQueue.setTaskId(taskId);// ## Needs to change for every user##
                                addMailQueue.setMailToRole(editorManuScript.getAssignedUserRole());// ######THIS IS FOR
                                // EDITOR#####
                                addMailQueue.setModuleName("AutoReminderEditor");
                                mailQueueRepo.saveAndFlush(addMailQueue);
                                log.info("Editor- Insert query mail queue table and manuscript_id ...: " + manuscriptId
                                        + "  AutoReminderEditor");
                                // insert data in reminder_tracker table
                                ReminderTracker reminderTracker = new ReminderTracker();
                                reminderTracker.setJournalId(journalId);
                                reminderTracker.setManuscriptId(manuscriptId);
                                reminderTracker.setManuscriptStatusId(manuscriptStatus);
                                reminderTracker.setAssignedUser(assignedUser);
                                reminderTracker.setReminderNo(reminderNo + 1);
                                reminderTracker.setReminderStatus(1);
                                reminderTracker.setIsActive(1);
                                reminderTracker.setCreatedBy("");
                                reminderTracker.setCreatedDate(new Date());
                                reminderTracker.setUpdatedBy("");
                                reminderTracker.setUpdatedDate(new Date());
                                reminderTrackerRepo.saveAndFlush(reminderTracker);
                                log.info("Editor-  Insert query reminder_tracker table and manuscript_id ...: "
                                        + manuscriptId + "  AutoReminderEditor");
                            }
                        }
                    }
                }

            } catch (Exception e) {
                log.error("Error occured during sending reminder to Editor for manuscript :: "
                        + editorManuScript.getManuscriptCode() + "  :: " + e.getMessage());
            }
        }

        return "Run sucessfully";
    }

    // Author: NIC
    // Date: 2023-09-20
    // Worked for APC payment reminder improvement.
    @Override
    public String autoReminderForAuthor() {
        List<Integer> statusId = Arrays.asList(63, 64, 4, 46);
        List<Tuple> authorReminderArticles = manuScriptRepo.getAllManuscriptForReminder(statusId);

        log.info("Author- Articles reminder size: " + authorReminderArticles.size());

        for (Tuple tuple : authorReminderArticles) {
            try {
                int journalId = tuple.get(0, Integer.class);
                int manuscriptId = tuple.get(1, Integer.class);
                int manuscriptVersion = tuple.get(2, Integer.class);
                int manuscriptStatus = tuple.get(3, Integer.class);
                Integer assignedUser = tuple.get(4, Integer.class);
                Date createdAt = tuple.get(5, Date.class);

                int taskId = getTaskIdByManuscriptStatus(manuscriptStatus);
                if (taskId == 0) {
                    log.error("No matching task found for manuscriptStatus: " + manuscriptStatus);
                    continue; // Skip processing this article
                }

                JournalPolicies reminderPolicies = journalPoliciesRepo.getJournalPoliciesDetails(journalId);
                if (reminderPolicies == null || (reminderPolicies.getReminderFrequencyAuthor() == null
                        || reminderPolicies.getReminderFrequencyAuthor() <= 0)) {
                    log.info("Author- No reminder policies found for journal: " + journalId);
                    continue; // Skip processing this article
                }

                int reminderPolicyForAuthor = reminderPolicies.getReminderFrequencyAuthor();
                log.info("Author- Reminder policies.no. of days: " + reminderPolicyForAuthor);

                Date dateobj = new Date();
                DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                ReminderTracker reminderTrackerData = reminderTrackerRepo.getReminderTrackerData(journalId,
                        manuscriptId, assignedUser, manuscriptStatus);
                Date startDate = (reminderTrackerData != null) ? reminderTrackerData.getCreatedDate() : createdAt;
                int reminderNo = (reminderTrackerData != null) ? reminderTrackerData.getReminderNo() : 0;

                int tat = getTatForTask(taskId, journalId, reminderPolicyForAuthor);
             //   Date dueDate = calculateDueDate(startDate, tat);
                Date dueDate = calculateDueDate(journalId, manuscriptId,manuscriptVersion,manuscriptStatus,assignedUser);

                int flow = determineFlow(dateobj, df, startDate, dueDate, reminderPolicyForAuthor, reminderTrackerData);
                if (flow == 0) {
                    continue; // Skip processing this article
                }
                
                //impact reminder
    			ManuScript manuScript = manuScriptRepo.getFindByManuScriptDetail(manuscriptId);
    			boolean isImpact = RawJDBC.isImpactJournalById(manuScript.getJournal().getJournalCode());
    		    List<ImpactArticleDTO> impactList = RawJDBC.fetchImpactData(manuScript.getManuscriptCode());
    		    if(manuscriptStatus==57 && isImpact && !impactList.isEmpty()) {
			        ImpactArticleDTO impactDTO = impactList.get(0); // only expecting one row per AID
			        if (impactDTO.getAuthorReviewUrl() != null
			        		&&!impactDTO.getAuthorReviewUrl().isEmpty()) {
			        	taskId =95;
			        	log.info(manuScript.getManuscriptCode()+" is impact Article and task Id For Reminder is 95");
			        }
    		    }

                TaskEmailMapReminder getTaskEmailMapReminderData = taskEmailMapReminderRepo
                        .getTaskEmailMapDetails(taskId, journalId);
                if (getTaskEmailMapReminderData == null || !isEmailTemplateActive(getTaskEmailMapReminderData)) {
                    log.info("Author- No active email template found for taskId: " + taskId);
                    continue; // Skip processing this article
                }

                sendReminderOrNotificationEmail(journalId, manuscriptId, manuscriptVersion, assignedUser, tuple, taskId,
                        reminderNo, getTaskEmailMapReminderData);

            } catch (ParseException e) {
                log.error("For author revision automailer failed getting error for article: "
                        + tuple.get(1, Integer.class), e);
            }
        }

        return "Run successfully";
    }
    
    
    
    
    @Override
    public String autoWithdrawReminderForAuthor() {

        List<Tuple> manuscripts = manuScriptRepo.getAllManuscriptForReminder(AUTO_WITHDRAW_STATUSES);
        int warningCount = 0;
        int withdrawnCount = 0;
        int extendedCount = 0;
        int disabledCount = 0;

        log.info("AutoWithdrawReminderForAuthor - Manuscripts found: {}", manuscripts.size());

        Date today = truncateDate(new Date());

        for (Tuple tuple : manuscripts) {
            try {
                int journalId = tuple.get(0, Integer.class);
                int manuscriptId = tuple.get(1, Integer.class);
                int manuscriptVersion = tuple.get(2, Integer.class);
                int manuscriptStatus = tuple.get(3, Integer.class);
                Integer assignedAuthor = tuple.get(4, Integer.class);
                Integer assignedUser = tuple.get(9, Integer.class);
                Date createdAt = tuple.get(5, Date.class);
                Date currentDueDate = tuple.get(8, Date.class); // existing due date from query

                // Safety check: process only statuses 4 and 46
                if (!AUTO_WITHDRAW_STATUSES.contains(manuscriptStatus)) {
                    continue;
                }
                
                if (createdAt == null) {
                    log.info("No assigned date found for manuscript {}, skipping", manuscriptId);
                    continue;
                }
                
                Date assignedDate = truncateDate(createdAt);

                JournalPolicies policies = journalPoliciesRepo.getJournalPoliciesDetails(journalId);
                Integer finalWithdrawalWeek = (policies != null) ? policies.getJournalArticleAutoWithdrawWeeks() : null;
                
                // -------------------------------------------------
                // Feature disabled for this journal: finalWithdrawalWeek explicitly set to 0
                // -------------------------------------------------
                if (finalWithdrawalWeek != null && finalWithdrawalWeek == 0) {
                    log.info("Auto-withdraw disabled for journal {} (finalWithdrawalWeek = 0). Skipping manuscript {}.",
                            journalId, manuscriptId);
                    disabledCount++;
                    continue;
                }
            	int totalWeeks=0;
                if(finalWithdrawalWeek != null) {
                	totalWeeks = finalWithdrawalWeek;
                }else {
                    log.info("Auto-withdraw disabled for this journal {}. Skipping manuscript {}.",
                            journalId, manuscriptId);
                    disabledCount++;
                    continue;
                }
                
                Date computedFinalDueDate = addDays(assignedDate, totalWeeks * 7);

                // -------------------------------------------------
                // Manual extension check: if editorial has pushed the manuscript's
                // due date beyond the computed auto-withdraw date, the entire
                // auto-withdraw flow (warnings + withdrawal) is skipped for now.
                // -------------------------------------------------
                if (currentDueDate != null) {
                    Date manuscriptDueDate = truncateDate(currentDueDate);
                    if (manuscriptDueDate.after(computedFinalDueDate)) {
                        log.info("Manual due date extension detected for manuscript {} (due: {}, computed: {}). Skipping auto-withdraw flow.",
                                manuscriptId, manuscriptDueDate, computedFinalDueDate);
                        extendedCount++;
                        continue;
                    }
                }

                long elapsedDays = (today.getTime() - assignedDate.getTime()) / (24 * 60 * 60 * 1000);
                long elapsedWeeks = elapsedDays / 7;

                int firstWarningWeek = totalWeeks - 2;
                int finalWarningWeek = totalWeeks - 1;

                int firstWarningTaskId = (manuscriptStatus == TECHNICAL_MODIFICATION) ? 200 : 202;
                int finalWarningTaskId = (manuscriptStatus == TECHNICAL_MODIFICATION) ? 201 : 203;

                log.info("Manuscript {} - Status: {}, AssignedDate: {}, ElapsedWeeks: {}, TotalWeeks: {}",
                        manuscriptId, manuscriptStatus, assignedDate, elapsedWeeks, totalWeeks);

                // -------------------------------------------------
                // STEP 1: First auto-withdraw warning (finalWithdrawalWeek - 2 weeks from assignedDate)
                // -------------------------------------------------
                if (elapsedWeeks >= firstWarningWeek) {

                	boolean firstWarningAlreadySent = mailQueueRepo.existsByManuscriptIdAndManuscriptVersionAndTaskId(
                            manuscriptId, manuscriptVersion, firstWarningTaskId);

                    if (!firstWarningAlreadySent) {
                        log.info("Sending first auto-withdraw warning for manuscript {} at week {}", manuscriptId, elapsedWeeks);
                        sendAutoWithdrawWarningEmail(journalId, manuscriptId, manuscriptVersion, firstWarningTaskId, assignedAuthor, tuple);
                        warningCount++;
                        continue;
                    }
                }
                
                // -------------------------------------------------
                // STEP 2: Final auto-withdraw warning (finalWithdrawalWeek - 1 weeks from assignedDate)
                // -------------------------------------------------
                if (elapsedWeeks >= finalWarningWeek) {
                    boolean finalWarningAlreadySent = mailQueueRepo.existsByManuscriptIdAndManuscriptVersionAndTaskId(
                            manuscriptId, manuscriptVersion, finalWarningTaskId);

                    if (!finalWarningAlreadySent) {
                        log.info("Sending final auto-withdraw warning for manuscript {} at week {}", manuscriptId, elapsedWeeks);
                        sendAutoWithdrawWarningEmail(journalId, manuscriptId, manuscriptVersion, finalWarningTaskId, assignedAuthor, tuple);
                        warningCount++;
                        continue;
                    }
                }

                // -------------------------------------------------
                // STEP 3: Auto withdraw once elapsed weeks reach the full withdrawal window
                // -------------------------------------------------
                if (elapsedWeeks >= totalWeeks && !today.before(computedFinalDueDate)) {
                        log.info("Auto withdrawing manuscript {}", manuscriptId);
                        autoWithdrawManuscript(journalId, manuscriptId, manuscriptVersion, assignedUser, assignedAuthor, manuscriptStatus);
                        withdrawnCount++;
                        continue;
                }
                
                if (elapsedWeeks < firstWarningWeek) {
                	int taskId = (manuscriptStatus == TECHNICAL_MODIFICATION) ? 11 : 10;
                	Date lastMailDate = mailQueueRepo.getMailDate(manuscriptId, taskId);
                	boolean flag = true;
                	if(lastMailDate!=null) {
                		long calculateMailSentDays =(lastMailDate.getTime() - assignedDate.getTime()) / (24 * 60 * 60 * 1000);
                		long calculateMailSentWeek = calculateMailSentDays/7;
                        log.info("Manuscript {} - Status: {}, AssignedDate: {}, calculateMailSentWeek: {}, elapsedWeeks: {}",
                                manuscriptId, manuscriptStatus, assignedDate, calculateMailSentWeek, elapsedWeeks);
                		if (calculateMailSentWeek == elapsedWeeks) {
                			flag = false;
                		}
                	}
                	if(flag) {
                        log.info("Sending Author Reminder for manuscript {} at week {}", manuscriptId, elapsedWeeks);
                        sendAutoReminderEmail(journalId, manuscriptId, manuscriptVersion, taskId, assignedAuthor, tuple);
                        continue;
                	}
                }
            } catch (Exception e) {
                log.error("Error in autoWithdrawReminderForAuthor for manuscript {}",
                        tuple.get(1, Integer.class), e);
            }
        }

        log.info("AutoWithdrawReminderForAuthor completed. Warnings sent: {}, Withdrawn: {}, Skipped (manual extension): {}, Skipped (disabled): {}",
                warningCount, withdrawnCount, extendedCount, disabledCount);

        return "Completed successfully. Warnings=" + warningCount + ", Withdrawn=" + withdrawnCount
                + ", Extended=" + extendedCount + ", Disabled=" + disabledCount;
    }
    
    // Date Utilities

    private Date addDays(Date date, int days) {
        return new Date(date.getTime() + (long) days * 24 * 60 * 60 * 1000);
    }

    private Date truncateDate(Date date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            return sdf.parse(sdf.format(date));
        } catch (ParseException e) {
            return date;
        }
    }
    
    // Send Auto-Withdraw Warning Email

    private void sendAutoWithdrawWarningEmail(int journalId,
                                        int manuscriptId,
                                        int manuscriptVersion,
                                        int taskId,
                                        Integer assignedUser,
                                        Tuple tuple) {

    	TaskEmailMapReminder taskEmail = taskEmailMapReminderRepo.getTaskEmailMapDetails(taskId, journalId);

        if (taskEmail == null || !isEmailTemplateActive(taskEmail)) {
            log.info("Auto-withdraw warning template not configured for journal {}, taskId {}", journalId, taskId);
            return;
        }
        
        EmailTriggerVO addEmailTriggerVOData = new EmailTriggerVO();
        addEmailTriggerVOData.setJournalId(journalId);
        addEmailTriggerVOData.setManuscriptId(manuscriptId);
        addEmailTriggerVOData.setManuscriptVersion(manuscriptVersion);
        addEmailTriggerVOData.setAuthorUserId(assignedUser);
        addEmailTriggerVOData.setReceiverRoleId(AUTHOR_ROLE_ID);

        String ccMailId = taskEmail.getJournalMailTemplates().getMailcc();
        String mailSubject = emailTriggerTemplateUtils.getSubjectForReminder(addEmailTriggerVOData, taskEmail);
        try {
            String mailBody = emailTriggerTemplateUtils.getAllDataForReminder(addEmailTriggerVOData, taskEmail);

            MailQueue addMailQueue = new MailQueue();
            addMailQueue.setIsSeen(0);
            addMailQueue.setJournalId(journalId);
            addMailQueue.setMail_send_date(new Date());
            addMailQueue.setMailBody(mailBody);
            addMailQueue.setMailTo(tuple.get(7, String.class));
            addMailQueue.setMailCC(ccMailId);
            addMailQueue.setMailSubject(mailSubject);
            addMailQueue.setMailTOUserId(assignedUser);
            addMailQueue.setMailToRole(AUTHOR_ROLE_ID);
            addMailQueue.setIsActive(0);
            addMailQueue.setManuscriptId(manuscriptId);
            addMailQueue.setManuscriptVersion(manuscriptVersion);
            addMailQueue.setMailFrom(tuple.get(6, String.class));
            addMailQueue.setManuscriptStatus(tuple.get(3, Integer.class));
            addMailQueue.setTaskId(taskId);
            addMailQueue.setModuleName("AutoWithdrawReminderAuthor");
            mailQueueRepo.save(addMailQueue);

            log.info("Auto-withdraw warning queued for manuscript_id: {}, taskId: {}", manuscriptId, taskId);
        } catch (MalformedURLException | UnsupportedEncodingException e) {
            log.error("Error while sending auto-withdraw warning email for manuscript_id: {}", manuscriptId, e);
        }
    }
    
    
    private void sendAutoReminderEmail(int journalId,
            int manuscriptId,
            int manuscriptVersion,
            int taskId,
            Integer assignedUser,
            Tuple tuple) {


    	TaskEmailMapReminder taskEmail = taskEmailMapReminderRepo.getTaskEmailMapDetails(taskId, journalId);

    	if (taskEmail == null || !isEmailTemplateActive(taskEmail)) {
    		log.info("template not configured for journal {}, taskId {}", journalId, taskId);
    		return;
    	}

    	EmailTriggerVO addEmailTriggerVOData = new EmailTriggerVO();
        addEmailTriggerVOData.setJournalId(journalId);
        addEmailTriggerVOData.setManuscriptId(manuscriptId);
		addEmailTriggerVOData.setManuscriptVersion(manuscriptVersion);
		addEmailTriggerVOData.setAuthorUserId(assignedUser);
		addEmailTriggerVOData.setReceiverRoleId(AUTHOR_ROLE_ID);

		String ccMailId = taskEmail.getJournalMailTemplates().getMailcc();
		String mailSubject = emailTriggerTemplateUtils.getSubjectForReminder(addEmailTriggerVOData, taskEmail);
		try {
		String mailBody = emailTriggerTemplateUtils.getAllDataForReminder(addEmailTriggerVOData, taskEmail);
		
		MailQueue addMailQueue = new MailQueue();
		addMailQueue.setIsSeen(0);
		addMailQueue.setJournalId(journalId);
		addMailQueue.setMail_send_date(new Date());
		addMailQueue.setMailBody(mailBody);
		addMailQueue.setMailTo(tuple.get(7, String.class));
		addMailQueue.setMailCC(ccMailId);
		addMailQueue.setMailSubject(mailSubject);
		addMailQueue.setMailTOUserId(assignedUser);
		addMailQueue.setMailToRole(AUTHOR_ROLE_ID);
		addMailQueue.setIsActive(0);
		addMailQueue.setManuscriptId(manuscriptId);
		addMailQueue.setManuscriptVersion(manuscriptVersion);
		addMailQueue.setMailFrom(tuple.get(6, String.class));
		addMailQueue.setManuscriptStatus(tuple.get(3, Integer.class));
		addMailQueue.setTaskId(taskId);
		addMailQueue.setModuleName("AutoReminderAuthor");
		mailQueueRepo.save(addMailQueue);

		log.info("queued for manuscript_id: {}, taskId: {}", manuscriptId, taskId);
		} catch (MalformedURLException | UnsupportedEncodingException e) {
			log.error("Error while sending reminder email for manuscript_id: {}", manuscriptId, e);
		}
    }
    

    // ----------------------------------------------------------------
 	// -------------------- AUTO WITHDRAWAL ---------------------------
 	// ----------------------------------------------------------------
 	private void autoWithdrawManuscript(Integer journalId, Integer manuscriptId, Integer version,
 			Integer assignedUser, Integer assignedAuthor, Integer status) {
 		try {
 			ManuScript manuscript = manuScriptRepo.findById(manuscriptId).orElse(null);
 			if (manuscript == null)
 				return;

 			manuscript.setManuscriptStatusBeforeWithdrawn(status);
 			manuscript.setManuscriptStatusId(WITHDRAWN_STATUS);
 			manuscript.setWithdrawnDate(new Date());
 			manuScriptRepo.save(manuscript);

 			log.info("✅ Manuscript {} auto-withdrawn", manuscriptId);

 			// Email to Author
 			int authorTask = (status == TECHNICAL_MODIFICATION) ? 204 : 206;
 			TaskEmailMap authorTemplate = taskEmailMapRepo.getTaskEmailMapDetails(authorTask, journalId);
 			queueMail(journalId, manuscriptId, version, assignedAuthor, AUTHOR_ROLE_ID, authorTask, authorTemplate, status);

 			// Email to Editors (Editor/EIC)
 			Integer editorRole = userRepo.getEditorRoleForJournal(assignedUser, journalId);
 			if (editorRole != null) {
 				int editorTask = (status == TECHNICAL_MODIFICATION) ? 205 : 207;
 				TaskEmailMap editorTemplate = taskEmailMapRepo.getTaskEmailMapDetails(editorTask, journalId);
 				queueMail(journalId, manuscriptId, version, assignedUser, editorRole, editorTask, editorTemplate, status);
 			}

 		} catch (Exception e) {
 			log.error("❌ Error auto-withdrawing manuscript {}", manuscriptId, e);
 		}
 		
 	}
 	
 	private void queueMail(Integer journalId, Integer manuscriptId, Integer version, Integer userId,
			Integer roleId, Integer taskId, TaskEmailMap template, Integer status)
			throws MalformedURLException, UnsupportedEncodingException {

		if (template == null || template.getJournalMailTemplates() == null)
			return;

		User user = userRepo.getByID(userId);
		if (user == null || user.getEmail() == null)
			return;

		String fromMail = getFromMail(template);
		EmailTriggerVO trigger = buildTriggerVO(journalId, manuscriptId, version, status, userId, taskId);
		String subject = emailTriggerTemplateUtils.getSubject(trigger, template);
		String body = emailTriggerTemplateUtils.getAllData(trigger, template);

		MailQueue mail = new MailQueue();
		mail.setIsActive(0);
		mail.setIsSeen(0);
		mail.setTaskId(taskId);
		mail.setJournalId(journalId);
		mail.setMail_send_date(new Date());
		mail.setMailFrom(fromMail);
		mail.setMailTo(user.getEmail());
		mail.setMailTOUserId(user.getId());
		mail.setMailToRole(roleId);
		mail.setMailSubject(subject);
		mail.setMailBody(body);
		mail.setManuscriptId(manuscriptId);
		mail.setManuscriptVersion(version);
		mail.setManuscriptStatus(status);

		mailQueueRepo.save(mail);
	}
 	
 	private EmailTriggerVO buildTriggerVO(Integer journalId, Integer manuscriptId, Integer version,
			Integer status, Integer userId, Integer taskId) {
		EmailTriggerVO vo = new EmailTriggerVO();
		vo.setJournalId(journalId);
		vo.setManuscriptId(manuscriptId);
		vo.setManuscriptVersion(version);
		vo.setManuscriptStatus(status);
		vo.setReceiverRoleId(AUTHOR_ROLE_ID);
		vo.setAuthorUserId(userId);
		vo.setTaskId(taskId);
		return vo;
	}

	private String getFromMail(TaskEmailMap map) {
		if (map.getJournalMailTemplates().getJournal().getJournalMetadata() != null
				&& map.getJournalMailTemplates().getJournal().getJournalMetadata().getJournalEmail() != null) {
			return map.getJournalMailTemplates().getJournal().getJournalMetadata().getJournalEmail();
		}
		return env.getProperty("fromMail");
	}
    
    private Date calculateDueDate(int journalId, int manuscriptId, int manuscriptVersion, int manuscriptStatus,
			Integer assignedUser) {

    // Log method entry with parameters
    	log.info("Entering calculateDueDate method with parameters: journalId={}, manuscriptId={}, manuscriptVersion={}, manuscriptStatus={}, assignedUser={}",
                journalId, manuscriptId, manuscriptVersion, manuscriptStatus, assignedUser);
    
    Date dueDate = null;
    try {
        // Log the repository call to retrieve the due date
    	log.info("Calling reminderTrackerRepo.findDueDate() with manuscriptId={}, manuscriptVersion={}, journalId={}, manuscriptStatus={}, assignedUser={}",
                    manuscriptId, manuscriptVersion, journalId, manuscriptStatus, assignedUser);
        
        // Fetch the due date from the repository
        dueDate = reminderTrackerRepo.findDueDate(manuscriptId, manuscriptVersion, journalId, manuscriptStatus, assignedUser);

        // Log the result of the due date
        if (dueDate != null) {
        	log.info("ReminderTrackerRepo returned a valid dueDate: {}", dueDate);
        } else {
        	log.info("No due date found for manuscriptId={}, manuscriptVersion={}", manuscriptId, manuscriptVersion);
        }
        
    } catch (Exception e) {
        // Log the exception with an info message indicating an error occurred
    	log.info("Error occurred while calculating due date for manuscriptId={}, manuscriptVersion={}: {}", manuscriptId, manuscriptVersion, e.getMessage());
    }

    // Log the method exit with the result (or lack thereof)
    if (dueDate != null) {
    	log.info("Exiting calculateDueDate method with dueDate: {}", dueDate);
    } else {
    	log.info("Exiting calculateDueDate method with no due date found.");
    }

    return dueDate;
}


	@Override
    public String autoReminderForAuthorForSchedular() {
        List<Integer> statusId = Arrays.asList(63, 64, 4, 46, 57, 62);
        List<Tuple> authorReminderArticles = manuScriptRepo.getAllManuscriptForReminderForSchedular(statusId);
        int count = 0;
        log.info("Author- Articles reminder size: " + authorReminderArticles.size());
        
        List<Integer> manuscriptIds = authorReminderArticles.stream()
                .map(t -> t.get(1, Integer.class))
                .collect(Collectors.toList());

        Map<String, ImpactArticleDTO> impactMap = Collections.emptyMap();
        if (!manuscriptIds.isEmpty()) {
            List<String> allManuscriptCodes = manuScriptRepo.getManuscriptCodesByIds(manuscriptIds);
            List<ImpactArticleDTO> impactList = authorProofingService.callFetchImpactBulk(allManuscriptCodes);
            impactMap = impactList.stream()
                    .collect(Collectors.toMap(ImpactArticleDTO::getAid, dto -> dto));
        }

        for (Tuple tuple : authorReminderArticles) {
            try {
                int journalId = tuple.get(0, Integer.class);
                int manuscriptId = tuple.get(1, Integer.class);
                int manuscriptVersion = tuple.get(2, Integer.class);
                int manuscriptStatus = tuple.get(3, Integer.class);
                Integer assignedUser = tuple.get(4, Integer.class);
                Date createdAt = tuple.get(5, Date.class);
                Date dueDate = tuple.get(8, Date.class);

                int taskId = getTaskIdByManuscriptStatus(manuscriptStatus);
                if (taskId == 0) {
                    log.error("No matching task found for manuscriptStatus: " + manuscriptStatus);
                    continue; // Skip processing this article
                }

                JournalPolicies reminderPolicies = journalPoliciesRepo.getJournalPoliciesDetails(journalId);
                if (reminderPolicies == null || (reminderPolicies.getReminderFrequencyAuthor() == null
                        || reminderPolicies.getReminderFrequencyAuthor() <= 0)) {
                    log.info("Author- No reminder policies found for journal: " + journalId);
                    continue; // Skip processing this article
                }

                int reminderPolicyForAuthor = reminderPolicies.getReminderFrequencyAuthor();
                log.info("Author- Reminder policies.no. of days: " + reminderPolicyForAuthor);

                Date dateobj = new Date();
                DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                ReminderTracker reminderTrackerData = reminderTrackerRepo.getReminderTrackerData(journalId,
                        manuscriptId, assignedUser, manuscriptStatus);
                Date startDate = (reminderTrackerData != null) ? reminderTrackerData.getCreatedDate() : createdAt;
                int reminderNo = (reminderTrackerData != null) ? reminderTrackerData.getReminderNo() : 0;

//				int tat = getTatForTask(taskId, journalId, reminderPolicyForAuthor);
//				Date dueDate = calculateDueDate(startDate, tat);

                int flow = determineFlow(dateobj, df, startDate, dueDate, reminderPolicyForAuthor, reminderTrackerData);
                log.info("Here's the flow: {}, for Manuscript ID: {}, Manuscript Status: {}, for Due Date: {}, for Reminder Days: {}"
                        , flow, manuscriptId, manuscriptStatus, dueDate, reminderPolicyForAuthor);
                if (flow == 0) {
                    log.info("<<<<<<<<Skipping the process for journalId: "+ journalId +" , ManuscriptId: "+manuscriptId +", Manuscript Status: "+manuscriptStatus+">>>>>>>>>>>");
                    continue; // Skip processing this article
                }

                //impact reminder
                // ✅ Impact filter
                if (manuscriptStatus == 57) {
                    ManuScript ms = manuScriptRepo.getFindByManuScriptDetail(manuscriptId);
                    ImpactArticleDTO impact = impactMap.get(ms.getManuscriptCode());
                    if (impact != null && Boolean.TRUE.equals(impact.getIsImpact())) {
                        log.info("Skipping impact article {} in Non-Impact scheduler", manuscriptId);
                        continue;
                    }
                }
                
                TaskEmailMapReminder getTaskEmailMapReminderData = taskEmailMapReminderRepo
                        .getTaskEmailMapDetails(taskId, journalId);
                if (getTaskEmailMapReminderData == null || !isEmailTemplateActive(getTaskEmailMapReminderData)) {
                    log.info("Author- No active email template found for taskId: {} and journalId: {} ", taskId, journalId);
                    continue; // Skip processing this article
                }

                sendReminderOrNotificationEmail(journalId, manuscriptId, manuscriptVersion, assignedUser, tuple, taskId,
                        reminderNo, getTaskEmailMapReminderData);
                log.info("manuscriptId : " + manuscriptId + " assignedUser : " + assignedUser);
                count++;
            } catch (ParseException e) {
                log.error("For author revision automailer failed getting error for article: "
                        + tuple.get(1, Integer.class), e);
            }
        }
        log.info("Total count of AutoReminderAuthor Mail : " + count);
        return "Run successfully";
    }
	
	public String autoReminderForAuthorForSchedularImpact() {
	    List<Integer> statusId = Arrays.asList(57);
	    List<Tuple> authorReminderArticles = manuScriptRepo.getAllManuscriptForReminderForSchedular(statusId);
	    log.info("Author- Impact Articles reminder size (before filtering): {}", authorReminderArticles.size());

	    // ✅ Collect manuscript codes from tuple (no DB hits)
	    List<String> allManuscriptCodes = authorReminderArticles.stream()
	            .map(t -> t.get(9, String.class)) // manuscript_code is at index 9 in SELECT
	            .filter(Objects::nonNull)
	            .collect(Collectors.toList());

	    // ✅ Fetch impact data in bulk
	    Map<String, ImpactArticleDTO> impactMap = Collections.emptyMap();
	    if (!allManuscriptCodes.isEmpty()) {
	        List<ImpactArticleDTO> impactList = authorProofingService.callFetchImpactBulk(allManuscriptCodes);
	        impactMap = impactList.stream()
	                .filter(dto -> Boolean.TRUE.equals(dto.getIsImpact()) &&
	                               dto.getAuthorReviewUrl() != null &&
	                               !dto.getAuthorReviewUrl().isEmpty())
	                .collect(Collectors.toMap(ImpactArticleDTO::getAid, dto -> dto));
	    }

	    // ✅ Pre-filter tuples in memory (no DB hit per row!)
	    Set<String> validImpactCodes = impactMap.keySet();
	    List<Tuple> impactTuples = authorReminderArticles.stream()
	            .filter(t -> validImpactCodes.contains(t.get(9, String.class))) // use manuscript_code from tuple
	            .collect(Collectors.toList());

	    log.info("Author- Impact Articles reminder size (after filtering): {}", impactTuples.size());

	    int count = 0;
	    for (Tuple tuple : impactTuples) {
	        try {
	            int journalId = tuple.get(0, Integer.class);
	            int manuscriptId = tuple.get(1, Integer.class);
	            int manuscriptVersion = tuple.get(2, Integer.class);
	            int manuscriptStatus = tuple.get(3, Integer.class);
	            Integer assignedUser = tuple.get(4, Integer.class);
	            Date createdAt = tuple.get(5, Date.class);
	            Date dueDate = tuple.get(8, Date.class);
	            // manuscript_code is at tuple.get(9, String.class) if needed

	            int taskId = 95;
	            JournalPolicies reminderPolicies = journalPoliciesRepo.getJournalPoliciesDetails(journalId);
	            if (reminderPolicies == null || reminderPolicies.getReminderFrequencyAuthor() == null
	                    || reminderPolicies.getReminderFrequencyAuthor() <= 0) {
	                continue;
	            }

	            int reminderPolicyForAuthor = reminderPolicies.getReminderFrequencyAuthor();
	            Date dateobj = new Date();
	            DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
	            ReminderTracker reminderTrackerData = reminderTrackerRepo.getReminderTrackerData(
	                    journalId, manuscriptId, assignedUser, manuscriptStatus);
	            Date startDate = (reminderTrackerData != null) ? reminderTrackerData.getCreatedDate() : createdAt;
	            int reminderNo = (reminderTrackerData != null) ? reminderTrackerData.getReminderNo() : 0;

	            int flow = determineFlow(dateobj, df, startDate, dueDate, reminderPolicyForAuthor, reminderTrackerData);
	            if (flow == 0) continue;

	            TaskEmailMapReminder getTaskEmailMapReminderData =
	                    taskEmailMapReminderRepo.getTaskEmailMapDetails(taskId, journalId);
	            if (getTaskEmailMapReminderData == null || !isEmailTemplateActive(getTaskEmailMapReminderData)) {
	                continue;
	            }

	            sendReminderOrNotificationEmail(journalId, manuscriptId, manuscriptVersion, assignedUser, tuple,
	                    taskId, reminderNo, getTaskEmailMapReminderData);
	            count++;
	        } catch (Exception e) {
	            log.error("Impact scheduler error for article: {}", tuple.get(1, Integer.class), e);
	        }
	    }

	    log.info("Total count of AutoReminderAuthor (Impact) Mail : {}", count);
	    return "Impact Run successfully";
	}


    private int getTaskIdByManuscriptStatus(int manuscriptStatus) {
        switch (manuscriptStatus) {
            case 4:
                return 10;
            case 46:
                return 11;
            case 57:
                return 28;
            case 62:
                return 39;
            case 63:
                return 68;
            case 64:
                return 67;
            default:
                return 0; // Default taskId when no match found
        }
    }

    private int getTatForTask(int taskId, int journalId, int reminderPolicyForAuthor) {
        WorkflowRoleSeq byWorkflowIdAndTaskId = workflowRoleRepo.getJournalWorkflowIdTaskId(taskId, journalId);
        return (byWorkflowIdAndTaskId != null) ? byWorkflowIdAndTaskId.getTat()
                : ((reminderPolicyForAuthor > 0) ? reminderPolicyForAuthor : 7);
    }

	/*
	 * private Date calculateDueDate(Date startDate, int tat) { long ltime =
	 * startDate.getTime() + tat * 24 * 60 * 60 * 1000; return new Date(ltime); }
	 */

    private int determineFlow(Date dateobj, DateFormat df, Date startDate, Date dueDate, int reminderPolicyForAuthor,
                              ReminderTracker reminderTrackerData) throws ParseException {
        Date todaysDateObj = df.parse(df.format(dateobj));
        Date startDateObj = df.parse(df.format(startDate));

        if (dueDate != null) {
            Date dueDateObj = df.parse(df.format(dueDate));
            long diff = todaysDateObj.getTime() - startDateObj.getTime();
            int diffDays = (int) (diff / (24 * 60 * 60 * 1000)); // difference in days

            if (todaysDateObj.after(dueDateObj) && (diffDays >= reminderPolicyForAuthor)) {
                // email reminder
                if (reminderTrackerData != null) {
                    long lastReminderSendDiff = todaysDateObj.getTime()
                            - reminderTrackerData.getCreatedDate().getTime();
                    int lastReminderSendDiffDays = (int) (lastReminderSendDiff / (24 * 60 * 60 * 1000));
                    log.info("lastReminderSendDiffDays: "+lastReminderSendDiffDays);
                    if (lastReminderSendDiffDays >= reminderPolicyForAuthor) {
                        log.info("Author- With reminder flow1...");
                        return 1;
                    }
                } else {
                    log.info("Author- Without reminder flow1...");
                    return 1;
                }
            } else if ((todaysDateObj.before(dueDateObj) && todaysDateObj.after(startDateObj)
                    && (diffDays >= reminderPolicyForAuthor))
                    || (todaysDateObj.equals(dueDateObj) && (diffDays >= reminderPolicyForAuthor))) {
                // notification
                if (reminderTrackerData != null) {
                    long lastReminderSendDiff = todaysDateObj.getTime()
                            - reminderTrackerData.getCreatedDate().getTime();
                    int lastReminderSendDiffDays = (int) (lastReminderSendDiff / (24 * 60 * 60 * 1000));
                    if (lastReminderSendDiffDays >= reminderPolicyForAuthor) {
                        log.info("Author- With reminder flow2...");
                        return 2;
                    }
                } else {
                    log.info("Author- Without reminder flow2...");
                    return 2;
                }
            }
        }
        return 0;
    }

    private boolean isEmailTemplateActive(TaskEmailMapReminder taskEmailMapReminderData) {
        return taskEmailMapReminderData.getJournalMailTemplates().getEmailTemplateForm().equalsIgnoreCase("REMINDER")
                || taskEmailMapReminderData.getJournalMailTemplates().getEmailTemplateForm()
                .equalsIgnoreCase("NOTIFICATION")
                && taskEmailMapReminderData.getJournalMailTemplates().getIsActive().equalsIgnoreCase("yes");
    }

    private void sendReminderOrNotificationEmail(int journalId, int manuscriptId, int manuscriptVersion,
                                                 Integer assignedUser, Tuple tuple, int taskId, int reminderNo,
                                                 TaskEmailMapReminder getTaskEmailMapReminderData) {

        List<String> mailCcTemp = new ArrayList<>();
        String mailCcString = null;

        EmailTriggerVO addEmailTriggerVOData = new EmailTriggerVO();
        addEmailTriggerVOData.setJournalId(journalId);
        addEmailTriggerVOData.setManuscriptId(manuscriptId);
        addEmailTriggerVOData.setManuscriptVersion(manuscriptVersion);
        addEmailTriggerVOData.setAuthorUserId(assignedUser);
        addEmailTriggerVOData.setReceiverRoleId(4);

        String ccMailId = getTaskEmailMapReminderData.getJournalMailTemplates().getMailcc();
        String mailSubject = emailTriggerTemplateUtils.getSubjectForReminder(addEmailTriggerVOData,
                getTaskEmailMapReminderData);

        try {
            String mailBody = emailTriggerTemplateUtils.getAllDataForReminder(addEmailTriggerVOData,
                    getTaskEmailMapReminderData);

            MailQueue addMailQueue = new MailQueue();
            addMailQueue.setIsSeen(0);
            addMailQueue.setJournalId(journalId);
            addMailQueue.setMail_send_date(new Date());
            addMailQueue.setMailBody(mailBody);
            addMailQueue.setMailTo(tuple.get(7, String.class));
            addMailQueue.setMailCC(ccMailId);

            //AIAN Requirements starts
            // submission payment reminder - 67
            // tech mod reminder 11
            // revision reminder 10
            // apc rem 68
            // author check 39
            // author proof 28

            if ((journalId==259 || journalId==1819)
                    && (taskId == 67 || taskId == 11 || taskId == 10 || taskId == 68 || taskId == 39 || taskId == 28)) {
                List<ManuScriptCoAuthor> manuscriptCorrespondingAuthors = manuScriptCoAuthorRepo
                        .getCorrespondingAuthorListFromManuscriptId(manuscriptId);
                manuscriptCorrespondingAuthors
                        .removeIf(manuScriptCoAuthor -> manuScriptCoAuthor
                                .getCoAuthorId()
                                .equals(assignedUser));
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

            addMailQueue.setMailSubject(mailSubject);
            addMailQueue.setMailTOUserId(assignedUser);
            addMailQueue.setMailToRole(4);
            addMailQueue.setIsActive(0);
            addMailQueue.setManuscriptId(manuscriptId);
            addMailQueue.setManuscriptVersion(manuscriptVersion);
            addMailQueue.setMailFrom(tuple.get(6, String.class));
            addMailQueue.setManuscriptStatus(tuple.get(3, Integer.class));
            addMailQueue.setTaskId(taskId);
            addMailQueue.setMailToRole(4);
            addMailQueue.setModuleName("AutoReminderAuthor");
            mailQueueRepo.saveAndFlush(addMailQueue);
            log.info("Author- Inserted into mail queue table for manuscript_id: " + manuscriptId
                    + " AutoReminderAuthor");

            ReminderTracker reminderTracker = new ReminderTracker();
            reminderTracker.setJournalId(journalId);
            reminderTracker.setManuscriptId(manuscriptId);
            reminderTracker.setManuscriptStatusId(tuple.get(3, Integer.class));
            reminderTracker.setAssignedUser(assignedUser);
            reminderTracker.setReminderNo(reminderNo + 1);
            reminderTracker.setReminderStatus(1);
            reminderTracker.setIsActive(1);
            reminderTracker.setCreatedBy("");
            reminderTracker.setCreatedDate(new Date());
            reminderTracker.setUpdatedBy("");
            reminderTracker.setUpdatedDate(new Date());
            reminderTrackerRepo.saveAndFlush(reminderTracker);
            log.info("Author- Inserted into reminder_tracker table for manuscript_id: " + manuscriptId
                    + " AutoReminderAuthor");
        } catch (MalformedURLException | UnsupportedEncodingException e) {
            log.error("Error while sending reminder/notification email for manuscript_id: " + manuscriptId, e);
        }
    }

    @Override
    public String autoReminderForReviewer() {
        List<Integer> statusIdReviewer = Arrays.asList(8, 35, 66);
        List<Integer> statusId = Arrays.asList(8, 66);
        List<Tuple> reviewerReminderArticles = manuScriptRepo.getAllManuscriptForReminder(statusId);
        String acceptByReviewerUrl = "";
        String rejectByReviewerUrl = "";
        String viewByReviewerUrl = "";
        if (serverENV.equalsIgnoreCase("AWS")) {
            acceptByReviewerUrl = "https://review.jow.medknow.com/outside/accept-review-invitation";
            rejectByReviewerUrl = "https://review.jow.medknow.com/outside/reject-review-invitation";
            viewByReviewerUrl = "https://review.jow.medknow.com/outside/view-abstract";
        } else {
            acceptByReviewerUrl = "https://uniprruat.digiscapetech.com/outside/accept-review-invitation";
            rejectByReviewerUrl = "https://uniprruat.digiscapetech.com/outside/reject-review-invitation";
            viewByReviewerUrl = "https://uniprruat.digiscapetech.com/outside/view-abstract";
        }
        log.info("Reviewer -  Articles reminder size............: " + reviewerReminderArticles.size());
        for (Tuple tuple : reviewerReminderArticles) {

            try {
                int journalId = tuple.get(0, Integer.class);
                int manuscriptId = tuple.get(1, Integer.class);
                int manuscriptVersion = tuple.get(2, Integer.class);
                int manuscriptStatus = tuple.get(3, Integer.class);
                int taskId;
                if (manuscriptStatus == 8) {
                    taskId = 4;
                } else if (manuscriptStatus == 66) {
                    taskId = 26;
                } else {
                    taskId = 0; // Default taskId when no match found
                }
                if (taskId == 0) {
                    log.error("No matching task found for manuscriptStatus: " + manuscriptStatus);
                    continue; // Skip processing this article
                }
                Date dueDate = null;
                JournalPolicies reminderPolicies = journalPoliciesRepo.getJournalPoliciesDetails(journalId);
                if (reminderPolicies == null || (reminderPolicies.getReminderFrequencyReviewers() == null
                        || reminderPolicies.getReminderFrequencyReviewers() <= 0)) {
                    log.info("Author- No reminder policies found for journal: " + journalId);
                    continue; // Skip processing this article
                }

                int reminderPolicyForReviewer = reminderPolicies.getReminderFrequencyReviewers();
                log.info("Reviewer-  Reminder policies.no. of days.............: " + reminderPolicyForReviewer);

                Date dateobj = new Date();

                DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                WorkflowRoleSeq byWorkflowIdAndTaskId = workflowRoleRepo.getJournalWorkflowIdTaskId(taskId, journalId);
                int tat = 0;
                if (byWorkflowIdAndTaskId != null) {
                    tat = byWorkflowIdAndTaskId.getTat();
                }

                List<ManuScriptReviewer> listManuScriptReviewer = manuScriptReviewerRepo
                        .getmanuScriptReviewerForReminder(manuscriptId, journalId, statusIdReviewer, manuscriptVersion);

                if (listManuScriptReviewer == null || listManuScriptReviewer.size() == 0) {
                    log.info("No active reviewer for sending auto reminder {} {}", journalId, manuscriptId);
                    continue; // Skip processing this article
                }
                for (ManuScriptReviewer manuScriptReviewer : listManuScriptReviewer) {
                    try {
                        Date createdAt = manuScriptReviewer.getAssignedDate();
                        Integer assignedUser = manuScriptReviewer.getReviewerId();
                        Integer manuscriptStatusId = manuScriptReviewer.getManuscriptStatus();

                        ReminderTracker reminderTrackerData = reminderTrackerRepo.getReminderTrackerData(journalId,
                                manuscriptId, assignedUser, manuscriptStatusId);
                        Date startDate = null;
                        Integer reminderNo = 0;
                        Date lastReminderSent = null;
                        if (reminderTrackerData != null) {
                            lastReminderSent = reminderTrackerData.getCreatedDate();
                            reminderNo = reminderTrackerData.getReminderNo();
                            log.info("Reviewer -  Last reminder Sent..: " + lastReminderSent + " Reminder No.. "
                                    + reminderNo);
                        }

                        startDate = createdAt;
                        long ltime = startDate.getTime() + tat * 24 * 60 * 60 * 1000;
                        dueDate = new Date(ltime);
                        int flow = 0;
                        Date todaysDateObj = df.parse(df.format(dateobj));
                        Date startDateObj = df.parse(df.format(startDate));
                        if (dueDate != null) {
                            Date dueDateObj = df.parse(df.format(dueDate));
                            long diff = todaysDateObj.getTime() - startDateObj.getTime();
                            int diffDays = (int) (diff / (24 * 60 * 60 * 1000));// difference in days
                            if (todaysDateObj.after(dueDateObj) && diffDays >= reminderPolicyForReviewer) {
                                // email reminder
                                if (lastReminderSent != null) {
                                    long lastReminderSendDiff = todaysDateObj.getTime() - lastReminderSent.getTime();// new
                                    int lastReminderSendDiffDays = (int) (lastReminderSendDiff / (24 * 60 * 60 * 1000));// new

                                    if (lastReminderSendDiffDays >= reminderPolicyForReviewer) {
                                        flow = 1;
                                        log.info("Reviewer - With reminder flow1...: ");
                                    }
                                } else {
                                    flow = 1;
                                    log.info("Reviewer - Without reminder flow1...: ");
                                }
                            } else if ((todaysDateObj.before(dueDateObj) || todaysDateObj.equals(dueDateObj))
                                    && diffDays >= reminderPolicyForReviewer && todaysDateObj.after(startDateObj)) {
                                // notification
                                if (lastReminderSent != null) {
                                    long lastReminderSendDiff = todaysDateObj.getTime() - lastReminderSent.getTime();// new
                                    int lastReminderSendDiffDays = (int) (lastReminderSendDiff / (24 * 60 * 60 * 1000));// new

                                    if (lastReminderSendDiffDays >= reminderPolicyForReviewer) {
                                        flow = 2;
                                        log.info("Reviewer - With reminder flow2...: ");
                                    }
                                } else {
                                    flow = 2;
                                    log.info("Reviewer - Without reminder flow2...: ");
                                }
                            }
                        }

                        if (flow == 1 || flow == 2) {

                            TaskEmailMapReminder getTaskEmailMapReminderData = taskEmailMapReminderRepo
                                    .getTaskEmailMapDetails(taskId, journalId);// what will be the task id eg. 4 ##
                            if (getTaskEmailMapReminderData != null && getTaskEmailMapReminderData
                                    .getJournalMailTemplates().getIsActive().equalsIgnoreCase("yes")) {
                                String fromMailID = "";
                                String toMailID = "";
                                String mailSubject = "";
                                String mailBody = "";
                                String ccMailId = "";
                                EmailTriggerVO addEmailTriggerVOData = new EmailTriggerVO();
                                addEmailTriggerVOData.setJournalId(journalId);
                                addEmailTriggerVOData.setManuscriptId(manuscriptId);
                                addEmailTriggerVOData.setManuscriptVersion(manuscriptVersion);
                                addEmailTriggerVOData.setReviewerUserId(assignedUser);
                                addEmailTriggerVOData.setReceiverRoleId(5);
                                if (getTaskEmailMapReminderData.getJournalMailTemplates() != null) {
                                    fromMailID = getTaskEmailMapReminderData.getJournalMailTemplates().getMailFrom();
                                    ccMailId = getTaskEmailMapReminderData.getJournalMailTemplates().getMailcc();
                                }
                                addEmailTriggerVOData.setUrlManuscript(manuscriptId);
                                addEmailTriggerVOData.setUrlManuscriptVersion(manuscriptVersion);
                                addEmailTriggerVOData.setUrlJournalId(journalId);
                                addEmailTriggerVOData.setUrlReviewerId(assignedUser);
                                addEmailTriggerVOData.setUrlManuscriptStatus(manuscriptStatus);
                                addEmailTriggerVOData.setAcceptByReviewerUrl(acceptByReviewerUrl);
                                addEmailTriggerVOData.setRejectByReviewerUrl(rejectByReviewerUrl);
                                addEmailTriggerVOData.setViewByReviewerUrl(viewByReviewerUrl);

                                toMailID = manuScriptReviewer.getUser().getEmail();
                                mailSubject = emailTriggerTemplateUtils.getSubjectForReminder(addEmailTriggerVOData,
                                        getTaskEmailMapReminderData);

                                try {
                                    addEmailTriggerVOData.setReviewerDueDate(manuScriptReviewer.getDueDate());
                                    mailBody = emailTriggerTemplateUtils.getAllDataForReminder(addEmailTriggerVOData,
                                            getTaskEmailMapReminderData);
                                    if (flow == 1 || flow == 2) {
                                        MailQueue addMailQueue = new MailQueue();
                                        addMailQueue.setIsSeen(0);
                                        addMailQueue.setJournalId(addEmailTriggerVOData.getJournalId());
                                        addMailQueue.setMail_send_date(new Date());
                                        addMailQueue.setMailBody(mailBody);
                                        addMailQueue.setMailTo(toMailID);
                                        addMailQueue.setMailCC(ccMailId);
                                        addMailQueue.setMailSubject(mailSubject);
                                        addMailQueue.setMailTOUserId(assignedUser);
                                        addMailQueue.setMailToRole(addEmailTriggerVOData.getReceiverRoleId());
                                        if (flow == 1) {
                                            addMailQueue.setIsActive(0);
                                        } else {
                                            addMailQueue.setIsActive(0);
                                        }
                                        addMailQueue.setManuscriptId(manuscriptId);
                                        addMailQueue.setManuscriptVersion(manuscriptVersion);
                                        addMailQueue.setMailFrom(tuple.get(6, String.class));
                                        addMailQueue.setManuscriptStatus(manuscriptStatusId);
                                        addMailQueue.setTaskId(taskId);
                                        addMailQueue.setMailToRole(5);
                                        addMailQueue.setModuleName("AutoReminderReviewer");
                                        mailQueueRepo.saveAndFlush(addMailQueue);
                                        log.info("Reviewer - Insert query mail queue table and manuscript_id ...: "
                                                + manuscriptId + "  AutoReminderReviewer");

                                        // insert data in reminder_tracker table
                                        ReminderTracker reminderTracker = new ReminderTracker();
                                        reminderTracker.setJournalId(journalId);
                                        reminderTracker.setManuscriptId(manuscriptId);
                                        reminderTracker.setManuscriptStatusId(manuscriptStatusId);
                                        reminderTracker.setAssignedUser(assignedUser);
                                        reminderTracker.setReminderNo(reminderNo + 1);
                                        reminderTracker.setReminderStatus(1);
                                        reminderTracker.setIsActive(1);
                                        reminderTracker.setCreatedBy("");
                                        reminderTracker.setCreatedDate(new Date());
                                        reminderTracker.setUpdatedBy("");
                                        reminderTracker.setUpdatedDate(new Date());
                                        reminderTrackerRepo.saveAndFlush(reminderTracker);
                                        log.info("Reviewer- Insert query reminder_tracker table and manuscript_id ...: "
                                                + manuscriptId + "  AutoReminderReviewer");
                                    }
                                } catch (MalformedURLException | UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return "Run sucessfully";
    }


    public String autoReminderForReviewerForSchedular() {
        List<Integer> statusIdReviewer = Arrays.asList(8, 35, 66);
        List<Integer> statusId = Arrays.asList(8, 66);
        List<Tuple> reviewerReminderArticles = manuScriptRepo.getAllManuscriptForReminder(statusId);
        String acceptByReviewerUrl = "";
        String rejectByReviewerUrl = "";
        String viewByReviewerUrl = "";
        if (serverENV.equalsIgnoreCase("AWS")) {
            acceptByReviewerUrl = "https://review.jow.medknow.com/outside/accept-review-invitation";
            rejectByReviewerUrl = "https://review.jow.medknow.com/outside/reject-review-invitation";
            viewByReviewerUrl = "https://review.jow.medknow.com/outside/view-abstract";
        } else {
            acceptByReviewerUrl = "https://uniprruat.digiscapetech.com/outside/accept-review-invitation";
            rejectByReviewerUrl = "https://uniprruat.digiscapetech.com/outside/reject-review-invitation";
            viewByReviewerUrl = "https://uniprruat.digiscapetech.com/outside/view-abstract";
        }
        log.info("Reviewer -  Articles reminder size............: " + reviewerReminderArticles.size());
        for (Tuple tuple : reviewerReminderArticles) {

            try {
                int journalId = tuple.get(0, Integer.class);
                int manuscriptId = tuple.get(1, Integer.class);
                int manuscriptVersion = tuple.get(2, Integer.class);
                int manuscriptStatus = tuple.get(3, Integer.class);
                int taskId;
                if (manuscriptStatus == 8) {
                    taskId = 4;
                } else if (manuscriptStatus == 66) {
                    taskId = 26;
                } else {
                    taskId = 0; // Default taskId when no match found
                }
                if (taskId == 0) {
                    log.error("No matching task found for manuscriptStatus: " + manuscriptStatus);
                    continue; // Skip processing this article
                }
                Date dueDate = null;
                JournalPolicies reminderPolicies = journalPoliciesRepo.getJournalPoliciesDetails(journalId);
                if (reminderPolicies == null || (reminderPolicies.getReminderFrequencyReviewers() == null
                        || reminderPolicies.getReminderFrequencyReviewers() <= 0)) {
                    log.info("Author- No reminder policies found for journal: " + journalId);
                    continue; // Skip processing this article
                }

                int reminderPolicyForReviewer = reminderPolicies.getReminderFrequencyReviewers();
                log.info("Reviewer-  Reminder policies.no. of days.............: " + reminderPolicyForReviewer);

                Date dateobj = new Date();

                DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                WorkflowRoleSeq byWorkflowIdAndTaskId = workflowRoleRepo.getJournalWorkflowIdTaskId(taskId, journalId);
                int tat = 0;
                if (byWorkflowIdAndTaskId != null) {
                    tat = byWorkflowIdAndTaskId.getTat();
                }

                List<ManuScriptReviewer> listManuScriptReviewer = manuScriptReviewerRepo
                        .getmanuScriptReviewerForReminderForSchedular(manuscriptId, journalId, statusIdReviewer, manuscriptVersion);

                if (listManuScriptReviewer == null || listManuScriptReviewer.size() == 0) {
                    log.info("No active reviewer for sending auto reminder {} {}", journalId, manuscriptId);
                    continue; // Skip processing this article
                }
                for (ManuScriptReviewer manuScriptReviewer : listManuScriptReviewer) {
                    try {
                        Date createdAt = manuScriptReviewer.getAssignedDate();
                        Integer assignedUser = manuScriptReviewer.getReviewerId();
                        Integer manuscriptStatusId = manuScriptReviewer.getManuscriptStatus();

                        ReminderTracker reminderTrackerData = reminderTrackerRepo.getReminderTrackerData(journalId,
                                manuscriptId, assignedUser, manuscriptStatusId);
                        Date startDate = null;
                        Integer reminderNo = 0;
                        Date lastReminderSent = null;
                        if (reminderTrackerData != null) {
                            lastReminderSent = reminderTrackerData.getCreatedDate();
                            reminderNo = reminderTrackerData.getReminderNo();
                            log.info("Reviewer -  Last reminder Sent..: " + lastReminderSent + " Reminder No.. "
                                    + reminderNo);
                        }

                        startDate = createdAt;
                        long ltime = startDate.getTime() + tat * 24 * 60 * 60 * 1000;
                        dueDate = new Date(ltime);
                        int flow = 0;
                        Date todaysDateObj = df.parse(df.format(dateobj));
                        Date startDateObj = df.parse(df.format(startDate));
                        if (dueDate != null) {
                            Date dueDateObj = df.parse(df.format(dueDate));
                            long diff = todaysDateObj.getTime() - startDateObj.getTime();
                            int diffDays = (int) (diff / (24 * 60 * 60 * 1000));// difference in days
                            if (todaysDateObj.after(dueDateObj) && diffDays >= reminderPolicyForReviewer) {
                                // email reminder
                                if (lastReminderSent != null) {
                                    long lastReminderSendDiff = todaysDateObj.getTime() - lastReminderSent.getTime();// new
                                    int lastReminderSendDiffDays = (int) (lastReminderSendDiff / (24 * 60 * 60 * 1000));// new

                                    if (lastReminderSendDiffDays >= reminderPolicyForReviewer) {
                                        flow = 1;
                                        log.info("Reviewer - With reminder flow1...: ");
                                    }
                                } else {
                                    flow = 1;
                                    log.info("Reviewer - Without reminder flow1...: ");
                                }
                            } else if ((todaysDateObj.before(dueDateObj) || todaysDateObj.equals(dueDateObj))
                                    && diffDays >= reminderPolicyForReviewer && todaysDateObj.after(startDateObj)) {
                                // notification
                                if (lastReminderSent != null) {
                                    long lastReminderSendDiff = todaysDateObj.getTime() - lastReminderSent.getTime();// new
                                    int lastReminderSendDiffDays = (int) (lastReminderSendDiff / (24 * 60 * 60 * 1000));// new

                                    if (lastReminderSendDiffDays >= reminderPolicyForReviewer) {
                                        flow = 2;
                                        log.info("Reviewer - With reminder flow2...: ");
                                    }
                                } else {
                                    flow = 2;
                                    log.info("Reviewer - Without reminder flow2...: ");
                                }
                            }
                        }

                        if (flow == 1 || flow == 2) {

                            TaskEmailMapReminder getTaskEmailMapReminderData = taskEmailMapReminderRepo
                                    .getTaskEmailMapDetails(taskId, journalId);// what will be the task id eg. 4 ##
                            if (getTaskEmailMapReminderData != null && getTaskEmailMapReminderData
                                    .getJournalMailTemplates().getIsActive().equalsIgnoreCase("yes")) {
                                String fromMailID = "";
                                String toMailID = "";
                                String mailSubject = "";
                                String mailBody = "";
                                String ccMailId = "";
                                EmailTriggerVO addEmailTriggerVOData = new EmailTriggerVO();
                                addEmailTriggerVOData.setJournalId(journalId);
                                addEmailTriggerVOData.setManuscriptId(manuscriptId);
                                addEmailTriggerVOData.setManuscriptVersion(manuscriptVersion);
                                addEmailTriggerVOData.setReviewerUserId(assignedUser);
                                addEmailTriggerVOData.setReceiverRoleId(5);
                                if (getTaskEmailMapReminderData.getJournalMailTemplates() != null) {
                                    fromMailID = getTaskEmailMapReminderData.getJournalMailTemplates().getMailFrom();
                                    ccMailId = getTaskEmailMapReminderData.getJournalMailTemplates().getMailcc();
                                }
                                addEmailTriggerVOData.setUrlManuscript(manuscriptId);
                                addEmailTriggerVOData.setUrlManuscriptVersion(manuscriptVersion);
                                addEmailTriggerVOData.setUrlJournalId(journalId);
                                addEmailTriggerVOData.setUrlReviewerId(assignedUser);
                                addEmailTriggerVOData.setUrlManuscriptStatus(manuscriptStatus);
                                addEmailTriggerVOData.setAcceptByReviewerUrl(acceptByReviewerUrl);
                                addEmailTriggerVOData.setRejectByReviewerUrl(rejectByReviewerUrl);
                                addEmailTriggerVOData.setViewByReviewerUrl(viewByReviewerUrl);

                                toMailID = manuScriptReviewer.getUser().getEmail();
                                mailSubject = emailTriggerTemplateUtils.getSubjectForReminder(addEmailTriggerVOData,
                                        getTaskEmailMapReminderData);

                                try {
                                    Date reviewerDueDate = df.parse(df.format(manuScriptReviewer.getDueDate()));
                                    addEmailTriggerVOData.setReviewerDueDate(reviewerDueDate);
                                    mailBody = emailTriggerTemplateUtils.getAllDataForReminder(addEmailTriggerVOData,
                                            getTaskEmailMapReminderData);
                                    if (flow == 1 || flow == 2) {
                                        MailQueue addMailQueue = new MailQueue();
                                        addMailQueue.setIsSeen(0);
                                        addMailQueue.setJournalId(addEmailTriggerVOData.getJournalId());
                                        addMailQueue.setMail_send_date(new Date());
                                        addMailQueue.setMailBody(mailBody);
                                        addMailQueue.setMailTo(toMailID);
                                        addMailQueue.setMailCC(ccMailId);
                                        addMailQueue.setMailSubject(mailSubject);
                                        addMailQueue.setMailTOUserId(assignedUser);
                                        addMailQueue.setMailToRole(addEmailTriggerVOData.getReceiverRoleId());
                                        if (flow == 1) {
                                            addMailQueue.setIsActive(0);
                                        } else {
                                            addMailQueue.setIsActive(0);
                                        }
                                        addMailQueue.setManuscriptId(manuscriptId);
                                        addMailQueue.setManuscriptVersion(manuscriptVersion);
                                        addMailQueue.setMailFrom(tuple.get(6, String.class));
                                        addMailQueue.setManuscriptStatus(manuscriptStatusId);
                                        addMailQueue.setTaskId(taskId);
                                        addMailQueue.setMailToRole(5);
                                        addMailQueue.setModuleName("AutoReminderReviewer");
                                        mailQueueRepo.saveAndFlush(addMailQueue);
                                        log.info("Reviewer - Insert query mail queue table and manuscript_id ...: "
                                                + manuscriptId + "  AutoReminderReviewer");

                                        // insert data in reminder_tracker table
                                        ReminderTracker reminderTracker = new ReminderTracker();
                                        reminderTracker.setJournalId(journalId);
                                        reminderTracker.setManuscriptId(manuscriptId);
                                        reminderTracker.setManuscriptStatusId(manuscriptStatusId);
                                        reminderTracker.setAssignedUser(assignedUser);
                                        reminderTracker.setReminderNo(reminderNo + 1);
                                        reminderTracker.setReminderStatus(1);
                                        reminderTracker.setIsActive(1);
                                        reminderTracker.setCreatedBy("");
                                        reminderTracker.setCreatedDate(new Date());
                                        reminderTracker.setUpdatedBy("");
                                        reminderTracker.setUpdatedDate(new Date());
                                        reminderTrackerRepo.saveAndFlush(reminderTracker);
                                        log.info("Reviewer- Insert query reminder_tracker table and manuscript_id ...: "
                                                + manuscriptId + "  AutoReminderReviewer");
                                    }
                                } catch (MalformedURLException | UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return "Run sucessfully";
    }

    @Override
    @Transactional
    public String autoNoresponseReviewerReminderForEditor() {
        try {
            Integer taskId = 0;
            List<ManuScriptForDashboard> editorReminderArticles = manuscriptDashboardArticleService
                    .getNoResponseReminder();
            log.info("No response reviewer -  Articles reminder size............: " + editorReminderArticles.size());
            for (ManuScriptForDashboard editorManuScript : editorReminderArticles) {
                try {
                    int journalId = editorManuScript.getJournalId();

                    int manuscriptId = editorManuScript.getManuScriptId();
                    int manuscriptVersion = editorManuScript.getManuscriptVersion();
                    int manuscriptStatus = editorManuScript.getManuscriptStatusId();
                    log.info("Manuscript ID: {}, Version: {}, Status: {}", manuscriptId, manuscriptVersion, manuscriptStatus);
                    if (manuscriptStatus == 8 || manuscriptStatus == 66) {
                        taskId = 64;
                    }
                    Date dueDate = null;
                    JournalPolicies reminderPolicies = null;
                    try {
                        reminderPolicies = journalPoliciesRepo.getJournalPoliciesDetails(journalId);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    int reminderPolicyForEicEditor = 0;
                    if (reminderPolicies != null && reminderPolicies.getReminderFrequencyEicEditor() != null) {
                        reminderPolicyForEicEditor = reminderPolicies.getReminderFrequencyEicEditor();
                        log.info("Editor-- Reminder policies.no. of days............: " + reminderPolicyForEicEditor);
                    } else {
                        continue;
                    }

                    Date dateobj = new Date();

                    DateFormat df = new SimpleDateFormat("MMM dd, yyyy");

                    Date createdAt = editorManuScript.getNoResponseNotificationDate();
                    log.info("createdAt: {}"+createdAt);
                    Integer assignedUser = editorManuScript.getAssignedUser();
                    log.info("assignedUser: {}"+assignedUser);
                    ReminderTracker reminderTrackerData = null;
                    reminderTrackerData = reminderTrackerRepo.getReminderTrackerData(journalId, manuscriptId,
                            assignedUser, 64);
                    log.info("reminderTrackerData: {}",reminderTrackerData);
                    Date startDate = null;
                    Integer reminderNo = 0;
                    Date lastReminderSent = null;
                    if (reminderTrackerData != null) {
                        lastReminderSent = reminderTrackerData.getCreatedDate();// get date from tracker table if data
                        // is present in tracker table
                        reminderNo = reminderTrackerData.getReminderNo();
                        log.info(
                                "Editor-  Last reminder Sent..: " + lastReminderSent + " Reminder No.. " + reminderNo);
                    }

                    startDate = createdAt;
                    JournalMetadata metaDatalistByJournalId = null;
                    metaDatalistByJournalId = journalMetadataRepo.metaDatalistByJournalId(journalId);
                    Integer workflowId = metaDatalistByJournalId.getWorkflowId();
                    log.info("Workflow Id....: " + workflowId);
                    // in case of editor task_id=64
                    WorkflowRoleSeq byWorkflowIdAndTaskId = null;
                    byWorkflowIdAndTaskId = workflowRoleRepo.getJournalWorkflowIdTaskId(taskId, journalId);

                    if (byWorkflowIdAndTaskId != null) {
                        Integer tat = byWorkflowIdAndTaskId.getTat();
                        long ltime = startDate.getTime() + tat * 24 * 60 * 60 * 1000;
                        dueDate = new Date(ltime);
                        log.info("tat: {}, ltime: {}, dueDate: {}", tat, ltime, dueDate);
                    }

                    int flow = 0;
                    Date todaysDateObj = df.parse(df.format(dateobj));
                    Date startDateObj = df.parse(df.format(startDate));
                    log.info("todaysDateObj: {}, startDateObj: {}", todaysDateObj, startDateObj);
                    if (dueDate != null) {

                        Date dueDateObj = df.parse(df.format(dueDate));
                        log.info("dueDateObj: {}",dueDateObj);
                        long diff = todaysDateObj.getTime() - startDateObj.getTime();
                        int diffDays = (int) (diff / (24 * 60 * 60 * 1000));// difference in days
                        log.info("diffDays: {}",diffDays);
                        if (todaysDateObj.after(dueDateObj) && (diffDays >= reminderPolicyForEicEditor)) {
                            // email reminder sent to enduser
                            if (lastReminderSent != null) {
                                long lastReminderSendDiff = todaysDateObj.getTime() - lastReminderSent.getTime();// new
                                int lastReminderSendDiffDays = (int) (lastReminderSendDiff / (24 * 60 * 60 * 1000));// new

                                if (lastReminderSendDiffDays >= reminderPolicyForEicEditor) {
                                    flow = 1;
                                    log.info("Editor with reminder flow1...: ");
                                }
                            } else {
                                flow = 1;
                                log.info("Editor without reminder flow1...: ");
                            }

                        } else if ((todaysDateObj.before(dueDateObj) && todaysDateObj.after(startDateObj)
                                && (diffDays >= reminderPolicyForEicEditor))
                                || (todaysDateObj.equals(dueDateObj) && (diffDays >= reminderPolicyForEicEditor))) {
                            // notification only shown to user (not sent any email)
                            if (lastReminderSent != null) {
                                long lastReminderSendDiff = todaysDateObj.getTime() - lastReminderSent.getTime();// new
                                int lastReminderSendDiffDays = (int) (lastReminderSendDiff / (24 * 60 * 60 * 1000));// new

                                if (lastReminderSendDiffDays >= reminderPolicyForEicEditor) {
                                    flow = 2;
                                    log.info("Editor with reminder flow2...: ");
                                }
                            } else {
                                flow = 2;
                                log.info("Editor without reminder flow2...: ");
                            }
                        }
                    }

                    if (flow == 1 || flow == 2) {
                        User userDetail = null;
                        userDetail = userRepo.getByID(assignedUser);
                        String lastName = userDetail.getLastName();
                        TaskEmailMapReminder getTaskEmailMapReminderData = null;

                        getTaskEmailMapReminderData = taskEmailMapReminderRepo.getTaskEmailMapDetails(taskId,
                                journalId);// what will be the task id eg. 4 ##
                        if (getTaskEmailMapReminderData != null) {
                            String fromMailID = "";
                            String toMailID = "";
                            String mailSubject = "";
                            String mailBody = "";
                            String ccMailId = "";
                            if ((getTaskEmailMapReminderData.getJournalMailTemplates().getEmailTemplateForm()
                                    .equalsIgnoreCase("REMINDER")
                                    || getTaskEmailMapReminderData.getJournalMailTemplates().getEmailTemplateForm()
                                    .equalsIgnoreCase("NOTIFICATION"))
                                    && getTaskEmailMapReminderData.getJournalMailTemplates().getIsActive()
                                    .equalsIgnoreCase("yes")) {
                                EmailTriggerVO addEmailTriggerVOData = new EmailTriggerVO();
                                addEmailTriggerVOData.setJournalId(journalId);
                                addEmailTriggerVOData.setManuscriptId(manuscriptId);
                                addEmailTriggerVOData.setManuscriptVersion(manuscriptVersion);
                                addEmailTriggerVOData.setEditorUserId(assignedUser);// Not correct
                                addEmailTriggerVOData.setReceiverRoleId(3);
                                if (userDetail != null) {
                                    if (getTaskEmailMapReminderData.getJournalMailTemplates() != null) {
                                        fromMailID = getTaskEmailMapReminderData.getJournalMailTemplates()
                                                .getMailFrom();
                                        ccMailId = getTaskEmailMapReminderData.getJournalMailTemplates().getMailcc();
                                    }

                                    toMailID = userDetail.getEmail();
                                    mailSubject = emailTriggerTemplateUtils.getSubjectForReminder(addEmailTriggerVOData,
                                            getTaskEmailMapReminderData);

                                    mailBody = emailTriggerTemplateUtils.getAllDataForReminder(addEmailTriggerVOData,
                                            getTaskEmailMapReminderData);

                                    if (flow == 1 || flow == 2) {
                                        MailQueue addMailQueue = new MailQueue();
                                        addMailQueue.setIsSeen(0);
                                        addMailQueue.setJournalId(addEmailTriggerVOData.getJournalId());
                                        addMailQueue.setMail_send_date(new Date());
                                        addMailQueue.setMailBody(mailBody);
                                        addMailQueue.setMailTo(toMailID);
                                        addMailQueue.setMailCC(ccMailId);
                                        addMailQueue.setMailSubject(mailSubject);
                                        addMailQueue.setMailTOUserId(userDetail.getId());
                                        addMailQueue.setMailToRole(addEmailTriggerVOData.getReceiverRoleId());
                                        if (flow == 1) {
                                            addMailQueue.setIsActive(0);// Means Reminder will not be shown in
                                            // Notification 0 means Inactive
                                        } else {
                                            addMailQueue.setIsActive(0);// Means Notification will be shown in
                                            // Notification 1 means active
                                        }

                                        ManuScript getManuScript = null;
                                        getManuScript = manuScriptRepo.getById(addEmailTriggerVOData.getManuscriptId());
                                        if (getManuScript != null) {
                                            addMailQueue.setManuscriptId(getManuScript.getManuScriptId());
                                            addMailQueue.setManuscriptVersion(getManuScript.getManuscriptVersion());
                                        }
                                        addMailQueue.setMailFrom(
                                                editorManuScript.getJournal().getJournalMetadata().getJournalEmail());
                                        addMailQueue.setManuscriptStatus(manuscriptStatus);
                                        addMailQueue.setTaskId(taskId);// ## Needs to change for every user##
                                        addMailQueue.setMailToRole(3);// ######THIS IS FOR EDITOR#####
                                        addMailQueue.setModuleName("AutoReminderEditor");
                                        mailQueueRepo.saveAndFlush(addMailQueue);
                                        log.info("Editor- Insert query mail queue table and manuscript_id ...: "
                                                + manuscriptId + "  AutoReminderEditor");

                                        // insert data in reminder_tracker table
                                        ReminderTracker reminderTracker = new ReminderTracker();
                                        reminderTracker.setJournalId(journalId);
                                        reminderTracker.setManuscriptId(manuscriptId);
                                        reminderTracker.setManuscriptStatusId(64);
                                        reminderTracker.setAssignedUser(assignedUser);
                                        reminderTracker.setReminderNo(reminderNo + 1);
                                        reminderTracker.setReminderStatus(1);
                                        reminderTracker.setIsActive(1);
                                        reminderTracker.setCreatedBy("");
                                        reminderTracker.setCreatedDate(new Date());
                                        reminderTracker.setUpdatedBy("");
                                        reminderTracker.setUpdatedDate(new Date());
                                        reminderTrackerRepo.saveAndFlush(reminderTracker);
                                        log.info("Editor-  Insert query reminder_tracker table and manuscript_id ...: "
                                                + manuscriptId + "  AutoReminderEditor");
                                    }
                                }
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "Run sucessfully";
    }


}
