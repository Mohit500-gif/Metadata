package com.digi.uniprr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.digi.uniprr.model.IpTable;

public interface IpTableRepo extends JpaRepository<IpTable, Integer> {

	/*
	 * @Query("SELECT m FROM  iptable  m WHERE 20 between m.dotStart and m.dotEnd")
	 * IpTable getChargeFlagOnCountry(String ipInLong);
	 */
	
//	@Query(value="select * from iptable m where m.dot_start=:ipInLong or m.dot_end=:ipInLong",nativeQuery = true)
//	List<IpTable> getChargeFlagOnCountry(String ipInLong);
	
	@Query(value="SELECT * FROM  iptable WHERE :ipInLong between dot_start and dot_end",nativeQuery = true)
	List<IpTable> getChargeFlagOnCountry(long ipInLong);
	
	@Query("SELECT m FROM IpTable m WHERE m.country=:country")
	List<IpTable> getChargeFlagCountry(String country);
}
