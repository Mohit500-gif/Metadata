package com.digi.uniprr.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.List;

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

import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.VO.JournalManuscriptTypeVo;
import com.digi.uniprr.VO.JournalsConfigurationVO;
import com.digi.uniprr.model.ManuscriptType;
import com.digi.uniprr.model.ManuscriptTypeJournal;
import com.digi.uniprr.service.ManuscriptTypeService;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/manuscriptType")
public class ManuscriptTypeRestController {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	ManuscriptTypeService manuscriptTypeService;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ResponseEntity<?> listManuscriptType() {
		List<ManuscriptType> list = manuscriptTypeService.getManuscriptTypeList();
		return new ResponseEntity<> (list, HttpStatus.OK);
	}
	
	@PostMapping("getListByJournal")
	public ResponseEntity<?> getListByJournal(@RequestBody IDVO id) {
		List<ManuscriptType> list = manuscriptTypeService.getListByJournal(id.getId());
		return new ResponseEntity<> (list, HttpStatus.OK);
	}
	
	@PostMapping("getListByJournalId")
	public ResponseEntity<?> getListByJournalId(@RequestBody IDVO id) {
		List<ManuscriptType> list = manuscriptTypeService.getListByJournalId(id.getId());
		return new ResponseEntity<> (list, HttpStatus.OK);
	}

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public ResponseEntity<?> createManuscriptType(@RequestBody ManuscriptType manuscriptType) {
		JSONObject obj = new JSONObject();
		try {
			boolean isManuscriptTypeExist= false;
			isManuscriptTypeExist = manuscriptTypeService.isManuscriptTypeExist(manuscriptType);
			if (isManuscriptTypeExist) {
				obj.put("message", "Manuscript type already exists.");
			}
			else{
				manuscriptTypeService.saveManuscriptType(manuscriptType);
				obj.put("message", "Data Created Successfully");
			}
		}
		catch (Exception e) {
			obj.put("message", "Some Error Occured");
		}
		return new ResponseEntity<> (obj, HttpStatus.OK);
	}

	@RequestMapping(value = "/createJournalManuscriptType", method = RequestMethod.POST)
	public ResponseEntity<?> createJournalManuscriptType(@RequestBody JournalManuscriptTypeVo manuscriptType) {
		JSONObject obj = new JSONObject();
		try {
			boolean isJournalManuscriptTypeExist= false;
			isJournalManuscriptTypeExist = manuscriptTypeService.isJournalManuscriptTypeExist(manuscriptType);
			if (isJournalManuscriptTypeExist) {
				obj.put("message", "Journal Manuscript type already exists.");
			}
			else if(manuscriptType.getMtjId()!=null){
				manuscriptTypeService.saveJournalManuscriptType(manuscriptType);
				obj.put("message", "Journal Manuscript Type Updated Successfully");
			}else{
				manuscriptTypeService.saveJournalManuscriptType(manuscriptType);
				obj.put("message", "Journal Manuscript Type Created Successfully");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			obj.put("message", "Some Error Occured");
		}
		return new ResponseEntity<> (obj, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/deleteJournalManuscriptType", method = RequestMethod.POST)
	public ResponseEntity<?> deleteJournalManuscriptType(@RequestBody JournalManuscriptTypeVo manuscriptType) {
		JSONObject obj = new JSONObject();
		try {
			if(manuscriptType.getMtjId()!=null){
				manuscriptTypeService.deleteJournalManuscriptType(manuscriptType);
				obj.put("message", "Journal Manuscript Type Created Successfully");
			}else {
				obj.put("message", "Please provide primary mjid");
			}
		}
		catch (Exception e) {
			obj.put("message", "Some Error Occured");
		}
		return new ResponseEntity<> (obj, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/getJournalManuscriptType", method = RequestMethod.POST)
	public ResponseEntity<?> getJournalManuscriptType(@RequestBody JournalManuscriptTypeVo manuscriptType) {
		JSONObject obj = new JSONObject();
		try {
			if(manuscriptType.getJournalId()!=null){
				List<ManuscriptTypeJournal> j=manuscriptTypeService.getJournalManuscriptType(manuscriptType);
				if(j!=null && !j.isEmpty()) {
					obj.put("message", j);
				}else {
					obj.put("message", "There is no data");
				}
				
			}else {
				obj.put("message", "Please provide journal id");
			}
		}
		catch (Exception e) {
			obj.put("message", "Some Error Occured");
		}
		return new ResponseEntity<> (obj, HttpStatus.OK);
	}
	
	
	
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public ResponseEntity<?> updateManuscriptType(@RequestBody ManuscriptType manuscriptType) {
		JSONObject obj = new JSONObject();
		try {
			manuscriptTypeService.saveManuscriptType(manuscriptType);
			obj.put("message", "Data Created Successfully");
		}
		catch (Exception e) {
			obj.put("message", "Some Error Occured");
		}
		
		return new ResponseEntity<> (obj, HttpStatus.OK);
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public ResponseEntity<?> deleteManuscriptType(@RequestBody IDVO vo) {
		JSONObject obj = new JSONObject();
		try {
			manuscriptTypeService.deleteManuscriptType(vo.getId());
			obj.put("message", "Data Created Successfully");
		}
		catch (Exception e) {
			obj.put("message", "Some Error Occured");
		}
		
		return new ResponseEntity<> (obj, HttpStatus.OK);
	}

	@RequestMapping(value = "/getById", method = RequestMethod.POST)
	public ResponseEntity<?> getManuscriptTypeDetails(@RequestBody IDVO vo) {
		ManuscriptType type = manuscriptTypeService.getManuscriptTypeById(vo.getId());
		return new ResponseEntity<> (type, HttpStatus.OK);
	}
	
	@GetMapping("TestDataUpload")
	public ResponseEntity<?> TestDataUpload() {

		String str ="Test";
		
		
		//SubjectRepo subjectRepo = SubjectRepo(); /
		try {
			FileWriter myWriter = new FileWriter("C:\\Users\\\\80076\\Desktop\\outputxml.txt");
			JournalsConfigurationVO journalsConfigurationVO = new JournalsConfigurationVO();
		    
			File file = new File("C:\\Users\\80076\\Desktop\\ManuscriptType.xlsx"); // creating a new file instance
			FileInputStream fis = new FileInputStream(file); // obtaining bytes from the file
			//creating Workbook instance that refers to .xlsx file  
			XSSFWorkbook wb = new XSSFWorkbook(fis);
			XSSFSheet sheet = wb.getSheetAt(0); // creating a Sheet object to retrieve object
			Iterator<Row> itr = sheet.iterator(); // iterating over excel file
			int j=0;
			while (itr.hasNext()) {
				Row row = itr.next();
				Iterator<Cell> cellIterator = row.cellIterator(); // iterating over each column
				int i=0;
				if(j!=0) {
					while (cellIterator.hasNext()) {
						
						Cell cell = cellIterator.next();
						if(i==2) {
							String manuscriptTypeArray[]=cell.toString().trim().split(",");
							for (String string : manuscriptTypeArray) {
								if(string!="") {
									manuscriptTypeService.saveDataInDb(string);
								}
								
							}
							
						}
						i++;
					}
				}
				
				myWriter.write(System.getProperty("line.separator"));
				log.debug("");
				log.debug("One Row COmplete" +j);
				j++;
			}
			myWriter.close();
			log.debug("ManuScript Type All Set");
		} catch (Exception e) {
			e.printStackTrace();
		}
		JSONObject obj = new JSONObject();
		if(str!=null && str!="") {
			obj.put("data", str);
		}else {
			obj.put("data", "Data Not Found");
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

}