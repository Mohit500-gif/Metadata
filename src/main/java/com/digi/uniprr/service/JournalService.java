package com.digi.uniprr.service;

import java.util.List;

import com.digi.uniprr.VO.AdminJournalVo;
import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.VO.JournalDetailsVo;
import com.digi.uniprr.VO.JournalListVO;
import com.digi.uniprr.VO.JournalPerformanceVo;
import com.digi.uniprr.VO.JournalPoliciesVO;
import com.digi.uniprr.VO.JournalVO;
import com.digi.uniprr.model.Journal;
import com.digi.uniprr.model.JournalChargeDetails;
import com.digi.uniprr.model.JournalMetadata;
import com.digi.uniprr.model.ManuscriptTypeJournal;

public interface JournalService 
{
    public String createJournal(Journal journals);
    
    public List<JournalListVO> getJournalDetails();
    
    public List<AdminJournalVo> getJournalList();
    
    public String updateJournal(Journal journals);
    
    public String deleteJournalById(Integer id);
    
    public boolean isIdExist(Integer id);

	public JournalVO getJournalById(Integer id);
	
	public JournalChargeDetails getJournalChargeDetailsByJournalId(Integer id);
	
	public JournalPerformanceVo getJournalPerfomance(JournalPerformanceVo journalPerfomanceVO);
	
	public Journal getjournalById(Integer journalId);

	public List<JournalDetailsVo> getJournalAdmin(JournalDetailsVo jdetails);
	
	public List<JournalVO> getByJournalCode(IDVO id);
	
	/*******************OPERATIONAL POLICIES***********/
	public JournalPoliciesVO getOperationalPolices(JournalPoliciesVO journalPoliciesVO);
	
	public String setupOperationalPolices(JournalPoliciesVO journalPoliciesVO);
	
	public List<IDVO> getByJournalEic(IDVO id);

	public List<ManuscriptTypeJournal> getManuscriptTypeByJournal(Integer journalId);

	public Journal getJournalByAcronym(String name);

	public IDVO updateManuscriptId(String name);

	public List<JournalListVO> getAllJournalList();

	public String updateJournalActivation(JournalListVO journalListVO, boolean isUnitouchTrue);
	
	public String updateJournalValidateAcro(JournalListVO journalListVO);
	
	public int journalCountSearch(JournalDetailsVo jdetails);

	public int journalCount();

	public String updateJournalReviewerSetting(Journal journal);

	public String updateCoAuthorCount(Journal journal);
	
	JournalMetadata getJournalMetadata(int journalId);

	boolean isJournalExistsByCode(JournalListVO journalListVO);
	
	public String updateJournalCharge(int journalId, String chargeStatus, String chargeOn);

}
