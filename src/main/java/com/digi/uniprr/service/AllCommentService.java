package com.digi.uniprr.service;

import java.util.List;

import com.digi.uniprr.VO.CommentVO;
import com.digi.uniprr.VO.FileVo;
import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.VO.ManuscriptRatingVo;
import com.digi.uniprr.VO.OverDueManuscriptVO;

public interface AllCommentService {

	public CommentVO getAllComment(IDVO id);
	
	public CommentVO getAllCommentAuthor(IDVO id);
	
	public String ratingSetup(ManuscriptRatingVo manuscriptRatingVo);
	
	public ManuscriptRatingVo getRatingSetupData(ManuscriptRatingVo manuscriptRatingVo);

	public OverDueManuscriptVO getOverDueManuscript(Integer journalId);

	public List<FileVo> getAllReviewerFileOnManuscript(IDVO id);

	public void insertRole();

	public String submitPaymentDetailsByEic(IDVO id);
	
	public CommentVO getBlinkerComment(IDVO id);
	//UN-10 CR changes
	public CommentVO getOtherReviewersComment(IDVO id);
}
