package com.digi.uniprr.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.digi.uniprr.model.BundleTracker;


@Repository
public interface BundleTrackerRepo extends JpaRepository<BundleTracker, Integer> {
	
	@Query(value="SELECT * FROM bundle_tracker r WHERE r.manuscript_id=:manuscriptId", nativeQuery = true)
	BundleTracker getFailedManuscriptById(Integer manuscriptId);
	
	@Query(value="SELECT * FROM bundle_tracker r WHERE r.bundle_status='Failed' ORDER BY id LIMIT :size", nativeQuery = true)
	List<BundleTracker> failedListArticle(Integer size);
	
	@Transactional
	@Modifying(clearAutomatically=true, flushAutomatically=true)
	@Query("UPDATE BundleTracker f SET f.bundleStatus=:status WHERE f.manuscriptId=:manuscriptId")
	void updateFailedbudlefileStatus(String status, int manuscriptId);
	
	@Query(value="SELECT * FROM bundle_tracker r WHERE r.bundle_status != 'Success' AND manuscript_id=:manuscriptId", nativeQuery = true)
	List<BundleTracker> manuscriptFailedStatus(Integer manuscriptId);
	
}
