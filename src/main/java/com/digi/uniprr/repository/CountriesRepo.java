package com.digi.uniprr.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.digi.uniprr.model.Countries;

import java.util.List;

public interface CountriesRepo extends JpaRepository<Countries, Integer> {

	@Query("SELECT c FROM Countries c where c.name=:trim")
	Countries getCountryByName(String trim);

	@Query(value = "select * from countries c where c.id!=249",nativeQuery = true)
	List<Countries> findAllExceptNA();

}
