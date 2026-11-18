package com.digi.uniprr.controller;

import java.time.YearMonth;
import java.util.List;

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
import org.springframework.web.bind.annotation.RestController;

import com.digi.uniprr.VO.FailedIthenicateVo;
import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.VO.ManuscriptFileVO;
import com.digi.uniprr.VO.PlagiarismListVO;
import com.digi.uniprr.VO.PlagiarismVO;
import com.digi.uniprr.model.FailedIauthenticate;
import com.digi.uniprr.model.PlagiarismCheck;
import com.digi.uniprr.repository.FailedIauthRepo;
import com.digi.uniprr.repository.PlagiarismCheckRepo;
import com.digi.uniprr.service.CheckPlagiarismService;
import com.digi.uniprr.service.ManuScriptFileService;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/Plagiarism")
public class CheckPlagiarismRestController {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	CheckPlagiarismService checkPlagiarismService;
	
	@Autowired
	ManuScriptFileService manuScriptFileService;

	JSONObject obj = new JSONObject();
	
	PlagiarismCheckRepo plagiarismCheckRepo;
	
	@PostMapping("checkPlagiarism")
	public ResponseEntity<?> checkPlagiarism(@RequestBody IDVO id){
		log.info("Inside checkPlagiarism");
		log.info("Request Time :"+System.currentTimeMillis());
		try {
			boolean check = checkPlagiarismService.checkIfExist(id);
			if(check) {
				obj.put("data", "Already in process");
			}else {
				String data = checkPlagiarismService.uploadFile(id);
				obj.put("data", data);
			}
			
		}catch(Exception e) {
			obj.put("data", "Something went wrong");
			return new ResponseEntity<>(obj, HttpStatus.CONFLICT);
		}
		return new ResponseEntity<>(obj, HttpStatus.OK); 
	}
	
	@PostMapping("activePlagiarism")
	public ResponseEntity<?> activePlagiarism(@RequestBody IDVO id){
		log.info("Inside checkPlagiarism");
		log.info("Request Time :"+System.currentTimeMillis());
		try {
			String data = checkPlagiarismService.activePlagiarism(id.getId());
			obj.put("data", data);
		}catch(Exception e) {
			return new ResponseEntity<>("Something went wrong", HttpStatus.OK);
		}
		return new ResponseEntity<>(obj, HttpStatus.OK); 
	}
	@GetMapping("manualActivatePalagrismURL")
	public ResponseEntity<?> manualActivatePalagrismURL(){
		List<PlagiarismCheck> plagiarismCheck = checkPlagiarismService.getInActiveData();
		if (plagiarismCheck != null && !plagiarismCheck.isEmpty()) {
			for (PlagiarismCheck plagiarismCheck2 : plagiarismCheck) {
				try {
					checkPlagiarismService.activePlagiarism(plagiarismCheck2.getId());
				}catch(Exception e) {
					e.printStackTrace();
					log.info("Exception In plagiarismCheck   ::::"  +e);
					obj.put(plagiarismCheck2.getManuscriptId(), e);
				}
				
			}
		}
			
	return new ResponseEntity<>(obj, HttpStatus.OK); 
	}
	

	
	@GetMapping("manualPalagrismURLGeneration")
	public ResponseEntity<?> manualPalagrismURLGeneration(){
		log.info("Inside checkPlagiarism");
		log.info("Request Time :"+System.currentTimeMillis());
		try {
		  // 1st step : Get the list of item for palgrism URL generation
			
			List<PlagiarismCheck> ListForURLGeneration = checkPlagiarismService.getListForURLGeneration();
			
			if(ListForURLGeneration !=null)
			{
				for (PlagiarismCheck plagiarismCheck : ListForURLGeneration) {
					
					FailedIthenicateVo failedIthenicateVo = new FailedIthenicateVo();
					failedIthenicateVo.setJournalId(plagiarismCheck.getJournalId());
					failedIthenicateVo.setManuscriptId(plagiarismCheck.getManuscriptId());

				try {
					// 2nd Step:  Delete data 
					checkPlagiarismService.deletePalagrismById(plagiarismCheck.getId());
					
					// 3rd Step: insert data for palagrism check url
					IDVO id = new IDVO();
					id.setIsActive(1);
					id.setManuscriptId(plagiarismCheck.getManuscriptId());
					id.setManuscriptVersion(plagiarismCheck.getManuscriptVersion());
					id.setJournalId(plagiarismCheck.getJournalId());
					String data = checkPlagiarismService.uploadFile(id);
					
					// 4th Step activate the palagrism check
					
					List<PlagiarismCheck> plagiarismCheck1 = plagiarismCheckRepo.getInActiveData();
					if (plagiarismCheck1 != null && !plagiarismCheck1.isEmpty()) {
						for (PlagiarismCheck plagiarismCheck2 : plagiarismCheck1) {
							try {
								checkPlagiarismService.activePlagiarism(plagiarismCheck2.getId());
								// 5th Step generate URL
								IDVO id1 = new IDVO();
								id1.setIsActive(1);
								id1.setManuscriptId(plagiarismCheck2.getManuscriptId());
								id1.setManuscriptVersion(plagiarismCheck2.getManuscriptVersion());
								PlagiarismCheck data2 = checkPlagiarismService.checkPlagiarismReport(id1);
								obj.put("data", data2);
							}catch(Exception e) {
								checkPlagiarismService.insertFailedPalagarism(failedIthenicateVo);
								log.info("Exception In plagiarism URL generation   ::::"  +e);
							}
						}
					}
				} catch(Exception e) {
					checkPlagiarismService.insertFailedPalagarism(failedIthenicateVo);
					log.info("Exception In plagiarism URL generation   ::::"  +e);
					
				}}
			}
			
			
		}catch(Exception e) {
			return new ResponseEntity<>("Something went wrong", HttpStatus.OK);
			

		}
		return new ResponseEntity<>(obj, HttpStatus.OK); 
	}
	
	@GetMapping("getPlagiarismReport")
	public ResponseEntity<?> getPlagiarismReport(){
		log.info("Inside getPlagiarismReport");
		log.info("Request Time :"+System.currentTimeMillis());
		try {
			List<PlagiarismCheck> data = checkPlagiarismService.getReport();
			if(data!=null && !data.isEmpty()) {
				obj.put("data", data);
			}else {
				obj.put("data", "Data Not Found");
			}
			
		}catch(Exception e) {
			log.info("Exception : " +e);
			obj.put("data", "Something went wrong");
		}
		return new ResponseEntity<>(obj, HttpStatus.OK); 
	}
	
	@PostMapping("checkPlagiarismReport")
	public ResponseEntity<?> checkPlagiarismReport(@RequestBody IDVO id){
		log.info("Inside checkPlagiarismReport");
		log.info("Request Time :"+System.currentTimeMillis());
		try {
			PlagiarismCheck data = checkPlagiarismService.checkPlagiarismReport(id);
			if(data!=null && data.getUrl()!="") {
				obj.put("data", data);
			}else {
				obj.put("data", "URL not available");
			}
		}catch(Exception e) {
			return new ResponseEntity<>("Something went wrong", HttpStatus.OK);
		}
		return new ResponseEntity<>(obj, HttpStatus.OK); 
	}
	
	@PostMapping("checkCopyscape")
	public ResponseEntity<?> checkCopyscape(@RequestBody IDVO id){
		log.info("Inside checkPlagiarism");
		log.info("Request Time :"+System.currentTimeMillis());
		try {
			String data = checkPlagiarismService.checkCopyscape(id);
			if(data.equalsIgnoreCase("Something went wrong")) {
				obj.put("data", "Something went wrong");
			}else {
				obj.put("data", data);
			}
			//obj.put("data", data);
		}catch(Exception e) {
			return new ResponseEntity<>("Something went wrong", HttpStatus.OK);
		}
		return new ResponseEntity<>(obj, HttpStatus.OK); 
	}
	
	@PostMapping("getFile")
	public ResponseEntity<?> getFile(@RequestBody IDVO id){
		log.info("Inside getFile");
		log.info("Request Time :"+System.currentTimeMillis());
		List<ManuscriptFileVO> manuscriptFileList = manuScriptFileService.getMainDocumentByIdVersion(id);
		int year = YearMonth.now().getYear();
		int month = YearMonth.now().getMonthValue();
		Integer yearlyPcount = manuScriptFileService.yearlyPcount(id.getJournalId(), year);
		Integer monthlyPcount = manuScriptFileService.monthlyPcount(id.getJournalId(), month);
		if(manuscriptFileList!=null && !manuscriptFileList.isEmpty()) {
			obj.put("Data", manuscriptFileList);
			obj.put("yearlyPcount", yearlyPcount);
			obj.put("monthlyPcount", monthlyPcount);
			
		}else {
			obj.put("Data", "No data found");
		}
		return new ResponseEntity<>(obj, HttpStatus.OK); 
	}
	
	
	@PostMapping("checkPlagiarismCount")
	public ResponseEntity<?> checkPlagiarismCount(@RequestBody PlagiarismVO pvo){
		log.info("Inside checkPlagiarismReport");
		try {
			
			//this will be for all month
			if(pvo.getMonth()!=0) {
				
			}
			List<PlagiarismListVO> data = checkPlagiarismService.getPlagiarism(pvo);
			
			if(data!=null ) { 
				obj.put("data", data); 
			}else {
				obj.put("data", "URL not available"); 
			}

		}catch(Exception e) {
			return new ResponseEntity<>("Something went wrong", HttpStatus.OK);
		}
		return new ResponseEntity<>(obj, HttpStatus.OK); 
	}
	
}
