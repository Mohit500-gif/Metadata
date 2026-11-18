package com.digi.uniprr.model;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name="workflow")
public class Workflow {

	@Id 
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="workflow_id")
	private Integer workflowId;
	
	@OneToMany(cascade=CascadeType.ALL)
    @JoinColumn(name="workflow_id")
	private Set<WorkflowRoleSeq> workflowRoleSeq;
	
	@Column(name="name")
	private String name;
	
	@Column(name="is_master")
	private Integer isMaster;
	
	@Column(name="created_by")
	private Integer createdBy;
	
	@Column(name="created_at")
	@CreationTimestamp
	private Date createdOn;
	
	@Column(name="updated_by")
	private Integer updatedBy;
	
	@Column(name="updated_at")
	@UpdateTimestamp
	private Date updatedOn;

	@Column(name="is_active")
	private Integer isActive;
	
	public Integer getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(Integer workflowId) {
		this.workflowId = workflowId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public Integer getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMM yyyy")
	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Set<WorkflowRoleSeq> getWorkflowRoleSeq() {
		return workflowRoleSeq;
	}

	public void setWorkflowRoleSeq(Set<WorkflowRoleSeq> workflowRoleSeq) {
		this.workflowRoleSeq = workflowRoleSeq;
	}

	public Integer getIsMaster() {
		return isMaster;
	}

	public void setIsMaster(Integer isMaster) {
		this.isMaster = isMaster;
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

}

