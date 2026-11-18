package com.digi.uniprr.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digi.uniprr.model.ManuscriptComment;
import com.digi.uniprr.repository.ManuscriptCommentRepo;
import com.digi.uniprr.service.ManuscriptCommentService;

@Service
public class ManuscriptCommentServiceImpl implements ManuscriptCommentService {
	
	@Autowired
	private ManuscriptCommentRepo manuscriptCommentRepo;

	@Override
	public void save(ManuscriptComment manuscriptComment) {
		manuscriptCommentRepo.save(manuscriptComment);

	}


}
