package com.digi.uniprr.service;

public interface ManuscriptAuthorCountService {

	void saveOrUpdateAuthorCount(Long manuscriptId, Integer authorCount);
	Integer getAuthorCount(Long manuscriptId);
}
