package com.digi.uniprr.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "annoucements")
public class Announcements {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private Integer id;

	@Column(name = "role_id")
	private int roleId;

	@Column(name = "journal_id")
	private int journalId;

	@Column(name = "journal_code")
	private String journalCode;

	@Column(name = "annoucement_msg")
	private String annoucementMsg;

	@Column(name = "active")
	private int active;

	@Column(name = "valid_from")
	private String validFrom;

	@Column(name = "valid_to")
	private String validTo;
	
	@Column(name = "created_at")
	private Date created_at;

}
