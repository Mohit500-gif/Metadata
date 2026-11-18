package com.digi.uniprr.service;

import com.digi.uniprr.VO.JournalsConfigurationVO;
import com.digi.uniprr.model.JournalManuscriptTypeCharge;

public interface ManuscriptTypeChargeService {

	void saveJournalManuscriptTypeCharge(JournalsConfigurationVO journalsConfigurationVO);
	
	JournalManuscriptTypeCharge getJournalManuscriptTypeCharge(JournalsConfigurationVO journalsConfigurationVO);
	
	void updateJournalManuscriptTypeCharge(JournalsConfigurationVO journalsConfigurationVO);
	
	void deleteJournalManuscriptTypeCharge(JournalsConfigurationVO journalsConfigurationVO);
}
