package com.digi.uniprr.controller;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.digi.uniprr.utils.AppConstants;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digi.uniprr.VO.LetterToEditorVo;
import com.digi.uniprr.model.JournalMailTemplates;
import com.digi.uniprr.model.JournalTemp;
import com.digi.uniprr.model.ManuscriptDashboardList;
import com.digi.uniprr.model.ManuscriptType;
import com.digi.uniprr.model.TaskEmailMap;
import com.digi.uniprr.repository.JournalMailTemplatesRepo;
import com.digi.uniprr.repository.JournalsTempRepo;
import com.digi.uniprr.repository.ManuscriptTypeRepo;
import com.digi.uniprr.repository.TaskEmailMapRepo;
import com.digi.uniprr.service.LetterToEditorService;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping(value = "/letterToEditor")
public class LetterToEditorRestController {

	@Autowired
	LetterToEditorService letterToEditorService;

	@Autowired
	JournalMailTemplatesRepo journalMailTemplatesRepo;

	@Autowired
	TaskEmailMapRepo taskEmailMapRepo;

	@Autowired
	JournalsTempRepo journalsRepo;

	@Autowired
	ManuscriptTypeRepo typeRepo;

	private final Logger log = LoggerFactory.getLogger(this.getClass());


	@PostMapping("getManuscriptList")
	public ResponseEntity<?> getManuscriptList(@RequestBody ManuscriptDashboardList letterToEditorVo) {
		JSONObject obj = new JSONObject();

		try {
			Integer totalManuscriptCount = letterToEditorService
				.getManuscriptByStatusCount(letterToEditorVo);

			List<ManuscriptDashboardList> manuscriptList = letterToEditorService
					.getManuscriptByStatus(letterToEditorVo);

			obj.put("data",manuscriptList);
			obj.put("resultCount",totalManuscriptCount);
			return new ResponseEntity<Object>(obj,HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			obj.put("data","error");
			return new ResponseEntity<Object>(obj,HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("getIssueNameList")
	public ResponseEntity<?> getIssueNameList(@RequestBody ManuscriptDashboardList letterToEditorVo) {
		JSONObject obj = new JSONObject();

		try {
			List<String> issueNameList = letterToEditorService.getIssueList(letterToEditorVo.getJournalId());

			obj.put("data",issueNameList);
			return new ResponseEntity<Object>(obj,HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			obj.put("data","error");
			return new ResponseEntity<Object>(obj,HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


	@PostMapping("updateManuscriptToLTEArticle")
	public ResponseEntity<?> updateManuscriptToLTEArticle(@RequestBody LetterToEditorVo letterToEditorVo) {
		JSONObject obj = new JSONObject();

		try {
			letterToEditorService
					.updateManuscriptToLTEArticle(letterToEditorVo.getLteManuscriptId(),letterToEditorVo.getManuscriptId(),letterToEditorVo.getJournalId());

			obj.put("data","Article assigned successfully");
			return new ResponseEntity<Object>(obj,HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			obj.put("data","Error while assigning article");
			return new ResponseEntity<Object>(obj,HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


	@PostMapping("inviteAuthorForResponse")
	public ResponseEntity<?> inviteAuthorForResponse(@RequestBody LetterToEditorVo letterToEditorVo) {
		JSONObject obj = new JSONObject();
		try {
				letterToEditorService.inviteAuthorForResponse(letterToEditorVo);
			obj.put("data", "Invitation for commentary sent");

		} catch (Exception e) {
			e.printStackTrace();
			obj.put("data", "Something went wrong");
			return new ResponseEntity<Object>(obj,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Object>(obj,HttpStatus.OK);
	}

	@PostMapping("getAuthorResponseList")
	public ResponseEntity<?> getAuthorResponseList(@RequestBody LetterToEditorVo letterToEditorVo) {
		JSONObject obj = new JSONObject();
		log.info(AppConstants.REQUEST_METHOD+" :: "+AppConstants.POST+
				AppConstants.REQUEST_URI+" :: /letterToEditor/getAuthorResponseList"+
				AppConstants.REQUEST_BODY+" :: "+letterToEditorVo.toString());
		try {
			List<LetterToEditorVo>  a= letterToEditorService.getAuthorResponseList(letterToEditorVo);
			if(a!=null && !a.isEmpty()) {
				obj.put("data", a);
			}else {
				obj.put("data", "No Data Found");
			}
		} catch (Exception e) {
			e.printStackTrace();
			obj.put("data", "Something went wrong");
			return new ResponseEntity<Object>(obj,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Object>(obj,HttpStatus.OK);
	}


	@PostMapping("getAuthorResponse")
	public ResponseEntity<?> getAuthorResponse(@RequestBody LetterToEditorVo letterToEditorVo) {
		JSONObject results = new JSONObject();
		try {
			List<LetterToEditorVo> letterToEditorVos = letterToEditorService.getAuthorResponse(letterToEditorVo.getManuscriptId(),letterToEditorVo.getJournalId());

			results.put("result",letterToEditorVos);

		} catch (Exception e) {
			e.printStackTrace();
			results.put("result", "failed");
			return new ResponseEntity<Object>(results,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Object>(results,HttpStatus.OK);
	}


	@PostMapping("approveRejectInvitation")
	public ResponseEntity<?> approveRejectInvitation(@RequestBody LetterToEditorVo letterToEditorVo) {
		JSONObject results = new JSONObject();
		try {

			if(StringUtils.equalsIgnoreCase(letterToEditorVo.getAction(),"accept"))
			{
				letterToEditorVo.setApprovedOn(new Date());
				letterToEditorService.approveInvitationStatus(letterToEditorVo.getId(),letterToEditorVo.getUserId(),letterToEditorVo.getApprovedOn(),letterToEditorVo.getManuscriptId(),letterToEditorVo.getUserRole(),letterToEditorVo.getAction());
			}else if(StringUtils.equalsIgnoreCase(letterToEditorVo.getAction(),"reject"))
			{
				letterToEditorVo.setRejectOn(new Date());
				letterToEditorService.rejectInvitationStatus(letterToEditorVo.getId(),letterToEditorVo.getUserId(),letterToEditorVo.getRejectOn(),letterToEditorVo.getManuscriptId(),letterToEditorVo.getUserRole(),letterToEditorVo.getAction());
			}else
			{
				letterToEditorVo.setSubmittedDate(new Date());
				letterToEditorService.submitInvitationStatus(letterToEditorVo.getId(),letterToEditorVo.getUserId(),letterToEditorVo.getSubmittedDate(),letterToEditorVo.getManuscriptId(),letterToEditorVo.getUserRole(),letterToEditorVo.getAction());
			}

			results.put("result","success");

		} catch (Exception e) {
			e.printStackTrace();
			results.put("result", "failed");
			return new ResponseEntity<Object>(results,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Object>(results,HttpStatus.OK);
	}

	@PostMapping("deleteInvitation")
	public ResponseEntity<?> deleteInvitation(@RequestBody LetterToEditorVo letterToEditorVo) {
		JSONObject results = new JSONObject();
		try {
			String message = letterToEditorService.deleteInvitation(letterToEditorVo);

			results.put("result",message);

		} catch (Exception e) {
			e.printStackTrace();
			results.put("result", "failed");
			return new ResponseEntity<Object>(results,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Object>(results,HttpStatus.OK);
	}

	@PostMapping("updateEmailTemplates")
	public ResponseEntity<?> updateEmailTemplates(@RequestBody LetterToEditorVo userVo) {
		JSONObject results = new JSONObject();
		try {
			JournalMailTemplates temp = journalMailTemplatesRepo.getUniqueJournalMailTemplates(userVo.getJournalId(), userVo.getTemplateName());

			List<Integer> journalIds = journalsRepo.getAllJournal().stream().map(obj -> obj.getJournalId()).collect(Collectors.toList());
			List<Integer> addedJournalIds = journalMailTemplatesRepo.getEmailTemaplatesByName(userVo.getTemplateName()).stream().map(obj -> obj.getJournalId()).collect(Collectors.toList());

			journalIds.removeIf(obj-> addedJournalIds.contains(obj));

			for (Integer journalId : journalIds)
			{

				JournalMailTemplates templates = journalMailTemplatesRepo.getUniqueJournalMailTemplates(journalId, userVo.getTemplateName());

				if(templates == null && temp !=null)
				{
					JournalMailTemplates templates1 = new JournalMailTemplates();
					BeanUtils.copyProperties(temp,templates1);
					templates1.setJournalId(journalId);
					templates1.setJournalEmailTemplateId(null);
					journalMailTemplatesRepo.save(templates1);
				}
			}

			List<Integer> journalMailTemplates = taskEmailMapRepo.customiseMailTemplate(userVo.getTemplateId()).stream().map(obj -> obj.getJournalId()).collect(Collectors.toList());
			List<Integer> totalJournalIds = journalsRepo.getAllJournal().stream().map(obj -> obj.getJournalId()).collect(Collectors.toList());

			totalJournalIds.removeIf(obj -> journalMailTemplates.contains(obj));

			System.out.println("Total Count to be inserted : "+ totalJournalIds);
			AtomicInteger count = new AtomicInteger();
			for(Integer journalId : totalJournalIds)
			{
				JournalMailTemplates templates = journalMailTemplatesRepo.getUniqueJournalMailTemplates(journalId, userVo.getTemplateName());

				TaskEmailMap taskEmailMap = new TaskEmailMap();
				taskEmailMap.setTaskId(userVo.getTemplateId());
				taskEmailMap.setJournalEmailTemplateId(templates.getJournalEmailTemplateId());
				taskEmailMap.setJournalId(journalId);
				taskEmailMapRepo.save(taskEmailMap);

				System.out.println("Count of Insert" + count.incrementAndGet());
				System.out.println("Journal Id" + journalId);
			}

			results.put("result","success");

		} catch (Exception e) {
			e.printStackTrace();
			results.put("result", "failed");
			return new ResponseEntity<Object>(results,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Object>(results,HttpStatus.OK);
	}

	@PostMapping("updateManuscriptType")
	public ResponseEntity<?> updateManuscriptType(@RequestBody LetterToEditorVo userVo) {
		JSONObject results = new JSONObject();
		try {
			ManuscriptType temp = typeRepo.getManuscriptTypeByJournal(userVo.getTemplateName(), userVo.getJournalId());

			List<JournalTemp> journalIds = journalsRepo.getActivejournal();  //.stream().map(obj -> obj.getJournalId()).collect(Collectors.toList());

			for (JournalTemp journalId : journalIds)
			{

				ManuscriptType templates = typeRepo.getManuscriptTypeByJournal(userVo.getTemplateName(),journalId.getJournalId());

				if(templates == null && temp !=null)
				{
					ManuscriptType templates1 = new ManuscriptType();
					BeanUtils.copyProperties(temp,templates1);
					templates1.setId(null);
					templates1.setJournalId(journalId.getJournalId());
					typeRepo.save(templates1);
				}
			}

			results.put("result","success");

		} catch (Exception e) {
			e.printStackTrace();
			results.put("result", "failed");
			return new ResponseEntity<Object>(results,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Object>(results,HttpStatus.OK);
	}




}
