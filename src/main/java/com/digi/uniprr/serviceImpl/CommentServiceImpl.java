package com.digi.uniprr.serviceImpl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.model.Comment;
import com.digi.uniprr.repository.CommentRepo;
import com.digi.uniprr.service.CommentService;

@Service
@Transactional
public class CommentServiceImpl implements CommentService {

	@Autowired
	CommentRepo commentRepo;
	
	@Override
	public Comment createComment(Comment comment) {
		comment.setIsActive(1);
		Comment data = commentRepo.save(comment);
		return data;
	}

	@Override
	public Comment deleteComment(Comment comment) {
		Comment input = commentRepo.getOne(comment.getId());
		input.setIsActive(0);
		Comment data = commentRepo.save(input);
		return data;
	}

	@Override
	public List<Comment> getDataByJournal(IDVO id) {
		List<Comment> data = commentRepo.getDataByJournal(id.getJournalId(), id.getFileType());
		return data;
	}

}
