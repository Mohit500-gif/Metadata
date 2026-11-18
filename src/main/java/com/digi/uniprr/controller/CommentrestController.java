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
import org.springframework.web.bind.annotation.RestController;

import com.digi.uniprr.VO.IDVO;
import com.digi.uniprr.VO.UserVO;
import com.digi.uniprr.model.Comment;
import com.digi.uniprr.service.CommentService;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/comment")
public class CommentrestController {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	CommentService commentService;

	@PostMapping("createComment")
	public ResponseEntity<?> createComment(@RequestBody Comment comment) {
		log.info("Inside createComment");
		JSONObject obj = new JSONObject();
		try {
			Comment data = commentService.createComment(comment);
			if (data != null) {
				obj.put("data", "data saved...");
			} else {
				obj.put("data", "Some error Occured");
			}

		} catch (Exception e) {
			return new ResponseEntity<>("Something went wrong", HttpStatus.CONFLICT);
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@PostMapping("deleteComment")
	public ResponseEntity<?> deleteComment(@RequestBody Comment comment) {
		log.info("Inside deleteComment");
		JSONObject obj = new JSONObject();
		try {
			Comment data = commentService.deleteComment(comment);
			if (data != null) {
				obj.put("data", "data deleted...");
			} else {
				obj.put("data", "Some error Occured");
			}

		} catch (Exception e) {
			return new ResponseEntity<>("Something went wrong", HttpStatus.CONFLICT);
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@PostMapping("getDataByJournal")
	public ResponseEntity<?> getDataByJournal(@RequestBody IDVO id) {
		log.info("Inside deleteComment");
		JSONObject obj = new JSONObject();
		try {
			List<Comment> data = commentService.getDataByJournal(id);
			if (data != null && !data.isEmpty()) {
				obj.put("data", data);
			} else {
				obj.put("data", "No data found");
			}

		} catch (Exception e) {
			return new ResponseEntity<>("Something went wrong", HttpStatus.CONFLICT);
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@GetMapping("testData")
	public ResponseEntity<?> testData() {
		log.info("Inside createComment");
		JSONObject obj = new JSONObject();
		try {
			//File file = new File("C:\\Users\\80076\\Desktop\\Commonrejection.xlsx"); // creating a new file instance
			File file = new File("C:\\Users\\80076\\Desktop\\technicalComment.xlsx");
			
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

					while (cellIterator.hasNext()) {
						Cell cell = cellIterator.next();
						if (i == 2) {

							Comment comment = new Comment();
							comment.setJournalId(31357);
							comment.setQuestion(cell.toString());
							//comment.setType("Common Rejection");
							comment.setType("Technical Comment");
							Comment data = commentService.createComment(comment);
						}
						i++;
					}
					
				}
				j++;

			}
		} catch (Exception e) {
			return new ResponseEntity<>("Something went wrong", HttpStatus.CONFLICT);
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

}