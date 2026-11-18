package com.digi.uniprr.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.digi.uniprr.model.ImpactMessageLogs;

public interface ImpactMessageLogRepository extends JpaRepository<ImpactMessageLogs, Long> {
	boolean existsByDoi(String doi);

}
