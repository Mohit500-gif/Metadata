package com.digi.uniprr.VO;

public class ImpactArticleDTO {
	private String aid;
    private String authorReviewUrl;
    private boolean isImpact;
    
    
    
    
	public String getAid() {
		return aid;
	}
	public void setAid(String aid) {
		this.aid = aid;
	}
	public String getAuthorReviewUrl() {
		return authorReviewUrl;
	}
	public void setAuthorReviewUrl(String authorReviewUrl) {
		this.authorReviewUrl = authorReviewUrl;
	}
	public Boolean getIsImpact() {
		return isImpact;
	}
	public void setIsImpact(Boolean isImpact) {
		this.isImpact = isImpact;
	}
	@Override
	public String toString() {
		return "ImpactArticleDTO [aid=" + aid + ", authorReviewUrl=" + authorReviewUrl + ", isImpact=" + isImpact + "]";
	}

    
	
}

