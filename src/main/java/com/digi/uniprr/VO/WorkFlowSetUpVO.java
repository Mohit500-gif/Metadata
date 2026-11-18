package com.digi.uniprr.VO;

import java.util.List;

public class WorkFlowSetUpVO {

	private Integer workFlowId;
	private String workFlowName;
	private Integer createdBy;
	private Integer journalId;
	private String journalCode;
	private String workflowName;
	
	private List<WorkFlowTaskVO> workFlowTaskVO;

	public Integer getWorkFlowId() {
		return workFlowId;
	}

	public void setWorkFlowId(Integer workFlowId) {
		this.workFlowId = workFlowId;
	}

	public String getWorkFlowName() {
		return workFlowName;
	}

	public void setWorkFlowName(String workFlowName) {
		this.workFlowName = workFlowName;
	}

	public Integer getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}

	public List<WorkFlowTaskVO> getWorkFlowTaskVO() {
		return workFlowTaskVO;
	}

	public void setWorkFlowTaskVO(List<WorkFlowTaskVO> workFlowTaskVO) {
		this.workFlowTaskVO = workFlowTaskVO;
	}

	public Integer getJournalId() {
		return journalId;
	}

	public void setJournalId(Integer journalId) {
		this.journalId = journalId;
	}

	public String getJournalCode() {
		return journalCode;
	}

	public void setJournalCode(String journalCode) {
		this.journalCode = journalCode;
	}

	public String getWorkflowName() {
		return workflowName;
	}

	public void setWorkflowName(String workflowName) {
		this.workflowName = workflowName;
	}
}
