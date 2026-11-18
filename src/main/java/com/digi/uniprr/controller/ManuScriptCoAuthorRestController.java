package com.digi.uniprr.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.digi.uniprr.utils.AppConstants;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.VO.ManuscriptCoAuthorVO;
import com.digi.uniprr.VO.UserVO;
import com.digi.uniprr.model.Journal;
import com.digi.uniprr.model.ManuScript;
import com.digi.uniprr.model.ManuScriptCoAuthor;
import com.digi.uniprr.model.User;
import com.digi.uniprr.repository.JournalsRepo;
import com.digi.uniprr.repository.ManuScriptCoAuthorRepo;
import com.digi.uniprr.repository.ManuScriptRepo;
import com.digi.uniprr.service.ManuScriptService;
import com.digi.uniprr.service.ManuScrpitCoAuthorService;
import com.digi.uniprr.service.UserService;
import com.digi.uniprr.serviceImpl.ManuScriptCoAuthorServiceImpl;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/manuScriptCoAuthor")

public class ManuScriptCoAuthorRestController {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	UserService userService;
	
	@Autowired
	ManuScriptService manuscriptService;

	@Autowired
	ManuScriptCoAuthorServiceImpl manuScriptCoAuthorService;
	
	@Autowired
	ManuScrpitCoAuthorService manuScrpitCoAuthorService;
	
	
	@Autowired
	ManuScriptCoAuthorRepo manuScriptCoAuhor;
	
	@Autowired
	ManuScriptRepo manuScriptRepo;

	@Autowired
	JournalsRepo journalRepo;

	JSONObject obj = new JSONObject();

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public ResponseEntity<?> createCoAuthor(@RequestBody ManuscriptCoAuthorVO manuscriptCoAuthorVO) {
		try {
			
			//check for duplicate Author for the same Manuscript start
			Integer uniqueCoAuthorCount = manuScriptCoAuhor.getUniqueAuthorCount(
					manuscriptCoAuthorVO.getManuscriptId(),manuscriptCoAuthorVO.getId());
			if(uniqueCoAuthorCount > 0)
			{
				return new ResponseEntity<>("duplicate author", HttpStatus.BAD_REQUEST);
			}
            // Added by Utkarsh to check for duplicate authors
			if(manuscriptCoAuthorVO.getEmail()!=null && !Objects.equals(manuscriptCoAuthorVO.getEmail(), ""))
			{
				User user = userService.getByEmail(manuscriptCoAuthorVO.getEmail());
				if(user!=null)
				{
					Integer uniqueCoAuthorCount1 = manuScriptCoAuhor.getUniqueAuthorCount(
							manuscriptCoAuthorVO.getManuscriptId(), user.getId());
					if(uniqueCoAuthorCount1 > 0)
					{
						return new ResponseEntity<>("duplicate author", HttpStatus.BAD_REQUEST);
					}
				}
			}


			// End

			ManuScript getManuScript = manuScriptRepo.getById(manuscriptCoAuthorVO.getManuscriptId());
			User usr = userService.getByEmail(manuscriptCoAuthorVO.getEmail());

			if(manuscriptCoAuthorVO.getId() == null && usr==null) {
				List<Integer> roleId = new ArrayList();
				roleId.add(4);
				List<Integer> journalId = new ArrayList();
				journalId.add(getManuScript.getJournalId());
				UserVO userVO = new UserVO();
				userVO.setSalutation(manuscriptCoAuthorVO.getSalutation());
				userVO.setFirstName(manuscriptCoAuthorVO.getFirstName());
				userVO.setMiddleName(manuscriptCoAuthorVO.getMiddleName());
				userVO.setLastName(manuscriptCoAuthorVO.getLastName());
				userVO.setRoleId(roleId);
				userVO.setJournalId(journalId);
				userVO.setEmail(manuscriptCoAuthorVO.getEmail());
				userVO.setOrcidId(manuscriptCoAuthorVO.getOrcidId());
                userVO.setTrialRegistrationNo(manuscriptCoAuthorVO.getTrialRegistrationNo());
                userVO.setResearcherId(manuscriptCoAuthorVO.getResearcherId());
				userVO.setInstitute(manuscriptCoAuthorVO.getInstitution());
				userVO.setAcademic(manuscriptCoAuthorVO.getAcademic());
				userVO.setIsCoAuthor("YES");
				userVO.setState(manuscriptCoAuthorVO.getState());
				userVO.setCountryId(manuscriptCoAuthorVO.getCountryId());
				userVO.setManuscriptId(manuscriptCoAuthorVO.getManuscriptId());
				userVO.setActivationUrl(manuscriptCoAuthorVO.getActivationUrl());	
				userVO.setAddress(manuscriptCoAuthorVO.getAddress());
				userVO.setPubmedId(manuscriptCoAuthorVO.getPubmedId());
				userVO.setSpecialization(manuscriptCoAuthorVO.getSpecialization());
				userVO.setResearcherId(manuscriptCoAuthorVO.getResearcherId());
				userVO.setPostalCode(manuscriptCoAuthorVO.getPostalCode());
				userVO.setPhoneNumber(manuscriptCoAuthorVO.getPhoneNumber());
				userVO.setMobileNumber(manuscriptCoAuthorVO.getMobileNumber());
				userVO.setCountryCode(manuscriptCoAuthorVO.getCountryCode());
				userVO.setScopusId(manuscriptCoAuthorVO.getScopusId());
				userVO.setExpertise(manuscriptCoAuthorVO.getExpertise());
				userVO.setInstitute(manuscriptCoAuthorVO.getInstitute());
				userVO.setCity(manuscriptCoAuthorVO.getCity());
				User savedUser = userService.createUser(userVO);
				ManuScriptCoAuthor manuScriptCoAuthor = new ManuScriptCoAuthor();
				manuScriptCoAuthor.setCoAuthorId(savedUser.getId());
				manuScriptCoAuthor.setCoAuthorManuscriptId(manuscriptCoAuthorVO.getManuscriptId());
				manuScriptCoAuthor.setAuthorOrder(manuscriptCoAuthorVO.getOrder());
				manuScriptCoAuthor.setCorrespondingAuthor(manuscriptCoAuthorVO.getCorrespondingAuthor());
				manuScriptCoAuthor.setCoAuthorStatus(1);
				manuScriptCoAuthor.setIsEmailSent(0);
				manuScriptCoAuthor.setCoAuthorType(1);
				// 10-12-2021 new change added
				manuScriptCoAuthor.setIsCorrespondingAuthor(manuscriptCoAuthorVO.getIsCorrespondingAuthor());
				manuScriptCoAuhor.save(manuScriptCoAuthor);
				obj.put("data", "CoAuthor created...");
				return new ResponseEntity<>(obj, HttpStatus.OK);
			} else {
				UserVO userVO = new UserVO();
				userVO.setId(manuscriptCoAuthorVO.getId());
				userVO.setEmail(manuscriptCoAuthorVO.getEmail());
				userVO.setSalutation(manuscriptCoAuthorVO.getSalutation());
				userVO.setFirstName(manuscriptCoAuthorVO.getFirstName());
				userVO.setMiddleName(manuscriptCoAuthorVO.getMiddleName());
				userVO.setLastName(manuscriptCoAuthorVO.getLastName());
				userVO.setInstitute(manuscriptCoAuthorVO.getInstitute());
				userVO.setAcademic(manuscriptCoAuthorVO.getAcademic());
				userVO.setState(manuscriptCoAuthorVO.getState());
				userVO.setCity(manuscriptCoAuthorVO.getCity());
				userVO.setCountryId(manuscriptCoAuthorVO.getCountryId());	
				userVO.setAddress(manuscriptCoAuthorVO.getAddress());
				userVO.setExpertise(manuscriptCoAuthorVO.getExpertise());
				userVO.setScopusId(manuscriptCoAuthorVO.getScopusId());
				userVO.setPubmedId(manuscriptCoAuthorVO.getPubmedId());
				userVO.setSpecialization(manuscriptCoAuthorVO.getSpecialization());
				userVO.setResearcherId(manuscriptCoAuthorVO.getResearcherId());
				userVO.setPostalCode(manuscriptCoAuthorVO.getPostalCode());
				userVO.setPhoneNumber(manuscriptCoAuthorVO.getPhoneNumber());
				userVO.setMobileNumber(manuscriptCoAuthorVO.getMobileNumber());
				userVO.setCountryCode(manuscriptCoAuthorVO.getCountryCode());
				userService.updateUser(userVO);
				// 10-12-2021 new change added corresponding author
				if (StringUtils.isNotBlank(manuscriptCoAuthorVO.getIsCorrespondingAuthor())
						&& StringUtils.equalsIgnoreCase(manuscriptCoAuthorVO.getIsCorrespondingAuthor(),"Y")) {
					List<ManuScriptCoAuthor> list = manuScriptCoAuhor.getCorrespondingAuthorListFromManuscriptId(
							getManuScript.getManuScriptId());
					Journal journalData = journalRepo.getById(getManuScript.getJournalId());
					if (journalData.getCorrespondingCount() != null && list.size() < journalData.getCorrespondingCount()) {
						manuScriptCoAuthorService.createCoAuthor(manuscriptCoAuthorVO);
						obj.put("data", "corresponding created...");
						return new ResponseEntity<>(obj, HttpStatus.OK);
					} else {
						obj.put("data", "Please update corresponding count in Journal Configuraion-"+journalData.getCorrespondingCount());
						return new ResponseEntity<>(obj, HttpStatus.OK);
					}
				} else { // Existing flow to create co-author
					manuScriptCoAuthorService.createCoAuthor(manuscriptCoAuthorVO);
					obj.put("data", "CoAuthor created...");
					return new ResponseEntity<>(obj, HttpStatus.OK);
				}
			}
		} catch (Exception e) {
			log.error("Exception : " + e.getMessage());
			obj.put("data", "Something went wrong");
			return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
		}
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public ResponseEntity<?> deleteCoAuthor(@RequestBody IDVO vo) {
		JSONObject obj = new JSONObject();
		try {
			manuscriptService.deleteCoAuthor(vo.getId());
			obj.put("message", "Succsessfully updated");
		} catch (Exception e) {
			e.printStackTrace();
			obj.put("message", "Error");
		}

		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@RequestMapping(value = "/updateCopyright", method = RequestMethod.POST)
	public ResponseEntity<?> updateCopyright(@RequestBody IDVO vo) {
		log.info(AppConstants.REQUEST_METHOD+" :: "+AppConstants.POST+
				AppConstants.REQUEST_URI+" :: /manuScriptCoAuthor/updateCopyright"+
				AppConstants.REQUEST_BODY+" ::"+vo.toString());
		JSONObject obj = new JSONObject();
		try {
			obj.put("data", manuScrpitCoAuthorService.updateCopyright(vo));
		} catch (Exception e) {
			log.error("Inside ManuScrpitCoAuthorService :: updateCopyright() :::: Exception : " + e);
			log.info("Inside ManuScrpitCoAuthorService :: updateCopyright() :::: Exception : " + e);
//			obj.put("message", "Error");
		}

		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

}
