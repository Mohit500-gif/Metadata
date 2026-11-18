package com.digi.uniprr.VO;

import java.io.Serializable;
import java.util.List;

public class EditorPerformanceVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<DecisionForEditorVO> articlesForDecision ;
	private List<DecisionForEditorVO> articlesForReview;
	private List<DecisionForEditorVO> articlesForProof;
	private List<DecisionForEditorVO> time;
	
	public List<DecisionForEditorVO> getArticlesForDecision() {
		return articlesForDecision;
	}
	public void setArticlesForDecision(List<DecisionForEditorVO> articlesForDecision) {
		this.articlesForDecision = articlesForDecision;
	}
	public List<DecisionForEditorVO> getArticlesForReview() {
		return articlesForReview;
	}
	public void setArticlesForReview(List<DecisionForEditorVO> articlesForReview) {
		this.articlesForReview = articlesForReview;
	}
	public List<DecisionForEditorVO> getArticlesForProof() {
		return articlesForProof;
	}
	public void setArticlesForProof(List<DecisionForEditorVO> articlesForProof) {
		this.articlesForProof = articlesForProof;
	}
	public List<DecisionForEditorVO> getTime() {
		return time;
	}
	public void setTime(List<DecisionForEditorVO> time) {
		this.time = time;
	}
	@Override
	public String toString() {
		return "EditorPerformanceVo [articlesForDecision=" + articlesForDecision + ", articlesForReview="
				+ articlesForReview + ", articlesForProof=" + articlesForProof + ", time=" + time + "]";
	}

	
}
