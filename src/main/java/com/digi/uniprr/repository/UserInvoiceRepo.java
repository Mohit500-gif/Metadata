package com.digi.uniprr.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.digi.uniprr.model.UserInvoice;
@Repository
@Transactional
public interface UserInvoiceRepo extends JpaRepository<UserInvoice, Integer>{

	@Query("Select ui from UserInvoice ui where ui.manuscriptId=:manuScriptId AND ui.userId=:userId")
	UserInvoice getPaymentDetailByUser(Integer manuScriptId, Integer userId);
	
	@Query("Select ui from UserInvoice ui where ui.manuscriptId=:manuScriptId")
	UserInvoice getPaymentDetailByMid(Integer manuScriptId);
	
	@Query("Select ui from UserInvoice ui where ui.userId=:userId")
	List<UserInvoice> getPaymentHistoryByUser(Integer userId);

	@Query("Select ui from UserInvoice ui where ui.journalId IN :journalId")
	List<UserInvoice> getPaymentHistoryByJournalId(List<Integer> journalId);
	
	@Query("Select ui from UserInvoice ui where ui.journalId=:journalId")
	List<UserInvoice> getPaymentHistoryJournalId(Integer journalId);
	
}
