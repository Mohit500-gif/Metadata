package com.digi.uniprr.VO;

import java.util.List;

public class ManuscriptTypeTitleAbstractVO {

	private Integer manuscriptId;

	private Integer manuscriptVersion;

	private String manuscriptCode;

	private String manuscriptTypeJournal;

	private String title;

	private String shortTitle;

	private String manuscriptAbstract;

	private String runningHead;

	private String manuscriptFigures;

	private String manuscriptColorFigures;

	private String blackAndWhiteFigures;

	private String manuscriptTables;
	
	private String trialRegistrationNo;

	private String manuscriptWords;

	private String manuscriptPages;

	private String manuscriptRegionOfOrigin;
	
	private List<ManuscriptJournalSubjectVo> manuscriptJournalSubject;

	private List<KeywordsVO> journalKeywords;

	private String manuscriptKeywords;

	private Boolean saveAndDraft;

	private Integer journalId;

	private Integer authorId;

	private String trailNumberMandatory;

	public String getTrailNumberMandatory() {
		return trailNumberMandatory;
	}

	public void setTrailNumberMandatory(String trailNumberMandatory) {
		this.trailNumberMandatory = trailNumberMandatory;
	}

	public Integer getManuscriptId() {
		return manuscriptId;
	}

	public void setManuscriptId(Integer manuscriptId) {
		this.manuscriptId = manuscriptId;
	}

	public Integer getManuscriptVersion() {
		return manuscriptVersion;
	}

	public void setManuscriptVersion(Integer manuscriptVersion) {
		this.manuscriptVersion = manuscriptVersion;
	}

	public String getManuscriptCode() {
		return manuscriptCode;
	}

	public void setManuscriptCode(String manuscriptCode) {
		this.manuscriptCode = manuscriptCode;
	}

	public String getManuscriptTypeJournal() {
		return manuscriptTypeJournal;
	}

	public void setManuscriptTypeJournal(String manuscriptTypeJournal) {
		this.manuscriptTypeJournal = manuscriptTypeJournal;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getShortTitle() {
		return shortTitle;
	}

	public void setShortTitle(String shortTitle) {
		this.shortTitle = shortTitle;
	}

	public String getManuscriptAbstract() {
		return manuscriptAbstract;
	}

	public void setManuscriptAbstract(String manuscriptAbstract) {
		this.manuscriptAbstract = manuscriptAbstract;
	}

	public String getRunningHead() {
		return runningHead;
	}

	public void setRunningHead(String runningHead) {
		this.runningHead = runningHead;
	}

	public String getManuscriptFigures() {
		return manuscriptFigures;
	}

	public void setManuscriptFigures(String manuscriptFigures) {
		this.manuscriptFigures = manuscriptFigures;
	}

	public String getManuscriptColorFigures() {
		return manuscriptColorFigures;
	}

	public void setManuscriptColorFigures(String manuscriptColorFigures) {
		this.manuscriptColorFigures = manuscriptColorFigures;
	}

	public String getBlackAndWhiteFigures() {
		return blackAndWhiteFigures;
	}

	public void setBlackAndWhiteFigures(String blackAndWhiteFigures) {
		this.blackAndWhiteFigures = blackAndWhiteFigures;
	}

	public String getManuscriptTables() {
		return manuscriptTables;
	}

	public void setManuscriptTables(String manuscriptTables) {
		this.manuscriptTables = manuscriptTables;
	}

	public String getManuscriptWords() {
		return manuscriptWords;
	}

	public void setManuscriptWords(String manuscriptWords) {
		this.manuscriptWords = manuscriptWords;
	}

	public String getManuscriptPages() {
		return manuscriptPages;
	}

	public void setManuscriptPages(String manuscriptPages) {
		this.manuscriptPages = manuscriptPages;
	}

	public String getManuscriptRegionOfOrigin() {
		return manuscriptRegionOfOrigin;
	}

	public void setManuscriptRegionOfOrigin(String manuscriptRegionOfOrigin) {
		this.manuscriptRegionOfOrigin = manuscriptRegionOfOrigin;
	}

	public List<KeywordsVO> getJournalKeywords() {
		return journalKeywords;
	}

	public void setJournalKeywords(List<KeywordsVO> journalKeywords) {
		this.journalKeywords = journalKeywords;
	}

	public Boolean getSaveAndDraft() {
		return saveAndDraft;
	}

	public void setSaveAndDraft(Boolean saveAndDraft) {
		this.saveAndDraft = saveAndDraft;
	}

	public Integer getJournalId() {
		return journalId;
	}

	public void setJournalId(Integer journalId) {
		this.journalId = journalId;
	}

	public Integer getAuthorId() {
		return authorId;
	}

	public void setAuthorId(Integer authorId) {
		this.authorId = authorId;
	}

	public String getManuscriptKeywords() {
		return manuscriptKeywords;
	}

	public void setManuscriptKeywords(String manuscriptKeywords) {
		this.manuscriptKeywords = manuscriptKeywords;
	}

	public String getTrialRegistrationNo() {
		return trialRegistrationNo;
	}

	public void setTrialRegistrationNo(String trialRegistrationNo) {
		this.trialRegistrationNo = trialRegistrationNo;
	}

	public List<ManuscriptJournalSubjectVo> getManuscriptJournalSubject() {
		return manuscriptJournalSubject;
	}

	public void setManuscriptJournalSubject(List<ManuscriptJournalSubjectVo> manuscriptJournalSubject) {
		this.manuscriptJournalSubject = manuscriptJournalSubject;
	}

}
