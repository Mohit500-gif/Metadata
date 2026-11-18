package com.digi.uniprr.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.digi.uniprr.model.JournalPolicies;

@Repository
@Transactional
public interface JournalPoliciesRepo extends JpaRepository<JournalPolicies, Integer>{

	@Query("SELECT mt FROM JournalPolicies mt WHERE mt.journalId=:journalId")
	JournalPolicies getJournalPoliciesDetails(Integer journalId);
}
