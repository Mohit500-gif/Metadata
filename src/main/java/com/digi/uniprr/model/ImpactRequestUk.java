package com.digi.uniprr.model;

import java.util.List;

public class ImpactRequestUk {

	private String partner;
    private String event;
    private String state;
    private String doi;
    private String fileName;
    private int status;
    private String message;
    private List<UrlInfo> urls;

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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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

    public List<UrlInfo> getUrls() {
        return urls;
    }

    public void setUrls(List<UrlInfo> urls) {
        this.urls = urls;
    }

    
}
