package com.digi.uniprr.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.digi.uniprr.model.logging.ErrorLog;

@Repository
public interface ErrorLogRepo extends JpaRepository<ErrorLog, Long> {

}
