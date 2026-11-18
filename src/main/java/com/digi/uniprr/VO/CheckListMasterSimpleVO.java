package com.digi.uniprr.VO;

import java.io.Serializable;
import java.util.Date;

public class CheckListMasterSimpleVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		
	    private Integer chkId;
		private Integer journalId;
		private String chkCategory;
		private String chkKey;
		private String chkRule;
		private Integer createdBy;
		private Date createdOn;
		private Integer updatedBy;
		private Date updatedOn;
		private Integer isActive;
		
		public Integer getChkId() {
			return chkId;
		}
		public void setChkId(Integer chkId) {
			this.chkId = chkId;
		}
		public Integer getJournalId() {
			return journalId;
		}
		public void setJournalId(Integer journalId) {
			this.journalId = journalId;
		}
		public String getChkCategory() {
			return chkCategory;
		}
		public void setChkCategory(String chkCategory) {
			this.chkCategory = chkCategory;
		}
		public String getChkKey() {
			return chkKey;
		}
		public void setChkKey(String chkKey) {
			this.chkKey = chkKey;
		}
		public String getChkRule() {
			return chkRule;
		}
		public void setChkRule(String chkRule) {
			this.chkRule = chkRule;
		}
		public Integer getCreatedBy() {
			return createdBy;
		}
		public void setCreatedBy(Integer createdBy) {
			this.createdBy = createdBy;
		}
		public Date getCreatedOn() {
			return createdOn;
		}
		public void setCreatedOn(Date createdOn) {
			this.createdOn = createdOn;
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
		@Override
		public String toString() {
			return "CheckListMasterSimpleVO [chkId=" + chkId + ", journalId=" + journalId + ", chkCategory="
					+ chkCategory + ", chkKey=" + chkKey + ", chkRule=" + chkRule + ", createdBy=" + createdBy
					+ ", createdOn=" + createdOn + ", updatedBy=" + updatedBy + ", updatedOn=" + updatedOn
					+ ", isActive=" + isActive + "]";
		}

}
