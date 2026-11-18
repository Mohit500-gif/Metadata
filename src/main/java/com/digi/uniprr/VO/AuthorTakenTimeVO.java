package com.digi.uniprr.VO;

public class AuthorTakenTimeVO {
	
	private Long manuscriptCount;
	private Long totalsum;
	public Long getManuscriptCount() {
		return manuscriptCount;
	}
	public void setManuscriptCount(Long manuscriptCount) {
		this.manuscriptCount = manuscriptCount;
	}
	public Long getTotalsum() {
		return totalsum;
	}
	public void setTotalsum(Long totalsum) {
		this.totalsum = totalsum;
	}
	public AuthorTakenTimeVO(Long manuscriptCount, Long totalsum) {
		super();
		this.manuscriptCount = manuscriptCount;
		this.totalsum = totalsum;
	}
	
	

}
