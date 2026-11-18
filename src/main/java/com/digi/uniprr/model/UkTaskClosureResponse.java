package com.digi.uniprr.model;

public class UkTaskClosureResponse {

	private int status;
    private String message;
    private String role;
    private String receiptId;
    private String doi;

    public UkTaskClosureResponse(int status, String message, String role, String receiptId, String doi) {
        this.status = status;
        this.message = message;
        this.role = role;
        this.receiptId = receiptId;
        this.doi = doi;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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
}
