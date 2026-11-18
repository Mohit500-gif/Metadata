package com.digi.uniprr.repository;

import java.util.List;

import javax.persistence.Tuple;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.digi.uniprr.model.EditorRole;

public interface EditorRoleServiceRepo extends JpaRepository<EditorRole, Integer>{

	@Query(value="SELECT er.id,er.role, er.editor_access, er.is_active FROM editor_role er WHERE er.is_active=1", nativeQuery = true)
	public List<Tuple> getActiveData();
	
}
