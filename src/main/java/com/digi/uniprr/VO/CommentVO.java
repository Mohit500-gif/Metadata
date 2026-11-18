package com.digi.uniprr.VO;

import java.io.Serializable;
import java.util.List;

import com.digi.uniprr.model.JournalChargeDetails;

public class CommentVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ManuscriptListVO manuscriptListVO;
	private List<ManuscriptFileVO> manuscriptFileVO;
	private List<ManuscriptFileVO> previousVerisonManuscriptFileVO;
	private List<ManuscriptReviewerCommentsVO> manuscriptReviewerCommentsVO;
	private List<ManuscriptReviewerCommentsVO> manuscriptReviewerCommentsForAllVersionVO;
	private List<ManuscriptEditorCommentsVO> manuscriptEditorCommentsVO;
	private List<ManuscriptEditorCommentsVO> manuscriptCommentsToAuthorVO;
	private List<ManuscriptEditorCommentsVO> manuscriptEditorCommentsForAllVersionVO;
	private List<ManuscriptEditorCommentsVO> manuscriptEditorCommentsForFinalRecommandation;
	private List<UserVO> coAuthorVO;
	private List<KeywordsVO> keyword;
	private JournalChargeDetails journalChargeDetails;
	private ManuscriptRatingVo manuscriptRatingVo;
	private PaymentHistoryVO userInvoice;
	private String copyrightContent;
	private String submissionMessage;
	private String articleStatus;
	private List<IDVO> editorsNameList;
	private List<IDVO> reviewerNameList;
	private List<IDVO> managingEditorNameList;
	private List<ManuscriptCoAuthorVO> manuscriptCoAuthorVOList;
	private List<ManuscriptJournalSubjectVo> manuscriptJournalSubject;
	private List<EditorCommentsVo> editorCommentVo;
	private List<FileVo> reviewerListFile;
	private List<ManuscriptEditorCommentsVO> sendBackToEICComment;
	private List<ManuscriptEditorCommentsVO> manuscriptEditorCommentsForAllVersionVORFSend;
	private List<EditorCommentsVo> editorCommentAuthorVo;
	private List<String> missingFiles;
	private List<String> corruptFiles;

	public List<ManuscriptEditorCommentsVO> getManuscriptCommentsToAuthorVO() {
		return manuscriptCommentsToAuthorVO;
	}

	public void setManuscriptCommentsToAuthorVO(List<ManuscriptEditorCommentsVO> manuscriptCommentsToAuthorVO) {
		this.manuscriptCommentsToAuthorVO = manuscriptCommentsToAuthorVO;
	}

	public ManuscriptListVO getManuscriptListVO() {
		return manuscriptListVO;
	}

	public void setManuscriptListVO(ManuscriptListVO manuscriptListVO) {
		this.manuscriptListVO = manuscriptListVO;
	}

	public List<ManuscriptFileVO> getManuscriptFileVO() {
		return manuscriptFileVO;
	}

	public void setManuscriptFileVO(List<ManuscriptFileVO> manuscriptFileVO) {
		this.manuscriptFileVO = manuscriptFileVO;
	}

	public List<ManuscriptReviewerCommentsVO> getManuscriptReviewerCommentsVO() {
		return manuscriptReviewerCommentsVO;
	}

	public void setManuscriptReviewerCommentsVO(List<ManuscriptReviewerCommentsVO> manuscriptReviewerCommentsVO) {
		this.manuscriptReviewerCommentsVO = manuscriptReviewerCommentsVO;
	}

	public List<ManuscriptEditorCommentsVO> getManuscriptEditorCommentsVO() {
		return manuscriptEditorCommentsVO;
	}

	public void setManuscriptEditorCommentsVO(List<ManuscriptEditorCommentsVO> manuscriptEditorCommentsVO) {
		this.manuscriptEditorCommentsVO = manuscriptEditorCommentsVO;
	}

	public List<UserVO> getCoAuthorVO() {
		return coAuthorVO;
	}

	public void setCoAuthorVO(List<UserVO> coAuthorVO) {
		this.coAuthorVO = coAuthorVO;
	}

	public List<KeywordsVO> getKeyword() {
		return keyword;
	}

	public void setKeyword(List<KeywordsVO> keyword) {
		this.keyword = keyword;
	}

	public JournalChargeDetails getJournalChargeDetails() {
		return journalChargeDetails;
	}

	public void setJournalChargeDetails(JournalChargeDetails journalChargeDetails) {
		this.journalChargeDetails = journalChargeDetails;
	}

	public PaymentHistoryVO getUserInvoice() {
		return userInvoice;
	}

	public void setUserInvoice(PaymentHistoryVO userInvoice) {
		this.userInvoice = userInvoice;
	}

	public ManuscriptRatingVo getManuscriptRatingVo() {
		return manuscriptRatingVo;
	}

	public void setManuscriptRatingVo(ManuscriptRatingVo manuscriptRatingVo) {
		this.manuscriptRatingVo = manuscriptRatingVo;
	}

	public String getCopyrightContent() {
		return copyrightContent;
	}

	public void setCopyrightContent(String copyrightContent) {
		this.copyrightContent = copyrightContent;
	}

	

	public List<ManuscriptFileVO> getPreviousVerisonManuscriptFileVO() {
		return previousVerisonManuscriptFileVO;
	}

	public void setPreviousVerisonManuscriptFileVO(List<ManuscriptFileVO> previousVerisonManuscriptFileVO) {
		this.previousVerisonManuscriptFileVO = previousVerisonManuscriptFileVO;
	}

	public List<IDVO> getEditorsNameList() {
		return editorsNameList;
	}

	public void setEditorsNameList(List<IDVO> editorsNameList) {
		this.editorsNameList = editorsNameList;
	}

	public List<IDVO> getReviewerNameList() {
		return reviewerNameList;
	}

	public void setReviewerNameList(List<IDVO> reviewerNameList) {
		this.reviewerNameList = reviewerNameList;
	}

	public List<ManuscriptCoAuthorVO> getManuscriptCoAuthorVOList() {
		return manuscriptCoAuthorVOList;
	}

	public void setManuscriptCoAuthorVOList(List<ManuscriptCoAuthorVO> manuscriptCoAuthorVOList) {
		this.manuscriptCoAuthorVOList = manuscriptCoAuthorVOList;
	}

	public String getSubmissionMessage() {
		return submissionMessage;
	}

	public void setSubmissionMessage(String submissionMessage) {
		this.submissionMessage = submissionMessage;
	}

	public List<ManuscriptReviewerCommentsVO> getManuscriptReviewerCommentsForAllVersionVO() {
		return manuscriptReviewerCommentsForAllVersionVO;
	}

	public void setManuscriptReviewerCommentsForAllVersionVO(
			List<ManuscriptReviewerCommentsVO> manuscriptReviewerCommentsForAllVersionVO) {
		this.manuscriptReviewerCommentsForAllVersionVO = manuscriptReviewerCommentsForAllVersionVO;
	}

	public List<ManuscriptEditorCommentsVO> getManuscriptEditorCommentsForAllVersionVO() {
		return manuscriptEditorCommentsForAllVersionVO;
	}

	public void setManuscriptEditorCommentsForAllVersionVO(
			List<ManuscriptEditorCommentsVO> manuscriptEditorCommentsForAllVersionVO) {
		this.manuscriptEditorCommentsForAllVersionVO = manuscriptEditorCommentsForAllVersionVO;
	}

	public List<IDVO> getManagingEditorNameList() {
		return managingEditorNameList;
	}

	public void setManagingEditorNameList(List<IDVO> managingEditorNameList) {
		this.managingEditorNameList = managingEditorNameList;
	}

	public List<ManuscriptJournalSubjectVo> getManuscriptJournalSubject() {
		return manuscriptJournalSubject;
	}

	public void setManuscriptJournalSubject(List<ManuscriptJournalSubjectVo> manuscriptJournalSubject) {
		this.manuscriptJournalSubject = manuscriptJournalSubject;
	}

	public List<EditorCommentsVo> getEditorCommentVo() {
		return editorCommentVo;
	}

	public void setEditorCommentVo(List<EditorCommentsVo> editorCommentVo) {
		this.editorCommentVo = editorCommentVo;
	}

	public String getArticleStatus() {
		return articleStatus;
	}

	public void setArticleStatus(String articleStatus) {
		this.articleStatus = articleStatus;
	}

	public List<FileVo> getReviewerListFile() {
		return reviewerListFile;
	}

	public void setReviewerListFile(List<FileVo> reviewerListFile) {
		this.reviewerListFile = reviewerListFile;
	}

	

	public List<ManuscriptEditorCommentsVO> getManuscriptEditorCommentsForFinalRecommandation() {
		return manuscriptEditorCommentsForFinalRecommandation;
	}

	public void setManuscriptEditorCommentsForFinalRecommandation(
			List<ManuscriptEditorCommentsVO> manuscriptEditorCommentsForFinalRecommandation) {
		this.manuscriptEditorCommentsForFinalRecommandation = manuscriptEditorCommentsForFinalRecommandation;
	}
	
	public List<ManuscriptEditorCommentsVO> getSendBackToEICComment() {
		return sendBackToEICComment;
	}

	public void setSendBackToEICComment(List<ManuscriptEditorCommentsVO> sendBackToEICComment) {
		this.sendBackToEICComment = sendBackToEICComment;
	}

	public List<ManuscriptEditorCommentsVO> getManuscriptEditorCommentsForAllVersionVORFSend() {
		return manuscriptEditorCommentsForAllVersionVORFSend;
	}

	public void setManuscriptEditorCommentsForAllVersionVORFSend(
			List<ManuscriptEditorCommentsVO> manuscriptEditorCommentsForAllVersionVORFSend) {
		this.manuscriptEditorCommentsForAllVersionVORFSend = manuscriptEditorCommentsForAllVersionVORFSend;
	}
	

	public List<EditorCommentsVo> getEditorCommentAuthorVo() {
		return editorCommentAuthorVo;
	}

	public void setEditorCommentAuthorVo(List<EditorCommentsVo> editorCommentAuthorVo) {
		this.editorCommentAuthorVo = editorCommentAuthorVo;
	}
	

	public List<String> getMissingFiles() {
		return missingFiles;
	}

	public void setMissingFiles(List<String> missingFiles) {
		this.missingFiles = missingFiles;
	}

	public List<String> getCorruptFiles() {
		return corruptFiles;
	}

	public void setCorruptFiles(List<String> corruptFiles) {
		this.corruptFiles = corruptFiles;
	}

	@Override
	public String toString() {
		return "CommentVO [manuscriptListVO=" + manuscriptListVO + ", manuscriptFileVO=" + manuscriptFileVO
				+ ", previousVerisonManuscriptFileVO=" + previousVerisonManuscriptFileVO
				+ ", manuscriptReviewerCommentsVO=" + manuscriptReviewerCommentsVO
				+ ", manuscriptReviewerCommentsForAllVersionVO=" + manuscriptReviewerCommentsForAllVersionVO
				+ ", manuscriptEditorCommentsVO=" + manuscriptEditorCommentsVO
				+ ", manuscriptEditorCommentsForAllVersionVO=" + manuscriptEditorCommentsForAllVersionVO
				+ ", manuscriptEditorCommentsForFinalRecommandation=" + manuscriptEditorCommentsForFinalRecommandation
				+ ", coAuthorVO=" + coAuthorVO + ", keyword=" + keyword + ", journalChargeDetails="
				+ journalChargeDetails + ", manuscriptRatingVo=" + manuscriptRatingVo + ", userInvoice=" + userInvoice
				+ ", copyrightContent=" + copyrightContent + ", submissionMessage=" + submissionMessage
				+ ", articleStatus=" + articleStatus + ", editorsNameList=" + editorsNameList + ", reviewerNameList="
				+ reviewerNameList + ", managingEditorNameList=" + managingEditorNameList
				+ ", manuscriptCoAuthorVOList=" + manuscriptCoAuthorVOList + ", manuscriptJournalSubject="
				+ manuscriptJournalSubject + ", editorCommentVo=" + editorCommentVo + ", reviewerListFile="
				+ reviewerListFile + ", sendBackToEICComment=" + sendBackToEICComment + "]";
	}
	
}
