package com.digi.uniprr.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.List;

import com.digi.uniprr.utils.AppConstants;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.VO.JournalsConfigurationVO;
import com.digi.uniprr.VO.SubjectVO;
import com.digi.uniprr.model.Subject;
import com.digi.uniprr.service.SubjectService;

@RestController
@RequestMapping("subject")
public class SubjectRestController {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	SubjectService subjectService;
	
	JSONObject obj = new JSONObject();
	
	@PostMapping("createSubject")
	public ResponseEntity<?> createSubject(@RequestBody SubjectVO SubjectVO){
		try {
			subjectService.createSubject(SubjectVO);
			obj.put("data", "Created...");
		}catch(Exception e) {
			log.error("Exception : " + e.getMessage());
			obj.put("data", "Something Went Wrong");
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}
	
	@GetMapping("getSubjectList")
	public ResponseEntity<?> getSubjectList() {

		log.info(AppConstants.REQUEST_METHOD+" :: "+AppConstants.GET+
				AppConstants.REQUEST_URI+" :: /subject/getSubjectList");
		List<Subject> subject = subjectService.getSubjectList();
		if(subject!=null && !subject.isEmpty()) {
			obj.put("data", subject);
		}else {
			obj.put("data", "Data Not Found");
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}
	
	@PostMapping("deleteSubject")
	public ResponseEntity<?> deleteSubject(@RequestBody IDVO id){
		try {
			subjectService.deleteSubject(id);
			obj.put("data", "Deleted...");
		}catch(Exception e) {
			log.error("Exception : " + e.getMessage());
			obj.put("data", "Something Went Wrong");
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}
	
	@PostMapping("updateSubject")
	public ResponseEntity<?> updateSubject(@RequestBody Subject Subject){
		try {
			subjectService.updateSubject(Subject);
			obj.put("data", "Upadted...");
		}catch(Exception e) {
			log.error("Exception : " + e.getMessage());
			obj.put("data", "Something Went Wrong");
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}
	
	@GetMapping("TestDataUpload")
	public ResponseEntity<?> TestDataUpload() {

		String str ="Test";
		
		
		/* SubjectRepo subjectRepo = SubjectRepo(); */
		try {
			FileWriter myWriter = new FileWriter("C:\\Users\\\\80076\\Desktop\\outputxml.txt");
			JournalsConfigurationVO journalsConfigurationVO = new JournalsConfigurationVO();
		    
			File file = new File("C:\\Users\\80076\\Desktop\\Journals_Migration.xlsx"); // creating a new file instance
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
						if(i==6) {
							
							str = subjectService.saveData(cell.toString().trim());
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
			log.debug("Subject All Set");
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		if(str!=null && str!="") {
			obj.put("data", str);
		}else {
			obj.put("data", "Data Not Found");
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}
	
}
