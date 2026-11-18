package com.digi.uniprr.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.digi.uniprr.model.ManuscriptEditorComment;
@Repository
@Transactional
public interface ManuscriptEditorCommentRepo extends JpaRepository<ManuscriptEditorComment, Integer> {

	@Query("SELECT m FROM ManuscriptEditorComment m WHERE m.manuscriptId=:manuscriptId")
	List<ManuscriptEditorComment> getManuscriptEditorComment(Integer manuscriptId);
	
	@Query("SELECT m FROM ManuscriptEditorComment m WHERE m.manuscriptId=:manuscriptId and (file_type NOT IN ('Send Back To EIC') OR file_type IS NULL)")
	List<ManuscriptEditorComment> getManuscriptEditorCommentAuthor(Integer manuscriptId);

}
