package com.digi.uniprr.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.digi.uniprr.model.ManuScriptFile;
import com.digi.uniprr.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.model.ManuScript;
import com.digi.uniprr.model.ManuscriptTat;
import com.digi.uniprr.model.PlagiarismCheck;
import com.digi.uniprr.repository.ManuScriptRepo;
import com.digi.uniprr.repository.ManuscriptTatRepo;
import com.digi.uniprr.repository.PlagiarismCheckRepo;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableScheduling
@Slf4j
public class Scheduler {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    ManuscriptDashboardArticleService manuscriptDashboardArticleService;
    @Autowired
    ManuScriptFileService manuScriptFileService;
    @Autowired
    private ManuscriptTatRepo manuscriptTatRepo;
    @Autowired
    private ManuScriptRepo manuScriptRepo;
    @Autowired
    private PlagiarismCheckRepo plagiarismCheckRepo;
    @Autowired
    private CheckPlagiarismService checkPlagiarismService;
    @Autowired
    private ReminderService reminderService;
    @Autowired
    private MailQueueService mailQueueService;
    @Autowired
    private AutoWithdrawalSchedulerService autoWithdrawalService;

    @Value("${scheduling.enabled}")
    private boolean schedulingEnabled;

    @Value("${spring.mail.username}")
    private String mailFrom;

    @Value("${support.email}")
    private String mailTo;

    @Value("${supportCC.email}")
    private String mailCC;

    @Scheduled(cron = "0 0 7 * * ?")
    public void runJob() {
        logger.info("Scheduling Enabled : " + schedulingEnabled);
        if (schedulingEnabled) {
            List<ManuscriptTat> manuscriptTat = manuscriptTatRepo.getDataForMail();
            for (ManuscriptTat manuscriptTatData : manuscriptTat) {
                if (manuscriptTatData.getIsEmailSend() == 1) {
                    // notificationMailSend
                } else if (manuscriptTatData.getIsEmailSend() == 2) {
                    // tatExpiredMailSend
                } else if (manuscriptTatData.getIsEmailSend() == 3) {
                    // BufferPeriodEndMailSend
                }
            }
        }
    }

//    @Scheduled(fixedDelay = 300000)
    public void checkPlagiarismStatus() {
        logger.info("Scheduling Enabled : " + schedulingEnabled);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String currentTime = LocalDateTime.now().format(formatter);

        logger.info("checkPlagiarismStatus - " + currentTime);
        if (schedulingEnabled) {
            List<PlagiarismCheck> plagiarismCheck = plagiarismCheckRepo.getInActiveData();
            if (plagiarismCheck != null && !plagiarismCheck.isEmpty()) {
                for (PlagiarismCheck plagiarismCheck2 : plagiarismCheck) {
                    try {
                        checkPlagiarismService.activePlagiarism(plagiarismCheck2.getId());
                    } catch (Exception e) {
                        ManuScript manuscript = manuScriptRepo
                                .getFindByManuScriptDetail(plagiarismCheck2.getManuscriptId());
                        String body = "Palagarism URL generation Failed for Artcle  : " + manuscript.getManuscriptCode()
                                + "\nFailure Reason: " + e.getMessage();
                        String subject = "Palagarism failed for Manuscript " + manuscript.getManuscriptCode();
                        Integer journalId = plagiarismCheck2.getJournalId();
                        Integer manuscriptId = plagiarismCheck2.getManuscriptId();
                        mailQueueService.insertMailQueue(mailFrom, mailTo, mailCC, journalId, manuscriptId, subject,
                                body);
                        logger.info("Exception In plagiarismCheck   ::::" + e);
                    }

                }
            }
        }
    }

    /*
     * URL generate for uploaded file for iThenticate
     */
    @Scheduled(fixedDelay = 60000)
    public void generatePlagiarismURL() {
        logger.info("Scheduling Enabled : " + schedulingEnabled);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String currentTime = LocalDateTime.now().format(formatter);

        logger.info("generatePlagiarismURL - " + currentTime);
        if (schedulingEnabled) {
            List<PlagiarismCheck> listForURLGeneration = checkPlagiarismService.getListForURLGeneration();
            if (listForURLGeneration != null && !listForURLGeneration.isEmpty()) {
                for (PlagiarismCheck plagiarismCheck : listForURLGeneration) {
                    try {
                        IDVO id = new IDVO();
                        id.setIsActive(plagiarismCheck.getFileNubmer());
                        id.setManuscriptId(plagiarismCheck.getManuscriptId());
                        id.setManuscriptVersion(plagiarismCheck.getManuscriptVersion());
                        id.setJournalId(plagiarismCheck.getJournalId());

                        PlagiarismCheck plagiarismCheck2= checkPlagiarismService.checkPlagiarismReport(id);
                        
                        logger.info("plagiarismCheck2 - " + plagiarismCheck2);
                    } catch (Exception e) {
                        logger.info("Exception In URL geneartion for Plagiarism   *****" + e);
                    }

                }
            }
        }
    }
    
//    @Scheduled(fixedDelay = 300000)
//    public void generatePlagiarismURL() {
//        logger.info("Scheduling Enabled : {}", schedulingEnabled);
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        String currentTime = LocalDateTime.now().format(formatter);
//
//        logger.info("generatePlagiarismURL - {}", currentTime);
//
//        if (!schedulingEnabled) return;
//
//        List<PlagiarismCheck> listForURLGeneration = checkPlagiarismService.getListForURLGeneration();
//
//        if (listForURLGeneration == null || listForURLGeneration.isEmpty()) return;
//
//        for (PlagiarismCheck plagiarismCheck : listForURLGeneration) {
//            try {
//                // increment retry count
//                int retryCount = plagiarismCheck.getRetryCount() == null ? 0 : plagiarismCheck.getRetryCount();
//                plagiarismCheck.setRetryCount(retryCount + 1);
//                plagiarismCheckRepo.saveAndFlush(plagiarismCheck);
//
//                // If retry count exceeds threshold, delete & recreate entry
//                if (plagiarismCheck.getRetryCount() > 2) {
//                	logger.warn("Retry count exceeded for ManuscriptId: {}, deleting and recreating entry",
//                            plagiarismCheck.getManuscriptId());
//
//                    IDVO id = new IDVO();
//                    id.setIsActive(plagiarismCheck.getFileNubmer());
//                    id.setManuscriptId(plagiarismCheck.getManuscriptId());
//                    id.setManuscriptVersion(plagiarismCheck.getManuscriptVersion());
//                    id.setJournalId(plagiarismCheck.getJournalId());
//
//                    // delete old entry
//                    plagiarismCheckRepo.deleteByIdSafely(plagiarismCheck.getId());
//
//                    // create fresh entry with uploadFile()
//                    String result = checkPlagiarismService.uploadFile(id);
//                    logger.info("Re-upload result for ManuscriptId {} : {}", plagiarismCheck.getManuscriptId(), result);
//
//                    continue; // skip to next entry
//                }
//
//                // normal plagiarism check
//                IDVO id = new IDVO();
//                id.setIsActive(plagiarismCheck.getFileNubmer());
//                id.setManuscriptId(plagiarismCheck.getManuscriptId());
//                id.setManuscriptVersion(plagiarismCheck.getManuscriptVersion());
//                id.setJournalId(plagiarismCheck.getJournalId());
//
//                PlagiarismCheck plagiarismCheck2 = checkPlagiarismService.checkPlagiarismReport(id);
//                logger.info("plagiarismCheck2 - {}", plagiarismCheck2);
//
//            } catch (Exception e) {
//                logger.error("Exception in plagiarism URL generation for ManuscriptId: {}", 
//                             plagiarismCheck.getManuscriptId(), e);
//            }
//        }
//    }

//    @Scheduled(fixedDelay = 600000)
    public void generatePlagiarismURLIfInProgress() {
        logger.info("Scheduling Enabled : " + schedulingEnabled);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String currentTime = LocalDateTime.now().format(formatter);

        logger.info("generatePlagiarismURLIfInProgress - " + currentTime);
        if (schedulingEnabled) {
            List<PlagiarismCheck> listForURLGeneration = checkPlagiarismService.getListForURLGeneration();
            if (listForURLGeneration != null && !listForURLGeneration.isEmpty()) {
                for (PlagiarismCheck plagiarismCheck : listForURLGeneration) {
                    try {

                        IDVO id = new IDVO();
                        id.setIsActive(plagiarismCheck.getFileNubmer() + 1);
                        id.setManuscriptId(plagiarismCheck.getManuscriptId());
                        id.setManuscriptVersion(plagiarismCheck.getManuscriptVersion());
                        id.setJournalId(plagiarismCheck.getJournalId());
                        checkPlagiarismService.uploadFileForInProgress(id);
                        id.setIsActive(plagiarismCheck.getFileNubmer());
                        checkPlagiarismService.checkPlagiarismReport(id);


                    } catch (Exception e) {
                        logger.info("Exception In URL geneartion for Plagiarism   *****" + e);
                    }

                }
            }
        }
    }

    @Scheduled(cron = "0 1 1 * * *")
    public void sendMailReminderForEditor() {
        logger.info("Scheduling Enabled : " + schedulingEnabled);
        if (schedulingEnabled) {
            reminderService.autoReminderForEditor();
        }
    }

    @Scheduled(cron = "0 1 3 * * *")
    public void sendMailReminderForAuthor() {
        logger.info("Scheduling Enabled : " + schedulingEnabled);
        if (schedulingEnabled) {
            logger.info("Author Reminder Start ************************* start Runing");
            String status = reminderService.autoReminderForAuthorForSchedular();
            logger.info("Author Reminder run succeefully ##########################" + status);
        }
    }
    
    @Scheduled(cron = "0 1 5 * * *")
    public void SendAutoWithdrawReminderForAuthor() {
        logger.info("Scheduling Enabled : " + schedulingEnabled);
        if (schedulingEnabled) {
            logger.info("Auto Withdraw Reminder For Author Reminder Start ************************* start Runing");
            String status = reminderService.autoWithdrawReminderForAuthor();
            logger.info("Auto Withdraw Reminder FOr Author run succeefully ##########################" + status);
        }
    }
    
    @Scheduled(cron = "0 30 3 * * *") // run at 3:30 AM, adjust time as needed
    public void sendMailReminderForAuthorImpact() {
        logger.info("Scheduling Enabled : " + schedulingEnabled);
        if (schedulingEnabled) {
            logger.info("Author Impact Reminder Start *************************");
            String status = reminderService.autoReminderForAuthorForSchedularImpact();
            logger.info("Author Impact Reminder run successfully ##########################" + status);
        }
    }


    @Scheduled(cron = "0 1 23 * * *")
    public void sendMailReminderForReviewer() {
        logger.info("Scheduling Enabled : " + schedulingEnabled);
        if (schedulingEnabled) {
            reminderService.autoReminderForReviewerForSchedular();
        }
    }

    @Scheduled(cron = "0 1 6 * * *")
    public void sendNoresponseNotification() {
        logger.info("Scheduling Enabled : " + schedulingEnabled);
        if (schedulingEnabled) {
            manuscriptDashboardArticleService.getNoResponseManuscriptNotification();
        }
    }

    @Scheduled(cron = "0 1 7 * * *")
    public void sendNoresponseReminder() {
        logger.info("Scheduling Enabled : " + schedulingEnabled);
        if (schedulingEnabled) {
        	logger.info("schedular start for no response reminder");
            reminderService.autoNoresponseReviewerReminderForEditor();
        }
    }



    // Run every Monday at 1 AM
//    @Scheduled(cron = "0 0 1 * * *")
    public void triggerAutoWithdrawal() {
        autoWithdrawalService.processAutoWithdrawalReminders();
    }
}
