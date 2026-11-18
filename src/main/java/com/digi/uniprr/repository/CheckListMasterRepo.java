package com.digi.uniprr.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.digi.uniprr.model.CheckListMaster;

@Repository
@Transactional
public interface CheckListMasterRepo extends JpaRepository<CheckListMaster, Integer> {

	@Query("select cm from CheckListMaster cm where cm.journalId=:journalId AND cm.chkCategory=:chkCategory AND cm.isActive=1")
	List<CheckListMaster> getCheckListByisActive(Integer journalId,String chkCategory);
	
	@Query("select cm from CheckListMaster cm where cm.journalId=:journalId AND cm.chkCategory=:chkCategory AND cm.isActive=1")
	List<CheckListMaster> getCheckList(Integer journalId,String chkCategory);
	
	@Modifying
	@Query("UPDATE CheckListMaster SET isActive=0 WHERE chkid=:chkid")
	public void deactivateisActive(Integer chkid);
	
	@Query("select cm from CheckListMaster cm where cm.journalId is null  ")
	List<CheckListMaster> getCheckListForMaster();
	
	@Query("select cm from CheckListMaster cm where cm.journalId is null AND cm.isActive=1 ")
	List<CheckListMaster> getCheckListForMasterForJournal();
	
	@Query("select cm from CheckListMaster cm where cm.journalId=:journalId AND cm.isActive=1 ORDER BY cm.sequence ")
	List<CheckListMaster> getCheckListForMasterAsJournalBased(Integer journalId);
	
	@Modifying
	@Query("UPDATE CheckListMaster SET sequence=:sequence WHERE chkid=:chkid")
	public void sequenceUpdateInCheckList(Integer chkid,Integer sequence);
	
	@Query("SELECT c FROM CheckListMaster c WHERE c.chkKey=:chkKey AND c.journalId=null")
	public CheckListMaster isCheckListExistInMaster(String chkKey);
	
	@Query("SELECT c FROM CheckListMaster c WHERE c.chkKey=:chkKey AND c.journalId=:journalId AND c.isActive=1")
	public CheckListMaster isCheckListExistInJournalLevel(String chkKey,Integer journalId);
}
