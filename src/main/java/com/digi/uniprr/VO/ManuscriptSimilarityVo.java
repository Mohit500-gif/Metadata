package com.digi.uniprr.VO;

public class ManuscriptSimilarityVo {
	
    private int matchPercent;
    private boolean exactMatch;
    private String matchedTitle;
	public int getMatchPercent() {
		return matchPercent;
	}
	public void setMatchPercent(int matchPercent) {
		this.matchPercent = matchPercent;
	}
	public boolean isExactMatch() {
		return exactMatch;
	}
	public void setExactMatch(boolean exactMatch) {
		this.exactMatch = exactMatch;
	}
	public String getMatchedTitle() {
		return matchedTitle;
	}
	public void setMatchedTitle(String matchedTitle) {
		this.matchedTitle = matchedTitle;
	}
	@Override
	public String toString() {
		return "ManuscriptSimilarityVo [matchPercent=" + matchPercent + ", exactMatch=" + exactMatch + ", matchedTitle="
				+ matchedTitle + "]";
	}
    

}
