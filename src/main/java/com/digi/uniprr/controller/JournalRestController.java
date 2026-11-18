package com.digi.uniprr.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.digi.uniprr.utils.AppConstants;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.digi.uniprr.VO.AdminJournalVo;
import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.VO.JournalDetailsVo;
import com.digi.uniprr.VO.JournalListVO;
import com.digi.uniprr.VO.JournalPerformanceVo;
import com.digi.uniprr.VO.JournalVO;
import com.digi.uniprr.model.Groups;
import com.digi.uniprr.model.Journal;
import com.digi.uniprr.model.ManuscriptTypeJournal;
import com.digi.uniprr.service.GroupService;
import com.digi.uniprr.service.JournalService;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping(value = "/journal")
public class JournalRestController {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	JournalService journalService;

	@Autowired
	GroupService groupService;

	JSONObject obj = new JSONObject();

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public ResponseEntity<Object> createJournal(@RequestBody Journal journals) {
		log.info("Inside journal createJournal ");
		if (journalService.isIdExist(journals.getJournalId())) {
			return new ResponseEntity<Object>("Already exist", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		journalService.createJournal(journals);
		return new ResponseEntity<Object>("Journal created", HttpStatus.CREATED);
	}

	@RequestMapping(value = "/listEditorById", method = RequestMethod.POST)
	public ResponseEntity<?> getEditorGroupId(@RequestParam Integer id) {
		log.info("Inside journal getEditorGroupId ");
		JournalVO vo = new JournalVO();
		List<JournalVO> vos = new ArrayList<JournalVO>();
		List<Groups> groupList = groupService.getgroupsById(id);
		for (int i = 0; i < groupList.size(); i++) {
			vo.setId(groupList.get(i).getId());
			vo.setGroupName(groupList.get(i).getGroupName());
			vos.add(vo);
		}
		return new ResponseEntity<>(vos, HttpStatus.OK);
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public List<JournalListVO> getJournalDetails() {
		log.info(AppConstants.REQUEST_METHOD+" :: "+AppConstants.GET+
				AppConstants.REQUEST_URI+" :: /journal/list");
		log.info("Inside journal getJournalDetails ");
		List<JournalListVO> journalsList = journalService.getJournalDetails();
		return journalsList;
	}
	
	@RequestMapping(value = "/journalList", method = RequestMethod.GET)
	public List<AdminJournalVo> getJournalList() {
		log.info("Inside journal getJournalList ");
		log.info(AppConstants.REQUEST_METHOD+" :: "+AppConstants.GET+
				AppConstants.REQUEST_URI+" :: /journal/journalList");
		List<AdminJournalVo> journalsList = journalService.getJournalList();
		return journalsList;
	}

	@RequestMapping(value = "/getAllJournalList", method = RequestMethod.GET)
	public ResponseEntity<Object> getAllJournalList() {
		log.info("Inside journal getAllJournalList ");
		JSONObject obj = new JSONObject();
		try {
			List<JournalListVO> journalsList = journalService.getAllJournalList();
			if (journalsList != null && !journalsList.isEmpty()) {
				obj.put("data", journalsList);
			} else {
				obj.put("data", "data not found");
			}
		} catch (Exception e) {
			e.printStackTrace();
			obj.put("data", "something went wrong");
		}
		return new ResponseEntity<Object>(obj, HttpStatus.OK);
	}

	@RequestMapping(value = "/updatevalidateacronym", method = RequestMethod.POST)
	public ResponseEntity<Object> updateJournalValidateAcro(@RequestBody JournalListVO journalListVO) {
		log.info("Inside journal getAllJournalList ");
		JSONObject obj = new JSONObject();
		try {
			String journalsList = journalService.updateJournalValidateAcro(journalListVO);
			obj.put("data", journalsList);
		} catch (Exception e) {
			e.printStackTrace();
			obj.put("data", "something went wrong");
		}
		return new ResponseEntity<Object>(obj, HttpStatus.OK);
	}

	@RequestMapping(value = "/updateJournalActivation", method = RequestMethod.POST)
	public ResponseEntity<Object> updateJournalActivation(@RequestBody JournalListVO journalListVO) {
		log.info("Inside journal getAllJournalList ");
		JSONObject obj = new JSONObject();
		try {
			String journalsList = journalService.updateJournalActivation(journalListVO, true);
			obj.put("data", journalsList);
		} catch (Exception e) {
			e.printStackTrace();
			obj.put("data", "something went wrong");
		}
		return new ResponseEntity<Object>(obj, HttpStatus.OK);
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public ResponseEntity<Object> updateJournal(@RequestBody Journal journals) {
		log.info("Inside journal updateJournal ");
		if (journalService.isIdExist(journals.getJournalId())) {
			journalService.updateJournal(journals);
			return new ResponseEntity<Object>("Journal Updated", HttpStatus.CREATED);
		}
		return new ResponseEntity<Object>("Does not exist", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@RequestMapping(value = "/delete", method = RequestMethod.GET)
	public ResponseEntity<Object> deleteJournal(@RequestBody Journal journals) {
		log.info("Inside journal deleteJournal ");
		if (journalService.isIdExist(journals.getJournalId())) {
			journalService.deleteJournalById(journals.getJournalId());
			return new ResponseEntity<Object>("Journal deleted", HttpStatus.ACCEPTED);
		}
		return new ResponseEntity<>("Id does not exist", HttpStatus.BAD_REQUEST);
	}

	@RequestMapping(value = "/listEditorByJId", method = RequestMethod.POST)
	public ResponseEntity<?> getEditorByJournalId(@RequestParam Integer id) {
		log.info("Inside journal getEditorByJournalId ");
		List<Groups> groupList = groupService.getJournalDetailsById(id);
		return new ResponseEntity<>(groupList, HttpStatus.OK);
	}

	@RequestMapping(value = "/getById", method = RequestMethod.POST)
	public ResponseEntity<?> getJournalById(@RequestBody IDVO id) {
		log.info("Inside journal getJournalById ");
		log.info(AppConstants.REQUEST_METHOD+" :: "+AppConstants.POST+
				AppConstants.REQUEST_URI+" :: /journal/getById"+
				AppConstants.REQUEST_BODY+" ::"+id.toString());
		JournalVO journalDetail = journalService.getJournalById(id.getId());
		if (journalDetail != null) {
			obj.put("data", journalDetail);
			return new ResponseEntity<>(obj, HttpStatus.OK);
		} else {
			obj.put("data", "No data found");
			return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
		}

	}

	// About the Journal Setup on the Reviewer level
	@RequestMapping(value = "/getJournalPerfomance", method = RequestMethod.POST)
	public ResponseEntity<?> getJournalPerfomance(@RequestBody JournalPerformanceVo journalPerformanceVo) {
		try {
			JournalPerformanceVo data = journalService.getJournalPerfomance(journalPerformanceVo);
			if (data != null)
				obj.put("data", data);
			else
				obj.put("data", "data not found");

		} catch (Exception e) {
			e.printStackTrace();
			obj.put("data", "Something went wrong");
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}
	// Author: NIC
	// Date: 2023-09-07
	// Description: This controller improve for fetching journal details.
	@RequestMapping(value = "/getJournalAdmin", method = RequestMethod.POST)
	public ResponseEntity<?> getJournalAdmin(@RequestBody JournalDetailsVo jdetails) {
		log.info(AppConstants.REQUEST_METHOD+" :: "+AppConstants.POST+
				AppConstants.REQUEST_URI+" :: /journal/getJournalAdmin"+
				AppConstants.REQUEST_BODY+" ::"+jdetails.toString());
	    try {
	        JSONObject obj = new JSONObject();
	        int journalCount = 0;
	        List<JournalDetailsVo> journalData;

	        if (StringUtils.isNotEmpty(jdetails.getSearch())) {
	            journalData = journalService.getJournalAdmin(jdetails);
	            journalCount = journalService.journalCountSearch(jdetails);
	        } else {
	            journalData = journalService.getJournalAdmin(jdetails);
	            journalCount = journalService.journalCount();
	        }

	        sortJournalData(journalData, jdetails);

	        obj.put("journalCount", journalCount);
	        obj.put("data", journalData);

	        return new ResponseEntity<>(obj, HttpStatus.OK);
	    } catch (Exception e) {
	        e.printStackTrace();
	        JSONObject errorObj = new JSONObject();
	        errorObj.put("error", "Something went wrong");
	        return new ResponseEntity<>(errorObj, HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}

	private void sortJournalData(List<JournalDetailsVo> journalData, JournalDetailsVo jdetails) {
	    Comparator<JournalDetailsVo> comparator = null;
	    String sortBy = jdetails.getSortBy();
	    String sortOrder = jdetails.getSort();

	    if ("journalTitle".equalsIgnoreCase(sortBy)) {
	        comparator = Comparator.comparing(JournalDetailsVo::getJournalTitle);
	    } else if ("journalCode".equalsIgnoreCase(sortBy)) {
	        comparator = Comparator.comparing(JournalDetailsVo::getJournalCode);
	    }

	    if (comparator != null) {
	        if ("asc".equalsIgnoreCase(sortOrder)) {
	            journalData.sort(comparator);
	        } else {
	            journalData.sort(comparator.reversed());
	        }
	    }
	}

	@RequestMapping(value = "/getByJournalCode", method = RequestMethod.POST)
	public ResponseEntity<?> getByJournalCode(@RequestBody IDVO id) {
		log.info("Inside journal getJournalById ");
		log.info(AppConstants.REQUEST_METHOD+" :: "+AppConstants.POST +" "+
				AppConstants.REQUEST_URI+" :: /journal/getByJournalCode"+
				AppConstants.REQUEST_PARAMS+" :: [ ]" +" "+
				AppConstants.REQUEST_BODY+" ::"+id.toString());

		try {
			if (id.getJournalCode() != null && !id.getJournalCode().isEmpty()) {
				List<JournalVO> journalDetail = journalService.getByJournalCode(id);
				if (journalDetail != null && !journalDetail.isEmpty()) {
					obj.put("data", journalDetail);
					return new ResponseEntity<>(obj, HttpStatus.OK);
				} else {
					obj.put("data", "data not found");
					return new ResponseEntity<>(obj, HttpStatus.OK);
				}
			} else {
				obj.put("data", "please provide the journal code");
				return new ResponseEntity<>(obj, HttpStatus.valueOf("Please Provide Journal Code"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			obj.put("data", "Something went wrong");
			return new ResponseEntity<>(obj, HttpStatus.EXPECTATION_FAILED);
		}
	}

	@RequestMapping(value = "/getByJournalEic", method = RequestMethod.POST)
	public ResponseEntity<?> getByJournalEic(@RequestBody IDVO id) {
		log.info("Inside journal getJournalById ");

		try {
			List<IDVO> getData = journalService.getByJournalEic(id);
			if (getData != null && !getData.isEmpty()) {
				obj.put("data", getData);
				return new ResponseEntity<>(obj, HttpStatus.OK);
			} else {
				obj.put("data", "data not found");
				return new ResponseEntity<>(obj, HttpStatus.NOT_FOUND);
			}

		} catch (Exception e) {
			e.printStackTrace();
			obj.put("data", "Something went wrong");
			return new ResponseEntity<>(obj, HttpStatus.EXPECTATION_FAILED);
		}
	}

	@PostMapping("getManuscriptTypeByJournal")
	public ResponseEntity<?> getManuscriptTypeByJournal(@RequestBody IDVO id) {
		try {
			List<ManuscriptTypeJournal> manuscriptTypeJournal = journalService
					.getManuscriptTypeByJournal(id.getJournalId());
			if (manuscriptTypeJournal != null && !manuscriptTypeJournal.isEmpty()) {
				List<String> manuscriptType = new ArrayList();
				for (ManuscriptTypeJournal data : manuscriptTypeJournal) {
					manuscriptType.add(data.getManutypeName());
				}
				obj.put("data", manuscriptType);
			} else {
				obj.put("data", "No data found");
			}
		} catch (Exception e) {
			e.printStackTrace();
			obj.put("data", "Something went wrong");
			return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@RequestMapping(value = "/updateManuscriptIdByUnitouch", method = RequestMethod.POST)
	public ResponseEntity<?> updateManuscriptIdBbyUnitouch(@RequestBody IDVO id) {
		log.info("Inside journal updateManuscriptIdBbyUnitouch ");
		log.info(AppConstants.REQUEST_METHOD+" :: "+AppConstants.POST+
				AppConstants.REQUEST_URI+" :: /journal/updateManuscriptIdByUnitouch"+
				AppConstants.REQUEST_BODY+" :: "+id.toString());
		// JSONObject obj = new JSONObject();
		IDVO journalData = new IDVO();
		Journal journal = journalService.getJournalByAcronym(id.getManuscriptCode());

		try {
			journalData = journalService.updateManuscriptId(id.getManuscriptCode());
			if (journalData != null) {
				obj.put("data", journalData);
			}
		} catch (Exception e) {
			log.info("Exception :" + e);
			return new ResponseEntity<>(journalData, HttpStatus.CONFLICT);
		}

		return new ResponseEntity<>(journalData, HttpStatus.OK);

	}

	@RequestMapping(value = "/saveReviewerSetting", method = RequestMethod.POST)
	public ResponseEntity<?> updateJournalReviewerSetting(@RequestBody Journal journal) {

		String updateString = "";

		try {
			updateString = journalService.updateJournalReviewerSetting(journal);

			obj.put("message", updateString);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@RequestMapping(value = "/updateCoAuthorCount", method = RequestMethod.POST)
	public ResponseEntity<?> updateCoAuthorCount(@RequestBody Journal journal) {

		String updateString = "";

		try {
			updateString = journalService.updateCoAuthorCount(journal);
			obj.put("message", updateString);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/updateJournalActivationForUnitouch", method = RequestMethod.POST)
	public ResponseEntity<?> updateJournalActivationForUnitouch(@RequestBody JournalListVO journalListVO) {
		log.info(AppConstants.REQUEST_METHOD+" :: "+AppConstants.POST+
				AppConstants.REQUEST_URI+" :: /journal/updateJournalActivationForUnitouch"+
				AppConstants.REQUEST_BODY+" :: "+journalListVO.toString());
		log.info("Inside journal updateJournalActivationForUnitouch ");
		JSONObject obj = new JSONObject();
		try {
			if(journalService.isJournalExistsByCode(journalListVO)) {
				String journalsList = journalService.updateJournalActivation(journalListVO, false);
				obj.put("data", journalsList);
				return new ResponseEntity<Object>(obj, HttpStatus.OK);
			}else {
				obj.put("data", "Journal not found");
				return new ResponseEntity<Object>(obj, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (Exception e) {
			e.printStackTrace();
			obj.put("data", "something went wrong");
			return new ResponseEntity<Object>(obj, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
