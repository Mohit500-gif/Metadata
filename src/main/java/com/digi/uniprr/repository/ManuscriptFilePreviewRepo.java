package com.digi.uniprr.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.digi.uniprr.model.ManuscriptFilePreview;

public interface ManuscriptFilePreviewRepo extends JpaRepository<ManuscriptFilePreview, Integer>{

	@Query("SELECT mfp FROM ManuscriptFilePreview mfp WHERE mfp.manuscriptId=:manuscriptId AND mfp.manuscriptVersion=:manuscriptVersion")
	public  ManuscriptFilePreview getManuscriptFilePreviewDetails(Integer manuscriptId,Integer manuscriptVersion);
	
}
