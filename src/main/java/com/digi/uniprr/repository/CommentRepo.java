package com.digi.uniprr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.digi.uniprr.model.Comment;

public interface CommentRepo extends JpaRepository<Comment, Integer>{

	@Query("SELECT c FROM Comment c where c.journalId=:journalId AND c.type=:fileType AND c.isActive=1")
	List<Comment> getDataByJournal(Integer journalId, String fileType);

}
