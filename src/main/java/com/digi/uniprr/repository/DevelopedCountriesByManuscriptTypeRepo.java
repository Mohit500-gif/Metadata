package com.digi.uniprr.repository;

import com.digi.uniprr.model.DevelopedCountriesByManuscriptType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface DevelopedCountriesByManuscriptTypeRepo extends JpaRepository<DevelopedCountriesByManuscriptType, Integer> {

    @Modifying
    @Query("DELETE FROM DevelopedCountriesByManuscriptType j where j.journalId=:journalId")
    void deleteJournalManuscriptTypeCharge(Integer journalId);

    @Query(value = "select * from developed_countries_by_manuscript_type where journal_id=:journalId and manuscript_type_id=:manuscriptTypeId", nativeQuery = true)
    DevelopedCountriesByManuscriptType getByJournalAndManuscriptType(Integer journalId, Integer manuscriptTypeId);


}
