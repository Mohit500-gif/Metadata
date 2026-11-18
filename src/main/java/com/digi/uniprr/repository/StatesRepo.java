package com.digi.uniprr.repository;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.digi.uniprr.model.States;

public interface StatesRepo extends JpaRepository<States, Integer> {

	@Query("select s from States s WHERE s.countryId=:countryId")
	List<States> getStatesByCoutryId(Integer countryId);
	
	@Query("SELECT NEW com.digi.uniprr.model.States(s.id, s.code, s.countryId, s.name, s.flag) FROM States s WHERE s.countryId = :countryId")
	List<States> getStates(Integer countryId);
	
	States findByName(String name);
	
	@Query(value="SELECT * FROM states s WHERE s.state_code = :code AND s.country_id = 101",nativeQuery = true)
	States getNameAndCountryId(String code);
}
