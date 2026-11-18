package com.digi.uniprr.serviceImpl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.digi.uniprr.VO.EmailTriggerVO;
import com.digi.uniprr.VO.ManuscriptIssuesPlannerVO;
import com.digi.uniprr.VO.ManuscriptListVO;
import com.digi.uniprr.VO.ReviewerVO;
import com.digi.uniprr.model.EditorAvailability;
import com.digi.uniprr.model.ManuScript;
import com.digi.uniprr.model.ManuscriptEditor;
import com.digi.uniprr.model.ManuscriptEditorComment;
import com.digi.uniprr.model.ManuscriptIssuesPlanner;
import com.digi.uniprr.model.ReviewerAvailability;
import com.digi.uniprr.model.Role;
import com.digi.uniprr.model.User;
import com.digi.uniprr.repository.EditorAvailabilityRepo;
import com.digi.uniprr.repository.ManuScriptRepo;
import com.digi.uniprr.repository.ManuscriptEditorCommentRepo;
import com.digi.uniprr.repository.ManuscriptEditorRepo;
import com.digi.uniprr.repository.ManuscriptIssuesPlannerRepo;
import com.digi.uniprr.repository.UserRepo;
import com.digi.uniprr.service.EditorService;
import com.digi.uniprr.service.EmailTriggerService;

@Service
@Transactional
public class EditorServiceImpl implements EditorService {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	ManuScriptRepo manuScriptRepo;
	
	@Autowired
	EditorAvailabilityRepo editorAvailabilityRepo;
	
	@Autowired
	UserRepo userRepo;
	
	@Autowired
	ManuscriptIssuesPlannerRepo manuscriptIssuesPlannerRepo;
	
	@Autowired
	ManuscriptEditorRepo manuscriptEditorRepo;
	
	@Autowired
	EmailTriggerService emailTriggerService;
	
	@Autowired
	ManuscriptEditorCommentRepo manuscriptEditorCommentRepo;
	
	@Autowired
	Environment environment;

	@Value("${uniprrFolderPath}")
	private String UPLOAD_FOLDER;
	
	
	@SuppressWarnings("unused")
	@Override
	public List<ManuscriptListVO> getManuScriptDataByStatus(ManuscriptListVO manuscriptListVO) {
		for (int i = 0; i <= manuscriptListVO.getManuscriptStatusId().size() - 1; i++) {
			List<ManuScript> manuList = manuScriptRepo.getManuScriptDataByStatus(manuscriptListVO.getJournalId(),
					manuscriptListVO.getManuscriptStatusId().get(i));
			List<ManuscriptListVO> manuVo = new ArrayList<ManuscriptListVO>();
			manuList.stream().forEach(manu -> {
				ManuscriptListVO manuListVo = new ManuscriptListVO();
				manuListVo.setManuScriptId(manu.getManuScriptId());
				manuListVo.setJournalId(manu.getJournalId());
				manuListVo.setManuscripCode(manu.getManuscriptCode());
				manuListVo.setManuscripTitle(manu.getManuscriptTitle());
				manuListVo.setManuscriptAuthorId(manu.getAuthorId());
				manuListVo.setManuScriptId(manu.getManuscriptStatusId());
				manuListVo.setManuscriptVersion(manu.getManuscriptVersion());
				manuListVo.setSubmissionDate(manu.getSubmissionDate());
				manuVo.add(manuListVo);
			});
			return manuVo;
		}
		return null;
	}

	@Override
	public void updateManuScriptByAuthor(ManuscriptListVO manuscriptListVO) {
		
			manuScriptRepo.updateManuScriptByAuthor(manuscriptListVO.getManuScriptId(),
					manuscriptListVO.getManuscriptAuthorId(), manuscriptListVO.getManuStatusId());
		
	}
	
	@Override
	public String disableAllEditor(ManuscriptListVO manuscriptListVO,  HttpServletRequest request) {
		String message ="";
		BufferedOutputStream stream = null;
        try {
    	ManuScript manuScript = manuScriptRepo.getById(manuscriptListVO.getManuScriptId());
    	if(!manuScript.getAssignedUserRole().equals(2))
    	{
		ManuscriptEditor manuscriptEditor = manuscriptEditorRepo.getAllAssignedEdiorsOnManuscriptActive(manuscriptListVO.getManuScriptId(), manuscriptListVO.getManuscriptVersion());
		if(manuscriptEditor != null) {
		manuscriptEditorRepo.disableEditor(manuscriptListVO.getManuScriptId());
		manuscriptEditorRepo.updateSendBackEic(manuscriptListVO.getManuScriptId(), manuscriptListVO.getEditorUserId());
		if(manuscriptListVO.getManuStatusId() != null && manuscriptListVO.getManuStatusId().equals(50))
		{
			manuScript.setAssignedUserRole(2);
			manuScript.setAssignedUser(manuscriptEditor.getManagingEditorId());
			manuScript.setManuscriptStatusId(73);
			manuScript.setUpdatedOn(new Date());
			manuScriptRepo.save(manuScript);
		    //manuScriptRepo.updateForSendBackEic(manuscriptListVO.getManuScriptId(),manuscriptEditor.getManagingEditorId(),73);
		}else{
			manuScript.setAssignedUserRole(2);
			manuScript.setAssignedUser(manuscriptEditor.getManagingEditorId());
			manuScript.setManuscriptStatusId(72);
			manuScript.setUpdatedOn(new Date());
			manuScriptRepo.save(manuScript);
		    //manuScriptRepo.updateForSendBackEic(manuscriptListVO.getManuScriptId(),manuscriptEditor.getManagingEditorId(),72);
		}	
		ManuscriptEditorComment addManuscriptEditorComment = new ManuscriptEditorComment();

		if (manuscriptListVO.getFile() != null && !manuscriptListVO.getFile().isEmpty()) {
		log.info("-------------------Creation manuscript Editor Comment ------------------");

			String location = UPLOAD_FOLDER;
			String attachmentFolderPath = null;
			
				log.info("-------------------Send back EIC File Folder------------------");
				attachmentFolderPath = location + File.separator + "UniprrSendBackEicFolder" + File.separator
						+ "MNT_LWW_JOURNAL_" + "R" + manuscriptEditor.getManuscriptVersion() + "_"
						+ manuscriptEditor.getJournalId() + "_" + manuscriptEditor.getManuscriptId();
				log.info("-------------------Send back EIC File Folder------------------" + attachmentFolderPath);
			
			File dir = new File(attachmentFolderPath);
			if (!dir.exists()) {
				log.info("-------------------File Folder Created------------------");
				dir.mkdirs();
			}
			
			byte[] bytes = manuscriptListVO.getFile().getBytes();
			File serverFile = new File(dir.getAbsolutePath() + File.separator
					+ manuscriptListVO.getFile().getOriginalFilename());
			stream = new BufferedOutputStream(new FileOutputStream(serverFile));
			stream.write(bytes);
			double fileSizeinBytes = manuscriptListVO.getFile().getSize();
			double fileSizeInKB = Math.round((fileSizeinBytes / 1024));
			Integer port = request.getServerPort();
			InetAddress inetAddress = InetAddress.getLocalHost();
			String fileServerPath = null;
			
				log.info("-------------------Inside In Send back EIC File Path------------------");
				if (environment.getProperty("server.status").equalsIgnoreCase("AWS")) {
					String Ip = "https://" + environment.getProperty("awsFileIp");
					fileServerPath = Ip + File.separator + "UniprrSendBackEicFolder" + File.separator
							+ "MNT_LWW_JOURNAL_" + "R" + manuscriptEditor.getManuscriptVersion() + "_"
							+ manuscriptEditor.getJournalId() + "_" + manuscriptEditor.getManuscriptId()
							+ File.separator + manuscriptListVO.getFile().getOriginalFilename();
					log.info("Server Path for AWS : " + fileServerPath);
				} else if (environment.getProperty("server.status").equalsIgnoreCase("LOCAL")) {
					String Ip = "https://" + inetAddress.getHostAddress() + ":" + port;
					fileServerPath = Ip + File.separator + "UniprrRevisonEicFolder" + File.separator
							+ "MNT_LWW_JOURNAL_" + "R" + manuscriptEditor.getManuscriptVersion() + "_"
							+ manuscriptEditor.getJournalId() + "_" + manuscriptEditor.getManuscriptId()
							+ File.separator + manuscriptListVO.getFile().getOriginalFilename();
				} 
				
				addManuscriptEditorComment.setFilePath(fileServerPath);
				addManuscriptEditorComment.setFileSize((int) fileSizeInKB);	
				addManuscriptEditorComment.setFileName(manuscriptListVO.getFile().getOriginalFilename());

		}
		
		if(manuscriptListVO.getManuStatusId() != null && manuscriptListVO.getManuStatusId().equals(50))
		{
			addManuscriptEditorComment.setStatusId(manuscriptListVO.getManuStatusId());
		}else{
			addManuscriptEditorComment.setStatusId(2);
		}
		addManuscriptEditorComment.setCreatedAt(new Date());
		addManuscriptEditorComment.setManuscriptId(manuscriptListVO.getManuScriptId());
		addManuscriptEditorComment.setManuscriptVersion(manuscriptEditor.getManuscriptVersion());
		addManuscriptEditorComment.setUserId(manuscriptEditor.getEditorId());
		addManuscriptEditorComment.setRoleId(2);
		addManuscriptEditorComment.setComment(manuscriptListVO. getSendBackEicComment());
		addManuscriptEditorComment.setFileType("Send Back To EIC");
		log.info("----------------------File Details has been added in Editor Comment--------------------");
        manuscriptEditorCommentRepo.save(addManuscriptEditorComment);
	    log.info("----------------------Editor Comment Has Been Created--------------------");
		
		// Email Trigger to Eic/Editor While Send back to EIC
		if (manuscriptListVO.getJournalMailTemplatesBody() != null && !manuscriptListVO.getJournalMailTemplatesBody().isEmpty()) {
			EmailTriggerVO emailTriggerVO = new EmailTriggerVO();
			emailTriggerVO.setEicFolderName("Returned by Editorial Staff");	
			emailTriggerVO.setJournalId(manuscriptEditor.getJournalId());
			emailTriggerVO.setUserId(manuscriptEditor.getManagingEditorId());
			emailTriggerVO.setEditorUserId(manuscriptListVO.getEditorUserId());
			emailTriggerVO.setManagingEditorId(manuscriptEditor.getManagingEditorId());
			emailTriggerVO.setManuscriptId(manuscriptListVO.getManuScriptId());
			emailTriggerVO.setManuscriptStatus(manuscriptListVO.getManuStatusId());
			emailTriggerVO.setReceiverRoleId(2);
			emailTriggerVO.setEmailTemplateBody(manuscriptListVO.getJournalMailTemplatesBody());
			emailTriggerVO.setEmailTemplateSubject(manuscriptListVO.getSubject());
			emailTriggerVO.setTaskId(76);
			emailTriggerVO.setRoleId(3);
			log.info("Inside in Email Trigger to Eic While Send back to EIC For Email Trigger Data are:->"
					+ emailTriggerVO.toString());
			 message = emailTriggerService.emailTrigger(emailTriggerVO);

			log.info("End of assignUser Email Trigger to Eic/Editor While Send back to EIC For Email Trigger ");
		}

		}else {
			message ="Editor not found";
		}
    	} else {
    		message ="Article already processed.";
    	}
		
        }catch(Exception ex) {
        	
        	log.error("Exception occured during article send back to EIC"+ ex.getMessage());
        }finally {
			try {
				if (manuscriptListVO.getFile() != null)
					stream.close();
				log.info("-------------------File Close------------------");
			} catch (IOException e) {
				e.printStackTrace();
				log.info("-------------------Exception due to file ------------------>" + e);
			}
		}
		return message;
	}

	@Override
	public void saveEditorAvailability(EditorAvailability editorAvailability) {
		if(editorAvailability.getIsActive() == null) {
			editorAvailability.setIsActive(1);
		}
		editorAvailabilityRepo.save(editorAvailability);
		
	}

	@Override
	public List<EditorAvailability> getEditorAvailabilityById(Integer id) {
		return editorAvailabilityRepo.getEditorAvailabilityById(id);
		 
	}

	@Override
	public void deleteEditorAvailabilityById(Integer id) {
		editorAvailabilityRepo.deleteReviewerAvailabilityById(id);
		
	}

	@Override
	public List<ReviewerVO> getEditorWithAvailability() {

		List<ReviewerVO> editorData = new ArrayList<>();

		List<User> user = userRepo.getAllActiveUser();
		if (user != null && !user.isEmpty()) {

			for (User uerDetail : user) {
				ReviewerVO data = new ReviewerVO();
				for (Role roleDetail : uerDetail.getRole()) {
					if (roleDetail.getRoleId().equals(3)) {
						List<EditorAvailability> editorUnavaailableList = editorAvailabilityRepo
								.getEditorAvailabilityById(uerDetail.getId());
						List<ReviewerAvailability> eUnavaailable = new ArrayList<>();
						if (editorUnavaailableList != null && !editorUnavaailableList.isEmpty()) {
							for (EditorAvailability eData : editorUnavaailableList) {
								ReviewerAvailability eUnavailableData = new ReviewerAvailability();
								eUnavailableData.setUnavilableFrom(eData.getUnavilableFrom());
								eUnavailableData.setUnavailableTo(eData.getUnavailableTo());
								eUnavaailable.add(eUnavailableData);
							}
						}
						data.setFirstname(uerDetail.getFirstName());
						data.setLastname(uerDetail.getLastName());
						data.setEmail(uerDetail.getEmail());
						if (eUnavaailable != null) {
							data.setReviewerAvailability(eUnavaailable);
						}
						editorData.add(data);
					}

				}

			}

		}

		return editorData;
	
	}

	@Override
	public String createIssueForManuscript(ManuscriptIssuesPlannerVO manuscriptIssuesPlannerVO) {
		ManuscriptIssuesPlanner manuscriptIssuePlannerData=manuscriptIssuesPlannerRepo.getManuscriptIssuesPlannerList(manuscriptIssuesPlannerVO.getIssueId(), manuscriptIssuesPlannerVO.getManuscriptId(), manuscriptIssuesPlannerVO.getManuscriptVersion());
		if(manuscriptIssuePlannerData!=null) {
			return "Choose The Unique Issue And Manuscript";
		}
		else {
			if(manuscriptIssuesPlannerVO.getId()!=null) {
				ManuscriptIssuesPlanner updateManuscriptIssuePlanner=manuscriptIssuesPlannerRepo.getOne(manuscriptIssuesPlannerVO.getId());
				updateManuscriptIssuePlanner.setIssueId(manuscriptIssuesPlannerVO.getIssueId());
				updateManuscriptIssuePlanner.setUpdatedBy(manuscriptIssuesPlannerVO.getUpdatedBy());
				updateManuscriptIssuePlanner.setUpdatedDate(new Date());
				manuscriptIssuesPlannerRepo.save(updateManuscriptIssuePlanner);
				return "Issue has been updated successfully";
			}
			else {
				ManuscriptIssuesPlanner createManuscriptIssuePlanner=new ManuscriptIssuesPlanner();
				createManuscriptIssuePlanner.setIssueId(manuscriptIssuesPlannerVO.getIssueId());
				createManuscriptIssuePlanner.setManuscriptId(manuscriptIssuesPlannerVO.getManuscriptId());
				createManuscriptIssuePlanner.setManuscriptVersion(manuscriptIssuesPlannerVO.getManuscriptVersion());
				createManuscriptIssuePlanner.setCreatedBy(manuscriptIssuesPlannerVO.getCreatedBy());
				createManuscriptIssuePlanner.setJournalId(manuscriptIssuesPlannerVO.getJournalId());
				createManuscriptIssuePlanner.setIsActive(1);
				manuscriptIssuesPlannerRepo.save(createManuscriptIssuePlanner);
				return "Issue has been created successfully";
			}
		}
	}

	@Override
	public List<ManuscriptIssuesPlannerVO> getIssueListForManuscript(
			ManuscriptIssuesPlannerVO manuscriptIssuesPlannerVO) {
		List<ManuscriptIssuesPlanner> manuscriptAssginList=manuscriptIssuesPlannerRepo.getManuscripAssignList(manuscriptIssuesPlannerVO.getJournalId());
		List<ManuscriptIssuesPlannerVO> addManuscriptAssignListToTheVo=new ArrayList<>();
		if(manuscriptAssginList!=null && !manuscriptAssginList.isEmpty()) {
			manuscriptAssginList.stream().forEach(i->{
				ManuscriptIssuesPlannerVO addManuscriptList=new ManuscriptIssuesPlannerVO();
				addManuscriptList.setId(i.getId());
				addManuscriptList.setManuscriptId(i.getManuscriptId());
				addManuscriptList.setManuscriptVersion(i.getManuscriptVersion());
				addManuscriptList.setIssueId(i.getIssueId());
				addManuscriptList.setManuscriptName(i.getManuScript().getManuscriptTitle());
				addManuscriptList.setJournalId(i.getJournalId());
				addManuscriptList.setJournalName(i.getJournalIssuesPlanner().getJournal().getJournalTitle());
				addManuscriptList.setIssueMonth(i.getJournalIssuesPlanner().getIssueMonth());
				addManuscriptList.setIssueName(i.getJournalIssuesPlanner().getIssueName());
				addManuscriptList.setIssueVolume(i.getJournalIssuesPlanner().getIssueVolume());
				addManuscriptList.setIssueYear(i.getJournalIssuesPlanner().getIssueYear());
				addManuscriptList.setCreatedBy(i.getCreatedBy());
				addManuscriptList.setUpdatedBy(i.getUpdatedBy());
				addManuscriptList.setCreatedDate(i.getCreatedDate());
				addManuscriptAssignListToTheVo.add(addManuscriptList);
			});
			
		}
		return addManuscriptAssignListToTheVo;
	}

	@Override
	public String deleteAssginManuscriptONIssue(ManuscriptIssuesPlannerVO manuscriptIssuesPlannerVO) {
		manuscriptIssuesPlannerRepo.deleteManuscriptIssuesPlanner(manuscriptIssuesPlannerVO.getId());
		return "Assign Manuscript on Issue has been deleted successfully";
	}

}
