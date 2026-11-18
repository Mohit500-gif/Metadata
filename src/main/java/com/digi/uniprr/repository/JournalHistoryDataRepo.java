package com.digi.uniprr.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.digi.uniprr.model.JournalHistoryData;

@Repository
public interface JournalHistoryDataRepo extends JpaRepository<JournalHistoryData, Integer>{

}





