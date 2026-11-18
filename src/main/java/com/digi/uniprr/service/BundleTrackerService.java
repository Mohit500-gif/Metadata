package com.digi.uniprr.service;

import java.util.List;

import com.digi.uniprr.VO.BundleStatusResponse;
import com.digi.uniprr.model.BundleTracker;

public interface BundleTrackerService {
	
	public BundleTracker getFailedBundelFIleById(Integer manuscriptId);
	
	public List<BundleTracker> failedListArticle(Integer size);

	public String getBundelStatus(Integer manuscriptId);

	BundleStatusResponse getBundleDetails(Integer manuscriptId);

}
