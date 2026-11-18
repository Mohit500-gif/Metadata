package com.digi.uniprr.service;

import java.util.List;

import com.digi.uniprr.model.JournalPolicies;
import com.digi.uniprr.model.ManuScript;

public interface ReminderService {
	public JournalPolicies getReminderPolicies(Integer journalId);
	
	public List<ManuScript> getAuthorReminderArticles();
	
	public List<ManuScript> getEditorReminderArticles();
	
	public List<ManuScript> getEICReminderArticles();
	
	public List<ManuScript> getReviewerReminderArticles();
	
	public String autoReminderForEditor();
	
	public String autoReminderForAuthor();
	public String autoReminderForReviewer();
	public String autoNoresponseReviewerReminderForEditor() ;
	public String autoReminderForReviewerForSchedular();
	public String autoReminderForAuthorForSchedular();
	public String autoReminderForAuthorForSchedularImpact();
	public String autoWithdrawReminderForAuthor();
	
//	public void dataCorrectionForEditorReminder();

}
