package com.digi.uniprr.repository;

import org.springframework.data.jpa.repository.JpaRepository;import com.digi.uniprr.model.ImpactMessageLogsUK;

public interface ImpactMessageUKLogRepository extends JpaRepository<ImpactMessageLogsUK, Long>  {

	boolean existsByDoi(String doi);
}
