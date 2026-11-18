package com.digi.uniprr.controller;

import com.digi.uniprr.VO.CommentaryVo;
import com.digi.uniprr.VO.ManuScriptReviewerCommonVO;
import com.digi.uniprr.model.Journal;
import com.digi.uniprr.model.JournalMailTemplates;
import com.digi.uniprr.model.TaskEmailMap;
import com.digi.uniprr.repository.*;
import com.digi.uniprr.service.CommentaryService;
import com.digi.uniprr.service.ManuScriptReviewerService;
import com.digi.uniprr.utils.AppConstants;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping(value = "/commentary")
public class CommentaryRestController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	CommentaryService commentaryService;

	@Autowired
	UserRoleRepo userRoleRepo;

	@Autowired
	ManuScriptReviewerRepo manuScriptReviewerRepo;

	@Autowired
	ManuScriptReviewerService manuScriptReviewerService ;

	@Autowired
	JournalMailTemplatesRepo journalMailTemplatesRepo;

	@Autowired
	TaskEmailMapRepo taskEmailMapRepo;

	@Autowired
	JournalsRepo journalsRepo;
	private final Logger log = LoggerFactory.getLogger(this.getClass());


	@PostMapping("inviteCommentary")
	public ResponseEntity<?> inviteCommentary(@RequestBody CommentaryVo commentaryVo) {
		JSONObject obj = new JSONObject();
		try {
			commentaryService.inviteCommentary(commentaryVo);
			obj.put("data", "Invitation for commentary sent");
		} catch (Exception e) {
			e.printStackTrace();
			obj.put("data", "Something went wrong");
		}
		return new ResponseEntity<Object>(obj,HttpStatus.OK);
	}
	
	@PostMapping("getCommentary")
	public ResponseEntity<?> getCommentary(@RequestBody CommentaryVo commentaryVo) {
		log.info(AppConstants.REQUEST_METHOD+" :: "+AppConstants.POST+
				AppConstants.REQUEST_URI+" :: /commentary/getCommentary"+
				AppConstants.REQUEST_BODY+" :: "+commentaryVo.toString());
		JSONObject obj = new JSONObject();
		try {
			List<CommentaryVo>  a= commentaryService.getCommentary(commentaryVo);
			if(a!=null && !a.isEmpty()) {
				obj.put("data", a);
			}else {
				obj.put("data", "No Data Found");
			}
		} catch (Exception e) {
			e.printStackTrace();
			obj.put("data", "Something went wrong");
		}
		return new ResponseEntity<Object>(obj,HttpStatus.OK);
	}


	@PostMapping("getUsersForCommentary")
	public ResponseEntity<?> getUsersForCommentary(@RequestBody ManuScriptReviewerCommonVO commentaryVo) {
		JSONObject results = new JSONObject();
		try {
			List<ManuScriptReviewerCommonVO>   commentaryList;
			Integer commentaryCount;

			if(StringUtils.isNotBlank(commentaryVo.getSearch()))
			{
				commentaryList = manuScriptReviewerService.getRefineReviewerData(commentaryVo);
				commentaryCount = manuScriptReviewerRepo.getRefineReviewerDataCount(commentaryVo.getJournalId(),commentaryVo.getManuscriptId(),commentaryVo.getSearch());
			}else
			{
				//Reviewers who has said Yes for commentary for the manuscript
				  commentaryList = manuScriptReviewerService.getCommentaryDataForManuscript(commentaryVo);
				 commentaryCount = manuScriptReviewerService.getCommentaryDataForManuscriptCount(commentaryVo);

			}






			Comparator<ManuScriptReviewerCommonVO>  nameComparator = Comparator.comparing(ManuScriptReviewerCommonVO::getName);
			Comparator<ManuScriptReviewerCommonVO>  emailComparator = Comparator.comparing(ManuScriptReviewerCommonVO::getEmail);
			Comparator<ManuScriptReviewerCommonVO>  countryComparator = Comparator.comparing(ManuScriptReviewerCommonVO::getCountry);
			Comparator<ManuScriptReviewerCommonVO>  specializationComparator = Comparator.comparing(ManuScriptReviewerCommonVO::getSpecialization);
			Comparator<ManuScriptReviewerCommonVO>  institutionComparator = Comparator.comparing(ManuScriptReviewerCommonVO::getInstitution);
			Comparator<ManuScriptReviewerCommonVO>  subSpecializationComparator = Comparator.comparing(ManuScriptReviewerCommonVO::getSubSpecialization);


			if(commentaryList.size() > 0) {
				// Sorting criteria put
				if (StringUtils.equalsIgnoreCase(commentaryVo.getSortBy(), "name"))
				{
					if (StringUtils.equalsIgnoreCase(commentaryVo.getSort(), "asc"))
						commentaryList.sort(nameComparator);
					else
						commentaryList.sort(nameComparator.reversed());
				}
				if (StringUtils.equalsIgnoreCase(commentaryVo.getSortBy(), "email"))
				{
					if (StringUtils.equalsIgnoreCase(commentaryVo.getSort(), "asc"))
						commentaryList.sort(emailComparator);
					else
						commentaryList.sort(emailComparator.reversed());
				}

				if (StringUtils.equalsIgnoreCase(commentaryVo.getSortBy(), "country"))
				{
					if (StringUtils.equalsIgnoreCase(commentaryVo.getSort(), "asc"))
						commentaryList.sort(countryComparator);
					else
						commentaryList.sort(countryComparator.reversed());
				}

				if (StringUtils.equalsIgnoreCase(commentaryVo.getSortBy(), "specialization"))
				{
					if (StringUtils.equalsIgnoreCase(commentaryVo.getSort(), "asc"))
						commentaryList.sort(specializationComparator);
					else
						commentaryList.sort(specializationComparator.reversed());
				}

				if (StringUtils.equalsIgnoreCase(commentaryVo.getSortBy(), "subSpecialization"))
				{
					if (StringUtils.equalsIgnoreCase(commentaryVo.getSort(), "asc"))
						commentaryList.sort(subSpecializationComparator);
					else
						commentaryList.sort(subSpecializationComparator.reversed());
				}

				if (StringUtils.equalsIgnoreCase(commentaryVo.getSortBy(), "institution"))
				{
					if (StringUtils.equalsIgnoreCase(commentaryVo.getSort(), "asc"))
						commentaryList.sort(institutionComparator);
					else
						commentaryList.sort(institutionComparator.reversed());
				}
			}

			results.put("commentaryList", commentaryList);
			results.put("commentaryListCount", commentaryCount);

		} catch (Exception e) {
			e.printStackTrace();
			results.put("data", "Something went wrong");
			return new ResponseEntity<Object>(results,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Object>(results,HttpStatus.OK);
	}

	@PostMapping("checkAuthorRoleForReviewer")
	public ResponseEntity<?> checkAuthorRoleForReviewer(@RequestBody CommentaryVo commentaryVo) {
		JSONObject results = new JSONObject();
		try {

			Integer roleCount = userRoleRepo.getAuthorRoleCount(commentaryVo.getUserId());

			results.put("count", roleCount);

		} catch (Exception e) {
			e.printStackTrace();
			results.put("data", "Something went wrong");
			return new ResponseEntity<Object>(results,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Object>(results,HttpStatus.OK);
	}


	@PostMapping("updateRole")
	public ResponseEntity<?> updateRole(@RequestBody CommentaryVo userVo) {
		JSONObject results = new JSONObject();
		try {
			userVo.setUserRole(4);
			commentaryService.updateUser(userVo);

			results.put("result","success");

		} catch (Exception e) {
			e.printStackTrace();
			results.put("result", "failed");
			return new ResponseEntity<Object>(results,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Object>(results,HttpStatus.OK);
	}


	@PostMapping("deleteInvitation")
	public ResponseEntity<?> deleteInvitation(@RequestBody CommentaryVo userVo) {
		JSONObject results = new JSONObject();
		try {
			String message = commentaryService.deleteInvitation(userVo);

			results.put("result",message);

		} catch (Exception e) {
			e.printStackTrace();
			results.put("result", "failed");
			return new ResponseEntity<Object>(results,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Object>(results,HttpStatus.OK);
	}


	@PostMapping("getCommentaryList")
	public ResponseEntity<?> getCommentaryList(@RequestBody CommentaryVo userVo) {
		JSONObject results = new JSONObject();
		try {
			List<CommentaryVo> commentaryVos = commentaryService.getCommentaryList(userVo.getManuscriptId(),userVo.getJournalId());

			results.put("result",commentaryVos);

		} catch (Exception e) {
			e.printStackTrace();
			results.put("result", "failed");
			return new ResponseEntity<Object>(results,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Object>(results,HttpStatus.OK);
	}


	@PostMapping("linkCommentaryToManuscript")
	public ResponseEntity<?> linkCommentaryToManuscript(@RequestBody CommentaryVo userVo) {
		JSONObject results = new JSONObject();
		try {

			commentaryService.linkCommentaryToManuscript(userVo.getManuscriptId(),userVo.getCommentaryId(),userVo.getJournalId());

			results.put("result","success");

		} catch (Exception e) {
			e.printStackTrace();
			results.put("result", "failed");
			return new ResponseEntity<Object>(results,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Object>(results,HttpStatus.OK);
	}

	@PostMapping("commentaryGlobalSearch")
	public ResponseEntity<?> commentaryGlobalSearch(
			@RequestBody ManuScriptReviewerCommonVO manuScriptReviewerCommonVO) {
		JSONObject obj = new JSONObject();
		try {

			List<ManuScriptReviewerCommonVO> databaseSuggestedreviewer = commentaryService
					.commentaryGlobalSearch(manuScriptReviewerCommonVO);

			// Sorting criteria put
			if (StringUtils.equalsIgnoreCase(manuScriptReviewerCommonVO.getSortBy(), "name")) {
				Comparator<ManuScriptReviewerCommonVO> compareById = (ManuScriptReviewerCommonVO o1,
																	  ManuScriptReviewerCommonVO o2) -> o1.getName().compareTo(o2.getName());

				if (manuScriptReviewerCommonVO.getSort().equalsIgnoreCase("asc"))
					databaseSuggestedreviewer.stream().sorted(compareById);

				else
					databaseSuggestedreviewer.stream().sorted(compareById.reversed());

			}

			obj.put("dtabaseSuggestedReviewer", databaseSuggestedreviewer);
			obj.put("totalReviewerSuggestedBydatabase", databaseSuggestedreviewer.size());

			return new ResponseEntity<>(obj, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			obj.put("data", "Something went wrong");
			return new ResponseEntity<>(obj, HttpStatus.BAD_REQUEST);
		}

	}


	@PostMapping("getDatabaseAuthorsAndReviewers")
	public ResponseEntity<?> getDatabaseAuthorsAndReviewers(@RequestBody ManuScriptReviewerCommonVO manuScriptReviewerCommonVO) {
		JSONObject results = new JSONObject();
		try {


			List<ManuScriptReviewerCommonVO> databaseSuggestedreviewer = commentaryService
					.getReviewerDatabaseSuggested(manuScriptReviewerCommonVO);


			// Total count of reviewer for pagination
			Integer totalReviewerSuggestedByDatabase = commentaryService
					.totalReviewerSuggestedBtDatabaseCount(manuScriptReviewerCommonVO);


			// Sorting criteria put
			if (manuScriptReviewerCommonVO.getSortBy().equalsIgnoreCase("name")) {
				Comparator<ManuScriptReviewerCommonVO>  nameComparator = Comparator.comparing(ManuScriptReviewerCommonVO::getName);

				if (manuScriptReviewerCommonVO.getSort().equalsIgnoreCase("asc"))
					databaseSuggestedreviewer.sort(nameComparator);
				else
					databaseSuggestedreviewer.sort(nameComparator.reversed());
			}

			if (manuScriptReviewerCommonVO.getSortBy().equalsIgnoreCase("email")) {

				Comparator<ManuScriptReviewerCommonVO>  emailComparator = Comparator.comparing(ManuScriptReviewerCommonVO::getEmail);

				if (manuScriptReviewerCommonVO.getSort().equalsIgnoreCase("asc"))
					databaseSuggestedreviewer.sort(emailComparator);
				else
					databaseSuggestedreviewer.sort(emailComparator.reversed());

			}

			if (manuScriptReviewerCommonVO.getSortBy().equalsIgnoreCase("country")) {

				Comparator<ManuScriptReviewerCommonVO>  countryComparator = Comparator.comparing(ManuScriptReviewerCommonVO::getCountry);

				if (manuScriptReviewerCommonVO.getSort().equalsIgnoreCase("asc"))
					databaseSuggestedreviewer.sort(countryComparator);
				else
					databaseSuggestedreviewer.sort(countryComparator.reversed());

			}

			if (manuScriptReviewerCommonVO.getSortBy().equalsIgnoreCase("specialization")) {

				Comparator<ManuScriptReviewerCommonVO>  specializationComparator = Comparator.comparing(ManuScriptReviewerCommonVO::getSpecialization);

				if (manuScriptReviewerCommonVO.getSort().equalsIgnoreCase("asc"))
					databaseSuggestedreviewer.sort(specializationComparator);
				else
					databaseSuggestedreviewer.sort(specializationComparator.reversed());
			}

			if (manuScriptReviewerCommonVO.getSortBy().equalsIgnoreCase("subSpecialization")) {

				Comparator<ManuScriptReviewerCommonVO>  subSpecializationComparator = Comparator.comparing(ManuScriptReviewerCommonVO::getSubSpecialization);

				if (manuScriptReviewerCommonVO.getSort().equalsIgnoreCase("asc"))
					databaseSuggestedreviewer.sort(subSpecializationComparator);
				else
					databaseSuggestedreviewer.sort(subSpecializationComparator.reversed());

			}

			results.put("dtabaseSuggestedReviewer", databaseSuggestedreviewer);
			results.put("totalReviewerSuggestedBydatabase", totalReviewerSuggestedByDatabase);

		} catch (Exception e) {
			e.printStackTrace();
			results.put("data", "Something went wrong");
			return new ResponseEntity<>(results, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<Object>(results,HttpStatus.OK);
	}


    @PostMapping("approveRejectInvitation")
    public ResponseEntity<?> approveRejectInvitation(@RequestBody CommentaryVo userVo) {
        try {
            if (userVo == null || StringUtils.isEmpty(userVo.getAction())) {
                logger.error("Invalid request. UserVo or action is missing.");
                return ResponseEntity.badRequest().body("Invalid request.");
            }

            // Initialize result JSON object
            JSONObject results = new JSONObject();

            if (StringUtils.equalsIgnoreCase(userVo.getAction(), "accept")) {
                userVo.setApprovedOn(new Date());
                commentaryService.approveInvitationStatus(userVo.getId(), userVo.getUserId(), userVo.getApprovedOn(), userVo.getManuscriptId(), userVo.getUserRole(), userVo.getAction());
            } else if (StringUtils.equalsIgnoreCase(userVo.getAction(), "reject")) {
                userVo.setRejectOn(new Date());
                commentaryService.rejectInvitationStatus(userVo.getId(), userVo.getUserId(), userVo.getRejectOn(), userVo.getManuscriptId(), userVo.getUserRole(), userVo.getAction());
            } else {
                userVo.setSubmittedDate(new Date());
                commentaryService.submitInvitationStatus(userVo.getId(), userVo.getUserId(), userVo.getSubmittedDate(), userVo.getManuscriptId(), userVo.getUserRole(), userVo.getAction());
            }

            results.put("result", "success");
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            logger.error("An error occurred while processing the request.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
        }
    }


	@PostMapping("updateEmailTemplates")
	public ResponseEntity<?> updateEmailTemplates(@RequestBody CommentaryVo userVo) {
		JSONObject results = new JSONObject();
		try {
			JournalMailTemplates temp = journalMailTemplatesRepo.getUniqueJournalMailTemplates(userVo.getJournalId(), "Commentary Invitation");

			List<Journal> journalIds = journalsRepo.getActivejournal();  //.stream().map(obj -> obj.getJournalId()).collect(Collectors.toList());

			for (Journal journalId : journalIds)
			{

				JournalMailTemplates templates = journalMailTemplatesRepo.getUniqueJournalMailTemplates(journalId.getJournalId(), "Commentary Invitation");

				if(templates == null)
				{
					JournalMailTemplates templates1 = new JournalMailTemplates();
					BeanUtils.copyProperties(temp,templates1);
					templates1.setJournalId(journalId.getJournalId());
					templates1.setJournalEmailTemplateId(null);
					journalMailTemplatesRepo.save(templates1);

				}

			}

			List<JournalMailTemplates> journalMailTemplates = journalMailTemplatesRepo.getEmailTemaplatesByName("Commentary Invitation");
			for(JournalMailTemplates tmp : journalMailTemplates)
			{

				if(tmp.getJournalId() == userVo.getJournalId())
					continue;

				TaskEmailMap taskEmailMap = new TaskEmailMap();
				taskEmailMap.setTaskId(41);
				taskEmailMap.setJournalEmailTemplateId(tmp.getJournalEmailTemplateId());
				taskEmailMap.setJournalId(tmp.getJournalId());

				taskEmailMapRepo.save(taskEmailMap);

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
