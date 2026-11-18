package com.digi.uniprr.repository;

import java.util.Date;
import java.util.List;

import com.digi.uniprr.VO.UserVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.digi.uniprr.model.Commentary;

public interface CommentaryRepo extends JpaRepository<Commentary, Integer>{
	
	@Query("SELECT c FROM Commentary c WHERE c.journalId=:journalId AND c.editorRole=:editorRole  AND c.userRole=:userRole")
	List<Commentary> getCommentaryForEditorForAuthor(Integer journalId, Integer editorRole,Integer userRole);
	
	@Query("SELECT c FROM Commentary c WHERE c.journalId=:journalId AND c.userRole=:userRole AND c.userId=:userId")
	List<Commentary> getCommentaryAuthor(Integer journalId, Integer userRole,Integer userId);

	@Query("SELECT c FROM Commentary c WHERE c.journalId=:journalId AND c.userRole=:userRole AND c.userId=:userId and (c.action != 'reject' or c.action is null) group by c.manuscriptId")
	List<Commentary> getCommentaryList(Integer journalId, Integer userRole,Integer userId);

	@Query("SELECT c FROM Commentary c WHERE c.journalId=:journalId AND c.manuscriptId=:manuscriptId GROUP BY c.userId")
    List<Commentary> getCommentaryList(Integer manuscriptId, Integer journalId);

	@Modifying
	@Query("DELETE from Commentary c WHERE c.journalId=:journalId AND c.manuscriptId=:manuscriptId and c.userRole=:userRole AND c.userId=:userId and c.id=:id")
	Integer deleteInvitation(Integer journalId, Integer userId, Integer userRole, Integer manuscriptId,Integer id);

	@Modifying
	@Query("UPDATE Commentary c SET c.approvedDate=:approvedOn, c.action =:action WHERE c.id=:id and c.manuscriptId=:manuscriptId and c.userRole=:userRole AND c.userId=:userId")
	void approveInvitationStatus(Integer userId, Date approvedOn, Integer manuscriptId, Integer userRole,String action,Integer id);

	@Modifying
	@Query("UPDATE Commentary c SET c.rejectDate=:rejectOn, c.action=:action WHERE c.id=:id and c.manuscriptId=:manuscriptId and c.userRole=:userRole AND c.userId=:userId")
	void rejectInvitationStatus(Integer userId, Date rejectOn, Integer manuscriptId, Integer userRole,String action,Integer id);

	@Modifying
	@Query("UPDATE Commentary c SET c.submittedDate=:submittedOn, c.action=:action WHERE c.id=:id and c.manuscriptId=:manuscriptId and c.userRole=:userRole AND c.userId=:userId")
	void submitInvitationStatus(Integer userId, Date submittedOn, Integer manuscriptId, Integer userRole,String action,Integer id);

	@Query(value = "SELECT count(*)  FROM commentary WHERE journal_id=:journalId and manuscript_id =:manuscriptId and user_id =:userId and user_role =:userRole and action != 'reject'", nativeQuery = true)
    Integer getCommentaryCount(Integer journalId, Integer userId, Integer userRole, Integer manuscriptId);
}
