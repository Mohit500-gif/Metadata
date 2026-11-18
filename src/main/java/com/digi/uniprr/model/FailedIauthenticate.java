package com.digi.uniprr.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name="failed_iauthenticate")
public class FailedIauthenticate {
		
		@Id
		@Column(name="id")
		@GeneratedValue(strategy = GenerationType.SEQUENCE)
		private Integer id;
		
		@Column(name="journal_id")
		private Integer journalId;
		
		@Column(name="manuscript_id")
		private Integer manuscriptId;

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public Integer getJournalId() {
			return journalId;
		}

		public void setJournalId(Integer journalId) {
			this.journalId = journalId;
		}

		public Integer getManuscriptId() {
			return manuscriptId;
		}

		public void setManuscriptId(Integer manuscriptId) {
			this.manuscriptId = manuscriptId;
		}



}
