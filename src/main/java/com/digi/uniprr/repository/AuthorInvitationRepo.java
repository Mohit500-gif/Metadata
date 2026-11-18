package com.digi.uniprr.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.digi.uniprr.model.AuthorInvitation;

@Repository
@Transactional
public interface AuthorInvitationRepo extends JpaRepository<AuthorInvitation, Integer>{

	@Query("SELECT ai from AuthorInvitation ai where ai.sender=:id AND ai.isActive=1")
	List<AuthorInvitation> getAuthorInvitationByUserId(Integer id);
	
	@Query("SELECT ai from AuthorInvitation ai where ai.receiver=:name AND ai.isActive=1")
	List<AuthorInvitation> getAuthorInvitationByReceiver(String name);

	@Modifying(clearAutomatically = true)
	@Query("update AuthorInvitation ai set ai.isSeen =1 where ai.id =:id")
	void readIvitatio(Integer id);
}
