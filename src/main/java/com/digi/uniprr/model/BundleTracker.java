package com.digi.uniprr.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="bundle_tracker")
public class BundleTracker {
		
		@Id
		@GeneratedValue(strategy = GenerationType.SEQUENCE)
		@Column(name="id")
		private Integer id;
				
		@Column(name="manuscript_id")
		private Integer manuscriptId;
		
		@Column(name="bundle_status")
		private String bundleStatus;
		
		@Column(name="bundle_error_message")
		private String bundleErrorMessage;
		
		@Column(name="bundle_date")
		private Date bundleDate;
		
		@Column(name="manuscript_version")
		private Integer manuscriptVersion;

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

		public String getBundleStatus() {
			return bundleStatus;
		}

		public void setBundleStatus(String bundleStatus) {
			this.bundleStatus = bundleStatus;
		}

		public String getBundleErrorMessage() {
			return bundleErrorMessage;
		}

		public void setBundleErrorMessage(String bundleErrorMessage) {
			this.bundleErrorMessage = bundleErrorMessage;
		}

		public Date getBundleDate() {
			return bundleDate;
		}

		public void setBundleDate(Date bundleDate) {
			this.bundleDate = bundleDate;
		}
		

		public Integer getManuscriptVersion() {
			return manuscriptVersion;
		}

		public void setManuscriptVersion(Integer manuscriptVersion) {
			this.manuscriptVersion = manuscriptVersion;
		}

		@Override
		public String toString() {
			return "BundleTracker [id=" + id + ", manuscriptId=" + manuscriptId + ", bundleStatus=" + bundleStatus
					+ ", bundleErrorMessage=" + bundleErrorMessage + ", bundleDate=" + bundleDate
					+ ", manuscriptVersion=" + manuscriptVersion + "]";
		}


}
