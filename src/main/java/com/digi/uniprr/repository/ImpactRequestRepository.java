package com.digi.uniprr.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.digi.uniprr.model.ImpactRequest;

@Repository
public interface ImpactRequestRepository extends JpaRepository<ImpactRequest, String> {

	boolean existsByDoi(String doi);
}
