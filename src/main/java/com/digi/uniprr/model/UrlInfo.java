package com.digi.uniprr.model;

public class UrlInfo {
	
	private String taskId;
    private String role;
    private String link;

    public UrlInfo(String taskId, String role, String link) {
        this.taskId = taskId;
        this.role = role;
        this.link = link;
    }

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
    
    

}
