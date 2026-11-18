package com.digi.uniprr.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.digi.uniprr.model.GoLive;

public interface GoLiveRepo extends JpaRepository<GoLive, Integer> {
	
	@Query(" SELECT gl from GoLive gl")
	GoLive getGoLiveData();
	
}
