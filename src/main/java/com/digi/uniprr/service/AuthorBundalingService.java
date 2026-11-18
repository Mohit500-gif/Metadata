package com.digi.uniprr.service;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.digi.uniprr.VO.BundleTrackerManuscriptVo;
import com.digi.uniprr.VO.CommentarySubmissionVo;
import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.VO.ResubmissionManuscriptFileVo;


public interface AuthorBundalingService {

	public String callMetadataAPi(ResubmissionManuscriptFileVo resubmissionManuscriptFileVo,HttpServletRequest request);
    
	ResponseEntity<Resource> downloadManuscript(IDVO idvo);

	public String updateFile(Integer manuscriptFileId,String fileStatus, MultipartFile file, HttpServletRequest request,boolean isUploaded);

	public JSONObject submitCommentarySubmission(CommentarySubmissionVo commentarySubmissionVo, HttpServletRequest request);

	public String updateManuscriptFile(ResubmissionManuscriptFileVo resubmissionManuscriptFileVo,
			HttpServletRequest request);

	public String bundledFile(ResubmissionManuscriptFileVo resubmissionManuscriptFileVo, HttpServletRequest request);

	public String updateManDocumentFile(Integer manuscriptFileId,String fileStatus, MultipartFile file,
			HttpServletRequest request);
	
	public String updateManDocumentFileWithRoleTask(Integer manuscriptFileId,Integer role,Integer task, String fileStatus, Integer userId, MultipartFile file,
			HttpServletRequest request);
	
	// Author file upload
	public boolean submissionUpdateUploadedFile(ResubmissionManuscriptFileVo resubmissionManuscriptFileVo);
	public boolean generatingBundlingFile(ResubmissionManuscriptFileVo resubmissionManuscriptFileVo, boolean update);
	
	public void updateCheckListResponse(ResubmissionManuscriptFileVo resubmissionManuscriptFileVo);
	
	public String generatingBundleForFailed(Integer manuscriptId);
	
	public boolean renameMainFirstDocument(ResubmissionManuscriptFileVo resubmissionManuscriptFileVo);
	
	public String insertFiledBundleManuscript(Integer manuscriptId, String bundleStatus, String message);
	public String insertFiledBundleManuscript(Integer manuscriptId, String bundleStatus, String message,Integer manuscriptVersion);
	
	void saveBundleTrackerDetails(Integer manuscriptId, String bundleStatus, String errorMessage,Integer manuscriptVersion);
	void saveBundleTrackerDetails(Integer manuscriptId, String bundleStatus, String errorMessage);
	

	String doPythonBundling(int manuscriptId, int manuscriptVersion);
	
	public void backupFilesWithTimestamp(ResubmissionManuscriptFileVo vo);
	
}
