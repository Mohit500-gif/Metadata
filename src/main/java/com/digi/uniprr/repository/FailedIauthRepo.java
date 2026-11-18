package com.digi.uniprr.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.digi.uniprr.model.FailedIauthenticate;


public interface FailedIauthRepo extends JpaRepository<FailedIauthenticate, Integer> {
	

	
}
