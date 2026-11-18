package com.digi.uniprr.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "impact_message_logs_uk")
public class ImpactMessageLogsUK {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String partner;
    private String event;
    private String doi;
    private String author;
    private String editor;
    private int status;
    private String message;
    private String receiptId;
    private String urls;  // Storing as JSON-like string
    private String fileName;

    public ImpactMessageLogsUK(String partner, String event, String doi, String author, String editor,
                             int status, String message, String receiptId, String urls, String fileName) {
        this.partner = partner;
        this.event = event;
        this.doi = doi;
        this.author = author;
        this.editor = editor;
        this.status = status;
        this.message = message;
        this.receiptId = receiptId;
        this.urls = urls;
        this.fileName = fileName;
    }

    public ImpactMessageLogsUK() {}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPartner() {
		return partner;
	}

	public void setPartner(String partner) {
		this.partner = partner;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getDoi() {
		return doi;
	}

	public void setDoi(String doi) {
		this.doi = doi;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getEditor() {
		return editor;
	}

	public void setEditor(String editor) {
		this.editor = editor;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getReceiptId() {
		return receiptId;
	}

	public void setReceiptId(String receiptId) {
		this.receiptId = receiptId;
	}

	public String getUrls() {
		return urls;
	}

	public void setUrls(String urls) {
		this.urls = urls;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}



    // Getters and Setters
}

