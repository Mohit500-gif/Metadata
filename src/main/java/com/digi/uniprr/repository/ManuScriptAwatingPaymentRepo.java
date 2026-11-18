package com.digi.uniprr.repository;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.digi.uniprr.model.ManuScript;
import com.digi.uniprr.model.ManuScriptAwatingPayment;


@Repository
@Transactional
public interface ManuScriptAwatingPaymentRepo extends JpaRepository<ManuScriptAwatingPayment, Integer> {
	
	@Query("SELECT m FROM ManuScript m where m.isActive=1 AND m.manuscriptStatusId IN :statusId")
     List<ManuScript> getAllPayedManuscript(List<Integer> statusId , Pageable paging);
	
	@Query("SELECT m FROM ManuScript m where (m.manuscriptTitle LIKE %:search% OR m.manuscriptCode LIKE %:search% OR m.journal.journalTitle LIKE %:search% OR m.user.firstName LIKE %:search% OR m.user.lastName LIKE %:search% OR m.updatedOn LIKE %:search% OR m.manuscriptTypeDetails.manuscriptType LIKE %:search%) AND m.manuscriptStatusId IN :statusId AND m.isActive=1")
	List<ManuScript> getAllPayedManuscriptSearch(List<Integer> statusId, String search , Pageable paging);
	
	@Query("SELECT COUNT(*) FROM ManuScript m where m.isActive=1 AND m.manuscriptStatusId IN :statusId")
	Integer getTotalAwatingPaymentManuscript(List<Integer> statusId);
	
	@Query("SELECT COUNT(*) FROM ManuScript m where (m.manuscriptTitle LIKE %:search% OR m.manuscriptCode LIKE %:search% OR m.journal.journalTitle LIKE %:search% OR m.user.firstName LIKE %:search% OR m.user.lastName LIKE %:search% OR m.updatedOn LIKE %:search% OR m.manuscriptTypeDetails.manuscriptType LIKE %:search%) AND m.manuscriptStatusId IN :statusId AND m.isActive=1")
	Integer getTotalAwatingPaymentManuscriptSearch(List<Integer> statusId, String search);
	

	
}