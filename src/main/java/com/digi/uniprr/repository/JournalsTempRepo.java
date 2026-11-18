package com.digi.uniprr.repository;

import com.digi.uniprr.VO.AdminJournalVo;
import com.digi.uniprr.model.Journal;
import com.digi.uniprr.model.JournalTemp;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface JournalsTempRepo extends JpaRepository<JournalTemp, Integer> {

	@Query("Select j from JournalTemp j where j.isActive=1")
	public List<JournalTemp> getActivejournal();

	@Query("Select j from JournalTemp j")
	public List<JournalTemp> getAllJournal();
	
}
 