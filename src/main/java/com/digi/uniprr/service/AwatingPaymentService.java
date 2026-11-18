package com.digi.uniprr.service;

import java.util.List;

import com.digi.uniprr.VO.CommentVO;
import com.digi.uniprr.VO.FileVo;
import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.VO.ManuscriptListVO;
import com.digi.uniprr.VO.ManuscriptRatingVo;
import com.digi.uniprr.VO.OverDueManuscriptVO;

public interface AwatingPaymentService {

	public List<ManuscriptListVO> getAwatingPaymentManuscript(ManuscriptListVO manuscriptVO);
	
	public int getTotalAwatingPaymentManuscript(ManuscriptListVO manuscriptVO);
	
	public int getTotalAwatingPaymentManuscriptSearch(ManuscriptListVO manuscriptVO);
}
