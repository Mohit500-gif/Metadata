package com.digi.uniprr.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.digi.uniprr.model.ManuScriptCoAuthor;
@Repository
@Transactional
public interface ManuScriptCoAuthorRepo extends JpaRepository<ManuScriptCoAuthor, Integer>{
	
	@Query(value="SELECT mca FROM ManuScriptCoAuthor mca JOIN ManuScript m ON (mca.coAuthorManuscriptId = m.manuScriptId AND mca.coAuthorId = m.authorId) JOIN User u on mca.coAuthorId= u.id WHERE m.manuScriptId=:manuId")
	List<ManuScriptCoAuthor> getPrimaryAuthorsByManuscriptId(Integer manuId);
	
	@Query("SELECT mca FROM ManuScriptCoAuthor mca where mca.coAuthorManuscriptId=:manuscriptId AND mca.coAuthorStatus=1")
	public List<ManuScriptCoAuthor> getCoAuthorsByManuscriptId(Integer manuscriptId);
	
	@Query("SELECT mk FROM ManuScriptCoAuthor mk where mk.coAuthorManuscriptId=:manuscriptId AND mk.coAuthorStatus=1")
	public List<ManuScriptCoAuthor> getCoAuthorListFromManuscriptId(Integer manuscriptId);
	
	@Modifying
	@Query("UPDATE ManuScriptCoAuthor SET coAuthorStatus=0 WHERE id=:id")
	public void deactivateCoAuthor(Integer id);
	
	@Query("SELECT m FROM ManuScriptCoAuthor m WHERE m.coAuthorManuscriptId=:manuscriptId AND m.isEmailSent=:isEmailSent")
	public List<ManuScriptCoAuthor> sendEmailToCoAuthorOnManuscript(Integer manuscriptId,Integer isEmailSent);
	
	@Query("SELECT m FROM ManuScriptCoAuthor m WHERE m.coAuthorManuscriptId=:manuscriptId")
	public List<ManuScriptCoAuthor> sendEmailToCoAuthorOnManuscriptList(Integer manuscriptId);
	
	@Query("SELECT m FROM ManuScriptCoAuthor m WHERE m.coAuthorId=:coAuthorId AND m.coAuthorManuscriptId=:coAuthorManuscriptId")
	public List<ManuScriptCoAuthor> getManuScriptCoAuthorDatas(Integer coAuthorId,Integer coAuthorManuscriptId);
	
	@Query("SELECT m FROM ManuScriptCoAuthor m WHERE m.coAuthorId=:coAuthorId AND m.coAuthorManuscriptId=:coAuthorManuscriptId")
	public ManuScriptCoAuthor getManuScriptCoAuthorData(Integer coAuthorId,Integer coAuthorManuscriptId);
	
	@Query("SELECT m FROM ManuScriptCoAuthor m WHERE m.id=:coAuthorId")
	public ManuScriptCoAuthor getCoAuthorById(Integer coAuthorId);
	
	@Modifying
	@Query("UPDATE ManuScriptCoAuthor SET is_corresponding_author=:isCorrespondingAuthor WHERE id=:id")
	public void updateCorrespondingAuthor(String isCorrespondingAuthor, Integer id);
	
	@Query("SELECT mk FROM ManuScriptCoAuthor mk where mk.coAuthorManuscriptId=:manuscriptId AND mk.isCorrespondingAuthor='Y'")
	public List<ManuScriptCoAuthor> getCorrespondingAuthorListFromManuscriptId(Integer manuscriptId);
	
	@Query("SELECT m FROM ManuScriptCoAuthor m WHERE m.coAuthorId=:coAuthorId AND m.id=:id")
	public ManuScriptCoAuthor getManuScriptCoAuthorDetails(Integer id, Integer coAuthorId);

	@Query("SELECT count(*) FROM ManuScriptCoAuthor m  WHERE m.coAuthorManuscriptId=:manuscriptId and m.coAuthorId=:id")
	public Integer getUniqueAuthorCount(Integer manuscriptId, Integer id);
	
	@Query("SELECT mca.coAuthorId FROM ManuScriptCoAuthor mca where mca.coAuthorManuscriptId=:manuscriptId AND mca.coAuthorStatus=1")
	public List<Integer> getCoAuthorForManuscript(Integer manuscriptId);
	
	@Query(value="SELECT u.user_email  FROM manuscript_coauthordetails mca INNER JOIN users u ON u.id=mca.coauth_id where mca.coauth_mansucripts_id=:manuscriptId AND mca.coauth_status=1", nativeQuery=true)
	public List<String> getCoAuthorEmail(Integer manuscriptId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE manuscript_coauthordetails SET coauth_id = :newUserId WHERE coauth_id = :oldUserId", nativeQuery = true)
    void updateCoAuthorUser(Integer oldUserId, Integer newUserId);
    
    @Query("SELECT COUNT(c) FROM ManuScriptCoAuthor c WHERE c.coAuthorManuscriptId = :manuscriptId AND c.coAuthorId = :userId")
    int existsByManuscriptAndUser(@Param("manuscriptId") Integer manuscriptId, @Param("userId") Integer userId);

    @Modifying
    @Query("UPDATE ManuScriptCoAuthor c SET c.coAuthorId = :newUserId " +
           "WHERE c.coAuthorManuscriptId = :manuscriptId AND c.coAuthorId = :oldUserId")
    void updateSingleCoAuthorUser(@Param("oldUserId") Integer oldUserId,
                                  @Param("newUserId") Integer newUserId,
                                  @Param("manuscriptId") Integer manuscriptId);

    @Modifying
    @Query("UPDATE ManuScriptCoAuthor m SET m.isEmailSent = :status WHERE m.id = :id")
    void updateIsEmailSentById(@Param("id") Integer id, @Param("status") Integer status);

	List<ManuScriptCoAuthor> findByCoAuthorManuscriptId(Integer manuscriptId);
	
	@Modifying
	@Transactional
	@Query(
	    value =
	        "UPDATE manuscript_coauthordetails c " +
	        "SET c.coauth_id = :newUserId " +
	        "WHERE c.coauth_id = :oldUserId " +
	        "AND c.coauth_mansucripts_id IN (:manuscriptIds) " +
	        "AND NOT EXISTS ( " +
	        "    SELECT 1 FROM ( " +
	        "         SELECT * FROM manuscript_coauthordetails " +
	        "    ) x " +
	        "    WHERE x.coauth_mansucripts_id = c.coauth_mansucripts_id " +
	        "    AND x.coauth_id = :newUserId " +
	        ")",
	    nativeQuery = true
	)
	int bulkUpdateCoAuthor(
	        @Param("oldUserId") Integer oldUserId,
	        @Param("newUserId") Integer newUserId,
	        @Param("manuscriptIds") List<Integer> manuscriptIds
	);




}
