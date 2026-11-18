package com.digi.uniprr.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "manuscript_editor")
public class ManuscriptEditor implements Serializable{
	
	private static final long serialVersionUID = -8833127469093489350L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id")
	private Integer id;

	@Column(name = "manuscript_id")
	private Integer manuscriptId;

	@Column(name = "manuscript_version")
	private Integer manuscriptVersion;

	@Column(name = "editor_id")
	private Integer editorId;
	
	@OneToOne
	@JoinColumn(name = "editor_id", updatable = false, insertable = false)
	private User user;


	@Column(name = "managing_editor_id")
	private Integer managingEditorId;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMM yyyy")
	@Column(name = "due_date")
	private Date dueDate;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMM yyyy")
	@Column(name = "assigned_date")
	private Date assignedDate;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMM yyyy")
	@Column(name = "editor_decision_date")
	private Date editorDecisionDate;
	
	@Column(name = "journal_id")
	private Integer journalId;

	@OneToOne
	@JoinColumn(name = "manuscript_status", updatable = false, insertable = false)
	private StatusMaster statusMaster;
	
	@Column(name = "manuscript_status")
	private Integer manuscriptStatus;

	@Column(name = "comments_for_editor")
	private String commentsForEditor;
	
	@Column(name = "comments_for_author")
	private String commentsForAuthor;
	
	@Column(name = "comments_for_reviewer")
	private String commentsForReviewer;
	
	@Column(name = "comments_for_managing_editor")
	private String commentsForManagingEditor;
	
	@Column(name = "decision_Letters")
	private String decisionLetter;
	
	@Column(name = "editor_decision")
	private String editorDecision;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMM yyyy")
	@Column(name = "created_on")
	@CreationTimestamp
	private Date createdOn;

	@Column(name = "created_by")
	private Integer createdBy;

	@Column(name = "updated_by")
	private Integer updatedBy;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMM yyyy")
	@Column(name = "updated_on")
	@UpdateTimestamp
	private Date updatedOn;

	@Column(name = "is_active")
	private Integer isActive;
	
	@Column(name="production_date")
	private Date productionDate;
	
	@Column(name="rejected_date")
	private Date rejectedDate;
	
	@Column(name="send_back_eic")
	private Integer sendBackEic;
	

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public Integer getEditorId() {
		return editorId;
	}

	public void setEditorId(Integer editorId) {
		this.editorId = editorId;
	}

	public Integer getManagingEditorId() {
		return managingEditorId;
	}

	public void setManagingEditorId(Integer managingEditorId) {
		this.managingEditorId = managingEditorId;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public Integer getJournalId() {
		return journalId;
	}

	public void setJournalId(Integer journalId) {
		this.journalId = journalId;
	}

	public Integer getManuscriptStatus() {
		return manuscriptStatus;
	}

	public void setManuscriptStatus(Integer manuscriptStatus) {
		this.manuscriptStatus = manuscriptStatus;
	}

	public String getCommentsForEditor() {
		return commentsForEditor;
	}

	public void setCommentsForEditor(String commentsForEditor) {
		this.commentsForEditor = commentsForEditor;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Integer getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}

	public Integer getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(Integer updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Date getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}

	public Integer getIsActive() {
		return isActive;
	}

	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}

	public String getCommentsForAuthor() {
		return commentsForAuthor;
	}

	public void setCommentsForAuthor(String commentsForAuthor) {
		this.commentsForAuthor = commentsForAuthor;
	}

	public String getCommentsForReviewer() {
		return commentsForReviewer;
	}

	public void setCommentsForReviewer(String commentsForReviewer) {
		this.commentsForReviewer = commentsForReviewer;
	}

	public String getCommentsForManagingEditor() {
		return commentsForManagingEditor;
	}

	public void setCommentsForManagingEditor(String commentsForManagingEditor) {
		this.commentsForManagingEditor = commentsForManagingEditor;
	}

	public String getDecisionLetter() {
		return decisionLetter;
	}

	public void setDecisionLetter(String decisionLetter) {
		this.decisionLetter = decisionLetter;
	}

	public Date getAssignedDate() {
		return assignedDate;
	}

	public void setAssignedDate(Date assignedDate) {
		this.assignedDate = assignedDate;
	}

	public Date getEditorDecisionDate() {
		return editorDecisionDate;
	}

	public void setEditorDecisionDate(Date editorDecisionDate) {
		this.editorDecisionDate = editorDecisionDate;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public StatusMaster getStatusMaster() {
		return statusMaster;
	}

	public void setStatusMaster(StatusMaster statusMaster) {
		this.statusMaster = statusMaster;
	}

	public String getEditorDecision() {
		return editorDecision;
	}

	public void setEditorDecision(String editorDecision) {
		this.editorDecision = editorDecision;
	}

	public Date getProductionDate() {
		return productionDate;
	}

	public void setProductionDate(Date productionDate) {
		this.productionDate = productionDate;
	}

	public Date getRejectedDate() {
		return rejectedDate;
	}

	public void setRejectedDate(Date rejectedDate) {
		this.rejectedDate = rejectedDate;
	}

	public Integer getSendBackEic() {
		return sendBackEic;
	}

	public void setSendBackEic(Integer sendBackEic) {
		this.sendBackEic = sendBackEic;
	}
	
}