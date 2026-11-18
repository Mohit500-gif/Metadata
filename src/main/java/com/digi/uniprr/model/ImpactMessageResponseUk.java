package com.digi.uniprr.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImpactMessageResponseUk {

	private int status;
    private String message;
    private String receiptId;
    private String doi;
    private List<ImpactUrl> urls;
    private String fileName;
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
	public String getDoi() {
		return doi;
	}
	public void setDoi(String doi) {
		this.doi = doi;
	}
	public List<ImpactUrl> getUrls() {
		return urls;
	}
	public void setUrls(List<ImpactUrl> urls) {
		this.urls = urls;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public ImpactMessageResponseUk(int status, String message, String receiptId, String doi, List<ImpactUrl> urls,
			String fileName) {

		this.status = status;
		this.message = message;
		this.receiptId = receiptId;
		this.doi = doi;
		this.urls = urls;
		this.fileName = fileName;
	}
	public ImpactMessageResponseUk(int status, String message, String receiptId, String doi) {
		super();
		this.status = status;
		this.message = message;
		this.receiptId = receiptId;
		this.doi = doi;
	}
    
	
}
