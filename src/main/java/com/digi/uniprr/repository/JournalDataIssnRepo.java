package com.digi.uniprr.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.digi.uniprr.model.JournalDataIssn;

@Repository
@Transactional
public interface JournalDataIssnRepo extends  JpaRepository<JournalDataIssn, String>{

    @Query(value = "SELECT * FROM journal_data_issn WHERE journal_issn_url LIKE CONCAT('%issn=', :issn, '%') LIMIT 1",nativeQuery = true)
    	    JournalDataIssn getIssnUrl(String issn);
}
