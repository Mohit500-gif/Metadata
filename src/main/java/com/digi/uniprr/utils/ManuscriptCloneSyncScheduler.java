package com.digi.uniprr.utils;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.digi.uniprr.model.ManuScript;
import com.digi.uniprr.repository.ManuScriptRepo;
import com.digi.uniprr.repository.ManuscriptCloneRepository;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ManuscriptCloneSyncScheduler {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
	@Autowired
    private ManuscriptCloneRepository manuscriptCloneRepo;

    @Autowired
    private ManuScriptRepo manuScriptRepo;


    @Scheduled(fixedDelay = 300000) // every 5 minutes
    @Transactional
    public void syncManuscriptCloneJob() {
        List<ManuscriptClone> pendingClones = manuscriptCloneRepo.findBySyncWithManuscriptFalse();
        if(pendingClones.isEmpty()) {
        	logger.info("not pending manuscript for sync");
        }
        for (ManuscriptClone clone : pendingClones) {
            try {
                ManuScript manuscript = manuScriptRepo.findById(clone.getManuscriptId())
                        .orElseThrow(() -> new IllegalArgumentException("No manuscript found for ID: " + clone.getManuscriptId()));
                
                if(manuscript.getManuscriptCode()==null) {
                	manuscript.setManuscriptCode(clone.getManuscriptCode());
                }
                if(clone.getDoi()!=null) {
                	manuscript.setDoi(clone.getDoi());
                }
                manuscript.setManuscriptVersion(clone.getManuscriptVersion());
                manuscript.setManuscriptStatusId(clone.getStatusCode());
                manuscript.setSubmissionDate(clone.getSubmissionDate());
                manuscript.setDeclarationOfInterest(clone.getDeclarationOfInterest());
                manuscript.setDeclarationOfInterestContent(clone.getDeclarationOfInterestContent());
                manuscript.setCopyrightAgreement(clone.getCopyrightAgreement());
                manuscript.setCopyrightAgreementContent(clone.getCopyrightAgreementContent());

                manuScriptRepo.saveAndFlush(manuscript);

                clone.setSyncWithManuscript(true);
                clone.setStatus("SYNCED");
                manuscriptCloneRepo.save(clone);

                logger.info("✅ Synced ManuscriptClone {} -> Manuscript {}", clone.getCloneId(), clone.getManuscriptId());
            } catch (Exception e) {
                logger.error("❌ Failed syncing ManuscriptClone {}. Error: {}", clone.getCloneId(), e.getMessage(), e);
            }
        }
    }
}

