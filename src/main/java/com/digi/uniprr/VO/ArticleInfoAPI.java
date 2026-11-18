package com.digi.uniprr.VO;

import java.io.Serializable;
import java.util.List;

public class ArticleInfoAPI implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<AuthorArticleDetails> authors;
	private ArticleDetail article;
	private String journalAbbr;
	public List<AuthorArticleDetails> getAuthors() {
		return authors;
	}
	public void setAuthors(List<AuthorArticleDetails> authors) {
		this.authors = authors;
	}
	public ArticleDetail getArticle() {
		return article;
	}
	public void setArticle(ArticleDetail article) {
		this.article = article;
	}
	public String getJournalAbbr() {
		return journalAbbr;
	}
	public void setJournalAbbr(String journalAbbr) {
		this.journalAbbr = journalAbbr;
	}
	@Override
	public String toString() {
		return "ArticleInfoAPI [authors=" + authors + ", article=" + article + ", journalAbbr=" + journalAbbr + "]";
	}
	
}
