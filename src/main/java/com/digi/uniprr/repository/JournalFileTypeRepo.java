package com.digi.uniprr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.digi.uniprr.model.JournalFileType;

public interface JournalFileTypeRepo extends JpaRepository<JournalFileType, Integer> {
	
	@Query("select jf from JournalFileType jf where jf.isActive=1  ORDER BY LTRIM(jf.fileType) ASC")
	List<JournalFileType> findAllFileType();

}
