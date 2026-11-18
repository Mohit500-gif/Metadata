package com.digi.uniprr.VO;

public class ReviewersTakenTimeVO {

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
	public ReviewersTakenTimeVO(Long manuscriptCount, Long totalsum) {
		super();
		this.manuscriptCount = manuscriptCount;
		this.totalsum = totalsum;
	}
}
