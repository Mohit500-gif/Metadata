package com.digi.uniprr.repository;

import java.util.List;

import javax.persistence.Tuple;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.digi.uniprr.model.ManuscriptComment;

@Repository
public interface ManuscriptCommentRepo extends JpaRepository<ManuscriptComment, Long> {

	@Query("SELECT m FROM ManuscriptComment m where m.manuscriptId=:manuId")
	List<ManuscriptComment> getCommentsByManuscriptId(Integer manuId);

	@Query(value="SELECT m.id,m.manuscript_id,m.author_email,m.task_id,m.task_name,m.comment_data,m.comment_date,m.uniprr_task FROM manuscript_comment m where m.manuscript_id=:manuId", nativeQuery = true)
	List<Tuple> getListOfCommentsByManuscriptId(Integer manuId);
	
}
