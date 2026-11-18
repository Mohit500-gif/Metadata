package com.digi.uniprr.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.digi.uniprr.VO.BundleStatusResponse;
import com.digi.uniprr.VO.BundleTrackerManuscriptVo;
import com.digi.uniprr.VO.CommentarySubmissionVo;
import com.digi.uniprr.VO.EmailTriggerVO;
import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.VO.ResubmissionManuscriptFileVo;
import com.digi.uniprr.model.BundleTracker;
import com.digi.uniprr.model.ManuScript;
import com.digi.uniprr.repository.ManuScriptFileRepo;
import com.digi.uniprr.repository.ManuScriptRepo;
import com.digi.uniprr.service.AuthorBundalingService;
import com.digi.uniprr.service.BundleTrackerService;
import com.digi.uniprr.service.EmailTriggerService;
import com.digi.uniprr.utils.AppConstants;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class AuthorBundalingRestController {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	AuthorBundalingService authorBundalingService;
	
	@Autowired
	EmailTriggerService emailTriggerService;
	
	@Autowired
	ManuScriptRepo manuScriptRepo;
	
	@Autowired
	
	ManuScriptFileRepo manuScriptFileRepo;
	
	@Autowired
	BundleTrackerService bundleTrackerService;


//	@PostMapping("updateManu")
//	public ResponseEntity<?> updateManuscriptFileUpload(
//			@RequestBody ResubmissionManuscriptFileVo resubmissionManuscriptFileVo, HttpServletRequest request) {
//		log.info("Inside AuthorRestController updateManuscriptFileUpload");
//		log.info("Mnuscript Id :" + resubmissionManuscriptFileVo.getManuScriptId() + " " + "Manuscript Version :"
//				+ resubmissionManuscriptFileVo.getManuscriptVersion());
//		JSONObject obj = new JSONObject();
//		try {
//			String message  = "";
//			boolean renameFileStatus;
//			
//			if(StringUtils.isNotBlank(resubmissionManuscriptFileVo.getManuscriptChecklistResponse())) {
//				authorBundalingService.updateCheckListResponse(resubmissionManuscriptFileVo);
//			}
//			
//			// This method submissionUpdateUploadedFile call for update file name , file type etc in DB.
//			boolean fileUpdateStatus = authorBundalingService.submissionUpdateUploadedFile(resubmissionManuscriptFileVo);
//			if(fileUpdateStatus) {
//				 renameFileStatus = authorBundalingService.renameMainFirstDocument(resubmissionManuscriptFileVo);
//			     if(renameFileStatus) {
//			    	// This method call API for generation of metadata. Its Calling Python Api.
//			    	//TODO Enable metadata Extraction 
//					/* String metadataAPIMessage = authorBundalingService.callMetadataAPi(resubmissionManuscriptFileVo, request);
//					 if(!metadataAPIMessage.equalsIgnoreCase("")) {
//						   message = "Not able to generate metadata";
//						  // authorBundalingService.deleteTypeEmptyFile(resubmissionManuscriptFileVo);
//						   log.error("Metadata generation is failed using Python api for manuscript :::::"+ resubmissionManuscriptFileVo.getManuScriptId());
//					      // Generating Bundle using Java Api
//					   }*/
//					   
//					  // This method generatingBundlingFile call for bundling file generation & DB update
//			    	 
//					  authorBundalingService.insertFiledBundleManuscript(resubmissionManuscriptFileVo.getManuScriptId(), AppConstants.INPROGRESS, "Bundle file creation started !",resubmissionManuscriptFileVo.getManuscriptVersion());
//				      boolean bundlingStatus =  authorBundalingService.generatingBundlingFile(resubmissionManuscriptFileVo, false);
//				      log.info("bundlingStatus : "+ bundlingStatus);
//				      message = bundlingStatus ? "Success" : "Not able to Bundled files";
//				      if(bundlingStatus) {
//						  authorBundalingService.insertFiledBundleManuscript(resubmissionManuscriptFileVo.getManuScriptId(), AppConstants.SUCCESS, "Bundle file creation completed !",resubmissionManuscriptFileVo.getManuscriptVersion());
//				      }
//			      }else {
//					   message = "Not able to Bundled files";
//					  // authorBundalingService.deleteTypeEmptyFile(resubmissionManuscriptFileVo);
//					   log.error("File Rename process for author bundling is failed for manuscript :::::"+ resubmissionManuscriptFileVo.getManuScriptId());
//			      }
//			}else {
//				 message = "Not able to Bundled files";
//				// authorBundalingService.deleteTypeEmptyFile(resubmissionManuscriptFileVo);
//     	         log.error("File Rename process for author bundling is failed for manuscript :::::"+ resubmissionManuscriptFileVo.getManuScriptId());
//			}
//
//			log.info("Submission proccess Done");
//			obj.put("data", message);
//		} catch (Exception e) {
//			String message = "Exception occured during bundling of manuscript : "+ resubmissionManuscriptFileVo.getManuScriptId() + e.getMessage();
//			log.error(message, e);
//			obj.put("data", "Error");
//			authorBundalingService.insertFiledBundleManuscript(resubmissionManuscriptFileVo.getManuScriptId(), AppConstants.FAILED, message,resubmissionManuscriptFileVo.getManuscriptVersion());
//		}
//		return new ResponseEntity<Object>(obj, HttpStatus.OK);
//	}
	
	@PostMapping("updateManu")
	public ResponseEntity<?> updateManuscriptFileUpload(
	        @RequestBody ResubmissionManuscriptFileVo resubmissionManuscriptFileVo, HttpServletRequest request) {
	    log.info("Inside AuthorRestController updateManuscriptFileUpload");
	    log.info("Manuscript Id: {} Manuscript Version: {}", 
	             resubmissionManuscriptFileVo.getManuScriptId(), 
	             resubmissionManuscriptFileVo.getManuscriptVersion());

	    JSONObject obj = new JSONObject();
	    try {
	        String message = "";
	        boolean renameFileStatus;

	        if (StringUtils.isNotBlank(resubmissionManuscriptFileVo.getManuscriptChecklistResponse())) {
	            authorBundalingService.updateCheckListResponse(resubmissionManuscriptFileVo);
	        }

	        // Update file info in DB
	        boolean fileUpdateStatus = authorBundalingService.submissionUpdateUploadedFile(resubmissionManuscriptFileVo);
	        if (fileUpdateStatus) {
	            renameFileStatus = authorBundalingService.renameMainFirstDocument(resubmissionManuscriptFileVo);
	            if (renameFileStatus) {

	                // Insert progress status
	                authorBundalingService.insertFiledBundleManuscript(
	                        resubmissionManuscriptFileVo.getManuScriptId(),
	                        AppConstants.INPROGRESS,
	                        "Bundle file creation started !",
	                        resubmissionManuscriptFileVo.getManuscriptVersion()
	                );

	                // Generate bundle
	                boolean bundlingStatus = authorBundalingService.generatingBundlingFile(resubmissionManuscriptFileVo, false);
	                log.info("bundlingStatus : {}", bundlingStatus);

	                message = bundlingStatus ? "Success" : "Not able to Bundled files";
	                if (bundlingStatus) {
	                    authorBundalingService.insertFiledBundleManuscript(
	                            resubmissionManuscriptFileVo.getManuScriptId(),
	                            AppConstants.SUCCESS,
	                            "Bundle file creation completed !",
	                            resubmissionManuscriptFileVo.getManuscriptVersion()
	                    );

	                    // 🔹 After success → Backup files
	                    authorBundalingService.backupFilesWithTimestamp(resubmissionManuscriptFileVo);
	                }

	            } else {
	                message = "Not able to Bundled files";
	                log.error("File Rename process for author bundling failed for manuscript: {}", 
	                          resubmissionManuscriptFileVo.getManuScriptId());
	            }
	        } else {
	            message = "Not able to Bundled files";
	            log.error("File update process for author bundling failed for manuscript: {}", 
	                      resubmissionManuscriptFileVo.getManuScriptId());
	        }

	        log.info("Submission process Done");
	        obj.put("data", message);

	    } catch (Exception e) {
	        String message = "Exception occurred during bundling of manuscript : " 
	                + resubmissionManuscriptFileVo.getManuScriptId() + " " + e.getMessage();
	        log.error(message, e);
	        obj.put("data", "Error");
	        authorBundalingService.insertFiledBundleManuscript(
	                resubmissionManuscriptFileVo.getManuScriptId(),
	                AppConstants.FAILED,
	                message,
	                resubmissionManuscriptFileVo.getManuscriptVersion()
	        );
	    }
	    return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	
	@PostMapping("updateManuscriptFile")
	public ResponseEntity<?> updateManuscriptFile(
			@RequestBody ResubmissionManuscriptFileVo resubmissionManuscriptFileVo, HttpServletRequest request) {
		log.info("Inside AuthorRestController updateManuscriptFileUpload");
		log.info("Mnuscript Id :" + resubmissionManuscriptFileVo.getManuScriptId() + " " + "Manuscript Version :"
				+ resubmissionManuscriptFileVo.getManuscriptVersion());
		JSONObject obj = new JSONObject();
		try {
			String message = authorBundalingService.updateManuscriptFile(resubmissionManuscriptFileVo, request);
				obj.put("data", message);
				return new ResponseEntity<Object>(obj, HttpStatus.OK);
		} catch (Exception e) {
			log.info("Exception" + e);
			obj.put("data", "Error");
			return new ResponseEntity<Object>(obj, HttpStatus.OK);
		}
	}
	
	@PostMapping("bundledFile")
	public ResponseEntity<?> bundledFile(
			@RequestBody ResubmissionManuscriptFileVo resubmissionManuscriptFileVo, HttpServletRequest request) {
		log.info("Inside AuthorRestController updateManuscriptFileUpload");
		log.info("Mnuscript Id :" + resubmissionManuscriptFileVo.getManuScriptId() + " " + "Manuscript Version :"
				+ resubmissionManuscriptFileVo.getManuscriptVersion());
		JSONObject obj = new JSONObject();
		try {
			String message = authorBundalingService.bundledFile(resubmissionManuscriptFileVo, request);
				obj.put("data", message);
				return new ResponseEntity<Object>(obj, HttpStatus.OK);
		} catch (Exception e) {
			log.info("Exception" + e);
			obj.put("data", "Error");
			return new ResponseEntity<Object>(obj, HttpStatus.OK);
		}
	}
	
	
	@PostMapping("/downloadManuscriptFile")
	public ResponseEntity<Resource> downloadManuscript(@RequestBody IDVO idvo,
	                                                   HttpServletRequest request) {

	    log.info("==============================================");
	    log.info("Download Manuscript Request");
	    log.info("Manuscript Id      : {}", idvo.getManuscriptId());
	    log.info("Version            : {}", idvo.getManuscriptVersion());
	    log.info("File Type          : {}", idvo.getFileType());

	    return authorBundalingService.downloadManuscript(idvo);
	}
	
	@PostMapping("updateFile")
	public ResponseEntity<?> updateFile(@RequestParam("manuscriptFileId") Integer manuscriptFileId,@RequestParam("fileStatus") String fileStatus,MultipartFile file, HttpServletRequest request,boolean isUploaded) {
		JSONObject obj = new JSONObject();
		try {
			String getData=authorBundalingService.updateFile(manuscriptFileId,fileStatus,file,request,isUploaded);
			
			if(getData=="There is no file to update") {
				isUploaded=false;
				String message =  !isUploaded ? "Failed to upload file(s)" : "";
				return new ResponseEntity<Object>( message,HttpStatus.OK);
			}
			obj.put("data", getData);
		} catch (Exception e) {
			log.info("Exception" + e);
			obj.put("data", "Error");
		}
		return new ResponseEntity<Object>( obj,HttpStatus.OK);
	}
	
	@PostMapping("updateManDocumentFile")
	public ResponseEntity<?> updateManDocumentFile(@RequestParam("manuscriptFileId") Integer manuscriptFileId, @RequestParam("fileStatus") String fileStatus,MultipartFile file, HttpServletRequest request) {
		JSONObject obj = new JSONObject();
		try {
			String getData=authorBundalingService.updateManDocumentFile(manuscriptFileId,fileStatus,file,request);
			obj.put("data", getData);
		} catch (Exception e) {
			log.info("Exception" + e);
			obj.put("data", "Error");
		}
		return new ResponseEntity<Object>( obj,HttpStatus.OK);
	}
	
	
	@PostMapping("updateManDocumentFileWithRoleTask")
	public ResponseEntity<?> updateManDocumentFileWithRoleTask(@RequestParam("manuscriptFileId") Integer manuscriptFileId,@RequestParam("role") Integer role, @RequestParam("manuscriptStatus") Integer manuscriptStatus,@RequestParam("fileStatus") String fileStatus,@RequestParam("userId") Integer userId,MultipartFile file, HttpServletRequest request) {
		JSONObject obj = new JSONObject();
		try {
			String getData=authorBundalingService.updateManDocumentFileWithRoleTask(manuscriptFileId,role, manuscriptStatus,fileStatus,userId,file,request);
			obj.put("data", getData);
		} catch (Exception e) {
			log.info("Exception" + e);
			obj.put("data", "Error");
		}
		return new ResponseEntity<Object>( obj,HttpStatus.OK);
	}
	
	@PostMapping("submitCommentarySubmission")
	public ResponseEntity<?> submitCommentarySubmission(@ModelAttribute CommentarySubmissionVo commentarySubmissionVo, HttpServletRequest request) {
		JSONObject obj = new JSONObject();
		try {
			JSONObject getData=authorBundalingService.submitCommentarySubmission(commentarySubmissionVo,request);
			Integer manusriptId = (Integer) getData.get("manuscriptId");
			// Email Trigger
						log.info("Inside AuthorRestController submitManuscript For Email Trigger");
						if (commentarySubmissionVo.getJournalId() != null && commentarySubmissionVo.getCommentaryUserId() != null
								&& manusriptId != null) {
							log.debug("-----------------manusriptId------------");
							// Sending Mail To Author
							EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
							emailTriggerVO.setJournalId(commentarySubmissionVo.getJournalId());
							emailTriggerVO.setUserId(commentarySubmissionVo.getCommentaryUserId());
							emailTriggerVO.setAuthorUserId(commentarySubmissionVo.getCommentaryUserId());
							emailTriggerVO.setManuscriptId(manusriptId);
							emailTriggerVO.setManuscriptStatus(2);
							emailTriggerVO.setReceiverRoleId(4);
							emailTriggerVO.setTaskId(2);
							emailTriggerVO.setRoleId(4);
							log.info("Inside in AuthorRestController submitManuscript For Email Trigger Data are:->"
									+ emailTriggerVO.toString());
							log.info("User Receiver Id:->" + commentarySubmissionVo.getCommentaryUserId());
							log.info("User Receiver Role Id:->" + 4);
							log.info("User Sender Role Id:->" + 4);
							log.info("User Sender Id:->" + commentarySubmissionVo.getCommentaryUserId());
							log.info("Task Id:->" + 2);
							String message = emailTriggerService.emailTrigger(emailTriggerVO);
							obj.put("data", message);
							log.info("Message of AuthorRestController submitManuscript For Email Trigger " + message);
							log.info("End of AuthorRestController submitManuscript For Email Trigger ");

							// Sending Email to Assign Editor And Managing Editor On that Journal
						//	List<UserJournal> getAllUserJournal = userJournalRepo.getAllUserJournalList(vo.getJournalId());
							
							ManuScript  i=manuScriptRepo.getOne(manusriptId);
							if (i != null) {
								log.info(
										"Inside in AuthorRestController submitManuscript For Sending Email to Assign Editor And Managing Editor On that Journal");
								//getAllUserJournal.stream().forEach(i -> {
									//i.getUser().getRole().stream().forEach(j -> {
										//if (j.getRoleId().equals(2)) {
											EmailTriggerVO emailTrigger = new EmailTriggerVO();
											emailTrigger.setJournalId(commentarySubmissionVo.getJournalId());
											emailTrigger.setUserId(i.getAssignedUser());
											emailTrigger.setManagingEditorId(i.getAssignedUser());
											emailTrigger.setManuscriptId(manusriptId);
											emailTrigger.setManuscriptStatus(2);
											
											//if (j.getRoleId().equals(2)) {
												log.info("User Receiver Role Id:->" + 2);
												emailTrigger.setReceiverRoleId(2);
											emailTrigger.setTaskId(13);
											emailTrigger.setRoleId(4);
											log.info(
													"Inside in AuthorRestController submitManuscript For Sending Email to Assign Editor And Managing Editor On that Journal Data are:->"
															+ emailTriggerVO.toString());
											log.info("User Receiver Id:->" + i.getAssignedUser());
											log.info("Task Id:->" + 13);
											String messageS = emailTriggerService.emailTrigger(emailTrigger);
											obj.put("data", messageS);
											log.info(
													"Message of AuthorRestController submitManuscript For Sending Email to Assign Editor And Managing Editor On that Journal "
															+ message);
											log.info(
													"End of AuthorRestController submitManuscript For Sending Email to Assign Editor And Managing Editor On that Journal ");
										}
						}
				return new ResponseEntity<Object>( getData,HttpStatus.OK);
		} catch (Exception e) {
			log.info("Exception" + e);
			obj.put("data", "Error");
			return new ResponseEntity<Object>( obj,HttpStatus.OK);
		}
		
	}
	
	
	@PostMapping("grenratePdfFile")
	public ResponseEntity<?> grenratePdfFile(@RequestBody ResubmissionManuscriptFileVo resubmissionManuscriptFileVo, HttpServletRequest request) {
		log.info("Inside AuthorRestController updateManuscriptFileUpload");
		JSONObject obj = new JSONObject();
		try {
			authorBundalingService.insertFiledBundleManuscript(resubmissionManuscriptFileVo.getManuScriptId(), AppConstants.INPROGRESS, "Bundle file creation started !",resubmissionManuscriptFileVo.getManuscriptVersion());			
			boolean	success = authorBundalingService.generatingBundlingFile(resubmissionManuscriptFileVo, true);
			String message = success ? "success" : "Fail";
		      if(success) {
				  authorBundalingService.insertFiledBundleManuscript(resubmissionManuscriptFileVo.getManuScriptId(), AppConstants.SUCCESS, "Bundle file creation completed !",resubmissionManuscriptFileVo.getManuscriptVersion());
		      }
			obj.put("data", message);
			return new ResponseEntity<Object>(obj, HttpStatus.OK);
		} catch (Exception e) {
			obj.put("data", "Error");
			String message ="Exception from grenratePdfFile : " + e.getMessage(); 
			log.error(message, e);
			
			authorBundalingService.insertFiledBundleManuscript(resubmissionManuscriptFileVo.getManuScriptId(), AppConstants.FAILED, message,resubmissionManuscriptFileVo.getManuscriptVersion());
			return new ResponseEntity<Object>(obj, HttpStatus.OK);
		}
	}
	
	/**
	 * Not in use
	 * @return
	 */
	@GetMapping("genrateBundleForFailed")
	public ResponseEntity<?> grenratePdfFileForFailed(@RequestParam Integer size) {
		log.info("Inside AuthorRestController updateManuscriptFileUpload");
		JSONObject obj = new JSONObject();
		String message = null;
		try {
			List<BundleTracker> failedListArticle = bundleTrackerService.failedListArticle(size);
			    for (BundleTracker failedBundelFile : failedListArticle) {
			    	try {
			    		ManuScript manuscript = manuScriptRepo.getById(failedBundelFile.getManuscriptId());
			    		ResubmissionManuscriptFileVo resubmissionManuscriptFileVo = new ResubmissionManuscriptFileVo();
			    		resubmissionManuscriptFileVo.setManuScriptId(manuscript.getManuScriptId());
			    		resubmissionManuscriptFileVo.setManuscriptVersion(manuscript.getManuscriptVersion());
						boolean	success = authorBundalingService.generatingBundlingFile(resubmissionManuscriptFileVo, true);
						message = success ? "success" : "Fail";
					      if(success) {
							  authorBundalingService.insertFiledBundleManuscript(failedBundelFile.getManuscriptId(), AppConstants.SUCCESS, "Bundle file creation completed !",resubmissionManuscriptFileVo.getManuscriptVersion());
					      }
					      }catch(Exception e) {
					    	log.info("Bundle Generation has been failed for manuscript ::= "+failedBundelFile.getManuscriptId() + "Error message ::"+e.getMessage());
			    	}
					wait(5000);
				}
			   
				obj.put("data", message);
				return new ResponseEntity<Object>(obj, HttpStatus.OK);
				
		} catch (Exception e) {
			log.info("Exception" + e);
			obj.put("data", "Error");
			return new ResponseEntity<Object>(obj, HttpStatus.OK);
		}
	}
	
	@PostMapping("failedBundleManuscript")
	public ResponseEntity<?> failedBundleManuscript(@RequestBody BundleTrackerManuscriptVo failedBundleManuscriptVo) {
		log.info("Inside AuthorRestController updateManuscriptFileUpload");
		JSONObject obj = new JSONObject();
		try {
			String message = authorBundalingService.insertFiledBundleManuscript(failedBundleManuscriptVo.getManuscriptId(), failedBundleManuscriptVo.getBundleStatus(), failedBundleManuscriptVo.getErrorMessage());
				obj.put("data", message);
				return new ResponseEntity<Object>(obj, HttpStatus.OK);
		} catch (Exception e) {
			log.info("Exception" + e);
			obj.put("data", "Error");
			return new ResponseEntity<Object>(obj, HttpStatus.OK);
		}
	}
	
	@GetMapping("doPythonBundling")
	public ResponseEntity<String> doPythonBundling(@RequestParam int manuscriptId, @RequestParam int manuscriptVersion) {
		String message = authorBundalingService.doPythonBundling(manuscriptId, manuscriptVersion);
		return ResponseEntity.ok(message);
	}
	
	@PostMapping("getBundleStatus")
	public ResponseEntity<?> getBundleStatus(@RequestBody BundleTrackerManuscriptVo failedBundleManuscriptVo) {
	    JSONObject obj = new JSONObject();
	    BundleStatusResponse failedManuscript = bundleTrackerService.getBundleDetails(failedBundleManuscriptVo.getManuscriptId());

	    if (failedManuscript != null) {
	        obj.put("data", failedManuscript.getBundleStatus());
	        
	        // Check if the status is Failed or Failed2
	        if (("Failed".equalsIgnoreCase(failedManuscript.getBundleStatus()) ||
	            "Failed2".equalsIgnoreCase(failedManuscript.getBundleStatus()))&&!failedManuscript.getFailureReason().equals("")) {
	            
	            // Get the failure reason and ignore the unwanted message
	            String failureReason = failedManuscript.getFailureReason();
	            
	            obj.put("failureReason", failureReason);
	        }
	    } else {
	        obj.put("data", "Success");
	    }

	    return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	
}
