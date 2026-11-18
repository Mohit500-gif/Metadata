package com.digi.uniprr.model;

import java.util.List;

public class ImpactMessageResponse {
	
    private int status;
    private String message;
    private String receiptId;
    private String doi;
    
    public ImpactMessageResponse(int status, String message, String receiptId, String doi) {
        this.status = status;
        this.message = message;
        this.receiptId  = receiptId;
        this.doi = doi;
    }
    
    public ImpactMessageResponse(int status, String message) {
        this.status = status;
        this.message = message;
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

	public String getDoi() {
		return doi;
	}
	public void setDoi(String doi) {
		this.doi = doi;
	}

	public String getReceiptId() {
		return receiptId;
	}

	public void setReceiptId(String receiptId) {
		this.receiptId = receiptId;
	}
    

}
