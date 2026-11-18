package com.digi.uniprr.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.digi.uniprr.model.Announcements;

@Repository
@Transactional
public interface AnnouncementRepo extends JpaRepository<Announcements, Integer> {

	@Query("SELECT j from Announcements j WHERE j.active=1")
	public List<Announcements> getActiveAnnouncements();
	
	@Transactional
	@Modifying
	@Query("update Announcements a set a.active = 0 WHERE a.id = :id")
	void inactivateAnnouncement(int id);
	
	@Transactional
	@Modifying
	@Query("delete from Announcements b where b.id=:id")
	void deleteByAnnouncementId(int id);

	@Query("SELECT j from Announcements j WHERE j.journalId=:journalId and (j.roleId=:roleId or 0=:roleId)")
	public List<Announcements> findByJournalAndRole(int journalId, int roleId);
}
