package com.digi.uniprr.service;

import java.util.List;

import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.model.AuthorInvitation;

public interface AuthorInvitationService {

	List<AuthorInvitation> getAuthorInvitationByUserId(IDVO id);

	void createAuthorInvitation(AuthorInvitation authorInvitation);
	
	void readInvitation(IDVO id);
}
