package com.digi.uniprr.controller;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.digi.uniprr.VO.AuthorFeedbackVO;
import com.digi.uniprr.VO.FeedbackQuestionMasterVO;
import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.VO.ManuscriptListVO;
import com.digi.uniprr.model.AuthorFeedback;
import com.digi.uniprr.model.Comment;
import com.digi.uniprr.model.JournalMetadata;
import com.digi.uniprr.service.AuthorFeedbackService;
import com.digi.uniprr.service.FeedbackQuestionMasterService;
import com.digi.uniprr.service.JournalService;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/feedbackQuestionMaster")
public class FeedbackQuestionMasterRestController {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	FeedbackQuestionMasterService feedbackQuestionMasterService;
	
	@Autowired
	AuthorFeedbackService authorFeedbackService;
	
	@Autowired
	private JournalService journalService;
	
	JSONObject obj = new JSONObject();

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ResponseEntity<?> getFeedbackList() {
		log.info("Inside FeedbackQuestionMasterRestController getFeedbackList");
		List<FeedbackQuestionMasterVO> feedbackList = feedbackQuestionMasterService.getFeedbackList();
		if (feedbackList != null && !feedbackList.isEmpty()) {
			obj.put("data", feedbackList);
			return new ResponseEntity<>(obj, HttpStatus.OK);
		}
		obj.put("data", "Data Not found");
		return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
	}

//	@RequestMapping(value = "/listById", method = RequestMethod.POST)
//	public ResponseEntity<?> getFeedbackListById(@RequestBody IDVO id) {
//		log.info("Inside FeedbackQuestionMasterRestController getFeedbackListById");
//		List<FeedbackQuestionMasterVO> feedbackList = feedbackQuestionMasterService.getFeedbackListById(id.getJournalId());
//		if (feedbackList != null && !feedbackList.isEmpty()) {
//			obj.put("data", feedbackList);
//			return new ResponseEntity<>(obj, HttpStatus.OK);
//		}
//		obj.put("data", "No Data found regarding this id: " + id);
//		return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
//	}
	
	@RequestMapping(value = "/listById", method = RequestMethod.POST)
	public ResponseEntity<?> getFeedbackListById(@RequestBody IDVO id) {
		log.info("Inside FeedbackQuestionMasterRestController getFeedbackListById");
		List<FeedbackQuestionMasterVO> feedbackList = feedbackQuestionMasterService.getFeedbackListById(id.getJournalId());
		if(feedbackList.size()>0) {
			if (feedbackList != null && !feedbackList.isEmpty()) {
				obj.put("data", feedbackList);
			}
			if(id.getManuscriptId() !=null && id.getManuscriptId() != 0) {
				List<ManuscriptListVO> checkList = feedbackQuestionMasterService.getCheckListById(id.getJournalId(),id.getManuscriptId());
				if (checkList != null && !checkList.isEmpty()) {
					obj.put("selectchecklist", checkList);
				} else {
					obj.put("selectchecklist", null);
				}
			}
			
			return new ResponseEntity<>(obj, HttpStatus.OK);	
		}else {
			obj.put("data", "No Data found regarding this id: " + id);
			return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
		}
	}
	
	@RequestMapping(value = "/createQuestions", method = RequestMethod.POST)
	public ResponseEntity<?> createQuestions(@RequestBody List<FeedbackQuestionMasterVO> feedbackQuestionMasterVO) {
		log.info("Inside FeedbackQuestionMasterRestController createQuestions");
		try {
			feedbackQuestionMasterService.createQuestions(feedbackQuestionMasterVO);
			obj.put("data", "Question created...");
		}catch(Exception e) {
			obj.put("data", "Something Went Wrong");
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/authorSubmitFeedback", method = RequestMethod.POST)
	public ResponseEntity<?> authorSubmitFeedback(@RequestBody AuthorFeedbackVO authorFeedbackVO) {
		log.info("Inside FeedbackQuestionMasterRestController createQuestions");
		try {
			authorFeedbackService.saveAuthorFeedback(authorFeedbackVO);
			obj.put("data", "data save ...");
		}catch(Exception e) {
			obj.put("data", "Something Went Wrong");
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}
	@RequestMapping(value = "/updateeQuestions", method = RequestMethod.POST)
	public ResponseEntity<?> updateeQuestions(@RequestBody FeedbackQuestionMasterVO feedbackQuestionMasterVO) {
		log.info("Inside FeedbackQuestionMasterRestController createQuestions");
		try {
			feedbackQuestionMasterService.updateeQuestions(feedbackQuestionMasterVO);
			obj.put("data", "Question updated...");
		}catch(Exception e) {
			obj.put("data", "Something Went Wrong");
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/deleteQuestions", method = RequestMethod.POST)
	public ResponseEntity<?> deleteQuestions(@RequestBody FeedbackQuestionMasterVO feedbackQuestionMasterVO) {
		log.info("Inside FeedbackQuestionMasterRestController createQuestions");
		try {
			feedbackQuestionMasterService.deleteQuestions(feedbackQuestionMasterVO);
			obj.put("data", "Question deleted...");
		}catch(Exception e) {
			obj.put("data", "Something Went Wrong");
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/getMasterList", method = RequestMethod.GET)
	public ResponseEntity<?> geMasterList() {
		log.info("Inside FeedbackQuestionMasterRestController getFeedbackList");
		List<FeedbackQuestionMasterVO> feedbackList = feedbackQuestionMasterService.geMasterList();
		if (feedbackList != null && !feedbackList.isEmpty()) {
			obj.put("data", feedbackList);
			return new ResponseEntity<>(obj, HttpStatus.OK);
		}
		obj.put("data", "Data Not found");
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/saveAuthorFeedback", method = RequestMethod.POST)
	public ResponseEntity<?> saveAuthorFeedback(@RequestBody IDVO id) {
		log.info("Inside saveAuthorFeedback");
		try {
			authorFeedbackService.saveAuthorFeedback(id);
			obj.put("data", "Feedback Save successfully...");
		}catch(Exception e) {
			obj.put("data", "Something Went Wrong");
			return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}
	
	@PostMapping("listByJournalAndRole")
	public ResponseEntity<?> listByJournalAndRole(@RequestBody IDVO id) {
		log.info("Inside FeedbackQuestionMasterRestController listByJournalAndRole");
		List<FeedbackQuestionMasterVO> feedbackList = feedbackQuestionMasterService.listByJournalAndRole(id);
		if (feedbackList != null && !feedbackList.isEmpty()) {
			obj.put("data", feedbackList);
			return new ResponseEntity<>(obj, HttpStatus.OK);
		}
		obj.put("data", "Data Not found");
		return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
	}
	
	@PostMapping("/getQuestionByManuscriptType")
	public ResponseEntity<?> getQuestionByManuscriptType(@RequestBody IDVO id) {
		log.info("Inside getQuestionByManuscriptType listByJournalAndRole");
		List<FeedbackQuestionMasterVO> feedbackList = feedbackQuestionMasterService.getQuestionByManuscriptType(id);
		
		JournalMetadata journalMetadata = journalService.getJournalMetadata(id.getJournalId());
		if(journalMetadata != null) {
			obj.put("publonsCode", journalMetadata.getPublonsCode());
 		}
		
		if (feedbackList != null && !feedbackList.isEmpty()) {
			obj.put("data", feedbackList);
			return new ResponseEntity<>(obj, HttpStatus.OK);
		}
		obj.put("data", "Data Not found");
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}
	
	@PostMapping("getAuthorFeedback")
	public ResponseEntity<?> getAuthorFeedback(@RequestBody IDVO id) {
		log.info("Inside getQuestionByManuscriptType listByJournalAndRole");
		try {
			List<AuthorFeedbackVO> feedbackList = feedbackQuestionMasterService.getAuthorFeedback(id);
			if (feedbackList != null && !feedbackList.isEmpty()) {
				obj.put("data", feedbackList);
			}else {
			obj.put("data", "Data Not found");
			}
			return new ResponseEntity<>(obj, HttpStatus.OK);
		} catch (Exception e) {
			obj.put("data", "Something went wrong");
			return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
		}
	}
	
	@GetMapping("testData")
	public ResponseEntity<?> testData() {
		log.info("Inside createComment");
		JSONObject obj = new JSONObject();
		try {
			File file = new File("C:\\Users\\80076\\Desktop\\ReviewerQuestionData.xlsx"); // creating a new file instance
			FileInputStream fis = new FileInputStream(file); // obtaining bytes from the file
// creating Workbook instance that refers to .xlsx file
			XSSFWorkbook wb = new XSSFWorkbook(fis);
			XSSFSheet sheet = wb.getSheetAt(0); // creating a Sheet object to retrieve object
			Iterator<Row> itr = sheet.iterator(); // iterating over excel file
			int j = 0;
			boolean status = false;
			while (itr.hasNext()) {
				Row row = itr.next();
				Iterator<Cell> cellIterator = row.cellIterator(); // iterating over each column
				int i = 0;
				if (j != 0) {
					List<FeedbackQuestionMasterVO> data = new ArrayList();
					FeedbackQuestionMasterVO feedbackQuestionMasterVO = new FeedbackQuestionMasterVO();
					List<String> answer = new ArrayList<String>();
					answer.add("Good/Appropriate");
					answer.add("Fair/Just Appropriate");
					answer.add("Poor/Inappropriate");
					
					
					while (cellIterator.hasNext()) {
						Cell cell = cellIterator.next();
						if (i == 2) {
							feedbackQuestionMasterVO.setQuestion(cell.toString().trim());
							
						}else if (i == 3) {
							if(cell.toString().trim().equals("1")){
								feedbackQuestionMasterVO.setRevisedManuscript("YES");
							}else {
								feedbackQuestionMasterVO.setRevisedManuscript("NO");
							}
						}
						i++;
					}
					feedbackQuestionMasterVO.setAnswer(answer);
					feedbackQuestionMasterVO.setJournalId(1504);
					feedbackQuestionMasterVO.setQuestionType("RADIO");
					feedbackQuestionMasterVO.setRole("REVIEWER");
					feedbackQuestionMasterVO.setType("QUESTION");
					feedbackQuestionMasterVO.setUserId(1);
					data.add(feedbackQuestionMasterVO);
					feedbackQuestionMasterService.createQuestions(data);
				}
				j++;

			}
		} catch (Exception e) {
			return new ResponseEntity<>("Something went wrong", HttpStatus.CONFLICT);
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}
}
