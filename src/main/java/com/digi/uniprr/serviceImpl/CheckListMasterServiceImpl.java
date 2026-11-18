package com.digi.uniprr.serviceImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digi.uniprr.VO.AllCheckListVO;
import com.digi.uniprr.VO.CheckListMasterSimpleVO;
import com.digi.uniprr.VO.CheckListMasterVO;
import com.digi.uniprr.VO.JournalBasedCheckListVO;
import com.digi.uniprr.model.CheckListMaster;
import com.digi.uniprr.repository.CheckListMasterRepo;
import com.digi.uniprr.service.CheckListMasterService;

@Service
@Transactional
public class CheckListMasterServiceImpl implements CheckListMasterService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	CheckListMasterRepo checkListMasterRepo;

	JSONObject obj = new JSONObject();

	@Override
	public String addChecklistDeatils(CheckListMaster checkListMaster) {
		checkListMasterRepo.save(checkListMaster);
		return "Data Added Successfully";
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject saveCheckListMaster(CheckListMasterVO checkListMasterVO) {
		try {
			for (int i = 0; i <= checkListMasterVO.getCheckList().size() - 1; i++) {
				if (checkListMasterVO.getChkid() == null) {
					CheckListMaster checkListMaster = new CheckListMaster();
					checkListMaster.setJournalId(checkListMasterVO.getJournalId());
					checkListMaster.setChkCategory(checkListMasterVO.getChkCategory());
					checkListMaster.setCreatedBy(checkListMasterVO.getCreatedBy());
					checkListMaster.setChkKey(checkListMasterVO.getCheckList().get(i).getChkKey());
					checkListMaster.setChkRule(checkListMasterVO.getChkRule());
					checkListMaster.setIsActive(checkListMasterVO.getCheckList().get(i).getIsActive());
					checkListMasterRepo.save(checkListMaster);
					obj.put("message", "Checklist Save Successfully");
				} else {
					CheckListMaster checkListUpdate = checkListMasterRepo.getOne(checkListMasterVO.getChkid());
					checkListUpdate.setJournalId(checkListMasterVO.getJournalId());
					checkListUpdate.setChkCategory(checkListMasterVO.getChkCategory());
					checkListUpdate.setChkKey(checkListMasterVO.getCheckList().get(i).getChkKey());
					checkListUpdate.setChkRule(checkListMasterVO.getChkRule());
					checkListUpdate.setIsActive(checkListMasterVO.getCheckList().get(i).getIsActive());
					checkListUpdate.setUpdatedBy(checkListMasterVO.getUpdatedBy());
					checkListUpdate.setUpdatedOn(new Date());
					checkListUpdate.setSequence(0);
					checkListMasterRepo.save(checkListUpdate);
					obj.put("message", "Checklist Updated Successfully");
				}
			}
			return obj;
		} catch (Exception e) {
			log.error("Exception : " + e.getMessage());
			return obj;
		}
	}

	@Override
	public AllCheckListVO getCheckListByRole() {
		List<CheckListMaster> checkList = checkListMasterRepo.getCheckListForMaster();
		AllCheckListVO allcheckList=new AllCheckListVO();
		List<CheckListMasterSimpleVO> checkListMaster1 = new ArrayList<>();
		List<CheckListMasterSimpleVO> checkListMaster2 = new ArrayList<>();
		List<CheckListMasterSimpleVO> checkListMaster3 = new ArrayList<>();
		List<CheckListMasterSimpleVO> checkListMaster4 = new ArrayList<>();
		if (checkList != null && !checkList.isEmpty()) {
			checkList.stream().forEach(i -> {
				CheckListMasterSimpleVO vo = new CheckListMasterSimpleVO();
				if (i.getChkCategory().equalsIgnoreCase("MANAGING_EDITOR_CHECKLIST")) {
					vo.setChkId(i.getChkid());
					vo.setJournalId(i.getJournalId());
					vo.setChkCategory(i.getChkCategory());
					vo.setChkKey(i.getChkKey());
					vo.setChkRule(i.getChkRule());
					vo.setCreatedBy(i.getCreatedBy());
					vo.setCreatedOn(i.getCreatedOn());
					vo.setUpdatedBy(i.getUpdatedBy());
					vo.setUpdatedOn(i.getUpdatedOn());
					vo.setIsActive(i.getIsActive());
					checkListMaster1.add(vo);
					allcheckList.setManagingEditorCheckList(checkListMaster1);
				} else if (i.getChkCategory().equalsIgnoreCase("EDITOR_CHECKLIST")) {
					vo.setChkId(i.getChkid());
					vo.setJournalId(i.getJournalId());
					vo.setChkCategory(i.getChkCategory());
					vo.setChkKey(i.getChkKey());
					vo.setChkRule(i.getChkRule());
					vo.setCreatedBy(i.getCreatedBy());
					vo.setCreatedOn(i.getCreatedOn());
					vo.setUpdatedBy(i.getUpdatedBy());
					vo.setUpdatedOn(i.getUpdatedOn());
					vo.setIsActive(i.getIsActive());
					checkListMaster2.add(vo);
					allcheckList.setEditorCheckList(checkListMaster2);
				} else if(i.getChkCategory().equalsIgnoreCase("REVIEW_CHECKLIST")){
					vo.setChkId(i.getChkid());
					vo.setJournalId(i.getJournalId());
					vo.setChkCategory(i.getChkCategory());
					vo.setChkKey(i.getChkKey());
					vo.setChkRule(i.getChkRule());
					vo.setCreatedBy(i.getCreatedBy());
					vo.setCreatedOn(i.getCreatedOn());
					vo.setUpdatedBy(i.getUpdatedBy());
					vo.setUpdatedOn(i.getUpdatedOn());
					vo.setIsActive(i.getIsActive());
					checkListMaster3.add(vo);
					allcheckList.setReviewerCheckList(checkListMaster3);
				}else if(i.getChkCategory().equalsIgnoreCase("AUTHOR_CHECKLIST")) {
					vo.setChkId(i.getChkid());
					vo.setJournalId(i.getJournalId());
					vo.setChkCategory(i.getChkCategory());
					vo.setChkKey(i.getChkKey());
					vo.setChkRule(i.getChkRule());
					vo.setCreatedBy(i.getCreatedBy());
					vo.setCreatedOn(i.getCreatedOn());
					vo.setUpdatedBy(i.getUpdatedBy());
					vo.setUpdatedOn(i.getUpdatedOn());
					vo.setIsActive(i.getIsActive());
					checkListMaster4.add(vo);
					allcheckList.setAuthorCheckList(checkListMaster4);
				}
			});
		}
		return allcheckList;
	}

	@Override
	public JournalBasedCheckListVO getCheckList(Integer journalId) {
		List<CheckListMaster> checkListBasedOnMaster = checkListMasterRepo.getCheckListForMasterForJournal();
		List<CheckListMaster> checkListBasedOnJournal=checkListMasterRepo.getCheckListForMasterAsJournalBased(journalId);
		JournalBasedCheckListVO addJournalBasedCheckListVO=new JournalBasedCheckListVO();
		AllCheckListVO allcheckListBasedOnMaster=new AllCheckListVO();
		List<CheckListMasterSimpleVO> checkListBasedOnMaster1 = new ArrayList<>();
		List<CheckListMasterSimpleVO> checkListBasedOnMaster2 = new ArrayList<>();
		List<CheckListMasterSimpleVO> checkListBasedOnMaster3 = new ArrayList<>();
		List<CheckListMasterSimpleVO> checkListBasedOnMaster4 = new ArrayList<>();
		AllCheckListVO allListBasedOnJournal=new AllCheckListVO();
		List<CheckListMasterSimpleVO> checkListBasedOnJournal1 = new ArrayList<>();
		List<CheckListMasterSimpleVO> checkListBasedOnJournal2 = new ArrayList<>();
		List<CheckListMasterSimpleVO> checkListBasedOnJournal3 = new ArrayList<>();
		List<CheckListMasterSimpleVO> checkListBasedOnJournal4 = new ArrayList<>();
		if(checkListBasedOnMaster!=null && !checkListBasedOnMaster.isEmpty() || checkListBasedOnJournal!=null && !checkListBasedOnJournal.isEmpty()) {
			checkListBasedOnMaster.stream().forEach(i->{
				CheckListMasterSimpleVO vo = new CheckListMasterSimpleVO();
				if (i.getChkCategory().equalsIgnoreCase("MANAGING_EDITOR_CHECKLIST")) {
					vo.setChkId(i.getChkid());
					vo.setJournalId(i.getJournalId());
					vo.setChkCategory(i.getChkCategory());
					vo.setChkKey(i.getChkKey());
					vo.setChkRule(i.getChkRule());
					vo.setCreatedBy(i.getCreatedBy());
					vo.setCreatedOn(i.getCreatedOn());
					vo.setUpdatedBy(i.getUpdatedBy());
					vo.setUpdatedOn(i.getUpdatedOn());
					vo.setIsActive(i.getIsActive());
					checkListBasedOnMaster1.add(vo);
					allcheckListBasedOnMaster.setManagingEditorCheckList(checkListBasedOnMaster1);
				} else if (i.getChkCategory().equalsIgnoreCase("EDITOR_CHECKLIST")) {
					vo.setChkId(i.getChkid());
					vo.setJournalId(i.getJournalId());
					vo.setChkCategory(i.getChkCategory());
					vo.setChkKey(i.getChkKey());
					vo.setChkRule(i.getChkRule());
					vo.setCreatedBy(i.getCreatedBy());
					vo.setCreatedOn(i.getCreatedOn());
					vo.setUpdatedBy(i.getUpdatedBy());
					vo.setUpdatedOn(i.getUpdatedOn());
					vo.setIsActive(i.getIsActive());
					checkListBasedOnMaster2.add(vo);
					allcheckListBasedOnMaster.setEditorCheckList(checkListBasedOnMaster2);
				} else if(i.getChkCategory().equalsIgnoreCase("REVIEW_CHECKLIST")){
					vo.setChkId(i.getChkid());
					vo.setJournalId(i.getJournalId());
					vo.setChkCategory(i.getChkCategory());
					vo.setChkKey(i.getChkKey());
					vo.setChkRule(i.getChkRule());
					vo.setCreatedBy(i.getCreatedBy());
					vo.setCreatedOn(i.getCreatedOn());
					vo.setUpdatedBy(i.getUpdatedBy());
					vo.setUpdatedOn(i.getUpdatedOn());
					vo.setIsActive(i.getIsActive());
					checkListBasedOnMaster3.add(vo);
					allcheckListBasedOnMaster.setReviewerCheckList(checkListBasedOnMaster3);
				}else if(i.getChkCategory().equalsIgnoreCase("AUTHOR_CHECKLIST")) {
					vo.setChkId(i.getChkid());
					vo.setJournalId(i.getJournalId());
					vo.setChkCategory(i.getChkCategory());
					vo.setChkKey(i.getChkKey());
					vo.setChkRule(i.getChkRule());
					vo.setCreatedBy(i.getCreatedBy());
					vo.setCreatedOn(i.getCreatedOn());
					vo.setUpdatedBy(i.getUpdatedBy());
					vo.setUpdatedOn(i.getUpdatedOn());
					vo.setIsActive(i.getIsActive());
					checkListBasedOnMaster4.add(vo);
					allcheckListBasedOnMaster.setAuthorCheckList(checkListBasedOnMaster4);
				}
				addJournalBasedCheckListVO.setMasterLevelCheckList(allcheckListBasedOnMaster);
			});
			checkListBasedOnJournal.stream().forEach(j->{
				CheckListMasterSimpleVO vo = new CheckListMasterSimpleVO();
				if (j.getChkCategory().equalsIgnoreCase("MANAGING_EDITOR_CHECKLIST")) {
					vo.setChkId(j.getChkid());
					vo.setJournalId(j.getJournalId());
					vo.setChkCategory(j.getChkCategory());
					vo.setChkKey(j.getChkKey());
					vo.setChkRule(j.getChkRule());
					vo.setCreatedBy(j.getCreatedBy());
					vo.setCreatedOn(j.getCreatedOn());
					vo.setUpdatedBy(j.getUpdatedBy());
					vo.setUpdatedOn(j.getUpdatedOn());
					vo.setIsActive(j.getIsActive());
					checkListBasedOnJournal1.add(vo);
					allListBasedOnJournal.setManagingEditorCheckList(checkListBasedOnJournal1);
				} else if (j.getChkCategory().equalsIgnoreCase("EDITOR_CHECKLIST")) {
					vo.setChkId(j.getChkid());
					vo.setJournalId(j.getJournalId());
					vo.setChkCategory(j.getChkCategory());
					vo.setChkKey(j.getChkKey());
					vo.setChkRule(j.getChkRule());
					vo.setCreatedBy(j.getCreatedBy());
					vo.setCreatedOn(j.getCreatedOn());
					vo.setUpdatedBy(j.getUpdatedBy());
					vo.setUpdatedOn(j.getUpdatedOn());
					vo.setIsActive(j.getIsActive());
					checkListBasedOnJournal2.add(vo);
					allListBasedOnJournal.setEditorCheckList(checkListBasedOnJournal2);
				} else if(j.getChkCategory().equalsIgnoreCase("REVIEW_CHECKLIST")){
					vo.setChkId(j.getChkid());
					vo.setJournalId(j.getJournalId());
					vo.setChkCategory(j.getChkCategory());
					vo.setChkKey(j.getChkKey());
					vo.setChkRule(j.getChkRule());
					vo.setCreatedBy(j.getCreatedBy());
					vo.setCreatedOn(j.getCreatedOn());
					vo.setUpdatedBy(j.getUpdatedBy());
					vo.setUpdatedOn(j.getUpdatedOn());
					vo.setIsActive(j.getIsActive());
					checkListBasedOnJournal3.add(vo);
					allListBasedOnJournal.setReviewerCheckList(checkListBasedOnJournal3);
				}else if(j.getChkCategory().equalsIgnoreCase("AUTHOR_CHECKLIST")) {
					vo.setChkId(j.getChkid());
					vo.setJournalId(j.getJournalId());
					vo.setChkCategory(j.getChkCategory());
					vo.setChkKey(j.getChkKey());
					vo.setChkRule(j.getChkRule());	
					vo.setCreatedBy(j.getCreatedBy());
					vo.setCreatedOn(j.getCreatedOn());
					vo.setUpdatedBy(j.getUpdatedBy());
					vo.setUpdatedOn(j.getUpdatedOn());
					vo.setIsActive(j.getIsActive());
					checkListBasedOnJournal4.add(vo);
					allListBasedOnJournal.setAuthorCheckList(checkListBasedOnJournal4);
				}
				
				addJournalBasedCheckListVO.setJournalLevelCheckList(allListBasedOnJournal);
			});
		}
		return addJournalBasedCheckListVO;
	}

	@SuppressWarnings("null")
	@Override
	public boolean isExist(Integer journalId, String chkCategory) {
		List<CheckListMaster> isExist = checkListMasterRepo.getCheckList(journalId, chkCategory);
		if (isExist != null || isExist.isEmpty()) {
			return true;
		}
		return false;
	}

	@Override
	public String deletechecklistById(Integer chkid) {
		checkListMasterRepo.deactivateisActive(chkid);
		return "Deleted successfully";
	}

	@Override
	public boolean isExist(Integer chkid) {
		return checkListMasterRepo.existsById(chkid);
	}

	@Override
	public List<CheckListMaster> getCheckListAll(Integer journalId, String chkCategory) {
		return checkListMasterRepo.getCheckListByisActive(journalId, chkCategory);
	}

	@Override
	public String sequenceBasedCheckListSave(List<CheckListMasterVO> checkListMasterVO) {
		int count=1;
		for (CheckListMasterVO i : checkListMasterVO) {
			checkListMasterRepo.sequenceUpdateInCheckList(i.getChkid(),count++);
		}
		return "Sequence Has been Updated Successfully";
	}

	@Override
	public boolean isCheckListExistInMaster(CheckListMasterVO checkListMasterVO) {
		CheckListMaster getCheckListMasterData=checkListMasterRepo.isCheckListExistInMaster(checkListMasterVO.getCheckList().get(0).getChkKey());
		if(getCheckListMasterData!=null)
		    return true;
		return false;
	}

	@Override
	public boolean isCheckListExistInJournalLevel(CheckListMasterVO checkListMasterVO) {
		CheckListMaster getCheckListMasterData=checkListMasterRepo.isCheckListExistInJournalLevel(checkListMasterVO.getCheckList().get(0).getChkKey(), checkListMasterVO.getJournalId());
	    if(getCheckListMasterData!=null)
	    	return true;
		return false;
	}

}
