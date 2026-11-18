package com.digi.uniprr.repository;

import com.digi.uniprr.model.LetterToEditor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface LetterToEditorRepo extends JpaRepository<LetterToEditor, Integer>{

    @Query(value = "SELECT *  FROM letter_to_editor WHERE journal_id=:journalId and manuscript_id =:manuscriptId and user_id =:userId and user_role =:userRole", nativeQuery = true)
    LetterToEditor getLetterToEditorCount(Integer journalId, Integer userId, Integer userRole, Integer manuscriptId);

    @Query("SELECT c FROM LetterToEditor c WHERE c.journalId=:journalId AND c.userRole=:userRole AND c.userId=:userId and (c.action != 'reject' or c.action is null)")
    List<LetterToEditor> getAuthorResponseList(Integer journalId, Integer userRole, Integer userId);

    @Query("SELECT c FROM LetterToEditor c WHERE c.journalId=:journalId AND c.manuscriptId=:manuscriptId")
    List<LetterToEditor> getAuthorResponse(Integer manuscriptId, Integer journalId);

    @Modifying
    @Query("UPDATE LetterToEditor c SET c.approvedDate=:approvedOn, c.action =:action WHERE c.id=:id and c.manuscriptId=:manuscriptId and c.userRole=:userRole AND c.userId=:userId")
    void approveInvitationStatus(Integer userId, Date approvedOn, Integer manuscriptId, Integer userRole, String action, Integer id);

    @Modifying
    @Query("UPDATE LetterToEditor c SET c.rejectDate=:rejectOn, c.action=:action WHERE c.id=:id and c.manuscriptId=:manuscriptId and c.userRole=:userRole AND c.userId=:userId")
    void rejectInvitationStatus(Integer userId, Date rejectOn, Integer manuscriptId, Integer userRole,String action,Integer id);

    @Modifying
    @Query("UPDATE LetterToEditor c SET c.submittedDate=:submittedOn, c.action=:action WHERE c.id=:id and c.manuscriptId=:manuscriptId and c.userRole=:userRole AND c.userId=:userId")
    void submitInvitationStatus(Integer userId, Date submittedOn, Integer manuscriptId, Integer userRole,String action,Integer id);

    @Modifying
    @Query("DELETE from LetterToEditor c WHERE c.journalId=:journalId AND c.manuscriptId=:manuscriptId and c.userRole=:userRole AND c.userId=:userId and c.id=:id")
    Integer deleteInvitation(Integer journalId, Integer userId, Integer userRole, Integer manuscriptId, Integer id);

    @Modifying
    @Query("DELETE from LetterToEditor c WHERE c.journalId=:journalId AND c.manuscriptId=:manuscriptId")
    void deleteInvitationResponse(Integer manuscriptId, Integer journalId);

    @Modifying
    @Query("UPDATE LetterToEditor c set c.action= null WHERE c.id=:id")
    void updateLTEInvitation(Integer id);
}
