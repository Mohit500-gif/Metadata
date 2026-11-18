package com.digi.uniprr.service;

import java.util.List;

import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.model.Comment;

public interface CommentService {

	Comment createComment(Comment comment);

	Comment deleteComment(Comment comment);

	List<Comment> getDataByJournal(IDVO id);

}
