package com.digi.uniprr.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartFile;

import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.VO.ImpactArticleDTO;

public interface AuthorProofingService {

	String getManuscriptOutputFile(IDVO vo);
	
	String uploadManuscriptInputFile(Integer manuScriptId,MultipartFile[] files,String isRecheck, String noChangeApproved, String comment, int manuscriptStatusId);
	
	public List<ImpactArticleDTO> callFetchImpact(String aid);
	
	public List<ImpactArticleDTO> callFetchImpactBulk(List<String> aidList);
}
